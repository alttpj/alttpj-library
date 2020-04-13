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

package io.github.alttpj.library.image;

import io.github.alttpj.library.i18n.I18N;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class Alttp3bppImageReader extends ImageReader {

    private static final int BUFFER_SIZE = 512;

    private static final int BYTES_PER_TILE_UNPACKED = 64;

    private static final int BYTES_PER_TILE_3BPP = 24;

    private static final int[] ROW_INDICES_3BPP = new int[]{
        0, 0, 1, 1, 2, 2, 3, 3,
        4, 4, 5, 5, 6, 6, 7, 7,
        0, 1, 2, 3, 4, 5, 6, 7
    };

    private static final int[] BITPLANE_INDICES_3BPP = new int[]{
        0, 1, 0, 1, 0, 1, 0, 1,
        0, 1, 0, 1, 0, 1, 0, 1,
        2, 2, 2, 2, 2, 2, 2, 2
    };

    /**
     * The input stream where reads from
     */
    private ImageInputStream iis = null;

    /**
     * The destination image.
     */
    private BufferedImage bi;

    /**
     * Constructs an {@code ImageReader} and sets its
     * {@code originatingProvider} field to the supplied value.
     *
     * <p> Subclasses that make use of extensions should provide a
     * constructor with signature {@code (ImageReaderSpi,Object)}
     * in order to retrieve the extension object.  If
     * the extension object is unsuitable, an
     * {@code IllegalArgumentException} should be thrown.
     *
     * @param originatingProvider
     *     the {@code ImageReaderSpi} that is
     *     invoking this constructor, or {@code null}.
     */
    protected Alttp3bppImageReader(final ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    protected static void unpackRowOfBitplaneOfTile(final byte[] input,
                                                    final int inputOffset,
                                                    final byte[] output,
                                                    final int outputOffset,
                                                    final int bitplaneIndex) {
        for (int pixelIndex = 0; pixelIndex < 8; pixelIndex++) {
            final int BitIndex = 7 - pixelIndex;
            final int bit = (input[inputOffset] >> BitIndex) & 1;
            output[outputOffset + pixelIndex] |= (byte) (bit << bitplaneIndex);
        }
    }

    protected static void unpackTile(final byte[] input,
                                     final int inputOffset,
                                     final byte[] output,
                                     final int outputOffset,
                                     final int[] rowIndices,
                                     final int[] bitplaneIndices) {
        for (int index = 0; index < rowIndices.length; index++) {
            unpackRowOfBitplaneOfTile(input, inputOffset + index, output, outputOffset + rowIndices[index] * 8, bitplaneIndices[index]);
        }
    }

    protected static void unpackTiles(final byte[] input,
                                      final int inputOffset,
                                      final byte[] output,
                                      final int outputOffset,
                                      final int tileCount,
                                      final int bytesPerTile,
                                      final int[] rowIndices,
                                      final int[] bitplaneIndices) {
        for (int index = 0; index < tileCount; index++) {
            unpackTile(input,
                inputOffset + bytesPerTile * index,
                output,
                outputOffset + BYTES_PER_TILE_UNPACKED * index,
                rowIndices,
                bitplaneIndices);
        }
    }

    public static void unpack3bppTiles(final byte[] input,
                                       final int inputOffset,
                                       final byte[] output,
                                       final int outputOffset,
                                       final int tileCount) {
        unpackTiles(input, inputOffset, output, outputOffset, tileCount, BYTES_PER_TILE_3BPP, ROW_INDICES_3BPP, BITPLANE_INDICES_3BPP);
    }

    protected static byte[] unpack3bppTiles(final byte[] input, final int tileCount) {
        final int totalSize = tileCount * BYTES_PER_TILE_UNPACKED;
        final byte[] buffer = new byte[totalSize];
        unpack3bppTiles(input, 0, buffer, 0, tileCount);

        return buffer;
    }

    public static byte[] unpack3bppTiles(final byte[] inputCompressed) {
        final int tileCount = inputCompressed.length / BYTES_PER_TILE_3BPP;

        return unpack3bppTiles(inputCompressed, tileCount);
    }

    /**
     * Overrides the method defined in the superclass.
     */
    @Override
    public void setInput(final Object input,
                         final boolean seekForwardOnly,
                         final boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        // Always works, since we defined only ImageInputStream allowed.
        this.iis = (ImageInputStream) input;

        if (this.iis != null) {
            this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }
    }

    private void checkIndex(final int imageIndex) {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("Alttp3bppImageReader0"));
        }
    }

    @Override
    public int getNumImages(final boolean allowSearch) throws IOException {
        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("InputStreamNull"));
        }

        if (this.seekForwardOnly && allowSearch) {
            throw new IllegalStateException(I18N.getString("SeekForwardAndAllowSearch"));
        }

        return 1;
    }

    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkIndex(imageIndex);

        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("InputStreamNull"));
        }

        return 8;
    }

    // implementation

    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkIndex(imageIndex);

        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("InputStreamNull"));
        }

        return 8;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        return null;
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    @Override
    public IIOMetadata getImageMetadata(final int imageIndex) throws IOException {
        return null;
    }

    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        checkIndex(imageIndex);

        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("InputStreamNull"));
        }

        clearAbortRequest();
        processImageStarted(imageIndex);
        if (abortRequested()) {
            processReadAborted();
            return this.bi;
        }

        // unpack
        final byte[] readInput = readCompressedImage();

        final byte[] uncompressedOut = unpack3bppTiles(readInput, 1);

        final int height = getHeight(imageIndex);
        final int width = getWidth(imageIndex);
        this.bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            if (abortRequested()) {
                break;
            }
        }

        return this.bi;
    }

    private byte[] readCompressedImage() throws IOException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int numRead;

            while ((numRead = this.iis.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, numRead);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

}
