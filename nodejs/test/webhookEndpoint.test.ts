import * as assert from "assert";
import { ChildProcess, spawn } from "child_process";
import { randomUUID } from "crypto";
import * as fs from "fs";
import * as http from "http";
import * as net from "net";
import * as os from "os";
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
  let signer: FakeSigner | undefined;
  let endpoint: ChildProcess | undefined;
  let port: number;
  // The loopback address the endpoint listens on: it listens on "localhost", which Node.js 18 may
  // resolve to ::1 rather than 127.0.0.1.
  let host: string;
  // What the endpoint prints goes to one file, stdout and stderr together, so that it stays in order.
  let outputFolder: string | undefined;
  let outputFile: string;

  before(async () => {
    try {
      signer = await FakeSigner.start();
      port = await freePort();
      outputFolder = fs.mkdtempSync(path.join(os.tmpdir(), "webhook-tests-"));
      outputFile = path.join(outputFolder, "output.txt");
      const outputFd = fs.openSync(outputFile, "a");
      try {
        // Runs ts-node's own entry point, as npx ts-node does. Node 22.18 and later strip the types
        // of a .ts entry file themselves, so "node --require ts-node/register" would bypass ts-node,
        // and that stripping rejects TypeScript-only syntax such as parameter properties.
        endpoint = spawn(process.execPath, [require.resolve("ts-node/dist/bin"), path.join("scenarios", "webhookEndpointScenario.ts")], {
          cwd: path.join(__dirname, ".."),
          env: {
            ...process.env,
            SIGNER_ENDPOINT: signer.url,
            SIGNER_API_KEY: "Node Tests|0123456789abcdef",
            SIGNER_WEBHOOK_PORT: String(port),
            SIGNER_WEBHOOK_TOKEN: token,
          },
          stdio: ["ignore", outputFd, outputFd],
        });
      } finally {
        // The endpoint has its own copy.
        fs.closeSync(outputFd);
      }
      // ts-node compiles the scenario first.
      await waitFor("Listening on", 1, 60000);
      host = (await accepts("127.0.0.1", port)) ? "127.0.0.1" : "::1";
    } catch (error) {
      // When "before" fails, node:test may skip "after", and the fake Signer and the endpoint
      // would keep the tests running.
      await cleanUp();
      throw error;
    }
  });

  after(cleanUp);

  async function cleanUp() {
    if (endpoint) {
      const exited = isRunning(endpoint) ? new Promise((resolve) => endpoint!.once("exit", resolve)) : null;
      endpoint.kill();
      await exited;
      endpoint = undefined;
    }
    await signer?.close();
    signer = undefined;
    if (outputFolder) {
      fs.rmSync(outputFolder, { recursive: true, force: true });
      outputFolder = undefined;
    }
  }

  it("answers 401 to an event without the token, and doesn't record it", async () => {
    const documentId = randomUUID();
    const flowActionId = randomUUID();

    assertStatus(await post(signedEvent(documentId, flowActionId), { token: null }), 401);

    await assertTakenAsNewAndHandledOnce(documentId, flowActionId);
  });

  it("answers 401 to an event with another token, and doesn't record it", async () => {
    const documentId = randomUUID();
    const flowActionId = randomUUID();

    assertStatus(await post(signedEvent(documentId, flowActionId), { token: "not-the-token" }), 401);

    await assertTakenAsNewAndHandledOnce(documentId, flowActionId);
  });

  it("answers 404 to a GET and to other paths", async () => {
    assertStatus(await send("GET", "/webhook", {}), 404);
    assertStatus(await post(signedEvent(randomUUID(), randomUUID()), { path: "/other" }), 404);
    assertStatus(await post(signedEvent(randomUUID(), randomUUID()), { path: "/webhook/other" }), 404);
  });

  for (const body of ["not json", '{ "data": { "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6" } }']) {
    it(`answers 400 to a body that is not a webhook: ${body}`, async () => {
      assertStatus(await post(body), 400);
    });
  }

  it("accepts a signed event, then handles it", async () => {
    const documentId = randomUUID();
    const flowActionId = randomUUID();

    const response = await post(signedEvent(documentId, flowActionId));

    assertStatus(response, 200);
    await waitFor(`Accepted: DocumentSigned:${documentId}:${flowActionId}`);
    await waitFor(`Document ${documentId} is signed`);
  });

  it("answers 200 to a repeated event, but handles it once", async () => {
    const documentId = randomUUID();
    const flowActionId = randomUUID();

    await post(signedEvent(documentId, flowActionId));
    await waitFor(`Document ${documentId} is signed`);
    const repeated = await post(signedEvent(documentId, flowActionId));

    assertStatus(repeated, 200);
    await waitFor(`Ignored a repeated event: DocumentSigned:${documentId}:${flowActionId}`);
    await waitForEarlierEvents();
    assert.strictEqual(count(`Document ${documentId} is signed`), 1, output());
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
      assertNoErrorAbout(documentId);
    });
  }

  it("answers an event before it processes it", async () => {
    const documentId = randomUUID();
    signer!.documentStatus = "Concluded";
    // Processing a DocumentConcluded event reads the document; hold that read until the answer arrives.
    let release!: () => void;
    signer!.documentDetailsDelay = new Promise<void>((resolve) => (release = resolve));
    try {
      const response = await post(concludedEvent(documentId), { timeout: 5000 });

      assertStatus(response, 200);
      assert.doesNotMatch(output(), new RegExp(`Document ${documentId} is concluded`));
    } finally {
      release();
      signer!.documentDetailsDelay = null;
    }
    await waitFor(`Document ${documentId} is concluded`);
  });

  it("handles a concluded event after reading the document as concluded", async () => {
    const documentId = randomUUID();
    signer!.documentStatus = "Concluded";

    assertStatus(await post(concludedEvent(documentId)), 200);

    await waitFor(`Document ${documentId} is concluded`);
    assert.strictEqual(signer!.requestsTo("GET", `/api/documents/${documentId}`).length, 1);
  });

  it("does nothing with a concluded event for a document still pending", async () => {
    const documentId = randomUUID();
    signer!.documentStatus = "Pending";

    assertStatus(await post(concludedEvent(documentId)), 200);

    await waitFor(`Document ${documentId} is Pending, not Concluded: nothing to do yet`);
    assert.doesNotMatch(output(), new RegExp(`Document ${documentId} is concluded`));
  });

  it("keys a deleted event on all the documents it lists", async () => {
    const first = randomUUID();
    const second = randomUUID();

    const response = await post(JSON.stringify({ type: "DocumentDeleted", data: { documents: [{ id: first }, { id: second }] } }));

    assertStatus(response, 200);
    await waitFor(`Accepted: DocumentDeleted:${first},${second}`);
    await waitForEarlierEvents();
    assertNoErrorAbout(first);
  });

  it("accepts a paused event, which handleWebhook does not handle", async () => {
    const documentId = randomUUID();

    const response = await post(JSON.stringify({ type: "DocumentPaused", data: { id: documentId, name: "sample.pdf" } }));

    assertStatus(response, 200);
    await waitFor(`Accepted: DocumentPaused:${documentId}`);
    await waitForEarlierEvents();
    assertNoErrorAbout(documentId);
  });

  it("prints an event it could not process, and still handles the next one", async () => {
    const documentId = randomUUID();
    signer!.documentDetailsError = 503;
    try {
      assertStatus(await post(concludedEvent(documentId)), 200);

      await waitFor(`Could not process DocumentConcluded:${documentId}`);
    } finally {
      signer!.documentDetailsError = null;
    }
    await waitForEarlierEvents();
  });

  // A rejected event must leave no trace: sent again with the token, it is new, not a repeat, and
  // it is handled once.
  async function assertTakenAsNewAndHandledOnce(documentId: string, flowActionId: string) {
    assertStatus(await post(signedEvent(documentId, flowActionId)), 200);
    await waitFor(`Accepted: DocumentSigned:${documentId}:${flowActionId}`);
    await waitForEarlierEvents();
    assert.strictEqual(count(`Document ${documentId} is signed`), 1, output());
  }

  // Sends a signed event and waits until it is handled. The endpoint doesn't wait for one event to
  // be processed before it reads the next, but for every event other than DocumentConcluded,
  // processEvent does all its work, and prints any error, before the request that brought the event
  // is done. So once the signed event sent afterwards is handled, what the earlier ones printed is
  // in the output.
  async function waitForEarlierEvents() {
    const documentId = randomUUID();
    await post(signedEvent(documentId, randomUUID()));
    await waitFor(`Document ${documentId} is signed`);
  }

  // Looks only for this test's document, so that another test's failure doesn't fail this one.
  function assertNoErrorAbout(documentId: string) {
    const printed = output();
    assert.doesNotMatch(printed, new RegExp(`Could not process [^\\n]*${documentId}`), printed);
  }

  // Shows what the endpoint printed when the status differs.
  function assertStatus(response: { status: number }, expected: number) {
    assert.strictEqual(response.status, expected, `Expected ${expected}, got ${response.status}. The endpoint printed:\n${output()}`);
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

  function output(): string {
    return fs.readFileSync(outputFile, "utf8");
  }

  function count(text: string): number {
    return output().split(text).length - 1;
  }

  // Waits until the endpoint has printed the text the given number of times.
  async function waitFor(text: string, times = 1, timeout = 10000) {
    const deadline = Date.now() + timeout;
    while (count(text) < times) {
      if (Date.now() > deadline || !isRunning(endpoint!)) {
        assert.fail(`The endpoint did not print "${text}" ${times} time(s). It printed:\n${output()}`);
      }
      await new Promise((resolve) => setTimeout(resolve, 20));
    }
  }
});

// Whether the process hasn't exited, by itself (exitCode) or killed by a signal (signalCode).
function isRunning(child: ChildProcess): boolean {
  return child.exitCode === null && child.signalCode === null;
}

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

// A port free on both 127.0.0.1 and ::1, as the endpoint may take either: "::" takes IPv4
// connections too. Without IPv6, a port free on 127.0.0.1.
function freePort(host = "::"): Promise<number> {
  return new Promise((resolve, reject) => {
    const server = net.createServer();
    server.on("error", (error) => (host === "::" ? freePort("127.0.0.1").then(resolve, reject) : reject(error)));
    server.listen(0, host, () => {
      const port = (server.address() as net.AddressInfo).port;
      server.close(() => resolve(port));
    });
  });
}
