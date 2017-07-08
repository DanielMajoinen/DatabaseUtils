package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.AbstractDatabaseProperties;
import com.majoinen.d.database.exception.ConfigFileNotFoundException;
import com.majoinen.d.database.exception.DBUtilsException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * SQLiteDatabaseProperties singleton which provides access to properties
 * defined in a provided config file.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseProperties extends AbstractDatabaseProperties {

    // The subdirectory where the database will be created
    static final String DATABASE_DIRECTORY = "sqlite/";

    // The jdbc connection type
    static final String DATABASE_TYPE_PREFIX = "jdbc:sqlite:";

    // The SQLite database file extension
    static final String DATABASE_FILE_EXTENSION = ".db";

    // Message when required key is missing in config
    private static final String MISSING_KEY_MSG =
      "DatabaseUtils missing property key: ";

    // Message when value is missing in config
    private static final String MISSING_VALUE_MSG =
      "DatabaseUtils missing property value: ";

    private String databaseName;
    private List<String> tableNames;

    /**
     * Get the value of the database name as specified in the appropriate
     * config file.
     *
     * @return The database name as a String.
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    @Override
    public String getDatabaseName() throws DBUtilsException {
        if(databaseName == null)
            getProperties();
        return databaseName;
    }

    /**
     * Get a list of table names as specified in the appropriate config file.
     *
     * @return A list of table names in/or meant to be in the database.
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    @Override
    public List<String> getTableNames() throws DBUtilsException {
        if(tableNames == null)
            getProperties();
        return tableNames;
    }

    /**
     * Loads properties found in the config file. Defines databaseName and
     * tableNames.
     *
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    private void getProperties() throws DBUtilsException {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getResourceAsStream(
          CONFIG_RESOURCE_DIR + CONFIG_FILENAME);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
                setDatabaseName(properties);
                setTableNames(properties);
                inputStream.close();
            } catch(IOException e) {
                throw new DBUtilsException("Error loading properties file", e);
            }
        } else {
            throw new ConfigFileNotFoundException("DBUtils requires config " +
              "file: resources" + CONFIG_RESOURCE_DIR + CONFIG_FILENAME);
        }
    }

    /**
     * Gets the database name from the properties file and provides error
     * handling.
     *
     * @param properties Properties object with database.properties file loaded.
     */
    private void setDatabaseName(Properties properties) {
        String property = properties.getProperty(DATABASE_NAME_KEY);
        if(property == null) {
            throw new NullPointerException(
              MISSING_KEY_MSG + DATABASE_NAME_KEY);
        } else if(property.length() == 0) {
            throw new NullPointerException(
              MISSING_VALUE_MSG + DATABASE_NAME_KEY);
        }
        databaseName = property;
    }

    /**
     * Gets the table names and delimiter for the table names from the
     * properties file and provides error handling.
     *
     * @param properties Properties object with database.properties file loaded.
     */
    private void setTableNames(Properties properties) {
        String tables = properties.getProperty(TABLE_NAMES_KEY);
        String delimiter = properties.getProperty(TABLES_DELIMITER_KEY);
        if(tables == null) {
            throw new NullPointerException(
              MISSING_KEY_MSG + TABLE_NAMES_KEY);
        } else if(tables.length() == 0) {
            throw new NullPointerException(
              MISSING_VALUE_MSG + TABLE_NAMES_KEY);
        } else if(delimiter == null) {
            throw new NullPointerException(
              MISSING_KEY_MSG + TABLES_DELIMITER_KEY);
        } else if(delimiter.length() == 0) {
            throw new NullPointerException(
              MISSING_VALUE_MSG + TABLES_DELIMITER_KEY);
        }
        tableNames = Arrays.asList(tables.split(delimiter));
    }
}
