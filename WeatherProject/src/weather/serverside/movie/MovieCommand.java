package weather.serverside.movie;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;

/**
 * 
 * A class that represents a command that can be executed on the
 * MovieMakerServer.
 * 
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class MovieCommand implements Serializable {

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     * @serial
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The command to be executed upon the Resource. This command type will
     * specify if a the video system should start or stop creating movies for a
     * resource every hour, if videos are being requested based on the images
     * sent to the system, or if a day-long movie is to be made from existing
     * hour-long movies.  Note that videos being made from images are returned
     * in pairs with both an AVI and an MP4 copy.
     */
    private MovieCommandType commandType;
    
    /**
     * Specifies the resource that is to have video(s) created by the movie
     * making system. Depending on the movie command, it could be a request to
     * create videos every hour, a request to make a day-long video or a request
     * for videos to be created from images  and returned. One use of image
     * video creation is to create a default daytime or nighttime video for a
     * particular resource. In this case the images array list may contain only
     * 1 image.
     *
     * May be null if the command is to create generic NoData videos.
     */
    private Resource resource;
    
    /**
     * Holds the image(s) to be used to make a video pair. If a no data video is
     * being requested, then the size of this vector should be 1.  This field is
     * ignored if the command is not making videos from images.
     *
     * @see MovieCommandType Note: ResourceInstances is used for Flexibility, it
     * could also be ImageInstance. The video creation code will need to check
     * the actual type of any entry in this vector list and ensure it is a type
     * it can use to create a video.
     */
    private ArrayList<ResourceInstance> images;
    
    /**
     * A <code>Date</code> that specifies the time when a single requested video
     * or video pair is to start.  It is ignored if the command is starting or
     * stopping a resource.
     * 
     * May be null if the command is to create a default daytime or nighttime 
     * video for a particular resource or a generic NoData video.
     */
    private Date startTime;
    
    /**
     * The length of any video to be created from images, in seconds.  This will
     * be ignored if the command is not making videos from images.
     */
    private int videoLength;
    
    /**
     * The codec to be used for creating an AVI video with FFmpeg.  Is is needed
     * when the command is making videos from images.  This will be ignored if 
     * the command is not making videos from images.
     */
    private String aviCodec;
    
    /**
     * A boolean to specify whether or not the low-quality copy of a day-long 
     * video is to be made.  This will be ignored if the command is not making
     * day-long videos.
     */
    private boolean makingLowQuality;
    
    /**
     * Constructor for a MovieCommand.
     *
     * @param commandType The desired command type to be executed. This command
     * type will specify if a the video system should start or stop creating
     * movies for a resource every hour, if videos are being requested based on
     * the images sent to the system, or if a day-long movie is to be made from
     * existing hour-long movies. Note that videos being made from images are
     * returned in pairs with both an AVI and an MP4 copy.
     * @param resource Specifies the resource that is to have video(s) created
     * by the movie making system. Depending on the movie command, it could be a
     * request to create videos every hour, a request to make a day-long video
     * or a request for videos to be created from images and returned. This may
     * be null if the command is to create generic NoData videos.
     * @param images The images the Movie system will use to create a video.
     * @param startTime A <code>Date</code> that specifies the time when a
     * single requested video or video pair is to start. It is ignored if the
     * command is starting or stopping a resource. May be null if the command is
     * to create a default daytime or nighttime video for a particular resource
     * or a generic NoData video.
     * @param videoLength The length of any video to be created from images, in
     * seconds. This will be ignored if the command is not making videos from
     * images.
     * @param aviCodec The codec to be used for creating an AVI video with
     * FFmpeg. Is is needed when the command is making videos from images. This
     * will be ignored if the command is not making videos from images.
     * @param makingLowQuality A boolean to specify whether or not the 
     * low-quality copy of a day-long video is to be made.  This will be ignored 
     * if the command is not making day-long videos.
     */
    public MovieCommand(MovieCommandType commandType, Resource resource, 
            ArrayList<ResourceInstance> images, Date startTime, int videoLength,
            String aviCodec, boolean makingLowQuality) {
        this.commandType = commandType;
        this.resource = resource;
        this.images = images;
        this.startTime = startTime;
        this.videoLength = videoLength;
        this.aviCodec = aviCodec;
        this.makingLowQuality = makingLowQuality;
    }
    
    /**
     * Returns the SerialVersionUID.
     * @return The SerialVersionUID.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Returns the MovieCommandType.
     * @return The MovieCommandType.
     */
    public MovieCommandType getCommandType() {
        return commandType;
    }

    /**
     * Sets the MovieCommandType.
     * @param commandType The desired MovieCommandType.
     */
    public void setCommandType(MovieCommandType commandType) {
        this.commandType = commandType;
    }

    /**
     * Returns the Resource.
     * @return The Resource.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the Resource and ensures the ResourceInstance is null.
     * @param resource The Resource.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    } 
    
    /**
     * Returns the <code>ArrayList</code> of type <code>ResourceInstance</code>
     * holding the images.
     * @return The <code>ArrayList</code> of type <code>ResourceInstance</code>
     * holding the images. 
     */
    public ArrayList<ResourceInstance> getImages() {
        return images;
    }

    /**    
     * Sets the <code>ArrayList</code> of type <code>ResourceInstance</code>
     * holding the images.   
     * @param images The <code>ArrayList</code> of type 
     * <code>ResourceInstance</code> holding the images.
     */
    public void setImages(ArrayList<ResourceInstance> images) {
        this.images = images;
    }
    
    /**
     * Returns the start time to be used for video creation.
     * @return The start time to be used for video creation.
     */
    public Date getStartTime() {
        return startTime;
    }
    
    /**
     * Sets the start time to be used for video creation.
     * @param startTime start time to be used for video creation.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Returns the length of the video to be made in seconds.
     * @return The length of the video to be made in seconds.
     */
    public int getVideoLength() {
        return videoLength;
    }
    
    /**
     * Sets the length of the video to be made in seconds.
     * @param videoLength The length of the video to be made in seconds.
     */
    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }
    
    /**
     * Returns the video codec to be used for AVI video creation.
     * @return The video codec to be used for AVI video creation.
     */
    public String getAVICodec() {
        return aviCodec;
    }
    
    /**
     * Sets video codec to be used for AVI video creation.
     * @param aviCodec The video codec to be used for AVI video creation.
     */
    public void setAVICodec(String aviCodec) {
        this.aviCodec = aviCodec;
    }

    /**
     * Returns the boolean to specify whether or not the low-quality copy of a
     * day-long video is to be made. 
     * @return The boolean to specify whether or not the low-quality copy of a day-long 
     * video is to be made.  
     */
    public boolean getMakingLowQuality() {
        return makingLowQuality;
    }
    
    /**
     * Sets the boolean to specify whether or not the low-quality copy of a
     * day-long video is to be made. This will be ignored if the command is not
     * making day-long videos.
     * @param makingLowQuality A boolean to specify whether or not the
     * low-quality copy of a day-long video is to be made.
     */
    public void setMakingLowQuality(boolean makingLowQuality) {
        this.makingLowQuality = makingLowQuality;
    }
}
