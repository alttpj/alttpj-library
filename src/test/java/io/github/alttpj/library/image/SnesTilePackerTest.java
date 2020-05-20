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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alttpj.library.testhelper.IOUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class SnesTilePackerTest {

  @Test
  public void testEmpty() throws IOException {
    // given uncompressed but packed sprite of 4 tiles.
    final InputStream oneUpPackedUncompressedStream = this.getClass().getResourceAsStream("/gfx/u_1up.bin");
    final byte[] inputPacked = IOUtils.readAllBytes(oneUpPackedUncompressedStream);
    final byte[] unpack3bppTiles = new SnesTileUnpacker().unpack3bppTiles(inputPacked);
    assertEquals(4096, unpack3bppTiles.length);

    // when
    final SnesTilePacker snesTilePacker = new SnesTilePacker();
    final byte[] packed = snesTilePacker.pack3bppTiles(unpack3bppTiles);

    // then
    assertAll(
        () -> assertEquals(1536, packed.length),
        () -> assertArrayEquals(inputPacked, packed)
    );
  }

  @Test
  public void testTileCount() throws IOException {
    // given uncompressed but packed sprite of 4 tiles.
    final InputStream oneUpPackedUncompressedStream = this.getClass().getResourceAsStream("/gfx/u_1up.bin");
    final byte[] inputPacked = IOUtils.readAllBytes(oneUpPackedUncompressedStream);
    final byte[] unpack3bppTiles = new SnesTileUnpacker().unpack3bppTiles(inputPacked);
    assertEquals(4096, unpack3bppTiles.length);

    // when
    final SnesTilePacker snesTilePacker = new SnesTilePacker();
    final byte[][] packed = snesTilePacker.pack3bppTilesIntoTiles(unpack3bppTiles);

    // then
    assertAll(
        () -> assertEquals(64, packed.length)
    );
  }
}
