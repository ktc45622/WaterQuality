package weather.serverside.retrieval;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.WeatherResourceType;
import weather.common.data.weatherstation.WeatherStationInstance;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.utilities.*;
import weather.serverside.utilities.ResourceCollectionTimeUtility;

/**
 * A <code>Runnable</code> object used to construct the ResourceRetriever's
 * scheduled threads. 
 * @author Bloomsburg University Software Engineering
 * @author David Reichert (2008)
 * @version Spring 2008
 */
public class ResourceRetrieverRunnable implements Runnable {

    private Resource resource;
    private StorageControlSystem storageSystem;
    int consecutiveMissedAttempts;
    public final int CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD ;
    private static WeatherTracer log;

    /**
     * Constructor for ResourceRetrieverRunnable.
     * @param resource the Resource to be retrieved
     * @param storageSystem the StorageControlSystem used to store the ResourceInstance
     */
    public ResourceRetrieverRunnable(Resource resource, StorageControlSystem storageSystem) {
        this.resource = resource;
        this.storageSystem = storageSystem;
        this.consecutiveMissedAttempts = 0;
        // Get logger.
        log = WeatherTracer.getRetrievalLog ();

        if(resource==null){
            this.CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD =
                    Integer.parseInt(PropertyManager.getServerProperty(
                    "consecutiveMissedAttemptsErrorThresholdDefault"));
        }
        else if (resource.getResourceType() == WeatherResourceType.WeatherCamera){
            this.CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD =
                    Integer.parseInt(PropertyManager.getServerProperty(
                    "consecutiveMissedAttemptsErrorThresholdCamera"));
        }
        else if (resource.getResourceType() == WeatherResourceType.WeatherMapLoop){
            this.CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD =
                    Integer.parseInt(PropertyManager.getServerProperty(
                    "consecutiveMissedAttemptsErrorThresholdMapLoop"));
        }
        else{
            this.CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD =
                    Integer.parseInt(PropertyManager.getServerProperty(
                    "consecutiveMissedAttemptsErrorThresholdUnknownResourceType"));
        }
    }

    /**
     * Fetches the ResourceInstance and stores it using the StorageControlSystem.
     */
    @Override
    public void run() {
    //    Debug.println("Getting resource# " + resource.getResourceNumber() + " at " + resource.getURL());
        if (ResourceCollectionTimeUtility.validDataCollectionTime(resource,
		new GregorianCalendar ())) {
            storeInstance(fetchInstance());
        }
    }

    /**
     * Fetches the ResourceInstance.
     *
     * @return the ResourceInstance, or null if it couldn't be fetched.
     */
    private ResourceInstance fetchInstance() {
        log.finest ("Fetching instance for: " + resource.getName ());

        try {
            ResourceInstance instance;
    //        Debug.println("Fetching instance " + resource.getResourceNumber() );
            switch (resource.getFormat()) {
                case jpeg:
                case gif:
                case png:
                case image:
                    instance = new ImageInstance(resource);
                    instance.readURL(resource.getURL());
                    this.consecutiveMissedAttempts = 0;
                    return instance;
                case text:
                    instance = new WeatherStationInstance(resource);
                    instance.readURL(resource.getURL());
                    this.consecutiveMissedAttempts = 0;
                    return instance;
                case comma_separated_values:
                    if(WeatherUndergroundInstance.isWeatherUndergroundInstance(resource)){
                        instance = new WeatherUndergroundInstance(resource);
                        instance.readURL(resource.getURL());
                        this.consecutiveMissedAttempts = 0;
                        return instance;
                    }//else go to default
                default:
                    this.consecutiveMissedAttempts = 0;
                    return null;
            }
        } catch (IOException ex) {
            this.consecutiveMissedAttempts++;

            WeatherLogger.log(Level.SEVERE, "Could not obtain " +
                    resource.getName() + " from Location " +
                    resource.getURL() + " Consecutive missed attempts is " +
                    consecutiveMissedAttempts);
            log.severe ("Could not obtain resource instance for " + 
                    resource.getName () + " from location " +
                    resource.getURL (), ex);

            if( this.CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD ==
                      this.consecutiveMissedAttempts){               
             try{
                    Emailer.emailAdmin("Resource " + resource.getName() +
                        " is currently unavailable. " +
                        consecutiveMissedAttempts +
                        " consecutive attempts have failed to retrieve data",
                        "Resource " + resource.getName() +
                        " is currently unavailable.");
                }catch (WeatherException e){
                    WeatherLogger.log(Level.WARNING, "Unable to email " +
                            "administrators about a failed attempt to access " +
                            resource.getName() + ".");
                }

                // @todo: set time period to once per hour or day  for error messages--
                // need to make sure how this would happen
             }
            return null;
        } catch (WeatherException ex) {
            this.consecutiveMissedAttempts++;

            WeatherLogger.log(Level.SEVERE, "Could not obtain " +
                    resource.getName() + " from Location " +
                    resource.getURL() + " Consecutive missed attempts is " +
                    consecutiveMissedAttempts);
            log.severe ("Could not obtain resource instance for " + 
                    resource.getName () + " from location " +
                    resource.getURL (), ex);

            if( this.CONSECUTIVE_MISSED_ATTEMPTS_ERROR_THRESHOLD ==
                      this.consecutiveMissedAttempts){

             try{
                    Emailer.emailAdmin("Resource " + resource.getName() +
                        " is currently unavailable. " +
                        consecutiveMissedAttempts +
                        " consecutive attempts have failed to retrieve data",
                        "Resource " + resource.getName() +
                        " is currently unavailable.");
                }catch (WeatherException e){
                    WeatherLogger.log(Level.WARNING, "Unable to email " +
                            "administrators about a failed attempt to access " +
                            resource.getName() + ".");
                }
                 // @todo set time period to once per hour --
                //need to make sure how this  would happen
             }
            log.severe ("Could not obtain resource instance for " + 
		    resource.getName () + " from location " +
		    resource.getURL (), ex);
            return null;
        }
    }

    /**
     * Stores the ResourceInstance using the StorageControlSystem.
     * @param instance the ResourceInstance to be stored
     */
    private void storeInstance(ResourceInstance instance) {
        if (instance == null) {
            return;
        }
        log.finest("Storing instance for: " + resource.getResourceName());
        if (!storageSystem.placeResourceInstance(instance)) {
            WeatherLogger.log(Level.SEVERE, "Error storing instance of "
                    + resource.getResourceName());
            log.severe("Error storing instance of "
                    + resource.getResourceName());
        }
    }
   
}
