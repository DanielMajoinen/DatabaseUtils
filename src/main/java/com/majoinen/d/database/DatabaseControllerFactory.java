package com.majoinen.d.database;

import com.majoinen.d.database.sqlite.SQLiteDatabaseController;

/**
 * A factory to create an appropriate database controller for the desired
 * database implementation type.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class DatabaseControllerFactory {

    private static DatabaseControllerFactory instance = null;

    public static DatabaseControllerFactory getInstance() {
        if(instance == null)
            instance = new DatabaseControllerFactory();
        return instance;
    }

    public DatabaseController getController(DatabaseType type, Class<?>
      caller) {
        DatabaseController controller = null;
        if(type.equals(DatabaseType.SQLITE))
            controller = new SQLiteDatabaseController(caller);
        return controller;
    }
}
