package org.bithome.api.messages;

import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a message from the controller to the device
 * requesting information about its catalog
 *
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageCatalogRequest extends MessageTx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageCatalogRequest.class);

    private final int actionIndex;

    /**
     * Initialization constructor
     *
     * @param destNodeId
     * @param actionIndex
     */
    public MessageCatalogRequest(
            final long destNodeId,
            final int actionIndex) {
        super(destNodeId);

        this.actionIndex = actionIndex;

        LOGGER.trace(toString());
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.CATALOG_REQUEST;
    }

    /**
     * @return the byte array representing the message
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[3];
        bytes[0] = MessageConstants.PACKET_START.getByteValue();
        bytes[1] = getMessageApi().getByteValue();
        bytes[2] = (byte) actionIndex;

        return bytes;
    }

    /**
     * @return string description
     */
    @Override
    public String toString() {
        return "MessageCatalogRequest{" +
                "actionIndex=" + actionIndex +
                '}';
    }
}
