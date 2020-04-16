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

import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_NORMAL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

public class CopyCompressAlgorithm extends AbstractCompressionAlgorithm {

  private static final Logger LOG = LoggerFactory.getLogger(CopyCompressAlgorithm.class);

  public CopyCompressAlgorithm() {
    super(0);
  }

  @Override
  public int brute(final byte[] input, final byte[] alreadyProcessedUncompressed) {
    // we would not want to use this for just one byte, but to show up at top when sorted.
    return 0;
  }

  @Override
  public byte[] apply(final byte[] input, final int commandLength) {
    final int contentLength = commandLength + 1;

    if (commandLength > COMMAND_LENGTH_MAX_NORMAL) {
      // we do not want a long copy of a byte array, because it might contain better
      // compression options at other indices.
      LOG.warn("Copying more than COMMAND_LENGTH_MAX_NORMAL(" + COMMAND_LENGTH_MAX_NORMAL + ") bytes.");
      LOG.warn("If you see this message, refactor the code to not use copy for this amount.");
      // add two headers and contentlength = commandlength+1
      final byte[] out = new byte[2 + contentLength];
      writeExtendedHeader(out, commandLength);
      System.arraycopy(input, 0, out, 2, contentLength);

      return out;
    }

    // one header + contentlength
    final byte[] out = new byte[1 + contentLength];
    out[0] = (byte) (commandLength & 0xFF);
    System.arraycopy(input, 0, out, 1, contentLength);

    return out;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "CopyCompressAlgorithm{", "}")
        .add("super=" + super.toString())
        .toString();
  }
}
