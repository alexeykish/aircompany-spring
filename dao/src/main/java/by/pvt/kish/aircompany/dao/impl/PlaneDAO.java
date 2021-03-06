package by.pvt.kish.aircompany.dao.impl;

import by.pvt.kish.aircompany.dao.BaseDAO;
import by.pvt.kish.aircompany.dao.IPlaneDAO;
import by.pvt.kish.aircompany.enums.PlaneStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * This class represents a concrete implementation of the IDAO interface for plane model.
 *
 * @author Kish Alexey
 */
@Repository
public class PlaneDAO extends BaseDAO<Plane> implements IPlaneDAO{

    private static final String HQL_UPDATE_PLANE_STATUS = "update FROM Plane P set P.status =:planestatus where P.pid =:id";
    private static final String HQL_GET_PLANES_LAST_FIVE_FLIGHTS = "select F from Flight F where F.plane.pid =:id ORDER BY F.date DESC";
    private static final String HQL_GET_ALL_AVAILABLE_PLANES = "select P from Plane P where not P.status=:planestatus and P.pid not in (select F.plane from Flight F where F.date=:date)";

    private static final String MESSAGE_UPDATE_PLANE_STATUS_FAIL = "Updating plane status failed";
    private static final String MESSAGE_GET_PLANE_FLIGHTS_FAIL = "Getting planes flights failed";
    private static final String MESSAGE_GET_ALL_AVAILABLE_PLANES_FAIL = "Getting all available planes failed";

    private static final String QUERY_PARAMETER_PLANE_STATUS = "planestatus";
    private static final String QUERY_PARAMETER_ID = "id";
    private static final String QUERY_PARAMETER_DATE = "date";

    @Autowired
    private PlaneDAO(SessionFactory sessionFactory) {
        super(Plane.class, sessionFactory);
    }

    /**
     * Update particular plane status int the DB matching the given ID
     *
     * @param id - The ID of the flight
     * @throws DaoException If something fails at DB level
     */
    @Override
    public void setPlaneStatus(Long id, PlaneStatus status) throws DaoException {
        try {
            Query query = getSession().createQuery(HQL_UPDATE_PLANE_STATUS);
            query.setParameter(QUERY_PARAMETER_PLANE_STATUS,status);
            query.setParameter(QUERY_PARAMETER_ID,id);
            query.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException(MESSAGE_UPDATE_PLANE_STATUS_FAIL);
        }
    }

    /**
     * Returns a list of five last flights of the concrete plane from the DB
     *
     * @param id - The ID of the plane
     * @return - the list of last five flight of the concrete plane
     * @throws DaoException If something fails at DB level
     */
    public List<Flight> getPlaneLastFiveFlights(Long id) throws DaoException {
        List<Flight> flights;
        try {
            Query query = getSession().createQuery(HQL_GET_PLANES_LAST_FIVE_FLIGHTS);
            query.setParameter(QUERY_PARAMETER_ID,id);
            query.setMaxResults(5);
            flights = query.list();
        } catch (HibernateException e) {
            throw new DaoException(MESSAGE_GET_PLANE_FLIGHTS_FAIL);
        }
        return flights;
    }

    /**
     * Returns a list of all available planes at this date from the DB
     * @param date - The date of the flight
     * @return - a list of all available employees at this date from the DB
     * @throws DaoException If something fails at DB level
     */
    @Override
    public List<Plane> getAllAvailablePlanes(Date date) throws DaoException {
        List<Plane> planes;
        try {
            Query query = getSession().createQuery(HQL_GET_ALL_AVAILABLE_PLANES);
            query.setParameter(QUERY_PARAMETER_DATE, date);
            query.setParameter(QUERY_PARAMETER_PLANE_STATUS, PlaneStatus.AVAILABLE);
            planes = query.list();
        } catch (HibernateException e) {
            throw new DaoException(MESSAGE_GET_ALL_AVAILABLE_PLANES_FAIL);
        }
        return planes;
    }
}
