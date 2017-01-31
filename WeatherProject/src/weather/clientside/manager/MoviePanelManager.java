package weather.clientside.manager;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.JPanel;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.utilities.WeatherException;

/**
 * This class specifies the operations needed in any class 
 * that contains video and wants to be controlled by a MovieController.
 * This includes playing and stopping the video, setting the play rate, and
 * extracting snapshots from a video.
 * 
 * @see MovieController
 * @author Joe Sharp
 */
public interface MoviePanelManager{

    /**
     * Specifies the signature of the method to return the main panel from this 
     * class to display in the calling window.
     */
    public JPanel getPanel(int width, int height);

    /**
     * Specifies the signature of the method to set the resource range for this 
     * manager.  Retrieves any resource instances needed for this range from the 
     * ResourceTreeManager.
     */
    public void setResourceRange(ResourceRange range);

    /**
     * Specifies the signature of the method to set the size of the move in the 
     * calling window.
     * @param width The width of the movie.
     * @param height The height of the movie.
     */
    public void setMovieSize(int width, int height);
    
    /**
     * Specifies the signature of a method to set the resource currently 
     * displayed by the panel. Uses the first appropriate resource in the given
     * list  The change id only guaranteed to take effect when the range is next
     * set.
     * @param resources A list of resources, the first appropriate resource will
     *      be displayed by the panel.
     */
    public void setFutureResource(Vector<Resource> resources);

    /**
     * Specifies the signature of the method to cause the video contained in 
     * this manager to stop playing.
     */
    public void stop();

    /**
     * Specifies the signature of the method to set the rate of play for the
     * video contained in this manager.
     * @param rate The rate of play.
     * @param isPlaying Whether or not the video is currently playing.
     */
    public void setMovieRate(float rate, boolean isPlaying);
    
    /**
     * Checks if the contained video player is playing.
     * @return If the contained video player is playing.
     */
    public boolean isPlaying();

    /**
     * Specifies the signature of the method to set the progress of the movie to
     * the given seconds.
     * @param progress The tick count of the movie that has played.
     */
    public void setMovieProgress(int progress);
    
    /**
     * Resets the player back to it's original state.
     */
    public void reset();
    
    /**
     * Returns the number of seconds into the video of the image that is
     * showing.
     * @return The number of seconds into the video of the image that is
     * showing.
     */
    public int getCurrentVideoSecond();

    /**
     * Specifies the signature of the method to cleanup the contained Player.
     */
    public void cleanup();

    /**
     * Specifies the signature of the method to save the movie.
     * @param filenames The list of file names to hold the movie segments
     * @throws WeatherException if save is not successful.
     */
    public void saveMovie(ArrayList<String> filenames) throws WeatherException;

    /**
     * Specifies the signature of the method to allow a snapshot to be obtained from a video.
     * This can be used for exporting/saving and printing pictures.
     * 
     * @return A snapshot from the video.
     */
    public BufferedImage getPictFromMovie();

    /**
     * Specifies the signature of the method to return the resource currently
     * used by this manager.
     * @return The resource currently in use, -1 if there is no resource, or -2
     * if this is a local file viewer.
     */
    public int getCurrentResourceNumber();
    
    /**
     * Gets the <code>TimeZone</code> in which the current video was taken.
     * @return the <code>TimeZone</code> in which the current video was taken 
     * or the local <code>TimeZone</code> if there is no video.
     */
    public TimeZone getTimeZone();
    
    /**
     * Sets resource to none.
     */
    public void setToNone();
    
    /**
     * Returns true if instance is weather camera manager, false if not.
     * @return Is this a Weather Camera Manager?
     */
    public boolean isCameraManager();
    
    /**
     * Tests if panel is showing none.
     * @return True if panel shows none, false otherwise
     */
    public boolean isSetToNone();
}
