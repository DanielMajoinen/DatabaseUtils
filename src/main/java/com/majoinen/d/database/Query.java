package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.exception.InsertException;
import com.majoinen.d.database.util.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds ability to add parameters to a query, execute an update or execute
 * and map results to an object.
 *
 * @author Daniel Majoinen
 * @version 1.0, 10/7/17
 */
public class Query {

    private DBUtilsConnection connection;

    public Query(DBUtilsConnection connection) {
        this.connection = connection;
    }

    protected DBUtilsConnection getDBUtilsConnection() {
        return connection;
    }

    /**
     * Add an unknown amount of parameters to the query.
     *
     * @param parameters The parameters to add.
     * @param <T> The type of the parameters.
     * @return The query object with parameters applied.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    public <T> Query addParameters(T... parameters) throws DBUtilsException {
        setParameters(parameters);
        return this;
    }

    /**
     * Add a list of parameters to the query.
     *
     * @param parameters The parameter list to add.
     * @param <T> The type of the parameters.
     * @return The query object with parameters applied.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    public <T> Query addParameters(List<T> parameters) throws DBUtilsException {
        setParameters(parameters.toArray());
        return this;
    }

    /**
     * Method which actually sets the parameters in the query, used by all
     * addParameters methods.
     *
     * @param parameters The parameter list to add.
     * @param <T> The type of the parameters.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    protected <T> void setParameters(T[] parameters) throws DBUtilsException {
        try {
            int i = 0;
            for (T parameter : parameters) {
                connection.getStatement().setObject(++i, parameter);
            }
        } catch(SQLException e) {
            throw new DBUtilsException("Error adding parameter to query", e);
        }
    }

    /**
     * Execute an update query.
     *
     * @return The amount of affected rows in the database.
     * @throws DBUtilsException If any SQLException occurs during the
     * execution of the query.
     */
    public int executeUpdate() throws DBUtilsException {
        try {
            return connection.getStatement().executeUpdate();
        } catch(SQLException e) {
            throw new InsertException(e);
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
        try {
            return connection.getStatement().executeQuery();
        } catch(SQLException e) {
            throw new DBUtilsException("Error executing query", e);
        }
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
        ResultSet resultSet = executeQuery();
        try {
            if(resultSet == null || resultSet.isClosed())
                return null;
            return mapper.map(resultSet);
        } catch(SQLException e) {
            throw new DBUtilsException("Error mapping results to object", e);
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
        ResultSet resultSet = executeQuery();
        List<T> list = new ArrayList<>();
        try {
            if(resultSet == null || resultSet.isClosed())
                return new ArrayList<>();
            while(resultSet.next())
                list.add(mapper.map(resultSet));
        } catch(SQLException e) {
            throw new DBUtilsException("Error mapping results to list", e);
        } finally {
            connection.close();
        }
        return list;
    }
}
