package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum BootloadTransmitType {

    REBOOT_DEVICE(0x00),
    BOOTLOAD_REQUEST(0x01),
    DATA_TRANSMIT(0x03),
    DATA_COMPLETE(0x04);

    private byte byteValue;

    BootloadTransmitType(int byteValue) {
        this.byteValue = (byte)byteValue;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
