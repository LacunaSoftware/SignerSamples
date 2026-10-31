namespace Console.Tests.TestSupport;

/// <summary>
/// Sets environment variables until disposed, then puts back the values they had. A scenario reads
/// SIGNER_ENDPOINT and SIGNER_API_KEY in its constructor, so set them before creating it.
/// </summary>
public sealed class EnvironmentVariables : IDisposable
{
    private readonly Dictionary<string, string?> previous = [];

    public EnvironmentVariables(IReadOnlyDictionary<string, string?> values)
    {
        foreach (var (name, value) in values)
        {
            previous[name] = Environment.GetEnvironmentVariable(name);
            Environment.SetEnvironmentVariable(name, value);
        }
    }

    public void Dispose()
    {
        foreach (var (name, value) in previous)
        {
            Environment.SetEnvironmentVariable(name, value);
        }
    }
}
