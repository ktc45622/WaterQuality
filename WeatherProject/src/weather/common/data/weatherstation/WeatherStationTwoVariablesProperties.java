package weather.common.data.weatherstation;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The properties section may require some explanation. When the system was
 * first designed we used these files with <code>.properties</code> file
 * extensions. We will call these properties files. These properties files
 * contained meta-data in the form of <code> property = value </code> pairs.
 * This is a common practice in Java but when the decision was made to
 * include more weather stations these property files became a problem.
 *
 * The problem is that if an administrator specifies a new resource each
 * client should be able to use that resource. If the resource is a weather
 * station however, we require each client to have a properties file on
 * their local machine. This clashes with the way cameras and maps work.
 * There was talk of implementing a database solution but then we started
 * work on the weather underground stations. The weather underground has a
 * fixed number of properties files (3) that can support all the weather
 * stations hosted there. This class will continue to be used for the
 * foreseeable future.
 *
 * This class is a wrapper for the weather multiple variable properties contained
 * in the BUWeatherStationTwoVariables.properties file.  It provides weather multiple
 * variable specific methods for getting properties for multiple variables.
 *
 * 
 * @author Bloomsburg University Software Engineering
 * @author Fen Qin (2009)
 * @author Xianrui Meng (2010)
 * @version Spring 2010
 * @see weather.common.data.weatherstation.WeatherStationVariableProperties
 */
public class WeatherStationTwoVariablesProperties extends WeatherStationProperties {
    private static final int MAX_DEFAULT_PROPERTIES_TRIES = 1;
    private static final long serialVersionUID = 1L;
    public static final String BU_WEATHER_STATION_PATH = 
            PropertyManager.getGeneralProperty("WEATHER_STATION_PATH") +
            PropertyManager.getGeneralProperty("WUndergroundTwoVariables");
    private static final String DISPLAY_NAME = ".displayName";
    private static final String FIRST_NAME = ".firstName";
    private static final String SECOND_NAME = ".secondName";
    private static final String DESCRIPTION = ".description";
    private static final String UNITS = ".units";
    private static final String FIRSTMIN = ".firstMin";
    private static final String FIRSTMAX = ".firstMax";
    private static final String SECONDMIN = ".secondMin";
    private static final String SECONDMAX = ".secondMax";
    private static final String COLOR = ".color";
    private static final String FIRSTCOLOR = ".firstColor";
    private static final String SECONDCOLOR = ".secondColor";
    private static final String FIRSTTYPE = ".firstType";
    private static final String SECONDTYPE = ".secondType";
    private static final String INDEX = ".index";
    private static final String CONSTANT = "constant.";
    private static final String TEXT_FILE_URL = "weatherstationtextfile.url";
    private static final String FLOAT = ".float";
    private static final String INT = ".int";
    private static final String LONG = ".long";
    private static final String VARIABLE = "common.";
    private static final String USED = ".used";
    private Properties wvProperties;
    private String propertiesFilePath; //Get this out
    private Number DEFAULT_FLOAT;
    private Number DEFAULT_INT;
    private Number DEFAULT_LONG;

    /**
     * Use the given file path for this properties file.
     * @param propertiesFilePath The path to a properly formatted weather variable
     * properties file.
     */
    public WeatherStationTwoVariablesProperties(String propertiesFilePath) {
        initialize(0);
        DEFAULT_FLOAT = getDefaultValue(WeatherStationDataType.FLOAT);
        DEFAULT_INT = getDefaultValue(WeatherStationDataType.INT);
        DEFAULT_LONG = getDefaultValue(WeatherStationDataType.LONG);
    }

    /**
     * This method initializes the properties file. It defaults to the
     * BU_WEATHER_STATION_PATH if the provided propertiesFilePath
     * is no good. It is a helper that ensures that the default will always
     * be used even if the provided path is no good.
     *
     * @param propertiesFilePath The path to the properly formatted weather
     * variable properties file.
     * @param attempts The number of attempts to initialize the properties file.
     */
    private void initialize(int attempts) {
        try {
            wvProperties = getProperties();
        } catch (WeatherException ex) {
            attempts++;
            WeatherLogger.log(Level.SEVERE, "properties file path is incorrect.", ex);
            //this is a recursive call that makes it VERY VERY important that
            //the value we choose for BU_WEATHER_STATION_PATH is appropriate
            //if it is not, I have a counter to ensure that there is no way
            //to have infinite recursion.
            if(attempts > MAX_DEFAULT_PROPERTIES_TRIES) {
                wvProperties = new Properties();
            }
            else initialize(attempts);
        }
    }

    /**
     * Get the URL of the weatherStation text file.
     * @return The URL of the weatherStation text file.
     * @throws weather.common.utilities.WeatherException if the URL is corrupt
     * in the properties file.
     */
    public URL getURLofWeatherStationTextFile() throws WeatherException {
        URL retVal = null;
        try {
            StringBuffer sb = new StringBuffer(CONSTANT);
            sb.append(TEXT_FILE_URL);
            retVal = new URL(wvProperties.getProperty(sb.toString()));
            // property above is specified in LogFile.properties
            /**
             * @TODO -- get a list of each property file and why we have them.
             */
        } catch (MalformedURLException ex) {
            throw new WeatherException(3015, ex);
        } finally {
            return retVal;
        }
    }

    /**
     * Get the variableKey for given two variable names.
     * @param nameOne The name of the first variable.
     * @param nameTwo The name of the second variable.
     * @return The variableKey for the multi-variable.
     */
    public String getVariableKeyForVariables(String nameOne, String nameTwo) {
        Vector<String> orderedCommonKeys = getOrderedCommonKeys();
        String firstName, secondName, currentKey;
        for (int i = 1; i < orderedCommonKeys.size(); ++i) {
            currentKey = orderedCommonKeys.get(i);
            firstName = getFirstName(currentKey);
            secondName = getSecondName(currentKey);
            if ((nameOne.equals(firstName) && nameTwo.equals(secondName))
                    || (nameOne.equals(secondName) && nameTwo.equals(firstName))) {
                return currentKey;
            }
        }
        return "";
    }

    /**
     * Gets the variable keys to index into the properties file in the order
     * specified by the weather station text file URL.
     * @return The ordered list of property keys.
     */
    public Vector<String> getOrderedCommonKeys() {
        Vector<String> orderedCommonKeys = new Vector<String>();
        Vector<String> vTemp = new Vector<String>();
        Set<Object> commonKeys = wvProperties.keySet();
        int start, end;
        for (Object key : commonKeys) {
            StringBuffer sb = new StringBuffer((String) key);
            start = sb.indexOf(VARIABLE) + VARIABLE.length();
            end = sb.lastIndexOf(INDEX);
            if (end != -1) { // why -1??
                vTemp.add(sb.substring(start, end));
            }
        }
        orderedCommonKeys.setSize(vTemp.size());
        for (String key : vTemp) {
            orderedCommonKeys.set(getIndex(key), key);
        }
        return orderedCommonKeys;
    }

    /**
     * Gets the index of the specified variable as represented by a column in
     * the weather station text file URL.
     * @param variableKey The variable of which to get the index.
     * @return The index of the column of the variable in the log URL.
     */
    public int getIndex(String variableKey) {
        return Integer.parseInt(getVariableProperty(variableKey, INDEX));
    }

    /**
     * Get the data type of the specified variable.
     * @param variableKey The variable of which to get the type.
     * @return The type of weather variable specified by the variable key.
     */
    public WeatherStationDataType getFirstType(String variableKey) {
        return WeatherStationDataType.valueOf(getVariableProperty(variableKey, FIRSTTYPE).toUpperCase());
    }

    /**
     * Get the data type of the specified variable.
     * @param variableKey The variable of which to get the type.
     * @return The type of weather variable specified by the variable key.
     */
    public WeatherStationDataType getSecondType(String variableKey) {
        return WeatherStationDataType.valueOf(getVariableProperty(variableKey, SECONDTYPE).toUpperCase());
    }

    /**
     * The string used to represent the variable in the log URL.
     * @param variableKey The variable whose display name is desired.
     * @return The string representation of the specified variable.
     */
    public String getShortName(String variableKey) {
        return getVariableProperty(variableKey, DISPLAY_NAME);
    }

    /**
     * The longer version of short name.
     * @param variableKey The variable whose display name is desired.
     * @return The string representation of the specified variable.
     */
    public String getLongName(String variableKey) {
        return getVariableProperty(variableKey, DESCRIPTION);
    }

    /**
     * The string used to represent the first variable.
     * @param variableKey The variable whose display name is desired.
     * @return The string representation of the first variable.
     */
    public String getFirstName(String variableKey) {
        return getVariableProperty(variableKey, FIRST_NAME);
    }

    /**
     * The string used to represent the second variable.
     * @param variableKey The variable whose display name is desired.
     * @return The string representation of the second variable.
     */
    public String getSecondName(String variableKey) {
        return getVariableProperty(variableKey, SECOND_NAME);
    }

    /**
     * Gets the units of the specified variable.
     * @param variableKey The variable whose units are desired.
     * @return String representation of the units of the specified variable.
     */
    public String getUnits(String variableKey) {
        return getVariableProperty(variableKey, UNITS);
    }

    /**
     * Get the minimum value for the given variable.
     * @param variableKey The variable whose minimum value is desired.
     * @return The minimum value of the specified variable.
     */
    public Number getFirstMinValue(String variableKey) {
        System.err.println("key:"+variableKey);
        if (getFirstType(variableKey) == WeatherStationDataType.INT) {
            return getVariableProperty(variableKey, FIRSTMIN).length() > 0
                    ? Integer.parseInt(getVariableProperty(variableKey, FIRSTMIN))
                    : Integer.MIN_VALUE;
        } else if (getFirstType(variableKey) == WeatherStationDataType.FLOAT) {
            return getVariableProperty(variableKey, FIRSTMIN).length() > 0
                    ? Float.parseFloat(getVariableProperty(variableKey, FIRSTMIN))
                    : -1 * Float.MAX_VALUE;
        } else //LONG or TIME
        {
            return getVariableProperty(variableKey, FIRSTMIN).length() > 0
                    ? Long.parseLong(getVariableProperty(variableKey, FIRSTMIN))
                    : Long.MIN_VALUE;
        }
    }

    /**
     * Get the minimum value for the given variable.
     * @param variableKey The variable whose minimum value is desired.
     * @return The minimum value of the specified variable.
     */
    public Number getSecondMinValue(String variableKey) {
        if (getSecondType(variableKey) == WeatherStationDataType.INT) {
            return getVariableProperty(variableKey, SECONDMIN).length() > 0
                    ? Integer.parseInt(getVariableProperty(variableKey, SECONDMIN))
                    : Integer.MIN_VALUE;
        } else if (getSecondType(variableKey) == WeatherStationDataType.FLOAT) {
            return getVariableProperty(variableKey, SECONDMIN).length() > 0
                    ? Float.parseFloat(getVariableProperty(variableKey, SECONDMIN))
                    : -1 * Float.MAX_VALUE;
        } else //LONG or TIME
        {
            return getVariableProperty(variableKey, SECONDMIN).length() > 0
                    ? Long.parseLong(getVariableProperty(variableKey, SECONDMIN))
                    : Long.MIN_VALUE;
        }
    }

    /**
     * Get the maximum value for the given variable.
     * @param variableKey The variable whose maximum value is desired.
     * @return The maximum value of the specified variable.
     */
    public Number getFirstMaxValue(String variableKey) {
        if (getFirstType(variableKey) == WeatherStationDataType.INT) {
            return getVariableProperty(variableKey, FIRSTMAX).length() > 0
                    ? Integer.parseInt(getVariableProperty(variableKey, FIRSTMAX))
                    : Integer.MAX_VALUE;
        } else if (getFirstType(variableKey) == WeatherStationDataType.FLOAT) {
            return getVariableProperty(variableKey, FIRSTMAX).length() > 0
                    ? Float.parseFloat(getVariableProperty(variableKey, FIRSTMAX))
                    : Float.MAX_VALUE;
        } else {//LONG or TIME
            return getVariableProperty(variableKey, FIRSTMAX).length() > 0
                    ? Long.parseLong(getVariableProperty(variableKey, FIRSTMAX))
                    : Long.MAX_VALUE;
        }
    }

    /**
     * Get the maximum value for the given variable.
     * @param variableKey The variable whose maximum value is desired.
     * @return The maximum value of the specified variable.
     */
    public Number getSecondMaxValue(String variableKey) {
        if (getSecondType(variableKey) == WeatherStationDataType.INT) {
            return getVariableProperty(variableKey, SECONDMAX).length() > 0
                    ? Integer.parseInt(getVariableProperty(variableKey, SECONDMAX))
                    : Integer.MAX_VALUE;
        } else if (getSecondType(variableKey) == WeatherStationDataType.FLOAT) {
            return getVariableProperty(variableKey, SECONDMAX).length() > 0
                    ? Float.parseFloat(getVariableProperty(variableKey, SECONDMAX))
                    : Float.MAX_VALUE;
        } else //LONG or TIME
        {
            return getVariableProperty(variableKey, SECONDMAX).length() > 0
                    ? Long.parseLong(getVariableProperty(variableKey, SECONDMAX))
                    : Long.MAX_VALUE;
        }
    }

    /**
     * Get the color for representation of the specified variable.
     * @param variableKey The variable whose color is desired.
     * @return The color to represent the specified variable.
     */
    public Color getColor(String variableKey) {
        return Color.decode(getVariableProperty(variableKey, COLOR));
    }

    /**
     * Get the color for representation of the first specified variable.
     * @param variableKey The variable whose color is desired.
     * @return The color to represent the first specified variable.
     */
    public Color getFirstColor(String variableKey) {
        return Color.decode(getVariableProperty(variableKey, FIRSTCOLOR));
    }

    /**
     * Get the color for representation of the second specified variable.
     * @param variableKey The variable whose color is desired.
     * @return The color to represent the second specified variable.
     */
    public Color getSecondColor(String variableKey) {
        return Color.decode(getVariableProperty(variableKey, SECONDCOLOR));
    }

    /**
     * Check if the variable is used.
     * @param variableKey The variable to be checked.
     * @return True if the .used is equal to Yes, false otherwise.
     */
    public boolean isUsed(String variableKey) {
        if (getVariableProperty(variableKey, USED).equals("Yes")) {
            return true;
        }
        return false;
    }

    /**
     * Gets the default value for the specified variable type.
     * @param type The weather variable type whose default value is desired.
     * @return The default value for the specified weather variable type.
     */
    private Number getDefaultValue(WeatherStationDataType type) {
        String tempDefaultValue;
        StringBuffer sb = new StringBuffer(CONSTANT);
        //sb.append(DEFAULT_VALUE);
        switch (type) {
            case INT:
                sb.append(INT);
                tempDefaultValue = wvProperties.getProperty(sb.toString());
                return tempDefaultValue == null ? Integer.MIN_VALUE : Integer.parseInt(tempDefaultValue);
            case FLOAT:
                sb.append(FLOAT);
                tempDefaultValue = wvProperties.getProperty(sb.toString());
                return tempDefaultValue == null ? Float.MIN_VALUE : Float.parseFloat(tempDefaultValue);
            case TIME:
            case LONG:
                sb.append(LONG);
                tempDefaultValue = wvProperties.getProperty(sb.toString());
                return tempDefaultValue == null ? Long.MIN_VALUE : Long.parseLong(tempDefaultValue);
            default:
                return null;
        }

    }

    /**
     * Gets the default value for the specified variable.
     * @param variableKey The variable whose default value is desired.
     * @return The default value for the specified variable.
     */
    public Number getDefaultValue(String variableKey) {
        switch (getFirstType(variableKey)) {
            case FLOAT:
                return DEFAULT_FLOAT;
            case INT:
                return DEFAULT_INT;
            case TIME:
            case LONG:
                return DEFAULT_LONG;
            default:
                return null;
        }

    }

    /**
     * Creates and returns an exact copy, or clone, of the object calling this method.
     * @return A clone of the object calling this method.
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        WeatherStationTwoVariablesProperties retVal = new WeatherStationTwoVariablesProperties(propertiesFilePath);
        retVal.DEFAULT_FLOAT = this.DEFAULT_FLOAT;
        retVal.DEFAULT_INT = this.DEFAULT_INT;
        retVal.DEFAULT_LONG = this.DEFAULT_LONG;
        retVal.wvProperties = (Properties) this.wvProperties.clone();
        return retVal;
    }

    /**
     * Returns the Hash Code of the object calling this method.
     * @return The hash code of the object calling this method.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.wvProperties != null ? this.wvProperties.hashCode() : 0);
        hash = 73 * hash + (this.propertiesFilePath != null ? this.propertiesFilePath.hashCode() : 0);
        hash = 73 * hash + (this.DEFAULT_FLOAT != null ? this.DEFAULT_FLOAT.hashCode() : 0);
        hash = 73 * hash + (this.DEFAULT_INT != null ? this.DEFAULT_INT.hashCode() : 0);
        hash = 73 * hash + (this.DEFAULT_LONG != null ? this.DEFAULT_LONG.hashCode() : 0);
        return hash;
    }

    /**
     * Checks to see if two instances of WeatherStationVariableProperties are equal or not.
     *
     * @param o The object to compare the current object to.
     * @return True is the two objects are equal and false otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeatherStationVariableProperties)) {
            return false;
        }
        WeatherStationTwoVariablesProperties other = (WeatherStationTwoVariablesProperties) o;
        return (this.wvProperties == null ? other.wvProperties == null : this.wvProperties.equals(other.wvProperties))
                && (this.propertiesFilePath == null ? other.propertiesFilePath == null : this.propertiesFilePath.equals(other.propertiesFilePath))
                && (this.DEFAULT_FLOAT == null ? other.DEFAULT_FLOAT == null : this.DEFAULT_FLOAT.equals(other.DEFAULT_FLOAT))
                && (this.DEFAULT_INT == null ? other.DEFAULT_INT == null : this.DEFAULT_INT.equals(other.DEFAULT_INT))
                && (this.DEFAULT_LONG == null ? other.DEFAULT_LONG == null : this.DEFAULT_LONG.equals(other.DEFAULT_LONG));
    }

    /**
     * Gets the value of the specified variable and property keys.
     * @param variableKey The variable whose property is desired.
     * @param propertyKey The property desired.
     * @return The value of the specified variable and property key.
     */
    private String getVariableProperty(String variableKey, String propertyKey) {
        StringBuffer sb = new StringBuffer(VARIABLE);
        sb.append(variableKey);
        sb.append(propertyKey);
        return wvProperties.getProperty(sb.toString());
    }

    /**
     * Initializes the property file with the specified path.
     * @param propertiesFilePath The path to the property file.
     * @return An initialized property file.
     * @throws weather.common.utilities.WeatherException if an error occurs when
     * loading the property file.
     */
    private Properties getProperties() throws WeatherException {
        
        Properties properties = new Properties();
        
        try {
            properties = MySQLImpl.getMySQLDMBSSystem().getPropertyManager().getWeatherStationTwoVariableProperties();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(WeatherStationTwoVariablesProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return properties;
    }
}
