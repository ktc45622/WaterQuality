package weather;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.common.data.*;
import weather.common.data.resource.*;
import weather.common.dbms.DBMSSystemManager;

/**
 *
 * This interface specifies the requests that the storage retrieval system will
 * accept. Methods to get and clear the error log are specified by this
 * interface.
 *
 * @author Bloomsburg University Software Engineering
 * @author Bill Katsak (2008)
 * @version Spring 2008
 */
/*
 * Note -- as of now -- does not change what it does when a resource setting
 * changes Could implement ResourceChangeListener in future if it would help our
 * system. Need to determine what to do in some cases. For example, when a
 * resource is added -- make a new directory When modified -- copy old data over
 * to new location if folder name changed When removed -- archive data ? When
 * frequency changes or active or valid -- do nothing.
 *
 */
public interface StorageControlSystem {

    /**
     * Specifies the signature of the method that will create whatever does not
     * exist of the file structure needed to save weather resource instances and
     * data for instructors. This method will not replace or delete any existing
     * data.
     *
     * @return True if all required copying was successful; false otherwise.
     */
    public boolean createFileStructure();

    /**
     * Specifies the signature of the method to place a specified weather
     * resource instance into a file system so long as the instance is NOT a
     * day-long video.
     *
     * @param resourceInstance A weather resource instance to be put into the
     * file system.
     * @return True if the save was successful; False otherwise.
     */
    public boolean placeResourceInstance(ResourceInstance resourceInstance);
    
    /**
     * Specifies the signature of the method to place a specified weather
     * resource instance into a file system when the instance IS a day-long
     * video, which must be in mp4 format.  A standard-quality version must be 
     * provided.  The low-quality version can be null if one is not available.
     *
     * @param standardInstance The standard-quality version of a day-long movie 
     * instance to be put into the file system.
     * @param lowQualityInstance The low-quality version of a day-long movie 
     * instance to be put into the file system.
     * @return True if the save was successful; False otherwise.
     */
    public boolean placeDayLongMovie(ResourceInstance standardInstance,
            ResourceInstance lowQualityInstance);

    /**
     * Specifies the signature of the method that will retrieve a bundle of
     * resource instances within a certain time range when day-long movies are
     * NOT being requested.
     *
     * @param request The object specifying the resources being requested.
     * @return The resource instances bundled in the ResourceInstancesReturned
     * object.  This will be an empty ResourceInstancesReturned if no data is
     * returned.
     */
    public ResourceInstancesReturned 
        getResourceInstances(ResourceInstancesRequested request);
    
    /**
     * Specifies the signature of the method that will retrieve a the requested
     * resource instances within a certain time range when day-long movies ARE
     * being requested.
     *
     * @param request The object specifying the resource and range for the data
     * being requested.  (All its other fields are ignored.)
     * @return An ArrayList of ResourceInstancesReturned objects holding the
     * requested movies.  Array index 0 is to hold the standard-quality movies
     * and array index 1 is to hold the low-quality movies.  This will be an
     * empty ArrayList if no data is returned.
     */
    public ArrayList<ResourceInstancesReturned> 
        getDayLongMovies(ResourceInstancesRequested request);
    
    /**
     * This method attempts to connect to the server stored in the class data.
     * If there is no response from the server, a fatal exception is thrown.
     *
     * @return True if the storage system is available and responding to
     * requests, false otherwise.
     */
    public boolean pingServer();

    /**
     * Specifies the signature of the method that will get the error log text
     * file. The current error log file is returned as a string.
     *
     * @return The text version of the error log.
     */
    public String getErrorLog();

    /**
     * Specifies the signature of the method that will set the root directory
     * for all new resource instances. The server will need to update its
     * property file so that this root directory is used on all system restarts.
     *
     * @param rootDirectory The root folder of the file system to which all new
     * resource instances will be saved.
     */
    public void setRootDirectory(String rootDirectory);

    /**
     * Specifies the signature of the method that will return the current root
     * folder under which all resource instances are currently being saved.
     *
     * @return The name of the root folder of the file system as a string value.
     */
    public String getRootDirectory();

    /**
     * Sets a new default nighttime movie for a specific resource. It will be
     * stored in the resource folder under "Generic Movies/DefaultNight"
     * as to files with .avi and .mp4 extensions.
     *
     * @param resource The resource to create a new nighttime movie for.
     * @param picture The picture to use to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    public boolean setNewDefaultNightimeMovie(Resource resource, 
            ImageInstance picture);

    /**
     * Sets a new default daytime movie for a specific resource. It will be
     * stored in the resource folder under "Generic Movies/DefaultDay"
     * as to files with .avi and .mp4 extensions.
     *
     * @param resource The resource to create a new nighttime movie for.
     * @param picture The picture to use to create the movie.
     * @return True if the save was successful; False otherwise.
     */
    public boolean setNewDefaultDaytimeMovie(Resource resource, 
            ImageInstance picture);

    /**
     * Sets a new default no-data movie for the entire file system. It will be
     * stored in the storage root folder as "NoData.avi".
     * @return True if the save was successful; False otherwise.
     *
     * @param picture The picture to use to create the movie.
     */
    public boolean setNewDefaultGenericNoDataMovie(ImageInstance picture);

    /**
     * Retrieves a list of currently existing folders from the storage system.
     *
     * @return An <code>ArrayList</code> containing a list of the folder names.
     * This will be an empty ArrayList if no data is returned.
     */
    public ArrayList<String> retrieveFolderList();
    
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
    public File getFileForMP4Movie(int resourceID, Date time, boolean isFullDay,
            boolean isLowQuality);
    
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
    public File getDefaultMP4MovieForTime(Resource resource,
            GregorianCalendar calendar);
    
    /**
     * Method to determine if there are hour-long MP4 videos are present for the
     * given <code>Resource</code> on the given day to make a day-long video.
     *
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the
     * past. It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if there are enough hour-long MP4 videos present for the
     * given <code>Resource</code> on the given day to make a day-long video;
     * False otherwise.
     */
    public boolean hasHourLongMP4Videos(int resourceID, Date time);
    
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
    public boolean canMakeEnoughMP4VideosFromAVIVideos(int resourceID, 
            Date time);
    
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
    public boolean convertMOVVideosToMP4Videos(int resourceID, Date time);
    
    /**
     * Gets the DBMS from this object.
     * 
     * @return A <code>DBMSSystemManager</code> that is the DBMS from this
     * object.
     */
    public DBMSSystemManager getDBMS();
}
