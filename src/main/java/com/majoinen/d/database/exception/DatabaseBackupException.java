package com.majoinen.d.database.exception;

import java.io.IOException;

/**
 * Exception which can occur when backing up a database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 6/7/17
 */
public class DatabaseBackupException extends IOException {
    public DatabaseBackupException() {}

    public DatabaseBackupException(String message) {
        super(message);
    }
}
