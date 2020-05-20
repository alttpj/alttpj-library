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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class IOUtils {

  public static byte[] readAllBytes(final InputStream oneUpStream) throws IOException {
    final byte[] inputCompressed;

    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[512];
      int readCount;
      while ((readCount = oneUpStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, readCount);
      }
      inputCompressed = byteArrayOutputStream.toByteArray();
    }
    return inputCompressed;
  }
}
