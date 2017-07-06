package com.majoinen.d.database.sqlite;

import com.google.common.io.CharStreams;
import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.DatabaseControllerFactory;
import com.majoinen.d.database.DatabaseInitialiser;
import com.majoinen.d.database.DatabaseType;
import com.majoinen.d.database.exception.DatabaseBackupException;
import com.majoinen.d.database.exception.InsertFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    // The file extension of a backed up database
    private static final String BACKUP_FILE_EXTENSION = ".bak";

    // SQL command used when verifying a table in the database
    private static final String VERIFY_TABLE_STRING =
      "SELECT `sql` FROM `sqlite_master` WHERE `name` = ?";

    // Column label to check when verifying a table in the database
    private static final String VERIFY_TABLE_COLUMN_LABEL = "sql";

    private final DatabaseController databaseController;
    private final Class<?> caller;

    public SQLiteDatabaseInitialiser(Class<?> caller) {
        this.caller = caller;
        databaseController = DatabaseControllerFactory.getInstance()
          .getController(DatabaseType.SQLITE, caller);
    }

    /**
     * Verify the database. If the database does not exist, create it. If the
     * database fails verification for any reason, back up the database and
     * create a new database.
     * @throws SQLException If there was a database error when verifying a
     * table or connecting to the database.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    @Override
    public void init() throws SQLException, IOException {
        File directory = new File(SQLiteDatabaseProperties.DATABASE_DIRECTORY);
        if(directory.mkdirs() && !verifyDatabase()) {
            backupDatabase();
            initDatabase();
        }
    }

    /**
     * Verify the entire database, single table at a time.
     *
     * @return True if all tables successfully verify, or false otherwise.
     * @throws SQLException If there was a database error when verifying a
     * table or connecting to the database.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private boolean verifyDatabase() throws SQLException, IOException {
        List<String> tableNames =
          SQLiteDatabaseProperties.getInstance(caller).getTableNames();
        for (String tableName : tableNames) {
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
     * @throws SQLException If there was a database error when verifying the
     * table.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private boolean verifyTable(String tableName) throws SQLException, IOException {
        InputStream inputStream;
        InputStreamReader isReader;
        ResultSet resultSet;
        String query;
        String sql;

        inputStream = caller.getResourceAsStream(SQL_RESOURCE_DIR + tableName + SQL_FILE_EXTENSION);
        isReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        query = CharStreams.toString(isReader);
        resultSet = databaseController.select(VERIFY_TABLE_STRING, Arrays.asList(tableName));
        sql = (String) databaseController.getObject(resultSet, VERIFY_TABLE_COLUMN_LABEL, String.class, true);
        inputStream.close();
        isReader.close();
        return sql != null && sql.equals(query);
    }

    /**
     * Begins the process of initialising all tables provided in the database
     * config file.
     *
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private void initDatabase() throws SQLException,
      IOException {
        List<String> tableNames =
          SQLiteDatabaseProperties.getInstance(caller).getTableNames();
        for(String tableName : tableNames) {
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
     * @throws SQLException If there was a database error creating the table.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private boolean initTable(String tableName) throws SQLException,
      IOException {
        InputStream inputStream;
        InputStreamReader isReader;
        String[] queries;

        inputStream = caller.getResourceAsStream(SQL_RESOURCE_DIR + tableName + SQL_FILE_EXTENSION);
        if(inputStream == null)
            throw new InsertFailedException("SQL File Missing for: "+tableName);
        isReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        queries = CharStreams.toString(isReader).split(QUERY_DELIMITER);
        for(String query : queries)
            databaseController.insert(query, null);
        inputStream.close();
        isReader.close();
        return true;
    }

    /**
     * Backups a database by renaming it.
     *
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private void backupDatabase() throws IOException {
        File database = new File(currentFilename());
        File backupDatabase = new File(newFilename());
        if(database.length() > 0) {
            if(database.renameTo(backupDatabase))
                logger.debug("Backed up old database");
            else
                throw new DatabaseBackupException();
        }
        else
            logger.debug("Database is empty: Skipping backup");
    }

    /**
     * Determine the new name for the database file when backing it up, based
     * on the time.
     *
     * @return New name for the database.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private String newFilename() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return SQLiteDatabaseProperties.DATABASE_DIRECTORY
          .concat(SQLiteDatabaseProperties.getInstance(caller).getDatabaseName())
          .concat("-")
          .concat(timestamp)
          .concat(BACKUP_FILE_EXTENSION);
    }

    /**
     * Get the current database filename so that it can be backed up.
     *
     * @return The current database file name.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    private String currentFilename() throws IOException {
        return SQLiteDatabaseProperties.DATABASE_DIRECTORY
          .concat(SQLiteDatabaseProperties.getInstance(caller).getDatabaseName())
          .concat(SQLiteDatabaseProperties.DATABASE_FILE_EXTENSION);
    }
}
