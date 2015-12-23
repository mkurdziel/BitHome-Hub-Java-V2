package org.bithome.api.messages;

import org.bithome.api.Version;
import org.bithome.api.data.UnsignedInteger8;
import org.bithome.api.protocol.DeviceStatus;
import org.bithome.api.protocol.MessageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageDeviceStatusResponse extends MessageRx {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageDeviceStatusResponse.class);

    private final UnsignedInteger8 protocolVersion;
    private final Version revision;
    private final DeviceStatus deviceStatus;

    /**
     * Initialization constructor
     *
     * @param sourceNodeId
     * @param protocolVersion
     * @param revision
     */
    public MessageDeviceStatusResponse(
            final long sourceNodeId,
            final UnsignedInteger8 protocolVersion,
            final DeviceStatus deviceStatus,
            final Version revision) {
        super(sourceNodeId);
        this.protocolVersion = protocolVersion;
        this.revision = revision;
        this.deviceStatus = deviceStatus;

        LOGGER.trace(toString());
    }

    /**
     * Data constructor
     *
     * @param sourceNodeId
     * @param data
     * @param dataOffset
     */
    public MessageDeviceStatusResponse(
            final long sourceNodeId,
            final int[] data,
            final int dataOffset) {
        super(sourceNodeId);

        this.protocolVersion = new UnsignedInteger8(data[dataOffset+2]);
        int majorVersion = data[dataOffset+3];
        int minorVersion = data[dataOffset+4];
        this.revision = new Version(majorVersion, minorVersion);
        this.deviceStatus = DeviceStatus.parse(data[dataOffset+5]);

        LOGGER.trace(toString());
    }

    /**
     * @return the protocol version
     */
    public UnsignedInteger8 getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * @return the device revision
     */
    public Version getRevision() {
        return revision;
    }

    /**
     * @return the device status
     */
    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.DEVICE_STATUS_RESPONSE;
    }

    /**
     * equals override
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDeviceStatusResponse)) return false;

        MessageDeviceStatusResponse that = (MessageDeviceStatusResponse) o;

        if (deviceStatus != that.deviceStatus) return false;
        if (protocolVersion != null ? !protocolVersion.equals(that.protocolVersion) : that.protocolVersion != null)
            return false;
        if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;

        return true;
    }

    /**
     * hashcode override
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = protocolVersion != null ? protocolVersion.hashCode() : 0;
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (deviceStatus != null ? deviceStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageDeviceStatusResponse{" +
                "protocolVersion=" + protocolVersion +
                ", revision=" + revision +
                ", deviceStatus=" + deviceStatus +
                '}';
    }
}

