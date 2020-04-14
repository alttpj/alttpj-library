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
  private static final int COPY_MAX_READ = 32;

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
      this.inputStream.unread(buffer);

      final TreeSet<CompressionResult> algos = Arrays.stream(CompressionAlgorithms.values())
          .map(CompressionAlgorithms::getCompressionAlgorithm)
          .map(algo -> new CompressionResult(algo, buffer, this.alreadyProcessed.toByteArray()))
          .sorted()
          .collect(toCollection(TreeSet::new));
      final CompressionResult compressionResult = algos.stream()
          .findFirst().orElseThrow(NoSuchElementException::new);

      if (compressionResult.getAlgorithm() instanceof CopyCompressAlgorithm) {
        // if best gain is COPY, then iterate up to COMMAND_LENGTH_MAX_NORMAL +1 segments forward to find a better algorithm.
        // this will help to determine the best size for the COPY.

        LOG.info("Choosing algorithm [{}] with [{}] bytes.", compressionResult.getAlgorithm(), COPY_MAX_READ);

        final byte[] processed = new byte[COPY_MAX_READ];
        final int read1 = this.inputStream.read(processed, 0, COPY_MAX_READ);
        final byte[] compressed = new CopyCompressAlgorithm().apply(processed, read1 - 1);
        this.alreadyProcessed.write(processed);
        this.getCompressed().write(compressed);
        continue;
      }

      // use algorithm
      LOG.info("Choosing algorithm [{}] with [{}] bytes.", compressionResult.getAlgorithm(), compressionResult.getCommandLength());
      final byte[] compressed = compressionResult.apply();
      final int consumed = compressionResult.getCommandLength() + 1;
      LOG.warn("compressed [{}] bytes into [{}] bytes using algo [{}].", consumed, compressed.length, compressionResult.getAlgorithm());
      final byte[] processed = new byte[consumed];
      this.inputStream.read(processed);
      this.alreadyProcessed.write(processed);

      this.output.write(compressed);
    }

    this.isCompressed = true;
  }

  private int gainRepeatByte() throws IOException {
    int numByteRepeating = 0;
    final int read = this.inputStream.read();

    while (this.inputStream.read() == read && numByteRepeating <= COMMAND_LENGTH_MAX_EXTENDED + 1) {
      numByteRepeating++;
    }

    this.inputStream.unread(numByteRepeating + 1);

    return numByteRepeating;
  }

  @Override
  public void close() throws IOException {
    this.inputStream.close();
  }

  public int getOriginalLength() throws IOException {
    ensureCompressed();

    return this.alreadyProcessed.size();
  }
}
