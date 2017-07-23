package com.majoinen.d.database.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Majoinen
 * @version 1.0, 22/7/17
 */
public class SQLParameterParserTest {

    private static final String SQL =
      "INSERT INTO `table` ('id', 'name') VALUES (:id, :name)";

    private Map<String, Object> parameters;

    @Before
    public void beforeEachTest() {
        parameters = new HashMap<>();
        parameters.put(":name", "Daniel");
        parameters.put(":id", 1);
    }

    @Test
    public void removeParameterKeys() throws Exception {
        String expectedSql = "INSERT INTO `table` ('id', 'name') VALUES (?, ?)";

        assertTrue(SQLParameterParser
          .removeParameterKeys(SQL, parameters)
          .equals(expectedSql));
    }

    @Test
    public void getParameterKeys() throws Exception {
        List<String> parameterKeys = SQLParameterParser
          .getParameterKeys(SQL, parameters);

        assertTrue(parameterKeys.get(0).equals(":id"));
        assertTrue(parameterKeys.get(1).equals(":name"));
    }
}