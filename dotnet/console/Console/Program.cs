using Console.Scenarios;
using System.Threading.Tasks;

namespace Console
{
	class Program {

		static void Main(string[] args)
		{
			// Modify the line below to run different scenarios in folder 'Scenarios'.
			var scenario = new EmbeddedSignatureScenario();

            Task.Run(() => scenario.RunAsync()).GetAwaiter().GetResult();
		}
	}
}
