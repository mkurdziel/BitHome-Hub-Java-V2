package org.bithome.api.messages;

import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedInteger;
import org.apache.commons.lang3.tuple.Pair;
import org.bithome.api.protocol.DataRequestType;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * This class represents a message sent to the node to request a specific parameter value.
 *
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageDataRequest extends MessageTx {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageDataRequest.class);

    private final HashSet<Pair<Integer,Integer>> parameters;
    private final DataRequestType dataRequestType;
    private final UnsignedInteger time;

    /**
     * Constructor for a single parameter pair
     *
     * @param destinationNodeId
     * @param actionIndex
     * @param parameterIndex
     */
    public MessageDataRequest(
            final long destinationNodeId,
            final int actionIndex,
            final int parameterIndex) {
        this(destinationNodeId,
                actionIndex,
                parameterIndex,
                DataRequestType.POLL_REQUEST,
                UnsignedInteger.asUnsigned(0));
    }


    /**
     * Initialization constructor
     *
     * @param destinationNodeId
     * @param actionIndex
     * @param parameterIndex
     * @param dataRequestType
     * @param time
     */
    public MessageDataRequest(
            final long destinationNodeId,
            final int actionIndex,
            final int parameterIndex,
            final DataRequestType dataRequestType,
            final UnsignedInteger time) {
        super(destinationNodeId);
        this.parameters = Sets.newHashSet(Pair.of(actionIndex, parameterIndex));
        this.dataRequestType = dataRequestType;
        this.time = time;

        LOGGER.trace(toString());
    }

    /**
     * Initialization constructor
     *
     * @param destinationNodeId
     * @param parameters
     * @param dataRequestType
     * @param time
     */
    public MessageDataRequest(
            final long destinationNodeId,
            final HashSet<Pair<Integer, Integer>> parameters,
            final DataRequestType dataRequestType,
            final UnsignedInteger time) {
        super(destinationNodeId);
        this.parameters = Sets.newHashSet(parameters);
        this.dataRequestType = dataRequestType;
        this.time = time;

        LOGGER.trace(toString());
    }

    /**
     * @return the API value of this message
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.DATA_REQUEST;
    }

    /**
     * @return the byte array for this message
     */
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[7 + parameters.size()*2];
        byte[] timeBytes = DataHelpers.toBytes(time, DataType.UINT16.getByteWidth());

        bytes[0] = MessageConstants.PACKET_START.getByteValue();
        bytes[1] = getMessageApi().getByteValue();
        bytes[2] = (byte) parameters.size();
        bytes[3] = dataRequestType.getByteValue();
        bytes[4] = 0x00; // options
        bytes[5] = timeBytes[0];
        bytes[6] = timeBytes[1];

        return bytes;
    }

    /**
     * @return string description
     */
    @Override
    public String toString() {
        return "MessageDataRequest{" +
                "parameters=" + parameters +
                ", dataRequestType=" + dataRequestType +
                ", time=" + time +
                '}';
    }
}

