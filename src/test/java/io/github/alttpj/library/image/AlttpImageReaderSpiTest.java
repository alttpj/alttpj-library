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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

public class AlttpImageReaderSpiTest {

  @Test
  void testSpiImplemented() {
    // given
    // resource ImageReaderSpi exists

    // when
    final Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("3bpp");

    // then
    assertAll(
        () -> assertTrue(readers.hasNext()),
        () -> assertThat(readers.next(), Matchers.instanceOf(Alttp3bppImageReader.class))
    );
  }

  @Test
  public void testSpiDescription() {
    // given
    final Alttp3bppImageReaderSpi spi = new Alttp3bppImageReaderSpi();

    // when
    final String description = spi.getDescription(Locale.ENGLISH);

    // then
    assertThat(description, containsStringIgnoringCase("3bpp"));
    assertThat(description, containsStringIgnoringCase("ALTTP"));
  }
}
