package org.bithome.test.api.messages;

import com.google.common.collect.Sets;
import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.messages.MessageDataResponse;
import org.bithome.api.messages.MessageDeviceInfoResponse;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.bithome.core.exception.InvalidMessageDataException;
import org.bithome.core.helpers.DataHelpers;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by Mike Kurdziel on 5/23/14.
 */
public class MessageDeviceInfoResponseTest {
    @Test
    public void testCreation()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger16 manufacturerId = new UnsignedInteger16(1);
        final UnsignedInteger8 actionCount = new UnsignedInteger8(2);
        final UnsignedInteger8 interfaceCount = new UnsignedInteger8(2);
        final Set<UnsignedInteger16> interfaces = Sets.newHashSet(
                new UnsignedInteger16(4),
                new UnsignedInteger16(5)
        );

        MessageDeviceInfoResponse message1 = new MessageDeviceInfoResponse(
                sourceNodeId,
                manufacturerId,
                actionCount,
                interfaceCount,
                interfaces);

        assertEquals(sourceNodeId, (long)message1.getSourceNode().get());
        assertEquals(MessageApi.DEVICE_INFO_RESPONSE, message1.getMessageApi());
        assertFalse(message1.getDestNode().isPresent());
        assertEquals(manufacturerId, message1.getManufactuererId());
        assertEquals(actionCount, message1.getActionCount());
        assertEquals(interfaceCount, message1.getInterfaceCount());
        assertEquals(interfaces, message1.getInterfaces());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message1.getTimeStamp()).getSeconds() < 1);

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.DEVICE_INFO_RESPONSE.getByteValue(),
                DataHelpers.toBytes(manufacturerId)[0],
                DataHelpers.toBytes(manufacturerId)[1],
                DataHelpers.toBytes(actionCount)[0],
                DataHelpers.toBytes(interfaceCount)[0],
                DataHelpers.toBytes(new UnsignedInteger16(4))[0],
                DataHelpers.toBytes(new UnsignedInteger16(4))[1],
                DataHelpers.toBytes(new UnsignedInteger16(5))[0],
                DataHelpers.toBytes(new UnsignedInteger16(5))[1]};


        MessageDeviceInfoResponse message2 = new MessageDeviceInfoResponse(sourceNodeId, data, 0);

        assertEquals(message1, message2);
        assertEquals(sourceNodeId, (long)message2.getSourceNode().get());
        assertEquals(MessageApi.DEVICE_INFO_RESPONSE, message2.getMessageApi());
        assertFalse(message2.getDestNode().isPresent());
        assertEquals(manufacturerId, message2.getManufactuererId());
        assertEquals(actionCount, message2.getActionCount());
        assertEquals(interfaceCount, message2.getInterfaceCount());
        assertEquals(interfaces, message2.getInterfaces());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message2.getTimeStamp()).getSeconds() < 1);
    }

    @Test
    public void testErrorReponse()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger16 manufacturerId = new UnsignedInteger16(1);
        final UnsignedInteger8 actionCount = new UnsignedInteger8(2);
        final UnsignedInteger8 interfaceCount = new UnsignedInteger8(2);

        // Test new short message
        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.DEVICE_INFO_RESPONSE.getByteValue(),
                DataHelpers.toBytes(manufacturerId)[0],
                DataHelpers.toBytes(manufacturerId)[1],
                DataHelpers.toBytes(actionCount)[0],
                DataHelpers.toBytes(interfaceCount)[0],
                DataHelpers.toBytes(new UnsignedInteger16(4))[0],
                DataHelpers.toBytes(new UnsignedInteger16(4))[1],
                DataHelpers.toBytes(new UnsignedInteger16(5))[0]};

        try {
            new MessageDeviceInfoResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
}
