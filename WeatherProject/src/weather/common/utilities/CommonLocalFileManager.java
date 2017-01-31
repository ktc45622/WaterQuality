package weather.common.utilities;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.WeatherResourceType;

/**
 * Contains common functions for saving files locally.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * @version Spring 2010
 */
public class CommonLocalFileManager {

    private static String ROOT_DIR;
    private static String DATA_DIR;
    private static String BIN_DIR;
    private static String NOTE_DIR;
    private static String BOOKMARK_DIR;
    private static String CAMERA_MOVIE_DIR;
    private static String MAP_LOOP_DIR;
    private static String PICTURE_DIR;
    private static String WEATHER_STATIONS_DIR;
    private static String TEMP_DIR;
    private static String LESSON_DIR;
    private static String WEB_LINK_DIR;
    private static String INSTRUCTOR_GRADEBOOKS_DIR;
    private static String FORECASTING_LESSON_DIR;
    private static String FORECASTING_LESSON_EXCEL_DIR;
    private static String userID;

    private static String DIARY_CSS_FILE = "css" + File.separator + "diary.css";

    /**
     * Configures the preferred save directories for local file saving. This
     * method must be called before the class can work correctly.
     *
     * @param tempUserID The current user's login ID.
     */
    public static void configure(String tempUserID) {
        userID = tempUserID;
        ROOT_DIR = PropertyManager.getLocalProperty("CORE_DIR")
                + File.separator + PropertyManager.getLocalProperty("ROOT_DIR")
                + File.separator + userID;
        DATA_DIR = PropertyManager.getLocalProperty("DATA_DIR");
        NOTE_DIR = PropertyManager.getLocalProperty("NOTE_DIR");
        BOOKMARK_DIR = PropertyManager.getLocalProperty("BOOKMARK_DIR");
        CAMERA_MOVIE_DIR = PropertyManager.getLocalProperty("CAMERA_MOVIE_DIR");
        MAP_LOOP_DIR = PropertyManager.getLocalProperty("MAP_LOOP_DIR");
        PICTURE_DIR = PropertyManager.getLocalProperty("PICTURE_DIR");
        LESSON_DIR = PropertyManager.getLocalProperty("LESSON_DIR");
        WEB_LINK_DIR = PropertyManager.getLocalProperty("WEB_LINK_DIR");
        WEATHER_STATIONS_DIR = PropertyManager.getLocalProperty("WEATHER_STATIONS_DIR");
        TEMP_DIR = PropertyManager.getLocalProperty("TEMP_DIR");
        BIN_DIR = PropertyManager.getLocalProperty("BIN_DIR");
        INSTRUCTOR_GRADEBOOKS_DIR = PropertyManager.getLocalProperty("INSTRUCTOR_GRADEBOOKS_DIR");
        FORECASTING_LESSON_DIR = PropertyManager.getLocalProperty("FORECASTING_LESSON_DIR");
        FORECASTING_LESSON_EXCEL_DIR = PropertyManager.getLocalProperty("FORECASTING_LESSON_EXCEL_DIR");

    }

    /**
     * Returns the full path to the user's temporary directory.
     *
     * @return The directory path as a string.
     */
    public static String getTemporaryDirectory() {
        return ROOT_DIR + File.separator + TEMP_DIR;
    }

    /**
     * Returns the full path of the avi movie directory.
     *
     * @return The absolute path of the directory.
     */
    public static String getAVIDir() {
        File file = new File(PropertyManager.getLocalProperty("CORE_DIR")
                + File.separator + PropertyManager.getLocalProperty("ROOT_DIR")
                + File.separator + "Stored Data");
        if (!file.exists()) {
            file.mkdir();
            file.deleteOnExit();
        }
        return file.getAbsolutePath();
    }

    /**
     * Returns the full path of the avi camera directory.
     *
     * @return The absolute path of the directory.
     */
    public static String getAVICameraDir() {
        File file = new File(getAVIDir() + File.separator + "Weather Cameras");
        if (!file.exists()) {
            file.mkdir();
            file.deleteOnExit();
        }
        return file.getAbsolutePath();
    }

    /**
     * Returns the full path of the avi maploop directory.
     *
     * @return The absolute path of the directory.
     */
    public static String getAVIMaploopDir() {
        File file = new File(getAVIDir() + File.separator + "Weather Map Loops");
        if (!file.exists()) {
            file.mkdir();
            file.deleteOnExit();
        }
        return file.getAbsolutePath();
    }

    /**
     * Returns the full path to the bin directory
     *
     * @return The absolute path of bin.
     */
    public static String getBinDir() {
        return ROOT_DIR + File.separator + BIN_DIR;
    }

    /**
     * Returns the full path to the user's root directory.
     *
     * @return The directory path as a string.
     */
    public static String getRootDirectory() {
        return ROOT_DIR;
    }

    /**
     * Returns the full path to the user's data directory.
     *
     * @return The directory path as a string.
     */
    public static String getDataDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR;
    }

    /**
     * This will return the full path to the directory where pictures are
     * stored.
     *
     * @return The full path to where the pictures are stored.
     */
    public static String getPictureDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR + File.separator + PICTURE_DIR;
    }

    /**
     * This will return the full path to the directory where camera movies are
     * stored.
     *
     * @return The full path to where the camera movies are stored.
     */
    public static String getCameraMovieDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR + File.separator + CAMERA_MOVIE_DIR;
    }

    /**
     * This will return the full path to the directory where radar and satellite
     * movies are stored.
     *
     * @return The full path to where the radar and satellite movies are stored.
     */
    public static String getMapLoopDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR + File.separator + MAP_LOOP_DIR;
    }

    /**
     * This will return the full path to the directory where notes are stored.
     *
     * @return The full path to where the notes are stored.
     */
    public static String getNoteDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR + File.separator + NOTE_DIR;
    }

    /**
     * This will return the full path to the directory where lessons are stored.
     *
     * @return The full path to where the lessons are stored.
     */
    public static String getLessonDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR + File.separator
                + LESSON_DIR;
    }

    /**
     * This will return the full path to the directory where web links are
     * stored.
     *
     * @return The full path to where the web links are stored.
     */
    public static String getWebLinkDirectory() {

        File file = new File(ROOT_DIR + File.separator + DATA_DIR + File.separator
                + WEB_LINK_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * This will return the full path to the directory where bookmarks are
     * stored.
     *
     * @return The full path to where the bookmarks are stored.
     */
    public static String getBookmarksDirectory() {
        return ROOT_DIR + File.separator + DATA_DIR + File.separator + BOOKMARK_DIR;
    }

    /**
     * This will return the full path to the directory where instructor
     * gradebooks are stored.
     *
     * @return The full path to where the instructor gradebooks are stored.
     */
    public static String getInstructorGradebooksDirectory() {
        File file = new File(ROOT_DIR + File.separator + DATA_DIR
                + File.separator + INSTRUCTOR_GRADEBOOKS_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * This will return the full path to the directory where the forecasting
     * lessons are stored.
     *
     * @return The full path to the directory where the forecasting lessons are
     * stored.
     */
    public static String getForecastingLessonDirectory() {
        File file = new File(ROOT_DIR + File.separator + DATA_DIR
                + File.separator + INSTRUCTOR_GRADEBOOKS_DIR
                + File.separator + FORECASTING_LESSON_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * This will return the full path to the directory where the forecasting
     * lessons excel files are stored.
     *
     * @return The full path to the directory where the forecasting lessons
     * excel files are stored.
     */
    public static String getForecastingLessonExcelFilesDirectory() {
        File file = new File(ROOT_DIR + File.separator + DATA_DIR
                + File.separator + INSTRUCTOR_GRADEBOOKS_DIR
                + File.separator + FORECASTING_LESSON_EXCEL_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * This will return the full path to the directory where the weather station
     * data is stored.
     *
     * @return The full path to the directory where the weather station data is
     * stored.
     */
    public static String getWeatherStationsDirectory() {
        File file = new File(PropertyManager.getLocalProperty("CORE_DIR") + File.separator 
                + PropertyManager.getLocalProperty("ROOT_DIR") + File.separator 
                + "Stored Data" + File.separator 
                + PropertyManager.getLocalProperty("WEATHER_STATIONS_DIR"));
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * This will return the user login name that is currently logged into this
     * instance of the weather program.
     *
     * @return userID The user login that is currently logged into this instance
     * of the weather program.
     */
    public static String getUserLogin() {
        return userID;
    }

    /**
     * Get the path of the diary css file.
     *
     * @return Get the path of the diary css file.
     */
    public static String getDiaryCssFilename() {
        return DIARY_CSS_FILE;
    }

    /**
     * Configures the default save directories for local file saving. This
     * method must be called before the class can work correctly. Currently this
     * gets called in ClientServiceImpl.validateLogin.
     *
     * @param tempUserID The current user's login ID.
     */
    public static void configureDefaults(String tempUserID) {
        userID = tempUserID;
        /*Allows for a value to be put in the core directory value.
         If left black the program assumes the core directory is the user's
         home directory, since System.getProperty("user.home") can't be saved
         in the default property file as it differs for each user.*/

        ROOT_DIR = System.getProperty("user.home") + PropertyManager.getDefaultProperty("ROOT_DIR") + File.separator + userID;
        NOTE_DIR = PropertyManager.getDefaultProperty("NOTE_DIR");
        BOOKMARK_DIR = PropertyManager.getDefaultProperty("BOOKMARK_DIR");
        CAMERA_MOVIE_DIR = PropertyManager.getDefaultProperty("CAMERA_MOVIE_DIR");
        MAP_LOOP_DIR = PropertyManager.getDefaultProperty("MAP_LOOP_DIR");
        PICTURE_DIR = PropertyManager.getDefaultProperty("PICTURE_DIR");
        WEATHER_STATIONS_DIR = PropertyManager.getDefaultProperty("WEATHER_STATIONS_DIR");
        TEMP_DIR = PropertyManager.getDefaultProperty("TEMP");
        INSTRUCTOR_GRADEBOOKS_DIR = PropertyManager.getDefaultProperty("INSTRUCTOR_GRADEBOOKS_DIR");
        FORECASTING_LESSON_DIR = PropertyManager.getDefaultProperty("FORECASTING_LESSON_DIR");
        FORECASTING_LESSON_EXCEL_DIR = PropertyManager.getDefaultProperty("FORECASTING_LESSON_EXCEL_DIR");
    }
    
    /**
     * Saves a given file to the location specified by the path.
     *
     * @param extraPath The path the file will be saved to.
     * @param fileName The file to be saved at the given location.
     * @param resourceToSave The resource that will be saved to the file.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return Whether or not the save was successful.
     */
    private static boolean saveFile(String extraPath, String fileName, 
            ResourceInstance resourceToSave, Component parent) {
        File filePath;
        WeatherResourceType type = resourceToSave.getResourceType();
        StringBuilder strPath = new StringBuilder();

        switch (type) {
            case WeatherImage:
                strPath.append(getPictureDirectory());
                break;

            case WeatherNotes:
                strPath.append(getNoteDirectory());
                break;

            default:
                break;
        }

        strPath.append(File.separator);
        strPath.append(extraPath);

        filePath = new File(strPath.toString());

        return saveFileToDirectory(filePath, fileName, resourceToSave, parent);
    }

    /*
     * Deletes a given file at the location specified by the path.
     * @param extraPath The path of the file to be deleted.
     * @param fileName The file to be deleted at the given location.
     * @param resourceToSave The resource that will be deleted.
     */
    public static void deleteFile(String extraPath, String filename, ResourceInstance resourceToSave) {
        File filePath = null;
        WeatherResourceType type = resourceToSave.getResourceType();
        StringBuilder strPath = new StringBuilder();

        switch (type) {
            case WeatherImage:
                strPath.append(getPictureDirectory());
                break;

            case WeatherNotes:
                strPath.append(getNoteDirectory());
                break;

            default:
                break;
        }

        strPath.append(File.separator);
        strPath.append(extraPath);

        filePath = new File(strPath.toString());
        File fileToDelete = new File(filePath, filename);
        fileToDelete.delete();

    }

    /**
     * Saves a file to the specified directory rather than the default one for
     * the type of resource being saved.
     *
     * @param dir The directory to save the file to.
     * @param filename The name of the file to save.
     * @param resourceToSave The resource to save.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return Whether or not the save was successful.
     */
    public static boolean saveFileToDirectory(File dir, String filename, 
            ResourceInstance resourceToSave, Component parent) {
        //Assume save will be successful until we know otherwise.
        boolean result = true;
        
        if (!dir.mkdirs() && !dir.exists()) {
            configureDefaults(userID);
            return saveFile("", filename, resourceToSave, parent);
        } else {
            File fileToSave = new File(dir, filename);
            /**
             * Attempts to create the file by the specific name if and only if
             * it doesn't already exist
             */
            try {
                fileToSave.createNewFile();
            } catch (IOException ex) {
                WeatherLogger.log(Level.SEVERE,
                        "Could not save file " + fileToSave.getPath(), ex);
                new WeatherException(3017).show();
                result = false;
            }
            
            //Check if file creation was succussful before trying write.
            if(result) {
                /**
                 * Call the writeFile method of the specific resourceInstance to
                 * be saved
                 */
                try {
                    resourceToSave.writeFile(fileToSave);
                } catch (WeatherException e) {
                    WeatherLogger.log(Level.SEVERE,
                            "Could not save file " + fileToSave.getPath(), e);
                    JOptionPane.showMessageDialog(parent, "Unable to save file.");
                    result = false;
                }
            }
        }
        
        return result;
    }

    /**
     * Ensures that the specified directory exists and can be written to.
     *
     * @param dir The directory to be ensured.
     * @return True if the directory has been created and can be written to;
     * false otherwise.
     */
    public static boolean ensureDirectory(File dir, int type) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dir.exists() && (type == WeatherFileChooser.OPEN ? dir.canRead() : dir.canWrite())) {
            return true;
        } else {
            WeatherLogger.log(Level.SEVERE, "Could not create/save in directory " + dir);
            return false;
        }
    }
}
