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
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.TeamCreator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_ADD_EMPLOYEE);
                    return "redirect:/employeeList";
                }
            } else {
                model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "employee/add";
    }

    @RequestMapping(value = "/deleteEmployee/{id}")
    public String deleteEmployee(RedirectAttributes redirectAttributes,
                                 @PathVariable("id") Long id) {
        try {
            employeeService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_DELETE_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "redirect:/employeeList";
    }

    @RequestMapping(value = "/employeeReport/{id}")
    public String createEmployeeReport(ModelMap model,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request) {
        try {
            Employee employee = employeeService.getById(id);
            List<Flight> flights = employeeService.getEmployeeLastFiveFlights(employee.getEid());
            model.addAttribute(Attribute.EMPLOYEE, employee);
            model.addAttribute(Attribute.FLIGHTS, flights);
            model.addAttribute(Attribute.STATUSES, Arrays.asList(EmployeeStatus.values()));
            model.addAttribute(Attribute.PERMISSION_CHANGE_DELETE_STATUS, flights.size() != 0);
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
            model.addAttribute(Attribute.EMPLOYEES, employeeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/list";
    }

    @RequestMapping(value = "/changeEmployeeStatus/{id}")
    public String changeEmployeeStatus(@PathVariable("id") Long id,
                                       @RequestParam("status") String status,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        try {
            employeeService.setStatus(id, EmployeeStatus.valueOf(status));
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_SET_STATUS_EMPLOYEE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "redirect:/employeeList";
    }

    @RequestMapping(value = "/updateEmployee")
    public String updateEmployee(ModelMap model,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (employee != null) {
                    employeeService.update(employee);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_UPDATE_EMPLOYEE);
                    return "redirect:/employeeList";
                }
            } else {
                model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "employee/update";
    }

    @RequestMapping(value = "/addCrewPage/{id}")
    public String addCrew(ModelMap model,
                          @PathVariable("id") Long id,
                          HttpServletRequest request) {
        try {
            List<Employee> crew = employeeService.getFlightCrewByFlightId(id);
            Flight flight = flightService.getById(id);
            model.addAttribute(Attribute.FLIGHT, flight);
            model.addAttribute(Attribute.EMPLOYEES, employeeService.getAllAvailable(flight.getDate()));
            model.addAttribute(Attribute.POSITIONS, TeamCreator.getPlanePositions(planeService.getById(flight.getPlane().getPid())));
            if (crew.size() != 0) {
                model.addAttribute(Attribute.TEAM, crew);
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
        model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
        return "employee/add";
    }

    @RequestMapping(value = "/updateEmployeePage/{id}")
    public String showUpdateEmployeePage(ModelMap model,
                                         @PathVariable("id") Long id) {
        try {
            model.addAttribute(Attribute.EMPLOYEE, employeeService.getById(id));
            model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
        } catch (ServiceException e) {
            ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "employee/update";
    }
}
