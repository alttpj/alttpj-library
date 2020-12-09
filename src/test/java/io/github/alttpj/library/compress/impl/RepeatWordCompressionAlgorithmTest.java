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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RepeatWordCompressionAlgorithmTest {

  @Test
  public void testSimplePatternApplied() {
    // given
    final RepeatWordCompressionAlgorithm algo = new RepeatWordCompressionAlgorithm();
    final byte[] in = {(byte) 0xfa, (byte) 0xaf, (byte) 0xfa, (byte) 0xaf, (byte) 0xfa};

    // when
    final byte[] compressed = algo.apply(in, in.length - 1);

    // then
    final byte[] expected = new byte[] {(byte) 0b010_00100, (byte) 0xfa, (byte) 0xaf};
    assertArrayEquals(expected, compressed);
  }

  @Test
  public void testSimplePatternDetected() {
    // given
    final RepeatWordCompressionAlgorithm algo = new RepeatWordCompressionAlgorithm();
    final byte[] in = {(byte) 0xfa, (byte) 0xaf, (byte) 0xfa, (byte) 0xaf, (byte) 0xfa, (byte) 0x00};

    // when
    final int commandLength = algo.brute(in, new byte[0]);

    // then
    assertEquals(4, commandLength);
  }
}
