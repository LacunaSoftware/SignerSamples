using Lacuna.Signer.Client;


namespace Console.Scenarios
{
    public abstract class Scenario
    {
        protected SignerClient signerClient;

        public void Init()
        {
            var token = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
            signerClient = new SignerClient("https://signer-lac.azurewebsites.net", token);
        }

        public abstract void Run();
    }
}
