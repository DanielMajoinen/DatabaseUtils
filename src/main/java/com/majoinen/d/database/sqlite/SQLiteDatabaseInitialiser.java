package com.majoinen.d.database.sqlite;

import com.majoinen.d.database.DatabaseInitialiser;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.log.LogManager;
import com.majoinen.d.database.log.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Determines if the database does not already exist, and creates it when
 * needed. A corresponding database file should be located in the
 * resources/databases directory.
 *
 *
 * Initialises a SQLite database and populates it with tables. If the
 * database exists it will verify all tables schema matches the corresponding
 * sql file.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLiteDatabaseInitialiser implements DatabaseInitialiser {

    private static final Logger logger =
      LogManager.getLogger(SQLiteDatabaseInitialiser.class);

    // The resource folder all databases are located
    private static final String DB_RESOURCE_DIR = "/databases/";

    private static Map<SQLiteDatabaseController, SQLiteDatabaseInitialiser> map;
    private SQLiteDatabaseController databaseController;

    private SQLiteDatabaseInitialiser(SQLiteDatabaseController controller) {
        this.databaseController = controller;
    }

    static SQLiteDatabaseInitialiser getInstance(SQLiteDatabaseController
      databaseController) {
        if(map == null)
            map = new HashMap<>();
        else if(map.containsKey(databaseController))
            return map.get(databaseController);
        SQLiteDatabaseInitialiser initialiser =
          new SQLiteDatabaseInitialiser(databaseController);
        map.put(databaseController, initialiser);
        return initialiser;
    }

    /**
     * Verify the database exists. If the database does not exist, create it.
     *
     * @throws DBUtilsException If there was a database error when verifying a
     * table or connecting to the database; If the database config file is not
     * found or if there are any permission issues when accessing the config
     * file.
     */
    @Override
    public void init() throws DBUtilsException {
        String configFilename = databaseController.getConfigFilename();
        File directory = new File(SQLiteDatabaseProperties
          .getDatabaseDirectory(configFilename));
        logger.debug("Database directory: " + directory.getAbsolutePath());
        String filename = DB_RESOURCE_DIR
          .concat(configFilename)
          .concat(".")
          .concat(SQLiteDatabaseProperties.getDatabaseFileExtension(
            configFilename));
        if(!directory.exists() && !directory.mkdirs())
            throw new DBUtilsException("Error creating db directory");
        if(!new File(databaseController.getDatabaseAbsolutePath()).exists()) {
            logger.debug("Creating database: " + filename);
            createDatabase(filename);
            logger.debug("Database created successfully");
        } else
            logger.debug("Database already exists, skip creation.");
    }

    /**
     * Makes a copy of the source database in the desired location.
     *
     * @param filename Name of the database file in resources/databases
     * @throws DBUtilsException If any IOException occurs reading the
     * source database or writing to the destination.
     */
    private void createDatabase(String filename) throws DBUtilsException {
        try (InputStream in = getClass().getResourceAsStream(filename)) {
            if(in == null)
                throw new NullPointerException("[DBUtils] resources" +
                  filename + " is missing");
            try (OutputStream out = new FileOutputStream(new File(
              databaseController.getDatabaseAbsolutePath()))) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);
            } catch (IOException e) {
                throw new DBUtilsException("Error writing to file", e);
            }
        } catch (IOException e) {
            throw new DBUtilsException("Error reading file", e);
        }
    }
}
