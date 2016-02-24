package by.pvt.kish.aircompany.dao.impl;

import by.pvt.kish.aircompany.dao.BaseDAO;
import by.pvt.kish.aircompany.pojos.Airport;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * This class represents a concrete implementation of the IDAO interface for airport model.
 *
 * @author Kish Alexey
 */
@Repository
public class AirportDAO extends BaseDAO<Airport> {

    @Autowired
    private AirportDAO(SessionFactory sessionFactory) {
        super(Airport.class, sessionFactory);
    }
}
