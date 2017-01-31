package weather.serverside.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.*;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.dbms.DBMSSystemManager;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.*;
import weather.serverside.FFMPEG.VideoLengthCalculator;
import weather.serverside.FFMPEG.VideoTrimmer;

/**
 * Storage Server Local is used by tasks running on the same computer
 * that implements our storage system.  It directly accesses the file system
 * instead of using the storage system server. Normally this class
 * will be used by our data retrieval and movie maker systems.
 * This class currently only handles requests to store and provide date from
 * <code>StorageControlSystemLocalImpl</code> as of 1/13/16.
 *
 * @author Bloomsburg University Software Engineering
 * @author Bill Katsak (2008)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class StorageServerLocal {

    private final StorageManagement storageManagement;

    // Storage log.
    private static final WeatherTracer log = WeatherTracer.getStorageLog ();
    

    /**
     * Constructor for the StorageServerLocal class.
     * @param dbms The database management system to use for this server.
     * @param fileSystemRoot The system root for this server.
     */
    public StorageServerLocal(DBMSSystemManager dbms, String fileSystemRoot) {
        log.info ("Creating new local storage server.");
        storageManagement = new StorageManagement(dbms, fileSystemRoot);
    }

    /**
     * Executes a store command that stores data on the local server.
     *
     * @param command The store command.
     * @throws weather.common.utilities.WeatherException
     */
    public synchronized void executeLocalStore(StorageCommand command) 
            throws WeatherException {
        //log.finest ("Running executeLocalStore local.");

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
                new Date(startTime), ResourceFileFormatType.mp4, true, false);
            file.getParentFile().mkdirs();
            log.finest("Writing standard-quality day-long movie " + file.getName() + ".");
            Debug.println("Writing standard-quality day-long movie " + file.getName() + ".");
            resourceInstance1.writeFile(file);
            //Add low-quality copy if provided.
            if (resourceInstance2 != null) {
                File file2 = storageManagement.getNewStorageFile(resourceID,
                        new Date(startTime), ResourceFileFormatType.mp4,
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
                new Date(startTime), movieType, false, false);
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
        //log.finest ("Finished executeLocalStore local.");
    }

    /**
     * Executes a provide command that stores data on the local server and 
     * returns the requested data.
     *
     * @param command The provide command.
     * @return An ArrayList of type ResrouceInstancesReturned object containing 
     * the resources, if any that have been collected for this provide command.
     * All data is contained at index 0 unless day-long videos are requested,
     * in which case, low-quality versions are at index 1.
     * @throws weather.common.utilities.WeatherException
     */
    public synchronized ArrayList<ResourceInstancesReturned>
         executeLocalProvide(StorageCommand command) throws WeatherException {
       Debug.println ("Running executeProvide local.");
       
       ArrayList<ResourceInstancesReturned> returnList = new ArrayList<>();

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

            //Return results.
            ResourceInstancesReturned standardQualityData
                    = new ResourceInstancesReturned(standardQuality,
                            standardQuality.size(), resourceRequest.getResourceRange());
            ResourceInstancesReturned lowQualityData
                    = new ResourceInstancesReturned(lowQuality,
                            lowQuality.size(), resourceRequest.getResourceRange());
            returnList.add(standardQualityData);
            returnList.add(lowQualityData);
            
            return returnList;
        }

        //Provide other data (StorageCommandType == PROVIDE)
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
                    //WeatherUndergroundInstance is the only type we allow right now
                    resourceInstance = new WeatherUndergroundInstance(resource);
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

        //Return results.
        ResourceInstancesReturned returnData = new ResourceInstancesReturned(values, 
                values.size(), resourceRequest.getResourceRange());
            
        returnList.add(returnData);
        
        return returnList;
    }
         
    /**
     * Returns the path to an MP4 movie as a <code>File</code> object given a
     * resource number and a time. For hour-long movies, a path to a default
     * movie is returned if the movie does not exist. For full-day movies, the
     * original path is always returned and the caller must specify the quality
     * (standard or low) of the requested movie.
     *
     * @param resourceID The resource number to match the <code>Resource</code>
     * of the requested movie.
     * @param time The start time of the movie as a <code>Date</code> object.
     * @param isFullDay True if a day-long movie is being requested; False
     * otherwise.
     * @param isLowQuality True if a low-quality movie is being requested; False
     * otherwise. This parameter is ignored if an hour-long movie is being
     * requested.
     * @return The path in a <code>File</code> object. For hour-long movies, a
     * path to a default movie is returned if the movie does not exist. For
     * full-day movies, the original path is always returned.
     */
    public File getFileForMP4Movie(int resourceID, Date time, boolean isFullDay,
            boolean isLowQuality) {
        return storageManagement.getFileForMP4Movie(resourceID, time, 
                isFullDay, isLowQuality);
    }
    
    /**
     * Gets the default MP4 hour-long movie out of the storage system for a 
     * given resource at a given time.  The method will bypass the actual 
     * recorded data if it exists.
     *
     * If it's nighttime, we set the path to a default nighttime movie for the
     * resource Otherwise, we set the path to a default daytime movie for the
     * resource.
     *
     * Next, create a new file from that path. If that file exists (which means
     * we have a default picture to return), return it. Otherwise, return the
     * storage system's default NoData movie instead.
     *
     * @param resource The resource whose movie we are searching for.
     * @param calendar A calender holding the time of the movie we're looking
     * for.
     *
     * @return If it's nighttime, a default nighttime movie. If it's daytime, 
     * a default daytime movie. The next option is the storage system's default
     * NoData movie. The final option is null.
     */
    public File getDefaultMP4MovieForTime(Resource resource, 
            GregorianCalendar calendar) {
        return storageManagement.getDefaultMP4MovieForTime(resource, calendar);
    }
    
    /**
     * Method to determine if there are hour-long MP4 videos are present for the
     * given <code>Resource</code> on the given day to make a day-long video.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if there are enough hour-long MP4 videos present for the
     * given <code>Resource</code> on the given day to make a day-long video;
     * False otherwise.
     */
    public boolean hasHourLongMP4Videos(int resourceID, Date time) {
        return storageManagement.hasHourLongMP4Videos(resourceID, time);
    }
    
    /**
     * Copies any missing default files to the file system specified by the file
     * system root passed to the constructor. The source files must already be
     * in the portion of the project's file structure the will be distributed.
     * No existing files are replaced.
     *
     * @return True if all required copying was successful; false otherwise.
     */
    public boolean saveDefaultVideos() {
        return storageManagement.saveDefaultVideos();
    }
    
    /**
     * Method to determine if enough hour-long MP4 videos can be made form AVI
     * videos that are present for the given <code>Resource</code> on the given 
     * day to make a day-long video.  This function will make an MP4 copy of 
     * each AVI video that is present for the given day.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if enough hour-long MP4 videos can be made form AVI videos
     * that are present for the given <code>Resource</code> on the given day to
     * make a day-long video; False otherwise.
     */
    public boolean canMakeEnoughMP4VideosFromAVIVideos(int resourceID, 
            Date time) {
        return storageManagement.canMakeEnoughMP4VideosFromAVIVideos(resourceID, 
                time);
    }
    
     /**
     * Method to convert all the hour-long MOV videos that are present for a
     * given <code>Resource</code> on the given day to MP4 format.
     * 
     * NOTE: This method will delete, rather than convert, any hour-long MOV
     * videos of the wrong length.
     * 
     * @param resourceID The given <code>Resource</code>.
     * @param time A <code>Date</code> indicating the start of the day in the 
     * past.  It must be midnight in the local time of the given 
     * <code>Resource</code>.
     * @return True if all copying and file clean-up was successful; False 
     * otherwise.
     */
    public boolean convertMOVVideosToMP4Videos(int resourceID, Date time) {
        return storageManagement.convertMOVVideosToMP4Videos(resourceID, time);
    }
}
