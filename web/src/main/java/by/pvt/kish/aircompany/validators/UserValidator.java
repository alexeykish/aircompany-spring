package by.pvt.kish.aircompany.validators;

import by.pvt.kish.aircompany.pojos.User;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Kish Alexey
 */
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class aClass) {
        return User.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.user.firstName");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.user.lastName");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "NotEmpty.user.login");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.user.password");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.user.email");
    }
}
