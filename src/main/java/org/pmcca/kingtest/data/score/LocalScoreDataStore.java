package org.pmcca.kingtest.data.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalScoreDataStore implements ScoreDataStore {
  private static final Logger log = LoggerFactory.getLogger(LocalScoreDataStore.class);

  private final Map<String, List<Score>> scores;

  public LocalScoreDataStore() {
    this.scores = new ConcurrentHashMap<>();
  }

  @Override
  public synchronized List<Score> getScoresById(ScoreId scoreId) {
    List<Score> scoresForId = scores.get(scoreId.getId());
    if (scoresForId == null) {
      log.info("Score {} not present.", scoreId);
      return Collections.emptyList();
    }
    return List.copyOf(scoresForId);
  }

  @Override
  public synchronized boolean addScore(ScoreId scoreId, Score score) {
    List<Score> scoresForId = scores.get(scoreId.getId());
    if (scoresForId == null) {
      scoresForId = new ArrayList<>();
    } else if (containsScore(score, scoresForId)) {
      log.info("Score {} already exists. Not adding score.", score);
      return false;
    }

    scoresForId.add(score);
    scores.put(scoreId.getId(), scoresForId);
    return true;
  }

  private boolean containsScore(Score score, List<Score> scoresById) {
    return scoresById.contains(score);
  }
}
