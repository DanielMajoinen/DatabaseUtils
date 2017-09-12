package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/9/17
 */
public class BatchQueryTest {

    private static final String SQL = "INSERT INTO table (key) VALUES (:value)";

    private static final String KEY = "key";

    private static final String VALUE = ":value";

    private static final int EXPECTED_AFFECTED_ROWS = 1;

    private BatchQuery query;

    @Mock
    private DBUtilsConnection connection;

    @Before
    public void beforeEachTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        query = new BatchQuery(connection, null);
    }

    @Test
    public void setParameterValid() throws Exception {
        assertEquals(query.setParameter(KEY, VALUE), query);
        assertTrue(query.getParameter(KEY).equals(VALUE));
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

    @Test
    public void prepareBatchQuery() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(true);
        when(connection.executeUpdate()).thenReturn(EXPECTED_AFFECTED_ROWS);

        query.setSql(SQL);
        assertEquals(query.setParameter(KEY, VALUE), query);
        assertEquals(query.prepareBatchQuery(SQL), query);
        assertEquals(query.executeUpdate(), EXPECTED_AFFECTED_ROWS * 2);
    }

    @Test(expected = DBUtilsException.class)
    public void executeUpdateThrowsException() throws Exception {
        when(connection.prepareStatement(anyString()))
          .thenThrow(DBUtilsException.class);

        query.setSql(SQL);
        query.executeUpdate();
    }

    @Test(expected = DBUtilsException.class)
    public void prepareBatchQueryThrowsException() throws Exception {
        when(connection.prepareStatement(anyString()))
          .thenThrow(DBUtilsException.class);

        query.setSql(SQL);
        query.prepareBatchQuery(SQL);
    }
}