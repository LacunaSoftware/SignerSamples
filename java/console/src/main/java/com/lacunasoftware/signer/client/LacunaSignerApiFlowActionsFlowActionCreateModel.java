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
import com.lacunasoftware.signer.client.LacunaSignerApiUsersParticipantUserModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LacunaSignerApiFlowActionsFlowActionCreateModel
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-01-07T17:53:40.828-02:00[America/Sao_Paulo]")
class LacunaSignerApiFlowActionsFlowActionCreateModel {
  /**
   * Gets or Sets type
   */
  @JsonAdapter(TypeEnum.Adapter.class)
  public enum TypeEnum {
    SIGNER("Signer"),
    APPROVER("Approver"),
    SIGNRULE("SignRule");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
    public static class Adapter extends TypeAdapter<TypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TypeEnum.fromValue(String.valueOf(value));
      }
    }
  }
  @SerializedName("type")
  private TypeEnum type = null;

  @SerializedName("step")
  private Integer step = null;

  @SerializedName("user")
  private LacunaSignerApiUsersParticipantUserModel user = null;

  @SerializedName("numberRequiredSignatures")
  private Integer numberRequiredSignatures = null;

  @SerializedName("ruleName")
  private String ruleName = null;

  @SerializedName("signRuleUsers")
  private List<LacunaSignerApiUsersParticipantUserModel> signRuleUsers = null;

  @SerializedName("allowElectronicSignature")
  private Boolean allowElectronicSignature = null;
  public LacunaSignerApiFlowActionsFlowActionCreateModel type(TypeEnum type) {
    this.type = type;
    return this;
  }

  

  /**
  * Get type
  * @return type
  **/
  @Schema(description = "")
  public TypeEnum getType() {
    return type;
  }
  public void setType(TypeEnum type) {
    this.type = type;
  }
  public LacunaSignerApiFlowActionsFlowActionCreateModel step(Integer step) {
    this.step = step;
    return this;
  }

  

  /**
  * The order in which this action should take place.
  * @return step
  **/
  @Schema(description = "The order in which this action should take place.")
  public Integer getStep() {
    return step;
  }
  public void setStep(Integer step) {
    this.step = step;
  }
  public LacunaSignerApiFlowActionsFlowActionCreateModel user(LacunaSignerApiUsersParticipantUserModel user) {
    this.user = user;
    return this;
  }

  

  /**
  * Get user
  * @return user
  **/
  @Schema(description = "")
  public LacunaSignerApiUsersParticipantUserModel getUser() {
    return user;
  }
  public void setUser(LacunaSignerApiUsersParticipantUserModel user) {
    this.user = user;
  }
  public LacunaSignerApiFlowActionsFlowActionCreateModel numberRequiredSignatures(Integer numberRequiredSignatures) {
    this.numberRequiredSignatures = numberRequiredSignatures;
    return this;
  }

  

  /**
  * Number of required signatures (if type is SignRule)
  * @return numberRequiredSignatures
  **/
  @Schema(description = "Number of required signatures (if type is SignRule)")
  public Integer getNumberRequiredSignatures() {
    return numberRequiredSignatures;
  }
  public void setNumberRequiredSignatures(Integer numberRequiredSignatures) {
    this.numberRequiredSignatures = numberRequiredSignatures;
  }
  public LacunaSignerApiFlowActionsFlowActionCreateModel ruleName(String ruleName) {
    this.ruleName = ruleName;
    return this;
  }

  

  /**
  * Name of the rule (if type is SignRule)
  * @return ruleName
  **/
  @Schema(description = "Name of the rule (if type is SignRule)")
  public String getRuleName() {
    return ruleName;
  }
  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }
  public LacunaSignerApiFlowActionsFlowActionCreateModel signRuleUsers(List<LacunaSignerApiUsersParticipantUserModel> signRuleUsers) {
    this.signRuleUsers = signRuleUsers;
    return this;
  }

  public LacunaSignerApiFlowActionsFlowActionCreateModel addSignRuleUsersItem(LacunaSignerApiUsersParticipantUserModel signRuleUsersItem) {
    if (this.signRuleUsers == null) {
      this.signRuleUsers = new ArrayList<LacunaSignerApiUsersParticipantUserModel>();
    }
    this.signRuleUsers.add(signRuleUsersItem);
    return this;
  }

  /**
  * Get signRuleUsers
  * @return signRuleUsers
  **/
  @Schema(description = "")
  public List<LacunaSignerApiUsersParticipantUserModel> getSignRuleUsers() {
    return signRuleUsers;
  }
  public void setSignRuleUsers(List<LacunaSignerApiUsersParticipantUserModel> signRuleUsers) {
    this.signRuleUsers = signRuleUsers;
  }
  public LacunaSignerApiFlowActionsFlowActionCreateModel allowElectronicSignature(Boolean allowElectronicSignature) {
    this.allowElectronicSignature = allowElectronicSignature;
    return this;
  }

  

  /**
  * Set to true if the electronic signature option should be available. (only if the type of the action is Signer or SignRule)
  * @return allowElectronicSignature
  **/
  @Schema(description = "Set to true if the electronic signature option should be available. (only if the type of the action is Signer or SignRule)")
  public Boolean isAllowElectronicSignature() {
    return allowElectronicSignature;
  }
  public void setAllowElectronicSignature(Boolean allowElectronicSignature) {
    this.allowElectronicSignature = allowElectronicSignature;
  }
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LacunaSignerApiFlowActionsFlowActionCreateModel lacunaSignerApiFlowActionsFlowActionCreateModel = (LacunaSignerApiFlowActionsFlowActionCreateModel) o;
    return Objects.equals(this.type, lacunaSignerApiFlowActionsFlowActionCreateModel.type) &&
        Objects.equals(this.step, lacunaSignerApiFlowActionsFlowActionCreateModel.step) &&
        Objects.equals(this.user, lacunaSignerApiFlowActionsFlowActionCreateModel.user) &&
        Objects.equals(this.numberRequiredSignatures, lacunaSignerApiFlowActionsFlowActionCreateModel.numberRequiredSignatures) &&
        Objects.equals(this.ruleName, lacunaSignerApiFlowActionsFlowActionCreateModel.ruleName) &&
        Objects.equals(this.signRuleUsers, lacunaSignerApiFlowActionsFlowActionCreateModel.signRuleUsers) &&
        Objects.equals(this.allowElectronicSignature, lacunaSignerApiFlowActionsFlowActionCreateModel.allowElectronicSignature);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(type, step, user, numberRequiredSignatures, ruleName, signRuleUsers, allowElectronicSignature);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LacunaSignerApiFlowActionsFlowActionCreateModel {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    numberRequiredSignatures: ").append(toIndentedString(numberRequiredSignatures)).append("\n");
    sb.append("    ruleName: ").append(toIndentedString(ruleName)).append("\n");
    sb.append("    signRuleUsers: ").append(toIndentedString(signRuleUsers)).append("\n");
    sb.append("    allowElectronicSignature: ").append(toIndentedString(allowElectronicSignature)).append("\n");
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
