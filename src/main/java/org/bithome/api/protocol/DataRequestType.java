package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum DataRequestType {
    POLL_REQUEST(0x00),
    ON_CHANGE(0x01),
    ON_INTEVAL(0x02);

    private byte byteValue;

    DataRequestType(int byteValue) {
        this.byteValue = (byte) byteValue;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
