using Console.Scenarios;

namespace Console
{
	class Program {

		static void Main(string[] args)
		{
			var scenario = new SubmitDocumentWithTwoOrMoreSignersWithoutOrderScenario();
			scenario.Init();
			scenario.Run();
		}
	}
}
