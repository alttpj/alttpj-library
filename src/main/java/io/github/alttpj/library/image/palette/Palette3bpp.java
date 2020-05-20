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

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public final class Palette3bpp implements Palette {

  public static final Palette GREEN = new Palette3bpp("GREEN", Palette3bppColors.GREEN_LOOKUP);

  public static final Palette BLUE = new Palette3bpp("BLUE", Palette3bppColors.BLUE_LOOKUP);

  public static final Palette RED = new Palette3bpp("RED", Palette3bppColors.RED_LOOKUP);

  private final Snes3bppColorIndex[] colors;

  private final String name;

  private Palette3bpp(final String name, final Snes3bppColorIndex[] colors) {
    this.name = name.toUpperCase(Locale.ENGLISH);
    this.colors = colors;
  }

  @Override
  public int getBitsPerPixel() {
    return 3;
  }

  @Override
  public int[] getColor(final int in) {
    return this.colors[in].colors;
  }

  @Override
  public List<int[]> getColors() {
    return Arrays.stream(this.colors)
        .map(Snes3bppColorIndex::getColors)
        .collect(toList());
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "Palette3bpp{", "}")
        .add("name=" + this.name)
        .add("colors=" + Arrays.toString(this.colors))
        .toString();
  }

  // Palettes. Must have their own class due to initializing order.

  static class Palette3bppColors {
    private static final Snes3bppColorIndex[] GREEN_LOOKUP = new Snes3bppColorIndex[] {
        // 0b000: transparent
        new Snes3bppColorIndex((byte) 0x00, new int[] {0, 0, 0, 0}),
        // 0b001: white // FIX
        new Snes3bppColorIndex((byte) 0x01, new int[] {248, 248, 248, 255}),
        // 0b001: Red
        new Snes3bppColorIndex((byte) 0x02, new int[] {200, 48, 24, 255}),
        // 0b011: dark green
        new Snes3bppColorIndex((byte) 0x03, new int[] {72, 144, 48, 255}),
        // 0b100: light green
        new Snes3bppColorIndex((byte) 0x04, new int[] {152, 208, 112, 255}),
        // 0b101: black // FIX
        new Snes3bppColorIndex((byte) 0x05, new int[] {40, 40, 40, 255}),
        // 0b110: light yellow
        new Snes3bppColorIndex((byte) 0x06, new int[] {248, 208, 55, 255}),
        // 0b111: dark yellow
        new Snes3bppColorIndex((byte) 0x07, new int[] {184, 136, 32, 255})
    };

    private static final Snes3bppColorIndex[] BLUE_LOOKUP = new Snes3bppColorIndex[] {
        // 0b000: transparent
        new Snes3bppColorIndex((byte) 0x00, new int[] {0, 0, 0, 0}),
        // 0b001: white // FIX
        new Snes3bppColorIndex((byte) 0x01, new int[] {248, 248, 248, 255}),
        // 0b001: pink
        new Snes3bppColorIndex((byte) 0x02, new int[] {248, 128, 176, 255}),
        // 0b011: dark blue
        new Snes3bppColorIndex((byte) 0x03, new int[] {80, 104, 168, 255}),
        // 0b100: light blue
        new Snes3bppColorIndex((byte) 0x04, new int[] {144, 168, 232, 255}),
        // 0b101: black // FIX
        new Snes3bppColorIndex((byte) 0x05, new int[] {40, 40, 40, 255}),
        // 0b110: light gold
        new Snes3bppColorIndex((byte) 0x06, new int[] {248, 176, 80, 255}),
        // 0b111: dark gold-brown
        new Snes3bppColorIndex((byte) 0x07, new int[] {184, 96, 40, 255})
    };

    private static final Snes3bppColorIndex[] RED_LOOKUP = new Snes3bppColorIndex[] {
        // 0b000: transparent
        new Snes3bppColorIndex((byte) 0x00, new int[] {0, 0, 0, 0}),
        // 0b001: white // FIX
        new Snes3bppColorIndex((byte) 0x01, new int[] {248, 248, 248, 255}),
        // 0b001: orange-brown
        new Snes3bppColorIndex((byte) 0x02, new int[] {200, 88, 48, 255}),
        // 0b011: dark red
        new Snes3bppColorIndex((byte) 0x03, new int[] {176, 40, 40, 255}),
        // 0b100: light red
        new Snes3bppColorIndex((byte) 0x04, new int[] {224, 112, 112, 255}),
        // 0b101: black // FIX
        new Snes3bppColorIndex((byte) 0x05, new int[] {40, 40, 40, 255}),
        // 0b110: light silver
        new Snes3bppColorIndex((byte) 0x06, new int[] {184, 184, 200, 255}),
        // 0b111: dark silver
        new Snes3bppColorIndex((byte) 0x07, new int[] {120, 120, 136, 255})
    };
  }

  static class Snes3bppColorIndex {

    private final byte colorIndex;

    private final int[] colors;

    public Snes3bppColorIndex(final byte colorIndex, final int[] colors) {
      this.colorIndex = colorIndex;
      this.colors = colors;
    }

    public byte getColorIndex() {
      return this.colorIndex;
    }

    public int[] getColors() {
      return this.colors;
    }
  }
}
