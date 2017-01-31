package weather.common.data;

import java.io.Serializable;

/**
 * Adds the ability for instructors to add user id's to their students.
 *
 * @author Mike Nacko
 * @author Joseph Horro spring 2011
 */
public class EnrolledStudent implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The student's studentID.
     *
     * @serial
     */
    private String studentID;
    /**
     * The course number for this enrollment.
     */
    private int courseNumber;
    /**
     * The user number which links to a primary key in the users table.
     */
    private int userNumber;
    
    /**
     * Default constructor - creates a blank object.
     */
    public EnrolledStudent()
    {
        this.studentID = null;
        this.courseNumber = -1;
        this.userNumber = -1;
    }
    
    /**
     * Creates an enrolled student with complete information.
     *
     * @param studentID A student's ID, is not unique per student.
     * @param courseNumber The primary key of the courseNumber, used to link to
     *                       the course table.
     * @param userNumber The user tables primary key, for the users table, used
     *                     to link to the user table.
     */
    public EnrolledStudent(String studentID, int courseNumber, int userNumber)
    {
        this.studentID = studentID;
        this.courseNumber = courseNumber;
        this.userNumber = userNumber;
    }

    /**
     * Gets the student's studentID.
     *
     * @return The student's userID.
     */
    public String getStudentID() {
        return studentID;
    }

    /**
     * Sets the user's studentID.
     *
     * @param studentID A String student ID.
     */
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    /**
     * Gets the courseNumber for this enrollment.
     *
     * @return The courseNumber for this enrollment.
     */
    public int getCourseNumber() {
        return courseNumber;
    }

    /**
     * Sets the course number for this enrollment.
     *
     * @param courseNumber An integer course number.
     */
    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    /**
     * Gets the user number.
     *
     * @return The user number.
     */
    public int getUserNumber() {
        return userNumber;
    }

    /**
     * Sets the user number.
     *
     * @param userNumber The user number for some user.
     */
    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }
}
