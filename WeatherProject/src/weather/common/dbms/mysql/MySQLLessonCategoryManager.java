package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.lesson.LessonCategory;
import weather.common.dbms.DBMSLessonCategoryManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the lesson_categories table in the database.
 * 
 * @author Nicole Burfeind
 * @version Spring 2012
 */
public class MySQLLessonCategoryManager implements DBMSLessonCategoryManager {
    
    private MySQLImpl dbms;
    
    public MySQLLessonCategoryManager(MySQLImpl dbms){
        this.dbms = dbms;
    }

    /**
     * Adds a <code>LessonCategory</code> to the database.
     * @param category The <code>LessonCategory</code> to be added.
     * @return True if the <code>LessonCategory</code> is added, false otherwise.
     */
    @Override
    public boolean add(LessonCategory category) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccessful = false;
        
        try {
            conn = dbms.getLocalConnection();
            String sql = "SELECT MAX(orderRank) FROM bookmark_categories";
            rs = conn.createStatement().executeQuery(sql);
            int orderRank = 0;
            if (rs.next()) {
                orderRank = rs.getInt("MAX(orderRank)") + 1;
            }
            sql = "INSERT INTO lesson_categories ( lessonCategoryName, "
                    + "displayOrder, instructorNumber, accessRights)"
                    + "VALUES (?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

            ps.setString(1, category.getCategoryName());
            ps.setInt(2, orderRank);
            ps.setInt(3, category.getInstructorNumber());
            ps.setString(4, category.getAccessRights().toString());
            category.setLessonCategoryNumber(MySQLHelper.executeStatementAndReturnGeneratedKey(ps));
            if (category.getLessonCategoryNumber() > 0)
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
     * Updates the <code>LessonCategory</code> in the database.
     * @param category The <code>LessonCategory</code> to be updated.
     * @return True if the <code>LessonCategory</code> was updated, false otherwise.
     */
    @Override
    public boolean update(LessonCategory category) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "UPDATE lesson_categories SET " +
                    "lessonCategoryNumber = ?, lessonCategoryName = ?, displayOrder = ?,"+
                    "instructorNumber = ?, accessRights = ? "
                    + "WHERE lessonCategoryNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, category.getLessonCategoryNumber());
            ps.setString(2, category.getCategoryName());
            ps.setInt(3, category.getDisplayOrder());
            ps.setInt(4, category.getInstructorNumber());
            ps.setString(5, category.getAccessRights().toString());
            ps.setInt(6, category.getLessonCategoryNumber());
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
     * Deletes a <code>LessonCategory</code> from the database.
     * @param category The <code>LessonCategory</code> to be deleted.
     * @return True if the <code>LessonCategory</code> was deleted, false otherwise.
     */
    @Override
    public boolean delete(LessonCategory category) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "DELETE FROM lesson_categories WHERE lessonCategoryNumber = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, category.getLessonCategoryNumber());
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
     * Gets a <code>LessonCategory</code> from the database.
     * @param categoryNumber The <code>LessonCategory</code> number.
     * @return The <code>LessonCategory</code> at that <code>LessonCategory</code> number.
     */
    @Override
    public LessonCategory get(int categoryNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonCategory category = null;
        
        try {
            String sql = "SELECT * FROM lesson_categories WHERE lessonCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            rs = ps.executeQuery();
            if (rs.first())
                category = MySQLHelper.makeLessonCategoryFromResultSet(rs);
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
        
        return category;
    }

    /**
     * Obtains a <code>Vector</code> of all the <code>LessonCategory</code>s.
     * @return A <code>Vector</code> of all the <code>LessonCategory</code>s.
     */
    @Override
    public Vector<LessonCategory> obtainAll() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<LessonCategory> list = new Vector<LessonCategory>();
        try {
            String sql = "SELECT * FROM lesson_categories ORDER BY displayOrder ASC";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonCategoryFromResultSet(rs));
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
     * Obtains a <code>Vector</code> of all the <code>LessonCategory</code>s by userNumber.
     * @param userNumber The userNumber.
     * @return A <code>Vector</code> of <code>LessonCategory</code>s by userNumber.
     */
    @Override
    public Vector<LessonCategory> obtainViewableBy(int userNumber) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<LessonCategory> list = new Vector<LessonCategory>();

        try {
            String sql = "SELECT * FROM lesson_categories "
                    + "WHERE accessRights = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonCategoryFromResultSet(rs));
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
