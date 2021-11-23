package org.pmcca.kingtest.server.resource;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import org.pmcca.kingtest.server.PathMatcher;
import org.pmcca.kingtest.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The root handler is where all requests start. Due to the limitations of the Oracle HttpServer
 * class in getting path parameters, each URL needs to be manually parsed and mapped to handlers,
 * which the RootHanlder is responsible for.
 */
public class RootHandler implements HttpHandler {
  private static final Logger log = LoggerFactory.getLogger(RootHandler.class);
  public static final String PATH = "/";

  private final PathMatcher pathMatcher;

  public RootHandler(PathMatcher pathMatcher) {
    this.pathMatcher = pathMatcher;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      String uri = exchange.getRequestURI().toString();
      log.info(
          "Got request for resource {}, method {}",
          exchange.getRequestURI(),
          exchange.getRequestMethod());

      Resource res = this.pathMatcher.mapUriToResource(uri, exchange.getRequestMethod());

      if (res != null) {
        Response response = res.handleRequest(exchange);
        writeResponse(exchange, response);
      } else {
        writeResponse(
            exchange,
            new Response(
                "Resource not found: " + exchange.getRequestMethod() + ", " + uri,
                HttpURLConnection.HTTP_NOT_FOUND));
      }

    } catch (IllegalArgumentException e) {
      log.error(
          "Invalid request - Path: {}, Method: {}, Exception: {}",
          exchange.getRequestURI(),
          exchange.getRequestMethod(),
          e);

      writeResponse(
          exchange,
          new Response(
              "Bad request: " + exchange.getRequestMethod() + ", " + exchange.getRequestURI(),
              HttpURLConnection.HTTP_BAD_REQUEST));
    }
    // General catch-all error, returns 500
    catch (Exception e) {
      // TODO: Log more stuff about request
      log.error("Exception while handling request {}: {}", exchange.getRequestURI(), e);
      writeResponse(
          exchange, new Response("Internal server error", HttpURLConnection.HTTP_INTERNAL_ERROR));
    }
  }

  // Write a response to the request, including a response body and HTTP status code
  public void writeResponse(HttpExchange exchange, Response response) throws IOException {
    exchange.sendResponseHeaders(response.getResponseCode(), 0);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(response.getBody().getBytes());
    }
  }
}
