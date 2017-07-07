package com.majoinen.d.database.exception;

/**
 * An InsertException occurs when an SQLException is
 * caught during the insert process.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class InsertException extends DBUtilsException {
    public InsertException() {}

    public InsertException(String message) {
        super(message);
    }

    public InsertException(Exception e) {
        super(e);
    }
}
