package org.bithome.core.exception;

/**
 * User: Mike Kurdziel
 * Date: 4/24/14
 */
public class NotAuthorizedException extends BitHomeBaseException {

    public NotAuthorizedException() {
        super("Request is not authorized");
    }
}
