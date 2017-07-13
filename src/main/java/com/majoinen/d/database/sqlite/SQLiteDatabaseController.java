package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.*;
import com.majoinen.d.database.exception.DBUtilsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * A database facade DAOs will use when communicating with an SQLite database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseController implements DatabaseController,
    ConnectionProvider {

    private static final Logger logger =
      LogManager.getLogger(SQLiteDatabaseController.class);

    private SQLiteDatabaseProperties properties;

    public SQLiteDatabaseController() throws DBUtilsException {
        this.properties = new SQLiteDatabaseProperties();
        new SQLiteDatabaseInitialiser(this, properties).init();
    }

    /**
     * Prepare a provided single query for execution.
     *
     * @param query The query to prepare. This is a String that follows SQL
     * syntax.
     * @return A Query object which can have parameters added to it and
     * provides ability to execute the query.
     * @throws DBUtilsException If any SQLExceptions occur preparing the
     * statement or creating the Query.
     */
    @Override
    public Query prepareQuery(String query) throws DBUtilsException {
        logger.debug("[DBUtils] Preparing single query");
        DBUtilsConnection connection = new DBUtilsConnection(this);
        connection.prepareStatement(query);
        return new Query(connection);
    }

    /**
     * Prepare a batch query, where all queries are dependent on each other.
     *
     * @param queries An unknown amount of queries to add to the batch queries
     * list. This is a list of Strings that follows SQL syntax.
     * @return A BatchQuery object which can have further queries, or
     * parameters added to it, and provides ability to execute queries.
     * @throws DBUtilsException If any SQLExceptions occur preparing the
     * statement or creating the BatchQuery.
     */
    @Override
    public BatchQuery prepareBatchQuery(List<String> queries) throws
      DBUtilsException {
        logger.debug("[DBUtils] Preparing list of batch queries");
        return prepareBatchQuery(queries.toArray(new String[0]));
    }

    /**
     * Prepare a batch query, where all queries are dependent on each other.
     *
     * @param queries An unknown amount of queries to add to the batch queries
     * list. This is a list of Strings that follows SQL syntax.
     * @return A BatchQuery object which can have further queries, or
     * parameters added to it, and provides ability to execute queries.
     * @throws DBUtilsException If any SQLExceptions occur preparing the
     * statement or creating the BatchQuery.
     */
    @Override
    public BatchQuery prepareBatchQuery(String... queries) throws
      DBUtilsException {
        logger.debug("[DBUtils] Preparing batch queries");
        DBUtilsConnection connection = new DBUtilsConnection(this);
        connection.prepareStatement(queries[0]);
        BatchQuery batchQuery = new BatchQuery(connection);
        for (int i = 1; i < queries.length - 1; i++)
            batchQuery.prepareBatchQuery(queries[i]);
        return batchQuery;
    }

    /**
     * Opens a connection to the database.
     *
     * @throws DBUtilsException If a database access error occurs. Ensure URL is
     * correct; if the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    @Override
    public synchronized Connection openConnection() throws DBUtilsException {
        logger.debug("[DBUtils] Opening connection to the database");
        try {
            return DriverManager.getConnection(
              SQLiteDatabaseProperties.DATABASE_TYPE_PREFIX +
                SQLiteDatabaseProperties.DATABASE_DIRECTORY +
                properties.getDatabaseName() +
                SQLiteDatabaseProperties.DATABASE_FILE_EXTENSION);
        } catch (SQLException e) {
            throw new DBUtilsException("Error opening connection", e);
        }
    }
}
