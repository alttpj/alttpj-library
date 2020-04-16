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

import java.util.Arrays;
import java.util.StringJoiner;

public class RingBuffer {

  private final byte[] elements;
  private int pos;
  private int readCount;

  public RingBuffer(final byte[] elements) {
    this.elements = elements;
  }

  public byte read() {
    return read(0);
  }

  private byte read(final int resetTo) {
    final byte element = this.elements[this.pos];
    this.pos++;
    if (this.pos >= this.elements.length) {
      this.pos = resetTo;
    }
    this.readCount++;
    return element;
  }

  public void reset() {
    this.pos = 0;
    this.readCount = 0;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "RingBuffer{", "}")
        .add("elements=" + Arrays.toString(this.elements))
        .add("pos=" + this.pos)
        .add("readCount=" + this.readCount)
        .toString();
  }

  public int size() {
    return this.elements.length;
  }

  public byte[] toArrayFrom(final int posInB, final int length) {
    final int oldpos = this.pos;
    final int oldReadCount = this.readCount;

    this.pos = posInB;
    this.readCount = 0;

    final byte[] out = new byte[length];

    for (int ii = 0; ii < length; ii++) {
      out[ii] = read(posInB);
    }

    this.pos = oldpos;
    this.readCount = oldReadCount;

    return out;
  }

}
