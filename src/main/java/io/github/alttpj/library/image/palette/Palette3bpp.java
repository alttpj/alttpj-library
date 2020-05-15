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

package io.github.alttpj.library.image.palette;

import java.util.Arrays;
import java.util.StringJoiner;

public final class Palette3bpp implements Palette {

  public static final Palette GREEN = new Palette3bpp(Palette3bppColors.GREEN_LOOKUP);

  private final int[][] colors;

  private Palette3bpp(final int[][] colors) {
    this.colors = colors;
  }

  @Override
  public int getBitsPerPixel() {
    return 3;
  }

  @Override
  public int[] getColor(final int in) {
    return this.colors[in];
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "Palette3bpp{", "}")
        .add("colors=" + Arrays.toString(this.colors))
        .toString();
  }

  // Palettes. Must have their own class due to initializing order.

  static class Palette3bppColors {
    private static final int[][] GREEN_LOOKUP = new int[][] {
        // 0b000: light grey
        new int[] {150, 150, 150, 255},
        // 0b001: white // FIX
        new int[] {255, 255, 255, 255},
        // 0b001: light grey
        new int[] {150, 150, 150, 255},
        // 0b011: dark green // fix
        new int[] {0, 150, 0, 255},
        // 0b100: green
        new int[] {0, 255, 0, 255},
        // 0b101: black // FIX
        new int[] {0, 0, 0, 255},
        // 0b110: light blue
        new int[] {100, 100, 255, 255},
        // 0b111: black
        new int[] {0, 0, 0, 255}
    };
  }
}
