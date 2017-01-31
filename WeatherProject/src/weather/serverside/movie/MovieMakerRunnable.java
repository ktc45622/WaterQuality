package weather.serverside.movie;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.*;
import weather.common.utilities.*;
import weather.serverside.FFMPEG.ImageVideoMaker;
import weather.serverside.utilities.ResourceCollectionTimeUtility;

/**
 * The Runnable that creates movies for the MovieMakerScheduler.  It is also
 * responsible for checking if it is time is see if the image dimension of a
 * resource have changed and checking the image size when it is time.  The
 * database will be updated if necessary.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class MovieMakerRunnable implements Runnable {

    private final static int minMovieFileLength = 100;
    //TODO: the following should really be a percentage of the pictures we are supposed to get.
    /**
     * Minimum number of instances of a weather camera resource to enable the
     * ability to turn it into a movie.
     */
    private static final int WEATHER_CAM_VALID_INSTANCES = 1;
    /**
     * Minimum number of instances of a weather map resource to enable the
     * ability to turn it into a movie.
     */
    private static final int WEATHER_MAP_VALID_INSTANCES = 1;
    /**
     * The WeatherTracer's Movie log.
     */
    private static final WeatherTracer log = WeatherTracer.getMovieLog();
    /**
     * The Resource from which a movie will be made.
     */
    private final Resource resource;
    /**
     * The storage system the movie will save to.
     */
    private final StorageControlSystem storageSystem;
    /**
     * The time this MovieMakerRunnable is to be run.
     */
    private final GregorianCalendar now;

    /**
     * Constructor for MovieMakerRunnable.
     *
     * @param resource The specified Resource.
     * @param storageSystem The storage system the movie will save to.
     * @param now The time this MovieMakerRunnable is to be run.
     */
    public MovieMakerRunnable(Resource resource, StorageControlSystem storageSystem,
            GregorianCalendar now) {
        this.resource = resource;
        this.storageSystem = storageSystem;
        this.now = now;
    }

    /**
     * Builds and saves the movie when thread runs.
     */
    @Override
    public void run() {
        // Check if the thread is running for the resource's update hour.
        // If so check image dimensions or Internet.
        Debug.println("MovieMakerRunnable dimension check "
                + resource.getName()+" with now = " 
                + CalendarFormatter.formatTime (now));
        
        GregorianCalendar timeZoneCal = new GregorianCalendar();
        timeZoneCal.setTimeInMillis(now.getTimeInMillis());
        timeZoneCal.setTimeZone(resource.getTimeZone().getTimeZone());
        
        if (timeZoneCal.get(GregorianCalendar.HOUR_OF_DAY) == resource.
                getUpdateHour()) {
            Dimension webDim = getCurrentResourceDimension();

            //Try to update resource if necessary.
            if (webDim.height > 0 && webDim.width > 0
                    && (resource.getImageHeight() != webDim.height
                    || resource.getImageWidth() != webDim.width)) {
                //Update a copy is case of errors.
                Resource copiedResource = new Resource(resource);
                copiedResource.setImageWidth(webDim.width);
                copiedResource.setImageHeight(webDim.height);
                copiedResource = storageSystem.getDBMS().getResourceManager()
                        .updateWeatherResource(copiedResource);
                if (copiedResource.getResourceNumber() != -1) {
                    //No errors - update original resource.
                    resource.setImageWidth(copiedResource.getImageWidth());
                    resource.setImageHeight(copiedResource.getImageHeight());
                    
                    Debug.println("Updated " + resource);
                }
            }
        }
        
        Debug.println("END: MovieMakerRunnable dimension check "
                + resource.getName()+" with now = " 
                + CalendarFormatter.formatTime (now));
        
        // Number of resource instances.
        int size = 0;
        // The time that this method was called.
        GregorianCalendar currentTime;
        //This movie will be made for resources collected in the past hour
        GregorianCalendar movieStartTime;
        // Resource is collected between these times.
        GregorianCalendar collectStart, collectEnd;
        // Flag for valid time to make movie.
        boolean isValidTime = false;

        currentTime = new GregorianCalendar();

        log.finest("\nResource is " + resource.getName());

        if (resource.getCollectionSpan() == ResourceCollectionSpan.FullTime) {
            isValidTime = true;
            log.finest("Full time.");
        } // Not full time, need to check if times within collection time.
        else {
            movieStartTime = new GregorianCalendar();

            // pictures for this movie should have started 1 hr before, so wait
            // an hour before we check
            // just to be safe, make sure we do not go back a day
            if (movieStartTime.get(GregorianCalendar.HOUR_OF_DAY) > 0) {
                movieStartTime.add(GregorianCalendar.HOUR_OF_DAY, -1);
            }

            collectStart = ResourceCollectionTimeUtility.getCollectionStartTime(resource, currentTime);
            collectEnd = ResourceCollectionTimeUtility.getCollectionStopTime(resource, currentTime);
            log.finest("Not full time.");
            log.finest("movieStartTime: " + CalendarFormatter.formatTime(movieStartTime));
            log.finest("collectStart: " + CalendarFormatter.formatTime(collectStart));
            log.finest("collectEnd: " + CalendarFormatter.formatTime(collectEnd));
            log.finest("Local variable now is " + CalendarFormatter.formatTime(now));
            log.finest("currentTime is " + CalendarFormatter.formatTime(currentTime));

            // Current time must be within collection time.
            isValidTime = (movieStartTime.compareTo(collectStart) >= 0
                    && movieStartTime.compareTo(collectEnd) <= 0);
        }

        // If not valid, don't make movie.
        // recordMovieCreationFailure not called here because movie not intended to be created
        if (!isValidTime) {
            log.finest("NOT a Valid Time\n");
            now.add(GregorianCalendar.HOUR_OF_DAY, 1); // for next invocation
            return; // necessary return, do not listen to netbeans
        } // Make movie.
        else {
            log.finest("Valid time.\n");

            try {
                Debug.println("Building movie for: " + resource.getResourceName());
                if (!isMakeable()) {
                    Debug.println("Returning... movie is not makeable -- wrong file type: " + resource.getResourceName());
                    // this logging is already performed in isMakeable()
                    //log.severe ("Movie is not makeable: " + resource.getResourceName () + ", #" + resource.getResourceNumber () + ".");
                    weather.common.utilities.WeatherLogger.log(Level.SEVERE, "In MovieMakerRunnable. "
                            + "The movie is not makeable -- wrong file type: " + resource.getResourceName());
                    return;
                }

                ArrayList<ResourceInstance> resourceInstances = fetchResourceInstances();
                Debug.println("fetchResourceInstances() returned "+ resourceInstances.size()+" images.");
                now.add(GregorianCalendar.HOUR_OF_DAY, 1); // for next invocation
                size = resourceInstances.size();

                if (validResourceInstanceSize(size)) {
                    Debug.println("Creating movie instance for: " + resource.getResourceName());
                    LinkedList<AVIInstance> movies = createAVIInstance(resourceInstances);
                    Iterator<AVIInstance> instances = movies.iterator();
                    // loop for each instance, log for each instance that fails
                    Debug.println("The number of movies created for " + resource.getResourceName() + " is  " + movies.size());
                    int count = 0;
                    while (instances.hasNext()) {
                        count++;
                        Debug.println("About to place instance for: " + resource.getResourceName() + " in to the storage system.");
                        AVIInstance movieInstance = instances.next();

                        // check for null instance
                        if (movieInstance == null) {
                            Debug.println("Cannot place instance for: " + resource.getResourceName() + " in to the storage system."
                                    + "  Instance is null.");
                            continue;
                        }
                        Debug.println("count = " + count + " Movie instance class is " + movieInstance.getClass());
                        Debug.println("count = " + count + " Movie instance resource type is " + movieInstance.getResourceType());
                        
                        //Try to place instance.
                        if (storageSystem.placeResourceInstance(movieInstance)) {
                            WeatherLogger.log(Level.SEVERE,
                                    "Unable to place movie instance " + resource.getResourceName() + " into the storage system.");
                            log.severe("Unable to place movie instance into storage system.");
                        }
                    }
                }
                else
                    Debug.println("Not enough images to make the video for "+resource.getResourceName());
            } catch (WeatherException ex) {
                /*
                 String errorMessage = "Unable to retrieve ResourceInstances for this movie.";
                 recordMovieCreationFailure(ResourceFileFormatType.unknown, size, errorMessage);
                 */
                WeatherLogger.log(Level.SEVERE, "The resource instances were "
                        + "unable to be retrieved from the server for resource "
                        + resource.getName(), ex);
                log.severe("The resource instances were "
                        + "unable to be retrieved from the server for resource "
                        + resource.getName(), ex);
            }
        }
    }
    
    /**
     * Looks on the Internet to find the dimensions of the images currently
     * being produced by the given <code>Resource</code>.
     *
     * @return The dimensions in a <code>Dimension</code> object, which will
     * have width and height of zero if an error occurs.
     */
    private Dimension getCurrentResourceDimension() {
        //Set defalt values in case of an error
        int width = 0;
        int height = 0;

        ImageInstance instance = new ImageInstance(resource);

        try {
            instance.readURL(resource.getURL());

            BufferedImage img = (BufferedImage) instance.getImage();
            width = img.getWidth();
            height = img.getHeight();
        } catch (ConnectException | SocketTimeoutException | WeatherException ex) {
            //No work needed.
            Debug.println(ex.getMessage());
        }

        return new Dimension(width, height);
    }


    /**
     * Checks if there is enough ResourceInstances to make a movie.
     *
     * @param size The amount of ResourceInstances.
     * @return True if there are enough ResourceInstances to make a movie, false
     * otherwise.
     */
    private boolean validResourceInstanceSize(int size) {
        boolean value;
        // Minimum valid size.
        int minimum = 0;
        WeatherResourceType type = resource.getResourceType();

        switch (type) {
            case WeatherCamera:
                value = (size >= WEATHER_CAM_VALID_INSTANCES);
                minimum = WEATHER_CAM_VALID_INSTANCES;
                break;
            case WeatherMapLoop:
                value = (size >= WEATHER_MAP_VALID_INSTANCES);
                minimum = WEATHER_MAP_VALID_INSTANCES;
                break;
            default:
                value = false;
                break;
        }

        if (!value) {
            log.severe("Movie unmakeable, " + type + " " + resource.getResourceName() + " does not have enough pictures. Number: " + size + ". Minimum: " + minimum + ".");
            WeatherLogger.log(Level.SEVERE,
                    "Movie unmakeable, " + type + " " + resource.getResourceName() + " does not have enough pictures. Number: " + size + ". Minimum: " + minimum + ".");
        }

        return value;
    }

    /**
     * Fetches the resource instances from the storage system.
     *
     * Gets resources starting at the previous hour and spanning that entire
     * hour (e.g. 03:00:00 - 03:59:59).
     *
     * @return a Vector containing the ResourceInstances.
     * @throws WeatherException
     */
    private ArrayList<ResourceInstance> fetchResourceInstances()
            throws WeatherException {
        ArrayList<ResourceInstance> instances = new ArrayList<>();
        GregorianCalendar rangeStart = (GregorianCalendar) now.clone();
        GregorianCalendar rangeEnd = (GregorianCalendar) now.clone();

        // Start time is an hour earlier than now.
        rangeStart.add(GregorianCalendar.HOUR_OF_DAY, -1);

        rangeEnd.add(GregorianCalendar.HOUR_OF_DAY, -1);
        rangeEnd.set(GregorianCalendar.MINUTE, 59);
        rangeEnd.set(GregorianCalendar.SECOND, 59);
        rangeEnd.set(GregorianCalendar.MILLISECOND, 999);
        Debug.println("Fetching resource instances for resource " + resource.getName()
                + " from time: " + CalendarFormatter.formatTime(rangeStart) + " to " + CalendarFormatter.formatTime(rangeEnd) + ".");
        ResourceRange range = new ResourceRange(new Date(rangeStart.getTimeInMillis()), new Date(rangeEnd.getTimeInMillis()));
        Debug.println("Resource is "+resource.getName() +" format is " + resource.getFormat());
        ResourceInstancesRequested request = 
                new ResourceInstancesRequested(range,  360,false,
                    resource.getFormat(),resource); // assume at most one per second should do better
        ResourceInstancesReturned returned = storageSystem.getResourceInstances(request);

        for (ResourceInstance instance : returned.getResourceInstances()) {
            instances.add(instance);
        }
        Debug.println("Number instances returned was: " + instances.size());

        log.finest("Fetching resource instances for resource " + resource.getName()
                + " from time: " + CalendarFormatter.formatTime(rangeStart) + " to " + CalendarFormatter.formatTime(rangeEnd) + ".");
        log.finest("Number instances: " + instances.size());

        return instances;
    }

    /**
     * Builds two instances of AVIInstance from the images in the provided 
     * vector, the first being an AVI instance and the second being an MP4 
     * instance. An element will be null if that instance could not be made.
     *
     * @param resourceInstances The ResourceInstances used to create a movie.
     * @return A LinkekList with the zeroth element being the AVI AVIInstance
     * and the next element being the MP4 AVIInstance, both of which are null
     * if they cannot be made.
     */
    private LinkedList<AVIInstance> createAVIInstance(
            ArrayList<ResourceInstance> resourceInstances) {
        // Return object.
        LinkedList<AVIInstance> instances = new LinkedList<>();

        GregorianCalendar timeStamp = (GregorianCalendar) now.clone();
        GregorianCalendar rangeStart = (GregorianCalendar) now.clone();
        GregorianCalendar rangeEnd = (GregorianCalendar) now.clone();

        // By this point, now has advanced to prepare for the next hour, so the
        // start time and time stamp are 2 hours earlier than now.
        timeStamp.add(GregorianCalendar.HOUR_OF_DAY, -2);
        rangeStart.add(GregorianCalendar.HOUR_OF_DAY, -2);
        
        // Must save start time as Date object for video maker.
        Date startTime = new Date(rangeStart.getTimeInMillis());
        
        // Set the end time to the end of the same hour.
        rangeEnd.add(GregorianCalendar.HOUR_OF_DAY, -2);
        rangeEnd.set(GregorianCalendar.MINUTE, 59);
        rangeEnd.set(GregorianCalendar.SECOND, 59);
        rangeEnd.set(GregorianCalendar.MILLISECOND, 999);
        
        //Get saved properties.
        int videoLength = Integer.parseInt(PropertyManager
                .getGeneralProperty("MOVIE_LENGTH"));
        String aviCodec = PropertyManager.getServerProperty("FFMPEG_VCODEC");
        
        //Create videos.
        ImageVideoMaker ivm = new ImageVideoMaker(storageSystem, resourceInstances, 
                resource, videoLength, startTime, aviCodec);
        ivm.createVideos();

        //Retrive videos as files.
        File aviFile;
        try {
             aviFile = ivm.getAVI();
             if (aviFile.length() < minMovieFileLength) {
                 WeatherLogger.log(Level.SEVERE,
                         "Movie length was too small for Resource "
                         + resource.getName() + "." + " Length was "
                         + aviFile.length());
                 throw new WeatherException();
             }
        } catch (WeatherException ex) {
            Debug.println("Error getting AVI file.");
            aviFile = null;
        }
        
        File mp4File;
        try {
             mp4File = ivm.getMP4();
             if (mp4File.length() < minMovieFileLength) {
                WeatherLogger.log(Level.SEVERE,
                        "Movie length was too small for Resource "
                        + resource.getName() + "." + " Length was " 
                        + mp4File.length());
                 throw new WeatherException();
             }
        } catch (WeatherException ex) {
            Debug.println("Error getting MP4 file.");
            mp4File = null;
        }
        
        // Make AVI instance.
        AVIInstance myAVI;
        if (aviFile == null) {
            myAVI = null;
        } else {
            try {
                myAVI = new AVIInstance(resource);
                
                myAVI.readFile(aviFile);
                
                myAVI.setTime(new Date(timeStamp.getTimeInMillis()));
                myAVI.setStartTime(rangeStart.getTimeInMillis());
                myAVI.setEndTime(rangeEnd.getTimeInMillis());
            } catch (WeatherException ex) {
                WeatherLogger.log(Level.SEVERE,
                        "Unable to read file when making mivie for Resource "
                        + resource.getName() + ".");
                myAVI = null;
            }
        }
        instances.add(myAVI);

        // Make MP4 instance.
        MP4Instance myMP4;
        if (mp4File == null) {
            myMP4 = null;
        } else {
            try {
                myMP4 = new MP4Instance(resource);
                
                myMP4.readFile(mp4File);
                
                myMP4.setTime(new Date(timeStamp.getTimeInMillis()));
                myMP4.setStartTime(rangeStart.getTimeInMillis());
                myMP4.setEndTime(rangeEnd.getTimeInMillis());
            } catch (WeatherException ex) {
                WeatherLogger.log(Level.SEVERE,
                        "Unable to read file when making mivie for Resource "
                        + resource.getName() + ".");
                myMP4 = null;
            }
        }
        instances.add(myMP4);

        // Clean up and return data.
        ivm.cleanup();
        return instances;
    }

    /**
     * Returns whether a movie can or cannot be made from the provided Resource.
     * If the Resource's ResourceFileFormatType is of a valid image type, we can
     * make a movie.
     *
     * @return True when the Resource is a valid image type, false otherwise.
     */
    private boolean isMakeable() {
        boolean value;
        ResourceFileFormatType format = resource.getFormat();

        switch (format) {
            // All valid formats are inteded to fall through to case image
            case jpeg:
            case gif:
            case png:
            case image:
                value = true;
                break;
            case text:
                value = false;
                break;
            default:
                value = false;
                break;
        }

        if (!value) {
            log.severe("Resource type, " + format
                    + ", is not makeable for resource: " + resource.getName()
                    + ", #" + resource.getResourceNumber());
        }

        return value;
    }
}
