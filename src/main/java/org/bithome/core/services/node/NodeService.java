package org.bithome.core.services.node;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.lifecycle.Managed;
import org.bithome.api.actions.NodeAction;
import org.bithome.api.node.nodes.BroadcastNode;
import org.bithome.api.node.nodes.Node;
import org.bithome.api.node.NodeInvestigationStatus;
import org.bithome.api.node.NodeUpdateStatus;
import org.bithome.core.exception.NotFoundException;
import org.bithome.core.services.ServiceUtils;
import org.bithome.core.services.storage.StorageService;
import org.bithome.core.util.ManualResetEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public class NodeService implements Managed {
    private final static Logger LOGGER = LoggerFactory.getLogger(NodeService.class);

    private static final int QUERY_INVERVAL_MS = 1000 * 60; // 2 minutes
    private static final int INVESTIGATION_INTERVAL_MS = 100; // 100 ms
    private static final int INVESTIGATION_TIMEOUT_MS = 1000 * 5; // 5 seconds
    private static final int INVESTIGATION_RETRIES = 3; // Retry 3 times

    private final StorageService storageService;

    private int threadWaitMs = QUERY_INVERVAL_MS;

    private boolean isRunning = false;
    private final Map<Long, Node> nodes = Maps.newHashMap();
    private final List<Long> nodesToInvestigate = Lists.newArrayList();
    private final Map<Long, NodeUpdateStatus> updateStatusMap = Maps.newHashMap();

    private final ManualResetEvent resetEventWorkNeeded = new ManualResetEvent(false);
    private final Timer timerNodeRefresh = new Timer();
    private boolean isTimerElapsed = false;
    private NodeServiceThread nodeThread = new NodeServiceThread();
    private boolean isPeriodicCheckEnabled = true;
    private boolean isInvestigating = false;

    private final BroadcastNode broadcastNode = new BroadcastNode();

    /**
     * Constructor
     */
    public NodeService(StorageService storageService) {

        this.storageService = storageService;

        setQueryInterval();

        LOGGER.trace("NodeService created");
    }

    /**
     * Start the node service
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        this.isRunning = true;

        loadDataFromStorage();

        nodeThread.start();

        LOGGER.trace("NodeService started");

    }

    private void scheduleTimer() {
        timerNodeRefresh.schedule(new TimerTask() {
            @Override
            public void run() {
                timerEventOccured();
            }
        }, threadWaitMs);
    }

    /**
     * Stop the node service
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        this.isRunning = false;

        this.resetEventWorkNeeded.set();

        this.timerNodeRefresh.cancel();

        this.nodeThread.wait();

        LOGGER.trace("NodeService stopped");
    }

    /**
     * @return a set of node IDs in the system
     */
    public ImmutableSet<Long> getNodeIds() {
        return ImmutableSet.copyOf(this.nodes.keySet());
    }

    /**
     * @return a set of node objects in the system
     */
    public ImmutableSet<Node> getNodes() {
        return ImmutableSet.copyOf(this.nodes.values());
    }

    /**
     * @param nodeId a desired node ID
     * @return a node object
     */
    public Optional<Node> getNode(long nodeId) {
        if (this.nodes.containsKey(nodeId)) {
            return Optional.of(this.nodes.get(nodeId));
        }
        return Optional.absent();
    }

    /**
     * Load the data from the persistent storage into memory
     */
    private void loadDataFromStorage() {
        LOGGER.info("Loading nodes from storage");
        ImmutableSet<Node> storedNodes = storageService.getNodes();

        for (Node storedNode : storedNodes) {
            LOGGER.debug("Found stored node: {}", storedNode.toString());

            nodes.put(storedNode.getId(), storedNode);
        }
    }

    /**
     * Set the NodeService to run on the query interval so it only checks periodically
     */
    public void setQueryInterval() {
        LOGGER.trace("Switching to query interval");
        this.isInvestigating = false;
        this.threadWaitMs = QUERY_INVERVAL_MS;
    }

    /**
     * Set the NodeService to run on an investigation interval so it checks at a faster pace
     */
    public void setInvestigationInterval() {
        LOGGER.trace("Switching to investigation interval");
        this.isInvestigating = true;
        this.threadWaitMs = INVESTIGATION_INTERVAL_MS;
        // Since the investigate interval is smaller than the query interval,
        // We need to notify the thread to start spinning at the new interval
        resetEventWorkNeeded.set();
    }

    /**
     * @return the investigation interval in milliseconds
     */
    public static int getInvestigationIntervalMs() {
        return INVESTIGATION_INTERVAL_MS;
    }

    /**
     * @return the query interval in milliseconds
     */
    public static int getQueryInvervalMs() {
        return QUERY_INVERVAL_MS;
    }

    public void setNodeAction(long nodeId, int actionIndex, NodeAction action) throws NotFoundException {
        Optional<Node> node = getNode(nodeId);

        if (node.isPresent()) {
            node.get().setNodeAction(actionIndex, action.getId());

            saveNode(node.get());
        } else {
            LOGGER.warn("Setting node action to node not found. NodeId: {}", nodeId);
            throw new NotFoundException();
        }
    }

    private void saveNode(Node node) {
        storageService.saveNode(node);
    }

    private void unsaveNode(Node node) {
        storageService.unsaveNode(node);
    }

    public Node addNode(Node node) {

        // Create the new node and give it a unique ID
        if (node.getId() == null) {
            node.setId( ServiceUtils.getRandomId() );
        }

        LOGGER.debug("Creating node {}", node.getId());

        // Save it in the lookup table
        this.nodes.put(node.getId(), node);

        saveNode(node);

        return node;
    }

    public void removeNode(Node node) throws NotFoundException {

        Long nodeId = node.getId();

        LOGGER.debug("Removing node {}", nodeId);

        // Remove it from investigation if necessary
        if (nodesToInvestigate.contains(nodeId)) {

            removeNodeForInvestigation(node);

            unsaveNode(node);
        }

        if (nodes.containsKey(nodeId)) {
           nodes.remove(nodeId);
        } else {
            LOGGER.warn("Trying to remove node not found. NodeId:{}", nodeId);
            throw new NotFoundException();
        }
    }

    private void addNodeForInvestigation(Node node) {
        synchronized (nodesToInvestigate) {
            if (!nodesToInvestigate.contains(node.getId())) {
                LOGGER.debug("Adding node {} for investigation", node.getId());

                nodesToInvestigate.add(node.getId());

                // ResetInvestigationAttempts(node);

                setInvestigationInterval();
            }
        }
    }

    private void removeNodeForInvestigation(Node node) {
        synchronized(nodesToInvestigate) {
            if (nodesToInvestigate.contains(node.getId())) {
                LOGGER.debug("Removing node {} for investigation", node.getId());

                nodesToInvestigate.remove(node.getId());

                if (nodesToInvestigate.size() == 0) {
                    setQueryInterval();
                }
            }
        }
    }

    private boolean checkIfInvestigationNeeded(Node node) {

        return (node.getInvestigationStatus() == NodeInvestigationStatus.TIMEOUT ||
                node.getInvestigationStatus() == NodeInvestigationStatus.UNKNOWN);
    }

    /**
     * Occurs when the timer has elapsed to set work in motion
     */
    private void timerEventOccured() {
        isTimerElapsed = true;
        resetEventWorkNeeded.set();
    }

    public void reinvestigateNode(Node node) {
        throw new NotImplementedException();
//        log.Trace ("Reinvestigating Node {0}", p_node.Identifier);
//        p_node.Reset ();
//        AddNodeForInvestigation (p_node);
    }

    private void investigateNodes() {
        LOGGER.trace("Investigating Nodes");
        for(Long nodeId : nodesToInvestigate) {
            // TODO: handle missing node
            Node node = nodes.get(nodeId);
            investigateNode(node);
        }
    }

    private void refreshNodes() {
        LOGGER.trace("Refreshing Nodes");
        // TODO: Implement
    }

    private void investigateNode(Node node) {
        // TODO: handle null node
        LOGGER.trace("Investigating node: {}", node.toString());
        throw new NotImplementedException();
    }

    /**
     * Thread to manage the nodes
     */
    private class NodeServiceThread extends Thread {
        private final Logger LOGGER = LoggerFactory.getLogger(NodeServiceThread.class);

        private NodeServiceThread() {
            this.setName("NodeService Thread");
        }

        @Override
        public void run() {
            super.run();
            LOGGER.info("Starting NodeServiceThread");

            // First time, we want to refresh the info to make sure
            // that the version numbers match up
            refreshNodes();

            // Set up the timer
            scheduleTimer();


            try {
                while (isRunning) {
                    LOGGER.trace("Running interval tasks");

                    // See if there are any unknown nodes to investigate
                    // Don't send out the pings if we are investigating
                    if (isInvestigating) {
                        investigateNodes();
                    } else if (isPeriodicCheckEnabled) {

                        // Send out the periodic if the timer has elapsed
                        if (isTimerElapsed)
                        {
                            refreshNodes();
                        }
                    }

                    // See if there is anything to be updated
                    checkNodesForUpdate();

                    // Wait for the next one
                    if (isRunning)
                    {
                        // reset our expired indicator
                        isTimerElapsed = false;
                        // Set to the current interval
                        scheduleTimer();

                        // Wait if necessary
                        resetEventWorkNeeded.reset();
                        resetEventWorkNeeded.waitOne();


                        // Catch this in case we were woken up to exit
                        if (!isRunning) {
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.warn("NodeService Thread interrupted", e);
            }
        }
    }

    private void checkNodesForUpdate() {
        LOGGER.trace("Checking nodes for update");
        // TODO: implement
    }
}

//private void InvestigateNode(Node p_node)
//        {
//        DateTime nextInvestigation = p_node.TimeNextInvestigation;
//
//        // Check to see if we've already retried too many times
//        if (p_node.InvestigationRetries == INVESTIGATION_RETRIES)
//        {
//        log.Info ("Investigating node {0} has timed out", p_node.Identifier);
//
//        p_node.InvestigationStatus = NodeInvestigationStatus.Timeout;
//
//        RemoveNodeForInvestigation(p_node);
//        }
//        // First check to see that the current time is
//        // beyond the next investigation checkpoint
//        else if (nextInvestigation < DateTime.Now)
//        {
//        log.Debug("Investigating:{0} function:{1} now:{2} next:{3}", p_node.Identifier, p_node.NextUnknownAction, DateTime.Now, p_node.TimeNextInvestigation);
//
//        switch(p_node.InvestigationStatus)
//        {
//        // If unknown, we need to get some information
//        case NodeInvestigationStatus.Unknown:
//        case NodeInvestigationStatus.Timeout:
//        {
//        log.Debug ("Investigating Status for {0}", p_node.Identifier);
//
//        MessageDeviceStatusRequest msg = new MessageDeviceStatusRequest ();
//
//        ServiceManager.MessageDispatcherService.SendMessage (msg, p_node);
//        }
//        break;
//        case NodeInvestigationStatus.Status:
//        {
//        log.Debug("Investigating INFO for {0}", p_node.Identifier);
//
//        MessageDeviceInfoRequest msg = new MessageDeviceInfoRequest ();
//
//        ServiceManager.MessageDispatcherService.SendMessage (msg, p_node);
//        }
//        break;
//        // Query until we have all the functions
//        case NodeInvestigationStatus.Info:
//        case NodeInvestigationStatus.Action:
//        {
//        int action = p_node.NextUnknownAction;
//
//        if (action != -1)
//        {
//        log.Debug("Investigating ACTION {0} for {1}", action, p_node.Identifier);
//
//        // TODO avoid conversion
//        MessageCatalogRequest msg = new MessageCatalogRequest ((byte)action);
//
//        ServiceManager.MessageDispatcherService.SendMessage (msg, p_node);
//        }
//        }
//        break;
//        // Query until we have all the parameters
//        case NodeInvestigationStatus.Parameter:
//        {
//        Tuple<byte, byte> pair = p_node.NextUnknownParameter;
//
//        if (pair != null)
//        {
//        log.Debug ("Investigating PARAMETER {0} : {1} for {2}", pair.Item1, pair.Item2, p_node.Identifier);
//
//        MessageParameterRequest msg = new MessageParameterRequest (pair.Item1, pair.Item2);
//
//        ServiceManager.MessageDispatcherService.SendMessage (msg, p_node);
//        }
//        }
//        break;
//default:
//        {
//        log.Warn ("An investigation attept is made in an unimplemented state: {0} for {1}", p_node.InvestigationStatus, p_node.Identifier);
//        }
//        break;
//        }
//
//        if(p_node.InvestigationStatus == NodeInvestigationStatus.Completed)
//        {
//        RemoveNodeForInvestigation(p_node);
//
//        } else {
//        p_node.InvestigationRetries++;
//        p_node.TimeNextInvestigation = p_node.TimeNextInvestigation.AddMilliseconds(INVESTIGATION_TIMEOUT_MS);
//        log.Debug("Setting next investigation time for {0} funtion:{1} to {2}", p_node.Identifier,  p_node.NextUnknownAction, p_node.TimeNextInvestigation);
//        }
//        }
//
//        SaveNode (p_node);
//        }
//
//private void ResetInvestigationAttempts(Node p_node)
//        {
//        log.Debug ("Resetting investigation attempts on node {0}", p_node.Id);
//
//        p_node.TimeNextInvestigation = DateTime.Now;
//        p_node.InvestigationRetries = 0;
//        }
//
//private void CheckForInvestigation(Node p_node) {
//        if (p_node.InvestigationStatus != NodeInvestigationStatus.Completed &&
//        !m_nodesToInvestigate.Contains(p_node)) {
//
//        log.Trace ("{0} needs investigating", p_node.Id);
//
//        AddNodeForInvestigation (p_node);
//        }
//        }
//
//private void CheckNodesForUpdate()
//        {
//        // See if there is anything in the map to be updated
//        if (m_updateStatusMap.IsEmpty() == false) {
//        foreach (String key in m_updateStatusMap.Keys) {
//        NodeUpdateStatus status = m_updateStatusMap [key];
//
//        // If the status is unknown, it hasn't been started
//        // Ininitiate the reboot
//        if (status.getStatus () == NodeBootloadStatus.UNKNOWN) {
//        SendRebootRequest (status.getNode ());
//
//        status.setStatus (NodeBootloadStatus.RESET);
//        } else if (status.getTimeNextUpdate () < DateTime.Now) {
//        log.Info ("Update {0} timed out. Resending Update", key);
//        SendNextUpdate(status.getNode ());
//        }
//        }
//        }
//        }
//
//private void ResetUpdateAttempts(Node p_node)
//        {
//        if (m_updateStatusMap.ContainsKey (p_node.Id)) {
//        log.Debug ("Resetting update attempts on node {0}", p_node.Id);
//
//        NodeUpdateStatus status = m_updateStatusMap [p_node.Id];
//        status.setTimeNextUpdate(DateTime.Now + TimeSpan.FromMilliseconds(INVESTIGATION_TIMEOUT_MS));
//        status.setNumRetries (0);
//        }
//        }
//
//
//        #region Node Management Methods
//
//public void UpdateNode(Node p_node, String p_updateFile)
//        {
//        log.Info("Updating node {0} with file {1}", p_node.Identifier, p_updateFile);
//
//        NodeUpdateFile file = new NodeUpdateFile(p_updateFile);
//
////			if (file.parseFile())
////			{
////				Logger.i(TAG, "Success parsing node update file");
////				// Create the update status, hash it, and wake up the
////				// node manager to handle this
////				NodeUpdateStatus status = new NodeUpdateStatus(p_node, file);
////
////				// If the node is unknown and has only reported a HW reset,
////				// this may be an initial load. Skip the reset and
////				// go straight to waiting for confirmation
////				if (p_node.getInvestigationStatus()== EsnInvestigationStatusEnum.UNKNOWN)
////				{
////					Logger.i(TAG, "Unknown node. Could be an initial software load. Skipping reset");
////					status.setStatus(BootloadStatusEnum.BOOTLOAD_REQUEST);
////				}
////				// Hash this so the node manage thread can pick it up
////				m_updateStatusMap.put(p_node.getNodeId(), status);
////
////				// Change the query interval
////				setInvestigateInterval();
////
////				m_nodeManagerThread.notifyThread();
////
////			}
////			else
////			{
////			log.Warn("Unable to parse update file {)] for node {1}", p_updateFile, p__node.Identifier);
////			}
//
//        }
//
//private void RefreshNodesInfos() {
//        log.Debug ("Refreshing Node Infos");
//
//        ServiceManager.MessageDispatcherService.BroadcastMessage (
//        new MessageDeviceStatusRequest()
//        );
//        }
//
//private void RefreshNodes() {
//        log.Debug ("Refreshing Nodes");
//
//        ServiceManager.MessageDispatcherService.BroadcastMessage (
//        new MessageDeviceStatusRequest()
//        );
//        }
//
//        #endregion
//
//
//        #region Message Processing Methods
//
//private void ProcessMessageDeviceStatus(MessageDeviceStatusResponse p_msg)
//        {
//        // Since we've seen the node, update it's last seen time
//        p_msg.SourceNode.LastSeen = DateTime.Now;
//
//
//        // Handle the types of status messages
//        switch (p_msg.DeviceStatus)
//        {
//        case DeviceStatusValue.ACTIVE:
//        ProcessStatusActive(p_msg.SourceNode);
//        break;
//        case DeviceStatusValue.HW_RESET:
//        ProcessStatusHwReset(p_msg.SourceNode);
//        break;
//        }
//
//        if (p_msg.SourceNode.InvestigationStatus == NodeInvestigationStatus.Unknown) {
//        p_msg.SourceNode.InvestigationStatus = NodeInvestigationStatus.Status;
//        }
//
//        // Check to see if the version is newer
//        if (p_msg.Revision > p_msg.SourceNode.Revision) {
//        // Set the version and add for further investigation
//        p_msg.SourceNode.Revision = p_msg.Revision;
//
//        AddNodeForInvestigation (p_msg.SourceNode);
//        }
//        }
//
//private void ProcessMessageDeviceInfo(MessageDeviceInfoResponse p_msg)
//        {
//        // Since we've seen the node, update it's last seen time
//        p_msg.SourceNode.LastSeen = DateTime.Now;
//
//        ProcessStatusInfo(p_msg);
//        }
//
//private void ProcessMessageCatalogResponse(MessageCatalogResponse p_msg)
//        {
//        Node node = p_msg.SourceNode;
//
//        log.Trace ("Adding node action {0} to node {1}", p_msg.ActionIndex, node.Identifier);
//        log.Trace (p_msg.ToString);
//
//
//        ServiceManager.ActionService.AddNodeAction (
//        node,
//        p_msg.ActionIndex,
//        p_msg.Name,
//        p_msg.ReturnType,
//        p_msg.ParameterCount);
//
//
//        // If we have all the functions, increment the investigation status
//        if(node.NextUnknownAction == -1)
//        {
//        log.Trace ("No more unknown actions for {0}", node.Identifier);
//
//        node.InvestigationStatus = NodeInvestigationStatus.Parameter;
//        }
//        else
//        {
//        node.InvestigationStatus = NodeInvestigationStatus.Action;
//        log.Trace ("Next unknown action for {0} is {1}", node.Identifier, node.NextUnknownAction);
//        }
//
//        ResetInvestigationAttempts(node);
//
//        SaveNode (node);
//        }
//
//private void ProcessMessageParameterResponse(MessageParameterResponse p_msg)
//        {
//        Node node = p_msg.SourceNode;
//        log.Trace ("Processing: {0}", p_msg.ToString ());
//        log.Trace ("Adding parameter to {0} {1}:{2}", node.Identifier, p_msg.ActionIndex, p_msg.ParameterIndex);
//
//        ServiceManager.ActionService.AddNodeParameter (
//        node,
//        p_msg.ActionIndex,
//        p_msg.ParameterIndex,
//        p_msg.Name,
//        p_msg.DataType,
//        p_msg.MaxValue,
//        p_msg.MinValue,
//        p_msg.EnumValues
//        );
//
////			Logger.v(TAG, parameter.getDescription());
//
//        // If we have all the parameters for this function, send a notification
//        //		if(node.getNextUnknownParameter(parameter.getFunctionId()) == 0)
//        //		{
//        //			fireNodeFunctionAdded(node.getFunction(parameter.getFunctionId()));
//        //		}
//
//        // If we have all the functions, increment the investigation status
//        if(node.NextUnknownParameter == null)
//        {
//        node.InvestigationStatus = NodeInvestigationStatus.Completed;
//        node.IsUnknown = false;
//        }
//        else
//        {
//        Tuple<byte, byte> actionPair = node.NextUnknownParameter;
//
//        log.Trace ("Next unknown parameter for {0} is {1}:{2}", node.Identifier, actionPair.Item1, actionPair.Item2);
//        }
//
//        ResetInvestigationAttempts(node);
//
//        SaveNode (node);
//        }
//
//private void ProcessStatusHwReset(Node p_node)
//        {
//        log.Debug ("Node {0} hardware reset", p_node.Identifier);
//
//        // First we check to see if this is a HW reset to start
//        // a software update
//        if (m_updateStatusMap.ContainsKey(p_node.Id))
//        {
//        NodeUpdateStatus status = m_updateStatusMap[p_node.Id];
//
//        // If this device is in the reset state, then we are on track
//        if (status.getStatus() == NodeBootloadStatus.RESET)
//        {
//        // We have the proper restart, now we will send the request
//        status.setStatus(NodeBootloadStatus.BOOTLOAD_REQUEST);
//
//        SendNextUpdate(p_node);
//        }
//        // If this device is in the reset again, keep sending bootload requests
//        else if (status.getStatus() == NodeBootloadStatus.BOOTLOAD_REQUEST)
//        {
//        log.Debug ("Node {0} restarted unexpectidly. Sending another bootlaod request", p_node.Identifier);
//
//        SendNextUpdate(p_node);
//        }
//        else
//        {
//        log.Warn ("Node {0} rebooted without an update complete confirmation", p_node.Identifier);
//
//        m_updateStatusMap.Remove(p_node.Id);
//        }
//        }
//        else
//        {
//        // let's change the investigation status to unknown since the firmware
//        // and catalog may have changed during the reboot;
//        p_node.InvestigationStatus = NodeInvestigationStatus.Unknown;
//        }
//
//        SaveNode (p_node);
//        }
//
//private void ProcessStatusActive(Node p_node)
//        {
//        log.Debug ("Node {0} is ACTIVE", p_node.Identifier);
//
//        CheckForInvestigation(p_node);
//        }
//
//private void ProcessStatusInfo(MessageDeviceInfoResponse p_msg)
//        {
//        // Populate the node information
//        Node node = p_msg.SourceNode;
//
//        log.Debug ("Processing status info for node {0}", node.Identifier);
//        log.Trace (p_msg.ToString ());
//
//        if (node.IsBeingInvestigated)
//        {
//        node.TotalNumberOfActions = p_msg.ActionCount;
//        node.InvestigationStatus = NodeInvestigationStatus.Info;
//        }
//
//        ResetInvestigationAttempts(node);
//
//        SaveNode (node);
//        }
//
//        #endregion
//
//        #region Message Sending Methods
//
//public void SendUpdateRequest(Node p_node)
//        {
//        log.Info ("Requesting update for node {0}", p_node.Identifier);
//
////			MsgBootloadTransmit msg = new MsgBootloadTransmit(p_node, EsnAPIBootloadTransmit.BOOTLOAD_REQUEST);
////			m_msgDispatcher.sendMessage(msg);
//        }
//
//private void SendRebootRequest(Node p_node)
//        {
//        log.Info("Rebooting node {0}", p_node.Identifier);
//        MessageBootloadTransmit msg = new MessageBootloadTransmit(BootloadTransmitType.REBOOT_DEVICE);
//        ServiceManager.MessageDispatcherService.SendMessage(msg, p_node);
//        }
//
//
//private void SendNextUpdate(Node p_node)
//        {
//        }
//
//
//        #endregion
//
//        #region Persistence Methods
//
//private void SaveNodeList() {
//        StorageService.Store<String[]>.Insert (KEY_NODES, this.NodeIds);
//        }
//
//private void SaveNode(Node node) {
//        StorageService.Store<Node>.Insert (node.Id, node);
//        }
//
//private void UnSaveNode(Node node) {
//        StorageService.Store<Node>.Remove (node.Id);
//        }
//
//
//public void WaitFinishSaving ()
//        {
//        StorageService.Store<String[]>.WaitForCompletion ();
//        StorageService.Store<Node>.WaitForCompletion ();
//        }
//        #endregion
//
////		public void AddMsgBootloadResponse(MsgBootloadResponse p_msg)
////		{
////			NodeBase node = p_msg.getSourceNode();
////
////			// First lets get the update status for this node
////			if (m_updateStatusMap.containsKey(node.getNodeId()))
////			{
////				NodeUpdateStatus status = m_updateStatusMap.get(node.getNodeId());
////
////				switch(p_msg.getBootloadResponse())
////				{
////					case BOOTLOAD_READY:
////				{
////					Logger.v(TAG, "node is ready to update. Starting update.");
////					status.setStatus(BootloadStatusEnum.DATA_TRANSMIT);
////
////				}
////					break;
////					case DATA_SUCCESS:
////				{
////					Logger.v(TAG, String.format("Node update data success. %s Address: %x", node.getDescString(), p_msg.getMemoryAddress()));
////					status.markAsSent(p_msg.getMemoryAddress());
////				}
////					break;
////					case BOOTLOAD_COMPLETE:
////				{
////					Logger.v(TAG, "Node update complete: " + node.getDescString());
////					m_updateStatusMap.remove(node.getNodeId());
////					setQueryInterval();
////				}
////					break;
////					case ERROR_ADDRESS:
////					case ERROR_API:
////					case ERROR_BOOTLOADAPI:
////					case ERROR_BOOTLOADSTART:
////					case ERROR_CHECKSUM:
////					case ERROR_MY16_ADDR:
////					case ERROR_PAGELENGTH:
////					case ERROR_SIZE:
////					case ERROR_SNAPI:
////					case ERROR_SNSTART:
////					case ERROR_START_BIT:
////				{
////					Logger.w(TAG, "Received error from updating device " + node.getDescString() + " " + p_msg.getBootloadResponse());
////				}
////					break;
////				}
////
////				// We received an update message to reset the update attempts
////				resetUpdateAttempts(node);
////
////				// Send the next update if not complete
////				if(p_msg.getBootloadResponse() != EsnAPIBootloadResponse.BOOTLOAD_COMPLETE)
////				{
////
////					sendNextUpdate(node, status);
////				}
////			}
////			else
////			{
////				Logger.e(TAG, "received a bootload message when there is no bootload status");
////				// Just reboot the node
////				rebootNode(node);
////			}
////		}
//
////		/**
////     * Send the next update to the node
////     *
////     * @param p_node
////     * @param p_status
////     */
////		private void sendNextUpdate(NodeBase p_node, NodeUpdateStatus p_status)
////		{
////			switch(p_status.getStatus())
////			{
////				case RESET:
////				case BOOTLOAD_REQUEST:
////			{
////				Logger.i(TAG, p_node.getDescString() + "Sending bootload request");
////				sendUpdateRequest(p_node);
////			}
////				break;
////				case DATA_TRANSMIT:
////			{
////				if (p_status.isComplete())
////				{
////					sendUpdateDataComplete(p_node);
////				}
////				else
////				{
////					sendUpdateDataNext(p_node, p_status);
////				}
////			}
////				break;
////				case UNKNOWN:
////			{
////				Logger.w(TAG, "send next update for unknown status state");
////			}
////				break;
////			}
////
////			p_status.setNumRetries(p_status.getNumRetries()+1);
////			p_status.setTimeNextUpdate(p_status.getTimeNextUpdate().plus(C_INVESTIGATE_TIMEOUT));
////			Logger.v(TAG, "setting next update time for " + p_node.getDescString() + " to " + p_status.getTimeNextUpdate());
////		}
////
////		/**
////     * Tell the node that the update has completed
////     *
////     * @param p_node
////     */
////		private void sendUpdateDataComplete(NodeBase p_node)
////		{
////			Logger.v(TAG, String.format("Sending update complete to %s", p_node.getDescString()));
////			MsgBootloadTransmit msg = new MsgBootloadTransmit(p_node, EsnAPIBootloadTransmit.DATA_COMPLETE);
////			m_msgDispatcher.sendMessage(msg);
////		}
////
////		/**
////     * Send the next chunk of update data
////     * @param p_status
////     */
////		private void sendUpdateDataNext(NodeBase p_node, NodeUpdateStatus p_status)
////		{
////			int nextAddress = p_status.getNextAddress();
////
////			byte[] dataBytes = new byte[p_node.getCodeUpdatePageSize()];
////
////			int checksum = p_node.getCodeUpdatePageSize();
////			checksum += (nextAddress>>8);
////			checksum += (nextAddress & 0xff);
////			for (int i=0; i<p_node.getCodeUpdatePageSize(); ++i)
////			{
////				dataBytes[i] = p_status.getDataByte(nextAddress + i);
////				checksum += dataBytes[i];
////			}
////
////			// Now reduce check_sum to 8 bits
////			while (checksum > 256)
////				checksum -= 256;
////
////			// now take the two's compliment
////			checksum = 256 - checksum;
////
////			Logger.v(TAG, String.format("Sending update address %x to %s", nextAddress, p_node.getDescString()));
////			MsgBootloadTransmit msg = new MsgBootloadTransmit(
////				p_node,
////				EsnAPIBootloadTransmit.DATA_TRANSMIT,
////				dataBytes,
////				nextAddress,
////				checksum,
////				p_node.getCodeUpdatePageSize()
////				);
////			m_msgDispatcher.sendMessage(msg);
////		}
////
//

//        void OnMessageRecievedEvent (object sender, Messaging.MessageRecievedEventArgs e)
//        {
//        switch (e.Message.Api) {
//        case Messaging.Protocol.Api.DEVICE_STATUS_RESPONSE:
//        ProcessMessageDeviceStatus ((MessageDeviceStatusResponse)e.Message);
//        break;
//        case Messaging.Protocol.Api.DEVICE_INFO_RESPONSE:
//        ProcessMessageDeviceInfo ((MessageDeviceInfoResponse)e.Message);
//        break;
//        case Messaging.Protocol.Api.CATALOG_RESPONSE:
//        ProcessMessageCatalogResponse ((MessageCatalogResponse)e.Message);
//        break;
//        case Messaging.Protocol.Api.PARAMETER_RESPONSE:
//        ProcessMessageParameterResponse ((MessageParameterResponse)e.Message);
//        break;
//        }
//        }
//
//public void SetNodeName(string id, string name)
//        {
//        Node node = GetNode(id);
//        if (node!= null)
//        {
//        node.Name = name;
//        }
//        }
//
//public void RebootNode(string id)
//        {
//        Node node = GetNode(id);
//        if (node != null)
//        {
//        SendRebootRequest(node);
//        }
//        }
//        }
//        }
//

