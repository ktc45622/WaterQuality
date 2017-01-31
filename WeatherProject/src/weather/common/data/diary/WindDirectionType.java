package weather.common.data.diary;

import java.io.Serializable;

/**
 * This class represents an enumeration type representing the wind direction types.
 *
 * @author Chris Mertens
 * @author Joe Van Lente
 */
public enum WindDirectionType implements Serializable
{
    // Note: These values MUST match the values of the JRadioButton array in the
    //       getWindDirectionButtonArray() method in the 
    //       NotesAndDiaryPanelManager class exactly with NA added at the top of
    //       the list.
    NA ("N/A"),
    NW ("NW"),
    N ("N"),
    NE ("NE"),
    E ("E"),
    SE ("SE"),
    S ("S"),
    SW ("SW"),
    W ("W"),    
    CALM ("Calm");

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

    private String stringValue;

    private WindDirectionType(String str) {
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
     * Return the WindDirectionType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static WindDirectionType getEnum (String value) {
        WindDirectionType type = null;

        for (WindDirectionType t : WindDirectionType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
}
