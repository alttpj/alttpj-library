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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.StringJoiner;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class Alttp3bppImageReaderSpi extends ImageReaderSpi {

  private static final Logger LOG = LoggerFactory.getLogger(Alttp3bppImageReaderSpi.class);

  private static final String[] WRITER_SPI_NAMES = {"com.sun.imageio.plugins.bmp.BMPImageWriterSpi"};
  private static final String[] FORMAT_NAMES = {"3bpp", "3bp"};
  private static final String[] EXTENSIONS = {"3bpp"};
  private static final String[] MIME_TYPE = {"image/3bpp"};

  public Alttp3bppImageReaderSpi() {
    super("Alttp Randomizer Team",
        "1.0.0",
        FORMAT_NAMES,
        EXTENSIONS,
        MIME_TYPE,
        Alttp3bppImageReader.class.getCanonicalName(),
        new Class<?>[] {ImageInputStream.class},
        WRITER_SPI_NAMES,
        false,
        null, null, null, null,
        true,
        "alttp_3bpp",
        Alttp3bppImageFormat.class.getCanonicalName(),
        null, null);
  }

  @Override
  public boolean canDecodeInput(final Object source) {
    LOG.trace("Source: [{}].", source);
    return true;
  }

  @Override
  public ImageReader createReaderInstance(final Object extension) {
    return new Alttp3bppImageReader(this);
  }

  @Override
  public String getDescription(final Locale locale) {
    return "ALTTP 3bpp Image Reader";
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Alttp3bppImageReaderSpi.class.getSimpleName() + "[", "]")
        .add("names=" + Arrays.toString(this.names))
        .add("suffixes=" + Arrays.toString(this.suffixes))
        .add("vendorName='" + this.vendorName + "'")
        .add("version='" + this.version + "'")
        .toString();
  }
}
