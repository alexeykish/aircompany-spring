package by.pvt.kish.aircompany.dao.impl;

import by.pvt.kish.aircompany.dao.BaseDAO;
import by.pvt.kish.aircompany.dao.IFlightDAO;
import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.DaoException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a concrete implementation of the IDAO interface for flight model.
 *
 * @author Kish Alexey
 */
@Repository
public class FlightDAO extends BaseDAO<Flight> implements IFlightDAO{

    private static final String HQL_UPDATE_FLIGHT_STATUS = "UPDATE FROM Flight f SET f.status =:flightstatus WHERE f.fid =:id";

    private static final String UPDATE_FLIGHT_STATUS_FAIL = "Updating flight status failed";

    @Autowired
    private FlightDAO(SessionFactory sessionFactory) {
        super(Flight.class, sessionFactory);
    }

    /**
     * Update particular plane status int the DB matching the given ID
     *
     * @param id - The ID of the flight
     * @throws DaoException If something fails at DB level
     */
    public void setFlightStatus(Long id, FlightStatus status) throws DaoException {
        try {
            Query query = getSession().createQuery(HQL_UPDATE_FLIGHT_STATUS);
            query.setParameter("flightstatus",status);
            query.setParameter("id",id);
            query.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException(UPDATE_FLIGHT_STATUS_FAIL);
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
    public List<Flight> getAllToPage(int pageSize, int pageNumber) throws DaoException {
        List<Flight> results = new ArrayList<>();
        try {
            Criteria criteria = getSession().createCriteria(Flight.class);
            criteria.addOrder(Order.desc("date"));
            criteria.setFirstResult((pageNumber - 1) * pageSize);
            criteria.setMaxResults(pageSize);
            results = criteria.list();
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
        return results;
    }
}
