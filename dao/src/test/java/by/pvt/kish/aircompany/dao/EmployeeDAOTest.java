package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.dao.impl.EmployeeDAO;
import by.pvt.kish.aircompany.dao.impl.FlightDAO;
import by.pvt.kish.aircompany.enums.EmployeeStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.utils.HibernateUtil;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testDaoContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EmployeeDAOTest {

    @Autowired
    private EmployeeDAO employeeDao;

    @Autowired
    private FlightDAO flightDao;

    private Employee testEmployee;
    Employee addedEmployee;
    private Long id;

    @Before
    public void setUp() throws Exception {
        testEmployee = new Employee();
        testEmployee.setFirstName("FirstName");
        testEmployee.setLastName("LastName");
        testEmployee.setPosition(Position.PILOT);
        testEmployee = employeeDao.add(testEmployee);
        id = testEmployee.getEid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testEmployee);
        addedEmployee = employeeDao.getById(id);
        assertEquals("Add method failed: wrong firstname", addedEmployee, testEmployee);
    }

    @Test
    public void testUpdate() throws Exception {
        Employee prepareToUpdateEmployee = employeeDao.getById(id);
        prepareToUpdateEmployee.setFirstName("updatedFirstname");
        prepareToUpdateEmployee.setLastName("updatedLastname");
        prepareToUpdateEmployee.setPosition(Position.NAVIGATOR);
        employeeDao.update(prepareToUpdateEmployee);
        Employee updatedEmployee = employeeDao.getById(id);
        assertEquals("Update method failed: wrong eid", updatedEmployee, prepareToUpdateEmployee);
    }

    @Test
    public void testGetAll() throws Exception {
        int count =  employeeDao.getAll().size();
        int countFact = employeeDao.getCount();
        assertEquals("Get all method failed", count, countFact);
    }

    @Test
    public void testDelete() throws Exception {
        employeeDao.delete(id);
        assertNull("Delete employee: failed", employeeDao.getById(id));
    }

    @Test
    public void testSetStatus() throws Exception {
        Employee prepareToUpdateEmployee = new Employee();
        prepareToUpdateEmployee.setEid(id);
        employeeDao.setEmployeeStatus(id, EmployeeStatus.BLOCKED);
        Employee updatedEmployee = employeeDao.getById(id);
        assertEquals("Update method failed: wrong status", updatedEmployee.getStatus(), prepareToUpdateEmployee.getStatus());
    }

    @Test
    public  void testEmployeeAvailability() throws Exception {
        Flight testFlight = new Flight();
        Date testDate1 = new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L);
        testFlight.setDate(testDate1);
        Set<Employee> testCrew = new HashSet<>();
        testCrew.add(testEmployee);
        testFlight.setCrew(testCrew);
        flightDao.add(testFlight);
        assertFalse("Expected false, but failed" , employeeDao.checkEmployeeAvailability(id, testDate1));
        Date testDate2 = new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 2);
        assertTrue("Expected false, but failed" , employeeDao.checkEmployeeAvailability(id, testDate2));
    }
}