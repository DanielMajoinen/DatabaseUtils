package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.util.ObjectMapper;
import com.majoinen.d.database.util.ResultSetHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/9/17
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ResultSetHandler.class)
public class QueryTest {

    private static final String SQL = "INSERT INTO table (key) VALUES (:value)";

    private static final String KEY = "key";

    private static final String VALUE = ":value";

    private static final int EXPECTED_AFFECTED_ROWS = 1;

    private static final String EXPECTED_MAP = "result";

    private static List<String> EXPECTED_MAP_LIST = new ArrayList<>();

    private Query query;

    @Mock
    private DBUtilsConnection connection;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ObjectMapper<String> stringMapper;

    @Before
    public void beforeEachTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        query = new Query(connection, null);
    }

    @Test
    public void toStringEqual() throws Exception {
        query.setSql(SQL);
        assertEquals(query.toString(), SQL);
    }

    @Test
    public void setSQL() throws Exception {
        assertNull(query.getSql());
        query.setSql(SQL);
        assertEquals(query.getSql(), SQL);
    }

    @Test
    public void getDBUtilsConnection() throws Exception {
        assertTrue(query.getDBUtilsConnection().equals(connection));
    }

    @Test
    public void setParameterValid() throws Exception {
        assertEquals(query.setParameter(KEY, VALUE), query);
        assertTrue(query.getParameter(KEY).equals(VALUE));
    }

    @Test(expected = NullPointerException.class)
    public void getParameterInvalid() throws Exception {
        assertEquals(query.setParameter(KEY, VALUE), query);
        query.getParameter(VALUE);
    }

    @Test
    public void executeUpdateEmptyParameters() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeUpdate()).thenReturn(EXPECTED_AFFECTED_ROWS);

        query.setSql(SQL);
        assertEquals(query.executeUpdate(), EXPECTED_AFFECTED_ROWS);
    }

    @Test
    public void executeUpdateValid() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeUpdate()).thenReturn(EXPECTED_AFFECTED_ROWS);

        query.setSql(SQL);
        assertEquals(query.setParameter(KEY, VALUE), query);
        assertEquals(query.executeUpdate(), EXPECTED_AFFECTED_ROWS);
    }

    @Test(expected = DBUtilsException.class)
    public void executeUpdateThrowsException() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeUpdate()).thenThrow(DBUtilsException.class);

        query.setSql(SQL);
        assertEquals(query.setParameter(KEY, VALUE), query);
        query.executeUpdate();
    }

    @Test
    public void executeAndMap() throws Exception {
        PowerMockito.mockStatic(ResultSetHandler.class);
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeQuery()).thenReturn(resultSet);
        when(ResultSetHandler.handle(resultSet, stringMapper))
          .thenReturn(EXPECTED_MAP);

        assertEquals(query.executeAndMap(stringMapper), EXPECTED_MAP);
    }

    @Test(expected = DBUtilsException.class)
    public void executeAndMapThrowsException() throws Exception {
        PowerMockito.mockStatic(ResultSetHandler.class);
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeQuery()).thenReturn(resultSet);
        when(ResultSetHandler.handle(resultSet, stringMapper))
          .thenThrow(DBUtilsException.class);

        query.executeAndMap(stringMapper);
    }

    @Test
    public void executeAndMapAll() throws Exception {
        EXPECTED_MAP_LIST.add(EXPECTED_MAP);

        PowerMockito.mockStatic(ResultSetHandler.class);
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeQuery()).thenReturn(resultSet);
        when(ResultSetHandler.handleAll(resultSet, stringMapper))
          .thenReturn(EXPECTED_MAP_LIST);

        assertEquals(query.executeAndMapAll(stringMapper), EXPECTED_MAP_LIST);
    }

    @Test(expected = DBUtilsException.class)
    public void executeAndMapAllThrowsException() throws Exception {
        EXPECTED_MAP_LIST.add(EXPECTED_MAP);

        PowerMockito.mockStatic(ResultSetHandler.class);
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeQuery()).thenReturn(resultSet);
        when(ResultSetHandler.handleAll(resultSet, stringMapper))
          .thenThrow(DBUtilsException.class);

        query.executeAndMapAll(stringMapper);
    }
}