package by.pvt.kish.aircompany.exceptions;

import org.hibernate.HibernateException;

/**
 * This class represents a generic DAO exception. It should wrap any exception of the underlying
 * code, such as SQLExceptions.
 *
 * @author Kish Alexey
 */
public class DaoException extends HibernateException {
    /**
     * Constructs a DAOException with the given detail message.
     *
     * @param message The detail message of the DAOException.
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * Constructs a DAOException with the given root cause.
     *
     * @param cause The root cause of the DAOException.
     */
    public DaoException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a DAOException with the given detail message and root cause.
     *
     * @param message The detail message of the DAOException.
     * @param cause   The root cause of the DAOException.
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
