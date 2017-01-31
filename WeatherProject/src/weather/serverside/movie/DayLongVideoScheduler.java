
package weather.serverside.movie;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherLogger;

/**
 * A class responsible for scheduling day-long movie creation.
 *
 * Currently, this class forces the system to make one movie at a time.
 * 
 * NOTE: A <code>Future</code> represents the result of an asynchronous
 *        computation. Methods are provided to check if the computation is
 *        complete, to wait for its completion, and to retrieve the result of
 *        the computation.
 *
 * @author Brian Bankes
 */
public class DayLongVideoScheduler extends ScheduledThreadPoolExecutor {
    /**
     * Core pool size is 1 so only one movie will be made at a time to prevent
     * thrashing.
     */
    private static final int POOL_SIZE = 1;

    /**
     * Map of the tasks for day-long movie creation. 
     * Maps the resource ID with the Future object.
     */ 
    private final HashMap<Integer, Future> futureMap;
    
    /**
     * The storage system to which the movies will be saved.
     */
    private final StorageControlSystem storageSystem;

    /**
     * The constructor for DayLongVideoScheduler.
     * @param storageSystem The storage system movies will be stored on.
     */
    public DayLongVideoScheduler(StorageControlSystem storageSystem) {
        super(POOL_SIZE);

        this.storageSystem = storageSystem;
        this.futureMap = new HashMap<>();
        Debug.println("DayLongVideoScheduler's constructor invoked.");
    }
    
    /**
     * Logs and show an error message.
     * @param message The error message.
     */
    private void logError(String message) {
        Debug.println(message);
        WeatherLogger.log(Level.SEVERE, message);
        
    }

    /**
     * Stops making day-long movies on the specified Resource.
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

        Debug.println("DayLongVideoScheduler:stopMaker called for resource "
                + resource.getName());

        return (stopped);
    }

    /**
     * Starts making day-long movies using the specified Resource.
     * 
     * @param resource The specified Resource.
     */
    public void startMaker(Resource resource) {
        if (resource == null) {
            logError("DayLongVideoScheduler:startMaker resource was null");
            return;
        }
        Debug.println("DayLongVideoScheduler:startMaker called for resource "
                + resource.getName());
        
        // Check to see if the resource is already scheduled. 
        if (isScheduled(resource)) {
            logError("Resource " + resource.getName() + " was already scheduled"
                    + " to be made into a day-long movie.");
            Future future = futureMap.get(resource.getResourceNumber());

            if (future != null) {
                future.cancel(false); // let it finish
                futureMap.remove(resource.getResourceNumber());
                logError("Resource " + resource.getName() 
                        + " was stopped from making a day-long video.");
            }
        }

        //Make sure the resource is currently active.
        if(!(resource.isActive())){
            logError("DayLongVideoScheduler:startMaker Resource " + resource
                    .getName() + " was inactive");
            return;
        }
        
        // Make sure the resource is of a valid type.
        // Valid types are cameras and maps.
        if(!(resource.getResourceType() == WeatherResourceType.WeatherCamera ||
             resource.getResourceType() == WeatherResourceType.WeatherMapLoop)){
            logError("DayLongVideoScheduler:startMaker Resource " + resource
                    .getName() + "was not of the correct type.");
            return;
        }


        // Calculate how long we need to wait so day-long movie creation occurs
        // at the currect time durring every hour.  The action should happrn 
        // after the retrieval grace period for the hour has passed.
        
        // Start calculation with calendar set to the current time.
        GregorianCalendar now = new GregorianCalendar();

        // Set to the most resent minute.
        now.set(GregorianCalendar.SECOND, 0);
        now.set(GregorianCalendar.MILLISECOND, 0);
        
        // Get a second calendar with the time of the first run.
        GregorianCalendar firstRunTime = (GregorianCalendar)now.clone();
        firstRunTime.set(GregorianCalendar.MINUTE, Integer
                .parseInt(PropertyManager
                .getGeneralProperty("rangeRetrieveGracePeriod")));
        if (firstRunTime.getTimeInMillis() < System.currentTimeMillis()) {
            // Too late for this hour, so wait for next one.
            firstRunTime.add(GregorianCalendar.HOUR, 1);
        }

        // Calculate wait time in minutes for the first run of the thread. 
        int wait = (int)((firstRunTime.getTimeInMillis() - now 
                .getTimeInMillis()) / ResourceTimeManager
                .MILLISECONDS_PER_MINUTE);

        // The runnable to be run to make the movie. It will be scheduled at the
        // next hour.
        DayLongVideoRunnable runnable = new DayLongVideoRunnable(resource, 
                storageSystem);

        Debug.println("New DayLongVideoRunnable created for "
                + resource.getName() + " with firstRunTime = " 
                + CalendarFormatter.formatTime (firstRunTime)
                + ". Wait time is " + wait + " minutes.");

        // Wait for the specified time, then make a movie every 60 minutes.
        Future future = scheduleAtFixedRate(runnable, wait, 60, 
                TimeUnit.MINUTES);
        futureMap.put(resource.getResourceNumber(), future);
        
    }

    /**
     * Check if a resource is already scheduled to be made into a day-long 
     * movie.
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
