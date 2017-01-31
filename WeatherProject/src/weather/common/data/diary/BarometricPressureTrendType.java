package weather.common.data.diary;

import java.io.Serializable;

/**
 * This class represents an enumeration type that defines the trend types for
 * barometric pressure.
 *
 * @author Chris Mertens
 */
public enum BarometricPressureTrendType implements Serializable
{
    STEADY ("Steady"),
    RISING ("Rising"),
    FALLING ("Falling"),
    RISE_THEN_FALL ("Rising then falling"),
    FALL_THEN_RISE ("Falling then rising");

    private static final long serialVersionUID = 1L;
    
    private String stringValue;

    private BarometricPressureTrendType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation of for displaying on screen.
     *
     * NOTE: To get a string for updating the database, use the toString method.
     *
     * @return A String representation.
     */
    public String displayString () {
        return (stringValue);
    }

    /**
     * Return the BarometricPressureTrendType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static BarometricPressureTrendType getEnum (String value) {
        BarometricPressureTrendType type = null;

        for (BarometricPressureTrendType t : BarometricPressureTrendType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
}
