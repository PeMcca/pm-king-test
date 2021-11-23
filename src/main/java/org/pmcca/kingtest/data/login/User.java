package org.pmcca.kingtest.data.login;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.pmcca.kingtest.data.score.ScoreId;
import org.pmcca.kingtest.data.session.SessionKey;

public class User {

  private final int userId;
  private SessionKey sessionKey;
  private final Map<String, Boolean> scoreIds; // TODO test

  public User(int userId) {
    this.userId = userId;
    this.scoreIds = new ConcurrentHashMap<>();
  }

  public int getUserId() {
    return userId;
  }

  public SessionKey getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(SessionKey sessionKey) {
    this.sessionKey = sessionKey;
  }

  public synchronized List<String> getScores() {
    return List.copyOf(this.scoreIds.keySet());
  }

  public void addScore(ScoreId scoreId) {
    scoreIds.put(scoreId.getId(), true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return userId == user.userId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId);
  }

  @Override
  public String toString() {
    return "User{" + "userId=" + userId + ", sessionKey=" + sessionKey + '}';
  }
}
