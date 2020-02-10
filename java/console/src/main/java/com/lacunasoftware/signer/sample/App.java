package com.lacunasoftware.signer.sample;


import com.lacunasoftware.signer.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class App {
	public static void main(String[] args) throws URISyntaxException, RestException, IOException {

		String token = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
		SignerClient client = new SignerClient("https://signer-lac.azurewebsites.net", token);

		// 1 - Upload a file
		// PDF files are signed as PAdES.
		// If you need to sign as CAdES, you may set the mime type to null or "application/octet-stream".
		byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel upload = client.uploadFile("sample.pdf", content, "application/pdf");

		// 2 - Create document
		CreateDocumentRequest documentRequest = new CreateDocumentRequest();

		List<FileUploadModel> files = new ArrayList<>();
		FileUploadModel sampleDocument = new FileUploadModel(upload);
		sampleDocument.setDisplayName("Sample document");
		files.add(sampleDocument);
		documentRequest.setFiles(files);

		List<FlowActionCreateModel> actions = new ArrayList<>();
		FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
		flowActionCreateModel.setType(FlowActionType.SIGNER);
		ParticipantUserModel user = new ParticipantUserModel();
		user.setName("Jack Bauer");
		user.setEmail("jack.bauer@mailinator.com");
		user.setIdentifier("75502846369");
		flowActionCreateModel.setUser(user);
		actions.add(flowActionCreateModel);
		documentRequest.setFlowActions(actions);

		List<UUID> documentIds = client.createDocument(documentRequest);

		// 3 - Get document details.
		UUID documentId = documentIds.get(0);
		DocumentModel details = client.getDocumentDetails(documentId);

		// Note: to check if the document is concluded, you can check the details.isConcluded()
		if (details.isConcluded()) {
		}

		// And to check if individual participants have concluded their actions check the status of
		// the corresponding action.
		for (FlowActionModel action : details.getFlowActions()) {
			if (action.getStatus() == ActionStatus.COMPLETED) {

			}
		 }

		// 4 - Send email reminder to participant
		client.sendFlowActionReminder(documentId, details.getFlowActions().get(0).getId());

		// 5 - Download content
		// Printer friendly and signed versions may only be downloaded if the document has at least
		// one signature.
		UUID signedDocumentId = UUID.fromString("2e20e453-2bd5-44c5-9be4-fe66d954c619");

		// You may retrieve a pre-authenticated URL (ticket) to download printer-friendly, signed and
		// original versions.
		TicketModel printerFriendlyTicket = client.getDocumentDownloadTicket(signedDocumentId, DocumentTicketType.PRINTER_FRIENDLY_VERSION);
		TicketModel signedFileTicket = client.getDocumentDownloadTicket(signedDocumentId, DocumentTicketType.SIGNATURES);

		// Or directly download the files.
		InputStream printerFriendlyVersionStream = client.getDocument(signedDocumentId, DocumentTicketType.PRINTER_FRIENDLY_VERSION);
		byte[] signedFileBytes = client.getDocumentBytes(signedDocumentId, DocumentTicketType.SIGNATURES);
	}


}
