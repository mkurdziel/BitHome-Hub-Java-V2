package org.bithome.core.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Mike Kurdziel
 * Date: 4/24/14
 */
public class InternalServiceException extends BitHomeBaseException {
    private final static Logger LOGGER = LoggerFactory.getLogger(InternalServiceException.class);

    private InternalServiceException(String error) {
        super("Internal service exception");

        LOGGER.error("Internal service exception: {}", error);
    }

    public InternalServiceException(JsonProcessingException e) {
        super("Internal service exception");

        LOGGER.error("Internal service exception", e);
    }
}
