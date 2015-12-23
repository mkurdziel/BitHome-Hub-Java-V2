package org.bithome.api.messages;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bithome.api.data.UnsignedInteger16;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.protocol.MessageApi;
import org.bithome.core.helpers.DataHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageDeviceInfoResponse extends MessageRx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageDeviceInfoResponse.class);

    private final UnsignedInteger16 manufactuererId;
    private final UnsignedInteger8 actionCount;
    private final UnsignedInteger8 interfaceCount;
    private final Set<UnsignedInteger16> interfaces;

    public MessageDeviceInfoResponse(
            final long sourceNodeId,
            final UnsignedInteger16 manufacturerId,
            final UnsignedInteger8 actionCount,
            final UnsignedInteger8 interfaceCount,
            final Set<UnsignedInteger16> interfaces) {
        super(sourceNodeId);
        this.manufactuererId = manufacturerId;
        this.actionCount = actionCount;
        this.interfaceCount = interfaceCount;
        this.interfaces = Sets.newHashSet(interfaces);

        LOGGER.trace(toString());
    }

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageDeviceInfoResponse(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset) {
        super(sourceNodeId);

        this.manufactuererId = new UnsignedInteger16(DataHelpers.toUInt16(data, dataOffset+2));
        this.actionCount = new UnsignedInteger8(data[dataOffset+4]);
        this.interfaceCount = new UnsignedInteger8(data[dataOffset+5]);
        this.interfaces = Sets.newHashSet();

        // TODO: implement this
        for (int i=0; i<interfaceCount.intValue(); i++) {
            interfaces.add(new UnsignedInteger16(DataHelpers.toUInt16(data, dataOffset + 6 + i + i)));
        }

        LOGGER.trace(toString());
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.DEVICE_INFO_RESPONSE;
    }

    /**
     * @return manufacturer Id
     */
    public UnsignedInteger16 getManufactuererId() {
        return manufactuererId;
    }

    /**
     * @return number of actions
     */
    public UnsignedInteger8 getActionCount() {
        return actionCount;
    }

    /**
     * @return number of interfaces
     */
    public UnsignedInteger8 getInterfaceCount() {
        return interfaceCount;
    }

    /**
     * @return list of interfaces
     */
    public ImmutableSet<UnsignedInteger16> getInterfaces() {
        return ImmutableSet.copyOf(interfaces);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDeviceInfoResponse)) return false;

        MessageDeviceInfoResponse that = (MessageDeviceInfoResponse) o;

        if (actionCount != null ? !actionCount.equals(that.actionCount) : that.actionCount != null) return false;
        if (interfaceCount != null ? !interfaceCount.equals(that.interfaceCount) : that.interfaceCount != null)
            return false;
        if (interfaces != null ? !interfaces.equals(that.interfaces) : that.interfaces != null) return false;
        if (manufactuererId != null ? !manufactuererId.equals(that.manufactuererId) : that.manufactuererId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = manufactuererId != null ? manufactuererId.hashCode() : 0;
        result = 31 * result + (actionCount != null ? actionCount.hashCode() : 0);
        result = 31 * result + (interfaceCount != null ? interfaceCount.hashCode() : 0);
        result = 31 * result + (interfaces != null ? interfaces.hashCode() : 0);
        return result;
    }

    /**
     * @return string description
     */
    @Override
    public String toString() {
        return "MessageDeviceInfoResponse{" +
                "manufactuererId=" + manufactuererId +
                ", actionCount=" + actionCount +
                ", interfaceCount=" + interfaceCount +
                ", interfaces=" + interfaces +
                '}';
    }
}

