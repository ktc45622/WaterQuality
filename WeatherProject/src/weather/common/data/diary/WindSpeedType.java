package weather.common.data.diary;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * This class represents an enumeration type representing the wind speed types.
 * @author Chris Mertens
 */
public enum WindSpeedType implements Serializable
{
    NA ("N/A"),
    CALM ("<1 mph: Calm"),
    LIGHT_AIR ("01-02 mph: Light air"),
    LIGHT_BREEZE ("03-07 mph: Light breeze"),
    GENTLE_BREEZE ("08-12 mph: Gentle breeze"),
    MODERATE_BREEZE ("13-17 mph: Moderate breeze"),
    FRESH_BREEZE ("18-24 mph: Fresh breeze"),
    STRONG_BREEZE ("25-30 mph: Strong breeze"),
    HIGH_WIND_MODERATE_GALE ("31-38 mph: High wind / Moderate gale"),
    FRESH_GALE ("39-46 mph: Fresh gale"),
    STRONG_GALE ("47-54 mph: Strong gale"),
    WHOLE_GALE_STORM ("55-63 mph: Whole gale / Storm"),
    VIOLENT_STORM ("64-72 mph: Violent storm"),
    HURRICANE_FORCE (">72 mph: Hurricane-force");

    /**
    * Determines if a de-serialized file is compatible with this class.
    *
    * Not necessary to include in first version of the class, but included here
    * as a reminder of its importance. Maintainers must change this value if
    * and only if the new version of this class is not compatible with old
    * versions.
    *
    * @see <a href="http://java.sun.com/products/jdk/1.1/docs/guide
    * /serialization/spec/version.doc.html">Java specification for
    * serialization</a>
    * @serial
    */
    private static final long serialVersionUID = 1L;

    private String stringValue;

    private WindSpeedType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation for displaying on screen.
     *
     * NOTE: to get a string for updating the database, use the toString method.
     *
     * @return A String representation.
     */
    public String displayString () {
        return (stringValue);
    }
    
     /**
     * Gets a string representation that stops before the colon and with the
     * "mph" if present.
     *
     * NOTE: to get a string for updating the database, use the toString method.
     *
     * @return A String representation.
     */
    public String displayMPH () {
        StringTokenizer st = new StringTokenizer(stringValue, ":", false);
        return st.nextToken();
    }

    /**
     * Return the WindSpeedType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return Enumeration with that string value.
     */
    public static WindSpeedType getEnum (String value) {
        WindSpeedType type = null;

        for (WindSpeedType t : WindSpeedType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
}
