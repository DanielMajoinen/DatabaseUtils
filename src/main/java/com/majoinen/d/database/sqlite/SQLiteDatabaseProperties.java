package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.DatabaseProperties;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.util.PropertiesHandler;

/**
 * SQLiteDatabaseProperties singleton which provides access to properties
 * defined in a provided config file.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public final class SQLiteDatabaseProperties extends DatabaseProperties {

    // The key for where to place the database if it is file based
    private static final String DATABASE_DIRECTORY_KEY = "database.directory";

    // The key for the database directory if it is file based
    private static final String DATABASE_FILE_EXTENSION_KEY =
      "database.file.extension";

    private SQLiteDatabaseProperties() { }

    public static String getDatabaseDirectory(String databaseName)
      throws DBUtilsException {
        return PropertiesHandler.getProperty(databaseName,
          DATABASE_DIRECTORY_KEY);
    }

    public static String getDatabaseFileExtension(String databaseName)
      throws DBUtilsException {
        return PropertiesHandler.getProperty(databaseName,
          DATABASE_FILE_EXTENSION_KEY);
    }
}
