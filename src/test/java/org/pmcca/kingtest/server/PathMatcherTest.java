package org.pmcca.kingtest.server;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.core.IsNull.*;

import org.junit.Before;
import org.junit.Test;
import org.pmcca.kingtest.server.resource.GetScoreResource;
import org.pmcca.kingtest.server.resource.LoginResource;
import org.pmcca.kingtest.server.resource.PostScoreResource;
import org.pmcca.kingtest.server.resource.Resource;

public class PathMatcherTest {

  private PathMatcher pathMatcher;

  @Before
  public void init() {
    // Not using data stores, so can be null
    this.pathMatcher = new PathMatcher(null, null, null, null);
  }

  @Test
  public void login_path_maps_to_login_resource() {
    String path = "/12345/login";
    String method = HttpMethod.GET;

    Resource actual = this.pathMatcher.mapUriToResource(path, method);

    assertThat(actual, instanceOf(LoginResource.class));
  }

  @Test
  public void postScore_path_maps_to_login_resource() {
    String path = "/12345/score?sessionkey=abc123sjiL";
    String method = HttpMethod.POST;

    Resource actual = this.pathMatcher.mapUriToResource(path, method);

    assertThat(actual, instanceOf(PostScoreResource.class));
  }

  @Test
  public void getScore_path_maps_to_login_resource() {
    String path = "/12345/highscorelist";
    String method = HttpMethod.GET;

    Resource actual = this.pathMatcher.mapUriToResource(path, method);

    assertThat(actual, instanceOf(GetScoreResource.class));
  }

  @Test
  public void no_mapping_returns_null() {
    String path = "someFakePath";
    String method = HttpMethod.GET;

    Resource actual = this.pathMatcher.mapUriToResource(path, method);

    assertThat(actual, nullValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void null_path_throws_IllegalArgumentException() {
    String path = null;
    String method = HttpMethod.GET;

    this.pathMatcher.mapUriToResource(path, method);
  }

  @Test(expected = IllegalArgumentException.class)
  public void null_method_throws_IllegalArgumentException() {
    String path = "/123/login";
    String method = null;

    this.pathMatcher.mapUriToResource(path, method);
  }
}
