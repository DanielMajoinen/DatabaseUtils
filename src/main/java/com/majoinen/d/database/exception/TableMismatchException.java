package com.majoinen.d.database.exception;

/**
 * A TableMismatchException occurs when a table is not as expected while
 * verifying a database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class TableMismatchException extends DBUtilsException {
    public TableMismatchException() {}

    public TableMismatchException(String message) {
        super(message);
    }
}
