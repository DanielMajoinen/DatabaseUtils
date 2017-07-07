package com.majoinen.d.database;

import java.io.IOException;
import java.sql.SQLException;

/**
 * The DatabaseInitialiser is responsible for creating/initialising the
 * database and potentially validating all tables.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public interface DatabaseInitialiser {

    /**
     * Verify the database. If the database does not exist, create it. If the
     * database fails verification for any reason, back up the database and
     * create a new database.
     * @throws SQLException If there was a database error when verifying a
     * table or connecting to the database.
     * @throws IOException If the database config file is not found or if there
     * are any permission issues when accessing the config file.
     */
    void init() throws SQLException, IOException;
}
