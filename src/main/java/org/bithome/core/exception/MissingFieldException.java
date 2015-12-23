package org.bithome.core.exception;

/**
 * Created by Mike Kurdziel on 4/22/14.
 */
public class MissingFieldException extends BitHomeBaseException {
    private final String fieldName;

    public MissingFieldException(String fieldName) {
        super("Missing field: " + fieldName);

        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
