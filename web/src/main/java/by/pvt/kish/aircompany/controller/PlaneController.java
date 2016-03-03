package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.enums.PlaneStatus;
import by.pvt.kish.aircompany.enums.Position;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.pojos.Flight;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.pojos.PlaneCrew;
import by.pvt.kish.aircompany.services.IPlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author Kish Alexey
 */
@Controller
@RequestMapping(value = "/plane")
public class PlaneController {

    private static String className = PlaneController.class.getName();
    private static Logger logger = Logger.getLogger(PlaneController.class.getName());

    private static final String REDIRECT_PATH_PLANE_MAIN = "redirect:/plane/main";
    private static final String PATH_PLANE_ADD = "plane/add";
    private static final String PATH_PLANE_UPDATE = "plane/update";
    private static final String PATH_PLANE_LIST = "plane/list";
    private static final String PATH_PLANE_REPORT = "plane/report";

    @Autowired
    private IPlaneService planeService;

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ModelAttribute("plane")
    public Plane createPlane() {
        return new Plane();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addPlane(@Valid @ModelAttribute("plane") Plane plane,
                           BindingResult bindingResult,
                           Locale locale,
                           RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (plane != null) {
                    PlaneCrew planeCrew = plane.getPlaneCrew();
                    planeCrew.setPlane(plane);
                    planeService.add(plane);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.plane.add", null, locale));
                    return REDIRECT_PATH_PLANE_MAIN;
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_PLANE_ADD;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deletePlane(RedirectAttributes redirectAttributes,
                              Locale locale,
                              @PathVariable("id") Long id,
                              HttpServletRequest request) {
        try {
            planeService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.plane.delete", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return REDIRECT_PATH_PLANE_MAIN;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String getAllPlanes(ModelMap model) {
        try {
            model.addAttribute(Attribute.PLANES, planeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_PLANE_LIST;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String createPlaneReport(ModelMap model,
                                    @PathVariable("id") Long id,
                                    HttpServletRequest request) {
        try {
            Plane plane = planeService.getById(id);
            List<Flight> flights = planeService.getPlaneLastFiveFlights(plane.getPid());
            model.addAttribute(Attribute.PLANE, plane);
            model.addAttribute(Attribute.TEAM, getCrew(plane));
            model.addAttribute(Attribute.FLIGHTS, flights);
            model.addAttribute(Attribute.STATUSES, Arrays.asList(PlaneStatus.values()));
            model.addAttribute(Attribute.PERMISSION_CHANGE_DELETE_STATUS, flights.size() != 0);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_PLANE_REPORT;
    }

    @RequestMapping(value = "/changeStatus/{id}", method = RequestMethod.POST)
    public String changePlaneStatus(RedirectAttributes redirectAttributes,
                                    @PathVariable("id") Long id,
                                    @RequestParam("status") String status,
                                    Locale locale,
                                    HttpServletRequest request) {
        try {
            planeService.setStatus(id, PlaneStatus.valueOf(status));
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.plane.set.status", null, locale));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return REDIRECT_PATH_PLANE_MAIN;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updatePlane(@Valid @ModelAttribute("plane") Plane plane,
                              BindingResult bindingResult,
                              Locale locale,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!bindingResult.hasErrors()) {
                if (plane != null) {
                    planeService.update(plane);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, messageSource.getMessage("message.success.plane.update", null, locale));
                    return REDIRECT_PATH_PLANE_MAIN;
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return PATH_PLANE_UPDATE;
    }

    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String showAddPlanePage() {
        return PATH_PLANE_ADD;
    }

    @RequestMapping(value = "/updatePage/{id}", method = RequestMethod.GET)
    public String showUpdatePlanePage(ModelMap model,
                                      @PathVariable("id") Long id,
                                      HttpServletRequest request) {
        try {
            model.addAttribute(Attribute.PLANE, planeService.getById(id));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return PATH_PLANE_UPDATE;
    }

    private Map<String, Integer> getCrew(Plane plane) {
        Map<String, Integer> team = new HashMap<>();
        team.put(Position.PILOT.toString(), plane.getPlaneCrew().getNumberOfPilots());
        team.put(Position.NAVIGATOR.toString(), plane.getPlaneCrew().getNumberOfNavigators());
        team.put(Position.RADIOOPERATOR.toString(), plane.getPlaneCrew().getNumberOfRadiooperators());
        team.put(Position.STEWARDESS.toString(), plane.getPlaneCrew().getNumberOfStewardesses());
        return team;
    }

}
