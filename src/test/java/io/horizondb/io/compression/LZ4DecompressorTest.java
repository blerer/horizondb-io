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

import java.io.IOException;

import io.horizondb.io.Buffer;
import io.horizondb.io.ReadableBuffer;
import io.horizondb.io.buffers.Buffers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Benjamin
 *
 */
public class LZ4DecompressorTest {
    
    @Test
    public void testDecompressionWithNonHeapBuffer() throws IOException {
        
        byte[] array = "12345345234572".getBytes("UTF-8");
        
        int decompressedLength = array.length;
        
        Buffer directBuffer = Buffers.allocateDirect(30);
        directBuffer.writeBytes(array);
        
        LZ4Compressor compressor = new LZ4Compressor();
        
        ReadableBuffer compressedData = compressor.compress(directBuffer);
        
        LZ4Decompressor decompressor = new LZ4Decompressor();
        
        directBuffer = Buffers.allocateDirect(compressedData.readableBytes());
        directBuffer.transfer(compressedData);
        
        ReadableBuffer uncompressedData = decompressor.decompress(directBuffer, decompressedLength);

        assertArrayEquals(array, ((Buffer) uncompressedData).array());
    }
}
