
package weather.common.data;

import java.io.Serializable;

/**
 * An enumeration containing all semesters within an academic year.
 *
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public enum SemesterType implements Serializable{
    /**
     * Represents the Winter semester.
     */
    Winter,

    /**
     * Represents the Spring semester.
     */
    Spring,

    /**
     * Represents the Summer semester.
     */
    Summer,
    
    /**
     * Represents the Fall semester.
     */
    Fall;

    private static final long serialVersionUID = 1L;
    
}
