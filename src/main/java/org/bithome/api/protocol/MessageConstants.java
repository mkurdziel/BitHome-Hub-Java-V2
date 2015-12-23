package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum MessageConstants {
    PACKET_START(0xA5);

    private byte byteValue;

    MessageConstants(int byteValue) {
        this.byteValue = (byte)byteValue;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
