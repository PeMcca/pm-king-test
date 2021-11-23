package org.pmcca.kingtest.server.resource;

import com.sun.net.httpserver.HttpExchange;
import org.pmcca.kingtest.server.Response;

/** A Resource is a valid path that the HTTP server can map to. */
public interface Resource {

  public Response handleRequest(HttpExchange exchange);
}
