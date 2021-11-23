package org.pmcca.kingtest.data.session;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SessionKeyTest {

  @Mock KeyGenerator keyGenerator;

  @Test
  public void isExpired_returns_false_on_valid_key() {
    int expTime = 10;
    String key = "somekey123";
    when(keyGenerator.generateKey()).thenReturn(key);

    SessionKey sessionKey = new SessionKey(keyGenerator, expTime);

    assertThat(sessionKey.isExpired(), is(false));
  }

  @Test
  public void isExpired_returns_true_when_current_time_larger_than_key() {
    int expTime = 0; // Expires immediately: timestamp + 0 = timestamp
    String key = "somekey456";
    when(keyGenerator.generateKey()).thenReturn(key);

    SessionKey sessionKey = new SessionKey(keyGenerator, expTime);

    assertThat(sessionKey.isExpired(), is(true));
  }
}
