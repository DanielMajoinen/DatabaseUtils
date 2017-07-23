package com.majoinen.d.database;

/**
 * Abstract properties class which provides required keys and parameters for
 * any implementation type.
 *
 * @author Daniel Majoinen
 * @version 1.0, 6/7/17
 */
public abstract class AbstractDatabaseProperties implements DatabaseProperties {

    // The config resource folder
    protected static final String CONFIG_RESOURCE_DIR = "/config/";

    // The name of the config file
    protected static final String PROPERTIES_FILE_EXTENSION = ".properties";

    // The key for the table names list in the config .properties file
    protected static final String TABLE_NAMES_KEY = "table.names";

    // The key for the tables delimiter in dbutils.properties
    protected static final String TABLES_DELIMITER_KEY =
      "table.names.delimiter";

    protected AbstractDatabaseProperties() {}
}
