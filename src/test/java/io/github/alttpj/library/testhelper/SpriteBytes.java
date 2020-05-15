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

package io.github.alttpj.library.testhelper;

import io.github.alttpj.library.image.Tile;

public final class SpriteBytes {

  private SpriteBytes() {
    // helper
  }

  public static Tile[] get1up() {
    return new Tile[] {
        () -> new byte[] {
            (byte) 0x07, 0x00, (byte) 0x1f, 0x01, (byte) 0x3e, (byte) 0x00, (byte) 0x7c, (byte) 0x00,
            (byte) 0x79, 0x00, (byte) 0xe3, 0x60, (byte) 0xf3, (byte) 0x40, (byte) 0xfb, (byte) 0x00,
            (byte) 0x07, 0x18, (byte) 0x21, 0x43, (byte) 0x46, (byte) 0x9c, (byte) 0x8c, (byte) 0x84
        },
        () -> new byte[] {
            (byte) 0xe0, 0x00, (byte) 0xf8, (byte) 0xe0, (byte) 0x1c, 0x00, (byte) 0x0e, 0x00,
            (byte) 0xe6, 0x00, (byte) 0xf3, (byte) 0x02, (byte) 0xf3, 0x00, (byte) 0xf3, 0x00,
            (byte) 0xe0, 0x18, (byte) 0xe4, (byte) 0xf2, (byte) 0x1a, 0x0d, (byte) 0x0d, 0x0d
        },
        () -> new byte[] {
            (byte) 0xf9, (byte) 0x00, (byte) 0xff, (byte) 0x4f, (byte) 0xff, (byte) 0x70, (byte) 0x7f, (byte) 0x00,
            (byte) 0x3f, (byte) 0x00, (byte) 0x3f, (byte) 0x00, (byte) 0x1f, (byte) 0x00, (byte) 0x0f, (byte) 0x00,
            (byte) 0x86, (byte) 0x80, (byte) 0x8f, (byte) 0x72, (byte) 0x22, (byte) 0x20, (byte) 0x10, (byte) 0x0f
        },
        () -> new byte[] {
            (byte) 0xe7, (byte) 0x00, (byte) 0xff, (byte) 0xf8, (byte) 0xff, (byte) 0x0c, (byte) 0xfe, (byte) 0x00,
            (byte) 0xfc, (byte) 0x00, (byte) 0xfc, (byte) 0x00, (byte) 0xf8, (byte) 0x00, (byte) 0xf0, (byte) 0x00,
            (byte) 0x19, (byte) 0x01, (byte) 0xf1, (byte) 0x43, (byte) 0x44, (byte) 0x04, (byte) 0x08, (byte) 0xf0
        }
    };
  }

}
