package by.pvt.kish.aircompany.services;

import by.pvt.kish.aircompany.enums.EmployeeStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.impl.EmployeeService;
import by.pvt.kish.aircompany.services.impl.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testServiceContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FlightService flightService;

    private Employee testEmployee;
    Employee addedEmployee;
    private Long id;

    @Before
    public void setUp() throws Exception {
        testEmployee = new Employee();
        testEmployee.setFirstName("FirstName");
        testEmployee.setLastName("LastName");
        testEmployee.setPosition(Position.PILOT);
        testEmployee = employeeService.add(testEmployee);
        id = testEmployee.getEid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testEmployee);
        addedEmployee = employeeService.getById(id);
        assertEquals("Add method failed: wrong firstname", addedEmployee, testEmployee);
    }

    @Test
    public void testUpdate() throws Exception {
        Employee prepareToUpdateEmployee = employeeService.getById(id);
        prepareToUpdateEmployee.setFirstName("updatedFirstname");
        prepareToUpdateEmployee.setLastName("updatedLastname");
        prepareToUpdateEmployee.setPosition(Position.NAVIGATOR);
        employeeService.update(prepareToUpdateEmployee);
        Employee updatedEmployee = employeeService.getById(id);
        assertEquals("Update method failed: wrong eid", updatedEmployee, prepareToUpdateEmployee);
    }

    @Test
    public void testGetAll() throws Exception {
        int count =  employeeService.getAll().size();
        int countFact = employeeService.getCount();
        assertEquals("Get all method failed", count, countFact);
    }

    @Test
    public void testDelete() throws Exception {
        employeeService.delete(id);
        assertNull("Delete employee: failed", employeeService.getById(id));
    }

    @Test
    public void testSetStatus() throws Exception {
        Employee prepareToUpdateEmployee = new Employee();
        prepareToUpdateEmployee.setEid(id);
        employeeService.setStatus(id, EmployeeStatus.BLOCKED);
        Employee updatedEmployee = employeeService.getById(id);
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
        flightService.add(testFlight);
        assertFalse("Expected false, but failed" , employeeService.checkEmployeeAvailability(id, testDate1));
        Date testDate2 = new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 2);
        assertTrue("Expected false, but failed" , employeeService.checkEmployeeAvailability(id, testDate2));
    }



}