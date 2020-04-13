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
import org.junit.jupiter.api.Test;

public class SnesDecompressorTest {

  @Test
  void testDecompression() throws IOException {
    final byte[] decompressed;
    try (
        final InputStream oneUpStream = this.getClass().getResourceAsStream("/gfx/1up.bin");
        final SnesDecompressor snesD = new SnesDecompressor(oneUpStream)) {
      decompressed = snesD.readFully();
    }

    final byte[] expected;
    try (
        final InputStream oneUpStream = this.getClass().getResourceAsStream("/gfx/u_1up.bin")) {
      final byte[] buffer = new byte[2048];
      final int readCount = oneUpStream.read(buffer, 0, buffer.length);
      expected = new byte[readCount];
      System.arraycopy(buffer, 0, expected, 0, readCount);
    }

    Assertions.assertAll(
        () -> Assertions.assertArrayEquals(null, decompressed)
    );
  }
}
