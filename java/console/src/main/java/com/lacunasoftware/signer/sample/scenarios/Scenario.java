package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.users.ParticipantUserModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.SignerClient;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.sample.Util;

public abstract class Scenario {

    protected SignerClient signerClient;

    // Kept for callSignerApi.
    private String domain;
    private String token;

    public void Init() throws URISyntaxException {
        // Signer instance URL. Without SIGNER_ENDPOINT, the samples use Lacuna's Signer sandbox.
        domain = getEnvOrDefault("SIGNER_ENDPOINT", "https://signer-lac.azurewebsites.net");
        // Application credentials token, "<application name>|<key>". Without SIGNER_API_KEY, the
        // samples use a shared key of the sandbox.
        token = getEnvOrDefault("SIGNER_API_KEY", "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99");
		signerClient = new SignerClient(domain, token);
    }

    // Calls the Signer REST API directly, for the features that signer-client 2.13.0 doesn't have yet,
    // such as pausing a document. The body is sent as JSON, with the field names and enum values of the
    // API reference (https://www.dropsigner.com/swagger); pass null for no body. Returns the JSON of the
    // response, or null when it has none. Throws on an answer other than 2xx, with the body, which for
    // a 422 holds Signer's error code and message.
    protected JsonElement callSignerApi(String method, String path, JsonElement body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(domain.replaceAll("/$", "") + path).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("X-Api-Key", token);
        if (!"GET".equals(method)) {
            // A POST or PUT without a body still sends Content-Length: 0.
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream output = connection.getOutputStream()) {
                if (body != null) {
                    output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        int status = connection.getResponseCode();
        InputStream stream = status < 400 ? connection.getInputStream() : connection.getErrorStream();
        String content = "";
        if (stream != null) {
            try (Scanner scanner = new Scanner(stream, "UTF-8")) {
                scanner.useDelimiter("\\A");
                content = scanner.hasNext() ? scanner.next() : "";
            }
        }
        if (status < 200 || status >= 300) {
            throw new IOException(String.format("%s %s answered %d: %s", method, path, status, content));
        }
        return content.isEmpty() ? null : new JsonParser().parse(content);
    }

    // The JSON of an uploaded file, for the "files" of a create request sent with callSignerApi.
    protected static JsonObject fileJson(UploadModel uploadModel, String displayName) {
        JsonObject file = new JsonObject();
        file.addProperty("id", uploadModel.getId());
        file.addProperty("name", uploadModel.getName());
        file.addProperty("contentType", uploadModel.getContentType());
        file.addProperty("displayName", displayName);
        return file;
    }

    // The JSON of a signer's flow action, for the "flowActions" of a create request sent with
    // callSignerApi.
    protected static JsonObject signerJson(String name, String email, String identifier) {
        JsonObject user = new JsonObject();
        user.addProperty("name", name);
        user.addProperty("email", email);
        user.addProperty("identifier", identifier);
        JsonObject flowAction = new JsonObject();
        flowAction.addProperty("type", "Signer");
        flowAction.add("user", user);
        return flowAction;
    }

    // Returns the value of an environment variable, or the fallback when it is unset or empty.
    protected static String getEnvOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isEmpty() ? fallback : value;
    }

    // Creates a generic document, useful for certain scenarios.
    public abstract void Run() throws IOException, RestException, Exception;

    protected CreateDocumentResult createDocument() throws IOException, RestException {
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");
        
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Simple Document");

        ParticipantUserModel user = new ParticipantUserModel();
		user.setName("Jack Bauer");
		user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(new ArrayList<FileUploadModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(fileUploadModelBuilder.toModel());
            }
        });
        documentRequest.setFlowActions(new ArrayList<FlowActionCreateModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(flowActionCreateModel);
            }          
        });
        List<CreateDocumentResult> documentResults = signerClient.createDocument(documentRequest);

        return documentResults.get(0);
    }
}