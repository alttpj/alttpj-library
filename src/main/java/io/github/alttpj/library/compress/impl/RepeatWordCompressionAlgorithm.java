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

import io.github.alttpj.library.compress.CompressionAlgorithm;
import java.util.StringJoiner;

public class RepeatWordCompressionAlgorithm extends AbstractCompressionAlgorithm implements CompressionAlgorithm {

  public RepeatWordCompressionAlgorithm() {
    super(2);
  }

  @Override
  public int brute(final byte[] input, final byte[] alreadyProcessedUncompressed) {
    return 0;
  }

  @Override
  public byte[] apply(final byte[] input, final int commandLength) {
    return new byte[0];
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "RepeatWordCompressionAlgorithm{", "}")
        .add("super=" + super.toString())
        .toString();
  }
}
