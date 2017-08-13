package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.exception.DBUtilsException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Majoinen
 * @version 1.0, 13/8/17
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { FileUtils.class })
@PowerMockIgnore("javax.management.*")
public class SQLiteDatabaseInitialiserTest {

    @Mock private SQLiteDatabaseController databaseController;

    private SQLiteDatabaseInitialiser initialiser;

    @Before
    public void beforeEachTest() throws Exception {
        MockitoAnnotations.initMocks(this);

        initialiser = SQLiteDatabaseInitialiser.getInstance(databaseController);
    }

    @Test(expected =  NullPointerException.class)
    public void getTableSQLMissingRequiredFile() throws Exception {
        initialiser.getTableSQL("missing_file", true);
    }

    @Test
    public void getTableSQLMissingNotRequiredFile() throws Exception {
        assertTrue(initialiser.getTableSQL("missing_file", false) == null);
    }

    @Test
    public void getTableSQLEmptyNotRequiredFile() throws Exception {
        assertTrue(initialiser.getTableSQL("test_empty", false) == null);
    }

    @Test(expected = DBUtilsException.class)
    public void getTableSQLIOException() throws Exception {
        String tableName = "test_table";
        String filename = "/sql/"+tableName+".sql";
        File file = new File(getClass().getResource(filename).getPath());

        // Mock IOException when reading file
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.doThrow(new IOException()).when(FileUtils.class,
          "readFileToString", file, "UTF-8");

        initialiser.getTableSQL(tableName, true);
    }

    @Test
    public void getTableSQL() throws Exception {
        String expected = "CREATE TABLE `test_table`\n" +
          "    (`id` int,\n" +
          "     `uuid` TEXT NOT NULL,\n" +
          "     PRIMARY KEY(`id`))";

        assertTrue(expected.equals(initialiser.getTableSQL("test_table",
          true)));
    }
}