import * as assert from "assert";
import { ChildProcess, spawn } from "child_process";
import { randomUUID } from "crypto";
import * as http from "http";
import * as net from "net";
import * as path from "path";
import { after, before, describe, it } from "node:test";
import { FakeSigner } from "./fakeSigner";

const token = "webhook-tests-token";

/**
 * The webhook endpoint's rules: check the token, answer at once, ignore repeated events, and read
 * the document back before acting on DocumentConcluded. scenarios/webhookEndpointScenario.ts runs
 * once, in its own process as `npx ts-node` runs it, on a free port and with its own fake Signer;
 * the tests read what it prints.
 */
describe("webhook endpoint", () => {
  let signer: FakeSigner;
  let endpoint: ChildProcess;
  let port: number;
  // The loopback address the endpoint listens on: it listens on "localhost", which Node.js 18 may
  // take as ::1 alone, while its clients connect to 127.0.0.1.
  let host: string;
  let output = "";

  before(async () => {
    signer = await FakeSigner.start();
    port = await freePort();
    // Runs ts-node's own entry point, as npx ts-node does. (Node 22.18 and later strip the types of a
    // .ts entry file themselves, so "node --require ts-node/register" would bypass ts-node.)
    endpoint = spawn(process.execPath, [require.resolve("ts-node/dist/bin"), path.join("scenarios", "webhookEndpointScenario.ts")], {
      cwd: path.join(__dirname, ".."),
      env: {
        ...process.env,
        SIGNER_ENDPOINT: signer.url,
        SIGNER_API_KEY: "Node Tests|0123456789abcdef",
        SIGNER_WEBHOOK_PORT: String(port),
        SIGNER_WEBHOOK_TOKEN: token,
      },
    });
    endpoint.stdout!.on("data", (chunk: Buffer) => (output += chunk.toString("utf8")));
    endpoint.stderr!.on("data", (chunk: Buffer) => (output += chunk.toString("utf8")));
    // ts-node compiles the scenario first.
    await waitFor("Listening on", 1, 60000);
    host = (await accepts("127.0.0.1", port)) ? "127.0.0.1" : "::1";
  });

  after(async () => {
    endpoint.kill();
    await signer.close();
  });

  it("answers 401 to an event without the token", async () => {
    const response = await post(signedEvent(randomUUID(), randomUUID()), { token: null });

    assert.strictEqual(response.status, 401);
  });

  it("answers 401 to an event with another token", async () => {
    const response = await post(signedEvent(randomUUID(), randomUUID()), { token: "not-the-token" });

    assert.strictEqual(response.status, 401);
  });

  it("answers 404 to a GET and to other paths", async () => {
    assert.strictEqual((await send("GET", "/webhook", {})).status, 404);
    assert.strictEqual((await post(signedEvent(randomUUID(), randomUUID()), { path: "/other" })).status, 404);
  });

  for (const body of ["not json", '{ "data": { "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6" } }']) {
    it(`answers 400 to a body that is not a webhook: ${body}`, async () => {
      assert.strictEqual((await post(body)).status, 400);
    });
  }

  it("accepts a signed event, then handles it", async () => {
    const documentId = randomUUID();
    const flowActionId = randomUUID();

    const response = await post(signedEvent(documentId, flowActionId));

    assert.strictEqual(response.status, 200);
    await waitFor(`Accepted: DocumentSigned:${documentId}:${flowActionId}`);
    await waitFor(`Document ${documentId} is signed`);
  });

  it("answers 200 to a repeated event, but handles it once", async () => {
    const documentId = randomUUID();
    const flowActionId = randomUUID();

    await post(signedEvent(documentId, flowActionId));
    await waitFor(`Document ${documentId} is signed`);
    const repeated = await post(signedEvent(documentId, flowActionId));

    assert.strictEqual(repeated.status, 200);
    await waitFor(`Ignored a repeated event: DocumentSigned:${documentId}:${flowActionId}`);
    assert.strictEqual(count(`Document ${documentId} is signed`), 1);
  });

  // The flow action id is in "signature", "approval" or "refusal", by event type; without it the
  // second participant's event would look like a repeat of the first and be dropped.
  for (const [type, part, what] of [
    ["DocumentSigned", "signature", "signed"],
    ["DocumentApproved", "approval", "approved"],
    ["DocumentRefused", "refusal", "refused"],
  ]) {
    it(`takes the ${type} events of two participants as two events`, async () => {
      const documentId = randomUUID();
      const first = randomUUID();
      const second = randomUUID();

      await post(participantEvent(type, part, documentId, first));
      await post(participantEvent(type, part, documentId, second));

      await waitFor(`Accepted: ${type}:${documentId}:${first}`);
      await waitFor(`Accepted: ${type}:${documentId}:${second}`);
      await waitFor(`Document ${documentId} is ${what}`, 2);
      assert.doesNotMatch(output, /Could not process/);
    });
  }

  it("answers an event before it processes it", async () => {
    const documentId = randomUUID();
    signer.documentStatus = "Concluded";
    // Processing a DocumentConcluded event reads the document; hold that read until the answer arrives.
    let release!: () => void;
    signer.documentDetailsDelay = new Promise<void>((resolve) => (release = resolve));
    try {
      const response = await post(concludedEvent(documentId), { timeout: 5000 });

      assert.strictEqual(response.status, 200);
      assert.doesNotMatch(output, new RegExp(`Document ${documentId} is concluded`));
    } finally {
      release();
      signer.documentDetailsDelay = null;
    }
    await waitFor(`Document ${documentId} is concluded`);
  });

  it("handles a concluded event after reading the document as concluded", async () => {
    const documentId = randomUUID();
    signer.documentStatus = "Concluded";

    assert.strictEqual((await post(concludedEvent(documentId))).status, 200);

    await waitFor(`Document ${documentId} is concluded`);
    assert.strictEqual(signer.requestsTo("GET", `/api/documents/${documentId}`).length, 1);
  });

  it("does nothing with a concluded event for a document still pending", async () => {
    const documentId = randomUUID();
    signer.documentStatus = "Pending";

    assert.strictEqual((await post(concludedEvent(documentId))).status, 200);

    await waitFor(`Document ${documentId} is Pending, not Concluded: nothing to do yet`);
    assert.doesNotMatch(output, new RegExp(`Document ${documentId} is concluded`));
  });

  it("keys a deleted event on all the documents it lists", async () => {
    const first = randomUUID();
    const second = randomUUID();

    const response = await post(JSON.stringify({ type: "DocumentDeleted", data: { documents: [{ id: first }, { id: second }] } }));

    assert.strictEqual(response.status, 200);
    await waitFor(`Accepted: DocumentDeleted:${first},${second}`);
    await assertProcessedWithoutError();
  });

  it("accepts a paused event, which handleWebhook does not handle", async () => {
    const documentId = randomUUID();

    const response = await post(JSON.stringify({ type: "DocumentPaused", data: { id: documentId, name: "sample.pdf" } }));

    assert.strictEqual(response.status, 200);
    await waitFor(`Accepted: DocumentPaused:${documentId}`);
    await assertProcessedWithoutError();
  });

  // handleWebhook prints nothing for an event it doesn't handle. The endpoint processes events in
  // the order they arrive, so once a signed event sent afterwards is handled, the earlier one is
  // done too.
  async function assertProcessedWithoutError() {
    const documentId = randomUUID();
    await post(signedEvent(documentId, randomUUID()));
    await waitFor(`Document ${documentId} is signed`);
    assert.doesNotMatch(output, /Could not process/);
  }

  async function post(body: string, options: { token?: string | null; path?: string; timeout?: number } = {}) {
    const sentToken = options.token === undefined ? token : options.token;
    const headers: Record<string, string> = { "Content-Type": "application/json" };
    if (sentToken !== null) {
      headers["Authorization"] = `Bearer ${sentToken}`;
    }
    return send("POST", options.path ?? "/webhook", headers, body, options.timeout);
  }

  // Sends a request to the endpoint and returns the status of the answer.
  function send(method: string, path: string, headers: Record<string, string>, body?: string, timeout = 30000) {
    return new Promise<{ status: number }>((resolve, reject) => {
      const request = http.request({ host: host, port: port, method: method, path: path, headers: headers, timeout: timeout }, (response) => {
        response.resume();
        response.on("end", () => resolve({ status: response.statusCode ?? 0 }));
      });
      request.on("timeout", () => request.destroy(new Error(`No answer to ${method} ${path} in ${timeout} ms`)));
      request.on("error", reject);
      request.end(body);
    });
  }

  function count(text: string): number {
    return output.split(text).length - 1;
  }

  // Waits until the endpoint has printed the text the given number of times.
  async function waitFor(text: string, times = 1, timeout = 10000) {
    const deadline = Date.now() + timeout;
    while (count(text) < times) {
      if (Date.now() > deadline || endpoint.exitCode !== null) {
        assert.fail(`The endpoint did not print "${text}" ${times} time(s). It printed:\n${output}`);
      }
      await new Promise((resolve) => setTimeout(resolve, 20));
    }
  }
});

function signedEvent(documentId: string, flowActionId: string): string {
  return participantEvent("DocumentSigned", "signature", documentId, flowActionId);
}

// An event about one participant's action: "part" is "signature", "approval" or "refusal".
function participantEvent(type: string, part: string, documentId: string, flowActionId: string): string {
  return JSON.stringify({
    type: type,
    data: {
      id: documentId,
      name: "sample.pdf",
      [part]: { flowActionId: flowActionId, name: "Jack Bauer", identifier: "75502846369", emailAddress: "jack.bauer@mailinator.com" },
    },
  });
}

function concludedEvent(documentId: string): string {
  return JSON.stringify({ type: "DocumentConcluded", data: { id: documentId, name: "sample.pdf" } });
}

// Whether something accepts connections on the address and port.
function accepts(host: string, port: number): Promise<boolean> {
  return new Promise((resolve) => {
    const socket = net.connect({ host: host, port: port });
    socket.on("connect", () => {
      socket.destroy();
      resolve(true);
    });
    socket.on("error", () => resolve(false));
  });
}

function freePort(): Promise<number> {
  return new Promise((resolve, reject) => {
    const server = net.createServer();
    server.on("error", reject);
    server.listen(0, "localhost", () => {
      const port = (server.address() as net.AddressInfo).port;
      server.close(() => resolve(port));
    });
  });
}
