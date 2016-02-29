package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.pojos.Employee;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.services.IEmployeeService;
import by.pvt.kish.aircompany.services.IFlightService;
import by.pvt.kish.aircompany.services.IPlaneService;
import by.pvt.kish.aircompany.services.IService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.TeamCreator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class CrewController {

    private static String className = CrewController.class.getName();
    private static Logger logger = Logger.getLogger(CrewController.class.getName());

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IPlaneService planeService;

    @Autowired
    private IEmployeeService employeeService;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
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
                return "crew/update";
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "crew/add";
    }

    @RequestMapping(value = "/saveCrewToFlight/{id}")
    public String saveCrewToFlight(@PathVariable("id") Long id,
                                   @RequestParam("num") Integer num,
                                   Locale locale,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        try {
            flightService.addTeam(id, getTeam(request, num));
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("SUCCESS_TEAM_CHANGE", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(),className);
        }
        return "redirect:/flightList?page=1";
    }


    public static List<Long> getTeam(HttpServletRequest request, int count) {
        List<Long> team = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            team.add(Long.parseLong(request.getParameter(String.valueOf(i))));
        }
        return team;
    }
}
