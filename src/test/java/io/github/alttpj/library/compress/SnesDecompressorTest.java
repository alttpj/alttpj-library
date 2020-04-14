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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SnesDecompressorTest {

  @ParameterizedTest
  @ValueSource(strings = {"1up", "birb", "coin", "icerod", "meat", "yoshi", "z1link"})
  void testDecompression(final String gfx) throws IOException {
    final byte[] expected;
    try (
        final InputStream oneUpStream = this.getClass().getResourceAsStream("/gfx/u_" + gfx + ".bin")) {
      final byte[] buffer = new byte[2048];
      final int readCount = oneUpStream.read(buffer, 0, buffer.length);
      expected = new byte[readCount];
      System.arraycopy(buffer, 0, expected, 0, readCount);
    }

    // when decompressed
    final byte[] decompressed;
    try (
        final InputStream oneUpStream = this.getClass().getResourceAsStream("/gfx/" + gfx + ".bin");
        final SnesDecompressor snesD = new SnesDecompressor(oneUpStream)) {
      decompressed = snesD.getDecompressed();
    }

    // then
    Assertions.assertAll(
        () -> assertArrayEquals(expected, decompressed)
    );
  }

  @Test
  void testReadExtendedHeaderCommand4Length40() throws IOException {
    // given
    final byte[] bytes = new byte[]{
        // copy one byte "as-is", to set up "already decompressed.
        (byte) 0b000_00000, (byte) 0x00,
        // "extended, command 4, len = 41, offset = 0
        (byte) 0b111_10000, (byte) 0b00101001, (byte) 0b00000000, (byte) 0b00000000};

    // when decompressed
    final byte[] decompressed;
    try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final SnesDecompressor snesDecompressor = new SnesDecompressor(bais)) {
      snesDecompressor.readNextCommand();
      final byte read = (byte) bais.read();
      decompressed = snesDecompressor.readExtensionCommand(read);
    }

    // then
    final byte[] expected = new byte[42];
    assertArrayEquals(expected, decompressed);
  }

  @Test
  void testReadExtendedHeaderCommand4Length40Pos1() throws IOException {
    // given
    final byte[] bytes = new byte[]{
        // copy next two bytes, to set up "already decompressed.
        (byte) 0b001_00001, (byte) 0x00,
        // "extended, command 4, len = 41, offset = 0
        (byte) 0b111_100_00, (byte) 0b00101001, (byte) 0b00000001, (byte) 0b00000000};

    // when decompressed
    final byte[] decompressed;
    try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final SnesDecompressor snesDecompressor = new SnesDecompressor(bais)) {
      snesDecompressor.readNextCommand();
      final byte read = (byte) bais.read();
      decompressed = snesDecompressor.readExtensionCommand(read);
    }

    // then
    final byte[] expected = new byte[42];
    assertArrayEquals(expected, decompressed);
  }
}
