package org.bithome.api.protocol;

import com.google.common.base.Optional;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public enum BootloadResponseType {
    BOOTLOAD_READY(0x00),
    DATA_SUCCESS(0x01),
    BOOTLOAD_COMPLETE(0x02);

    private int byteValue;

    BootloadResponseType(int byteValue) {
        this.byteValue = byteValue;
    }

    public int getByteValue() {
        return byteValue;
    }

    public static BootloadResponseType parse(final int b) {
        for (BootloadResponseType type : BootloadResponseType.values()) {
            if (b == type.getByteValue()) {
                return type;
            }
        }
        return null;
    }
}
