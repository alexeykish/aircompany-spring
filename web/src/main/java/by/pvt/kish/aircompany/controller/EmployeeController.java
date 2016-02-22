package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.enums.EmployeeStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.impl.EmployeeService;
import by.pvt.kish.aircompany.services.impl.FlightService;
import by.pvt.kish.aircompany.services.impl.PlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.TeamCreator;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kish Alexey
 */
@Controller
public class EmployeeController {

    private static String className = EmployeeController.class.getName();

    @RequestMapping(value = "/addEmployee")
    public String addEmployee(Model model,
                              @ModelAttribute Employee employee,
                              HttpServletRequest request) {
        try {
            EmployeeService.getInstance().add(employee);

            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/deleteEmployee")
    public String deleteEmployee(Model model,
                                 @RequestParam("eid") Long id) {
        try {
            EmployeeService.getInstance().delete(id);

            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/employeeReport")
    public String createEmployeeReport(Model model,
                                 @RequestParam("eid") Long id) {
        try {
            Employee employee = EmployeeService.getInstance().getById(id);
            List<Flight> flights = EmployeeService.getInstance().getEmployeeLastFiveFlights(employee.getEid());
            boolean permissionChangeDeleteStatus = flights.size() != 0;
            List<EmployeeStatus> employeeStatuses = Arrays.asList(EmployeeStatus.values());

            model.addAttribute(Attribute.EMPLOYEE_ATTRIBUTE, employee);
            model.addAttribute(Attribute.FLIGHTS_ATTRIBUTE, flights);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, employeeStatuses);
            model.addAttribute(Attribute.PERMISSION_CHANGE_DELETE_STATUS_ATTRIBUTE, permissionChangeDeleteStatus);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/report";
    }

    @RequestMapping(value = "/employeeList")
    public String getAllEmployees(Model model) {
        try {
            List<Employee> employees = EmployeeService.getInstance().getAll();

            model.addAttribute(Attribute.EMPLOYEES_ATTRIBUTE, employees);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/list";
    }

    @RequestMapping(value = "/changeEmployeeStatus")
    public String changeEmployeeStatus(Model model,
                                       @RequestParam("eid") Long id,
                                       @RequestParam("status") String status) {
        try {
            EmployeeService.getInstance().setStatus(id, EmployeeStatus.valueOf(status));

            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_SET_STATUS_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/updateEmployee")
    public String updateEmployee(Model model,
                                 @ModelAttribute Employee employee,
                                 HttpServletRequest request) {
        try {
            EmployeeService.getInstance().update(employee);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/addCrewPage")
    public String addCrew(Model model,
                          @RequestParam("fid") Long id) {
        try {
            List<Employee> crew = EmployeeService.getInstance().getFlightCrewByFlightId(id);
            Flight flight = FlightService.getInstance().getById(id);
            List<Employee> employees = EmployeeService.getInstance().getAllAvailable(flight.getDate());
            List<String> positions = TeamCreator.getPlanePositions(PlaneService.getInstance().getById(flight.getPlane().getPid()));
            model.addAttribute(Attribute.FLIGHT_ATTRIBUTE, flight);
            model.addAttribute(Attribute.EMPLOYEES_ATTRIBUTE, employees);
            model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, positions);

            if (crew.size() != 0) {
                model.addAttribute(Attribute.TEAM_ATTRIBUTE, crew);
                return "updateCrew";
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "addCrew";
    }

    @RequestMapping(value = "/addEmployeePage")
    public String showAddEmployeePage(Model model) {
        List<Position> positions = Arrays.asList(Position.values());
        model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, positions);
        return "employee/add";
    }

    @RequestMapping(value = "/updateEmployeePage")
    public String showUpdateEmployeePage(Model model,
                                         @RequestParam("eid") Long id) {
        try {
            Employee employee = EmployeeService.getInstance().getById(id);
            List<Position> positions = Arrays.asList(Position.values());
            model.addAttribute(Attribute.EMPLOYEE_ATTRIBUTE, employee);
            model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, positions);
        } catch (ServiceException e) {
            ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/update";
    }
}
