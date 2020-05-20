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

import java.util.Arrays;

public class SnesTilePacker extends AbstractSnesTilePacker {

  public void pack3bppTiles(final byte[] input,
                            final int inputOffset,
                            final byte[] output,
                            final int outputOffset,
                            final int tileCount) {
    packTiles(input,
        inputOffset,
        output,
        outputOffset, tileCount,
        TileConstants.BYTES_PER_TILE_3BPP,
        TileConstants.ROW_INDICES_3BPP,
        TileConstants.BITPLANE_INDICES_3BPP);
  }

  public byte[] pack3bppTiles(final byte[] unpacked3bppTiles) {
    // divide by 4 bpp. Unpacked it is always 4bpp.
    final int tiles = unpacked3bppTiles.length / TileConstants.BYTES_PER_TILE_UNPACKED;

    // packed 3bpp it is always 24 bytes per tile.
    final byte[] output = new byte[TileConstants.BYTES_PER_TILE_3BPP * tiles];
    pack3bppTiles(unpacked3bppTiles, 0, output, 0, tiles);

    return output;
  }

  public byte[][] pack3bppTilesIntoTiles(final byte[] unpacked3bppTiles) {
    final int tileCount = unpacked3bppTiles.length / TileConstants.BYTES_PER_TILE_UNPACKED;
    final byte[] bytes = pack3bppTiles(unpacked3bppTiles);

    final byte[][] tiles = new byte[tileCount][];
    final int tileByteLength = bytes.length / tileCount;
    for (int tileNum = 0; tileNum < tileCount; tileNum++) {
      final int start = tileNum * tileByteLength;
      final int end = start + tileByteLength;
      tiles[tileNum] = Arrays.copyOfRange(bytes, start, end);
    }

    return tiles;
  }
}
