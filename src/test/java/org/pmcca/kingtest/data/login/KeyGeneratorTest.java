package org.pmcca.kingtest.data.login;

import static org.hamcrest.MatcherAssert.*;

import java.util.regex.Pattern;
import org.hamcrest.core.*;
import org.junit.Before;
import org.junit.Test;
import org.pmcca.kingtest.data.session.KeyGenerator;

public class KeyGeneratorTest {

  final Pattern keyRegex = Pattern.compile("^[0-9 a-z A-Z]{10}$");
  KeyGenerator keyGenerator;

  @Before
  public void init() {
    this.keyGenerator = new KeyGenerator();
  }

  @Test
  public void generates_keys_within_range() {
    for (int i = 0; i < 100; i++) {
      String key = keyGenerator.generateKey();
      assertThat(keyRegex.matcher(key).matches(), Is.is(true));
    }
  }
}
