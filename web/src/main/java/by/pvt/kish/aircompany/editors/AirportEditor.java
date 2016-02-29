package by.pvt.kish.aircompany.editors;

import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.pojos.Airport;
import by.pvt.kish.aircompany.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * @author Kish Alexey
 */
@Component
public class AirportEditor extends PropertyEditorSupport {

    @Autowired
    private IService<Airport> airportService;
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
            Airport airport = airportService.getById(Long.parseLong(text));
            setValue(airport);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }
}
