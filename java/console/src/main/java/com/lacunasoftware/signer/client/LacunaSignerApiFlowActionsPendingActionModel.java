/*
 * Lacuna.Signer.Site API
 * <!--------------------------------------------------------------------------------------------------------------------->  <h2>Authentication</h2>  <p>  In order to call this APIs, you will need an <strong>API key</strong>. Set the API key in the header <span class=\"code\">X-Api-Key</span>: </p>  <pre>X-Api-Key: your-app|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</pre>  <!---------------------------------------------------------------------------------------------------------------------> <br />  <h2>HTTP Codes</h2>  <p>   The APIs will return the following HTTP codes:  </p>  <table>  <thead>   <tr>     <th>Code</th>     <th>Description</th>    </tr>  </thead>  <tbody>    <tr>     <td><strong class=\"model-title\">200 (OK)</strong></td>     <td>Request processed successfully. The response is different for each API, please refer to the operation's documentation</td>    </tr>    <tr>     <td><strong class=\"model-title\">400 (Bad Request)</strong></td>     <td>Syntax error. For instance, when a required field was not provided</td>    </tr>    <tr>     <td><strong class=\"model-title\">401 (Unauthorized)</strong></td>     <td>API key not provided or invalid</td>    </tr>    <tr>     <td><strong class=\"model-title\">403 (Forbidden)</strong></td>     <td>API key is valid, but the application has insufficient permissions to complete the requested operation</td>    </tr>    <tr>     <td><strong class=\"model-title\">422 (Unprocessable Entity)</strong></td>     <td>API error. The response is as defined in <a href=\"#model-Lacuna.Signer.Api.ErrorModel\">Lacuna.Signer.Api.ErrorModel</a></td>    </tr>   </tbody> </table>  <br />  <h3>Error Codes</h3>  <p>Some of the error codes returned in a 422 response are provided bellow*:</p>  <ul>  <li>CertificateNotFound</li>  <li>DocumentNotFound</li>  <li>FolderNotFound</li>  <li>CpfMismatch</li>  <li>CpfNotExpected</li>  <li>InvalidFlowAction</li>  <li>DocumentInvalidKey</li> </ul>  <p style=\"font-size: 0.9em\">  *The codes shown above are the main error codes. Nonetheless, this list is not comprehensive. New codes may be added anytime without previous warning. </p>  <!--------------------------------------------------------------------------------------------------------------------->  <br />  <h2>Webhooks</h2>  <p>   It is recomended to subscribe to Webhook events <strong>instead</strong> of polling APIs. To do so, enable webhooks and register an URL that will receive a POST request   whenever one of the events bellow occur.  </p> <p>   All requests have the format described in <a href=\"#model-Lacuna.Signer.Api.Webhooks.WebhookModel\">WebhookModel</a>.   The data field varies according to the webhook event type:  </p>   <table>   <thead>    <tr>     <th>Event type</th>     <th>Description</th>     <th>Payload</th>    </tr>   </thead>   <tbody>    <tr>     <td><strong class=\"model-title\">DocumentConcluded</strong></td>     <td>Triggered when the flow of a document is concluded.</td>     <td><a href=\"#model-Lacuna.Signer.Api.Webhooks.DocumentConcludedModel\">DocumentConcludedModel</a></td>    </tr>   </tbody>  </table>  <p>  To register your application URL and enable Webhooks, access the integrations section in your <a href=\"/private/organizations\" target=\"_blank\">organization's details page</a>. </p> 
 *
 * OpenAPI spec version: 0.34.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.lacunasoftware.signer.client;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.UUID;

/**
 * Contains the ids of the pending actions if there are any.
 */
@Schema(description = "Contains the ids of the pending actions if there are any.")
class LacunaSignerApiFlowActionsPendingActionModel {

  @SerializedName("signerId")
  private UUID signerId = null;

  @SerializedName("signRuleId")
  private UUID signRuleId = null;

  @SerializedName("approverId")
  private UUID approverId = null;
  public LacunaSignerApiFlowActionsPendingActionModel signerId(UUID signerId) {
    this.signerId = signerId;
    return this;
  }

  

  /**
  * Get signerId
  * @return signerId
  **/
  @Schema(description = "")
  public UUID getSignerId() {
    return signerId;
  }
  public void setSignerId(UUID signerId) {
    this.signerId = signerId;
  }
  public LacunaSignerApiFlowActionsPendingActionModel signRuleId(UUID signRuleId) {
    this.signRuleId = signRuleId;
    return this;
  }

  

  /**
  * Get signRuleId
  * @return signRuleId
  **/
  @Schema(description = "")
  public UUID getSignRuleId() {
    return signRuleId;
  }
  public void setSignRuleId(UUID signRuleId) {
    this.signRuleId = signRuleId;
  }
  public LacunaSignerApiFlowActionsPendingActionModel approverId(UUID approverId) {
    this.approverId = approverId;
    return this;
  }

  

  /**
  * Get approverId
  * @return approverId
  **/
  @Schema(description = "")
  public UUID getApproverId() {
    return approverId;
  }
  public void setApproverId(UUID approverId) {
    this.approverId = approverId;
  }
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LacunaSignerApiFlowActionsPendingActionModel lacunaSignerApiFlowActionsPendingActionModel = (LacunaSignerApiFlowActionsPendingActionModel) o;
    return Objects.equals(this.signerId, lacunaSignerApiFlowActionsPendingActionModel.signerId) &&
        Objects.equals(this.signRuleId, lacunaSignerApiFlowActionsPendingActionModel.signRuleId) &&
        Objects.equals(this.approverId, lacunaSignerApiFlowActionsPendingActionModel.approverId);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(signerId, signRuleId, approverId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LacunaSignerApiFlowActionsPendingActionModel {\n");
    
    sb.append("    signerId: ").append(toIndentedString(signerId)).append("\n");
    sb.append("    signRuleId: ").append(toIndentedString(signRuleId)).append("\n");
    sb.append("    approverId: ").append(toIndentedString(approverId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
