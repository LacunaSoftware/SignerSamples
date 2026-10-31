import {Configuration, DocumentsCreateDocumentResult, UploadApi, FileUploadModel, UsersParticipantUserModel, FlowActionsFlowActionCreateModel, FlowActionType, DocumentsCreateDocumentRequest, DocumentsApi, getBase64 } from "signer-node-client";
import * as path from "path";

// Returns the value of an environment variable, or the fallback when it is unset or empty.
export function envOrDefault(name: string, fallback: string): string {
    return process.env[name] || fallback;
}

// Application credentials token, "<application name>|<key>". Without SIGNER_API_KEY, the
// samples use a shared key of the sandbox.
const apiKey = envOrDefault("SIGNER_API_KEY", "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99");
// Signer instance URL. Without SIGNER_ENDPOINT, the samples use Lacuna's Signer sandbox.
const basePath = envOrDefault("SIGNER_ENDPOINT", "https://signer-lac.azurewebsites.net");

export const config = new Configuration({
    apiKey: apiKey,
    basePath: basePath,
})

// Calls the Signer REST API directly, for the features that signer-node-client 1.1.0 doesn't have
// yet, such as pausing a document. The body is sent as JSON, with the field names and enum values of
// the API reference (https://www.dropsigner.com/swagger). Returns the parsed JSON of the response, or
// null when it has none. Throws on an answer other than 2xx, with the body, which for a 422 holds
// Signer's error code and message.
export async function callSignerApi(method: string, apiPath: string, body?: object): Promise<any> {
    const response = await fetch(basePath.replace(/\/$/, "") + apiPath, {
        method: method,
        headers: { "X-Api-Key": apiKey, "Content-Type": "application/json" },
        body: body === undefined ? undefined : JSON.stringify(body),
    });
    const content = await response.text();
    if (!response.ok) {
        throw new Error(`${method} ${apiPath} answered ${response.status}: ${content}`);
    }
    return content ? JSON.parse(content) : null;
}

const uploadApi = new UploadApi(config)
const documentApi = new DocumentsApi(config);
const filepath = path.join(__dirname, "..", "samples", "sample.pdf")
const fileName = "sample.pdf"

export async function CreateDocument(): Promise<DocumentsCreateDocumentResult> {
    const response = await uploadApi.apiUploadsBytesPost({bytes: getBase64(filepath)});
    console.log(response.data);

    const fileUploadModel: FileUploadModel = {
        displayName: "Check Status Sample",
        id: response.data.id,
        contentType: "application/pdf",
        name:fileName
    }
    const participant: UsersParticipantUserModel = {
        name: "Jack Bauer",
        email: "jack.bauer@mailinator.com",
        identifier: "75502846369"
    }
    const flowAction: FlowActionsFlowActionCreateModel = {
        type: FlowActionType.Signer,
        user: participant
    }
    const files = new Array(fileUploadModel);
    const flowActions = new Array(flowAction);
    const documentRequest: DocumentsCreateDocumentRequest = {
        files: files,
        flowActions: flowActions
    }
    const result = await documentApi.apiDocumentsPost(documentRequest);
    return result.data[0]
}