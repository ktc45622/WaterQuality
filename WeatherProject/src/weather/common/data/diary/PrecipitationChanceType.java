package weather.common.data.diary;

import java.io.Serializable;

/**
 * An enumeration representing choices for the question of what the chance of
 * precipitation is for tomorrow.
 *
 * @author Eric Subach
 * @version 2011
 */
public enum PrecipitationChanceType implements Serializable {
    NA (""),
    NOT_SURE ("Not sure"),
    CLEAR ("Clear"),
    UNSETTLED ("Unsettled");

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

    // String representation.
    private String stringValue;


    PrecipitationChanceType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation for displaying on screen.
     */
    public String displayString() {
        return stringValue;
    }

    /**
     * Return the PrecipitationChanceType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static PrecipitationChanceType getEnum (String value) {
        PrecipitationChanceType type = null;

        for (PrecipitationChanceType r : PrecipitationChanceType.values ()) {
            if (r.stringValue.equals (value))
            {
                type = r;
                break;
            }
        }

        return (type);
    }
}
