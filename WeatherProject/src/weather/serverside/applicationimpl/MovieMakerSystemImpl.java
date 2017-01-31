package weather.serverside.applicationimpl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.MovieMakerSystem;
import weather.common.data.resource.AVIInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.movie.MovieCommand;
import weather.serverside.movie.MovieCommandType;
import weather.serverside.movie.MovieMakerClient;

/**
 * An implementation of the MovieMakerSystem.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class MovieMakerSystemImpl implements MovieMakerSystem {

    /**
     * Adds a <code>Resource</code> to <code>MovieMakerSystem</code>, which 
     * will cause the <code>MovieMakerSystem</code> to start making videos 
     * based on this <code>Resource</code>.
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully added.
     */
    @Override
    public boolean addResource(Resource resource) {
        try {
            MovieCommand start = new MovieCommand(MovieCommandType.START, 
                    resource, null, null, 0, null, false);
            MovieMakerClient.executeCommand(start);
            return true;
        } catch (WeatherException we) {
            WeatherLogger.log(Level.SEVERE, null, we);
        }

        return false;
    }

    /**
     * Updates a <code>Resource</code> in the <code>MovieMakerSystem</code>. 
     * This method provides the same functionality as 
     * <code>addResource(Resource)</code>.
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully updated.
     */
    @Override
    public boolean updateResource(Resource resource) {
        return addResource(resource);
    }

    /**
     * Removes a <code>Resource</code> from the <code>MovieMakerSystem</code>.
     * This will cause the <code>MovieMakerSystem</code> to cease making videos
     * of the specified <code>Resource</code>.
     * @param resource The desired <code>Resource</code>.
     * @return True when the <code>Resource</code> is successfully removed.
     */
    @Override
    public boolean removeResource(Resource resource) {
        try {
            MovieCommand stop = new MovieCommand(MovieCommandType.STOP, 
                    resource, null, null, 0, null, false);
            MovieMakerClient.executeCommand(stop);
            return true;
        } catch (WeatherException we) {
            WeatherLogger.log(Level.SEVERE, null, we);
        }
        return false;
    }  

    /**
     * Method to create videos from a provide collection of images. The videos 
     * will be returned as an <code>ArrayList</code> with the zeroth element 
     * being the AVI<code>AVIInstance</code> and the next element being the
     * MP4 <code>AVIInstance</code>.  The <code>ArrayList</code> will have no
     * elements if the videos cannot be made.
     *
     * @param resource Specifies the resource that is to have videos created by
     * the movie making system. This may be null if the request is to create
     * generic NoData videos.
     * @param images The images the Movie system will use to create the videos.
     * @param startTime A <code>Date</code> that specifies the time when the
     * requested videos are to start. May be null if the request is to create a
     * default daytime or nighttime video for a particular resource or a generic
     * NoData video.
     * @param videoLength The length of any video to be created from images, in
     * seconds.
     * @param aviCodec The codec to be used for creating an AVI video with
     * FFmpeg. Is is needed when the command is making videos from images.
     * @return An <code>ArrayList</code> with the zeroth element being the AVI
     * <code>AVIInstance</code> and the next element being the MP4
     * <code>AVIInstance</code>.  The <code>ArrayList</code> will have no
     * elements if the videos cannot be made.
     */
    @Override
    public ArrayList<AVIInstance> makeMovies(Resource resource, 
            ArrayList<ResourceInstance> images, Date startTime, int videoLength, 
            String aviCodec) {
        MovieCommand command = new MovieCommand(MovieCommandType
                .MAKE_TWO_MOVIES, resource, images, startTime, videoLength, 
                aviCodec, false);
        try {
            Object returnedObject = MovieMakerClient.sendCommand(command);
            return (ArrayList<AVIInstance>)returnedObject;
        } catch (WeatherException ex) {
            //Couldn't get data; send back empty list.
            ArrayList<AVIInstance> emptyList = new ArrayList<>();
            return emptyList;
        }
    }

    /**
     * Method to create day-long video and its low-quality copy from hour-long
     * video that are already in the storage system. Both copies will fill
     * missing hours with "No Data" videos, be saved to the storage system, be
     * in MP4 format, and be returned as a <code>AVIInstance</code>. The two
     * instances of <code>AVIInstance</code> will be in an
     * <code>ArrayList</code> with the zeroth element being the standard-quality
     * movie and the next element being the low-quality copy.  If the third
     * parameter is false, the low-quality element will be a day-long "no-data" 
     * video.  The  <code>ArrayList</code> will have no elements if the videos 
     * cannot be made. Any existing videos will be overridden.
     * 
     * @param resource Specifies the resource that is to have the video created
     * by the movie making system. 
     * @param startTime A <code>Date</code> that specifies the time when the 
     * requested videos are to start.
     * @param makingLowQuality A boolean to specify whether or not the
     * low-quality copy of a day-long video is to be made.
     * @return An <code>ArrayList</code> of type <code>AVIInstance</code> 
     * holding the day-long MP4 movie at index 0 and its low-quality copy at
     * index 1.  The<code>ArrayList</code> will have no elements if the videos 
     * cannot be made.
     */
    @Override
    public ArrayList<AVIInstance> makeAndSaveDayLongMP4(Resource resource, 
            Date startTime, boolean makingLowQuality) {
        MovieCommand command = new MovieCommand(MovieCommandType
                .MAKE_AND_SAVE_DAY_LONG, resource, null, startTime, 0, null,
                makingLowQuality);
        try {
            Object returnedObject = MovieMakerClient.sendCommand(command);
            return (ArrayList<AVIInstance>)returnedObject;
        } catch (WeatherException ex) {
            //Couldn't get data; send back empty list.
            ArrayList<AVIInstance> emptyList = new ArrayList<>();
            return emptyList;
        }
    }
}
