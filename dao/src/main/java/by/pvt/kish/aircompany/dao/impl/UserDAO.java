/**
 *
 */
package by.pvt.kish.aircompany.dao.impl;

import by.pvt.kish.aircompany.dao.BaseDAO;
import by.pvt.kish.aircompany.dao.IUserDAO;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.pojos.User;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class represents a concrete implementation of the {@link IUserDAO} interface.
 *
 * @author Kish Alexey
 */
@Repository
public class UserDAO extends BaseDAO<User> implements IUserDAO {

    private static final String HQL_CHECK_LOGIN = "SELECT U.uid FROM User U WHERE U.login=:login ";
    private static final String HQL_GET_USER = "SELECT U FROM User U WHERE U.login=:login AND U.password=:password";
    private static final String HQL_UPDATE_USER_STATUS = "update From User U set U.status =:userstatus where U.uid =:uid";
    private static final String HQL_GET_BY_LOGIN = "from User where login = :login";

    private static final String CHECK_LOGIN_USER_FAILED = "Check user login failed";
    private static final String GET_USER_FAILED = "Get user by login and password failed";
    private static final String GET_BY_LOGIN_FAILED = "Get user by login failed";
    private static final String UPDATE_USER_STATUS_FAILED = "Set user status failed";

    @Autowired
    private UserDAO(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }

    /**
     * Checks unique login of the user in the DB
     *
     * @param login - checked login
     * @return false is login exists, true if don`t
     * @throws DaoException If something fails at DB level
     */
    @Override
    public boolean checkLogin(String login) throws DaoException {
        List results;
        try {
            Query query = getSession().createQuery(HQL_CHECK_LOGIN);
            query.setParameter("login", login);
            results = query.list();
            if (!results.isEmpty()) {
                return false;
            }
        } catch (HibernateException e) {
            throw new DaoException(CHECK_LOGIN_USER_FAILED, e);
        }
        return true;
    }

    /**
     * Returns the user from the DB matching the given login and password
     *
     * @param login    - login of the user to be returned
     * @param password - password of the user to be returned
     * @return The user from the DB matching the given login and password
     * @throws DaoException If something fails at DB level
     */
    @Override
    public User getUser(String login, String password) throws DaoException {
        User user;
        try {
            Query query = getSession().createQuery(HQL_GET_USER);
            query.setParameter("login", login);
            query.setParameter("password", password);
            user = (User) query.uniqueResult();
        } catch (HibernateException e) {
            throw new DaoException(GET_USER_FAILED, e);
        }
        return user;
    }

    /**
     * Set user status to DB
     *
     * @param login - The login of the user
     * @throws DaoException If something fails at DB level
     */
    @Override
    public User getByLogin(String login) throws DaoException {
        User user;
        try {
            Session session = getSession();
            Query query = session.createQuery(HQL_GET_BY_LOGIN);
            query.setParameter("login", login);
            user = (User) query.uniqueResult();
        }
        catch(HibernateException e){
            throw new DaoException(GET_BY_LOGIN_FAILED, e);
        }
        return user;
    }
}
