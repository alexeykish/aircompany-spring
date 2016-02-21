package by.pvt.kish.aircompany.command.plane;

import by.pvt.kish.aircompany.command.ActionCommand;
import by.pvt.kish.aircompany.constants.Attribute;
import by.pvt.kish.aircompany.constants.Message;
import by.pvt.kish.aircompany.constants.Page;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceValidateException;
import by.pvt.kish.aircompany.services.impl.PlaneService;
import by.pvt.kish.aircompany.utils.ErrorHandler;
import by.pvt.kish.aircompany.utils.RequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Kish Alexey
 */
public class AddPlaneCommand implements ActionCommand {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String className = AddPlaneCommand.class.getName();
        try {
            Plane plane = RequestHandler.getPlane(request);
            PlaneService.getInstance().add(plane);
            request.setAttribute(Attribute.MESSAGE_ATTRIBUTE, Message.SUCCESS_ADD_PLANE);
            return Page.MAIN;
        } catch (IllegalArgumentException e) {
            return ErrorHandler.returnErrorPage(Message.ERROR_IAE, className);
        } catch (ServiceException e) {
            return ErrorHandler.returnErrorPage(e.getMessage(), className);
        } catch (ServiceValidateException e) {
            return ErrorHandler.returnValidateErrorPage(request, e.getMessage(), className);
        }
    }
}
