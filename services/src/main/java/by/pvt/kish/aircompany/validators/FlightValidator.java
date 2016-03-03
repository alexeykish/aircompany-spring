/**
 *
 */
package by.pvt.kish.aircompany.validators;

import by.pvt.kish.aircompany.enums.Waypoint;
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

    private static final String CHECKED_FIELD_DATE = "date";
    private static final String CHECKED_FIELD_WAYPOINT = "waypoints['ARRIVAL']";

    private static final String ERROR_EMPTY_DATE = "NotEmpty.flight.date";
    private static final String ERROR_EMPTY_FLIGHT_DATE = "message.error.flight.date";
    private static final String ERROR_EMPTY_FLIGHT_WAYPOINT = "message.error.flight.waypoints";

    @Override
    public boolean supports(Class aClass) {
        return Flight.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Flight flight = (Flight) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, CHECKED_FIELD_DATE, ERROR_EMPTY_DATE);
        if (checkDate(flight)) {
            errors.rejectValue(CHECKED_FIELD_DATE, ERROR_EMPTY_FLIGHT_DATE);
        }
        if (checkWaypoints(flight)) {
            errors.rejectValue(CHECKED_FIELD_WAYPOINT, ERROR_EMPTY_FLIGHT_WAYPOINT);
        }
    }

    /**
     * The method checks the place of departure and place of arrival, they will not be the same
     *
     * @param flight - Flight object being checked
     * @return - false, if everything checks out correctly; true - if the data is invalid
     */
    private static boolean checkWaypoints(Flight flight) {
        return flight.getWaypoints().get(Waypoint.DEPARTURE).equals(flight.getWaypoints().get(Waypoint.ARRIVAL));
    }

    /**
     * The method checks the date of departure, it would not be the in the past
     *
     * @param flight - Flight object being checked
     * @return - false, if everything checks out correctly; true - if the data is invalid
     */
    private boolean checkDate(Flight flight) {
        boolean result = true;
        Date yesterdayDate = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        Date flightDate = flight.getDate();
        if (flightDate != null) {
            result = yesterdayDate.after(flightDate);
        }
        return result;
    }
}
