package com.majoinen.d.database.exception;

/**
 * Exception which can occur when backing up a database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 6/7/17
 */
public class BackupDatabaseException extends DBUtilsException {
    public BackupDatabaseException() {}

    public BackupDatabaseException(String message) {
        super(message);
    }

    public BackupDatabaseException(Exception e) {
        super(e);
    }
}
