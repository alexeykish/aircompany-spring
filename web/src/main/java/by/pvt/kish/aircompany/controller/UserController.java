package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.enums.UserStatus;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceLoginException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.User;
import by.pvt.kish.aircompany.services.impl.UserService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Kish Alexey
 */
@Controller
public class UserController {

    private static String className = UserController.class.getName();
    private static Logger logger = Logger.getLogger(UserController.class.getName());

    @RequestMapping(value = "/login-user", method = RequestMethod.POST)

    public String loginUser(Model model,
                            HttpSession session,
                            HttpServletRequest request,
                            @RequestParam("login") String login,
                            @RequestParam("password") String password) {
        try {
            User user = UserService.getInstance().getUser(login, password);
            switch (user.getUserType()) {
                case ADMINISTRATOR:
                    session.setAttribute(Attribute.USERTYPE_ATTRIBUTE, 2);
                    break;
                case DISPATCHER:
                    session.setAttribute(Attribute.USERTYPE_ATTRIBUTE, 1);
                    break;
                default:
                    session.setAttribute(Attribute.USERTYPE_ATTRIBUTE, 0);
                    break;
            }
            session.setAttribute(Attribute.USER_ATTRIBUTE, user);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceLoginException e) {
            return ErrorHandler.returnLoginErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/logout-user", method = RequestMethod.POST)
    public String logoutUser(Model model,
                             HttpSession session,
                             HttpServletRequest request) {
        if (session != null) {
            try {
                User user = (User)session.getAttribute(Attribute.USER_ATTRIBUTE);
                UserService.getInstance().setStatus(user.getUid(), UserStatus.OFFLINE);
                session.invalidate();
                logger.info(Message.USER_LOGOUT);
            } catch (ServiceException e) {
               model.addAttribute(Attribute.LOGIN_MESSAGE_ATTRIBUTE, Message.ERROR_REG_LOGOUT);
                return ErrorHandler.returnErrorPage(e.getMessage(), className);
            } catch (ServiceValidateException e) {
                return ErrorHandler.returnLoginErrorPage(request, e.getMessage(), className);
            }
        }
        return "index";
    }

    @RequestMapping(value = "/add-user", method = RequestMethod.POST)
    public String addUser(Model model,
                          @ModelAttribute User user,
                          HttpServletRequest request) {
        try {
            if (user != null) {
                UserService.getInstance().add(user);
                model.addAttribute(Attribute.LOGIN_MESSAGE_ATTRIBUTE, Message.SUCCESS_REG);
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceLoginException e) {
            return ErrorHandler.returnLoginErrorPage(request, e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/usersList", method = RequestMethod.GET)
    public String getAllUsers(Model model) {
        try {
            List<User> users = UserService.getInstance().getAll();
            model.addAttribute(Attribute.USERS_ATTRIBUTE, users);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "user/list";
    }
}
