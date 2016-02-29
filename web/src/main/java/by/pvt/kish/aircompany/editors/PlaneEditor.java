package by.pvt.kish.aircompany.editors;

import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.pojos.Plane;
import by.pvt.kish.aircompany.services.IPlaneService;
import by.pvt.kish.aircompany.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * @author Kish Alexey
 */
@Component
public class PlaneEditor extends PropertyEditorSupport {

    @Autowired
    private IPlaneService planeService;
    /**
     * Set the property value by parsing a given String.  May raise
     * java.lang.IllegalArgumentException if either the String is
     * badly formatted or if this kind of property can't be expressed
     * as text.
     *
     * @param text The string to be parsed.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            Plane plane = planeService.getById(Long.parseLong(text));
            setValue(plane);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }
}
