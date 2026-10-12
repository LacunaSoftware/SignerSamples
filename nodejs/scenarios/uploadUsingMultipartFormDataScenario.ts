import fs from 'fs';
import path from 'path';
import { UploadApi } from "signer-node-client";
import { config } from "./scenario";

const uploadApi = new UploadApi(config);
const filepath = path.join(__dirname, "..", "samples", "sample.pdf")
const fileName = "sample.pdf"

const file = fs.readFileSync(filepath);

uploadApi.apiUploadsPost({ name: fileName, file: file, contentType: "application/pdf" })
.then((res)=>{
    console.log(res.data);
})
.catch((err) => {
    console.log(err)
})