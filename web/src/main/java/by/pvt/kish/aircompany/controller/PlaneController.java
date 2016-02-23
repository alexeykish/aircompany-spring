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
import by.pvt.kish.aircompany.services.impl.PlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
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

    @ModelAttribute("plane")
    public Plane createPlane() {
        logger.info("Model attribute plane is created");
        return new Plane();
    }

    @RequestMapping(value = "/addPlane")
    public String addPlane(Model model,
                           @ModelAttribute("plane") Plane plane,
                           HttpServletRequest request) {
        try {
            PlaneCrew planeCrew = plane.getPlaneCrew();
            planeCrew.setPlane(plane);
            PlaneService.getInstance().add(plane);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_PLANE);
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/deletePlane/{id}")
    public String deletePlane(Model model,
                              @PathVariable("id") Long id) {
        try {
            PlaneService.getInstance().delete(id);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_DELETE_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/planeList")
    public String getAllPlanes(Model model) {
        try {
            List<Plane> planes = PlaneService.getInstance().getAll();
            model.addAttribute(Attribute.PLANES_ATTRIBUTE, planes);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "plane/list";
    }

    @RequestMapping(value = "/planeReport")
    public String createPlaneReport(Model model,
                                    @RequestParam("pid") Long id) {
        try {
            Plane plane = PlaneService.getInstance().getById(id);
            Map<String, Integer> team = new HashMap<>();
            team.put(Position.PILOT.toString(), plane.getPlaneCrew().getNumberOfPilots());
            team.put(Position.NAVIGATOR.toString(), plane.getPlaneCrew().getNumberOfNavigators());
            team.put(Position.RADIOOPERATOR.toString(), plane.getPlaneCrew().getNumberOfRadiooperators());
            team.put(Position.STEWARDESS.toString(), plane.getPlaneCrew().getNumberOfStewardesses());
            List<Flight> flights = PlaneService.getInstance().getPlaneLastFiveFlights(plane.getPid());
            boolean permissionChangeDeleteStatus = flights.size() != 0;
            List<PlaneStatus> planeStatuses = Arrays.asList(PlaneStatus.values());
            model.addAttribute(Attribute.PLANE_ATTRIBUTE, plane);
            model.addAttribute(Attribute.TEAM_ATTRIBUTE, team);
            model.addAttribute(Attribute.FLIGHTS_ATTRIBUTE, flights);
            model.addAttribute(Attribute.STATUSES_ATTRIBUTE, planeStatuses);
            model.addAttribute(Attribute.PERMISSION_CHANGE_DELETE_STATUS_ATTRIBUTE, permissionChangeDeleteStatus);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "plane/report";
    }

    @RequestMapping(value = "/changePlaneStatus/{id}")
    public String changePlaneStatus(Model model,
                                    @PathVariable("id") Long id,
                                    @RequestParam("status") String status) {
        try {
            PlaneService.getInstance().setStatus(id, PlaneStatus.valueOf(status));
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_SET_STATUS_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/updatePlane")
    public String updatePlane(Model model,
                              @ModelAttribute("plane") Plane plane,
                              HttpServletRequest request) {
        try {
            PlaneService.getInstance().update(plane);
            model.addAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_UPDATE_PLANE);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
        return "main";
    }

    @RequestMapping(value = "/addPlanePage")
    public String showAddPlanePage() {
        return "plane/add";
    }

    @RequestMapping(value = "/updatePlanePage/{id}")
    public String showUpdatePlanePage(Model model,
                                      @PathVariable("id") Long id) {
        try {
            Plane plane = PlaneService.getInstance().getById(id);
            model.addAttribute(Attribute.PLANE_ATTRIBUTE, plane);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        }
        return "plane/update";
    }
}
