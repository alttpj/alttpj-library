/*
 * Copyright 2020-${YEAR} the ALttPJ Team @ https://github.com/alttpj
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

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SnesDecompressorTest {

  @ParameterizedTest
  @ValueSource(strings = {"1up", "coin", "meat", "yoshi"})
  void testDecompression(final String gfx) throws IOException {
    final byte[] expected;
    try (
        final InputStream oneUpStream = this.getClass().getResourceAsStream("/gfx/u_" + gfx + ".bin")) {
      final byte[] buffer = new byte[2048];
      final int readCount = oneUpStream.read(buffer, 0, buffer.length);
      expected = new byte[readCount];
      System.arraycopy(buffer, 0, expected, 0, readCount);
    }

    final byte[] decompressed;
    try (
        final InputStream oneUpStream = this.getClass().getResourceAsStream("/gfx/" + gfx + ".bin");
        final SnesDecompressor snesD = new SnesDecompressor(oneUpStream)) {
      decompressed = snesD.getDecompressed();
    }

    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(expected, decompressed)
    );
  }
}
