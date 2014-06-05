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
package io.horizondb.io.buffers;

import io.horizondb.io.Buffer;
import io.horizondb.io.BufferAllocator;

/**
 * <code>BufferAllocator</code> that simply allocate on demand.
 * 
 * @author Benjamin
 *
 */
final class DefaultBufferAllocator implements BufferAllocator {

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer allocate(int size) {
        return new HeapBuffer(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {

    }
}
