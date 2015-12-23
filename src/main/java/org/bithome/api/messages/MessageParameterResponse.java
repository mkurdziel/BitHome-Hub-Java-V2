package org.bithome.api.messages;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.core.exception.InvalidMessageDataException;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageParameterResponse extends MessageRx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageParameterResponse.class);

    private final UnsignedInteger8 actionIndex;
    private final UnsignedInteger8 parameterIndex;
    private final DataType dataType;
    private final UnsignedInteger8 options;
    private final String name;
    private final String maxValue;
    private final String minValue;
    private final Map<String, Integer> enumValues;

    /**
     * Initialization constructor
     *
     * @param sourceNodeId
     * @param actionIndex
     * @param parameterIndex
     * @param dataType
     * @param options
     * @param name
     * @param maxValue
     * @param minValue
     * @param enumValues
     */
    public MessageParameterResponse(final long sourceNodeId,
                                    final UnsignedInteger8 actionIndex,
                                    final UnsignedInteger8 parameterIndex,
                                    final DataType dataType,
                                    final UnsignedInteger8 options,
                                    final String name,
                                    final String minValue,
                                    final String maxValue,
                                    final Map<String, Integer> enumValues) {
        super(sourceNodeId);
        this.actionIndex = actionIndex;
        this.parameterIndex = parameterIndex;
        this.dataType = dataType;
        this.options = options;
        this.name = name;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.enumValues = Maps.newHashMap(enumValues);

        LOGGER.trace(toString());
    }

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageParameterResponse(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset)
            throws InvalidMessageDataException {
        super(sourceNodeId);

        int index = dataOffset + 2;

        this.actionIndex = new UnsignedInteger8(data[index++]);
        this.parameterIndex = new UnsignedInteger8(data[index++]);
        this.dataType = DataType.parse(data[index++]);
        if (dataType == null) {
            throw new InvalidMessageDataException("DataType");
        }
        this.options = new UnsignedInteger8(data[index++]);

        // Get the parameter name
        this.name = DataHelpers.toString(data, index);
        index += name.length() + 1; // offset is for the null termination

        // Get the min and max values
        minValue = DataHelpers.extractValue(data, index, dataType);
        index += dataType.getByteWidth();
        maxValue = DataHelpers.extractValue(data, index, dataType);

        // TODO: enum values
        enumValues = Maps.newHashMap();

        LOGGER.trace(toString());
    }

    public UnsignedInteger8 getActionIndex() {
        return actionIndex;
    }

    public UnsignedInteger8 getParameterIndex() {
        return parameterIndex;
    }

    public DataType getDataType() {
        return dataType;
    }

    public UnsignedInteger8 getOptions() {
        return options;
    }

    public String getName() {
        return name;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public Map<String, Integer> getEnumValues() {
        return enumValues;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageParameterResponse)) return false;

        MessageParameterResponse that = (MessageParameterResponse) o;

        if (actionIndex != null ? !actionIndex.equals(that.actionIndex) : that.actionIndex != null) return false;
        if (dataType != that.dataType) return false;
        if (enumValues != null ? !enumValues.equals(that.enumValues) : that.enumValues != null) return false;
        if (maxValue != null ? !maxValue.equals(that.maxValue) : that.maxValue != null) return false;
        if (minValue != null ? !minValue.equals(that.minValue) : that.minValue != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (options != null ? !options.equals(that.options) : that.options != null) return false;
        if (parameterIndex != null ? !parameterIndex.equals(that.parameterIndex) : that.parameterIndex != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = actionIndex != null ? actionIndex.hashCode() : 0;
        result = 31 * result + (parameterIndex != null ? parameterIndex.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (maxValue != null ? maxValue.hashCode() : 0);
        result = 31 * result + (minValue != null ? minValue.hashCode() : 0);
        result = 31 * result + (enumValues != null ? enumValues.hashCode() : 0);
        return result;
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.PARAMETER_RESPONSE;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return "MessageParameterResponse{" +
                "actionIndex=" + actionIndex +
                ", parameterIndex=" + parameterIndex +
                ", dataType=" + dataType +
                ", options=" + options +
                ", name='" + name + '\'' +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", enumValues=" + enumValues +
                '}';
    }
}
