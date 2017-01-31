package weather.serverside.watchdog;

import java.util.GregorianCalendar;
import java.util.Vector;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.utilities.ServiceControl;
import weather.serverside.utilities.WeatherServiceNames;

/**
 * This watchdog monitors the Storage system for errors. Refactored from the old
 * ServerWatchdog
 *
 * @author Joe Sharp
 * @author Dustin Jones (2010)
 * @author Ryan Kelly (2010)
 * @author Eric Subach (2010)
 * @author Patrick Brinich (2013)
 */
public class StorageWatchdog extends ServerWatchdog {

    /**
     * Time of the last error found--in milliseconds.
     */
    long lastError;
    /**
     * Time of the last critical error found--in milliseconds.
     */
    long lastCritical;
    /**
     * How often the watchdog is run--in minutes
     */
    int checkTime;

    /**
     * Creates a new StorageWatchdog.
     * @param checkTime how often in minutes the watchdog is run.
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws WeatherException 
     */
    public StorageWatchdog(int checkTime) throws ClassNotFoundException, InstantiationException, IllegalAccessException, WeatherException {
        super();
        super.log.finer("Retrieved base storage folder.");
        lastError       = 0;
        lastCritical    = 0;
        this.checkTime  = checkTime;
    }

    /**
     * Checks if the Storage system service is running
     *
     * @return true if it is running; false otherwise.
     */
    @Override
    public boolean isServiceRunning() {
        return ServiceControl.isRunning(WeatherServiceNames.STORAGE);
    }

    /**
     * Restarts the Storage System service and notifies the admin
     */
    @Override
    public void handleStoppedService() {
        ServiceControl.restartService(WeatherServiceNames.STORAGE);

        //log and notify
        String system = WeatherServiceNames.STORAGE.getShortName();
        String error = "The system service was stopped.";
        String action = "The system service was restarted.";
        String info = "This may indicate an error with the the system.";

        ServerWatchdogErrorEvent event = new ServerWatchdogErrorEvent(system, error,
                action, info, null);
        event.logError();
        event.notifyAdmin();
        super.addServerWatchdogErrorEvent(event);
    }

    /**
     * Checks the Storage System for errors
     *
     * @param resources all Resources
     */
    @Override
    public void checkSystem(Vector<Resource> resources) {
        Debug.println("Trying to obtain storage system.");
        StorageControlSystem storage = null;
        try {
            storage = super.getStorageControlSystem();
        } catch (Exception ex) {
            handleStorageSystemError();
            return;
        }
        
        //try to ping server
        Debug.println("Pinging storage server");
        boolean canPingServer = false;
        canPingServer = storage.pingServer();
        if(!canPingServer) {
            handleStorageSystemError();
            return; //Necessary if you uncomment code below
        }
// <editor-fold defaultstate="collapsed" desc="Commented out code">        
//        Debug.println("Checking resources for storage error.");
//        boolean storageOK = false;
//        storageOK = checkResources(resources, storage);
//
//        
//        if (!storageOK) {
//            Debug.println("Storage is not okay.");
//            handleStorageSystemError();
//        }
// </editor-fold>  
    }
    
    /**
     * Handles a storage system error by restarting necessary systems as well as
     * logging and notifying admins of the error.
     */    
    private void handleStorageSystemError() {
        long currentTime = System.currentTimeMillis();
        boolean isCritical = isCriticalError(currentTime);

        String system = WeatherServiceNames.STORAGE.getShortName();
        String error = "The storage system was not available.";
        String action = "The system service was restarted.";
        String info = "This may indicate an error with the the system.";
        if (isCritical) {
            info = "This is a critical error and may require administrator attention.";
        }

        ServerWatchdogErrorEvent event = new ServerWatchdogErrorEvent(system, error,
                action, info, null, currentTime);
        event.logError();
        event.printErrorToDebug();
        super.addServerWatchdogErrorEvent(event);
        if (!isCritical || canNotifyCritical(currentTime)) {
            event.notifyAdmin();
        }
        
        lastError = currentTime;
        if(isCritical) {
            lastCritical = currentTime;
        }
        
        restartSystems();
    }

    /**
     * Checks if an error is a critical error. An error is critical if it occurs
     * less than two check intervals (ie at the next check) after a previous 
     * error is found.
     * @param time the time of the error
     * @return true if the error is critical; false otherwise.
     */
    private boolean isCriticalError(long time) {
        long difference = time - lastError;
        return difference < 2 * checkTime * ResourceTimeManager.MILLISECONDS_PER_MINUTE;
    }

    /**
     * Restarts the Storage, Retrieval and MovieMaker systems in that order.
     */
    private void restartSystems() {
        ServiceControl.restartService(WeatherServiceNames.STORAGE);
        ServiceControl.restartService(WeatherServiceNames.RETRIEVAL);
        ServiceControl.restartService(WeatherServiceNames.MOVIE);
    }

    /**
     * Checks if it is okay to notify an admin of a critical error.
     * Notifications should be sent once per day.
     * @param currentTime the current time
     * @return true if notification is allowed; false otherwise.
     */
    private boolean canNotifyCritical(long currentTime) {
        long difference = currentTime - lastCritical;
        return difference > ResourceTimeManager.MILLISECONDS_PER_DAY;
    }

    /**
     * Checks over resources to see if the storage system is okay
     * @param resources a Vector of Resources
     * @param storage the storage control system
     * @return true if the storage system is okay for the given Resources;
     *         false otherwise.
     */
    private boolean checkResources(Vector<Resource> resources, StorageControlSystem storage) {
        //Build resource range
        GregorianCalendar start = new GregorianCalendar();
        start.add(GregorianCalendar.DAY_OF_MONTH, -1);
        GregorianCalendar cur = new GregorianCalendar();

        ResourceRange range = new ResourceRange(
                new java.sql.Date(start.getTimeInMillis()),
                new java.sql.Date(cur.getTimeInMillis()));

        //Check over resources. Try to fetch anything within the last day
        //If we get a ResourceInstanceReturned object, the storage system is
        //working fine
        for (Resource resource : resources) {
            if (resource.isActive()) {
                Debug.println("Checking: " + resource.getResourceName());
                ResourceInstancesRequested request;
                
                try {
                    //1 is arbitrary--we don't want everything, it would take  
                    //too long
                    request = new ResourceInstancesRequested(range, 1,
                                false,resource.getFormat(),resource);
                    ResourceInstancesReturned returned = storage.getResourceInstances(request);
                    return true; 
                } catch (Exception ex) {
                    Debug.println("Could not fetch for " + resource.getResourceName());
                    return false;
                }
            }
        }
        return false;
    }
}
