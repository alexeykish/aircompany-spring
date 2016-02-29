/**
 *
 */
package by.pvt.kish.aircompany.validators;

import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.pojos.Flight;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Date;

/**
 * Describes the utility class to test the Flight object before adding or changing it in the DB
 *
 * @author Kish Alexey, 2015
 */
@Component
public class FlightValidator implements Validator {


    @Override
    public boolean supports(Class aClass) {
        return Flight.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Flight flight = (Flight) o;
        if (checkDate(flight)) {
            errors.rejectValue("date", "ERROR_FLIGHT_DATE", null, "ERROR_FLIGHT_DATE");
        }
        if (checkEntry(flight)) {
            errors.rejectValue("arrival", "ERROR_FLIGHT_VALID", null, "ERROR_FLIGHT_VALID");
        }
    }

    /**
     * The method checks the place of departure and place of arrival, they will not be the same
     *
     * @param flight - Flight object being checked
     * @return - false, if everything checks out correctly; true - if the data is invalid
     */
    private static boolean checkEntry(Flight flight) {
        return flight.getDeparture().equals(flight.getArrival());
    }

    /**
     * The method checks the date of departure, it would not be the in the past
     *
     * @param flight - Flight object being checked
     * @return - false, if everything checks out correctly; true - if the data is invalid
     */
    private boolean checkDate(Flight flight) {
        Date yesterdayDate = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        Date flightDate = flight.getDate();
        return yesterdayDate.after(flightDate);
    }


}
