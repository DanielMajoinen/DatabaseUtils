package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.DatabaseProperties;
import com.majoinen.d.database.exception.InsertFailedException;

import java.io.IOException;
import java.sql.*;
import java.util.List;

/**
 * A database facade DAOs will use when communicating with an SQLite database.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseController implements DatabaseController {

    private DatabaseProperties databaseProperties;
    private Connection connection = null;
    private PreparedStatement statement = null;

    public SQLiteDatabaseController(Class<?> caller) {
        databaseProperties = SQLiteDatabaseProperties.getInstance(caller);
    }

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
    @Override
    public int insert(String query, List<?> vars) throws SQLException,
      IOException {
        prepareStatement(true, query);
        if (vars != null)
            setVariables(vars);
        return executeUpdate(true);
    }

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
    @Override
    public int insert(List<String> queries, List<List<?>> varsList) throws
      SQLException, IOException {
        int affectedRows = 0;
        try {
            openConnectionWithoutAutoCommit();
            affectedRows += executeInserts(queries, varsList);
        }
        catch(SQLException e) {
            connection.rollback();
            throw new InsertFailedException(e.getMessage());
        }
        finally {
            connection.commit();
            closeConnection();
        }
        return affectedRows;
    }

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
    @Override
    public ResultSet select(String query, List<?> vars) throws SQLException,
      IOException {
        prepareStatement(true, query);
        if(vars != null)
            setVariables(vars);
        return statement.executeQuery();
    }

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
    @Override
    public Object getObject(ResultSet resultSet, String columnLabel, Class<?>
      type, boolean closeConnection) throws SQLException {
        Object result = null;
        if(!resultSet.isClosed()) {
            result = resultSet.getObject(columnLabel);
            if(closeConnection)
                closeResultSetAndConnection(resultSet);
        }
        return type.cast(result);
    }

    /**
     * Closes a connection and ResultSet.
     * @param resultSet The ResultSet to close.
     * @throws SQLException If a database access error occurs when closing
     * the connection or the ResultSet.
     */
    private void closeResultSetAndConnection(ResultSet resultSet) throws
      SQLException {
        closeConnection();
        resultSet.close();
    }

    /**
     * Executes a list of queries which are dependent on each other. If one
     * fails, none of the queries will be executed.
     *
     * @param queries List of queries to insert.
     * @param varsList List of variables which match type the query expects.
     * One list per query is required.
     * @return The amount of affected rows.
     * @throws SQLException If parameterIndex does not correspond to a parameter
     * marker in the SQL statement; if a database access error occurs; this
     * method is called on a closed PreparedStatement or the type of the given
     * object is ambiguous.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    private int executeInserts(List<String> queries, List<List<?>> varsList)
      throws SQLException, IOException {
        int affectedRows = 0;
        for(String query : queries) {
            prepareStatement(false, query);
            if(varsList != null) {
                List<?> vars = varsList.remove(0);
                setVariables(vars);
            }
            affectedRows += executeUpdate(false);
        }
        return affectedRows;
    }

    /**
     * Executes a statement, used when inserting.
     *
     * @param closeConnection Whether to close the connection after the
     * execution or not.
     * @return The amount of affected rows.
     * @throws SQLException If a database access error occurs; this method is
     * called on a closed PreparedStatement or the SQL statement returns a
     * ResultSet object.
     */
    private int executeUpdate(boolean closeConnection) throws SQLException {
        int affectedRows = statement.executeUpdate();
        if(closeConnection)
            closeConnection();
        return affectedRows;
    }

    /**
     * Sets variables of a query which has already been prepared.
     *
     * @param vars List of variables which match type the query expects.
     * @throws SQLException If parameterIndex does not correspond to a parameter
     * marker in the SQL statement; if a database access error occurs; this
     * method is called on a closed PreparedStatement or the type of the given
     * object is ambiguous.
     */
    private void setVariables(List<?> vars) throws SQLException {
        for(int i = 0; i < vars.size(); i++)
            statement.setObject(i + 1, vars.get(i));
    }

    /**
     * Prepares a statement with the provided query ready to be executed.
     * Also allows the option to open the connection prior.
     *
     * @param openConnection Whether to open a connection first or not.
     * @param query Query which is prepared for execution.
     * @throws SQLException If a database access error occurs or this method is
     * called on a closed connection.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    private void prepareStatement(boolean openConnection, String query) throws
      SQLException, IOException {
        if(openConnection)
            openConnection();
        statement = connection.prepareStatement(query);
    }

    /**
     * Allow the option to open a connection without enabling auto commit.
     * This is used when inserting multiple queries at the same time that are
     * dependent on each other.
     *
     * @throws SQLException If a database access error occurs or this method is
     * called on a closed connection.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    private void openConnectionWithoutAutoCommit() throws SQLException,
      IOException {
        openConnection();
        connection.setAutoCommit(false);
    }

    /**
     * Opens a connection to the database.
     *
     * @throws SQLException If a database access error occurs. Ensure URL is
     * correct.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the file.
     */
    private void openConnection() throws SQLException, IOException {
        connection = DriverManager.getConnection(
          SQLiteDatabaseProperties.DATABASE_TYPE_PREFIX +
            SQLiteDatabaseProperties.DATABASE_DIRECTORY +
            databaseProperties.getDatabaseName() +
            SQLiteDatabaseProperties.DATABASE_FILE_EXTENSION);
    }

    /**
     * Closes the statement and connection, then setting them to null for
     * future use.
     *
     * @throws SQLException SQLException can be thrown when either the
     * statement or connection fails to close for any reason.
     */
    private void closeConnection() throws SQLException {
        statement.close();
        connection.close();
        connection = null;
        statement = null;
    }
}
