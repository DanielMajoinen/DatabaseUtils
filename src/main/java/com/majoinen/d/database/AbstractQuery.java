package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.exception.InsertException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides ability to execute and map results to objects for all query
 * subtypes, as well as close the PreparedStatement and Connection.
 *
 * @author Daniel Majoinen
 * @version 1.0, 11/7/17
 */
public abstract class AbstractQuery {

    private static final Logger logger =
      LogManager.getLogger(AbstractQuery.class);

    protected DBUtilsConnection connection;

    public AbstractQuery(DBUtilsConnection connection) {
        this.connection = connection;
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
            return connection.statement().executeUpdate();
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
            return connection.statement().executeQuery();
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
                return null;
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
