package org.bithome.test.api.messages;

import com.google.common.collect.Maps;
import org.apache.commons.lang.ArrayUtils;
import org.bithome.api.Version;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.messages.MessageDeviceStatusResponse;
import org.bithome.api.messages.MessageParameterResponse;
import org.bithome.api.protocol.DataType;
import org.bithome.api.protocol.DeviceStatus;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.bithome.core.exception.InvalidMessageDataException;
import org.bithome.core.helpers.DataHelpers;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by Mike Kurdziel on 5/23/14.
 */
public class MessageParameterResponseTest {
    @Test
    public void testCreation()
            throws InvalidMessageDataException {

        final long sourceNodeId = 1L;
        final UnsignedInteger8 actionIndex = new UnsignedInteger8(1);
        final UnsignedInteger8 parameterIndex = new UnsignedInteger8(2);
        final UnsignedInteger8 options = new UnsignedInteger8(3);
        final DataType dataType = DataType.UINT8;
        final String name = "param1";
        final String maxValue = "50";
        final String minValue = "10";
        final Map<String, Integer> enumValues = Maps.newHashMap();

        MessageParameterResponse message1 = new MessageParameterResponse(
                sourceNodeId,
                actionIndex,
                parameterIndex,
                dataType,
                options,
                name,
                minValue,
                maxValue,
                enumValues);

        assertEquals(sourceNodeId, (long)message1.getSourceNode().get());
        assertEquals(MessageApi.PARAMETER_RESPONSE, message1.getMessageApi());
        assertFalse(message1.getDestNode().isPresent());
        assertEquals(actionIndex, message1.getActionIndex());
        assertEquals(parameterIndex, message1.getParameterIndex());
        assertEquals(options, message1.getOptions());
        assertEquals(dataType, message1.getDataType());
        assertEquals(name, message1.getName());
        assertEquals(maxValue, message1.getMaxValue());
        assertEquals(minValue, message1.getMinValue());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message1.getTimeStamp()).getSeconds() < 1);

        int[] data = new int[]{
                MessageConstants.PACKET_START.getByteValue(),
                MessageApi.PARAMETER_RESPONSE.getByteValue(),
                DataHelpers.toBytes(actionIndex)[0],
                DataHelpers.toBytes(parameterIndex)[0],
                dataType.getByteValue(),
                DataHelpers.toBytes(options)[0]};

        data = ArrayUtils.addAll(data, DataHelpers.toInts(name));
        data = ArrayUtils.add(data, 10);
        data = ArrayUtils.add(data, 50);


        MessageParameterResponse message2 = new MessageParameterResponse(sourceNodeId, data, 0);

        assertEquals(message1, message2);
        assertEquals(sourceNodeId, (long) message2.getSourceNode().get());
        assertEquals(MessageApi.PARAMETER_RESPONSE, message2.getMessageApi());
        assertFalse(message2.getDestNode().isPresent());
        assertEquals(actionIndex, message2.getActionIndex());
        assertEquals(parameterIndex, message2.getParameterIndex());
        assertEquals(options, message2.getOptions());
        assertEquals(dataType, message2.getDataType());
        assertEquals(name, message2.getName());
        assertEquals(maxValue, message2.getMaxValue());
        assertEquals(minValue, message2.getMinValue());
        assertTrue(Seconds.secondsBetween(DateTime.now(), message2.getTimeStamp()).getSeconds() < 1); 
        // TODO: test enum values when implemented
    }
}
