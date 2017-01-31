package weather.common.dbms.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * 
 * A Singleton that maintains the MySQL Properties of the WeatherProject.
 * 
 * Never use the constructor for this Object, always use the <code>getInstance()</code> method.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Chad Hall (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class MySQLPropertiesSingleton {

    private static MySQLPropertiesSingleton instance = null;
    private final static String propertiesFilePath = "config" + File.separator+ "database" +File.separator+"MySQLProperties.xml";
    private static Properties in;

    /**
     * The MySQLPropertiesSingleton constructor: <b>never use!</b>.
     * Catches <code>FileNotFoundException</code> if <code>propertiesFilePath</code>
     * not found. Logs this exception in the log file, displays the message to
     * a user and terminates this program.
     * Catches <code>IOException</code> if <code>propertiesFilePath</code> is
     * corrupt. Logs this exception in the log file, displays the message to
     * a user and terminates this program.
     */
    protected MySQLPropertiesSingleton() {
        try {
            in = new Properties();
            in.loadFromXML(new FileInputStream(new File(propertiesFilePath)));
        } catch (FileNotFoundException e) {
            WeatherLogger.log(Level.SEVERE, "FileNotFoundException is thrown while "
                    + "trying to locate the database property file.", e);
            new WeatherException(4010, true, e, "We cannot find the file " + propertiesFilePath
                    + " .\n  This program needs to terminate.").show();
        } catch (IOException e2) {
            WeatherLogger.log(Level.SEVERE, "The database property file contains invalid information. "
                    + " Unable to connect to the database. ", e2);
            new WeatherException(4011, true, e2, "The file " + propertiesFilePath
                    + " is corrupt.\n This program needs to terminate.").show();
        }
    }

    /**
     * Returns an instance of the <code>MySQLPropertiesSingleton</code> class.
     *
     * @return An instance of the <code>MySQLPropertiesSingleton</code> class.
     */
    public static MySQLPropertiesSingleton getInstance() {
        if (instance == null) {
            instance = new MySQLPropertiesSingleton();
        }
        return instance;
    }

    /**
     * Returns the CreateResourcesTable query as a String.
     * @return The CreateResourcesTable query as a String.
     */
    public String getCreateResourcesTable() {
        return in.getProperty("CreateResources");
    }

    /**
     * Returns the CreateUsersTable query as a String.
     * @return The CreateUsersTable query as a String.
     */
    public String getCreateUsersTable() {
        return in.getProperty("CreateUsers");
    }

    /**
     * Returns the Database password as a String.
     * @return The database password.
     */
    public String getDatabasePassword() {
        
        File file = new File("config" + File.separator+ "database" +File.separator+ "DoNotDeleteFile.txt");
        SecretKey key = DESEncryptor.readKey(file);
        return DESEncryptor.decrypt(key, in.getProperty("DatabasePassword"));

        //return "project";
    }

    /**
     * Returns the local database server as a String.
     * @return The local database server as a String.
     */
    public String getLocalDatabaseServer() {
        
        File file = new File("config" + File.separator+ "database" +File.separator+"DoNotDeleteFile.txt");
        SecretKey key = DESEncryptor.readKey(file);
        // use this for connecting to the test database
        // make sure to comment out the name and password to match yours
        // and for the love of all that is good, don't push up your password
        // and name :)
      //  return "jdbc:mysql://148.137.66.202/wptest";
       // return DESEncryptor.decrypt(key, in.getProperty("DatabaseServer"));
      
        //weather project 2 has an ip of 148.137.66.197
        return "jdbc:mysql://148.137.9.29/weatherproject";
        //return "jdbc:mysql://www.atlanticdividegames.com/weatherproject?SendStringParametersAsUnicode=false";
    }
    
    /**
     * Returns the BU database server as a String.
     * @return The BU database server as a String.
     */
    public String getBUDatabaseServer() {
        //TODO: Change to permanent connection string.
        return "jdbc:mysql://148.137.9.29/wp_client_downloaded_versions";
    }

    /**
     * Returns the Database Username as a String.
     * @return The Database Username as a String.
     */
    public String getDatabaseUsername() {
        File file = new File("config" + File.separator+ "database" +File.separator+"DoNotDeleteFile.txt");
        SecretKey key = DESEncryptor.readKey(file);
        return DESEncryptor.decrypt(key, in.getProperty("DatabaseUsername"));
  
      //return "weather";
    }
    
    public boolean setDatabaseUserName(String userName)
    {
        File file = new File("config" + File.separator+ "database" +File.separator+"DoNotDeleteFile.txt");
        SecretKey key = DESEncryptor.readKey(file);
        String encryptedUserName = DESEncryptor.encrypt(key, userName);
        
        in.setProperty("DatabaseUsername", encryptedUserName);
        
        return true;
    }
    
    public boolean setDatabasePassword(String password)
    {
        File file = new File("config" + File.separator+ "database" +File.separator+"DoNotDeleteFile.txt");
        SecretKey key = DESEncryptor.readKey(file);
        String encryptedPassword = DESEncryptor.encrypt(key, password);
        
        in.setProperty("DatabasePassword", encryptedPassword);
        
        return true;
    }
    
    public boolean setDatabaseURL(String url)
    {
        File file = new File("config" + File.separator+ "database" +File.separator+"DoNotDeleteFile.txt");
        SecretKey key = DESEncryptor.readKey(file);
        String encryptedServer = DESEncryptor.encrypt(key, url);
        
        in.setProperty("DatabaseServer", encryptedServer);
        
        return true;
    }
    
    public boolean saveProperties()
    {
        try {
            in.storeToXML(new FileOutputStream(new File(propertiesFilePath)), propertiesFilePath);
            return true;
        } catch (FileNotFoundException e) {
            WeatherLogger.log(Level.SEVERE, "FileNotFoundException is thrown while "
                    + "trying to locate the database property file.", e);
            new WeatherException(4010, true, e, "We cannot find the file " + propertiesFilePath
                    + " .\n  This program needs to terminate.").show();
        } catch (IOException e2) {
            WeatherLogger.log(Level.SEVERE, "The database property file contains invalid information. "
                    + " Unable to connect to the database. ", e2);
            new WeatherException(4011, true, e2, "The file " + propertiesFilePath
                    + " is corrupt.\n This program needs to terminate.").show();
        }
        return false;
    }

    /**
     * Returns the default users as a string.
     * @return The default users as a string.
     */
    public String getDefaultUsers() {
        return in.getProperty("DefaultUsers");
    }
}
