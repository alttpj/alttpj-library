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

import io.github.alttpj.library.compress.CompressionAlgorithm;

import java.util.Objects;
import java.util.StringJoiner;

abstract class AbstractCompressionAlgorithm implements CompressionAlgorithm {


  private final int commandNum;

  public AbstractCompressionAlgorithm(final int commandNum) {
    this.commandNum = commandNum;
  }

  /**
   * Returns the maximum command length which can be achieved with this compression command.
   */
  @Override
  public abstract int brute(final byte[] input, byte[] alreadyProcessedUncompressed);

  @Override
  public abstract byte[] apply(final byte[] input, final int commandLength);

  @Override
  public final int getCommandNum() {
    return this.commandNum;
  }

  protected void writeExtendedHeader(final byte[] buffer, final int commandLength) {
    if (buffer.length < 3) {
      throw new IllegalArgumentException("buffer.length < 3 does not make any sense.");
    }

    if (getCommandNum() > 4) {
      throw new IllegalArgumentException("CommandNum too large!");
    }

    if (commandLength > COMMAND_LENGTH_MAX_EXTENDED) {
      throw new IllegalArgumentException(
          "CommandLength cannot exceed [" + (COMMAND_LENGTH_MAX_EXTENDED + 1) + "] but was [" + commandLength + "].");
    }

    // use the bits 0011_0000 from the commandLength int and use them as 0000_0011;
    //     out[1] = (byte) ((this.posInUncompressed >> 8) & 0xFF);
    int headerByte = (byte) (commandLength >> 8 & 0xFF);
    // nudge the commandNum to position 0001_11000.
    headerByte = headerByte | (getCommandNum() << 2);
    // set first three bits to 111 to indicate extended header
    headerByte = headerByte | 0b11100000;
    buffer[0] = (byte) headerByte;
    buffer[1] = (byte) (commandLength & 0xFF);
  }

  @Override
  public final boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    final AbstractCompressionAlgorithm that = (AbstractCompressionAlgorithm) other;
    return this.commandNum == that.commandNum;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.commandNum);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "AbstractCompressionAlgorithm{", "}")
        .add("commandNum=" + this.commandNum)
        .toString();
  }
}
