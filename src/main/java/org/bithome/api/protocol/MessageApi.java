package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum MessageApi {
    DEVICE_STATUS_REQUEST(0x02),
    DEVICE_STATUS_RESPONSE(0x03),
    DEVICE_INFO_REQUEST(0x04),
    DEVICE_INFO_RESPONSE(0x05),
    CATALOG_REQUEST(0x10),
    CATALOG_RESPONSE(0x11),
    PARAMETER_REQUEST(0x12),
    PARAMETER_RESPONSE(0x13),
    BOOTLOAD_TRANSMIT(0x20),
    BOOTLOAD_RESPONSE(0x21),
    FUNCTION_TRANSMIT_REQUEST(0x40),
    FUNCTION_TRANSMIT_RESPONSE(0x80),
    FUNCTION_RECEIVE_REQUEST(0x60),
    FUNCTION_RECEIVE_RESPONSE(0x61),
    DATA_REQUEST(0x50),
    DATA_RESPONSE(0x51);

    private byte byteValue;

    MessageApi(int byteValue) {
       this.byteValue = (byte)byteValue;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
