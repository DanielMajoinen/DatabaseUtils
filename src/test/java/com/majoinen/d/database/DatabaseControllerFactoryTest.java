package com.majoinen.d.database;

import com.majoinen.d.database.sqlite.SQLiteDatabaseController;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Majoinen
 * @version 1.0, 15/7/17
 */
public class DatabaseControllerFactoryTest {

    private static final String EMPTY_CONFIG_FILENAME = "empty";
    private static final String SQLITE_DATABASE_NAME = "test_sqlite";
    private static final String SQLITE_CONFIG_FILENAME = "test_sqlite_config";

    @Test
    public void getSQLiteControllerWithoutConfigFile() throws Exception {
        assertTrue(DatabaseControllerFactory.getController(SQLITE_DATABASE_NAME)
          instanceof SQLiteDatabaseController);
    }

    @Test
    public void getSQLiteControllerWithConfigFile() throws Exception {
        assertTrue(DatabaseControllerFactory.getController(
          EMPTY_CONFIG_FILENAME, SQLITE_CONFIG_FILENAME)
          instanceof SQLiteDatabaseController);
    }
}