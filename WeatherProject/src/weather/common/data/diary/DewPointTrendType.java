
package weather.common.data.diary;
import java.io.Serializable;

/**
 * This class represents An enumeration type that defines the trend types for
 * Dew Point.
 *
 * @author Alinson Antony(2012)
 */
public enum DewPointTrendType implements Serializable{
    STEADY ("Steady"),
    RISING ("Rising"),
    FALLING ("Falling"),
    RISE_THEN_FALL ("Rising then falling"),
    FALL_THEN_RISE ("Falling then rising");

    private static final long serialVersionUID = 1L;
    
    private String stringValue;

    private DewPointTrendType(String str) {
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
     * Return the Dew Point TrendType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static DewPointTrendType getEnum (String value) {
        DewPointTrendType type = null;

        for (DewPointTrendType t : DewPointTrendType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
    
}
