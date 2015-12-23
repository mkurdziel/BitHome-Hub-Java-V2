package org.bithome.test.api.messages;

import org.apache.commons.lang.ArrayUtils;
import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.messages.MessageBootloadResponse;
import org.bithome.api.messages.MessageCatalogResponse;
import org.bithome.api.protocol.BootloadResponseType;
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
public class MessageCatalogResponseTest {
    @Test
    public void testCreation()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger8 actionIndex = new UnsignedInteger8(1);
        final UnsignedInteger8 parameterCount = new UnsignedInteger8(2);
        final DataType returnType = DataType.UINT16;
        final String name = "action1";
        final UnsignedInteger8 options = new UnsignedInteger8(3);

        MessageCatalogResponse message1 = new MessageCatalogResponse(
                sourceNodeId,
                actionIndex,
                parameterCount,
                returnType,
                name,
                options);

        assertEquals(sourceNodeId, (long)message1.getSourceNode().get());
        assertEquals(MessageApi.CATALOG_RESPONSE, message1.getMessageApi());
        assertFalse(message1.getDestNode().isPresent());
        assertEquals(actionIndex, message1.getActionIndex());
        assertEquals(parameterCount, message1.getParameterCount());
        assertEquals(returnType, message1.getReturnType());
        assertEquals(name, message1.getName());
        assertEquals(options, message1.getOptions());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message1.getTimeStamp()).getSeconds() < 1);

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.CATALOG_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                returnType.getByteValue(),
                DataHelpers.toBytes(parameterCount)[0],
                DataHelpers.toBytes(options)[0]};

        data = ArrayUtils.addAll(data, DataHelpers.toInts(name));


        MessageCatalogResponse message2 = new MessageCatalogResponse(sourceNodeId, data, 0);

        assertEquals(message1, message2);
        assertEquals(sourceNodeId, (long)message2.getSourceNode().get());
        assertEquals(MessageApi.CATALOG_RESPONSE, message2.getMessageApi());
        assertFalse(message2.getDestNode().isPresent());
        assertEquals(actionIndex, message2.getActionIndex());
        assertEquals(parameterCount, message2.getParameterCount());
        assertEquals(returnType, message2.getReturnType());
        assertEquals(name, message2.getName());
        assertEquals(options, message2.getOptions());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message2.getTimeStamp()).getSeconds() < 1);
    }

    @Test
    public void testErrorReponse()
            throws InvalidMessageDataException {
        final long sourceNodeId = 1L;
        final UnsignedInteger8 actionIndex = new UnsignedInteger8(1);
        final UnsignedInteger8 parameterCount = new UnsignedInteger8(2);
        final DataType returnType = DataType.UINT16;
        final String name = "action1";
        final UnsignedInteger8 options = new UnsignedInteger8(3);

        // Test invalid datatype
        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.CATALOG_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                0xff,
                DataHelpers.toBytes(parameterCount)[0],
                DataHelpers.toBytes(options)[0]};

        data = ArrayUtils.addAll(data, DataHelpers.toInts(name));

        try {
            new MessageCatalogResponse(sourceNodeId, data, 0);
            fail();
        } catch (InvalidMessageDataException e) {
            // Expected
        }

        // Test short message
        data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.CATALOG_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                returnType.getByteValue()};

        try {
            new MessageCatalogResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Test no name
        data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.CATALOG_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                returnType.getByteValue(),
                DataHelpers.toBytes(parameterCount)[0],
                DataHelpers.toBytes(options)[0]};

        try {
            new MessageCatalogResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Test no name null terminate
        data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.CATALOG_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                returnType.getByteValue(),
                DataHelpers.toBytes(parameterCount)[0],
                DataHelpers.toBytes(options)[0],
                'a'};

        try {
            new MessageCatalogResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

    }
}
