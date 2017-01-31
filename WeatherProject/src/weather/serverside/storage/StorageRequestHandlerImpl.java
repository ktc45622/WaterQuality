package weather.serverside.storage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.*;
import weather.common.data.weatherstation.WeatherStationInstance;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.*;
import weather.serverside.FFMPEG.VideoLengthCalculator;
import weather.serverside.FFMPEG.VideoTrimmer;

/**
 * A thread that handles storage requests such as storing, providing, and
 * deleting data that are made from a remote computer.
 *
 * @author Bloomsburg University Software Engineering (2008)
 * @author Thomas Crouse (2012)
 * @author Zach Rothweiler (2012)
 * @version Spring 2012
 */
public class StorageRequestHandlerImpl implements StorageRequestHandler {

    private boolean available = true;
    private Socket socket = null;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private StorageCommand command;
    private StorageManagement storageManagement;
    // Storage log.
    private static final WeatherTracer log = WeatherTracer.getStorageLog();

    /**
     * Creates a new instance of StorageRequestHandlerImpl
     *
     * @param storageManagement the storage management to use
     */
    public StorageRequestHandlerImpl(StorageManagement storageManagement) {
        this.setStorageManagement(storageManagement);
    }

    /**
     * Close the socket and all streams, and make the handler available.
     */
    private void clear() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                WeatherLogger.log(Level.WARNING, "Exception trying to close socket", ex);
            }
        }
        socket = null;
        ois = null;
        oos = null;
        command = null;
        setAvailable(true);
    }

    @Override
    public void run() {
        log.info("Running StorageRequestHandlerImpl.");

        //If no socket, then return
        if (socket == null) {
            log.severe("Socket is null in StorageRequestHandlerImpl, returning.");
            WeatherLogger.log(Level.SEVERE, "Socket is null in StorageRequestHandlerImpl, returning.");
            clear();
            return;
        }

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            Debug.println("output stream created");
            ois = new ObjectInputStream(socket.getInputStream());
            Debug.println("input stream created");
        } catch (EOFException ex) {
            // I/O error occurs while reading stream header
            //@see http://blogs.sun.com/oleksiys/entry/strange_software_caused_connection_abort
            //@see http://forums.sun.com/thread.jspa?threadID=748677
            log.severe("Error establishing streams with client, EOF.", ex);
            WeatherLogger.log(Level.SEVERE, "Error establishing streams with client, EOF", ex);

            clear();
            return;
        } catch (IOException ex) {
            // I/O error occurs while reading stream header
            //@see http://forums.sun.com/thread.jspa?threadID=430179&start=0
            log.severe("Error establishing streams with client.", ex);
            WeatherLogger.log(Level.SEVERE, "Error establishing streams with client", ex);

            clear();
            return;
        }
        Debug.println("no errors caught making streams");
        try {
            // Read the storage command from the input stream.
            command = (StorageCommand) ois.readObject();
        } catch (ClassNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, "Error- Could not find a class definition "
                    + "for the object received from client", ex);
            clear();
            return;
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "Error: Could not read command from client", ex);
            Debug.println("IOException:"+ex);
            ex.printStackTrace();
            clear();
            return;
        }
        Debug.println("command read correctly:" +command.getCommandType());

        //Process Command
        if (command == null) {
            clear();
            return;
        }
        //storing a normal file
        if (command.getCommandType() == StorageCommandType.STORE
                || //storing day-long movie 
                command.getCommandType() == StorageCommandType.STORE_DAY_LONG_MP4
                || //storing a default day file
                command.getCommandType() == StorageCommandType.STORE_DEFAULT_DAY
                || //storing a default night file
                command.getCommandType() == StorageCommandType.STORE_DEFAULT_NIGHT
                || //storing a no data file
                command.getCommandType() == StorageCommandType.STORE_NO_DATA_MP4
                || command.getCommandType() == StorageCommandType.STORE_NO_DATA_AVI) {
            //Get success state of store.
            boolean result = store(command);
            
            //Send result to client.
            Boolean resultObject = new Boolean(result);
            try {
                oos.writeObject(resultObject);
                oos.flush();
                oos.close();
            } catch (IOException ex) {
                WeatherLogger.log(Level.WARNING, "Error: could not write value to client");
            }
        } else { //This is some form of PROVIDE
            //Provide Data Command
            try {
                provide(command);
            } catch (WeatherException e) {
                WeatherLogger.log(Level.SEVERE, "Error- Could not store command.", e);
                clear();
                return;
            }
        }
        
        clear();
    }

    /**
     * Store a resource instance in the file system, thus executing "store" 
     * commands.  IT IS PUBLIC FOR TESTING ONLY AND SHOULD NOT BE CALLED.
     *
     * @param command Type of storage command to execute
     * @return True if the save was successful; False otherwise;
     */
    public boolean store(StorageCommand command) {
        if (storageManagement == null) {
            WeatherLogger.log(Level.SEVERE, " Storage management variable is NULL");
            return false;
        }
        
        try {
            //Set local variables as required.
            ResourceInstance resourceInstance1 = command.getFirstResourceInstance();
            if (resourceInstance1 == null) {
                log.finest("Missing storage data (first resource instance).");
                Debug.println("Missing storage data (first resource instance).");
                throw new WeatherException(6007);
            }
            long startTime = 0;
            if (command.getCommandType() == StorageCommandType.STORE
                    || command.getCommandType() == StorageCommandType.STORE_DAY_LONG_MP4) {
                startTime = resourceInstance1.getStartTime();
            }
            int resourceID = resourceInstance1.getResourceNumber();
            ResourceInstance resourceInstance2 = null;
            if (command.getCommandType() == StorageCommandType.STORE_DAY_LONG_MP4) {
                resourceInstance2 = command.getSecondResourceInstance();
            }

            //Perform stores according to command type.
            if (command.getCommandType() == StorageCommandType.STORE_DAY_LONG_MP4) {
                File file = storageManagement.getNewStorageFile(resourceID,
                    new java.sql.Date(startTime), ResourceFileFormatType.mp4, 
                    true, false);
                file.getParentFile().mkdirs();
                log.finest("Writing standard-quality day-long movie " + file.getName() + ".");
                Debug.println("Writing standard-quality day-long movie " + file.getName() + ".");
                resourceInstance1.writeFile(file);
                //Add low-quality copy if provided.
                if (resourceInstance2 != null) {
                    File file2 = storageManagement.getNewStorageFile(resourceID,
                            new java.sql.Date(startTime), ResourceFileFormatType.mp4,
                            true, true);
                    log.finest("Writing low-quality day-long movie " + file2.getName() + ".");
                    Debug.println("Writing low-quality day-long movie " + file2.getName() + ".");
                    resourceInstance2.writeFile(file2);
                }
            } else if (command.getCommandType() == StorageCommandType.STORE) {
                ResourceFileFormatType movieType = null;

                if (resourceInstance1 instanceof AVIInstance) {
                    movieType = ResourceFileFormatType.avi;
                }
                if (resourceInstance1 instanceof MP4Instance) {
                    movieType = ResourceFileFormatType.mp4;
                }
                File file = storageManagement.getNewStorageFile(resourceID,
                    new java.sql.Date(startTime), movieType, false, false);
                file.getParentFile().mkdirs();
                log.finest("Writing data from command type STORE: " + file.getName() + ".");
                Debug.println("Writing data from command type STORE: " + file.getName() + ".");
                resourceInstance1.writeFile(file);
            } else if (command.getCommandType() == StorageCommandType.STORE_NO_DATA_MP4) {
                storageManagement.createAndStoreNoDataMP4File(resourceInstance1);
            } else if (command.getCommandType() == StorageCommandType.STORE_NO_DATA_AVI) {
                storageManagement.createAndStoreNoDataAVIFile(resourceInstance1);
            } else { //change default day/night images
                storageManagement.createAndStoreDefaultMovieFile(resourceID, 
                        command.getCommandType(), command.getFirstResourceInstance(),
                        ResourceFileFormatType.mp4);
                storageManagement.createAndStoreDefaultMovieFile(resourceID, 
                        command.getCommandType(), command.getFirstResourceInstance(),
                        ResourceFileFormatType.avi);
            }
            return true;
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Write the requested data to the socket, thus executing "provide" 
     * commands.  IT IS PUBLIC FOR TESTING ONLY AND SHOULD NOT BE CALLED.
     *
     * @param command Type of storage command to execute
     * @throws WeatherException
     */
    public void provide(StorageCommand command) throws WeatherException {
        //Provide this file or files
        if (command.getCommandType() == StorageCommandType.PROVIDE_FOLDER_LIST) {
            File root = new File(storageManagement.getFileSystemRoot() 
                    + File.separator);
            ArrayList<String> folderList = new ArrayList<>();

            for (File f : root.listFiles()) {
                if (f.isDirectory()) {
                    folderList.add(f.getName());
                }
            }

            try {
                oos.writeObject(folderList);
                oos.flush();
                oos.close();
            } catch (IOException ex) {
                WeatherLogger.log(Level.WARNING,"Error: could not write value to client");
            }
        }
    
        //Provide day-long video.
        if (command.getCommandType() == StorageCommandType.PROVIDE_DAY_LONG_MP4) {
            ResourceInstancesRequested resourceRequest = command.getResourceRequest();
            
            if (resourceRequest == null) {
                log.finest("Missing storage data (resource request).");
                Debug.println("Missing storage data (resource request).");
                throw new WeatherException(6007);
            }

            // Retrieve the files requested.
            Vector<File> files = storageManagement
                    .retrieveFileVector(resourceRequest, true);
            
            // Save the number of files, including nulls, at each resolution.
            // This is also the number of unique days.
            int uniqueDays = files.size() / 2;
            
            // Counter for the number of elements tarversed.
            int counter = 0;

            // Vectors to hold standard-quality and low-quality resource instances.
            Vector<ResourceInstance> standardQuality = new Vector<>();
            Vector<ResourceInstance> lowQuality = new Vector<>();

            Iterator<File> it = files.iterator();
            // Get the resource of this request.
            Resource resource = storageManagement.getResource(resourceRequest.getResourceID());

            if (resource == null) {
                throw new WeatherException(6001);
            }

            /**
             * Calculate value to test if any trimming of no-data or default
             * videos is necessary.
             */
            /* The idea is that we give the storage system ten minutes grace the
             * movie for 1:00 is ready at 1:10.
             */
            long millisecondsToSubtract
                    = Integer.parseInt(PropertyManager
                            .getGeneralProperty("rangeRetrieveGracePeriod"))
                    * ResourceTimeManager.MILLISECONDS_PER_MINUTE;

            /* We must compare against a time that is millisecondsToSubtract
             * milliseconds in the past because that is alwaya the time that is
             * during the last available hour of video for the day. 
             */
            long timeToCompare = System.currentTimeMillis()
                    - millisecondsToSubtract;
            
            // Get the resource instances from the files.
            while (it.hasNext()) {
                ResourceInstance resourceInstance = null;

                File file = it.next();
                
                // For use in case a returned file is too long.
                File fileForTrimming = null;
                
                // Make sure file is not null before building resource instance.
                if (file != null) {
                    resourceInstance = new MP4Instance(resource);

                    // Begin to calculate start and end times of instance.
                    int resourceCount = counter;
                    if (counter >= uniqueDays) {
                        resourceCount -= uniqueDays;
                    }

                    // Next, find point that is the same number of days into 
                    // range as the number of videos of the same quality added 
                    // so far.
                    long startTime = ResourceTimeManager.
                            getStartOfDayFromMilliseconds(resourceRequest
                            .getResourceRange().getStartTime().getTime(),
                            resource.getTimeZone().getTimeZone());
                    GregorianCalendar startCal = new GregorianCalendar();
                    startCal.setTimeInMillis(startTime);
                    startCal.add(GregorianCalendar.DATE, resourceCount);
                    startTime = startCal.getTimeInMillis();

                    // Calculate end time of video.
                    // Get length of video in milliseconds of video time.
                    long videoTimeInMillis = VideoLengthCalculator
                            .getLengthOfMP4FileInMillis(file);
                    // Calculate hours of real time from value in database.
                    long realHours = Math.round(((double) videoTimeInMillis)
                            / (Integer.parseInt(PropertyManager
                                    .getGeneralProperty("MOVIE_LENGTH")) * 1000));

                    // Set new endTime value, which wll match startTime if
                    // the length is not found.
                    long endTime = startTime + realHours * ResourceTimeManager
                            .MILLISECONDS_PER_HOUR;
                    
                    // If the end time is after the start time, it should refect
                    // the end of an hour.
                    if (endTime > startTime) {
                        endTime--;
                    }

                    resourceInstance.setStartTime(startTime);
                    resourceInstance.setEndTime(endTime);
                    
                    /**
                     * This section ensures that no video is longer than 
                     * expected because it is too soon to get all the data.
                     * It will test all videos, but the only necessary trims
                     * will be to default or no-data videos.
                     */
                    
                    /**
                     * Now, there must be two decrementing counters. The first
                     * one will start at the end of the day in milliseconds. The
                     * second one will start at the total number of seconds that
                     * a full day's worth of video is expected to contain. The
                     * code will decrement the first of these counters by an
                     * hour until the time is less than or equal to the time to
                     * compare. With each loop, it will also decrement the
                     * length of the desired video by one hour's worth of real
                     * time. When this process is done, we will have the length
                     * to which the supplied file must be trimmed.
                     */
                    long realTimeCounter = endTime;

                    // If the real time counter is after the time to compare,
                    // no trimming is necessary.
                    if (realTimeCounter > timeToCompare) {
                        // Set second counter.
                        int hourVideoLength = Integer.parseInt(PropertyManager
                                .getGeneralProperty("MOVIE_LENGTH"));
                        int videoTimeCounter = 24 * hourVideoLength;

                        // Do reductions
                        while (realTimeCounter > timeToCompare) {
                            videoTimeCounter -= hourVideoLength;
                            realTimeCounter -= ResourceTimeManager.MILLISECONDS_PER_HOUR;
                        }
                        
                        /**
                         * If the day is in the future, do nothing with the file
                         * and go the the next loop iteration.
                         */
                        if (videoTimeCounter <= 0) {
                            Debug.println("Can't retrieve day long video for "
                                + "the future.");
                            
                            // Prepare for next loop.
                            counter++;
                            
                            continue;
                        }

                        // Make temporary copy to trim and trim it.
                        String originalFileName = file.getAbsolutePath();
                        int originalExtensionStart = originalFileName.lastIndexOf(".");
                        String extension = originalFileName.substring(originalExtensionStart);
                        fileForTrimming = new File(originalFileName
                                .substring(0, originalExtensionStart) + 
                                "_Storage_Copy" + extension);
                        try {
                            Files.copy(file.toPath(), fileForTrimming.toPath());
                        } catch (IOException e) {
                            Debug.println("Could not copy file for trimming.");
                            WeatherLogger.log(Level.SEVERE, "Error- Could not copy file.", e);
                            break;
                        }
                        VideoTrimmer.trimVideo(fileForTrimming, videoTimeCounter);
                        
                        // Must adjust end time.
                        resourceInstance.setEndTime(realTimeCounter);
                    }
                    
                    /**
                     * End of trimming check.
                     */
                    
                    // Place the data from the file into the resource instance.
                    try {
                        // If the trimmed file is not null, it supercedes the
                        // original.
                        if (fileForTrimming == null) {
                            resourceInstance.readFile(file);
                        } else {
                            resourceInstance.readFile(fileForTrimming);
                        }
                    } catch (WeatherException e) {
                        //if error reading file, return vectors with 0 entries
                        lowQuality.clear();
                        standardQuality.clear();
                        WeatherLogger.log(Level.SEVERE, "Error- Could not read file.", e);
                        break;
                    }
                } //End of null check.
                
                // For debugging.
                String outputString;
                if (file == null) {
                    outputString = "NULL";
                } else if (fileForTrimming != null) {
                   outputString = fileForTrimming.getAbsolutePath();
                } else {
                    outputString = file.getAbsolutePath();
                }
                
                // Delete trimming file if present.
                if (fileForTrimming != null) {
                   fileForTrimming.delete();
                }
                
                // Assign instance to correct vector.
                if (counter >= uniqueDays) {
                    Debug.println("Assigning to low quality list: "
                    + outputString);
                    lowQuality.add(resourceInstance);
                } else {
                    Debug.println("Assigning to standard quality list: "
                    + outputString);
                    standardQuality.add(resourceInstance);
                }
                
                // Prepare for next loop.
                counter++;
            }
            
            //Debeg results
            Debug.println("\nStandard Vector: ");
            for (ResourceInstance value : standardQuality) {
                Debug.println(value == null ? "NULL" : value);
                Debug.println();
            }
            
            Debug.println("Low-Quality Vector: ");
            for (ResourceInstance value : lowQuality) {
                Debug.println(value == null ? "NULL" : value);
                Debug.println();
            }

            //Semd back results.
            try {
                ResourceInstancesReturned standardQualityData
                        = new ResourceInstancesReturned(standardQuality, 
                        standardQuality.size(), resourceRequest.getResourceRange());
                ResourceInstancesReturned lowQualityData
                        = new ResourceInstancesReturned(lowQuality, 
                        lowQuality.size(), resourceRequest.getResourceRange());
                ArrayList<ResourceInstancesReturned> returnedList
                        = new ArrayList<>();
                returnedList.add(standardQualityData);
                returnedList.add(lowQualityData);
                if (oos != null) { //Condition to make testing possible
                    oos.writeObject(returnedList);//send vector to requestor
                    oos.flush();
                    oos.close();
                }
            } catch (IOException ex) {

                WeatherLogger.log(Level.WARNING, "Error: could not write value to client",
                        ex);
            }
        }
        
        if (command.getCommandType() == StorageCommandType.PROVIDE) {
            ResourceInstancesRequested resourceRequest = command.getResourceRequest();
            
            if (resourceRequest == null) {
                log.finest("Missing storage data (resource request).");
                Debug.println("Missing storage data (resource request).");
                throw new WeatherException(6007);
            }

            // Retrieve the files requested.
            Vector<File> files = storageManagement
                    .retrieveFileVector(resourceRequest, false);
            Vector<ResourceInstance> values = new Vector<>();

            Iterator<File> it = files.iterator();
            // Get the resource of this request.
            Resource resource = storageManagement.getResource(resourceRequest.getResourceID());

            if (resource == null) {
                throw new WeatherException(6001);
            }

            WeatherResourceType resourceType = resource.getResourceType();
            int resourceCount = 0;

        // Get the resource instances from the files.
            while (it.hasNext()) {
                ResourceInstance resourceInstance;

                File file = (File) it.next();

                //Handle movie requests.
                if (resourceRequest.requestingMovie()) {

                    AVIInstance movieInstance;
                    // Instantiate correct movie type.
                    if (resourceRequest.getFileType() == ResourceFileFormatType.avi) {
                        movieInstance = new AVIInstance(resource);
                    } else // if .mp4
                    {
                        movieInstance = new MP4Instance(resource);
                    }
                    resourceInstance = movieInstance;

                    //Set start time of AVIInstance.
                    long startTime;
                    //First, find point that is the same number of hours into range 
                    //as the number of videos added so far.
                    startTime = resourceRequest.getResourceRange().getStartTime().getTime()
                            + resourceCount * ResourceTimeManager.MILLISECONDS_PER_HOUR;
                    //Now, use integer arithmetic to drop any factional hour.
                    startTime /= ResourceTimeManager.MILLISECONDS_PER_HOUR;
                    startTime *= ResourceTimeManager.MILLISECONDS_PER_HOUR;
                    WeatherLogger.log(Level.SEVERE, "Provide:Generic Movie being returned "
                            + "Resource was " + resource.getName() + " file name was " + file.getName());
                    movieInstance.setStartTime(startTime);

                    //The end time of a movie is 1 millisecond less than the next hour
                    long endTime = startTime + ResourceTimeManager.MILLISECONDS_PER_HOUR - 1;

                    //Now set the start and end time for this instance
                    movieInstance.setEndTime(endTime);
                } //Request was not for a movie, so return an image
                else if (resourceType == WeatherResourceType.WeatherCamera
                        || resourceType == WeatherResourceType.WeatherMapLoop) {
                    resourceInstance = new ImageInstance(resource);
                    long startTime = ResourceTimeManager
                            .extractTimeFromFilename(file.getName(), resource
                                    .getTimeZone().getTimeZone());
                    long endTime = startTime; //Same for a picture

                    resourceInstance.setStartTime(startTime);
                    resourceInstance.setEndTime(endTime);
                } //Request is for a weather station text file
                else if (resourceType == WeatherResourceType.WeatherStation
                        // || resourceType == WeatherResourceType.TextFile
                        || resourceType == WeatherResourceType.WeatherStationValues) {
                    if (WeatherUndergroundInstance.isWeatherUndergroundInstance(resource)) {
                        resourceInstance = new WeatherUndergroundInstance(resource);
                    } else {
                        resourceInstance = new WeatherStationInstance(resource);
                    }
                    // startTime is the start of the day
                    long startTime
                            = ResourceTimeManager.extractTimeFromPath(file
                                    .getParent(), resource.getTimeZone().getTimeZone());

                    //Assume this is a complete past day and then change if not
                    long endTime = ResourceTimeManager
                            .getEndOfDayFromMilliseconds(startTime, resource
                                    .getTimeZone().getTimeZone());
                    if (endTime > System.currentTimeMillis()) {
                        endTime = System.currentTimeMillis();
                    }

                    resourceInstance.setStartTime(startTime);
                    resourceInstance.setEndTime(endTime);
                } else {
                    WeatherLogger.log(Level.SEVERE, "Provide:Unrecognized Resource Type "
                            + "Resource was " + resource.getName() + " Resource Type was "
                            + resource.getResourceType());
                    break; //Can't load this reaource type
                }

                //Debug.println("File name was "+ file.getName());
                //Place the data from the file into the resource instance.
                try {
                    resourceInstance.readFile(file);
                } catch (WeatherException e) {
                    //if error reading file, return vector with 0 entries
                    values.clear();
                    WeatherLogger.log(Level.SEVERE, "Error- Could not read file.", e);
                    break;

                }
                values.add(resourceInstance);
                resourceCount++;
            }

            //Debeg results
            Debug.println("Provided Vector: ");
            for (ResourceInstance value : values) {
                Debug.println(value);
            }

            //Semd back results.
            try {
                ResourceInstancesReturned returnData
                        = new ResourceInstancesReturned(values, values.size(), resourceRequest.getResourceRange());
                if (oos != null) { //Condition to make testing possible
                    oos.writeObject(returnData);//send vector to requestor
                    oos.flush();
                    oos.close();
                }
            } catch (IOException ex) {

                WeatherLogger.log(Level.WARNING, "Error: could not write value to client",
                        ex);
            }
        }
    }

    /**
     * Returns true if the storage handler is available and false otherwise
     *
     * @return true if handler is available and false otherwise
     */
    @Override
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets whether the storage handler is available or not
     *
     * @param available the boolean value to assign to the available attribute
     * of the handler true is handler is available and false otherwise
     */
    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Assigns a value to the socket attribute of the storage handler
     *
     * @param socket the socket number to assign to the socket attribute of the
     * handler
     */
    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Returns the storage management of the storage handler
     *
     * @return the storage management of the the handler
     */
    public StorageManagement getStorageManagement() {
        return storageManagement;
    }

    /**
     * Assigns a value to the storage management attribute of the storage
     * handler
     *
     * @param storageManagement the value to assign to the storage management
     * attribute of the handler
     */
    public void setStorageManagement(StorageManagement storageManagement) {
        this.storageManagement = storageManagement;
    }
}
