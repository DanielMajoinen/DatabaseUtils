package com.majoinen.d.database.util;

import com.majoinen.d.database.BatchQuery;
import com.majoinen.d.database.Query;
import com.majoinen.d.database.sqlite.SQLiteDatabaseController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 15/8/17
 */
public class SQLFileExecutorTest {

    @Mock private SQLiteDatabaseController databaseController;
    @Mock private BatchQuery batchQuery;
    @Mock private Query query;

    private SQLFileExecutor executor;

    @Before
    public void beforeEachTest() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(databaseController.prepareBatchQuery(anyList()))
          .thenReturn(batchQuery);
        when(databaseController.prepareQuery(anyString()))
          .thenReturn(query);

        executor = SQLFileExecutor.getInstance(databaseController);
    }

    @Test(expected =  NullPointerException.class)
    public void getSQLMissingRequiredFile() throws Exception {
        executor.getSQLFileContents("missing_file", true);
    }

    @Test
    public void getTableSQLMissingNotRequiredFile() throws Exception {
        assertTrue(executor.getSQLFileContents("missing_file", false) == null);
    }

    @Test
    public void getTableSQLEmptyNotRequiredFile() throws Exception {
        assertTrue(executor.getSQLFileContents("test_empty", false) == null);
    }

    @Test
    public void getTableSQLValid() throws Exception {
        String expected = "CREATE TABLE `test_table`\n" +
          "    (`id` int,\n" +
          "     `uuid` TEXT NOT NULL,\n" +
          "     PRIMARY KEY(`id`))";

        assertTrue(expected.equals(executor.getSQLFileContents("test_table",
          true)));
    }

    @Test
    public void executeSQLFileNullFile() throws Exception {
        assertTrue(!executor.executeFile("missing_file"));
    }

    @Test
    public void executeSQLFileEmptyQuery() throws Exception {
        assertTrue(!executor.executeFile("test_empty_query"));
    }

    @Test
    public void executeSQLFileValidRequired() throws Exception {
        when(batchQuery.executeUpdate()).thenReturn(1);
        assertTrue(executor.executeRequiredFile("test_table"));
    }

    @Test
    public void executeSQLFileValidNotRequired() throws Exception {
        when(batchQuery.executeUpdate()).thenReturn(1);
        assertTrue(executor.executeFile("test_table"));
    }
}