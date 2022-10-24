"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
const fs_1 = __importDefault(require("fs"));
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const filepath = "..\\samples\\sample.pdf";
const fileName = "sample.pdf";
const file = fs_1.default.readFileSync(filepath);
uploadApi.apiUploadsPostForm(fileName, filepath, "application/pdf")
    .then((res) => {
    console.log(res.data);
})
    .catch((err) => {
    console.log(err);
});
