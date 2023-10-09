package utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomStringGenerator {

  public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String LOWERCASE = UPPERCASE.toLowerCase(Locale.ROOT);
  public static final String DIGITS = "0123456789";
  public static final String ALPHANUMERIC = UPPERCASE + LOWERCASE + DIGITS;
  public static final int DEFAULT_LENGTH = 30;
  public static final int MINIMUM_NUMBER_OF_SYMBOLS = 2;
  private final Random random;
  private final char[] symbols;
  private final char[] buf;

  /**
   * Generate a random string.
   */
  public String nextString() {
    for (int idx = 0; idx < buf.length; ++idx) {
      buf[idx] = symbols[random.nextInt(symbols.length)];
    }
    return new String(buf);
  }

  /**
   * Create an alphanumeric string generator.
   * @param length the length of the string
   * @param random Random seed
   * @param symbols the symbols to be used to generate the string
   */
  public RandomStringGenerator(int length, Random random, String symbols) {
    if (length < 1) {
      throw new IllegalArgumentException();
    }
    if (symbols.length() < MINIMUM_NUMBER_OF_SYMBOLS) {
      throw new IllegalArgumentException();
    }
    this.random = Objects.requireNonNull(random);
    this.symbols = symbols.toCharArray();
    this.buf = new char[length];
  }

  /**
   * Create an alphanumeric string generator.
   */
  public RandomStringGenerator(int length, Random random) {
    this(length, random, ALPHANUMERIC);
  }

  /**
   * Create an alphanumeric strings from a secure generator.
   */
  public RandomStringGenerator(int length) {
    this(length, new SecureRandom());
  }

  /**
   * Create session identifiers.
   */
  public RandomStringGenerator() {
    this(DEFAULT_LENGTH);
  }

}

