package org.pmcca.kingtest.data.score;

import java.util.Objects;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.login.User;

/**
 * Generates a scoreId which is a combination of a userId and a levelId. The final ID is of the
 * form: u[userId]l[levelId]
 */
public class ScoreId {
  private final String id;

  public ScoreId(User user, Level level) {
    this.id = "u" + user.getUserId() + "l" + level.getLevelId();
  }

  public ScoreId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScoreId scoreId = (ScoreId) o;
    return Objects.equals(id, scoreId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ScoreId{" + "id='" + id + '\'' + '}';
  }
}
