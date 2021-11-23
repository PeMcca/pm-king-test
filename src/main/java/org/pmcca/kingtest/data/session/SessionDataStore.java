package org.pmcca.kingtest.data.session;

/** SessionDataStore stores session key information. */
public interface SessionDataStore {

  Integer getUserIdFromSessionKey(String sessionKey);

  boolean addSessionKeyUserIdMapping(String sessionKey, int userId);

  boolean containsKeyMapping(String sessionKey);
}
