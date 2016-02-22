package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.enums.FlightStatus;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.services.impl.AirportService;
import by.pvt.kish.aircompany.services.impl.FlightService;
import by.pvt.kish.aircompany.services.impl.PlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.RequestHandler;
import by.pvt.kish.aircompany.validators.FlightStatusValidator;
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
public class FlightController {

    private static String className = FlightController.class.getName();

    @RequestMapping(value = "/addFlight")
    public String addFlight(Model model,
                            @ModelAttribute Flight flight,
                            HttpServletRequest request) {
        try {
            FlightService.getInstance().add(flight);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_FLIGHT);
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/deleteFlight")
    public String deleteFlight(Model model,
                               @RequestParam("fid") Long id) {
        try {
            FlightService.getInstance().delete(id);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_FLIGHT);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/flightReport")
    public String createFlightReport(Model model,
                                    @RequestParam("fid") Long id) {
        try {
            Flight flight = FlightService.getInstance().getById(id);
            List<FlightStatus> flightStatuses = Arrays.asList(FlightStatus.values());
            model.addAttribute(Attribute.FLIGHT_ATTRIBUTE, flight);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, flightStatuses);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/report";
    }

    @RequestMapping(value = "/flightList")
    public String getAllFlights(Model model,
                                @RequestParam("page") Integer page) {
        int recordsPerPage = 5;
        int currentPage = 1;
        if(page != null) {
            currentPage = page;
        }
        try {
            int noOfRecords = FlightService.getInstance().getCount();
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
            FlightStatusValidator.updateFlightsStatus();
            List<Flight> flights = FlightService.getInstance().getAllToPage(recordsPerPage, currentPage);
            model.addAttribute(Attribute.FLIGHTS_ATTRIBUTE, flights);
            model.addAttribute("noOfPages", noOfPages);
            model.addAttribute("currentPage", currentPage);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/list";
    }

    @RequestMapping(value = "/updateFlight")
    public String updateFlight(Model model,
                               @ModelAttribute Flight flight,
                               HttpServletRequest request) {
        try {
            FlightService.getInstance().update(flight);
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

    @RequestMapping(value = "/saveCrewToFlight")
    public String saveCrewToFlight(Model model,
                                   @RequestParam("fid") Long id,
                                   @RequestParam("num") Integer num,
                                   HttpServletRequest request) {
        try {
            List<Long> team = RequestHandler.getTeam(request, num);
            FlightService.getInstance().addTeam(id, team);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_TEAM_CHANGE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/addFlightPage")
    public String showAddFlightPage(Model model) {
        try {
            List<Plane> planes = PlaneService.getInstance().getAll();
            List<Airport> airports = AirportService.getInstance().getAll();
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
            Flight flight = FlightService.getInstance().getById(id);
            List<FlightStatus> statuses = Arrays.asList(FlightStatus.values());
            List<Plane> planes = PlaneService.getInstance().getAll();
            List<Airport> airports = AirportService.getInstance().getAll();
            model.addAttribute(Attribute.FLIGHT_ATTRIBUTE, flight);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, statuses);
            model.addAttribute(Attribute.AIRPORTS_ATTRIBUTE, airports);
            model.addAttribute(Attribute.PLANES_ATTRIBUTE, planes);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/update";
    }
}
