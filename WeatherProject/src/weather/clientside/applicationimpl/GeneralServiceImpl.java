package weather.clientside.applicationimpl;

import java.awt.Component;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.GeneralService;
import weather.clientside.gui.client.ChangePassword;
import weather.clientside.utilities.ResourceVisibleTester;
import weather.common.data.User;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.*;

/**
 * The purpose of this class is to keep track of the current user and to
 * facilitate the transfer of data between the main application program and our
 * database system. This class adds functionality to database routines to
 * accomplish a specific task for the GUI. This class should know the following
 * when it is correctly implemented.
 * <pre>
 * (1) What Database System we are using and what services its provides.
 *      (a) Provide Get and set methods for the database system object
 * (2) What weather camera, weather site and weather station are being displayed
 *     in the main application GUI.
 * </pre> It should also contain routines that add value to operations in the
 * database -- (for example to validate a user or add a user) This class should
 * not have references to any GUI element. One or two line utility operations
 * should not be in this class
 *
 *
 * @author bcmckenz (2009)
 * @author Bloomsburg University Software Engineering
 * @version Spring 2010
 */
public class GeneralServiceImpl implements GeneralService {
    /* Our current User */

    private User currentUser;
    /*The override user*/
    private User overrideUser = null;

    /* Our Database management System */
    private DBMSSystemManager dbms;
//    private WeatherDbClient wdbClient;
    private Resource currentWeatherCamera = null;
    /**
     * The current Resource begin displayed in any "site" JPanelPlayers (radar
     * video).
     */
    private Resource currentWeatherSite = null;
    /**
     * The current Resource being displayed on the data plot.
     */
    private Resource currentWeatherStation = null;

    /**
     * Constructor for class ClientServiceImpl to create an instance of this
     * class. It requires, as a parameter, the dbms system with the database
     * that stores user data.
     *
     * @param dbms A DBMSSystemManager object that points to the database with
     * user login information.
     */
    public GeneralServiceImpl(DBMSSystemManager dbms) {
        this.currentUser = null;
        this.dbms = dbms;
        currentWeatherStation = null;
        currentWeatherSite = null;
        currentWeatherCamera = null;
    }

    /**
     * Returns the current user.
     *
     * @return The current user
     */
    @Override
    public User getUser() {
        if (overrideUser == null) {
            return currentUser;
        } else {
            return overrideUser;
        }
    }

    /**
     * Directly sets our current user without database verification of
     * credentials.
     *
     * @param user The current user
     */
    @Override
    public void setUser(User user) {
        this.currentUser = user;
        // The following code is here as reminder that we could use the UserUtility class
        //      weather.clientside.utilities.UserUtility.setUser(currentUser);
    }

    /**
     * Attempts to add a new user to our system. If this user has a unique login
     * identification string, then this user is added to our database.
     *
     * @param newUser The user to add to our database.
     * @return The user as recorded in the database.
     */
    @Override
    public User insertNewUser(User newUser) {
        if (dbms.getUserManager().obtainUser(newUser.getLoginId()) == null) {
            dbms.getUserManager().addUser(newUser);
            return dbms.getUserManager().obtainUser(newUser.getLoginId());
        } else {
            return null;
        }
    }

    /**
     * Checks to see if a user is in the database and updates that record if it
     * is present. The user login identifier is used to determine if this user
     * is in the database. We allow different users to have the same name or
     * email address. But login identifiers must be unique.
     *
     * @param user The data of the user to be updated.
     * @return True if the user is updated, false otherwise.
     * @throws WeatherException Not thrown in this implementation.
     */
    @Override
    public User updateUser(User user) throws WeatherException {
        User findUser = dbms.getUserManager().obtainUser(user.getLoginId());
        if (findUser != null) {
            dbms.getUserManager().updateUser(user, findUser.getUserNumber());
            return findUser;
        }
        return null;
    }

    /**
     * Validates a given user's userName and password and sets this user to be
     * the current user of the system. A user is validated if the password and
     * user name match our database records.
     *
     * @param userName Username of the user.
     * @param password Password of the user.
     * @return True if the login and password are in our database, false
     * otherwise.
     */
    @Override
    public boolean validateAndSetUser(String userName, String password) {
        password = PropertyManager.encrypt(password);
        currentUser = dbms.getUserManager().obtainUser(userName);
        if (currentUser != null) {
            if (currentUser.getPassword().equals(password)) {
                return true;
            } else {
                currentUser = null;
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * This method compliments validateAndSetUser - this method also updates the
     * last login time and number of logins for that user.
     *
     * @param userName Username of the user.
     * @param password Password of the user.
     * @return True if the login and password are in our database, false
     * otherwise.
     */
    @Override
    public boolean validateUserAndUpdateLoginInformation(String userName, String password) {
        boolean isValidated = validateAndSetUser(userName, password);
        // The user object must be constructed and validated before we update their
        // login time and number of logins. This also keeps the old information in
        // the object for use in the application - as current information is obvious.
        if (isValidated) {
            checkForFirstLogin();
            dbms.getUserManager().updateLoginDateAndNumberOfLogins(currentUser);
        }
        return isValidated;
    }

    /**
     * Determine whether the current user is logged in for the first time before
     * the main application window shows up and makes the user change password
     * if so,
     */
    private void checkForFirstLogin() {
        //Every user has a login count, if this number is 0 we can tell that this
        //is the first time the user login into our system. When the login button
        //is clicked, a message will show up to force the user to change the password
        if (currentUser.isFirstLogIn()) {
            new ChangePassword(this, this.getUser().getLoginId(), true);
        }
    }

    /*
     * Determines if the supplied username is already in our database.
     *
     * @param userName the user name being checked.
     * @return true if the user name is in hte database,
     *  otherwise false is returned.
     */
    /*   @Override
     public boolean isUserNameInDatabase(String userName) {
     try {
     if ((dbms.getUserManager().obtainUser(userName)) != null) {
     return true;
     } else {
     return false;
     }

     } catch (WeatherException ex) {
     WeatherLogger.log(Level.SEVERE,
     "We could not reach the database system. ", ex);
     ex.show("Error occurred while attempting to validate the user.");
     return false;
     }
     }
     */
    /*
     * Checks to see if a user has an email address
     * in our database.
     *
     * @param email address The email address of the user
     * @return Returns true if the email is in our databse, otherwise, false
     *    is returned.
     */
    /*  @Override
     public boolean isUserEmailInDatabase(String emailAddress) {
     try {
     if ((dbms.getUserManager().obtainUserEmail(emailAddress)) != null) {
     return true;
     } else {
     return false;
     }

     } catch (WeatherException ex) {
     WeatherLogger.log(Level.SEVERE,
     "We could not reach the database system. ", ex);
     ex.show("Error occurred while attempting to validate the user.");
     return false;
     }
     }
     */
    /**
     * This service will return the DBMSSystemManager object associated with
     * this ClientService object.
     *
     * @return The DBMSSystemManager object for this ClientService.
     */
    @Override
    public DBMSSystemManager getDBMSSystem() {
        return dbms;
    }

    /**
     * Returns the WeatherDbClient object associated with this ClientService
     * object.
     *
     * @return The WeatherDbClient object for this ClientService.
     */
//    public WeatherDbClient getWdbClient() {
//        return this.wdbClient;
    // }
    /**
     * Sets the DBMS object for this service.
     *
     * @param dbms The DBMS object.
     */
    @Override
    public void setDBMSSystem(DBMSSystemManager dbms) {
        this.dbms = dbms;
    }

    /**
     * Updates the record in the database pertaining to the specified resource.
     *
     * @param resource The resource object to be updated.
     * @return The updated resource or an empty resource if the update was not
     * successful.
     */
    @Override
    public Resource updateWeatherResource(Resource resource) {
        return dbms.getResourceManager().updateWeatherResource(resource);
    }

    /**
     * Returns the current Weather Camera resource.
     *
     * @return The current Weather Camera resource.
     */
    @Override
    public Resource getCurrentWeatherCameraResource() {
        //Check for null to avoid exception below
        if (currentWeatherCamera == null) {
            return null;
        }
        //Get latest copy of resource.
        return getDBMSSystem().getResourceManager()
                .getWeatherResourceByNumber(currentWeatherCamera
                .getResourceNumber());
    }
    //These guys get resources, should go into a Resource Manager.

    /**
     * Returns the current Weather Site resource.
     *
     * @return The current Weather Site resource.
     */
    @Override
    public Resource getCurrentWeatherMapLoopResource() {
        //Check for null to avoid exception below
        if (currentWeatherSite == null) {
            return null;
        }
        //Get latest copy of resource.
        return getDBMSSystem().getResourceManager()
                .getWeatherResourceByNumber(currentWeatherSite
                .getResourceNumber());
    }

    /**
     * Returns the current Weather Station.
     *
     * @return The current Weather Station resource.
     */
    @Override
    public Resource getCurrentWeatherStationResource() {
        //Check for null to avoid exception below
        if (currentWeatherStation == null) {
            return null;
        }
        //Get latest copy of resource.
        return getDBMSSystem().getResourceManager()
                .getWeatherResourceByNumber(currentWeatherStation
                .getResourceNumber());
    }

    /**
     * Gets the vector of available WeatherCamera resources.
     *
     * @return The vector of available WeatherCamera resources.
     */
    @Override
    public Vector<Resource> getWeatherCameraResources() {
        Vector<Resource> resources = null;
        Vector<Resource> weatherCameras = new Vector<Resource>();
        try {
            resources = getDBMSSystem().getResourceManager().getResourceList();
            if (resources == null) {
                throw new WeatherException(4);
            }
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "No resources available. ",
                    ex);
            return weatherCameras; // vector of size 0;
        }
        for (Resource resource2 : resources) {
            if (resource2.getResourceType().equals(WeatherResourceType.WeatherCamera)) {
                weatherCameras.add(resource2);
            }
        }
        resources.clear(); // empty vector to help keep down garbage
        return weatherCameras;
    }

    /**
     * Gets the vector of available WeatherMapLoop resources.
     *
     * @return The vector of available WeatherMapLoop resources.
     */
    @Override
    public Vector<Resource> getWeatherMapLoopResources() {
        Vector<Resource> resources = null;
        Vector<Resource> weatherSites = new Vector<Resource>();
        resources = getDBMSSystem().getResourceManager().getResourceList();
        if (resources == null) {
            WeatherLogger.log(Level.SEVERE, "Weather Map Loop resource problem in our database. ");
            return weatherSites; //empty vector is returned
        }
        for (Resource resource2 : resources) {
            if (resource2.getResourceType().equals(WeatherResourceType.WeatherMapLoop)) {
                weatherSites.add(resource2);
            }
        }
        resources.clear(); // Keep down garbage
        return weatherSites;
    }

    /**
     * Returns the vector of available WeatherStation resources. Returns only
     * Weather Station resources
     *
     * @return The vector of available WeatherStation resources.
     */
    @Override
    public Vector<Resource> getWeatherStationResources() {
        Vector<Resource> resources = null;
        Vector<Resource> weatherStations = new Vector<Resource>();
        try {
            // Returning ALL resources, only WeatherStationValues needed
            resources = getDBMSSystem().getResourceManager().getResourceList();
            if (resources == null) {
                throw new WeatherException(4);
            }
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "No resources available. ", ex);
            ex.show();
            return weatherStations; // Zero length vector
        }

        // return only WeatherStationValues
        // @TODO Make overloaded ResourceManager.getResource to get subset of
        //       results.
        for (Resource resource2 : resources) {
            if (resource2.getResourceType().equals(WeatherResourceType.WeatherStationValues)) {
                weatherStations.add(resource2);
            }
        }
        resources.clear();// Keep down garbage
        return weatherStations;
    }

    /**
     * Sets this ClientControlImpl's current weather camera resource.
     *
     * @param weatherCamera The new weather camera resource.
     */
    @Override
    public void setCurrentWeatherCameraResource(Resource weatherCamera) {
        if (weatherCamera == null) {
            Debug.println("Setting camera resource to null");
        }
        this.currentWeatherCamera = weatherCamera;
    }

    /**
     * Sets this ClientControlImpl's current weather site.
     *
     * @param weatherSite The new weatherSite resource for this
     * clientControlImpl to use.
     */
    @Override
    public void setCurrentWeatherMapLoopResource(Resource weatherSite) {
        this.currentWeatherSite = weatherSite;
    }

    /**
     * Sets this ClientControlImpl's current weather station.
     *
     * @param weatherStation The new weatherStation resource for this
     * clientControlImpl to use.
     */
    @Override
    public void setCurrentWeatherStationResource(Resource weatherStation) {
        this.currentWeatherStation = weatherStation;
    }

    /**
     * Attempts to get all resources (cameras, station data, and sites). If
     * successful, each type of resource is put into a respective vector and the
     * first element of each is set as the default when the main program is run.
     */
    @Override
    public void setDefaultWeatherResourcesForGUI() {
        Vector<Resource> resources = null;
        try {

            // @TODO Need to filter incoming results to cut down data time
            // need an overriden getResourceMethod to pull less data down.
            Debug.println("starting to get resources");
            resources = getDBMSSystem().getResourceManager().getResourceList();
            Debug.println("got resources");
            if (resources == null) {
                Debug.println("resources are null");
                throw new WeatherException(0004);
            }
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Resource problem in database", ex);
        }
        for (Resource resource : resources) {
            if (resource.getResourceType() == WeatherResourceType.WeatherCamera
                    && PropertyManager.getLocalProperty("DEFAULT_WEATHER_CAMERA").
                    equals(resource.getResourceName())) {
                if (ResourceVisibleTester.canUserSeeResource(getUser(), resource)) {
                    this.currentWeatherCamera = resource;
                } else {
                    this.currentWeatherCamera = null;
                }
            } else if (resource.getResourceType().equals(WeatherResourceType.WeatherMapLoop)
                    && PropertyManager.getLocalProperty("DEFAULT_WEATHER_MAP_LOOP").
                    equals(resource.getResourceName())) {
                if (ResourceVisibleTester.canUserSeeResource(getUser(), resource)) {
                    this.currentWeatherSite = resource;
                } else {
                    this.currentWeatherSite = null;
                }
            } else if (resource.getResourceType().equals(WeatherResourceType.WeatherStationValues)
                    && PropertyManager.getLocalProperty("DEFAULT_WEATHER_STATION").
                    equals(resource.getResourceName())) {
                if (ResourceVisibleTester.canUserSeeResource(getUser(), resource)) {
                    this.currentWeatherStation = resource;
                } else {
                    this.currentWeatherStation = null;
                }
            }
        }

        Debug.println("finished setdefaultweatherresources");
    }

    /**
     * Generic method for use in closing certain small windows, rather than the
     * main program. Will display a "Leave without saving changes?" window, and
     * returns an answer.
     * 
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return True if YES, false if NO.
     * @author Eric Lowrie (2012)
     */
    @Override
    public boolean leaveWithoutSaving(Component parent) {
        if (JOptionPane.showConfirmDialog(parent, "Leave without saving changes?",
                "Weather Viewer", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }

    /**
     * Sets a user to override setUser.
     *
     * @param user The override user.
     */
    @Override
    public void setOverrideUser(User user) {
        this.overrideUser = user;
    }
}
