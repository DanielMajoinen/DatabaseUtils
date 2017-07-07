package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.DatabaseInitialiser;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.exception.TableMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 * Initialises a SQLite database and populates it with tables. If the
 * database exists it will verify all tables schema matches the corresponding
 * sql file.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseInitialiser implements DatabaseInitialiser {

    private static final Logger logger =
      LogManager.getLogger(SQLiteDatabaseInitialiser.class);

    // The resource folder all sql files are located
    private static final String SQL_RESOURCE_DIR = "/sql/";

    // The delimiter used to separate queries in sql files
    private static final String QUERY_DELIMITER = ";";

    // The suffix added onto sql filenames when inserting base data
    private static final String INSERT_FILE_SUFFIX = "-insert";

    // The file extension of sql files
    private static final String SQL_FILE_EXTENSION = ".sql";

    // SQL command used when verifying a table in the database
    private static final String VERIFY_TABLE_QUERY =
      "SELECT `sql` FROM `sqlite_master` WHERE `name` = ?";

    // Column label to check when verifying a table in the database
    private static final String VERIFY_TABLE_COLUMN_LABEL = "sql";

    private SQLiteDatabaseController databaseController;
    private SQLiteDatabaseProperties properties;
    private Class<?> caller;

    SQLiteDatabaseInitialiser(SQLiteDatabaseController controller,
      SQLiteDatabaseProperties properties, Class<?> caller) {
        this.databaseController = controller;
        this.properties = properties;
        this.caller = caller;
    }

    /**
     * Verify the database. If the database does not exist, create it. If the
     * database fails verification for any reason, back up the database and
     * create a new database.
     *
     * @throws DBUtilsException If there was a database error when verifying a
     * table or connecting to the database; If the database config file is not
     * found or if there are any permission issues when accessing the config
     * file.
     */
    @Override
    public void init() throws DBUtilsException {
        File directory = new File(SQLiteDatabaseProperties.DATABASE_DIRECTORY);
        directory.mkdirs();
        if(!verifyDatabase()) {
            initDatabase();
        }
    }

    /**
     * Verify the entire database, single table at a time.
     *
     * @return True if all tables successfully verify, or false otherwise.
     * @throws DBUtilsException If there was a database error when verifying a
     * table or connecting to the database; If the database config file is not
     * found or if there are any permission issues when accessing the config
     * file.
     */
    private boolean verifyDatabase() throws DBUtilsException {
        for (String tableName : properties.getTableNames()) {
            if (verifyTable(tableName)) {
                logger.debug("Successfully verified table: " + tableName);
            } else {
                logger.debug("Failed verifying table: " + tableName);
                return false;
            }
        }
        return true;
    }

    /**
     * Verify a table in the database. It compares the table schema to a
     * matching sql file found in sql resource folder.
     *
     * @param tableName The table to verify.
     * @return Returns true if the table exists and matches schema, or false
     * otherwise.
     * @throws DBUtilsException If there was a database error when verifying the
     * table; If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private boolean verifyTable(String tableName) throws DBUtilsException {
        String query = getTableSQL(tableName, true);
        // Get current schema from database
        ResultSet resultSet = databaseController.select(VERIFY_TABLE_QUERY,
          Arrays.asList(tableName));
        String sql = (String) databaseController.getObject(resultSet,
          VERIFY_TABLE_COLUMN_LABEL, String.class, true);
        // Compare table sql file to current schema and return
        if(sql == null)
            return false;
        else if(!sql.equals(query))
            throw new TableMismatchException("Incorrect schema: " + tableName);
        return true;
    }

    /**
     * Begins the process of initialising all tables provided in the database
     * config file.
     *
     * @throws DBUtilsException If the database config file is not found or if
     * there are any permission issues when accessing the config file.
     */
    private void initDatabase() throws DBUtilsException {
        for(String tableName : properties.getTableNames()) {
            if(initTable(tableName))
                logger.debug("Successfully added table: " + tableName);
            if(initTable(tableName + INSERT_FILE_SUFFIX))
                logger.debug("Successfully inserted data into: " + tableName);
        }
    }

    /**
     * Initialises a single table run by the initDatabase() method. This is
     * also used to insert base data into the tables after creation.
     *
     * @param tableName Name of the table to create.
     * @return True if the table is successfully initialised.
     * @throws DBUtilsException If there was a database error creating the
     * table; if the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private boolean initTable(String tableName) throws DBUtilsException {
        String file = getTableSQL(tableName, false);
        String filename = SQL_RESOURCE_DIR.
          concat(tableName).
          concat(SQL_FILE_EXTENSION);
        if(file != null) {
            String[] queries = file.split(QUERY_DELIMITER);
            for (String query : queries) {
                if (query.length() == 0)
                    throw new NullPointerException(
                      "resources"+filename+" contains empty query");
                databaseController.insert(query, null);
            }
        } else {
            logger.debug("Skipping resources"+filename+": File not found");
            return false;
        }
        return true;
    }

    /**
     * Gets a specified tables sql file contents from the resource folder and
     * returns it in the form of a string. Used when verifying and creating
     * tables.
     *
     * @param tableName The name of the table to retrieve sql file contents.
     * @param required Whether to throw an exception if the sql file does not
     * exist or not.
     * @return The contents of the tables.sql file as a String.
     * @throws DBUtilsException If there is any issue accessing the tables .sql
     * file.
     */
    private String getTableSQL(String tableName, boolean required) throws
      DBUtilsException {
        String filename = SQL_RESOURCE_DIR.
          concat(tableName).
          concat(SQL_FILE_EXTENSION);
        URL url = caller.getResource(filename);
        if(url == null && required)
            throw new NullPointerException("resources/"+filename+" is missing");
        else if(url == null)
            return null;
        try {
            return new String(Files.readAllBytes(Paths.get(url.getPath())));
        } catch(IOException e) {
            throw new DBUtilsException(e);
        }
    }
}
