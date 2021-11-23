package org.pmcca.kingtest.server.cache;

import java.time.LocalDateTime;
import org.pmcca.kingtest.server.Response;

public class CachedResponse {
  private final Response response;
  private final LocalDateTime localDateTime;

  public CachedResponse(Response response) {
    this.response = response;
    this.localDateTime = LocalDateTime.now();
  }

  public Response getResponse() {
    return response;
  }

  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }
}
