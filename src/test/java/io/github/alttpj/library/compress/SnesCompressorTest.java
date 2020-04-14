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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SnesCompressorTest {

  @ParameterizedTest
  @ValueSource(strings = {"1up", "coin", "meat", "yoshi"})
  void testCompression(final String gfx) throws IOException {
    final int expectedMaxLength = getExpectedMaxLength(gfx);

    final byte[] compressed;
    final int originalLength;

    try (
        final InputStream uncompressed = this.getClass().getResourceAsStream("/gfx/u_" + gfx + ".bin");
        final SnesCompressor snesC = new SnesCompressor(uncompressed)) {
      compressed = snesC.getCompressed().toByteArray();
      originalLength = snesC.getOriginalLength();
    }

    assertAll(
        () -> assertThat("must be smaller than input", compressed.length, is(lessThanOrEqualTo(originalLength))),
        () -> assertThat("must be smaller than reference.", compressed.length, is(lessThanOrEqualTo(expectedMaxLength))),
        () -> assertThat("must not be unbelievably small", compressed.length, is(greaterThan(10)))
    );
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
}
