using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Signature;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class PublicSignatureScenario : Scenario
    {
        /**
         * This scenario demonstrates the public certificate-based digital signature flow
         * using StartPublicSignatureAsync / CompletePublicSignatureAsync.
         *
         * The signer is identified through the document's public key and the ticket (both
         * obtained from the action URL) rather than the document id. This is the typical
         * integration pattern when the signature is performed outside of the Signer web
         * application - for instance from your own application or from a batch process.
         *
         * Prerequisites:
         *   - A PFX file holding the signer's certificate and private key, whose subject
         *     matches the participant's identifier (e.g. 56072386105.pfx). Copy it next to
         *     the executable, or change PfxPath below.
         */

        // The signer must match the certificate used to sign, since Signer validates that the
        // certificate's identifier (CPF/CNPJ) matches the participant's identifier.
        private static readonly ParticipantUserModel Signer = new ParticipantUserModel()
        {
            Name = "Alan Turing",
            Email = "alan.turing@mailinator.com",
            Identifier = "56072386105"
        };

        private static readonly string PfxPath = $"{Signer.Identifier}.pfx";
        private const string PfxPassword = "1234";

        public override async Task RunAsync()
        {
            // 1. Create the document with a single signer
            var documentResult = await CreateDocumentForSigningAsync();
            System.Console.WriteLine($"Document {documentResult.DocumentId} created");

            // 2. Get the action URL for the participant and extract the document key and the ticket
            var actionUrlRequest = new ActionUrlRequest()
            {
                EmailAddress = Signer.Email,
                Identifier = Signer.Identifier
            };
            var actionUrlResponse = await SignerClient.GetActionUrlAsync(documentResult.DocumentId, actionUrlRequest);

            var key = ExtractKey(actionUrlResponse.Url);
            var ticket = ExtractTicket(actionUrlResponse.Url);
            System.Console.WriteLine($"Document key: {key}");

            // 3. Load the signer's certificate. In a real application the certificate and the
            //    private key operation would come from the user's hardware token, smart card or
            //    from the browser's certificate API - here we use a local PFX for simplicity.
            var certificate = X509CertificateLoader.LoadPkcs12FromFile(PfxPath, PfxPassword);

            // 4. Start the signature. The server returns the hash to be signed and a token
            //    identifying this signature attempt.
            var startRequest = new PublicStartSignatureRequest()
            {
                Certificate = certificate.RawData,
                Ticket = ticket
            };
            var startResponse = await SignerClient.StartPublicSignatureAsync(key, startRequest);
            // The response can come back with Success = false and the reasons in ValidationResults (for
            // instance, a certificate Signer does not accept), so check Success before going on.
            if (!startResponse.Success) {
                throw new InvalidOperationException($"Signer could not start the signature: {DescribeErrors(startResponse.ValidationResults)}");
            }
            System.Console.WriteLine($"Signature started, digest algorithm: {startResponse.DigestAlgorithm}");

            // 5. Sign the hash with the private key matching the certificate sent above
            var signature = SignHash(certificate, startResponse.ToSignHash, startResponse.DigestAlgorithm);

            // 6. Complete the public signature, sending the signature bytes and the token
            var completeRequest = new CompleteSignatureRequest()
            {
                Signature = signature,
                Token = startResponse.Token
            };
            var completeResponse = await SignerClient.CompletePublicSignatureAsync(key, completeRequest);
            if (!completeResponse.Success) {
                throw new InvalidOperationException($"Signer did not accept the signature: {DescribeErrors(completeResponse.ValidationResults)}");
            }

            System.Console.WriteLine("Public signature completed successfully");
        }

        private static string DescribeErrors(ValidationResultsModel validationResults)
        {
            var errors = validationResults?.Errors?.Select(error => error.Message).ToList();
            return errors?.Count > 0 ? string.Join("; ", errors) : "no validation errors reported";
        }

        private async Task<CreateDocumentResult> CreateDocumentForSigningAsync()
        {
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Public Signature Sample" };

            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = Signer
            };

            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModel },
                // The signature happens through the API, so don't e-mail the participant a signing link
                DisablePendingActionNotifications = true
            };

            return (await SignerClient.CreateDocumentAsync(documentRequest)).First();
        }

        // The action URL has the format {domain}/document/key/{key}/sign?ticket={ticket}
        private static string ExtractKey(string actionUrl)
        {
            var match = Regex.Match(actionUrl ?? "", "/key/([^/?]+)");
            if (!match.Success) {
                throw new InvalidOperationException($"Could not extract the document key from URL: {actionUrl}");
            }
            return match.Groups[1].Value;
        }

        private static string ExtractTicket(string actionUrl)
        {
            var match = Regex.Match(actionUrl ?? "", "[?&]ticket=([^&]+)");
            if (!match.Success) {
                throw new InvalidOperationException($"Could not extract the ticket from URL: {actionUrl}");
            }
            return Uri.UnescapeDataString(match.Groups[1].Value);
        }

        /**
         * Signs the hash returned by StartPublicSignatureAsync with the certificate's private key.
         *
         * @param certificate     the signer's certificate, holding the private key
         * @param toSignHash      the bytes returned by StartPublicSignatureAsync that must be signed
         * @param digestAlgorithm the hash algorithm reported by the server, by OID (e.g. "2.16.840.1.101.3.4.2.1")
         *                        or by name (e.g. "SHA256")
         */
        private static byte[] SignHash(X509Certificate2 certificate, byte[] toSignHash, string digestAlgorithm)
        {
            var hashAlgorithm = GetHashAlgorithmName(digestAlgorithm);

            using (var rsa = certificate.GetRSAPrivateKey()) {
                if (rsa != null) {
                    return rsa.SignHash(toSignHash, hashAlgorithm, RSASignaturePadding.Pkcs1);
                }
            }

            using (var ecdsa = certificate.GetECDsaPrivateKey()) {
                if (ecdsa != null) {
                    return ecdsa.SignHash(toSignHash);
                }
            }

            throw new InvalidOperationException("The certificate does not have an usable private key");
        }

        // Signer reports the digest algorithm by its OID (SHA-256 is "2.16.840.1.101.3.4.2.1"); the
        // names are accepted too.
        private static HashAlgorithmName GetHashAlgorithmName(string digestAlgorithm)
        {
            switch ((digestAlgorithm ?? "").Replace("-", "").ToUpperInvariant()) {
                case "1.3.14.3.2.26":
                case "SHA1":
                    return HashAlgorithmName.SHA1;
                case "2.16.840.1.101.3.4.2.1":
                case "SHA256":
                    return HashAlgorithmName.SHA256;
                case "2.16.840.1.101.3.4.2.2":
                case "SHA384":
                    return HashAlgorithmName.SHA384;
                case "2.16.840.1.101.3.4.2.3":
                case "SHA512":
                    return HashAlgorithmName.SHA512;
                default:
                    throw new NotSupportedException($"Unsupported digest algorithm: {digestAlgorithm}");
            }
        }
    }
}
