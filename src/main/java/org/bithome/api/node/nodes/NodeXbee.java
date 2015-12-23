package org.bithome.api.node.nodes;

import com.google.common.base.Optional;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.bithome.api.Version;
import org.bithome.api.node.NodeInvestigationStatus;
import org.bithome.api.node.NodeType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public class NodeXbee extends Node {
    private final static Logger LOGGER = LoggerFactory.getLogger(Node.class);

    private UnsignedLong address64;
    private UnsignedInteger address16;

    /**
     * Full Initialization Constructor
     *
     * @param id
     * @param manufacturerId
     * @param revision
     * @param nodeType
     * @param name
     * @param dateCreated
     * @param lastSeen
     * @param investigationStatus
     * @param interfaces
     * @param actionsIds
     * @param catalogTotalNumActions
     * @param address64
     * @param address16
     */
    public NodeXbee(
            final Long id,
            final Integer manufacturerId,
            final Version revision,
            final NodeType nodeType,
            final String name,
            final DateTime dateCreated,
            final DateTime lastSeen,
            final NodeInvestigationStatus investigationStatus,
            final List<Integer> interfaces,
            final Map<Integer, Long> actionsIds,
            final Optional<Integer> catalogTotalNumActions,
            final UnsignedLong address64,
            final UnsignedInteger address16) {
        super(id, manufacturerId, revision, nodeType, name, dateCreated, lastSeen, investigationStatus,
                interfaces, actionsIds, catalogTotalNumActions);

        this.address64 = address64;
        this.address16 = address16;
    }

    /**
     * Copy constructor
     *
     * @param other
     */
    public NodeXbee(NodeXbee other) {
        super(other);

        this.address64 = other.address64;
        this.address16 = other.address16;
    }

    public String getAddress64String() {
        return String.format("0x%s", address64.toString());
    }

    public String getAddress16String() {
        return String.format("0x%s", address16.toString());
    }

    public NodeXbee(UnsignedLong address64, UnsignedInteger address16)
    {
        this.address16 =address16;
        this.address64 = address64;

        LOGGER.trace("(64={} 16={})", address64, address16);
    }

    public UnsignedLong getAddress64() {
        return address64;
    }

    public UnsignedInteger getAddress16() {
        return address16;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeXbee)) return false;
        if (!super.equals(o)) return false;

        NodeXbee nodeXbee = (NodeXbee) o;

        if (address16 != null ? !address16.equals(nodeXbee.address16) : nodeXbee.address16 != null) return false;
        if (address64 != null ? !address64.equals(nodeXbee.address64) : nodeXbee.address64 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (address64 != null ? address64.hashCode() : 0);
        result = 31 * result + (address16 != null ? address16.hashCode() : 0);
        return result;
    }

    @Override
    public NodeXbee clone() {
        return new NodeXbee(this);
    }
}
