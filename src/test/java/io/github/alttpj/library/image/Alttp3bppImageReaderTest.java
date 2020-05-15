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

import io.github.alttpj.library.image.palette.Palette;
import io.github.alttpj.library.image.palette.Palette3bpp;
import io.github.alttpj.library.testhelper.SpriteBytes;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class Alttp3bppImageReaderTest {

  @Test
  void testUnpack3bppTiles() throws IOException {
    final InputStream oneUpPackedUncompressedStream = this.getClass().getResourceAsStream("/gfx/u_1up.bin");
    final byte[] inputPacked = readAllBytes(oneUpPackedUncompressedStream);

    final byte[] unpacked = Alttp3bppImageReader.unpack3bppTiles(inputPacked);

    assertAll(
        () -> assertEquals(4096, unpacked.length)
    );
  }

  @Test
  public void testUncompress() throws IOException {
    final TiledSprite imageInputStream = new TiledSprite() {
      @Override
      public Tile[] getTiles() {
        return SpriteBytes.get1up();
      }

      @Override
      public Palette getPalette() {
        return Palette3bpp.GREEN;
      }
    };

    // when
    final byte[] uncompressedPacked = Alttp3bppImageReader.readPackedTiles(imageInputStream);

    // then
    assertEquals(24 * 4L, uncompressedPacked.length);
  }

  @Test
  public void readImage() throws IOException {
    final TiledSprite tiled1upSprite = new TiledSprite() {
      @Override
      public Tile[] getTiles() {
        return SpriteBytes.get1up();
      }

      @Override
      public Palette getPalette() {
        return Palette3bpp.GREEN;
      }
    };
    final Alttp3bppImageReader alttp3bppImageReader = new Alttp3bppImageReader(tiled1upSprite);

    // when
    final BufferedImage read = alttp3bppImageReader.read();

    // then
    assertAll(
        () -> assertEquals(16, read.getHeight()),
        () -> assertEquals(16, read.getWidth())
    );
  }

  private byte[] readAllBytes(final InputStream oneUpStream) throws IOException {
    final byte[] inputCompressed;

    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[512];
      int readCount;
      while ((readCount = oneUpStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, readCount);
      }
      inputCompressed = byteArrayOutputStream.toByteArray();
    }
    return inputCompressed;
  }
}
