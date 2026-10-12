import {Configuration, DocumentsCreateDocumentResult, UploadApi, FileUploadModel, UsersParticipantUserModel, FlowActionsFlowActionCreateModel, FlowActionType, DocumentsCreateDocumentRequest, DocumentsApi, getBase64 } from "signer-node-client";
import * as path from "path";

// Returns the value of an environment variable, or the fallback when it is unset or empty.
export function envOrDefault(name: string, fallback: string): string {
    return process.env[name] || fallback;
}

export const config = new Configuration({
    // Application credentials token, "<application name>|<key>". Without SIGNER_API_KEY, the
    // samples use a shared key of the sandbox.
    apiKey: envOrDefault("SIGNER_API_KEY", "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99"),
    // Signer instance URL. Without SIGNER_ENDPOINT, the samples use Lacuna's Signer sandbox.
    basePath: envOrDefault("SIGNER_ENDPOINT", "https://signer-lac.azurewebsites.net"),
})

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