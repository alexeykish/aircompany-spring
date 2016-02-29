/**
 *
 */
package by.pvt.kish.aircompany.validators;

import by.pvt.kish.aircompany.pojos.Employee;
import org.springframework.stereotype.Component;

/**
 * Describes the utility class to test the Employee object before adding or changing it in the DB
 *
 * @author Kish Alexey
 */
@Component
public class EmployeeValidator implements IValidator<Employee> {

    /**
     * Check the validity of:
     * <li>the presence of empty fields</li>
     *
     * @param employee - Employee object being checked
     * @return - Null, if everything checks out correctly; error page if the data is incorrect
     */
    public String validate(Employee employee) {

//    if (checkEmpty(employee)) {
//        return Message.ERROR_EMPTY;
//    }
//    if (checkEntry(employee)) {
//        return Message.ERROR_TEAM_VALID;
//    }
//    if (checkPositions(id, employee)) {
//        return Message.ERROR_TEAM_POSITIONS_VALID;
//    }
//    if (checkEmployee(id, employee)) {
//        return Message.ERROR_TEAM_MEMBER_VALID;
//    }
    return null;
}


    /**
     * The method checks the position of employees to their positions on the team
     *
     * @param team - The flight team being checked
     * @return false if it corresponds to the position correctly, true if found at least one to inadequate
     */
//    private boolean checkPositions(Long fid, List<Long> team) throws ServiceException {
//        List<Employee> list = new ArrayList<>();
//        for (Long i : team) {
//            list.add(employeeService.getById(i));
//        }
//        Plane plane = flightService.getById(fid).getPlane();
//        int num_pilots = 0;
//        int num_navigators = 0;
//        int num_radiooperators = 0;
//        int num_stewardesses = 0;
//        for (Employee e : list) {
//            if (e.getPosition().equals(Position.PILOT)) {
//                num_pilots++;
//            }
//            if (e.getPosition().equals(Position.NAVIGATOR)) {
//                num_navigators++;
//            }
//            if (e.getPosition().equals(Position.RADIOOPERATOR)) {
//                num_radiooperators++;
//            }
//            if (e.getPosition().equals(Position.STEWARDESS)) {
//                num_stewardesses++;
//            }
//        }
//        return num_pilots != plane.getPlaneCrew().getNumberOfPilots() ||
//                num_navigators != plane.getPlaneCrew().getNumberOfNavigators() ||
//                num_radiooperators != plane.getPlaneCrew().getNumberOfRadiooperators() ||
//                num_stewardesses != plane.getPlaneCrew().getNumberOfStewardesses();
//    }
//
//    /**
//     * The method checks the object to <code>null</code> completeness of all positions are empty positions are not allowed
//     *
//     * @param team - The flight team being checked
//     * @return - false if all positions are filled, true if one of the items is null
//     */
//    private boolean checkEmpty(List<Long> team) {
//        if (team == null) {
//            return true;
//        }
//        for (Long i : team) {
//            if (i == null) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * The method checks the uniqueness of each employee in the team, the employee should take no more than one position
//     *
//     * @param team - The flight team being checked
//     * @return - false all items are unique, true if a match is found
//     */
//    private boolean checkEntry(List<Long> team) {
//        Set<Long> set = new HashSet<>(team);
//        return set.size() != team.size();
//    }
//
//    public boolean checkEmployee(Long id, List<Long> team) throws ServiceException, ServiceValidateException {
//        Date flightDate = (Date) flightService.getById(id).getDate();
//        for (Long eid : team) {
//            if (employeeService.checkEmployeeAvailability(eid, flightDate)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
