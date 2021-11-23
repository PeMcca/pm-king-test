package org.pmcca.kingtest.server.resource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import java.net.HttpURLConnection;
import java.net.URI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pmcca.kingtest.data.login.LoginDataStore;
import org.pmcca.kingtest.data.login.User;
import org.pmcca.kingtest.data.session.KeyGenerator;
import org.pmcca.kingtest.data.session.SessionDataStore;
import org.pmcca.kingtest.server.Response;

@RunWith(MockitoJUnitRunner.class)
public class LoginResourceTest {

  @Mock LoginDataStore loginDataStore;

  @Mock SessionDataStore sessionDataStore;

  @Mock HttpExchange exchange;

  @Mock KeyGenerator keyGenerator;

  LoginResource loginResource;

  @Before
  public void init() {
    this.loginResource = new LoginResource(loginDataStore, sessionDataStore, keyGenerator);
  }

  @Test
  public void existing_user_can_log_in() throws Exception {
    int userId = 123;
    URI uri = new URI("/" + userId + "/login");
    String sessionKey = "abc123";
    User user = new User(userId);

    when(exchange.getRequestURI()).thenReturn(uri);

    when(loginDataStore.userExists(userId)).thenReturn(true);
    when(loginDataStore.getUserById(userId)).thenReturn(user);
    when(loginDataStore.updateUser(user)).thenReturn(true);
    when(keyGenerator.generateKey()).thenReturn(sessionKey);
    when(sessionDataStore.addSessionKeyUserIdMapping(sessionKey, userId)).thenReturn(true);

    Response expected = new Response(sessionKey, HttpURLConnection.HTTP_OK);

    Response actual = loginResource.handleRequest(exchange);
    verify(loginDataStore, times(0)).addUser(user);
    verify(loginDataStore, times(1)).updateUser(user);
    verify(sessionDataStore, times(1)).addSessionKeyUserIdMapping(sessionKey, userId);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void new_user_created_and_logged_in() throws Exception {
    int userId = 124;
    String sessionKey = "abc124";
    URI uri = new URI("/" + userId + "/login");

    when(exchange.getRequestURI()).thenReturn(uri);

    when(loginDataStore.userExists(userId)).thenReturn(false);
    when(loginDataStore.addUser(any(User.class))).thenReturn(true);
    when(keyGenerator.generateKey()).thenReturn(sessionKey);

    Response expected = new Response(sessionKey, HttpURLConnection.HTTP_OK);

    Response actual = loginResource.handleRequest(exchange);

    verify(loginDataStore, times(1)).addUser(any(User.class));
    verify(loginDataStore, times(0)).getUserById(userId);
    verify(loginDataStore, times(0)).updateUser(any(User.class));
    verify(sessionDataStore, times(1)).addSessionKeyUserIdMapping(sessionKey, userId);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void problem_writing_result_returns_internal_error_response() throws Exception {
    int userId = 124;
    String sessionKey = "abc124";
    URI uri = new URI("/" + userId + "/login");

    when(exchange.getRequestURI()).thenReturn(uri);

    when(loginDataStore.userExists(userId)).thenReturn(false);
    when(keyGenerator.generateKey()).thenReturn(sessionKey);
    when(loginDataStore.addUser(any(User.class))).thenReturn(false);

    Response expected =
        new Response("Internal error updating user", HttpURLConnection.HTTP_INTERNAL_ERROR);

    Response actual = loginResource.handleRequest(exchange);

    verify(loginDataStore, times(1)).addUser(any(User.class));
    verify(loginDataStore, times(0)).getUserById(userId);
    verify(loginDataStore, times(0)).updateUser(any(User.class));
    verify(sessionDataStore, times(0)).addSessionKeyUserIdMapping(sessionKey, userId);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void negative_userId_given_returns_bad_request() throws Exception {
    int userId = -1;
    URI uri = new URI("/" + userId + "/login");

    when(exchange.getRequestURI()).thenReturn(uri);

    Response expected =
        new Response("Bad request: Invalid UserID given", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = loginResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void unparsable_userId_returns_bad_request() throws Exception {
    URI uri = new URI("/invalidId/login");

    when(exchange.getRequestURI()).thenReturn(uri);

    Response expected =
        new Response("Bad request: Invalid UserID given", HttpURLConnection.HTTP_BAD_REQUEST);
    Response actual = loginResource.handleRequest(exchange);

    assertThat(actual, is(equalTo(expected)));
  }
}
