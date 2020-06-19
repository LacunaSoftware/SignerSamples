using Console.Scenarios;

namespace Console
{
	class Program {

		static void Main(string[] args)
		{
			var scenario = new SubmitElementSignXMLDocumentScenario();
			scenario.Init();
			scenario.Run();
		}
	}
}
