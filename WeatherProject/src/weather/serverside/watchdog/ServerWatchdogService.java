package weather.serverside.watchdog;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WeatherTracer;

/**
 * Main class for the server watchdog. Ideally run as a windows service. 
 * Refactored from old ServerWatchdog.
 * 
 * @author Joe Sharp
 * @author Dustin Jones (2010)
 * @author Ryan Kelly (2010)
 * @author Eric Subach (2010)
 * @author Patrick Brinich
 */
public class ServerWatchdogService {

    // Command start/stop options.
    private static final String COMMAND_R = "-R";
    private static final String COMMAND_M = "-M";
    private static final String COMMAND_S = "-S";
    // Logger for watchdog.
    private static final WeatherTracer log= WeatherTracer.getWatchdogLog();;

    /**
     * Main method: starts a MovieMakerWatchdog, a Retrieval Watchdog and 
     * a StorageWatchdog to check on their respective systems intermittently.
     * Currently, the MovieMakerWatchdog is run every 16 minutes past the hour.
     * The RetrievalWatchdog is run every 15 minutes starting at 14 minutes past
     * the hour. The StorageWatchdog is run every 15 minutes starting 12 minutes
     * past the hour. Finally, the RemoteDatabaseWatchdog is run every 15
     * minutes starting 5 minutes past the hour.
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
        ServerWatchdog movieWatchdog        = null;
        ServerWatchdog retrievalWatchdog    = null;
        ServerWatchdog storageWatchdog      = null;
        ServerWatchdog dbWatchdog           = null;
        
        int mwInterval = 60;
        int rwInterval = 15;
        int swInterval = 15;
        int dwInterval = 15;
        
        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int minutes = cal.get(Calendar.MINUTE);
        
        //Until 16 minutes past the hour
        int mwWaitTime = (minutes <= 16) ? 16 - minutes : (60+16) - minutes;
        //Until 14 minutes past the hour
        int rwWaitTime = (minutes <= 14) ? 14 - minutes : (60+14) - minutes;
        //Until 12 minutes past the hour
        int swWaitTime = (minutes <= 12) ? 12 - minutes : (60+12) - minutes;
        //Until 5 minutes past the hour
        int dwWaitTime = (minutes <= 12) ?  5 - minutes : (60+ 5) - minutes;
       /* 
        try {
            dbWatchdog = new RemoteDatabaseWatchdog();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | WeatherException ex) {
            log.severe("Error starting RemoteDatabasWatchdog.", ex);
            Debug.println("Error starting RemoteDatabasWatchdog.");
        }
        */
        try {
            movieWatchdog = new MovieMakerWatchdog();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | WeatherException ex) {
            log.severe("Error starting MovieMakerWatchdog.", ex);
            Debug.println("Error starting MovieMakerWatchdog.");
        }
        try{
            retrievalWatchdog = new RetrievalWatchdog();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | WeatherException ex) {
            log.severe("Error starting RetrievalWatchdog.", ex);
            Debug.println("Error starting RetreivalWatchdog.");
        }
        try {
            storageWatchdog = new StorageWatchdog(swInterval);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | WeatherException ex) {
            log.severe("Error starting StorageWatchdog.", ex);
            Debug.println("Error starting StorageWatchdog.");
        }

       // scheduleWatchdog(dbWatchdog       , scheduler, dwWaitTime, dwInterval, TimeUnit.MINUTES);
        scheduleWatchdog(movieWatchdog    , scheduler, mwWaitTime, mwInterval, TimeUnit.MINUTES);
        scheduleWatchdog(retrievalWatchdog, scheduler, rwWaitTime, rwInterval, TimeUnit.MINUTES);
        scheduleWatchdog(storageWatchdog  , scheduler, swWaitTime, swInterval, TimeUnit.MINUTES);
        
    }
    
    /**
     * Old main method
     * @param commands 
     * @deprecated
     */
    public static void mainOld (String[] commands) {
        
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
        ServerWatchdog movieWatchdog = null;
        ServerWatchdog retrievalWatchdog = null;
        ServerWatchdog storageWatchdog = null;

        //log = WeatherTracer.getWatchdogLog();

        // How long to wait before checking after first start.
        int minutesToWait;
        // How often to check.
        int interval;
        TimeUnit timeUnit;
        Calendar cal;


        cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        minutesToWait = 0;


        // Start our watchdogs.
        try {
            movieWatchdog       = new MovieMakerWatchdog();
            retrievalWatchdog   = new RetrievalWatchdog();
            storageWatchdog     = new StorageWatchdog(5);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | WeatherException ex) {
            WeatherLogger.log(Level.SEVERE,
                    "Error starting one of the watchdog systems.", ex);
            //e.printStackTrace();
            Debug.println("Error starting one of the watchdog systems");
        }

        // Parse command parameters.
        if (commands.length > 0) {
            for (String command : commands) {
                switch (command) {
                    case COMMAND_R:
                        interval = 10;
                        timeUnit = TimeUnit.MINUTES;
                        minutesToWait = 10 - (cal.get(Calendar.MINUTE) % 10);
                        scheduler.scheduleAtFixedRate(retrievalWatchdog, minutesToWait, interval, timeUnit);
                        log.fine("Resource retrieval watchdog set to run every "
                                + interval + " " + timeUnit + ". Waiting "
                                + minutesToWait + " " + timeUnit + ".");
                        Debug.println("Resource retrieval watchdog set to run every "
                                + interval + " " + timeUnit + ". Waiting "
                                + minutesToWait + " " + timeUnit + ".");
                        break;
                    case COMMAND_M:
                        int minutes = cal.get(Calendar.MINUTE);
                        interval = 60;
                        timeUnit = TimeUnit.MINUTES;
                        // Schedule to run 8 minutes past the hour.
                        minutesToWait = (minutes <= 8) ? 8 - minutes : 68 - minutes;
                        scheduler.scheduleAtFixedRate(movieWatchdog, minutesToWait, interval, timeUnit);
                        log.fine("Movie maker watchdog set to run every "
                                + interval + " " + timeUnit + ". Waiting "
                                + minutesToWait + " " + timeUnit + ".");
                        Debug.println("Movie maker watchdog set to run every "
                                + interval + " " + timeUnit + ". Waiting "
                                + minutesToWait + " " + timeUnit + ".");
                        break;
                    case COMMAND_S:
                        interval = 5;
                        timeUnit = TimeUnit.MINUTES;
                        minutesToWait = 5;
                        scheduler.scheduleAtFixedRate(storageWatchdog, minutesToWait, interval, timeUnit);
                        log.fine("Storage watchdog set to run every "
                                + interval + " " + timeUnit + ". Waiting "
                                + minutesToWait + " " + timeUnit + ".");
                        Debug.println("Storage watchdog set to run every "
                                + interval + " " + timeUnit + ". Waiting "
                                + minutesToWait + " " + timeUnit + ".");
                        break;
                    default:
                        Debug.println("Unrecognized argument: " + command);
                        log.severe("Unrecognized argument: " + command);
                        WeatherLogger.log(Level.SEVERE, "Unrecognized argument: " + command);
                        break;
                }
            }
        } else {
            Debug.println("Usage: -R (retrieval), -M (movie), -S (storage)");
            log.severe("No arguments passed to main method.");
            WeatherLogger.log(Level.SEVERE, "No arguments passed to main method.");
        }
    }

    /**
     * Schedules a ServerWatchdog. Returns if <code>watchdog</code> is null.
     * @param watchdog the ServerWatchdog to be scheduled
     * @param scheduler the ScheduledThreadPoolExecuter with which to schedule
     * @param waitTime the initial time to wait before starting
     * @param interval the interval to run the watchdog
     * @param unit the TimeUnit of the <code>waitTime</code> and <code>interval</code>
     */
    private static void scheduleWatchdog(ServerWatchdog watchdog, ScheduledThreadPoolExecutor scheduler, int waitTime, int interval, TimeUnit unit) {
        if (watchdog==null) {
            return;
        }
        scheduler.scheduleAtFixedRate(watchdog, waitTime, interval, unit);
        String name = watchdog.getClass().getName();
        logAndDebug(Level.FINE, "Starting " + name + " to run every " + interval 
                    +  " " + unit + " with a " + waitTime + " " +  unit+ " delay.");
        
    }

    /**
     * Logs a message with a fine level and prints to the debug console.
     * @param level the level at which to log
     * @param message the message to log/print;
     */
    private static void logAndDebug(Level level, String message) {
        Debug.println(message);
        log.log(level, message);
    }
    
}
