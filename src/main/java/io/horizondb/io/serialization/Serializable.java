/**
 * Copyright 2013 Benjamin Lerer
 * 
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
package io.horizondb.io.serialization;

import io.horizondb.io.ByteWriter;

import java.io.IOException;

/**
 * @author Benjamin
 * 
 */
public interface Serializable {

    /**
     * Returns the size in byte necessary to encode this object.
     * 
     * @return the size in byte necessary to encode this object.
     * @throws IOException if an I/O error occurs while computing the serialized size.
     */
    int computeSerializedSize() throws IOException;

    /**
     * Serialize this object and write it to the specified writer.
     * 
     * @param writer the writer to write to.
     * @throws IOException if an I/O problem occurs.
     */
    void writeTo(ByteWriter writer) throws IOException;
}
