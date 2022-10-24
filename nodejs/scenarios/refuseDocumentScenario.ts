import {DocumentsApi, RefusalRefusalRequest} from 'signer-node-client';
import { config, CreateDocument } from './scenario';

/**
 * This scenario demonstrates how to refuse a document as an organization application.
 */
const docApi = new DocumentsApi(config);
CreateDocument().then((res) => {
    //1. You need a document id
    const docId = res.documentId;
    
    //2 - Create a refusal request and give it a reason
    const refuseDocument: RefusalRefusalRequest = {
        reason: "This is a document refusal"
    }
    
    //3 - Send the refusal request
    docApi.apiDocumentsIdRefusalPost(docId, refuseDocument).then((response) => {
        console.log(response.status);
    });
});
