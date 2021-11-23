package org.pmcca.kingtest.data.login;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.core.*;
import org.junit.Before;
import org.junit.Test;

public class LocalLoginDataStoreTest {

  LocalLoginDataStore localLoginDataStore;

  @Before
  public void init() {
    this.localLoginDataStore = new LocalLoginDataStore();
  }

  @Test
  public void existing_user_is_returned() {
    int userId = 123;
    User user = new User(userId);
    localLoginDataStore.addUser(user);
    localLoginDataStore.getUserById(userId);

    User actual = localLoginDataStore.getUserById(userId);
    assertThat(actual.getUserId(), Is.is(equalTo(userId)));
  }

  @Test
  public void addUser_adds_new_user() {
    int userId = 132;
    assertThat(localLoginDataStore.userExists(userId), is(false));

    User newUser = new User(userId);
    assertThat(localLoginDataStore.addUser(newUser), is(true));

    User fetchedUser = localLoginDataStore.getUserById(userId);
    assertThat(localLoginDataStore.userExists(userId), is(true));
    assertThat(fetchedUser, is(equalTo(newUser)));
  }

  @Test
  public void user_not_found_returns_null() {
    int userId = 456;
    assertThat(localLoginDataStore.getUserById(userId), IsNull.nullValue());
    assertThat(localLoginDataStore.userExists(userId), is(false));
  }

  @Test
  public void non_existent_user_cannot_be_updated() {
    int userId = 789; // Doesn't exist in datastore
    assertThat(localLoginDataStore.updateUser(new User(userId)), is(false));
    assertThat(localLoginDataStore.userExists(userId), is(false));
  }
}
