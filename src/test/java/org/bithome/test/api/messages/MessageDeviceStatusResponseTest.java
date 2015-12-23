package org.bithome.test.api.messages;

import com.google.common.collect.Sets;
import org.bithome.api.Version;
import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.messages.MessageDeviceInfoResponse;
import org.bithome.api.messages.MessageDeviceStatusResponse;
import org.bithome.api.protocol.DeviceStatus;
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
public class MessageDeviceStatusResponseTest {
    @Test
    public void testCreation()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger8 protocolVersion = new UnsignedInteger8(1);
        final Version revision = new Version(2,3);
        final DeviceStatus deviceStatus = DeviceStatus.ACTIVE;

        MessageDeviceStatusResponse message1 = new MessageDeviceStatusResponse(
                sourceNodeId,
                protocolVersion,
                deviceStatus,
                revision);

        assertEquals(sourceNodeId, (long)message1.getSourceNode().get());
        assertEquals(MessageApi.DEVICE_STATUS_RESPONSE, message1.getMessageApi());
        assertFalse(message1.getDestNode().isPresent());
        assertEquals(protocolVersion, message1.getProtocolVersion());
        assertEquals(revision, message1.getRevision());
        assertEquals(deviceStatus, message1.getDeviceStatus());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message1.getTimeStamp()).getSeconds() < 1);

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.DEVICE_STATUS_RESPONSE.getByteValue(),
                DataHelpers.toBytes(protocolVersion)[0],
                DataHelpers.toBytes((byte)revision.getMajorVersion())[0],
                DataHelpers.toBytes((byte)revision.getMinorVersion())[0],
                DataHelpers.toBytes(deviceStatus.getByteValue())[0]};


        MessageDeviceStatusResponse message2 = new MessageDeviceStatusResponse(sourceNodeId, data, 0);

        assertEquals(message1, message2);
        assertEquals(sourceNodeId, (long)message2.getSourceNode().get());
        assertEquals(MessageApi.DEVICE_STATUS_RESPONSE, message2.getMessageApi());
        assertFalse(message2.getDestNode().isPresent());
        assertEquals(protocolVersion, message2.getProtocolVersion());
        assertEquals(revision, message2.getRevision());
        assertEquals(deviceStatus, message2.getDeviceStatus());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message2.getTimeStamp()).getSeconds() < 1);
    }

    @Test
    public void testErrorReponse()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger8 protocolVersion = new UnsignedInteger8(1);
        final Version revision = new Version(2,3);
        final DeviceStatus deviceStatus = DeviceStatus.ACTIVE;

        // Test too short
        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.DEVICE_STATUS_RESPONSE.getByteValue(),
                DataHelpers.toBytes(protocolVersion)[0],
                DataHelpers.toBytes((byte)revision.getMajorVersion())[0],
                DataHelpers.toBytes((byte)revision.getMinorVersion())[0]};

        try {
            new MessageDeviceStatusResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
}
