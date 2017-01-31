package weather.common.data.diary;

import java.io.Serializable;

/**
 * An enumeration representing a summary of the wind direction for the entire day.
 *
 * @author Eric Subach
 * @version 2011
 */
public enum WindDirectionSummaryType implements Serializable {
    NA ("N/A"),
    LIGHT_AND_VARIABLE ("Winds were light and variable"),
    CONSTANT ("Wind direction remained constant"),
    NORTHERLY ("Winds shifted to a northerly direction"),
    EASTERLY ("Winds shifted to a easterly direction"),
    SOUTHERLY ("Winds shifted to a southerly direction"),
    WESTERLY ("Winds shifted to a westerly direction");

   /**
    * Determines if a de-serialized file is compatible with this class.
    *
    * Not necessary to include in first version of the class, but included here
    * as a reminder of its importance. Maintainers must change this value if
    * and only if the new version of this class is not compatible with old
    * versions.
    *
    * @see <a href="http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html">Java specification for
    * serialization</a>
    * @serial
    */
    private static final long serialVersionUID = 1L;

    // String representation.
    private String stringValue;

    
    WindDirectionSummaryType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation for displaying on screen.
     */
    public String displayString() {
        return stringValue;
    }

    /**
     * Return the WindDirectionSummaryType with the corresponding string value.
     *
     * @param value string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static WindDirectionSummaryType getEnum (String value) {
        WindDirectionSummaryType type = null;

        for (WindDirectionSummaryType r : WindDirectionSummaryType.values ()) {
            if (r.stringValue.equals (value))
            {
                type = r;
                break;
            }
        }

        return (type);
    }
}
