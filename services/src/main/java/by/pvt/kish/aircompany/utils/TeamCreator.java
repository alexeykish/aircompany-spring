package by.pvt.kish.aircompany.utils;

import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.pojos.PlaneCrew;
import by.pvt.kish.aircompany.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for TeamService. Contains utility for team creation
 *
 * @author Kish Alexey
 */
@Service
public class TeamCreator {

    @Autowired
    private IEmployeeService employeeService;

    /**
     * Returns list of team members, according to the needs of the crew for a specific aircraft.
     *
     * @param plane - aircraft for which formed the team
     * @return list of members of the crew for a specific aircraft
     */
    public static List<String> getPlanePositions(Plane plane) {
        List<String> positions = new ArrayList<>();
        PlaneCrew crew = plane.getPlaneCrew();
        for (int i = 0; i < crew.getNumberOfPilots(); i++) {
            positions.add(Position.PILOT.toString());
        }
        for (int i = 0; i < crew.getNumberOfNavigators(); i++) {
            positions.add(Position.NAVIGATOR.toString());
        }
        for (int i = 0; i < crew.getNumberOfRadiooperators(); i++) {
            positions.add(Position.RADIOOPERATOR.toString());
        }
        for (int i = 0; i < crew.getNumberOfStewardesses(); i++) {
            positions.add(Position.STEWARDESS.toString());
        }
        return positions;
    }

    /**
     * Returns set of employees by its ids
     *
     * @param team - set od employees id
     * @return set of members of the crew
     */
    public Set<Employee> getEmployeeListById(List<Long> team) throws ServiceException, ServiceValidateException {
        Set<Employee> employees = new HashSet<>();
        for (Long l: team) {
            employees.add(employeeService.getById(l));
        }
        return employees;
    }
}
