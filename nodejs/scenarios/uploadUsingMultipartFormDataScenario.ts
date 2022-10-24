import { UploadApi } from "signer-node-client";
import { config } from "./scenario";
import fs from 'fs';

const uploadApi = new UploadApi(config);
const filepath = "..\\samples\\sample.pdf"
const fileName = "sample.pdf"

const file = fs.readFileSync(filepath);

uploadApi.apiUploadsPostForm(fileName, filepath, "application/pdf")
.then((res)=>{
    console.log(res.data);
})
.catch((err) => {
    console.log(err)
})