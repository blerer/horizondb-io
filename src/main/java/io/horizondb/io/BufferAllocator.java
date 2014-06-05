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
package io.horizondb.io;


/**
 * Buffer allocator.
 * 
 * @author Benjamin
 *
 */
public interface BufferAllocator {

    /**
     * Retrieves a slice of memory of the specified size.
     * 
     * @param size the size of the buffer that need to be returned.
     * @return a buffer of the specified size.
     */
    Buffer allocate(int size);

    /**
     * Release the resources used by this allocator.
     */
    void release();

}