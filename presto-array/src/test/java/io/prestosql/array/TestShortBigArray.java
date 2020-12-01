/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.array;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class TestShortBigArray
{
    @Test
    public void testFill()
    {
        ShortBigArray array = new ShortBigArray();
        assertFillCapacity(array, 0, (short) 1);
        assertFillCapacity(array, 1, (short) 2);
        assertFillCapacity(array, 1000, (short) 3);
        assertFillCapacity(array, BigArrays.SEGMENT_SIZE, (short) 4);
        assertFillCapacity(array, BigArrays.SEGMENT_SIZE + 1, (short) 5);
    }

    private static void assertFillCapacity(ShortBigArray array, long capacity, short value)
    {
        array.ensureCapacity(capacity);
        array.fill(value);

        for (int i = 0; i < capacity; i++) {
            assertEquals(array.get(i), value);
        }
    }

    @Test
    public void testCopyTo()
    {
        ShortBigArray source = new ShortBigArray();
        ShortBigArray destination = new ShortBigArray();

        for (long sourceIndex : Arrays.asList(0, 1, BigArrays.SEGMENT_SIZE, BigArrays.SEGMENT_SIZE + 1)) {
            for (long destinationIndex : Arrays.asList(0, 1, BigArrays.SEGMENT_SIZE, BigArrays.SEGMENT_SIZE + 1)) {
                for (long length : Arrays.asList(0, 1, BigArrays.SEGMENT_SIZE, BigArrays.SEGMENT_SIZE + 1)) {
                    assertCopyTo(source, sourceIndex, destination, destinationIndex, length);
                }
            }
        }
    }

    private static void assertCopyTo(ShortBigArray source, long sourceIndex, ShortBigArray destination, long destinationIndex, long length)
    {
        long sourceCapacity = sourceIndex + length;
        source.ensureCapacity(sourceCapacity);
        long destinationCapacity = destinationIndex + length + 1; // Add +1 to let us verify that copy does not go out of bounds
        destination.ensureCapacity(destinationCapacity);

        short destinationFillValue = -1;
        destination.fill(destinationFillValue);

        // Write indicies as values in source
        for (long i = 0; i < sourceCapacity; i++) {
            source.set(i, (short) i);
        }

        source.copyTo(sourceIndex, destination, destinationIndex, length);

        for (long i = 0; i < destinationIndex; i++) {
            assertEquals(destination.get(i), destinationFillValue);
        }
        for (long i = 0; i < length; i++) {
            assertEquals(source.get(sourceIndex + i), destination.get(destinationIndex + i));
        }
        for (long i = destinationIndex + length; i < destinationCapacity; i++) {
            assertEquals(destination.get(i), destinationFillValue);
        }
    }
}
