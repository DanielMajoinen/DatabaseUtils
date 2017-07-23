package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.database.sqlite.SQLiteConnectionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Majoinen
 * @version 1.0, 23/7/17
 */
public class DatabaseConnectionProviderFactory {

    private static Map<String, DatabaseConnectionProvider> providers;

    private DatabaseConnectionProviderFactory() { }

    public static DatabaseConnectionProvider getConnectionProvider(
      String databaseName) throws DBUtilsException {
        if(providers == null)
            providers = new HashMap<>();
        else if(providers.containsKey(databaseName))
            return providers.get(databaseName);

        DatabaseConnectionProvider provider = null;
        DatabaseType type = DatabaseProperties.getDatabaseType(databaseName);

        if(type.equals(DatabaseType.SQLITE)) {
            provider = new SQLiteConnectionProvider(databaseName);
        }

        providers.put(databaseName, provider);
        return provider;
    }

}
