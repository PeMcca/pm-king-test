package org.pmcca.kingtest.data.level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.pmcca.kingtest.data.score.ScoreId;

public class Level {

  private final int levelId;
  private final Map<String, Boolean> scoreIds;

  public Level(int levelId) {
    this.levelId = levelId;
    this.scoreIds = new ConcurrentHashMap<>();
  }

  public int getLevelId() {
    return levelId;
  }

  public synchronized List<String> getScores() {
    return List.copyOf(this.scoreIds.keySet());
  }

  public void addScore(ScoreId scoreId) {
    scoreIds.put(scoreId.getId(), true);
  }

  @Override
  public String toString() {
    return "Level{" + "levelId=" + levelId + '}';
  }
}
