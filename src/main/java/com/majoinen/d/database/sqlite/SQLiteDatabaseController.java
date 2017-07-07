package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.exception.BackupDatabaseException;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.exception.InsertException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A database facade DAOs will use when communicating with an SQLite database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseController implements DatabaseController {

    private static final Logger logger =
      LogManager.getLogger(SQLiteDatabaseController.class);

    // The file extension of a backed up database
    private static final String BACKUP_FILE_EXTENSION = ".bak";

    private SQLiteDatabaseProperties properties;

    private Connection connection;
    private PreparedStatement statement;

    public SQLiteDatabaseController(Class<?> caller) throws DBUtilsException {
        this.properties = new SQLiteDatabaseProperties(caller);
        new SQLiteDatabaseInitialiser(this, properties, caller).init();
    }

    /**
     * Run a single INSERT query on the database.
     *
     * @param query Insert query to execute.
     * @param vars List of variables which match the type the query expects.
     * @return The amount of affected rows.
     * @throws DBUtilsException If parameterIndex does not correspond to a
     * parameter marker in the SQL statement; if a database access error
     * occurs; this method is called on a closed PreparedStatement; the type
     * of the given object is ambiguous or the SQL statement returns a
     * ResultSet object; If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    @Override
    public int insert(String query, List<?> vars) throws DBUtilsException {
        prepareStatement(true, query);
        if (vars != null)
            setVariables(vars);
        return executeUpdate(true);
    }

    /**
     * Executes a list of queries which are dependent on each other. If one
     * fails, none of the queries will be executed.
     *
     * @param queries List of insert query to execute.
     * @param varsList 2D List of variables which must match the type the query
     * expects.
     * @return The amount of affected rows.
     * @throws DBUtilsException If parameterIndex does not correspond to a
     * parameter marker in the SQL statement; if a database access error
     * occurs; this method is called on a closed PreparedStatement; the type
     * of the given object is ambiguous or the SQL statement returns a
     * ResultSet object; If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    @Override
    public int insert(List<String> queries, List<List<?>> varsList) throws
      DBUtilsException {
        int affectedRows = 0;
        try {
            openConnectionWithoutAutoCommit();
            affectedRows += executeInserts(queries, varsList);
        } catch(DBUtilsException e) {
            rollbackConnection();
            throw new InsertException(e);
        } finally {
            commitConnection();
            closeConnection();
        }
        return affectedRows;
    }

    /**
     * Commit a connection, providing exception handling.
     * s
     * @throws DBUtilsException If any SQLException occurs committing the
     * connection.
     */
    private void commitConnection() throws DBUtilsException {
        try {
            connection.commit();
        } catch(SQLException e) {
            throw new DBUtilsException("Error committing connection", e);
        }
    }

    /**
     * Rollback the connection, providing exception handling.
     *
     * @throws DBUtilsException If any SQLException occurs rolling back the
     * connection.
     */
    private void rollbackConnection() throws DBUtilsException {
        try {
            connection.rollback();
        } catch(SQLException e) {
            throw new DBUtilsException("Error rolling back connection", e);
        }
    }

    /**
     * Execute a SELECT query with provided parameters on the database.
     *
     * @param query Select query to execute.
     * @param vars List of variables which must match the type the query
     * expects.
     * @return Returns the queries results in the form of a ResultSet.
     * @throws DBUtilsException If parameterIndex does not correspond to a
     * parameter marker in the SQL statement; if a database access error
     * occurs; this method is called on a closed PreparedStatement; the type
     * of the given object is ambiguous or the SQL statement does not return
     * a ResultSet object; If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    @Override
    public ResultSet select(String query, List<?> vars) throws
      DBUtilsException {
        prepareStatement(true, query);
        if(vars != null)
            setVariables(vars);
        try {
            return statement.executeQuery();
        } catch(SQLException e) {
            throw new DBUtilsException("Error executing select query", e);
        }
    }

    /**
     * Retrieves the value of the designated column in the current row of this
     * ResultSet object and will convert from the SQL type of the column to the
     * requested Java data type, if the conversion is supported. If the
     * conversion is not supported or null is specified for the type, a
     * SQLException is thrown.
     *
     * @param resultSet The ResultSet object to retrieve the value from.
     * @param columnLabel The name of the column.
     * @param type Class representing the Java data type to convert the
     * designated column to.
     * @param closeConnection Whether to close the ResultSet and Connection
     * or not.
     * @return an instance of type holding the column value.
     * @throws DBUtilsException if conversion is not supported, type is null or
     * another error occurs.
     */
    @Override
    public Object getObject(ResultSet resultSet, String columnLabel, Class<?>
      type, boolean closeConnection) throws DBUtilsException {
        Object result = null;
        try {
            if (!resultSet.isClosed()) {
                result = resultSet.getObject(columnLabel);
                if (closeConnection)
                    closeResultSetAndConnection(resultSet);
            }
        } catch(SQLException e) {
            throw new DBUtilsException("Error getting object from result", e);
        }
        return type.cast(result);
    }

    /**
     * Makes a copy of the database, with the current time in the filename.
     *
     * @return True if successful backup or false otherwise.
     * @throws DBUtilsException If an IOException occurs when creating
     * a copy of the database file.
     */
    @Override
    public boolean backupDatabase() throws DBUtilsException {
        try {
            File database = new File(currentFilename());
            File backupDatabase = new File(newFilename());
            if (database.length() > 0) {
                if (Files.copy(database.toPath(), backupDatabase.toPath()).
                  equals(backupDatabase.toPath())) {
                    logger.debug("Backed up old database");
                } else {
                    throw new BackupDatabaseException(
                      "Expected backup path does not match result");
                }
            } else {
                logger.debug("Database is empty: Skipping backup");
            }
        } catch(IOException e) {
            throw new BackupDatabaseException(e);
        }
        return true;
    }

    /**
     * Determine the new name for the database file when backing it up, based
     * on the time.
     *
     * @return New name for the database.
     * @throws DBUtilsException If the database config file is not found or if
     * there are any permission issues when accessing the config file.
     */
    private String newFilename() throws DBUtilsException {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").
          format(new java.util.Date());
        return SQLiteDatabaseProperties.DATABASE_DIRECTORY.
          concat(properties.getDatabaseName()).
          concat("-").
          concat(timestamp).
          concat(BACKUP_FILE_EXTENSION);
    }

    /**
     * Get the current database filename so that it can be backed up.
     *
     * @return The current database file name.
     * @throws DBUtilsException If the database config file is not found or if
     * there are any permission issues when accessing the config file.
     */
    private String currentFilename() throws DBUtilsException {
        return SQLiteDatabaseProperties.DATABASE_DIRECTORY.
          concat(properties.getDatabaseName()).
          concat(SQLiteDatabaseProperties.DATABASE_FILE_EXTENSION);
    }

    /**
     * Closes a connection and ResultSet.
     *
     * @param resultSet The ResultSet to close.
     * @throws DBUtilsException If a database access error occurs when closing
     * the connection or the ResultSet.
     */
    private void closeResultSetAndConnection(ResultSet resultSet) throws
      DBUtilsException {
        closeConnection();
        try {
            resultSet.close();
        } catch(SQLException e) {
            throw new DBUtilsException("Error closing ResultSet", e);
        }
    }

    /**
     * Executes a list of queries which are dependent on each other. If one
     * fails, none of the queries will be executed.
     *
     * @param queries List of queries to insert.
     * @param varsList List of variables which match type the query expects.
     * One list per query is required.
     * @return The amount of affected rows.
     * @throws DBUtilsException If parameterIndex does not correspond to a
     * parameter marker in the SQL statement; if a database access error
     * occurs; this method is called on a closed PreparedStatement or the
     * type of the given object is ambiguous; If the database config file is
     * not found or if there are any permission issues when accessing the file.
     */
    private int executeInserts(List<String> queries, List<List<?>> varsList)
      throws DBUtilsException {
        int affectedRows = 0;
        for(String query : queries) {
            prepareStatement(false, query);
            if(varsList != null) {
                List<?> vars = varsList.remove(0);
                setVariables(vars);
            }
            affectedRows += executeUpdate(false);
        }
        return affectedRows;
    }

    /**
     * Executes a statement, used when inserting.
     *
     * @param closeConnection Whether to close the connection after the
     * execution or not.
     * @return The amount of affected rows.
     * @throws DBUtilsException If a database access error occurs; this method
     * is called on a closed PreparedStatement or the SQL statement returns a
     * ResultSet object.
     */
    private int executeUpdate(boolean closeConnection) throws DBUtilsException {
        int affectedRows;
        try {
            affectedRows = statement.executeUpdate();
        } catch(SQLException e) {
            throw new InsertException(e);
        }
        if(closeConnection)
            closeConnection();
        return affectedRows;
    }

    /**
     * Sets variables of a query which has already been prepared.
     *
     * @param vars List of variables which match type the query expects.
     * @throws DBUtilsException If parameterIndex does not correspond to a
     * parameter marker in the SQL statement; if a database access error
     * occurs; this method is called on a closed PreparedStatement or the
     * type of the given object is ambiguous.
     */
    private void setVariables(List<?> vars) throws DBUtilsException {
        try {
            for (int i = 0; i < vars.size(); i++)
                statement.setObject(i + 1, vars.get(i));
        } catch(SQLException e) {
            throw new DBUtilsException("Error adding variables to query", e);
        }
    }

    /**
     * Prepares a statement with the provided query ready to be executed.
     * Also allows the option to open the connection prior.
     *
     * @param openConnection Whether to open a connection first or not.
     * @param query Query which is prepared for execution.
     * @throws DBUtilsException If a database access error occurs or this method
     * is called on a closed connection; If the database config file is not
     * found or if there are any permission issues when accessing the file.
     */
    private void prepareStatement(boolean openConnection, String query) throws
      DBUtilsException {
        if(openConnection)
            openConnection();
        try {
            statement = connection.prepareStatement(query);
        } catch(SQLException e) {
            throw new DBUtilsException("Error preparing query", e);
        }
    }

    /**
     * Allow the option to open a connection without enabling auto commit.
     * This is used when inserting multiple queries at the same time that are
     * dependent on each other.
     *
     * @throws DBUtilsException If a database access error occurs or this method
     * is called on a closed connection; If the database config file is not
     * found or if there are any permission issues when accessing the file.
     */
    private void openConnectionWithoutAutoCommit() throws DBUtilsException {
        openConnection();
        try {
            connection.setAutoCommit(false);
        } catch(SQLException e) {
            throw new DBUtilsException("Error disabling auto commit", e);
        }
    }

    /**
     * Opens a connection to the database.
     *
     * @throws DBUtilsException If a database access error occurs. Ensure URL is
     * correct; if the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    private void openConnection() throws DBUtilsException {
        try {
            connection = DriverManager.getConnection(
              SQLiteDatabaseProperties.DATABASE_TYPE_PREFIX +
                SQLiteDatabaseProperties.DATABASE_DIRECTORY +
                properties.getDatabaseName() +
                SQLiteDatabaseProperties.DATABASE_FILE_EXTENSION);
        } catch(SQLException e) {
            throw new DBUtilsException("Error opening connection", e);
        }
    }

    /**
     * Closes the statement and connection, then setting them to null for
     * future use.
     *
     * @throws DBUtilsException If either the statement or connection fails
     * to close for any reason.
     */
    private void closeConnection() throws DBUtilsException {
        try {
            statement.close();
            connection.close();
            connection = null;
            statement = null;
        } catch(SQLException e) {
            throw new DBUtilsException("Error closing connection", e);
        }
    }
}
