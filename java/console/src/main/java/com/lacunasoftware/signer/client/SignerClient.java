package com.lacunasoftware.signer.client;


import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

	// region SIGNATURE

	public StartSignatureResponse startPublicSignature(String key, StartSignatureRequest request) throws RestException {
		LacunaSignerApiSignatureStartSignatureResponse response = getRestClient().post(String.format("/api/documents/keys/%s/signature/certificate", key), request.toModel(), LacunaSignerApiSignatureStartSignatureResponse.class);
		return new StartSignatureResponse(response);
	}

	public CompleteSignatureResponse completePublicSignature(String key, CompleteSignatureRequest request) throws RestException {
		LacunaSignerApiSignatureCompleteSignatureResponse response = getRestClient().post(String.format("/api/documents/keys/%s/signature", key), request.toModel(), LacunaSignerApiSignatureCompleteSignatureResponse.class);
		return new CompleteSignatureResponse(response);
	}

	public StartSignatureResponse startSignature(UUID id, StartSignatureRequest request) throws RestException {
		LacunaSignerApiSignatureStartSignatureResponse response = getRestClient().post(String.format("/api/documents/%s/signature/certificate", id.toString()), request.toModel(), LacunaSignerApiSignatureStartSignatureResponse.class);
		return new StartSignatureResponse(response);
	}

	public CompleteSignatureResponse completeSignature(UUID id, CompleteSignatureRequest request) throws RestException {
		LacunaSignerApiSignatureCompleteSignatureResponse response = getRestClient().post(String.format("/api/documents/%s/signature", id.toString()), request.toModel(), LacunaSignerApiSignatureCompleteSignatureResponse.class);
		return new CompleteSignatureResponse(response);
	}

	public void sendElectronicSignatureAuthenticationCode(SendElectronicSignatureAuthenticationRequest request) throws RestException {
		getRestClient().post("/api/documents/sms-authentication-code", request.toModel());
	}

	public void signElectronically(UUID id, ElectronicSignatureRequest request) throws RestException {
		getRestClient().post(String.format("/api/documents/%s/electronic-signature", id.toString()), request.toModel());
	}

	// endregion

	// region FOLDER

	public FolderInfoModel createFolder(FolderCreateRequest request) throws RestException {
		LacunaSignerApiFoldersFolderInfoModel model = getRestClient().post("/api/folders", request.toModel(), LacunaSignerApiFoldersFolderInfoModel.class);
		return new FolderInfoModel(model);
	}

	public FolderDetailsModel getFolderDetails(UUID folderId) throws RestException {
		LacunaSignerApiFoldersFolderDetailsModel model = getRestClient().get(String.format("/api/folders/%s/details", folderId.toString()), LacunaSignerApiFoldersFolderDetailsModel.class);
		return new FolderDetailsModel(model);
	}

	@SuppressWarnings("unchecked")
	public PaginatedSearchResponse<FolderInfoModel> listFoldersPaginated(PaginatedSearchParams searchParams, UUID organizationId) throws RestException {
		String orgIdStr = organizationId != null ? organizationId.toString() : "";
		LacunaSignerApiPaginatedSearchResponse<LacunaSignerApiFoldersFolderInfoModel> model = (LacunaSignerApiPaginatedSearchResponse<LacunaSignerApiFoldersFolderInfoModel>)getRestClient().get(String.format("/api/folders%s&organizationId=%s", buildSearchPaginatedParamsString(searchParams), orgIdStr), TypeToken.getParameterized(LacunaSignerApiPaginatedSearchResponse.class, LacunaSignerApiFoldersFolderInfoModel.class));

		List<FolderInfoModel> items = new ArrayList<>();
		for (LacunaSignerApiFoldersFolderInfoModel m : model.getItems()) {
			items.add(new FolderInfoModel(m));
		}
		return new PaginatedSearchResponse<>(items, model.getTotalCount());
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

	private String buildSearchPaginatedParamsString(PaginatedSearchParams searchParams) {
		return String.format("?q=%s&limit=%s&offset=%s", getParameterOrEmpty(searchParams.getQ()), searchParams.getLimit(), searchParams.getOffset());
	}

	private String getParameterOrEmpty(String parameter) {
		if (parameter == null || parameter.length() == 0) {
			return "";
		}

		try {
			return URLEncoder.encode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

}
