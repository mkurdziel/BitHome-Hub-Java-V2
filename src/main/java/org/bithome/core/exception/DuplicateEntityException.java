package org.bithome.core.exception;

/**
 * Created by Mike Kurdziel on 4/23/14.
 */
public class DuplicateEntityException extends BitHomeBaseException {
    public DuplicateEntityException() {
        super("Duplicate item present");
    }
}
