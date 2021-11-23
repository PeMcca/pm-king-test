package org.pmcca.kingtest.data.level;

public interface LevelDataStore {

  Level getLevelById(int levelId);

  boolean addLevel(Level level);
}
