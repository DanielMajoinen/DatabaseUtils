package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 15/7/17
 */
public class DBUtilsConnectionTest {

    private static final String VALID_QUERY =
      "CREATE TABLE `test_table` " +
      "(`id` int, " +
      "PRIMARY KEY(`id`))";

    private DBUtilsConnection dbUtilsConnection;
    @Mock private DatabaseConnectionProvider connectionProvider;

    @BeforeClass
    public static void beforeAll() throws Exception {
        if(!new File("test/").mkdirs())
            throw new Exception("Failed creating test db directory");
    }

    @AfterClass
    public static void afterAll() throws Exception {
        if(!new File("test/database.db").delete())
            throw new Exception("Failed deleting test db directory");
        if(!new File("test/").delete())
            throw new Exception("Failed deleting test db directory");
    }

    @Before
    public void beforeEachTest() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Mock necessary methods
        when(connectionProvider.openConnection())
          .thenReturn(getTestConnection());

        dbUtilsConnection = new DBUtilsConnection(connectionProvider);
    }

    private Connection getTestConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:test/database.db");
    }

    @Test
    public void disableAutoCommit() throws Exception {
        assertTrue(dbUtilsConnection.disableAutoCommit());
    }

    @Test(expected = DBUtilsException.class)
    public void disableAutoCommitOnClosedConnection() throws Exception {
        assertTrue(closeAPreparedStatement());
        dbUtilsConnection.disableAutoCommit();
    }

    @Test
    public void prepareValidStatement() throws Exception {
        assertTrue(dbUtilsConnection.prepareStatement(VALID_QUERY));
    }

    @Test(expected = DBUtilsException.class)
    public void prepareInvalidStatementNullQuery() throws Exception {
        dbUtilsConnection.prepareStatement(null);
    }

    @Test(expected = DBUtilsException.class)
    public void prepareInvalidStatementEmptyQuery() throws Exception {
        dbUtilsConnection.prepareStatement("");
    }

    @Test(expected = DBUtilsException.class)
    public void prepareValidStatementNullConnection() throws Exception {
        assertTrue(closeAPreparedStatement());
        dbUtilsConnection.prepareStatement(VALID_QUERY);
    }

    @Test
    public void close() throws Exception {
        assertTrue(closeAPreparedStatement());
        assertTrue(dbUtilsConnection.getStatement().isClosed());
        assertTrue(dbUtilsConnection.getConnection().isClosed());
    }

    @Test(expected = DBUtilsException.class)
    public void forceCloseStatementSQLException() throws Exception {
        assertTrue(dbUtilsConnection.prepareStatement(VALID_QUERY));
        dbUtilsConnection.getConnection().close();
        dbUtilsConnection.close();
    }

    private boolean closeAPreparedStatement() throws DBUtilsException {
        return dbUtilsConnection.prepareStatement(VALID_QUERY) &&
        dbUtilsConnection.close();
    }
}