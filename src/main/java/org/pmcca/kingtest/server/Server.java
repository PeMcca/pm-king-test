package org.pmcca.kingtest.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.pmcca.kingtest.data.level.LevelDataStore;
import org.pmcca.kingtest.data.level.LocalLevelDataStore;
import org.pmcca.kingtest.data.login.LocalLoginDataStore;
import org.pmcca.kingtest.data.login.LoginDataStore;
import org.pmcca.kingtest.data.score.LocalScoreDataStore;
import org.pmcca.kingtest.data.score.ScoreDataStore;
import org.pmcca.kingtest.data.session.LocalSessionDataStore;
import org.pmcca.kingtest.data.session.SessionDataStore;
import org.pmcca.kingtest.server.resource.RootHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger log = LoggerFactory.getLogger(Server.class);

  private static final String LOCALHOST = "localhost";
  private final int port;

  private final HttpServer httpServer;

  public Server(int port, int backlog, int threadCount) throws IOException {
    this.port = port;
    this.httpServer = HttpServer.create(new InetSocketAddress(LOCALHOST, this.port), backlog);
    this.httpServer.setExecutor(
        Executors.newScheduledThreadPool(threadCount, Executors.defaultThreadFactory()));

    LoginDataStore loginDataStore = new LocalLoginDataStore();
    SessionDataStore sessionDataStore = new LocalSessionDataStore();
    LevelDataStore levelDataStore = new LocalLevelDataStore();
    ScoreDataStore scoreDataStore = new LocalScoreDataStore();
    this.httpServer.createContext(
        RootHandler.PATH,
        new RootHandler(
            new PathMatcher(loginDataStore, sessionDataStore, levelDataStore, scoreDataStore)));
  }

  public void listenAndServe() {
    log.info("Starting HTTP server, listening on {}:{}", LOCALHOST, this.port);
    this.httpServer.start();
  }
}
