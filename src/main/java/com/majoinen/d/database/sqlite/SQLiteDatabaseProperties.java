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

    // The jdbc connection type
    protected static final String DATABASE_TYPE_PREFIX = "jdbc:sqlite:";

    // The key for where to place the database if it is file based
    private static final String DATABASE_DIRECTORY_KEY = "database.directory";

    // The key for the database directory if it is file based
    private static final String DATABASE_FILE_EXTENSION_KEY =
      "database.file.extension";

    private String databaseName;
    private List<String> tableNames;
    private String databaseFileExtension;
    private String databaseDirectory;

    SQLiteDatabaseProperties(String databaseName) {
        if(databaseName == null)
            throw new NullPointerException("[DBUtils] Database name null");
        this.databaseName = databaseName;
    }

    /**
     * Get the value of the database name as specified in the appropriate
     * config file.
     *
     * @return The database name as a String.
     */
    @Override
    public String getDatabaseName() {
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
     * Get the name of the directory where the database is located set in the
     * properties file.
     *
     * @return the directory name.
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    public String getDatabaseDirectory() throws DBUtilsException {
        if(databaseDirectory == null)
            getProperties();
        return databaseDirectory;
    }

    /**
     * Get the file extension of the database set in the properties file.
     *
     * @return the database file extension.
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    public String getDatabaseFileExtension() throws DBUtilsException {
        if(databaseFileExtension == null)
            getProperties();
        return databaseFileExtension;
    }

    /**
     * Loads properties found in the config file.
     *
     * @throws DBUtilsException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    private void getProperties() throws DBUtilsException {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getResourceAsStream(
          CONFIG_RESOURCE_DIR + databaseName + PROPERTIES_FILE_EXTENSION);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
                tableNames = tableNames(properties);
                databaseDirectory = databaseDirectory(properties);
                databaseFileExtension = databaseFileExtension(properties);
                inputStream.close();
            } catch(IOException e) {
                throw new DBUtilsException(
                  "[DBUtils] Error loading properties file", e);
            }
        } else {
            throw new ConfigFileNotFoundException(
              "[DBUtils] DBUtils requires config file: resources" +
                CONFIG_RESOURCE_DIR + databaseName + PROPERTIES_FILE_EXTENSION);
        }
    }

    /*
     * Gets the table names and delimiter for the table names from the
     * properties file and provides error handling.
     */
    private List<String> tableNames(Properties properties) {
        String delimiter = super.getProperty(properties, TABLES_DELIMITER_KEY);
        String tables = super.getProperty(properties, TABLE_NAMES_KEY);
        return Arrays.asList(tables.split(delimiter));
    }

    /* Get the database file extension property */
    private String databaseFileExtension(Properties properties) {
        return super.getProperty(properties, DATABASE_FILE_EXTENSION_KEY);
    }

    /* Get the database directory property */
    private String databaseDirectory(Properties properties) {
        return super.getProperty(properties, DATABASE_DIRECTORY_KEY);
    }
}
