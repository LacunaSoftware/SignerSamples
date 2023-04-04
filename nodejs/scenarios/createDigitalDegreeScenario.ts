import {
  UploadApi,
  FileUploadModel,
  UsersParticipantUserModel,
  XmlNamespaceModel,
  FlowActionsXadesOptionsModel,
  XadesSignatureTypes,
  XadesElementIdentifierTypes,
  XadesInsertionOptions,
  FlowActionsFlowActionCreateModel,
  FlowActionType,
  DocumentsCreateDocumentRequest,
  DocumentsApi,
  getBase64,
} from "signer-node-client";
import { config } from "./scenario";


const filePath = "../../samples/sample-degree.xml";
const filename = "sample-degree.xml";
const file = getBase64(filePath);

const uploadApi = new UploadApi(config);
const documentApi = new DocumentsApi(config);

// 1. The file's bytes must be read by the application and uploaded
uploadApi.apiUploadsBytesPost({
    bytes: file,
  })
  .then((res) => {

    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel: FileUploadModel = {
      contentType: "application/xml",
      displayName: "Digital Degree Sample",
      id: res.data.id,
      name: filename,
    };

    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participantUserOne: UsersParticipantUserModel = {
      name: "Jack Bauer",
      email: "jack.bauer@mailinator.com",
      identifier: "75502846369",
    };
    const participantUserTwo: UsersParticipantUserModel = {
      name: "James Bond",
      email: "james.bond@mailinator.com",
      identifier: "95588148061",
    };
    const participantUserThree: UsersParticipantUserModel = {
      name: "Garry Eggsy",
      email: "garry.eggsy@mailinator.com",
      identifier: "87657257008",
    };

    // 4. Specify the element that holds the namespace of the issuer
    const xmlNamespaceModel: XmlNamespaceModel = {
      prefix: "dip",
      uri: "http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd",
    };

     // 5. The fields 'DadosDiploma' and 'DadosRegistro' and the entire XML file must be signed
    const xadesOptionsDegreeData: FlowActionsXadesOptionsModel = {
      signatureType: XadesSignatureTypes.XmlElement,
      elementToSignIdentifierType: XadesElementIdentifierTypes.XPath,
      elementToSignIdentifier: "//dip:DadosDiploma",
      insertionOption: XadesInsertionOptions.AppendChild,
    };

    const xadesOptionsModelRegisterData: FlowActionsXadesOptionsModel = {
      signatureType: XadesSignatureTypes.XmlElement,
      elementToSignIdentifierType: XadesElementIdentifierTypes.XPath,
      elementToSignIdentifier: "//dip:DadosRegistro",
      insertionOption: XadesInsertionOptions.AppendChild,
    };

    const xadesOptionsModelFull: FlowActionsXadesOptionsModel = {
        signatureType: XadesSignatureTypes.FullXml
    }

    // 6. Each signature requires its own flow action
    const degreeDataAction: FlowActionsFlowActionCreateModel = {
        type: FlowActionType.Signer,
        user: participantUserOne,
        xadesOptions: xadesOptionsDegreeData
    }

    const registerDataAction: FlowActionsFlowActionCreateModel = {
        type: FlowActionType.Signer,
        user: participantUserTwo,
        xadesOptions: xadesOptionsModelRegisterData
    }

    const flowActionCreateModelFull : FlowActionsFlowActionCreateModel = {
        type: FlowActionType.Signer,
        user: participantUserThree,
        xadesOptions: xadesOptionsModelFull        
    }
    
     // 7. Send the document create request
     const documentRequest: DocumentsCreateDocumentRequest = {
        newFolderName: "New folder",
        files: [fileUploadModel],
        flowActions: [degreeDataAction, registerDataAction, flowActionCreateModelFull],
        xmlNamespaces: [xmlNamespaceModel]
     }

     documentApi.apiDocumentsPost(documentRequest).then((res) => {
        console.log("Document id: ", res.data[0].documentId);
     })
  });
