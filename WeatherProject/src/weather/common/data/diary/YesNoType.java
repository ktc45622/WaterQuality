package weather.common.data.diary;

import java.io.Serializable;

/**
 * An enumeration representing a yes or no response.
 *
 * @author Eric Subach
 * @version 2011
 */
public enum YesNoType implements Serializable {
    NA (""),
    YES ("Yes"),
    NO ("No");

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


    YesNoType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation for displaying on screen.
     */
    public String displayString() {
        return stringValue;
    }

    /**
     * Return the YesNoType with the corresponding string value.
     *
     * @param value String value of the enumeration
     *
     * @return The enumeration with that string value.
     */
    public static YesNoType getEnum (String value) {
        YesNoType type = null;

        for (YesNoType r : YesNoType.values ()) {
            if (r.stringValue.equals (value))
            {
                type = r;
                break;
            }
        }

        return (type);
    }
}
