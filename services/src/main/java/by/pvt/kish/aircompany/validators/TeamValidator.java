/**
 *
 */
package by.pvt.kish.aircompany.validators;

import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.services.IEmployeeService;
import by.pvt.kish.aircompany.services.IFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describes the utility class to test the flight team before adding or changing it in the DB
 *
 * @author Kish Alexey, 2015
 */
@Component
public class TeamValidator {

    private static final String ERROR_EMPTY = "Empty data at request";
    private static final String ERROR_TEAM_VALID = "Duplicate employees in flight crew";
    private static final String ERROR_TEAM_POSITIONS_VALID = "Wrong employee position in flight crew";
    private static final String ERROR_TEAM_MEMBER_VALID = "Employee is already busy in the other team at this day";

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IEmployeeService employeeService;

    /**
     * Check the validity of:
     * <li>the presence of empty fields</li>
     * <li>one and the same employee should not be in command at several positions</li>
     * <li>each employee must be located at a position corresponding to its position</li>
     * <li>each employee must be free from another flights at flights date</li>
     *
     * @param id   - The flight id
     * @param team - The flight team being checked
     */
    public void validate(Long id, List<Long> team, boolean updateKey) throws ServiceException, ServiceValidateException {
        if (checkEmpty(team)) {
            throw new ServiceValidateException(ERROR_EMPTY);
        }
        if (checkEntry(team)) {
            throw new ServiceValidateException(ERROR_TEAM_VALID);
        }
        if (checkPositions(id, team)) {
            throw new ServiceValidateException(ERROR_TEAM_POSITIONS_VALID);
        }
        if (!updateKey) {
            if (checkEmployee(id, team)) {
                throw new ServiceValidateException(ERROR_TEAM_MEMBER_VALID);
            }
        }
    }


    /**
     * The method checks the position of employees to their positions on the team
     *
     * @param team - The flight team being checked
     * @return false if it corresponds to the position correctly, true if found at least one to inadequate
     */
    private boolean checkPositions(Long fid, List<Long> team) throws ServiceException, ServiceValidateException {
        List<Employee> list = new ArrayList<>();
        for (Long i : team) {
            list.add(employeeService.getById(i));
        }
        Plane plane = flightService.getById(fid).getPlane();
        int num_pilots = 0;
        int num_navigators = 0;
        int num_radiooperators = 0;
        int num_stewardesses = 0;
        for (Employee e : list) {
            if (e.getPosition().equals(Position.PILOT)) {
                num_pilots++;
            }
            if (e.getPosition().equals(Position.NAVIGATOR)) {
                num_navigators++;
            }
            if (e.getPosition().equals(Position.RADIOOPERATOR)) {
                num_radiooperators++;
            }
            if (e.getPosition().equals(Position.STEWARDESS)) {
                num_stewardesses++;
            }
        }
        return num_pilots != plane.getPlaneCrew().getNumberOfPilots() ||
                num_navigators != plane.getPlaneCrew().getNumberOfNavigators() ||
                num_radiooperators != plane.getPlaneCrew().getNumberOfRadiooperators() ||
                num_stewardesses != plane.getPlaneCrew().getNumberOfStewardesses();
    }

    /**
     * The method checks the object to <code>null</code> completeness of all positions are empty positions are not allowed
     *
     * @param team - The flight team being checked
     * @return - false if all positions are filled, true if one of the items is null
     */
    private boolean checkEmpty(List<Long> team) {
        if (team == null) {
            return true;
        }
        for (Long i : team) {
            if (i == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * The method checks the uniqueness of each employee in the team, the employee should take no more than one position
     *
     * @param team - The flight team being checked
     * @return - false all items are unique, true if a match is found
     */
    private boolean checkEntry(List<Long> team) {
        Set<Long> set = new HashSet<>(team);
        return set.size() != team.size();
    }

    private boolean checkEmployee(Long id, List<Long> team) throws ServiceException, ServiceValidateException {
        Date flightDate = (Date) flightService.getById(id).getDate();
        for (Long eid : team) {
            if (employeeService.checkEmployeeAvailability(eid, flightDate)) {
                return true;
            }
        }
        return false;
    }
}
