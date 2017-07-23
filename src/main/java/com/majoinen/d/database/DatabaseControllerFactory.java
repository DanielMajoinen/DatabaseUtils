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
        if(controllers == null)
            controllers = new HashMap<>();
        else if(controllers.containsKey(databaseName))
            return controllers.get(databaseName);

        DatabaseController controller = null;
        DatabaseType type = DatabaseProperties.getDatabaseType(databaseName);
        if(type.equals(DatabaseType.SQLITE))
            controller = new SQLiteDatabaseController(databaseName);

        controllers.put(databaseName, controller);
        return controller;
    }
}
