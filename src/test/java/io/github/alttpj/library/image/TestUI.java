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

import io.github.alttpj.library.image.palette.Palette;
import io.github.alttpj.library.image.palette.Palette3bpp;
import io.github.alttpj.library.testhelper.SpriteBytes;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestUI {

  public static void main(final String[] args) throws InterruptedException, IOException {
    final TiledSprite tiledSprite = new TiledSprite() {
      @Override
      public Tile[] getTiles() {
        return SpriteBytes.get1up();
      }

      @Override
      public Palette getPalette() {
        return Palette3bpp.GREEN;
      }
    };
    final Alttp3bppImageReader alttp3bppImageReader = new Alttp3bppImageReader(tiledSprite);
    final BufferedImage bufferedImage = alttp3bppImageReader.read();
    final Image scaledInstance = bufferedImage
        .getScaledInstance(bufferedImage.getWidth() * 4, bufferedImage.getHeight() * 4, Image.SCALE_AREA_AVERAGING);

    final JFrame frame = new JFrame("Test window");
    frame.setMinimumSize(new Dimension(256, 256));
    final ImageIcon icon = new ImageIcon(scaledInstance);
    final JLabel label = new JLabel(icon);
    frame.add(label);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    while (true) {
      TimeUnit.SECONDS.sleep(1L);
      if (!frame.isVisible()) {
        break;
      }
    }
  }
}
