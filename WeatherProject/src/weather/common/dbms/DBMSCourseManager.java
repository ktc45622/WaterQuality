
package weather.common.dbms;

import java.sql.Date;
import java.util.Vector;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.mysql.TableMetaData;

/**
 * The <code>DBMSCourseManager</code> allows client classes to add, remove, and
 * update courses stored in the Courses table.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall
 * @version Spring 2008
 */
public interface DBMSCourseManager {

    /**
     * Returns a <code>Vector</code> of all courses in the database.
     *
     * @return A <code>Vector</code> of all courses.
     */
    public Vector<Course> obtainAllCourses();
    
    /**
     * Returns a <code>Vector</code> of all courses in the database.
     *
     * @return A <code>Vector</code> of all courses.
     */
    public Vector<Course> obtainCoursesByStudent(User user);

    /**
     * Obtains a <code>Course</code> with the specified class identifier,
     * section, and class name.
     *
     * @param classIdentifier The identifier of the class.
     * @param section The section of the course.
     * @param className The name of the class.
     * @return A <code>Course</code> object representing the course.
     */
    public Course obtainCourse(String classIdentifier, int section, String className);
    
    /**
     * Obtains a <code>Course</code> with the specified course number.
     *
     * @param courseNumber The course number of the class.
     * @return A <code>Course</code> object representing the course.
     */
    public Course obtainCourse(int courseNumber);

    /**
     * Removes a <code>Course</code> with the id of the given course.
     *
     * @param course The <code>Course</code> to be removed.
     * @return True if the given <code>Course</code> was removed, false otherwise.
     */
    public boolean removeCourse(Course course);

    /**
     * Updates a <code>Course</code> with the id and the attributes of
     * the given course.
     *
     * @param course The <code>Course</code> with the id and the attributes to
     * be changed.
     * @return True if the <code>Course</code> information was updated, false
     * otherwise.
     */
    public boolean updateCourse(Course course);
    
    /**
     * Creates a new <code>Course</code> in the database with the attributes
     * of the given <code>Course</code>.
     *
     * @param course The <code>Course</code> to be created in the database.
     * @return The <code>Course</code> with its 'number' field now set.
     */
    public Course insertCourse(Course course);

    /**
     * Removes Removes courses created before this date.
     * @param date Removes courses created before this date.
     * @return True if courses were removed, false otherwise.
     */
    public Vector<Course> obtainInactiveCourses(Date date);

    /**
     * Removes Removes courses created before this date.
     * @param date Removes courses created before this date.
     * @return True if courses were removed, false otherwise.
     */
    public boolean removeCoursesBeforeDate(Date date);

    /**
     * Retrieves table meta data.
     * @return A table meta data object for this table.
     */
    public TableMetaData getMetaData();

    /**
     * Retrieves all the courses taught by the currently logged in user.
     * @param currentLoggedInUser The user currently logged in.
     * @return A vector of all the courses the user is teaching, or null if
     *  he/she is not in any.
     */
    public Vector<Course> obtainAllCoursesTaughyByUser(User currentLoggedInUser);
}
