/*
 * Includes various database managing 
 */
package database;

import common.DataValue;
import common.User;
import common.UserRole;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import security.SecurityCode;

/**
 *
 * @author Tyler Mutzek
 */
public class DatabaseManager 
{
    
    /*
        Creates the data value table
        entryID is the unique id number of the data value
        dataName is the name of the data type (e.g. Temperature)
        units is the units associated with the data value
        sensor is the name of the sensor that recorded the data value
        timeRecorded is a the time the data was recorded
        dataValue is the value of the of data recorded
        delta is the difference between this data value and the last
    */
    public static void createDataValueTable()    
    {
        Statement createTable = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            createTable = conn.createStatement();
            String createSQL = "Create Table IF NOT EXISTS DataValues("
                    + "entryID INT primary key AUTO_INCREMENT,"
                    + "dataName varchar(40),"
                    + "units varchar(10),"
                    + "sensor varchar(20),"
                    + "timeRecorded varchar(25),"
                    + "dataValue FLOAT(3),"
                    + "delta FLOAT(8)"
                    + ");";
            createTable.execute(createSQL);
        }
        catch (Exception ex)//SQLException ex 
        {
            LogError("Error creating Data Value Table: " + ex);
        }
        finally
        {
            try
            {
                if(createTable != null)
                    createTable.close();
            }
            catch(SQLException e)
            {
                LogError("Error closing statement:" + e);
            }
        }
    }
    
    /*
        Creates the data description table
        dataName is the data type of the data value (e.g. Temperature)
        description is the description of this data type
    */
    public static void createDataDescriptionTable()    
    {
        Statement createTable = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            createTable = conn.createStatement();
            String createSQL = "Create Table IF NOT EXISTS DataDescriptions("
                    + "dataName varchar(40) primary key,"
                    + "description varchar(500)"
                    + ");";
            createTable.executeUpdate(createSQL);
        }
        catch (Exception ex)//SQLException ex 
        {
            LogError("Error creating Data Description Table: " + ex);
        }
        finally
        {
            try
            {
                if(createTable != null)
                    createTable.close();
            }
            catch(SQLException e)
            {
                LogError("Error closing statement: " + e);
            }
        }
    }
    
    /*
        Creates the user table
        userNumber is the unique id number for the user
        password is encrypted with SHA256
        locked is whether this user is locked or not
        AttemptedLoginCount is the number of recent failed logins
        The rest are self explanitory
    */
    public static void createUserTable()
    {
        Statement createTable = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            createTable = conn.createStatement();
            String createSQL = "Create Table IF NOT EXISTS users("
                    + "userNumber INT primary key AUTO_INCREMENT,"
                    + "loginName varchar(15),"
                    + "password varchar(64),"
                    + "firstName varchar(15),"
                    + "lastName varchar(15),"
                    + "emailAddress varchar(50),"
                    + "userRole varchar(20),"
                    + "lastLoginTime varchar(25),"
                    + "loginCount INT,"
                    + "salt varchar(30),"
                    + "LastAttemptedLoginTime varchar(25),"
                    + "locked boolean,"
                    + "AttemptedLoginCount INT"
                    + ");";
            createTable.executeUpdate(createSQL);
        }
        catch (Exception ex)//SQLException ex 
        {
            LogError("Error creating Users Table: " + ex);
        }
        finally
        {
            try
            {
                if(createTable != null)
                    createTable.close();
            }
            catch(SQLException e)
            {
                LogError("Error closing statement: " + e);
            }
        }
    }
    
    /*
        Creates a table to log the errors that may occur
        Time occured is the time the error happened
        Error is the error message
    */
    public static void createErrorLogsTable()    
    {
        Statement createTable = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            createTable = conn.createStatement();
            String createSQL = "Create Table IF NOT EXISTS ErrorLogs("
                    + "timeOccured varchar(25) primary key,"
                    + "error varchar(100)"
                    + ");";
            createTable.execute(createSQL);
        }
        catch (Exception ex)//SQLException ex 
        {
            LogError("Error creating Error Logs Table: " + ex);
        }
        finally
        {
            try
            {
                if(createTable != null)
                    createTable.close();
            }
            catch(SQLException e)
            {
                LogError("Error closing statement: " + e);
            }
        }
    }
    
    /*
        Allows an admin to insert data into the data values table
        @param name the name of the data type
        @param units the units of this data type
        @param time the time this piece of data was retrieved
        @param value the value of this piece of data
        @param delta the difference between this data value and the last
        @param u the user who entered this data value
        @return whether this function was successful or not
    */
    public static boolean manualInput(String name, String units, LocalDateTime time, float value, float delta, int id, User u)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement insertData = null;
        try
        {
            conn.setAutoCommit(false);
            String insertSQL = "INSERT INTO DataValues (dataName,units,sensor,timeRecorded,dataValue,delta) "
                    + "values(?,?,?,?,?,?)";
            String sensor = u.getFirstName()+u.getLastName();
            if(sensor.length() > 20)
                sensor = sensor.substring(0, 20);
            
            //removes special characters as prepared statement will replace them with ?
            if(units.equals("℃"))
                units = "C";
            units = units.replace("μ","u");
            
            insertData = conn.prepareStatement(insertSQL);
            insertData.setString(1, name);
            insertData.setString(2, units);
            insertData.setString(3, sensor);
            insertData.setString(4, time+"");
            insertData.setFloat(5, value);
            insertData.setFloat(6, delta);
            insertData.setInt(7, id);
            insertData.executeUpdate();
            conn.commit();
            status = true;
        }
        catch (Exception ex)//SQLException ex 
        {
            status = false;
            LogError("Error Manualing Inserting Data: " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(insertData != null)
                    insertData.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
        return status;
    }
    
    /*
        Allows an admin to delete data from the data values table
        @param entryID the id of the data to be deleted
        @param u the user doing the deletion
        @return whether this function was successful or not
    */
    public static boolean manualDeletion(int entryID, User u)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement deleteData = null;
        try
        {
            //throws an error if a user without proper roles somehow invokes this function
            if(u.getUserRole() != common.UserRole.SystemAdmin)
                throw new Exception("Attempted Data Deletion by Non-Admin");
            conn.setAutoCommit(false);
            String deleteSQL = "Delete from DataValues where entryID = ?";
                
            deleteData = conn.prepareStatement(deleteSQL);
            deleteData.setInt(1, entryID);
            deleteData.executeUpdate();
            conn.commit();
            status = true;
        }
        catch (Exception ex)//SQLException ex 
        {
            status = false;
            LogError("Error Manualing Deleting Data: " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(deleteData != null)
                    deleteData.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
        return status;
    }
    
    /*
        Deletes user with parameter user number
        @param userID the user number of the user being deleted
        @param u the user who is doing the deletion
        @return whether this function was successful or not
    */
    public static boolean deleteUser(int userID, User u)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement deleteUser = null;
        try
        {
            //throws an error if a user without proper roles somehow invokes this function
            if(u.getUserRole() != common.UserRole.SystemAdmin)
                throw new Exception("Attempted User Deletion by Non-Admin");
            if(userID == u.getUserNumber())
                throw new Exception("User Attempting to delete self");
            conn.setAutoCommit(false);
            String deleteSQL = "Delete from users where userNumber = ?";
                
            deleteUser = conn.prepareStatement(deleteSQL);
            deleteUser.setInt(1, userID);
            deleteUser.executeUpdate();
            conn.commit();
            status = true;
        }
        catch (Exception ex)//SQLException ex 
        {
            status = false;
            LogError("Error Manualing Deleting User: " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(deleteUser != null)
                    deleteUser.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
        return status;
    }
    
    /*
        Returns a list of data within a certain time range
        @param name the name of the data type for which data is being requested
        @param lower the lower range of the time
        @param upper the upper range of the time
    */
    public static ArrayList<DataValue> getGraphData(String name, LocalDateTime lower, LocalDateTime upper)
    {
        ArrayList<DataValue> graphData = new ArrayList<>();
        PreparedStatement selectData = null;
        ResultSet dataRange = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            String query = "Select * from DataValues Where dataName = ?"
                + " AND timeRecorded >= ? AND timeRecorded <= ?;";
            selectData = conn.prepareStatement(query);
            selectData.setString(1, name);
            selectData.setString(2, lower+"");
            selectData.setString(3, upper+"");
            dataRange = selectData.executeQuery();
            
            int entryID;
            String units;
            LocalDateTime time;
            float value;
            float delta;
            String sensor;
            while(dataRange.next())
            {
                entryID = dataRange.getInt(1);
                name = dataRange.getString(2);
                units = dataRange.getString(3);
                sensor = dataRange.getString(4);
                time = LocalDateTime.parse(dataRange.getString(5));
                value = dataRange.getFloat(6);
                delta = dataRange.getFloat(7);
                DataValue dV = new DataValue(entryID,name,units,sensor,time,value,delta);
                graphData.add(dV);
            }
        }
        catch (Exception ex)//SQLException ex 
        {
            LogError("Error Retrieving Graph Data: " + ex);
        }
        finally
        {
            try
            {
                if(selectData != null)
                    selectData.close();
                if(dataRange != null)
                    dataRange.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or result set: " + excep);
            }
        }
        return graphData;
    }
    
    /*
        Adds a new user to the user table
        Encrypts the password via SHA256 encryption with salt before storing
        Last login and attempted login are initiallized to now
        @return whether this function was successful or not
    */
    public static boolean addNewUser(String username, String password, String firstName,
            String lastName, String email, UserRole userRole, User u)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement insertUser = null;
        try
        {
            //throws an error if a user without proper roles somehow invokes this function
            if(u.getUserRole() != common.UserRole.SystemAdmin)
                throw new Exception("Attempted Data Deletion by Non-Admin");
            
            conn.setAutoCommit(false);
            String insertSQL = "INSERT INTO users (loginName,password,firstName,lastName,"
                    + "emailAddress,userRole,lastLoginTime,loginCount,salt,"
                    + "LastAttemptedLoginTime,locked,AttemptedLoginCount)"
                    + " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            String salt = "Brandon";
            password = SecurityCode.encryptSHA256(password + salt);
            
            insertUser = conn.prepareStatement(insertSQL);
            insertUser.setString(1, username);
            insertUser.setString(2, password);
            insertUser.setString(3, firstName);
            insertUser.setString(4, lastName);
            insertUser.setString(5, email);
            insertUser.setString(6, userRole.getRoleName());
            insertUser.setString(7, LocalDateTime.now() + "");//last login time
            insertUser.setInt(8, 0);//login count
            insertUser.setString(9, salt);
            insertUser.setString(10, LocalDateTime.now() + "");//last attempted login time
            insertUser.setBoolean(11, false);
            insertUser.setInt(12, 0);//login attempted count
            insertUser.executeUpdate();
            conn.commit();
            status = true;
        }
        catch (Exception ex)//SQLException ex 
        {
            status = false;
            LogError("Error Adding New User: " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(insertUser != null)
                    insertUser.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
        return status;
    }
    
    /*
        locks the user with the parameter userID
        @param userID the ID of the user being locked
        @param u the user doing the locking
        @return whether this function was successful or not
    */
    public static boolean lockUser(int userID, User u)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement lockUser = null;
        try
        {
            //throws an error if a user without proper roles somehow invokes this function
            if(u.getUserRole() != common.UserRole.SystemAdmin)
                throw new Exception("Attempted User Lock by Non-Admin");
            
            conn.setAutoCommit(false);
            String modifySQL = "UPDATE users "
                    + "SET locked = 1 "
                    + "WHERE userNumber = ?;";
          
            
            lockUser = conn.prepareStatement(modifySQL);
            lockUser.setInt(1, userID);
            lockUser.executeUpdate();
            conn.commit();
            status = true;
        }
        catch (Exception ex)//SQLException ex 
        {
            status = false;
            LogError("Error Locking User #" + userID + ": " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(lockUser != null)
                    lockUser.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection:" + excep);
            }
        }
        return status;
    }
    
    /*
        Unlocks the user with the parameter userID
        @param userID the ID of the user being unlocked
        @param u the user doing the unlocking
        @return whether this function was successful or not
    */
    public static boolean unlockUser(int userID, User u)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement unlockUser = null;
        try
        {
            //throws an error if a user without proper roles somehow invokes this function
            if(u.getUserRole() != common.UserRole.SystemAdmin)
                throw new Exception("Attempted User Unlock by Non-Admin");
            
            conn.setAutoCommit(false);
            String modifySQL = "UPDATE users "
                    + "SET locked = 0 "
                    + "WHERE userNumber = ?;";
          
            
            unlockUser = conn.prepareStatement(modifySQL);
            unlockUser.setInt(1, userID);
            unlockUser.executeUpdate();
            conn.commit();
            status = true;
        }
        catch (Exception ex)//SQLException ex 
        {
            status = false;
            LogError("Error Unlocking User #" + userID + ": " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(unlockUser != null)
                    unlockUser.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
        return status;
    }
    
    /*
        Inserts a JSONObject into the data table
        Made primarily for pulling data from netronix and inputing it into our
        own tables
    
        @param j a json object containing data for the data value table
    */
    public static void insertJSON(JSONObject j)
    {
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement insertData = null;
        try
        {
            conn.setAutoCommit(false);
            String insertSQL = "INSERT INTO DataValues (dataName,units,sensor,timeRecorded,dataValue,delta) "
                    + "values (?,?,?,?,?,?)";
            
            //removes special characters as prepared statement will replace them with ?
            String units = (String)j.get("unit");
            if(units.equals("℃"))
                units = "C";
            units = units.replace("μ","u");
            
            insertData = conn.prepareStatement(insertSQL);
            insertData.setString(1, (String)j.get("name"));
            insertData.setString(2, units);
            insertData.setString(3, (String)j.get("sensor_name"));
            insertData.setString(4, ((String)j.get("timestamp")).substring(0,19));
            insertData.setFloat(5, (float)(double)j.get("value"));
            insertData.setFloat(6, (float)(double)j.get("delta"));
            insertData.executeUpdate();
            conn.commit();
        }
        catch (Exception ex)//SQLException ex 
        {
            LogError("Error with JSON Insertion: " + ex);
            if(conn!=null)
            {
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
            }
        }
        finally
        {
            try
            {
                if(insertData != null)
                    insertData.close();
                if(conn != null)
                    conn.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
    }
    
    /*
        Gets the user with the parameter login name
        @return the user with this login name (null if none was found)
    */
    public static User getUserByLoginName(String username) 
    {
        User u = null;
        
        PreparedStatement getUserByLogin = null;
        ResultSet selectedUser = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            String getSQL = "SELECT * FROM users WHERE loginName = ?;";
            getUserByLogin = conn.prepareStatement(getSQL);
            getUserByLogin.setString(1, username);
            selectedUser = getUserByLogin.executeQuery();
            selectedUser.next();
            u = new User(
                selectedUser.getInt("userNumber"),
                selectedUser.getString("loginName"),
                selectedUser.getString("password"),
                selectedUser.getString("salt"),
                selectedUser.getString("lastName"),
                selectedUser.getString("firstName"),
                selectedUser.getString("emailAddress"),
                UserRole.getUserRole(selectedUser.getString("userRole")),
                LocalDateTime.parse(selectedUser.getString("lastLoginTime")),
                LocalDateTime.parse(selectedUser.getString("lastAttemptedLoginTime")),
                selectedUser.getInt("loginCount"),
                selectedUser.getInt("attemptedLoginCount"),
                selectedUser.getBoolean("locked")
                );
        }
        catch(Exception e)
        {
            LogError("Error retrieving user with login name \"" + username + "\": "+ e);
        }
        finally
        {
            try
            {
                if(getUserByLogin != null)
                    getUserByLogin.close(); 
                if(selectedUser != null)
                    selectedUser.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or result set: " + excep);
            }
        }
        
        return u;
    }

    /*
        Returns the user info if the username and password are correct
        @return a user with these specs, or null if either are wrong
    */
    public static User validateUser(String username, String password) 
    {
        User u = null;
        
        PreparedStatement selectUser = null;
        ResultSet validatee = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            password = security.SecurityCode.encryptSHA256(password+getSaltByLoginName(username));
            String getSQL = "SELECT * FROM users WHERE loginName = ? and password = ?;";
            selectUser = conn.prepareStatement(getSQL);
            selectUser.setString(1, username);
            selectUser.setString(2, password);
            validatee = selectUser.executeQuery();
            validatee.next();
            u = new User(
                validatee.getInt("userNumber"),
                validatee.getString("loginName"),
                validatee.getString("password"),
                validatee.getString("salt"),
                validatee.getString("lastName"),
                validatee.getString("firstName"),
                validatee.getString("emailAddress"),
                UserRole.getUserRole(validatee.getString("userRole")),
                LocalDateTime.parse(validatee.getString("lastLoginTime")),
                LocalDateTime.parse(validatee.getString("lastAttemptedLoginTime")),
                validatee.getInt("loginCount"),
                validatee.getInt("attemptedLoginCount"),
                validatee.getBoolean("locked")
                );
        }
        catch(Exception e)
        {
            LogError("Error validating user \"" + username + "\": " + e);
        }
        finally
        {
            try
            {
                if(selectUser != null)
                    selectUser.close();
                if(validatee != null)
                    validatee.close();
            }
            catch(SQLException excep)
            {
                LogError("Error closing statement or result set: " + excep);
            }
        }
        
        return u;
    }

    /*
        Updates the user's loginCount, attemptedLoginCount, lastLoginTime
        and lastAttemptedLoginTime
    */
    public static void updateUserLogin(User potentialUser) 
    {
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement updateUser = null;
        try
        {
            conn.setAutoCommit(false);
            String updateSQL = "UPDATE users "
                    + "SET lastLoginTime = ?,"
                    + "lastAttemptedLoginTime = ?,"
                    + "loginCount = ?,"
                    + "attemptedLoginCount = ? "
                    + "WHERE userNumber = ?;";
            updateUser = conn.prepareStatement(updateSQL);
            updateUser.setString(1, potentialUser.getLastLoginTime().toString());
            updateUser.setString(2, potentialUser.getLastAttemptedLoginTime().toString());
            updateUser.setInt(3, potentialUser.getLoginCount());
            updateUser.setInt(4, potentialUser.getAttemptedLoginCount());
            updateUser.setInt(5, potentialUser.getUserNumber());
            updateUser.executeUpdate();
            conn.commit();
        }
        catch(Exception e)
        {
            LogError("Error updating user login: " + e);
            if(conn != null)
                try
                {
                    conn.rollback();
                }
            catch(SQLException excep)
            {
                LogError("Rollback unsuccessful: " + excep);
            }
        }
        finally
        {
            try
            {
                if(updateUser != null)
                    updateUser.close();
                if(conn != null)
                    conn.close();
            }
            catch(Exception excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
    }
    
    /*
        Updates the description with dataName 'name' using the description 'desc'
        @return whether this operation was sucessful or not
    */
    public static boolean updateDescription(String desc, String name)
    {
        boolean status;
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement updateDesc = null;
        try
        {
            conn.setAutoCommit(false);
            String updateSQL = "UPDATE DataDescriptions "
                    + "SET description = ? "
                    + "WHERE dataName = ?;";
            updateDesc = conn.prepareStatement(updateSQL);
            updateDesc.setString(1, desc);
            updateDesc.setString(2, name);
            updateDesc.executeUpdate();
            conn.commit();
            status = true;
        }
        catch(Exception e)
        {
            status = false;
            LogError("Error updating description for \"" + name + "\": " + e);
            if(conn != null)
                try
                {
                    conn.rollback();
                }
                catch(SQLException excep)
                {
                    LogError("Rollback unsuccessful: " + excep);
                }
        }
        finally
        {
            try
            {
                if(updateDesc != null)
                    updateDesc.close();
                if(conn != null)
                    conn.close();
            }
            catch(Exception excep)
            {
                LogError("Error closing statement or connection:" + excep);
            }
        }
        return status;
    }
    
    /*
        Retrieves the description for the parameter data name
        @param name the name of the data type being requested
    */
    public static String getDescription(String name)
    {
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement getDesc = null;
        ResultSet selectedDesc = null;
        String desc = null;
        try
        {
            String getSQL = "SELECT * FROM DataDescriptions WHERE dataName = ?";
            getDesc = conn.prepareStatement(getSQL);
            getDesc.setString(1, name);
            selectedDesc = getDesc.executeQuery();
            selectedDesc.next();
            desc = selectedDesc.getString("description");
        }
        catch(Exception e)
        {
            LogError("Error retrieving description for \"" + name + "\": " + e);
        }
        finally
        {
            try
            {
                if(getDesc != null)
                    getDesc.close();
                if(selectedDesc != null)
                    selectedDesc.close();
                if(conn != null)
                    conn.close();
            }
            catch(Exception excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
        return desc;
    }
    
    /*
        Inserts a description into the DataDescriptions table
        @param name the name of the data type that is getting a description
        @param the description (limit 500 characters)
    */
    public static void insertDescription(String name, String desc)
    {
        Connection conn = Web_MYSQL_Helper.getConnection();
        PreparedStatement insertDesc = null;
        try
        {
            String insertSQL = "INSERT INTO DataDescriptions values(?, ?)";
            insertDesc = conn.prepareStatement(insertSQL);
            insertDesc.setString(1, name);
            insertDesc.setString(2, desc);
            insertDesc.executeUpdate();
        }
        catch(Exception e)
        {
            LogError("Error inserting description for \"" + name + "\": " + e);
        }
        finally
        {
            try
            {
                if(insertDesc != null)
                    insertDesc.close();
                if(conn != null)
                    conn.close();
            }
            catch(Exception excep)
            {
                LogError("Error closing statement or connection: " + excep);
            }
        }
    }

    /*
        Retrieves the salt of the user with the parameter login name
    */
    public static String getSaltByLoginName(String loginName) 
    {
        PreparedStatement getUserByLogin = null;
        ResultSet selectedUser = null;
        String salt = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            String getSQL = "SELECT * FROM users WHERE loginName = ?;";
            getUserByLogin = conn.prepareStatement(getSQL);
            getUserByLogin.setString(1, loginName);
            selectedUser = getUserByLogin.executeQuery();
            selectedUser.next();
            salt = selectedUser.getString("salt");
        }
        catch(SQLException e)
        {
            LogError("Error retrieving salt by login name for \"" + loginName + "\": " + e);
        }
        finally
        {
            try
            {
                if(getUserByLogin != null)
                    getUserByLogin.close();
                if(selectedUser != null)
                    selectedUser.close();
            }
            catch(Exception excep)
            {
                LogError("Error closing statement or result set: " + excep);
            }
        }
        
        return salt;
    }
    
    public static void LogError(String errorMessage)
    {
        PreparedStatement logError = null;
        try(Connection conn = Web_MYSQL_Helper.getConnection();)
        {
            String getSQL = "INSERT INTO ErrorLogs values (?,?)";
            logError = conn.prepareStatement(getSQL);
            logError.setString(1, LocalDateTime.now().toString());
            logError.setString(2, errorMessage);
            logError.executeUpdate();
        }
        catch(SQLException e)
        {
            //LogError("Error inserting error:\n" + e);
        }
        finally
        {
            try
            {
                if(logError != null)
                    logError.close();
            }
            catch(Exception excep)
            {
                //LogError("Error closing statement or result set:\n" + excep);
            }
        }
    }
    
    public static void main(String[] args)
    {
        DatabaseManager.createDataValueTable();
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader("P:/Compsci480/environet_api_data.json"));
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray jarray = (JSONArray)jsonObject.get("data");
            Iterator<JSONObject> iterator = jarray.iterator();
            while(iterator.hasNext())
                DatabaseManager.insertJSON(iterator.next());
        }
        catch(Exception e)
        {}

        /*
        LocalDateTime l = LocalDateTime.parse("2017-02-06T04:15:00");
        LocalDateTime u = LocalDateTime.parse("2017-02-08T04:15:00");
        ArrayList<DataValue> a = d.getGraphData("Temperature", l, u);
        for(DataValue data: a)
        {
            System.out.println(data);
        }
        */
    }
}