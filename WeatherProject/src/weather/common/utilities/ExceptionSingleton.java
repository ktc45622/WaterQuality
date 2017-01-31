package weather.common.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * The purpose of this class is to ensure that only one instance of possible error
 * messages is stored in memory at once.
 * 
 * This is used in conjunction with WeatherException to generate
 * error exceptions.
 *
 * @author Bloomsburg University Software Engineering
 * @author Dave Moser (2008)
 * @version Spring 2008
 */
public class ExceptionSingleton {
    private static ExceptionSingleton instance = null;
    
    private final static String propertiesFileLocation =
            PropertyManager.getGeneralProperty("ExceptionPropertiesLocation");

    //must be hard coded literal, if the properties can't be loaded, neither can 
    //the default one.
    private final static String defaultErrorMessage = 
     "Weather Project Error";
    private static Properties in;
    
    protected ExceptionSingleton() {
        try {
            in = new Properties();
            in.load(new FileInputStream(new File(propertiesFileLocation)));
        } catch (Exception e) {
          String message =  " Program could not read the file"
                   + propertiesFileLocation;
           WeatherLogger.log(Level.SEVERE, message);
           new WeatherException(WeatherException.GEN_PROP_ERROR,e).show(message);
        }
    }
    /**
     * Used by other classes to get the instance of this object because the only
     * constructor for this class has protected scope to ensure only one instance
     * exists.
     * 
     * @return the current instance of the singleton.
     */
    public static ExceptionSingleton getInstance() {
        if (instance == null) {
            instance = new ExceptionSingleton();
        }
        return instance;
    }
    
    /**
     * Returns the Description of the Exception that was thrown as a string
     * @param exceptionID the id of the exception to retrieve the description of
     * @return the description as to why the exception was thrown as a string,
     * or a default error message if the exceptionID key isn't found.
     */
    public String getExceptionString(int exceptionID){
        // Searches for the property with the specified key in this property
        // list and return the default value argument if the property is not
        // found.
        return in.getProperty(Integer.toString(exceptionID), defaultErrorMessage);
    }
    
}
