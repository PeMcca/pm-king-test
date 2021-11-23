package org.pmcca.kingtest.data.login;

/** LoginDataStore represents a data storage solution for holding all login data. */
public interface LoginDataStore {

  // Fetches a user from the datastore from its ID.
  public User getUserById(int userId);

  // Adds a new user to the datastore
  public boolean addUser(User user);

  // Checks if a user exists in the datastore.
  public boolean userExists(int userId);

  // Takes a user and sets it in the datastore
  public boolean updateUser(User user);
}
