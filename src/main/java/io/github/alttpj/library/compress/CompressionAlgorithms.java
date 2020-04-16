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

import io.github.alttpj.library.compress.impl.CopyCompressAlgorithm;
import io.github.alttpj.library.compress.impl.CopyExistingCompressionAlgorithm;
import io.github.alttpj.library.compress.impl.IncrementByteCompressionAlgorithm;
import io.github.alttpj.library.compress.impl.RepeatByteCompressionAlgorithm;
import io.github.alttpj.library.compress.impl.RepeatWordCompressionAlgorithm;

import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;

public enum CompressionAlgorithms {
  READ_AS_IS(CopyCompressAlgorithm.class),
  REPEAT_BYTE(RepeatByteCompressionAlgorithm.class),
  REPEAT_WORD(RepeatWordCompressionAlgorithm.class),
  INCREMENT_BYTE(IncrementByteCompressionAlgorithm.class),
  COPY_EXISTING(CopyExistingCompressionAlgorithm.class);

  private final Class<? extends CompressionAlgorithm> compressionAlgorithm;

  CompressionAlgorithms(final Class<? extends CompressionAlgorithm> compressionAlgorithm) {
    this.compressionAlgorithm = compressionAlgorithm;
  }

  public CompressionAlgorithm getCompressionAlgorithm() {
    try {
      return this.compressionAlgorithm.getConstructor().newInstance();
    } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException constructEx) {
      throw new IllegalArgumentException("Cannot construct instance of [" + this.compressionAlgorithm + "]", constructEx);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "CompressionAlgorithms{", "}")
        .add("compressionAlgorithm=" + this.compressionAlgorithm)
        .toString();
  }
}
