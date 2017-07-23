package com.majoinen.d.database;

import java.util.Properties;

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

    // Message when required key is missing in config
    private static final String MISSING_KEY_MSG =
      "DatabaseUtils missing property key: ";

    // Message when value is missing in config
    private static final String MISSING_VALUE_MSG =
      "DatabaseUtils missing property value: ";

    protected AbstractDatabaseProperties() {}

    /*
     * Gets the value of the desired key from the supplied properties object
     * providing exception handling.
     */
    protected String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new NullPointerException(MISSING_KEY_MSG + key);
        } else if(value.length() == 0) {
            throw new NullPointerException(MISSING_VALUE_MSG + key);
        }
        return value;
    }
}
