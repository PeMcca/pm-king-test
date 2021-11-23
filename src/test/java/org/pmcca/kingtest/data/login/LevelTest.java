package org.pmcca.kingtest.data.login;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.score.ScoreId;

public class LevelTest {

  @Test
  public void can_add_score_ids_to_level() {
    int levelId = 5;
    int userId = 123;
    Level level = new Level(levelId);
    User user = new User(userId);

    ScoreId expected = new ScoreId(user, level);

    level.addScore(expected);

    List<String> actual = level.getScores();

    assertThat(actual, contains(expected.getId()));
  }

  @Test
  public void list_of_scoreIds_can_be_retrieved() {
    Level level = new Level(6);
    List<ScoreId> scoreIds = generateScoreIds(10, level); // 10 ScoreIds

    for (ScoreId sid : scoreIds) {
      level.addScore(sid);
    }

    List<String> actual = level.getScores();

    assertThat(actual, containsInAnyOrder(scoreIds.stream().map(ScoreId::getId).toArray()));
  }

  private List<ScoreId> generateScoreIds(int count, Level level) {
    List<ScoreId> scoreIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      User user = new User(i);
      scoreIds.add(new ScoreId(user, level));
    }

    return scoreIds;
  }
}
