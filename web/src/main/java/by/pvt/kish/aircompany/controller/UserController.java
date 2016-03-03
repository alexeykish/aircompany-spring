package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.enums.UserRole;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceLoginException;
import by.pvt.kish.aircompany.pojos.User;
import by.pvt.kish.aircompany.services.IUserService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author Kish Alexey
 */
@Controller
public class UserController {

    private static String className = UserController.class.getName();
    private static Logger logger = Logger.getLogger(UserController.class.getName());

    private static final String REDIRECT_PATH_SIGNIN = "redirect:/signIn";
    private static final String PATH_USER_SIGNIN = "signIn";
    private static final String PATH_USER_REGISTRATION = "registration";
    private static final String PATH_USER_LIST = "user/list";

    @Autowired
    private IUserService userService;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ModelAttribute("user")
    public User createUser() {
        return new User();
    }

    @RequestMapping(value = "/login")
    public String loginUser() {
        return "main";
    }

    @RequestMapping(value = "/logout")
    public String logoutUser(HttpServletRequest request,
                             HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return REDIRECT_PATH_SIGNIN;
    }

    @RequestMapping(value = "/user/add")
    public String addUser(ModelMap model,
                          @Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (user != null) {
                    userService.addUser(user);
                    model.addAttribute(Attribute.LOGIN_MESSAGE, "message.success.registration");
                    return PATH_USER_SIGNIN;
                }
            } else {
                model.addAttribute(Attribute.USERROLES, Arrays.asList(UserRole.values()));
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceLoginException e) {
            return ErrorHandler.returnLoginErrorPage(request, e.getMessage(), className);
        }
        return PATH_USER_REGISTRATION;
    }

    @RequestMapping(value = "/user/main")
    public String getAllUsers(ModelMap model) {
        try {
            model.addAttribute(Attribute.USERS, userService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_USER_LIST;
    }

    @RequestMapping(value = "/signIn")
    public String showAuthorisationPage() {
        return PATH_USER_SIGNIN;
    }

    @RequestMapping(value = "/registrationPage")
    public String showRegistrationPage(ModelMap model) {
        model.addAttribute(Attribute.USERROLES, Arrays.asList(UserRole.values()));
        return PATH_USER_REGISTRATION;
    }

    @RequestMapping(value = "/access_denied")
    public String accessDeniedPage(Locale locale,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(Attribute.LOGIN_MESSAGE, messageSource.getMessage("message.access.denied", null, locale));
        return REDIRECT_PATH_SIGNIN;
    }
}
