package weather.serverside.applicationimpl;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.*;
import weather.common.data.resource.*;
import weather.common.dbms.DBMSSystemManager;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.storage.StorageServerLocal;

/**
 * This class implements the StorageControlSystem interface in such a way as to
 * retrieve files stored on the local machine.  All requests to store and 
 * provide data are handled by <code>StorageServerLocal</code>.
 *
 * @author Bloomsburg University Software Engineering
 * @version Spring 2007
 */
public class StorageControlSystemLocalImpl implements StorageControlSystem, Serializable {

    private String fileSystemRoot;
    private StorageServerLocal storageServerLocal;
    private DBMSSystemManager dbms;
    
    /**
     * Constructs a
     * <code>StorageControlSystem<code> using the given
     * <code>DBMSSystemManager</code>.
     *
     * @param dbms The local
     * <code>DBMSSystemManage</code>.
     */
    public StorageControlSystemLocalImpl(DBMSSystemManager dbms) {
        this.fileSystemRoot = PropertyManager.getServerProperty("storageRootFolder");
        this.storageServerLocal = new StorageServerLocal(dbms, fileSystemRoot);
        this.dbms = dbms;
    }

    /**
     * Constructs a
     * <code>StorageControlSystem<code> using the given
     * <code>DBMSSystemManager</code>.
     *
     * @param dbms The local
     * <code>DBMSSystemManage</code>.
     * @param rootDir The root directory of the file system, ex. "E:"
     */
    public StorageControlSystemLocalImpl(DBMSSystemManager dbms, String rootDir) {
        this.fileSystemRoot = rootDir;
        this.storageServerLocal = new StorageServerLocal(dbms, fileSystemRoot);
        this.dbms = dbms;
    }
    
    /**
     * Gets the DBMS from this object.
     * 
     * @return A <code>DBMSSystemManager</code> that is the DBMS from this
     * object.
     */
    @Override
    public DBMSSystemManager getDBMS() {
        return this.dbms;
    }
    
    /**
     * Creates whatever does not exist of the file structure needed to save
     * weather resource instances and data for instructors. This method will not
     * delete any existing data.
     *
     * @return True if all creation actions were successful; false otherwise.
     */
    @Override
    public boolean createFileStructure() {
        //Only the default data must be copied.  Other data is copied as needed.
        return storageServerLocal.saveDefaultVideos();
    }
    
    
    /**
     * Used to place a specified weather resource instance into a file system so
     * long as the instance is NOT a day-long video.
     *
     * @param resourceInstance A weather resource instance to be put into the
     * file system.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean placeResourceInstance(ResourceInstance resourceInstance) {
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.STORE);
        command.setFirstResourceInstance(resourceInstance);

        try {
            storageServerLocal.executeLocalStore(command);
            return true;
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

  
    /**
     * Used to place a specified weather resource instance into a file system
     * when the instance IS a day-long video, which must be in mp4 format.  A
     * standard-quality version must be provided. The low-quality version can be
     * null if one is not available.
     *
     * @param standardInstance The standard-quality version of a day-long movie
     * instance to be put into the file system.
     * @param lowQualityInstance The low-quality version of a day-long movie
     * instance to be put into the file system.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean placeDayLongMovie(ResourceInstance standardInstance,
            ResourceInstance lowQualityInstance) {
        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.STORE_DAY_LONG_MP4);
        command.setFirstResourceInstance(standardInstance);
        command.setSecondResourceInstance(lowQualityInstance);

        try {
            storageServerLocal.executeLocalStore(command);
            return true;
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    /**
     * Gets the error log text file. The current error log file is returned as a
     * string. TODO: implement method
     *
     * @return The error log as a <code>String</code>.
     */
    @Override
    public String getErrorLog() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Sets the root directory to which all new resource instances will be
     * saved. The server will need to update its property file so that this root
     * directory is used on all system restarts. TODO: implement method
     *
     * @param rootDirectory The folder to set as the root for the file storage
     * system.
     */
    @Override
    public void setRootDirectory(String rootDirectory) {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Returns the current root folder of the file storage system. 
     *
     * @return The name of the root folder of the file storage system as a
     * string.
     */
    @Override
    public String getRootDirectory() {
        return fileSystemRoot;
    }
    
    /**
     * Gets a single series resource instances within a certain time range when
     * day-long movies are NOT being requested.
     *
     * @param request The object providing the resource instances being
     * requested.
     * @return An instance ResourceInstancesReturned holding the requested data.
     * This will be an empty ResourceInstancesReturned if no data is returned.
     */
    @Override
    public ResourceInstancesReturned getResourceInstances
        (ResourceInstancesRequested request) {
        ResourceInstancesReturned returnedData 
                = new ResourceInstancesReturned();

        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.PROVIDE);
        command.setResourceRequest(request);

        try {
            returnedData = storageServerLocal.executeLocalProvide(command)
                    .get(0);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }
        return returnedData;
    }

    /**
     * Used to retrieve two series of resource instances within a certain time
     * range when day-long movies ARE being requested.
     *
     * @param request The object specifying the resource and range for the data
     * being requested. (All its other fields are ignored.)
     * @return An ArrayList of ResourceInstancesReturned objects holding the
     * requested movies. Array index 0 is to hold the standard-quality movies
     * and array index 1 is to hold the low-quality movies.  This will be an 
     * empty ArrayList if no data is returned.
     */
    @Override
    public ArrayList<ResourceInstancesReturned> 
        getDayLongMovies(ResourceInstancesRequested request) {
        ArrayList<ResourceInstancesReturned> returnedData = new ArrayList<>();

        StorageCommand command = new StorageCommand();
        command.setCommandType(StorageCommandType.PROVIDE_DAY_LONG_MP4);
        command.setResourceRequest(request);

        try {
            returnedData = storageServerLocal.executeLocalProvide(command);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }
        return returnedData;
    }


    /**
     * This method will let the calling task know that the storage system is 
     * working correctly. 
     *  
     *
     * @return True if the file system is available
     */
    @Override
    public boolean pingServer() {
        /*
         * File f = new File(fileSystemRoot); if (f.exists()) { return true; }
         * else { throw new WeatherException(6003, true); }
         */

        return true; //Need to check the hardrive for space and if it is setup correctly.
        // for example is the generic movies directory present, 
        // can we get a responce by asking for a resoruce using resoruce requested. As 
        //long as we get a responce -- even an empty one we are good. 
    }

    /**
     * Creates a new command sent to the storage system to create a new default
     * nighttime movie.
     *
     * @param resource The
     * <code>Resource</code> for which the movie is created.
     * @param picture The
     * <code>ImageInstance</code> used to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setNewDefaultNightimeMovie(Resource resource, 
            ImageInstance picture) {
        picture.setResourceNumber(resource.getResourceNumber());
        StorageCommand command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_DEFAULT_NIGHT);
        
        try {
            storageServerLocal.executeLocalStore(command);
            return true;
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    /**
     * Creates a command for the storage system to create a new no data movie.
     *
     * @param picture The
     * <code>ImageInstance</code> used to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setNewDefaultGenericNoDataMovie(ImageInstance picture) {
        StorageCommand command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_NO_DATA_MP4);
        try {
            storageServerLocal.executeLocalStore(command);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
        }

        command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_NO_DATA_AVI);
        try {
            storageServerLocal.executeLocalStore(command);
            return true;
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    /**
     * Takes a resource, and a picture, and creates a new command sent to the
     * storage system to create a new default daytime (no data) movie from this
     * picture for the specific resource.
     *
     * @param resource The
     * <code>Resource</code> for which the movie is created.
     * @param picture The
     * <code>ImageInstance</code> used to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    @Override
    public boolean setNewDefaultDaytimeMovie(Resource resource, 
            ImageInstance picture) {
        picture.setResourceNumber(resource.getResourceNumber());
        StorageCommand command = new StorageCommand();
        command.setFirstResourceInstance(picture);
        command.setCommandType(StorageCommandType.STORE_DEFAULT_DAY);
        
        try {
            storageServerLocal.executeLocalStore(command);
            return true;
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a list of currently existing folders from the storage system.
     *
     * @return An
     * <code>ArrayList</code> containing a list of the folder names.  This will 
     * be an empty ArrayList if no data is returned.
     */
    @Override
    public ArrayList<String> retrieveFolderList() {
        throw new RuntimeException("Not yet implemented");
    }
    
    /**
     * Returns the path to an MP4 movie as a <code>File</code> object given a
     * resource number and a time. For hour-long movies, a path to a default
     * movie is returned if the movie does not exist. For full-day movies, the
     * original path is always returned and the caller must specify the quality
     * (standard or low) of the requested movie.
     *
     * @param resourceID The resource number to match the <code>Resource</code>
     * of the requested movie.
     * @param time The start time of the movie as a <code>Date</code> object.
     * @param isFullDay True if a day-long movie is being requested; False
     * otherwise.
     * @param isLowQuality True if a low-quality movie is being requested; False
     * otherwise. This parameter is ignored if an hour-long movie is being
     * requested.
     * @return The path in a <code>File</code> object. For hour-long movies, a
     * path to a default movie is returned if the movie does not exist. For
     * full-day movies, the original path is always returned.
     */
    @Override
    public File getFileForMP4Movie(int resourceID, Date time, boolean isFullDay,
            boolean isLowQuality) {        
        return storageServerLocal.getFileForMP4Movie(resourceID, time, 
                isFullDay, isLowQuality);
    }

    /**
     * Gets the default MP4 hour-long movie out of the storage system for a
     * given resource at a given time. The method will bypass the actual
     * recorded data if it exists.
     *
     * If it's nighttime, we set the path to a default nighttime movie for the
     * resource Otherwise, we set the path to a default daytime movie for the
     * resource.
     *
     * Next, create a new file from that path. If that file exists (which means
     * we have a default picture to return), return it. Otherwise, return the
     * storage system's default NoData movie instead.
     *
     * @param resource The resource whose movie we are searching for.
     * @param calendar A calender holding the time of the movie we're looking
     * for.
     *
     * @return If it's nighttime, a default nighttime movie. If it's daytime, a
     * default daytime movie. The next option is the storage system's default
     * NoData movie. The final option is null.
     */
    @Override
    public File getDefaultMP4MovieForTime(Resource resource,
            GregorianCalendar calendar) {
        return storageServerLocal.getDefaultMP4MovieForTime(resource, calendar);
    }

    /**
     * Method to determine if there are hour-long MP4 videos are present for the
     * given <code>Resource</code> on the given day to make a day-long video.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if there are enough hour-long MP4 videos present for the
     * given <code>Resource</code> on the given day to make a day-long video;
     * False otherwise.
     */
    @Override
    public boolean hasHourLongMP4Videos(int resourceID, Date time) {
        return storageServerLocal.hasHourLongMP4Videos(resourceID, time);
    }

    /**
     * Method to determine if enough hour-long MP4 videos can be made form AVI
     * videos that are present for the given <code>Resource</code> on the given 
     * day to make a day-long video.  This function will make an MP4 copy of 
     * each AVI video that is present for the given day.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if enough hour-long MP4 videos can be made form AVI videos
     * that are present for the given <code>Resource</code> on the given day to
     * make a day-long video; False otherwise.
     */
    @Override
    public boolean canMakeEnoughMP4VideosFromAVIVideos(int resourceID, 
            Date time) {
        return storageServerLocal
                .canMakeEnoughMP4VideosFromAVIVideos(resourceID, time);
    }

     /**
     * Method to convert all the hour-long MOV videos that are present for a
     * given <code>Resource</code> on the given day to MP4 format.
     * 
     * NOTE: This method will delete, rather than convert, any hour-long MOV
     * videos of the wrong length.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if all copying and file clean-up was successful; False 
     * otherwise.
     */
    @Override
    public boolean convertMOVVideosToMP4Videos(int resourceID, Date time) {
        return storageServerLocal.convertMOVVideosToMP4Videos(resourceID, time);
    }
}
