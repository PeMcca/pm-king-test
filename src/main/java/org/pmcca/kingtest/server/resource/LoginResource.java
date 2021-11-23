package org.pmcca.kingtest.server.resource;

import com.sun.net.httpserver.HttpExchange;
import java.net.HttpURLConnection;
import org.pmcca.kingtest.data.login.LoginDataStore;
import org.pmcca.kingtest.data.login.User;
import org.pmcca.kingtest.data.session.KeyGenerator;
import org.pmcca.kingtest.data.session.SessionDataStore;
import org.pmcca.kingtest.data.session.SessionKey;
import org.pmcca.kingtest.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginResource implements Resource {
  private static final Logger log = LoggerFactory.getLogger(LoginResource.class);

  private static final int DEFAULT_SESSION_EXP = 10; // 10 minutes

  private final LoginDataStore loginDataStore;
  private final SessionDataStore sessionDataStore;

  private final KeyGenerator keyGenerator;

  public LoginResource(
      LoginDataStore loginDataStore, SessionDataStore sessionDataStore, KeyGenerator keyGenerator) {
    this.loginDataStore = loginDataStore;
    this.sessionDataStore = sessionDataStore;
    this.keyGenerator = keyGenerator;
  }

  // Logs a user in, given by their userId, by setting their session key.
  // If the user does not exist, a new user will be created and their session key set.
  // If the session has not expired yet, the session key will be reset to now.
  @Override
  public Response handleRequest(HttpExchange exchange) {
    User user;
    int userId;
    try {
      userId = extractUserIdFromPath(exchange.getRequestURI().toString());
    } catch (IllegalArgumentException e) {
      log.error("Error extracting userId from path {}", exchange.getRequestURI(), e);
      return new Response("Bad request: Invalid UserID given", HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // Write user (either new or existing) to the datastore, along with their new session key.
    boolean writeResult;
    if (!loginDataStore.userExists(userId)) {
      log.info("User {} does not exist. Creating new user.", userId);
      user = new User(userId);
      writeResult = addUserWithSessionKey(user);
    } else {
      user = loginDataStore.getUserById(userId);
      writeResult = updateUserWithSessionKey(user);
    }

    if (!writeResult) {
      log.error("Could not write user data to datastore {}", userId);
      return new Response("Internal error updating user", HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    // Save session key -> userId mapping
    sessionDataStore.addSessionKeyUserIdMapping(user.getSessionKey().getKey(), user.getUserId());

    log.info("Set user {} session key to {}", userId, user.getSessionKey());
    return new Response(user.getSessionKey().getKey(), HttpURLConnection.HTTP_OK);
  }

  private int extractUserIdFromPath(String path) {
    int userId = Integer.parseInt(path.split("/")[1]);
    if (userId < 0) {
      throw new IllegalArgumentException("Negative user ID given: " + userId);
    }

    return userId;
  }

  private boolean addUserWithSessionKey(User user) {
    user.setSessionKey(new SessionKey(keyGenerator, DEFAULT_SESSION_EXP));
    return loginDataStore.addUser(user);
  }

  private boolean updateUserWithSessionKey(User user) {
    user.setSessionKey(new SessionKey(keyGenerator, DEFAULT_SESSION_EXP));
    return loginDataStore.updateUser(user);
  }
}
