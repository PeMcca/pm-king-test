package org.pmcca.kingtest.data.session;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A KeyGenerator creates a unique 10-digit random string of alphanumeric characters. Internally, a
 * string of all possible characters is chosen from at random 10 times. The final string is built
 * from these 10 random selections and returned.
 */
public class KeyGenerator {
  private static final String DIGITS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

  public String generateKey() {
    StringBuilder builder = new StringBuilder(10);
    for (int i = 0; i < 10; i++) {
      builder.append(DIGITS.charAt(ThreadLocalRandom.current().nextInt(DIGITS.length() - 1)));
    }

    return builder.toString();
  }
}
