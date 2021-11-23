package org.pmcca.kingtest.server.resource;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.level.LevelDataStore;
import org.pmcca.kingtest.data.login.LoginDataStore;
import org.pmcca.kingtest.data.login.User;
import org.pmcca.kingtest.data.score.Score;
import org.pmcca.kingtest.data.score.ScoreDataStore;
import org.pmcca.kingtest.data.score.ScoreId;
import org.pmcca.kingtest.data.session.SessionDataStore;
import org.pmcca.kingtest.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostScoreResource implements Resource {
  private static final Logger log = LoggerFactory.getLogger(PostScoreResource.class);

  private final LoginDataStore loginDataStore;
  private final SessionDataStore sessionDataStore;
  private final LevelDataStore levelDataStore;
  private final ScoreDataStore scoreDataStore;

  public PostScoreResource(
      LoginDataStore loginDataStore,
      SessionDataStore sessionDataStore,
      LevelDataStore levelDataStore,
      ScoreDataStore scoreDataStore) {
    this.loginDataStore = loginDataStore;
    this.sessionDataStore = sessionDataStore;
    this.levelDataStore = levelDataStore;
    this.scoreDataStore = scoreDataStore;
  }

  @Override
  public Response handleRequest(HttpExchange exchange) {

    String body =
        new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining());

    if (body.isBlank()) {
      log.info("Body for request {} is empty.", exchange.getRequestURI());
      return new Response("Bad Request: Empty Body", HttpURLConnection.HTTP_BAD_REQUEST);
    }

    int scoreValue;
    try {
      scoreValue = Integer.parseInt(body);
    } catch (NumberFormatException e) {
      log.info("Unable to parse score {} from body", body);
      return new Response(
          "Bad request: Invalid score body " + body, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    ParsedUri parsedUri = parseUri(exchange.getRequestURI());
    if (parsedUri == null) {
      return new Response(
          "Bad request: Invalid URI given " + exchange.getRequestURI(),
          HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Check session is attached to user
    Integer userId = sessionDataStore.getUserIdFromSessionKey(parsedUri.getSessionKey());
    if (userId == null) {
      log.info("User for session {} could not be found", parsedUri.getSessionKey());
      return new Response(
          "Bad request: No active user for sessionKey " + parsedUri.getSessionKey(),
          HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Get user from userId
    User user = loginDataStore.getUserById(userId);
    if (user == null) {
      log.info("User {} not found.", userId);
      return new Response(
          "Bad request: No user found for userId " + userId, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Check if user is logged in
    if (user.getSessionKey().isExpired()) {
      log.info(
          "Session {} for user {} is expired.", user.getSessionKey().getKey(), user.getUserId());
      return new Response(
          "Bad request: User " + userId + "not logged in", HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Get level from levelId
    Level level = levelDataStore.getLevelById(parsedUri.getLevelId());
    if (level == null) {
      log.info("Level {} not found.", parsedUri.getLevelId());
      return new Response(
          "Bad request: Level " + parsedUri.getLevelId() + " not found",
          HttpURLConnection.HTTP_BAD_REQUEST);
    }

    ScoreId scoreId = new ScoreId(user, level);
    Score score = new Score(scoreId, scoreValue, user.getUserId(), level.getLevelId());

    // Attempt to add score to list of scores for id.
    if (scoreDataStore.addScore(scoreId, score)) {
      user.addScore(score.getScoreId());
      level.addScore(score.getScoreId());
      log.info("Saved score {} to level {} and user {}", score, level, user);
    } else {
      log.error("Could not add score {}", score);
      return new Response(
          "Internal error: Could not save score " + scoreValue,
          HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
    return new Response("", HttpURLConnection.HTTP_OK);
  }

  private ParsedUri parseUri(URI uri) {
    try {
      String[] splitUri = uri.toString().split("/");
      int levelId = Integer.parseInt(splitUri[1]);

      String queryParam = uri.getQuery();
      if (queryParam == null) {
        log.error("Query param not found for {}", uri);
        return null;
      }

      String sessionKey = queryParam.split("=")[1];

      return new ParsedUri(levelId, sessionKey);
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      log.error("Unable to parse URI {}", uri, e);
      return null;
    }
  }

  class ParsedUri {
    private final int levelId;
    private final String sessionKey;

    ParsedUri(int levelId, String sessionId) {
      this.levelId = levelId;
      this.sessionKey = sessionId;
    }

    public int getLevelId() {
      return levelId;
    }

    public String getSessionKey() {
      return sessionKey;
    }
  }
}
