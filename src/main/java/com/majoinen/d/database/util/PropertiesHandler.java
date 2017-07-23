package com.majoinen.d.database.util;

import com.majoinen.d.database.exception.ConfigFileNotFoundException;
import com.majoinen.d.database.exception.DBUtilsException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Daniel Majoinen
 * @version 1.0, 23/7/17
 */
public final class PropertiesHandler {

    // The config resource folder
    protected static final String CONFIG_RESOURCE_DIR = "/config/";

    // The name of the config file
    protected static final String PROPERTIES_FILE_EXTENSION = ".properties";

    // Message when required key is missing in config
    private static final String MISSING_KEY_MSG =
      "DatabaseUtils missing property key: ";

    // Message when value is missing in config
    private static final String MISSING_VALUE_MSG =
      "DatabaseUtils missing property value: ";

    // Cache properties mapped to database name
    private static Map<String, Properties> propertiesMap;

    private PropertiesHandler() { }

    private static Properties getDatabaseProperties(String databaseName)
      throws DBUtilsException {
        if(propertiesMap == null)
            propertiesMap = new HashMap<>();
        else if(propertiesMap.containsKey(databaseName))
            return propertiesMap.get(databaseName);

        Properties properties = new Properties();
        InputStream inputStream = PropertiesHandler.class.getResourceAsStream(
          CONFIG_RESOURCE_DIR + databaseName + PROPERTIES_FILE_EXTENSION);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
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

        propertiesMap.put(databaseName, properties);
        return properties;
    }

    public static String getProperty(String databaseName, String key)
      throws DBUtilsException {
        Properties properties = getDatabaseProperties(databaseName);
        String value = properties.getProperty(key);
        if(value == null) {
            throw new NullPointerException(MISSING_KEY_MSG + key);
        } else if(value.length() == 0) {
            throw new NullPointerException(MISSING_VALUE_MSG + key);
        }
        return value;
    }
}
