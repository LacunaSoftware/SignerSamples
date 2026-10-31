import * as assert from "assert";
import { after, before, beforeEach, describe, it } from "node:test";
import { FakeSigner } from "./fakeSigner";

const apiKey = "Node Tests|0123456789abcdef";

/**
 * callSignerApi and requiredField, from scenarios/scenario.ts, against a fake Signer. scenario.ts
 * reads SIGNER_ENDPOINT and SIGNER_API_KEY when it loads, so it is loaded after they are set.
 */
describe("callSignerApi", () => {
  let signer: FakeSigner;
  let scenario: typeof import("../scenarios/scenario");

  before(async () => {
    signer = await FakeSigner.start();
    // With a trailing slash, which callSignerApi drops.
    process.env.SIGNER_ENDPOINT = signer.url + "/";
    process.env.SIGNER_API_KEY = apiKey;
    try {
      scenario = require("../scenarios/scenario");
    } catch (error) {
      // When "before" fails, node:test may skip "after", and the open fake Signer would keep the
      // tests running.
      await signer.close();
      throw error;
    }
  });

  after(() => signer.close());

  beforeEach(() => {
    signer.requests.length = 0;
    signer.answer = null;
  });

  it("sends the body as JSON with the API key and returns the parsed answer", async () => {
    signer.answer = { status: 200, body: '{"disableRefusal":true}' };

    const result = await scenario.callSignerApi("POST", "/api/documents", { description: "ação" });

    assert.deepStrictEqual(result, { disableRefusal: true });
    const [request] = signer.requestsTo("POST", "/api/documents");
    assert.strictEqual(request.apiKey, apiKey);
    assert.match(request.contentType ?? "", /^application\/json/);
    assert.deepStrictEqual(JSON.parse(request.body), { description: "ação" });
  });

  it("sends no body when given none, and returns null for an empty answer", async () => {
    signer.answer = { status: 200, body: "" };
    const path = "/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/resume";

    const result = await scenario.callSignerApi("POST", path);

    assert.strictEqual(result, null);
    assert.strictEqual(signer.requestsTo("POST", path)[0].body, "");
  });

  it("throws with the status and body of an error answer", async () => {
    const body = '{"code":"DocumentNotPending","message":"The document is not pending"}';
    signer.answer = { status: 422, body: body };
    const path = "/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/pause";

    await assert.rejects(scenario.callSignerApi("POST", path, {}), {
      message: `POST ${path} answered 422: ${body}`,
    });
    // The fake answers every path alike, and the message is built from the arguments: check the
    // request itself.
    assert.strictEqual(signer.requestsTo("POST", path).length, 1);
  });

  it("throws on a success answer that is not JSON", async () => {
    signer.answer = { status: 200, body: "%PDF-1.4 fake" };
    const path = "/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/content";

    await assert.rejects(scenario.callSignerApi("GET", path), {
      message: `GET ${path} answered 200 with invalid JSON: %PDF-1.4 fake`,
    });
    const requests = signer.requestsTo("GET", path);
    assert.strictEqual(requests.length, 1);
    assert.strictEqual(requests[0].body, "");
  });

  describe("requiredField", () => {
    it("returns the field, even when it is false or zero", () => {
      assert.strictEqual(scenario.requiredField({ disableRefusal: false }, "disableRefusal"), false);
      assert.strictEqual(scenario.requiredField({ step: 0 }, "step"), 0);
    });

    it("throws when Signer left the field out or sent null", () => {
      const message = "Signer did not return timestampEnabled: this instance may predate the feature";
      assert.throws(() => scenario.requiredField({}, "timestampEnabled"), { message: message });
      assert.throws(() => scenario.requiredField({ timestampEnabled: null }, "timestampEnabled"), { message: message });
    });
  });
});
