package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private ConnectionProvider connectionProvider;
    private Connection connection;
    private PreparedStatement statement;

    public DBUtilsConnection(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Connection connection() {
        return connection;
    }

    public PreparedStatement statement() {
        return statement;
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
            throw new DBUtilsException("Error disabling auto commit", e);
        }
        return true;
    }

    /**
     * Prepare a statement for execution, using the provided query.
     *
     * @param query The query to prepare the statement with.
     * @return True if the statement is successfully prepared.
     * @throws DBUtilsException If any SQLException occurs when preparing the
     * statement.
     */
    public boolean prepareStatement(String query) throws DBUtilsException {
        if(query.length() == 0)
            throw new DBUtilsException("Empty query");
        openConnection();
        try {
            statement = connection.prepareStatement(query);
        } catch(SQLException e) {
            throw new DBUtilsException("Error preparing statement", e);
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
        if(connection == null)
            connection = connectionProvider.openConnection();
    }

    /**
     * Close the prepared statement and connection.
     *
     * @return True if both successfully close, or false otherwise.
     * @throws DBUtilsException If any SQLException occurs when closing the
     * statement or connection.
     */
    public boolean close() throws DBUtilsException {
        logger.debug("[DBUtils] Closing connection to the database");
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
            statement.close();
            statement = null;
        } catch(SQLException e) {
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
            connection.close();
            connection = null;
        } catch(SQLException e) {
            throw new DBUtilsException("Error closing connection", e);
        }
        return true;
    }
}
