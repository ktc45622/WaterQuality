package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.AccessRights;
import weather.common.data.lesson.LessonEntry;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the lesson_entry table in the database.
 * 
 * @author Nicole Burfeind
 * @version Spring 2012
 */
public class MySQLLessonEntryManager implements DBMSLessonEntryManager {

    private MySQLImpl dbms;
    
    public MySQLLessonEntryManager(MySQLImpl dbms){
        this.dbms = dbms;
    }
    
    /**
     * Adds a <code>LessonEntry</code> to the database.
     * @param entry The <code>LessonEntry</code> to add.
     * @return True if the <code>LessonEntry</code> was added, false otherwise.
     */
    public boolean add(LessonEntry entry) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        
        try {
            String sql = "INSERT INTO lesson_entry ( lessonNumber, lessonEntryName, "
                    + "bookmarkNumber, bookmarkResourceIdentifier, windowPosition)"
                    + "VALUES (?, ?, ?, ?, ?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

            ps.setInt(1, entry.getLessonNumber());
            ps.setString(2, entry.getLessonEntryName());
            ps.setInt(3, entry.getBookmarkNumber());
            ps.setString(4, entry.getBookmarkResourceID().toString());
            ps.setInt(5, entry.getPreferredWindowLocation());
            entry.setLessonEntryNumber(MySQLHelper.executeStatementAndReturnGeneratedKey(ps));
            if (entry.getLessonEntryNumber() > 0)
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
     * Updates a <code>LessonEntry</code> in the database.
     * @param entry The <code>LessonEntry</code> to be updated.
     * @return True if the <code>LessonEntry</code> was updated, false otherwise.
     */
    @Override
    public boolean update(LessonEntry entry) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "UPDATE lesson_entry SET " +
                    "lessonEntryNumber = ?, lessonNumber = ?, lessonEntryName = ?, bookmarkNumber = ?,"+
                    "bookmarkResourceIdentifier = ?, windowPosition = ? "
                    + "WHERE lessonEntryNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, entry.getLessonEntryNumber());
            ps.setInt(2, entry.getLessonNumber());
            ps.setString(3, entry.getLessonEntryName());
            ps.setInt(4, entry.getBookmarkNumber());
            ps.setString(5, entry.getBookmarkResourceID().toString());
            ps.setInt(6, entry.getPreferredWindowLocation());
            ps.setInt(7, entry.getLessonEntryNumber());
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
     * Deletes a <code>LessonEntry</code> from the database.
     * @param entry The <code>LessonEntry</code> to be deleted.
     * @return True if the <code>LessonEntry</code> was deleted, false otherwise.
     */
    @Override
    public boolean delete(LessonEntry entry) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "DELETE FROM lesson_entry WHERE lessonEntryNumber = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, entry.getLessonEntryNumber());
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
     * Gets a <code>LessonEntry</code> from the database by lessonEntryNumber.
     * @param lessonEntryNumber The <code>LessonEntry</code> number.
     * @return A <code>LessonEntry</code> found at that number.
     */
    @Override
    public LessonEntry get(int lessonEntryNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonEntry entry = null;
        
        try {
            String sql = "SELECT * FROM lesson_entry WHERE lessonEntryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonEntryNumber);
            rs = ps.executeQuery();
            if (rs.first())
                entry = MySQLHelper.makeLessonEntryFromResultSet(rs);
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
        
        return entry;
    }

    /**
     * Gets a <code>Vector</code> of <code>LessonEntry</code> objects.
     * @return A <code>Vector</code> of <code>LessonEntry</code> objects.
     */
    @Override
    public Vector<LessonEntry> obtainAll() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<LessonEntry> list = new Vector<LessonEntry>();
        try {
            String sql = "SELECT * FROM lesson_entry";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonEntryFromResultSet(rs));
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
     * Gets a <code>Vector</code> of <code>LessonEntry</code> objects by lessonNumber
     * @param lessonNumber The <code>Lesson</code> number.
     * @return A <code>Vector</code> of <code>LessonEntry</code> objects by lessonNumber.
     */
    @Override
    public Vector<LessonEntry> obtainByLessonNumber(int lessonNumber){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<LessonEntry> list = new Vector<LessonEntry>();
        try {
            String sql = "SELECT * FROM lesson_entry WHERE lessonNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonEntryFromResultSet(rs));
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

    @Override
    public Vector<LessonEntry> obtainByUser(int userNumber) {
        throw new UnsupportedOperationException("Lesson Entries do not have a user in the table.");
    }

    @Override
    public Vector<LessonEntry> obtainByCategory(int categoryNumber) {
        throw new UnsupportedOperationException("Lesson Entries do not have Categories.");
    }

    @Override
    public Vector<LessonEntry> obtainByAccessRights(AccessRights access) {
        throw new UnsupportedOperationException("Lesson Entries do not have access rights.");
    }
    
}
