package org.pmcca.kingtest;

import java.io.IOException;
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
}
