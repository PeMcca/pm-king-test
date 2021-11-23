package org.pmcca.kingtest.server;

import java.util.Objects;

public class Response {
  String body;
  int responseCode;

  public Response(String body, int responseCode) {
    this.body = body;
    this.responseCode = responseCode;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Response response = (Response) o;
    return responseCode == response.responseCode && Objects.equals(body, response.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body, responseCode);
  }

  @Override
  public String toString() {
    return "Response{" + "body='" + body + '\'' + ", responseCode=" + responseCode + '}';
  }
}
