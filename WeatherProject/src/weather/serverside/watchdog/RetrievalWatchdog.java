package weather.serverside.watchdog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.resource.Resource;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.retrieval.RetrievalClient;
import weather.serverside.retrieval.RetrievalCommand;
import weather.serverside.retrieval.RetrievalCommandType;
import weather.serverside.utilities.ResourceCollectionTimeUtility;
import weather.serverside.utilities.ServiceControl;
import weather.serverside.utilities.WeatherServiceNames;

/**
 * This watchdog watches the resource retrieval system for errors.
 * Refactored from the old ServerWatchdog.
 * 
 * @author Joe Sharp
 * @author Dustin Jones (2010)
 * @author Ryan Kelly (2010)
 * @author Eric Subach (2010)
 * @author Patrick Brinich (2013)
 */
public class RetrievalWatchdog extends ServerWatchdog {

    /**
     * Base directory of storage.
     */
    private static String storageRootBase;
    /**
     * Positive multiple that adjusts how tolerant the system is before it is
     * restarted. Higher numbers indicate higher tolerance. A value of 15 states
     * that we need 15 consecutive misses before we register the missing data as
     * a problem. Should be set per Resource, not as a global for all Resources.
     */
    private long resourceRetrievalToleranceFactor = 15;
    /**Map to keep track of resource IDs and when they notified admins last*/
    private Map<Integer, Long> notificationMap;
    /**Number of consecutive failures for a resource*/
    private Map<Integer, Integer> failureCounts;

    /**
     * Constructs a new RetrievalWatchdog
     * @throws ClassNotFoundException from ServerWatchdog
     * @throws InstantiationException from ServerWatchdog
     * @throws IllegalAccessException from ServerWatchdog
     * @throws WeatherException from ServerWatchdog
     */
    public RetrievalWatchdog() throws ClassNotFoundException, InstantiationException, IllegalAccessException, WeatherException {
        super();
        notificationMap = new HashMap<>();
        failureCounts   = new HashMap<>();
        // Retrieve our base storage folder.
        storageRootBase = PropertyManager.getServerProperty("storageRootFolder");
        super.log.finer("Retrieved base storage folder.");
        Debug.println("New RetrievalWatchdog created with storage root folder " + storageRootBase );
    }

    /**
     * Checks if the Retrieval service is running
     * @return true if the Retrieval service is running; false otherwise
     */
    @Override
    public boolean isServiceRunning() {
        return ServiceControl.isRunning(WeatherServiceNames.RETRIEVAL);
    }

    /**
     * Restarts the Retrieval service and notifies the admin
     */
    @Override
    public void handleStoppedService() {
        notifyAdminServiceRestart();
        ServiceControl.restartService(WeatherServiceNames.RETRIEVAL);
    }

    /**
     * Checks all resources for retrieval errors
     * @param resources all Resources
     */
    @Override
    public void checkSystem(Vector<Resource> resources) {
        boolean restartSystem = false;
        for (Resource resource : resources) {
            if (isError(resource)) {
                incrementFailureCount(resource);
                notifyAdminResourceError(resource);
                restartResource(resource);
                
                if(failureCounts.get(resource.getResourceNumber())>=2) {
                    restartSystem = true;
                }    
            } else {
                //reset consecutive failure count
                failureCounts.put(resource.getResourceNumber(),0);
            }
        }
        
        if(restartSystem) {
            Debug.println("Restarting retrieval service");
            ServiceControl.restartService(WeatherServiceNames.RETRIEVAL);
        }
    }

    /**
     * Check if the retrieval of a particular resource has encountered an error.
     *
     * TODO: Implement time zone checks on retrieval times.
     * TODO: Implement checks for time span of specified times.
     *
     * @NOTE Currently, if the collection span is at specified
     *       times, the watchdog will not report any errors, even if they occur.
     * @NOTE I think the above note and TODOs are lying
     *
     * @param resource resource to check for errors
     * @return true if retrieval system encountered an error, false otherwise
     */
    private boolean isError(Resource resource) {
        if (!resource.isActive()) {
            Debug.println("Resource " + resource.getName() + " is not active in "
                    + "isResourceRetrievalError");
            return false;
        }
        
        Debug.println("Checking resource: "+resource.getResourceName());

        boolean isError;
        // Folder name for the resource.
        // Assumes local implementation
        // What about path separators?
        String folderName = storageRootBase + File.separator + resource.getStorageFolderName();

        // The most recent valid filestamp in a folder.
        long timestamp;
        long currentTime = System.currentTimeMillis();

        // Get number of seconds between resource instance acquisition.
        int frequency = resource.getFrequency();

        /*
         * errorTolerance holds the number of milliseconds we are willing to wait
         * for a resource to be collected before registering it as a problem.
         * The value is calculated using the class variable
         * resourceRetrievalToleranceFactor.
         * This variable keeps how many consecutive misses will constitute a
         * problem. So if tolerance is 15, then it takes 15 consecutive missing
         * data items
         * for us to register the missing data as a problem.
         */
        long errorTolerance;

        // How long it has been since we collected data for this resource.
        long errorMagnitude;

        errorTolerance = frequency
                * ResourceTimeManager.MILLISECONDS_PER_SECOND
                * resourceRetrievalToleranceFactor;

        GregorianCalendar currentCal;

        GregorianCalendar calStart, calEnd;
        long timeStart, timeEnd;

        currentCal = new GregorianCalendar();
        currentCal.setTimeInMillis(currentTime);

        if (ResourceCollectionTimeUtility.validDataCollectionTime(resource, currentCal)) {
             timestamp = getMostRecentValidFileTimestamp(folderName);
             errorMagnitude = currentTime - timestamp;
        } else {
            errorMagnitude = 0;//mark no missing data
        }
        
        // If the error is outside the tolerance range, the system encountered
        // an error, otherwise it is okay.
        // Time since we last collected data and now is too large, we have a problem
        isError = (errorMagnitude > errorTolerance);

        if (isError) {
            Debug.println("ERROR! in retrieval system on resource "
                    + resource.getName());

            super.log.fine("Retrieval system error.");
        }

        super.log.exiting("ServerWatchdog", "RetrievalWatchdog.isError()");
        return (isError);
    }

    /**
     * Gets the most recent, valid timestamp on a file in a given folder from
     * the server.
     *
     * @NOTE Movie folder structure is Year/Month/Day (e.g. 2010/May/29)
     *
     * @param folderName the folder name to look for the most recent, valid
     * timestamp.
     * @return the lastmodified() time of the most recent file (according to
     * timestamp), or 0 if no files
     */
    private long getMostRecentValidFileTimestamp(String folderName) {
        final String[] MONTH_ARRAY = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayList<String> months = new ArrayList<>(Arrays.asList(MONTH_ARRAY));

        try {
            File rootFolder = new File(folderName);
            File yearFolder = null;
            int nYear = 0;

            File monthFolder;
            String[] monthList;
            int nMonth;

            File dayFolder;
            String[] dayList;
            int nDay;

            File fileFolder;

            // Get list of all available years.
            String[] yearList = rootFolder.list();
            if (yearList == null || yearList.length == 0) {
                return 0;
            }

            // Get the highest year.
            for (String year : yearList) {
                if (yearFolder == null || year.compareTo(yearFolder.getName()) > 0) {
                    if (!year.equals((String) "Generic Movies")
                            && year.matches("^\\d*$")) //contains only characters 0-9
                    {
                        yearFolder = new File(rootFolder.getAbsoluteFile() + File.separator + year);
                        nYear = Integer.parseInt(year);
                    }
                }
            }

            // If no movies, return.
            if (yearFolder == null) {
                return (0);
            }

            // Get highest month.
            monthFolder = null;
            monthList = yearFolder.list();
            if (monthList == null || monthList.length == 0) {
                return 0;
            }
            nMonth = 0;

            for (String month : monthList) {
                if (monthFolder == null || (months.indexOf(month) > months.indexOf(monthFolder.getName()))) {
                    monthFolder = new File(yearFolder.getAbsoluteFile() + File.separator + month);
                    nMonth = months.indexOf(month);
                }
            }

            // If no movies, return.
            if (monthFolder == null) {
                return (0);
            }

            // Get highest day.
            dayFolder = null;
            dayList = monthFolder.list();
            if (dayList == null || dayList.length == 0) {
                return 0;
            }
            nDay = 0;

            for (String day : dayList) {
                if (dayFolder == null || Integer.parseInt(day) > Integer.parseInt(dayFolder.getName())) {
                    dayFolder = new File(monthFolder.getAbsoluteFile() + File.separator + day);
                    nDay = Integer.parseInt(day);
                }
            }

            // If no movies, return.
            if (dayFolder == null) {
                return (0);
            }

            // If need to check movies, append it to path.
            fileFolder = new File(dayFolder.getAbsolutePath());
            String[] fileList = fileFolder.list();

            //if there are no files in the file folder, go back one day to see if any are in the previous day's folder.
            if (fileList == null || fileList.length == 0) {
                Debug.println("folderName: " + folderName
                        + ", fileFolder: " + fileFolder
                        + ". This directory was empty .... Moving back one day....");
                Debug.println("This occurred at time: " + CalendarFormatter.formatTime(new GregorianCalendar()));

                if (nDay > 1) {
                    nDay--;
                } else {
                    if (nMonth > 0) {
                        nMonth--;
                        nDay = ResourceTimeManager.getDaysInMonth(nMonth, nYear);
                    } else {
                        nYear--;
                        nMonth = 11;
                        nDay = 31;
                    }
                }
                fileFolder = new File(folderName + File.separator + nYear + File.separator
                        + months.get(nMonth) + File.separator + nDay);
                //      Debug.println(fileFolder.getAbsolutePath());
                fileList = fileFolder.list();
            }

            // If no file, return.
            if (fileList == null || fileList.length == 0) {
                return (0);
            }

            String mostRecentFileName = fileList[fileList.length - 1];

            if (mostRecentFileName.equals("movies")) {
                if (fileList.length < 2) {
                    return (0);
                }
                mostRecentFileName = fileList[fileList.length - 2];
            }

            File mostRecentFile = new File(fileFolder.getAbsolutePath() + File.separator + mostRecentFileName);
            long lastModified = mostRecentFile.lastModified();

            return lastModified;
        } catch (Exception ex) {
            WeatherLogger.log(Level.SEVERE,
                    "Watchdog error getting the most recent time stamp. ", ex);
            //ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Notifies the admin about an error caused by a specific resource
     * @param resource the resource that caused the error
     */
    private void notifyAdminResourceError(Resource resource) {
        String resName     = resource.getResourceName();
        int    resID       = resource.getResourceNumber();
        long   currentTime = System.currentTimeMillis();

        String system = WeatherServiceNames.RETRIEVAL.getShortName();
        String error  = "A resource malfunctioned.";
        String action = "Retrieval of the resource was restarted.";
        String info = "This may indicate an error with the resource or the system." + " "
                    + "This message will be sent once daily until the issue is resolved.";
        
        if (failureCounts.containsKey(resID) && failureCounts.get(resID) >= 2) {
            error = "A resource malfunctioned twice in a row";
            action = "The retrieval system was restarted";
        }
        
        ServerWatchdogErrorEvent event;
        event = new ServerWatchdogErrorEvent(system, error, action, info, resource, currentTime);
        event.logError();
        event.printErrorToDebug();
        
        if (!resourceIDCanNotify(resID, currentTime)) {
            Debug.println("Notification not sent for " + resName);
        } else {
            event.notifyAdmin();
            notificationMap.put(resID, currentTime);
        }
        super.addServerWatchdogErrorEvent(event);
    }

    /**
     * Determines if a specific resource ID can send a notification. Notifications
     * are currently sent once per day
     * @param resID the resource ID
     * @param time the time the notification is trying to be sent. Usually the
     *             current time
     * @return true if the resource ID can notify; false otherwise.
     */
    private boolean resourceIDCanNotify(int resID, long time) {
        //failed two times in a row
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
     * Notifies the admin about needing to restart the retrieval service
     * @param notification the notification type
     * @param action the action type taken
     */
    private void notifyAdminServiceRestart() {
        String system = WeatherServiceNames.RETRIEVAL.getShortName();
        String error  = "The system service was stopped.";
        String action = "The system service was restarted.";
        String info = "This may indicate an error with the system. "
                    + "This message will be sent once daily until the issue is resolved.";
        
        ServerWatchdogErrorEvent event;
        event = new ServerWatchdogErrorEvent(system, error, action, info, null);
        event.logError();
        event.printErrorToDebug();
        event.notifyAdmin();
        super.addServerWatchdogErrorEvent(event);
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
            Debug.println("Could not restart retrieval of resource: " + resource.getResourceName());
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
