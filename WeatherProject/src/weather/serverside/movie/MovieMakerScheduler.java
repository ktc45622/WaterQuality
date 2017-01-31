
package weather.serverside.movie;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherTracer;

/**
 * A class responsible for scheduling movie creation.
 *
 * Currently, this class forces the system to make one movie at a time.
 * 
 * NOTE: A <code>Future</code> represents the result of an asynchronous
 *        computation. Methods are provided to check if the computation is
 *        complete, to wait for its completion, and to retrieve the result of
 *        the computation.
 *
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class MovieMakerScheduler extends ScheduledThreadPoolExecutor {
    /**
     * Core pool size is 1 so only one movie will be made at a time to prevent
     * thrashing.
     */
    private static final int POOL_SIZE = 1;
    
    /**
     * Movie log.
     */
    private static WeatherTracer log = WeatherTracer.getMovieLog();

    /**
     * Map of the tasks for movie creation. 
     * Maps the resource ID with the Future object.
     */ 
    private HashMap<Integer, Future> futureMap;
    
    /**
     * The storage system the movie will save to.
     */
    private StorageControlSystem storageSystem;

    /**
     * The constructor for MovieMakerScheduler.
     * 
     * @param storageSystem The storage system movies will be stored on.
     */
    public MovieMakerScheduler(StorageControlSystem storageSystem) {
        super(POOL_SIZE);

        this.storageSystem = storageSystem;
        this.futureMap = new HashMap<Integer, Future>();

        Debug.println("MovieMakerScheduler's constructor invoked.");
    }
    
    /**
     * Returns the storage system.
     * 
     * @return The storage system. 
     */
    public StorageControlSystem getStorageControlSystem() {
        return this.storageSystem;
    }
    
    /**
     * Stops making movies on the specified Resource.
     *
     * If resource was not found, nothing changes.
     *
     * @param resource The specified Resource.
     * @return True if the given resource was found in the map (which is 
     * subsequently stopped).
     */
    public boolean stopMaker(Resource resource) {
        Future future = futureMap.get(resource.getResourceNumber());
        // Flag for if we found the resource in the map.
        boolean stopped = false;

        if (future != null) {
            future.cancel(false);
            futureMap.remove(resource.getResourceNumber());
            stopped = true;
        }

        Debug.println("MovieMakerScheduler:stopMaker called for resource "
                +resource.getName());

        return (stopped);
    }

    /**
     * Starts making movies using the specified Resource.
     * 
     * @param resource The specified Resource.
     */
    public void startMaker(Resource resource) {
        if (resource == null) {
            Debug.println("MovieMakerScheduler: startMaker resource was null");
            return;
        }
        Debug.println("MovieMakerScheduler:startMaker called for resource "
                +resource.getName());

        // Flag for if this resource type can be scheduled.
        //boolean validType = false;
       
        //boolean shouldSchedule = false;
        
        // Check to see if the resource is already scheduled. 
        if (isScheduled (resource)) {
            log.finest ("Resource " + resource.getName()
                      + " was already scheduled to be made into a movie.");
            Future future = futureMap.get(resource.getResourceNumber());

            if (future != null) {
                future.cancel(false); // let it finish
                futureMap.remove(resource.getResourceNumber());
                log.finest("Resource " + resource.getName() 
                        + " was stopped in MovieMakerSystem.");
            }
        }

        //Make sure the resource is currently active.
        if(!(resource.isActive())){
            Debug.println("MovieMakerScheduler: startMaker Resource was"
                    + " inactive");
            return;
        }
        
        // Make sure the resource is of a valid type.
        // Valid types are cameras and maps.
        if(!(resource.getResourceType() == WeatherResourceType.WeatherCamera ||
             resource.getResourceType() == WeatherResourceType.WeatherMapLoop)){
            Debug.println("MovieMakerScheduler: startMaker Resource was not of "
                    + "the correct type.");
            return;
        }

        //@TODO -- we should not need to tell storage system to reload
        // the resource list. Need to check into this -- why was code here
        /*
        try{
           storageSystem.reloadResourceList();
        }catch(WeatherException ex){
            WeatherLogger.log(Level.SEVERE, "Failed to reload resource list.", ex);
        }       
        */

        // Debug.println("Starting new movie maker for: "+resource.getResourceName());

        // Calculate how long we need to wait so movie creation
        // occurs on the hour, every hour.
        GregorianCalendar now = new GregorianCalendar();

        now.set(GregorianCalendar.SECOND, 0);
        now.set(GregorianCalendar.MILLISECOND, 0);

        // Add necessary minutes to advance the time to the next hour
        // (on the hour).
        int wait = (60 - now.get(GregorianCalendar.MINUTE)) % 60;
        now.add(GregorianCalendar.MINUTE, wait);

        // The runnable to be run to make the movie. It will be scheduled at the
        // next hour.
        MovieMakerRunnable runnable = new MovieMakerRunnable(resource, storageSystem, now);

        Debug.println("new MovieMakerRunnable created for "
                + resource.getName()+" with now = " 
                + CalendarFormatter.formatTime (now));

        // Wait for the specified time, then make a movie every 60 minutes.
        Future future = scheduleAtFixedRate(runnable, wait, 60, TimeUnit.MINUTES);
        futureMap.put(resource.getResourceNumber(), future);
    }

    /**
     * Check if a resource is already scheduled to be made into a movie.
     *
     * @param resource Resource to check.
     * @return True if scheduled, false otherwise.
     */
    private boolean isScheduled(Resource resource) {
        int id = resource.getResourceNumber();
        boolean value = false;
        
        if (futureMap.containsKey(id)) {
            value = true;
        }

        return (value);
    }
}
