package com.majoinen.d.database;

import com.majoinen.d.database.exception.DatabaseBackupException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * Run a single INSERT query on the database.
     *
     * @param query Insert query to execute.
     * @param vars List of variables which match the type the query expects.
     * @return The amount of affected rows.
     * @throws SQLException If parameterIndex does not correspond to a parameter
     * marker in the SQL statement; if a database access error occurs; this
     * method is called on a closed PreparedStatement; the type of the given
     * object is ambiguous or the SQL statement returns a ResultSet object.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    int insert(String query, List<?> vars) throws SQLException, IOException;

    /**
     * Executes a list of queries which are dependent on each other. If one
     * fails, none of the queries will be executed.
     *
     * @param queries List of insert query to execute.
     * @param varsList 2D List of variables which must match the type the query
     * expects.
     * @return The amount of affected rows.
     * @throws SQLException If parameterIndex does not correspond to a parameter
     * marker in the SQL statement; if a database access error occurs; this
     * method is called on a closed PreparedStatement; the type of the given
     * object is ambiguous or the SQL statement returns a ResultSet object.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    int insert(List<String> queries, List<List<?>> varsList) throws
      SQLException, IOException;

    /**
     * Execute a SELECT query with provided parameters on the database.
     *
     * @param query Select query to execute.
     * @param vars List of variables which must match the type the query
     * expects.
     * @return Returns the queries results in the form of a ResultSet.
     * @throws SQLException If parameterIndex does not correspond to a parameter
     * marker in the SQL statement; if a database access error occurs; this
     * method is called on a closed PreparedStatement; the type of the given
     * object is ambiguous or the SQL statement does not return a ResultSet
     * object.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    ResultSet select(String query, List<?> vars) throws SQLException, IOException;

    /**
     * Retrieves the value of the designated column in the current row of this
     * ResultSet object and will convert from the SQL type of the column to the
     * requested Java data type, if the conversion is supported. If the
     * conversion is not supported or null is specified for the type, a
     * SQLException is thrown.
     * @param resultSet The ResultSet object to retrieve the value from.
     * @param columnLabel The name of the column.
     * @param type Class representing the Java data type to convert the
     * designated column to.
     * @param closeConnection Whether to close the ResultSet and Connection
     * or not.
     * @return an instance of type holding the column value.
     * @throws SQLException if conversion is not supported, type is null or
     * another error occurs.
     */
    Object getObject(ResultSet resultSet, String columnLabel, Class<?> type,
      boolean closeConnection) throws SQLException;

    /**
     * Backups the database.
     *
     * @return True if successfully backups the database, or false otherwise.
     * @throws DatabaseBackupException If any error occurs during the backup
     * process.
     */
    boolean backupDatabase() throws DatabaseBackupException;
}
