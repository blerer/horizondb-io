/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.horizondb.io.compression;

import io.horizondb.io.Buffer;
import io.horizondb.io.ReadableBuffer;
import io.horizondb.io.buffers.Buffers;

import java.io.IOException;

import net.jpountz.lz4.LZ4Factory;

/**
 * <code>Compressor</code> compressing using the LZ4 compression. 
 * 
 * @author Benjamin
 *
 */
final class LZ4Compressor implements Compressor {
    
    /**
     * The current maximum size supported for the input.
     */
    private int maxInputSize;
    
    /**
     * The buffer used to compress the data.
     */
    private Buffer buffer = Buffers.EMPTY_BUFFER;
    
    /**
     * The compressor.
     */
    private final net.jpountz.lz4.LZ4Compressor compressor;
    
    /**
     * Creates a new <code>LZ4Compressor</code> instance.
     */
    public LZ4Compressor() {
        
        LZ4Factory factory = LZ4Factory.fastestInstance();
        this.compressor = factory.fastCompressor();
    }
    
    /**    
     * {@inheritDoc}
     */
    @Override
    public CompressionType getType() {
        return CompressionType.LZ4;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ReadableBuffer compress(ReadableBuffer in) throws IOException {

        adjustBufferSizeIfNeeded(in);

        this.buffer.clear();

        if (!in.isReadable()) {
            return this.buffer;
        }

        if (in instanceof Buffer && ((Buffer) in).hasArray()) {

            Buffer inputBuffer = (Buffer) in;
            compress(inputBuffer.array(), inputBuffer.arrayOffset(), inputBuffer.readableBytes());

        } else {

            byte[] inputArray = new byte[in.readableBytes()];
            in.readBytes(inputArray);

            compress(inputArray, 0, inputArray.length);
        }

        return this.buffer;
    }

    /**
     * Adjust the size of the internal buffer if needed to handle the specified input.
     *   
     * @param in the input buffer 
     */
    private void adjustBufferSizeIfNeeded(ReadableBuffer in) {
        
        if (in.readableBytes() > this.maxInputSize) {

            this.maxInputSize = in.readableBytes();
            int maxOutputSize = this.compressor.maxCompressedLength(this.maxInputSize);
            this.buffer = Buffers.allocate(maxOutputSize);
        }
    }

    /**
     * Compress the specified bytes.
     * 
     * @param inputArray the input bytes
     * @param offset the bytes offset
     * @param length the amount of bytes that need to be uncompressed
     */
    private void compress(byte[] inputArray, int offset, int length) {

        int compressedLength = this.compressor.compress(inputArray, offset, length, this.buffer.array(), this.buffer.arrayOffset(), this.buffer.capacity());
        this.buffer.writerIndex(compressedLength);
    }

}
