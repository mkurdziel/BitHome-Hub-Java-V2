package org.bithome.test.core.helpers;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.google.common.primitives.UnsignedLongs;
import org.bithome.api.protocol.DataType;
import org.bithome.core.helpers.DataHelpers;
import org.junit.Test;
import org.junit.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Mike Kurdziel on 5/22/14.
 */
public class DataHelpersTest {

    public static final String UINT8_MAX = "255";
    public static final String UINT16_MAX = "65535";
    public static final String UINT32_MAX = "4294967295";
    public static final String INT8_MAX = "127";
    public static final String INT16_MAX = "32767";
    public static final String INT32_MAX = "2147483647";
    public static final String INT8_MIN = "-128";
    public static final String INT16_MIN = "-32768";
    public static final String INT32_MIN = "-2147483648";

    @Test
    public void testExtractString() {
        String result;
        int[] data = new int[9];
        data[0] = 'T';
        data[1] = 'e';
        data[2] = 's';
        data[3] = 't';
        data[4] = 'D';
        data[5] = 'a';
        data[6] = 't';
        data[7] = 'a';
        data[8] = 0x00;

        // Test a normal extraction
        result = DataHelpers.toString(data, 4);

        assertEquals("Data", result);

        // Test out of bounds
        try {
            result = DataHelpers.toString(data, 10);

            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Test a string without null termination
        data[8] = 'z';
        // Test out of bounds
        try {
            result = DataHelpers.toString(data, 0);

            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testExtractValue() {
        String result;
        int[] data = new int[9];
        data[0] = 'T';
        data[1] = 'e';
        data[2] = 's';
        data[3] = 't';
        data[4] = 'D';
        data[5] = 'a';
        data[6] = 't';
        data[7] = 'a';
        data[8] = 0x00;

        // Test string
        result = DataHelpers.extractValue(data, 0, DataType.STRING);
        assertEquals("TestData", result);

        // Test boolean
        data[3] = 0;
        result = DataHelpers.extractValue(data, 3, DataType.BOOL);
        assertEquals("false", result);
        result = DataHelpers.extractValue(data, 4, DataType.BOOL);
        assertEquals("true", result);

        data = new int[4];

        // Test max
        data[0] = 0xff;
        data[1] = 0xff;
        data[2] = 0xff;
        data[3] = 0xff;
        assertEquals(UINT8_MAX, DataHelpers.extractValue(data, 0, DataType.UINT8));
        assertEquals(UINT16_MAX, DataHelpers.extractValue(data, 0, DataType.UINT16));
        assertEquals(UINT32_MAX, DataHelpers.extractValue(data, 0, DataType.UINT32));
        assertEquals("-1", DataHelpers.extractValue(data, 0, DataType.INT8));
        assertEquals("-1", DataHelpers.extractValue(data, 0, DataType.INT16));
        assertEquals("-1", DataHelpers.extractValue(data, 0, DataType.INT32));

        // Test min
        data[0] = 0x80;
        data[1] = 0x00;
        data[2] = 0x00;
        data[3] = 0x00;
        assertEquals("128", DataHelpers.extractValue(data, 0, DataType.UINT8));
        assertEquals("32768", DataHelpers.extractValue(data, 0, DataType.UINT16));
        assertEquals("2147483648", DataHelpers.extractValue(data, 0, DataType.UINT32));
        assertEquals(INT8_MIN, DataHelpers.extractValue(data, 0, DataType.INT8));
        assertEquals(INT16_MIN, DataHelpers.extractValue(data, 0, DataType.INT16));
        assertEquals(INT32_MIN, DataHelpers.extractValue(data, 0, DataType.INT32));

        // Test zero
        data[0] = 0x00;
        data[1] = 0x00;
        data[2] = 0x00;
        data[3] = 0x00;
        assertEquals("0", DataHelpers.extractValue(data, 0, DataType.UINT8));
        assertEquals("0", DataHelpers.extractValue(data, 0, DataType.UINT16));
        assertEquals("0", DataHelpers.extractValue(data, 0, DataType.UINT32));
        assertEquals("0", DataHelpers.extractValue(data, 0, DataType.INT8));
        assertEquals("0", DataHelpers.extractValue(data, 0, DataType.INT16));
        assertEquals("0", DataHelpers.extractValue(data, 0, DataType.INT32));

        // Test out of bounds bool
        try {
            assertEquals("0", DataHelpers.extractValue(data, 9, DataType.BOOL));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Test out of bounds UINT8
        try {
            assertEquals("0", DataHelpers.extractValue(data, 9, DataType.UINT8));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Test out of bounds UINT16
        try {
            assertEquals("0", DataHelpers.extractValue(data, 8, DataType.UINT16));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Test out of bounds UINT32
        try {
            assertEquals("0", DataHelpers.extractValue(data, 7, DataType.UINT32));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Test out of bounds INT8
        try {
            assertEquals("0", DataHelpers.extractValue(data, 9, DataType.INT8));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Test out of bounds INT16
        try {
            assertEquals("0", DataHelpers.extractValue(data, 8, DataType.INT16));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Test out of bounds INT32
        try {
            assertEquals("0", DataHelpers.extractValue(data, 7, DataType.INT32));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Test not implemented
        try {
            assertEquals("0", DataHelpers.extractValue(data, 7, DataType.VOID));
            Assert.fail();
        } catch (NotImplementedException e) {
            // Expected
        }
    }

    @Test
    public void testToBytes() {
        byte[] bytes;


        // Test boolean
        bytes = DataHelpers.toBytes(false);
        assertEquals(0, bytes[0]);
        bytes = DataHelpers.toBytes(true);
        assertEquals(1, bytes[0]);

        // Test string
        bytes = DataHelpers.toBytes("Test");
        assertEquals(bytes[0], 'T');
        assertEquals(bytes[1], 'e');
        assertEquals(bytes[2], 's');
        assertEquals(bytes[3], 't');

        // Test UINT8
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf(UINT8_MAX), 1);
        assertEquals(1, bytes.length);
        assertEquals(-1, bytes[0]); //0xff
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf("10"), 1);
        assertEquals(1, bytes.length);
        assertEquals(10, bytes[0]); //0xff

        // Test UINT16
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf(UINT16_MAX), 2);
        assertEquals(2, bytes.length);
        assertEquals(-1, bytes[0]); //0xff
        assertEquals(-1, bytes[1]); //0xff
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf(UINT8_MAX).add(UnsignedLong.valueOf("1")), 2);
        assertEquals(2, bytes.length);
        assertEquals(1, bytes[0]); //0xff
        assertEquals(0, bytes[1]); //0xff

        // Test UINT32
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf(UINT32_MAX), 4);
        assertEquals(4, bytes.length);
        assertEquals(-1, bytes[0]); //0xff
        assertEquals(-1, bytes[1]); //0xff
        assertEquals(-1, bytes[2]); //0xff
        assertEquals(-1, bytes[3]); //0xff
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf(UINT16_MAX).add(UnsignedLong.valueOf("1")), 4);
        assertEquals(4, bytes.length);
        assertEquals(0, bytes[0]); //0xff
        assertEquals(1, bytes[1]); //0xff
        assertEquals(0, bytes[2]); //0xff
        assertEquals(0, bytes[3]); //0xff

        // Test INT8
        bytes = DataHelpers.getValueBytes(Long.parseLong(INT8_MAX), DataType.INT8);
        assertEquals(1, bytes.length);
        assertEquals(127, bytes[0]); //0xff
        bytes = DataHelpers.toBytes(UnsignedLong.valueOf("10"), 1);
        assertEquals(1, bytes.length);
        assertEquals(10, bytes[0]); //0xff

        // Test INT16
        bytes = DataHelpers.getValueBytes(Long.parseLong(INT16_MAX), DataType.INT16);
        assertEquals(2, bytes.length);
        assertEquals(127, bytes[0]); //0xff
        assertEquals(-1, bytes[1]); //0xff
        bytes = DataHelpers.getValueBytes(Long.parseLong("256"), DataType.INT16);
        assertEquals(2, bytes.length);
        assertEquals(1, bytes[0]); //0xff
        assertEquals(0, bytes[1]); //0xff

        // Test INT32
        bytes = DataHelpers.getValueBytes(Long.parseLong(INT32_MAX), DataType.INT32);
        assertEquals(4, bytes.length);
        assertEquals(127, bytes[0]); //0xff
        assertEquals(-1, bytes[1]); //0xff
        assertEquals(-1, bytes[2]); //0xff
        assertEquals(-1, bytes[3]); //0xff
        bytes = DataHelpers.getValueBytes(Long.parseLong("16843009"), DataType.INT32);
        assertEquals(4, bytes.length);
        assertEquals(1, bytes[0]); //0xff
        assertEquals(1, bytes[1]); //0xff
        assertEquals(1, bytes[2]); //0xff
        assertEquals(1, bytes[3]); //0xff
    }
}
