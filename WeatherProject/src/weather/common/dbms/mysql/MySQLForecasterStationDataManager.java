package weather.common.dbms.mysql;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSForecasterStationDataManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages the station data in the database
 * @author jbenscoter
 */
public class MySQLForecasterStationDataManager implements DBMSForecasterStationDataManager{

    /**
     *
     */
    public MySQLImpl dbms;
    
    /**
     * constructs a new station data manager
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterStationDataManager(MySQLImpl dbms)
    {
        this.dbms = dbms;
    }

    @Override
    public Station getStation(String stationCode, Date date) {
        ResultSet rs = null;
        Connection conn = null;
        Station station = null;
        HashMap<String, String> data = null;
        
        try {
            String sql = "{call sp_getStationDataByIdAndDate(?,?)}";
            conn = dbms.getLocalConnection();
            
            if(conn == null || conn.isClosed() || !conn.isValid(4)){
                Debug.println("connection in getStation() is not valid");
                conn = dbms.getLocalConnection();
            }
            else
                Debug.println("connection in getStation() is valid");
            
            
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, stationCode);
            statement.setDate(2, date);
            rs = statement.executeQuery();
            
            while (rs.next()) {
                if(station == null)
                {
                    station = MySQLHelper.makeStationFromResultSet(rs);
                    rs.getString("dataKey");
                    
                    if(!rs.wasNull())
                    {
                        data = new HashMap<>();
                    }
                }
                
                if(data != null)
                {
                    data.put(rs.getString("dataKey"), rs.getString("dataValue"));
                }
            }
            
            if(station != null && data != null)
            {
                station.setData(data);
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
        return station;
    }

    @Override
    public boolean addStationData(String stationCode, Date date, HashMap<String, String> data) {
        
        ResultSet rs = null;
        Connection conn = null;
        try {       
            //Users.Number AS UserNumber is to avoid potentially strange column names from the DB
            String sql = "{call sp_insertStationData(?,?,?,?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.getConnection().setAutoCommit(false);
            ps.setString(1,stationCode);
            ps.setDate(2, date);
            
            for(int i = 0; i < data.size(); i++)
            {
                String key = data.keySet().toArray()[i].toString();
                ps.setString(3, key);
                ps.setString(4, data.get(key));
                ps.execute();
            }
            
            ps.getConnection().commit();
            ps.getConnection().setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return false;
    }
    
}
