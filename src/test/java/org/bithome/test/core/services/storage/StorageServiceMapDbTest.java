package org.bithome.test.core.services.storage;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.bithome.api.node.nodes.NodeXbee;
import org.bithome.core.services.storage.StorageService;
import org.bithome.core.services.storage.StorageServiceMapDb;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public class StorageServiceMapDbTest {
    private StorageService storageService;

    @Before
    public void setup() throws Exception {
        this.storageService = new StorageServiceMapDb();
        this.storageService.start();
    }

    @After
    public void tearDown() throws Exception {
        this.storageService.stop();
    }

    @Test
    public void TestSaveNode() {
        NodeXbee xbeeNode = new NodeXbee(UnsignedLong.asUnsigned(1), UnsignedInteger.asUnsigned(2));
        xbeeNode.setId(1L);

        this.storageService.saveNode(xbeeNode);

    }
}
