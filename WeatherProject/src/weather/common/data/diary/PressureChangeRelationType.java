package weather.common.data.diary;

import java.io.Serializable;

/**
 * An enumeration representing choices for the question of how the daily pressure
 * change relates to the standard atmospheric pressure.
 *
 * @author Eric Subach
 * @version 2011
 */
public enum PressureChangeRelationType implements Serializable {
    NA (""),
    ABOVE ("Above"),
    BELOW ("Below");

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


    PressureChangeRelationType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation for displaying on screen.
     */
    public String displayString() {
        return stringValue;
    }

    /**
     * Return the PressureChangeRelationType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static PressureChangeRelationType getEnum (String value) {
        PressureChangeRelationType type = null;

        for (PressureChangeRelationType r : PressureChangeRelationType.values ()) {
            if (r.stringValue.equals (value))
            {
                type = r;
                break;
            }
        }

        return (type);
    }
}
