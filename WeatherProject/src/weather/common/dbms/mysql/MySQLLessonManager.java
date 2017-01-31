package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.AccessRights;
import weather.common.data.lesson.Lesson;
import weather.common.dbms.DBMSLessonManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the lessons table in the database.
 * 
 * @author Nicole Burfeind
 * @version Spring 2012
 */
public class MySQLLessonManager implements DBMSLessonManager {

    private MySQLImpl dbms;
    
    public MySQLLessonManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    
    /**
     * Adds a <code>Lesson</code> to the database.
     * @param lesson The <code>Lesson</code> to be added.
     * @return True if it was added successfully, false otherwise.
     */
    @Override
    public boolean add(Lesson lesson) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccessful = false;
        
        try {
            String sql = "INSERT INTO lessons ( instructorNumber, "
                    + "lessonCategoryNumber, accessRights, lessonName) "
                    + "VALUES (?, ?, ?, ?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

            ps.setInt(1, lesson.getInstructorNumber());
            ps.setInt(2, lesson.getLessonCategoryNumber());
            ps.setString(3, lesson.getAccessRights().toString());
            ps.setString(4, lesson.getName());
            lesson.setLessonNumber(MySQLHelper.executeStatementAndReturnGeneratedKey(ps));
            if (lesson.getLessonNumber() > 0)
                isSuccessful = true;

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Updates a <code>Lesson</code> in the database.
     * @param lesson The <code>Lesson</code> to be updated.
     * @return True if the update was successful, false otherwise.
     */
    @Override
    public boolean update(Lesson lesson) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "UPDATE lessons SET " +
                    "instructorNumber = ?, lessonCategoryNumber = ?, accessRights = ?, "+
                    "lessonName = ? WHERE lessonNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lesson.getInstructorNumber());
            ps.setInt(2, lesson.getLessonCategoryNumber());
            ps.setString(3, lesson.getAccessRights().toString());
            ps.setString(4, lesson.getName());
            ps.setInt(5, lesson.getLessonNumber());
            isSuccessful = !ps.execute();
        } catch (SQLException e) {       
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Deletes a <code>Lesson</code> from the database.
     * @param lesson The <code>Lesson</code> to be deleted.
     * @return True if the delete was successful, false otherwise.
     */
    @Override
    public boolean delete(Lesson lesson) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "DELETE FROM lessons WHERE lessonNumber = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lesson.getLessonNumber());
            isSuccessful = !ps.execute();
        } catch (SQLException e) {   
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Gets a <code>Lesson</code> from the database by lessonNumber.
     * @param lessonNumber The ID of the <code>Lesson</code> the user wants.
     * @return The <code>Lesson</code> at that lessonNumber.
     */
    @Override
    public Lesson get(int lessonNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Lesson lesson = null;
        
        try {
            String sql = "SELECT * FROM lessons WHERE lessonNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonNumber);
            rs = ps.executeQuery();
            if (rs.first())
                lesson = MySQLHelper.makeLessonFromResultSet(rs);
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
        
        return lesson;
    }

    /**
     * Obtains a <code>Vector</code> of <code>Lesson</code>s from the database.
     * @return A <code>Vector</code> of <code>Lesson</code>s.
     */
    @Override
    public Vector<Lesson> obtainAll() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<Lesson> list = new Vector<Lesson>();
        try {
            String sql = "SELECT * FROM lessons";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonFromResultSet(rs));
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
     * Obtains a <code>Vector</code> of <code>Lesson</code>s from the database by user.
     * @param userNumber The ID of the <code>User</code> to see their <code>Lesson</code>s.
     * @return A <code>Vector</code> of <code>Lesson</code>s.
     */
    @Override
    public Vector<Lesson> obtainByUser(int userNumber) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Lesson> list = new Vector<Lesson>();

        try {
            String sql = "SELECT * FROM lessons "
                    + "WHERE createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonFromResultSet(rs));
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
     * Obtains a <code>Vector</code> of <code>Lesson</code>s from the database by category.
     * @param categoryNumber The ID of the category for the <code>Lesson</code>s.
     * @return A <code>Vector</code> of <code>Lesson</code>s.
     */
    @Override
    public Vector<Lesson> obtainByCategory(int categoryNumber) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Lesson> list = new Vector<Lesson>();

        try {
            String sql = "SELECT * FROM lessons "
                    + "WHERE lessonCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonFromResultSet(rs));
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
     * Obtains a <code>Vector</code> of <code>Lesson</code>s from the database by <code>AccessRights</code>.
     * @param access The <code>AccessRights</code> to check for.
     * @return A <code>Vector</code> of <code>Lesson</code>s.
     */
    @Override
    public Vector<Lesson> obtainByAccessRights(AccessRights access) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<Lesson> list = new Vector<Lesson>();

        try {
            String sql = "SELECT * FROM lessons "
                    + "WHERE accessRights = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, access.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonFromResultSet(rs));
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
}
