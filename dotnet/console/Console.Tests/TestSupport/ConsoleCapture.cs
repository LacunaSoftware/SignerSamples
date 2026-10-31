using System.Text;

namespace Console.Tests.TestSupport;

/// <summary>
/// Records what is written to System.Console until disposed. The webhook endpoint writes from
/// other threads after it has answered, so WaitForAsync polls for a line.
/// </summary>
public sealed class ConsoleCapture : IDisposable
{
    private readonly TextWriter original = System.Console.Out;
    private readonly Recorder recorder = new();

    public ConsoleCapture()
    {
        System.Console.SetOut(recorder);
    }

    public string Text => recorder.Text;

    public async Task<string> WaitForAsync(string expected, TimeSpan? timeout = null)
    {
        var deadline = DateTime.UtcNow + (timeout ?? TimeSpan.FromSeconds(10));
        while (!Text.Contains(expected))
        {
            Assert.True(DateTime.UtcNow < deadline, $"The console never printed \"{expected}\". It printed:\n{Text}");
            await Task.Delay(20);
        }
        return Text;
    }

    public void Dispose()
    {
        System.Console.SetOut(original);
    }

    private sealed class Recorder : TextWriter
    {
        private readonly StringBuilder text = new();

        public override Encoding Encoding => Encoding.UTF8;

        public string Text
        {
            get { lock (text) { return text.ToString(); } }
        }

        public override void Write(char value)
        {
            lock (text) { text.Append(value); }
        }

        public override void Write(string? value)
        {
            lock (text) { text.Append(value); }
        }
    }
}
