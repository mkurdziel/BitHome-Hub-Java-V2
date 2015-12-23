package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum DeviceStatus {
    HARDWARE_RESET(0x00),
    ACTIVE(0x01);

    private byte byteValue;

    DeviceStatus(int byteValue) {
        this.byteValue = (byte) byteValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public static DeviceStatus parse(final int i) {
        for (DeviceStatus type : DeviceStatus.values()) {
            if (i == type.getByteValue()) {
                return type;
            }
        }
        return null;
    }
}
