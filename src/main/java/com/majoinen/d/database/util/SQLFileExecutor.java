package com.majoinen.d.database.util;

import com.majoinen.d.database.SQLDatabaseController;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.LogManager;
import com.majoinen.d.database.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Daniel Majoinen
 * @version 1.0, 15/8/17
 */
public class SQLFileExecutor {

    private static final Logger logger =
      LogManager.getLogger(SQLFileExecutor.class);

    // The resource folder all sql files are located
    private static final String SQL_RESOURCE_DIR = "/sql/";

    // The delimiter used to separate queries in sql files
    private static final String QUERY_DELIMITER = ";";

    // The file extension of sql files
    private static final String SQL_FILE_EXTENSION = ".sql";

    private static Map<SQLDatabaseController, SQLFileExecutor> map;

    private SQLDatabaseController databaseController;

    private SQLFileExecutor(SQLDatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public static SQLFileExecutor getInstance(SQLDatabaseController
      databaseController) {
        if(map == null)
            map = new HashMap<>();
        else if(map.containsKey(databaseController))
            return map.get(databaseController);
        SQLFileExecutor executor = new SQLFileExecutor(databaseController);
        map.put(databaseController, executor);
        return executor;
    }

    public boolean executeFile(String filename) throws DBUtilsException {
        return executeFile(filename, false);
    }

    public boolean executeRequiredFile(String filename)
      throws DBUtilsException {
        return executeFile(filename, true);
    }

    /**
     * Executes all queries found in the specified resources/sql file.
     * Queries should be separated with a semi-colon.
     *
     * @param filename Name of the file to execute.
     * @return True if all the queries were successful.
     * @throws DBUtilsException If there was a database error executing the
     * queries; if the databases config file is not found or if there
     * are any read/write permission issues when accessing files.
     */
    private boolean executeFile(String filename, boolean required)
      throws DBUtilsException {
        String file = getSQLFileContents(filename, required);
        if(file != null) {
            String[] queries = file.split(QUERY_DELIMITER);
            List<String> validQueries = new ArrayList<>();
            for (String query : queries) {
                if (query != null && query.length() != 0)
                    validQueries.add(query);
            }
            return !validQueries.isEmpty() && databaseController
              .prepareBatchQuery(validQueries)
              .executeUpdate() > 0;
        } else {
            String completeFilename = SQL_RESOURCE_DIR
              .concat(filename)
              .concat(SQL_FILE_EXTENSION);
            logger.info("Skipping resources" + completeFilename +
              ": File not found");
            return false;
        }
    }

    /**
     * Gets a specified files sql file contents from the resource folder and
     * returns it in the form of a string.
     *
     * @param filename The name of the file to retrieve contents of.
     * @param required Whether to throw an exception if the sql file does not
     * exist or not.
     * @return The contents of the files sql file as a String.
     * @throws DBUtilsException If there is any issue accessing the files
     * contents.
     */
    String getSQLFileContents(String filename, boolean required)
      throws DBUtilsException {
        String file = SQL_RESOURCE_DIR
          .concat(filename)
          .concat(SQL_FILE_EXTENSION);

        try (InputStream is = getClass().getResourceAsStream(file)) {
            if (is == null && required) {
                logger.error("Sql file missing for file: "+filename);
                throw new NullPointerException("resources"+file+" is missing");
            } else if (is == null) {
                return null;
            }

            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            String contents = null;
            if (scanner.hasNext()) {
                contents = scanner.next();
            } else if (!scanner.hasNext() && required) {
                logger.error("resources"+ file +" is empty");
                throw new NullPointerException("resources"+ file +" is empty");
            } else {
                logger.debug("resources"+ file +" is empty");
            }
            return contents;
        } catch(IOException e) {
            throw new DBUtilsException("IOException reading resource file" +
              file, e);
        }
    }

}
