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

package io.github.alttpj.library.image;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alttpj.library.testhelper.IOUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class SnesTileUnpackerTest {

  @Test
  public void testUnpack3bppTiles() throws IOException {
    final InputStream oneUpPackedUncompressedStream = this.getClass().getResourceAsStream("/gfx/u_1up.bin");
    final byte[] inputPacked = IOUtils.readAllBytes(oneUpPackedUncompressedStream);

    final byte[] unpacked = new SnesTileUnpacker().unpack3bppTiles(inputPacked);

    assertAll(
        () -> assertEquals(4096, unpacked.length)
    );
  }
}
