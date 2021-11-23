package org.pmcca.kingtest.data.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO if time, poll every minute or so to remove stale sessions
public class LocalSessionDataStore implements SessionDataStore {
  private static final Logger log = LoggerFactory.getLogger(LocalSessionDataStore.class);

  private final Map<String, Integer> sessionKeyToUserId;

  public LocalSessionDataStore() {
    this.sessionKeyToUserId = new ConcurrentHashMap<>();
  }

  @Override
  public Integer getUserIdFromSessionKey(String sessionKey) {
    Integer userId = sessionKeyToUserId.get(sessionKey);
    if (userId == null) {
      log.info("Session key {} not present.", sessionKey);
    }
    return userId;
  }

  // If the key -> userId mapping already exists, refresh the mapping.
  @Override
  public boolean addSessionKeyUserIdMapping(String sessionKey, int userId) {
    sessionKeyToUserId.put(sessionKey, userId);
    return true;
  }

  @Override
  public boolean containsKeyMapping(String sessionKey) {
    return sessionKeyToUserId.get(sessionKey) != null;
  }
}
