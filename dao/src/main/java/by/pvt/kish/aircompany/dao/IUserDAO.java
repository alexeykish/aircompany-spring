package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.enums.UserStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.pojos.User;

/**
 * This interface represents a contract for a IDAO for the {@link User} model.
 * Note that all methods which returns the {@link User} from the DB, will not
 * fill the model with the password, due to security reasons.
 *
 * @author Kish Alexey
 */
public interface IUserDAO extends IDAO<User>{

    /**
     * Checks unique login to the DB
     *
     * @param login - checked login
     * @return false is login exists, true is don`t
     * @throws DaoException If something fails at DB level
     */
    boolean checkLogin(String login) throws DaoException;

    /**
     * Returns the user from the DB matching the given login and password
     *
     * @param login    - login of the user to be returned
     * @param password - password of the user to be returned
     * @return The user from the DB matching the given login and password
     * @throws DaoException If something fails at DB level
     */
    User getUser(String login, String password) throws DaoException;

    /**
     * Set user status to DB
     *
     * @param login  - The login of the user
     * @throws DaoException If something fails at DB level
     */
    User getByLogin(String login) throws DaoException;
}
