using System.Text.RegularExpressions;
using Console.Scenarios;
using Console.Tests.TestSupport;
using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.Folders;
using Newtonsoft.Json.Linq;

namespace Console.Tests;

/// <summary>
/// The scenarios that create a document: each uploads its file, then sends POST /api/documents
/// with the flow its sample is about.
/// </summary>
public class CreateDocumentScenarioTests : FakeSignerTest
{
    [Fact]
    public async Task One_signer_uploads_the_pdf_and_creates_a_document_for_jack_bauer()
    {
        var output = await RunAsync<CreateDocumentWithOneSignerScenario>();

        Assert.Equal(["sample.pdf"], UploadedFileNames());
        var request = CreateRequest();
        var file = Assert.Single(request["files"]!);
        Assert.Equal("upload-1", (string?)file["id"]);
        Assert.Equal("One Signer Sample", (string?)file["displayName"]);
        var action = Assert.Single(request["flowActions"]!);
        Assert.Equal("Signer", (string?)action["type"]);
        AssertUser(action["user"]!, "Jack Bauer", "jack.bauer@mailinator.com", "75502846369");
        Assert.Contains($"Document {Signer.DocumentId} created", output);
    }

    [Fact]
    public async Task Description_is_sent_with_the_document()
    {
        await RunAsync<CreateDocumentWithDescriptionScenario>();

        Assert.Equal("Some Description Sample", (string?)CreateRequest()["description"]);
    }

    [Fact]
    public async Task Attachment_is_a_second_upload_sent_as_a_public_attachment()
    {
        await RunAsync<CreateDocumentWithAttachmentScenario>();

        Assert.Equal(["sample.pdf", "sample.pdf"], UploadedFileNames());
        var request = CreateRequest();
        Assert.Equal("upload-1", (string?)Assert.Single(request["files"]!)["id"]);
        var attachment = Assert.Single(request["attachments"]!);
        Assert.Equal("upload-2", (string?)attachment["id"]);
        Assert.Equal("One Attachment Sample", (string?)attachment["displayName"]);
        Assert.False((bool)attachment["isPrivate"]!);
    }

    [Fact]
    public async Task Envelope_merges_two_uploads_into_one_named_document()
    {
        var output = await RunAsync<CreateDocumentWithEnvelopeScenario>();

        Assert.Equal(["sample.pdf", "sample.pdf"], UploadedFileNames());
        var request = CreateRequest();
        Assert.Equal(["upload-1", "upload-2"], request["files"]!.Select(file => (string?)file["id"]));
        Assert.True((bool)request["isEnvelope"]!);
        Assert.Equal("Envelope", (string?)request["envelopeName"]);
        Assert.Contains($"Document {Signer.DocumentId} created", output);
    }

    [Fact]
    public async Task Signers_with_order_are_on_steps_1_and_2()
    {
        await RunAsync<CreateDocumentWithTwoOrMoreSignersWithOrderScenario>();

        var actions = CreateRequest()["flowActions"]!.ToList();
        Assert.Equal(2, actions.Count);
        AssertUser(actions[0]["user"]!, "Jack Bauer", "jack.bauer@mailinator.com", "75502846369");
        Assert.Equal(1, (int)actions[0]["step"]!);
        AssertUser(actions[1]["user"]!, "James Bond", "james.bond@mailinator.com", "95588148061");
        Assert.Equal(2, (int)actions[1]["step"]!);
    }

    [Fact]
    public async Task Signers_without_order_share_one_step()
    {
        await RunAsync<CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario>();

        var actions = CreateRequest()["flowActions"]!.ToList();
        Assert.Equal(["75502846369", "95588148061"], actions.Select(action => (string?)action["user"]!["identifier"]));
        Assert.All(actions, action => Assert.Equal("Signer", (string?)action["type"]));
        // The client sends the default step, 0, for both: actions on the same step run in parallel.
        Assert.Equal((int)actions[0]["step"]!, (int)actions[1]["step"]!);
    }

    [Fact]
    public async Task Approver_is_an_approver_action()
    {
        await RunAsync<CreateDocumentWithApproversScenario>();

        var action = Assert.Single(CreateRequest()["flowActions"]!);
        Assert.Equal("Approver", (string?)action["type"]);
        AssertUser(action["user"]!, "Jack Bauer", "jack.bauer@mailinator.com", "75502846369");
    }

    [Fact]
    public async Task Signing_rule_is_one_action_for_two_users_that_needs_one_signature()
    {
        await RunAsync<CreateDocumentWithSigningRuleScenario>();

        var action = Assert.Single(CreateRequest()["flowActions"]!);
        Assert.Equal("SignRule", (string?)action["type"]);
        Assert.Equal(1, (int)action["numberRequiredSignatures"]!);
        Assert.Equal(["75502846369", "95588148061"], action["signRuleUsers"]!.Select(user => (string?)user["identifier"]));
    }

    [Fact]
    public async Task Positioned_signature_is_a_mark_on_page_1_of_the_upload()
    {
        await RunAsync<CreateDocumentWithPositionedSignatures>();

        var action = Assert.Single(CreateRequest()["flowActions"]!);
        var mark = Assert.Single(action["prePositionedMarks"]!);
        AssertEnum(DocumentMarkType.SignatureVisualRepresentation, mark["type"]);
        Assert.Equal("upload-1", (string?)mark["uploadId"]);
        Assert.Equal(1, (int)mark["pageNumber"]!);
        Assert.Equal(395.0, (double)mark["topLeftX"]!);
        Assert.Equal(560.0, (double)mark["topLeftY"]!);
        Assert.Equal(170.0, (double)mark["width"]!);
        Assert.Equal(94.0, (double)mark["height"]!);
    }

    [Fact]
    public async Task Cades_is_forced_on_the_pdf()
    {
        await RunAsync<CreatePDFDocumentWithCadesSignatureScenario>();

        Assert.True((bool)CreateRequest()["forceCadesSignature"]!);
    }

    [Fact]
    public async Task Full_xml_signature_signs_the_whole_xml()
    {
        await RunAsync<CreateXMLWithFullDocumentSignatureScenario>();

        Assert.Equal(["sample.xml"], UploadedFileNames());
        var action = Assert.Single(CreateRequest()["flowActions"]!);
        AssertEnum(XadesSignatureTypes.FullXml, action["xadesOptions"]!["signatureType"]);
    }

    [Fact]
    public async Task Element_xml_signature_signs_the_element_with_the_nfe_id()
    {
        await RunAsync<CreateXMLWithElementSignatureScenario>();

        Assert.Equal(["sample.xml"], UploadedFileNames());
        var options = Assert.Single(CreateRequest()["flowActions"]!)["xadesOptions"]!;
        AssertEnum(XadesSignatureTypes.XmlElement, options["signatureType"]);
        AssertEnum(XadesElementIdentifierTypes.Id, options["elementToSignIdentifierType"]);
        Assert.Equal("NFe35141214314050000662550010001084271182362300", (string?)options["elementToSignIdentifier"]);
        // The element exists in the sample file, so Signer can find it.
        Assert.Contains("Id=\"NFe35141214314050000662550010001084271182362300\"", File.ReadAllText("sample.xml"));
    }

    [Fact]
    public async Task Digital_degree_signs_two_elements_by_xpath_then_the_whole_xml()
    {
        await RunAsync<CreateDigitalDegreeScenario>();

        Assert.Equal(["sample-degree.xml"], UploadedFileNames());
        var request = CreateRequest();
        var ns = Assert.Single(request["xmlNamespaces"]!);
        Assert.Equal("dip", (string?)ns["prefix"]);
        Assert.Equal("http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd", (string?)ns["uri"]);

        var options = request["flowActions"]!.Select(action => action["xadesOptions"]!).ToList();
        Assert.Equal(3, options.Count);
        AssertEnum(XadesSignatureTypes.XmlElement, options[0]["signatureType"]);
        AssertEnum(XadesSignatureTypes.XmlElement, options[1]["signatureType"]);
        AssertEnum(XadesSignatureTypes.FullXml, options[2]["signatureType"]);
        AssertEnum(XadesElementIdentifierTypes.XPath, options[0]["elementToSignIdentifierType"]);
        AssertEnum(XadesElementIdentifierTypes.XPath, options[1]["elementToSignIdentifierType"]);
        Assert.Equal("//dip:DadosDiploma", (string?)options[0]["elementToSignIdentifier"]);
        Assert.Equal("//dip:DadosRegistro", (string?)options[1]["elementToSignIdentifier"]);
        Assert.All(options.Take(2), o => AssertEnum(XadesInsertionOptions.AppendChild, o["insertionOption"]));
    }

    [Fact]
    public async Task New_folder_is_named_in_the_create_request()
    {
        await RunAsync<CreateDocumentInNewFolderScenario>();

        Assert.Equal("New Folder", (string?)CreateRequest()["newFolderName"]);
    }

    [Fact]
    public async Task Existing_folder_is_searched_by_name_and_its_id_sent()
    {
        var folderId = Guid.NewGuid();
        // The search matches part of the name, so it finds the archive first: the scenario must check the name.
        Signer.Folders.Add(new FolderInfoModel { Id = Guid.NewGuid(), Name = "Sample Folder Archive" });
        Signer.Folders.Add(new FolderInfoModel { Id = folderId, Name = "Sample Folder" });

        await RunAsync<CreateDocumentInExistingFolderScenario>();

        Assert.Equal("Sample Folder", Signer.Single("GET", "/api/folders").Query["q"]);
        Assert.Equal(folderId, (Guid)CreateRequest()["folderId"]!);
    }

    [Fact]
    public async Task Existing_folder_that_is_missing_stops_before_creating_the_document()
    {
        var error = await Assert.ThrowsAsync<Exception>(RunAsync<CreateDocumentInExistingFolderScenario>);

        Assert.Equal("Folder was not found", error.Message);
        Assert.Empty(Signer.RequestsTo("POST", "/api/documents"));
    }

    private JToken CreateRequest() => Signer.Single("POST", "/api/documents").Json;

    // The file name of each multipart upload, in order.
    private List<string> UploadedFileNames() =>
        Signer.RequestsTo("POST", "/api/uploads").Select(upload => Regex.Match(upload.Body, "filename=([^;\r\n]+)").Groups[1].Value).ToList();

    // The client sends some enums by name ("Signer") and the nested ones by number (XAdES options,
    // marks); the API accepts both.
    private static void AssertEnum<TEnum>(TEnum expected, JToken? actual) where TEnum : struct, Enum
    {
        Assert.NotNull(actual);
        var value = actual.Type == JTokenType.Integer
            ? (TEnum)Enum.ToObject(typeof(TEnum), (int)actual)
            : Enum.Parse<TEnum>((string)actual!);
        Assert.Equal(expected, value);
    }

    private static void AssertUser(JToken user, string name, string email, string identifier)
    {
        Assert.Equal(name, (string?)user["name"]);
        Assert.Equal(email, (string?)user["email"]);
        Assert.Equal(identifier, (string?)user["identifier"]);
    }
}
