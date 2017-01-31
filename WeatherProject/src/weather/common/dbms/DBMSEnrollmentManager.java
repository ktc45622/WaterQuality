
package weather.common.dbms;

import java.util.Vector;
import weather.common.data.Course;
import weather.common.data.User;

/**
 * This manager enables you to add and remove students from courses on 
 * the database level. Maintains the Enrollment table.
 * 
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @version Spring 2008
 */
public interface DBMSEnrollmentManager {
    
    /**
     * Returns a list of courses that the given student is in.
     *
     * @param student The student to receive a list of courses for.
     * @return A <code>Vector</code> of courses for the passed student.
     */
    public Vector<Course> getCoursesForStudent(User student);
    
    /**
     * Returns a list of the courses that the given instructor teaches.
     *
     * @param instructor The instructor to receive a list of courses for.
     * @return A <code>Vector</code> of the courses the given instructor teaches.
     */
    public Vector<Course> getCoursesForInstructor(User instructor);
    
    /**
     * Returns a list of students who are in the given course.
     *
     * @param course The course to return a list of students for.
     * @return A <code>Vector</code> of students in the given course.
     */
    public Vector<User> getStudentsInCourse(Course course);
    
    /**
     * Inserts the given <code>User</code> into the given <code>Course</code>.
     *
     * @param student The student to be placed into the course.
     * @param course The course to place the student into.
     * @return True if the operation is a success, false otherwise.
     */
    public boolean insertStudentIntoCourse(User student, Course course);
    
    /**
     * Removes the given <code>User</code> from the given <code>Course</code>.
     *
     * @param student The student to be removed from the course.
     * @param course The course to remove the student from.
     * @return True if the operation is a success, false otherwise.
     */
    public boolean removeStudentFromCourse(User student, Course course);

    /**
     * Removes all students from the given course.
     * 
     * @param course The <code>Course</code> to remove all students from.
     */
    public void removeAllStudentsFromCourse(Course course);

    /**
     * Delete all students enrolled in no courses.
     * @return True if all students enrolled in no courses were removed, false otherwise.
     */
    public boolean deleteStudentsEnrolledInNoCourses();
}
