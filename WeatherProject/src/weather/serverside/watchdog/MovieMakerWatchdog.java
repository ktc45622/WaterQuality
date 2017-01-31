package weather.serverside.watchdog;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.data.resource.WeatherResourceType;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.movie.MovieCommand;
import weather.serverside.movie.MovieCommandType;
import weather.serverside.movie.MovieMakerClient;
import weather.serverside.retrieval.RetrievalClient;
import weather.serverside.retrieval.RetrievalCommand;
import weather.serverside.retrieval.RetrievalCommandType;
import weather.serverside.utilities.ResourceCollectionTimeUtility;
import weather.serverside.utilities.ServiceControl;
import weather.serverside.utilities.WeatherServiceNames;

/**
 * This watchdog monitors the MovieMaker system for errors. Refactored from the
 * old ServerWatchdog
 *
 * @author Joe Sharp
 * @author Dustin Jones (2010)
 * @author Ryan Kelly (2010)
 * @author Eric Subach (2010)
 * @author Patrick Brinich (2013)
 */
public class MovieMakerWatchdog extends ServerWatchdog {

    /**
     * Base directory of storage.
     */
    private static String storageRootBase;
    /**
     * Storage control system
     */
    private StorageControlSystem storageSystem;
    /**
     * Map containing resource IDs and when they last caused a notification (ms)
     */
    private Map<Integer, Long> notificationMap;
    /**Number of consecutive failures for a resource ID*/
    private Map<Integer, Integer> failureCounts;
    /**
     * Array containing the error and action descriptions for a URL error
     */
    private static final String[] URL_ERROR = {"The Resource URL is not producing images.", 
                                               "No action taken.",
                                               "No action taken."};
    /**
     * Array containing the error and action descriptions for when there are no images
     */
    private static final String[] NO_IMAGES = {"No images were available to make a movie.",
                                               "Collection and movie making wer restarted for the resource.",
                                               "Retrieval and MovieMaker systems were restarted."};
    /**
     * Array containing the error and action descriptions for when there was no movie made
     */
    private static final String[] NO_MOVIE  = {"No movie was made.", 
                                               "Movie making was restarted for the resource.",
                                               "The MovieMaker system was restarted."};
    
    
    /**
     * Constructs a new MovieMakerWatchdog
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws WeatherException
     */
    public MovieMakerWatchdog() throws ClassNotFoundException, InstantiationException, IllegalAccessException, WeatherException {
        super();
        // Retrieve our base storage folder.
        storageRootBase = PropertyManager.getServerProperty("storageRootFolder");
        super.log.finer("Retrieved base storage folder.");
        storageSystem = super.getStorageControlSystemLocal();
        notificationMap = new HashMap<>();
        failureCounts   = new HashMap<>();
    }

    /**
     * Checks if the Movie Maker Service is running
     * Philadelphia 
     * @return true if the service is running; false otherwise.
     */
    @Override
    public boolean isServiceRunning() {
        return ServiceControl.isRunning(WeatherServiceNames.MOVIE);
    }

    /**
     * Restarts the Movie Maker System and notifies the admin that the service
     * was stopped.
     */
    @Override
    public void handleStoppedService() {
        ServiceControl.restartService(WeatherServiceNames.MOVIE);
        notifyServiceStopped();
    }

    /**
     * Checks for Movie Maker errors. Checks per resource. We consider an error
     * has occurred if one of the following is true: A.) The resource URL is not
     * producing images. B.) No images are available for the resource in
     * storage. C.) No movie has been made.
     *
     * @param resources all resources. This method checks for only Camera and
     * Map Loop resources.
     */
    @Override
    public void checkSystem(Vector<Resource> resources) {
        WeatherResourceType type;
        boolean restartRetrieval  = false;
        boolean restartMovieMaker = false;
        for (Resource resource : resources) {
            type = resource.getResourceType();

            // If active and is either a weather camera or weather
            // map loop.
            if (resource.isActive()
                    && (type == WeatherResourceType.WeatherCamera
                    || type == WeatherResourceType.WeatherMapLoop)
                    && resourceCollectsNow(resource)) {

                Debug.println("Checking Resource: "+resource.getStorageFolderName());
                
                if (!isURLProducingImages(resource)) {
                    Debug.println("\tURL not producing images");
                    incrementFailureCount(resource);
                    notifyResourceError(resource, URL_ERROR);
                    continue;
                }
                if (resourceCollectsOneHourAgo(resource) && !hasImages(resource)) {
                    Debug.println("\tNo Images");
                    incrementFailureCount(resource);
                    notifyResourceError(resource, NO_IMAGES);
                    restartResource(resource);
                    restartMovieMaking(resource);
                    
                    if(failureCounts.get(resource.getResourceNumber())>=2) {
                        restartRetrieval  = true;
                        restartMovieMaker = true;
                    }
                    continue;
                }
                if (resourceCollectsOneHourAgo(resource) && !hasMovie(resource)) {
                    Debug.println("\tNo movie");
                    incrementFailureCount(resource);
                    notifyResourceError(resource, NO_MOVIE);
                    restartMovieMaking(resource);
                    
                    if(failureCounts.get(resource.getResourceNumber())>=2) {
                        restartMovieMaker = true;
                    }
                    continue;
                }
                Debug.println("\tNo current MovieMaker System error for resource");
                failureCounts.put(resource.getResourceNumber(),0);
            }
        }
        
        if(restartRetrieval) {
            ServiceControl.restartService(WeatherServiceNames.RETRIEVAL);
        }
        if(restartMovieMaker) {
            ServiceControl.restartService(WeatherServiceNames.MOVIE);
        }
    }

    /**
     * Checks if a resource's url is producing images.
     *
     * @param resource the Resource to check
     * @return true if the url is producing images; false otherwise
     */
    private boolean isURLProducingImages(Resource resource) {
        ResourceInstance ri = new ImageInstance(resource);
        try {
            ri.readURL(resource.getURL());
            return true;
        } catch (IOException | WeatherException e) {
            return false;
        }
    }

    /**
     * Checks if the storage system has images for a given Resource at the
     * current time.
     *
     * @param resource the Resource to check
     * @return true if the storage system has images; false otherwise.
     */
    private boolean hasImages(Resource resource) {
        try {
            Vector<ResourceInstance> ris = fetchInstancesLastHour(resource, false);
            Debug.println("\tNumber of ImageInstances: " + ris.size());
            return ris.size() > 0;
        } catch (WeatherException ex) {
            return false;
        }
    }

    /**
     * Checks if the storage system has a movie for the given Resource at the
     * current time.
     *
     * @param resource the Resource to check
     * @return true if the storage system has a movie; false otherwise.
     */
    private boolean hasMovie(Resource resource) {
        long time = System.currentTimeMillis();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        
        //Check for movies for the last hour
        cal.add(Calendar.HOUR, -1);
        time = cal.getTimeInMillis();
        
        return hasMovieForTime(resource, time);     
    }
    
    /**
     * Checks if a movie was made for a particular Resource for a particular time
     * Credit to storage system authors for original algorithm.
     * @param resource the Resource to check
     * @param time the time the movie should have been made. Since movies 
     *             are made on the hour, minutes and seconds are insignificant.
     * @return true if a movie has been made; false otherwise.
     */
    private boolean hasMovieForTime(Resource resource, long time) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        
        String ext = ".avi";
        String resFolder   = storageRootBase + File.separator + resource.getStorageFolderName() + File.separator;
        String yearFolder  = cal.get(Calendar.YEAR) + File.separator;
        String monthFolder = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + File.separator;
        String dayFolder   = cal.get(Calendar.DAY_OF_MONTH) + File.separator;
        String movieFolder = "movies" + File.separator;
        
        StringBuffer path = new StringBuffer();
        path.append(resFolder).append(yearFolder).append(monthFolder);
        path.append(dayFolder).append(movieFolder);
        
        //build file name 
        //<resourceStorage>YYYYMMDD-HHMMSS
        path.append(resource.getStorageFolderName());
        path.append(cal.get(Calendar.YEAR));
        
        if(cal.get(Calendar.MONTH) < 9) {
            path.append("0");
        }
        path.append(cal.get(Calendar.MONTH) + 1);
        
        if(cal.get(Calendar.DAY_OF_MONTH) < 10)
            path.append("0");
        path.append(cal.get(Calendar.DAY_OF_MONTH));
        path.append("-");
        
        if(cal.get(Calendar.HOUR_OF_DAY) < 10)
            path.append("0");
        path.append(cal.get(Calendar.HOUR_OF_DAY));
        path.append("0000");
        path.append(ext);
        
        File file = new File(path.toString());
        return file.exists();
    }

    /**
     * Checks if the specified Resource is collecting at the current time.
     *
     * @param resource the Resource to be checked
     * @return true if the Resource is collecting; false otherwise.
     */
    private boolean resourceCollectsNow(Resource resource) {
        GregorianCalendar curDate = new GregorianCalendar();
        curDate.setTimeInMillis(System.currentTimeMillis());
        return ResourceCollectionTimeUtility.validDataCollectionTime(resource, curDate);
    }
    
    /**
     * Checks if the specified Resource was collecting an hour earlier.
     *
     * @param resource the Resource to be checked
     * @return true if the Resource is collecting; false otherwise.
     */
    private boolean resourceCollectsOneHourAgo(Resource resource) {
        GregorianCalendar date = new GregorianCalendar();
        date.setTimeInMillis(System.currentTimeMillis());
        date.add(GregorianCalendar.HOUR, -1);
        return ResourceCollectionTimeUtility.validDataCollectionTime(resource, date);
    }

    /**
     * Fetches the ResourceInstances for a particular Resource for the last
     * whole hour.
     *
     * @param resource the Resource whose ResourceInstances will be fetched.
     * @param wantMovie Set true if a movie should be fetched; false for images.
     * @return a vector containing all ResourceInstances for the last whole
     * hour.
     * @throws WeatherException
     */
    private Vector<ResourceInstance> fetchInstancesLastHour(Resource resource, boolean wantMovie) throws WeatherException {
        if (resource == null) return new Vector<ResourceInstance> (); 
        ResourceFileFormatType type = resource.getFormat();
        if (wantMovie) {
            type = ResourceFileFormatType.avi;
        }

        GregorianCalendar rangeStart = new GregorianCalendar(); //current time
        GregorianCalendar rangeEnd = new GregorianCalendar();

        if (wantMovie) {
            //@TODO: double check if this is the right time frame
            //Change start date to the exact hour + 15 min before now.
            rangeStart.add(GregorianCalendar.HOUR_OF_DAY, -1);
            rangeEnd.set(GregorianCalendar.MINUTE, 15);
            rangeEnd.set(GregorianCalendar.SECOND, 0);
            rangeEnd.set(GregorianCalendar.MILLISECOND, 0);
            
            rangeEnd.set(GregorianCalendar.MINUTE, 15);
            rangeEnd.set(GregorianCalendar.SECOND, 0);
            rangeEnd.set(GregorianCalendar.MILLISECOND, 0);
        } else {
            //Change start date to the exact hour before now.
            rangeStart.add(GregorianCalendar.HOUR_OF_DAY, -1);
            rangeEnd.set(GregorianCalendar.MINUTE, 0);
            rangeEnd.set(GregorianCalendar.SECOND, 0);
            rangeEnd.set(GregorianCalendar.MILLISECOND, 0);

            //change end date to right before one hour later than the start date
            rangeEnd.add(GregorianCalendar.HOUR_OF_DAY, -1);
            rangeEnd.set(GregorianCalendar.MINUTE, 59);
            rangeEnd.set(GregorianCalendar.SECOND, 59);
            rangeEnd.set(GregorianCalendar.MILLISECOND, 999);
        }

        ResourceRange range = new ResourceRange(new java.sql.Date(rangeStart.getTimeInMillis()), new java.sql.Date(rangeEnd.getTimeInMillis()));
        ResourceInstancesRequested request = 
                new ResourceInstancesRequested(range,  360, 
                wantMovie, type,resource);
        ResourceInstancesReturned returned = storageSystem.getResourceInstances(request);

        return returned.getResourceInstances();
    }

    /**
     * Notifies the admin and logs that the service was stopped via 
     * ServerWatchdogErrorEvent
     */
    private void notifyServiceStopped() {
        String system = WeatherServiceNames.MOVIE.getShortName();
        String error  = "The system service was stopped.";
        String action = "The system service was restarted.";
        String info = "This may indicate an error with the the system.";
        
        ServerWatchdogErrorEvent event = new ServerWatchdogErrorEvent (system, error, 
                                        action, info, null);
        event.logError();
        event.notifyAdmin();
        super.addServerWatchdogErrorEvent(event);
    }

    /**
     * Notifies the admin and logs that there was a resource error via
     * ServerWatchdogErrorEvent
     */
    private void notifyResourceError(Resource resource, String[] errorAndAction) {
        long currentTime = System.currentTimeMillis();
        int  resID       = resource.getResourceNumber();
        
        String system = WeatherServiceNames.MOVIE.getShortName();
        String error  = errorAndAction[0];
        String action = errorAndAction[1];
        String info = "This may indicate an error with the resource or the system." + " "
                    + "This message will be sent once daily until the issue is resolved.";
        
        if (failureCounts.containsKey(resID) && failureCounts.get(resID) >= 2) {
            action = errorAndAction[2];
            info = "This resource has failed at least twice in a row. " + info;
        }
        
        ServerWatchdogErrorEvent event = new ServerWatchdogErrorEvent(system, error, 
                                        action, info, resource, currentTime);
        event.logError();
        event.printErrorToDebug();
        
        if (!resourceIDCanNotify(resID, currentTime)) {
            Debug.println("Notification not sent for "+ resource.getResourceName());
        } else {
            //Store when this resource ID last notified an admin.
            event.notifyAdmin();
            notificationMap.put(resID, currentTime);
        }
        
        super.addServerWatchdogErrorEvent(event);
    }
    
    /**
     * Checks if a particular resource ID is allowed to send out a
     * notification email.
     * @param resID the Resource number identifying a resource
     * @param time the time the resource id wants to notify an admin
     * @return true if the resource id is allowed to notify; false otherwise
     */
    private boolean resourceIDCanNotify(int resID, long time) {
        if(failureCounts.containsKey(resID) && (failureCounts.get(resID) == 2)){
            return true;
        }
        if (notificationMap.containsKey(resID)){
            long lastTime   = notificationMap.get(resID);
            long difference = time-lastTime;
            if (difference >= ResourceTimeManager.MILLISECONDS_PER_DAY) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }
    
    /**
     * Restarts collection for a particular resource
     * @param resource the Resource for which collection is restarted
     */
    private void restartResource(Resource resource) {
        RetrievalCommand stop  = new RetrievalCommand(RetrievalCommandType.STOP, resource);
        RetrievalCommand start = new RetrievalCommand(RetrievalCommandType.START, resource);
        
        try {
            RetrievalClient.sendCommand(stop);
            RetrievalClient.sendCommand(start);
        } 
        //UnknownHostException is a subclass of IOException
        catch (IOException ex) {
            super.log.severe("Could not restart retrieval of resource: " + resource.getResourceName(), ex);
        }
    }
    
    /**
     * Restarts movie making for a particular resource
     * @param resource the Resource for which movie making is restarted.
     * <b>Must be a resource for which a movie can be made.</b>
     */
    private void restartMovieMaking(Resource resource) {
        MovieCommand stop = new MovieCommand(MovieCommandType.STOP, resource,
            null, null, 0, null, false);
        MovieCommand start = new MovieCommand(MovieCommandType.START, resource,
            null, null, 0, null, false);
        
        try {
            MovieMakerClient.executeCommand(stop);
            MovieMakerClient.executeCommand(start);
        } catch (WeatherException ex) {
            super.log.severe("Could not restart movie making for resource: " 
                             + resource.getResourceName(), ex);
        }
        
    }
    
    /**
     * Increments the consecutive failure count in <code>failureCounts</code>
     * for a Resource. Sets the failure count to 1 if <code>failureCounts</code>
     * doesn't contain an entry for the resource's resource number.
     * @param resource The Resource for which failure count is incremented.
     */
    private void incrementFailureCount(Resource resource) {
        int resNum = resource.getResourceNumber();
        
        if(failureCounts.containsKey(resNum)) {
            int count = failureCounts.get(resNum);
            failureCounts.put(resNum, count+1);
        } else {
            failureCounts.put(resNum, 1);
        }
    }
}
