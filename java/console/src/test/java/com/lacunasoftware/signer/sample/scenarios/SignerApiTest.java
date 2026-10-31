package com.lacunasoftware.signer.sample.scenarios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;

/**
 * callSignerApi and requiredField, from Scenario.java, against a fake Signer. build.gradle sets
 * SIGNER_ENDPOINT, with a trailing slash that callSignerApi drops, and SIGNER_API_KEY.
 */
public class SignerApiTest {
    // Exposes the base class's helpers to the tests.
    private static class ApiCallScenario extends Scenario {
        @Override
        public void Run() {
        }
    }

    private FakeSigner signer;
    private ApiCallScenario scenario;

    @Before
    public void setUp() throws Exception {
        signer = FakeSigner.instance();
        signer.reset();
        scenario = new ApiCallScenario();
        scenario.Init();
    }

    @Test
    public void sendsTheBodyAsJsonWithTheApiKeyAndReturnsTheParsedAnswer() throws IOException {
        signer.setAnswer(200, "{\"disableRefusal\":true}");
        JsonObject body = new JsonObject();
        body.addProperty("description", "ação");

        JsonElement result = scenario.callSignerApi("POST", "/api/documents", body);

        assertTrue(result.getAsJsonObject().get("disableRefusal").getAsBoolean());
        List<FakeSigner.Request> requests = signer.requestsTo("POST", "/api/documents");
        assertEquals(1, requests.size());
        assertEquals(System.getenv("SIGNER_API_KEY"), requests.get(0).apiKey);
        assertTrue(requests.get(0).contentType.startsWith("application/json"));
        assertEquals(body, new JsonParser().parse(requests.get(0).body));
    }

    @Test
    public void sendsNoBodyWhenGivenNoneAndReturnsNullForAnEmptyAnswer() throws IOException {
        signer.setAnswer(200, "");
        String path = "/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/resume";

        JsonElement result = scenario.callSignerApi("POST", path, null);

        assertNull(result);
        assertEquals("", signer.requestsTo("POST", path).get(0).body);
    }

    @Test
    public void throwsWithTheStatusAndBodyOfAnErrorAnswer() {
        String body = "{\"code\":\"DocumentNotPending\",\"message\":\"The document is not pending\"}";
        signer.setAnswer(422, body);
        String path = "/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/pause";

        IOException error = assertThrows(IOException.class, () -> scenario.callSignerApi("POST", path, new JsonObject()));

        assertEquals(String.format("POST %s answered 422: %s", path, body), error.getMessage());
    }

    @Test
    public void throwsOnASuccessAnswerThatIsNotJson() {
        signer.setAnswer(200, "%PDF-1.4 fake");
        String path = "/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/content";

        IOException error = assertThrows(IOException.class, () -> scenario.callSignerApi("GET", path, null));

        assertEquals(String.format("GET %s answered 200 with invalid JSON: %%PDF-1.4 fake", path), error.getMessage());
    }

    @Test
    public void requiredFieldReturnsTheFieldEvenWhenItIsFalse() {
        JsonObject parent = new JsonObject();
        parent.addProperty("disableRefusal", false);

        assertFalse(Scenario.requiredField(parent, "disableRefusal").getAsBoolean());
    }

    @Test
    public void requiredFieldThrowsWhenSignerLeftTheFieldOutOrSentNull() {
        String message = "Signer did not return timestampEnabled: this instance may predate the feature";
        JsonObject withNull = new JsonObject();
        withNull.add("timestampEnabled", JsonNull.INSTANCE);

        assertEquals(message, assertThrows(IllegalStateException.class, () -> Scenario.requiredField(new JsonObject(), "timestampEnabled")).getMessage());
        assertEquals(message, assertThrows(IllegalStateException.class, () -> Scenario.requiredField(withNull, "timestampEnabled")).getMessage());
    }
}
