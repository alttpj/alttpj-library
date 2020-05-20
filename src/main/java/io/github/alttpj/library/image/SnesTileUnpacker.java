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

public class SnesTileUnpacker extends AbstractSnesTileUnpacker {

  public void unpack3bppTiles(final byte[] input,
                              final int inputOffset,
                              final byte[] output,
                              final int outputOffset,
                              final int tileCount) {
    unpackTiles(input, inputOffset, output, outputOffset, tileCount,
        TileConstants.BYTES_PER_TILE_3BPP,
        TileConstants.ROW_INDICES_3BPP,
        TileConstants.BITPLANE_INDICES_3BPP);
  }

  protected byte[] unpack3bppTiles(final byte[] input, final int tileCount) {
    final int totalSize = tileCount * TileConstants.BYTES_PER_TILE_UNPACKED;
    final byte[] buffer = new byte[totalSize];
    unpack3bppTiles(input, 0, buffer, 0, tileCount);

    return buffer;
  }

  public byte[] unpack3bppTiles(final byte[] inputPackedUncompressed) {
    final int tileCount = inputPackedUncompressed.length / TileConstants.BYTES_PER_TILE_3BPP;

    return unpack3bppTiles(inputPackedUncompressed, tileCount);
  }
}
