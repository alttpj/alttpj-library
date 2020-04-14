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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.alttpj.library.compress.SnesDecompressor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class CopyExistingCompressionAlgorithmTest {

  @Test
  public void testFromExisting_startOfString() throws IOException {
    // given
    final CopyExistingCompressionAlgorithm algo = new CopyExistingCompressionAlgorithm();
    final byte[] input = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe, 0x00, 0x33, 0x66, (byte) 0x99};
    final byte[] existing = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe, 0x1a, 0x00};

    // when
    final int commandLength = algo.brute(input, existing);
    final byte[] compressed = algo.apply(input, commandLength);

    // then
    assertAll(
        () -> assertThat("commandlength should be 3 (cafebabe)-1", commandLength, is(3)),
        () -> assertThat("index should be 0", algo.getPosInUncompressed(), is(0)),
        () -> assertThat("compressed length should be 3 (header, a, b)", compressed.length, is(3)),
        () -> assertThat("compression should be as expected", compressed, is(new byte[]{(byte) 0b10000011, 0, 0}))
    );

    // try to convert back
    final ByteArrayInputStream is = new ByteArrayInputStream(compressed);
    is.skip(1);
    final byte[] bytes = SnesDecompressor.readCommand4CopyExisting(commandLength + 1, existing, is);
    final byte[] expected = {(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void testFromExisting_pos8() throws IOException {
    final CopyExistingCompressionAlgorithm algo = new CopyExistingCompressionAlgorithm();
    final byte[] search = new byte[]{
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0x00, (byte) 0x01,
    };
    final byte[] alreadyProcessed = new byte[]{
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        // next position is 8 and should be found with 5 matching, e.g. commandLength = 4.
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac
    };

    // when
    final int commandLength = algo.brute(search, alreadyProcessed);
    final byte[] compressed = algo.apply(search, commandLength);

    // then
    final byte[] expected = new byte[]{
        // command 4
        (byte) 0b100_00100, (byte) 0x08, (byte) 0x00
    };

    assertArrayEquals(expected, compressed);

    // try to convert back
    final ByteArrayInputStream is = new ByteArrayInputStream(compressed);
    is.read();
    final byte[] bytes = SnesDecompressor.readCommand4CopyExisting(commandLength + 1, alreadyProcessed, is);
    final byte[] expectedOut = {(byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a};
    assertArrayEquals(expectedOut, bytes);

  }

  @Test
  public void testFromExisting_pos8_extended() throws IOException {
    final CopyExistingCompressionAlgorithm algo = new CopyExistingCompressionAlgorithm();
    final byte[] search = new byte[]{
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac,
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac,
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac,
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac,
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac
    };
    final byte[] alreadyProcessed = new byte[]{
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        // next position is 8 and should be found with 5 matching, e.g. commandLength = 4.
        (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x0a, (byte) 0xac, (byte) 0xac, (byte) 0xac
    };

    // when
    final int commandLength = algo.brute(search, alreadyProcessed);
    final byte[] compressed = algo.apply(search, commandLength);

    // then
    final byte[] expected = new byte[]{
        // extended, command 4, length 39, pos 8
        (byte) 0b111_100_00, (byte) 0x27, (byte) 0x08, (byte) 0x00
    };

    assertArrayEquals(expected, compressed);

    // try to convert back
    final ByteArrayInputStream is = new ByteArrayInputStream(compressed);
    is.skip(2);
    final byte[] bytes = SnesDecompressor.readCommand4CopyExisting(commandLength + 1, alreadyProcessed, is);

    assertArrayEquals(search, bytes);
  }

  @Test
  public void testRingBufferFromOneByte() {
    // this would not happen in real life because the repeat command is one bit shorter.
    // but is easier to test the ring buffer this way.
    final CopyExistingCompressionAlgorithm algo = new CopyExistingCompressionAlgorithm();
    final byte[] search = new byte[42];
    final byte[] alreadyProcessed = new byte[1];

    // when
    final int commandLength = algo.brute(search, alreadyProcessed);
    final byte[] compressed = algo.apply(search, commandLength);

    // then
    final byte[] expected = new byte[]{
        // extended, command 4, length 32, pos 0
        (byte) 0b111_100_00, (byte) 0b00101001, (byte) 0b00000000, (byte) 0b00000000
    };

    assertArrayEquals(expected, compressed);
  }

}
