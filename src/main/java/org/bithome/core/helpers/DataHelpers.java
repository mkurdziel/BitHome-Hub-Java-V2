package org.bithome.core.helpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.apache.commons.lang.ArrayUtils;
import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.parameters.NodeParameter;
import org.bithome.api.parameters.Parameter;
import org.bithome.api.protocol.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.print.DocFlavor;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class DataHelpers {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataHelpers.class);

    /**
     * Convert a byte array to a UINT8
     *
     * @param data
     * @param startIndex
     * @return
     */
    public static short toUInt8(int[] data, int startIndex)
    {
        return 	(short)(0xff & data[startIndex]);
    }

    /**
     * Convert a byte array to a UINT16
     *
     * @param data
     * @param startIndex
     * @return
     */
    public static int toUInt16(int[] data, int startIndex)
    {
        return 	(int)(0xff & data[startIndex]) << 8 |
                (int)(0xff & data[startIndex + 1]);
    }

    /**
     * Convert a byte array to a UINT32
     *
     * @param data
     * @param startIndex
     * @return
     */
    public static UnsignedInteger toUInt32(int[] data, int startIndex)
    {
        return 	UnsignedInteger.valueOf(
                (long)(0xff & data[startIndex]) << 24 |
                (long)(0xff & data[startIndex+1]) << 16 |
                (long)(0xff & data[startIndex+2]) << 8 |
                (long)(0xff & data[startIndex+3]));
    }

//    /**
//     * Convert a byte array to a UINT64
//     * @param data
//     * @param startIndex
//     * @return
//     */
//    public static UnsignedLong toUInt64(int[] data, int startIndex)
//    {
//        BigInteger bigInt = BigInteger.ZERO;
//
//        for (int nByteCt = 0; nByteCt < 8; nByteCt++)
//        {
//            bigInt = bigInt.shiftLeft(8).add(BigInteger.valueOf(data[startIndex + nByteCt]));
//        }
//
//        UnsignedLong unsignedLong = UnsignedLong.valueOf(bigInt);
//
//        return unsignedLong;
//    }

    /**
     * Extract a null-terminated string from an array of int data
     *
     * @param data
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static String toString(int[] data, int index) throws IndexOutOfBoundsException
    {
        int length = data.length;
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        StringBuilder sb = new StringBuilder();
        int c;
        while( (c = data[index++]) != 00)
        {
            sb.append((char)c);
        }

        return sb.toString();
    }

    /**
     * Extract a boolean value from an array of int data
     *
     * @param data
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static boolean toBool(int[] data, int index) throws IndexOutOfBoundsException
    {
        int length = data.length;
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        return data[index] == 0 ? false : true;
    }

    /**
     * Extract a signed 8-bit number from an array of int data
     * @param data
     * @param startIndex
     * @return
     */
    public static byte extractInt8(
            int[] data,
            int startIndex)
    {
        int length = data.length;
        if (startIndex < 0 || startIndex > length) {
            throw new IndexOutOfBoundsException("Index: " + startIndex + ", Length: " + length);
        }

        return (byte)data[startIndex];
    }

    /**
     * Extract a signed 16-bit number from an array of int data
     * @param data
     * @param startIndex
     * @return
     */
    public static short toInt16(
            int[] data,
            int startIndex)
    {
        int dataLength = 2;
        int length = data.length;
        if (startIndex < 0 || (startIndex+dataLength) > length) {
            throw new IndexOutOfBoundsException("Index: " + startIndex + ", Length: " + length);
        }

        short value = 0;
        for (int i=0; i<dataLength; i++) {
            value = (short) ((value << 8) + data[startIndex + i]);
        }

        return value;
    }

    /**
     * Extract a signed 32-bit number from an array of int data
     * @param data
     * @param startIndex
     * @return
     */
    public static int toInt32(
            int[] data,
            int startIndex)
    {
        int dataLength = 4;
        int length = data.length;
        if (startIndex < 0 || (startIndex+dataLength) > length) {
            throw new IndexOutOfBoundsException("Index: " + startIndex + ", Length: " + length);
        }

        int value = 0;
        for (int i=0; i<dataLength; i++) {
            value = (value << 8) + data[startIndex + i];
        }

        return value;
    }

    public static byte[] toBytes(boolean v) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (v ? 1 : 0);
        return bytes;
    }

    public static int[] toInts(String str) {
        return toIntArray(toBytes(str));
    }

    public static byte[] toBytes(String str) {
        byte[] bytes = str.getBytes();
        return ArrayUtils.add(bytes, (byte)0x00);
    }


    /**
     * Convert a long to bytes
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(long value) {
        byte[] writeBuffer = new byte[ 8 ];

        writeBuffer[0] = (byte)(value >>> 56);
        writeBuffer[1] = (byte)(value >>> 48);
        writeBuffer[2] = (byte)(value >>> 40);
        writeBuffer[3] = (byte)(value >>> 32);
        writeBuffer[4] = (byte)(value >>> 24);
        writeBuffer[5] = (byte)(value >>> 16);
        writeBuffer[6] = (byte)(value >>>  8);
        writeBuffer[7] = (byte)(value >>>  0);

        return writeBuffer;
    }

    public static byte[] toBytes(int value) {
        byte[] writeBuffer = new byte[ 4 ];

        writeBuffer[0] = (byte)(value >>> 24);
        writeBuffer[1] = (byte)(value >>> 16);
        writeBuffer[2] = (byte)(value >>>  8);
        writeBuffer[3] = (byte)(value >>>  0);

        return writeBuffer;
    }

    public static byte[] toBytes(short value) {
        byte[] writeBuffer = new byte[ 2 ];

        writeBuffer[0] = (byte)(value >>>  8);
        writeBuffer[1] = (byte)(value >>>  0);

        return writeBuffer;
    }

    public static byte[] toBytes(byte value) {
        byte[] writeBuffer = new byte[ 1 ];

        writeBuffer[0] = (byte)(value);

        return writeBuffer;
    }

    /**
     * Convert an unsigned integer to bytes
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(
            final UnsignedInteger value,
            final int dataWidth) {
        return toBytes(UnsignedLong.asUnsigned(value.longValue()), dataWidth);
    }

    /**
     * Convert an unsigned integer to bytes
     *
     * @param value
     * @return
     */
    public static byte[] toBytes( final UnsignedInteger8 value) {
        return toBytes(UnsignedLong.asUnsigned(value.longValue()), 1);
    }

    /**
     * Convert an unsigned integer to bytes
     *
     * @param value
     * @return
     */
    public static byte[] toBytes( final UnsignedInteger16 value) {
        return toBytes(UnsignedLong.asUnsigned(value.longValue()), 2);
    }

    /**
     * Convert an unsigned integer to bytes
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(
            final UnsignedLong value,
            final int dataWidth) {

        byte[] writeBuffer = new byte[ dataWidth ];

        long val = value.longValue();

        switch (dataWidth) {
            case 1:
                writeBuffer[0] = (byte)(val);
                return writeBuffer;
            case 2:
                writeBuffer[0] = (byte)(val >>>  8);
                writeBuffer[1] = (byte)(val >>>  0);
                return writeBuffer;
            case 4:
                writeBuffer[0] = (byte)(val >>>  24);
                writeBuffer[1] = (byte)(val >>>  16);
                writeBuffer[2] = (byte)(val >>>  8);
                writeBuffer[3] = (byte)(val >>>  0);
                return writeBuffer;
            case 8:
                writeBuffer[0] = (byte)(val >>> 56);
                writeBuffer[1] = (byte)(val >>> 48);
                writeBuffer[2] = (byte)(val >>> 40);
                writeBuffer[3] = (byte)(val >>> 32);
                writeBuffer[4] = (byte)(val >>> 24);
                writeBuffer[5] = (byte)(val >>> 16);
                writeBuffer[6] = (byte)(val >>>  8);
                writeBuffer[7] = (byte)(val >>>  0);
                return writeBuffer;
            default:
                LOGGER.error("Trying to convert unsigned long of unknown size");
                throw new NotImplementedException();
        }
    }

    /**
     * Extract a value as a string based on its DataType
     *
     * @param data
     * @param startIndex
     * @param dataType
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static String extractValue(
            final int[] data,
            final int startIndex,
            final DataType dataType)
        throws IndexOutOfBoundsException {

        switch (dataType) {
            case BOOL:
                return String.valueOf(toBool(data, startIndex));
            case STRING:
                return toString(data, startIndex);
            case INT8:
                return String.valueOf(extractInt8(data, startIndex));
            case INT16:
                return String.valueOf(toInt16(data, startIndex));
            case INT32:
                return String.valueOf(toInt32(data, startIndex));
            case UINT8:
                return String.valueOf(toUInt8(data, startIndex));
            case UINT16:
                return String.valueOf(toUInt16(data, startIndex));
            case UINT32:
                return String.valueOf(toUInt32(data, startIndex));
            default:
                LOGGER.error("Trying to extract value from unimplemented type");
                throw new NotImplementedException();

        }
    }

    public static byte[] getValueBytes(final Number number, final DataType dataType) {
        switch (dataType) {
            case INT8:
                return toBytes(number.byteValue());
            case INT16:
                return toBytes(number.shortValue());
            case INT32:
                return toBytes(number.intValue());
            case UINT8:
            case UINT16:
            case UINT32:
                return toBytes((UnsignedLong) number, dataType.getByteWidth());
            default:
                LOGGER.error("Trying to get value bytes from unimplemented type");
                throw new NotImplementedException();
        }
    }

    public static byte[] getValueBytes(final Parameter parameter) {

        switch (parameter.getDataType()) {
            case BOOL:
                return toBytes(Boolean.parseBoolean(parameter.getStrValue()));
            case STRING:
                return toBytes(parameter.getStrValue());
            case INT8:
            case INT16:
            case INT32:
            case UINT8:
            case UINT16:
            case UINT32:
                return getValueBytes(parameter.getIntValue(), parameter.getDataType());
            default:
                LOGGER.error("Trying to get value bytes from unimplemented type");
                throw new NotImplementedException();
        }
    }

    public static int[] toIntArray(final byte[] bytes) {
        int len = bytes.length;
        int[] arr = new int[len];

        for(int i = 0; i < len; i++)
            arr[i] = (int)bytes[i];

        return arr;
    }
}
