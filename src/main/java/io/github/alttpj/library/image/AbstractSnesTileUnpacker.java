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

public abstract class AbstractSnesTileUnpacker {

  protected final void unpackRowOfBitplaneOfTile(final byte[] input,
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

  protected final void unpackTile(final byte[] input,
                                  final int inputOffset,
                                  final byte[] output,
                                  final int outputOffset,
                                  final int[] rowIndices,
                                  final int[] bitplaneIndices) {
    for (int index = 0; index < rowIndices.length; index++) {
      unpackRowOfBitplaneOfTile(input, inputOffset + index, output, outputOffset + rowIndices[index] * 8, bitplaneIndices[index]);
    }
  }

  protected final void unpackTiles(final byte[] input,
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
          outputOffset + TileConstants.BYTES_PER_TILE_UNPACKED * index,
          rowIndices,
          bitplaneIndices);
    }
  }

}
