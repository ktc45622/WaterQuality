package weather.serverside.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import weather.MovieMakerSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.resource.*;
import weather.common.dbms.DBMSSystemManager;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.*;
import weather.serverside.FFMPEG.MP4CopyMaker;
import weather.serverside.FFMPEG.VideoLengthCalculator;
import weather.serverside.applicationimpl.MovieMakerSystemImpl;
import weather.serverside.utilities.ResourceCollectionTimeUtility;

/**
 * This helper class will provide common file and folder operations on the local
 * file system.
 *
 *
 * @author Bloomsburg University Software Engineering
 * @author David Lusby (2008)
 * @author Bill Katsak (2008)
 * @author Ryan Kelly (2010)
 * @author Thomas Crouse (2012)
 * @version Spring 2012
 */
public class StorageManagement {
    // Storage log.
    private static WeatherTracer log = WeatherTracer.getStorageLog();
    
    //Saved properties from database.
    int hourVideoLength = Integer.parseInt(PropertyManager
            .getGeneralProperty("MOVIE_LENGTH"));
    String videoCodec = PropertyManager.getServerProperty("FFMPEG_VCODEC");
    
    private String fileSystemRoot;
    private DBMSSystemManager dbms = null;
    private MovieMakerSystem movieMakerSystem = null;
    
    
    /**
     * Constructor.
     * 
     * @param dbms The database management system.
     * @param fileSystemRoot The starting point for all file storage.
     */
    public StorageManagement(DBMSSystemManager dbms, String fileSystemRoot) {
        setDBMS(dbms);
        setFileSystemRoot(fileSystemRoot);
        movieMakerSystem = new MovieMakerSystemImpl();
    }

    /**
     * Returns a resource given its resource number.
     * 
     * @param resourceID The resource number.
     * @return The <code>Resource</code> with that number or null if there is 
     * none. 
     */
    public Resource getResource(int resourceID) {
        Resource r = dbms.getResourceManager()
                .getWeatherResourceByNumber(resourceID);
        return r;
    }
    
    /**
     * Determines what the server-side path to a file holding a given piece of 
     * data, which can be loaded into a <code>ResourceInstance</code>, should 
     * be.  The path will not contain the file name.
     * 
     * @param resourceID The number of the <code>Resource</code> providing the
     * data.
     * @param time A <code>Date</code> providing the time of the data.
     * @param isMovie Whether on not a movie is requested.  The will be ignored
     * if the indicated <code>Resource</code> does not provide movies.
     * @return The file path to the data with a trailing separator.
     */
    private String getFilePathForData(int resourceID, Date time, 
            boolean isMovie) {
        // Get resource.
        Resource r = getResource(resourceID);
        
        // Set up time tools.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(r.getTimeZone().getTimeZone());
        calendar.setTime(time);
        
        // Start making path.
        StringBuilder path = new StringBuilder(fileSystemRoot);
        path.append(File.separator).append(r.getStorageFolderName());
        path.append(File.separator).append(calendar.get(Calendar.YEAR));
        path.append(File.separator).append(calendar.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(calendar.get(Calendar.DAY_OF_MONTH));


        // Determine whether we need a movie path.
        if (isMovie) {
            path.append(File.separator).append("movies").append(File.separator);
        } else {
            path.append(File.separator);
        }
        
        // Return result.
        return path.toString();
    }
    
    /**
     * Determines what the server-side name for a file holding a given piece of 
     * data, which can be loaded into a <code>ResourceInstance</code>, should 
     * be.  Returns the file name without the file path.
     * 
     * @param resourceID The number of the <code>Resource</code> providing the
     * data.
     * @param time A <code>Date</code> providing the time of the data.
     * @param extension The file extension that will finish the path.
     * @return The server-side file path.  This will be ignored if the file name
     * in not requested,
     * @param addTimeToDate Flag used to indicate whether to date included in
     * the file name should also include the time. Note that this flag will have
     * no effect if the file name is that of a weather station file, as those
     * contain no dates. The two possible date formats are as follows:
     *      True:  yyyyMMdd-HHmmss
     *      False: _MM-dd-yyyy
     * @param isLowQuality True if the requested data is low-quality video; 
     * False otherwise.
     * @return The file name without the file path.
     */
    private String getFileNameForData(int resourceID, Date time, 
            String extension, boolean addTimeToDate, boolean isLowQuality) {
        // Get resource.
        Resource r = getResource(resourceID);
        
        // Set up time tools.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(r.getTimeZone().getTimeZone());
        calendar.setTime(time);
        DateFormat df;
        if (addTimeToDate) {
            df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        } else {
            df = new SimpleDateFormat("_MM-dd-yyyy");
        }
        df.setTimeZone(r.getTimeZone().getTimeZone());
        
        // Start making file name.
        StringBuilder name = new StringBuilder(r.getStorageFolderName());

        // Only append the timestamp if we are NOT saving a Weather Station file.
        if (r.getResourceType() != WeatherResourceType.WeatherStationValues) {
            name.append(df.format(time));
        }
        
        // Add notation for for low-quality video if requested.
        if (isLowQuality) {
            name.append("_low");
        }
        
        // Add extension.
        name.append(extension);
        
        // Return result.
        return name.toString();
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
        //Find hour-long video.  There are several places to look and one will
        //always succeed.
        if(!isFullDay) {
            GregorianCalendar timeCal = new GregorianCalendar();
            timeCal.setTimeInMillis(time.getTime());
            Resource resource = getResource(resourceID);
            return getMovieForTime(resource, timeCal, ResourceFileFormatType
                    .mp4);
        }
        
        //If the code gets here a full-day path is requsted.  There can only be
        //one path, which is assumed to be correct as the dete is take feom an
        //existing video instance.
        String path = getFilePathForData(resourceID, time, true);
        String name = getFileNameForData(resourceID, time, ".mp4", false, 
                isLowQuality);
        return new File(path + name);
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
        //Get the total needed and a counter
        int totalNeeded = Integer.parseInt(PropertyManager.
                getServerProperty("OLD_VIDEO_MINIMUM"));
        int totalFound = 0;
        
        String testingPath = getFilePathForData(resourceID, time, true);
        File testingFolder = new File(testingPath);
        if (!testingFolder.exists()) {
            //There is no folder to check for hour-long moovies.
            return false;
        }
        
        //Test videos for the correct length and type.
        for (File file : testingFolder.listFiles()) {
            if (file.getAbsolutePath().endsWith(".mp4")) {
                if (VideoLengthCalculator.getLengthOfMP4FileInSeconds(file) 
                        == Integer.parseInt(PropertyManager
                        .getGeneralProperty("MOVIE_LENGTH"))) {
                        totalFound++;
                        //Stop if enough have been found.
                        if (totalFound == totalNeeded) {
                            return true;
                        }
                }
            }
        }
        
        //Not enough hour-long MP4 videos found.
        return false;
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
        String testingPath = getFilePathForData(resourceID, time, true);
        File testingFolder = new File(testingPath);
        if (!testingFolder.exists()) {
            //There is no folder to check for hour-long moovies.
            return false;
        }
        
        //Test videos for the correct length and type.
        for (File aviFile : testingFolder.listFiles()) {
            if (aviFile.getAbsolutePath().endsWith(".avi")) {
                if (VideoLengthCalculator.getLengthOfMP4FileInSeconds(aviFile) 
                        == Integer.parseInt(PropertyManager
                        .getGeneralProperty("MOVIE_LENGTH"))) {
                        
                    //Try to convert file.
                    String aviPath = aviFile.getAbsolutePath();
                    String mp4Path = aviPath.substring(0, 
                            aviPath.lastIndexOf(".")) + ".mp4";
                    File mp4File = new File(mp4Path);
                    MP4CopyMaker.makeMP4Copy(aviFile, mp4File);
                }
            }
        }
        
        //See if enough hour-long MP4 videos were made.
        return hasHourLongMP4Videos(resourceID, time);
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
        String testingPath = getFilePathForData(resourceID, time, true);
        File testingFolder = new File(testingPath);
        if (!testingFolder.exists()) {
            //There is no folder to check for hour-long moovies.
            return false;
        }

        //Make list of all videos to convert.
        ArrayList<File> movFiles = new ArrayList<>();
        for (File file : testingFolder.listFiles()) {
            if (file.getAbsolutePath().endsWith(".mov")) {
                movFiles.add(file);
            }
        }

        //Assume all copies and deletes are successful; change later if not.
        boolean success = true;

        //Copy and delete files.
        for (File movFile : movFiles) {
            //Handle videos of the wrong length.
            if (VideoLengthCalculator.getLengthOfMP4FileInSeconds(movFile)
                    != Integer.parseInt(PropertyManager
                            .getGeneralProperty("MOVIE_LENGTH"))) {
                if (!movFile.delete()) {
                    success = false;    //Failed operation.
                }
                continue;
            }
            
            //Try to convert file.
            String movPath = movFile.getAbsolutePath();
            String mp4Path = movPath.substring(0,
                    movPath.lastIndexOf(".")) + ".mp4";
            File mp4File = new File(mp4Path);
            boolean copyResult = MP4CopyMaker.makeMP4Copy(movFile, mp4File);
            
            //Try to delete MOV file if copying was successful.
            if (!copyResult) {
                success = false;    //Failed operation.
            } else {
                if (!movFile.delete()) {
                    success = false;    //Failed operation.
                }
            }
        }

        //Return success state.
        return success;
    }
    
    /**
     * Copies the given default file to the file system specified by the current 
     * file system root if it is not already there.  The given file must already 
     * be in the portion of the project's file structure the will be 
     * distributed.
     *
     * @param fileName The name of the given file.
     * @return True if all required copying was successful; false otherwise.
     */
    private boolean saveDefaultVideo(String fileName) {
        //Specify target file.
        String targetPath = fileSystemRoot + File.separator + "Generic Movies";
        File targetFile = new File(targetPath + File.separator + fileName);
        
        //Copy file if needed.
        if (!targetFile.exists()) {
            try {
                Files.copy((new File(fileName).toPath()), targetFile.toPath());
            } catch (IOException ex) {
                return false;
            }
        } else {
            Debug.println(fileName + " exists.");
        }
        
        //If code gets here, there was no ploblem.
        return true;
    }
    
    /**
     * Copies any missing default files to the file system specified by the
     * current file system root. The source files must already be in the portion
     * of the project's file structure the will be distributed.
     *
     * @return True if all required copying was successful; false otherwise.
     */
    public boolean saveDefaultVideos() {
        //Spicify target folder.
        String targetPath = fileSystemRoot + File.separator + "Generic Movies";
        File targetFolder = new File(targetPath);
        
        //Make sure target folder exists.
        if(!targetFolder.exists()) {
            //Retun false if folder cannot be made.
            if (!targetFolder.mkdirs()) {
                return false;
            }
        }
        
        //Specify names of files.
        String hourLongMP4 = "NoData.mp4"; 
        String hourLongAVI = "NoData.avi";
        String dayLongMP4 = "GenericDayVideo.mp4";
        String dayLongLowQualityMP4 = "GenericDayVideo_low.mp4";
        
        //Assume success, but change to false with first failure.
        boolean success = true;
        
        //Try to copy files.
        if (!saveDefaultVideo(hourLongMP4)) {
            success = false;
        }
        if (!saveDefaultVideo(hourLongAVI)) {
            success = false;
        }
        if (!saveDefaultVideo(dayLongMP4)) {
            success = false;
        }
        if (!saveDefaultVideo(dayLongLowQualityMP4)) {
            success = false;
        }
        
        //Return success state.
        return success;
    }

    /**
     * Saves a new "No Data" MP4 video for the storage system and returns and 
     * backs up this file.
     *
     * @param image The image to be made into a video.
     * @return A file called "NoData.mp4" that was stored in the Generic Movies
     * folder.
     * @throws weather.common.utilities.WeatherException if the files cannot be
     * created.
     */
    public File createAndStoreNoDataMP4File(ResourceInstance image) throws WeatherException {
        if (!(image instanceof ImageInstance)) {
            throw new WeatherException(6006, "The provided image was not of type ImageInstance.");
        }

        StringBuilder path = new StringBuilder(fileSystemRoot);
        path.append(File.separator).append("Generic Movies");
        path.append(File.separator).append("NoData.mp4");
        File file = new File(path.toString());

        ArrayList<ResourceInstance> images = new ArrayList<>();
        images.add(image);

        ArrayList<AVIInstance> movieList = movieMakerSystem.makeMovies(null, 
                images, null, hourVideoLength, videoCodec);
        
        // Check if data was returned from movie maker system.
        if (movieList.isEmpty()) {
            throw new WeatherException(6010, "No videos were returned.");
        }

        file.getParentFile().mkdirs();
        
        // MP4 movie is at position 1 of list. 
        AVIInstance movieInstance = movieList.get(1);
        movieInstance.writeFile(file);
        
        // Backup file.
        StringBuilder backupPath = new StringBuilder(fileSystemRoot);
        backupPath.append(File.separator).append("Generic Movies");
        backupPath.append(File.separator).append("Backup"); 
        backupPath.append(File.separator).append("NoData");         
        SimpleDateFormat format = new SimpleDateFormat("-M-dd-yyyy");
        backupPath.append(format.format(Calendar.getInstance().getTime()));
        backupPath.append(".mp4");
        
        File backupFile = new File(backupPath.toString());

        backupFile.getParentFile().mkdirs();
        movieInstance.writeFile(backupFile); 

        return file;
    }

    /**
     * Saves a new "No Data" AVI video for the storage system and returns and 
     * backs up the file.
     *
     * @param image The image to be made into a video.
     * @return A file called "NoData.avi" that was stored in the Generic Movies
     * folder.
     * @throws weather.common.utilities.WeatherException if the files cannot be
     * created.
     */
    public File createAndStoreNoDataAVIFile(ResourceInstance image) throws WeatherException {
        if (!(image instanceof ImageInstance)) {
            throw new WeatherException(6006, "The provided image was not of type ImageInstance.");
        }

        StringBuilder path = new StringBuilder(fileSystemRoot);
        path.append(File.separator).append("Generic Movies");
        path.append(File.separator).append("NoData.avi");
        File file = new File(path.toString());

        ArrayList<ResourceInstance> images = new ArrayList<>();
        images.add(image);

        ArrayList<AVIInstance> movieList = movieMakerSystem.makeMovies(null, 
                images, null, hourVideoLength, videoCodec);
        
        // Check if data was returned from movie maker system.
        if (movieList.isEmpty()) {
            throw new WeatherException(6010, "No videos were returned.");
        }

        file.getParentFile().mkdirs();
        
        // AVI movie is at position 0 of list. 
        AVIInstance movieInstance = movieList.get(0);
        movieInstance.writeFile(file);
        
        // Backup file.
        StringBuilder backupPath = new StringBuilder(fileSystemRoot);
        backupPath.append(File.separator).append("Generic Movies");
        backupPath.append(File.separator).append("Backup"); 
        backupPath.append(File.separator).append("NoData");         
        SimpleDateFormat format = new SimpleDateFormat("-M-dd-yyyy");
        backupPath.append(format.format(Calendar.getInstance().getTime()));
        backupPath.append(".avi");
        
        File backupFile = new File(backupPath.toString());

        backupFile.getParentFile().mkdirs();
        movieInstance.writeFile(backupFile); 

        return file;
    }
    
    /**
     * Saves a new default day or night video for the storage system and returns
     * and backs up this file.
     *
     * @param resourceID The ID number of the resource we are storing a new
     * video for.
     * @param commandType The storage command type which must be either
     * STORE_DEFAULT_DAY or STORE_DEFAULT_NIGHT.
     * @param image The image to be used to make the video in a 
     * <code>ResourceInstance</code>.
     * @param movieType The file type of the video to be created, either AVI or
     * MP4.
     * @return A file to which the default video was written.
     * @throws weather.common.utilities.WeatherException if the files cannot be
     * created.
     */
    public File createAndStoreDefaultMovieFile(int resourceID,
            StorageCommandType commandType, ResourceInstance image, 
            ResourceFileFormatType movieType) throws WeatherException {
        if (!(image instanceof ImageInstance)) {
            throw new WeatherException(6006, 
                    "The provided image was not of type ImageInstance.");
        }
        if (movieType != ResourceFileFormatType.avi 
                && movieType != ResourceFileFormatType.mp4) {
            throw new WeatherException(6008, 
                    "The provided format was not AVI or MP4.");
        }
        if (commandType != StorageCommandType.STORE_DEFAULT_DAY 
                &&commandType != StorageCommandType.STORE_DEFAULT_NIGHT) {
            throw new WeatherException(6009, 
                    "The provided storage command was not to store a default"
                    + " day or night video.");
        }
        
        /**
         * Convert image to match that provided by resource.
         */
        
        //Get image from ResourceInstance.
        BufferedImage bImage;
        try {
            bImage = (BufferedImage)(((ImageInstance)image).getImage());
        } //IndexOutOfBoundsException comes deep from within getImage()
        //It happens when an image is bad.
        catch (IndexOutOfBoundsException ex) {
            throw new WeatherException(4002,
                "StorageManagement could not retrieve image (" + ex + ").");
        }

        if (bImage == null) {
            throw new WeatherException(4002,
                "StorageManagement could not retrieve image (null found).");
        }

        //Get new image extension.
        Resource resource = getResource(resourceID);
        String formatAsString;
        if (resource.getFormat() == ResourceFileFormatType.jpeg) {
            formatAsString = "jpg";
        } else if (resource.getFormat() == ResourceFileFormatType.gif) {
            formatAsString = "gif";
        } else { //Only other option is png.
            formatAsString = "png";
        }
        
        //Set up path for converted image, start with part common to image and
        //final result video.
        StringBuilder commonPath = new StringBuilder(fileSystemRoot);
        commonPath.append(File.separator).append(resource
                .getStorageFolderName());
        commonPath.append(File.separator).append("Generic Movies");
        if (commandType == StorageCommandType.STORE_DEFAULT_DAY) {
            commonPath.append(File.separator).append("DefaultDay.");
        } else if (commandType == StorageCommandType.STORE_DEFAULT_NIGHT) {
            commonPath.append(File.separator).append("DefaultNight.");
        }
        
        String tempImagePath = commonPath.toString() + formatAsString;
        File tempImageFile = new File(tempImagePath);

        //Write the temporary file.
        tempImageFile.getParentFile().mkdirs();
        try {
            ImageIO.write(bImage, formatAsString, tempImageFile);

        } catch (IOException | IllegalArgumentException ex) {
            throw new WeatherException(4105,
                "StorageManagement could not change the format of an image (" 
                        + ex + ").");
        }
        
        //Make movie from temporary image.
        File movieFile;
        File backupFile;
        AVIInstance movieInstance;
        
        ArrayList<ResourceInstance> images = new ArrayList<>();
        ResourceInstance convertedInstance = new ImageInstance();
        convertedInstance.readFile(tempImageFile);
        images.add(convertedInstance);

        ArrayList<AVIInstance> movieList = movieMakerSystem
                .makeMovies(resource, images, null, hourVideoLength, 
                videoCodec);
        
        //Check if data was returned from movie maker system.
        if (movieList.isEmpty()) {
            throw new WeatherException(6010, "No videos were returned.");
        }

        //Make path to movie file.
        String moviePath = commonPath.toString() + movieType;
        
        movieFile = new File(moviePath);

        //Get correct instance from list.
        if (movieType == ResourceFileFormatType.avi) {
            movieInstance = movieList.get(0);
        } else {
            movieInstance = movieList.get(1);
        }

        movieFile.getParentFile().mkdirs();
        movieInstance.writeFile(movieFile);
        
        //Backup file.
        StringBuilder backupPath = new StringBuilder(fileSystemRoot);
        backupPath.append(File.separator).append(resource
                .getStorageFolderName());
        backupPath.append(File.separator).append("Generic Movies");
        
        backupPath.append(File.separator).append("Backup"); 
        
        if (commandType == StorageCommandType.STORE_DEFAULT_DAY) {
            backupPath.append(File.separator).append("DefaultDay");
        } else if (commandType == StorageCommandType.STORE_DEFAULT_NIGHT) {
            backupPath.append(File.separator).append("DefaultNight");
        }
        
        SimpleDateFormat format = new SimpleDateFormat("-M-dd-yyyy");
        backupPath.append(format.format(Calendar.getInstance().getTime()));
        
        backupPath.append(".").append(movieType);
        backupFile = new File(backupPath.toString());

        backupFile.getParentFile().mkdirs();
        movieInstance.writeFile(backupFile);
        
        //Delete temporary file.
        tempImageFile.delete();

        return movieFile;
    }

    /**
     * Returns the file path path to the location in the file system where a new
     * file should be stored, including the name of the file.
     *
     * @param resourceID The number resource for which data is being stored.
     * @param time The time for which data is being stored.
     * @param movieType Used to indicate either the avi or mp4 file type (leave 
     * as null if we are not storing a movie).
     * @param wantPathToFullDayMovie True if storing a day-long movie; False 
     * otherwise.
     * @param isLowQuality True if the requested data is low-quality video; 
     * False otherwise.
     * @return a new file in the correct folder, with the correct time, and the
     * correct extension from the file system.
     */
    public File getNewStorageFile(int resourceID, Date time, 
            ResourceFileFormatType movieType, boolean wantPathToFullDayMovie,
            boolean isLowQuality) {
        boolean isMovie = movieType != null;
        
        // Get resource.
        Resource r = getResource(resourceID);
        
        // Get the proper file extension.
        String extension;
        if (isMovie) {
            if (movieType == ResourceFileFormatType.avi) {
                extension = ".avi";
            } else {
                extension = ".mp4";
            }
        } else {
            switch (r.getFormat()) {
                case jpeg:
                    extension = ".jpg";
                    break;
                case gif:
                    extension = ".gif";
                    break;
                case mp4:
                    extension = ".mp4";
                    break;
                case png:
                    extension = ".png";
                    break;
                case mjpg:
                    extension = ".mjpg";
                    break;
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
        }
        
        // Get path string.
        String path = getFilePathForData(resourceID, time, isMovie)
                + getFileNameForData(resourceID, time, extension, 
                !wantPathToFullDayMovie, isLowQuality);

        // Build a file object from the String path and return it.
        return new File(path);
    }
    
    /**
     * Determines if a resource that cannot show a movie should show the default
     * day or night video for a given hour.
     * @param hourCalendar A calendar holding the hour to be checked.
     * @param resource The resource to be checked.
     * @return True if the daytime movie should be used, and false if the nighttime
     * movie should be used
     */
    private boolean isDaytimeHour(GregorianCalendar hourCalendar, Resource resource) {
        //Use sunrise/sunset algorithm if resoure collects images full time.
        if(resource.getCollectionSpan() == ResourceCollectionSpan.FullTime) {
            return ResourceCollectionTimeUtility.getCalculator(resource, hourCalendar).isDaytime();
        }
        
        //If execution gets here, the collection is not full time, so use 
        //collection hours.
        
        //Determine collection span
        GregorianCalendar spanStart =
            ResourceCollectionTimeUtility.getCollectionStartTime(resource, hourCalendar);
        GregorianCalendar spanStop =
            ResourceCollectionTimeUtility.getCollectionStopTime(resource, hourCalendar);
        
        //Get hours to compare.
        int rangeStart = spanStart.get(GregorianCalendar.HOUR_OF_DAY);
        int rangeEnd = spanStop.get(GregorianCalendar.HOUR_OF_DAY);
        int thisHour = hourCalendar.get(GregorianCalendar.HOUR_OF_DAY);
        return thisHour >= rangeStart && thisHour <= rangeEnd;
    }

    /**
     * Gets an hour-long movie out of the storage system for a given resource at 
     * a given time.
     *
     * If the movie for that time does not exist where it should, the time is
     * compared to the sunrise/sunset time for the day.
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
     * @param type The type of movie to be returned.
     *
     * @return The movie if it exists. If it doesn't exist, and it's nighttime,
     * a nighttime movie. If it's daytime, return the daytime movie. The next 
     * option is the storage system's default NoData movie. The final option is
     * null.
     */
    private File getMovieForTime(Resource resource, GregorianCalendar calendar, 
            ResourceFileFormatType type) {
        String ext;
        if (type == ResourceFileFormatType.avi) {
            ext = ".avi";
        } else {
            ext = ".mp4";
        }
        
        // Get file path.
        String filePath = getFilePathForData(resource.getResourceNumber(), 
                new Date(calendar.getTimeInMillis()), true)
                + getFileNameForData(resource.getResourceNumber(), 
                new Date(calendar.getTimeInMillis()), ext, true, false);
        
        File f = new File(filePath);

        Debug.println("Tested path: " + f.getAbsolutePath());
        // If file exists, return it.
        if (f.exists()) {
            Debug.println("File Found.");
            return f;
        }
        
        //Couldn't find video, so look for default day or night video.
        StringBuffer path;
        if (isDaytimeHour(calendar, resource)) {
            path = new StringBuffer(fileSystemRoot + File.separator + resource.getStorageFolderName());
            path.append(File.separator + "Generic Movies" + File.separator + "DefaultDay");
            path.append(ext);
        } // set the path to a default daytime movie for the resource.
        else {
            path = new StringBuffer(fileSystemRoot + File.separator + resource.getStorageFolderName());
            path.append(File.separator + "Generic Movies" + File.separator + "DefaultNight");
            path.append(ext);
        }

        f = new File(path.toString());
        Debug.println("Path constructed is " + path.toString());
        if (f.exists()) {
            Debug.println("In getMovieForTime -- Returning  " + path.toString());
            return f;
        } else {
            //No day or night movie found, so look for default "No Data" image.
            path = new StringBuffer(fileSystemRoot);
            path.append(File.separator + "Generic Movies");
            path.append(File.separator + "NoData");
            path.append(ext);
            f = new File(path.toString());
            Debug.print("NOT FOUND - looking for NoData");
            Debug.println("Path constructed is " + path.toString());
            if (f.exists()) {
                Debug.println("In getMovieForTime -- Returning  " + path.toString());
                return f;
            } else {
                //No default found.
                Debug.println("NoData not found - Returning NULL");
                return null;
            }
        }
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
        String ext = ".mp4";
        
        //Look for default daytime or nighttime video.
        File f;
        StringBuffer path;
        if (isDaytimeHour(calendar, resource)) {
            path = new StringBuffer(fileSystemRoot + File.separator + resource.getStorageFolderName());
            path.append(File.separator + "Generic Movies" + File.separator + "DefaultDay");
            path.append(ext);
        } // set the path to a default daytime movie for the resource.
        else {
            path = new StringBuffer(fileSystemRoot + File.separator + resource.getStorageFolderName());
            path.append(File.separator + "Generic Movies" + File.separator + "DefaultNight");
            path.append(ext);
        }

        f = new File(path.toString());
        Debug.println("Path constructed is " + path.toString());
        if (f.exists()) {
            Debug.println("In getMovieForTime -- Returning  " + path.toString());
            return f;
        } else {
            //No day or night movie found, so look for default "No Data" image.
            path = new StringBuffer(fileSystemRoot);
            path.append(File.separator + "Generic Movies");
            path.append(File.separator + "NoData");
            path.append(ext);
            f = new File(path.toString());
            Debug.print("NOT FOUND - looking for NoData");
            Debug.println("Path constructed is " + path.toString());
            if (f.exists()) {
                Debug.println("In getMovieForTime -- Returning  " + path.toString());
                return f;
            } else {
                //No default found.
                Debug.println("NoData not found - Returning NULL");
                return null;
            }
        }
    }
    
    /**
     * Returns the default day-long video for a given resource.  If the resource
     * has its own day-long default, that is returned.  Otherwise, The system
     * day-long default is returned.  A null value is returned as a last resort.
     * 
     * @param resource The resource whose default movie we are searching for.
     * @param isLowQuality True if a low-quality version is requested; False
     * otherwise.
     * 
     * @return The default day-long video for a given resource. If the resource
     * has its own day-long default, that is returned. Otherwise, The system
     * day-long default is returned.  A null value is returned as a last resort.
     */
    private File getDefaultDayLongMovie(Resource resource, 
            boolean isLowQuality) {
        //First, look for video in the resource's directory.
        StringBuilder path = new StringBuilder(fileSystemRoot);
        path.append(File.separator).append(resource.getStorageFolderName());  
        path.append(File.separator).append("Generic Movies");
        path.append(File.separator).append("GenericDayVideo");
        if (isLowQuality) {
            path.append("_low.mp4");
        } else {
            path.append(".mp4");
        }
        File f = new File(path.toString());
        Debug.println("getDefaultDayLongMovie: Testing path: "
            + path.toString());
        if (f.exists()) {
            Debug.println(path.toString() + " exists; returning it.");
            return f;
        } else {
            Debug.println(path.toString() + " does not exist.");
        }
        
        //File did not exist; go to top-level Generic Movies folder.
        path = new StringBuilder(fileSystemRoot);  
        path.append(File.separator).append("Generic Movies");
        path.append(File.separator).append("GenericDayVideo");
        if (isLowQuality) {
            path.append("_low.mp4");
        } else {
            path.append(".mp4");
        }
        f = new File(path.toString());
        Debug.println("getDefaultDayLongMovie: Testing path: "
            + path.toString());
        if (f.exists()) {
            Debug.println(path.toString() + " exists; returning it.");
            return f;
        } else {
            Debug.println(path.toString() + " does not exist.");
        }
        
        //No file found; return null.
        Debug.println("getDefaultDayLongMovie: returning NULL.");
        return null;
    }
    
    /**
     * Helper function to return an appropriate number of images taken from
     * a given range given that the maximum number of images is provided.
     * @param unfilteredList The list from which the images are to be taken.
     * @param resourceRangeStart The beginning of the range from which images are
     * to be taken.
     * @param resourceRangeEnd The end of the range from which images are
     * to be taken.
     * @param max The maximum number of images to be retrieved.
     * @return A filtered list of images.
     */
    private Vector<File> filterImages(Vector<File> unfilteredList, 
            Calendar resourceRangeStart, Calendar resourceRangeEnd, long max) {
        Debug.println("In filterImages...");
        
        //Find ends of range. (This function always gets lists that start and
        //end on the hour, so some images may be outside the range.)
        int startIndex = findStartingIndex(unfilteredList, resourceRangeStart);
        int stopIndex = findStopingIndex(unfilteredList, resourceRangeEnd);
        
        //Vector to return result
        Vector<File> filteredList = new Vector<>();

        //calculate a stepSize first
        float stepSize = ((float)(stopIndex - startIndex + 1)) / max;
        //If this stepSize is larger than 1, the image will be picked after one stepSize,
        //otherwise it means the max number of image per hour is larger than the total 
        //number of images taken per hour, so we pick all the images, which requires
        //the changing of variables.
        if (stepSize < 1) {
            stepSize = 1;
            max = unfilteredList.size();
        }
        //Now, get images.
        float unroundedIndex = startIndex;
        int index = Math.round(unroundedIndex);
        int count = 0;
        while (count < max && index <= stopIndex) {
            File addFile = unfilteredList.get(index);
            filteredList.add(addFile);
            Debug.println("Selecting index: " + index
                    + " Path: " + addFile.getAbsolutePath());
            unroundedIndex += stepSize;
            index = Math.round(unroundedIndex);
            count++;
        }
        Debug.println("Length of filteredList in filterImages is " + filteredList.size());
        Debug.println("filterImages returning...");
        return filteredList;
    }
    
    /**
     * Finds the number of milliseconds from a given hour that is within a given
     * range, i. e. the length of that hour's segment.
     * @param hour Calendar holding the hour to be checked.
     * @param rangeStart Calendar holding the start of the range.
     * @param rangeEnd Calendar holding the end of the range.
     * @return The length of that hour's segment.
     */
    private long findMillisForSegment(Calendar hour, Calendar rangeStart,
            Calendar rangeEnd) {
        //Clone start of hour so it can be changed without affecting the parameter.
        Calendar hourStart = (Calendar) hour.clone();
        //Find end of hour.
        Calendar hourEnd = (Calendar) hour.clone();
        hourEnd.add(Calendar.HOUR, 1);
        hourEnd.add(Calendar.MILLISECOND, -1);  //Last millisecond before next hour.

        //Change start if neccessary.
        if (rangeStart.getTimeInMillis() > hourStart.getTimeInMillis()) {
            hourStart.setTimeInMillis(rangeStart.getTimeInMillis());
        }

        //Change start if neccessary.
        if (rangeEnd.getTimeInMillis() < hourEnd.getTimeInMillis()) {
            hourEnd.setTimeInMillis(rangeEnd.getTimeInMillis());
        }

        //Compute Result.
        return hourEnd.getTimeInMillis() - hourStart.getTimeInMillis() + 1;
    }

    /**
     * Retrieves a file vector from the storage system for the resource
     * requested.
     *
     * @param resourceRequest The resource instances requested - that we want
     * files for.
     * @return A file vector of files that match the resourceRequest.
     */
    public Vector<File> getImageFiles(ResourceInstancesRequested resourceRequest) { 
        //Vector to hold returned list.
        Vector<File> returnList = new Vector<>();
        
        //Get needed info from range and resource that comes from request.
        ResourceRange range = resourceRequest.getResourceRange();
        Resource resource = getResource(resourceRequest.getResourceID());

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTimeZone(resource.getTimeZone().getTimeZone());
        startCalendar.setTimeInMillis(range.getStartTime().getTime());

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTimeZone(resource.getTimeZone().getTimeZone());
        endCalendar.setTimeInMillis(range.getStopTime().getTime());
        
        /*Needed to prorate filtering if all images are not returned.*/
        
        //Time for which images are requested.
        long lengthOfRange = range.getStopTime().getTime() 
                - range.getStartTime().getTime() + 1;
        
        //Time for which we have images.
        long totalVideosFoundTime = 0;
        
        Debug.println("In getImageFiles for resource " + resource.getName()
                + " from time: " + CalendarFormatter.formatWithTimeZone(startCalendar) 
                + " to " + CalendarFormatter.formatWithTimeZone(endCalendar) + ".");
        
        //Make calendar for below for loop.
        GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
        //Set loopCalendar to start of hour.
        loopCalendar.set(Calendar.MINUTE, 0);
        loopCalendar.set(Calendar.SECOND, 0);
        loopCalendar.set(Calendar.MILLISECOND, 0);
        Debug.println("Initial loop calendar time: " + CalendarFormatter.formatTime(loopCalendar));
        
        //Filter will retrieve all images for the current hour.
        ResourceTimeFilter myFilter;
        
        //Vector to hold all images taken from file system.
        Vector<File> fullFileList = new Vector<>(); 
        
        for (; loopCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.HOUR, 1)) {
            Debug.println("Current loop calendar time: " + CalendarFormatter.formatTime(loopCalendar));
            //Make path to folder with possible picture.
            String directoryPath = this.getFilePathForData(resource
                    .getResourceNumber(), new Date(loopCalendar
                    .getTimeInMillis()), false);
            
            //Make directory from string.
            File directory = new File(directoryPath);
            
            String filenames[];  //to store result of filter.
            Debug.println("Looking to see if file " + directory.getAbsolutePath() +" exists");
            if (directory.exists()) {    //found directory
                //Look for file
                myFilter = new ResourceTimeFilter(loopCalendar.get(Calendar.HOUR_OF_DAY));
                Debug.println("File Filter was " + myFilter);
                filenames = directory.list(myFilter);
                if (filenames == null) {
                    Debug.println("Filenames is null");
                } else if (filenames.length > 0) {
                    long lengthOfSegment = findMillisForSegment(loopCalendar, startCalendar, endCalendar);
                    Debug.println("Segment length: " + lengthOfSegment);
                    totalVideosFoundTime += lengthOfSegment;
                    Debug.println("Current total length: " + totalVideosFoundTime);
                    Debug.println("Range length: " + lengthOfRange);
                    for(String name : filenames){
                        File addFile = new File(directoryPath + name);
                        fullFileList.add(addFile);
                    }
                } else {
                    Debug.println("No files returned.");
                }
            }
        }
        Debug.println("Final loop calendar time: " + CalendarFormatter.formatTime(loopCalendar));
        
        /*This code does the filtering*/
        double rangeFraction;   //to hold the faction of the range for which images were found.
        rangeFraction = ((double)totalVideosFoundTime) / lengthOfRange;
        Debug.println("Range fraction: " + rangeFraction);
        
        long max = Math.round(rangeFraction * resourceRequest.getNumberOfResourceInstancesRequested());
        Debug.println("Images to be sought from filter: " + max);
        
        returnList = filterImages(fullFileList, startCalendar, endCalendar, max);
        
        Debug.println("Length of returnList in getImageFiles is " + returnList.size());
        for (File file : returnList) {
            log.finest("getImageFiles Added file: " + file + ".");
        }
        return returnList;
    }
    
    /**
     * Finds the first index in a vector of images with a time stamp on or after
     * a given time.
     * @param list The list of images as files.
     * @param startTime a calendar holding the time to be checked.
     * @return The first index in a vector of images with a time stamp on or
     * after a given time.
     */
    private int findStartingIndex(Vector<File> list, Calendar startTime) {
        for(int i = 0; i < list.size(); i++){
            String path = list.get(i).getAbsolutePath();
            
            //Extract time from file name.
            int year, month, day, hour, minute, second;
            //indexes calculeted by position relative to the dash betweem the
            //date and time.
            int dashIndex = path.lastIndexOf("-");
            try {
                year = Integer.parseInt(path.substring(dashIndex - 8, dashIndex - 4));
                month = Integer.parseInt(path.substring(dashIndex - 4, dashIndex - 2));
                day = Integer.parseInt(path.substring(dashIndex - 2, dashIndex));
                hour = Integer.parseInt(path.substring(dashIndex + 1, dashIndex + 3));
                minute = Integer.parseInt(path.substring(dashIndex + 3, dashIndex + 5));
                second = Integer.parseInt(path.substring(dashIndex + 5, dashIndex + 7));
            } catch (NumberFormatException ex) {
                return 0;
            }
            //Make calendar from parsed data
            Calendar fileTimeCalendar = new GregorianCalendar();
            fileTimeCalendar.setTimeZone(startTime.getTimeZone());
            fileTimeCalendar.set(year, month - 1, day, hour, minute, second);
            fileTimeCalendar.set(Calendar.MILLISECOND, 0);
            //Check for start time.
            if (fileTimeCalendar.getTimeInMillis() >= startTime.getTimeInMillis()){
                return i;
            }
        }
        return 0;
    }
    
    /**
     * Finds the last index in a vector of images with a time stamp on or before
     * a given time.
     * @param list The list of images as files.
     * @param stopTime a calendar holding the time to be checked.
     * @return The last index in a vector of images with a time stamp on or
     * before a given time. 
     */
    private int findStopingIndex(Vector<File> list, Calendar stopTime) {
        for(int i = list.size() - 1; i >= 0; i--){
            String path = list.get(i).getAbsolutePath();
            
            //Extract time from file name.
            int year, month, day, hour, minute, second;
            //indexes calculeted by position relative to the dash betweem the
            //date and time.
            int dashIndex = path.lastIndexOf("-");
            try {
                year = Integer.parseInt(path.substring(dashIndex - 8, dashIndex - 4));
                month = Integer.parseInt(path.substring(dashIndex - 4, dashIndex - 2));
                day = Integer.parseInt(path.substring(dashIndex - 2, dashIndex));
                hour = Integer.parseInt(path.substring(dashIndex + 1, dashIndex + 3));
                minute = Integer.parseInt(path.substring(dashIndex + 3, dashIndex + 5));
                second = Integer.parseInt(path.substring(dashIndex + 5, dashIndex + 7));
            } catch (NumberFormatException ex) {
                return list.size() - 1;
            }
            //Make calendar from parsed data
            Calendar fileTimeCalendar = new GregorianCalendar();
            fileTimeCalendar.setTimeZone(stopTime.getTimeZone());
            fileTimeCalendar.set(year, month - 1, day, hour, minute, second);
            fileTimeCalendar.set(Calendar.MILLISECOND, 0);
            //Check for start time.
            if (fileTimeCalendar.getTimeInMillis() <= stopTime.getTimeInMillis()){
                return i;
            }
        }
        return list.size() - 1;
    }
    
    /**
     * Retrieves a Vector of Files from the storage system for the resource
     * requested and for the ResourceRange provided in resourceRequest. If the
     * directory for the resource does not exist, the returned Vector will be of
     * size zero.  If day-long video are requested, the returned vector will
     * contain two series of the requested videos.  The first series will be of
     * standard quality and, placed after all of those, the second series will 
     * be of low quality.  Also, if day-long videos are requested, all default
     * videos will be the length of a full day so the caller must check if 
     * trimming is necessary to account for time when video should not be 
     * available.
     *
     * @param resourceRequest Specifies the Resource and the range and amount of
     * ResourceInstances to be provided.
     * @param wantDayLongVideos True if day-long videos ara being requested, 
     * False otherwise.
     * @return A Vector of Files for ResourceInstances contained within the
     * specified range.
     */
    public Vector<File> retrieveFileVector(ResourceInstancesRequested resourceRequest,
            boolean wantDayLongVideos) {
        Vector<File> retrievedFiles = new Vector<>();
        
        // Get information from request.
        Resource resource = getResource(resourceRequest.getResourceID());
        ResourceRange range = resourceRequest.getResourceRange();

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTimeZone(resource.getTimeZone().getTimeZone());
        startCalendar.setTimeInMillis(range.getStartTime().getTime());

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTimeZone(resource.getTimeZone().getTimeZone());
        endCalendar.setTimeInMillis(range.getStopTime().getTime());

        //Test for directory, and return empty vactor if directory isn't there.
        // example: "D:\BUWeatherCamera\"
        final String resourceDirectory = fileSystemRoot + File.separator
                + resource.getStorageFolderName() + File.separator;
        Debug.println("Test directory: " + resourceDirectory);
        //Return immediately if resource folder does not exist
        if (!(new File(resourceDirectory)).exists()) {
             Debug.println("Test directory: " + resourceDirectory + " did not exists");
            return retrievedFiles;
        }

        File file;

        //Handle videos.
        if (resourceRequest.requestingMovie() || wantDayLongVideos) {
            Debug.println("Looking for videos in retrieveFileVector");
            //Return day-long videos, if requested.
            if (wantDayLongVideos) {
                Debug.println("Looking for day-long videos.");
                
                // Must start the loop at the start of the first day.
                startCalendar.setTime(ResourceTimeManager
                        .getStartOfDayDateFromMilliseconds(startCalendar
                        .getTimeInMillis(), resource.getTimeZone()
                        .getTimeZone()));
                
                // Loop through days to find standard-quality videos.
                for (GregorianCalendar loopCalendar = 
                        (GregorianCalendar) startCalendar.clone();
                        loopCalendar.getTimeInMillis() < endCalendar
                        .getTimeInMillis();
                        loopCalendar.add(GregorianCalendar.DATE, 1)) {

                    // Get file path.
                    String filePath = this.getFilePathForData(resource
                            .getResourceNumber(), new Date(loopCalendar
                            .getTimeInMillis()), true) 
                            + getFileNameForData(resource
                            .getResourceNumber(), new Date(loopCalendar
                            .getTimeInMillis()), ".mp4", false, false);

                    file = new File(filePath);

                    // Add file if it exists.
                    if (file.exists()) {
                        retrievedFiles.add(file);
                        Debug.println("retrieveFileVector: Added: " + filePath);
                        log.finest("retrieveFileVector: Added file: " + file + ".");
                    } else {
                        // Get day-long default video for resoure (standard-
                        // quality version.
                        File noDataFile = getDefaultDayLongMovie(resource, 
                                false);
                        if (noDataFile == null) {
                            retrievedFiles.add(null);
                            Debug.println("retrieveFileVector: Added NULL.");
                            log.finest("retrieveFileVector: Added NULL.");
                        } else {
                            retrievedFiles.add(noDataFile);
                            Debug.println("retrieveFileVector: Added: " 
                                    + noDataFile.getAbsolutePath());
                            log.finest("retrieveFileVector: Added file: " 
                                    + noDataFile + ".");
                        }
                    }
                } // end of first for loop
                
                // Loop through days to find loe-quality videos.
                for (GregorianCalendar loopCalendar = 
                        (GregorianCalendar) startCalendar.clone();
                        loopCalendar.getTimeInMillis() < endCalendar
                        .getTimeInMillis();
                        loopCalendar.add(GregorianCalendar.DATE, 1)) {

                    // Get file path.
                    String filePath = this.getFilePathForData(resource
                            .getResourceNumber(), new Date(loopCalendar
                            .getTimeInMillis()), true) 
                            + getFileNameForData(resource
                            .getResourceNumber(), new Date(loopCalendar
                            .getTimeInMillis()), ".mp4", false, true);

                    file = new File(filePath);

                    // Add file if it exists.
                    if (file.exists()) {
                        retrievedFiles.add(file);
                        Debug.println("retrieveFileVector: Added: " + filePath);
                        log.finest("retrieveFileVector: Added file: " + file + ".");
                    } else {
                        // Get day-long default video for resoure (low-quality
                        // version.
                        File noDataFile = getDefaultDayLongMovie(resource, 
                                true);
                        if (noDataFile == null) {
                            retrievedFiles.add(null);
                            Debug.println("retrieveFileVector: Added NULL.");
                            log.finest("retrieveFileVector: Added NULL.");
                        } else {
                            retrievedFiles.add(noDataFile);
                            Debug.println("retrieveFileVector: Added: " 
                                    + noDataFile.getAbsolutePath());
                            log.finest("retrieveFileVector: Added file: " 
                                    + noDataFile + ".");
                        }
                    }
                } // end of second for loop
            } else {
                Debug.println("Looking for hour-long videos.");
                //Make calendar for below for loop.
                GregorianCalendar loopCalendar = 
                        (GregorianCalendar) startCalendar.clone();
                //Set loopCalendar to start of hour.
                loopCalendar.set(Calendar.MINUTE, 0);
                loopCalendar.set(Calendar.SECOND, 0);
                loopCalendar.set(Calendar.MILLISECOND, 0);
                for (; loopCalendar.getTimeInMillis() < endCalendar
                        .getTimeInMillis();
                        loopCalendar.add(GregorianCalendar.HOUR, 1)) {
                    file = getMovieForTime(resource, loopCalendar, 
                            resourceRequest.getFileType());
                    if (file != null && file.exists()) {
                        Debug.println(file.getAbsolutePath());
                        retrievedFiles.add(file);
                        Debug.println("retrieveFileVector: Added file: " 
                                + file + ".");
                        log.finest("retrieveFileVector: Added file: " + file 
                                + ".");
                    }
                }
            }
            return retrievedFiles;
        }  //End of if for video requests.

        //Handle weather stations

        //Get file extension
        String extension;
        switch (resource.getFormat()) {
            case comma_separated_values:
                extension = ".csv";
                break;
            case space_separated_values:
                extension = ".ssv";
                break;
            default:
                extension = ".txt";
        }
        
         // Must start the loop at the start of the first day.
        startCalendar.setTime(ResourceTimeManager
                .getStartOfDayDateFromMilliseconds(startCalendar
                .getTimeInMillis(), resource.getTimeZone().getTimeZone()));

        if (resource.getResourceType() == WeatherResourceType.WeatherStation
                || resource.getResourceType() == WeatherResourceType.WeatherStationValues) {
            Debug.println("Looking for WeatherStation files in retrieveFileVector");
            for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                    loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                    loopCalendar.add(GregorianCalendar.DATE, 1)) {
               
                
                // Get file path.
                String filePath = this.getFilePathForData(resource
                            .getResourceNumber(), new Date(loopCalendar
                            .getTimeInMillis()), false) 
                            + getFileNameForData(resource
                            .getResourceNumber(), new Date(loopCalendar
                            .getTimeInMillis()), extension, false, false);

                file = new File(filePath);
                
                // Add file if exists.
                if (file.exists()) {
                    retrievedFiles.add(file);
                   // Debug.println("Added: " + filePath);
                    log.finest("retrieveFileVector: Added file: " + file + ".");
                }
            } // end of for loop
            return retrievedFiles;
        }

        //Handle pictures.
        Debug.println("Getting images using getImageFiles(resourceRequest)");
        return getImageFiles(resourceRequest);
    }
    
    public String getFileSystemRoot() {
        return fileSystemRoot;
    }

    public final void setFileSystemRoot(String fileSystemRoot) {
        this.fileSystemRoot = fileSystemRoot;
    }

    public DBMSSystemManager getDBMS() {
        return dbms;
    }

    public final void setDBMS(DBMSSystemManager dbms) {
        this.dbms = dbms;
    }
}
