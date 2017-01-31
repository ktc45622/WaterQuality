package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.Property;
import weather.common.dbms.DBMSPropertyManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the weather properties table in the
 * database.
 * @author Colton Daily (2014)
 * @version Spring 2014
 */
public class MYSQLPropertyManager implements DBMSPropertyManager {

    private MySQLImpl dbms;
    
    public MYSQLPropertyManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }
    
    /**
     * Gets the all the general weather properties stored in the database.
     * @return a property file containing all the general weather properties.
     */
    @Override
    public Properties getGeneralProperties() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Properties prop = null;
        
        try {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'general'";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            prop = new Properties();
            
            while(rs.next()) {
                prop.setProperty(rs.getString("propName"), rs.getString("propValue"));
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
        return prop;
    }

     /**
     * Gets all the GUI properties stored in the database.
     * @return a property file containing all the GUI properties.
     */
    @Override
    public Properties getGUIProperties() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Properties prop = null;
        
        try {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'gui'";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            prop = new Properties();
            
            while(rs.next()) {
                prop.setProperty(rs.getString("propName"), rs.getString("propValue"));
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
        return prop;
    }

    /**
     * Gets all the WUnderground-TwoVariable properties stored in the database.
     * @return a property file containing all the WUnderground-TwoVariable
     * properties.
     */
    @Override
    public Properties getWeatherStationTwoVariableProperties() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Properties prop = null;
        
        try {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'wunder_twovar'";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            prop = new Properties();
            
            while(rs.next()) {
                prop.setProperty(rs.getString("propName"), rs.getString("propValue"));
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
        return prop;
    }

    /**
     * Gets all the WUnderground properties stored in the database.
     * @return a property file containing all the WUnderground
     * properties.
     */
    @Override
    public Properties getWeatherStationVariableProperties() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Properties prop = null;
        
        try {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'wunder'";
            conn = dbms.getLocalConnection();
            
            //Added after errors occurred during testing on 7/25/16.
            if (conn == null || conn.isClosed() || !conn.isValid(4)) {
                Debug.println("Connection in getWeatherStationVariableProperties() not valid.");
                conn = dbms.getLocalConnection();
            }
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            prop = new Properties();
            
            while(rs.next()) {
                prop.setProperty(rs.getString("propName"), rs.getString("propValue"));
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
        return prop;
    }

    
    /**
     * Gets all the WUnderground-NoSolar properties stored in the database.
     * @return a property file containing all the WUnderground-NoSolar
     * properties.
     */
    @Override
    public Properties getWeatherStationNoSolarVariableProperties() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Properties prop = null;
        
        try {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'wunder_nosolar'";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            prop = new Properties();
            
            while(rs.next()) {
                prop.setProperty(rs.getString("propName"), rs.getString("propValue"));
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
        return prop;
    }

    /**
     * Gets the all the general weather properties stored in the database
     * and put all the properties into a vector
     * @return a vector that includes all the weather general properties
     */
    @Override
    public Vector<Property> obtainAllGeneralProperties() {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Property> list = new Vector<Property>();
        try
        {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'general' ";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Property property = MySQLHelper.makePropertyFromResultSet(rs);
                list.add(property);
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

    /**
     * Gets all the gui weather properties stored in the database
     * and put all the properties into a vector
     * @return a vector that includes all the gui weather properties
     */
    @Override
    public Vector<Property> obtainAllGUIProperties() {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Property> list = new Vector<Property>();
        try
        {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'gui' ";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Property property = MySQLHelper.makePropertyFromResultSet(rs);
                list.add(property);
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

    /**
     * Gets all the Weather station two variable properties stored in the databse
     * and put all the properties into a vector
     * @return a vector that includes all the weather station variable properties
     */
    @Override
    public Vector<Property> obtainAllWeatherStationTwoVariableProperties() {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Property> list = new Vector<Property>();
        try
        {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'wunder_twovar'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Property property = MySQLHelper.makePropertyFromResultSet(rs);
                list.add(property);
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

    /**
     * Gets all the Weather station variable properties stored in the database 
     * and put all the properties into a vector
     * @return a vector that includes all the weather station variable properties
     */
    @Override
    public Vector<Property> obtainAllWeatherStationVariableProperties() {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Property> list = new Vector<Property>();
        try
        {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'wunder'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Property property = MySQLHelper.makePropertyFromResultSet(rs);
                list.add(property);
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

    /**
     * Gets all the Weather station No Solar properties stored in the database 
     * and put all the properties into a vector
     * @return a vector that includes all the weather station variable properties
     */
    @Override
    public Vector<Property> obtainAllWeatherStationNoSolarVariableProperties() {
         ResultSet rs = null;
        Connection conn = null;
        Vector<Property> list = new Vector<Property>();
        try
        {
            String sql = "SELECT * FROM weather_properties WHERE propType = 'wunder_nosolar'";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Property property = MySQLHelper.makePropertyFromResultSet(rs);
                list.add(property);
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

     /**
     * Remove one property from the database
     * @param property needs to be removed
     * @return true if removed the property successfully
     */
    @Override
    public boolean removeProperty(Property property) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        
        try
        {
            String sql = "DELETE FROM weather_properties WHERE propId = ?;";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, property.getPropertyID());
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

    /**
     * Update the value of one property
     * @param property needs to be updated
     * @return true if update the property successfully
     */
    @Override
    public boolean updateProperty(Property property) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        
        try
        {
            String sql = "UPDATE weather_properties SET "
                        + "propType = ?, propTypeDisplayName = ?, PropName = ?, propDisplayName = ?, "
                        + "propValue = ?, isEditable = ?, notes = ?, defaultValue = ?,"
                        + "previousValue = ? WHERE propId = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, property.getPropertyType());
            ps.setString(2, property.getPropertyTypeDisplayName());
            ps.setString(3, property.getPropertyName());
            ps.setString(4, property.getPropertyDisplayName());
            ps.setString(5, property.getPropertyValue());           
            ps.setByte(6, property.getIsEditable());
            ps.setString(7, property.getNotes());
            ps.setString(8, property.getDefaultValue());
            ps.setString(9, property.getPreviousValue());
            ps.setInt(10, property.getPropertyID());
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

    /**
     * Insert a property into the database
     * @param property needs to be inserted to the databse
     * @return the property
     */
    @Override
    public Property insertProperty(Property property) 
    {
       Connection conn = null;
       PreparedStatement ps = null;
       
       try
       {
           String sql = "INSERT INTO weather_properties "
                   + "(propType, propTypeDisplayName, propName, propDisplayName, propValue,"
                   + " isEditable, notes, defaultValue, previousValue) "
                   + "VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?)";
           conn = dbms.getLocalConnection();
           ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

           ps.setString(1, property.getPropertyType());
           ps.setString(2, property.getPropertyTypeDisplayName());
           ps.setString(3, property.getPropertyName());
           ps.setString(4, property.getPropertyDisplayName());
           ps.setString(5, property.getPropertyValue());
           ps.setByte(6, property.getIsEditable());
           ps.setString(7, property.getNotes());
           ps.setString(8, property.getDefaultValue());
           ps.setString(9, property.getPreviousValue());
           
           property.setPropertyID(MySQLHelper.executeStatementAndReturnGeneratedKey(ps));
           
       } catch (SQLException e) {   
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return property;
    }

    /**
     * Find an property with Prooperty ID
     * @param propertyID
     * @return the proerty
     */
    @Override
    public Property obtainProperty(int propertyID) 
    {
       Connection conn = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       Property property = null;
       
       try
       {
           String sql = "SELECT * FROM weather_properties WHERE propId = ?";
           conn = dbms.getLocalConnection();
           ps = conn.prepareStatement(sql);
           ps.setInt(1, propertyID);
           rs = ps.executeQuery();
           if(rs.first())
               property = MySQLHelper.makePropertyFromResultSet(rs);
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
        return property;
    }

    /**
     * Gets all the properties in the databse
     * @return a vector that includes all the properties
     */
    @Override
    public Vector<Property> obtainAllProperties() 
    {
        ResultSet rs = null;
        Connection conn = null;
        Vector<Property> list = new Vector<Property>();
        try
        {
            String sql = "SELECT * FROM weather_properties";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                Property property = MySQLHelper.makePropertyFromResultSet(rs);
                list.add(property);
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
    
}
