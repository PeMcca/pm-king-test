package org.pmcca.kingtest.data.level;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.core.*;
import org.junit.Before;
import org.junit.Test;

public class LocalLevelDataStoreTest {

  LocalLevelDataStore levelDataStore;

  @Before
  public void init() {
    this.levelDataStore = new LocalLevelDataStore();
  }

  @Test
  public void can_add_new_level() {
    int levelId = 12;

    Level expected = new Level(levelId);

    assertThat(levelDataStore.addLevel(expected), is(true));

    Level actual = levelDataStore.getLevelById(levelId);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void cannot_add_duplicate_level() {
    int levelId = 12;

    Level expected = new Level(levelId);

    assertThat(levelDataStore.addLevel(expected), is(true));

    Level actual = levelDataStore.getLevelById(levelId);

    assertThat(actual, is(equalTo(expected)));

    // Add duplicate level
    assertThat(levelDataStore.addLevel(expected), is(false));
    actual = levelDataStore.getLevelById(levelId);
    assertThat(actual, is(equalTo(expected)));
  }
}
