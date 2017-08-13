package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.sqlite.SQLiteDatabaseController;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory to create an appropriate database controller for the desired
 * database implementation type.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public final class DatabaseControllerFactory {

    private static Map<String, DatabaseController> controllers;

    private DatabaseControllerFactory() { }

    public static DatabaseController getController(String databaseName)
      throws DBUtilsException {
        return getController(databaseName, databaseName);
    }

    public static DatabaseController getController(String databaseName,
      String filename) throws DBUtilsException {
        DatabaseController controller = getIfExists(databaseName);
        if(controller != null)
            return controller;

        DatabaseType type = DatabaseProperties.getDatabaseType(filename);
        if(type.equals(DatabaseType.SQLITE))
            controller = new SQLiteDatabaseController(databaseName, filename);

        controllers.put(databaseName, controller);
        return controller;

    }

    private static DatabaseController getIfExists(String databaseName) {
        if(controllers == null)
            controllers = new HashMap<>();
        else if(controllers.containsKey(databaseName))
            return controllers.get(databaseName);
        return null;
    }
}
