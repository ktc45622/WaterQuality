package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.MissingWebGradingDataRecord;
import weather.common.dbms.DBMSMissingDataRecordManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages the <code>MissingWebGradingDataRecord</code> objects in 
 * the database.
 * @see MissingWebGradingDataRecord
 * @author Brian Bankes
 */
public class MySQLMissingDataRecordManager implements DBMSMissingDataRecordManager{
    
    private MySQLImpl dbms;

    /**
     * Constructor.
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLMissingDataRecordManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Inserts an <code>MissingWebGradingDataRecord</code> object in the 
     * database.
     * @param record The <code>MissingWebGradingDataRecord</code> object to 
     * insert.
     * @return The <code>MissingWebGradingDataRecord</code> object with the id 
     * filled out or null is the response could not be inserted.
     */
    @Override
    public MissingWebGradingDataRecord 
            insertRecord(MissingWebGradingDataRecord record) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_insertMissingDataEntry(?,?,?,?,?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            statement.setString(1, record.getLessonID());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(2, dateFormatter.format(record.gatDate()));
            statement.setString(3, record.getStationCode());
            statement.setBoolean(4, record.getIsInstructorDataSet());
            statement.setBoolean(5, record.getWasEmailSent());

            rs = statement.executeQuery();
            MissingWebGradingDataRecord newRecord = null;

            while (rs.next()) {
                newRecord = MySQLHelper.makeMissingWebGradingDataRecordFromResultSet(rs);
            }

            return newRecord;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return null;
    }

    /**
     * Updates a <code>MissingWebGradingDataRecord</code> object in the
     * database.
     * @param record The <code>MissingWebGradingDataRecor</code> object to
     * update.
     * @return True if the update was successful; False otherwise.
     */
    @Override
    public boolean updateRecord(MissingWebGradingDataRecord record) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "{call sp_updateMissingDataEntry(?,?,?)}";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, record.getRecordID());
            ps.setBoolean(2, record.getIsInstructorDataSet());
            ps.setBoolean(3, record.getWasEmailSent());
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
     * Gets the <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code>, <code>Date</code>, and weather station
     * code.  An object will always be returned.  If this combination is in the
     * database, it will be the database record.  If not, an object holding an
     * unsaved record will be returned.
     * @param lesson The given <code>ForecasterLesson</code>.
     * @param date The given <code>Date</code>.
     * @param stationCode The code of the station for which records are 
     * being sought.
     * @return The <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code>, <code>Date</code>, and weather station
     * code.  An object will always be returned.  If this combination is in the
     * database, it will be the database record.  If not, an object holding an
     * unsaved record will be returned.
     */
    @Override
    public MissingWebGradingDataRecord 
            getRecordByLessonAndDateAndStation(ForecasterLesson lesson, 
            Date date, String stationCode) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_getMissingDataEntryByLessonAndDateAndStation(?,?,?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            statement.setString(1, lesson.getLessonID());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(2, dateFormatter.format(date));
            statement.setString(3, stationCode);

            rs = statement.executeQuery();
            MissingWebGradingDataRecord newRecord;

            //Return database record if pressent.
            if (rs.next()) {
                newRecord = MySQLHelper.makeMissingWebGradingDataRecordFromResultSet(rs);
            } else {
                newRecord = new MissingWebGradingDataRecord(null, 
                        lesson.getLessonID(), date, stationCode, false, false);
            }
            return newRecord;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        
        //Final return in case exception is thrown.
        return new MissingWebGradingDataRecord(null, lesson.getLessonID(), date, 
                stationCode, false, false);
    }
    
    /**
     * Gets all of the saved instances of
     * <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code> in a <code>ArrayList</code>.
     * @param lesson The given <code>ForecasterLesson</code>.
     * @return All of the saved instances of
     * <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code> in a <code>ArrayList</code>.
     */
    @Override
    public ArrayList<MissingWebGradingDataRecord> 
            getAllRecordsForLesson(ForecasterLesson lesson) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<MissingWebGradingDataRecord> records = new ArrayList<>();

        try {
            String sql = "{call sp_getMissingDataEntriesForLesson(?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, lesson.getLessonID());
            
            rs = statement.executeQuery();
            while (rs.next()) {
                records.add(MySQLHelper
                        .makeMissingWebGradingDataRecordFromResultSet(rs));
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
        return records;
    }
}
