package org.pmcca.kingtest.data.level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalLevelDataStore implements LevelDataStore {
  private static final Logger log = LoggerFactory.getLogger(LocalLevelDataStore.class);

  private final Map<Integer, Level> levels;

  public LocalLevelDataStore() {
    this.levels = new ConcurrentHashMap<>();
  }

  @Override
  public Level getLevelById(int levelId) {
    Level level = levels.get(levelId);
    if (level == null) {
      log.info("Level {} not present. Creating.", levelId);
      level = new Level(levelId);
      addLevel(level);
    }
    return level;
  }

  @Override
  public boolean addLevel(Level level) {
    if (levelExists(level.getLevelId())) {
      log.info("Level {} already exists. Not adding level.", level);
      return false;
    }
    levels.put(level.getLevelId(), level);
    return true;
  }

  private boolean levelExists(int levelId) {
    return levels.get(levelId) != null;
  }
}
