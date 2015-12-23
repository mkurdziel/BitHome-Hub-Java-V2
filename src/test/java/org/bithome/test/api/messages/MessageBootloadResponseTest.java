package org.bithome.test.api.messages;

import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.messages.MessageBootloadResponse;
import org.bithome.api.protocol.BootloadResponseType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.bithome.core.exception.InvalidMessageDataException;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Mike Kurdziel on 5/23/14.
 */
public class MessageBootloadResponseTest {
    @Test
    public void testCreation()
            throws InvalidMessageDataException {
        final long sourceNodeId = 1L;
        final BootloadResponseType responseType = BootloadResponseType.DATA_SUCCESS;
        final UnsignedInteger16 memoryAddress = new UnsignedInteger16(UnsignedInteger16.MAX_VALUE);

        MessageBootloadResponse message1 = new MessageBootloadResponse(sourceNodeId, responseType, memoryAddress);
        assertEquals(sourceNodeId, (long)message1.getSourceNode().get());
        assertEquals(MessageApi.BOOTLOAD_RESPONSE, message1.getMessageApi());
        assertFalse(message1.getDestNode().isPresent());
        assertEquals(responseType, message1.getBootloadResponseType());
        assertEquals(memoryAddress, message1.getMemoryAddress());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message1.getTimeStamp()).getSeconds() < 1);

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.BOOTLOAD_RESPONSE.getByteValue(),
                responseType.getByteValue(),
                0xFF,
                0xFF};

        MessageBootloadResponse message2 = new MessageBootloadResponse(sourceNodeId, data, 0);

        assertEquals(message1, message2);
        assertEquals(sourceNodeId, (long)message2.getSourceNode().get());
        assertEquals(responseType, message2.getBootloadResponseType());
        assertEquals(memoryAddress, message2.getMemoryAddress());

        // Test non success
        final BootloadResponseType responseType2 = BootloadResponseType.BOOTLOAD_COMPLETE;
        data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.BOOTLOAD_RESPONSE.getByteValue(),
                responseType2.getByteValue(),
                0xFF,
                0xFF};

        MessageBootloadResponse message3 = new MessageBootloadResponse(sourceNodeId, data, 0);
        Assert.assertNull(message3.getMemoryAddress());
    }

    @Test
    public void testErrorReponse()
            throws InvalidMessageDataException {
        final long sourceNodeId = 1L;
        final BootloadResponseType responseType = BootloadResponseType.DATA_SUCCESS;

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.BOOTLOAD_RESPONSE.getByteValue(),
                0xFF,
                0xFF,
                0xFF};

        try {
            new MessageBootloadResponse(sourceNodeId, data, 0);
            fail();
        } catch (InvalidMessageDataException e) {
            // Expected
        }

        data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.BOOTLOAD_RESPONSE.getByteValue(),
                responseType.getByteValue(),
                0xFF}; // Try with missing address byte

        try {
            new MessageBootloadResponse(sourceNodeId, data, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testHashCode() {
        final long sourceNodeId = 1L;
        final BootloadResponseType responseType = BootloadResponseType.DATA_SUCCESS;
        final UnsignedInteger16 memoryAddress = new UnsignedInteger16(UnsignedInteger16.MAX_VALUE);

        MessageBootloadResponse message1 = new MessageBootloadResponse(sourceNodeId, responseType, memoryAddress);
        MessageBootloadResponse message2 = new MessageBootloadResponse(sourceNodeId, responseType, memoryAddress);

        Assert.assertTrue(message1.equals(message2) && message2.equals(message1));
        Assert.assertTrue(message1.hashCode() == message2.hashCode());
    }
}
