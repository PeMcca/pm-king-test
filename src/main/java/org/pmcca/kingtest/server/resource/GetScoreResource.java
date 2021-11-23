package org.pmcca.kingtest.server.resource;

import com.sun.net.httpserver.HttpExchange;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.stream.Collectors;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.level.LevelDataStore;
import org.pmcca.kingtest.data.score.Score;
import org.pmcca.kingtest.data.score.ScoreDataStore;
import org.pmcca.kingtest.data.score.ScoreId;
import org.pmcca.kingtest.server.Response;
import org.pmcca.kingtest.server.cache.CacheUpdater;
import org.pmcca.kingtest.server.cache.CachedResponse;
import org.pmcca.kingtest.server.cache.GetScoreResponseCache;
import org.pmcca.kingtest.server.cache.ResponseCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetScoreResource implements Resource {
  private static final Logger log = LoggerFactory.getLogger(GetScoreResource.class);

  private final int SCORE_COUNT = 15; // TODO change back to 15

  private final LevelDataStore levelDataStore;
  private final ScoreDataStore scoreDataStore;
  private final ResponseCache cache;
  private final CacheUpdater cacheUpdater;

  public GetScoreResource(LevelDataStore levelDataStore, ScoreDataStore scoreDataStore) {
    this.levelDataStore = levelDataStore;
    this.scoreDataStore = scoreDataStore;

    // TODO Can pass these in as parameters, as well as the delays.
    this.cache = new GetScoreResponseCache(100, 5);
    this.cacheUpdater = new CacheUpdater(this.cache, 1);
  }

  @Override
  public Response handleRequest(HttpExchange exchange) {
    int levelId;
    try {
      levelId = extractLevelIdFromPath(exchange.getRequestURI().toString());
      if (levelId < 0) {
        log.error("Negative levelId given {}", levelId);
        return new Response(
            "Bad request: Negative levelId given", HttpURLConnection.HTTP_BAD_REQUEST);
      }
    } catch (NumberFormatException e) {
      log.error("Could not parse levelId from path {}", exchange.getRequestURI());
      return new Response(
          "Bad request: Unparsable levelId from path " + exchange.getRequestURI(),
          HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // TODO: More efficient way of getting key
    CachedResponse cachedResponse = cache.getResponse(String.valueOf(levelId));
    if (cachedResponse != null) {
      log.info(
          "Returning cached response {} for levelId {}", cachedResponse.getResponse(), levelId);
      return cachedResponse.getResponse();
    }

    Level level = levelDataStore.getLevelById(levelId);
    if (level == null) { // TODO: Test
      return new Response(
          "Bad request: Level " + levelId + " not found", HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Get all scoreIds associated with this level
    List<String> scoreIdsForLevel = level.getScores();
    if (scoreIdsForLevel.isEmpty()) {
      log.info("No scores for level {}", levelId);
      return new Response(
          "Bad request: No scores for level " + levelId, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Get all scores for IDs
    List<Score> scores = new ArrayList<>();
    for (String scoreId : scoreIdsForLevel) {
      List<Score> scoresById = scoreDataStore.getScoresById(new ScoreId(scoreId));
      if (scoresById == null || scoresById.isEmpty()) {
        log.info("Scores {} for level not found", scoreId);
        return new Response(
            "Internal error: Scores " + scoreId + " not found",
            HttpURLConnection.HTTP_INTERNAL_ERROR);
      }
      scores.add(scoresById.stream().max(Comparator.comparing(Score::getScoreValue)).orElseThrow());
    }

    // Sort in descending order and return top SCORE_COUNT scores
    scores =
        scores.stream()
            .sorted((o1, o2) -> Integer.compare(o2.getScoreValue(), o1.getScoreValue()))
            .limit(Math.min(scores.size(), SCORE_COUNT))
            .collect(Collectors.toList());

    // Build output
    Set<Integer> duplicateUsers = new HashSet<>();
    StringJoiner joiner = new StringJoiner(",");
    for (Score score : scores) {
      if (!duplicateUsers.contains(score.getUserId())) {
        joiner.add(score.getUserId() + "=" + score.getScoreValue());
        duplicateUsers.add(score.getUserId());
      }
    }

    log.info("Got scores {} for level {}", scores, levelId);
    Response response = new Response(joiner.toString(), HttpURLConnection.HTTP_OK);
    if (cache.addToCache(String.valueOf(levelId), response)) {
      log.info("Cached response {} for levelId {}", response, levelId);
    }
    return response;
  }

  private int extractLevelIdFromPath(String path) {
    return Integer.parseInt(path.split("/")[1]);
  }
}
