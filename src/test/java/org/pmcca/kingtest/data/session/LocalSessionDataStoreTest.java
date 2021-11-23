package org.pmcca.kingtest.data.session;

import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.core.*;
import org.junit.Before;
import org.junit.Test;

public class LocalSessionDataStoreTest {

  LocalSessionDataStore localSessionDataStore;

  @Before
  public void init() {
    this.localSessionDataStore = new LocalSessionDataStore();
  }

  @Test
  public void no_mapping_present_returns_null() {
    String sessionKey = "abc123";

    assertThat(localSessionDataStore.containsKeyMapping(sessionKey), Is.is(false));
    assertThat(localSessionDataStore.getUserIdFromSessionKey(sessionKey), IsNull.nullValue());
  }

  @Test
  public void persists_key_mapping() {
    String sessionKey = "abc123";
    int userId = 123;

    assertThat(localSessionDataStore.containsKeyMapping(sessionKey), Is.is(false));
    assertThat(localSessionDataStore.addSessionKeyUserIdMapping(sessionKey, userId), Is.is(true));
    assertThat(localSessionDataStore.containsKeyMapping(sessionKey), Is.is(true));
    assertThat(localSessionDataStore.getUserIdFromSessionKey(sessionKey), Is.is(userId));
  }
}
