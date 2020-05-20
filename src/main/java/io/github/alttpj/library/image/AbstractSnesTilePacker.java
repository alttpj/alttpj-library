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

public abstract class AbstractSnesTilePacker {

  public void packRowOfBitplaneOfTile(final byte[] input,
                                      final int inputOffset,
                                      final byte[] output,
                                      final int outputOffset,
                                      final int bitplaneIndex) {
    for (int pixelIndex = 0; pixelIndex < 8; pixelIndex++) {
      final int bitIndex = 7 - pixelIndex;
      final int bit = (input[inputOffset + pixelIndex] >> bitplaneIndex) & 1;
      output[outputOffset] |= (byte) (bit << bitIndex);
    }
  }

  public void packTile(final byte[] input,
                       final int inputOffset,
                       final byte[] output,
                       final int outputOffset,
                       final int[] rowIndices,
                       final int[] bitplaneIndices) {
    for (int index = 0; index < rowIndices.length; index++) {
      packRowOfBitplaneOfTile(input, inputOffset + rowIndices[index] * 8, output, outputOffset + index, bitplaneIndices[index]);
    }
  }

  public void packTiles(final byte[] input,
                        final int inputOffset,
                        final byte[] output,
                        final int outputOffset,
                        final int tileCount,
                        final int bytesPerTile,
                        final int[] rowIndices,
                        final int[] bitplaneIndices) {
    for (int index = 0; index < tileCount; index++) {
      packTile(input,
          inputOffset + TileConstants.BYTES_PER_TILE_UNPACKED * index,
          output,
          outputOffset + bytesPerTile * index,
          rowIndices,
          bitplaneIndices);
    }
  }
}
