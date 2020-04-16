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

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import javax.imageio.ImageTypeSpecifier;

public class Alttp3bppImageFormatTest {

  @Test
  void testApiStable() {
    // given
    final Alttp3bppImageFormat alttp3bppImageFormat = new Alttp3bppImageFormat();
    final ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_BYTE_INDEXED);

    // when
    final boolean canNodeAppear =
        alttp3bppImageFormat.canNodeAppear("node", imageType);

    assertFalse(canNodeAppear);
  }
}
