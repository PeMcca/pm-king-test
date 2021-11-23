package org.pmcca.kingtest.data.score;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.login.User;

public class LocalScoreDataStoreTest {

  LocalScoreDataStore scoreDataStore;

  @Before
  public void init() {
    this.scoreDataStore = new LocalScoreDataStore();
  }

  @Test
  public void can_add_new_scores() {
    User user = new User(123);
    Level level = new Level(456);
    ScoreId scoreId = new ScoreId(user, level);
    Score score = new Score(scoreId, 100, user.getUserId(), level.getLevelId());

    assertThat(scoreDataStore.getScoresById(scoreId), empty());

    assertThat(scoreDataStore.addScore(scoreId, score), is(true));

    List<Score> expected = List.of(score);
    List<Score> actual = scoreDataStore.getScoresById(scoreId);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void duplicate_scores_are_not_added() {
    User user = new User(123);
    Level level = new Level(456);
    ScoreId scoreId = new ScoreId(user, level);
    Score score = new Score(scoreId, 100, user.getUserId(), level.getLevelId());

    assertThat(scoreDataStore.getScoresById(scoreId), empty());

    assertThat(scoreDataStore.addScore(scoreId, score), is(true));

    List<Score> expected = List.of(score);
    List<Score> actual = scoreDataStore.getScoresById(scoreId);

    assertThat(actual, is(equalTo(expected)));

    // Try and add same score again
    assertThat(scoreDataStore.addScore(scoreId, score), is(false));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void all_scores_for_scoreId_returned() {
    // Generate 10 scores for same Id
    User user = new User(123);
    Level level = new Level(456);
    ScoreId scoreId = new ScoreId(user, level);
    List<Score> expected = generateScores(10, scoreId, user, level);

    for (Score score : expected) {
      assertThat(scoreDataStore.addScore(scoreId, score), is(true));
    }

    List<Score> actual = scoreDataStore.getScoresById(scoreId);

    assertThat(actual.size(), is(10));
    assertThat(actual, containsInAnyOrder(expected.toArray()));
  }

  private static List<Score> generateScores(int count, ScoreId scoreId, User user, Level level) {
    List<Score> scores = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      scores.add(new Score(scoreId, i * 12, user.getUserId(), level.getLevelId()));
    }
    return scores;
  }
}
