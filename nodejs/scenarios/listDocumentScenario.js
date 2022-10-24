"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
// 1. Create an instance of the FlowsApi with current configs
const flowsApi = new signer_node_client_1.FlowsApi(scenario_1.config);
// 2. Perform the GET request (note that the parameters are not inside an interface)
flowsApi.apiDocumentFlowsGet("Doc1", 10, 0, signer_node_client_1.PaginationOrders.Desc).then((res) => {
    console.log("Listing query results: ", res.data);
});
