package com.majoinen.d.database.exception;

import java.sql.SQLException;

/**
 * An InsertFailedException occurs when an SQLException is
 * caught during the insert process.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class InsertFailedException extends SQLException {
    public InsertFailedException() {}

    public InsertFailedException(String message) {
        super(message);
    }
}
