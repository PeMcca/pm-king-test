package org.pmcca.kingtest.server.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheUpdater {
  private final ScheduledExecutorService executorService;

  public CacheUpdater(ResponseCache responseCache, int updateDelaySeconds) {

    this.executorService = Executors.newScheduledThreadPool(1, Executors.defaultThreadFactory());

    executorService.scheduleWithFixedDelay(
        new CacheRunnable(responseCache), 0L, updateDelaySeconds, TimeUnit.SECONDS);
  }

  private static class CacheRunnable implements Runnable {
    ResponseCache responseCache;

    public CacheRunnable(ResponseCache responseCache) {
      this.responseCache = responseCache;
    }

    @Override
    public void run() {
      responseCache.removeStaleResponses();
    }
  }
}
