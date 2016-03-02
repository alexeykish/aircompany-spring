package by.pvt.kish.aircompany.services;

import by.pvt.kish.aircompany.pojos.Address;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.services.impl.AirportService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testServiceContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AirportServiceTest {

    @Autowired
    private AirportService airportService;

    private Long id;
    private Airport testAirport;
    Airport addedAirport;

    @Before
    public void setUp() throws Exception {
        Address testAddress = new Address("testCountry", "testCity");
        testAirport = new Airport();
        testAirport.setName("testName");
        testAirport.setAddress(testAddress);
        testAirport = airportService.add(testAirport);
        id = testAirport.getAid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testAirport);
        addedAirport = airportService.getById(id);
        assertEquals("Add method failed: notEquals", testAirport, addedAirport);
    }

    @Test
    public void testUpdate() throws Exception {
        Airport prepareToUpdateAirport = new Airport();
        prepareToUpdateAirport.setAid(id);
        prepareToUpdateAirport.setName("updatedCity");
        airportService.update(prepareToUpdateAirport);
        Airport updatedAirport = airportService.getById(id);
        assertEquals("Update method failed", prepareToUpdateAirport, updatedAirport);
    }

    @Test
    public void testGetAll() throws Exception {
        int countAirports = airportService.getAll().size();
        int countLines = airportService.getCount();
        assertEquals("Get all method failed", countLines, countAirports);
    }

    @Test
    public void testDelete() throws Exception {
        airportService.delete(id);
        assertNull("Delete method: failed", airportService.getById(id));
    }
}