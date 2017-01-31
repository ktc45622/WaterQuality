package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>MySQLEnrollmentManager</code> class interacts with the following
 * tables: Users, Courses and Enrollment. This class helps to maintain
 * the information about students and courses in the Enrollment table.
 * The Enrollment table maps users to courses on a many-to-many basis. A record
 * in the Enrollment table defines that a <code>User</code> represented by
 * the field UserNumber is enrolled in a <code>Course</code> represented by
 * the field CourseNumber.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Mike Graboske (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 * 
 */
public class MySQLEnrollmentManager implements DBMSEnrollmentManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLEnrollmentManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Returns a list of courses that the given student is currently enrolled in.
     * The list of courses is a <code>Vector</code> object that contains
     * <code>Course</code> objects.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param student The <code>User</code> to obtain courses for.
     * @return A <code>Vector</code> of all courses the given student enrolled in,
     * or an empty <code>Vector</code> if there is no such student in the database.
     */
    @Override
    public Vector<Course> getCoursesForStudent(User student) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Course> list = new Vector<Course>();
        try {
            String sql = "select courses.*, users.*, users.userNumber AS UserNumber FROM users, courses "
                    + "LEFT JOIN enrollment ON courses.courseNumber = enrollment.courseNumber "
                    + "where enrollment.userNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, student.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                boolean isBroken = false;
                Course course = MySQLHelper.makeCourseFromResultSet(rs);
                for(Course c: list){
                    if (c.getCourseNumber()== course.getCourseNumber()){
                        isBroken = true;
                    }
                }
                if(!isBroken)
                    list.add(course);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Returns a list of courses that the given instructor teaches. The list of
     * courses is a <code>Vector</code> object that contains <code>Course</code>
     * objects.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param instructor The <code>User</code> to obtain courses for.
     * @return A <code>Vector</code> of courses the given instructor teaches.
     */
    @Override
    public Vector<Course> getCoursesForInstructor(User instructor) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Course> list = new Vector<Course>();
        try {
            String sql = "select distinct courses.*, users.*, users.userNumber AS UserNumber from courses "
                    + "LEFT JOIN enrollment ON courses.courseNumber = enrollment.courseNumber "
                    + "LEFT JOIN users ON users.userNumber = courses.instructorNumber "
                    + "where courses.instructorNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = MySQLHelper.makeCourseFromResultSet(rs);
                list.add(course);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Returns a list of students enrolled in the given course. The list of
     * students is represented by a <code>Vector</code> object.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param course The <Course> to obtain the students enrolled in.
     * @return A <code>Vector</code> of all students enrolled in the given course,
     * or an empty <code>Vector</code> if there is no record of the given
     * <code>Course</code> in the database.
     */
    @Override
    public Vector<User> getStudentsInCourse(Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<User> list = new Vector<User>();
        try {
            String sql = "SELECT users.* FROM users "
                    + "LEFT JOIN enrollment ON enrollment.userNumber = users.userNumber "
                    + "LEFT JOIN courses ON courses.courseNumber = enrollment.courseNumber "
                    + "WHERE courses.courseNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, course.getCourseNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = MySQLHelper.makeUserFromResultSet(rs);
                list.add(user);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Enrolls the given student into the given course.  Inserts a record for
     * the given student and the given <code>Course</code> into the Enrollment
     * table. The given student is represented by an <code>User</code> object.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param student The <code>User</code> to enroll into the given
     * <code>Course</code>.
     * @param course The <code>Course</code> to enroll the given <code>User</code>
     * into.
     * @return True if the given student was enrolled in the course,
     * false otherwise.
     */
    @Override
    public boolean insertStudentIntoCourse(User student, Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;

        try {
            String sql = "INSERT INTO enrollment (userNumber, courseNumber) VALUES (?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, student.getUserNumber());
            ps.setInt(2, course.getCourseNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(null);
        }
        return bSuccess;
    }

    /**
     * Removes the given student enrolled in the given course from that course.
     * This means that a record of the given student enrolled in the given
     * course is deleted from the Enrollment table.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param student The <code>User</code> to remove from the given
     * <code>Course</code>.
     * @param course The <code>Course</code> to remove the given <code>User</code>
     * from.
     * @return True if the given student was removed from the given course,
     * false otherwise.
     */
    @Override
    public boolean removeStudentFromCourse(User student, Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "DELETE FROM enrollment WHERE userNumber = ? and courseNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, student.getUserNumber());
            ps.setInt(2, course.getCourseNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax. The student with the " + student.getLoginId()
                    + " user login ID was not removed from the course with the "
                    + course.getCourseNumber() + " number.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Removes all students enrolled in the given course from that course.
     * This means that a record of each student enrolled in the given
     * course is deleted from the Enrollment table.
     * Students are also deleted from our user table unless they are
     * registered for other courses.
     * 
     * @param course The <code>Course</code> to remove all students from.
     */
    @Override
    public void removeAllStudentsFromCourse(Course course) {
        Vector<User> students = getStudentsInCourse(course);
        for (User user : students) {
            removeStudentFromCourse(user, course);
        }
        students.clear();// Empty vector
        //deleteStudentsEnrolledInNoCourses();
    }

    /**
     * Delete all students enrolled in no courses.
     * @return True if all students enrolled in no courses were removed, false otherwise.
     */
    @Override
    public boolean deleteStudentsEnrolledInNoCourses()
    {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccess = false;
        try {
            String sql = "DELETE FROM users WHERE "
                    + " userType = 'Student' AND "
                    + " userNumber IN "
                    + "(SELECT userNumber FROM enrollment WHERE"
                    + " users.userNumber = enrollment.userNumber)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            isSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccess;
    }
}
