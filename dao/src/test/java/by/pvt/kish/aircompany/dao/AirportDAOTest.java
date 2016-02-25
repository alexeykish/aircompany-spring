package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.dao.impl.AirportDAO;
import by.pvt.kish.aircompany.pojos.Address;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.utils.HibernateUtil;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testDaoContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AirportDAOTest {

    @Autowired
    private AirportDAO airportDAO;

    private Long id;
    private Airport testAirport;
    Airport addedAirport;

    @Before
    public void setUp() throws Exception {
        Address testAddress = new Address("testCountry", "testCity");
        testAirport = new Airport();
        testAirport.setName("testName");
        testAirport.setAddress(testAddress);
        testAirport = airportDAO.add(testAirport);
        id = testAirport.getAid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testAirport);
        addedAirport = airportDAO.getById(id);
        assertEquals("Add method failed: notEquals", testAirport, addedAirport);
    }

    @Test
    public void testUpdate() throws Exception {
        Airport prepareToUpdateAirport = new Airport();
        prepareToUpdateAirport.setAid(id);
        prepareToUpdateAirport.setName("updatedCity");
        airportDAO.update(prepareToUpdateAirport);
        Airport updatedAirport = airportDAO.getById(id);
        assertEquals("Update method failed", prepareToUpdateAirport, updatedAirport);
    }

    @Test
    public void testGetAll() throws Exception {
        int countAirports = airportDAO.getAll().size();
        int countLines = airportDAO.getCount();
        assertEquals("Get all method failed", countLines, countAirports);
    }

    @Test
    public void testDelete() throws Exception {
        airportDAO.delete(id);
        assertNull("Delete method: failed", airportDAO.getById(id));
    }
}