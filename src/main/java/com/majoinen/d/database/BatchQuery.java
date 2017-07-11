package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.exception.InsertException;

import java.sql.SQLException;
import java.util.List;

/**
 * Concrete AbstractQuery subclass which adds ability to do batch queries. A
 * batch query requires all queries to succeed for any to apply, failure of
 * any query will rollback the database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 11/7/17
 */
public class BatchQuery extends Query {

    private int affectedRows;

    public BatchQuery(DBUtilsConnection connection) throws DBUtilsException {
        super(connection);
        connection.disableAutoCommit();
    }

    /**
     * Execute the current query, and prepare a new query. This method is
     * only accessible after a query has already been prepared.
     *
     * @param query The next query to prepare for execution.
     * @return The BatchQuery with the new query prepared for execution.
     * @throws DBUtilsException If an SQLException occurs preparing the
     * statement with the provided query.
     */
    public BatchQuery prepareBatchQuery(String query) throws DBUtilsException {
        try {
            executeBatchUpdate();
            dbUtilsConnection().connection().prepareStatement(query);
        } catch(DBUtilsException e) {
            rollbackConnection();
            dbUtilsConnection().close();
            throw new DBUtilsException("Error executing batch update", e);
        } catch(SQLException e) {
            throw new DBUtilsException("Error preparing query", e);
        }
        return this;
    }

    /**
     * Add an unknown amount of parameters to the query.
     *
     * @param parameters The parameters to add.
     * @param <T> The type of the parameters.
     * @return This object with parameters applied.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    @Override
    public <T> BatchQuery addParameters(T... parameters) throws
      DBUtilsException {
        super.addParameters(parameters);
        return this;
    }

    /**
     * Add a list of parameters to the query.
     *
     * @param parameters The parameter list to add.
     * @param <T> The type of the parameters.
     * @return This object with parameters applied.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    @Override
    public <T> BatchQuery addParameters(List<T> parameters) throws
      DBUtilsException {
        super.addParameters(parameters);
        return this;
    }

    /**
     * Executes a batch query update. If the update fails for any reason the
     * connection will be rolled back. The connection and prepared statement
     * are then closed on return.
     *
     * @return The total affected rows of all queries processed.
     * @throws DBUtilsException If any SQLException occurs executing the update.
     */
    @Override
    public int executeUpdate() throws DBUtilsException {
        try {
            executeBatchUpdate();
            return affectedRows;
        } catch(DBUtilsException e) {
            rollbackConnection();
            throw new DBUtilsException("Error executing batch update", e);
        } finally {
            commitConnection();
            dbUtilsConnection().close();
        }
    }

    /**
     * Executes the current PreparedStatement, adding the affected rows to
     * the running total.
     *
     * @throws DBUtilsException If any SQLException occurs executing the update.
     */
    private void executeBatchUpdate() throws DBUtilsException {
        try {
            affectedRows += dbUtilsConnection().statement().executeUpdate();
        } catch(SQLException e) {
            throw new InsertException(e);
        }
    }

    /**
     * Commit a connection, providing exception handling.
     * s
     * @throws DBUtilsException If any SQLException occurs committing the
     * connection.
     */
    private void commitConnection() throws DBUtilsException {
        try {
            dbUtilsConnection().connection().commit();
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
            dbUtilsConnection().connection().rollback();
        } catch(SQLException e) {
            throw new DBUtilsException("Error rolling back connection", e);
        }
    }
}
