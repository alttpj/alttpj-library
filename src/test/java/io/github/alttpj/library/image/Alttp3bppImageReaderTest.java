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

package io.github.alttpj.library.image;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import org.junit.jupiter.api.Test;

class Alttp3bppImageReaderTest {

    @Test
    void testUnpack3bppTiles() throws IOException {
        final InputStream oneUpPackedUncompressedStream = this.getClass().getResourceAsStream("/gfx/u_1up.bin");
        final byte[] inputPacked = readAllBytes(oneUpPackedUncompressedStream);

        final byte[] unpacked = Alttp3bppImageReader.unpack3bppTiles(inputPacked);
        final byte[] onUpExpected = readAllBytes(this.getClass().getResourceAsStream("/gfx/u_1up.bin"));

        assertAll(
            () -> assertEquals(0x5ff + 1, unpacked.length),
            () -> assertArrayEquals(onUpExpected, unpacked)
        );
    }

    @Test
    public void testUncompress() throws IOException {
        final InputStream oneUpPackedCompressedStream = this.getClass().getResourceAsStream("/gfx/1up.bin");
        final ImageInputStream imageInputStream = ImageIO.createImageInputStream(oneUpPackedCompressedStream);

        // when
        final byte[] uncompressedPacked = Alttp3bppImageReader.readCompressedImage(imageInputStream);

        // then
        final byte[] expected = readAllBytes(this.getClass().getResourceAsStream("/gfx/u_1up.bin"));
        assertArrayEquals(expected, uncompressedPacked);

    }

    private byte[] readAllBytes(final InputStream oneUpStream) throws IOException {
        final byte[] inputCompressed;

        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[512];
            int readCount;
            while ((readCount = oneUpStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, readCount);
            }
            inputCompressed = byteArrayOutputStream.toByteArray();
        }
        return inputCompressed;
    }
}
