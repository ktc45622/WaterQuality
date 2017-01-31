package weather.common.data.diary;
import java.io.Serializable;

/**
 * This class represents an enumeration type that defines the trend types for
 * temperature.
 *
 * @author Alinson Antony (2012)
 */
public enum TemperatureTrendType implements Serializable{
        DIURINAL ("Typical Diurinal Pattern"),
        NOT_DIURINAL ("Non-Typical Diurinal Pattern");
   

    private static final long serialVersionUID = 1L;
    
    private String stringValue;

    private TemperatureTrendType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation of for displaying on screen.
     *
     * NOTE to get a string for updating the database, use the toString method.
     *
     * @return A String representation.
     */
    public String displayString () {
        return (stringValue);
    }

    /**
     * Return the TemperatureTrend Type with the corresponding string value.
     * @param value A string value of the enumeration.
     * @return An enumeration with that string value.
     */
    public static TemperatureTrendType getEnum (String value) {
        TemperatureTrendType type = null;

        for (TemperatureTrendType t : TemperatureTrendType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
    
}
