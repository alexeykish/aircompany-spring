package by.pvt.kish.aircompany.services.impl;

import by.pvt.kish.aircompany.dao.impl.AirportDAO;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.services.BaseService;
import by.pvt.kish.aircompany.validators.AirportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class represents a concrete implementation of the IService interface for airport model.
 *
 * @author Kish Alexey
 */
@Service
public class AirportService extends BaseService<Airport> {

}
