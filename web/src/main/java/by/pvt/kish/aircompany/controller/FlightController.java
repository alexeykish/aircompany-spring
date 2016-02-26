package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.services.IFlightService;
import by.pvt.kish.aircompany.services.IPlaneService;
import by.pvt.kish.aircompany.services.IService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.validators.FlightStatusValidator;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kish Alexey
 */
@Controller
public class FlightController {

    private static String className = FlightController.class.getName();
    private static Logger logger = Logger.getLogger(FlightController.class.getName());

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IPlaneService planeService;

    @Autowired
    private IService<Airport> airportService;

    @Autowired
    private FlightStatusValidator flightStatusValidator;

    @ModelAttribute("flight")
    public Flight createFlight() {
        return new Flight();
    }

    @RequestMapping(value = "/addFlight")
    public String addFlight(ModelMap model,
                            @Valid @ModelAttribute("flight") Flight flight,
                            BindingResult result,
                            HttpServletRequest request) {
        try {
            if(!result.hasErrors()) {
                if (flight != null) {
                    flight = new Flight();
                    model.addAttribute("flight", flight);
                    return "main";
                }
            }
            flightService.add(flight);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_FLIGHT);
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "flight/add";
    }

    @RequestMapping(value = "/deleteFlight/{id}")
    public String deleteFlight(ModelMap model,
                               @PathVariable("id") Long id) {
        try {
            flightService.delete(id);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_FLIGHT);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/flightReport/{id}")
    public String createFlightReport(ModelMap model,
                                    @PathVariable("id") Long id) {
        try {
            Flight flight = flightService.getById(id);
            List<FlightStatus> flightStatuses = Arrays.asList(FlightStatus.values());
            model.addAttribute(Attribute.FLIGHT_ATTRIBUTE, flight);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, flightStatuses);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/report";
    }

    @RequestMapping(value = "/flightList")
    public String getAllFlights(ModelMap model,
                                @RequestParam("page") Integer page,
                                HttpServletRequest request) {
        int recordsPerPage = 5;
        int currentPage = 1;
        if(page != null) {
            currentPage = page;
        }
        try {
            int noOfRecords = flightService.getCount();
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
            flightStatusValidator.updateFlightsStatus();
            List<Flight> flights = flightService.getAllToPage(recordsPerPage, currentPage);
            model.addAttribute(Attribute.FLIGHTS_ATTRIBUTE, flights);
            model.addAttribute("noOfPages", noOfPages);
            model.addAttribute("currentPage", currentPage);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(),className);
        }
        return "flight/list";
    }

    @RequestMapping(value = "/updateFlight")
    public String updateFlight(ModelMap model,
                               @ModelAttribute Flight flight,
                               HttpServletRequest request) {
        try {
            flightService.update(flight);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_FLIGHT);
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(),className);
        }
        return "main";
    }

    @RequestMapping(value = "/saveCrewToFlight/{id}")
    public String saveCrewToFlight(ModelMap model,
                                   @PathVariable("id") Long id,
                                   @RequestParam("num") Integer num,
                                   HttpServletRequest request) {
        try {
            List<Long> team = getTeam(request, num);
            flightService.addTeam(id, team);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_TEAM_CHANGE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(),className);
        }
        return "main";
    }

    @RequestMapping(value = "/addFlightPage")
    public String showAddFlightPage(Model model) {
        try {
            List<Plane> planes = planeService.getAll();
            List<Airport> airports = airportService.getAll();
            model.addAttribute(Attribute.AIRPORTS_ATTRIBUTE, airports);
            model.addAttribute(Attribute.PLANES_ATTRIBUTE, planes);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/add";
    }

    @RequestMapping(value = "/updateFlightPage")
    public String showUpdateFlightPage(Model model,
                                       @RequestParam("fid") Long id) {
        try {
            Flight flight = flightService.getById(id);
            List<FlightStatus> statuses = Arrays.asList(FlightStatus.values());
            List<Plane> planes = planeService.getAll();
            List<Airport> airports = airportService.getAll();
            model.addAttribute(Attribute.FLIGHT_ATTRIBUTE, flight);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, statuses);
            model.addAttribute(Attribute.AIRPORTS_ATTRIBUTE, airports);
            model.addAttribute(Attribute.PLANES_ATTRIBUTE, planes);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/update";
    }

    public static List<Long> getTeam(HttpServletRequest request, int count) {
        List<Long> team = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            team.add(Long.parseLong(request.getParameter(String.valueOf(i))));
        }
        return team;
    }
}
