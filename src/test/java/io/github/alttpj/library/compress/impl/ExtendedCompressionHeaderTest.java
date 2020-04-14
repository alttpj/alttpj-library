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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ExtendedCompressionHeaderTest {

  public static Stream<Arguments> providersLengthsResults() {
    return Stream.of(
        // algo, length, output bytes incl. null padding byte
        arguments(CopyCompressAlgorithm.class, 0, new byte[]{(byte) 0xE0, 0, 0}),
        arguments(RepeatByteCompressionAlgorithm.class, 0, new byte[]{(byte) 0xE4, 0, 0}),
        arguments(RepeatWordCompressionAlgorithm.class, 0, new byte[]{(byte) 0xE8, 0, 0}),
        arguments(IncrementByteCompressionAlgorithm.class, 0, new byte[]{(byte) 0xEC, 0, 0}),
        arguments(CopyExistingCompressionAlgorithm.class, 0, new byte[]{(byte) 0xF0, 0, 0}),

        // test max length
        arguments(CopyCompressAlgorithm.class, COMMAND_LENGTH_MAX_EXTENDED, new byte[]{(byte) 0b11100011, (byte) 0b11111111, 0}),
        arguments(CopyExistingCompressionAlgorithm.class, COMMAND_LENGTH_MAX_EXTENDED, new byte[]{(byte) 0b11110011, (byte) 0b11111111, 0})
    );
  }

  @ParameterizedTest(name = "[{index}] {arguments}")
  @MethodSource({"providersLengthsResults"})
  public void testWriteExtendedCompressionHeader(final Class<? extends AbstractCompressionAlgorithm> algo,
                                                 final int commandLength,
                                                 final byte[] expededHeader) throws Exception {
    final AbstractCompressionAlgorithm compressionAlgorithm = algo.getConstructor().newInstance();

    final byte[] buffer = new byte[expededHeader.length];
    compressionAlgorithm.writeExtendedHeader(buffer, commandLength);

    Assertions.assertArrayEquals(expededHeader, buffer, "expected header to match");
  }

  @Test
  public void testInvalidInput_smallBuffer() {
    final CopyCompressAlgorithm algo = new CopyCompressAlgorithm();
    assertThrows(
        IllegalArgumentException.class,
        () -> algo.writeExtendedHeader(new byte[2], 0));
  }

  @Test
  public void testInvalidInput_commandTooLarge() {
    final CopyCompressAlgorithm algo = new CopyCompressAlgorithm();
    assertThrows(
        IllegalArgumentException.class,
        () -> algo.writeExtendedHeader(new byte[3], COMMAND_LENGTH_MAX_EXTENDED + 1));
  }
}
