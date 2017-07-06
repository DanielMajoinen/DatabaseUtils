package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.AbstractDatabaseProperties;

import java.io.FileNotFoundException;
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

    private static SQLiteDatabaseProperties instance = null;

    private Class<?> caller;
    private String databaseName;
    private List<String> tableNames;

    static SQLiteDatabaseProperties getInstance(Class<?> caller) {
        if(instance == null)
            instance = new SQLiteDatabaseProperties();
        instance.caller = caller;
        return instance;
    }

    /**
     * Get the value of the database name as specified in the appropriate
     * config file.
     *
     * @return The database name as a String.
     * @throws IOException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    @Override
    public String getDatabaseName() throws IOException {
        if(databaseName == null)
            getProperties();
        return databaseName;
    }

    /**
     * Get a list of table names as specified in the appropriate config file.
     *
     * @return A list of table names in/or meant to be in the database.
     * @throws IOException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    @Override
    public List<String> getTableNames() throws IOException {
        if(tableNames == null)
            getProperties();
        return tableNames;
    }

    /**
     * Loads properties found in the config file. Defines databaseName and
     * tableNames.
     *
     * @throws IOException If the config file is not found or if there are
     * any permission issues when accessing the config file.
     */
    private void getProperties() throws IOException {
        Properties  properties = new Properties();
        InputStream inputStream = caller.getResourceAsStream(
          CONFIG_RESOURCE_DIR + CONFIG_FILENAME);
        if (inputStream != null) {
            properties.load(inputStream);
            databaseName = properties.getProperty(DATABASE_NAME_KEY);
            tableNames = Arrays.asList(properties.getProperty(TABLE_NAMES_KEY)
              .split(properties.getProperty(TABLES_DELIMITER_KEY)));
            inputStream.close();
        }
        else {
            throw new FileNotFoundException("Can't find config file: " +
              CONFIG_FILENAME);
        }
    }
}
