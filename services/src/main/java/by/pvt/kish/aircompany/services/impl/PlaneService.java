package by.pvt.kish.aircompany.services.impl;

import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.dao.IPlaneDAO;
import by.pvt.kish.aircompany.enums.PlaneStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.services.BaseService;
import by.pvt.kish.aircompany.services.IPlaneService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * This class represents a concrete implementation of the IService interface for plane model.
 *
 * @author Kish Alexey
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class PlaneService extends BaseService<Plane> implements IPlaneService{

    private static Logger logger = Logger.getLogger(PlaneService.class);

    @Autowired
    private IPlaneDAO planeDAO;

    /**
     * Set plane status to the DB
     *
     * @param id     - The ID of the plane
     * @param status - The status to be changed
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    public void setStatus(Long id, PlaneStatus status) throws ServiceException, ServiceValidateException {
        if (id == null) {
            throw new ServiceValidateException(Message.ERROR_ID_MISSING);
        }
        try {
            planeDAO.setPlaneStatus(id, status);
            logger.debug(SUCCESSFUL_TRANSACTION);
        } catch (DaoException e) {
            logger.debug(TRANSACTION_FAILED);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Returns a list of five last flights of the concrete plane from the DB
     *
     * @param id - The ID of the plane
     * @return - the list of last five flight of the concrete plane
     * @throws DaoException If something fails at DB level
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Flight> getPlaneLastFiveFlights(Long id) throws ServiceException, ServiceValidateException {
        List<Flight> results;
        if (id < 0) {
            throw new ServiceValidateException(Message.ERROR_ID_MISSING);
        }
        try {
            results =  planeDAO.getPlaneLastFiveFlights(id);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return results;
    }

    /**
     * Returns a list of all available planes at this date from the DB
     * @param date - The date of the flight
     * @return - a list of all available employees at this date from the DB
     * @throws ServiceException If something fails at DAO level
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Plane> getAllAvailable(Date date) throws ServiceException {
        List<Plane> results;
        try {
            results =  planeDAO.getAllAvailablePlanes(date);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        return results;
    }
}
