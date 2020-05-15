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

package io.github.alttpj.library.compress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SnesCompressorTest {

  @ParameterizedTest
  @ValueSource(strings = {"1up", "birb", "coin", "icerod", "meat", "yoshi", "z1link"})
  void testCompression(final String gfx) throws IOException {
    // given
    final byte[] compressed;
    final int originalLength;

    // when compressed
    try (
        final InputStream uncompressed = this.getClass().getResourceAsStream("/gfx/u_" + gfx + ".bin");
        final SnesCompressor snesC = new SnesCompressor(uncompressed)) {
      compressed = snesC.getCompressed().toByteArray();
      originalLength = snesC.getOriginalLength();
    }

    // then
    assertAll(
        () -> assertThat("must be smaller than input", compressed.length, is(lessThanOrEqualTo(originalLength))),
        //() -> assertThat("must be smaller than reference.", compressed.length, is(lessThanOrEqualTo(getExpectedMaxLength(gfx)))),
        () -> assertThat("must not be unbelievably small", compressed.length, is(greaterThan(10)))
    );

    // when decompressed
    final byte[] decompressed;
    try (final ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        final SnesDecompressor snesDecompressor = new SnesDecompressor(bais)) {
      decompressed = snesDecompressor.getDecompressed();
    }

    final byte[] original;
    try (final InputStream uncompressed = this.getClass().getResourceAsStream("/gfx/u_" + gfx + ".bin");
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      int read;
      final byte[] data = new byte[2048];
      while ((read = uncompressed.read(data)) != -1) {
        buffer.write(data, 0, read);
      }

      original = buffer.toByteArray();
    }

    // then should be same
    assertThat(decompressed, is(equalTo((original))));
  }

  @Test
  public void testCompressionByteByteByteByte() throws IOException {
    // given
    final byte[] in = new byte[]{
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0xab, (byte) 0xab, (byte) 0xab, (byte) 0xab, (byte) 0xab, (byte) 0xab, (byte) 0xab, (byte) 0xab,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xab, (byte) 0xab, (byte) 0xab, (byte) 0xab,
    };

    // when
    final byte[] compressed;
    try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(in);
        final SnesCompressor snesCompressor = new SnesCompressor(byteArrayInputStream)) {
      compressed = snesCompressor.getCompressed().toByteArray();
    }

    // then
    final byte[] expected = new byte[]{
        (byte) 0b001_00111, (byte) 0x00, // first 8 bytes to command 1 length=7
        (byte) 0b001_00111, (byte) 0xab, // next 8 bytes to command 1 length=7
        (byte) 0b001_01011, (byte) 0x00, // next 12 bytes to command 1 length=11,
        (byte) 0b001_00011, (byte) 0xab, // copy from position 4 of original input stream length=7
        (byte) 0xFF  // end of stream
    };

    assertArrayEquals(expected, compressed, "Should compress like this.");
  }

  private int getExpectedMaxLength(final String gfx) throws IOException {
    int expectedMaxLength = 0;

    try (
        final InputStream compressedInput = this.getClass().getResourceAsStream("/gfx/" + gfx + ".bin")) {
      while (compressedInput.read() != -1) {
        expectedMaxLength++;
      }
    }

    return expectedMaxLength;
  }

  @Test
  void testCompressionSequenceRepeating() throws IOException {
    // given
    final byte[] in = new byte[]{
        (byte) 0xab, (byte) 0xaf, (byte) 0x12, (byte) 0x01, (byte) 0x43, (byte) 0x44, (byte) 0x41, (byte) 0x23,
        (byte) 0xab, (byte) 0xaf, (byte) 0x12, (byte) 0x01, (byte) 0x43, (byte) 0x44, (byte) 0x41, (byte) 0x23
    };

    // when
    final byte[] compressed;
    try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(in);
        final SnesCompressor snesCompressor = new SnesCompressor(byteArrayInputStream)) {
      compressed = snesCompressor.getCompressed().toByteArray();
    }

    // then
    final byte[] expected = {
        // write first 8 (cl=7) bytes as-is
        (byte) 0b000_00111, (byte) 0xab, (byte) 0xaf, (byte) 0x12, (byte) 0x01, (byte) 0x43, (byte) 0x44, (byte) 0x41, (byte) 0x23,
        // make repeat: command 4, length 7, offset 00.
        (byte) 0b100_00111, (byte) 0x00, (byte) 0x00,
        // EOF
        (byte) 0xFF
    };

    assertArrayEquals(expected, compressed, "Should compress like this.");
  }
}
