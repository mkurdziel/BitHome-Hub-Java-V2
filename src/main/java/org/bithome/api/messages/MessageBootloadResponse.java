package org.bithome.api.messages;

import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.protocol.BootloadResponseType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.core.exception.InvalidMessageDataException;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a response coming back from a device regarding it's booatloading status
 * <p/>
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageBootloadResponse extends MessageRx {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageBootloadResponse.class);

    private final BootloadResponseType bootloadResponseType;
    private final UnsignedInteger16 memoryAddress;

    /**
     * Initialization constructor
     *
     * @param sourceNodeId
     * @param bootloadResponseType
     * @param memoryAddress
     */
    public MessageBootloadResponse(final long sourceNodeId,
                                   final BootloadResponseType bootloadResponseType,
                                   final UnsignedInteger16 memoryAddress) {
        super(sourceNodeId);
        this.bootloadResponseType = bootloadResponseType;
        this.memoryAddress = memoryAddress;
    }

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageBootloadResponse(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset)
            throws InvalidMessageDataException, IndexOutOfBoundsException {

        super(sourceNodeId);

        this.bootloadResponseType = BootloadResponseType.parse(data[dataOffset + 2]);

        if (this.bootloadResponseType == null) {
            throw new InvalidMessageDataException("bootloadResponseType");
        }

        // Memory address if only valid for data success command
        if (bootloadResponseType == BootloadResponseType.DATA_SUCCESS) {
            memoryAddress = new UnsignedInteger16(DataHelpers.toUInt16(data, dataOffset + 3));
        } else {
            memoryAddress = null;
        }

        LOGGER.trace(toString());
    }

    /**
     * @return the bootload response
     */
    public BootloadResponseType getBootloadResponseType() {
        return bootloadResponseType;
    }

    /**
     * @return the address of the memory this message is about
     */
    public UnsignedInteger16 getMemoryAddress() {
        return memoryAddress;
    }

    /**
     * @return a string description
     */
    @Override
    public String toString() {
        return "MessageBootloadResponse{" +
                "bootloadResponseType=" + bootloadResponseType +
                ", memoryAddress=" + memoryAddress +
                '}';
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.BOOTLOAD_RESPONSE;
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
        if (!(o instanceof MessageBootloadResponse)) return false;

        MessageBootloadResponse that = (MessageBootloadResponse) o;

        if (bootloadResponseType != that.bootloadResponseType) return false;
        return !(memoryAddress != null ? !memoryAddress.equals(that.memoryAddress) : that.memoryAddress != null);

    }

    /**
     * hashcode override
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = bootloadResponseType != null ? bootloadResponseType.hashCode() : 0;
        result = 31 * result + (memoryAddress != null ? memoryAddress.hashCode() : 0);
        return result;
    }
}
