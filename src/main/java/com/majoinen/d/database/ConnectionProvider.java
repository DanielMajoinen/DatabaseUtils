package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;

import java.sql.Connection;

/**
 * Defines a class which can provide a connection for any database types.
 *
 * @author Daniel Majoinen
 * @version 1.0, 11/7/17
 */
@FunctionalInterface
public interface ConnectionProvider {
    Connection openConnection() throws DBUtilsException;
}
