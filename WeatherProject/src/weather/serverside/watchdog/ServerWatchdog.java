package weather.serverside.watchdog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherTracer;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

/**
 * Abstract class meant to provide the framework for watching a single service.
 * Refactored from the old ServerWatchdog.
 *
 * @author Joe Sharp
 * @author Dustin Jones (2010)
 * @author Ryan Kelly (2010)
 * @author Eric Subach (2010)
 * @author Patrick Brinich (2013)
 */
public abstract class ServerWatchdog extends Thread {

    private DBMSSystemManager dbms;
    private Vector<Resource> allResources;
    /**
     * Logger for watchdogs.
     */
    protected WeatherTracer log;
    /**
     * List of Server watchdog events.
     * Please keep me sorted!
     */
    private List<ServerWatchdogErrorEvent> events;

    /**
     * Create a new ServerWatchdog object.
     *
     * @throws ClassNotFoundException thrown by MySQLImpl
     * @throws InstantiationException thrown by MySQLImpl
     * @throws IllegalAccessException thrown by MySQLImpl
     * @throws WeatherException thrown if there is an error getting the resource
     * list from the MySQLImpl.
     */
    public ServerWatchdog() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, WeatherException {

        log = WeatherTracer.getWatchdogLog();
        log.info("New watchdog.");

        dbms = MySQLImpl.getMySQLDMBSSystem();
        log.finer("Got DBMS.");

        reloadResources();
        log.finer("Got all resources.");

        events = new ArrayList<>();
    }

    /**
     * Overrides the run method from the thread class. The server watchdog will
     * first reload the resource list, then check if the service is running.
     * Finally, it will do any other necessary system checks to find any 
     * possible errors with the system.
     */
    @Override
    public void run() {
        String classname = this.getClass().getName();
        
        log.info("Running: "+classname);
        Debug.println("Running Watchdog: "+classname);
        reloadResources(); //@TODO: update resources instead of reloading
        if (!isServiceRunning()) {
            Debug.println("Need to restart service");
            handleStoppedService();
            Debug.println("Service restarted");
        }
        checkSystem(allResources);
    }

    /**
     * Adds a ServerWatchdogErrorEvent to the list of ServerWatchdogErrorEvents
     * <br />
     * TODO: Optimization! The current implementation of the SWEE list is a 
     * simple array list which is sorted whenever a new value is added.
     * While sorting on a nearly sorted list is relatively fast, it may be a
     * good idea to find a data structure that keeps a sorted list. The Java 
     * library does not have one out of the box.
     * @param e The ServerWatchdogErrorEvent to add to the list.
     * @return true if <code>e</code> was successfully added to the list; false
     * otherwise.
     */
    public boolean addServerWatchdogErrorEvent(ServerWatchdogErrorEvent e) {
        boolean retVal = events.add(e);
        Collections.sort(events);
        return retVal;
    }
    
    /**
     * Gets the sorted list of ServerWatchdogErrorEvents maintained by this 
     * ServerWatchdog.
     * @return the sorted list of ServerWatchdogErrorEvents.
     */
    public List getServerWatchdogErrorEvents() {
        return events;
    }

    /**
     * Check if the service monitored by this
     * <code>ServerWatchdog</code> is running.
     *
     * @return true if the service is running; false otherwise.
     */
    public abstract boolean isServiceRunning();

    /**
     * Does required actions when the service monitored by this
     * <code>ServerWatchdog.</code> isn't running.
     */
    public abstract void handleStoppedService();

    /**
     * Check all resources for errors relevant to this
     * <code>ServerWatchdog.</code>
     *
     * @param resources all Resources in a vector. Order of the list determined
     * by the database implementation's resource manager.
     */
    public abstract void checkSystem(Vector<Resource> resources);

    /**
     * Gets the local storage control system used by this ServerWatchdog.
     * @return The local storage control system.
     */
    protected StorageControlSystem getStorageControlSystemLocal() {
        return new StorageControlSystemLocalImpl(dbms);
    }
    
    /**
     * Gets the remote storage control system used by this ServerWatchdog.
     * @return The remote storage control system.
     */
    protected StorageControlSystem getStorageControlSystem() {
        return StorageControlSystemImpl.getStorageSystem();
    }

    /**
     * Reloads the list of resources.
     */
    private void reloadResources() {
        if (allResources != null) {
            allResources.clear(); //Empty Vector
        }
        allResources = dbms.getResourceManager().getResourceList();
        
        if (allResources == null) {
            allResources = new Vector<>();
        }
    }

    
}
