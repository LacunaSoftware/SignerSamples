import * as http from "http";
import { AddressInfo } from "net";

// A request the fake Signer received.
export interface RecordedRequest {
  method: string;
  path: string;
  apiKey: string | undefined;
  contentType: string | undefined;
  body: string;
}

// An answer the fake Signer sends: a status and a body, sent as is.
export interface Answer {
  status: number;
  body: string;
}

/**
 * A fake Signer on localhost, for the tests: it records every request and answers
 * GET /api/documents/{id} with a document whose status is documentStatus, or with the error
 * status in documentDetailsError. Set answer to send something else to every other request.
 */
export class FakeSigner {
  readonly requests: RecordedRequest[] = [];
  documentStatus = "Pending";
  // Holds the answer to GET /api/documents/{id} until it resolves.
  documentDetailsDelay: Promise<void> | null = null;
  documentDetailsError: number | null = null;
  answer: Answer | null = null;

  private constructor(private readonly server: http.Server) {}

  static async start(): Promise<FakeSigner> {
    const server = http.createServer();
    const signer = new FakeSigner(server);
    server.on("request", (req, res) => signer.handle(req, res));
    // An IP address, not "localhost": on Node.js 18, "localhost" may resolve to ::1 for the server
    // and to 127.0.0.1 for fetch.
    await new Promise<void>((resolve) => server.listen(0, "127.0.0.1", resolve));
    return signer;
  }

  get url(): string {
    return `http://127.0.0.1:${(this.server.address() as AddressInfo).port}`;
  }

  // The requests to one method and path.
  requestsTo(method: string, path: string): RecordedRequest[] {
    return this.requests.filter((request) => request.method === method && request.path === path);
  }

  close(): Promise<void> {
    return new Promise((resolve) => this.server.close(() => resolve()));
  }

  private handle(req: http.IncomingMessage, res: http.ServerResponse) {
    const chunks: Buffer[] = [];
    req.on("data", (chunk: Buffer) => chunks.push(chunk));
    req.on("end", async () => {
      const path = new URL(req.url ?? "/", "http://localhost").pathname;
      this.requests.push({
        method: req.method ?? "",
        path: path,
        apiKey: req.headers["x-api-key"] as string | undefined,
        contentType: req.headers["content-type"],
        body: Buffer.concat(chunks).toString("utf8"),
      });

      const document = /^\/api\/documents\/([0-9a-f-]{36})$/.exec(path);
      if (req.method === "GET" && document) {
        if (this.documentDetailsDelay) {
          await this.documentDetailsDelay;
        }
        if (this.documentDetailsError !== null) {
          res.writeHead(this.documentDetailsError, { "Content-Type": "application/json" });
          res.end(JSON.stringify({ message: "The fake Signer was told to fail this request" }));
          return;
        }
        res.writeHead(200, { "Content-Type": "application/json" });
        res.end(JSON.stringify({ id: document[1], name: "sample.pdf", status: this.documentStatus }));
        return;
      }
      const answer = this.answer ?? { status: 404, body: "" };
      res.writeHead(answer.status, { "Content-Type": "application/json" });
      res.end(answer.body);
    });
  }
}
