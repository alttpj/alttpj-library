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

package io.github.alttpj.library.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnesDecompressor implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(SnesDecompressor.class);

  /**
   * First half byte is the command.
   */
  private static final int HEADER_MASK_COMMAND = 0b11100000;

  /**
   * Second half is the length.
   */
  private static final int HEADER_MASK_LEN = 0b00011111;

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
  private final ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
  private boolean readFully = false;

  private static String toBinaryString(final int command) {
    return String.format(Locale.ENGLISH,
        "%8s", Integer.toBinaryString(command)).replace(' ', '0');
  }

  public SnesDecompressor(final InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public byte[] getDecompressed() {
    ensureReadFully();
    return this.decompressed.toByteArray();
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

    if ((header & 0xFF) == 0xFF) {
      this.eos = true;
      this.readFully = true;
      return null;
    }

    final int command = (header & HEADER_MASK_COMMAND) >> 5;
    // always use one more byte.
    // note: overwrite this if command is 0b0111! Then this is the actual command!
    final int commandLength = (header & HEADER_MASK_LEN) + 1;

    if ((header & HEADER_MASK_COMMAND) == HEADER_MASK_COMMAND) {
      return readExtensionCommand(header);
    }

    return evaluateCommand(command, commandLength);
  }

  private byte[] evaluateCommand(final int command, final int commandLength) throws IOException {
    switch (command) {
      case 0:
        return readCommand0Copy(commandLength);
      case 1:
        return readCommand1RepeatByte(commandLength);
      case 2:
        return readCommand2RepeatWord(commandLength);
      case 3:
        return readCommand3IncreaseByte(commandLength);
      case 4:
        return readCommand4CopyExisting(commandLength);
      default:
        final String commandStr = toBinaryString(command);
        throw new UnsupportedOperationException("Unknown command: [" + commandStr + "].");
    }
  }

  protected byte[] readCommand0Copy(final int commandLength) throws IOException {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Command 0 - Read the next n bytes as is");
    }

    final byte[] outputBuffer = new byte[commandLength];
    final int read = read(outputBuffer, 0, commandLength);

    if (read != commandLength) {
      throw new IllegalStateException("Read [" + read + "] bytes when command was [" + commandLength + "]!");
    }

    return outputBuffer;
  }

  protected byte[] readCommand1RepeatByte(final int commandLength) throws IOException {
    final int toDuplicate = read();
    final byte[] outputBuffer = new byte[commandLength];

    // copy all to output
    for (int ii = 0; ii < commandLength; ii++) {
      outputBuffer[ii] = (byte) toDuplicate;
    }

    return outputBuffer;
  }

  protected byte[] readCommand2RepeatWord(final int commandLength) throws IOException {
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

  /**
   * Read a byte copy it n times to the output buffer (like c1), but incrementing after each copy.
   *
   * @param commandLength
   *     the number of expected output bytes.
   * @return the uncompressed byte array.
   * @throws IOException
   *     error reading the next byte from the stream.
   */
  protected byte[] readCommand3IncreaseByte(final int commandLength) throws IOException {
    int increment = read();
    final byte[] out = new byte[commandLength];

    for (int ii = 0; ii < commandLength; ii++) {
      out[ii] = (byte) increment++;
    }

    return out;
  }

  /**
   * Command 4: copy the next two bytes alternating to the output until length bytes have been copied.
   * Acts as a ringbuffer if length exeeds current decompression output.
   */
  protected byte[] readCommand4CopyExisting(final int commandLength) throws IOException {
    // Command 4
    // A and B form an offset into the current output buffer (the bytes that have already been decompressed and copied to the output)
    // let this offset be X
    // X = A | ( B << 8 )
    // (length parameter + 1) bytes are copied from within the current output buffer and appended to the end of the current output buffer
    final byte[] currentOutputBuffer = this.decompressed.toByteArray();
    final int lengthOutputBuffer = currentOutputBuffer.length;

    final int a = read();
    final int b = read();
    final int x = a | (b << 8);

    // read as ring buffer
    if (x + commandLength > lengthOutputBuffer) {
      final byte[] out = new byte[commandLength];
      int read = 0;
      int pos = x;

      while (read < commandLength) {
        out[read] = currentOutputBuffer[pos];
        read++;
        pos++;
        if (pos >= lengthOutputBuffer) {
          // back to start
          pos = x;
        }
      }

      return out;
    }

    return Arrays.copyOfRange(currentOutputBuffer, x, x + commandLength);
  }

  private byte[] readExtensionCommand(final int header) throws IOException {
    final int command = (header & 0b00011100) >> 2;
    final int extensionByte = read();
    final int commandLength = (((header & 0b00000011) << 8) + extensionByte) + 1;

    return evaluateCommand(command, commandLength);
  }

  private void readFully() {
    try {
      byte[] buffer;

      while ((buffer = inflateNextCommand()) != null) {
        this.decompressed.write(buffer, 0, buffer.length);
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
