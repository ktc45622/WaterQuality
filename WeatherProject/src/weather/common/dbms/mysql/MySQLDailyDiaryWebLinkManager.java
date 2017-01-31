/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.common.dbms.mysql;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.diary.DailyDiaryWebLinks;
import weather.common.dbms.DBMSDailyDiaryWebLinkManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
/**
 * The <code>MySQLDailyDiaryWebLinkManager</code> class interacts with the dailyDiaryWebLinks table.
 * It allows to maintain web links in the database. An instance of this class
 * can be created and used in the following way:
 * <pre>
 *  DBMSSystem dbms;
 *  dbms = adminService.getGeneralService().getDBMSSystem();
 *  webLinkManager = new MySQLDailyDiaryWebLinkManager((MySQLImpl)dbms);
 *  webLinkManager.deleteLink(urlName.getText());
 * </pre>
 *
 * @author Alinson Antony
 * @version Spring 2012
  */

public class MySQLDailyDiaryWebLinkManager implements DBMSDailyDiaryWebLinkManager{
    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLDailyDiaryWebLinkManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }
    /**
     * Gets all webLinks from the database.
     * @return A vector of all webLinks.
     */
    @Override
    public Vector<DailyDiaryWebLinks> getLinks() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<DailyDiaryWebLinks> categories = new Vector<DailyDiaryWebLinks>();
        try {
            String sql = "SELECT * FROM daily_diary_weblinks ORDER BY linkNumber ASC";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                categories.add(MySQLHelper.makeDailyDiaryWebLinkFromResultSet(rs));
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
        return categories;
    }
    /**
     * Deletes a link with the given name from the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param name The name of the link to delete.
     * @return True if a link with the given URL was removed from the database,
     * false otherwise.
     */
   /* @Override
    public boolean deleteLink(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        try {
            String sql = "DELETE FROM daily_diary_weblinks WHERE name = (?)";
            conn = dbms.makeConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name.trim());
            // if the update was successfull, test whether any rows were affected,
            // if not, nothing was deleted, return false
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
            MySQLHelper.closeConnection(conn); // this looks wrong to me
        }
        return isSuccessfull;
    }*/
    /**
     * Updates a given DailyDiaryWebLink link that matches the links name.
     * @param link The link to update.
     * @return True if updated, false if not.
     */
    @Override
    public boolean updateLink(DailyDiaryWebLinks link) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        try {
            String sql = "UPDATE daily_diary_weblinks SET   URL = (?) WHERE linkName = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, link.getURLString().trim());
            ps.setString(2, link.getName().trim());
           
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 1) {
                    isSuccessful = true;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return isSuccessful;
    }
/**
 * Add new entry to the database.
 * @param name Name of the DailyDiaryWeblink to be added.
 * @param url Name of the DailyDiaryWeblink to be added.
 * @return  True if updated, false if not.
 */
  /*  @Override
    public boolean addLink(String name,String url) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        try {
            conn = dbms.makeConnection();
            String sql = "INSERT INTO daily_diary_weblinks (name, URL) "
                    + "VALUES ( (?), (?))";
            conn = dbms.makeConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, url);
            if (ps.executeUpdate() == 1) {
                isSuccessfull = true;
            }

        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
            MySQLHelper.closeConnection(conn);
        }
        return isSuccessfull;
        
    }*/

}
