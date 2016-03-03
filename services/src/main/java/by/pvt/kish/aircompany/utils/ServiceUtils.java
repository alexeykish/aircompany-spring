package by.pvt.kish.aircompany.utils;

import by.pvt.kish.aircompany.exceptions.ServiceLoginException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.User;

/**
 * Util class for service layer
 *
 * @author Kish Alexey
 */
public class ServiceUtils {

    private static final String MESSAGE_ERROR_ID_MISSING = "ERROR_ID_MISSING";
    private static final String MESSAGE_ERROR_USER_MISSING = "ERROR_USER_MISSING";

    public static void checkNullId(Long id) throws ServiceValidateException {
        if (id == null) {
            throw new ServiceValidateException(MESSAGE_ERROR_ID_MISSING);
        }
    }

    public static void checkNullUser(User user) throws ServiceLoginException {
        if (user == null) {
            throw new ServiceLoginException(MESSAGE_ERROR_USER_MISSING);
        }
    }
}
