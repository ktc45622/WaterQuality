
package weather.serverside.movie;

import java.io.Serializable;

/**
 * An enumerator that specifies the kinds of commands the MovieMakerServer can execute.
 * 
 * START: Either starts or updates a Resource on the MovieMakerServer.
 * 
 * STOP: Causes a Resource on the MovieMakerServer to stop making movies.
 * 
 * MAKE_TWO_MOVIES: Makes an AVI movie and an MP4 movie out of a provided
 * collection of images.
 * 
 * MAKE_DAY_LONG Makes a day-long MP4 video for a given day using available
 * hour-long videos for a given resource.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public enum MovieCommandType implements Serializable {
    
    /**
     * The Movie command to Start making videos for a particular resource.
     */
    START,
    
    /**
     * The Movie command to Stop making videos for a particular resource.
     */
    STOP,
    
    /**
     * The Movie command to make an AVI movie and an MP4 movie out of a provided
     * collection of images.  This command will return both of the resulting 
     * movies to the calling task.
     */
    MAKE_TWO_MOVIES,
    
    /**
     * The Movie command to make a day-long MP4 video for a given day using
     * available hour-long videos for a given resource.  This command will 
     * return the resulting movie and its low-quality copy to the calling task.
     * It will also save both copies. 
     */
    MAKE_AND_SAVE_DAY_LONG;
    
    public static final long serialVersionUID = 1L;
}
