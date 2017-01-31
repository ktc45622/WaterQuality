package weather.serverside.retrieval;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherTracer;

/**
 * A class responsible for retrieving and saving
 * ResourceInstances from Resources.
 * <br>
 * To use this class, simply start and update any Resources
 * with <code>startResource</code>.
 * Should you want a Resource to stop being retrieved, invoke
 * <code>stopRetrieval</code> on the Resource.
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class ResourceRetriever extends ScheduledThreadPoolExecutor
{

    private HashMap<Integer, Future> futureMap;
    private StorageControlSystem storageSystem;

    private static WeatherTracer log = WeatherTracer.getRetrievalLog ();

    /**
     * Constructor for ResourceRetriever. 
     * @param corePoolSize the number of threads to keep in the pool, even if they are idle
     * @param storageSystem the StorageControlSystem used to store ResourceInstances
     */
    public ResourceRetriever(int corePoolSize, StorageControlSystem storageSystem)
    {
        super(corePoolSize);
        this.futureMap = new HashMap<Integer, Future>();
        this.storageSystem = storageSystem;
    }

    /**
     * Causes the specified Resource to cease being retrieved. This method has no effect
     * if the specified Resource was never added.
     * @param resource the desired Resource
     */
    public void stopRetrieval(Resource resource)
    {
        log.finest ("Stopping retrieval of: " + resource.getResourceName ());

        Future future = futureMap.get(resource.getResourceNumber());

        // Make sure resource is scheduled first.
        if (future != null)
        {
            boolean result = future.cancel(false);//Let it finished if already started
            if(!result){
               Debug.println("Could not cancel future task for "
                       +resource.getName());
                log.finest ("Could not cancel future task for "
                        +resource.getName());
            }
            else{
                log.finest ("Future task for "
                        +resource.getName()+ " was cancelled.");
            }
            
            futureMap.remove(resource.getResourceNumber());
        }
    }

    /**
     * Causes the specified Resource to either start being retrieved, or updates
     * the information associated with that Resource if it is currently being retrieved.
     * Resources must be active and valid to be retrieved. 
     * @param resource the desired Resource
     */
    public void startRetrieval(Resource resource)
    {
        log.finest ("Attempting to start retrieval of: " + resource.getResourceName ());
        Debug.println("Attempting to start retrieval of: " + resource.getResourceName ());

        // Check to see if the resource is already scheduled. If so, don't make
        // any changes.
        if (isScheduled (resource)) {
            log.finest ("Resource " + resource.getName ()
                      + " was already scheduled to be retrieved. Will now attempt to stop it");
            stopRetrieval(resource); // Still needed -- future.cancel(resource) must be called
        }

        if(resource.isActive()){
            ResourceRetrieverRunnable runnable =
                       new ResourceRetrieverRunnable(resource, storageSystem);
            Future future =
                    this.scheduleWithFixedDelay(runnable, 0, resource.getFrequency(), TimeUnit.SECONDS);//changed 6/6/2014
                     // scheduleAtFixedRate(runnable, 0, resource.getFrequency(), TimeUnit.SECONDS);

            futureMap.put(resource.getResourceNumber(), future);//Replaces entry in map if key is present.
            log.finest("Started retrieval of: " + resource.getResourceName ());
        } else{
            log.finest("" + resource.getResourceName ()+" was not started. It is not active and valid.");
        }


    }

    /**
     * Check if a resource is already scheduled to be made into a movie.
     *
     * @param resource resource to check
     * @return true if scheduled, false otherwise
     */
    private boolean isScheduled (Resource resource) {
        return futureMap.containsKey (resource.getResourceNumber ());
    }


}
