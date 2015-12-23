package org.bithome.api.messages;

import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.core.exception.InvalidMessageDataException;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageCatalogResponse extends MessageRx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageCatalogResponse.class);

    private final UnsignedInteger8 actionIndex;
    private final UnsignedInteger8 parameterCount;
    private final DataType returnType;
    private final String name;
    private final UnsignedInteger8 options;

    /**
     * Initialization constructor
     *
     * @param sourceNodeId
     * @param actionIndex
     * @param parameterCount
     * @param returnType
     * @param name
     * @param options
     */
    public MessageCatalogResponse(
            final long sourceNodeId,
            final UnsignedInteger8 actionIndex,
            final UnsignedInteger8 parameterCount,
            final DataType returnType,
            final String name,
            final UnsignedInteger8 options) {
        super(sourceNodeId);
        this.actionIndex = actionIndex;
        this.parameterCount = parameterCount;
        this.returnType = returnType;
        this.name = name;
        this.options = options;

        LOGGER.trace(toString());
    }

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageCatalogResponse(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset)
            throws InvalidMessageDataException {
        super(sourceNodeId);

        this.actionIndex = new UnsignedInteger8(data[dataOffset + 2]);
        this.returnType = DataType.parse(data[dataOffset + 3]);
        if (returnType == null) {
            throw new InvalidMessageDataException("ReturnType");
        }
        this.parameterCount = new UnsignedInteger8(data[dataOffset + 4]);
        this.options = new UnsignedInteger8(data[dataOffset + 5]);
        this.name = DataHelpers.toString(data, dataOffset + 6);

        LOGGER.trace(toString());
    }

    /**
     * @return the action index
     */
    public UnsignedInteger8 getActionIndex() {
        return actionIndex;
    }

    /**
     * @return the number of parameters
     */
    public UnsignedInteger8 getParameterCount() {
        return parameterCount;
    }

    /**
     * @return the return data type
     */
    public DataType getReturnType() {
        return returnType;
    }

    /**
     * @return get the name of the action
     */
    public String getName() {
        return name;
    }

    /**
     * @return the response options
     */
    public UnsignedInteger8 getOptions() {
        return options;
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.CATALOG_RESPONSE;
    }

    /**
     * @return string description
     */
    @Override
    public String toString() {
        return "MessageCatalogResponse{" +
                "actionIndex=" + actionIndex +
                ", parameterCount=" + parameterCount +
                ", returnType=" + returnType +
                ", name='" + name + '\'' +
                ", options=" + options +
                '}';
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
        if (!(o instanceof MessageCatalogResponse)) return false;

        MessageCatalogResponse that = (MessageCatalogResponse) o;

        if (actionIndex != null ? !actionIndex.equals(that.actionIndex) : that.actionIndex != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (options != null ? !options.equals(that.options) : that.options != null) return false;
        if (parameterCount != null ? !parameterCount.equals(that.parameterCount) : that.parameterCount != null)
            return false;
        if (returnType != that.returnType) return false;

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
        result = 31 * result + (parameterCount != null ? parameterCount.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        return result;
    }
}
