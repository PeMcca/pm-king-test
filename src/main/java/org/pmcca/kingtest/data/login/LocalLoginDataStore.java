package org.pmcca.kingtest.data.login;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** LocalLoginDataStore is an in-memory data store for login information. */
public class LocalLoginDataStore implements LoginDataStore {
  private static final Logger log = LoggerFactory.getLogger(LocalLoginDataStore.class);

  private final Map<Integer, User> users;

  public LocalLoginDataStore() {
    this.users = new ConcurrentHashMap<>();
  }

  // Tries to get the user by the ID. If the user does not exist,
  // a new user will be created.
  @Override
  public User getUserById(int userId) {
    User user = users.get(userId);
    if (user == null) {
      log.info("User {} not found.", userId);
      return null;
    }

    return user;
  }

  @Override
  public boolean addUser(User user) {
    if (userExists(user.getUserId())) {
      log.info("User {} already exists. Not adding user.", user.getUserId());
      return false;
    }
    users.put(user.getUserId(), user);
    return true;
  }

  @Override
  public boolean userExists(int userId) {
    return users.get(userId) != null;
  }

  // Updates an existing user. If user doesn't exist, nothing will happen.
  @Override
  public boolean updateUser(User user) {
    if (!userExists(user.getUserId())) {
      log.info("User {} does not exist. Not updating.", user.getUserId());
      return false;
    }

    users.put(user.getUserId(), user);
    return true;
  }
}
