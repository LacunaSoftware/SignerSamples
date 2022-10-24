"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.CreateDocument = exports.config = void 0;
const signer_node_client_1 = require("signer-node-client");
exports.config = {
    apiKey: "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99",
    basePath: "https://signer-lac.azurewebsites.net"
};
const uploadApi = new signer_node_client_1.UploadApi(exports.config);
const documentApi = new signer_node_client_1.DocumentsApi(exports.config);
const filepath = "..\\samples\\sample.pdf";
const fileName = "sample.pdf";
function CreateDocument() {
    return __awaiter(this, void 0, void 0, function* () {
        const response = yield uploadApi.apiUploadsBytesPost({ bytes: (0, signer_node_client_1.getBase64)(filepath) });
        console.log(response.data);
        const fileUploadModel = {
            displayName: "Check Status Sample",
            id: response.data.id,
            contentType: "application/pdf",
            name: fileName
        };
        const participant = {
            name: "Jack Bauer",
            email: "jack.bauer@mailnator.com",
            identifier: "75502846369"
        };
        const flowAction = {
            type: signer_node_client_1.FlowActionType.Signer,
            user: participant
        };
        const files = new Array(fileUploadModel);
        const flowActions = new Array(flowAction);
        const documentRequest = {
            files: files,
            flowActions: flowActions
        };
        const result = yield documentApi.apiDocumentsPost(documentRequest);
        return result.data[0];
    });
}
exports.CreateDocument = CreateDocument;
