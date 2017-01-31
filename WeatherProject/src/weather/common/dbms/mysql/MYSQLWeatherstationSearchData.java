package weather.common.dbms.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import weather.common.data.weatherstation.WeatherStationDailyAverage;
import weather.common.dbms.DBMSWeatherStationSearchDataManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class interact with database table weather station search data
 *
 * @author Alinson Antony(2012)
 */
public class MYSQLWeatherstationSearchData implements DBMSWeatherStationSearchDataManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;
  

    public MYSQLWeatherstationSearchData(MySQLImpl dbms) {
        this.dbms = dbms;
       
        
    }

    public Collection<Double> getData(int resourceNumber, String variableKey, Date startRange, Date endRange) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Collection<Double> data = new ArrayList<Double>();
        try {

            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM weatherstation_data.station_");
            sqlBuilder.append(resourceNumber);
            sqlBuilder.append("_averages ");
            sqlBuilder.append("WHERE  date BETWEEN ? AND ?");

            String sql = sqlBuilder.toString();

            ps = conn.prepareStatement(sql);
            ps.setDate(1, startRange);
            ps.setDate(2, endRange);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                data.add(rs.getDouble(variableKey));
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
        return data;
    }
     public Collection<WeatherStationDailyAverage> getAllValues(int resourceNumber, Date startRange, Date endRange) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Collection<WeatherStationDailyAverage> result = new ArrayList<WeatherStationDailyAverage>();
       Calendar calendar= Calendar.getInstance();
       try {

            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM weatherstation_data.station_");
            sqlBuilder.append(resourceNumber);
            sqlBuilder.append("_averages ");
            sqlBuilder.append("WHERE  resourceNumber =? AND date BETWEEN ? AND ?" );

            String sql = sqlBuilder.toString();

            ps = conn.prepareStatement(sql);
            ps.setInt(1, resourceNumber);
            ps.setDate(2, startRange);
            ps.setDate(3, endRange);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                 WeatherStationDailyAverage data = null;
                data.setResourceNumber(rs.getInt(1));
                calendar.setTimeInMillis(rs.getTimestamp(2).getTime());
                data.setDate(calendar);
                data.setTemperatureMin(rs.getDouble(3));
                data.setTemperatureMax(rs.getDouble(4));
                data.setTemperatureMedian(rs.getDouble(5));
                data.setDewPointMin(rs.getDouble(6));
                data.setDewPointMax(rs.getDouble(7));
                data.setDewPointMedian(rs.getDouble(8));
                data.setRelativeHumidityMin(rs.getDouble(9));
                data.setRelativeHumidityMax(rs.getDouble(10));
                data.setRelativeHumidityMedian(rs.getDouble(11));
                data.setPressureMin(rs.getDouble(12));
                data.setPressureMax(rs.getDouble(13));
                data.setPressureMedian(rs.getDouble(14));
                data.setWindSpeedMin(rs.getDouble(15));
                data.setWindSpeedMax(rs.getDouble(16));
                data.setWindSpeedMedian(rs.getDouble(17));
                data.setSolarRadiationMin(rs.getDouble(18));
                data.setSolarRadiationMax(rs.getDouble(19));
                data.setSolarRadiationMedian(rs.getDouble(20));
                data.setHourlyPrecipitationMin(rs.getDouble(21));
                data.setHourlyPrecipitationMax(rs.getDouble(22));
                data.setHourlyPrecipitationMedian(rs.getDouble(23));
                data.setWindgustMin(rs.getDouble(24));
                data.setWindgustMax(rs.getDouble(25));
                data.setWindgustMedian(rs.getDouble(26));
                data.setDailyPrecipitation(rs.getDouble(27));
                
                result.add(data);
                
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
        return result;
    }
    
    /**
     * Inserts all variables contained in the WeatherStationDailyAverage object into the
     * database for a given Resource for a day.
     * @param resourceDailyAverage The <code>WeatherStationDailyAverage</code> containing the values.
     * @return True if successful, false otherwise.
     */
    public boolean insertData(WeatherStationDailyAverage resourceDailyAverage) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean isSuccess = false;
        
        try {
            StringBuilder sqlBuilder = new StringBuilder("INSERT INTO weatherstation_data.station_");
            sqlBuilder.append(resourceDailyAverage.getResourceNumber());
            sqlBuilder.append("_averages VALUES(");
            for (int i = 0; i < 26; i++)
                sqlBuilder.append("?, ");
            sqlBuilder.append("?)");
            String sql = sqlBuilder.toString();
            
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
           
            ps.setInt(1, resourceDailyAverage.getResourceNumber());
            ps.setTimestamp(2, new Timestamp(resourceDailyAverage.getDate().getTimeInMillis()));
            
            ps.setDouble(3, resourceDailyAverage.getTemperatureMin());
            ps.setDouble(4, resourceDailyAverage.getTemperatureMax());
            ps.setDouble(5, resourceDailyAverage.getTemperatureMedian());
            
            ps.setDouble(6, resourceDailyAverage.getDewPointMin());
            ps.setDouble(7, resourceDailyAverage.getDewPointMax());
            ps.setDouble(8, resourceDailyAverage.getDewPointMedian());
            
            ps.setDouble(9, resourceDailyAverage.getRelativeHumidityMin());
            ps.setDouble(10, resourceDailyAverage.getRelativeHumidityMax());
            ps.setDouble(11, resourceDailyAverage.getRelativeHumidityMedian());
            
            ps.setDouble(12, resourceDailyAverage.getPressureMin());
            ps.setDouble(13, resourceDailyAverage.getPressureMax());
            ps.setDouble(14, resourceDailyAverage.getPressureMedian());
            
            ps.setDouble(15, resourceDailyAverage.getWindSpeedMin());
            ps.setDouble(16, resourceDailyAverage.getWindSpeedMax());
            ps.setDouble(17, resourceDailyAverage.getWindSpeedMedian());
            
            ps.setDouble(18, resourceDailyAverage.getSolarRadiationMin());
            ps.setDouble(19, resourceDailyAverage.getSolarRadiationMax());
            ps.setDouble(20, resourceDailyAverage.getSolarRadiationMedian());
            
            ps.setDouble(21, resourceDailyAverage.getHourlyPrecipitationMin());
            ps.setDouble(22, resourceDailyAverage.getHourlyPrecipitationMax());
            ps.setDouble(23, resourceDailyAverage.getHourlyPrecipitationMedian());
            
            ps.setDouble(24, resourceDailyAverage.getWindgustMin());
            ps.setDouble(25, resourceDailyAverage.getWindgustMax());
            ps.setDouble(26, resourceDailyAverage.getWindgustMedian());
            
            ps.setDouble(27, resourceDailyAverage.getDailyPrecipitation());
           

            if (!ps.execute()) {
                if (ps.getUpdateCount() == 1) {
                    isSuccess = true;
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
        return isSuccess;
    }
}
