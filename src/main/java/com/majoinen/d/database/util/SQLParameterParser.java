package com.majoinen.d.database.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to work with SQL queries and their parameters.
 *
 * @author Daniel Majoinen
 * @version 1.0, 22/7/17
 */
public class SQLParameterParser {

    private SQLParameterParser() { }

    /* Replace occurrences of all parameter keys with a ? */
    public static String removeParameterKeys(String sql, Map<String, Object>
      parameters) {
        for (String key : parameters.keySet())
            sql = sql.replaceAll(key, "?");
        return sql;
    }

    /* Get a list of all parameter keys in the order they appear */
    public static List<String> getParameterKeys(String sql, Map<String, Object>
      parameters) {
        List<String> parameterKeys = new ArrayList<>();
        int fromIndex = 0;
        boolean finished = false;
        while(!finished) {
            // Reset each iteration
            int current = sql.length();
            String nextKey = null;
            // Find first occurrence of a key after previous key (if any)
            for (String key : parameters.keySet()) {
                final int index = sql.indexOf(key, fromIndex);
                final boolean beforeCurrent = index > 0 && index < current;
                if (beforeCurrent) {
                    current = index;
                    nextKey = key;
                }
            }
            // If a key was found add it to the list and shift fromIndex
            // Otherwise, end loop
            if(nextKey != null) {
                parameterKeys.add(nextKey);
                fromIndex = current + 1;
            } else {
                finished = true;
            }
        }
        return parameterKeys;
    }
}
