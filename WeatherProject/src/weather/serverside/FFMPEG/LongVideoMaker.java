package weather.serverside.FFMPEG;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.ResourceRange;
import weather.common.data.resource.MP4Instance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.utilities.Debug;
import weather.common.utilities.Emailer;
import weather.common.utilities.ImageDimensionFinder;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This file provides a utility for creating day-long MP4 videos. It takes the
 * data for the video to be created as arguments to its constructor.  Those 
 * arguments are a <code>StorageControlSystem</code>, a <code>Resource</code>,
 * and a <code>Date</code>.  The last of those represents the ending time of the
 * videos to be made.  This allows for videos of partial days to be made  From
 * the given time, the object instance calculates the start of the day in the
 * resource time zone and uses that as the starting point for the day-long 
 * video.  Calling the public method <code>createDayLongVideo</code> creates and
 * permanently stores the day-long videos.  Any existing videos will be 
 * overridden.  A standard-quality videos will always be made with an option to
 * make a low-quality copy.  Email error notifications will be sent to all 
 * administrators if the day-long videos cannot be made.
 * 
 * @author Brian Bankes
 */
public class LongVideoMaker {
    
    //A fudge factor for length determination  This is a PATCH because the hour-
    //long videos are not all MOVIE_LENGTH seconds long, as they should be.
    //The expected length of all videos is now tested within a fudge factor of
    //this constant, measured in seconds.  (PATCH DATE: 2/22/16)
    private final int MOVIE_LENGTH_TOLERANCE = 2;

    //The storage system passed to the constructor.
    private final StorageControlSystem storageSystem;

    //The resource passed to the constructor.
    private final Resource resource;

    //The endding time passed to the constructor and the starting time
    //calculated from it in the constructor.
    private final Date startOfLongVideo;
    private final Date endOfLongVideo;

    //Files to hold temporary copies of the standard-quality and low-quality 
    //videos being produced.  File paths are defined in the constructor.
    private final File standardQualityTempFile;
    private final File lowQualityTempFile;

    //The length of the videos to be made in real-time hours as calculated in
    //the constructor.
    private final int hoursInLongVideo;

    //A date format to be used when sending emails once ininialized in the 
    //constructor.
    private final SimpleDateFormat emailDateFormat;

    //A date format to be used when specifing file names once ininialized in the 
    //constructor.
    private final SimpleDateFormat fileNameFormat;

    //Object to hold the complete list of errors to be sent in the error email.
    private final StringBuilder errorMessageList;

    //A boolean to specify whether or not the low-quality copy of a day-long 
    //video is to be made.  This will be ignored if the command is not making
    //day-long videos.
    private final boolean makingLowQuality;

    //A string representation of the calling code used for error emails.
    private final String callingCodeDescription;

    //The file directory where the day-long video and, if requsted, its
    //low-quality copy are to be assembled.  This is a temporary location and
    //not the file save destination.
    private final File buildDirectory;

    //A common prefix to all temporary files created by this object.  It is
    //indicative of the calling code and date.  If another object of this class
    //shares the same calling code and date and did not successfully clear its
    //temporary files, this value will be used to clear them.
    private final String tempFilePrefix;
     
    /**
     * Makes a new <code>LongVideoMaker</code> object.  To get the videos from 
     * the object call <code>createDayLongVideo</code>.  Email error 
     * notification will be sent to all  administrators if the day-long videos 
     * cannot be made.  A standard-quality videos will always be made with an 
     * option to make a low-quality copy.  Any existing videos will be 
     * overridden.
     * 
     * @param storageSystem A <code>StorageControlSystem</code> with access the
     * the hour-long MP4 videos on the local machine.
     * @param resource The <code>Resource</code> for which the video is being 
     * made.
     * @param endOfLongVideo A <code>Date</code> that represents the ending time
     * of the videos to be made. This allows for videos of partial days to be
     * made  From the given time the object instance calculates the start of the
     * day in the resource time zone and uses that as the starting point for the
     * day-long videos.  This MUST be the last millisecond of an hour.
     * @param makingLowQuality A boolean to specify whether or not the
     * low-quality copy of a day-long video is to be made.
     * @param callingCodeDescription A <code>String</code> representation of the
     * calling code used for error e-mails.
     * @param callingCodeId A shortened version of the previous parameter. It is
     * used internally by this object be create temporary file name and should 
     * be unique to each usage of this constructor.
     */
    public LongVideoMaker(StorageControlSystem storageSystem, Resource resource,
            Date endOfLongVideo, boolean makingLowQuality, 
            String callingCodeDescription, String callingCodeId) {
        this.storageSystem = storageSystem;
        this.resource = resource;
        this.callingCodeDescription = callingCodeDescription;
        this.endOfLongVideo = endOfLongVideo;
        this.makingLowQuality = makingLowQuality;
        this.startOfLongVideo = ResourceTimeManager
                .getStartOfDayDateFromMilliseconds(endOfLongVideo.getTime(), 
                resource.getTimeZone().getTimeZone());
        this.fileNameFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.fileNameFormat.setTimeZone(resource.getTimeZone().getTimeZone());
        this.buildDirectory = new File(storageSystem.getRootDirectory()
                + File.separator + resource.getStorageFolderName()); 
        this.tempFilePrefix = callingCodeId + "-" + fileNameFormat
                .format(startOfLongVideo);
        this.standardQualityTempFile = new File(buildDirectory.getAbsoluteFile()
                + File.separator + tempFilePrefix + ".mp4");
        this.lowQualityTempFile = new File(buildDirectory.getAbsoluteFile()
                + File.separator + tempFilePrefix + "_LQ_Copy.mp4");
        this.hoursInLongVideo = (int)((this.endOfLongVideo.getTime()
                - this.startOfLongVideo.getTime() + 1) / ResourceTimeManager
                .MILLISECONDS_PER_HOUR);
        this.emailDateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss a z");
        this.emailDateFormat.setTimeZone(resource.getTimeZone().getTimeZone());
        this.errorMessageList = new StringBuilder("");
    }
    
    /**
     * A Helper function that attempts to concatenate the elements of an
     * <code>ArrayList</code> of type <code>File</code> into a single 
     * <code>File</code>.  The elements must be video files.
     * 
     * @param inputFiles The input <code>ArrayList</code>.
     * @param outputFile The output  <code>File</code>.
     * @return True if the concatenation was successful; false otherwise.
     */
    private boolean concatenateVideoFiles(ArrayList<File> inputFiles,
            File outputFile) {
        //Get image size and return if it can't be found.
        Dimension imageSize = ImageDimensionFinder
                .getDimensionOfResourceImage(storageSystem.getDBMS(), resource);
        if (imageSize == null) {
            //Get default image size.
            int width = Integer.parseInt(PropertyManager
                    .getGeneralProperty("DEFAULT_MOVIE_WIDTH"));
            int height = Integer.parseInt(PropertyManager
                    .getGeneralProperty("DEFAULT_MOVIE_HEIGHT"));
            imageSize = new Dimension(width, height);
        }
        
        //Get conponents of dimension and make sure they are even.
        int width;
        if (imageSize.width % 2 == 1) {
            width = imageSize.width + 1;
        } else {
            width = imageSize.width;
        }
        int height;
        if (imageSize.height % 2 == 1) {
            height = imageSize.height + 1;
        } else {
            height = imageSize.height;
        }
        
        //Perform concatenation.
        boolean result = VideoConcatenator.concatenateVideoFiles(inputFiles,
                outputFile, width, height);
        
        //Return if concatenation was successful.
        return result;
    }
    
    /**
     * Sends an email error notification to all administrators if the day-long
     * videos cannot be made.
     */
    private void sendErrorMessage() {
        StringBuilder subject = new StringBuilder();
        subject.append("BU Weather Viewer Day-Long Video Creation ");
        subject.append("Error for ").append(resource.getResourceName());
        StringBuilder message = new StringBuilder();
        message.append("An error has occurred while making a day-long video.  ");
        message.append("Please see the details below:\n");
        message.append("Resource: ").append(resource.getResourceName());
        message.append(" (#").append(resource.getResourceNumber()).append(")");
        message.append("\nVideo Start Time: ").append(emailDateFormat
            .format(startOfLongVideo));
        message.append("\nVideo End Time: ").append(emailDateFormat
            .format(endOfLongVideo));
        message.append("\n\nError messages:\n").append(errorMessageList);
        message.append("\nThis video was being made by ");
        message.append(callingCodeDescription).append(".");
        try {
            Emailer.emailAdmin(message.toString(), subject.toString());
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE,
                    "Day-long video maker unable to send error email message.");
            Debug.println(
                    "Day-long video maker unable to send error email message.");
        }
    }
    
    /**
     * A Helper function to record a error message.  It does the following:
     * 1. Adds the message to the list to be sent by email.
     * 2. Logs the message.
     * 3. Shows the message in Debug.
     * 
     * @param errorMessage The error message.
     */
    private void logError(String errorMessage) {
        errorMessageList.append(errorMessage).append("\n");
        WeatherLogger.log(Level.SEVERE, errorMessage + " (Resource: "
            + resource.getName() + ")");
        Debug.println(errorMessage); 
    }
    
    /**
     * A Helper function to record a message to the log.  It does the following:
     * 1. Logs the message.
     * 2. Shows the message in Debug.
     * 
     * @param messageToLog The message to log.
     */
    private void logMessage(String messageToLog) {
        WeatherLogger.log(Level.INFO, messageToLog + " (Resource: "
            + resource.getName() + ")");
        Debug.println(messageToLog); 
    }
    
    /**
     * Converts an MP4 file containing data the will be stored (one of the
     * temporary files) into an <code>MP4Instance</code> so the it can be
     * permanently saved.
     * 
     * @param file The <code>File</code> to be converted.
     * @return The resulting <code>MP4Instance</code>.
     * @throws WeatherException if file cannot be read.
     */
    private MP4Instance readDayLongFileIntoInstance(File file) 
            throws WeatherException {
        MP4Instance instanceToReturn = new MP4Instance(resource);
        instanceToReturn.readFile(file);
        instanceToReturn.setMillis(System.currentTimeMillis());
        instanceToReturn.setStartTime(startOfLongVideo.getTime());
        instanceToReturn.setEndTime(endOfLongVideo.getTime());
        
        return instanceToReturn;
    }
    
    /**
     * Permanently saves the temporary files.
     * 
     * @return True if the save was successful; false otherwise.
     */
    private boolean saveDataPermanantly() {
        try {
            MP4Instance standardInstance
                    = readDayLongFileIntoInstance(standardQualityTempFile);
            MP4Instance lowQualityInstance = null;

            //Keep low-quality instance null if that video wasn't made.
            if (makingLowQuality) {
                lowQualityInstance
                        = readDayLongFileIntoInstance(lowQualityTempFile);
            }
            
            storageSystem.placeDayLongMovie(standardInstance, 
                    lowQualityInstance);
        } catch (WeatherException ex) {
            return false;
        }
        return true;
    }
    
    /**
     * Deletes any temporary files that exist.
     */
    private void clearTempFiles() {
        if (this.lowQualityTempFile.exists()) {
            lowQualityTempFile.delete();
        }
        if (standardQualityTempFile.exists()) {
            standardQualityTempFile.delete();
        }
    }
    
    /**
     * 
     * Validates the existing day-long video for the day and resource in 
     * question.  If the file does not exist, is too small in terms or bytes, or 
     * is the wrong length in terms of time, false is returned.  Otherwise true
     * is returned.
     *
     * @param fileToValidate The <code>File</code> to validate.
     * @return An indication of the validity of the file.  If the file does not
     * exist, is too small in terms or bytes, or is the wrong length in terms of
     * time, false is returned.  Otherwise true is returned.
     */
    private boolean validateFullDayFile(File fileToValidate) {
        //Check if instance is null.
        if (fileToValidate == null) {
            logError(
                    "Day-long video maker passed file instance to day-long"
                    + " video validation function.");
            return false;
        }
        
        //Check if file exists.
        if (!fileToValidate.exists()) {
            logError(
                    "Day-long video maker attempted to retrieve day-long video"
                    + " that does not exist.");
            return false;
        }
        
        //Check file size in bytes.
        if(fileToValidate.length() <= 50) {
            logError(
                    "Day-long video maker attempted to retrieve day-long video"
                    + " that was too small in terms of bytes  The size was "
                    + fileToValidate.length() + " bytes and should be at least "
                    + "50 bytes.");
            return false;
        }
        
        //Check duration of video. (should be one hour less than the video being
        //produced in real time).
        //PATCH on 2/22/16: Any length within MOVIE_LENGTH_TOLERANCE seconds of
        //the expected length is accepted.
        int actualLength = VideoLengthCalculator
                .getLengthOfMP4FileInSeconds(fileToValidate);
        int expectedLength = (hoursInLongVideo - 1) 
                * Integer.parseInt(PropertyManager
                .getGeneralProperty("MOVIE_LENGTH"));
        if (Math.abs(actualLength - expectedLength) > MOVIE_LENGTH_TOLERANCE){
            logError(
                    "Day-long video maker attempted to retrieve day-long video"
                    + " that was not the right length in terms of time.  It"
                    + " should have been " + expectedLength + " seconds long,"
                    + " but it was " + actualLength + " seconds long instead.");
            return false;
        }
        
        //All tests passed, so return true.
        return true;
    }
    /**
     * Retrieves the default hour-long movie for a given time form the storage 
     * system.  
     * IMPORTANT: This function should only be called when a movie with 
     * retrieved data has been found and judged to be flawed.
     * 
     * @param timeInMillis The time for which the default movie should be 
     * retrieved in milliseconds. 
    * @return A <code>File</code> from the storage system whose data is the
    * default movie.
     */
    private File getDefaultMovieForTime(long timeInMillis) {
        GregorianCalendar timeCal = new GregorianCalendar();
        timeCal.setTimeZone(resource.getTimeZone().getTimeZone());
        timeCal.setTimeInMillis(timeInMillis);
        return this.storageSystem.getDefaultMP4MovieForTime(resource, timeCal);
    }
    
    /**
     * Tries to return the object that represents an hour-long MP4 file the
     * provided the data for an existing <code>MP4Instance</code>. If the file
     * does not exist, is too small in terms or bytes, or is the wrong length in
     * terms of time, null is returned.
     *
     * @param instance The <code>MP4Instance</code> whose <code>File</code> is
     * to be found.
     * @return The <code>File</code> if it is validated or null if it is not.
     */
    private File getAndValidateHourLongFile(MP4Instance instance) {
        //Check if instance is null.
        if (instance == null) {
           logError(
                    "Day-long video maker passed null instance to hour-long"
                    + " video validation function.");
            return null;
        }
        
        //Get file to return if valid.
        File instanceFile = storageSystem.getFileForMP4Movie(instance
            .getResourceNumber(), new Date(instance.getStartTime()), false, 
            false);
        
        //Check if file exists.
        if (!instanceFile.exists()) {
           logError(
                    "Day-long video maker attempted to retrieve hour-long video"
                    + " that does not exist.");
            return null;
        }
        
        //Check file size in bytes.
        if(instanceFile.length() <= 50) {
            logMessage(
                    "Day-long video maker attempted to retrieve hour-long video"
                    + " that was too small in terms of bytes  The size was "
                    + instanceFile.length() + " bytes and should be at least "
                    + "50 bytes.  A default video will be substituted.");
            return getDefaultMovieForTime(instance.getStartTime());
        }
        
        //Check duration of video. (should be one hour).
        //PATCH on 2/22/16: Any length within MOVIE_LENGTH_TOLERANCE seconds of
        //the expected length is accepted.
        int actualLength = VideoLengthCalculator
                .getLengthOfMP4FileInSeconds(instanceFile);
        int expectedLength = Integer.parseInt(PropertyManager
                .getGeneralProperty("MOVIE_LENGTH"));
        if (Math.abs(actualLength - expectedLength) > MOVIE_LENGTH_TOLERANCE){
            logMessage(
                    "Day-long video maker attempted to retrieve hour-long video"
                    + " that was not the right length in terms of time.  It"
                    + " should have been " + expectedLength + " seconds long,"
                    + " but it was " + actualLength + " seconds long instead."
                    + "  A default video will be substituted.");
            return getDefaultMovieForTime(instance.getStartTime());
        }
        
        //All tests passed, so return file.
        return instanceFile;
    }
    
    /**
     * Makes the low-quality temporary file from the standard-quality temporary
     * file.
     * @return True if the copy was successful; false otherwise.
     */
    private boolean makeLowQualityCopy() {
        boolean result = VideoLowQualityCopier
                .makeLowQualityCopy(standardQualityTempFile, 
                lowQualityTempFile);
        return result;
    }
    
    /**
     * Helper function to trim the standard quality temporary file to its 
     * expected length, as made necessary by extra milliseconds introduced by
     * ffmpeg.
     * @return True if the trimming was successful; false otherwise.
     */
    private boolean trimTempVideo() {
        int lengthToTrim = hoursInLongVideo * Integer.parseInt(PropertyManager
                .getGeneralProperty("MOVIE_LENGTH"));
        boolean result = VideoTrimmer.trimVideo(standardQualityTempFile, 
                lengthToTrim);
        return result;
    }
    
    /**
     * Creates the initial day-long video for the first hour of a given day.
     * Also makes the low-quality copy if the object is set to do so.
     * @return True if the copy was successful; false otherwise.
     */
    private boolean copySingleHourVideo() {
        //Clear any temporary file for the object's resource.
        clearTempFiles();
        
        //Make dayRequest for video to be copied.
        ResourceRange range = new ResourceRange(startOfLongVideo, 
                endOfLongVideo);
        ResourceInstancesRequested request 
                = new ResourceInstancesRequested(range, 1, true, 
                        ResourceFileFormatType.mp4, resource);
        
        //Get hour-long video from storage system.
        //Note that there is only one.
        ResourceInstancesReturned returned;
        returned = storageSystem.getResourceInstances(request);
        
        if(returned.getNumberOfValuesReturned() == 0) {
            logError(
                    "Day-long video maker unable to retrieve single-hour"
                    + " video when copying a single-hour video.");
            return false;
        }
            
        MP4Instance hourInstance = (MP4Instance)returned.getResourceInstances()
                .get(0);
        
        //Get file to copy and check if it is valid.
        File instanceDataFile = getAndValidateHourLongFile(hourInstance);
        if (instanceDataFile == null) {
            return false;
        }
        
        //Copy file.
        try {
            Files.copy(instanceDataFile.toPath(), 
                    standardQualityTempFile.toPath());
        } catch (IOException ex) {
            logError(
                    "Day-long video maker unable to copy single-hour"
                    + " video when creating a day-long video.");
            return false;
        }
        
        //Trim video, which must be done to remove extra milliseconds added by
        //ffmpeg processes.  If this is not done, day long bideos grow too long 
        //over time.
        if(!trimTempVideo()) {
            logError(
                    "Day-long video maker unable to trim video when creating a"
                    + " single-hour video.");
            return false;
        }
        
        //Make low-quality copy if set to do so and check if successful.
        if (makingLowQuality) {
            if (!makeLowQualityCopy()) {
                logError(
                        "Day-long video maker unable to create low-quality copy"
                        + " of video when creating a single-hour video.");
                return false;
            }
        }
        
        //Permanantly save vidros and check if save was successful.
        if(!saveDataPermanantly()) {
            logError(
                    "Day-long video maker unable to permanently save"
                    + " videos when creating a single-hour video.");
            return false;
        }
        
        //All steps worked, so return true.
        return true;
    }
    
    /**
     * Creates, or updates, a day-long video by adding an hour to the existing
     * video.  Also makes the low-quality copy if the object is set to do so.
     * @return True if the concatenation was successful; false otherwise. 
     */
    private boolean addHourToExistingVideo() {
        //Clear any temporary file for the object's resource.
        clearTempFiles();
        
        //Get path to existing day-long video from storage system.
        File dayInstanceDataFile = storageSystem
                .getFileForMP4Movie(resource.getResourceNumber(), 
                startOfLongVideo, true, false);
        
        //Validate file.
        if (!validateFullDayFile(dayInstanceDataFile)) {
            return false;
        }
        
        //Find start time of hour to be added.
        long startOfLastHourInMills = endOfLongVideo.getTime() + 1
            - ResourceTimeManager.MILLISECONDS_PER_HOUR;
        Date startOfLastHour = new Date(startOfLastHourInMills);
        
        //Make request for video to be added
        ResourceRange hourRange = new ResourceRange(startOfLastHour, 
                endOfLongVideo);
        ResourceInstancesRequested hourRequest 
                = new ResourceInstancesRequested(hourRange, 1, true, 
                        ResourceFileFormatType.mp4, resource);
        
        //Get hour-long video from storage system.
        //Note that there is only one.
        ResourceInstancesReturned hourReturned;
        hourReturned = storageSystem.getResourceInstances(hourRequest);
        
        if (hourReturned.getNumberOfValuesReturned() == 0) {
            logError(
                    "Day-long video maker unable to retrieve single-hour"
                    + " video when adding to an existing video.");
            return false;
        }
        
        MP4Instance hourInstance = (MP4Instance)hourReturned
                .getResourceInstances().get(0);
        
        //Get file to copy and check if it is valid.
        File hourInstanceDataFile = getAndValidateHourLongFile(hourInstance);
        if (hourInstanceDataFile == null) {
            return false;
        }
        
        //Setup concatenation.
        ArrayList<File> inputList = new ArrayList<>();
        inputList.add(dayInstanceDataFile);
        inputList.add(hourInstanceDataFile);
        
        //Do concatenation and check if it worked.
        if(!concatenateVideoFiles(inputList, standardQualityTempFile)) {
            logError(
                    "Day-long video maker unable to perform concatenation"
                    + " when adding to an existing video.");
            return false;
        }
        
        //Trim video, which must be done to remove extra milliseconds added by
        //ffmpeg processes.  If this is not done, day long bideos grow too long 
        //over time.
        if(!trimTempVideo()) {
            logError(
                    "Day-long video maker unable to trim video when adding"
                    + " to an existing video.");
            return false;
        }
        
        //Make low-quality copy if set to do so and check if successful.
        if (makingLowQuality) {
            if (!makeLowQualityCopy()) {
                logError(
                        "Day-long video maker unable to create low-quality copy"
                        + " of video when adding to an existing video.");
                return false;
            }
        }
        
        //Permanantly save vidros and check if save was successful.
        if(!saveDataPermanantly()) {
            logError(
                    "Day-long video maker unable to permanently save"
                    + " videos when adding to an existing video.");
            return false;
        }
        
        //All steps worked, so return true.
        return true;
    }
    
    /**
     * Create the day-long video by concatenating a series of hour-long videos.
     * It should only be called when adding a hour to the existing day-long
     * video fails.  Also makes the low-quality copy if the object is set to do 
     * so.
     * @return True if the concatenation was successful; false otherwise. 
     */
    private boolean makeDayLongVideoFromScratch() {
        //Clear any temporary file for the object's resource.
        clearTempFiles();
        
        //Make dayRequest for video to be copied.
        ResourceRange range = new ResourceRange(startOfLongVideo, 
                endOfLongVideo);
        ResourceInstancesRequested request 
                = new ResourceInstancesRequested(range, 1, true, 
                        ResourceFileFormatType.mp4, resource);
        
        //Loop through hour-long video from storage system and add them to
        //concatenation input list.
        ArrayList<File> inputList = new ArrayList<>();
        ResourceInstancesReturned returned;
        returned = storageSystem.getResourceInstances(request);
        
        if (returned.getNumberOfValuesReturned() == 0) {
            logError(
                    "Day-long video maker unable to retrieve single-hour videos"
                    + " when building day-long video from hour-long videos.");
            return false;
        }
        
        //Validate returned instances in loop.
        for (int i = 0; i < returned.getNumberOfValuesReturned(); i++) {
            MP4Instance hourInstance = (MP4Instance)returned
                    .getResourceInstances().get(i);
        
            //Get file to copy and check if it is valid.
            File instanceDataFile = getAndValidateHourLongFile(hourInstance);
            if (instanceDataFile == null) {
                return false;
            }
            
            //Add file to input list.
            inputList.add(instanceDataFile);
        }
        
        //Do concatenation and check if it worked.
        if(!concatenateVideoFiles(inputList, standardQualityTempFile)) {
            logError(
                    "Day-long video maker unable to perform concatenation"
                    + " when building day-long video from hour-long videos.");
            return false;
        }
        
        //Trim video, which must be done to remove extra milliseconds added by
        //ffmpeg processes.  If this is not done, day long bideos grow too long 
        //over time.
        if(!trimTempVideo()) {
            logError(
                    "Day-long video maker unable to trim video when building"
                    + " day-long video from hour-long videos.");
            return false;
        }
        
        //Make low-quality copy if set to do so and check if successful.
        if (makingLowQuality) {
            if (!makeLowQualityCopy()) {
                logError(
                        "Day-long video maker unable to create low-quality copy"
                        + " of video when building day-long video from hour-"
                        + "long videos.");
                return false;
            }
        }

        //Permanantly save vidros and check if save was successful.
        if(!saveDataPermanantly()) {
            logError(
                    "Day-long video maker unable to permanently save videos"
                    + " when building day-long video from hour-long videos.");
            return false;
        }
        
        //All steps worked, so return true.
        return true;
    }
    
    /**
     * This function will create day-long videos based on the information
     * provided to the constructor.  Both standard-quality and low-quality 
     * copies will be made with email error notification to all administrators 
     * if the day-long videos cannot be made.
     * @return True if the videos were made; false otherwise.
     */
    public boolean createDayLongVideo() {        
        //Delete any temporary files left over from previous attempts to make 
        //doy-long videos on behalf of the given calling code for the given
        //resource on the given day.
        if (standardQualityTempFile.exists()) {
            for (File file : buildDirectory.listFiles()) {
                if (file.isFile() && file.getName().startsWith(tempFilePrefix)) {
                    file.delete();
                }
            }
        }
        
        //Return variable; assume success until email is sent.
        boolean isSuccessful = true;
        
        //Check if videos should only be one hour long.
        if (hoursInLongVideo == 1) {
            //Try to copy hour-long video and make a low-quality copy.
            if (!copySingleHourVideo()) {
                //If videos were not made, send emails and change result.
                sendErrorMessage();
                isSuccessful = false;
            }
        } else {    //The desired video is over one hour long.
            //Try to add to existing day-long video and make a low-quality copy.
            if (!addHourToExistingVideo()) {
                //If that does not work, try to build day-long video from the
                //hour-long videos and make a low-quality copy.
                if (!makeDayLongVideoFromScratch()) {
                    //If videeos were not made, send emails and change result.
                    sendErrorMessage();
                    isSuccessful = false;
                }
            }
        }
      
        //Make sure temporary video are deleted.
        clearTempFiles();
        
        //Return result.
        return isSuccessful;
    }
}
