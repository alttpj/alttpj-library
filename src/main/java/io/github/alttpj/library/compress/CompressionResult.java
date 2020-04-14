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

import java.util.Comparator;
import java.util.StringJoiner;

public class CompressionResult implements Comparable<CompressionResult> {

  private final byte[] input;

  private final CompressionAlgorithm algorithm;

  private final int commandLength;

  private final byte[] alreadyProcessed;

  public CompressionResult(final CompressionAlgorithm algorithm, final byte[] input, final byte[] alreadyProcessed) {
    this.algorithm = algorithm;
    this.input = input;
    this.alreadyProcessed = alreadyProcessed;

    this.commandLength = algorithm.brute(input, alreadyProcessed);
  }

  public byte[] apply() {
    return this.algorithm.apply(this.input, this.commandLength);
  }

  public byte[] getInput() {
    return this.input;
  }

  public CompressionAlgorithm getAlgorithm() {
    return this.algorithm;
  }

  public int getCommandLength() {
    return this.commandLength;
  }

  public byte[] getAlreadyProcessed() {
    return this.alreadyProcessed;
  }

  @Override
  public int compareTo(final CompressionResult other) {
    // higher compressions (i.e. longer lengths) first
    return Comparator.comparing(CompressionResult::getCommandLength, Comparator.reverseOrder())
        // then sort by algo number ascending, because we can just use copy on equal results
        .thenComparing(res -> res.getAlgorithm().getCommandNum())
        .compare(this, other);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "CompressionResult{", "}")
        .add("input=" + this.input.length)
        .add("algorithm=" + this.algorithm)
        .add("commandLength=" + this.commandLength)
        .add("alreadyProcessed=" + this.alreadyProcessed.length)
        .toString();
  }
}
