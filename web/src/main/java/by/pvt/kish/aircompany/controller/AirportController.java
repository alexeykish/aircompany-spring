package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.services.IService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

/**
 * @author Kish Alexey
 */
@Controller
@RequestMapping(value = "/airport")
public class AirportController {

    private static String className = PlaneController.class.getName();
    private static Logger logger = Logger.getLogger(AirportController.class.getName());

    private static final String REDIRECT_PATH_AIRPORT_MAIN = "redirect:/airport/main";
    private static final String PATH_AIRPORT_ADD = "airport/add";
    private static final String PATH_AIRPORT_UPDATE = "airport/update";
    private static final String PATH_AIRPORT_LIST = "airport/list";
    private static final String PATH_AIRPORT_REPORT = "airport/report";

    @Autowired
    private IService<Airport> airportService;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ModelAttribute("airport")
    public Airport createAirport() {
        return new Airport();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addPlane(@Valid @ModelAttribute Airport airport,
                           BindingResult bindingResult,
                           Locale locale,
                           RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (airport != null) {
                    airportService.add(airport);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.airport.add", null, locale));
                    return REDIRECT_PATH_AIRPORT_MAIN;
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_AIRPORT_ADD;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deletePlane(RedirectAttributes redirectAttributes,
                              Locale locale,
                              @PathVariable("id") Long id,
                              HttpServletRequest request) {
        try {
            airportService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.airport.delete", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return REDIRECT_PATH_AIRPORT_MAIN;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String createAirportReport(ModelMap model,
                                      @PathVariable("id") Long id,
                                      HttpServletRequest request) {
        try {
            model.addAttribute(Attribute.AIRPORT, airportService.getById(id));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_AIRPORT_REPORT;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String getAllPlanes(ModelMap model) {
        try {
            model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_AIRPORT_LIST;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updatePlane(@Valid @ModelAttribute Airport airport,
                              BindingResult bindingResult,
                              Locale locale,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (airport != null) {
                    airportService.update(airport);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.airport.update", null, locale));
                    return REDIRECT_PATH_AIRPORT_MAIN;
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_AIRPORT_UPDATE;
    }

    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String showAddAirportPage() {
        return PATH_AIRPORT_ADD;
    }

    @RequestMapping(value = "/updatePage/{id}", method = RequestMethod.GET)
    public String showUpdateAirportPage(ModelMap model,
                                        @PathVariable("id") Long id,
                                        HttpServletRequest request) {
        try {
            model.addAttribute(Attribute.AIRPORT, airportService.getById(id));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_AIRPORT_UPDATE;
    }
}
