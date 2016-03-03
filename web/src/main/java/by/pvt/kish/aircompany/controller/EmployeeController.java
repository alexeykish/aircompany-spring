package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.enums.EmployeeStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.IEmployeeService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Kish Alexey
 */
@Controller
@RequestMapping(value = "/employee")
public class EmployeeController {

    private static String className = EmployeeController.class.getName();
    private static Logger logger = Logger.getLogger(EmployeeController.class.getName());

    private static final String REDIRECT_PATH_EMPLOYEE_MAIN = "redirect:/employee/main";
    private static final String PATH_EMPLOYEE_ADD = "employee/add";
    private static final String PATH_EMPLOYEE_UPDATE = "employee/update";
    private static final String PATH_EMPLOYEE_LIST = "employee/list";
    private static final String PATH_EMPLOYEE_REPORT = "employee/report";

    @Autowired
    private IEmployeeService employeeService;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ModelAttribute("employee")
    public Employee createEmployee() {
        return new Employee();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEmployee(ModelMap model,
                              Locale locale,
                              @Valid @ModelAttribute Employee employee,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (employee != null) {
                    employeeService.add(employee);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.employee.add", null, locale));
                    return REDIRECT_PATH_EMPLOYEE_MAIN;
                }
            } else {
                model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_EMPLOYEE_ADD;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteEmployee(RedirectAttributes redirectAttributes,
                                 Locale locale,
                                 @PathVariable("id") Long id,
                                 HttpServletRequest request) {
        try {
            employeeService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.employee.delete", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return REDIRECT_PATH_EMPLOYEE_MAIN;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
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
        return PATH_EMPLOYEE_REPORT;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String getAllEmployees(ModelMap model) {
        try {
            model.addAttribute(Attribute.EMPLOYEES, employeeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_EMPLOYEE_LIST;
    }

    @RequestMapping(value = "/changeStatus/{id}", method = RequestMethod.POST)
    public String changeEmployeeStatus(@PathVariable("id") Long id,
                                       @RequestParam("status") String status,
                                       Locale locale,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        try {
            employeeService.setStatus(id, EmployeeStatus.valueOf(status));
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.employee.set.status", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return REDIRECT_PATH_EMPLOYEE_MAIN;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateEmployee(ModelMap model,
                                 @Valid @ModelAttribute Employee employee,
                                 BindingResult bindingResult,
                                 Locale locale,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (employee != null) {
                    employeeService.update(employee);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.employee.update", null, locale));
                    return REDIRECT_PATH_EMPLOYEE_MAIN;
                }
            } else {
                model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_EMPLOYEE_UPDATE;
    }

    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String showAddEmployeePage(ModelMap model) {
        model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
        return PATH_EMPLOYEE_ADD;
    }

    @RequestMapping(value = "/updatePage/{id}", method = RequestMethod.GET)
    public String showUpdateEmployeePage(ModelMap model,
                                         @PathVariable("id") Long id,
                                         HttpServletRequest request) {
        try {
            model.addAttribute(Attribute.EMPLOYEE, employeeService.getById(id));
            model.addAttribute(Attribute.POSITIONS, Arrays.asList(Position.values()));
        } catch (ServiceException e) {
            ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_EMPLOYEE_UPDATE;
    }
}
