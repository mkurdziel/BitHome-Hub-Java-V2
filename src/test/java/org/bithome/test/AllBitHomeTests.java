package org.bithome.test;

import org.bithome.test.api.messages.AllMessageTests;
import org.bithome.test.api.node.nodes.NodeXbeeTest;
import org.bithome.test.core.services.storage.StorageServiceMapDbTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mike Kurdziel on 4/17/14.
 */
@RunWith(Suite.class)
@SuiteClasses({
        NodeXbeeTest.class,
        StorageServiceMapDbTest.class,
        AllMessageTests.class
})
public class AllBitHomeTests {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AllBitHomeTests.class);

    @BeforeClass
    public static void setUpClass()
            throws Exception {
        LOGGER.info("Starting BitHome Tests...");
        LOGGER.info("Working directory: {}", System.getProperty("user.dir"));
    }

    @AfterClass
    public static void tearDownClass()
            throws Exception {
        LOGGER.info("Stopping BitHome Tests...");
    }

}
