package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.BatchQuery;
import com.majoinen.d.database.Query;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 13/8/17
 */
public class SQLiteDatabaseInitialiserTest {

    @Mock private SQLiteDatabaseController databaseController;
    @Mock private BatchQuery batchQuery;
    @Mock private Query query;

    private SQLiteDatabaseInitialiser initialiser;

    @Before
    public void beforeEachTest() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(databaseController.getConfigFilename())
          .thenReturn("test_sqlite_config");
        when(databaseController.getDatabaseName())
          .thenReturn("test_database");

        initialiser = SQLiteDatabaseInitialiser.getInstance(databaseController);
    }
}