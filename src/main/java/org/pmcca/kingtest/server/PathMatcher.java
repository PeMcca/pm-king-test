package org.pmcca.kingtest.server;

import java.util.regex.Pattern;
import org.pmcca.kingtest.data.level.LevelDataStore;
import org.pmcca.kingtest.data.login.LoginDataStore;
import org.pmcca.kingtest.data.score.ScoreDataStore;
import org.pmcca.kingtest.data.session.KeyGenerator;
import org.pmcca.kingtest.data.session.SessionDataStore;
import org.pmcca.kingtest.server.resource.GetScoreResource;
import org.pmcca.kingtest.server.resource.LoginResource;
import org.pmcca.kingtest.server.resource.PostScoreResource;
import org.pmcca.kingtest.server.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PathMatcher takes a request URI as input and maps it to its known list of valid paths. Resources
 * are bundled into their HTTP method as well as their path.
 */
public class PathMatcher {
  private static final Logger log = LoggerFactory.getLogger(PathMatcher.class);

  private static final ResourcePair LOGIN =
      new ResourcePair(HttpMethod.GET, Pattern.compile("^/[0-9]+/login$"));
  private static final ResourcePair POST_SCORE =
      new ResourcePair(
          HttpMethod.POST, Pattern.compile("^/[0-9]+/score\\?sessionkey=[0-9 a-z A-Z]{10}$"));
  private static final ResourcePair GET_SCORE =
      new ResourcePair(HttpMethod.GET, Pattern.compile("^/[0-9]+/highscorelist$"));

  private final Resource loginResource;
  private final Resource postScoreResource;
  private final Resource getScoreResource;

  public PathMatcher(
      LoginDataStore loginDataStore,
      SessionDataStore sessionDataStore,
      LevelDataStore levelDataStore,
      ScoreDataStore scoreDataStore) {
    this.loginResource = new LoginResource(loginDataStore, sessionDataStore, new KeyGenerator());
    this.postScoreResource =
        new PostScoreResource(loginDataStore, sessionDataStore, levelDataStore, scoreDataStore);
    this.getScoreResource = new GetScoreResource(levelDataStore, scoreDataStore);
  }

  /**
   * Maps a URI path and a HTTP method to the required Resource object
   *
   * @param path The request path that the server received
   * @param method The HTTP method that the server received
   * @return The mapped Resource object, or null if no mapping found
   */
  public Resource mapUriToResource(String path, String method) {
    if (path == null || method == null) {
      throw new IllegalArgumentException(
          "Invalid mapping parameters: Got " + path + " for path and " + method + " for method.");
    }

    if (LOGIN.matches(path, method)) {
      return this.loginResource;
    } else if (POST_SCORE.matches(path, method)) {
      return this.postScoreResource;
    } else if (GET_SCORE.matches(path, method)) {
      return this.getScoreResource;
    }

    log.warn("Could not match path {} and method {} to resource", path, method);
    return null;
  }

  /** Pairs an HTTP method with its valid path */
  static class ResourcePair {
    private final String httpMethod;
    private final Pattern path;

    public ResourcePair(String httpMethod, Pattern path) {
      this.httpMethod = httpMethod;
      this.path = path;
    }

    public String getHttpMethod() {
      return httpMethod;
    }

    public Pattern getPath() {
      return path;
    }

    public boolean matches(String path, String method) {
      return this.path.matcher(path).matches() && this.httpMethod.equals(method);
    }
  }
}
