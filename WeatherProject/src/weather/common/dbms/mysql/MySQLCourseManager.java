package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.dbms.DBMSCourseManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>MySQLCourseManager</code> class performs all the interactions with
 * the Courses table. The Courses table contains records of courses. Each record
 * represents a course at a university. A course is defined by a <code>Course</code>
 * object. This class handles the removal, insertion, updating, and retrieval
 * of courses from the Courses table.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Mike Graboske (2008)
 * @author Ioulia Lee (2010)
 * @author Jeremy Benscoter (2014)
 * @version Spring 2010
 */
public class MySQLCourseManager implements DBMSCourseManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;
   
    public MySQLCourseManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }
/**
 * Obtains all the courses from the Courses table. Arranges the <code>Course</code>
 * objects in a Vector object and returns it.
 * Catches <code>SQLException</code> if there is an error in the SQL
 * statement. Logs <code>SQLException</code> in the log file, creates a new
 * <code>WeatherException</code> and displays it to a user.
 *
 * @return A <code>Vector</code> object representing all courses in the database,
 * or an empty <code>Vector</code> if there are no courses in the database.
 */
    @Override
    public Vector<Course> obtainAllCourses() {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Course> list = new Vector<Course>();
        try {       
            //Users.Number AS UserNumber is to avoid potentially strange column names from the DB
            String sql = "SELECT *, users.userNumber AS UserNumber"
                    + " FROM courses LEFT JOIN users ON users.userNumber = courses.instructorNumber";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Course course = MySQLHelper.makeCourseFromResultSet(rs);
                list.add(course);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
 * Obtains all the courses from the Courses table. Arranges the <code>Course</code>
 * objects in a Vector object and returns it.
 * Catches <code>SQLException</code> if there is an error in the SQL
 * statement. Logs <code>SQLException</code> in the log file, creates a new
 * <code>WeatherException</code> and displays it to a user.
 *
 * @return A <code>Vector</code> object representing all courses in the database,
 * or an empty <code>Vector</code> if there are no courses in the database.
 */
    @Override
    public Vector<Course> obtainAllCoursesTaughyByUser(User currentLoggedInUser) {
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        Vector<Course> list = new Vector<Course>();
        try {
            //Users.Number AS UserNumber is to avoid potentially strange column names from the DB
            String sql = "SELECT *, users.userNumber AS UserNumber FROM courses LEFT "
                    + " JOIN users ON users.userNumber = courses.instructorNumber "
                    + " WHERE users.userNumber = (?)";
            conn = dbms.getLocalConnection();

            ps = conn.prepareStatement(sql);
            ps.setInt(1, currentLoggedInUser.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = MySQLHelper.makeCourseFromResultSet(rs);
                list.add(course);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Returns a course that matches the passed department number, course number,
     * and section number.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param classIdentifier The class identifier.
     * @param className The name of the class.
     * @param section The section number of the specified course.
     * @return A <code>Course</code> with the given department number, course
     * number and course section, or null if the <code>Course</code> with
     * the given parameters does not exist.
     */
    @Override
    public Course obtainCourse(String classIdentifier, int section, String className) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Course c = null;
        try {
            String sql = "SELECT *, users.userNumber AS UserNumber FROM courses " +
                    "LEFT JOIN users ON users.userNumber = courses.instructorNumber " +
                    "WHERE classIdentifier = ? AND section = ? AND className = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, classIdentifier);
            ps.setInt(2, section);
            ps.setString(3, className);
            rs = ps.executeQuery();    
            if (rs.first())
                c = MySQLHelper.makeCourseFromResultSet(rs);
        } catch (SQLException e) {  
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return c;
    }
    /**
     * Removes a course from the database that matches the passed course 
     * information.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param course The course to be removed.
     * @return True if the course was deleted, false otherwise.
     */
    @Override
    public boolean removeCourse(Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        boolean bSuccess = false;
        try {
            String sql = "DELETE FROM courses WHERE courseNumber = ?;";
            String sql2 = "DELETE FROM enrollment WHERE courseNumber = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps2 = conn.prepareStatement(sql2);
            ps.setInt(1, course.getCourseNumber());
            ps2.setInt(1, course.getCourseNumber());
            bSuccess = !ps.execute() && !ps2.execute();
        } catch (SQLException e) {   
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
            MySQLHelper.closePreparedStatement(ps2);
        }
        return bSuccess;
    }
    /**
     * Updates the record of the given <code>Course</code> in the Courses table.
     * The fields of the record in the table are set to the values of
     * the corresponding attributes of the given <code>Course</code> object.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param course The course to be updated.
     * @return True if the course was updated, false otherwise.
     */
    @Override
    public boolean updateCourse(Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE courses SET " +
                    "departmentName = ?, classIdentifier = ?, Section = ?, "+
                    "className = ?, semesterType = ?, year = ?, instructorNumber = ? " +
                    "WHERE courseNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, course.getDepartmentName());
            ps.setString(2, course.getClassIdentifier());
            ps.setInt(3, course.getSection());
            ps.setString(4, course.getClassName());
            ps.setString(5, course.getSemester().toString());
            ps.setInt(6, course.getYear());
            ps.setInt(7, course.getInstructor().getUserNumber());
            ps.setInt(8, course.getCourseNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {       
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }
/**
 * Inserts a <code>Course</code> into the database with the attributes
 * that are passed from <code>Course</code>.
 * Catches <code>SQLException</code> if there is an error in the SQL
 * statement. Logs <code>SQLException</code> in the log file, creates a new
 * <code>WeatherException</code> and displays it to a user.
 *
 * @param course The course to be inserted.
 * @return A string representation of the inserted course.
 */
    @Override
    public Course insertCourse(Course course){
        Connection conn = null;
        PreparedStatement ps = null;
        Calendar calendar = Calendar.getInstance();
        Timestamp currentDateTime = new Timestamp(calendar.getTime().getTime());

        try {
            String sql = "INSERT INTO courses (departmentName, classIdentifier, "+
                    "section, className, semesterType, year, instructorNumber, creationDate) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
            ps.setString(1, course.getDepartmentName());
            ps.setString(2, course.getClassIdentifier());
            ps.setInt(3, course.getSection());
            ps.setString(4, course.getClassName());
            ps.setString(5, course.getSemester().toString());
            ps.setInt(6, course.getYear());
            ps.setInt(7, course.getInstructor().getUserNumber());
            ps.setTimestamp(8, currentDateTime);
            course.setCourseNumber(MySQLHelper.executeStatementAndReturnGeneratedKey(ps));
        } catch (SQLException e) {   
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return course;
    }

    /**
     * Removes Removes courses created before this date.
     * @param date Removes courses created before this date.
     * @return True if courses were removed, false otherwise.
     */
    @Override
    public Vector<Course> obtainInactiveCourses(Date date) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Course> list = new Vector<Course>();

        try {
            String sql = "SELECT *, users.userNumber AS UserNumber " +
                    " FROM courses LEFT JOIN users ON users.userNumber = courses.instructorNumber" +
                    " WHERE courses.creationDate < (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, date);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Removes Removes courses created before this date.
     * @param date Removes courses created before this date.
     * @return True if courses were removed, false otherwise.
     */
    @Override
    public boolean removeCoursesBeforeDate(Date date)
    {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        try {
            String sql = "DELETE FROM courses WHERE "
                    + " creationDate < (?) ";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, date);

            // if the update was successful, test whether any rows were affected,
            // if not, not users were deleted, return false
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 0) {
                    isSuccessfull = false;
                } else {
                    isSuccessfull = true;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return isSuccessfull;
    }

    /**
     * Retrieves meta data for the course table.
     * @return A meta data object that wraps the meta data of the courses table.
     */
    @Override
    public TableMetaData getMetaData(){
        ResultSet rs = null;
        Connection conn = null;
        TableMetaData tableMetaData = null;
        ResultSetMetaData md;
        int colCount = 0;
        int rowCount = 0;
        String[] colNames = null;
        try {
            //Users.Number AS UserNumber is to avoid potentially strange column names from the DB
            String sql = "SELECT * FROM courses";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);

            if(rs.next()){
            md = rs.getMetaData();
            colCount = md.getColumnCount();
            colNames = new String[colCount];
                for (int i = 1; i <= colCount; i++) {
                    colNames[i-1] = md.getColumnName(i);
                }
            rs.last();
            rowCount = rs.getRow();
            }

            tableMetaData = new TableMetaData(colCount, colNames, rowCount);
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return tableMetaData;
    }

    @Override
    public Course obtainCourse(int courseNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Course c = null;
        try {
            String sql = "SELECT *, users.userNumber AS UserNumber FROM courses " +
                    "LEFT JOIN users ON users.userNumber = courses.instructorNumber " +
                    "WHERE courseNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseNumber);
            rs = ps.executeQuery();    
            if (rs.first())
                c = MySQLHelper.makeCourseFromResultSet(rs);
        } catch (SQLException e) {  
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return c;
    }

    @Override
    public Vector<Course> obtainCoursesByStudent(User u) {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Course> list = new Vector<Course>();
        try {       
            //Users.Number AS UserNumber is to avoid potentially strange column names from the DB
            String sql = "SELECT courses.courseNumber"
                    + ", courses.departmentName"
                    + ", courses.classIdentifier"
                    + ", courses.section"
                    + ", courses.className"
                    + ", courses.semesterType"
                    + ", courses.year"
                    + ", courses.instructorNumber"
                    + ", courses.creationDate"
                    + ", users.userNumber"
                    + ", users.loginId"
                    + ", users.loginPassword"
                    + ", users.email"
                    + ", users.firstName"
                    + ", users.lastName"
                    + ", users.userType"
                    + ", users.notes"
                    + ", users.lastLoginTime"
                    + ", users.loginCount"
                    + " FROM courses "
                    + " INNER JOIN users ON users.userNumber = courses.instructorNumber"
                    + " INNER JOIN enrollment ON enrollment.courseNumber = courses.courseNumber"
                    + " WHERE enrollment.userNumber = ?;";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1,u.getUserNumber());
            rs = statement.executeQuery();
            while (rs.next()) {
                Course course = MySQLHelper.makeCourseFromResultSet(rs);
                list.add(course);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
}
