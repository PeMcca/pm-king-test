package org.pmcca.kingtest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import org.pmcca.kingtest.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);
  static AtomicInteger counter = new AtomicInteger(0);

  public static void main(String[] args) throws IOException {
    Server server = new Server(8000, 200, 100);
    server.listenAndServe();
  }
  // TODO clean up main
  static class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      int id = counter.getAndIncrement();
      log.info("Got request {}", id);
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {

      }
      log.info("Returning request {}", id);
      String response = "" + id + " ";
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

  static class MyHandlerB implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      log.info("In handlerB");

      String response = "in handler b";
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }
}
