package org.bithome.api.messages;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.bithome.api.protocol.MessageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * This class represents a message sent to the node to request a specific parameter value.
 *
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageFunctionRecieveResponse extends MessageTx {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageFunctionRecieveResponse.class);

    protected MessageFunctionRecieveResponse(final long destinationNodeId) {
        super(destinationNodeId);

        LOGGER.trace(toString());

        throw new NotImplementedException();
    }

    /**
     * @return the API value of this message
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.FUNCTION_RECEIVE_RESPONSE;
    }

    /**
     * @return the byte array for this message
     */
    @Override
    public byte[] getBytes() {
        List<Byte> bytes = Lists.newArrayList();

        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[bytes.size()]));
    }

}

