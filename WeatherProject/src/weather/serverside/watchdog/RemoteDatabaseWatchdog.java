package weather.serverside.watchdog;

import java.util.Vector;
import weather.common.data.resource.Resource;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.utilities.ServiceControl;
import weather.serverside.utilities.WeatherServiceNames;


/**
 * A ServerWatchdog thread used to keep an eye on the remote database server.
 * @author Patrick Brinich (2013)
 */
public class RemoteDatabaseWatchdog extends ServerWatchdog {
    /**The last time this RemoteDatabaseWatchdog sent out a notification*/
    private long notificationTime;
    /**How many times in a row this RemoteDatabaseWatchdog encountered a system
     * failure.*/
    private int  consecutiveFailures;

    /**
     * Constructs a new RemoteDatabaseWatchdog
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws WeatherException 
     */
    public RemoteDatabaseWatchdog() throws ClassNotFoundException, InstantiationException, IllegalAccessException, WeatherException {
        super();
        notificationTime    = 0L;
        consecutiveFailures = 0;
    }
    
    /**
     * Checks if the RemoteDatabaseServer Windows service is running and if
     * the server responds to commands.
     * @return true if the service is running and responds; false otherwise.
     */
    @Override
    public boolean isServiceRunning() {
        if(!ServiceControl.isRunning(WeatherServiceNames.WEATHERDB)) {
            return false;
        }
        
        RemoteDatabaseCommand cmd = new RemoteDatabaseCommand(
                                    DatabaseCommandType.Server_IsRunning,null);
        RemoteDatabaseResult result = null;
        try {
            result = cmd.execute();
        }
        catch (Exception e) {
            return false;
        }
        return (result!=null);
    }

    /**
     * Notifies admins that the service has been stopped and restarts the service
     */
    @Override
    public void handleStoppedService() {
        notifyAdminStoppedService();
        ServiceControl.startService(WeatherServiceNames.WEATHERDB);
    }

    /**
     * Checks if the remote database server can correctly respond to a query.
     * @param resources the list of resources. Not used.
     */
    @Override
    public void checkSystem(Vector<Resource> resources) {
        boolean isError = false;
        RemoteDatabaseCommand cmd   = new RemoteDatabaseCommand(DatabaseCommandType.Resource_GetResourceList, null);
        RemoteDatabaseResult result = null;
        try {
            result = cmd.execute();
        } catch (Exception e) {
            isError = true;
        }
        
        if (result==null) {
            isError = true;
        }
        else if (result.getResultStatus() == RemoteDatabaseResultStatus.ErrorObjectReturned) {
            isError = true;
        }
        
        /*
        if (isError) {
            consecutiveFailures++;
            notifyAdminError();
            ServiceControl.restartService(WeatherServiceNames.WEATHERDB);
        } else {
            consecutiveFailures = 0;
        }
      */  
    }

    /**
     * Logs a ServerWatchdogErrorEvent and notifies the admins that the service
     * was stopped.
     */
    private void notifyAdminStoppedService() {
        String system = WeatherServiceNames.WEATHERDB.getShortName();
        String error = "The system service was stopped.";
        String action = "The system service was restarted.";
        String info = "This may indicate an error with the the system.";

        ServerWatchdogErrorEvent event = new ServerWatchdogErrorEvent(system, error,
                action, info, null);
        event.logError();
        event.notifyAdmin();
        event.printErrorToDebug();
        super.addServerWatchdogErrorEvent(event);
    }

    /**
     * Logs a ServerWatchdogErrorEvent and notifies the admins that the 
     * remote database server could not correctly respond to a query.
     */
    private void notifyAdminError() {
        String system = WeatherServiceNames.WEATHERDB.getShortName();
        String error = "The system could not properly return a query.";
        String action = "The system service was restarted.";
        String info = "This may indicate an error with the the system.";
        long   time = System.currentTimeMillis();
        
        if(consecutiveFailures >= 2) {
            info = "The system has failed at least twice consecutively. " + info;
        }

        ServerWatchdogErrorEvent event = new ServerWatchdogErrorEvent(system, error,
                action, info, null, time);
        
        if(canNotify(time)) {
            event.notifyAdmin();
        } else {
            Debug.println("Notification not sent for "+this.getClass().getName());
        }
        event.logError();
        event.printErrorToDebug();
        super.addServerWatchdogErrorEvent(event);
        
        notificationTime = time;
    }
    
    /**
     * Determines whether a notification can be sent for a specific time.
     *
     * @param time
     * @return true if ,<code>time</code> is at least 24 hours past the last 
     * notification time or there are exactly two consecutive failures;
     * false otherwise.
     */
    private boolean canNotify(long time) {
        if (consecutiveFailures == 2) {
            return true;
        }
        
        if ( (time - notificationTime) < ResourceTimeManager.MILLISECONDS_PER_DAY) {
            return false;
        }
        
        return true;
    }
}
