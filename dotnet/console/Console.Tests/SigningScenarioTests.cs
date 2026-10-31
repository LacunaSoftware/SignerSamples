using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using Console.Scenarios;
using Console.Tests.TestSupport;

namespace Console.Tests;

/// <summary>
/// The scenarios that get a participant's action URL: embedded signature, and public signature,
/// which signs through the API with the test PFX.
/// </summary>
public class SigningScenarioTests : FakeSignerTest
{
    [Fact]
    public async Task Embedded_signature_asks_the_action_url_for_the_signer_and_prints_the_embed_url()
    {
        var output = await RunAsync<EmbeddedSignatureScenario>();

        // The participant signs in the page that embeds Signer, so Signer must not e-mail them a link.
        Assert.True((bool)Signer.Single("POST", "/api/documents").Json["disablePendingActionNotifications"]!);
        var request = Signer.Single("POST", $"/api/documents/{Signer.DocumentId}/action-url").Json;
        Assert.Equal("75502846369", (string?)request["identifier"]);
        Assert.Contains($"{Signer.Url}/document/embed/the-embed-ticket", output);
    }

    // Signer reports SHA-256 by its OID; the scenario accepts the name too.
    [Theory]
    [InlineData("2.16.840.1.101.3.4.2.1")]
    [InlineData("SHA256")]
    public async Task Public_signature_signs_the_hash_with_the_certificate_of_the_participant(string digestAlgorithm)
    {
        Signer.DigestAlgorithm = digestAlgorithm;

        var output = await RunAsync<PublicSignatureScenario>();

        // The document is created for the certificate's holder: Signer checks that the CPFs match.
        using var certificate = X509CertificateLoader.LoadPkcs12FromFile("56072386105.pfx", "1234");
        var create = Signer.Single("POST", "/api/documents").Json;
        var action = Assert.Single(create["flowActions"]!);
        Assert.Equal("56072386105", (string?)action["user"]!["identifier"]);
        // The signature goes through the API, so Signer must not e-mail the participant a link.
        Assert.True((bool)create["disablePendingActionNotifications"]!);
        // ICP-Brasil puts the CPF in the subject alternative name (otherName 2.16.76.1.3.1, after the birth date).
        var alternativeName = certificate.Extensions.OfType<X509SubjectAlternativeNameExtension>().Single();
        Assert.Contains("56072386105", System.Text.Encoding.ASCII.GetString(alternativeName.RawData));

        var actionUrl = Signer.Single("POST", $"/api/documents/{Signer.DocumentId}/action-url").Json;
        Assert.Equal("56072386105", (string?)actionUrl["identifier"]);
        Assert.Equal("alan.turing@mailinator.com", (string?)actionUrl["emailAddress"]);

        // The key and the ticket come from the action URL; the ticket is URL-decoded.
        var start = Signer.Single("POST", "/api/documents/keys/the-document-key/signature/certificate").Json;
        Assert.Equal("the+ticket", (string?)start["ticket"]);
        Assert.Equal(certificate.RawData, Convert.FromBase64String((string)start["certificate"]!));

        var complete = Signer.Single("POST", "/api/documents/keys/the-document-key/signature").Json;
        Assert.Equal("the-signature-token", (string?)complete["token"]);
        var signature = Convert.FromBase64String((string)complete["signature"]!);
        using var publicKey = certificate.GetRSAPublicKey()!;
        Assert.True(publicKey.VerifyHash(Signer.ToSignHash, signature, HashAlgorithmName.SHA256, RSASignaturePadding.Pkcs1), "The signature does not verify with the certificate's public key");

        Assert.Contains("Public signature completed successfully", output);
    }

    // The start and complete responses carry Success = false, with ValidationResults, when Signer rejects them.
    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public async Task Public_signature_rejected_by_signer_throws_with_its_errors(bool atStart)
    {
        const string rejection = "The certificate's CPF does not match the participant's";
        if (atStart)
        {
            Signer.StartSignatureError = rejection;
        }
        else
        {
            Signer.CompleteSignatureError = rejection;
        }

        var error = await Assert.ThrowsAsync<InvalidOperationException>(RunAsync<PublicSignatureScenario>);

        Assert.Contains(rejection, error.Message);
        Assert.Equal(atStart ? 0 : 1, Signer.RequestsTo("POST", "/api/documents/keys/the-document-key/signature").Count);
    }
}
