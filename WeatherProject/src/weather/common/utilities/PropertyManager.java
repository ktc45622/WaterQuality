package weather.common.utilities;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import weather.common.dbms.mysql.MySQLImpl;

/**
 * Utility class to access all properties and property files.
 *
 * The normal configure method must be called before making any requests for a
 * property other than a local property. The configureLocalProperties method
 * must be called right after a successful login since finding the local
 * property file requires a username. Once both have been successfully called a
 * request for any of the properties in those files may be made by calling the
 * respective getProperty method.
 *
 * TODO: Add some method that can compare the properties in the default property
 * file and the local property file. If there is a property in the default one
 * and not in the local one, have it create the new property. This will make
 * creating new local properties easier.
 *
 * @author Dave Moser (2008)
 * @author Chris Mertens (2009)
 * @author Joe Sharp (2009)
 * @author Joe Van Lente (2010)
 * @author Colton Daily (2014)
 *
 * @version Spring 2014
 */
public class PropertyManager {

    private static Properties generalProperties;
    private static Properties guiProperties;
    private static Properties localProperties;
    private static Properties defaultProperties;
    private static Properties localBookmarkProperties;
    private static Properties generalDatabaseProperties;
    private static Properties guiDatabaseProperties;
    private static Properties serverProperties;
    private static String userID;
    private static String storedDataPropertiesPath;
    private static File localPropertiesFile;
    private static File localBookmarkSavesFile;
    private static File localPropertiesDirectory;

    // List of default properties to copy to local file if they are missing.
    private static final String[] localPropertyKeys = new String[]{
        "ROOT_DIR", "DATA_DIR", "NOTE_DIR", "CAMERA_MOVIE_DIR", "MAP_LOOP_DIR",
        "PICTURE_DIR", "WEATHER_STATIONS_DIR", "TEMP_DIR", "SPLASH_SCREEN_ON",
        "ENCRYPTION", "DEFAULT_WEATHER_CAMERA", "DEFAULT_WEATHER_MAP_LOOP",
        "DEFAULT_WEATHER_STATION", "DEFAULT_START_HOURS",
        "DEFAULT_WEATHER_STATION_SPAN", "BOOKMARK_DIR",
        "LOCAL_BOOKMARK_SAVES_FILE", "LOCAL_BOOKMARKS_INITIAL_NUMBER",
        "WEB_LINK_DIR", "LESSON_DIR", "DEFAULT_MOVIE_TYPE",
        "INSTRUCTOR_GRADEBOOKS_DIR", "FORECASTING_LESSON_DIR",
        "FORECASTING_LESSON_EXCEL_DIR", "INITIAL_PLOT_DATA_TRACE"
    };

    // Enum for the two types of charting software we can use.
    public enum ChartType {

        WEATHER_CHART_2D("WEATHER_CHART_2D"),
        J_FREE_CHART("J_FREE_CHART");

        private final String chartType;

        private ChartType(String chartType) {
            this.chartType = chartType;
        }
    }

    /**
     * Configures all property files and variables EXCEPT localProperties since
     * a username is needed to property set that property file up.
     */
    public static void configure() {
        Debug.println("Configuring Property Manager");

        try {
            generalProperties = new Properties();
            guiProperties = new Properties();
            defaultProperties = new Properties();
            serverProperties = new Properties();

            //Need to hardcode the initial properties file, no way around this.
            //When using relative path processing in java these files will be
            //stored in the root of the jar that is being executed. In practice
            //netbeans moves that to the root of the project directory for
            //convienence. 
            guiProperties.load(new FileInputStream(
                    new File("config/GUI.properties")));
            defaultProperties.load(new FileInputStream(
                    new File("config/LocalDefaults.properties")));
            serverProperties.load(new FileInputStream(
                    new File("config/Server.properties")));
            
            //We must copy GeneralWeather.properties into the Stored Data folder
            //as it cannot be alter from within Program Files where it is
            //installed.  This should only be done once.  However, in testing to
            //see if it has been done, the code will initialize the var able
            //holding the file path.
            String storedDataFolderPath = System.getProperty("user.home")
                    + File.separator + getDefaultProperty("ROOT_DIR")
                    + File.separator + "Stored Data";
            storedDataPropertiesPath = storedDataFolderPath + File.separator
                        + "GeneralWeather.properties";
            
            Debug.println("Copied config file is to be placed at " 
                    + storedDataPropertiesPath);
            
            File storedDataPropertiesFile = new File(storedDataPropertiesPath);
            
            if (!storedDataPropertiesFile.exists()) { 
                File folder = new File(storedDataFolderPath);
                folder.mkdirs();
                File configFileToCopy = 
                        new File("config/GeneralWeather.properties");
                Files.copy(configFileToCopy.toPath(), 
                        storedDataPropertiesFile.toPath());
            }   //end of file copy
            
            //Now retrive infomation from the stored data copy.  The stream 
            //must be named so it can be closed, with is necessay for the
            //installer to exit gracefully if the program is running.
             FileInputStream generalInputStream = new FileInputStream(
                    storedDataPropertiesFile);
            generalProperties.load(generalInputStream);
            generalInputStream.close();
            
            //Load propeerties from database.
            generalDatabaseProperties = MySQLImpl.getMySQLDMBSSystem().getPropertyManager().getGeneralProperties();
            guiDatabaseProperties = MySQLImpl.getMySQLDMBSSystem().getPropertyManager().getGUIProperties();
        }  //end of try
        /**
         * TODO: need to change to our WeatherException System -- Need our
         * message based system -- Determine if this is a fatal error Display to
         * the user what we are looking for and where we are looking for it See
         * my notes in ExceptionSingleton on how to do this
         */
        catch (FileNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3009).show();
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3019).show();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
    /**
     * Gets the login id of the user for whom the local properties are currently
     * configured.
     * @return The login id of the user for whom the local properties are
     * currently configured.
     */
    public static String getUserId() {
        return userID;
    }

    /**
     * Configures the localProperties object and file, locating it by the
     * current logged in user. Also created a local.properties file if one does
     * not exist for the user and fills it with the defaults.
     *
     * @param tempUserID
     */
    public static void configureLocalProperties(String tempUserID) {
        userID = tempUserID;
        try {
            localProperties = new Properties();

            // local.properties location
            String pDrive = "P:";
            File tempFile = new File(pDrive);
            String dir;
            String applicationHome;
            String coreDirectory;
            if (tempFile.exists()) {
                coreDirectory = pDrive;
                dir = pDrive
                        + File.separator + getDefaultProperty("ROOT_DIR")
                        + File.separator + userID
                        + File.separator + getDefaultProperty("DATA_DIR");
                applicationHome = pDrive
                        + File.separator + getDefaultProperty("ROOT_DIR");
            } else {
                coreDirectory = System.getProperty("user.home");
                dir = System.getProperty("user.home")
                        + File.separator + getDefaultProperty("ROOT_DIR")
                        + File.separator + userID
                        + File.separator + getDefaultProperty("DATA_DIR");
                applicationHome = System.getProperty("user.home")
                        + File.separator + getDefaultProperty("ROOT_DIR");
            }
            Debug.println("User home is " + dir);
            Debug.println("Application home is " + applicationHome);
            Debug.println("coreDirectory is " + coreDirectory);
            
            localPropertiesDirectory = new File(dir);
            localPropertiesDirectory.mkdirs();
            localPropertiesFile = new File(localPropertiesDirectory
                    + File.separator + "local.properties");


            // If the file doesn't exist, set create it and set defaults.
            if (localPropertiesFile.createNewFile()) {
                setLocalDefaults(coreDirectory);
            }
            // Load the file and fill in missing properties.
            localProperties.load(new FileInputStream(localPropertiesFile));
            fillMissingProperties(coreDirectory);

            // Make temporary directory.
            String tempPath = coreDirectory + File.separator
                    + getDefaultProperty("ROOT_DIR") + File.separator
                    + userID + File.separator + "Temp";
            File tempDir = new File(tempPath);
            tempDir.mkdir();
        } catch (FileNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3009).show();
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3019).show();
        }
    }

    /**
     * Configures the local bookmark properties, creating and initializing the
     * file if necessary.
     */
    public static void configureBookmarkProperties() {
        localBookmarkProperties = new Properties();
        String directory = CommonLocalFileManager.getBookmarksDirectory();
        Debug.println("config bookmark dir: " + directory);
        new File(directory).mkdirs();
        localBookmarkSavesFile = new File(directory + File.separator
                + getLocalProperty("LOCAL_BOOKMARK_SAVES_FILE"));
        try {
            if (localBookmarkSavesFile.createNewFile()) {
                localBookmarkProperties.load(new FileInputStream(localBookmarkSavesFile));
                localBookmarkProperties.setProperty("next", getLocalProperty("LOCAL_BOOKMARKS_INITIAL_NUMBER"));
                localBookmarkProperties.store(new FileOutputStream(
                        localBookmarkSavesFile), "Local Bookmark Save Locations");
            } else {
                localBookmarkProperties.load(new FileInputStream(localBookmarkSavesFile));
            }
        } catch (FileNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3009).show();
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3019).show();
        }
    }

    /**
     * Obtains the requested property from the general properties file
     *
     * @param key the name of the property requested
     * @return The value of the property requested
     */
    public static String getGeneralProperty(String key) {
        if (generalProperties == null) {
            configure();
        }
        if (generalProperties.getProperty(key) == null) {
            return generalDatabaseProperties.getProperty(key, "Invalid Property").trim();
        }
        return generalProperties.getProperty(key, "Invalid Property!");
    }

    /**
     * Obtains the requested property from the GUI properties file
     *
     * @param key the name of the property requested
     * @return the value of the property requested
     */
    public static String getGUIProperty(String key) {
        if (guiProperties == null) {
            configure();
        }
        if (guiProperties.getProperty(key) == null) {
            return guiDatabaseProperties.getProperty(key, "Invalid Property").trim();
        }
        return guiProperties.getProperty(key, "Invalid Property!");
    }

    /**
     * Obtains the requested property from the default properties file
     *
     * @param key the name of the property requested
     * @return the value of the property requested
     */
    public static String getDefaultProperty(String key) {
        if (defaultProperties == null) {
            configure();
        }
        return defaultProperties.getProperty(key, "Invalid Property!").trim();
    }

    public static String getServerProperty(String key) {
        if (serverProperties == null) {
            configure();
        }
        return serverProperties.getProperty(key, "Invalid Property").trim();
    }

    /**
     * Obtains the requested property from the local properties file
     *
     * @param key the name of the property requested
     * @return the value of the property requested
     */
    public static String getLocalProperty(String key) {
        if (localProperties == null) {
            Debug.println("Configuring local properties to default");
            configureLocalProperties("default");
        }
        return localProperties.getProperty(key, "").trim();
    }

    /**
     * Obtains the requested property from the local bookmark properties file.
     *
     * @param key The name of the property requested.
     * @return The value of the property requested.
     */
    public static String getBookmarkProperty(String key) {
        if (localBookmarkProperties == null) {
            configureBookmarkProperties();
        }
        return localBookmarkProperties.getProperty(key, "");
    }

    /**
     * Returns a set containing keys for every locally saved bookmark.
     *
     * @return A set of keys for every saved bookmark.
     */
    public static ArrayList<String> getBookmarkKeys() {
        if (localBookmarkProperties == null) {
            configureBookmarkProperties();
        }
        Set<Object> keys = localBookmarkProperties.keySet();
        ArrayList<String> result = new ArrayList<String>();
        for (Object key : keys) {
            if (!"next".equals(key)) {
                result.add((String) key);
            }
        }
        return result;
    }

    /**
     * Obtains the default values of local properties and sets the current local
     * properties to the defaults.
     */
    public static void setLocalDefaults(String coreDir) {
        setLocalProperty("CORE_DIR", coreDir);

        for (String s : localPropertyKeys) {
            Debug.println("Settig Property " + s + " to " + getDefaultProperty(s) + ".");
            setLocalProperty(s, getDefaultProperty(s));
            Debug.println("Property set.");
        }
    }

    /**
     * Sets properties not found in local properties file.
     */
    public static void fillMissingProperties(String defaultCore) {
        if (getLocalProperty("CORE_DIR").isEmpty()) {
            setLocalProperty("CORE_DIR", defaultCore);
        }

        for (String s : localPropertyKeys) {
            if (getLocalProperty(s).isEmpty()) {
                setLocalProperty(s, getDefaultProperty(s));
            }
        }
    }

    public static void setGeneralProperty(String key, String value) {
        try {
            generalProperties.setProperty(key, value);

            //Must update file. The stream must be named so it can be closed, 
            //with is necessay for the installer to exit gracefully if the 
            //program is running.
            FileOutputStream generalOutputStream = new FileOutputStream(
                    new File(storedDataPropertiesPath));
            generalProperties.store(generalOutputStream, null);
            generalOutputStream.close();
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3010).show();
        }

    }

    /**
     * Sets the specified local property to the specified value. This works for
     * both existing properties and ones not yet created.
     *
     * @param key the name of the property to modify or create
     * @param value the value to set the property to
     */
    public static void setLocalProperty(String key, String value) {
        localProperties.setProperty(key, value);
        try {
            localProperties.store(new FileOutputStream(localPropertiesFile),
                    "Local Properties");
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3010).show();
        }
        CommonLocalFileManager.configure(userID);
    }

    /**
     * Sets the specified default property to the specified value. This works
     * for both existing properties and ones not yet created.
     *
     * @param key the name of the property to modify or create
     * @param value the value to set the property to
     */
    public static void setDefaultProperty(String key, String value) {
        try {
            defaultProperties.setProperty(key, value);

            defaultProperties.store(new FileOutputStream(new File("config/LocalDefaults.properties")), null);
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3010).show();
        }
    }

    /**
     * Sets the specified local bookmark property to the specified value. This
     * works for both existing properties and ones not yet created.
     *
     * @param key The name of the property to modify or create.
     * @param value The value to set the property to.
     */
    public static void setBookmarkProperty(String key, String value) {
        localBookmarkProperties.setProperty(key, value);
        try {
            localBookmarkProperties.store(new FileOutputStream(localBookmarkSavesFile),
                    "Local Bookmark Save Locations");
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3010).show();
        }
    }

    /**
     * Deletes the property represented by the given key. Used to remove deleted
     * bookmarks from the list.
     *
     * @param key The name of the property to delete.
     */
    public static void deleteBookmarkProperty(String key) {
        localBookmarkProperties.remove(key);
        try {
            localBookmarkProperties.store(new FileOutputStream(localBookmarkSavesFile),
                    "Local Bookmark Save Locations");
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            new WeatherException(3010).show();
        }
    }

    /**
     * Encrypts a string in hexadecimal format using the function specified in
     * the local properties file. The string will not be encrypted if the
     * encryption property in the local properties file is set to "none", is an
     * empty string, or does not exist in the file.
     *
     * @param orig the original string
     * @return the encrypted string
     */
    public static String encrypt(String orig) {
        String alg = getDefaultProperty("ENCRYPTION");
        if (orig == null || alg.isEmpty() || alg.equals("none") || alg.startsWith("Invalid")) {
            return orig;            // last condition is in case property does not exist; see getDefaultProperty()
        }
        byte[] bytes = null;
        try {
            // gets bytes from encryption algorithm
            bytes = MessageDigest.getInstance(alg).digest(orig.getBytes());
        } catch (NoSuchAlgorithmException e) {
            String msg = "The encryption property in the LocalDefaults.properties file is set to '"
                    + alg + "'; this algorithm is not available or does not exist.";
            WeatherLogger.log(Level.SEVERE, msg, e);
            new WeatherException(msg).show();
            return orig;
        }

        // translates bytes to hex string
        StringBuilder hexStrBuf = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xff);
            hexStrBuf.append(str.length() == 1 ? "0" : "").append(str);
        }

        return hexStrBuf.toString();
    }

    /**
     * @return the local properties directory
     */
    public static File getLocalPropertiesDirectory() {
        return localPropertiesDirectory;
    }
}
