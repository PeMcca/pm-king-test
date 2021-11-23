package org.pmcca.kingtest.server.resource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.level.LevelDataStore;
import org.pmcca.kingtest.data.login.User;
import org.pmcca.kingtest.data.score.Score;
import org.pmcca.kingtest.data.score.ScoreDataStore;
import org.pmcca.kingtest.data.score.ScoreId;
import org.pmcca.kingtest.data.session.SessionKey;
import org.pmcca.kingtest.server.Response;

@RunWith(MockitoJUnitRunner.class)
public class GetScoreResourceTest {

  @Mock LevelDataStore levelDataStore;

  @Mock ScoreDataStore scoreDataStore;

  @Mock HttpExchange exchange;

  @Mock User user;

  @Mock Level level;

  @Mock SessionKey sessionKey;

  GetScoreResource getScoreResource;

  @Before
  public void init() {
    this.getScoreResource = new GetScoreResource(levelDataStore, scoreDataStore);
  }

  @Test
  public void gets_scores_for_levelId() throws Exception {
    int userId1 = 5;
    int userId2 = 6;
    int levelId = 1;
    int score1 = 100;
    int score2 = 81;
    User user1 = new User(userId1);
    User user2 = new User(userId2);
    Level level = new Level(levelId);
    URI uri = new URI("/" + levelId + "/highscorelist");

    ScoreId scoreId1 = new ScoreId(user1, level);
    ScoreId scoreId2 = new ScoreId(user2, level);

    Score userScore1 = new Score(scoreId1, score1, userId1, levelId);
    Score userScore2 = new Score(scoreId2, score2, userId2, levelId);

    List<Score> scores1 = List.of(userScore1);
    List<Score> scores2 = List.of(userScore2);

    level.addScore(scoreId1);
    level.addScore(scoreId2);

    when(exchange.getRequestURI()).thenReturn(uri);
    when(levelDataStore.getLevelById(levelId)).thenReturn(level);
    when(scoreDataStore.getScoresById(scoreId1)).thenReturn(scores1);
    when(scoreDataStore.getScoresById(scoreId2)).thenReturn(scores2);

    Response expected =
        new Response(
            userId1 + "=" + score1 + "," + userId2 + "=" + score2, HttpURLConnection.HTTP_OK);
    Response actual = getScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void invalid_levelId_in_path_returns_bad_request() throws Exception {
    int levelId = 1;
    URI uri = new URI("/unparsableId/highscorelist");

    when(exchange.getRequestURI()).thenReturn(uri);
    Response expected =
        new Response(
            "Bad request: Unparsable levelId from path " + uri.getPath(),
            HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = getScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void negative_levelId_returns_bad_request() throws Exception {
    int levelId = -1;
    URI uri = new URI("/" + levelId + "/highscorelist");

    when(exchange.getRequestURI()).thenReturn(uri);
    Response expected =
        new Response("Bad request: Negative levelId given", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = getScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void null_level_from_levelId_returns_bad_request() throws Exception {
    int levelId = 1;
    URI uri = new URI("/" + levelId + "/highscorelist");

    when(exchange.getRequestURI()).thenReturn(uri);
    when(levelDataStore.getLevelById(levelId)).thenReturn(null);

    Response expected =
        new Response(
            "Bad request: Level " + levelId + " not found", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = getScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void empty_scoreIds_for_level_returns_bad_request() throws Exception {
    int levelId = 1;
    URI uri = new URI("/" + levelId + "/highscorelist");

    when(exchange.getRequestURI()).thenReturn(uri);
    when(levelDataStore.getLevelById(levelId)).thenReturn(level);
    when(level.getScores()).thenReturn(Collections.emptyList());

    Response expected =
        new Response(
            "Bad request: No scores for level " + levelId, HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = getScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void no_score_found_from_scoreId_returns_internal_error_response() throws Exception {
    int levelId = 1;
    String scoreId = "abc123";
    URI uri = new URI("/" + levelId + "/highscorelist");

    when(exchange.getRequestURI()).thenReturn(uri);
    when(levelDataStore.getLevelById(levelId)).thenReturn(level);
    when(level.getScores()).thenReturn(List.of(scoreId));
    when(scoreDataStore.getScoresById(any(ScoreId.class))).thenReturn(Collections.emptyList());

    Response expected =
        new Response(
            "Internal error: Scores " + scoreId + " not found",
            HttpURLConnection.HTTP_INTERNAL_ERROR);
    Response actual = getScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }
}
