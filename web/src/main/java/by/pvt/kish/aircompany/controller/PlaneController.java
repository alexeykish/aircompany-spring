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
import by.pvt.kish.aircompany.services.impl.PlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
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
                           HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (plane != null) {
                    PlaneCrew planeCrew = plane.getPlaneCrew();
                    planeCrew.setPlane(plane);
                    planeService.add(plane);
                    model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_PLANE);
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
        return "plane/add";
    }

    @RequestMapping(value = "/deletePlane/{id}")
    public String deletePlane(ModelMap model,
                              @PathVariable("id") Long id) {
        try {
            planeService.delete(id);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/planeList")
    public String getAllPlanes(ModelMap model) {
        try {
            List<Plane> planes = planeService.getAll();
            model.addAttribute(Attribute.PLANES_ATTRIBUTE, planes);
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
            Map<String, Integer> team = new HashMap<>();
            team.put(Position.PILOT.toString(), plane.getPlaneCrew().getNumberOfPilots());
            team.put(Position.NAVIGATOR.toString(), plane.getPlaneCrew().getNumberOfNavigators());
            team.put(Position.RADIOOPERATOR.toString(), plane.getPlaneCrew().getNumberOfRadiooperators());
            team.put(Position.STEWARDESS.toString(), plane.getPlaneCrew().getNumberOfStewardesses());
            List<Flight> flights = planeService.getPlaneLastFiveFlights(plane.getPid());
            boolean permissionChangeDeleteStatus = flights.size() != 0;
            List<PlaneStatus> planeStatuses = Arrays.asList(PlaneStatus.values());
            model.addAttribute(Attribute.PLANE_ATTRIBUTE, plane);
            model.addAttribute(Attribute.TEAM_ATTRIBUTE, team);
            model.addAttribute(Attribute.FLIGHTS_ATTRIBUTE, flights);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, planeStatuses);
            model.addAttribute(Attribute.PERMISSION_CHANGE_DELETE_STATUS_ATTRIBUTE, permissionChangeDeleteStatus);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "plane/report";
    }

    @RequestMapping(value = "/changePlaneStatus/{id}")
    public String changePlaneStatus(ModelMap model,
                                    @PathVariable("id") Long id,
                                    @RequestParam("status") String status,
                                    HttpServletRequest request) {
        try {
            planeService.setStatus(id, PlaneStatus.valueOf(status));
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_SET_STATUS_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/updatePlane")
    public String updatePlane(ModelMap model,
                              @Valid @ModelAttribute("plane") Plane plane,
                              BindingResult bindingResult,
                              HttpServletRequest request) {
        try {
            if (!bindingResult.hasErrors()) {
                if (plane != null) {
                    planeService.update(plane);
                    model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_PLANE);
                    return "main";
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
            Plane plane = planeService.getById(id);
            model.addAttribute(Attribute.PLANE_ATTRIBUTE, plane);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "plane/update";
    }
}
