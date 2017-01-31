
package weather;

import java.sql.Date;
import java.util.ArrayList;
import weather.common.data.resource.*;
import weather.common.dbms.ResourceChangeListener;


/**
 * This interface specifies the operations supported by
 * the Movie Maker System server.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public interface MovieMakerSystem extends ResourceChangeListener{

    /**
     * Specifies the signature of the method to add a Resource to 
     * MovieMakerSystem, which will cause the MovieMakerSystem to start making
     * videos based on this Resource.
     * 
     * @param resource The desired Resource.
     * @return True when the Resource is successfully added.
     */
    @Override
    public boolean addResource(Resource resource);

    /**
     * Specifies the signature of the method to update a Resource in the
     * MovieMakerSystem. This method provides the same
     * functionality as <code>addResource()</code>.
     * 
     * @param resource The desired Resource.
     * @return True when the Resource is successfully updated.
     */
    @Override
    public boolean updateResource(Resource resource);

    /**
     * Specifies the signature of the method to remove a Resource from the
     * MovieMakerSystem. This will cause the MovieMakerSystem
     * to cease making videos of the specified Resource.
     * 
     * @param resource The desired Resource.
     * @return True when the Resource is successfully removed.
     */
    @Override
    public boolean removeResource(Resource resource);
    
    /**
     * Specifies the signature of the method to create videos from a provided
     * collection of images. The videos will be returned as an
     * <code>ArrayList</code> with the zeroth element being the AVI
     * <code>AVIInstance</code> and the next element being the MP4
     * <code>AVIInstance</code>. The <code>ArrayList</code> will have no
     * elements if the videos cannot be made.
     * 
     * @param resource Specifies the resource that is to have videos created by
     * the movie making system.  This may be null if the request is to create 
     * generic NoData videos.
     * @param images The images the Movie system will use to create the videos.
     * @param startTime A <code>Date</code> that specifies the time when the 
     * requested videos are to start. May be null if the request is to create 
     * a default daytime or nighttime video for a particular resource or a 
     * generic NoData video.
     * @param videoLength The length of any video to be created from images, in
     * seconds.
     * @param aviCodec The codec to be used for creating an AVI video with
     * FFmpeg. Is is needed when the command is making videos from images.
     * @return An <code>ArrayList</code> with the zeroth element being the AVI
     * <code>AVIInstance</code> and the next element being the MP4
     * <code>AVIInstance</code>.  The <code>ArrayList</code> will have no
     * elements if the videos cannot be made.
     */
    public ArrayList<AVIInstance> makeMovies(Resource resource, 
            ArrayList<ResourceInstance> images, Date startTime, int videoLength,
            String aviCodec);
    
    /**
     * Specifies the signature of the method to create day-long video and its
     * low-quality copy from hour-long video that are already in the storage
     * system. Both copies will fill missing hours with "No Data" videos, be
     * saved to the storage system, be in MP4 format, and be returned as a
     * <code>AVIInstance</code>. The two instances of <code>AVIInstance</code>
     * will be in an <code>ArrayList</code> with the zeroth element being the
     * standard-quality movie and the next element being the low-quality copy.
     * If the third parameter is false, the low-quality element will be a
     * day-long "no-data" video. The <code>ArrayList</code> will have no
     * elements if the videos cannot be made. Any existing videos will be
     * overridden.
     *
     * @param resource Specifies the resource that is to have the video created
     * by the movie making system.
     * @param startTime A <code>Date</code> that specifies the time when the
     * requested videos are to start.
     * @param makingLowQuality A boolean to specify whether or not the
     * low-quality copy of a day-long video is to be made.
     * @return An <code>ArrayList</code> of type <code>AVIInstance</code>
     * holding the day-long MP4 movie at index 0 and its low-quality copy at
     * index 1. The<code>ArrayList</code> will have no elements if the videos
     * cannot be made.
     */
    public ArrayList<AVIInstance> makeAndSaveDayLongMP4(Resource resource, 
            Date startTime, boolean makingLowQuality);
    
    
}
