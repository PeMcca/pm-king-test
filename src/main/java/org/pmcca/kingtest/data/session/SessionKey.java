package org.pmcca.kingtest.data.session;

import java.time.LocalDateTime;

/** A SessionKey represents a expMinutes long session for a specific user. */
public class SessionKey {
  private final int expMinutes;
  private final LocalDateTime currentSession;
  private final String key;

  public SessionKey(KeyGenerator keyGenerator, int expMinutes) {
    this.expMinutes = expMinutes;
    this.currentSession = LocalDateTime.now();
    this.key = keyGenerator.generateKey();
  }

  public boolean isExpired() {
    LocalDateTime plusExp = currentSession.plusMinutes(expMinutes);
    return plusExp.isEqual(LocalDateTime.now()) || plusExp.isBefore(LocalDateTime.now());
  }

  public String getKey() {
    return this.key;
  }

  @Override
  public String toString() {
    return "SessionKey{" + "currentSession=" + currentSession + ", key='" + key + '\'' + '}';
  }
}
