package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.diary.DailyEntry;
import weather.common.data.diary.WindDirectionType;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSDiaryManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This manager is for managing daily diary entry database table.
 *
 * @author Bloomsburg University Software Engineering
 * @author Brian Bankes
 */

public class MySQLDiaryManager implements DBMSDiaryManager {
    
    private MySQLImpl dbms;

    /**
     * Constructor.
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLDiaryManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }
    
    /**
     * Saves a diary entry to the database table. The database operation will be
     * un update if an entry is already present for the parameter's user, camera
     * resource and entry date or an update otherwise.
     * 
     * @param entry The diary entry.
     * @param author The author of the entry.
     * @return True if the operation was successful, false otherwise.
     */
    @Override
    public boolean saveEntry(DailyEntry entry, User author) {
        PreparedStatement ps = null;
        Connection conn = null;
        SimpleDateFormat dateFormatter 
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "{call sp_enterNewDiaryData"
                + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            conn = dbms.getLocalConnection();
            
            //Added after errors occurred during testing on 8/5/16.
            if (conn == null || conn.isClosed() || !conn.isValid(4)) {
                Debug.println("Connection in saveEntry(...) not valid.");
                conn = dbms.getLocalConnection();
            }
            
            ps = conn.prepareCall(sql);
            ps.setInt(1, author.getUserNumber());
            ps.setString(2, dateFormatter.format(entry.getDate()));
            ps.setInt(3, entry.getCameraNumber());
            ps.setInt(4, entry.getStationNumber());
            ps.setString(5, dateFormatter.format(entry.getLastModifiedDate()));
            ps.setString(6, entry.getMaxTemp());
            ps.setString(7, entry.getMinTemp());
            ps.setString(8, entry.getTempTrendType().displayString());
            ps.setString(9, entry.getStartBP());
            ps.setString(10, entry.getEndBP());
            ps.setString(11, entry.getBPTrendType().displayString());
            ps.setString(12, entry.getStartDP());
            ps.setString(13, entry.getEndDP());
            ps.setString(14, entry.getDPTrendType().displayString());
            ps.setString(15, entry.getMaxRH());
            ps.setString(16, entry.getMinRH());
            ps.setString(17, entry.getRHTrendType().displayString());
            ps.setString(18, entry.getPrimaryMorningCloudType().displayString());
            ps.setString(19, entry.getSecondaryMorningCloudType().displayString());
            ps.setString(20, entry.getPrimaryAfternoonCloudType().displayString());
            ps.setString(21, entry.getSecondaryAfternoonCloudType().displayString());
            ps.setString(22, entry.getPrimaryNightCloudType().displayString());
            ps.setString(23, entry.getSecondaryNightCloudType().displayString());
            
            //Must assemble wind direction field.
            StringBuilder windDirectionList = new StringBuilder("");
            ArrayList<WindDirectionType> directionList 
                    = entry.getSurfaceAirWindDirections();
            if (directionList != null && !directionList.isEmpty()) {
                for (WindDirectionType type : directionList) {
                    if (windDirectionList.length() > 0) {
                        windDirectionList.append(",");
                    }
                    windDirectionList.append(type.displayString());
                }
            }
            
            ps.setString(24, windDirectionList.toString());
            ps.setString(25, entry.getWindDirectionSummary().displayString());
            ps.setString(26, entry.getWindSpeed().displayString());
            ps.setString(27, entry.getMaxGustSpeed());
            ps.setString(28, entry.getDailyPrecipitation());
            ps.setString(29, entry.getMaxHeatIndex());
            ps.setString(30, entry.getMinWindChill());
            ps.setString(31, entry.getUpperAirWindDirection().displayString());
            
            //Determine if the object's note should be saved.
            UserType userType = author.getUserType();
            if (userType == UserType.student || userType == UserType.guest) {
                ps.setString(32, entry.getNote(dbms, author).getText());
            } else {
                ps.setString(32, "");
            }
            return !ps.execute();
        } catch (SQLException e) {
            Debug.println("SQL Exception thrown is an " + e.toString());
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return false;
    }
    
    /**
     * Gets the diary entry for the given user, entry date, and camera resource.
     * 
     * @param user The given user.
     * @param entryDate The date of the desired entry, which must be the start
     * of the day in the time zone of the given camera resource.
     * @param cameraResource The given camera resource.
     * @return The diary entry for the given user, entry date, and camera
     * resource if it exists; null otherwise.
     */
    @Override
    public DailyEntry getEntry(User user, Date entryDate, 
            Resource cameraResource) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        DailyEntry entry = null;
        
        //Get formatted date string.
        SimpleDateFormat dateFormatter 
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date utilDate = new java.util.Date(entryDate.getTime());
        String formattedDate = dateFormatter.format(utilDate);

        try {
            String sql = "{call sp_getDiaryEntry(?,?,?)}";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getUserNumber());
            ps.setString(2, formattedDate);
            ps.setInt(3, cameraResource.getResourceNumber());
            
            rs = ps.executeQuery();
            while (rs.next()) {
                //Get resource names.
                int cameraNumber = rs.getInt("cameraNumber");
                String cameraName = dbms.getResourceManager().
                        getWeatherResourceByNumber(cameraNumber).getName();
                int stationNumber = rs.getInt("stationNumber");
                String stationName = dbms.getResourceManager().
                        getWeatherResourceByNumber(stationNumber).getName();
                
                entry = MySQLHelper.makeDailyEntryFromResultSet(rs, 
                        cameraName, stationName);
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
        return entry;
    }
    
    /**
     * Get a possibly empty list of all diary entries by the given user.
     * 
     * @param user The given user.
     * @return A possibly empty list of all diary entries by the given user.
     */
    @Override
    public ArrayList<DailyEntry> getAllEntriesByUser(User user) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        ArrayList<DailyEntry> entries = new ArrayList<>();

        try {
            String sql = "{call sp_getDiaryEntriesByUser(?)}";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getUserNumber());
            
            rs = ps.executeQuery();
            while (rs.next()) {
                //Get resource names.
                int cameraNumber = rs.getInt("cameraNumber");
                String cameraName = dbms.getResourceManager().
                        getWeatherResourceByNumber(cameraNumber).getName();
                int stationNumber = rs.getInt("stationNumber");
                String stationName = dbms.getResourceManager().
                        getWeatherResourceByNumber(stationNumber).getName();
                
                entries.add(MySQLHelper.makeDailyEntryFromResultSet(rs, 
                        cameraName, stationName));
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
        return entries;
    }
    
    /**
     * Deletes. the diary entry for the given user, entry date, and camera 
     * resource if one exists.
     * 
     * @param user The given user.
     * @param entryDate The date of the desired entry, which must be the start
     * of the day in the time zone of the given camera resource.
     * @param cameraResource The given camera resource.
     * @return True if the operation was successful, false otherwise.
     */
    @Override
    public boolean deleteEntry(User user, Date entryDate, 
            Resource cameraResource) {
        PreparedStatement ps = null;
        Connection conn = null;
        
        //Get formatted date string.
        SimpleDateFormat dateFormatter 
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date utilDate = new java.util.Date(entryDate.getTime());
        String formattedDate = dateFormatter.format(utilDate);
        
        try {
            String sql = "{call sp_deleteDiaryEntry(?,?,?)}";
            conn = dbms.getLocalConnection();
            ps = conn.prepareCall(sql);
            ps.setInt(1, user.getUserNumber());
            ps.setString(2, formattedDate);
            ps.setInt(3, cameraResource.getResourceNumber());
            return !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return false;
    }
}
