package weather.common.data.weatherstation;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.WeatherLogger;

/**
 *
 * The properties section may require some explanation. When the system was
 * first designed we used these files with <code>.properties</code> file
 * extensions. We will call these properties files. These properties files
 * contained meta-data in the form of <code> property = value </code> pairs.
 * This is a common practice in Java but when the decision was made to include
 * more weather stations these property files became a problem.
 *
 * The problem is that if an administrator specifies a new resource each client
 * should be able to use that resource. If the resource is a weather station
 * however, we require each client to have a properties file on their local
 * machine. This clashes with the way cameras and maps work. There was talk of
 * implementing a database solution but then we started work on the weather
 * underground stations. The weather underground has a fixed number of
 * properties files (3) that can support all the weather stations hosted there.
 * This class will continue to be used for the foreseeable future.
 *
 * This class is a wrapper for the weather variable properties contained in the
 * BUWeatherStationInputFormat.properties file. It provides weather variable
 * specific methods for getting properties for a specific variable.
 *
 * @author Bloomsburg University Software Engineering
 * @author Steve Rhein (2008)
 * @version Spring 2008
 * @see weather.common.data.weatherstation.WeatherStationTwoVariablesProperties
 */
public class WeatherStationVariableProperties extends WeatherStationProperties {

    private static final long serialVersionUID = 1L;
    private static final int MAX_DEFAULT_PROPERTIES_TRIES = 3;
    //   public static final String DEFAULT_PROPERTIES_FILE_PATH
    //         = PropertyManager.getGeneralProperty("WEATHER_STATION_PATH")
    //        + PropertyManager.getGeneralProperty("WUnderground");
    public static final String DEFUALT_PROPERTIES_FILE_PATH = "wunder";
    private static final String DISPLAY_NAME = ".displayName";
    private static final String DESCRIPTION = ".description";
    private static final String UNITS = ".units";
    private static final String MIN = ".min";
    private static final String MAX = ".max";
    private static final String COLOR = ".color";
    private static final String TYPE = ".type";
    private static final String INDEX = ".index";
    private static final String CONSTANT = "constant.";
    private static final String TEXT_FILE_URL = "weatherstationtextfile.url";
    private static final String FLOAT = ".float";
    private static final String INT = ".int";
    private static final String LONG = ".long";
    private static final String VARIABLE = "variable.";
    private static final String COMMON = ".common";
    private static final String USED = ".used";
    private static final String SPACING = ".spacing";
    private static final String ORDERING = ".ordering";
    private Properties wvProperties;
    private String propertiesFilePath;
    private String propFile;
    private Number DEFAULT_FLOAT;
    private Number DEFAULT_INT;
    private Number DEFAULT_LONG;

    /**
     * TODO:This constructor should locate the 'input format' from the storage
     * system.
     *
     * TODO: This class may be old and might need to be replaced by
     * WeatherStationPropertiesNew
     *
     */
    public WeatherStationVariableProperties() {
        initialize();
        //initialize to default property path
        propFile = DEFUALT_PROPERTIES_FILE_PATH;
        DEFAULT_FLOAT = getDefaultValue(WeatherStationDataType.FLOAT);
        DEFAULT_INT = getDefaultValue(WeatherStationDataType.INT);
        DEFAULT_LONG = getDefaultValue(WeatherStationDataType.LONG);
    }

    /**
     * Creates the property file for the Weather Stations which are grabbed from
     * the database.
     *
     * @param propFile the property type to be loaded ('wunder' or
     * 'wunder_nosolar').
     */
    public WeatherStationVariableProperties(String propFile) {
        this.propFile = propFile;
        initialize();
        DEFAULT_FLOAT = getDefaultValue(WeatherStationDataType.FLOAT);
        DEFAULT_INT = getDefaultValue(WeatherStationDataType.INT);
        DEFAULT_LONG = getDefaultValue(WeatherStationDataType.LONG);
    }

    /**
     * This method initializes the properties file. It defaults to the
     * <code>DEFAULT_PROPERTIES_FILE_PATH</code> if the provided
     * propertiesFilePath is no good. It is a helper that ensures that the
     * default will always be used even if the provided path is no good.
     *
     * @param propertiesFilePath The path to the properly formatted weather
     * variable properties file.
     * @param attempts This parameter is used to specify how many attempts have
     * occurred at getting the <code>DEFAULT_PROPERTIES_FILE_PATH</code> to
     * load. This should always be passed as a zero by default.
     */
    private void initializeRecursive(int attempts) {
        try {
            wvProperties = getProperties(propFile);
        } catch (FileNotFoundException ex) {
            attempts++;
            WeatherLogger.log(Level.SEVERE, "properties file path is incorrect.", ex);
            //this is a recursive call that makes it VERY VERY important that
            //the value we choose for DEFAULT_PROPERTIES_FILE_PATH is appropriate
            //if it is not, I have a counter to ensure that there is no way
            //to have infinite recursion.
            if (attempts > MAX_DEFAULT_PROPERTIES_TRIES) {
                wvProperties = new Properties();
            } else {
                initializeRecursive(attempts);
            }
        } catch (IOException e) {
            attempts++;
            WeatherLogger.log(Level.SEVERE, "properties file path is incorrect.", e);
            //this is a recursive call that makes it VERY VERY important that
            //the value we choose for DEFAULT_PROPERTIES_FILE_PATH is appropriate
            //if it is not, I have a counter to ensure that there is no way
            //to have infinite recursion.
            if (attempts > MAX_DEFAULT_PROPERTIES_TRIES) {
                wvProperties = new Properties();
            } else {
                initializeRecursive(attempts);
            }
        } //sorry for code duplication here but it would be a waste of a method
        //to make a helper for this purpose. 
    }

    /**
     * This method initializes the properties file. It defaults to the
     * <code>DEFAULT_PROPERTIES_FILE_PATH</code> if the provided
     * propertiesFilePath is no good. It is a helper that ensures that the
     * default will always be used even if the provided path is no good. This
     * method is a wrapper for the other private method initializeRecursive
     * because of the recursive behavior there is an optional parameter that
     * will default to zero as passed by this method.
     *
     * @param propertiesFilePath The path to the properly formatted weather
     * variable properties file.
     */
    private void initialize() {
        initializeRecursive(0);
    }

    /**
     * Return the URL of the log file.
     *
     * @return The URL of the log file.
     * @throws weather.common.utilities.WeatherException If the URL is corrupt
     * in the properties file.
     */
    public URL getURLofWeatherStationTextFile() throws MalformedURLException {
        URL retVal;
        StringBuffer sb = new StringBuffer(CONSTANT);
        sb.append(TEXT_FILE_URL);
        retVal = new URL(wvProperties.getProperty(sb.toString()));
        return retVal;
    }

    /**
     * Gets the variable keys to index into the properties file in the order
     * specified by the weatherstationtextfile url.
     *
     * @return The ordered list of property keys.
     */
    public Vector<String> getOrderedVariableKeys() {
        Vector<String> orderedVariableKeys = new Vector<String>();
        Vector<String> vTemp = new Vector<String>();
        Set<Object> variableKeys = wvProperties.keySet();
        int start, end;
        StringBuffer sb;
        for (Object key : variableKeys) {
            sb = new StringBuffer((String) key);
            start = sb.indexOf(VARIABLE) + VARIABLE.length();
            end = sb.lastIndexOf(INDEX);
            if (end != -1) {
                vTemp.add(sb.substring(start, end));
            }
        }
        orderedVariableKeys.setSize(vTemp.size());
        for (String key : vTemp) {
            orderedVariableKeys.set(getIndex(key), key);
        }
        return orderedVariableKeys;
    }

    /**
     * Gets the index of the specified variable as represented by a column in
     * the log URL.
     *
     * @param variableKey The variable of which to get the index.
     * @return The index of the column of the variable in the log url.
     */
    public int getIndex(String variableKey) {
        return Integer.parseInt(getVariableProperty(variableKey, INDEX));
    }

    /**
     * Gets the data type of the specified variable.
     *
     * @param variableKey The variable of which to get the type.
     * @return The type of weather variable specified by the variable key.
     */
    public WeatherStationDataType getType(String variableKey) {
        return WeatherStationDataType.valueOf(getVariableProperty(variableKey, TYPE).toUpperCase());
    }

    /**
     * Gets display name which represent the variable in the log URL.
     *
     * @param variableKey The variable whose display name is desired.
     * @return The string representation of the specified variable.
     */
    public String getDisplayName(String variableKey) {
        return getVariableProperty(variableKey, DISPLAY_NAME);
    }

    /**
     * Gets the full name of the variable in the log URL.
     *
     * @param variableKey The variable whose display name is desired.
     * @return The string representation of the specified variable.
     */
    public String getDescriptionName(String variableKey) {
        return getVariableProperty(variableKey, DESCRIPTION);
    }

    /**
     * Gets the units of the specified variable.
     *
     * @param variableKey The variable whose units are desired.
     * @return The units of the specified variable.
     */
    public String getUnits(String variableKey) {
        return getVariableProperty(variableKey, UNITS);
    }

    /**
     * Gets the minimum value for the given variable.
     *
     * @param variableKey The variable whose minimum value is desired.
     * @return The minimum value of the specified variable.
     */
    public Number getMinValue(String variableKey) {
        if (getType(variableKey) == WeatherStationDataType.INT) {
            return getVariableProperty(variableKey, MIN).length() > 0
                    ? Integer.parseInt(getVariableProperty(variableKey, MIN)) : Integer.MIN_VALUE;
        } else if (getType(variableKey) == WeatherStationDataType.FLOAT) {
            return getVariableProperty(variableKey, MIN).length() > 0
                    ? Float.parseFloat(getVariableProperty(variableKey, MIN)) : -1 * Float.MAX_VALUE;
        } else {//LONG or TIME
            return getVariableProperty(variableKey, MIN).length() > 0
                    ? Long.parseLong(getVariableProperty(variableKey, MIN)) : Long.MIN_VALUE;
        }
    }

    /**
     * Gets the maximum value for the given variable.
     *
     * @param variableKey The variable whose maximum value is desired.
     * @return The maximum value of the specified variable.
     */
    public Number getMaxValue(String variableKey) {
        if (getType(variableKey) == WeatherStationDataType.INT) {
            return getVariableProperty(variableKey, MAX).length() > 0
                    ? Integer.parseInt(getVariableProperty(variableKey, MAX)) : Integer.MAX_VALUE;
        } else if (getType(variableKey) == WeatherStationDataType.FLOAT) {
            return getVariableProperty(variableKey, MAX).length() > 0
                    ? Float.parseFloat(getVariableProperty(variableKey, MAX)) : Float.MAX_VALUE;
        } else {//LONG or TIME
            return getVariableProperty(variableKey, MAX).length() > 0
                    ? Long.parseLong(getVariableProperty(variableKey, MAX)) : Long.MAX_VALUE;
        }
    }

    /**
     * Gets the color for representation of the specified variable.
     *
     * @param variableKey The variable whose color is desired.
     * @return The color to represent the specified variable.
     */
    public Color getColor(String variableKey) {
        return Color.decode(getVariableProperty(variableKey, COLOR));
    }

    /**
     * Checks if the variable is used.
     *
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
     * Gets the ordering from the variable file.
     */
    public Vector<String> getOrdering() {
        Vector<String> ordering = new Vector<String>();
        Vector<String> orderedVariableKey = getOrderedVariableKeys();

//        for(String key : orderedVariableKey) if (isUsed(key)) ordering.add(key);
        int usedvars = 0;
        for (String key : orderedVariableKey) {
            if (isUsed(key)) {
                usedvars++;
            }
        }

        for (int i = 0; i < usedvars; i++) {
            ordering.add(i, "100");
        }
        for (int i = 0; i < orderedVariableKey.size(); i++) {
            if (isUsed(orderedVariableKey.get(i))) {
                ordering.setElementAt(orderedVariableKey.get(i),
                        Integer.parseInt(getVariableProperty(orderedVariableKey.get(i), ORDERING)));
            }
        }
        return ordering;
    }

    /**
     *
     * @param variableKey The variable whose Common group is desired.
     * @return The Common group of the variable.
     */
    public String getCommonGroup(String variableKey) {
        return getVariableProperty(variableKey, COMMON);
    }

    /**
     *
     * @param variableKey The variable to find.
     * @return The spacing defined in the properties file.
     */
    public int getSpacing(String variableKey) {
        return Integer.parseInt(getVariableProperty(variableKey, SPACING));
    }

    /**
     * Gets all the group names, without the group labeled "No Group".
     *
     * @return A vector of strings containing all the group names.
     */
    public Vector<String> getAllCommonGroups() {
        // Vector of all variable keys
        Vector<String> orderedVariableKey = getOrderedVariableKeys();
        // Vector of strings to return
        Vector<String> common = new Vector<String>();

        // For each variable in the properties file
        for (int i = 1; i < orderedVariableKey.size(); i++) {
            // Each time after the first
            if (i > 1) {
                // If the common variable and previous common variable are not null
                if (getCommonGroup(orderedVariableKey.get(i)) != null && getCommonGroup(orderedVariableKey.get(i - 1)) != null) {
                    // If the common group is equal to the previous common group or if the group is equal to no group, do nothing
                    if (getCommonGroup(orderedVariableKey.get(i - 1)).equalsIgnoreCase(getCommonGroup(orderedVariableKey.get(i)))
                            || getCommonGroup(orderedVariableKey.get(i)).toLowerCase().equalsIgnoreCase("no group")) {
                    } else {
                        // If the common group is not null
                        if (getCommonGroup(orderedVariableKey.get(i)) != null) {
                            // Add the group if it has not been added already
                            common.add(getCommonGroup(orderedVariableKey.get(i)));
                        }
                    }
                }
            } // On the first loop
            else {
                // Add the first variable
                common.add(getCommonGroup(orderedVariableKey.get(i)));
            }
        }
        // Return the vector of strings
        return common;
    }

    /**
     * Gets the default value for the specified variable type.
     *
     * @param type Weather variable type whose default value is desired.
     * @return Default value for the specified weather variable type.
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
     *
     * @param variableKey The variable whose default value is desired.
     * @return The default value for the specified variable.
     */
    public Number getDefaultValue(String variableKey) {
        switch (getType(variableKey)) {
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
     * Creates and returns an exact copy, or clone, of the object calling this
     * method.
     *
     * @return A clone of the object calling this method.
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        WeatherStationVariableProperties retVal = new WeatherStationVariableProperties();
        retVal.DEFAULT_FLOAT = this.DEFAULT_FLOAT;
        retVal.DEFAULT_INT = this.DEFAULT_INT;
        retVal.DEFAULT_LONG = this.DEFAULT_LONG;
        retVal.wvProperties = (Properties) this.wvProperties.clone();
        return retVal;
    }

    /**
     * Returns the Hash Code of the object calling this method.
     *
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
     * Checks to see if two instances of WeatherStationVariableProperties are
     * equal or not.
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
        WeatherStationVariableProperties other = (WeatherStationVariableProperties) o;
        return (this.wvProperties == null ? other.wvProperties == null : this.wvProperties.equals(other.wvProperties))
                && (this.propertiesFilePath == null ? other.propertiesFilePath == null : this.propertiesFilePath.equals(other.propertiesFilePath))
                && (this.DEFAULT_FLOAT == null ? other.DEFAULT_FLOAT == null : this.DEFAULT_FLOAT.equals(other.DEFAULT_FLOAT))
                && (this.DEFAULT_INT == null ? other.DEFAULT_INT == null : this.DEFAULT_INT.equals(other.DEFAULT_INT))
                && (this.DEFAULT_LONG == null ? other.DEFAULT_LONG == null : this.DEFAULT_LONG.equals(other.DEFAULT_LONG));
    }

    /**
     * Gets the value of the specified variable and property keys.
     *
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
     * This method initializes that properties from the database. It will either
     * grab the properties that contain the solar information or the the one
     * with no solar information. If it can't find the specified file path, it
     * will load the solar properties by default.
     *
     * @param propFile the property type that is in the current database. It
     * will either be 'wunder' or 'wunder_nosolar'.
     * @return the new property files.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Properties getProperties(String propFile) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        try {
            if (propFile == null || propFile.equals("wunder")) {
                properties = MySQLImpl.getMySQLDMBSSystem().getPropertyManager().getWeatherStationVariableProperties();
            } else if (propFile.equals("wunder_nosolar")) {
                properties = MySQLImpl.getMySQLDMBSSystem().getPropertyManager().getWeatherStationNoSolarVariableProperties();
            } else {
                properties = MySQLImpl.getMySQLDMBSSystem().getPropertyManager().getWeatherStationVariableProperties();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            WeatherLogger.log(Level.SEVERE, "Cannot find properties!");
        }

        return properties;
    }
}
