/**
 *
 */
package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.utils.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import java.util.List;

/**
 * The abstract class represents a implementation of the IDAO interface
 * The constructor is obtained from class name
 *
 * @author Kish Alexey
 */
public abstract class BaseDAO<T> implements IDAO<T> {

    private static Logger logger = Logger.getLogger(BaseDAO.class);
    private Class className;

    private static final String ADD_ENTITY_FAIL = "Add operation failed";
    private static final String UPDATE_ENTITY_FAIL = "Update operation failed";
    private static final String GET_BY_ENTITY_ID_FAIL = "Get by ID operation failed";
    private static final String GET_ALL_ENTITIES_FAIL = "Get all operation failed";
    private static final String DELETE_ENTITY_FAIL = "Delete operation failed";
    private static final String GET_COUNT_ENTITY_FAIL = "Get count operation failed";

    protected HibernateUtil util = HibernateUtil.getUtil();

    protected BaseDAO(Class className) {
        this.className = className;
    }

    /**
     * Create the given Entity in the DB
     *
     * @param t - object to be created
     * @return - The ID of the object, generated by DB
     * @throws DaoException If something fails at DB level
     */
    public Long add(T t) throws DaoException {
        Long iid;
        try {
            Session session = util.getSession();
            session.saveOrUpdate(t);
            iid = (Long) session.getIdentifier(t);
            logger.info("Saved object: " + t);
        } catch (HibernateException e) {
            throw new DaoException(ADD_ENTITY_FAIL, e);
        }
        return iid;
    }

    /**
     * Update the given Entity in the DB
     *
     * @param t - object to be updated
     * @throws DaoException If something fails at DB level
     */
    public void update(T t) throws DaoException {
        try {
            Session session = util.getSession();
            session.merge(t);
            logger.info("Updated object: " + t);
        } catch (HibernateException e) {
            throw new DaoException(UPDATE_ENTITY_FAIL, e);
        }
    }

    /**
     * Returns the Entity from the DB matching the given ID
     *
     * @param id - The ID of the pojos to be returned
     * @return - the pojos from the DB
     * @throws DaoException If something fails at DB level
     */
    public T getById(Long id) throws DaoException {
        T t;
        try {
            Session session = util.getSession();
            t = (T) session.get(className, id);
            logger.info("Get object: " + t);
        } catch (HibernateException e) {
            throw new DaoException(GET_BY_ENTITY_ID_FAIL, e);
        }
        return t;
    }

    /**
     * Returns a list of all Entities from the DB
     *
     * @return - a list of all Entities from the DB
     * @throws DaoException If something fails at DB level
     */
    public List<T> getAll() throws DaoException {
        List<T> results;
        try {
            Session session = util.getSession();
            Criteria criteria = session.createCriteria(className);
            results = criteria.list();
        } catch (HibernateException e) {
            throw new DaoException(GET_ALL_ENTITIES_FAIL, e);
        }
        return results;
    }

    /**
     * Delete the given pojos from the DB
     *
     * @param id - ID of the pojos to be deleted from the DB
     * @throws DaoException If something fails at DB level
     */
    public void delete(Long id) throws DaoException {
        try {
            Session session = util.getSession();
            T t = (T) session.get(className, id);
            if (t == null) {
                throw new DaoException("Cant find object with that ID: " + id);
            }
            session.delete(t);
            logger.info("Deleted object: " + t);
        } catch (HibernateException e) {
            throw new DaoException(DELETE_ENTITY_FAIL, e);
        }
    }

    /**
     * Returns the number of entities in the DB
     *
     * @throws DaoException If something fails at DB level
     */
    public int getCount() throws DaoException {
        int count;
        try {
            Session session = util.getSession();
            Criteria criteria = session.createCriteria(className);
            criteria.setProjection(Projections.rowCount());
            count = ((Long) criteria.uniqueResult()).intValue();
        } catch (HibernateException e) {
            throw new DaoException(GET_COUNT_ENTITY_FAIL,e);
        }
        return count;
    }

}