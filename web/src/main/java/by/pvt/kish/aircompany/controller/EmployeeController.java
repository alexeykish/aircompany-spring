package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.enums.EmployeeStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.IEmployeeService;
import by.pvt.kish.aircompany.services.IFlightService;
import by.pvt.kish.aircompany.services.IPlaneService;
import by.pvt.kish.aircompany.services.impl.EmployeeService;
import by.pvt.kish.aircompany.services.impl.FlightService;
import by.pvt.kish.aircompany.services.impl.PlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.TeamCreator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kish Alexey
 */
@Controller
public class EmployeeController {

    private static String className = EmployeeController.class.getName();
    private static Logger logger = Logger.getLogger(EmployeeController.class.getName());

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IPlaneService planeService;

    @ModelAttribute("employee")
    public Employee createEmployee() {
        return new Employee();
    }

    @RequestMapping(value = "/addEmployee")
    public String addEmployee(ModelMap model,
                              @Valid @ModelAttribute("employee") Employee employee,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (employee != null) {
                    employeeService.add(employee);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_EMPLOYEE);
                    return "redirect:/employeeList";
                }
            } else {
                model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, Arrays.asList(Position.values()));
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "employee/add";
    }

    @RequestMapping(value = "/deleteEmployee/{id}")
    public String deleteEmployee(ModelMap model,
                                 @PathVariable("id") Long id) {
        try {
            employeeService.delete(id);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/employeeReport/{id}")
    public String createEmployeeReport(ModelMap model,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request) {
        try {
            Employee employee = employeeService.getById(id);
            List<Flight> flights = employeeService.getEmployeeLastFiveFlights(employee.getEid());
            boolean permissionChangeDeleteStatus = flights.size() != 0;
            List<EmployeeStatus> employeeStatuses = Arrays.asList(EmployeeStatus.values());

            model.addAttribute(Attribute.EMPLOYEE_ATTRIBUTE, employee);
            model.addAttribute(Attribute.FLIGHTS_ATTRIBUTE, flights);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, employeeStatuses);
            model.addAttribute(Attribute.PERMISSION_CHANGE_DELETE_STATUS_ATTRIBUTE, permissionChangeDeleteStatus);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "employee/report";
    }

    @RequestMapping(value = "/employeeList")
    public String getAllEmployees(ModelMap model) {
        try {
            List<Employee> employees = employeeService.getAll();

            model.addAttribute(Attribute.EMPLOYEES_ATTRIBUTE, employees);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/list";
    }

    @RequestMapping(value = "/changeEmployeeStatus/{id}")
    public String changeEmployeeStatus(ModelMap model,
                                       @PathVariable("id") Long id,
                                       @RequestParam("status") String status,
                                       HttpServletRequest request) {
        try {
            employeeService.setStatus(id, EmployeeStatus.valueOf(status));
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_SET_STATUS_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/updateEmployee")
    public String updateEmployee(ModelMap model,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (employee != null) {
                    employeeService.update(employee);
                    model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_EMPLOYEE);
                    return "redirect:/employeeList";
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "employee/add";
    }

    @RequestMapping(value = "/addCrewPage/{id}")
    public String addCrew(ModelMap model,
                          @PathVariable("id") Long id,
                          HttpServletRequest request) {
        try {
            List<Employee> crew = employeeService.getFlightCrewByFlightId(id);
            Flight flight = flightService.getById(id);
            List<Employee> employees = employeeService.getAllAvailable(flight.getDate());
            List<String> positions = TeamCreator.getPlanePositions(planeService.getById(flight.getPlane().getPid()));
            model.addAttribute(Attribute.FLIGHT_ATTRIBUTE, flight);
            model.addAttribute(Attribute.EMPLOYEES_ATTRIBUTE, employees);
            model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, positions);
            if (crew.size() != 0) {
                model.addAttribute(Attribute.TEAM_ATTRIBUTE, crew);
                return "updateCrew";
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "addCrew";
    }

    @RequestMapping(value = "/addEmployeePage")
    public String showAddEmployeePage(ModelMap model) {
        model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, Arrays.asList(Position.values()));
        return "employee/add";
    }

    @RequestMapping(value = "/updateEmployeePage/{id}")
    public String showUpdateEmployeePage(ModelMap model,
                                         @PathVariable("id") Long id) {
        try {
            Employee employee = employeeService.getById(id);
            model.addAttribute(Attribute.EMPLOYEE_ATTRIBUTE, employee);
            model.addAttribute(Attribute.POSITIONS_ATTRIBUTE, Arrays.asList(Position.values()));
        } catch (ServiceException e) {
            ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/update";
    }
}
