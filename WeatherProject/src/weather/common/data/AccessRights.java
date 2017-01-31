package weather.common.data;

import java.io.Serializable;

/**
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Joseph Horro (2011)
 * @version Spring 2008
 * @version Spring 2011
 */

/**
 * This enumeration describes the visibility of any instructor created document or 
 * information that could be viewed by students. 
 * The choices are:
 *      Test (used for testing purposes);
 *      Everyone (allows Everyone to  have access to the information);
 *      AllStudents (allows access by all students);
 *      CourseStudents (allows students only in the instructor's courses 
 *                     to have access to the information);
 *      Instructors(allows only instructors to have access to the information);
 *      and Private(which allows only the person who created the information to have
 *          access to it).
 */
public enum AccessRights implements Serializable {
    /**
     * Only used when testing the system
     */
      test("Test"),
    /**
     * Allows everyone to be able to view the information.
     */
    Everyone("Everyone"),
    /**
     * Allows all students to be able to view the information.
     */
    AllStudents("AllStudents"),
    /**
     * Allows only the students in the course to be able to view the information.
     */
    CourseStudents("CourseStudents"),
    /**
     * Allows only instructors to be able to view the information.
     */
    Instructors("Instructors"),
    /**
     * Allows only the person who created the information to view it.
     */
    Private("Private");

    private String stringValue;


    AccessRights (String stringValue) {
        this.stringValue = stringValue;
    }
    /**
     * It return string value in String format.
     * @return The string value.
     */
    @Override
    public String toString () {
        return (stringValue);
    }

    /**
     * Return the AccessRights with the corresponding string value.
     *
     * @param value String value of the enumeration.
     * @return Enumeration with that string value.
     */
    public static AccessRights getEnum (String value) {
       AccessRights access = null;

        for (AccessRights e : AccessRights.values ()) {
            if (e.stringValue.equals (value))
            {
                access = e;
                break;
            }
        }
        return (access);
    }

    private static final long serialVersionUID = 1L;
};
