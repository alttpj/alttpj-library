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

import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_EXTENDED;
import static io.github.alttpj.library.compress.CompressorConstants.COMMAND_LENGTH_MAX_NORMAL;
import static java.util.stream.Collectors.toCollection;

import io.github.alttpj.library.compress.impl.CopyCompressAlgorithm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnesCompressor implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(SnesCompressor.class);
  private static final int COPY_MAX_READ = COMMAND_LENGTH_MAX_NORMAL;

  /**
   * Max gain is 48 when using extended header. +1 for the implicit +1.
   */
  private final PushbackInputStream inputStream;

  private final ByteArrayOutputStream output;

  private final ByteArrayOutputStream alreadyProcessed = new ByteArrayOutputStream();

  private boolean isCompressed = false;

  public SnesCompressor(final InputStream inputStream) {
    this.inputStream = new PushbackInputStream(inputStream, COMMAND_LENGTH_MAX_EXTENDED + 1);
    this.output = new ByteArrayOutputStream(2048);
  }

  public ByteArrayOutputStream getCompressed() throws IOException {
    ensureCompressed();
    return this.output;
  }

  private void ensureCompressed() throws IOException {
    if (!this.isCompressed) {
      compressInput();
    }
  }

  private void compressInput() throws IOException {
    final byte[] buffer = new byte[COMMAND_LENGTH_MAX_EXTENDED + 1];
    int readCount;

    while ((readCount = this.inputStream.read(buffer)) != -1) {
      // find best gain from current position.
      this.inputStream.unread(buffer, 0, readCount);
      final byte[] readBytes = new byte[readCount];
      System.arraycopy(buffer, 0, readBytes, 0, readCount);

      final CompressionResult compressionResult = getBestCompressionAlgorithm(readBytes);

      if (compressionResult.getAlgorithm() instanceof CopyCompressAlgorithm) {
        // if best gain is COPY, then iterate up to COMMAND_LENGTH_MAX_NORMAL +1 segments forward to find a better algorithm.
        // this will help to determine the best size for the COPY.
        tryChaining();
        continue;
      }

      // use better algorithm
      final byte[] compressed = compressionResult.apply();
      final int consumed = compressionResult.getCommandLength() + 1;
      final byte[] processed = new byte[consumed];
      final int read = this.inputStream.read(processed);
      this.alreadyProcessed.write(processed, 0, read);

      this.output.write(compressed);
    }

    this.output.write(CompressorConstants.ENF_OF_COMPRESSED_STREAM);

    this.isCompressed = true;
  }

  private CompressionResult getBestCompressionAlgorithm(final byte[] buffer) {
    final TreeSet<CompressionResult> algos = Arrays.stream(CompressionAlgorithms.values())
        .map(CompressionAlgorithms::getCompressionAlgorithm)
        .map(algo -> new CompressionResult(algo, buffer, this.alreadyProcessed.toByteArray()))
        .sorted()
        .collect(toCollection(TreeSet::new));
    return algos.stream()
        .findFirst().orElseThrow(NoSuchElementException::new);
  }

  private void tryChaining() throws IOException {
    int readUncompressed;
    CompressionResult chainedCompressionAlgo = null;

    final int maxRead = getMaxRead();
    // figure out at which position another compression algorithm might be able to kick in.
    for (readUncompressed = 1; readUncompressed < maxRead; readUncompressed++) {
      final byte[] buffer = new byte[maxRead];
      final int read = this.inputStream.read(buffer);
      if (read == -1) {
        return;
      }
      this.inputStream.unread(buffer, 0, read);

      // try compression on this.
      // TODO: simulate alreadyCompressed with readUncompressed added.
      final int maxSizeNewBuffer = Math.min(Math.abs(read - readUncompressed), COMMAND_LENGTH_MAX_EXTENDED);
      final byte[] newBuffer = new byte[maxSizeNewBuffer];
      System.arraycopy(buffer, readUncompressed, newBuffer, 0, newBuffer.length);

      final CompressionResult bestCompressionAlgorithm = getBestCompressionAlgorithm(newBuffer);
      if (!(bestCompressionAlgorithm.getAlgorithm() instanceof CopyCompressAlgorithm)) {
        chainedCompressionAlgo = bestCompressionAlgorithm;
        break;
      }
    }

    LOG.info("Not compressing next [{}] bytes.", readUncompressed);
    byte[] processed = new byte[readUncompressed];
    int read1 = this.inputStream.read(processed);
    byte[] compressed = new CopyCompressAlgorithm().apply(processed, read1 - 1);
    this.alreadyProcessed.write(processed, 0, readUncompressed);
    this.output.write(compressed);

    // did we actually find a chained algo?
    if ((chainedCompressionAlgo == null) || (chainedCompressionAlgo.getAlgorithm() instanceof CopyCompressAlgorithm)) {
      // no.
      return;
    }

    LOG.info("Chaining compression [{}].", chainedCompressionAlgo);

    compressed = chainedCompressionAlgo.apply();
    processed = new byte[chainedCompressionAlgo.getCommandLength() + 1];
    read1 = this.inputStream.read(processed);
    if (read1 == -1) {
      this.output.write(0xFF);
      return;
    }
    this.alreadyProcessed.write(processed, 0, read1);
    this.output.write(compressed);
  }

  /**
   * how much can be read until EOF or 32 bits are read?
   */
  private int getMaxRead() throws IOException {
    final byte[] buffer = new byte[COMMAND_LENGTH_MAX_EXTENDED];
    final int read = this.inputStream.read(buffer);
    if (read == -1) {
      return 0;
    }
    this.inputStream.unread(buffer, 0, read);

    final int min = Math.min(COPY_MAX_READ, read);

    return min;
  }

  @Override
  public void close() throws IOException {
    this.inputStream.close();
    this.output.close();
  }

  /**
   * Returns the number of bytes read from the original file.
   *
   * @return the number of bytes read from the original file.
   * @throws IOException
   *     error reading input.
   */
  protected int getOriginalLength() throws IOException {
    ensureCompressed();

    return this.alreadyProcessed.size();
  }
}
