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

import io.horizondb.io.ReadableBuffer;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CompositeBufferTest {

    @Test
    public void testAdd() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));

        assertEquals(5, buffer.readableBytes());
        assertEquals(0, buffer.readerIndex());

        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));

        assertEquals(8, buffer.readableBytes());
        assertEquals(0, buffer.readerIndex());

        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(10, buffer.readableBytes());
        assertEquals(0, buffer.readerIndex());
    }

    @Test
    public void testDuplicate() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        CompositeBuffer duplicate = buffer.duplicate();

        assertEquals(10, duplicate.readableBytes());
        assertEquals(0, duplicate.readerIndex());
    }

    @Test
    public void testDuplicateSlice() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        CompositeBuffer slice = buffer.readerIndex(3).slice(2);

        CompositeBuffer duplicate = slice.duplicate();

        assertEquals(2, duplicate.readableBytes());
        assertEquals(0, duplicate.readerIndex());
        assertEquals(0, duplicate.readByte());
        assertEquals(0, duplicate.readByte());
    }

    @Test
    public void testRead() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());

        assertEquals(-120, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(2, buffer.readerIndex());
        assertEquals(8, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(3, buffer.readerIndex());
        assertEquals(7, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(4, buffer.readerIndex());
        assertEquals(6, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(5, buffer.readerIndex());
        assertEquals(5, buffer.readableBytes());

        assertEquals(4, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(6, buffer.readerIndex());
        assertEquals(4, buffer.readableBytes());

        assertEquals(5, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(7, buffer.readerIndex());
        assertEquals(3, buffer.readableBytes());

        assertEquals(6, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(8, buffer.readerIndex());
        assertEquals(2, buffer.readableBytes());

        assertEquals(7, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(9, buffer.readerIndex());
        assertEquals(1, buffer.readableBytes());

        assertEquals(6, buffer.readByte());
        assertFalse(buffer.isReadable());
        assertEquals(10, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void testCompositeOfComposite() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));

        buffer.readerIndex(5);

        CompositeBuffer composite = new CompositeBuffer();
        composite.addBytes(buffer);

        assertEquals(4, composite.readByte());

    }

    @Test
    public void testReaderIndexChange() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());

        assertEquals(-120, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(2, buffer.readerIndex());
        assertEquals(8, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(3, buffer.readerIndex());
        assertEquals(7, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(4, buffer.readerIndex());
        assertEquals(6, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(5, buffer.readerIndex());
        assertEquals(5, buffer.readableBytes());

        buffer.readerIndex(2);

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(3, buffer.readerIndex());
        assertEquals(7, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(4, buffer.readerIndex());
        assertEquals(6, buffer.readableBytes());

        buffer.readerIndex(9);

        assertEquals(6, buffer.readByte());
        assertFalse(buffer.isReadable());
        assertEquals(10, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void testSkip() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        buffer.skipBytes(1);
        assertTrue(buffer.isReadable());
        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());

        buffer.skipBytes(4);
        assertTrue(buffer.isReadable());
        assertEquals(5, buffer.readerIndex());
        assertEquals(5, buffer.readableBytes());

        assertEquals(4, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(6, buffer.readerIndex());
        assertEquals(4, buffer.readableBytes());

        buffer.skipBytes(3);
        assertTrue(buffer.isReadable());
        assertEquals(9, buffer.readerIndex());
        assertEquals(1, buffer.readableBytes());

        assertEquals(6, buffer.readByte());
        assertFalse(buffer.isReadable());
        assertEquals(10, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void testSkipOverMultipleBuffers() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        buffer.skipBytes(9);
        assertEquals(9, buffer.readerIndex());
        assertEquals(1, buffer.readableBytes());

        assertEquals(6, buffer.readByte());
        assertFalse(buffer.isReadable());
        assertEquals(10, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void testReadBytes() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        byte[] bytes = new byte[4];

        buffer.readBytes(bytes, 0, 4);

        assertArrayEquals(new byte[] { 2, -120, 0, 0 }, bytes);
        assertTrue(buffer.isReadable());
        assertEquals(4, buffer.readerIndex());
        assertEquals(6, buffer.readableBytes());

        buffer.readBytes(bytes);

        assertArrayEquals(new byte[] { 0, 4, 5, 6 }, bytes);
        assertTrue(buffer.isReadable());
        assertEquals(8, buffer.readerIndex());
        assertEquals(2, buffer.readableBytes());

        buffer.readBytes(bytes, 1, 2);

        assertArrayEquals(new byte[] { 0, 7, 6, 6 }, bytes);
        assertFalse(buffer.isReadable());
        assertEquals(10, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void testReadBytesWithMultipleBufferReadAtOnce() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        byte[] bytes = new byte[10];

        buffer.readBytes(bytes);

        assertArrayEquals(new byte[] { 2, -120, 0, 0, 0, 4, 5, 6, 7, 6 }, bytes);
        assertEquals(10, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void testGet() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.getByte(0));
        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.getByte(1));
        assertEquals(0, buffer.getByte(2));
        assertEquals(0, buffer.getByte(3));
        assertEquals(0, buffer.getByte(4));
        assertEquals(4, buffer.getByte(5));
        assertEquals(5, buffer.getByte(6));
        assertEquals(6, buffer.getByte(7));
        assertEquals(7, buffer.getByte(8));
        assertEquals(6, buffer.getByte(9));
        assertEquals(4, buffer.getByte(5));

        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());
    }

    @Test
    public void testGetBytes() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        byte[] bytes = new byte[4];

        buffer.getBytes(0, bytes, 0, 4);
        assertArrayEquals(new byte[] { 2, -120, 0, 0 }, bytes);

        buffer.getBytes(4, bytes, 0, 4);
        assertArrayEquals(new byte[] { 0, 4, 5, 6 }, bytes);

        buffer.getBytes(8, bytes, 2, 2);
        assertArrayEquals(new byte[] { 0, 4, 7, 6 }, bytes);

        bytes = new byte[10];
        buffer.getBytes(0, bytes, 0, 10);
        assertArrayEquals(new byte[] { 2, -120, 0, 0, 0, 4, 5, 6, 7, 6 }, bytes);
    }

    @Test
    public void testSlice() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());

        ReadableBuffer slice = buffer.slice(8);

        assertTrue(buffer.isReadable());
        assertEquals(9, buffer.readerIndex());
        assertEquals(1, buffer.readableBytes());

        assertTrue(slice.isReadable());
        assertEquals(0, slice.readerIndex());
        assertEquals(8, slice.readableBytes());

        assertEquals(-120, slice.readByte());
        assertTrue(slice.isReadable());
        assertEquals(1, slice.readerIndex());
        assertEquals(7, slice.readableBytes());

        assertEquals(0, slice.readByte());
        assertTrue(slice.isReadable());
        assertEquals(2, slice.readerIndex());
        assertEquals(6, slice.readableBytes());

        slice.skipBytes(3);
        assertTrue(slice.isReadable());
        assertEquals(5, slice.readerIndex());
        assertEquals(3, slice.readableBytes());

        assertEquals(5, slice.readByte());
        assertTrue(slice.isReadable());
        assertEquals(6, slice.readerIndex());
        assertEquals(2, slice.readableBytes());

        byte[] bytes = new byte[4];
        slice.readBytes(bytes, 2, 2);

        assertArrayEquals(new byte[] { 0, 0, 6, 7 }, bytes);
        assertFalse(slice.isReadable());
        assertEquals(8, slice.readerIndex());
        assertEquals(0, slice.readableBytes());

        try {
            slice.readByte();
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSliceWithReaderIndexChange() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());

        ReadableBuffer slice = buffer.slice(8);

        assertTrue(buffer.isReadable());
        assertEquals(9, buffer.readerIndex());
        assertEquals(1, buffer.readableBytes());

        assertTrue(slice.isReadable());
        assertEquals(0, slice.readerIndex());
        assertEquals(8, slice.readableBytes());

        assertEquals(-120, slice.readByte());
        assertTrue(slice.isReadable());
        assertEquals(1, slice.readerIndex());
        assertEquals(7, slice.readableBytes());

        assertEquals(0, slice.readByte());
        assertTrue(slice.isReadable());
        assertEquals(2, slice.readerIndex());
        assertEquals(6, slice.readableBytes());

        slice.skipBytes(3);
        assertTrue(slice.isReadable());
        assertEquals(5, slice.readerIndex());
        assertEquals(3, slice.readableBytes());

        slice.readerIndex(1);

        assertEquals(0, slice.readByte());
        assertTrue(slice.isReadable());
        assertEquals(2, slice.readerIndex());
        assertEquals(6, slice.readableBytes());

        slice.readerIndex(6);

        byte[] bytes = new byte[4];
        slice.readBytes(bytes, 2, 2);

        assertArrayEquals(new byte[] { 0, 0, 6, 7 }, bytes);
        assertFalse(slice.isReadable());
        assertEquals(8, slice.readerIndex());
        assertEquals(0, slice.readableBytes());

        try {
            slice.readByte();
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSliceWithGet() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertTrue(buffer.isReadable());
        assertEquals(1, buffer.readerIndex());
        assertEquals(9, buffer.readableBytes());

        ReadableBuffer slice = buffer.slice(8);

        assertEquals(-120, slice.getByte(0));
        assertEquals(0, slice.getByte(1));
        assertEquals(0, slice.getByte(2));

        byte[] bytes = new byte[4];

        slice.getBytes(0, bytes, 0, 4);
        assertArrayEquals(new byte[] { -120, 0, 0, 0 }, bytes);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSliceWithLenghtGreaterThanNumberOfReadableBytes() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));

        buffer.readByte();
        buffer.slice(6);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveBytesWithNoData() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();
        buffer.removeBytes(0, 5);
    }

    @Test
    public void testRemoveBytesFirstEntireBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.removeBytes(0, 5);

        assertEquals(0, buffer.readerIndex());
        assertEquals(5, buffer.readableBytes());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesFirstEntireBufferWithIndexReaderWithinRemovedBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        buffer.removeBytes(0, 5);

        assertEquals(0, buffer.readerIndex());
        assertEquals(5, buffer.readableBytes());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesFirstEntireBufferWithIndexReaderAfterRemovedBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.readerIndex(6);
        assertEquals(5, buffer.readByte());
        buffer.removeBytes(0, 5);

        assertEquals(6, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesLastEntireBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.removeBytes(8, 2);

        assertEquals(8, buffer.readableBytes());
        buffer.readerIndex(5);
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertFalse(buffer.isReadable());
    }

    @Test
    public void testRemoveBytesFromTheStartOfTheFirstBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.removeBytes(0, 3);

        assertEquals(0, buffer.readerIndex());
        assertEquals(7, buffer.readableBytes());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesFromTheEndOfTheFirstBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.removeBytes(2, 3);

        assertEquals(0, buffer.readerIndex());
        assertEquals(7, buffer.readableBytes());
        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesFromTheMiddleOfTheFirstBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.removeBytes(1, 3);

        assertEquals(0, buffer.readerIndex());
        assertEquals(7, buffer.readableBytes());
        assertEquals(2, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesAcrossSeveralBuffers() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.readerIndex(4);
        buffer.removeBytes(2, 7);

        assertEquals(2, buffer.readerIndex());
        assertEquals(1, buffer.readableBytes());
        assertEquals(6, buffer.readByte());

        buffer.readerIndex(0);
        assertEquals(0, buffer.readerIndex());
        assertEquals(3, buffer.readableBytes());
        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testRemoveBytesAtTheEndAcrossSeveralBuffers() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 7, 6 }));

        buffer.readerIndex(4);
        buffer.removeBytes(2, 8);

        assertEquals(2, buffer.readerIndex());
        assertEquals(0, buffer.readableBytes());

        buffer.readerIndex(0);
        assertEquals(0, buffer.readerIndex());
        assertEquals(2, buffer.readableBytes());
        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
    }

    @Test
    public void testAddByteWithPositionToEmptyBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(0, new HeapBuffer(new byte[] { 2, -120}));
        assertEquals(0, buffer.readerIndex());
        assertEquals(2, buffer.readableBytes());
        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddByteWithPositionOverCapacity() throws IOException {

        CompositeBuffer buffer = new CompositeBuffer();
        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120}));
        buffer.addBytes(5, new HeapBuffer(new byte[] { 12, 10}));
    }

    @Test
    public void testAddByteWithPositionAtTheBegining() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(0, new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
    }

    @Test
    public void testAddByteWithPositionAtTheBeginingOfABuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(5, new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }
    
    @Test
    public void testAddByteWithPositionAtTheBeginingOfABufferAndIndexReaderAfterInsertionPoint() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));

        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(4, buffer.readByte());

        buffer.addBytes(5, new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(8, buffer.readerIndex());
        assertEquals(2, buffer.readableBytes());

        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testAddByteWithPositionInTheMiddleOfABuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(2, new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testAddByteWithPositionInTheMiddleOfABufferAndIndexReaderAfterInsertionPoint() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));

        buffer.readerIndex(4);

        buffer.addBytes(2, new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(6, buffer.readerIndex());
        assertEquals(4, buffer.readableBytes());

        assertEquals(0, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }

    @Test
    public void testAddByteWithPositionInTheMiddleOfTheLastBuffer() throws IOException {
        CompositeBuffer buffer = new CompositeBuffer();

        buffer.addBytes(new HeapBuffer(new byte[] { 2, -120, 0, 0, 0 }));
        buffer.addBytes(new HeapBuffer(new byte[] { 4, 5, 6 }));
        buffer.addBytes(6, new HeapBuffer(new byte[] { 7, 6 }));

        assertEquals(0, buffer.readerIndex());
        assertEquals(10, buffer.readableBytes());

        assertEquals(2, buffer.readByte());
        assertEquals(-120, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(0, buffer.readByte());
        assertEquals(4, buffer.readByte());
        assertEquals(7, buffer.readByte());
        assertEquals(6, buffer.readByte());
        assertEquals(5, buffer.readByte());
        assertEquals(6, buffer.readByte());
    }
}
