package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.LogManager;
import com.majoinen.d.database.log.Logger;
import com.majoinen.d.database.util.ObjectMapper;
import com.majoinen.d.database.util.ResultSetHandler;
import com.majoinen.d.database.util.SQLParameterParser;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds ability to add parameters to a query, execute an update or execute
 * and map results to an object.
 *
 * @author Daniel Majoinen
 * @version 1.0, 10/7/17
 */
public class Query {

    private static final Logger logger = LogManager.getLogger(Query.class);

    private final DBUtilsConnection connection;
    private String sql;
    private Map<String, Object> parameters;

    public Query(DBUtilsConnection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
        this.parameters = new HashMap<>();
    }

    /**
     * When converting this object to a string, return the sql query.
     *
     * @return the sql query.
     */
    @Override
    public String toString() {
        return sql;
    }

    /**
     * Getter for the sql query.
     *
     * @return the sql query.
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setter for sql, used by subclasses.
     *
     * @param sql the new sql query.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Getter for the DBUtilsConnection.
     *
     * @return the DBUtilsConnection associated with this query.
     */
    public DBUtilsConnection getDBUtilsConnection() {
        return connection;
    }

    /**
     * Set a parameter by providing its substring in the sql and its value.
     *
     * @param key a substring within the sql query which will be replaced
     * with the supplied value using setObject().
     * @param value the value for the supplied key.
     * @param <T> The type of the value.
     * @return the Query with the parameter added.
     */
    public <T> Query setParameter(String key, T value) {
        parameters.put(key, value);
        return this;
    }

    /**
     * Execute an update query, returning the value of affected rows.
     *
     * @return The amount of affected rows caused by the query.
     * @throws DBUtilsException If any SQLException occurs during the
     * execution of the query.
     */
    public int executeUpdate() throws DBUtilsException {
        prepareStatementWithParameters();
        try {
            return connection.executeUpdate();
        } finally {
            connection.close();
        }
    }

    /**
     * Executes a query and provides the resulting values from the database.
     *
     * @return The queries results in the form of a ResultSet.
     * @throws DBUtilsException If any SQLException occurs executing the query.
     */
    private ResultSet executeQuery() throws DBUtilsException {
        prepareStatementWithParameters();
        return connection.executeQuery();
    }

    /**
     * Handles flow of preparing a statement with parameters.
     *
     * @throws DBUtilsException If any SQLException occurs while preparing the
     * statement or setting parameters.
     */
    protected void prepareStatementWithParameters() throws DBUtilsException {
        connection.prepareStatement(SQLParameterParser
          .removeParameterKeys(sql, parameters));
        if(!parameters.isEmpty())
            setParameters(SQLParameterParser.getParameterKeys(sql, parameters));
        parameters.clear();
    }

    /**
     * Method which actually sets the parameters in the query, used by all
     * addParameters methods.
     *
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    protected boolean setParameters(List<String> parameterKeys) throws
      DBUtilsException {
        int i = 1;
        for (String parameter : parameterKeys)
            connection.setObject(i++, parameters.get(parameter));
        return true;
    }

    /**
     * Executes a query and provides the resulting values mapped to an object.
     *
     * @return The queries results mapped as an object, handled by the
     * ObjectMapper provided.
     * @throws DBUtilsException If any SQLException occurs executing the
     * query or mapping the object.
     */
    public <T> T executeAndMap(ObjectMapper<T> mapper) throws DBUtilsException {
        logger.debug("Executing query and mapping to object");
        try {
            return ResultSetHandler.handle(executeQuery(), mapper);
        } finally {
            connection.close();
        }
    }

    /**
     * Executes a query and provides the resulting list of values mapped to an
     * object.
     *
     * @return The queries results mapped as an object in a List, handled by the
     * ObjectMapper provided.
     * @throws DBUtilsException If any SQLException occurs executing the
     * query or mapping the object.
     */
    public <T> List<T> executeAndMapAll(ObjectMapper<T> mapper) throws
      DBUtilsException {
        try {
            return ResultSetHandler.handleAll(executeQuery(), mapper);
        } finally {
            connection.close();
        }
    }
}
