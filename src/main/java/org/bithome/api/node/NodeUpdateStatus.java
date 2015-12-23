package org.bithome.api.node;

import org.bithome.api.node.nodes.Node;
import org.joda.time.DateTime;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public class NodeUpdateStatus {

    private NodeUpdateFile updateFile;
    private Node node;
//    private int m_nextAddress = 0;
//    private NodeBootloadStatus m_statusEnum = NodeBootloadStatus.UNKNOWN;
//    private DateTime m_timeNextInvestigation;
//    private int m_numInvestigationRetries = 0;

    public NodeUpdateStatus(Node node, NodeUpdateFile updateFile)
    {
        this.updateFile = updateFile;
        node = node;
    }

    public void setNumRetries(int p_retries)
    {
        throw new NotImplementedException();
//        m_numInvestigationRetries = p_retries;
    }

    public int getNumRetries()
    {
        throw new NotImplementedException();
//        return m_numInvestigationRetries;
    }

    public void setTimeNextUpdate(DateTime p_time)
    {
        throw new NotImplementedException();
//        m_timeNextInvestigation = p_time;
    }

    public DateTime getTimeNextUpdate()
    {
        throw new NotImplementedException();
//        return m_timeNextInvestigation;
    }

    public NodeBootloadStatus getStatus()
    {
        throw new NotImplementedException();
//        return m_statusEnum;
    }

    public void setStatus(NodeBootloadStatus p_status)
    {
        throw new NotImplementedException();
//        m_statusEnum = p_status;
    }

    public Node getNode()
    {
        throw new NotImplementedException();
//        return m_node;
    }

    public int getNextAddress()
    {
        throw new NotImplementedException();
//        return m_nextAddress;
    }

    public void markAsSent(int p_memoryAddress)
    {
//			m_nextAddress = p_memoryAddress + m_node.getCodeUpdatePageSize();
    }

    public Boolean IsComplete()
    {
        throw new NotImplementedException();
//			return m_nextAddress > m_dataFile.getMaxAddress();
//        return true;
    }

    public byte getDataByte(int p_address)
    {
        throw new NotImplementedException();
//				return m_dataFile.getDataByte(p_address);
//        return 0;
    }
}
