package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.sqlite.SQLiteDatabaseController;

/**
 * A factory to create an appropriate database controller for the desired
 * database implementation type.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public final class DatabaseControllerFactory {

    private DatabaseControllerFactory() { }

    public static DatabaseController getController(DatabaseType type)
      throws DBUtilsException {
        DatabaseController controller = null;
        if(type.equals(DatabaseType.SQLITE))
            controller = new SQLiteDatabaseController();
        return controller;
    }
}
