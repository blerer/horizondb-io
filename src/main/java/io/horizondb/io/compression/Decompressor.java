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

import io.horizondb.io.ReadableBuffer;

import java.io.IOException;

/**
 * Utility to uncompress previously compressed data.
 * 
 * @author Benjamin
 */
public interface Decompressor {

    /**
     * Returns the type of compression that this decompressor can uncompress. 
     * 
     * @return the type of compression that this decompressor can uncompress. 
     */
    CompressionType getType();
    
    /**
     * Uncompress the specified data.
     * 
     * @param in the data to uncompress
     * @param decompressedLength the length of the uncompressed data
     * @return the uncompressed data
     * @throws IOException if an I/O problem occurs
     */
    ReadableBuffer decompress(ReadableBuffer in, int decompressedLength) throws IOException;
}