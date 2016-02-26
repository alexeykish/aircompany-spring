package by.pvt.kish.aircompany.controller;

import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kish Alexey
 */
@Controller
public class PlaneController {

    private static String className = PlaneController.class.getName();
    private static Logger logger = Logger.getLogger(PlaneController.class.getName());

    @Autowired
    private IPlaneService planeService;

    @ModelAttribute("plane")
    public Plane createPlane() {
        return new Plane();
    }

    @RequestMapping(value = "/addPlane")
    public String addPlane(ModelMap model,
                           @Valid @ModelAttribute("plane") Plane plane,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (plane != null) {
                    PlaneCrew planeCrew = plane.getPlaneCrew();
                    planeCrew.setPlane(plane);
                    planeService.add(plane);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_ADD_PLANE);
                    return "redirect:/planeList";
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "plane/add";
    }

    @RequestMapping(value = "/deletePlane/{id}")
    public String deletePlane(RedirectAttributes redirectAttributes,
                              @PathVariable("id") Long id) {
        try {
            planeService.delete(id);
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_DELETE_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "redirect:/planeList";
    }

    @RequestMapping(value = "/planeList")
    public String getAllPlanes(ModelMap model) {
        try {
            model.addAttribute(Attribute.PLANES, planeService.getAll());
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "plane/list";
    }

    @RequestMapping(value = "/planeReport/{id}")
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
        return "plane/report";
    }

    @RequestMapping(value = "/changePlaneStatus/{id}")
    public String changePlaneStatus(RedirectAttributes redirectAttributes,
                                    @PathVariable("id") Long id,
                                    @RequestParam("status") String status,
                                    HttpServletRequest request) {
        try {
            planeService.setStatus(id, PlaneStatus.valueOf(status));
            redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_SET_STATUS_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "redirect:/planeList";
    }

    @RequestMapping(value = "/updatePlane")
    public String updatePlane(@Valid @ModelAttribute("plane") Plane plane,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (plane != null) {
                    planeService.update(plane);
                    redirectAttributes.addFlashAttribute(Attribute.MESSAGE, Message.SUCCESS_UPDATE_PLANE);
                    return "redirect:/planeList";
                }
            }
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "plane/update";
    }

    @RequestMapping(value = "/addPlanePage")
    public String showAddPlanePage() {
        return "plane/add";
    }

    @RequestMapping(value = "/updatePlanePage/{id}")
    public String showUpdatePlanePage(ModelMap model,
                                      @PathVariable("id") Long id) {
        try {
            model.addAttribute(Attribute.PLANE, planeService.getById(id));
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "plane/update";
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
