package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum DataType {
    VOID(0x00, 0),
    UINT8(0x01, 1),
    UINT16(0x02, 2),
    UINT32(0x03, 4),
    UINT64(0x04, 8),
    INT8(0x05, 1),
    INT16(0x06, 2),
    INT32(0x07, 4),
    INT64(0x08, 8),
    STRING(0x09, 0),
    BOOL(0x0A, 1);

    private int byteValue;
    private int width;

    DataType(final int byteValue, final int width) {
        this.byteValue = byteValue;
        this.width = width;
    }

    public int getByteValue() {
        return byteValue;
    }

    public static DataType parse(final int i) {
        for (DataType type : DataType.values()) {
            if (i == type.getByteValue()) {
                return type;
            }
        }
        return null;
    }

    public int getByteWidth() {
        return width;
    }
}
