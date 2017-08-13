package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;

import java.util.List;

/**
 * Defines how data access objects will interact with the database irrespective
 * of the database types implementation.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public interface DatabaseController {

    /**
     * Initialise, verify and if needed create any missing tables.
     *
     * @throws DBUtilsException if any IOException occurs when accessing
     * database properties or SQLException when verifying or creating tables.
     */
    void init() throws DBUtilsException;

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
    Query prepareQuery(String query) throws DBUtilsException;

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
    BatchQuery prepareBatchQuery(String... queries) throws DBUtilsException;

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
    BatchQuery prepareBatchQuery(List<String> queries) throws DBUtilsException;

    void setProperty(String key, String value) throws DBUtilsException;
}
