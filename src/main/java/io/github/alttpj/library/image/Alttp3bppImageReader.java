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

import io.github.alttpj.library.i18n.I18N;
import io.github.alttpj.library.image.palette.Palette3bpp;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.metadata.IIOMetadata;

public class Alttp3bppImageReader {

  private static final int BUFFER_SIZE = 512;

  private static final int BYTES_PER_TILE_UNPACKED = 64;

  private static final int BYTES_PER_TILE_3BPP = 24;

  private static final int[] ROW_INDICES_3BPP = new int[] {
      0, 0, 1, 1, 2, 2, 3, 3,
      4, 4, 5, 5, 6, 6, 7, 7,
      0, 1, 2, 3, 4, 5, 6, 7
  };

  private static final int[] BITPLANE_INDICES_3BPP = new int[] {
      0, 1, 0, 1, 0, 1, 0, 1,
      0, 1, 0, 1, 0, 1, 0, 1,
      2, 2, 2, 2, 2, 2, 2, 2
  };

  /**
   * The input stream where reads from.
   */
  private final TiledSprite input;

  /**
   * The destination image.
   */
  private BufferedImage bi;
  private boolean aborted;

  /**
   * Constructs an {@code ImageReader}.
   */
  public Alttp3bppImageReader(final TiledSprite input) {
    this.input = input;
  }

  public int getWidth() {
    if (this.input == null) {
      throw new IllegalStateException(I18N.getString("InputStreamNull"));
    }

    final int tileCount = this.input.getTiles().length;
    return tileCount / 2 * 8;
  }

  // implementation

  public int getHeight() {
    if (this.input == null) {
      throw new IllegalStateException(I18N.getString("InputStreamNull"));
    }

    final int tileCount = this.input.getTiles().length;
    return tileCount / 2 * 8;
  }

  public IIOMetadata getImageMetadata() {
    return null;
  }

  public BufferedImage read() throws IOException {
    if (this.input == null) {
      throw new IllegalStateException(I18N.getString("InputStreamNull"));
    }

    clearAbortRequest();
    if (abortRequested()) {
      return this.bi;
    }

    final byte[] decompressedSpriteMap = readPackedTiles(this.input);

    final int tileCount = getTileCount(this.input, decompressedSpriteMap);
    final byte[] unpackedTiles = unpack3bppTiles(decompressedSpriteMap, tileCount);
    final byte[] rasterPaletteData = new byte[4096];
    System.arraycopy(unpackedTiles, 0, rasterPaletteData, 0, tileCount * 64);

    final int height = getHeight();
    final int width = getWidth();
    this.bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final WritableRaster raster = this.bi.getRaster();

    // write across each time, but is reset after a line of tiles.
    // therefore the Y counter is outwards.
    // tiles are always 8x8, so there will be width/8 + height/8 tiles in an image
    for (int tileY = 0; tileY < height / 8; tileY++) {
      // then for each 8x8 tile downwards.
      for (int tileX = 0; tileX < width / 8; tileX++) {
        if (abortRequested()) {
          break;
        }
        drawTile(rasterPaletteData, raster, tileX, tileY, width);
        if (abortRequested()) {
          break;
        }
      }
    }

    return this.bi;
  }

  private void clearAbortRequest() {
    this.aborted = false;
  }

  private boolean abortRequested() {
    return this.aborted;
  }

  private int getTileCount(final TiledSprite input, final byte[] decompressedSpriteMap) {
    final int bitsPerPixel = input.getPalette().getBitsPerPixel();
    final int bytesPerTile;

    // see https://mrclick.zophar.net/TilEd/download/consolegfx.txt
    switch (bitsPerPixel) {
      case 2:
        bytesPerTile = 16;
        break;

      case 3:
        bytesPerTile = 24;
        break;

      default:
        throw new IllegalArgumentException("BPP not supported: " + bitsPerPixel);
    }

    return decompressedSpriteMap.length / bytesPerTile;
  }

  private void drawTile(final byte[] uncompressedOut, final WritableRaster raster, final int tileX, final int tileY, final int width) {
    // offsets are relative to tilenumber * 8
    for (int offsetY = 0; offsetY < 8; offsetY++) {
      final int xOffsetRaster = tileX * 8;

      if (abortRequested()) {
        break;
      }

      for (int offsetX = 0; offsetX < 8; offsetX++) {
        final int yOffsetRaster = tileY * 8;

        if (abortRequested()) {
          break;
        }

        final int arrayTileOffset = xOffsetRaster * 8 + yOffsetRaster * width;
        final int posInDataArray = arrayTileOffset + offsetX + offsetY * 8;
        final byte byteForXY = uncompressedOut[posInDataArray];
        final int[] color = Palette3bpp.GREEN.getColor(byteForXY);

        raster.setPixel(offsetX + xOffsetRaster, offsetY + yOffsetRaster, color);
      }
    }
  }

  /**
   * Reads packed tiles into a byte array.
   */
  protected static byte[] readPackedTiles(final TiledSprite tiledSprite) throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (final Tile tile : tiledSprite.getTiles()) {
      byteArrayOutputStream.write(tile.getBytes());
    }

    return byteArrayOutputStream.toByteArray();
  }

  protected static void unpackRowOfBitplaneOfTile(final byte[] input,
                                                  final int inputOffset,
                                                  final byte[] output,
                                                  final int outputOffset,
                                                  final int bitplaneIndex) {
    for (int pixelIndex = 0; pixelIndex < 8; pixelIndex++) {
      final int bitIndex = 7 - pixelIndex;
      final int bit = (input[inputOffset] >> bitIndex) & 1;
      output[outputOffset + pixelIndex] |= (byte) (bit << bitplaneIndex);
    }
  }

  protected static void unpackTile(final byte[] input,
                                   final int inputOffset,
                                   final byte[] output,
                                   final int outputOffset,
                                   final int[] rowIndices,
                                   final int[] bitplaneIndices) {
    for (int index = 0; index < rowIndices.length; index++) {
      unpackRowOfBitplaneOfTile(input, inputOffset + index, output, outputOffset + rowIndices[index] * 8, bitplaneIndices[index]);
    }
  }

  protected static void unpackTiles(final byte[] input,
                                    final int inputOffset,
                                    final byte[] output,
                                    final int outputOffset,
                                    final int tileCount,
                                    final int bytesPerTile,
                                    final int[] rowIndices,
                                    final int[] bitplaneIndices) {
    for (int index = 0; index < tileCount; index++) {
      unpackTile(input,
          inputOffset + bytesPerTile * index,
          output,
          outputOffset + BYTES_PER_TILE_UNPACKED * index,
          rowIndices,
          bitplaneIndices);
    }
  }

  public static void unpack3bppTiles(final byte[] input,
                                     final int inputOffset,
                                     final byte[] output,
                                     final int outputOffset,
                                     final int tileCount) {
    unpackTiles(input, inputOffset, output, outputOffset, tileCount, BYTES_PER_TILE_3BPP, ROW_INDICES_3BPP, BITPLANE_INDICES_3BPP);
  }

  protected static byte[] unpack3bppTiles(final byte[] input, final int tileCount) {
    final int totalSize = tileCount * BYTES_PER_TILE_UNPACKED;
    final byte[] buffer = new byte[totalSize];
    unpack3bppTiles(input, 0, buffer, 0, tileCount);

    return buffer;
  }

  public static byte[] unpack3bppTiles(final byte[] inputPackedUncompressed) {
    final int tileCount = inputPackedUncompressed.length / BYTES_PER_TILE_3BPP;

    return unpack3bppTiles(inputPackedUncompressed, tileCount);
  }
}
