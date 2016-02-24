package by.pvt.kish.aircompany.services.impl;

import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.dao.IFlightDAO;
import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.BaseService;
import by.pvt.kish.aircompany.services.IFlightService;
import by.pvt.kish.aircompany.utils.TeamCreator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class represents a concrete implementation of the IService interface for flight model.
 *
 * @author Kish Alexey
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class FlightService extends BaseService<Flight> implements IFlightService {

    private static Logger logger = Logger.getLogger(FlightService.class);

    @Autowired
    private IFlightDAO flightDAO;

    @Autowired
    private TeamCreator teamCreator;

    /**
     * Set flight status to the DB
     *
     * @param id     - The ID of the flight
     * @param status - The status to be changed
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public void setStatus(Long id, FlightStatus status) throws ServiceException, ServiceValidateException {
        if (id < 0) {
            throw new ServiceValidateException(Message.ERROR_ID_MISSING);
        }
        try {
            flightDAO.setFlightStatus(id, status);
            logger.debug(SUCCESSFUL_TRANSACTION);
        } catch (DaoException e) {
            logger.debug(TRANSACTION_FAILED);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Add flight crew to existed flight
     *
     * @param id - The ID of the existed flight
     * @param team - The set of the employees that is flight crew
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public void addTeam(Long id, List<Long> team) throws ServiceException, ServiceValidateException {
        if (id < 0) {
            throw new ServiceValidateException(Message.ERROR_ID_MISSING);
        }
        Set<Employee> crew = teamCreator.getEmployeeListById(team);
        try {
            Flight flight = flightDAO.getById(id);
            flight.setCrew(crew);
            flightDAO.update(flight);
            logger.debug(SUCCESSFUL_TRANSACTION);
        } catch (DaoException e) {
            logger.debug(TRANSACTION_FAILED);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Returns a list of flights ordered by date, prepared for pagination from the DB
     *
     * @param pageSize - The number of flights at the page
     * @param pageNumber - The number of the showed page
     * @return - the list of the flights, ordered by date
     * @throws DaoException If something fails at DB level
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Flight> getAllToPage(int pageSize, int pageNumber) throws ServiceException {
        List<Flight> results = new ArrayList<>();
        try {
            results = flightDAO.getAllToPage(pageSize, pageNumber);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return results;
    }

    /**
     * Returns the number of flights in the DB
     *
     * @throws DaoException If something fails at DB level
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public int getCount() throws DaoException, ServiceException {
        int count;
        try {
            count = flightDAO.getCount();
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return count;
    }
}
