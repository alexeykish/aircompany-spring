package by.pvt.kish.aircompany.services;

import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.enums.Waypoint;
import by.pvt.kish.aircompany.pojos.*;
import by.pvt.kish.aircompany.services.impl.EmployeeService;
import by.pvt.kish.aircompany.services.impl.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testServiceContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional()
public class FlightServiceTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private EmployeeService employeeService;

    private Flight testFlight1;
    private Flight testFlight2;
    Address testAddress1;
    Address testAddress2;
    private Long id1;
    private Long id2;

    @Before
    public void setUp() throws Exception {
        testAddress1 = new Address("testCountry1", "testCity1");
        testAddress2 = new Address("testCountry2", "testCity2");
        Airport departedAirport = new Airport("departedAirport", testAddress1);
        Airport arrivalAirport = new Airport("arrivalAirport", testAddress2);
        Map<Waypoint, Airport> waypoints = new HashMap<>();
        waypoints.put(Waypoint.ARRIVAL, arrivalAirport);
        waypoints.put(Waypoint.DEPARTURE, departedAirport);
        PlaneCrew planeCrew1 = new PlaneCrew(1, 1, 1, 1);
        PlaneCrew planeCrew2 = new PlaneCrew(1, 0, 0, 1);
        Plane plane1 = new Plane("testModel", 100, 100);
        Plane plane2 = new Plane("testModel", 100, 100);

        plane1.setPlaneCrew(planeCrew1);
        plane2.setPlaneCrew(planeCrew2);
        planeCrew1.setPlane(plane1);
        planeCrew2.setPlane(plane2);

        testFlight1 = new Flight();
        testFlight1.setDate(new Date());
        testFlight1.setWaypoints(waypoints);
        testFlight1.setPlane(plane1);

        testFlight2 = new Flight();
        testFlight2.setDate(new Date());

        testFlight2.setWaypoints(waypoints);
        testFlight2.setPlane(plane2);

        Employee employeePilot = new Employee("Name", "Name", Position.PILOT);
        Employee employeeNavigator = new Employee("Name", "Name", Position.NAVIGATOR);
        Employee employeeRadiooperator = new Employee("Name", "Name", Position.RADIOOPERATOR);
        Employee employeeStewardess = new Employee("Name", "Name", Position.STEWARDESS);

        employeePilot.getFlights().add(testFlight1);
        employeeNavigator.getFlights().add(testFlight1);
        employeeRadiooperator.getFlights().add(testFlight1);
        employeeStewardess.getFlights().add(testFlight1);

        employeePilot.getFlights().add(testFlight2);
        employeeStewardess.getFlights().add(testFlight2);

        Set<Employee> testCrew1 = new HashSet<>();
        testCrew1.add(employeePilot);
        testCrew1.add(employeeNavigator);
        testCrew1.add(employeeRadiooperator);
        testCrew1.add(employeeStewardess);

        Set<Employee> testCrew2 = new HashSet<>();
        testCrew2.add(employeePilot);
        testCrew2.add(employeeStewardess);

        testFlight1.setCrew(testCrew1);
        testFlight2.setCrew(testCrew2);

        testFlight1 = flightService.add(testFlight1);
        testFlight2 = flightService.add(testFlight2);

        id1 = testFlight1.getFid();
        id2 = testFlight2.getFid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testFlight1);
        assertNotNull("Add method failed: null", testFlight2);
        Flight addedFlight1 = flightService.getById(id1);
        Flight addedFlight2 = flightService.getById(id2);
        assertEquals("Add method failed: wrong firstname", addedFlight1.getCrew().size(), testFlight1.getCrew().size());
        assertEquals("Add method failed: wrong firstname", addedFlight2.getCrew().size(), testFlight2.getCrew().size());
    }

    @Test
    public void testUpdate() throws Exception {
        Flight prepareToUpdateFlight = flightService.getById(id1);
        Airport changedDepartureAirport = new Airport("changedDepartureAirport", testAddress2);
        Airport changedArrivalAirport = new Airport("changedArrivalAirport", testAddress1);
        prepareToUpdateFlight.setDate(new Date());
        Map<Waypoint, Airport> changedWaypoints = new HashMap<>();
        changedWaypoints.put(Waypoint.ARRIVAL, changedArrivalAirport);
        changedWaypoints.put(Waypoint.DEPARTURE, changedDepartureAirport);
        prepareToUpdateFlight.setWaypoints(changedWaypoints);
        flightService.update(prepareToUpdateFlight);
        Flight updatedFlight = flightService.getById(id1);
        assertEquals("Update method failed: wrong eid", updatedFlight, prepareToUpdateFlight);
    }

    @Test
    public void testGetAll() throws Exception {
        int count = flightService.getAll().size();
        int countFact = flightService.getCount();
        assertEquals("Get all method failed", count, countFact);
    }

    @Test
    public void testDelete() throws Exception {
        flightService.delete(id1);
        flightService.delete(id2);
        assertNull("Delete employee: failed", flightService.getById(id1));
        assertNull("Delete employee: failed", flightService.getById(id2));
    }

    @Test
    public void testSetStatus() throws Exception {
        Flight prepareToUpdateStatusFlight = flightService.getById(id1);
        flightService.setStatus(id1, FlightStatus.CANCELED);
        Flight updatedStatusFlight = flightService.getById(id1);
        assertEquals("Update method failed: wrong status", updatedStatusFlight.getStatus(), prepareToUpdateStatusFlight.getStatus());
    }

    @Test
    public void testGetCrewByFlightId() throws Exception {
        List<Employee> executedCrew = employeeService.getFlightCrewByFlightId(id1);
        assertEquals("Get crew method failed", testFlight1.getCrew().size(), executedCrew.size());
    }

}