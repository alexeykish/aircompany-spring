package by.pvt.kish.aircompany.services;

import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Flight;

import java.util.List;

/**
 * @author Kish Alexey
 */
public interface IFlightService extends IService<Flight> {

    /**
     * Set flight status to the DB
     *
     * @param id     - The ID of the flight
     * @param status - The status to be changed
     * @throws ServiceException If something fails at DAO level
     */
    Void setStatus(Long id, FlightStatus status) throws ServiceException, ServiceValidateException;

    /**
     * Add flight crew to existed flight
     *
     * @param id - The ID of the existed flight
     * @param team - The set of the employees that is flight crew
     * @throws ServiceException If something fails at DAO level
     */
    Void addCrew(Long id, List<Long> team) throws ServiceException, ServiceValidateException;

    /**
     * Returns a list of flights ordered by date, prepared for pagination from the DB
     *
     * @param pageSize - The number of flights at the page
     * @param pageNumber - The number of the showed page
     * @return - the list of the flights, ordered by date
     * @throws DaoException If something fails at DAO level
     */
    List<Flight> getAllToPage(int pageSize, int pageNumber) throws DaoException, ServiceException;

    /**
     * Delete flight crew from existed flight
     *
     * @param id   - The ID of the existed flight
     * @throws ServiceException If something fails at DAO level
     */
    Void deleteCrew(Long id) throws ServiceException, ServiceValidateException;
}
