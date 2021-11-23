package org.pmcca.kingtest.data.score;

import java.util.List;

public interface ScoreDataStore {

  // Returns list of scores or an empty list if none available
  public List<Score> getScoresById(ScoreId scoreId);

  public boolean addScore(ScoreId scoreId, Score score);
}
