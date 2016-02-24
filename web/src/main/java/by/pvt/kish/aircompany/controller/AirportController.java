package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.services.IService;
import by.pvt.kish.aircompany.services.impl.AirportService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Kish Alexey
 */
@Controller
public class AirportController {

    private static String className = PlaneController.class.getName();

    @Autowired
    private IService<Airport> airportService;

    @ModelAttribute("airport")
    public Airport createAirport() {
        return new Airport();
    }

    @RequestMapping(value = "/addAirport")
    public String addPlane(ModelMap model,
                           @ModelAttribute("airport") Airport airport,
                           HttpServletRequest request) {
        try {
            airportService.add(airport);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_AIRPORT);
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/deleteAirport")
    public String deletePlane(ModelMap model, Airport airport) {
        try {
            airportService.delete(airport);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_AIRPORT);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/airportReport/{id}")
    public String airportReport(ModelMap model, @PathVariable("id") Long id) {
        return "airport/report";
    }

    @RequestMapping(value = "/airportList")
    public String getAllPlanes(ModelMap model) {
        try {
            List<Airport> airports = airportService.getAll();
            model.addAttribute(Attribute.AIRPORTS_ATTRIBUTE, airports);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "airport/list";
    }

    @RequestMapping(value = "/updateAirport")
    public String updatePlane(Model model,
                              @ModelAttribute Airport airport,
                              HttpServletRequest request) {
        try {
            airportService.update(airport);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_AIRPORT);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/addAirportPage")
    public String showAddAirportPage() {
        return "airport/add";
    }
}
