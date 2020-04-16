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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class Palette3bppTest {

  @Test
  void testGreen_idx4() {
    final Palette green = Palette3bpp.GREEN;

    // when
    final int[] color = green.getColor(4);

    // then
    assertThat("must contain rgba values", color.length, is(4));
  }

  /**
   * Test that array is not too big.
   */
  @Test
  void testGreen_outOfBounds() {
    // given
    final Palette green = Palette3bpp.GREEN;

    // when
    assertThrows(IndexOutOfBoundsException.class, () -> green.getColor(9));
  }
}
