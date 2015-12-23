package org.bithome.api.messages;

import org.bithome.api.Version;
import org.bithome.api.protocol.DeviceStatus;
import org.bithome.api.protocol.MessageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageFunctionRecieveRequest extends MessageRx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageFunctionRecieveRequest.class);

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageFunctionRecieveRequest(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset) {
        super(sourceNodeId);

        LOGGER.trace(toString());

        throw new NotImplementedException();
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.FUNCTION_RECEIVE_REQUEST;
    }
}

