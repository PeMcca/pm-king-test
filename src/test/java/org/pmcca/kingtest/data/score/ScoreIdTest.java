package org.pmcca.kingtest.data.score;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.pmcca.kingtest.data.level.Level;
import org.pmcca.kingtest.data.login.User;

public class ScoreIdTest {

  @Test
  public void scoreId_constructor_joins_user_and_level_ids() {
    User user = new User(123);
    Level level = new Level(456);
    ScoreId scoreId = new ScoreId(user, level);
    String expected = "u" + user.getUserId() + "l" + level.getLevelId();

    assertThat(scoreId.getId(), is(equalTo(expected)));
  }
}
