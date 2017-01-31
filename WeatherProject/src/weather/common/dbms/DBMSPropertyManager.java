package weather.common.dbms;

import java.util.Properties;
import java.util.Vector;
import weather.common.data.Property;

/**
 * This interface manages the weather property table in the database.
 * @author Colton Daily (2014)
 * @author Xiang Li (2014)
 * @version Spring 2014
 */
public interface DBMSPropertyManager {
    
    /**
     * Gets the all the general weather properties stored in the database
     * and put all the properties into a vector
     * @return a vector that includes all the weather general properties
     */
    public Vector<Property> obtainAllGeneralProperties();
    /**
     * Gets the all the general weather properties stored in the database.
     * @return a property file containing all the general weather properties.
     */
    public Properties getGeneralProperties();
    
    /**
     * Gets all the gui weather properties stored in the database
     * and put all the properties into a vector
     * @return a vector that includes all the gui weather properties
     */
    public Vector<Property> obtainAllGUIProperties();
    /**
     * Gets all the GUI properties stored in the database.
     * @return a property file containing all the GUI properties.
     */
    public Properties getGUIProperties();
    
    /**
     * Gets all the Weather station two variable properties stored in the databse
     * and put all the properties into a vector
     * @return a vector that includes all the weather station variable properties
     */
    public Vector<Property> obtainAllWeatherStationTwoVariableProperties();
    /**
     * Gets all the WUnderground-TwoVariable properties stored in the database.
     * @return a property file containing all the WUnderground-TwoVariable
     * properties.
     */
    
    public Properties getWeatherStationTwoVariableProperties();
    
     /**
     * Gets all the Weather station variable properties stored in the database 
     * and put all the properties into a vector
     * @return a vector that includes all the weather station variable properties
     */
    public Vector<Property> obtainAllWeatherStationVariableProperties();
    
    /**
     * Gets all the WUnderground properties stored in the database.
     * @return a property file containing all the WUnderground
     * properties.
     */
    
    public Properties getWeatherStationVariableProperties();
    
    /**
     * Gets all the Weather station No Solar properties stored in the database 
     * and put all the properties into a vector
     * @return a vector that includes all the weather station variable properties
     */
    public Vector<Property> obtainAllWeatherStationNoSolarVariableProperties();
    /**
     * Gets all the WUnderground-NoSolar properties stored in the database.
     * @return a property file containing all the WUnderground-NoSolar
     * properties.
     */
    public Properties getWeatherStationNoSolarVariableProperties();
    
    /**
     * Remove one property from the database
     * @param property needs to be removed
     * @return true if removed the property successfully
     */
    public boolean removeProperty(Property property);
    
    /**
     * Update the value of one property
     * @param property needs to be updated
     * @return true if update the property successfully
     */
    public boolean updateProperty(Property property);
    
    /**
     * Insert a property into the database
     * @param property needs to be inserted to the databse
     * @return the property
     */
    public Property insertProperty(Property property);
    
    /**
     * Find an property with Prooperty ID
     * @param propertyID
     * @return the proerty
     */
    public Property obtainProperty(int propertyID);
    
    /**
     * Gets all the properties in the databse
     * @return a vector that includes all the properties
     */
    public Vector<Property> obtainAllProperties();
}
