package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;

/**
 * Provide ability to do batch queries. A batch query requires all queries to
 * succeed for any to apply, failure of any query will rollback the database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 11/7/17
 */
public class BatchQuery extends Query {

    private int affectedRows;

    public BatchQuery(DBUtilsConnection connection, String sql) throws
      DBUtilsException {
        super(connection, sql);
        connection.disableAutoCommit();
    }

    /**
     * Set a parameter by providing its substring in the sql and its
     * desired value.
     *
     * @param key a substring within the sql query which will be replaced
     * with the supplied value using setObject().
     * @param value the value for the supplied key.
     * @param <T> The type of the value.
     * @return the BatchQuery with the parameter added.
     */
    @Override
    public <T> BatchQuery setParameter(String key, T value) {
        super.setParameter(key, value);
        return this;
    }

    /**
     * Set an encrypted parameter by providing its substring in the sql and its
     * desired value.
     *
     * @param key a substring within the sql query which will be replaced
     * with the supplied value using setObject().
     * @param value the value for the supplied key, which will be encrypted.
     * @param <T> The type of the value.
     * @return the BatchQuery with the parameter added.
     */
    @Override
    public <T> BatchQuery setEncryptedParameter(String key, T value) {
        super.setEncryptedParameter(key, value);
        return this;
    }

    /**
     * Execute the current query, and prepare a new query. This method is
     * only accessible after a query has already been prepared.
     *
     * @param sql The next query to prepare for execution.
     * @return The BatchQuery with the new query prepared for execution.
     * @throws DBUtilsException If an SQLException occurs preparing the
     * statement with the provided query.
     */
    public BatchQuery prepareBatchQuery(String sql) throws DBUtilsException {
        try {
            super.setSql(sql);
            super.prepareStatementWithParameters();
            affectedRows += super.getDBUtilsConnection().executeUpdate();
            super.getDBUtilsConnection().prepareStatement(sql);
        } catch(DBUtilsException e) {
            super.getDBUtilsConnection().rollback();
            super.getDBUtilsConnection().close();
            throw new DBUtilsException(
              "[DBUtils] Error executing batch update", e);
        }
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
            super.prepareStatementWithParameters();
            return affectedRows + super.getDBUtilsConnection().executeUpdate();
        } catch(DBUtilsException e) {
            super.getDBUtilsConnection().rollback();
            throw new DBUtilsException(
              "[DBUtils] Error executing batch update", e);
        } finally {
            super.getDBUtilsConnection().commit();
            super.getDBUtilsConnection().close();
        }
    }
}
