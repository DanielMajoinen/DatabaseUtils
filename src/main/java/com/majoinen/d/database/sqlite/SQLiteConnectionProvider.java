package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.DatabaseConnectionProvider;
import com.majoinen.d.database.exception.DBUtilsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Daniel Majoinen
 * @version 1.0, 23/7/17
 */
public class SQLiteConnectionProvider implements DatabaseConnectionProvider {

    // The jdbc connection type
    private static final String DATABASE_TYPE_PREFIX = "jdbc:sqlite:";

    private static final Logger logger =
      LogManager.getLogger(SQLiteDatabaseController.class);

    private String databaseName;
    private String configFilename;

    public SQLiteConnectionProvider(String databaseName,
      String configFilename) {
        this.databaseName = databaseName;
        this.configFilename = configFilename;
    }

    /**
     * Opens a connection to the database.
     *
     * @throws DBUtilsException If a database access error occurs; if the
     * database config file is not found or if there are any permission
     * issues when accessing the file.
     */
    @Override
    public synchronized Connection openConnection() throws
      DBUtilsException {
        logger.debug("[DBUtils] Opening connection to the database");
        try {
            return DriverManager.getConnection(DATABASE_TYPE_PREFIX +
              SQLiteDatabaseProperties.getDatabaseDirectory(configFilename) +
              "/" + databaseName + "." + SQLiteDatabaseProperties
              .getDatabaseFileExtension(configFilename));
        } catch (SQLException e) {
            throw new DBUtilsException("Error opening connection", e);
        }
    }
}
