package org.bithome.test.api.messages;

import org.apache.commons.lang.ArrayUtils;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.messages.MessageCatalogResponse;
import org.bithome.api.messages.MessageDataResponse;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.bithome.core.exception.InvalidMessageDataException;
import org.bithome.core.helpers.DataHelpers;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by Mike Kurdziel on 5/23/14.
 */
public class MessageDataResponseTest {
    @Test
    public void testCreation()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger8 actionIndex = new UnsignedInteger8(1);
        final UnsignedInteger8 parameterIndex = new UnsignedInteger8(2);
        final UnsignedInteger8 options = new UnsignedInteger8(3);
        final DataType dataType = DataType.UINT8;
        final String value = "8";

        MessageDataResponse message1 = new MessageDataResponse(
                sourceNodeId,
                actionIndex,
                parameterIndex,
                dataType,
                options,
                value);

        assertEquals(sourceNodeId, (long)message1.getSourceNode().get());
        assertEquals(MessageApi.DATA_RESPONSE, message1.getMessageApi());
        assertFalse(message1.getDestNode().isPresent());
        assertEquals(actionIndex, message1.getActionIndex());
        assertEquals(parameterIndex, message1.getParameterIndex());
        assertEquals(dataType, message1.getDataType());
        assertEquals(value, message1.getValue());
        assertEquals(options, message1.getOptions());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message1.getTimeStamp()).getSeconds() < 1);

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.DATA_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                DataHelpers.toBytes(parameterIndex)[0],
                dataType.getByteValue(),
                DataHelpers.toBytes(options)[0],
                Integer.valueOf(value)};


        MessageDataResponse message2 = new MessageDataResponse(sourceNodeId, data, 0);

        assertEquals(message1, message2);
        assertEquals(sourceNodeId, (long)message2.getSourceNode().get());
        assertEquals(MessageApi.DATA_RESPONSE, message2.getMessageApi());
        assertFalse(message2.getDestNode().isPresent());
        assertEquals(actionIndex, message2.getActionIndex());
        assertEquals(parameterIndex, message2.getParameterIndex());
        assertEquals(dataType, message2.getDataType());
        assertEquals(value, message2.getValue());
        assertEquals(options, message2.getOptions());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message2.getTimeStamp()).getSeconds() < 1);
    }

    @Test
    public void testErrorReponse()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger8 actionIndex = new UnsignedInteger8(1);
        final UnsignedInteger8 parameterIndex = new UnsignedInteger8(2);
        final UnsignedInteger8 options = new UnsignedInteger8(3);
        final DataType dataType = DataType.UINT8;
        final String value = "8";

        // Test short message
        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.DATA_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                DataHelpers.toBytes(parameterIndex)[0],
                dataType.getByteValue(),
                DataHelpers.toBytes(options)[0]};


        try {
            new MessageDataResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
}
