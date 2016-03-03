package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.IEmployeeService;
import by.pvt.kish.aircompany.services.IFlightService;
import by.pvt.kish.aircompany.services.IPlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.TeamCreator;
import by.pvt.kish.aircompany.validators.TeamValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Kish Alexey
 */
@Controller
@RequestMapping(value = "/crew")
public class CrewController {

    private static String className = CrewController.class.getName();
    private static Logger logger = Logger.getLogger(CrewController.class.getName());

    private static final String REDIRECT_PATH_FLIGHT_MAIN = "redirect:/flight/main?page=1";
    private static final String PATH_CREW_ADD = "crew/add";
    private static final String PATH_CREW_UPDATE = "crew/update";

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IPlaneService planeService;

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private TeamValidator teamValidator;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/addPage/{id}", method = RequestMethod.GET)
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
                return PATH_CREW_UPDATE;
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_CREW_ADD;
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.POST)
    public String saveCrewToFlight(@PathVariable("id") Long id,
                                   @RequestParam("num") Integer num,
                                   Locale locale,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        try {
            Flight flight = flightService.getById(id);
            List<Long> crew = getCrew(request, num);
            if (flight.getCrew().isEmpty()) {
                teamValidator.validate(id, crew, false);
            } else {
                teamValidator.validate(id, crew, true);
            }
            flightService.addCrew(id, getCrew(request, num));
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.crew.change", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(),className);
        }
        return REDIRECT_PATH_FLIGHT_MAIN;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteCrew(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes,
                             Locale locale,
                             HttpServletRequest request) {
        try {
            Flight flight = flightService.getById(id);
            if (flight.getStatus() == FlightStatus.READY) {
                flightService.deleteCrew(id);
            }
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.crew.delete", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(),className);
        }
        return REDIRECT_PATH_FLIGHT_MAIN;
    }


    private static List<Long> getCrew(HttpServletRequest request, int count) {
        List<Long> team = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            team.add(Long.parseLong(request.getParameter(String.valueOf(i))));
        }
        return team;
    }
}
