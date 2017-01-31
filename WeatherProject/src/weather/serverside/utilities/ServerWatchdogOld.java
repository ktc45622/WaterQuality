package weather.serverside.utilities;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceCollectionSpan;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.ResourceChangeListener;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.*;

/**
 * Watches processes on the server for errors and automates process restart
 * and notification of administrators.
 *
 * This class is normally executed as an operating system service.
 *
 * @author Joe Sharp
 * @author Dustin Jones (2010)
 * @author Ryan Kelly (2010)
 * @author Eric Subach (2010)
 */
public class ServerWatchdogOld extends Thread implements ResourceChangeListener{
    private DBMSSystemManager dbms;
    private Vector<Resource> allResources;

   // private static final int TIMEOUT = 300000; //5 minutes in milliseconds
    private static final String[] MONTH_ARRAY = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    private static ArrayList<String> months;

    // Commands (run as a new process) to stop/start the systems.
    private static final String[] STOP_RETRIEVAL = {"net", "stop", "\"WeatherProject Retrieval System\""};
    private static final String[] STOP_MOVIE_MAKER = {"net", "stop", "\"WeatherProject Movie System\""};
    private static final String[] STOP_STORAGE = {"net", "stop", "\"" + WeatherServiceNames.STORAGE.getLongName () + "\""};
    private static final String[] START_RETRIEVAL = {"net", "start", "\"WeatherProject Retrieval System\""};
    private static final String[] START_MOVIE_MAKER = {"net", "start", "\"WeatherProject Movie System\""};
    private static final String[] START_STORAGE = {"net", "start", "\"" + WeatherServiceNames.STORAGE.getLongName () + "\""};
    // Command to list all running services.
    private static final String[] LIST_RUNNING_SERVICES = {"net", "start"};
    // Command start/stop options.
    private static final String COMMAND_R = "-R";
    private static final String COMMAND_M = "-M";
    private static final String COMMAND_S = "-S";



    // What notification to send.
    private static enum NOTIFICATION_TYPE {THREE_IN_HOUR, FIVE_IN_DAY, 
                                            THREE_IN_DAY, ONE_IN_DAY};

    // Which system was restarted.
    private static enum RESTART_TYPE {
        MOVIE_MAKER ("Movie Maker"),
        RETRIEVAL ("Retrieval"),
        STORAGE ("Storage");
        
        String name;
        
        RESTART_TYPE (String name) {
            this.name = name;
        }
        
        @Override
        public String toString () {
            return (name);
        }
    };

    // What caused the error.
    private static enum ERROR_TYPE {
        RESOURCE ("A resource malfunctioned."),
        STOPPED_SERVICE ("The system service stopped."),
        STORAGE_UNAVAILABLE ("The storage system could not be contacted.");

        String message;

        ERROR_TYPE (String message)
        {
            this.message = message;
        }

        public String getMessage ()
        {
            return (message);
        }
    };
    
    // What action was taken after encountering the error.
    private static enum ACTION_TYPE {
        NONE ("No action was taken."),
        RESTART ("The system service was restarted.");

        String message;

        ACTION_TYPE (String message)
        {
            this.message = message;
        }

        public String getMessage ()
        {
            return (message);
        }
    }

    // Maps a resource ID of a system to the number of times that resource has
    // been restarted.
    private static Map<Integer, ArrayList<Long>> movieRestartCountMap = new HashMap<Integer, ArrayList<Long>>();
    private static Map<Integer, ArrayList<Long>> retrievalRestartCountMap = new HashMap<Integer, ArrayList<Long>>();

    //The next map should keep track of when administrators were notified.
    private static Map<Integer, Long> notificationMap = new HashMap<Integer, Long>();

    // Base directory of storage.
    private static String storageRootBase;

    // Flags for which systems should be monitored.
    private boolean checkMovieMaker;
    private boolean checkRetrieval;
    private boolean checkStorage;

    // Positive multiple that adjusts how tolerant the system is before it is
    // restarted. Higher numbers indicate higher tolerance.
    // A value of 15 states that we need 15 consecutive misses before we
    // register the missing data as a problem. Should be set per Resource, not
    // as a global for all Resources.
    //private long resourceRetrievalToleranceFactor = 2; //joe's setting
    private long resourceRetrievalToleranceFactor = 15;
    //private long movieMakerHourToleranceFactor = 2; //joe's setting
    //private long movieMakerMinuteToleranceFactor = 0; //joe's setting
    private long movieMakerHourToleranceFactor = 1; // why not 0?
    private long movieMakerMinuteToleranceFactor = 5;

    // Logger for watchdog.
    private static WeatherTracer log;



    /**
     * Create a new ServerWatchdog object.
     *
     * @throws ClassNotFoundException thrown by MySQLImpl
     * @throws InstantiationException thrown by MySQLImpl
     * @throws IllegalAccessException thrown by MySQLImpl
     * @throws WeatherException thrown if there is an error getting
     * the resource list from the MySQLImpl.
     */
    public ServerWatchdogOld() throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException, WeatherException {
        // Get logger.
        log = WeatherTracer.getWatchdogLog ();
        log.info ("New watchdog.");
        
        dbms =  MySQLImpl.getMySQLDMBSSystem();
        log.finer ("Got DBMS.");

        // Get all of our resources.
        reloadResources ();
        log.finer ("Got all resources.");

        // By default, don't check any systems.
        this.checkMovieMaker = false;
        this.checkRetrieval = false;
        this.checkStorage = false;

        // Make arraylist of month names.
        months = new ArrayList<String> (Arrays.asList(MONTH_ARRAY));

        // Retrieve our base storage folder.
        storageRootBase = PropertyManager.getGeneralProperty("storageRootFolder");
        log.finer ("Retrieved base storage folder.");
    }


    /**
     * Reloads the list of resources.
     */
    private void reloadResources () {
        if(allResources!=null){
            allResources.clear(); //Empty Vector
        }
        allResources = dbms.getResourceManager().getResourceList();
    }


    /**
     * Sets the boolean flag to run a check on the movie maker system. Also
     * updates the resource list.
     */
    public void setCheckMovieMaker (boolean value){
        allResources = dbms.getResourceManager().getResourceList();
        checkMovieMaker = value;
    }

    /**
     * Sets the boolean flag to run a check on the retrieval system.
     */
    public void setCheckRetrieval (boolean value){
        checkRetrieval = value;
        allResources = dbms.getResourceManager().getResourceList();
    }

    /**
     * Sets the boolean flag to run a check on the retrieval system.
     */
    public void setCheckStorage (boolean value){
        checkStorage = value;
        allResources = dbms.getResourceManager().getResourceList();
    }


    /**
     * Check if the retrieval of a particular resource has encountered an error.
     *
     * TODO: Implement time zone checks on retrieval times.
     * TODO: Implement checks for time span of specified times.
     *
     * @NOTE Currently, if the collection span is at specified
     *       times, the watchdog will not report any errors, even if they occur.
     *
     * @param resource resource to check for errors
     * @return true if retrieval system encountered an error, false otherwise
     */
    private boolean isResourceRetrievalError (Resource resource) {
        log.entering ("ServerWatchdog", "isResourceRetrievalError");
        //Can't have an error unless resource is both valid and active.
        if(!resource.isActive()){
            log.finer("Resource " + resource.getName()+" is not active in "+
                    "isResourceRetrievalError");
            log.exiting ("ServerWatchdog", "isResourceRetrievalError");
            return false;
        }
        boolean isError;
        // Folder name for the resource.
        // Assumes local implementation
        // What about path separators?
        String folderName = storageRootBase +File.separator + resource.getStorageFolderName();

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

        errorTolerance = frequency *
                         ResourceTimeManager.MILLISECONDS_PER_SECOND *
                         resourceRetrievalToleranceFactor;

        GregorianCalendar currentCal;

        GregorianCalendar calStart, calEnd;
        long timeStart, timeEnd;

        currentCal = new GregorianCalendar ();
        currentCal.setTimeInMillis (currentTime);

        
        // If collection time is only during daylight hours.
        if(resource.getCollectionSpan() == ResourceCollectionSpan.DaylightHours) {
            // Start and end collection times.
            calStart = ResourceCollectionTimeUtility.getCollectionStartTime (resource, currentCal);
            calEnd = ResourceCollectionTimeUtility.getCollectionStopTime (resource, currentCal);

            timeStart = calStart.getTimeInMillis ();
            timeEnd = calEnd.getTimeInMillis ();

            // If time is between start and end, the resource must be checked.
            if (currentTime > timeStart && currentTime < timeEnd) {
                timestamp = getMostRecentValidFileTimestamp (folderName, false);

                // Calculate how long it has been since we got data
                errorMagnitude = currentTime - timestamp;

                log.finest ("Recent timestamp: " + timestamp);
                log.finest ("Current time: " + currentTime);
                log.finest ("Time since last collected data: "+ errorMagnitude );
            }
            // If time is not within collection times, don't check the resource.
            else {
                errorMagnitude = 0;//mark no missing data
            }
        }
        // Resource should have new data all the time, so check.
        else if (resource.getCollectionSpan() == ResourceCollectionSpan.FullTime) {
            timestamp = getMostRecentValidFileTimestamp (folderName, false);

            errorMagnitude = currentTime - timestamp;
        }
        // Check for correctness - code written 2/12/2011
        // Specified times are specified.
        else {
             // What is the start and end collection times.
              calStart = ResourceCollectionTimeUtility.getCollectionStartTime (resource, currentCal);
              calEnd = ResourceCollectionTimeUtility.getCollectionStopTime (resource, currentCal);

              timeStart = calStart.getTimeInMillis ();
              timeEnd = calEnd.getTimeInMillis ();

              // If time is between start and end, the resource must be checked.
              if (currentTime > timeStart && currentTime < timeEnd) {
                   timestamp = getMostRecentValidFileTimestamp (folderName, false);

                // Calculate how long it has been since we got data
                errorMagnitude = currentTime - timestamp;

                log.finest ("Recent timestamp: " + timestamp);
                log.finest ("Current time: " + currentTime);
                log.finest ("Time since last collected data: "+ errorMagnitude );
            }
            // If time is not within collection times, don't check the resource.
            else {
                errorMagnitude = 0;//mark no missing data
            }
        }
        
        // If the error is outside the tolerance range, the system encountered
        // an error, otherwise it is okay.
        // Time since we last colelcted data and now is too large, we have a problem
        isError = (errorMagnitude > errorTolerance);

        if (isError) {
            Debug.println ("ERROR! in retrieval system on resource "
                    +resource.getName());

            log.fine ("Retrieval system error.");
        }

        log.exiting ("ServerWatchdog", "isResourceRetrievalError");

        return (isError);
    }


    /**
     * Check if there has been an error with the movie maker system.
     *
     * TODO: Currently, this is implemented with a crude check of when the last
     * movie has been made. If the difference between that time and the current
     * time is too large, the watchdog thinks an error occurred.
     *
     * TODO: Implement checks for time span of specified times.
     *
     * @NOTE Currently, if the collection span is at specified
     *       times, the watchdog will not report any errors, even if they occur.
     *
     * @param resource resource to check for errors
     * @return true if movie system encountered an error, false otherwise
     */
    private boolean isMovieMakerError (Resource resource) {
        log.entering ("ServerWatchdog", "isMovieMakerError");
        //Can't have an error unless resource is both valid and active.
        if(!resource.isActive()){
            log.finer("Resource " + resource.getName()+" is not active "+
                    "in isMovieMakerError");
            log.exiting ("ServerWatchdog", "isMovieMakerError");
            return false;
        }
        boolean isError;
        // Folder name for the resource.
        String folderName = storageRootBase+File.separator+resource.getStorageFolderName();

        // The most recent valid file timestamp in a folder.
        long timestamp;
        long currentTime = System.currentTimeMillis();

       /*
        * errorTolerance holds the number of milliseconds we are willing to wait
        * until we expect to see a new movie. This code needs to be checked by hand.
        * TODO: check
        */
        long errorTolerance;

        // How long it has been since we last made a movie for this resource.
        long errorMagnitude;

        /*
         * How many hours to wait in milliseconds + 
         * how many minutes to wait in milliseconds.
         * 
         * 
         */
        errorTolerance = ResourceTimeManager.MILLISECONDS_PER_HOUR *
                         movieMakerHourToleranceFactor
                         +
                         ResourceTimeManager.MILLISECONDS_PER_MINUTE *
                         resourceRetrievalToleranceFactor *
                         movieMakerMinuteToleranceFactor;

        GregorianCalendar currentCal;

        GregorianCalendar calStart, calEnd;
        long timeStart, timeEnd;



        // Get the current date in a calendar.
        currentCal = new GregorianCalendar ();
        currentCal.setTimeInMillis (currentTime);


        // If collection time is only during daylight hours.
        if(resource.getCollectionSpan() == ResourceCollectionSpan.DaylightHours)
        {
            // Start and end collection times.
            calStart = ResourceCollectionTimeUtility.getCollectionStartTime (resource, currentCal);
            calEnd = ResourceCollectionTimeUtility.getCollectionStopTime (resource, currentCal);

            // Movies are made 1 hour after collection time, 5 minutes past the
            // hour, so account for it.
            calStart.add (GregorianCalendar.HOUR_OF_DAY, 1);
            calStart.add (GregorianCalendar.MINUTE, 10);
            calEnd.add (GregorianCalendar.HOUR_OF_DAY, 1);
            calEnd.add (GregorianCalendar.MINUTE, 10);

            timeStart = calStart.getTimeInMillis ();
            timeEnd = calEnd.getTimeInMillis ();

            // If time is between start and end, the resource must be checked.
            if (currentTime > timeStart && currentTime < timeEnd) {
                timestamp = getMostRecentValidFileTimestamp (folderName, true);
                //if no files exist, the resource was just specified
                if(timestamp == 0)
                    timestamp = resource.getDateInitiated().getTime();

                // Calculate how large the error is.
                errorMagnitude = currentTime - timestamp;

                log.finest ("Recent timestamp: " + timestamp);
                log.finest ("Current time: " + currentTime);
            }
            // If time is not within collection times, don't check the resource.
            else {
                errorMagnitude = 0;
            }
        }
        else if (resource.getCollectionSpan() == ResourceCollectionSpan.FullTime) {
            timestamp = getMostRecentValidFileTimestamp (folderName, true);
            //if no files exist, the resource was just specified
            if(timestamp == 0)
                timestamp = resource.getDateInitiated().getTime();

            errorMagnitude = currentTime - timestamp;
            log.finest ("Recent timestamp: " + timestamp);
            log.finest ("Current time: " + currentTime);
            log.finest ("Time since last collected data: "+ errorMagnitude );
        }
        // TODO: Specified times.
        else {
             errorMagnitude = 0;
             // What is the start and end collection times.
             calStart = ResourceCollectionTimeUtility.getCollectionStartTime (resource, currentCal);
             calEnd = ResourceCollectionTimeUtility.getCollectionStopTime (resource, currentCal);
             // Movies are made 1 hour after collection time, 5 minutes past the
             // hour, so account for it.
             calStart.add (GregorianCalendar.HOUR_OF_DAY, 1);
             calStart.add (GregorianCalendar.MINUTE, 10);
             calEnd.add (GregorianCalendar.HOUR_OF_DAY, 1);
             calEnd.add (GregorianCalendar.MINUTE, 10);
             timeStart = calStart.getTimeInMillis ();
             timeEnd = calEnd.getTimeInMillis ();

             // If time is between start and end, the resource must be checked.
             if (currentTime > timeStart && currentTime < timeEnd) {
                timestamp = getMostRecentValidFileTimestamp (folderName, false);
                //if no files exist, the resource was just specified
                if(timestamp == 0)
                    timestamp = resource.getDateInitiated().getTime();

                // Calculate how long it has been since we got data
                errorMagnitude = currentTime - timestamp;

                log.finest ("Recent timestamp: " + timestamp);
                log.finest ("Current time: " + currentTime);
                log.finest ("Time since last collected data: "+ errorMagnitude );
              }
              else {
                  errorMagnitude = 0;
              }

              log.finest ("errorTolerance:  " + errorTolerance);
              log.finest ("Error Magnitude: " + errorMagnitude);
        }

        // If the error is outside the tolerance range, the system encountered
        // an error, otherwise it is okay.
        isError = (errorMagnitude > errorTolerance);

        if (isError) {
            Debug.println ("ERROR! in movie maker system. Resource was "+
                    resource.getName());
            log.fine ("Movie maker system error.");
        }


        log.exiting ("ServerWatchdog", "isMovieMakerError");

        return (isError);
    }

    /**
     * Check if there has been an error with the storage system.
     * TODO: actually make it check things?
     * @return true if there was an error, false otherwise
     */
    private boolean isStorageAvailableError () {
        log.entering ("ServerWatchdog", "isStorageAvailableError");

        boolean reachable = false;

        /*
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            Debug.println("Start");
            Debug.println(address.getHostAddress());

            reachable = address.isReachable(100);

            Debug.println("Reachable: "+reachable);
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
        */
        // @DEBUG
        reachable = true;

        if (!reachable) {
            log.severe ("Storage not available.");
            Debug.println("Storage system not reachable.");
        }

        log.exiting ("ServerWatchdog", "isStorageAvailableError");

        return (reachable);
    }

    /**
     * Gets the most recent, valid timestamp on a file in a given folder from the server.
     *
     * @NOTE Movie folder structure is Year/Month/Day (e.g. 2010/May/29)
     * @NOTE Call this function with checkMovieFolder to true only if movies are
     *       expected to be available
     *
     * @param folderName the folder name to look for the most recent, valid timestamp.
     * @param checkMovieFolder whether or not to check the movie folder for a timestamp.
     * @return the lastmodified() time of the most recent file (according to timestamp), or 0 if no files
     */
    private long getMostRecentValidFileTimestamp(String folderName, boolean checkMovieFolder){
        try{
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
            if(yearList == null || yearList.length == 0)
                return 0;

            // Get the highest year.
            for(String year : yearList){
                if(yearFolder == null || year.compareTo(yearFolder.getName())>0){
                    if(!year.equals((String)"Generic Movies") &&
                            year.matches("^\\d*$")) //contains only characters 0-9
                    {
                        yearFolder = new File(rootFolder.getAbsoluteFile()+File.separator+year);
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
            if(monthList == null || monthList.length == 0)
                return 0;
            nMonth = 0;

            for(String month : monthList){
                if(monthFolder == null || (months.indexOf(month) > months.indexOf(monthFolder.getName()))){
                    monthFolder = new File(yearFolder.getAbsoluteFile()+File.separator+month);
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
            if(dayList == null || dayList.length == 0)
                return 0;
            nDay = 0;
            
            for(String day : dayList){
                if(dayFolder == null || Integer.parseInt(day)>Integer.parseInt(dayFolder.getName())){
                    dayFolder = new File(monthFolder.getAbsoluteFile()+File.separator+day);
                    nDay = Integer.parseInt(day);
                }
            }

            // If no movies, return.
            if (dayFolder == null) {
                return (0);
            }

            // If need to check movies, append it to path.
            fileFolder = null;
            if (checkMovieFolder) {
                fileFolder = new File(dayFolder.getAbsoluteFile()+File.separator+"movies");
            }
            else {
                fileFolder = new File(dayFolder.getAbsolutePath());
            }

            
            String[] fileList = fileFolder.list();

            //if there are no files in the file folder, go back one day to see if any are in the previous day's folder.
            if (fileList == null || fileList.length == 0) {
                Debug.println("folderName: "+ folderName +
                        ", fileFolder: " + fileFolder +
                        ". This directory was empty .... Moving back one day....");
                Debug.println ("This occurred at time: " + CalendarFormatter.formatTime (new GregorianCalendar ()));

                if(nDay > 1)
                    nDay--;
                else{
                    if(nMonth > 0){
                        nMonth--;
                        nDay = ResourceTimeManager.getDaysInMonth(nMonth, nYear);
                    }
                    else{
                        nYear--;
                        nMonth = 11;
                        nDay = 31;
                    }
                }
                fileFolder = new File(folderName+File.separator+nYear+File.separator
                        +months.get(nMonth)+File.separator+nDay+(checkMovieFolder?File.separator+"movies":""));
          //      Debug.println(fileFolder.getAbsolutePath());
                fileList = fileFolder.list();
            }

            // If no file, return.
            if (fileList == null || fileList.length == 0) {
                return (0);
            }

            String mostRecentFileName = fileList[fileList.length-1];

            if(mostRecentFileName.equals("movies")) {
                if (fileList.length < 2) {
                    return (0);
                }
                mostRecentFileName = fileList[fileList.length-2];
            }

            File mostRecentFile = new File(fileFolder.getAbsolutePath()+File.separator+mostRecentFileName);
            long lastModified = mostRecentFile.lastModified();

//            Debug.println(ResourceTimeManager.getFormattedTimeString(lastModified));
            return lastModified;
        }catch(Exception ex){
            WeatherLogger.log(Level.SEVERE,
             "Watchdog error getting the most recent time stamp. ", ex);
            //ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Restart the MovieMaker system process.
     */
    private void restartMovieMaker() {
        // First, stop it with the STOP_MOVIE_MAKER command in a process, then
        // start it again with the START_MOVIE_MAKER command in a process.

        Debug.println("Restarting Movie Maker...");
        log.finer ("Restarting movie maker system.");
        
        try {
            runProcess(STOP_MOVIE_MAKER);
        }
        catch(IOException ex) {
            WeatherLogger.log(Level.SEVERE,
             "Watchdog error trying to stop the moviemaker system. ", ex);
            //ex.printStackTrace();
        }

        try {
            runProcess(START_MOVIE_MAKER);
        }
        catch (IOException ex) {
             WeatherLogger.log(Level.SEVERE,
             "Watchdog error trying to start the moviemaker system. ", ex);
            //ex.printStackTrace();
        }

        log.finer ("Movie maker system restarted.");
    }

    /**
     * Restart the retrieval system process.
     */
    private void restartRetrieval() {
        // First, stop it with the STOP_RETRIEVAL command in a process, then
        // start it again with the START_RETRIEVAL command in a process.

        Debug.println("Restarting Retrieval System...");
        log.finer ("Restarting retrieval system.");


        try {
            runProcess(STOP_RETRIEVAL);
        }
        catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE,
             "Watchdog error trying to stop the Retrieval system. ", ex);
            //ex.printStackTrace();
        }



        try {
            runProcess(START_RETRIEVAL);
        }
        catch (IOException ex) {
             WeatherLogger.log(Level.SEVERE,
             "Watchdog error trying to start the Retrieval system. ", ex);
            //ex.printStackTrace();
        }

        log.finer ("Retrieval system restarted.");
    }

    /**
     * Restart the Storage system process.
     */
    private void restartStorage() {
        // First, stop it with the STOP_STORAGE command in a process, the
        // start it again with the START_STORAGE command in a process.

        Debug.println("Restarting storage system...");
        log.finer ("Restarting storage system.");

        try {
            runProcess(STOP_STORAGE);
        }
        catch(IOException ex) {
            WeatherLogger.log(Level.SEVERE,
             "Watchdog error trying to stop the storage system. ", ex);
            //ex.printStackTrace();
        }

        try {
            runProcess(START_STORAGE);
        }
        catch (IOException ex) {
             WeatherLogger.log(Level.SEVERE,
             "Watchdog error trying to start the storage system. ", ex);
            //ex.printStackTrace();
        }

        log.finer ("Storage system restarted.");
    }

    /**
     * Runs a process on the server.
     *
     * Uses a ProcessBuilder to run the process and output any errors.
     *
     * TODO: - Exceptions are currently unhandled.
     * 
     * @param process string array containing the process to run and the arguments
     * @throws IOException
     *
     * @return output from running the process
     */
    private String runProcess(String[] process) throws IOException {
        log.finest ("Starting runProcess");

        // Total output.
        String output = "";
        // Single line of output.
        String outputLine = "";

        // Build a process from the command.
        ProcessBuilder pb = new ProcessBuilder(process);
        Process proc = pb.start();

        // Make scanners from the output and error streams.
        Scanner outputScanner = new Scanner(proc.getInputStream());
        Scanner errorScanner = new Scanner(proc.getErrorStream());

        // Write all output.
        while(outputScanner.hasNextLine()) {
            outputLine = outputScanner.nextLine ();
            output += outputLine;

            //Debug.println(outputLine);
        }

        // Write all errors.
        while(errorScanner.hasNextLine()) {
//            Debug.println(errorScanner.nextLine());
        }

        try {
            // Wait until process has terminated.
            proc.waitFor();
        }
        // Print error message to standard output stream and print stack trace.
        catch (InterruptedException ex) {
            Debug.println ("Process from: \"" + process + "\" was "
                              + "interrupted!");
           WeatherLogger.log(Level.SEVERE,
             "Process from: \"" + process + "\" was "
                              + "interrupted!", ex);
            //ex.printStackTrace();
        }

        log.finest ("Finished runProcess");

        return (output);
    }


    /**
     * Update the list of times a resource in the 
     * specified system has been restarted and notify administrators, if necessary.
     *
     * @param resource resource to register
     * @param type type of restart
     */
    private void registerRestart(Resource resource, RESTART_TYPE type) {
        switch (type) {
            case RETRIEVAL:
                updateRestartTimesAndNotify (retrievalRestartCountMap, resource, type);
                break;
            case MOVIE_MAKER:
                updateRestartTimesAndNotify (movieRestartCountMap, resource, type);
                break;
        }
    }


    /*
     * Helper method for registerRestart ().
     */
    private void updateRestartTimesAndNotify (Map<Integer, ArrayList<Long>> map,
            Resource resource, RESTART_TYPE type) {
        // Resource ID.
        int resourceID;
        int numRestarts;
        // Arraylist of resource restart times.
        // Each entry in the map is an array of restart times, key is resourceID
        ArrayList<Long> restartTimes;
        long currentTime;

        // Get resource ID.
        resourceID = resource.getResourceNumber();

        // If resource has been restarted before, increment its count.
        if (map.containsKey(resourceID)) {
            restartTimes = map.get (resourceID);
            currentTime = System.currentTimeMillis ();

            // Add current time to arraylist of restart times.
            restartTimes.add(currentTime);

            numRestarts = restartTimes.size();

            //@TODO -- This code seems to be  wrong
            if (numRestarts >= 3) {
                //Get time of third one back -- why
                long threeTime = restartTimes.get(numRestarts - 3);
                if (currentTime - threeTime <
                    ResourceTimeManager.MILLISECONDS_PER_HOUR) {
                    notifyAdmin(resource, ERROR_TYPE.RESOURCE, type, 
                            NOTIFICATION_TYPE.THREE_IN_HOUR, ACTION_TYPE.RESTART,
                            currentTime);
                }
                else if (numRestarts >= 5) {
                      long fiveTime = restartTimes.get(numRestarts - 5);

                      if (currentTime - fiveTime <
                          ResourceTimeManager.MILLISECONDS_PER_DAY){
                          notifyAdmin(resource, ERROR_TYPE.RESOURCE, type,
                             NOTIFICATION_TYPE.FIVE_IN_DAY, ACTION_TYPE.RESTART,
                             currentTime);
                      }
                }
            }

        }
        // If resource has not been restarted, add current time to hash map.
        else {
            restartTimes = new ArrayList<Long>();
            //Add the current restart to the list for this resource
            restartTimes.add(System.currentTimeMillis());
            map.put (resourceID, restartTimes);
        }
    }


    /**
     * Send email notification on error of a monitored system.
     *
     * If the administrator has been emailed within one day of the current time,
     * the email is not sent 
     * (that way the administrators only gets one email per day).
     *
     * @param resource resource that caused error; if no resource, is null
     * @param errorType cause of error
     * @param type type of restart
     * @param note_type type of notification
     * @param actionType
     * @param currentTime current time
     */
    private static void notifyAdmin(Resource resource, ERROR_TYPE errorType, RESTART_TYPE type, NOTIFICATION_TYPE note_type, ACTION_TYPE actionType, long currentTime) {
        // Which system encountered an error.
        String systemError;
        // Time between restarts (hour or day).
        String noteMsg;
        // Frequency of restarts (3 or 5).
        String numMsg;
        String message, subject;


        String resourceName = "";
        int resourceID = -1;

        DateFormat formatDate, formatTime;
        Date date;


        formatDate = new SimpleDateFormat ("MM-dd-yyyy");
        formatTime = new SimpleDateFormat ("hh:mm:ss a");
        date = new Date (currentTime);

        log.finer ("Date: " + formatDate.format (date));
        log.finer ("System: " + type);
        log.severe ("Error type: " + errorType);

        // If the cause was a resource.
        if (resource != null)  {
            // Get info about resource.
            resourceName = resource.getName ();
            resourceID = resource.getResourceNumber ();
            log.severe ("Cause of error was resource " +
                         resourceName + ", #" + resourceID);

            //Following code assumes once per day -- this needs to be fixed
            //TODO: -- fix code for different notification time periods
            if(notificationMap.containsKey(resourceID)){
                long lastNotification = notificationMap.get(resourceID);
                if(currentTime - lastNotification <= 
                   ResourceTimeManager.MILLISECONDS_PER_DAY){
                    Debug.println("Notification not sent for "+
                            resource.getName()+ " at "+
                            formatDate.format (date));
                    return;
                }
                notificationMap.remove(resourceID);//Longer than a day since last notification sent
            }
            notificationMap.put(resourceID, currentTime); //Put in new notification time
   
        }
        else { //Resource is null
            WeatherLogger.log(Level.SEVERE,"Resource is null in notifyAdmins.");
            return;
        }


        // Determine output messages based on error type.

        /*if (type == RESTART_TYPE.MOVIE_MAKER) {
            systemError = "Movie Maker System";
        }
        else {
            systemError = "Resource Retrieval System";
        }*/

        if (note_type == NOTIFICATION_TYPE.ONE_IN_DAY) {
            numMsg = "one (1) time";
        }
        else if(note_type == NOTIFICATION_TYPE.FIVE_IN_DAY) {
            numMsg = "five (5) times";
        }
        else {
            numMsg = "three (3) times";
        }

        if (note_type == NOTIFICATION_TYPE.THREE_IN_HOUR) {
            noteMsg = "hour";
        }
        else {
            noteMsg = "day";
        }


        message = "This is an automated message from the Server Watchdog. "
                + "An error has occurred.\n"
                + "\n"
                + "System:   " + type + "\n"
                + "Error Type: " + errorType.getMessage () + "\n";

        if (errorType == ERROR_TYPE.RESOURCE) {
            message += "Resource: " + resourceName + ", " + resourceID + "\n";
        }

        message += "Action Taken: " + actionType.getMessage () + "\n"
                + "\n"
                + "Date: " + formatDate.format (date) + "\n"
                + "Time: " + formatTime.format (date) + "\n"
                + "\n"
                //+ "This system has been restarted " + numMsg + " in one "
                //+ noteMsg + ". "
                + "This could indicate a problem with the system or the "
                + "resource. This message with be sent once per day until the "
                + "issue is resolved.";

        subject = "Server Watchdog: " + type + " System Error";

        // Send messages.
        try {
            Emailer.emailAdmin(message, subject);
            //Debug.println ("Error E-mail sent to admins.");
            log.fine ("Administrators have been notified by email.");
        }
        catch(WeatherException ex) {
           // ex.printStackTrace();
            WeatherLogger.log(Level.SEVERE,"Error while trying to email admins.",ex );
            log.severe ("Error emailing admins.", ex);
            Debug.println ("Error while trying to email admins.");
        }
    }
    
    
    /**
     * Check if a given service (long version of the name) is running.
     *
     * @param serviceList list of all the services running
     * @param longName the long name of the service
     *
     * @return true if the service is running, false otherwise
     */
    private boolean isServiceRunning (String serviceList, String longName)
    {
        return (serviceList.contains (longName));
    }


    /**
     * Checks to see if a service is running and restarts it if it has stopped.
     *
     * TODO: handle exceptions
     *
     * @param service - checks which service to use
     *
     * @return true if the service needed to be restarted, false otherwise
     */
    private boolean runServiceChecker (WeatherServiceNames service)
    {
        // Is there a problem with the service.
        boolean serviceRestart = false;
        // List of all running services.
        String output = "";
        String longName = service.getLongName ();
        String shortName = service.getShortName ();


        try {
            // Get list of all running processes.
            output = runProcess (LIST_RUNNING_SERVICES);
            log.finer ("Got list of all running processes");

            // If the service is not running, restart it.
            if (!isServiceRunning (output, longName)) {
                log.info ("Service " + longName + " needs to be restarted.");
                serviceRestart = true;
                runProcess (new String[] {LIST_RUNNING_SERVICES + shortName});
                log.info ("Service " + longName + " has been restarted.");
            }
        }
        catch (IOException e) {

        }

        return (serviceRestart);
    }

    /**
     * Runs the ServerWatchdog thread.
     * 
     * When run, the thread can do one of four
     * things, depending on the variables 'checkMovieMaker' and 'checkRetrieval'.
     * It will do nothing if both of these values are false.
     * It can check the movie maker, check the retrieval process, or both.
     *
     * --MovieMaker checking
     *  Looks through allResources - if the resource is an active camera or
     *  weather site, the process checks that movie resource.
     *  If isMovieMakerError (currentResource) is not true, then the system
     *  states that an error has occurred, and restarts the movie maker process.
     *
     *
     * --Retrieval process checking
     * Looks through allResources - if the resource is active and
     * isResourceRetrievalError returns true, then the process reports that an
     * error has occurred and restarts the retrieval system.
     *
     * TODO: Should the resource list be updated each time this is run? Could it
     *       hold outdated info?
     */
    @Override
    public void run() {
        log.info ("Starting run.");

        WeatherResourceType type;
        String name;

        // If the service needed a restart.
        boolean serviceRestart = false;

        // Make sure we have an updated resource list.
        // @NOTE: This is a temporary solution. This method is called each time
        //        a thread is run and is run more than needed. The watchdog
        //        class should be informed when the resource list is not up to
        //        date.
        reloadResources ();


        // Check movie maker system.
        if (checkMovieMaker){
//            Debug.println("Checking Movie Maker...");
            log.info ("Checking movie maker.");

            //serviceRestart = runServiceChecker (WeatherServiceNames.MOVIE);
            serviceRestart=ServiceControl.isRunning(WeatherServiceNames.MOVIE);
            // If had to restart, notify admins.
            if (serviceRestart) {
                notifyAdmin (null, ERROR_TYPE.STOPPED_SERVICE, RESTART_TYPE.MOVIE_MAKER, NOTIFICATION_TYPE.THREE_IN_DAY, ACTION_TYPE.RESTART, System.currentTimeMillis ());
            }

            // Check all resources.
            for (Resource resource : allResources){
                type = resource.getResourceType ();

                // If active and is either a weather camera or weather
                // map loop.
                if (resource.isActive() &&
                   (type == WeatherResourceType.WeatherCamera ||
                   type == WeatherResourceType.WeatherMapLoop)) {

                    // If error, notify admins and restart.
                    if(isMovieMakerError (resource)) {
                        name = resource.getResourceName ();

                        Debug.println ("Restarting movie system due to error detected on: " + name);
                        notifyAdmin (resource, ERROR_TYPE.RESOURCE, RESTART_TYPE.MOVIE_MAKER, NOTIFICATION_TYPE.THREE_IN_DAY, ACTION_TYPE.RESTART, System.currentTimeMillis ());
                        //restartMovieMaker ();
                        ServiceControl.restartService(WeatherServiceNames.MOVIE);
                    }
                }
            }
         //   Debug.println("Done Checking Movie Maker.");
            log.info ("Movie maker checked.");
        }

        // Check resource retrieval system.
        else if (checkRetrieval) {
          //  Debug.println ("Checking Retrieval System.");
            log.info ("Checking retrieval system.");

            //serviceRestart = runServiceChecker (WeatherServiceNames.RETRIEVAL);
            serviceRestart=ServiceControl.isRunning(WeatherServiceNames.WEATHERDB.RETRIEVAL);
            // If had to restart, notify admins.
            if (serviceRestart) {
                notifyAdmin (null, ERROR_TYPE.STOPPED_SERVICE, RESTART_TYPE.RETRIEVAL, NOTIFICATION_TYPE.THREE_IN_DAY, ACTION_TYPE.RESTART, System.currentTimeMillis ());
            }

            // Check all resources.
            for (Resource resource : allResources) {
                // If active, and error retrieving resource, notify admins and
                // restart.
                if (resource.isActive () && isResourceRetrievalError (resource)) {
                    Debug.println ("Restarting retrieval system due to error detected on: " + resource.getResourceName ());
                    notifyAdmin (resource, ERROR_TYPE.RESOURCE, RESTART_TYPE.RETRIEVAL, NOTIFICATION_TYPE.THREE_IN_DAY, ACTION_TYPE.RESTART, System.currentTimeMillis ());
                    //restartRetrieval ();
                    ServiceControl.restartService(WeatherServiceNames.RETRIEVAL);
                }
            }
         //   Debug.println ("Done Checking Retrieval System.");
            log.info ("Retrieval system checked.");
        }

        // Check storage system.
        else if (checkStorage) {
          //  Debug.println ("Checking storage system.");
            log.info ("Checking storage system.");

            //serviceRestart = runServiceChecker (WeatherServiceNames.STORAGE);
            serviceRestart=ServiceControl.isRunning(WeatherServiceNames.STORAGE);
            // If had to restart, notify admins.
            if (serviceRestart) {
                notifyAdmin (null, ERROR_TYPE.STOPPED_SERVICE, RESTART_TYPE.STORAGE, NOTIFICATION_TYPE.THREE_IN_DAY, ACTION_TYPE.RESTART, System.currentTimeMillis ());
            }

            if (isStorageAvailableError ()) {
                notifyAdmin (null, ERROR_TYPE.STORAGE_UNAVAILABLE, RESTART_TYPE.STORAGE, NOTIFICATION_TYPE.THREE_IN_DAY, ACTION_TYPE.RESTART, System.currentTimeMillis ());
                //restartStorage ();
                ServiceControl.restartService(WeatherServiceNames.STORAGE);
            }

         //   Debug.println ("Done checking storage system.");
            log.info ("Storage system checked.");
        }

        log.info ("Finished run.");
    }

    // TODO: -- code these methods
    // Right now the run method has a quick and easy fix that works well
    // and is the only fix possible.
    // see @NOTE in run method
    // The methods below can't be implemented until ServerWatch dog becomes a
    // server or object that accepts commands from the database.
    // Right now the database has no way to send commands to this object.
    // This thread is normally running in a different JVM on a different physical machine.
    
    @Override
    public boolean addResource(Resource resource) {
        //throw new UnsupportedOperationException("Not supported yet.");
        // Need to add a resource to the list being checked
        // If active and valid -- needs to be checked
        
        return true;
    }

    @Override
    public boolean updateResource(Resource resource) {
         //throw new UnsupportedOperationException("Not supported yet.");
        // Need to update a resource
        // Update the information for this resoruce in our list.
        // Make sure we are still checking it if it is

        return true;
    }

    @Override
    public boolean removeResource(Resource resource) {
        // throw new UnsupportedOperationException("Not supported yet.");
        // Need to remove a resource from the list to be checked
        // make sure it is in our list first

        return true;
    }

    /**
     * Creates a new ServerWatchdog to watch any of the movie maker, data retrieval, and storage systems.
     *
     * Watch dogs are named movieWatchdog, and retrievalWatchdog, respectively.
     *
     * Retrieval watchdog is started at the next 10 minute interval, and is
     * scheduled to run every 10 minutes.
     *
     * Movie watchdog is started at 8 minutes past the hour, and is scheduled
     * to run once per hour.
     *
     * Storage watchdog is started at the next 5 minute interval, and is
     * scheduled to run every 5 minutes.
     * 
     * @param commands commands to choose whether to start the movie watchdog
     * or the retrieval watchdog, or both.  Command must be "-R" to start
     * retrieval watchdog, "-M" for movie watchdog, "-S" for storage watchdog.
     */
    public static void main (String[] commands) {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
        ServerWatchdogOld movieWatchdog = null;
        ServerWatchdogOld retrievalWatchdog = null;
        ServerWatchdogOld storageWatchdog = null;

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
        try{
            movieWatchdog = new ServerWatchdogOld();
            movieWatchdog.setCheckMovieMaker (true);

            retrievalWatchdog = new ServerWatchdogOld();
            retrievalWatchdog.setCheckRetrieval (true);

            storageWatchdog = new ServerWatchdogOld ();
            storageWatchdog.setCheckStorage (true);

        }catch(Exception ex){
            WeatherLogger.log(Level.SEVERE,
                  "Error starting one of the watchdog systems.",ex);
            //e.printStackTrace();
            Debug.println("Error starting one of the watchdog systems");
        }

        // Parse command parameters.
        if(commands.length > 0){
            for(String command : commands){
                if(command.equals(COMMAND_R)){
                    interval = 10;
                    timeUnit = TimeUnit.MINUTES;
                    minutesToWait = 10 - (cal.get(Calendar.MINUTE)%10);
                    scheduler.scheduleAtFixedRate(retrievalWatchdog, minutesToWait, interval, timeUnit);
                    log.fine ("Resource retrieval watchdog set to run every " + 
                            interval + " " + timeUnit + ". Waiting " +
                            minutesToWait + " " + timeUnit + ".");
                     Debug.println("Resource retrieval watchdog set to run every " 
                             + interval + " " + timeUnit + ". Waiting " + 
                             minutesToWait + " " + timeUnit + ".");
                }
                else if(command.equals(COMMAND_M)){
                    int minutes = cal.get(Calendar.MINUTE);
                    interval = 60;
                    timeUnit = TimeUnit.MINUTES;
                    // Schedule to run 5 minutes past the hour.
                    minutesToWait = (minutes <= 8) ? 8-minutes : 68-minutes;
                    scheduler.scheduleAtFixedRate(movieWatchdog, minutesToWait, interval, timeUnit);
                    log.fine ("Movie maker watchdog set to run every " +
                            interval + " " + timeUnit + ". Waiting " +
                            minutesToWait + " " + timeUnit + ".");
                     Debug.println("Movie maker watchdog set to run every "
                             + interval + " " + timeUnit + ". Waiting " +
                             minutesToWait + " " + timeUnit + ".");
                }
                else if (command.equals (COMMAND_S)) {
                    interval = 5;
                    timeUnit = TimeUnit.MINUTES;
                    minutesToWait = 5;
                    scheduler.scheduleAtFixedRate (storageWatchdog, minutesToWait, interval, timeUnit);
                    log.fine ("Storage watchdog set to run every " +
                            interval + " " + timeUnit + ". Waiting " +
                            minutesToWait + " " + timeUnit + ".");
                    Debug.println("Storage watchdog set to run every "
                             + interval + " " + timeUnit + ". Waiting " +
                             minutesToWait + " " + timeUnit + ".");
                }
                else {
                    Debug.println("Unrecognized argument: "+command);
                    log.severe ("Unrecognized argument: " + command);
                    WeatherLogger.log(Level.SEVERE, "Unrecognized argument: " + command);
                }
            }
        }
        else {
            Debug.println("Usage: -R (retrieval), -M (movie), -S (storage)");
            log.severe ("No arguments passed to main method.");
            WeatherLogger.log(Level.SEVERE, "No arguments passed to main method.");
        }
    }  
}
