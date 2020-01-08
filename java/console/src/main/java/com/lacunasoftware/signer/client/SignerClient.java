package com.lacunasoftware.signer.client;


import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;


public class SignerClient {
	protected String apiKey;
	private URI endpointUri;
	private RestClient restClient;

	public SignerClient(String endpointUri, String apiKey) throws URISyntaxException {
			this(new URI(endpointUri), apiKey);
	}

	public SignerClient(URI endpointUri, String apiKey) {
		this.endpointUri = endpointUri;
		this.apiKey = apiKey;
	}

	protected RestClient getRestClient() {
		if (restClient == null) {
			restClient = new RestClient(endpointUri, apiKey);
		}
		return restClient;
	}

	// region SHARED

	public byte[] getFile(TicketModel ticket) throws RestException {
		return getRestClient().get(ticket.getLocation(), byte[].class);
	}

	public UploadModel uploadFile(String name, byte[] file, String mimeType) throws IOException, RestException {
		UploadModel model;
		try (ByteArrayInputStream stream = new ByteArrayInputStream(file)) {
			model = uploadFile(name, stream, mimeType);
		}
		return model;
	}

	public UploadModel uploadFile(String name, InputStream fileStream, String mimeType) throws RestException {
		return getRestClient().postMultipart("/api/uploads", fileStream, name, mimeType, UploadModel.class);
	}

	// endregion

	// region DOCUMENT

	@SuppressWarnings("unchecked")
	public List<UUID> createDocument(CreateDocumentRequest request) throws RestException {
		return (List<UUID>)getRestClient().post("/api/documents", request.toModel(), TypeToken.getParameterized(List.class, UUID.class));
	}

	public DocumentModel getDocumentDetails(UUID id) throws RestException {
		LacunaSignerApiDocumentsDocumentModel model = getRestClient().get(String.format("/api/documents/%s", id.toString()), LacunaSignerApiDocumentsDocumentModel.class);
		return new DocumentModel(model);
	}

	public TicketModel getDocumentDownloadTicket(UUID id, DocumentTicketType type) throws RestException {
		LacunaSignerApiTicketModel model = getRestClient().get(String.format("/api/documents/%s/ticket?type=%s", id.toString(), type.getValue()), LacunaSignerApiTicketModel.class);
		return new TicketModel(model);
	}

	public InputStream getDocument(UUID id, DocumentTicketType type) throws RestException {
		TicketModel ticket = getDocumentDownloadTicket(id, type);
		return getRestClient().getStream(ticket.getLocation());
	}

	public byte[] getDocumentBytes(UUID id, DocumentTicketType type) throws IOException, RestException {
		byte[] content;

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (InputStream stream = getDocument(id, type)) {
				int nRead;
				byte[] data = new byte[16384]; // 16KB
				while ((nRead = stream.read(data, 0, data.length)) != -1) {
					baos.write(data, 0, nRead);
				}
				content = baos.toByteArray();
			}
		}

		return content;
	}

	// endregion

	// region NOTIFICATIONS

	public void sendFlowActionReminder(UUID documentId, UUID flowActionId) throws RestException {
		CreateFlowActionReminderRequest request = new CreateFlowActionReminderRequest();
		request.setDocumentId(documentId);
		request.setFlowActionId(flowActionId);
		getRestClient().post("/api/notifications/flow-action-reminder", request.toModel());
	}

	// endregion

}
