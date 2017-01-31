package weather.common.servercomm.storage;

import java.io.Serializable;

/**
 * This class specifies all the available storage server 
 * commands that can be sent from the client. 
 * @author Bloomsburg University Software Engineering
 * @version Spring 2008
 */
public enum StorageCommandType implements Serializable{
    
    /**
     * Resource-specific STORE commands:
     * These commands store data from a given resource and require at least one
     * <code>ResourceInstance</code> to be provided with the command.  Any
     * <code>ResourceInstance</code> must have the correct resource number
     * specified.  Even though it is the act of storing that permanently 
     * associates the data in the <code>ResourceInstance</code>(s), either 1 
     * image, 1 movie, or 2 movies, to the <code>Resource</code> in question;
     * the resource number is to be provided with the object(s) with data to be
     * saved, which also must provide the time through a <code>Date</code> field 
     * when required.
     */
   
    /** 
     * Stores hour-long video for movie-making resources and day-long files of
     * weather station data.  Also handles images from movie-making resources. 
     * To be used in a <code>StorageCommand</code> with a single and first 
     * <code>ResourceInstance</code> that provides a resource number and time 
     * (see comment in type definition).  The <code>ResourceInstance</code> must
     * already be a video or a <code>WeatherStationInstance</code> containing 
     * all the data currently available.
     */
    STORE,
    
    /** 
     * Stores single-day video for movie-making resources in mp4 format.  To be 
     * used in a <code>StorageCommand</code> that provides at least one instance
     * of <code>ResourceInstance</code>, namely the standard-quality video to be
     * stored.  If a second instance is provided, it must be the low-quality 
     * copy of the video.  The first instance must provide a resource number and 
     * time (see comment in type definition).  
     */
    STORE_DAY_LONG_MP4,
    
    /**
     * Stores hour-long default nighttime videos for movie-making resources.
     * Videos are produced in both avi and mp4 formats.  To be used in a
     * <code>StorageCommand</code> whose single and first
     * <code>ResourceInstance</code> provides a resource number (see comment in 
     * type definition). The <code>ResourceInstance</code> need not provide a 
     * time or date and should be a <code>ImageInstance</code>.
     */
    STORE_DEFAULT_NIGHT, 
    
    /**
     * Stores hour-long default daytime videos for movie-making resources.
     * Videos are produced in both avi and mp4 formats.  To be used in a
     * <code>StorageCommand</code> whose single and first
     * <code>ResourceInstance</code> provides a resource number (see comment in 
     * type definition). The <code>ResourceInstance</code> need not provide a 
     * time or date and should be a <code>ImageInstance</code>.
     */
    STORE_DEFAULT_DAY,
    
    /**
     * STORE commands which are not specific to a given resource:
     * The first and single <code>ResourceInstane</code> within the 
     * <code>StorageCommand</code> is still used to provide data.
     */
    
    /**
     * Stores the default mp4 image to be used to make a given hour-long video 
     * if no data is available for a given resource at a given time.  To be used
     * in a <code>StorageCommand</code> whose first and single 
     * <code>ResourceInstance</code> is the <code>ImageInstance</code> providing 
     * the image (see comment in type definition).
     */
    STORE_NO_DATA_MP4,
    
    /**
     * Stores the default avi image to be used to make a given hour-long video 
     * if no data is available for a given resource at a given time.  To be used
     * in a <code>StorageCommand</code> whose first and single 
     * <code>ResourceInstance</code> is the <code>ImageInstance</code> providing 
     * the image (see comment in type definition).
     */
    STORE_NO_DATA_AVI,
    
    /**
     * Resource-specific PROVIDE commands:
     * Information about the requested data is provided via an instance of
     * <code>ResourceInstanceRequested</code>.
     */
    
    /**
     * Provides all weather station data.  Also provides images and hour long 
     * videos and images for movie-making resources.   To be used in a 
     * <code>StorageCommand</code> whose <code>ResourceInstanceRequested</code>
     * field supplies the parameters of the request.  These include the range 
     * and resource of the request, the desired extension of any returned files,
     * whether movies or images are requested (used for movie-making resources
     * only) and the maximum number of images requested per hour (when images
     * are requested).  Expected to cause the return of an instance of 
     * <code>ResourceInstanceReturned</code>.
     * @see ResourceInstanceRequested
     */
    PROVIDE,
    
    /**
     * Used to provide day-long mp4 videos of movie-making resources.  To be 
     * used in a <code>StorageCommand</code> whose 
     * <code>ResourceInstanceRequested</code> field supplies the parameters of 
     * the request.  Note that some of the fields from 
     * <code>ResourceInstanceRequested</code> will be ignored.  Only the 
     * resource and range will be used.  Expected to cause the return of an 
     * <code>ArrayList</code> of <code>ResourceInstanceReturned</code>, where
     * array index 0 is to hold the standard-quality movies and array index 1 is
     * to hold the low-quality movies.
     * @see ResourceInstanceRequested 
     */
    PROVIDE_DAY_LONG_MP4,
    
    /**
     * PROVIDE command to provide information about the file system.
     */
    
    /** 
     * Returns a list of the names of the folders currently being used to store 
     * data from resources in the storage system.  Each folder of the returned 
     * list holds all the data for a given resource.  To be used in a
     * <code>StorageCommand</code> with no additional data provided.
     * 
     */
    PROVIDE_FOLDER_LIST;

    public static final long serialVersionUID = 1L;
}
