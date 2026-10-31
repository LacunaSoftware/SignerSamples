using Console.Scenarios;

namespace Console.Tests.TestSupport;

/// <summary>
/// Base class of the tests that run a scenario against a new <see cref="FakeSigner"/>, pointed to
/// by SIGNER_ENDPOINT and SIGNER_API_KEY, from the output folder where the scenarios' input files
/// are, as `dotnet run` does.
/// </summary>
public abstract class FakeSignerTest : IAsyncLifetime
{
    protected const string ApiKey = "Console Tests|0123456789abcdef";

    private EnvironmentVariables? environment;
    private string? previousDirectory;

    protected FakeSigner Signer { get; private set; } = null!;

    public async ValueTask InitializeAsync()
    {
        Signer = await FakeSigner.StartAsync();
        environment = new EnvironmentVariables(new Dictionary<string, string?>
        {
            ["SIGNER_ENDPOINT"] = Signer.Url,
            ["SIGNER_API_KEY"] = ApiKey,
        });
        previousDirectory = Environment.CurrentDirectory;
        Environment.CurrentDirectory = AppContext.BaseDirectory;
    }

    public async ValueTask DisposeAsync()
    {
        Environment.CurrentDirectory = previousDirectory!;
        environment?.Dispose();
        await Signer.DisposeAsync();
    }

    /// <summary>Creates the scenario (after the environment is set), runs it and returns what it printed.</summary>
    protected static async Task<string> RunAsync<TScenario>() where TScenario : Scenario, new()
    {
        using var console = new ConsoleCapture();
        await new TScenario().RunAsync();
        return console.Text;
    }
}
