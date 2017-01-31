
package weather.common.data.diary;

import java.io.Serializable;

/**
 * This class represents an enumeration type that defines the trend types for
 * relative humidity.
 *
 * @author Alinson Antony (2012)
 */

public enum RelativeHumidityTrendType implements Serializable{
   
    DIURINAL ("Typical Diurinal Pattern"),
    NOT_DIURINAL ("Non-Typical Diurinal Pattern");
    

    private static final long serialVersionUID = 1L;
    
    private String stringValue;

    private RelativeHumidityTrendType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation of for displaying on screen.
     *
     * NOTE: to get a string for updating the database, use the toString method.
     *
     * @return A String representation.
     */
    public String displayString () {
        return (stringValue);
    }

    /**
     * Return the Relative humidity TrendType with the corresponding string value.
     *
     * @param value A string value of the  enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static RelativeHumidityTrendType getEnum (String value) {
        RelativeHumidityTrendType type = null;

        for (RelativeHumidityTrendType t : RelativeHumidityTrendType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
    
}
