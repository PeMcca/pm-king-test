package org.pmcca.kingtest.server.cache;

import org.pmcca.kingtest.server.Response;

public interface ResponseCache {

  boolean addToCache(String key, Response response);

  CachedResponse getResponse(String key);

  void removeStaleResponses();
}
