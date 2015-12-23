package org.bithome.api.node.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.bithome.api.Version;
import org.bithome.api.node.NodeInvestigationStatus;
import org.bithome.api.node.NodeStatus;
import org.bithome.api.node.NodeType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Mike Kurdziel on 5/19/14.
 */
public abstract class Node implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(Node.class);

    private final static String UNKNOWN_NAME_STR = "Unknown Node";
    // Number of minutes since we've seen the node that it is considered active
    private final static int NODE_ACTIVE_MIN = 5;
    // Number of minutes since we've seen the node that it is considered recent
    private final static int NODE_RECENT_MIN = 60;

    // Internal identifier
    private Long id;
    // The ID of the manufacturer of this device
    private Integer manufacturerId;
    // The revision of the software on this device
    private Version revision;
    // The type of node this is
    private NodeType nodeType;

    // Human-readable node name. User assignable
    private String name = UNKNOWN_NAME_STR;

    // The datetime this node was discovered by the controller
    private DateTime dateCreated = DateTime.now();
    // The last time the node communicated with the controller
    private DateTime lastSeen = DateTime.now();
    // The current status of the controllers investigation of the node
    private NodeInvestigationStatus investigationStatus = NodeInvestigationStatus.UNKNOWN;

    // List of interfaces that this node implements
    private List<Integer> interfaces = Lists.newArrayList();
    // Lookup table of actionsIds belonging to this node
    private Map<Integer, Long> actionsIds = Maps.newHashMap();

    // Number of actionsIds the catalog is supposed to have
    private Optional<Integer> catalogTotalNumActions = Optional.absent();

    /**
     * Copy constructor
     *
     * @param other
     */
    public Node(final Node other) {
        this.id = other.id;
        this.manufacturerId = other.manufacturerId;
        this.revision = other.revision;
        this.nodeType = other.nodeType;
        this.name = other.name;
        this.dateCreated = other.dateCreated;
        this.lastSeen = other.lastSeen;
        this.investigationStatus = other.investigationStatus;
        this.interfaces = Lists.newArrayList(other.interfaces);
        this.actionsIds = Maps.newHashMap(other.actionsIds);
        this.catalogTotalNumActions = other.catalogTotalNumActions;
    }

    /**
     * Full initizliation constructor
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
     */
    public Node(
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
            final Optional<Integer> catalogTotalNumActions) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.revision = revision;
        this.nodeType = nodeType;
        this.name = name;
        this.dateCreated = dateCreated;
        this.lastSeen = lastSeen;
        this.investigationStatus = investigationStatus;
        this.interfaces = interfaces;
        this.actionsIds = actionsIds;
        this.catalogTotalNumActions = catalogTotalNumActions;
    }

    protected Node() {
    }

    @JsonProperty
    public Long getId() {
        return id;
    }

    @JsonProperty
    public void setId(Long id) {
        this.id = id;
    }

    public ImmutableMap<Integer, Long> getActionsIds() {
        return ImmutableMap.copyOf(actionsIds);
    }

    public ImmutableList<Integer> getInterfaces() {
        return ImmutableList.copyOf(interfaces);
    }

    public void setInterfaces(List<Integer> interfaces) {
        this.interfaces = interfaces;
    }

    public NodeInvestigationStatus getInvestigationStatus() {
        return investigationStatus;
    }

    public void setInvestigationStatus(NodeInvestigationStatus investigationStatus) {
        this.investigationStatus = investigationStatus;
    }

    public DateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(DateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Version getRevision() {
        return revision;
    }

    public void setRevision(Version revision) {
        this.revision = revision;
    }

    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Integer manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Optional<Integer> getCatalogTotalNumActions() {
        return catalogTotalNumActions;
    }

    public void setCatalogTotalNumActions(int catalogTotalNumActions) {
        this.catalogTotalNumActions = Optional.of(catalogTotalNumActions);
    }

    /**
     * @return the status of the node based on the last time it communicated.
     */
    public NodeStatus getNodeStatus() {
        if (this.investigationStatus == NodeInvestigationStatus.UNKNOWN) {
            return NodeStatus.UNKNOWN;
        }

        if (this.lastSeen.isAfter(DateTime.now().minusMinutes(NODE_ACTIVE_MIN))) {
            return NodeStatus.ACTIVE;
        }

        if (this.lastSeen.isAfter(DateTime.now().minusMinutes(NODE_RECENT_MIN))) {
            return NodeStatus.RECENT;
        }

        return NodeStatus.DEAD;
    }

    /**
     * @return the next unknown action for this node.
     * Returns absent if there are no more unknown actionsIds. Returns null if it can't be determined.
     */
    public Optional<Integer> getNextUnknownAction() {
        if (catalogTotalNumActions.isPresent()) {
            for (int i=0; i< catalogTotalNumActions.get(); ++i) {
                if (actionsIds.containsKey(i) == false) {
                    return Optional.of(i);
                }
            }
        } else {
            return null;
        }
        return Optional.absent();
    }

    /**
     * @return the next unknown parameter for this node, based on the actionsIds we have already discovered.
     * Returns absent if there are no more unknown params. Returns null if it can't be determined.
     */
    public Optional<Pair<Integer, Integer>> getNextUnknownParameter() {
        throw new NotImplementedException();
//        if (catalogTotalNumActions.isPresent()) {
//            Optional<Pair<Integer, Integer>> parameterPair;
//            for (int i=0; i< catalogTotalNumActions.get(); ++i) {
//                if (actionsIds.containsKey(i)) {
//                    parameterPair = actionsIds.get(i).getNextUnknownParameter();
//
//                    if (parameterPair.isPresent()) {
//                        return parameterPair;
//                    }
//                }
//            }
//        } else {
//            return null;
//        }
//        return Optional.absent();
    }

    public Optional<Long> getActionId(final int index) {
        if (this.actionsIds.containsKey(index)) {
            return Optional.of(this.actionsIds.get(index));
        }
        return Optional.absent();
    }

    public void setNodeAction(final int index, final long actionId) {
        throw new NotImplementedException();
//        log.Info ("Node:{0} adding node action:{1}", Identifier, actionIndex);
//
//        if (Actions.ContainsKey (actionIndex)) {
//            log.Warn ("Node:{0} replacing node action:{1}", Identifier, actionIndex);
//
//            Actions.Remove (actionIndex);
//        }
//
//        Actions.Add (actionIndex, actionId);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", name=" + name +
                ", manufacturerId=" + manufacturerId +
                ", revision=" + revision +
                ", nodeType=" + nodeType +
                ", lastSeen=" + lastSeen +
                ", investigationStatus=" + investigationStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (actionsIds != null ? !actionsIds.equals(node.actionsIds) : node.actionsIds != null) return false;
        if (catalogTotalNumActions != null ? !catalogTotalNumActions.equals(node.catalogTotalNumActions) : node.catalogTotalNumActions != null)
            return false;
        if (dateCreated != null ? !dateCreated.equals(node.dateCreated) : node.dateCreated != null) return false;
        if (id != null ? !id.equals(node.id) : node.id != null) return false;
        if (interfaces != null ? !interfaces.equals(node.interfaces) : node.interfaces != null) return false;
        if (investigationStatus != node.investigationStatus) return false;
        if (lastSeen != null ? !lastSeen.equals(node.lastSeen) : node.lastSeen != null) return false;
        if (manufacturerId != null ? !manufacturerId.equals(node.manufacturerId) : node.manufacturerId != null)
            return false;
        if (name != null ? !name.equals(node.name) : node.name != null) return false;
        if (nodeType != node.nodeType) return false;
        if (revision != null ? !revision.equals(node.revision) : node.revision != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (manufacturerId != null ? manufacturerId.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (lastSeen != null ? lastSeen.hashCode() : 0);
        result = 31 * result + (investigationStatus != null ? investigationStatus.hashCode() : 0);
        result = 31 * result + (interfaces != null ? interfaces.hashCode() : 0);
        result = 31 * result + (actionsIds != null ? actionsIds.hashCode() : 0);
        result = 31 * result + (catalogTotalNumActions != null ? catalogTotalNumActions.hashCode() : 0);
        return result;
    }

    /**
     * Clone this object
     *
     * @return
     */
    public abstract Node clone();
}
