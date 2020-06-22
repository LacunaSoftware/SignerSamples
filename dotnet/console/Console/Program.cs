using Console.Scenarios;

namespace Console
{
	class Program {

		static void Main(string[] args)
		{
			var scenario = new DocumentOrganizationScenario();
			scenario.Init();
			scenario.Run();
		}
	}
}
