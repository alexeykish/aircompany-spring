package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.enums.PlaneStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;

import java.util.Date;
import java.util.List;

/**
 * This interface represents a contract for a IDAO for the plane model.
 *
 * @author Kish Alexey
 */
public interface IPlaneDAO extends IDAO<Plane>{

    /**
     * Update particular plane status int the DB matching the given ID
     *
     * @param id - The ID of the flight
     * @throws DaoException If something fails at DB level
     */
    void setPlaneStatus(Long id, PlaneStatus status) throws DaoException;

    /**
     * Returns a list of five last flights of the concrete plane from the DB
     *
     * @param id - The ID of the plane
     * @return - the list of last five flight of the concrete plane
     * @throws DaoException If something fails at DB level
     */
    List<Flight> getPlaneLastFiveFlights(Long id) throws DaoException;

    /**
     * Returns a list of all available planes at this date from the DB
     * @param date - The date of the flight
     * @return - a list of all available planes at this date from the DB
     * @throws DaoException If something fails at DB level
     */
    List<Plane> getAllAvailablePlanes(Date date) throws DaoException;
}
