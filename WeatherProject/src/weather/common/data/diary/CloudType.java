package weather.common.data.diary;

import java.io.Serializable;

/**
 * This class represents an enumeration of the cloud
 * coverage types.
 *
 * @author Chris Mertens
 */
public enum CloudType implements Serializable{
    NA ("N/A"),
    STRATUS ("Stratus"),
    NIMBOSTRATUS ("Nimbostratus"),
    STRATOCUMULUS ("Stratocumulus"),
    CUMULUS_HUMILIS ("Cumulus (Humilis)"),
    CUMULUS_MEDIOCRIS ("Cumulus (Mediocris)"),
    CUMULUS_CONGESTUS ("Cumulus (Congestus)"),    
    CUMULONIMBUS ("Cumulonimbus"),
    ALTOSTRATUS ("Altostratus"),
    ALTOCUMULUS ("Altocumulus"),
    CIRRUS ("Cirrus"),
    CIRROSTRATUS ("Cirrostratus"),
    CIRROCUMULUS ("Cirrocumulus"),
    CLEAR ("Clear");

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

    private CloudType(String str) {
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
     * Return the CloudType with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static CloudType getEnum (String value) {
        CloudType type = null;

        for (CloudType t : CloudType.values ()) {
            if (t.stringValue.equals (value))
            {
                type = t;
                break;
            }
        }

        return type;
    }
}
