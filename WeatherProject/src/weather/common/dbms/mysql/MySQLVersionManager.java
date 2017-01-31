package weather.common.dbms.mysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import weather.common.data.version.Version;
import weather.common.dbms.DBMSVersionManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class allows new version data to be uploaded to the Internet. This is 
 * done by inserting an instance of the <code>Version</code> class into a 
 * database that is accessible to all copies of the program. A method is also 
 * specified to retrieve the most recent version data as a <code>Version</code> 
 * object.
 *
 * @see Version
 * @author Brian Bankes
 */
public class MySQLVersionManager implements DBMSVersionManager{
    
    private final MySQLImpl dbms;

    /**
     * Constructor.
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLVersionManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Inserts an <code>Version</code> object in the database.
     * @param version The <code>Version</code> object to insert.
     * @return True if the version data was successfully inserted; 
     * False otherwise.
     */
    @Override
    public boolean insertVersion(Version version) {
        ResultSet rs = null;
        Connection conn;
        try {
            String sql = "{call sp_insertVersion(?,?,?,?,?)}";
            conn = dbms.getBUConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            statement.setInt(1, version.getMajorVersion());
            statement.setInt(2, version.getMinorVersion());
            statement.setInt(3, version.getMinorRelease());
            statement.setString(4, version.getVersionNotes());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(5, dateFormatter.format(version.getReleaseDate()));

            rs = statement.executeQuery();
            return true;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return false;
    }

    /**
     * Retrieves the most recent version data stored in the database.
     * @return A <code>Version</code> object holding the most recent version 
     * data stored in the database. 
     */
    @Override
    public Version getMostResentVersion() {
        ResultSet rs = null;
        Connection conn;
        ArrayList<Version> allVersions = new ArrayList<>();

        try {
            String sql = "{call sp_getAllVersions()}";
            conn = dbms.getBUConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            
            rs = statement.executeQuery();
            while (rs.next()) {
                allVersions.add(MySQLHelper.makeVersionFromResultSet(rs));
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
        Collections.sort(allVersions);
        //Return newest version.
        int newestIndex = allVersions.size() - 1;
        return allVersions.get(newestIndex);
    }
    
    /**
     * Gets a sorted list of all versions currently in the database.
     * @return An <code>ArrayList</code> holding all instances of 
     * <code>Version</code> currently in the database sorted from oldest to 
     * newest. 
     */
    @Override
    public ArrayList<Version> getAllVersions() {
        ResultSet rs = null;
        Connection conn;
        ArrayList<Version> allVersions = new ArrayList<>();

        try {
            String sql = "{call sp_getAllVersions()}";
            conn = dbms.getBUConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            
            rs = statement.executeQuery();
            while (rs.next()) {
                allVersions.add(MySQLHelper.makeVersionFromResultSet(rs));
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
        Collections.sort(allVersions);
        return allVersions;
    }
    
    /**
     * Updates the given <code>Version</code> in the database.  The notes field
     * is all that can be updated.
     * @param version The given <code>Version</code>.
     * @return True if the version data was successfully updated; 
     * False otherwise.
     */
    @Override
    public boolean updateVersionNotes(Version version) {
        ResultSet rs = null;
        Connection conn;
        try {
            String sql = "{call sp_updateVersionNotes(?,?,?,?)}";
            conn = dbms.getBUConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            statement.setInt(1, version.getMajorVersion());
            statement.setInt(2, version.getMinorVersion());
            statement.setInt(3, version.getMinorRelease());
            statement.setString(4, version.getVersionNotes());

            rs = statement.executeQuery();
            return true;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return false;
    }
    
    /**
     * Gets the <code>Version</code> in the database that matches the given
     * <code>String</code> or null if none exists.
     * @param versionString The given <code>String</code>.
     * @return The <code>Version</code> in the database that matches the given
     * <code>String</code> or null if none exists.
     */
    @Override
    public Version getVersionFromString(String versionString) {
        ResultSet rs = null;
        Connection conn;
        ArrayList<Version> allVersions = new ArrayList<>();

        try {
            String sql = "{call sp_getAllVersions()}";
            conn = dbms.getBUConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            
            rs = statement.executeQuery();
            while (rs.next()) {
                allVersions.add(MySQLHelper.makeVersionFromResultSet(rs));
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
        
        //Find requested version.
        for (Version version : allVersions) {
            if (version.toString().equals(versionString)) {
                return version;
            }
        }
        
        //The version was not found.
        return null;
    }
}
