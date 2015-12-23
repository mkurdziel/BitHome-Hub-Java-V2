package org.bithome.api.parameters;

import org.bithome.api.protocol.DataType;

import java.math.BigInteger;

/**
 * Created by Mike Kurdziel on 5/22/14.
 */
public abstract class Parameter {

    private DataType dataType;
    private String strValue;
    private Number intValue;

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(final DataType dataType) {
        this.dataType = dataType;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(final String strValue) {
        this.strValue = strValue;
    }

    public Number getIntValue() {
        return intValue;
    }

    public void setIntValue(final BigInteger intValue) {
        this.intValue = intValue;
    }
}
