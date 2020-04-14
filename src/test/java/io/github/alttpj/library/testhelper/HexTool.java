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

public final class HexTool {

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  private HexTool() {
    // util.
  }

  public static String toHexString(final byte[] bytes) {
    final char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }

    return new String(hexChars).replaceAll(".{2}", "$0 ").trim();
  }


  public static String toBinaryString(final byte[] in) {
    final StringBuffer sb = new StringBuffer();
    for (final byte b : in) {
      final String str = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replaceAll(" ", "0");
      sb.append(str);
      sb.append(' ');
    }

    return sb.toString();
  }

}
