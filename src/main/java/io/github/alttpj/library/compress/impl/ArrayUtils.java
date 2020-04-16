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

package io.github.alttpj.library.compress.impl;

final class ArrayUtils {

  private ArrayUtils() {
    // util class.
  }

  public static boolean equals(final byte[] firstArray, final int faFromIndex, final int faToIndex,
                               final byte[] secondArray, final int saFromIndex, final int saToIndex) {
    rangeCheck(firstArray.length, faFromIndex, faToIndex);
    rangeCheck(secondArray.length, saFromIndex, saToIndex);

    final int aLength = faToIndex - faFromIndex;
    final int bLength = saToIndex - saFromIndex;
    if (aLength != bLength) {
      return false;
    }

    final byte[] aSub = new byte[aLength];
    final byte[] bSub = new byte[bLength];
    System.arraycopy(firstArray, faFromIndex, aSub, 0, aLength);
    System.arraycopy(secondArray, saFromIndex, bSub, 0, bLength);

    for (int ii = 0; ii < aSub.length; ii++) {
      if (aSub[ii] != bSub[ii]) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks that {@code fromIndex} and {@code toIndex} are in
   * the range and throws an exception if they aren't.
   */
  static void rangeCheck(final int arrayLength, final int fromIndex, final int toIndex) {
    if (fromIndex > toIndex) {
      throw new IllegalArgumentException(
          "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
    }
    if (fromIndex < 0) {
      throw new ArrayIndexOutOfBoundsException("From-Index: " + fromIndex);
    }
    if (toIndex > arrayLength) {
      throw new ArrayIndexOutOfBoundsException("To-Index: " + toIndex);
    }
  }
}
