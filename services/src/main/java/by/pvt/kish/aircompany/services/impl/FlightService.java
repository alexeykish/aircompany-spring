package by.pvt.kish.aircompany.services.impl;

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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static by.pvt.kish.aircompany.utils.ServiceUtils.checkNullId;

/**
 * This class represents a concrete implementation of the IService interface for flight model.
 *
 * @author Kish Alexey
 */
@Service
public class FlightService extends BaseService<Flight> implements IFlightService {

    private static Logger logger = Logger.getLogger(FlightService.class);

    @Autowired
    private IFlightDAO flightDAO;

    @Autowired
    private TeamCreator teamCreator;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Set flight status to the DB
     *
     * @param id     - The ID of the flight
     * @param status - The status to be changed
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public Void setStatus(Long id, FlightStatus status) throws ServiceException, ServiceValidateException {
        checkNullId(id);
        return transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                try {
                    flightDAO.setFlightStatus(id, status);
                    logger.debug(SUCCESSFUL_TRANSACTION);
                } catch (DaoException e) {
                    transactionStatus.setRollbackOnly();
                    logger.debug(TRANSACTION_FAILED, e);
                }
                return null;
            }
        });
    }

    /**
     * Add flight crew to existed flight
     *
     * @param id   - The ID of the existed flight
     * @param team - The set of the employees that is flight crew
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public Void addCrew(Long id, List<Long> team) throws ServiceException, ServiceValidateException {
        checkNullId(id);
        Set<Employee> crew = teamCreator.getEmployeeListById(team);
        return transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                try {
                    Flight flight = flightDAO.getById(id);
                    flight.setCrew(crew);
                    flightDAO.update(flight);
                    logger.debug(SUCCESSFUL_TRANSACTION);
                } catch (DaoException e) {
                    transactionStatus.setRollbackOnly();
                    logger.debug(TRANSACTION_FAILED, e);
                }
                return null;
            }
        });
    }

    /**
     * Delete flight crew from existed flight
     *
     * @param id   - The ID of the existed flight
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public Void deleteCrew(Long id) throws ServiceException, ServiceValidateException {
        checkNullId(id);
        return transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                try {
                    Flight flight = flightDAO.getById(id);
                    flight.setCrew(null);
                    flightDAO.update(flight);
                    logger.debug(SUCCESSFUL_TRANSACTION);
                } catch (DaoException e) {
                    transactionStatus.setRollbackOnly();
                    logger.debug(TRANSACTION_FAILED, e);
                }
                return null;
            }
        });
    }

    /**
     * Returns a list of flights ordered by date, prepared for pagination from the DB
     *
     * @param pageSize   - The number of flights at the page
     * @param pageNumber - The number of the showed page
     * @return - the list of the flights, ordered by date
     * @throws DaoException If something fails at DAO level
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Flight> getAllToPage(int pageSize, int pageNumber) throws ServiceException {

        return transactionTemplate.execute(new TransactionCallback<List<Flight>>() {
            @Override
            public List<Flight> doInTransaction(TransactionStatus transactionStatus) {
                List<Flight> results = new ArrayList<>();
                try {
                    results = flightDAO.getAllToPage(pageSize, pageNumber);
                } catch (DaoException e) {
                    transactionStatus.setRollbackOnly();
                    logger.debug(TRANSACTION_FAILED, e);
                }
                return results;
            }
        });
    }
}
