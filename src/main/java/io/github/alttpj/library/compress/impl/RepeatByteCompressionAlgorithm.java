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

import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_EXTENDED;
import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_NORMAL;

import java.util.StringJoiner;

public class RepeatByteCompressionAlgorithm extends AbstractCompressionAlgorithm {

  public RepeatByteCompressionAlgorithm() {
    super(1);
  }

  @Override
  public int brute(final byte[] input, final byte[] alreadyProcessedUncompressed) {
    if (input.length < 4) {
      return 0;
    }

    int repeatCount = 1;
    for (int ii = 1; ii < Math.min(input.length, COMMAND_LENGTH_MAX_EXTENDED); ii++) {
      if (input[ii] != input[0]) {
        break;
      }

      repeatCount++;
    }

    if (repeatCount < 3) {
      // this is the same as just copying using Copy.cl=1
      return 0;
    }

    // command length is count - 1.
    return repeatCount - 1;
  }

  @Override
  public byte[] apply(final byte[] input, final int commandLength) {
    final byte repeatedChar = input[0];

    if (commandLength > COMMAND_LENGTH_MAX_NORMAL) {
      final byte[] out = new byte[3];

      // 0b11100000 (extension command) + length
      writeExtendedHeader(out, commandLength);

      out[2] = repeatedChar;

      return out;
    }

    final byte[] out = new byte[2];
    out[0] = (byte) ((getCommandNum() << 5) + commandLength);
    out[1] = repeatedChar;

    return out;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "RepeatByteCompressionAlgorithm{", "}")
        .add("super=" + super.toString())
        .toString();
  }
}
