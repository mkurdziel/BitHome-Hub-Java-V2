package org.bithome.api.messages;

import com.google.common.primitives.UnsignedInteger;
import org.bithome.api.protocol.BootloadTransmitType;
import org.bithome.api.protocol.MessageApi;
import org.bithome.api.protocol.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a message from the controller to the device
 * regarding the bootloading process
 *
 * Created by Mike Kurdziel on 5/21/14.
 */
public class MessageBootloadTransmit extends MessageTx {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageBootloadTransmit.class);

    private final UnsignedInteger address;
    private final UnsignedInteger checksum;
    private final UnsignedInteger blocksize;
    private final byte[] dataBytes;
    private final BootloadTransmitType bootloadTransmitType;

    /**
     * Initialization constructor
     *
     * @param destinationNodeId
     * @param dataBytes
     * @param address
     * @param checksum
     * @param blocksize
     * @param bootloadTransmitType
     */
    public MessageBootloadTransmit(
            final Long destinationNodeId,
            final byte[] dataBytes,
            final UnsignedInteger address,
            final UnsignedInteger checksum,
            final UnsignedInteger blocksize,
            final BootloadTransmitType bootloadTransmitType) {
        super(destinationNodeId);

        this.dataBytes = dataBytes;
        this.address = address;
        this.checksum = checksum;
        this.blocksize = blocksize;
        this.bootloadTransmitType = bootloadTransmitType;

        LOGGER.trace(toString());
    }

    /**
     * @return string description
     */
    @Override
    public String toString() {
        return "MessageBootloadTransmit{" +
                "blocksize=" + blocksize +
                ", checksum=" + checksum +
                ", address=" + address +
                ", bootloadTransmitType=" + bootloadTransmitType +
                '}';
    }

    /**
     * @return the message API
     */
    @Override
    public MessageApi getMessageApi() {
        return MessageApi.BOOTLOAD_TRANSMIT;
    }

    /**
     * @return the byte array representing this message
     */
    @Override
    public byte[] getBytes() {
        switch (bootloadTransmitType)
        {
            case REBOOT_DEVICE:
            case BOOTLOAD_REQUEST:
            case DATA_COMPLETE:
            {
                byte[] bytes = new byte[3];

                bytes[0] = MessageConstants.PACKET_START.getByteValue();
                bytes[1] = getMessageApi().getByteValue();
                bytes[2] = bootloadTransmitType.getByteValue();

                return bytes;
            }
            case DATA_TRANSMIT:
            {
                byte[] bytes = new byte[7 + dataBytes.length];
                bytes[0] = MessageConstants.PACKET_START.getByteValue();
                bytes[1] = getMessageApi().getByteValue();
                bytes[2] = bootloadTransmitType.getByteValue();
                bytes[3] = (byte) blocksize.intValue();
                bytes[4] = (byte) (address.intValue()>>16);
                bytes[5] = (byte) address.intValue();
                bytes[6] = (byte) checksum.intValue();

                int length = dataBytes.length;
                for (int i = 0; i < length; ++i)
                {
                    bytes[i + 7] = dataBytes[i];
                }

                return bytes;
            }
        }

        return null;
    }
}
