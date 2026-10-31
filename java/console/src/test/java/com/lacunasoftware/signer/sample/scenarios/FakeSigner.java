package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * A fake Signer on localhost, for the tests: it records every request and answers
 * GET /api/documents/{id} with a document whose status is documentStatus. Set answer to send
 * something else to every other request.
 *
 * The scenarios read SIGNER_ENDPOINT when they start, and build.gradle sets it to a free port for
 * the tests, so the fake listens there, once for the whole test run.
 */
public class FakeSigner {
    // A request the fake Signer received.
    public static class Request {
        public final String method;
        public final String path;
        public final String apiKey;
        public final String contentType;
        public final String body;

        Request(String method, String path, String apiKey, String contentType, String body) {
            this.method = method;
            this.path = path;
            this.apiKey = apiKey;
            this.contentType = contentType;
            this.body = body;
        }
    }

    private static final Pattern DOCUMENT_PATH = Pattern.compile("^/api/documents/([0-9a-f-]{36})$");
    private static FakeSigner instance;

    public final List<Request> requests = new CopyOnWriteArrayList<>();
    public volatile String documentStatus = "Pending";
    // While set, GET /api/documents/{id} waits for it (up to 10 seconds).
    public volatile CountDownLatch documentDetailsDelay;
    // The status and body of the answer to every other request; null answers 404.
    public volatile int answerStatus;
    public volatile String answerBody;

    public static synchronized FakeSigner instance() throws IOException {
        if (instance == null) {
            URI endpoint = URI.create(System.getenv("SIGNER_ENDPOINT"));
            instance = new FakeSigner(endpoint.getPort());
        }
        return instance;
    }

    private FakeSigner(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/", this::handle);
        server.setExecutor(Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }));
        server.start();
    }

    public void reset() {
        requests.clear();
        answerBody = null;
    }

    public void setAnswer(int status, String body) {
        answerStatus = status;
        answerBody = body;
    }

    // The requests to one method and path.
    public List<Request> requestsTo(String method, String path) {
        return requests.stream()
                .filter(request -> request.method.equals(method) && request.path.equals(path))
                .collect(Collectors.toList());
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        requests.add(new Request(
                method,
                path,
                exchange.getRequestHeaders().getFirst("X-Api-Key"),
                exchange.getRequestHeaders().getFirst("Content-Type"),
                read(exchange.getRequestBody())));

        Matcher document = DOCUMENT_PATH.matcher(path);
        if ("GET".equals(method) && document.matches()) {
            CountDownLatch delay = documentDetailsDelay;
            if (delay != null) {
                try {
                    delay.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            respond(exchange, 200, String.format("{\"id\":\"%s\",\"name\":\"sample.pdf\",\"status\":\"%s\"}", document.group(1), documentStatus));
            return;
        }
        String body = answerBody;
        if (body == null) {
            respond(exchange, 404, "");
        } else {
            respond(exchange, answerStatus, body);
        }
    }

    private static String read(InputStream stream) {
        try (Scanner scanner = new Scanner(stream, "UTF-8")) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
