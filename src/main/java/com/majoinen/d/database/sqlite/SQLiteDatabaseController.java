package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.SQLDatabaseController;
import com.majoinen.d.database.exception.DBUtilsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public SQLiteDatabaseController(String databaseName)
      throws DBUtilsException {
        super(databaseName);
        this.databaseName = databaseName;
    }

    /**
     * Initialise, verify and if needed create any missing tables.
     *
     * @throws DBUtilsException if any IOException occurs when accessing
     * database properties or SQLException when verifying or creating tables.
     */
    @Override
    public void init() throws DBUtilsException {
        logger.info("[DBUtils] Initialising database");
        new SQLiteDatabaseInitialiser(this).init(databaseName);
    }
}
