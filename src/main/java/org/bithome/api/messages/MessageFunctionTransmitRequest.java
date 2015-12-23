package org.bithome.api.messages;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.bithome.api.parameters.NodeParameter;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents a message sent to the node to request a specific parameter value.
 *
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageFunctionTransmitRequest extends MessageTx {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageFunctionTransmitRequest.class);

    private final int actionIndex;
    private final List<NodeParameter> parameters;
    private final DataType returnType;

    public MessageFunctionTransmitRequest(final long destinationNodeId,
                                          final int actionIndex,
                                          final List<NodeParameter> parameters,
                                          final DataType returnType) {
        super(destinationNodeId);
        this.actionIndex = actionIndex;
        this.parameters = Lists.newArrayList(parameters);
        this.returnType = returnType;

        LOGGER.trace(this.toString());
    }

    /**
     * @return the API value of this message
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.FUNCTION_TRANSMIT_REQUEST;
    }

    /**
     * @return the byte array for this message
     */
    @Override
    public byte[] getBytes() {
        List<Byte> bytes = Lists.newArrayList();

        bytes.add(MessageConstants.PACKET_START.getByteValue());
        bytes.add(getMessageApi().getByteValue());
        bytes.add((byte)actionIndex);
        bytes.add((byte)0x00); // Options

        for (NodeParameter parameter : parameters) {
            bytes.addAll(
                    Lists.newArrayList(
                            ArrayUtils.toObject(
                                    DataHelpers.getValueBytes(parameter))));
        }

        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[bytes.size()]));
    }

    @Override
    public String toString() {
        return "MessageFunctionTransmitRequest{" +
                "actionIndex=" + actionIndex +
                ", parameters=" + parameters +
                ", returnType=" + returnType +
                '}';
    }
}

