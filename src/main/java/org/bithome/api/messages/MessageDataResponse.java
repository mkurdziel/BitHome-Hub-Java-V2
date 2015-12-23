package org.bithome.api.messages;

import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageDataResponse extends MessageRx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageDataResponse.class);

    private final UnsignedInteger8 actionIndex;
    private final UnsignedInteger8 parameterIndex;
    private final DataType dataType;
    private final UnsignedInteger8 options;
    private final String value;

    /**
     * Initialization constructor
     *
     * @param sourceNodeId
     * @param actionIndex
     * @param parameterIndex
     * @param dataType
     * @param options
     */
    public MessageDataResponse(
            final long sourceNodeId,
            final UnsignedInteger8 actionIndex,
            final UnsignedInteger8 parameterIndex,
            final DataType dataType,
            final UnsignedInteger8 options,
            final String value) {
        super(sourceNodeId);
        this.actionIndex = actionIndex;
        this.parameterIndex = parameterIndex;
        this.dataType = dataType;
        this.options = options;
        this.value = value;

        LOGGER.trace(toString());
    }

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageDataResponse(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset) {
        super(sourceNodeId);

        this.actionIndex = new UnsignedInteger8(data[dataOffset + 2]);
        this.parameterIndex = new UnsignedInteger8(data[dataOffset + 3]);
        this.dataType = DataType.parse(data[dataOffset + 4]);
        this.options = new UnsignedInteger8(data[dataOffset + 5]);

        this.value = String.valueOf(DataHelpers.extractValue(data, 6, dataType));

        LOGGER.trace(toString());
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.DATA_RESPONSE;
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return "MessageDataResponse{" +
                "actionIndex=" + actionIndex +
                ", parameterIndex=" + parameterIndex +
                ", dataType=" + dataType +
                ", options=" + options +
                ", value='" + value + '\'' +
                '}';
    }

    /**
     * @return the action index
     */
    public UnsignedInteger8 getActionIndex() {
        return actionIndex;
    }

    /**
     * @return the parameter index
     */
    public UnsignedInteger8 getParameterIndex() {
        return parameterIndex;
    }

    /**
     * @return the data type
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * @return the options
     */
    public UnsignedInteger8 getOptions() {
        return options;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * equals override
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDataResponse)) return false;

        MessageDataResponse that = (MessageDataResponse) o;

        if (actionIndex != null ? !actionIndex.equals(that.actionIndex) : that.actionIndex != null) return false;
        if (dataType != that.dataType) return false;
        if (options != null ? !options.equals(that.options) : that.options != null) return false;
        if (parameterIndex != null ? !parameterIndex.equals(that.parameterIndex) : that.parameterIndex != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    /**
     * hashcode override
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = actionIndex != null ? actionIndex.hashCode() : 0;
        result = 31 * result + (parameterIndex != null ? parameterIndex.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}

