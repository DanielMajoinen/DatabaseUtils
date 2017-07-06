package com.majoinen.d.database;

import java.util.List;

/**
 * Getters for properties required no matter the database implementation type,
 * defined in its corresponding config file.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public interface DatabaseProperties {
    String getDatabaseName();
    List<String> getTableNames();
}
