package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.SQLDatabaseController;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.Logger;
import com.majoinen.d.database.log.LogManager;
import com.majoinen.d.database.util.PropertiesHandler;

import java.io.File;

/**
 * A database facade DAOs will use when communicating with an SQLite database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseController extends SQLDatabaseController {

    private static final Logger logger =
      LogManager.getLogger(SQLiteDatabaseController.class);

    private final String databaseName;
    private final String configFilename;

    public SQLiteDatabaseController(String databaseName,
      String configFilename) {
        super(databaseName, configFilename);
        this.databaseName = databaseName;
        this.configFilename = configFilename;
    }

    /**
     * Initialise, verify and if needed create any missing tables.
     *
     * @throws DBUtilsException if any IOException occurs when accessing
     * database properties or SQLException when verifying or creating tables.
     */
    @Override
    public void init() throws DBUtilsException {
        logger.debug("Initialising database");
        SQLiteDatabaseInitialiser.getInstance(this).init();
    }

    @Override
    public boolean databaseExists() throws DBUtilsException {
        File database = new File(getDatabaseAbsolutePath());
        return database.exists();
    }

    @Override
    public void setProperty(String key, String value) throws DBUtilsException {
        PropertiesHandler.setProperty(configFilename, key, value);
    }
}
