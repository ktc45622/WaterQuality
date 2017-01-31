package weather.common.data.diary;

import java.io.Serializable;

/**
 * An enumeration representing choices for predicting tomorrow's temperature.
 *
 * @author Eric Subach
 * @version 2011
 */
public enum TemperaturePredictionType implements Serializable {
    NA (""),
    SAME ("The same"),
    WARMER ("Warmer"),
    COLDER ("Colder");

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


    TemperaturePredictionType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation for displaying on screen.
     */
    public String displayString() {
        return stringValue;
    }

    /**
     * Return the TemperaturePredictionType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static TemperaturePredictionType getEnum (String value) {
        TemperaturePredictionType type = null;

        for (TemperaturePredictionType r : TemperaturePredictionType.values ()) {
            if (r.stringValue.equals (value))
            {
                type = r;
                break;
            }
        }

        return (type);
    }
}
