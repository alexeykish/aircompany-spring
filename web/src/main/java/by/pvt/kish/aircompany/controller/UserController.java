package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.enums.UserStatus;
import by.pvt.kish.aircompany.enums.UserType;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceLoginException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.User;
import by.pvt.kish.aircompany.services.IService;
import by.pvt.kish.aircompany.services.IUserService;
import by.pvt.kish.aircompany.services.impl.UserService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kish Alexey
 */
@Controller
public class UserController {

    private static String className = UserController.class.getName();
    private static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private IUserService userService;

    @ModelAttribute("user")
    public User createUser() {
        return new User();
    }

    @RequestMapping(value = "/loginUser")
    public String loginUser(ModelMap model,
                            HttpSession session,
                            HttpServletRequest request,
                            @RequestParam("login") String login,
                            @RequestParam("password") String password) {
        try {
            User user = userService.getUser(login, password);
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

    @RequestMapping(value = "/logoutUser")
    public String logoutUser(ModelMap model,
                             HttpSession session,
                             HttpServletRequest request) {
        if (session != null) {
            try {
                User user = (User) session.getAttribute(Attribute.USER_ATTRIBUTE);
                userService.setStatus(user.getUid(), UserStatus.OFFLINE);
                session.invalidate();
                logger.info(Message.USER_LOGOUT);
            } catch (ServiceException e) {
                model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.ERROR_REG_LOGOUT); //LOGIN_MESSAGE
                return ErrorHandler.returnErrorPage(e.getMessage(), className);
            } catch (ServiceValidateException e) {
                return ErrorHandler.returnLoginErrorPage(request, e.getMessage(), className);
            }
        }
        return "signIn";
    }

    @RequestMapping(value = "/addUser")
    public String addUser(ModelMap model,
                          @Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (user != null) {
                    userService.addUser(user);
                    model.addAttribute(Attribute.LOGIN_MESSAGE_ATTRIBUTE, Message.SUCCESS_REG);
                    return "signIn";
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceLoginException e) {
            return ErrorHandler.returnLoginErrorPage(request, e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "registration";
    }

    @RequestMapping(value = "/userList")
    public String getAllUsers(ModelMap model) {
        try {
            List<User> users = userService.getAll();
            model.addAttribute(Attribute.USERS_ATTRIBUTE, users);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "user/list";
    }

    @RequestMapping(value = "/signIn")
    public String showAuthorisationPage() {
        return "signIn";
    }

    @RequestMapping(value = "/registrationPage")
    public String showRegistrationPage(ModelMap model) {
        List<UserType> userTypes = Arrays.asList(UserType.values());
        model.addAttribute(Attribute.USERTYPES_ATTRIBUTE, userTypes);
        return "registration";
    }
}
