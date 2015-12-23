package org.bithome.core.exception;

import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mike Kurdziel on 4/23/14.
 */
public class StorageServiceException extends BitHomeBaseException {
    private final static Logger LOGGER = LoggerFactory.getLogger(StorageServiceException.class);

    public StorageServiceException() {
       super("An error occurred with the storage system");
    }

    public StorageServiceException(UnableToExecuteStatementException e) {
        this();

        LOGGER.warn("Storage level exception occurred", e);
    }

    private StorageServiceException(Exception e) {
        this();

        LOGGER.warn("Storage level exception occurred", e);
    }
}
