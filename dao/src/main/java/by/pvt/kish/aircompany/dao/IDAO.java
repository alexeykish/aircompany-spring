package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.exceptions.DaoException;

import java.util.List;

/**
 * This interface represents a contract for a IDAO for the Entity models.
 *
 * @author Kish Alexey
 */
public interface IDAO<T> {

    /**
     * Create the given Entity in the DB
     *
     * @param t - entity to be created
     * @return - The entity, generated by DB
     * @throws DaoException If something fails at DB level
     */
    T add(T t) throws DaoException;

    /**
     * Update the given Entity in the DB
     *
     * @param t - entity to be updated
     * @throws DaoException If something fails at DB level
     */
    void update(T t) throws DaoException;

    /**
     * Returns a list of all Entities from the DB
     *
     * @return - a list of all Entities from the DB
     * @throws DaoException If something fails at DB level
     */
    List<T> getAll() throws DaoException;

    /**
     * Returns the Entity from the DB matching the given ID
     *
     * @param id - The ID of the entities to be returned
     * @return - the entities from the DB
     * @throws DaoException If something fails at DB level
     */
    T getById(Long id) throws DaoException;

    /**
     * Delete the given entity from the DB
     *
     * @param id - The ID of the entity to be deleted from the DB
     * @throws DaoException If something fails at DB level
     */
    void delete(Long id) throws DaoException;

    /**
     * Returns the number of entities in the DB
     *
     * @throws DaoException If something fails at DB level
     */
    int getCount() throws DaoException;
}
