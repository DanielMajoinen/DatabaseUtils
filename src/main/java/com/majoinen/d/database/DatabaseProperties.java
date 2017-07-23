package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.util.PropertiesHandler;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract properties class which provides required keys and parameters for
 * any implementation type.
 *
 * @author Daniel Majoinen
 * @version 1.0, 6/7/17
 */
public abstract class DatabaseProperties {

    // The key for the database type
    private static final String DATABASE_TYPE_KEY = "database.type";

    // The key for the table names list in the config .properties file
    private static final String TABLE_NAMES_KEY = "table.names";

    // The key for the tables delimiter
    private static final String TABLES_DELIMITER_KEY = "table.names.delimiter";

    protected DatabaseProperties() {}

    /**
     * Get the database type defined in the database .properties file.
     *
     * @param databaseName the database to get the type of.
     * @return the database type as a DatabaseType enum.
     * @throws DBUtilsException if any IOException occurs accessing the database
     * properties.
     */
    public static DatabaseType getDatabaseType(String databaseName)
      throws DBUtilsException {
        String databaseType =
          PropertiesHandler.getProperty(databaseName, DATABASE_TYPE_KEY);
        if("SQLITE".equalsIgnoreCase(databaseType))
            return DatabaseType.SQLITE;
        throw new NullPointerException("[DBUtils] Database type unknown");
    }

    /**
     * Get a list of table names as specified in the appropriate config file.
     *
     * @return A list of table names in/or meant to be in the database.
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    public static List<String> getTableNames(String databaseName)
      throws DBUtilsException {
        String delimiter = PropertiesHandler
          .getProperty(databaseName, TABLES_DELIMITER_KEY);
        String tables = PropertiesHandler
          .getProperty(databaseName, TABLE_NAMES_KEY);
        return Arrays.asList(tables.split(delimiter));
    }
}
