using SignerDemo.Scenarios;
using System;

namespace SignerDemo // Note: actual namespace depends on the project name.
{
	internal class Program
	{
		static async Task Main(string[] args){
			var scenario = new CreateDocumentInExistingFolderScenario();
			await scenario.RunAsync();
		}
	}
}
