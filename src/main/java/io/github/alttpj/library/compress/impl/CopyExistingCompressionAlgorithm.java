/*
 * Copyright 2020-2020 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.library.compress.impl;

import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_EXTENDED;
import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_NORMAL;

import io.github.alttpj.library.compress.CompressionAlgorithm;
import java.util.StringJoiner;

public class CopyExistingCompressionAlgorithm extends AbstractCompressionAlgorithm implements CompressionAlgorithm {

  private int posInUncompressed = -1;
  /**
   * Found if longestSubArray if > 0.
   */
  private int longestSubArray = 0;

  public CopyExistingCompressionAlgorithm() {
    super(4);
  }

  @Override
  public int brute(final byte[] input, final byte[] alreadyProcessedUncompressed) {
    // search for the longest match first
    final int max = getMaxSearchLength(input);
    final RingBuffer ringBuffer = new RingBuffer(alreadyProcessedUncompressed);

    if (max < 4) {
      // not worth compressing.
      return 0;
    }

    /*
     * Algorithm:
     * We will start with half of the max possible length, as it is improbable that the
     * whole string can be found.
     *
     * If we found a match, we will try to find a larger string (halfway between current search size and max size).
     * If not, we will try to find a string of half the size until it is smaller than 4.
     */
    final byte[] search = new byte[max / 2];
    System.arraycopy(input, 0, search, 0, search.length);

    this.longestSubArray = findAinB(search, ringBuffer, input, max);

    return this.longestSubArray - 1;
  }

  private int getMaxSearchLength(final byte[] input) {
    return Math.min(input.length, COMMAND_LENGTH_MAX_EXTENDED + 1);
  }

  private int findAinB(byte[] search, final RingBuffer alreadyProcessedUncompressed, final byte[] originalInput, int max) {
    //LOG.info("Trying to find [{}] matching bytes.", search.length);

    final int lastIndexToStartComparing = alreadyProcessedUncompressed.size();

    for (int posInB = 0; posInB < lastIndexToStartComparing; posInB++) {
      if (!ArrayUtils.equals(search, 0, search.length,
          alreadyProcessedUncompressed.toArrayFrom(posInB, search.length), 0, search.length)) {
        continue;
      }

      // found it!
      this.posInUncompressed = posInB;
      this.longestSubArray = search.length;

      break;
    }

    // not found byte[] search in byte[] alreadyProcessedUncompressed
    // (1) either we already have a match, we could return that one.
    // (2) if we dont, search with half the size.

    // (1)
    if (this.posInUncompressed != -1) {
      // see if we can find a even bigger subarray.
      final int newLength = ((search.length + max) / 2) + ((search.length + max) % 2);
      if (newLength <= search.length) {
        // already the best we could do.
        // return previous match length
        return this.longestSubArray;
      }

      // see if we can find the bigger array.
      search = new byte[newLength];
      System.arraycopy(originalInput, 0, search, 0, search.length);
      return findAinB(search, alreadyProcessedUncompressed, originalInput, max);
    }

    // (2)
    max = search.length;
    search = new byte[search.length / 2];
    if (search.length <= 4) {
      // no smaller size possible.
      return this.longestSubArray;
    }

    System.arraycopy(originalInput, 0, search, 0, search.length);

    return findAinB(search, alreadyProcessedUncompressed, originalInput, max);
  }

  public int getPosInUncompressed() {
    return this.posInUncompressed;
  }


  @Override
  public byte[] apply(final byte[] input, final int commandLength) {
    final byte[] offset = getPosInUncompressedAsByteArray();

    if (commandLength > COMMAND_LENGTH_MAX_NORMAL) {
      final byte[] out = new byte[4];

      // 0b11100000 (extension command) + length to bytes 0,1
      writeExtendedHeader(out, commandLength);

      out[2] = offset[0];
      out[3] = offset[1];

      return out;
    }

    final byte[] out = new byte[3];
    out[0] = (byte) ((getCommandNum() << 5) + commandLength);
    out[1] = offset[0];
    out[2] = offset[1];

    return out;
  }

  private byte[] getPosInUncompressedAsByteArray() {
    if (this.posInUncompressed == -1) {
      throw new IllegalStateException("Did not execute brute method.");
    }

    final byte[] out = new byte[2];
    out[0] = (byte) (this.posInUncompressed & 0xFF);
    out[1] = (byte) ((this.posInUncompressed >> 8) & 0xFF);

    return out;
  }

  static String toBinaryString(final byte[] in) {
    final StringBuffer sb = new StringBuffer();
    for (final byte b : in) {
      final String str = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replaceAll(" ", "0");
      sb.append(str);
      sb.append(' ');
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "CopyExistingCompressionAlgorithm{", "}")
        .add("super=" + super.toString())
        .add("posInUncompressed=" + this.posInUncompressed)
        .toString();
  }
}
