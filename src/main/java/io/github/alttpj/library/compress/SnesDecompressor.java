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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnesDecompressor implements AutoCloseable {

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

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  private final InputStream inputStream;
  /**
   * Indicates end of input stream.
   */
  protected boolean eos;
  /**
   * current position in file. remove.
   */
  private int pos;
  private boolean closed = false;
  private byte[] decompressed = new byte[0];
  private boolean readFully = false;

  private static String toBinaryString(final int command) {
    return String.format(Locale.ENGLISH,
        "%8s", Integer.toBinaryString(command)).replace(' ', '0');
  }

  public SnesDecompressor(final InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public static String bytesToHex(final byte[] bytes) {
    final char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }


  public byte[] getDecompressed() {
    ensureReadFully();
    return this.decompressed;
  }

  /**
   * Check to make sure that this stream has not been closed
   */
  private void ensureReadFully() {
    if (!this.readFully) {
      readFully();
    }
  }

  private int read() throws IOException {
    final int read = this.inputStream.read();
    this.pos++;
    final String hex = String.format(Locale.ENGLISH, "%2s", Integer.toHexString(read)).replace(' ', '0');
    LOG.warn("read [{}] at [{}].", hex, this.pos);
    return read;
  }

  public int read(final byte[] outputBuffer, final int off, final int maxReadLength) throws IOException {
    final int read = this.inputStream.read(outputBuffer, off, maxReadLength);
    this.pos += read;
    return read;
  }

  protected byte[] inflateNextCommand() throws IOException {
    if (this.eos) {
      return null;
    }

    final int header = read();
    final int command = header & HEADER_MASK_COMMAND;
    // always use one more byte.
    // note: overwrite this if command is 0b0111! Then this is the actual command!
    final int commandLength = (header & HEADER_MASK_LEN) + 1;

    switch (command) {
      case 0b00000000:
        return readCommand0(commandLength);
      case 0b00010000:
        LOG.warn("Command [{}] at position [{}].", command, this.pos);
        break;
      case 0b00100000:
        return readCommand2(commandLength);
      case 0b00110000:
        LOG.warn("Command [{}] from header [{}] at position [{}].", toBinaryString(command), Integer.toHexString(header), this.pos);
        return readCommand3(commandLength);
      case 0b01000000:
        return readCommand4(commandLength);
      case 0b01110000:
        throw new UnsupportedOperationException("Extension header");
      default:
        final String commandStr = toBinaryString(command);
        throw new UnsupportedOperationException("Unknown command: [" + commandStr + "].");

    }

    throw new IllegalStateException("How did you get here?");
  }

  protected byte[] readCommand0(final int commandLength) throws IOException {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Command 0 - Read the next n bytes as is");
    }

    final byte[] outputBuffer = new byte[commandLength];
    final int read = read(outputBuffer, 0, commandLength);

    if (read != commandLength) {
      throw new IllegalStateException("Read [" + read + "] bytes when commoand was [" + commandLength + "]!");
    }

    return outputBuffer;
  }

  protected byte[] readCommand2(final int commandLength) throws IOException {
    if (LOG.isTraceEnabled()) {
      // doc says sth different.
      LOG.trace("Command 2 - Copy next byte n times?");
    }

    final int toDuplicate = read();

    final byte[] outputBuffer = new byte[commandLength];

    // copy all to output
    for (int ii = 0; ii < commandLength; ii++) {
      outputBuffer[ii] = (byte) toDuplicate;
    }

    return outputBuffer;
  }

  private byte[] readCommand3(final int commandLength) {
    LOG.warn("Len: [{}], Current: [{}]", Integer.toHexString(this.decompressed.length), bytesToHex(this.decompressed));

    throw new UnsupportedOperationException("TODO: imlement");
  }

  /**
   * Command 4: copy the next two bytes alternating to the output until length bytes have been copied.
   */
  protected byte[] readCommand4(final int commandLength) throws IOException {
    // Command 4
    // let the next byte of the input be A
    // let the byte of the input after A be B
    // A and B are copied (alternating, as in ABABABAB...) to the output until (length parameter + 1) bytes have been copied to the output
    final byte[] readBuffer = new byte[2];
    read(readBuffer, 0, 2);

    final byte[] output = new byte[commandLength];

    for (int ii = 0; ii < commandLength; ii++) {
      output[ii] = readBuffer[ii % 2];
    }

    return output;
  }

  private void readFully() {
    try {
      byte[] buffer;

      while ((buffer = inflateNextCommand()) != null) {
        final byte[] newDecompressed = new byte[this.decompressed.length + buffer.length];
        System.arraycopy(this.decompressed, 0, newDecompressed, 0, this.decompressed.length);
        System.arraycopy(buffer, 0, newDecompressed, this.decompressed.length, buffer.length);
        this.decompressed = newDecompressed;
      }

      this.readFully = true;
      this.close();
    } catch (final IOException ioEx) {
      throw new IllegalStateException("Unable to read.", ioEx);
    }
  }

  @Override
  public void close() throws IOException {
    if (this.inputStream != null && !this.closed) {
      this.closed = true;
      this.eos = true;
      this.inputStream.close();
    }
  }
}
