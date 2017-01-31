package weather;

import java.awt.Component;
import java.util.Vector;
import weather.common.data.User;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.WeatherException;


/**
 * This interface specifies methods that help and assist our application. One of
 * its main functionalities is to keep track of our current user.
 *
 * TODO: There is already a method to get the DBMS system
 * in ApplicationControlSystem.
 *        Do we need both?
 *
 * @author bcmckenz
 * @author Bloomsburg University Software Engineering
 * @version Spring 2009
 */
public interface GeneralService {

    /**
     * Specifies the signature of the method to directly set the current user without database verification of
     * credentials.
     * 
     * @param user The current user.
     */
    public void setUser(User user);
    
    /**
     * Sets a user to override setUser.  Use null to turn off override.
     * 
     * @param user The override user.
     */
    public void setOverrideUser(User user);
    
    /**
     * Specifies the signature of the method to get the override or current user.
     * 
     * @return The current user of our system.
     */
    public User getUser();

    /**
     * Specifies the signature of the method to validate a given user's username
     * and password and sets this user to
     * be the current user of the system if the password and
     * username match the current database.
     * 
     * @param userName The username of the user.
     * @param password The password of the user.
     * @return True if valid, false otherwise.
     */
    public boolean validateAndSetUser(String userName, String password);

    /**
     * Specifies the signature of the method that will allow an administrator
     * to update information about a user in the current database. 
     * 
     * The user identifier will be used to determine if this user 
     * is already in the system.
     * 
     * @param currentUser The user to update.
     * @return The user information as stored in the database.
     * @throws WeatherException
     */
    public User updateUser(User currentUser) throws WeatherException;

    /**
     * Specifies the signature of the method that will allow an administrator
     * to add a new user to our database system.
     * 
     * If this user has a unique name, then this user is added to our database.
     * 
     * Note: we do not change our current user object.
     * 
     * @param newUser The the new user to be added to our database.
     * @return The user as recorded in the database.
     */
    public User insertNewUser(User newUser);

    // Put Databse Mangement operations here
    /**
     * Specifies the signature of the method that will return the DBMSSystemManager
     * object associated with this ClientService object.
     *
     * @return The DBMSSystemManager object for this ClientService.
     */
    public DBMSSystemManager getDBMSSystem();
    
    /**
     * Specifies the signature of the method that will return the WeatherDbClient
     * object associated with this ClientService object.
     * @return The WeatherDbClient object for this ClientService.
     */
    //public WeatherDbClient getWdbClient();

    /**
     * Specifies the signature of the method to set the DBMS object
     * associated with this class.
     * 
     * @param DBMS The new DBMSSystemManager object.
     */
    public void setDBMSSystem(DBMSSystemManager DBMS);

    /**
     * Specifies the signature of the method to change
     * the information stored in our database for a
     * particular resource. The resource ID field is used to determine if
     * this is a new resource or an existing resource that needs
     * database information modified.
     *
     * @param resource The weather resource object containing the updated information.
     * @return The updated weather resource object containing the information 
     *         as stored in our database or an empty resource if the update was 
     *         not successful.
     * @throws WeatherException
     */
    public Resource updateWeatherResource(Resource resource)
            throws WeatherException;

    /**
     * Specifies the signature of the method to return the current weather
     * camera resource.
     * The current weather camera resource is the one that has been selected
     * in the main program's camera selection menu.
     * 
     * @return The current weather camera resource.
     */
    public Resource getCurrentWeatherCameraResource();

    /**
     * Specifies the signature of the method that will return the current
     * weather map resource.
     * The current weather map resource is the map that appears
     * in the weather map pane of the main program.
     * 
     * @return The current weather map resource.
     */
    public Resource getCurrentWeatherMapLoopResource();

    /**
     * Specifies the signature of the method that will return the current
     * weather station resource.
     * The current weather station appears in the data plot pane
     * of the main program.
     * 
     * @return The current weather station resource.
     */
    public Resource getCurrentWeatherStationResource();

    /**
     * Specifies the signature of the method that will return all available
     * weather camera resources.
     * 
     * @return A vector of available weather camera resources.
     */
    public Vector<Resource> getWeatherCameraResources();

    /**
     * Specifies the signature of the method that will return all available
     * weather map resources.
     * 
     * @return A vector of available weather map resources.
     */
    public Vector<Resource> getWeatherMapLoopResources();

    /**
     * Specifies the signature of the method that will return all available
     * weather station resources.
     * 
     * @return A vector of available weather station resources.
     */
    public Vector<Resource> getWeatherStationResources();

    /**
     * Specifies the signature of the method to set the ClientControlImpl's
     * weather camera resource.
     * 
     * @param weatherCamera The camera resource that has been selected.
     */
    public void setCurrentWeatherCameraResource(Resource weatherCamera);

    /**
     * Specifies the signature of the method to set the ClientControlImpl's
     * weather map resource.
     * 
     * @param weatherMap The weather map that has been selected.
     */
    public void setCurrentWeatherMapLoopResource(Resource weatherMap);

    /**
     * Specifies the signature of the method to set the ClientControlImpl's
     * weather station resource.
     * 
     * @param weatherStation The weather station that has been selected.
     */
    public void setCurrentWeatherStationResource(Resource weatherStation);

    /**
     * This method compliments validateAndSetUser - this method also updates
     * the last login time and number of logins for that user.
     * 
     * @param userName The username of the user.
     * @param password The password of the user.
     * @return True if the login and password are in our database, false otherwise.
     */
    public boolean validateUserAndUpdateLoginInformation(String userName, String password);
    
     /**
     * Attempts to get all resources (cameras, station data, and
     * sites). If successful, each type of resource is put into a respective
     * vector and the first element of each is set as the default when the main
     * program is run.
     */
    public void setDefaultWeatherResourcesForGUI();
    
    /**
     * Generic method for use in closing certain small windows, rather than the
     * main program. Will display a "Leave without saving changes?" window, and
     * returns an answer.
     * 
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return True if YES, false if NO.
     */
    public boolean leaveWithoutSaving(Component parent);
}
