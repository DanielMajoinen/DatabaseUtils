package com.majoinen.d.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional interface for mapping the result of a query to
 * an object.
 *
 * @author Daniel Majoinen
 * @version 1.0, 10/7/17
 */
@FunctionalInterface
public interface ObjectMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}
