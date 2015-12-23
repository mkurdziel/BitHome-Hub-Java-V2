package org.bithome.core.exception;

/**
 * Created by Mike Kurdziel on 4/23/14.
 */
public class NotFoundException extends BitHomeBaseException {
    public NotFoundException() {
        super("The item was not found");
    }
}
