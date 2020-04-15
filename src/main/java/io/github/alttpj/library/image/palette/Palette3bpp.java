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

public class Palette3bpp {

  public static final Palette GREEN = createGreen();
  private static final int[][] lookupGreen = createGreenLookup();

  private static int[][] createGreenLookup() {
    final int[][] greenLookup = new int[8][4];
    // 0b000: light grey
    greenLookup[0][0] = 150;
    greenLookup[0][1] = 150;
    greenLookup[0][2] = 150;
    greenLookup[0][3] = 255;

    // 0b001: dark grey
    greenLookup[1][0] = 50;
    greenLookup[1][1] = 50;
    greenLookup[1][2] = 50;
    greenLookup[1][3] = 255;

    // 0b001: light grey
    greenLookup[2][0] = 150;
    greenLookup[2][1] = 150;
    greenLookup[2][2] = 150;
    greenLookup[2][3] = 255;

    // 0b011: light green
    greenLookup[3][0] = 100;
    greenLookup[3][1] = 255;
    greenLookup[3][2] = 100;
    greenLookup[3][3] = 255;

    // 0b100: green
    greenLookup[4][0] = 0;
    greenLookup[4][1] = 255;
    greenLookup[4][2] = 0;
    greenLookup[4][3] = 255;

    // 0b101: blue
    greenLookup[5][0] = 0;
    greenLookup[5][1] = 0;
    greenLookup[5][2] = 255;
    greenLookup[5][3] = 255;

    // 0b110: light blue
    greenLookup[6][0] = 100;
    greenLookup[6][1] = 100;
    greenLookup[6][2] = 255;
    greenLookup[6][3] = 255;

    // 0b111: black
    greenLookup[7][0] = 0;
    greenLookup[7][1] = 0;
    greenLookup[7][2] = 0;
    greenLookup[7][3] = 255;

    return greenLookup;
  }

  private static Palette createGreen() {
    return in -> lookupGreen[in];
  }
}
