package com.majoinen.d.database.exception;

/**
 * Parent exception class for all DBUtils exceptions.
 *
 * @author Daniel Majoinen
 * @version 1.0, 7/7/17
 */
public class DBUtilsException extends Exception {

    public DBUtilsException() { }

    public DBUtilsException(String message) {
        super(message);
    }

    public DBUtilsException(Exception e) {
        super(e);
    }

    public DBUtilsException(String message, Exception e) {
        super(message, e);
    }
}
