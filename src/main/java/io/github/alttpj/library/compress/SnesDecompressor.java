/*
 * Copyright 2020-${YEAR} the ALttPJ Team @ https://github.com/alttpj
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

package io.github.alttpj.library.compress;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnesDecompressor extends FilterInputStream {

  private static final Logger LOG = LoggerFactory.getLogger(SnesDecompressor.class);

  private static final int BUFFER_SIZE = 512;

  /**
   * First half byte is the command.
   */
  private static final int HEADER_MASK_COMMAND = 0b11110000;

  /**
   * Second half is the length.
   */
  private static final int HEADER_MASK_LEN = 0b00001111;
  private final boolean closed = false;
  /**
   * Indicates end of input stream.
   */
  protected boolean eos;
  /**
   * current position in file. remove.
   */
  private int pos;
  /**
   * If more was read from a command than the output buffer would allow.
   */
  private byte[] remainder = new byte[0];

  public SnesDecompressor(final InputStream inputStream) {
    super(inputStream);
  }

  private static String toBinaryString(final int command) {
    return String.format(Locale.ENGLISH,
        "%8s", Integer.toBinaryString(command)).replace(' ', '0');
  }

  /**
   * Check to make sure that this stream has not been closed
   */
  private void ensureOpen() throws IOException {
    if (this.closed) {
      throw new IOException("Stream closed");
    }
  }

  @Override
  public int read() throws IOException {
    final int read = super.read();
    this.pos++;
    final String hex = String.format(Locale.ENGLISH, "%2s", Integer.toHexString(read)).replace(' ', '0');
    LOG.warn("read [{}] at [{}].", hex, this.pos);
    return read;
  }

  @Override
  public int read(final byte[] outputBuffer, final int off, final int maxReadLength) throws IOException {
    ensureOpen();

    if (this.eos) {
      return -1;
    }

    if (outputBuffer.length < 1) {
      return 0;
    }

    if (maxReadLength > outputBuffer.length) {
      throw new IndexOutOfBoundsException("maxReadLength is greather then the output buffer");
    }

    if (this.remainder.length > 0) {
      final int max = Math.max(maxReadLength, this.remainder.length);
      System.arraycopy(this.remainder, 0, outputBuffer, 0, max);

      if (max == this.remainder.length) {
        // remainder was fully read.
        this.remainder = new byte[0];
      } else {
        // remaining bytes from remainder
        final byte[] newRemainder = new byte[this.remainder.length - max];
        System.arraycopy(this.remainder, max, newRemainder, 0, newRemainder.length);
        this.remainder = newRemainder;
      }

      return max;
    }

    final int header = read();
    final int command = header & HEADER_MASK_COMMAND;
    // always use one more byte.
    final int commandLength = (header & HEADER_MASK_LEN) + 1;

    switch (command) {
      case 0b00000000:
        if (LOG.isTraceEnabled()) {
          LOG.trace("Command 0 - Read the next n bytes as is");
        }

        if (commandLength > maxReadLength) {
          final int read = super.read(outputBuffer, 0, maxReadLength);
          // TODO: implement add to remainder
          this.remainder = new byte[commandLength - maxReadLength];
          final int remainderRead = super.read(this.remainder, 0, this.remainder.length);
          if (remainderRead != this.remainder.length) {
            throw new IllegalStateException("Expected [" + this.remainder.length + "] bytes. Got [" + remainderRead + "].");
          }
          return read;
        }

        return super.read(outputBuffer, 0, commandLength);
      case 0b00010000:
        LOG.warn("Command [{}] at position [{}].", command, this.pos);
        break;
      case 0b00100000:
        if (LOG.isTraceEnabled()) {
          // doc says sth different.
          LOG.trace("Command 2 - Copy next byte n times?");
        }

        final int toDuplicate = read();

        if (commandLength > maxReadLength) {
          // copy maxReadLength to output
          // move the remainder to the remainder
          for (int ii = 0; ii < maxReadLength; ii++) {
            outputBuffer[ii] = (byte) toDuplicate;
          }

          this.remainder = new byte[commandLength - maxReadLength];

          for (int ii = maxReadLength; ii < commandLength; ii++) {
            outputBuffer[ii] = (byte) toDuplicate;
          }

          return maxReadLength;
        }

        // copy all to output
        for (int ii = 0; ii < commandLength; ii++) {
          outputBuffer[ii] = (byte) toDuplicate;
        }

        return commandLength;
      case 0b00110000:
        LOG.warn("Command [{}] from header [{}] at position [{}].", toBinaryString(command), Integer.toHexString(header), this.pos);
        break;
      case 0b01000000:
        // Command 4 - read A, B
        // A and B form an offset into the current output buffer (the bytes that have already been decompressed and copied to the output)
        // let this offset be X
        // X = A | ( B << 8 )
        // (length parameter + 1) bytes are copied from within the current output buffer and appended to the end of the current output buffer
        LOG.warn("Command [{}] from header [{}] at position [{}].", toBinaryString(command), Integer.toHexString(header), this.pos);
        break;
      case 0b01110000:
        throw new UnsupportedOperationException("Extension header");
      default:
        final String commandStr = toBinaryString(command);
        throw new UnsupportedOperationException("Unknown command: [" + commandStr + "].");

    }

    final int n = super.read(outputBuffer, off, maxReadLength);

    if (n == -1) {
      return this.read(outputBuffer, off, maxReadLength);
    }

    return n;
  }

  public byte[] readFully() {
    try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[BUFFER_SIZE];
      int numRead;

      while ((numRead = read(buffer)) != -1) {
        bos.write(buffer, 0, numRead);
      }

      return bos.toByteArray();
    } catch (final IOException ioEx) {
      throw new IllegalStateException("Unable to read.", ioEx);
    }
  }
}
