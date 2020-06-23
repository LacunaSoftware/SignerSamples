using Console.Scenarios;

namespace Console
{
	class Program {

		static void Main(string[] args)
		{
			// Modify the line below to run different scenarios in folder 'Scenarios'.
			var scenario = new ExtractEmbeddedSignatureAndRedirectionWithActionUrlScenario();
			scenario.Init();
			scenario.Run();
		}
	}
}
