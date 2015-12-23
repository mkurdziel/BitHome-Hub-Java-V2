package org.bithome.core.exception;

/**
 * Created by Mike Kurdziel on 5/23/14.
 */
public class InvalidMessageDataException extends BitHomeBaseException{
    public InvalidMessageDataException(final String field) {
        super("Invalid field: " + field);
    }

    public InvalidMessageDataException(final Throwable e) {
        super(e.getMessage());
    }
}
