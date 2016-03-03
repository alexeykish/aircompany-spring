package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.editors.AirportEditor;
import by.pvt.kish.aircompany.editors.PlaneEditor;
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
import by.pvt.kish.aircompany.handlers.FlightStatusHandler;
import by.pvt.kish.aircompany.validators.FlightValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * @author Kish Alexey
 */
@Controller
@RequestMapping(value = "/flight")
public class FlightController {

    private static String className = FlightController.class.getName();
    private static Logger logger = Logger.getLogger(FlightController.class.getName());

    private static final String REDIRECT_PATH_FLIGHT_MAIN = "redirect:/flight/main?page=1";
    private static final String PATH_FLIGHT_ADD = "flight/add";
    private static final String PATH_FLIGHT_UPDATE = "flight/update";
    private static final String PATH_FLIGHT_LIST = "flight/list";
    private static final String PATH_FLIGHT_REPORT = "flight/report";

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IPlaneService planeService;

    @Autowired
    private IService<Airport> airportService;

    @Autowired
    private FlightStatusHandler flightStatusHandler;

    @Autowired
    private FlightValidator flightValidator;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @ModelAttribute("flight")
    public Flight createFlight() {
        return new Flight();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(Airport.class, new AirportEditor()  {
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    Airport airport = airportService.getById(Long.parseLong(text));
                    setValue(airport);
                } catch (ServiceException | ServiceValidateException e) {
                    e.printStackTrace(); //TODO Exception handling
                }
            }
        });
        binder.registerCustomEditor(Plane.class, new PlaneEditor() {
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    Plane plane = planeService.getById(Long.parseLong(text));
                    setValue(plane);
                } catch (ServiceException | ServiceValidateException e) {
                    e.printStackTrace(); //TODO Exception handling
                }
            }
        });
        binder.setValidator(flightValidator);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addFlight(ModelMap model,
                            @Valid @ModelAttribute Flight flight,
                            BindingResult bindingResult,
                            Locale locale,
                            RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (flight != null) {
                    flightService.add(flight);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.flight.add", null, locale));
                    return REDIRECT_PATH_FLIGHT_MAIN;
                }
            } else {
                model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
                model.addAttribute(Attribute.PLANES, planeService.getAll());
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_FLIGHT_ADD;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteFlight(RedirectAttributes redirectAttributes,
                               Locale locale,
                               @PathVariable("id") Long id,
                               HttpServletRequest request) {
        try {
            flightService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.flight.delete", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return REDIRECT_PATH_FLIGHT_MAIN;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String createFlightReport(ModelMap model,
                                     @PathVariable("id") Long id,
                                     HttpServletRequest request) {
        try {
            model.addAttribute(Attribute.FLIGHT, flightService.getById(id));
            model.addAttribute(Attribute.STATUSES, Arrays.asList(FlightStatus.values()));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_FLIGHT_REPORT;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String getAllFlights(ModelMap model,
                                @RequestParam("page") Integer page,
                                HttpServletRequest request) {
        int recordsPerPage = 5;
        int currentPage = 1;
        if (page != null) {
            currentPage = page;
        }
        try {
            int noOfRecords = flightService.getCount();
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
            flightStatusHandler.updateFlightsStatus();
            model.addAttribute(Attribute.FLIGHTS, flightService.getAllToPage(recordsPerPage, currentPage));
            model.addAttribute("noOfPages", noOfPages);
            model.addAttribute("currentPage", currentPage);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_FLIGHT_LIST;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateFlight(ModelMap model,
                               RedirectAttributes redirectAttributes,
                               @Valid @ModelAttribute Flight flight,
                               BindingResult bindingResult,
                               Locale locale) {
        try {
            if (!bindingResult.hasErrors()) {
                if (flight != null) {
                    flightService.update(flight);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.flight.update", null, locale));
                    return REDIRECT_PATH_FLIGHT_MAIN;
                }
            } else {
                model.addAttribute(Attribute.STATUSES, Arrays.asList(FlightStatus.values()));
                model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
                model.addAttribute(Attribute.PLANES, planeService.getAll());
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_FLIGHT_UPDATE;
    }

    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String showAddFlightPage(Model model) {
        try {
            model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
            model.addAttribute(Attribute.PLANES, planeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_FLIGHT_ADD;
    }

    @RequestMapping(value = "/updatePage/{id}", method = RequestMethod.GET)
    public String showUpdateFlightPage(Model model,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request) {
        try {
            model.addAttribute(Attribute.FLIGHT, flightService.getById(id));
            model.addAttribute(Attribute.STATUSES, Arrays.asList(FlightStatus.values()));
            model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
            model.addAttribute(Attribute.PLANES, planeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_FLIGHT_UPDATE;
    }

}
