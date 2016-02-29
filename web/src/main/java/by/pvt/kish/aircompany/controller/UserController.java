package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.enums.UserStatus;
import by.pvt.kish.aircompany.enums.UserType;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceLoginException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.User;
import by.pvt.kish.aircompany.services.IUserService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;

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
    public String loginUser(HttpSession session,
                            HttpServletRequest request,
                            @RequestParam("login") String login,
                            @RequestParam("password") String password) {
        try {
            User user = userService.getUser(login, password);
            switch (user.getUserType()) {
                case ADMINISTRATOR:
                    session.setAttribute(Attribute.USER_TYPE, 2);
                    break;
                case DISPATCHER:
                    session.setAttribute(Attribute.USER_TYPE, 1);
                    break;
                default:
                    session.setAttribute(Attribute.USER_TYPE, 0);
                    break;
            }
            session.setAttribute(Attribute.USER, user);
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
                User user = (User) session.getAttribute(Attribute.USER);
                userService.setStatus(user.getUid(), UserStatus.OFFLINE);
                session.invalidate();
            } catch (ServiceException e) {
                model.addAttribute(Attribute.MESSAGE, "ERROR_REG_LOGOUT");
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
                    model.addAttribute(Attribute.LOGIN_MESSAGE, "SUCCESS_REG");
                    return "signIn";
                }
            } else {
                model.addAttribute(Attribute.USERTYPES, Arrays.asList(UserType.values()));
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
            model.addAttribute(Attribute.USERS, userService.getAll());
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
        model.addAttribute(Attribute.USERTYPES, Arrays.asList(UserType.values()));
        return "registration";
    }
}
