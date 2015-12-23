package org.bithome.api.protocol;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum BootloadErrorType {
    START_BIT(0x01),
    SIZE(0x02),
    API(0x03),
    ADDRESS(0x04),
    BOOTLOAD_API(0x05),
    DOWNLOAD_START(0x06),
    PAGE_LENGTH(0x07),
    DATA_ADDRESS(0x08),
    CHECKSUM(0x09),
    BITHOME_API(0x0A);

    private int byteValue;

    BootloadErrorType(int byteValue) {
        this.byteValue = byteValue;
    }

    public int getByteValue() {
        return byteValue;
    }
}
