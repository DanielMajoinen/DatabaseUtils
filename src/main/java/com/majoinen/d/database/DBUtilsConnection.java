package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.Logger;
import com.majoinen.d.database.log.LogManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Holds reference to the Connection and PreparedStatement. Used by
 * DatabaseController and AbstractQuery types.
 *
 * @author Daniel Majoinen
 * @version 1.0, 11/7/17
 */
public class DBUtilsConnection {

    private static final Logger logger =
      LogManager.getLogger(DBUtilsConnection.class);

    private DatabaseConnectionProvider connectionProvider;
    private Connection connection;
    private PreparedStatement statement;

    public DBUtilsConnection(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Getter for the current Connection. Used in unit tests.
     *
     * @return the Connection.
     */
    final Connection getConnection() {
        return connection;
    }

    /**
     * Getter for the current PreparedStatement. Used in unit tests.
     *
     * @return the PreparedStatement.
     */
    final PreparedStatement getStatement() {
        return statement;
    }

    /**
     * Executes a prepared query, used when inserting / altering the database.
     *
     * @return the amount of affected rows.
     * @throws DBUtilsException if any SQLException occurs when executing the
     * prepared statement.
     */
    public int executeUpdate() throws DBUtilsException {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DBUtilsException("Error executing update", e);
        }
    }

    /**
     * Executes a query and provides the resulting values from the database.
     *
     * @return The queries results in the form of a ResultSet.
     * @throws DBUtilsException If any SQLException occurs executing the query.
     */
    public ResultSet executeQuery() throws DBUtilsException {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new DBUtilsException("Error executing query", e);
        }
    }

    /**
     * Allow the option to open a connection without enabling auto commit.
     * This is used when inserting multiple queries at the same time that are
     * dependent on each other. If required a connection will be opened.
     *
     * @return True if auto commit is successfully disabled.
     * @throws DBUtilsException If a database access error occurs or this method
     * is called on a closed connection; If the database config file is not
     * found or if there are any permission issues when accessing the file.
     */
    public boolean disableAutoCommit() throws DBUtilsException {
        logger.debug("[DBUtils] Disabling auto commit");
        openConnection();
        try {
            connection.setAutoCommit(false);
        } catch(SQLException e) {
            throw new DBUtilsException("Error disabling auto commit",
              e);
        }
        return true;
    }

    /**
     * Prepare a statement for execution, using the provided query.
     *
     * @param sql The query to prepare the statement with.
     * @return True if the statement is successfully prepared.
     * @throws DBUtilsException If any SQLException occurs when preparing the
     * statement.
     */
    public boolean prepareStatement(String sql) throws
      DBUtilsException {
        if(sql == null || sql.length() == 0)
            throw new DBUtilsException("Null or empty query");
        openConnection();
        try {
            statement = connection.prepareStatement(sql);
        } catch(SQLException e) {
            throw new DBUtilsException("Error preparing statement",
              e);
        }
        return true;
    }

    /**
     * Open a connection if required.
     *
     * @throws DBUtilsException If any SQLException occurs when opening the
     * connection.
     */
    private void openConnection() throws DBUtilsException {
        try {
            if (connection == null || connection.isClosed())
                connection = connectionProvider.openConnection();
        } catch(SQLException e) {
            throw new DBUtilsException("Error opening connection", e);
        }
    }

    /**
     * Close the prepared statement and connection.
     *
     * @return True if both successfully close, or false otherwise.
     * @throws DBUtilsException If any SQLException occurs when closing the
     * statement or connection.
     */
    public boolean close() throws DBUtilsException {
        logger.debug("Closing connection to the database");
        return closeStatement() && closeConnection();
    }

    /**
     * Closes the PreparedStatement, providing exception handling.
     *
     * @return True if the statement successfully closes.
     * @throws DBUtilsException If any SQLException occurs when closing the
     * PrepareStatement.
     */
    private boolean closeStatement() throws DBUtilsException {
        try {
            if (statement != null && !statement.isClosed())
                statement.close();
        } catch(SQLException e) {
            logger.error("SQLException closing statement");
            throw new DBUtilsException("Error closing statement", e);
        }
        return true;
    }

    /**
     * Closes the Connection, providing exception handling.
     *
     * @return True if the connection successfully closes.
     * @throws DBUtilsException If any SQLException occurs when closing the
     * Connection.
     */
    private boolean closeConnection() throws DBUtilsException {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch(SQLException e) {
            logger.error("[DBUtils] SQLException closing connection");
            throw new DBUtilsException("[DBUtils] Error closing connection", e);
        }
        return true;
    }

    /**
     * Sets the value of the designated parameter using the given object.
     *
     * @param index the parameter index. Parameters start from 1.
     * @param object the object containing the input parameter value.
     * @throws DBUtilsException if any SQLException occurs when setting the
     * designated parameter.
     */
    public void setObject(int index, Object object) throws DBUtilsException {
        try {
            statement.setObject(index, object);
        } catch(SQLException e) {
            logger.error("[DBUtils] SQLException setting object");
            throw new DBUtilsException("[DBUtils] Error setting object", e);
        }
    }

    /**
     * Commit a connection, providing exception handling.
     *
     * @throws DBUtilsException If any SQLException occurs committing the
     * connection.
     */
    protected void commit() throws DBUtilsException {
        try {
            connection.commit();
        } catch(SQLException e) {
            throw new DBUtilsException(
              "[DBUtils] Error committing connection", e);
        }
    }

    /**
     * Rollback the connection, providing exception handling.
     *
     * @throws DBUtilsException If any SQLException occurs rolling back the
     * connection.
     */
    protected void rollback() throws DBUtilsException {
        try {
            connection.rollback();
        } catch(SQLException e) {
            throw new DBUtilsException(
              "[DBUtils] Error rolling back connection", e);
        }
    }
}
