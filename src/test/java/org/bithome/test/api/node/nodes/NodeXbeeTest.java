package org.bithome.test.api.node.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.bithome.api.Version;
import org.bithome.api.node.NodeInvestigationStatus;
import org.bithome.api.node.NodeType;
import org.bithome.api.node.nodes.NodeXbee;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public class NodeXbeeTest {

    @Test
    public void testCloneMethod() {
        NodeXbee node1 = new NodeXbee(
                1L,
                2,
                new Version(3,4),
                NodeType.XBEE,
                "node1",
                DateTime.now().minusDays(1),
                DateTime.now().minusDays(3),
                NodeInvestigationStatus.COMPLETED,
                Lists.newArrayList(4, 5),
                ImmutableMap.of(6,7L),
                Optional.of(8),
                UnsignedLong.asUnsigned(9),
                UnsignedInteger.asUnsigned(10));

        NodeXbee node2 = node1.clone();

        assertThat(node1.getId(), is(node2.getId()));
        assertThat(node1.getManufacturerId(), is(node2.getManufacturerId()));
        assertThat(node1.getRevision(), is(node2.getRevision()));
        assertThat(node1.getNodeType(), is(node2.getNodeType()));
        assertThat(node1.getName(), is (node2.getName()));
        assertThat(node1.getDateCreated(), is (node2.getDateCreated()));
        assertThat(node1.getLastSeen(), is (node2.getLastSeen()));
        assertThat(node1.getInvestigationStatus(), is (node2.getInvestigationStatus()));
        assertThat(node1.getAddress16(), is (node2.getAddress16()));
        assertThat(node1.getAddress64(), is (node2.getAddress64()));

        // TOD0: compare actions list and interfaces lift
    }

}
