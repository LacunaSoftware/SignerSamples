using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Spa.Api;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    class ListDocumentsScenario : Scenario
    {
        public override async Task RunAsync()
        {
             /**
              * This scenario describes how to list documents.
              */

            //1 - define the arguments to list the documents according to your requirements
            var searchParams = new DocumentListParameters
            {
                FilterByDocumentType = true, //Filter documents by type if its true
                DocumentType = DocumentTypes.Deed, // Define the document type
                Order = PaginationOrders.Asc, //Order the list: Ascending = Oldest on top / Descending = Newest on top
                OrganizationType = OrganizationType.Normal, //Define the organization type:  Normal, Personal , SharedWithMe
                Limit = 2, //Define the number of Documents that you want to list 
                IsConcluded = false, // List concluded documnets if it's "true"

            };

            //2 - Call the ListDocument method and pass "searchParams" as a parameter 
            var listOfDocuments = await SignerClient.ListDocumentsAsync(searchParams);


            foreach (var item in listOfDocuments.Items)
            {
                System.Console.WriteLine(item.Name);
            }

        }
        
    }
}
