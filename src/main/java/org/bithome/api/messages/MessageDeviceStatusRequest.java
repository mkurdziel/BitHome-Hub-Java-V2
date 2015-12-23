package org.bithome.api.messages;

import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a message sent to the node to request a specific parameter value.
 *
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageDeviceStatusRequest extends MessageTx {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageDeviceStatusRequest.class);

    public MessageDeviceStatusRequest(final long destinationNodeId) {
        super(destinationNodeId);
    }

    /**
     * @return the API value of this message
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.DEVICE_STATUS_REQUEST;
    }

    /**
     * @return the byte array for this message
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[2];

        bytes[0] = MessageConstants.PACKET_START.getByteValue();
        bytes[1] = getMessageApi().getByteValue();

        return bytes;
    }

    /**
     * @return string description
     */
    @Override
    public String toString() {
        return "MessageDeviceStatusRequest{}";
    }
}

