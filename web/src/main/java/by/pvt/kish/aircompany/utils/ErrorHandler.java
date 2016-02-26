package by.pvt.kish.aircompany.utils;

import by.pvt.kish.aircompany.constants.Attribute;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kish Alexey
 */
public class ErrorHandler {

    private static Logger logger = Logger.getLogger(ErrorHandler.class.getName());

    public static String returnValidateErrorPage(HttpServletRequest request, String validateResult, String className) {
        request.setAttribute(Attribute.MESSAGE, validateResult);
        logger.error(className + ": " + validateResult);
        return "main";
    }

    public static String returnLoginErrorPage(HttpServletRequest request, String error, String className) {
        request.setAttribute(Attribute.LOGIN_MESSAGE, error);
        logger.error(className + ": " + error);
        return "signIn";
    }

    public static String returnErrorPage(String error, String className) {
        logger.error(className + ": " + error);
        return "main";
    }
}
