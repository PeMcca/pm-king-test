package org.pmcca.kingtest.server.cache;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.pmcca.kingtest.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Caches a given number of responses based on the parameters given in the request. */
public class GetScoreResponseCache implements ResponseCache {
  private static final Logger log = LoggerFactory.getLogger(GetScoreResponseCache.class);

  private final Map<String, CachedResponse> responses;
  private final int cacheLimit;
  private final int staleSeconds;

  public GetScoreResponseCache(int cacheLimit, int staleSeconds) {
    responses = new ConcurrentHashMap<>();
    this.cacheLimit = cacheLimit;
    this.staleSeconds = staleSeconds;
  }

  // TODO: We can make this smarter by attempting to clear the cache if it's full
  @Override
  public synchronized boolean addToCache(String key, Response response) {
    if (responses.size() >= cacheLimit) {
      log.info("Cache is at limit. Not adding response {} for key {}", response, key);
      return false;
    }

    responses.put(key, new CachedResponse(response));
    return true;
  }

  @Override
  public CachedResponse getResponse(String key) {
    return responses.get(key);
  }

  @Override
  public synchronized void removeStaleResponses() {
    int count = 0;
    LocalDateTime now = LocalDateTime.now();
    Set<String> keys = responses.keySet();
    for (String key : keys) {
      LocalDateTime plusStaleTime = responses.get(key).getLocalDateTime().plusSeconds(staleSeconds);
      if (plusStaleTime.isBefore(now)) {
        responses.remove(key);
        count++;
      }
    }
    if (count > 0) {
      log.info("Removed {} responses", count);
    }
  }
}
