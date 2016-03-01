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
import by.pvt.kish.aircompany.validators.FlightStatusValidator;
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

    @Autowired
    private IFlightService flightService;

    @Autowired
    private IPlaneService planeService;

    @Autowired
    private IService<Airport> airportService;

    @Autowired
    private FlightStatusValidator flightStatusValidator;

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

    @RequestMapping(value = "/add")
    public String addFlight(ModelMap model,
                            @Valid @ModelAttribute("flight") Flight flight,
                            BindingResult bindingResult,
                            Locale locale,
                            RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (flight != null) {
                    flightService.add(flight);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("SUCCESS_ADD_FLIGHT", null, locale));
                    return "redirect:/flight/main?page=1";
                }
            } else {
                model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
                model.addAttribute(Attribute.PLANES, planeService.getAll());
            }
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage("ERROR_IAE", className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/add";
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteFlight(RedirectAttributes redirectAttributes,
                               Locale locale,
                               @PathVariable("id") Long id,
                               HttpServletRequest request) {
        try {
            flightService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("SUCCESS_DELETE_FLIGHT", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "redirect:/flight/main?page=1";
    }

    @RequestMapping(value = "/{id}")
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
        return "flight/report";
    }

    @RequestMapping(value = "/main")
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
            flightStatusValidator.updateFlightsStatus();
            model.addAttribute(Attribute.FLIGHTS, flightService.getAllToPage(recordsPerPage, currentPage));
            model.addAttribute("noOfPages", noOfPages);
            model.addAttribute("currentPage", currentPage);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "flight/list";
    }

    @RequestMapping(value = "/update")
    public String updateFlight(ModelMap model,
                               RedirectAttributes redirectAttributes,
                               @Valid @ModelAttribute Flight flight,
                               BindingResult bindingResult,
                               Locale locale) {
        try {
            if (!bindingResult.hasErrors()) {
                if (flight != null) {
                    flightService.update(flight);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("SUCCESS_UPDATE_FLIGHT", null, locale));
                    return "redirect:/flight/main?page=1";
                }
            } else {
                model.addAttribute(Attribute.STATUSES, Arrays.asList(FlightStatus.values()));
                model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
                model.addAttribute(Attribute.PLANES, planeService.getAll());
            }
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage("ERROR_IAE", className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/update";
    }

    @RequestMapping(value = "/addPage")
    public String showAddFlightPage(Model model) {
        try {
            model.addAttribute(Attribute.AIRPORTS, airportService.getAll());
            model.addAttribute(Attribute.PLANES, planeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "flight/add";
    }

    @RequestMapping(value = "/updatePage/{id}")
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
        return "flight/update";
    }

}
