package by.pvt.kish.aircompany.services.impl;

import by.pvt.kish.aircompany.dao.IEmployeeDAO;
import by.pvt.kish.aircompany.enums.EmployeeStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.BaseService;
import by.pvt.kish.aircompany.services.IEmployeeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * This class represents a concrete implementation of the IService interface for employee model.
 *
 * @author Kish Alexey
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class EmployeeService extends BaseService<Employee> implements IEmployeeService{

    private static Logger logger = Logger.getLogger(EmployeeService.class);

    @Autowired
    private IEmployeeDAO employeeDao;

    /**
     * Set employees status to the DB
     *
     * @param id     - The ID of the employee
     * @param status - The status to be changed
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void setStatus(Long id, EmployeeStatus status) throws ServiceException, ServiceValidateException {
        if (id == null) {
            throw new ServiceValidateException("ERROR_ID_MISSING");
        }
        try {
            employeeDao.setEmployeeStatus(id, status);
            logger.debug(SUCCESSFUL_TRANSACTION);
        } catch (DaoException e) {
            logger.debug(TRANSACTION_FAILED);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Returns a list of all available employees at this date from the DB
     *
     * @param date - The date of the flight
     * @return - a list of all available employees at this date from the DB
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public List<Employee> getAllAvailable(Date date) throws ServiceException {
        List<Employee> results;
        try {
            results =  employeeDao.getAllAvailableEmployee(date);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return results;
    }

    /**
     * Check if the employee is in another flight teams at that date
     *
     * @param id - The ID of the employee
     * @param flightDate - The flight date
     * @return - false if employee isn't in another flights at that date, true - if employee is busy at that date
     * @throws DaoException If something fails at DB level
     */
    @Override
    public boolean checkEmployeeAvailability(Long id, Date flightDate) throws ServiceException, ServiceValidateException {
        boolean result;
        if (id == null) {
            throw new ServiceValidateException("ERROR_ID_MISSING");
        }
        try {
            result =  employeeDao.checkEmployeeAvailability(id, flightDate);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return result;
    }

    /**
     * Returns a list of five last flights of the concrete employee from the DB
     *
     * @param id - The ID of the employee
     * @return - the list of last five flight of the concrete employee
     * @throws DaoException If something fails at DB level
     */
    @Override
    public List<Flight> getEmployeeLastFiveFlights(Long id) throws ServiceException, ServiceValidateException {
        List<Flight> results;
        if (id == null) {
            throw new ServiceValidateException("ERROR_ID_MISSING");
        }
        try {
            results =  employeeDao.getEmployeeLastFiveFlights(id);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return results;
    }

    /**
     * Returns a list of employees as flight crew of the concrete flight from the DB
     *
     * @param id - The ID of the flight
     * @return - the list of the employees as flight crew
     * @throws DaoException If something fails at DB level
     */
    @Override
    public List<Employee> getFlightCrewByFlightId(Long id) throws ServiceException, ServiceValidateException {
        List<Employee> results;
        if (id == null) {
            throw new ServiceValidateException("ERROR_ID_MISSING");
        }
        try {
            results =  employeeDao.getFlightCrewByFlightId(id);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return results;
    }
}
