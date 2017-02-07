/*
 * Includes various database managing 
 */
package waterquality;

import java.io.IOException;
import beans.DataValue;
import database.Web_MYSQL_Helper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author Tyler Mutzek
 */
public class DatabaseManager {

    public void createDataValueTable()    
    {
        Web_MYSQL_Helper sql = new Web_MYSQL_Helper();
        try(Connection conn = sql.getConnection();)
        {
            Statement s = conn.createStatement();
            String createTable = "Create Table DataValues("
                    + "entryID number primary key AUTO_INCREMENT,"
                    + "name varchar,"
                    + "units varchar,"
                    + "sensor varchar"
                    + "time Timestamp"
                    + "value number"
                    + ");";
            s.executeQuery(createTable);
            s.close();
        }
        catch (Exception ex)//SQLException ex 
        {
            System.out.println("Error processing request: Create Data Value Table");
        }
        
    }
    
    public void manualInput(String name, String units, Timestamp time, float value)
    {
        Web_MYSQL_Helper sql = new Web_MYSQL_Helper();
        try(Connection conn = sql.getConnection();)
        {
            String insertSQL = "INSERT INTO WaterData vales(?,?,?,?,?,?)";
            String sensor = "Manual Entry";
                
            PreparedStatement p = conn.prepareStatement(insertSQL);
            p.setString(1, name);
            p.setString(2, units);
            p.setString(3, sensor);
            p.setString(4, time+"");
            p.setString(5, value+"");
            p.executeUpdate();
            p.close();
        }
        catch (Exception ex)//SQLException ex 
        {
            System.out.println("Error processing request: Manual Data Insertion");
        }
    }
    
    public ArrayList<DataValue> getGraphData(String name, Timestamp lower, Timestamp upper, String sensor)
    {
        ArrayList<DataValue> graphData = new ArrayList<>();
        Web_MYSQL_Helper sql = new Web_MYSQL_Helper();
        try(Connection conn = sql.getConnection();)
        {
            String query = "Select * from WaterData Where name = ?"
                + " AND time >= ? AND time <= ? AND sensor = ?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, name);
            p.setString(2, lower+"");
            p.setString(3, upper+"");
            p.setString(4, sensor);
            ResultSet rs = p.executeQuery();
                
            
            int entryID;
            String units;
            Timestamp time;
            float value;
            while(!rs.isAfterLast())
            {
                entryID = rs.getInt(1);
                name = rs.getString(2);
                units = rs.getString(3);
                sensor = rs.getString(4);
                time = rs.getTimestamp(5);
                value = rs.getFloat(6);
                DataValue dV = new DataValue(entryID,name,units,sensor,time,value);
                graphData.add(dV);
                    
                rs.next();
            }
            rs.close();
            p.close();
        }
        catch (Exception ex)//SQLException ex 
        {
            System.out.println("Error processing request: Retrieve Graph Data");
        }
        return graphData;
    }
    
    public void sensorDataInput(String name, String units, String sensor, Timestamp time, float value)
    {
        Web_MYSQL_Helper sql = new Web_MYSQL_Helper();
        try(Connection conn = sql.getConnection();)
        {
            String insertSQL = "INSERT INTO WaterData vales(?,?,?,?,?,?)";
                
            PreparedStatement p = conn.prepareStatement(insertSQL);
            p.setString(1, name);
            p.setString(2, units);
            p.setString(3, sensor);
            p.setString(4, time+"");
            p.setString(5, value+"");
            p.executeUpdate();
            p.close();
        }
        catch (Exception ex)//SQLException ex 
        {
            System.out.println("Error processing request: Sensor Data Insertion");
        }
    }
            
}
