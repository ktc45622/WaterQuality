
package weather.common.data;

import java.io.Serializable;

/**
 * An enumeration containing all data types for an instructor.
 *
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public enum InstructorDataType implements Serializable {

    /**
     * Means the InstructorData belongs to the Notes type, which is for class
     * notes.
     */
    Notes("Notes"),
    /**
     * Means the InstructorData belong to the Instructional Lessons type.
     */
    InstructionalLessons("Instructional Lessons"),
    /**
     * Means the InstructorData belong to the Bookmarks type.
     */
    Bookmarks("Bookmarks"), 
    /**
     * Means the InstructorData is private.
     */
    Private("Private");
    
    private static final long serialVersionUID = 1L;
    private String stringValue;
    
    InstructorDataType(String stringValue) {
        this.stringValue = stringValue;
    }
    
    /**
     * Returns a String representation of this InstructorDataType.
     * @return String representation of this InstructorDataType.
     */
    public String toString() {
        return this.stringValue; 
    }
    
    /**
     * Returns an enumerated constant based on a String stored in the database.
     * If the string does not represent a defined enumerated value, it returns
     * null.
     * 
     * @param val String value of the enumeration.
     * @return An enumerated constant or null if none was found.
     */
    public static InstructorDataType fromString(String val) {
        
        for (InstructorDataType t : InstructorDataType.values()) {
            if (t.stringValue.equalsIgnoreCase(val))
                return t;
        }
        
        return null;
    }
}
