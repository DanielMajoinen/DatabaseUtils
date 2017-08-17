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

    private static final String ROOT_DIRECTORY_KEY =
      "root.directory";

    private SQLiteDatabaseProperties() { }

    public static String getDatabaseDirectory(String filename)
      throws DBUtilsException {
        String rootDirectory = PropertiesHandler.getProperty(filename,
          ROOT_DIRECTORY_KEY);
        String subDirectory = PropertiesHandler.getRequiredProperty(
          filename, DATABASE_DIRECTORY_KEY);
        if(rootDirectory != null)
            return rootDirectory + "/" + subDirectory;
        return subDirectory;
    }

    public static String getDatabaseFileExtension(String filename)
      throws DBUtilsException {
        return PropertiesHandler.getRequiredProperty(filename,
          DATABASE_FILE_EXTENSION_KEY);
    }
}
