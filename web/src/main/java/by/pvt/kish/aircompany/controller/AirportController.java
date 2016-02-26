package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.services.IService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (airport != null) {
                    airportService.add(airport);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_ADD_AIRPORT);
                    return "redirect:/airportList";
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
    public String deletePlane(RedirectAttributes redirectAttributes,
                              @PathVariable("id") Long id) {
        try {
            airportService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_DELETE_AIRPORT);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "redirect:/airportList";
    }

    @RequestMapping(value = "/airportReport/{id}")
    public String createAirportReport(ModelMap model,
                                      @PathVariable("id") Long id) {
        try {
            model.addAttribute(Attribute.AIRPORT, airportService.getById(id));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "airport/report";
    }

    @RequestMapping(value = "/airportList")
    public String getAllPlanes(ModelMap model) {
        try {
            model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "airport/list";
    }

    @RequestMapping(value = "/updateAirport")
    public String updatePlane(Model model,
                              @Valid @ModelAttribute("airport") Airport airport,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (airport != null) {
                    airportService.update(airport);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_UPDATE_AIRPORT);
                    return "redirect:/airportList";
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
            model.addAttribute(Attribute.AIRPORT, airportService.getById(id));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "airport/update";
    }
}
