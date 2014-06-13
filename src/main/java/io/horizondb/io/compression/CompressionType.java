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
package io.horizondb.io.compression;

import io.horizondb.io.ByteReader;
import io.horizondb.io.ByteWriter;
import io.horizondb.io.serialization.Parser;
import io.horizondb.io.serialization.Serializable;

import java.io.IOException;

/**
 * @author Benjamin
 * 
 */
public enum CompressionType implements Serializable {

    NONE(0){
                
        /**
         * The decompressor instance.
         */
        private final NoopDecompressor decompressor = new NoopDecompressor();

        /**
         * The compressor instance.
         */
        private final NoopCompressor compressor = new NoopCompressor();

        /**
         * {@inheritDoc}
         */
        @Override
        public Compressor newCompressor() {
            return this.compressor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Decompressor newDecompressor() {
            return this.decompressor;
        }
    },
    LZ4(1){

        /**
         * {@inheritDoc}
         */
        @Override
        public Compressor newCompressor() {
            return new LZ4Compressor();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Decompressor newDecompressor() {
            return new LZ4Decompressor();
        }
    };

    /**
     * The parser instance.
     */
    private static final Parser<CompressionType> PARSER = new Parser<CompressionType>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public CompressionType parseFrom(ByteReader reader) throws IOException {

            return CompressionType.toCompressionType(reader.readByte());
        }
    };
    
    /**
     * The field type binary representation.
     */
    private final int b;

    /**
     * Creates a new <code>FieldType</code> with the specified binary representation.
     * 
     * @param b the byte representing the <code>FieldType</code>.
     */
    private CompressionType(int b) {

        this.b = b;
    }

    /**
     * Creates a new <code>CompressionType</code> by reading the data from the specified reader.
     * 
     * @param reader the reader to read from.
     * @throws IOException if an I/O problem occurs
     */
    public static CompressionType parseFrom(ByteReader reader) throws IOException {

        return getParser().parseFrom(reader);
    }

    /**
     * Returns the parser that can be used to deserialize <code>CompressionType</code> instances.
     * @return the parser that can be used to deserialize <code>CompressionType</code> instances.
     */
    public static Parser<CompressionType> getParser() {

        return PARSER;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int computeSerializedSize() {

        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(ByteWriter writer) throws IOException {

        writer.writeByte(this.b);
    }
    
    /**
     * Creates a new <code>Compressor</code> instance.
     * 
     * @return a new <code>Compressor</code> instance
     */
    public abstract Compressor newCompressor();
    
    /**
     * Creates a new <code>Decompressor</code> instance.
     * 
     * @return a new <code>Decompressor</code> instance
     */
    public abstract Decompressor newDecompressor();


    /**
     * The binary representation of this compression type. 
     * 
     * @return the binary representation of this compression type
     */
    public int toByte() {
        return this.b;
    }

    /**
     * Returns the compression type represented by the next readable byte in the specified buffer.
     * 
     * @param int the binary representation of the compression type
     * @return the compression type represented by the next readable byte in the specified buffer.
     */
    public static CompressionType toCompressionType(int b) {

        CompressionType[] values = CompressionType.values();

        for (int i = 0; i < values.length; i++) {

            CompressionType compressionType = values[i];

            if (compressionType.b == b) {

                return compressionType;
            }
        }

        throw new IllegalStateException("The byte " + b + " does not match any compression type");
    }
}
