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

public final class TileConstants {

  public static final int BYTES_PER_TILE_UNPACKED = 64;

  public static final int BYTES_PER_TILE_3BPP = 24;

  public static final int[] ROW_INDICES_3BPP = new int[] {
      0, 0, 1, 1, 2, 2, 3, 3,
      4, 4, 5, 5, 6, 6, 7, 7,
      0, 1, 2, 3, 4, 5, 6, 7
  };

  public static final int[] BITPLANE_INDICES_3BPP = new int[] {
      0, 1, 0, 1, 0, 1, 0, 1,
      0, 1, 0, 1, 0, 1, 0, 1,
      2, 2, 2, 2, 2, 2, 2, 2
  };

  private TileConstants() {
    // util.
  }
}
