package by.pvt.kish.aircompany.handlers;

import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.IFlightService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Util class for handle flights status
 *
 * @author Kish Alexey
 */
@Component
public class FlightStatusHandler {

    @Autowired
    private IFlightService flightService;

    private static Logger logger = Logger.getLogger(FlightStatusHandler.class.getName());
    private static final String PAST = "Past";
    private static final String TODAY = "Today";
    private static final String FUTURE = "Future";
    private static final String MATCHED = "Matched";
    private static final String EMPTY = "Empty";
    private static final String FLIGHT = "Flight #";
    private static final String STATUS = " status changed to ";

    /**
     * Updates flight statuses depends of current date and set crew
     *
     * @throws ServiceException
     * @throws ServiceValidateException
     */
    public void updateFlightsStatus() throws ServiceException, ServiceValidateException {
        List<Flight> flights = flightService.getAll();
        for (Flight flight : flights) {
            if (checkDate(flight).equals(FUTURE) && checkTeam(flight).equals(EMPTY)) {
                if (flight.getStatus() != (FlightStatus.CREATED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.CREATED);
                    logger.info(FLIGHT + flight.getFid() + STATUS + FlightStatus.CREATED);
                }
            } else if (checkDate(flight).equals(FUTURE) && checkTeam(flight).equals(MATCHED)) {
                if (flight.getStatus() != (FlightStatus.READY)){
                    flightService.setStatus(flight.getFid(), FlightStatus.READY);
                    logger.info(FLIGHT + flight.getFid() + STATUS + FlightStatus.READY);
                }
            } else if (checkDate(flight).equals(TODAY) && checkTeam(flight).equals(EMPTY)) {
                if (flight.getStatus() != (FlightStatus.CANCELED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.CANCELED);
                    logger.info(FLIGHT + flight.getFid() + STATUS + FlightStatus.CANCELED);
                }
            } else if (checkDate(flight).equals(TODAY) && checkTeam(flight).equals(MATCHED)) {
                if (flight.getStatus() != (FlightStatus.DEPARTED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.DEPARTED);
                    logger.info(FLIGHT + flight.getFid() + STATUS + FlightStatus.DEPARTED);
                }
            } else if (checkDate(flight).equals(PAST) && checkTeam(flight).equals(EMPTY)) {
                if (flight.getStatus() != (FlightStatus.CANCELED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.CANCELED);
                    logger.info(FLIGHT + flight.getFid() + STATUS + FlightStatus.CANCELED);
                }
            } else if (checkDate(flight).equals(PAST) && checkTeam(flight).equals(MATCHED)) {
                if (flight.getStatus() != (FlightStatus.ARRIVAL)){
                    flightService.setStatus(flight.getFid(), FlightStatus.ARRIVAL);
                    logger.info(FLIGHT + flight.getFid() + STATUS + FlightStatus.ARRIVAL);
                }
            }
        }
    }

    /**
     * checks when the date was in time line
     *
     * @param flight - the flight to be checked
     */
    private static String checkDate(Flight flight) {
        Date yesterdayDate = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        Date todayDate = new Date(System.currentTimeMillis());
        Date flightDate = flight.getDate();
        if (yesterdayDate.after(flightDate)) {
            return PAST;
        } else if (todayDate.before(flightDate)) {
            return FUTURE;
        } else {
            return TODAY;
        }
    }

    /**
     * Check if flightw crew is set
     *
     * @param flight - the flight to be checked
     * @return MATCHED - if crew is set, EMPPTY - if doesn`t
     */
    private static String checkTeam(Flight flight) {
        if (!flight.getCrew().isEmpty()) {
            return MATCHED;
        } else {
            return EMPTY;
        }
    }
}
