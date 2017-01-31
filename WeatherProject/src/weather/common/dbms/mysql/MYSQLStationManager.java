/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSStationManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 *
 * @author Xiang Li(2014)
 */
public class MYSQLStationManager implements DBMSStationManager{

    private MySQLImpl dbms;
    
    public MYSQLStationManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }
    
    @Override
    public Station insertStation(Station station) {
        
       Connection conn = null;
       PreparedStatement ps = null;
       
       try
       {
           String sql = "INSERT INTO forecaster_stations "
                   + "(stationCode, stationName, state) "
                   + "VALUE (?, ?, ?)";
           conn = dbms.getLocalConnection();
           ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

           ps.setString(1, station.getStationId());
           ps.setString(2, station.getStationName());
           ps.setString(3, station.getState());
           
       } catch (SQLException e) {   
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return station;      
    }

    @Override
    public boolean updateStation(Station station) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        
        try
        {
            String sql = "UPDATE forecaster_stations SET "
                        + "stationName = ?, state = ? WHERE stationCode= ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, station.getStationName());
            ps.setString(2, station.getState());
            bSuccess = !ps.execute();

        }catch (SQLException e) {       
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

    @Override
    public boolean deleteStation(Station station) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        
        try
        {
            String sql = "DELETE FROM forecaster_stations WHERE stationCode = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, station.getStationId());
            bSuccess = !ps.execute();
        }catch (SQLException e) {   
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

    @Override
    public Vector<Station> getAllStations() {
        
        ResultSet rs = null;
        Connection conn = null;
        Vector<Station> list = new Vector<Station>();
        try
        {
            String sql = "SELECT * FROM forecaster_stations";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Station station = MySQLHelper.makeStationFromResultSet(rs);
                list.add(station);
            }
        }catch (SQLException e) {
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

    @Override
    public Vector<Station> getAllStationsByState(String helperState) {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Station> list = new Vector<Station>();
        try
        {
            String sql = "{call sp_getStationsByState(?)}";
            conn = dbms.getLocalConnection();
            
            //Added after errors occurred during testing on 8/18/16.
            if (conn == null || conn.isClosed() || !conn.isValid(4)) {
                Debug.println("Connection in getAllStationsByState(...) not valid.");
                conn = dbms.getLocalConnection();
            }
            
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setString(1, helperState);
            rs = ps.executeQuery();
            while(rs.next())
            {
                Station station = MySQLHelper.makeStationFromResultSet(rs);
                list.add(station);
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
    
    
    @Override
    public Station obtainStation(String stationName) {
       Connection conn = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       Station station = null;
       
       try
       {
           String sql = "SELECT * FROM forecaster_stations WHERE stationName = ? OR stationCode = ?";
           conn = dbms.getLocalConnection();
           ps = conn.prepareStatement(sql);
           ps.setString(1, stationName);
           ps.setString(2, stationName);
           rs = ps.executeQuery();
           if(rs.first())
               station = MySQLHelper.makeStationFromResultSet(rs);
       }catch (SQLException e) {  
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return station;
    }

    @Override
    public ArrayList<String> getStates() {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<String> list = new ArrayList<>();
        try
        {
            String sql = "{call sp_getStationStates()}";
            conn = dbms.getLocalConnection();
            PreparedStatement ps = conn.prepareCall(sql);
            rs = ps.executeQuery();
            while(rs.next())
            {
                list.add(rs.getString("state"));
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
