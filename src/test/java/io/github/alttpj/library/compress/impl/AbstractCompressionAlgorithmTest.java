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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractCompressionAlgorithmTest {

  @Test
  public void testWriteExtendedHeader() {
    // given
    final CopyCompressAlgorithm algo = new CopyCompressAlgorithm();
    final int givenCommandLength = 1;
    final byte[] buffer = new byte[3];

    // when
    algo.writeExtendedHeader(buffer, givenCommandLength);
    // … read again
    final int command = (buffer[0] & 0b00011100) >> 2;
    final int extensionByte = buffer[1];
    final int commandLength = (((buffer[0] & 0b00000011) << 8) + extensionByte);

    // then
    Assertions.assertAll(
        () -> assertEquals(algo.getCommandNum(), command),
        () -> assertEquals(givenCommandLength, commandLength)
    );
  }

}
