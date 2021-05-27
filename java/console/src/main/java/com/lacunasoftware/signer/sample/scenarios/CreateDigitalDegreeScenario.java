package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;

import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.XadesElementIdentifierTypes;
import com.lacunasoftware.signer.XadesInsertionOptions;
import com.lacunasoftware.signer.XadesSignatureTypes;
import com.lacunasoftware.signer.XmlNamespaceModel;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.flowactions.XadesOptionsModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

public class CreateDigitalDegreeScenario extends Scenario {
    /**
     * This scenario demonstrates the creation of a digital degree compliant with "PORTARIA Nº 554, DE 11 DE MARÇO DE 2019".
     */
    @Override
    public void Run() throws IOException, RestException, Exception {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample-degree.xml");
        UploadModel uploadModel = signerClient.uploadFile("sample-degree.xml", content, "application/xml");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Digital Degree Sample");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        ParticipantUserModel participantUserOne  = new ParticipantUserModel();
        participantUserOne.setName("Jack Bauer");
        participantUserOne.setEmail("jack.bauer@mailinator.com");
        participantUserOne.setIdentifier("75502846369");

        ParticipantUserModel participantUserTwo  = new ParticipantUserModel();
        participantUserTwo.setName("James Bond");
        participantUserTwo.setEmail("james.bond@mailinator.com");
        participantUserTwo.setIdentifier("95588148061");

        ParticipantUserModel participantUserThree  = new ParticipantUserModel();
        participantUserThree.setName("Garry Eggsy");
        participantUserThree.setEmail("garry.eggsy@mailinator.com");
        participantUserThree.setIdentifier("87657257008");

        // 4. Specify the element that holds the namespace of the issuer
        XmlNamespaceModel xmlNamespaceModel = new XmlNamespaceModel();
        xmlNamespaceModel.setPrefix("dip");
        xmlNamespaceModel.setUri("http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd");

        // 5. The fields 'DadosDiploma' and 'DadosRegistro' and the entire XML file must be signed
        XadesOptionsModel xadesOptionsDegreeData  = new XadesOptionsModel();
        xadesOptionsDegreeData.setSignatureType(XadesSignatureTypes.XMLELEMENT);
        xadesOptionsDegreeData.setElementToSignIdentifierType(XadesElementIdentifierTypes.XPATH);
        xadesOptionsDegreeData.setElementToSignIdentifier("//dip:DadosDiploma");
        xadesOptionsDegreeData.insertionOption(XadesInsertionOptions.APPENDCHILD);

        XadesOptionsModel xadesOptionsModelRegisterData  = new XadesOptionsModel();
        xadesOptionsModelRegisterData.setSignatureType(XadesSignatureTypes.XMLELEMENT);
        xadesOptionsModelRegisterData.setElementToSignIdentifierType(XadesElementIdentifierTypes.XPATH);
        xadesOptionsModelRegisterData.setElementToSignIdentifier("//dip:DadosRegistro");
        xadesOptionsModelRegisterData.insertionOption(XadesInsertionOptions.APPENDCHILD);

        XadesOptionsModel xadesOptionsModelFull = new XadesOptionsModel();
        xadesOptionsModelFull.setSignatureType(XadesSignatureTypes.FULLXML);

        // 6. Each signature requires its own flow action
        FlowActionCreateModel degreeDataAction = new FlowActionCreateModel();
        degreeDataAction.setType(FlowActionType.SIGNER);
        degreeDataAction.setUser(participantUserOne);
        degreeDataAction.setXadesOptions(xadesOptionsDegreeData);

        FlowActionCreateModel registerDataAction  = new FlowActionCreateModel();
        registerDataAction.setType(FlowActionType.SIGNER);
        registerDataAction.setUser(participantUserTwo);
        registerDataAction.setXadesOptions(xadesOptionsModelRegisterData);

        FlowActionCreateModel flowActionCreateModelFull = new FlowActionCreateModel();
        flowActionCreateModelFull.setType(FlowActionType.SIGNER);
        flowActionCreateModelFull.setUser(participantUserThree);
        flowActionCreateModelFull.setXadesOptions(xadesOptionsDegreeData);

        // 6. Send the document create request
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(new ArrayList<FileUploadModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(fileUploadModelBuilder.toModel());
            }
        });
        documentRequest.setFlowActions(new ArrayList<FlowActionCreateModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(degreeDataAction);
                add(registerDataAction);
                add(flowActionCreateModelFull);
            }
        });
        CreateDocumentResult result = signerClient.createDocument(documentRequest).get(0);

        System.out.println(String.format("Document %s created", result.getDocumentId().toString()));
    }

}