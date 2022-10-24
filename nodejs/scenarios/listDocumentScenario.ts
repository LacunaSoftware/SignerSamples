import { FlowsApi, PaginationOrders } from "signer-node-client";
import { config } from "./scenario";

// 1. Create an instance of the FlowsApi with current configs
const flowsApi = new FlowsApi(config)
// 2. Perform the GET request (note that the parameters are not inside an interface)
flowsApi.apiDocumentFlowsGet("Doc1", 10, 0, PaginationOrders.Desc).then((res) => {
    console.log("Listing query results: ", res.data);
})