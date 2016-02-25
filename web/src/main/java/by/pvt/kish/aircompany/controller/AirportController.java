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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
                           @Valid @ModelAttribute("airport") Airport airport,
                           BindingResult bindingResult,
                           HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (airport != null) {
                    airportService.add(airport);
                    model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_AIRPORT);
                    return "main";
                }
            }
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "airport/add";
    }

    @RequestMapping(value = "/deleteAirport/{id}")
    public String deletePlane(ModelMap model,
                              @PathVariable("id") Long id) {
        try {
            airportService.delete(id);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_AIRPORT);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/airportReport/{id}")
    public String airportReport(ModelMap model, @PathVariable("id") Long id) {
        try {
            Airport airport = airportService.getById(id);
            model.addAttribute(Attribute.AIRPORT_ATTRIBUTE, airport);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
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
                              @Valid @ModelAttribute("airport") Airport airport,
                              BindingResult bindingResult,
                              HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (airport != null) {
                    airportService.update(airport);
                    model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_AIRPORT);
                    return "main";
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "airport/update";
    }

    @RequestMapping(value = "/addAirportPage")
    public String showAddAirportPage() {
        return "airport/add";
    }

    @RequestMapping(value = "/updateAirportPage/{id}")
    public String showUpdateAirportPage(ModelMap model,
                                        @PathVariable("id") Long id) {
        try {
            Airport airport = airportService.getById(id);
            model.addAttribute(Attribute.AIRPORT_ATTRIBUTE, airport);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "airport/update";
    }
}
