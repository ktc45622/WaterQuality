package weather.clientside.utilities;

import java.io.File;
import java.io.FileFilter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.AVIInstance;
import weather.common.data.resource.MP4Instance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceKey;
import weather.common.data.resource.ResourceRange;
import weather.common.data.resource.WeatherResourceType;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.*;

/**
 * Manages all interaction with the application resource trees, which hold data
 * loaded downloaded from the storage server. This interaction includes
 * locating, downloading, and retrieving resources that are needed throughout
 * the application.
 *
 * For this class to function properly, several attributes must be set before
 * the resource management begins. The current resources for each resource type
 * must be set, and the StorageControl object must be set. Once all of these
 * attributes are correctly set, the public getCurrentWeather<>Instances methods
 * can be called, which will retrieve the resource instances for the resource
 * type specified spanning the resource range currently set in the
 * ResourceTimeManager.
 *
 * @author Joe Sharp (2009)
 * @author Dustin Jones (2010)
 *
 * @version Spring 2010
 */
public class ResourceTreeManager {
    //Flags to track what is being downloaded.
    private static boolean downloadingAVI;
    private static boolean downloadingMP4; 
    
    private static final VideoFilter aviVideoFilter;
    private static final VideoFilter mp4VideoFilter;
    private static StorageControlSystem storageControl;
    
    /**
     * Contains downloaded movies of type AVI.  The key for these resources is 
     * the hour the movie begins. Each movie should be for an hour in time, 
     * therefore the end time is assumed to be a millisecond less than one hour 
     * later than the start time.
     */
    private static final TreeMap<ResourceKey, ResourceInstance> aviFileTree;
    
    /**
     * Contains downloaded movies of type MP4.  The key for these resources is 
     * the hour the movie begins. Each movie should be for an hour in time, 
     * therefore the end time is assumed to be a millisecond less than one hour 
     * later than the start time.
     */
    private static final TreeMap<ResourceKey, ResourceInstance> mp4FileTree;
    
    /**
     * Contains downloaded data for the weather station. The key for these
     * resources is the day the resource has collected data for. Each instance
     * should be for an day in time, therefore the end time is assumed to be a
     * millisecond less than one day later than the start time.
     */
    private static final TreeMap<ResourceKey, ResourceInstance> weatherStationTree;
    
    private static MP4Instance noDataMovieMP4Instance = null;
    private static AVIInstance noDataMovieAVIInstance = null;

    static {
        //Video Filtera
        Debug.println(":In Resource Tree Manager initialization block.");
        aviVideoFilter = new VideoFilter(true);
        mp4VideoFilter = new VideoFilter(false);
        
        //TreeMaps
        aviFileTree = new TreeMap<>();     
        mp4FileTree = new TreeMap<>();
        weatherStationTree = new TreeMap<>();
    }
   
    /**
     * Completes creation of a "No Data" movie instance.
     * @param instance the instance.
     */
    private static void loadNoDataInstance(AVIInstance instance){
        boolean isAVI = !(instance instanceof MP4Instance);
        instance.setStorageFileName("NoData");
        instance.setResourceType(isAVI ? WeatherResourceType.AVIVideo : WeatherResourceType.MP4Video);
        //this gives the avi nodata a set starttime, since the resource key is 
        //based on the instance_avi's resource number and start time
        instance.setStartTime(0);
        java.sql.Date zeroDate = new java.sql.Date(0);
        instance.setTime(zeroDate);
        instance.setResourceRange(new ResourceRange(zeroDate, zeroDate));
        instance.setResourceNumber(-1);
        instance.setStorageFileName("NoData");

        File noDataMovieFileMP4 = new File("NoData.mp4");
        File noDataMovieFileAVI = new File("NoData.avi");
        
        try {
            //load the actual video files into the resource instances
            instance.readFile(isAVI ? noDataMovieFileAVI : noDataMovieFileMP4);
        } catch (WeatherException we) {
            //log and terminate
            WeatherLogger.log(Level.SEVERE,
                    "Resource Tree manager can't load our default no-data-movie", we);
            new WeatherException(4004, true, we, "Error loading no-data-movie.").show();
        }
    }
    
    /**
     * This method is used to get the resource number in a file name, 
     * @param fileName the filename containing the resource number 
     * between its first and second commas.
     * @return the resource number.
     */
    private static int getResourceNumber(String fileName) {
        //get the index of the 1st comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 1);
        //get the index of the 2nd comma in the given file name
        int indexOfEndComma = nthIndexOfComma(fileName, ',', 2);

        //use substring to get the string between the 1st comma and 2nd comma
        String stringOfValue = fileName.substring(indexOfHeadComma + 1,
                indexOfEndComma).trim();

        //parse return valse
        return Integer.parseInt(stringOfValue);
    }

    /**
     * This method is used to get the end time in a file name,
     * @param fileName the filename containing the end time between its
     * fourth comma and its file extension.
     * @return the millisecond of the end time.
     */
    private static long getEndTime(String fileName) {
        //get the index of the 4th comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 4);
        //get the index of the start of the file extension
        int indexOfEntension = fileName.lastIndexOf('.');

        //use substring to get the string between the 1st comma and 2nd comma
        String stringOfValue = fileName.substring(indexOfHeadComma + 1,
                indexOfEntension).trim();

        //parse return valse
        return Long.parseLong(stringOfValue);
    }
    
    /**
     * This method is used to get the start time in a file name,
     * @param fileName the filename containing the start time between its
     * third and fourth commas.
     * @return the millisecond of the start time.
     */
    private static long getStartTime(String fileName) {
        //get the index of the 3rd comma in the given file name
        int indexOfHeadComma = nthIndexOfComma(fileName, ',', 3);
        //get the index of the 4th comma in the given file name
        int indexOfEndComma = nthIndexOfComma(fileName, ',', 4);

        //use substring to get the string between the 3rd comma and 4th comma
        String stringOfValue = fileName.substring(indexOfHeadComma + 1,
                indexOfEndComma).trim();

        //parse return valse
        return Long.parseLong(stringOfValue);
    }
    
    /**
     * This method is used to get the nth index of the given character in one
     * string.
     * @param text is the string we need to look for.
     * @param needle is the character we need to find.
     * @param n nth given character.
     * @return the index of the nth character we need to find
     */
    private static int nthIndexOfComma(String text, char needle, int n) {
        //read the given string from the head to the end
        for (int i = 0; i < text.length(); i++) {
            //find the index of the nth character
            if (text.charAt(i) == needle) {
                n--;
                if (n == 0) {
                    return i;
                }
            }
        }
        //return -1 if the given character can not be find
        return -1;
    }
    
    /**
     * Finds a resource from its resource number.
     * @param number The resource number.
     * @return The resource or null if a resource is not found.
     */
    private static Resource getResourceByNumber(int number) {
        try {
            return MySQLImpl.getMySQLDMBSSystem().getResourceManager()
                    .getWeatherResourceByNumber(number);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Debug.println("Unable to get resource from number.");
            return null;
        }
    }
    
    /**
     * Determines whether the data in a file holding data for a whether station 
     * instance is current.
     * @param fileName The file holding the data.
     * @return True if the file is current, false otherwise.
     */
    private static boolean isResourceFileCurrent(String fileName) {
        Resource resource = getResourceByNumber(getResourceNumber(fileName));
        
        //Exclude if resource has been deleted.
        if(resource == null) {
            return false;
        }
        
        //Get frequency of resource given in file name.
        long frequency = resource.getFrequency();
        
        //Convert frequency to milliseconds.
        frequency *= 1000;
        
        //Get most recent millisecond of day in question.
        long mostResectMilli;
        
        long now = Calendar.getInstance().getTimeInMillis();
        //Files end at midnight local server time.
        long dayEnd = ResourceTimeManager
                .getEndOfDayFromMilliseconds(getStartTime(fileName),
                resource.getTimeZone().getTimeZone());
        
        mostResectMilli = dayEnd < now ? dayEnd : now;
        
        //Get lat millisecond of file data.
        long lastMilliOfFileData = getEndTime(fileName);
        
        //Return whether of not file is current.
        return lastMilliOfFileData + frequency > mostResectMilli;
    }
    
    /**
     * Determines if the <code>Resource</code> in a given file name still exists.
     * @param fileName The given file name.
     * @return True if the <code>Resource</code> still exists, false otherwise.
     */
    private static boolean isResourceOfFileCurrent(String fileName) {
        return getResourceByNumber(getResourceNumber(fileName)) != null;
    }

    /**
     * Retrieves resource instances of any type from the storage server within a
     * given date and time range.
     *
     * This method will call the appropriate retrieve method, depending on the
     * ResourceType of the given resource, which will retrieve the resource
     * instances for the given range from the appropriate tree, downloading them
     * from the storage server first, if appropriate.
     *
     * @param resource the resource for which to gather resource instances.
     * @param range the range over which to gather instances.
     * @return a Vector of ResourceInstance containing all of the resource
     * instances for the given type, over the specified range. If no resource
     * instances were found, an empty vector is returned.
     */
    public static Vector<ResourceInstance> getResourceInstancesForRange(Resource resource, ResourceRange range) {
        
        Vector<ResourceInstance> returnVector = new Vector<>();

        if (resource == null || range == null) {
            return returnVector;
        }
        Debug.println("Resource manager was asked to return data for resource " +resource.getName());
        WeatherResourceType resourceType = resource.getResourceType();
        Debug.println("Resource Type is " + resourceType);

        switch (resourceType) {
            case WeatherCamera:
                //fall through.
            case WeatherMapLoop:
                returnVector = retrieveVideoInstances(resource, range); 
                break;
            case WeatherStationValues:
                returnVector = retrieveWeatherStationInstances(resource, range);
                break;
        }

        return returnVector;
    }

    /**
     * Sets the Storage Control attribute of this class. This method must be
     * called in order for downloading to work.
     *
     * @param sControl the StorageControlSystem object.
     */
    public static void setStorageControlSystem(StorageControlSystem sControl) {
        if (!sControl.pingServer()) {
            WeatherLogger.log(Level.SEVERE, "Unable to connect to the server.");
            WeatherException exception = new WeatherException(6002, true);
            exception.show();
        }
        storageControl = sControl;
    }
    
    /**
     * Determines if a given <code>ResourceKey</code> is in a given 
     * <code>TreeMap</code>.
     * @param tree The data structure to be checked.
     * @param key The key to be found.
     * @param zone The <code>TimeZone</code> of the <code>Resource</code> in the
     * key (for debugging).
     * @return True if the key is found, False otherwise.
     */
    private static boolean isMovieKeyInTree(TreeMap<ResourceKey, 
            ResourceInstance> tree, ResourceKey key, TimeZone zone) {
        //Show tree in Debug.
        if(tree.equals(aviFileTree)) {
            Debug.println("Using AVI tree.");
        } else if(tree.equals(mp4FileTree)) {
            Debug.println("Using MP4 tree");
        } else {
            Debug.println("NOT using a correct tree.");
        }
        
        //Show key on Debug.
        Debug.print("In isMovieKeyInTree - Key: Resource ");
        Debug.print(key.getResourceNumber() + " Start Date: ");
        Debug.println(ResourceTimeManager.getFormattedTimeString(key
                .getStartDate(), zone));

        //Look for key and return false if not found.
        ResourceInstance valueAsResourceInstance =
                tree.get(key);
        if (valueAsResourceInstance == null) {
            Debug.println("Key not is tree.");
            return false;
        } else {
            Debug.println("Key is tree,");
            return true;
        }
    }
    
    /**
     * Determines if the <code>ResourceInstance</code> mapped to a given
     * <code>ResourceKey</code> is current in the weatherStationTree and deletes
     * it if not.  Returns a <code>boolean</code> indicating whether or not the
     * key is current of false if it was never in the tree.
     * @param key the <code>ResourceKey</code> to be searched.
     * @return a <code>boolean</code> indicating whether or not the key is 
     * current of false if it was never in the tree.
     * @param zone The <code>TimeZone</code> of the <code>Resource</code> in the
     * key (for debugging).
     */
    private static boolean isStationKeyInTreeAndCurrent(ResourceKey key,
            TimeZone zone) {
        //Show key on Debug
        Debug.print("In isStationKeyInTreeAndCurrent - Key: Resource ");
        Debug.print(key.getResourceNumber() + " Start Date: ");
        Debug.println(ResourceTimeManager.getFormattedTimeString(key
                .getStartDate(), zone));
        
        //Look for key and return false if not found.
        ResourceInstance valueAsResourceInstance = 
                weatherStationTree.get(key);
        if (valueAsResourceInstance == null) {
            Debug.println("Key not is tree.");
            return false;
        }
        
        //Grt frequency of resource given in the key.
        long frequency = getResourceByNumber(key.getResourceNumber())
                .getFrequency();

        //Convert frequency to milliseconds.
        frequency *= 1000;

        //Get most recent millisecond of day in question.
        long now = Calendar.getInstance().getTimeInMillis();
        //Files end at midnight server time.
        long dayEnd = ResourceTimeManager
                .getEndOfDayFromMilliseconds(valueAsResourceInstance
                .getStartTime(), zone);
        long mostResectMilli = dayEnd < now ? dayEnd : now;

        //Get lat millisecond of file data.
        long lastMilliOfFileData = valueAsResourceInstance.getEndTime();

        //Determine whether of not file is current.
        boolean isCurrent = lastMilliOfFileData + frequency > mostResectMilli;
        
        //Remove key if not current.  Alse show Debug if key is current.
        if (isCurrent) {
            Debug.println("Key is current.");
        } else {
            Debug.println("Key is NOT current.");
            weatherStationTree.remove(key);
        }
        
        //Return whether of not file is current.
        return isCurrent;
    }

    /**
     * Checks the WeatherStation tree for the resources for the given resource
     * and for the given resource range. If the tree contains the resource
     * instances, they are simply returned. If the tree does not contain the
     * resource instances, they are downloaded from the storage server, stored
     * in the Weather Station tree and then returned.
     *
     * @param range the resource range for which to download resource instances.
     * @param resource the resource for which to download resource instances.
     * @return a vector containing all of the WeatherStation Instances for the
     * given Resource and resource range.
     */
    private static Vector<ResourceInstance> retrieveWeatherStationInstances(
            Resource resource, ResourceRange range) {
        TimeZone resourceZone = resource.getTimeZone().getTimeZone();
        
        Debug.println("retrieveWeatherStationInstances Start Time: "
                + ResourceTimeManager.getFormattedTimeString(
                range.getStartTime(), resourceZone)
                + " End Time: "
                + ResourceTimeManager.getFormattedTimeString(
                range.getStopTime(), resourceZone));

        Vector<ResourceInstance> returnVector = new Vector<>();
        ResourceKey currentKey;

        //Determine end pointd of actual download range (stations are downloaded by day).
        long resourceStartTime = ResourceTimeManager
                .getStartOfDayFromMilliseconds(range.getStartTime().getTime(),
                resourceZone);
        long resourceEndTime = ResourceTimeManager
                .getStartOfDayFromMilliseconds(range.getStopTime().getTime(),
                resourceZone);
        resourceEndTime += ResourceTimeManager.MILLISECONDS_PER_DAY - 1;
        
        //Remove future days from range.
        long currentTime = System.currentTimeMillis();
        while (resourceEndTime - currentTime > ResourceTimeManager
                .MILLISECONDS_PER_DAY) {
            resourceEndTime -= ResourceTimeManager.MILLISECONDS_PER_DAY; 
        }
        
        int resourceID = resource.getResourceNumber();
        
        //Use ArrayList of booleans to keep track of which days are current.
        ArrayList<Boolean> areDaysPressent = new ArrayList<>();
        
        //Loop through days in range to check fot current data
        long timeOfCurrentDay = resourceStartTime;
        while (timeOfCurrentDay < resourceEndTime) {
            currentKey = new ResourceKey(resourceID, timeOfCurrentDay);
            areDaysPressent.add(isStationKeyInTreeAndCurrent(currentKey, 
                    resourceZone)); //Show output in resouse time.
            timeOfCurrentDay += ResourceTimeManager.MILLISECONDS_PER_DAY;
        }
        
        //Loop through ArrayList to either add instances we have or
        //downlosd and add instances we don't.
        int currentIndex = 0;
        while (currentIndex < areDaysPressent.size()) {
            if(areDaysPressent.get(currentIndex)) {
                long timeOfDayToAdd = resourceStartTime + ResourceTimeManager
                        .MILLISECONDS_PER_DAY * currentIndex;
                Debug.println("Adding station already is tree for "
                        + ResourceTimeManager
                        .getFormattedTimeString(timeOfDayToAdd, resourceZone));
                currentKey = new ResourceKey(resourceID, timeOfDayToAdd);
                returnVector.add(weatherStationTree.get(currentKey));
                currentIndex++;
            } else {
                Vector<ResourceKey> downloadedKeys = new Vector<>();
                long startOfDownloadRange = resourceStartTime + ResourceTimeManager
                        .MILLISECONDS_PER_DAY * currentIndex;
                //Range end will increase by a day with each iteration of the 
                //below loop.
                long endOfDownloadRange = startOfDownloadRange - 1;
                
                //Loop to find all "false" days in a row.
                while (currentIndex < areDaysPressent.size()
                        && !areDaysPressent.get(currentIndex)) {
                    //This will happen at least once.
                    endOfDownloadRange += ResourceTimeManager.MILLISECONDS_PER_DAY;
                    Debug.println("New range end time: " + ResourceTimeManager
                            .getFormattedTimeString(
                            new Date(endOfDownloadRange), resourceZone));
                    long timeForKey = resourceStartTime + ResourceTimeManager
                        .MILLISECONDS_PER_DAY * currentIndex;
                    currentKey = new ResourceKey(resourceID, timeForKey);
                    Debug.println("Adding key to download: " +
                            ResourceTimeManager.getFormattedTimeString(
                            new Date(timeForKey), resourceZone));
                    downloadedKeys.add(new ResourceKey(currentKey));//put in current key
                    currentIndex++;
                }
                
                //Loop is done, request all consecutive "false" days.
                ResourceRange requestRange = new ResourceRange(new Date(startOfDownloadRange),
                        new Date(endOfDownloadRange));
                Debug.println("Request range: " + ResourceTimeManager.
                        getFormattedTimeString(new Date(startOfDownloadRange),
                        resourceZone) + " -- " + ResourceTimeManager
                        .getFormattedTimeString(new Date(endOfDownloadRange),
                        resourceZone));
                ResourceInstancesRequested request = new ResourceInstancesRequested(
                        requestRange, 1, false,
                        resource.getFormat(), resource);
                try {
                    downloadWeatherStationInstance(request, startOfDownloadRange);
                } catch (WeatherException ex) {
                    WeatherLogger.log(Level.SEVERE, "Unable to retrieve "
                            + "weather station instances from server.", ex);
                    ex.show("Unable to retrieve weather site instances from server.");
                }
                
                //Get downloaded keys from tree.
                for (int i = 0; i < downloadedKeys.size(); i++) {
                    if (weatherStationTree.containsKey(downloadedKeys.get(i))) {
                        Debug.println("Adding station just added to tree for "
                        + ResourceTimeManager.getFormattedTimeString(downloadedKeys
                                .get(i).getStartDate(), resourceZone));
                        returnVector.add(weatherStationTree.get(downloadedKeys.get(i)));
                    }
                }
                downloadedKeys.clear(); // Empty for next time in inner loop
            } //End of if.
        } //End of while.
        
        return returnVector;
    }

    /**
     * Retrieves all data for a given movie-making resource over a given time 
     * range.
     *
     * @param range the resource range for which to download resource instances.
     * @param resource the resource for which to download resource instances.
     * @return a vector containing all of the movie instances for the
     * given Resource and ResourceRange.
     */
    private static Vector<ResourceInstance> retrieveVideoInstances(Resource resource,
            ResourceRange range) {
        //vectors to hold data
        Vector<ResourceInstance> aviData = null;
        Vector<ResourceInstance> mp4Data = null;
        
        //get data
        if(downloadingAVI) {
            aviData = retrieveMovieInstances(aviFileTree, resource, range, true);
        }
        if(downloadingMP4) {
            mp4Data = retrieveMovieInstances(mp4FileTree, resource, range, false);
        }
        
        //return data
        Vector<ResourceInstance> allData = new Vector<>();
        if(aviData != null) {
            allData.addAll(aviData);
        }
        if(mp4Data != null) {
            allData.addAll(mp4Data);
        }
        return allData;
    }

    /**
     * Checks the tree given in as a parameter for the resources given as a
     * parameter and for the given resource range. If the tree contains the
     * resource instances, they are simply returned. If the tree does not
     * contain the resource instances, they are downloaded from the storage
     * server, stored in the tree and then returned. This class is generic for
     * both weatherCamera and weatherSite instances.
     *
     * @param tree the tree used to store the instances being retrieved.
     * @param resource the resource for which to download resource instances.
     * @param range the resource range for which to download resource instances.
     * @param usingAVI True if retrieving avi files, false for mp4.
     * @return a vector containing all of the Resource Instances for the given
     * Resource and resource range.
     */
    private static Vector<ResourceInstance> retrieveMovieInstances(TreeMap<ResourceKey, 
            ResourceInstance> tree, Resource resource, ResourceRange range,
            boolean usingAVI) {
        TimeZone zone = resource.getTimeZone().getTimeZone();
        
        Debug.println("retrieveMovieInstances Start Time: "
                + ResourceTimeManager.getFormattedTimeString(
                range.getStartTime(), zone)
                + " End Time: "
                + ResourceTimeManager.getFormattedTimeString(
                range.getStopTime(), zone));

        Vector<ResourceInstance> returnVector = new Vector<>();
        ResourceKey currentKey;

        //Store range end points.
        long resourceStartTime = range.getStartTime().getTime();
        long resourceEndTime = range.getStopTime().getTime();

        int resourceID = resource.getResourceNumber();

        //Use ArrayList of booleans to keep track of which days are current.
        ArrayList<Boolean> areDaysPressent = new ArrayList<>();

        //Loop through days in range to check fot current data
        long timeOfCurrentDay = resourceStartTime;
        while (timeOfCurrentDay < resourceEndTime) {
            currentKey = new ResourceKey(resourceID, timeOfCurrentDay);
            areDaysPressent.add(isMovieKeyInTree(tree, currentKey, zone));
            timeOfCurrentDay += ResourceTimeManager.MILLISECONDS_PER_HOUR;
        }

        //Loop through ArrayList to either add instances we have or
        //downlosd and add instances we don't.
        int currentIndex = 0;
        while (currentIndex < areDaysPressent.size()) {
            if (areDaysPressent.get(currentIndex)) {
                long timeOfDayToAdd = resourceStartTime + ResourceTimeManager.MILLISECONDS_PER_HOUR * currentIndex;
                Debug.println("Adding movie already is tree for "
                        + ResourceTimeManager
                        .getFormattedTimeString(timeOfDayToAdd, zone));
                currentKey = new ResourceKey(resourceID, timeOfDayToAdd);
                returnVector.add(tree.get(currentKey));
                currentIndex++;
            } else {
                Vector<ResourceKey> downloadedKeys = new Vector<>();
                long startOfDownloadRange = resourceStartTime + ResourceTimeManager.MILLISECONDS_PER_HOUR * currentIndex;
                //Range end will increase by a hour with each iteration of the 
                //below loop.
                long endOfDownloadRange = startOfDownloadRange - 1;

                //Loop to find all "false" days in a row.
                while (currentIndex < areDaysPressent.size()
                        && !areDaysPressent.get(currentIndex)) {
                    //This will happen at least once.
                    endOfDownloadRange += ResourceTimeManager.MILLISECONDS_PER_HOUR;
                    Debug.println("New range end time: " + ResourceTimeManager
                            .getFormattedTimeString(new Date(
                            endOfDownloadRange), zone));
                    long timeForKey = resourceStartTime + ResourceTimeManager.MILLISECONDS_PER_HOUR * currentIndex;
                    currentKey = new ResourceKey(resourceID, timeForKey);
                    Debug.println("Adding key to download: " + ResourceTimeManager
                            .getFormattedTimeString(new Date(timeForKey), zone));
                    downloadedKeys.add(new ResourceKey(currentKey));//put in current key
                    currentIndex++;
                }

                //Loop is done, request all consecutive "false" days.
                ResourceRange requestRange = new ResourceRange(new Date(startOfDownloadRange),
                        new Date(endOfDownloadRange));
                Debug.println("Request range: " + ResourceTimeManager.
                        getFormattedTimeString(new Date(startOfDownloadRange),
                        zone) + " -- " + ResourceTimeManager
                        .getFormattedTimeString(new Date(endOfDownloadRange),
                        zone));
                
                //Determine format to reguest.
                ResourceFileFormatType formatType = usingAVI
                        ? ResourceFileFormatType.avi : ResourceFileFormatType.mp4;
                
                ResourceInstancesRequested request = new ResourceInstancesRequested(
                        requestRange, 1, true, formatType, resource);
                try {
                    downloadMovieInstance(tree, request, startOfDownloadRange,
                            usingAVI);
                } catch (WeatherException ex) {
                    WeatherLogger.log(Level.SEVERE, "Unable to retrieve "
                            + "weather movie instances from server.", ex);
                    ex.show("Unable to retrieve weather movie instances from server.");
                }

                //Get downloaded keys from tree.
                for (int i = 0; i < downloadedKeys.size(); i++) {
                    if (tree.containsKey(downloadedKeys.get(i))) {
                        Debug.println("Adding movie just added to tree for "
                                + ResourceTimeManager.getFormattedTimeString(
                                downloadedKeys.get(i).getStartDate(), zone));
                        returnVector.add(tree.get(downloadedKeys.get(i)));
                    }
                }
                downloadedKeys.clear(); // Empty for next time in inner loop
            } //End of if.
        } //End of while.

        Debug.println("returnVector.size() " + returnVector.size());

        return returnVector;
    }

    /**
     * This method is used to download one or more weather station instances at
     * a time, and store them in the weather station tree. The request is sent
     * to the storage control system, which returns a vector of returned
     * instances. The instances are then stored in the weather station tree.
     *
     * @param req the request to be sent to the storage system
     * @param startDate the starting date of the first resource to be retrieved
     * from the server
     * @throws weather.common.utilities.WeatherException
     */
    private static void downloadWeatherStationInstance(
            ResourceInstancesRequested req, long startDate) throws WeatherException {
        StopWatch sw = new StopWatch();

        //Get Resource and TimeZone
        Resource resource = getResourceByNumber(req.getResourceID());

        //Throw exception if resource is null (should not happen).
        if (resource == null) {
            throw new WeatherException();
        }
        
        TimeZone zone = resource.getTimeZone().getTimeZone();
        
        Debug.println("Downloading resource for weather station instance");
        if (req != null) {
            Debug.println("Downloading resources for " + req.getResource().getResourceName()
                    + " : "
                    + ResourceTimeManager.getFormattedTimeString(req
                    .getResourceRange().getStartTime(), zone) + " to "
                    + ResourceTimeManager.getFormattedTimeString(
                    req.getResourceRange().getStopTime(), zone));
        } else {
            Debug.println("Downloading resources for -- request is null " + " : "
                    + ResourceTimeManager.getFormattedTimeString(req
                    .getResourceRange().getStartTime(), zone) + " to "
                    + ResourceTimeManager.getFormattedTimeString(
                    req.getResourceRange().getStopTime(), zone));
        }
        if (Debug.isEnabled()) {
            sw.start();
        }

        ResourceInstancesReturned returnedVector = storageControl.getResourceInstances(req);

        if (returnedVector != null && returnedVector.getNumberOfValuesReturned() > 0) {
            if (Debug.isEnabled()) {
                sw.stop();
            
                  Debug.println("Storage server returned " + returnedVector.getNumberOfValuesReturned() + " instances in "
                    + sw.getElapsedTime() + " milliseconds");
            }
            ResourceKey key = new ResourceKey(req.getResourceID(), startDate);
            //Put each individual instance in the tree
            for (int i = 0; i < returnedVector.getNumberOfValuesReturned(); i++) {
                //PROBLEM HERE -- getting back wrong type from storage system 
                WeatherUndergroundInstance instance =  (WeatherUndergroundInstance) returnedVector.getResourceInstances().get(i);
                key.setStartDate(startDate + i * ResourceTimeManager.MILLISECONDS_PER_DAY);                
                weatherStationTree.put(new ResourceKey(key), instance);
                
                //Save file to local disk - first get file extension.
                String extension;
                switch (resource.getFormat()) { 
                    case comma_separated_values:
                        extension = ".csv";
                        break;
                    case space_separated_values:
                        extension = ".ssv";
                        break;
                    case text:
                    default:
                        extension = ".txt";
                        break;
                }
                File writtenFile = instance.writeToLocalDisk(resource.
                        getTimeZone().getTimeZone(), extension);
                instance.setFieldsFromFilename(writtenFile);
            }
        } else {
            Debug.println("No weather station data received");
        }

        sw = null;
        /*
         * Call to garbage collector...
        Debug.println("Running garbage collection...");
        Runtime.getRuntime().gc();
        Debug.println("Finished garbage collection...");
        */
    }

    /**
     * This method uses the given request instance to download the corresponding
     * data from the storage server. The request specifies the resource number,
     * the start date, and the end for the resource instance that we want from
     * the server. This method will be used to get all resources between the
     * specified dates. startTime is needed as a parameter to create the key to
     * store the resources in the correct table.
     *
     * @param tree our local storage system for this resource type
     * @param req the request to be sent to the server
     * @param startTime the start time of the first instance
     * @param usingAVI True if downloading avi files, false for mp4.
     * @throws WeatherException
     */
    private static void downloadMovieInstance(TreeMap<ResourceKey, ResourceInstance> tree,
            ResourceInstancesRequested req, long startDate, boolean usingAVI) throws WeatherException {
        //Create an instance_avi of our stop watch class for timing events
        StopWatch sw = new StopWatch();
        
        //Get Resource and TimeZone
        Resource resource = getResourceByNumber(req.getResourceID());

        //Throw exception if resource is null (should not happen).
        if (resource == null) {
            throw new WeatherException();
        }

        TimeZone zone = resource.getTimeZone().getTimeZone();

        //Find exepted number of videos
        int expectedNumberOfValuesReturned = 0;
        long currentTime = req.getResourceRange().getStartTime().getTime();
        long endTime = req.getResourceRange().getStopTime().getTime();
        while(currentTime < endTime) {
            expectedNumberOfValuesReturned++;
            currentTime += ResourceTimeManager.MILLISECONDS_PER_HOUR;
        }
        Debug.println("expectedNumberOfValuesReturned: " + expectedNumberOfValuesReturned);
        
        if (Debug.isEnabled()) {
            sw.start(); //Start the stop watch
        }

        if (req != null) {
            Debug.println("Downloading resources for " + req.getResource().getResourceName()
                    + " : "
                    + ResourceTimeManager.getFormattedTimeString(
                    req.getResourceRange().getStartTime(), zone)
                    + " to " + ResourceTimeManager.getFormattedTimeString(
                    req.getResourceRange().getStopTime(), zone));
        } else {
            Debug.println("Downloading resources for -- request is null " + " : "
                    + ResourceTimeManager.getFormattedTimeString(
                    req.getResourceRange().getStartTime(), zone)
                    + " to " + ResourceTimeManager.getFormattedTimeString(
                    req.getResourceRange().getStopTime(), zone));
        }

        ResourceInstancesReturned returnedVector = storageControl.getResourceInstances(req);
        WeatherResourceType typeOfMovie = req.getResource().getResourceType();

        //If we get movies from the storage server, then add them to the tree.
        Debug.println("Storage control system returned vector that is not null "
                + ((returnedVector != null) ? "true" : "false"));
        if (returnedVector != null) {
            Debug.println("Storage control system returned vector that had "
                    + returnedVector.getNumberOfValuesReturned() + " in it");
        }
        //If we get movies from the storage server, then add them to the tree.
        if (returnedVector != null && returnedVector.getNumberOfValuesReturned() > 0) {
            if (Debug.isEnabled()) {
                sw.stop();
                Debug.println("Storage server returned " + returnedVector.getNumberOfValuesReturned() + " instances in "
                        + sw.getElapsedTime() + " milliseconds");
            }

            int actualNumberOfValuesReturned = returnedVector.getNumberOfValuesReturned();
            ResourceKey key = new ResourceKey(req.getResourceID(), startDate);

            //Put each individual instance_avi in the tree
            for (int i = 0; i < actualNumberOfValuesReturned; i++) {
                AVIInstance movieInstance;

                if (usingAVI) {
                    Debug.println("Returned Class: " +  returnedVector.getResourceInstances().get(i).getClass().getName());
                    movieInstance = (AVIInstance) returnedVector.getResourceInstances().get(i);
                    movieInstance.setResourceType(typeOfMovie);
                    Debug.println("Got an AVI here, going to write it to disk " + ((AVIInstance) movieInstance).getSerializedFileName());
                    Debug.println("Start Date is " + ResourceTimeManager
                            .getFormattedTimeString(startDate, zone));
                    movieInstance.writeToDisk();
                    if (movieInstance.getMovieFile().length() < 1) {
                        Debug.println("Movie length was zero, replaced with no data "
                                + movieInstance.getMovieFile().getName());
                        movieInstance = noDataMovieAVIInstance;
                    }

                } else {
                    Debug.println("Returned Class: " +  returnedVector.getResourceInstances().get(i).getClass().getName());
                    movieInstance = (MP4Instance) returnedVector.getResourceInstances().get(i);
                    movieInstance.setResourceType(typeOfMovie);
                    Debug.println("Got an MP4 here, going to write it to disk " + ((MP4Instance) movieInstance).getSerializedFileName());
                    Debug.println("Start Date is " + ResourceTimeManager
                            .getFormattedTimeString(startDate, zone));
                    movieInstance.writeToDisk();
                    if (movieInstance.getMovieFile().length() < 1) {
                        Debug.println("Movie length was zero, replaced with no data "
                                + movieInstance.getMovieFile().getName());
                        movieInstance = noDataMovieMP4Instance;
                    }
                }

                key.setStartDate(startDate + i * ResourceTimeManager.MILLISECONDS_PER_HOUR);

                /*
                 * If we get the number of resources returned that we expected
                 * then we shouldn't expect any missing movies and store the
                 * resources normally, otherwise, the gap must be accounted for
                 * through each iteration through the loop by checking to see if
                 * the expected and actual start times match, then if they
                 * don't, add hours to the expected start time until they do.
                 */

                if (Math.abs(movieInstance.getStartTime() - key.getStartDate())
                        < ResourceTimeManager.TOLERANCE) {
                    Debug.println("Key.StartDate is "
                            + ResourceTimeManager.getFormattedTimeString(key
                            .getStartDate(), zone) 
                            + " movieInstance.getStartTime() is " 
                            + ResourceTimeManager
                            .getFormattedTimeString(movieInstance
                            .getStartTime(), zone));

                    tree.put(new ResourceKey(key), movieInstance);
                    if (tree.containsKey(key)) {
                        Debug.println(" Key " + key.getResourceNumber() + " "
                                + ResourceTimeManager.getFormattedTimeString(key
                                .getStartDate(), zone) + " is in tree");
                    } else {
                        Debug.println(" Problem, Key jsut placed in tree is not there "
                                + key.getResourceNumber() + " "
                                + ResourceTimeManager.getFormattedTimeString(key
                                .getStartDate(), zone) + " ");
                    }


                } else {
                    long newStartTime = key.getStartDate();
                    while (movieInstance.getStartTime() > newStartTime) {
                        ResourceKey tkey = new ResourceKey(req.getResourceID(), newStartTime);
                        Debug.println("Putting nodata movie into the resource tree.");

                        if (usingAVI) {
                            tree.put(new ResourceKey(tkey), noDataMovieAVIInstance);
                        } else {
                            tree.put(new ResourceKey(tkey), noDataMovieMP4Instance);
                        }

                        newStartTime += ResourceTimeManager.MILLISECONDS_PER_HOUR;
                    }
                    key.setStartDate(newStartTime);
                    tree.put(new ResourceKey(key), movieInstance);
                }
            }
            //Need to put Nodata movies into tree for any values that were missing here
            for (int i = actualNumberOfValuesReturned; i < expectedNumberOfValuesReturned; i++) {
                key.setStartDate(startDate + i * ResourceTimeManager.MILLISECONDS_PER_HOUR);
                if (usingAVI) {
                    tree.put(new ResourceKey(key), noDataMovieAVIInstance);
                } else {
                    tree.put(new ResourceKey(key), noDataMovieMP4Instance);
                }
                Debug.println("Put nodata movie in tree. Key.Startdate is " + key.getStartDate());
            }

            returnedVector.getResourceInstances().clear();
        } //Otherwise, add the default movie to the tree in it's place.
        else {
            Debug.println("No movie instances returned, adding all default movies.");
            ResourceInstance instance;

            if (usingAVI){
                instance = noDataMovieAVIInstance;
            } else {
                instance = noDataMovieMP4Instance;
            }

            for (int i = 0; i < expectedNumberOfValuesReturned; i++) {
                ResourceKey key = new ResourceKey(req.getResourceID(),
                        startDate + i * ResourceTimeManager.MILLISECONDS_PER_HOUR);
                tree.put(key, instance);
            }
        }

        //Call to garbage collector...
        Runtime.getRuntime().gc();
    }
    
    

    /**
     * Loads movies that are stored on the hard disk.  This is based on the
     * current local working directory and this method should be called if that
     * is changed.
     */
    public static void initializeData() {
        // Clear TreeMaps
        aviFileTree.clear();
        mp4FileTree.clear();
        weatherStationTree.clear();

        //Read and store the movie instance_avi showing "no data."
        //We apparently do not pull down the nodata video anymore
        noDataMovieMP4Instance = new MP4Instance();
        noDataMovieAVIInstance = new AVIInstance();
        loadNoDataInstance(noDataMovieMP4Instance);
        loadNoDataInstance(noDataMovieAVIInstance);

        //Debug.println("About to initialize Resoruce Trees with data from local file system.");
        AVIInstance instance_avi;
        MP4Instance instance_mp4;
        WeatherUndergroundInstance instance_station;
        File directory;
        File[] files;
//        Resource resource; //--Only used with Debug statements.

        //Populate aviFileTree
        if (downloadingAVI) {
            //Get camera videos.  
            directory = new File(CommonLocalFileManager.getAVICameraDir());
            if (directory.exists()) {
                files = directory.listFiles(aviVideoFilter);
                //Debug.println("Number of files found is " + files.length);
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    instance_avi = new AVIInstance(file);
                if (isResourceOfFileCurrent(file.getName())) {
//                    resource = getResourceByNumber(getResourceNumber(file
//                            .getName()));
                    } else {
                        //Debug.println("File with old resource number: " 
                        //+ getResourceNumber(file.getName()));
                        //Try to delete file.
                        if (file.delete()) {
                            //Debug.println("File fron old resource deleted.");
                        } else {
                            //Debug.println("File fron old resource NOT deleted.");
                        }
                        continue;
                    }
                    instance_avi.setResourceType(WeatherResourceType.WeatherCamera);

                    // Parse the filename to get information that goes into
                    // ResourceInstance.
                    instance_avi.setFieldsFromFilename();

                    // Add it to the correct resource tree.            
                    ResourceKey key = new ResourceKey(instance_avi.getResourceNumber(),
                            instance_avi.getStartTime());
//                    Debug.println("AVI instance about to be added to tree,  Key is :"
//                        + instance_avi.getResourceNumber() + " " 
//                        + ResourceTimeManager.getFormattedTimeString(instance_avi.
//                        getStartTime(), resource.getTimeZone().getTimeZone()));
                    aviFileTree.put(key, instance_avi);
                    //Debug.println(" -- Tree size after put operation " + aviFileTree.size());
                }
            }

            //Get map videos.
            directory = new File(CommonLocalFileManager.getAVIMaploopDir());
            if (directory.exists()) {
                files = directory.listFiles(aviVideoFilter);
                //Debug.println("Number of files found is " + files.length);
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    instance_avi = new AVIInstance(file);
                    if (isResourceOfFileCurrent(file.getName())) {
//                    resource = getResourceByNumber(getResourceNumber(file
//                            .getName()));
                    } else {
                        //Debug.println("File with old resource number: " 
                        //+ getResourceNumber(file.getName()));
                        //Try to delete file.
                        if (file.delete()) {
                            //Debug.println("File fron old resource deleted.");
                        } else {
                            //Debug.println("File fron old resource NOT deleted.");
                        }
                        continue;
                    }
                    instance_avi.setResourceType(WeatherResourceType.WeatherMapLoop);

                    // Parse the filename to get information that goes into
                    // ResourceInstance.
                    instance_avi.setFieldsFromFilename();

                    // Add it to the correct resource tree.            
                    ResourceKey key = new ResourceKey(instance_avi.getResourceNumber(),
                            instance_avi.getStartTime());
//                    Debug.println("AVI instance added to tree,  Key is : "
//                        + instance_avi.getResourceNumber() + " " 
//                        + ResourceTimeManager.getFormattedTimeString(instance_avi
//                        .getStartTime(), resource.getTimeZone().getTimeZone()));
                    aviFileTree.put(key, instance_avi);
                }
            }
        }

        //Populate mp4FileTree
        if (downloadingMP4) {
            //Get camera videos.  
            directory = new File(CommonLocalFileManager.getAVICameraDir());
            if (directory.exists()) {
                files = directory.listFiles(mp4VideoFilter);
                //Debug.println("Number of files found is " + files.length);
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    instance_mp4 = new MP4Instance(file);
                    if (isResourceOfFileCurrent(file.getName())) {
//                    resource = getResourceByNumber(getResourceNumber(file
//                            .getName()));
                    } else {
                        //Debug.println("File with old resource number: " 
                        //+ getResourceNumber(file.getName()));
                        //Try to delete file.
                        if (file.delete()) {
                            //Debug.println("File fron old resource deleted.");
                        } else {
                            //Debug.println("File fron old resource NOT deleted.");
                        }
                        continue;
                    }
                    instance_mp4.setResourceType(WeatherResourceType.WeatherCamera);

                    // Parse the filename to get information that goes into
                    // ResourceInstance.
                    instance_mp4.setFieldsFromFilename();

                    // Add it to the correct resource tree.            
                    ResourceKey key = new ResourceKey(instance_mp4.getResourceNumber(),
                            instance_mp4.getStartTime());
//                    Debug.println("MP4 instance added to tree,  Key is : "
//                        + instance_mp4.getResourceNumber() + " " 
//                        + ResourceTimeManager.getFormattedTimeString(instance_mp4
//                        .getStartTime(), resource.getTimeZone().getTimeZone()));
                    mp4FileTree.put(key, instance_mp4);
                }
            }

            //Get map videos.
            directory = new File(CommonLocalFileManager.getAVIMaploopDir());
            if (directory.exists()) {
                files = directory.listFiles(mp4VideoFilter);
                //Debug.println("Number of files found is " + files.length);
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    instance_mp4 = new MP4Instance(file);
                    if (isResourceOfFileCurrent(file.getName())) {
//                    resource = getResourceByNumber(getResourceNumber(file
//                            .getName()));
                    } else {
                        //Debug.println("File with old resource number: " 
                        //+ getResourceNumber(file.getName()));
                        //Try to delete file.
                        if (file.delete()) {
                            //Debug.println("File fron old resource deleted.");
                        } else {
                            //Debug.println("File fron old resource NOT deleted.");
                        }
                        continue;
                    }
                    instance_mp4.setResourceType(WeatherResourceType.WeatherMapLoop);

                    // Parse the filename to get information that goes into
                    // ResourceInstance.
                    instance_mp4.setFieldsFromFilename();

                    // Add it to the correct resource tree.            
                    ResourceKey key = new ResourceKey(instance_mp4.getResourceNumber(),
                            instance_mp4.getStartTime());
//                    Debug.println("MP4 instance added to tree,  Key is : "
//                        + instance_mp4.getResourceNumber() + " " 
//                        + ResourceTimeManager.getFormattedTimeString(instance_mp4.
//                        getStartTime(), resource.getTimeZone().getTimeZone()));
                    mp4FileTree.put(key, instance_mp4);
                }
            }
        }

        //Populate weatherStationTree
        directory = new File(CommonLocalFileManager.getWeatherStationsDirectory());
        if (directory.exists()) {
            files = directory.listFiles();
            //Exclude directories.
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                }

                //Get filename and exclude out-of-date fils.
                String fileName = file.getName();
                if (!isResourceFileCurrent(fileName)) {
                    //Debug.println("Excluded file: " + fileName);
                    //Try to delete file.
                    if (file.delete()) {
                        //Debug.println("Old file deleted.");
                        //repeat for "clean" file
                        String originialPath = file.getAbsolutePath();

                        //get parts of "clean" file path
                        String subDirectory = "CleanCSVFiles";
                        int splitIndex = originialPath.lastIndexOf(File.separator);
                        String pathStart = originialPath.substring(0, splitIndex);
                        String pathEnd = originialPath.substring(splitIndex);
                        String fullPath = pathStart + File.separator + subDirectory
                                + pathEnd;
                        //Debug.println("Clean path: " + fullPath);

                        //delete "clean" file
                        if (new File(fullPath).delete()) {
                            //Debug.println("Old clean file deleted.");
                        } else {
                            //Debug.println("Old clean file NOT deleted.");
                        }
                    } else {
                        //Debug.println("Old file NOT deleted.");
                    }
                    continue;
                }

                //File can now be added to tree.
                //Debug.println("Included file: " + fileName);

                //Find type of station instance.
                Resource fileResource =
                        getResourceByNumber(getResourceNumber(fileName));
                if (WeatherUndergroundInstance.isWeatherUndergroundInstance(fileResource)) {
                    instance_station = new WeatherUndergroundInstance(fileResource);
//                resource = getResourceByNumber(instance_station
//                        .getResourceNumber());
                } else {
                    continue;
                }
                try {
                    instance_station.readFile(file);
                    //Debug.println("WE DID READ THE FILE CORRECTY "+file.getAbsolutePath());
                } catch (WeatherException ex) {
                    //Debug.println("Error reading Weather Station Instance from "
                    //+ file.getAbsolutePath());
                    file.delete();
                    continue;
                }
                instance_station.setFieldsFromFilename(file);

                // Add it to the correct resource tree.            
                ResourceKey key = new ResourceKey(instance_station.getResourceNumber(),
                        instance_station.getStartTime());
                //Debug.println("Weather station instance added to tree,  Key is : "
//                    + instance_station.getResourceNumber() + " "
//                    + ResourceTimeManager.getFormattedTimeString(
//                    instance_station.getStartTime(), resource.getTimeZone()
//                    .getTimeZone()));
                weatherStationTree.put(key, instance_station);
            }
        }
    }

    /**
     * Sets the ResourceTreeManger to select MP4 files.
     */
    public static  void setToMP4() {
        downloadingAVI = false;
        downloadingMP4 = true;
    }

    /**
     * Sets the ResourceTreeManger to select AVI files.
     */
    public static  void setToAVI() {
        downloadingAVI = true;
        downloadingMP4 = false;
    }
    
     /**
     * Sets the ResourceTreeManger to select AVI and MP4 files.
     */
    public static  void setToAVIAndMP4() {
        downloadingAVI = true;
        downloadingMP4 = true;
    }
    
    /**
     * filter that only accepts files with the correct extension.
     */
    private static class VideoFilter implements FileFilter {
        private boolean usingAVI;
        
        /**
         * Constructor
         * @param usingAVI True if filtering avi files, false for mp4.
         */
        public VideoFilter(boolean usingAVI) {
            this.usingAVI = usingAVI;
        }

        @Override
        public boolean accept(File pathname) {
            if (usingAVI) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".avi");
            } else {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".mp4");
            }
        }
    }
}
