package by.pvt.kish.aircompany.validators;

import org.springframework.stereotype.Component;

/**
 * This interface represents a contract for a IValidator.
 *
 * @author Kish Alexey
 */
@Component
public interface IValidator<T> {

    /**
     * Check the validity of some parameters
     *
     * @param t - pojos to be validated
     * @return - Null, if everything checks out correctly; error page if the data is incorrect
     */
    String validate(T t);
}
