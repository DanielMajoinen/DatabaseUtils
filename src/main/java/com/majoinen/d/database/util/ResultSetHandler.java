package com.majoinen.d.database.util;

import com.majoinen.d.database.exception.DBUtilsException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle everything ResultSet related.
 *
 * @author Daniel Majoinen
 * @version 1.0, 22/7/17
 */
public class ResultSetHandler {

    private ResultSetHandler() { }

    public static <T> T handle(ResultSet resultSet, ObjectMapper<T> mapper)
      throws DBUtilsException {
        try {
            if(resultSet == null || resultSet.isClosed())
                return null;
            return mapper.map(resultSet);
        } catch(SQLException e) {
            throw new DBUtilsException(
              "[DBUtils] Error mapping results to object", e);
        } finally {
            closeResultSet(resultSet);
        }
    }

    public static <T> List<T> handleAll(ResultSet resultSet, ObjectMapper<T>
      mapper) throws DBUtilsException {
        List<T> list = new ArrayList<>();
        try {
            if(resultSet == null || resultSet.isClosed())
                return list;
            while(resultSet.next())
                list.add(mapper.map(resultSet));
        } catch(SQLException e) {
            throw new DBUtilsException(
              "[DBUtils] Error mapping results to list", e);
        } finally {
            closeResultSet(resultSet);
        }
        return list;
    }

    private static void closeResultSet(ResultSet resultSet) throws
      DBUtilsException {
        try {
            resultSet.close();
        } catch(SQLException e) {
            throw new DBUtilsException("[DBUtils] Error closing ResultSet", e);
        }
    }
}
