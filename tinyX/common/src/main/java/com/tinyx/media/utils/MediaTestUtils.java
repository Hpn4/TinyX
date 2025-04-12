package com.tinyx.media.utils;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class MediaTestUtils {
  private Random rand = new Random();

  /**
   * Generates a completely random byte array. This is used for testing medias by having a different
   * media every time. Every byte value is allowed, as that's what medias will essentially be.
   *
   * @return An array of random bytes of random sizes.
   */
  public byte[] genRandomBytes() {
    int minValue = 0; // The minimum value possible.
    int maxValue = 255; // The maximum value possible.
    int minLen = 10; // Minimum string length
    int maxLen = 1000; // Maximum string length
    return rand.ints(minValue, maxValue)
        .limit(rand.nextInt(minLen, maxLen))
        .toString()
        .getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Generates multiple random byte arrays and returns them. The generating parameters are the same
   * for all of them.
   *
   * @param n The number of arrays to generate.
   * @return The generated arrays.
   */
  public List<byte[]> genRandomBytesList(int n) {
    if (n < 0) return null;
    ArrayList<byte[]> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      list.add(genRandomBytes());
    }
    return list;
  }
}
