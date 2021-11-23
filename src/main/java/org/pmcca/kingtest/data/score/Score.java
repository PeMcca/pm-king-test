package org.pmcca.kingtest.data.score;

import java.util.Objects;

/** A Score is a numeric number coupled with its associated User and Level. */
public class Score {
  private final ScoreId scoreId;
  private final int scoreValue;
  private final int userId;
  private final int levelId;

  public Score(ScoreId scoreId, int score, int userId, int levelId) {
    this.scoreId = scoreId;
    this.scoreValue = score;
    this.userId = userId;
    this.levelId = levelId;
  }

  public ScoreId getScoreId() {
    return this.scoreId;
  }

  public int getScoreValue() {
    return scoreValue;
  }

  public int getUserId() {
    return userId;
  }

  public int getLevelId() {
    return levelId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Score score = (Score) o;
    return scoreValue == score.scoreValue
        && userId == score.userId
        && levelId == score.levelId
        && Objects.equals(scoreId, score.scoreId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scoreId, scoreValue, userId, levelId);
  }

  @Override
  public String toString() {
    return "Score{"
        + "scoreId="
        + scoreId
        + ", scoreValue="
        + scoreValue
        + ", userId="
        + userId
        + ", levelId="
        + levelId
        + '}';
  }
}
