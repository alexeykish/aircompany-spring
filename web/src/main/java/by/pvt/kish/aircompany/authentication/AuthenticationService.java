package by.pvt.kish.aircompany.authentication;

import by.pvt.kish.aircompany.dao.impl.UserDAO;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.pojos.User;
import by.pvt.kish.aircompany.services.IUserService;
import by.pvt.kish.aircompany.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * @author Kish Alexey
 */
//@Service
public class AuthenticationService implements UserDetailsService {

    private static final String ERROR_USER_NOT_FOUND = "Username not found";

    @Autowired
    private IUserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = null;
        GrantedAuthority authority = null;
        try {
            user = userService.getByLogin(login);
            authority = new SimpleGrantedAuthority(user.getRole().toString());
            if (user == null) {
                throw new UsernameNotFoundException(ERROR_USER_NOT_FOUND);
            }
        } catch (ServiceException e) {
            e.printStackTrace(); //TODO Exception handling
        }

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                user.isEnabled(), true, true, true, Arrays.asList(authority));
    }
}
