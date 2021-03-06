package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.Logger;
import com.majoinen.d.database.log.LogManager;

import java.util.List;

/**
 * @author Daniel Majoinen
 * @version 1.0, 23/7/17
 */
public abstract class SQLDatabaseController implements DatabaseController {

    private static final Logger logger =
      LogManager.getLogger(SQLDatabaseController.class);

    private final String databaseName;
    private final String configFilename;

    public SQLDatabaseController(String databaseName, String filename) {
        this.databaseName = databaseName;
        this.configFilename = filename;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getConfigFilename() {
        return configFilename;
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
        logger.debug("Preparing single query");
        return new Query(new DBUtilsConnection(DatabaseConnectionProviderFactory
            .getConnectionProvider(databaseName, configFilename)), query);
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
        logger.debug("Preparing list of batch queries");
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
        logger.debug("Preparing batch queries");
        DBUtilsConnection connection = new DBUtilsConnection(
          DatabaseConnectionProviderFactory.getConnectionProvider(databaseName,
            configFilename));
        BatchQuery batchQuery = new BatchQuery(connection, queries[0]);
        for (int i = 1; i < queries.length - 1; i++)
            batchQuery.prepareBatchQuery(queries[i]);
        return batchQuery;
    }
}
