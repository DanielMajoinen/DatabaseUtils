package com.majoinen.d.database.exception;

/**
 * Exception for when DBUtils .properties config file cannot be found. This
 * is by default called dbutils.properties and should be in the resources
 * directory.
 *
 * @author Daniel Majoinen
 * @version 1.0, 8/7/17
 */
public class ConfigFileNotFoundException extends DBUtilsException {
    public ConfigFileNotFoundException() { }

    public ConfigFileNotFoundException(String message) {
        super(message);
    }

    public ConfigFileNotFoundException(Exception e) {
        super(e);
    }

    public ConfigFileNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
