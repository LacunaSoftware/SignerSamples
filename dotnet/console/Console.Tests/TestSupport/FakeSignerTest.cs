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

    // Runs even when InitializeAsync failed part way, so it restores only what was set.
    public async ValueTask DisposeAsync()
    {
        if (previousDirectory != null)
        {
            Environment.CurrentDirectory = previousDirectory;
        }
        environment?.Dispose();
        if (Signer != null)
        {
            await Signer.DisposeAsync();
            // A scenario that catches and prints errors could pass while the fake failed.
            Assert.True(Signer.Errors.Count == 0, $"The fake Signer threw:\n{string.Join("\n", Signer.Errors)}");
        }
    }

    /// <summary>
    /// Creates the scenario (after the environment is set), runs it and returns what it printed. If it
    /// throws, what it printed and what the fake Signer threw go to the test's output.
    /// </summary>
    protected async Task<string> RunAsync<TScenario>() where TScenario : Scenario, new()
    {
        using var console = new ConsoleCapture();
        try
        {
            await new TScenario().RunAsync();
        }
        catch
        {
            TestContext.Current.TestOutputHelper?.WriteLine($"{typeof(TScenario).Name} printed:\n{console.Text}");
            foreach (var error in Signer.Errors)
            {
                TestContext.Current.TestOutputHelper?.WriteLine($"The fake Signer threw: {error}");
            }
            throw;
        }
        return console.Text;
    }
}
