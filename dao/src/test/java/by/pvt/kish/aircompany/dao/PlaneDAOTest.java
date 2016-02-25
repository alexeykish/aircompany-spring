package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.dao.impl.FlightDAO;
import by.pvt.kish.aircompany.dao.impl.PlaneDAO;
import by.pvt.kish.aircompany.enums.PlaneStatus;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.pojos.PlaneCrew;
import by.pvt.kish.aircompany.utils.HibernateUtil;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testDaoContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class PlaneDAOTest {

    @Autowired
    private PlaneDAO planeDAO;

    @Autowired
    private FlightDAO flightDao;

    private Long id;
    private Plane testPlane;
    private PlaneCrew testCrew;

    @Before
    public void setUp() throws Exception {
        testPlane = new Plane("testModel", 100, 200);
        testCrew = new PlaneCrew(1, 1, 1, 1);

        testPlane.setPlaneCrew(testCrew);
        testCrew.setPlane(testPlane);

        testPlane = planeDAO.add(testPlane);
        id = testPlane.getPid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testPlane);
        assertNotNull("Add method failed: null", testCrew);
        Plane addedPlane = planeDAO.getById(id);
        assertEquals("Add method failed: wrong plane", addedPlane, testPlane);
        assertEquals("Add method failed: wrong planeCrew", addedPlane.getPlaneCrew(), testCrew);
    }

    @Test
    public void testUpdate() throws Exception {
        Plane prepareToUpdatePlane = planeDAO.getById(id);
        prepareToUpdatePlane.setModel("updatedModel");
        prepareToUpdatePlane.setCapacity(300);
        prepareToUpdatePlane.setFlightRange(400);

        PlaneCrew updatedCrew = new PlaneCrew(2, 2, 2, 2);
        updatedCrew.setPid(id);
        prepareToUpdatePlane.setPlaneCrew(updatedCrew);
        planeDAO.update(prepareToUpdatePlane);

        Plane updatedPlane = planeDAO.getById(id);
        assertEquals("Update method failed: wrong pid", prepareToUpdatePlane, updatedPlane);
        assertEquals("Update method failed: wrong model", prepareToUpdatePlane.getPlaneCrew(), updatedPlane.getPlaneCrew());
    }

    @Test
    public void testGetAll() throws Exception {
        int countPlanes = planeDAO.getAll().size();
        int countLines = planeDAO.getCount();
        assertEquals("Get all method failed", countLines, countPlanes);
    }

    @Test
    public void testDelete() throws Exception {
        planeDAO.delete(id);
        assertNull("Delete method: failed", planeDAO.getById(id));
    }

    @Test

    public void testSetStatus() throws Exception {
        Plane prepareToUpdateStatusPlane = planeDAO.getById(id);
        planeDAO.setPlaneStatus(id, PlaneStatus.BLOCKED);
        Plane updatedStatusPlane = planeDAO.getById(id);
        assertEquals("Update method failed: wrong status", updatedStatusPlane.getStatus(), prepareToUpdateStatusPlane.getStatus());
    }

    @Test
    public void testGetAllAvailable() throws  Exception {
        Plane plane1 = new Plane("model1", 100, 100);
        plane1.setStatus(PlaneStatus.AVAILABLE);
        Plane plane2 = new Plane("model2", 100, 100);
        plane2.setStatus(PlaneStatus.BLOCKED);
        planeDAO.add(plane1);
        planeDAO.add(plane2);
        Flight flight = new Flight();
        Date testDate1 = new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L);
        flight.setDate(testDate1);
        flight.setPlane(testPlane);
        flightDao.add(flight);
        List<Plane> planes = planeDAO.getAllAvailablePlanes(testDate1);
        for (Plane p: planes) {
            System.out.println(p);
        }
        System.out.println("List size = " + planes.size());
    }
}