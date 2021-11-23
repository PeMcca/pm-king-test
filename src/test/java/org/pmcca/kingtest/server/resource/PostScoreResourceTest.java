package org.pmcca.kingtest.server.resource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.level.LevelDataStore;
import org.pmcca.kingtest.data.login.LoginDataStore;
import org.pmcca.kingtest.data.login.User;
import org.pmcca.kingtest.data.score.Score;
import org.pmcca.kingtest.data.score.ScoreDataStore;
import org.pmcca.kingtest.data.score.ScoreId;
import org.pmcca.kingtest.data.session.SessionDataStore;
import org.pmcca.kingtest.data.session.SessionKey;
import org.pmcca.kingtest.server.Response;

@RunWith(MockitoJUnitRunner.class)
public class PostScoreResourceTest {

  @Mock LoginDataStore loginDataStore;

  @Mock SessionDataStore sessionDataStore;

  @Mock LevelDataStore levelDataStore;

  @Mock ScoreDataStore scoreDataStore;

  @Mock HttpExchange exchange;

  @Mock User user;

  @Mock Level level;

  @Mock SessionKey sessionKey;

  PostScoreResource postScoreResource;

  @Before
  public void init() {
    this.postScoreResource =
        new PostScoreResource(loginDataStore, sessionDataStore, levelDataStore, scoreDataStore);
  }

  @Test
  public void posts_users_score_successfully() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    int userId = 456;
    String scoreValue = "100";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ScoreId scoreId = new ScoreId(new User(userId), new Level(levelId));
    Score score = new Score(scoreId, Integer.parseInt(scoreValue), userId, levelId);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    // Mock a perfect run with no errors
    when(user.getUserId()).thenReturn(userId);
    when(level.getLevelId()).thenReturn(levelId);
    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);
    when(sessionDataStore.getUserIdFromSessionKey(sessionKeyVal)).thenReturn(userId);
    when(loginDataStore.getUserById(userId)).thenReturn(user);
    when(user.getSessionKey()).thenReturn(sessionKey);
    when(sessionKey.isExpired()).thenReturn(false);
    when(levelDataStore.getLevelById(levelId)).thenReturn(level);
    when(scoreDataStore.addScore(scoreId, score)).thenReturn(true);

    Response expected = new Response("", HttpURLConnection.HTTP_OK);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(1)).addScore(scoreId, score);
    verify(user, times(1)).addScore(scoreId);
    verify(level, times(1)).addScore(scoreId);
  }

  @Test
  public void no_body_returns_bad_request() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    String scoreValue = "";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);

    Response expected = new Response("Bad Request: Empty Body", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }

  @Test
  public void unparsable_body_returns_bad_request() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    String scoreValue = "abc";

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestBody()).thenReturn(inputStream);

    Response expected =
        new Response(
            "Bad request: Invalid score body " + scoreValue, HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }

  @Test
  public void no_levelId_in_uri_returns_bad_request() throws Exception {
    String sessionKeyVal = "abc123def4";
    String scoreValue = "100";
    String uriPath = "//score?sessionkey=" + sessionKeyVal;
    URI uri = new URI(uriPath);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);

    Response expected =
        new Response(
            "Bad request: Invalid URI given " + uriPath, HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }

  @Test
  public void no_score_in_query_param_returns_bad_request() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    String scoreValue = "100";
    String uriPath = "/" + levelId + "/score?sessionkey=";
    URI uri = new URI(uriPath);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);

    Response expected =
        new Response(
            "Bad request: Invalid URI given " + uriPath, HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }

  @Test
  public void error_adding_score_returns_internal_error() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    int userId = 456;
    String scoreValue = "100";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ScoreId scoreId = new ScoreId(new User(userId), new Level(levelId));
    Score score = new Score(scoreId, Integer.parseInt(scoreValue), userId, levelId);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(user.getUserId()).thenReturn(userId);
    when(level.getLevelId()).thenReturn(levelId);
    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);
    when(sessionDataStore.getUserIdFromSessionKey(sessionKeyVal)).thenReturn(userId);
    when(loginDataStore.getUserById(userId)).thenReturn(user);
    when(user.getSessionKey()).thenReturn(sessionKey);
    when(sessionKey.isExpired()).thenReturn(false);
    when(levelDataStore.getLevelById(levelId)).thenReturn(level);
    when(scoreDataStore.addScore(scoreId, score)).thenReturn(false);

    Response expected =
        new Response(
            "Internal error: Could not save score " + scoreValue,
            HttpURLConnection.HTTP_INTERNAL_ERROR);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(1)).addScore(scoreId, score);
    verify(user, times(0)).addScore(scoreId);
    verify(level, times(0)).addScore(scoreId);
  }

  @Test
  public void no_userId_for_sessionId_returns_bad_request() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    String scoreValue = "100";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);
    when(sessionDataStore.getUserIdFromSessionKey(sessionKeyVal)).thenReturn(null);

    Response expected =
        new Response(
            "Bad request: No active user for sessionKey " + sessionKeyVal,
            HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }

  @Test
  public void no_user_for_userId_returns_bad_request() throws Exception {
    int userId = 123;
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    String scoreValue = "100";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);
    when(sessionDataStore.getUserIdFromSessionKey(sessionKeyVal)).thenReturn(userId);
    when(loginDataStore.getUserById(userId)).thenReturn(null);

    Response expected =
        new Response(
            "Bad request: No user found for userId " + userId, HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }

  @Test
  public void expired_session_key_returns_bad_req_response() throws Exception {
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    int userId = 456;
    String scoreValue = "100";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ScoreId scoreId = new ScoreId(new User(userId), new Level(levelId));
    Score score = new Score(scoreId, Integer.parseInt(scoreValue), userId, levelId);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);
    when(sessionDataStore.getUserIdFromSessionKey(sessionKeyVal)).thenReturn(userId);
    when(loginDataStore.getUserById(userId)).thenReturn(user);
    when(user.getSessionKey()).thenReturn(sessionKey);
    when(sessionKey.isExpired()).thenReturn(true);

    Response expected =
        new Response(
            "Bad request: User " + userId + "not logged in", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(scoreId, score);
    verify(user, times(0)).addScore(scoreId);
    verify(level, times(0)).addScore(scoreId);
  }

  @Test
  public void no_level_for_levelId_returns_bad_request() throws Exception {
    int userId = 123;
    int levelId = 5;
    String sessionKeyVal = "abc123def4";
    String scoreValue = "100";
    URI uri = new URI("/" + levelId + "/score?sessionkey=" + sessionKeyVal);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(scoreValue.getBytes());

    when(exchange.getRequestURI()).thenReturn(uri);
    when(exchange.getRequestBody()).thenReturn(inputStream);
    when(sessionDataStore.getUserIdFromSessionKey(sessionKeyVal)).thenReturn(userId);
    when(loginDataStore.getUserById(userId)).thenReturn(user);
    when(user.getSessionKey()).thenReturn(sessionKey);
    when(sessionKey.isExpired()).thenReturn(false);
    when(levelDataStore.getLevelById(levelId)).thenReturn(null);

    Response expected =
        new Response(
            "Bad request: Level " + levelId + " not found", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = postScoreResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));

    verify(scoreDataStore, times(0)).addScore(any(), any());
    verify(user, times(0)).addScore(any());
    verify(level, times(0)).addScore(any());
  }
}
