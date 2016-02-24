package by.pvt.kish.aircompany.validators;

import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.services.IFlightService;
import by.pvt.kish.aircompany.services.impl.FlightService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Kish Alexey
 */
@Service
public class FlightStatusValidator {

    @Autowired
    private IFlightService flightService;

    private static Logger logger = Logger.getLogger(FlightStatusValidator.class.getName());
    private static final String PAST = "Past";
    private static final String TODAY = "Today";
    private static final String FUTURE = "Future";
    private static final String MATCHED = "Matched";
    private static final String EMPTY = "Empty";

    public void updateFlightsStatus() throws ServiceException, ServiceValidateException {
        List<Flight> flights = flightService.getAll();
        for (Flight flight : flights) {
            if (checkDate(flight).equals(FUTURE) && checkTeam(flight).equals(EMPTY)) {
                if (flight.getStatus() != (FlightStatus.CREATED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.CREATED);
                    logger.info("Flight #" + flight.getFid() + " status changed to " + FlightStatus.CREATED);
                }
            } else if (checkDate(flight).equals(FUTURE) && checkTeam(flight).equals(MATCHED)) {
                if (flight.getStatus() != (FlightStatus.READY)){
                    flightService.setStatus(flight.getFid(), FlightStatus.READY);
                    logger.info("Flight #" + flight.getFid() + " status changed to " + FlightStatus.READY);
                }
            } else if (checkDate(flight).equals(TODAY) && checkTeam(flight).equals(EMPTY)) {
                if (flight.getStatus() != (FlightStatus.CANCELED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.CANCELED);
                    logger.info("Flight #" + flight.getFid() + " status changed to " + FlightStatus.CANCELED);
                }
            } else if (checkDate(flight).equals(TODAY) && checkTeam(flight).equals(MATCHED)) {
                if (flight.getStatus() != (FlightStatus.DEPARTED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.DEPARTED);
                    logger.info("Flight #" + flight.getFid() + " status changed to " + FlightStatus.DEPARTED);
                }
            } else if (checkDate(flight).equals(PAST) && checkTeam(flight).equals(EMPTY)) {
                if (flight.getStatus() != (FlightStatus.CANCELED)){
                    flightService.setStatus(flight.getFid(), FlightStatus.CANCELED);
                    logger.info("Flight #" + flight.getFid() + " status changed to " + FlightStatus.CANCELED);
                }
            } else if (checkDate(flight).equals(PAST) && checkTeam(flight).equals(MATCHED)) {
                if (flight.getStatus() != (FlightStatus.ARRIVAL)){
                    flightService.setStatus(flight.getFid(), FlightStatus.ARRIVAL);
                    logger.info("Flight #" + flight.getFid() + " status changed to " + FlightStatus.ARRIVAL);
                }
            }
        }
    }

    private static String checkDate(Flight flight) {
        java.util.Date yesterdayDate = new java.util.Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        java.util.Date todayDate = new java.util.Date(System.currentTimeMillis());
        java.util.Date flightDate = flight.getDate();
        if (yesterdayDate.after(flightDate)) {
            return PAST;
        } else if (todayDate.before(flightDate)) {
            return FUTURE;
        } else {
            return TODAY;
        }
    }

    private static String checkTeam(Flight flight) {
        if (flight.getCrew().size() > 0) {
            return MATCHED;
        } else {
            return EMPTY;
        }
    }
}
