package com.majoinen.d.database.util;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.LogManager;
import com.majoinen.d.database.log.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle everything ResultSet related.
 *
 * @author Daniel Majoinen
 * @version 1.0, 22/7/17
 */
public final class ResultSetHandler {

    private static final Logger logger =
      LogManager.getLogger(ResultSetHandler.class);

    private ResultSetHandler() { }

    public static <T> T handle(ResultSet resultSet, ObjectMapper<T> mapper)
      throws DBUtilsException {
        logger.debug("Handling ResultSet");
        try {
            if(resultSet == null || resultSet.isClosed()) {
                logger.debug("ResultSet null or closed");
                return null;
            }
            logger.debug("Moving cursor to first row");
            resultSet.next();
            logger.debug("Mapping to object");
            return mapper.map(resultSet);
        } catch(SQLException e) {
            throw new DBUtilsException("Error mapping results to object", e);
        } finally {
            logger.debug("Closing ResultSet");
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
            throw new DBUtilsException("Error mapping results to list", e);
        } finally {
            closeResultSet(resultSet);
        }
        return list;
    }

    private static void closeResultSet(ResultSet resultSet) throws
      DBUtilsException {
        try {
            if(resultSet != null && !resultSet.isClosed())
                resultSet.close();
        } catch(SQLException e) {
            throw new DBUtilsException("Error closing ResultSet", e);
        }
    }
}
