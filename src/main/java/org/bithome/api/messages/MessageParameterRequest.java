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
public class MessageParameterRequest extends MessageTx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageParameterRequest.class);

    private final int actionIndex;
    private final int parameterIndex;

    public MessageParameterRequest(final long destinationNodeId, final int actionIndex, final int parameterIndex) {
        super(destinationNodeId);

        this.actionIndex = actionIndex;
        this.parameterIndex = parameterIndex;

        LOGGER.trace(toString());
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.PARAMETER_REQUEST;
    }

    /**
     * @return the byte array representing the message
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[4];
        bytes[0] = MessageConstants.PACKET_START.getByteValue();
        bytes[1] = getMessageApi().getByteValue();
        bytes[2] = (byte) actionIndex;
        bytes[3] = (byte) parameterIndex;

        return bytes;
    }

    @Override
    public String toString() {
        return "MessageParameterRequest{" +
                "actionIndex=" + actionIndex +
                ", parameterIndex=" + parameterIndex +
                '}';
    }
}
