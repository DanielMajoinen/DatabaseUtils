package com.majoinen.d.database;

import com.majoinen.d.database.exception.DBUtilsException;

import java.sql.SQLException;
import java.util.List;

/**
 * Concrete AbstractQuery subclass that adds ability to set parameters of the
 * query.
 *
 * @author Daniel Majoinen
 * @version 1.0, 10/7/17
 */
public class Query extends AbstractQuery {

    public Query(DBUtilsConnection connection) {
        super(connection);
    }

    /**
     * Add an unknown amount of parameters to the query.
     *
     * @param parameters The parameters to add.
     * @param <T> The type of the parameters.
     * @return The query object with parameters applied.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    public <T> Query addParameters(T... parameters) throws DBUtilsException {
        setParameters(parameters);
        return this;
    }

    /**
     * Add a list of parameters to the query.
     *
     * @param parameters The parameter list to add.
     * @param <T> The type of the parameters.
     * @return The query object with parameters applied.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    public <T> Query addParameters(List<T> parameters) throws DBUtilsException {
        setParameters(parameters.toArray());
        return this;
    }

    /**
     * Method which actually sets the parameters in the query, used by all
     * addParameters methods.
     *
     * @param parameters The parameter list to add.
     * @param <T> The type of the parameters.
     * @throws DBUtilsException If any SQLException occurs while adding
     * parameters.
     */
    protected <T> void setParameters(T[] parameters) throws DBUtilsException {
        try {
            int i = 0;
            for (T parameter : parameters) {
                dbUtilsConnection().statement().setObject(++i, parameter);
            }
        } catch(SQLException e) {
            throw new DBUtilsException("Error adding parameter to query", e);
        }
    }
}
