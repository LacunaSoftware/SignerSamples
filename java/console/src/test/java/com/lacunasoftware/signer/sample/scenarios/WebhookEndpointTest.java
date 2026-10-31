package com.lacunasoftware.signer.sample.scenarios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The webhook endpoint's rules: check the token, answer at once, ignore repeated events, and read
 * the document back before acting on DocumentConcluded. WebhookEndpointScenario reads
 * SIGNER_WEBHOOK_PORT and SIGNER_WEBHOOK_TOKEN, which build.gradle sets for the tests, into static
 * fields, so one endpoint serves the whole test run; it never stops, and dies with the test JVM.
 * The tests read what it prints.
 */
public class WebhookEndpointTest {
    private static FakeSigner signer;
    private static String baseUrl;
    private static PrintStream originalOut;
    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeClass
    public static void startEndpoint() throws Exception {
        signer = FakeSigner.instance();
        String port = System.getenv("SIGNER_WEBHOOK_PORT");
        if (port == null) {
            throw new IllegalStateException("SIGNER_WEBHOOK_PORT is not set: run the tests with ./gradlew test, whose build.gradle sets it");
        }
        // Copy what the endpoint prints, on any thread, to output too.
        originalOut = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                synchronized (output) {
                    output.write(b);
                }
                originalOut.write(b);
            }

            @Override
            public void write(byte[] bytes, int offset, int length) {
                synchronized (output) {
                    output.write(bytes, offset, length);
                }
                originalOut.write(bytes, offset, length);
            }
        }, true, "UTF-8"));

        WebhookEndpointScenario endpoint = new WebhookEndpointScenario();
        endpoint.Init();
        endpoint.Run();
        baseUrl = "http://localhost:" + port;
    }

    @AfterClass
    public static void restoreOut() {
        System.setOut(originalOut);
    }

    @Before
    public void setUp() {
        signer.reset();
    }

    @Test
    public void eventWithoutTheTokenAnswers401AndIsNotRecorded() throws Exception {
        String documentId = newId();
        String flowActionId = newId();

        assertStatus(401, post(signedEvent(documentId, flowActionId), null, "/webhook"));

        assertTakenAsNewAndHandledOnce(documentId, flowActionId);
    }

    @Test
    public void eventWithAnotherTokenAnswers401AndIsNotRecorded() throws Exception {
        String documentId = newId();
        String flowActionId = newId();

        assertStatus(401, post(signedEvent(documentId, flowActionId), "not-the-token", "/webhook"));

        assertTakenAsNewAndHandledOnce(documentId, flowActionId);
    }

    @Test
    public void getAndOtherPathsAnswer404() throws IOException {
        assertStatus(404, send("GET", "/webhook", null, null, 30000));
        // The server itself answers /other; /webhook/other reaches the endpoint, which checks the path.
        assertStatus(404, post(signedEvent(newId(), newId()), token(), "/other"));
        assertStatus(404, post(signedEvent(newId(), newId()), token(), "/webhook/other"));
    }

    @Test
    public void bodyThatIsNotAWebhookAnswers400() throws IOException {
        assertStatus(400, post("not json"));
        assertStatus(400, post("{ \"data\": { \"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\" } }"));
    }

    @Test
    public void signedEventIsAcceptedThenHandled() throws Exception {
        String documentId = newId();
        String flowActionId = newId();

        assertStatus(200, post(signedEvent(documentId, flowActionId)));

        waitFor("Accepted: DocumentSigned:" + documentId + ":" + flowActionId, 1);
        waitFor("Document " + documentId + " is Signed", 1);
    }

    @Test
    public void repeatedEventAnswers200ButIsHandledOnce() throws Exception {
        String documentId = newId();
        String flowActionId = newId();

        post(signedEvent(documentId, flowActionId));
        waitFor("Document " + documentId + " is Signed", 1);
        int repeated = post(signedEvent(documentId, flowActionId));

        assertStatus(200, repeated);
        waitFor("Ignored a repeated event: DocumentSigned:" + documentId + ":" + flowActionId, 1);
        String printed = waitForEarlierEvents();
        assertEquals(printed, 1, count(printed, "Document " + documentId + " is Signed"));
    }

    // The flow action id is in "signature", "approval" or "refusal", by event type; without it the
    // second participant's event would look like a repeat of the first and be dropped.
    @Test
    public void signedEventsOfTwoParticipantsAreTwoEvents() throws Exception {
        assertTwoParticipantsAreTwoEvents("DocumentSigned", "signature", "Signed");
    }

    @Test
    public void approvedEventsOfTwoParticipantsAreTwoEvents() throws Exception {
        assertTwoParticipantsAreTwoEvents("DocumentApproved", "approval", "Approved");
    }

    @Test
    public void refusedEventsOfTwoParticipantsAreTwoEvents() throws Exception {
        assertTwoParticipantsAreTwoEvents("DocumentRefused", "refusal", "Refused");
    }

    @Test
    public void eventIsAnsweredBeforeItIsProcessed() throws Exception {
        String documentId = newId();
        signer.documentStatus = "Concluded";
        // Processing a DocumentConcluded event reads the document; hold that read until the answer arrives.
        CountDownLatch reading = new CountDownLatch(1);
        signer.documentDetailsDelay = reading;
        try {
            assertStatus(200, post(concludedEvent(documentId), token(), "/webhook", 5000));
            assertFalse(printed().contains("Document " + documentId + " is concluded"));
        } finally {
            reading.countDown();
            signer.documentDetailsDelay = null;
        }
        waitFor("Document " + documentId + " is concluded", 1);
    }

    @Test
    public void concludedEventIsHandledAfterReadingTheDocumentAsConcluded() throws Exception {
        String documentId = newId();
        signer.documentStatus = "Concluded";

        assertStatus(200, post(concludedEvent(documentId)));

        waitFor("Document " + documentId + " is concluded", 1);
        assertEquals(1, signer.requestsTo("GET", "/api/documents/" + documentId).size());
    }

    @Test
    public void concludedEventForADocumentStillPendingDoesNothing() throws Exception {
        String documentId = newId();
        signer.documentStatus = "Pending";

        assertStatus(200, post(concludedEvent(documentId)));

        String printed = waitFor(String.format("Document %s is Pending, not Concluded: nothing to do yet", documentId), 1);
        assertFalse(printed.contains("Document " + documentId + " is concluded"));
    }

    @Test
    public void deletedEventIsKeyedOnAllTheDocumentsItLists() throws Exception {
        String first = newId();
        String second = newId();

        int status = post(String.format("{ \"type\": \"DocumentDeleted\", \"data\": { \"documents\": [ { \"id\": \"%s\" }, { \"id\": \"%s\" } ] } }", first, second));

        assertStatus(200, status);
        waitFor("Accepted: DocumentDeleted:" + first + "," + second, 1);
        assertNoErrorAbout(first, waitForEarlierEvents());
    }

    @Test
    public void pausedEventIsAcceptedThoughTheClientDoesNotKnowItsType() throws Exception {
        String documentId = newId();

        int status = post(String.format("{ \"type\": \"DocumentPaused\", \"data\": { \"id\": \"%s\", \"name\": \"sample.pdf\" } }", documentId));

        assertStatus(200, status);
        waitFor("Accepted: DocumentPaused:" + documentId, 1);
        waitFor("This sample does not handle the event DocumentPaused:" + documentId, 1);
        assertNoErrorAbout(documentId, waitForEarlierEvents());
    }

    @Test
    public void eventThatFailsToProcessIsPrintedAndTheNextOneIsStillHandled() throws Exception {
        String documentId = newId();
        signer.documentDetailsError = 503;
        try {
            assertStatus(200, post(concludedEvent(documentId)));

            waitFor("Could not process DocumentConcluded:" + documentId, 1);
        } finally {
            signer.documentDetailsError = 0;
        }
        waitForEarlierEvents();
    }

    private void assertTwoParticipantsAreTwoEvents(String type, String part, String what) throws Exception {
        String documentId = newId();
        String first = newId();
        String second = newId();

        post(participantEvent(type, part, documentId, first));
        post(participantEvent(type, part, documentId, second));

        waitFor("Accepted: " + type + ":" + documentId + ":" + first, 1);
        waitFor("Accepted: " + type + ":" + documentId + ":" + second, 1);
        assertNoErrorAbout(documentId, waitFor("Document " + documentId + " is " + what, 2));
    }

    // A rejected event must leave no trace: sent again with the token, it is new, not a repeat, and
    // it is handled once.
    private void assertTakenAsNewAndHandledOnce(String documentId, String flowActionId) throws Exception {
        assertStatus(200, post(signedEvent(documentId, flowActionId)));
        waitFor("Accepted: DocumentSigned:" + documentId + ":" + flowActionId, 1);
        String printed = waitForEarlierEvents();
        assertEquals(printed, 1, count(printed, "Document " + documentId + " is Signed"));
    }

    // Sends a signed event, waits until it is handled, and returns all the endpoint printed. The
    // endpoint processes events one at a time, in the order they arrive (a single-thread executor),
    // so the events sent before it are done too, even those HandleWebhook prints nothing for.
    private static String waitForEarlierEvents() throws Exception {
        String documentId = newId();
        post(signedEvent(documentId, newId()));
        return waitFor("Document " + documentId + " is Signed", 1);
    }

    // Looks only for this test's document, so that another test's failure doesn't fail this one.
    private static void assertNoErrorAbout(String documentId, String printed) {
        assertFalse(printed, Pattern.compile("Could not process [^\\n]*" + documentId).matcher(printed).find());
    }

    // Shows what the endpoint printed when the status differs.
    private static void assertStatus(int expected, int status) {
        assertEquals("The endpoint printed:\n" + printed(), expected, status);
    }

    private static String token() {
        return System.getenv("SIGNER_WEBHOOK_TOKEN");
    }

    private static int post(String body) throws IOException {
        return post(body, token(), "/webhook");
    }

    private static int post(String body, String token, String path) throws IOException {
        return post(body, token, path, 30000);
    }

    private static int post(String body, String token, String path, int timeout) throws IOException {
        return send("POST", path, token, body, timeout);
    }

    // Sends a request, with the token and body unless they are null, and returns the status code of
    // the answer. HttpURLConnection waits forever by default, so it has a timeout.
    private static int send(String method, String path, String token, String body, int timeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + path).openConnection();
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setRequestMethod(method);
        if (token != null) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        int status = connection.getResponseCode();
        connection.disconnect();
        return status;
    }

    private static String printed() {
        synchronized (output) {
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static int count(String text, String part) {
        return text.split(Pattern.quote(part), -1).length - 1;
    }

    // Waits until the endpoint has printed the text the given number of times, and returns all it printed.
    private static String waitFor(String text, int times) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 10000;
        String printed;
        while (count(printed = printed(), text) < times) {
            if (System.currentTimeMillis() > deadline) {
                fail(String.format("The endpoint did not print \"%s\" %d time(s). It printed:%n%s", text, times, printed));
            }
            Thread.sleep(20);
        }
        return printed;
    }

    private static String signedEvent(String documentId, String flowActionId) {
        return participantEvent("DocumentSigned", "signature", documentId, flowActionId);
    }

    // An event about one participant's action: "part" is "signature", "approval" or "refusal".
    private static String participantEvent(String type, String part, String documentId, String flowActionId) {
        return String.format("{ \"type\": \"%s\", \"data\": { \"id\": \"%s\", \"name\": \"sample.pdf\", \"%s\": "
                + "{ \"flowActionId\": \"%s\", \"name\": \"Jack Bauer\", \"identifier\": \"75502846369\", \"emailAddress\": \"jack.bauer@mailinator.com\" } } }",
                type, documentId, part, flowActionId);
    }

    private static String concludedEvent(String documentId) {
        return String.format("{ \"type\": \"DocumentConcluded\", \"data\": { \"id\": \"%s\", \"name\": \"sample.pdf\" } }", documentId);
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
