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
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * <code>Decompressor</code> that uncompress LZ4 encoded blocks of data.
 * 
 * @author Benjamin
 *
 */
final class LZ4Decompressor implements Decompressor {
    
    /**
     * The buffer used internally to uncompress data.
     */
    private Buffer buffer;
    
    /**
     * The maximum output size supported by the internal buffer.
     */
    private int maxOutputSize;
    
    /**
     * The LZ4 decompressor.
     */
    private final LZ4FastDecompressor decompressor;
    
    /**
     * Creates a new <code>LZ4Decompressor</code> instance. 
     */
    public LZ4Decompressor() {
        
        LZ4Factory factory = LZ4Factory.fastestInstance();
        this.decompressor = factory.fastDecompressor();
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
    public ReadableBuffer decompress(ReadableBuffer in, int decompressedLength) throws IOException {
        
        adjustBufferSizeIfNeeded(decompressedLength);
        
        this.buffer.clear();
        
        if (!in.isReadable()) {
            return this.buffer;
        }
        
        if (in instanceof Buffer && ((Buffer) in).hasArray()) {
            
            Buffer inputBuffer = (Buffer) in;
            decompress(inputBuffer.array(), inputBuffer.arrayOffset(), decompressedLength);
            
        } else {
            
            byte[] inputArray = new byte[in.readableBytes()];
            in.readBytes(inputArray);
            
            decompress(inputArray, 0, decompressedLength);
        }
        
        return this.buffer;
    }

    /**
     * Uncompress the specified bytes.
     * 
     * @param inputArray the array containing the bytes to uncompress
     * @param offset the offset within the array
     * @param decompressedLength the uncompressed length of the data
     */
    private void decompress(byte[] inputArray, int offset, int decompressedLength) {
        
        this.decompressor.decompress(inputArray,
                                     offset,
                                     this.buffer.array(),
                                     this.buffer.arrayOffset(),
                                     decompressedLength);

        this.buffer.writerIndex(decompressedLength);
    }
    
    /**
     * Adjusts the size of the internal buffer if needed.
     * 
     * @param decompressedLength the capacity required for the internal buffer.
     */
    private void adjustBufferSizeIfNeeded(int decompressedLength) {
        
        if (decompressedLength > this.maxOutputSize) {

            this.maxOutputSize = decompressedLength;
            this.buffer = Buffers.allocate(this.maxOutputSize);
        }
    }

}
