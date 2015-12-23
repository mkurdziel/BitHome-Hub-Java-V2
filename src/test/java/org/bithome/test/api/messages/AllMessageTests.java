package org.bithome.test.api.messages;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mike Kurdziel on 5/23/14.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MessageBootloadResponseTest.class,
        MessageCatalogResponseTest.class,
        MessageDataResponseTest.class,
        MessageDeviceInfoResponseTest.class,
        MessageDeviceStatusResponseTest.class,
        MessageParameterResponseTest.class
})
public class AllMessageTests {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AllMessageTests.class);

    @BeforeClass
    public static void setUpClass()
            throws Exception {
        LOGGER.info("Starting BitHome Message Tests...");
    }

    @AfterClass
    public static void tearDownClass()
            throws Exception {
        LOGGER.info("Stopping BitHome Message Tests...");
    }

}
