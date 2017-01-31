package weather.serverside.FFMPEG;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.utilities.Debug;
import weather.common.utilities.Emailer;
import weather.common.utilities.ImageDimensionFinder;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class constructs an AVI video and an MP4 video from the same supplied
 * list of images. It does not permanently store the videos.  Instead, methods
 * return <code>File</code> objects, which can then be saved.  Also, the size of
 * the file is not tested.
 * @author Brian Bankes
 */
public class ImageVideoMaker {
    
    //Minimum size for a valid image.
    private static final int MINIMUM_VALID_FILE_SIZE = 6*1024;
    
    //The storage system passed to the constructor.
    private final StorageControlSystem storageSystem; 
    
    //The folder where all temp data is stored, which is created with this video 
    //maker and deleted at cleanup when the object is set to null.
    private File rootTempFolder;
    
    //The folder where all temp images are stored, which is created with this 
    //video maker and deleted at cleanup when the object is set to null.
    private File imageTempFolder;
    
    //The temporary location of the AVI file created with this object.  It is 
    //null if craation has not been attempted or fails or cleanup has been 
    //called.
    private File aviFile = null;
    
    //The temporary location of the MP4 file created with this object.  It is 
    //null if craation has not been attempted or fails or cleanup has been 
    //called.
    private File mp4File = null;
    
    //The width of the movie.
    private int width;
    
    //The height of the movie.
    private int height;
    
    //A vector of resource instances containing images.
    private final ArrayList<ResourceInstance> instances;
    
    //The resource providing the instances, which can be null for a generic no
    //data movies.
    private final Resource resource;
    
    //The date and time that the images that will make the movie start, which
    //can be null for default and no data videos.
    private final Date startTime;
    
    //A date format to be used when sending emails once ininialized in the 
    //constructor.
    private final SimpleDateFormat emailDateFormat;
    
    //The file format of the ResourceInstances in instances.
    private ResourceFileFormatType format;
    
    //The codec used to create the video.
    private final String codec;
    
    //The desired length of the video
    private final int videoLength;

    /**
     * Creates a new ImageVideoMaker.  
     *
     * @param storageSystem A <code>StorageControlSystem</code> with access to 
     * the file system is use.  (As of 3/1/15, This must be an instance of
     * <code>StorageControlSystemLocalImpl</code>).
     * @param instances A <code>ArrayList</code> of type 
     * <code>ResourceInstance</code> containing images.  If a resource is given
     * in the next parameter, the images must be of the format for that 
     * resource.
     * @param resource The <code>Resource</code> providing the instances, which 
     * can be null for a generic no data movies.
     * @param videoLength The desired length of the video in seconds
     * @param startTime A <code>Date</code> holding the date and time that the 
     * images that will make the movie start, which can be null for default and 
     * no data videos.
     * @param codec The video codec for FFMPEG.
     */
    public ImageVideoMaker(StorageControlSystem storageSystem, 
            ArrayList<ResourceInstance> instances, Resource resource, 
            int videoLength, Date startTime, String codec) {
        this.storageSystem = storageSystem;
        this.instances = instances;
        this.resource = resource;
        this.videoLength = videoLength;
        if (resource != null) {
            this.format = resource.getFormat();
        } else {
            //Images will be converted to .jpeg's.
            this.format = ResourceFileFormatType.jpeg;
        }
        this.codec = codec;
        this.startTime = startTime;
        this.emailDateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss a");
    }
    
    /**
     * Sets the width and height of the desired movie.
     */
    private void setVideoDimensions() {
        //Set class variables to default values thet change if an image from the
        //resource is found.
        width = Integer.parseInt(PropertyManager
                .getGeneralProperty("DEFAULT_MOVIE_WIDTH"));
        height = Integer.parseInt(PropertyManager
                .getGeneralProperty("DEFAULT_MOVIE_HEIGHT"));
        
        //Stay with defaults if resource is null.
        if (resource == null) {
            return;
        }
        
        //Look for image dimensions.
        Dimension imageSize = ImageDimensionFinder
                .getDimensionOfResourceImage(storageSystem.getDBMS(), resource);
        
        //Stay with default if image size was not found.
        if (imageSize == null) {
            return;
        }
        
         //Get conponents of dimension and make sure they are even.
        if (imageSize.width % 2 == 1) {
            width = imageSize.width + 1;
        } else {
            width = imageSize.width;
        }
        if (imageSize.height % 2 == 1) {
            height = imageSize.height + 1;
        } else {
            height = imageSize.height;
        }
    }
    
    /**
     * Creates an AVI video and an MP4 video to be retrieved by the object's
     * other methods. The method should be able to provide both AVI and MP4
     * versions of the constructed video. This method will create a temporary
     * AVI file and a temporary MP4 file, barring error, which should be deleted
     * via a call to cleanup() once the object is no longer needed. If error
     * occur, the provided files will be null and this method will send error
     * messages to the administrators via email. Should this happen, cleanup()
     * should still be called to delete other temporary files.
     */
    public void createVideos() {    
        //This section sets the class variables and creates the temporary file
        //structure needed to make the videos.  It also does some input 
        //validation.
        
        //Make sure there is at least one ImageInstance passed to the object.
        if (instances == null || instances.isEmpty()) {
            sendAndLogError("No instances sent to ImageVideoMaker.");
            return;
        }
        
        //Validate format.
        if (format != ResourceFileFormatType.jpeg
                && format != ResourceFileFormatType.gif
                && format != ResourceFileFormatType.png) {
            sendAndLogError("Invalid image format (" + format + ").");
            return;
        }
        
        //Make temporsry file and folders and make sure they get made.
        createTempFolders();
        if (!rootTempFolder.exists() || !imageTempFolder.exists()) {
            sendAndLogError("Temporary folders could not be made.");
            return;
        }
        
        makeTempImages();
        if (imageTempFolder.listFiles().length == 0) {
            sendAndLogError("Temporary files could not be made.");
            return;
        }
        
        aviFile = new File(rootTempFolder.getAbsolutePath() + File.separator
            + "Result.avi");
        mp4File = new File(rootTempFolder.getAbsolutePath() + File.separator
            + "Result.mp4");
        
        //Get video dimensions and check for errors.
        width = 0;  //For later error testing.
        height = 0; //For later error testing.
        setVideoDimensions();
        if (width == 0 || height == 0) {
            aviFile = null;
            mp4File = null;
            sendAndLogError("Video dimensions could not be found.");
            return;
        }
        
        //Now, make videos.
        
        //Attempt to make AVI video.
        boolean aviResult = AVIBuilder.makeAVIFromImages(imageTempFolder, 
                videoLength, format, width, height, codec, aviFile);
        
        //If the video was not made set both video files to null and return 
        //after logging error.
        if (!aviResult) {
            if (aviFile != null && aviFile.exists()) {
                aviFile.delete();
            }
            aviFile = null;
            if (mp4File != null && mp4File.exists()) {
                mp4File.delete();
            }
            mp4File = null;
            sendAndLogError("AVI maker could not make video from images.");
            return;
        }
        
        //Attempt to make MP4 video.
        boolean mp4Result = MP4CopyMaker.makeMP4Copy(aviFile, mp4File);
        
        //Clear variable and log error if video was not made
        if (!mp4Result) {
            if (mp4File != null && mp4File.exists()) {
                mp4File.delete();
            }
            mp4File = null;
            sendAndLogError("MP4 copy could not be made from AVI file.");
        }
        
        //Due to a lack of precision by FFMPEG, the produced videos must be
        //trimmed to the desired length (inner if statements clear data and send
        //emails if errors occur).
        if (aviFile != null) {
            if (!VideoTrimmer.trimVideo(aviFile, videoLength)) {
                if (aviFile != null && aviFile.exists()) {
                    aviFile.delete();
                }
                aviFile = null;
                sendAndLogError("AVI video could not be trimmed to the correct"
                        + " length.");
            }
        }
        if (mp4File != null) {
            if (!VideoTrimmer.trimVideo(mp4File, videoLength)) {
                if (mp4File != null && mp4File.exists()) {
                    mp4File.delete();
                }
                mp4File = null;
                sendAndLogError("MP4 video could not be trimmed to the correct"
                        + " length.");
            }
        }
    }
    
    /**
     * A Helper function to record a error message.  It does the following:
     * 1. Sends an error email.
     * 2. Logs the message.
     * 3. Shows the message in Debug.
     * 
     * @param errorMessage The error message.
     */
    private void sendAndLogError(String errorMessage) {
        sendErrorMessage(errorMessage);
        WeatherLogger.log(Level.SEVERE, errorMessage + " (Resource: "
            + resource.getName() + ")");
        Debug.println(errorMessage); 
    }
    
    /**
     * Sends an email error notification to all administrators if the videos 
     * cannot be made from the images.
     * 
     * @param errorDescription A description of the error to be included in the
     * email.
     */
    private void sendErrorMessage(String errorDescription) {
        //Get infomation or pick defaults
        String resourceName;
        if (resource == null) {
            resourceName = "Generic Movies";
        } else {
            resourceName = resource.getResourceName();
        }
        String videoStartTime;
        if (startTime == null) {
            videoStartTime = "Not Specified";
        } else {
            videoStartTime = emailDateFormat.format(startTime);
        }
        
        //Make email text.
        StringBuilder subject = new StringBuilder();
        subject.append("BU Weather Viewer Video Creation Error For ");
        subject.append(resourceName);
        StringBuilder message = new StringBuilder();
        message.append("An error has occurred while making a video from");
        message.append(" images.  Please see the details below:\n");
        if (resource != null) {
            message.append("Resource: ").append(resource.getResourceName());
            message.append(" (#").append(resource.getResourceNumber())
                    .append(")");
        } else {
            message.append("This was a generic movie.");
        }
        message.append("\nVideo Start Time: ").append(videoStartTime);
        message.append("\n\nError message:\n").append(errorDescription);
        
       //Send email.
        try {
            Emailer.emailAdmin(message.toString(), subject.toString());
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE,
                    "Image video maker unable to send error email message.");
            Debug.println(
                    "Image video maker unable to send error email message.");
        }
    }

    /**
     * This is a helper function for use when determining the name of a 
     * temporary file.  In order to preserve the lexicographic order of the file
     * names, leading zeros must be added to the image number.  This function 
     * does so while converting the image number to a string. 
     * 
     * @param input The image number.
     * @param length The size of the <code>ArrayList</code> of type 
     * <code>ResourceInstance</code> passed to the constructor of this object.
     * @return The first parameter as a <code>String</code> with the correct
     * number of leading zeros.
     */
    private String convertIntToStrirgInSequence(int input, int length) {
        //Find total number of required digits.
        int digits = String.valueOf(length).length();
        
        //Get string from of input as presumptive output.
        String output = String.valueOf(input);
        
        //Add zeros to preserve lexicographic order.
        while (output.length() < digits) {
            output = "0" + output;
        }
        return output;
    }
    
    /**
     * Creates the temporary images in the temporary image folder.  The name of 
     * those images MUST be lexicographically ordered in a fashion that 
     * indicates the place of each image in the sequence.  This function also 
     * converts gif images to jpeg images and converts all images to jpeg images
     * if no resource was supplied.  This function also screens for valid 
     * images.
     */
    private void makeTempImages() {
        //Build folder path
        StringBuffer thisPath;
        String folder = imageTempFolder.getAbsolutePath();
        folder += File.separator;
        int totalImages = instances.size();
        Debug.println("Going to make " + totalImages + " images in  " + folder);

        //Write images and convert from gif if necessary.
        //Also screen for valid images.
        int imageNumber = 1;
        int countWriteErrors = 0;
        int countReadErrors = 0;
        for (int j = 0; j < instances.size(); j++) {
            //Assume image is valid and change this flag if not.
            //This flag is used to halt later steps after a fail.
            boolean isValidImage = true;
            
            //Build image path.
            thisPath = new StringBuffer();
            
            //Add leading zeros to image number to preserve lexicographic order.
            String numberAsString = convertIntToStrirgInSequence(imageNumber,
                    instances.size());
            thisPath.append(folder).append("image").append(numberAsString)
                    .append(".").append(format);
            File thisFile = new File(thisPath.toString());   

            //Convert gif to jpeg to help ffmepeg.  Also make images jpeg's if
            //there is no resource.
            if (format == ResourceFileFormatType.gif || resource == null) {
                //Get image from ResourceInstance.
                BufferedImage image = null;

                ImageInstance imageInstance = (ImageInstance) instances.get(j);
                Debug.println("Attempting to read image " + j
                        + " for type conversion. Image number is "
                        + imageNumber);
                try {
                    image = (BufferedImage) imageInstance.getImage();
                } //IndexOutOfBoundsException comes deep from within 
                //getImage(); It happens when an image is bad.
                catch (WeatherException | IndexOutOfBoundsException ex) {
                    countReadErrors++;
                    WeatherLogger.log(Level.SEVERE,
                            "Could not get image from ImageInstance " + j, ex);
                    Debug.println("Could not get image from ImageInstance "
                            + j);
                    isValidImage = false;
                }

                //Check for null image.
                if (isValidImage && image == null) {
                    countReadErrors++;
                    WeatherLogger.log(Level.SEVERE, 
                            "Image is null after reading for type conversion.");
                    Debug.println(
                            "Image is null after reading for type conversion.");
                    isValidImage = false;
                }

                //Set up path for new jpeg.
                if (isValidImage) {
                    thisPath = new StringBuffer();
                    thisPath.append(folder).append("image")
                            .append(numberAsString).append(".jpeg");
                    thisFile = new File(thisPath.toString());

                    //Write the new jpeg.
                    Debug.println("Attempting to write image " + j
                            + " for type conversion. Image number is "
                            + imageNumber);
                    try {
                        javax.imageio.ImageIO.write(image, "JPG", thisFile);

                    } catch (IOException | IllegalArgumentException ex) {
                        countWriteErrors++;
                        WeatherLogger.log(Level.SEVERE,
                                "Could not convert the image file "
                                + thisFile.getAbsolutePath()
                                + " to a jpeg image.", ex);
                        Debug.println("Could not convert the image file "
                                + thisFile.getAbsolutePath()
                                + " to a jpeg image.");
                        isValidImage = false;
                    }
                }
            }   //End of clause to convert to jpeg's,
            else {
                //Simply write the file.
                try {
                    instances.get(j).writeFile(thisFile);
                } catch (WeatherException ex) {
                    countWriteErrors++;
                    WeatherLogger.log(Level.SEVERE, "Could not write file to" 
                            + thisFile.getAbsolutePath(), ex);
                    Debug.println("Could not write file to" 
                            + thisFile.getAbsolutePath());
                    isValidImage = false;
                }
            }   //End of else clause for copying files.
            
            //Check for valid image size.
            if (isValidImage) {
                if (thisFile.length() < MINIMUM_VALID_FILE_SIZE) {
                    //Image is not valid.
                    countReadErrors++;
                    WeatherLogger.log(Level.SEVERE, "Image file was not large "
                            + "enough to be used by ImageVideoMaker.  "
                            + thisFile.getAbsolutePath() + " had length "
                            + thisFile.length());
                    Debug.println("Image file was not large enough to be used "
                            + "by ImageVideoMaker.  " + thisFile
                            .getAbsolutePath() + " had length " 
                            + thisFile.length());
                    isValidImage = false;
                }
            }

            //Last check that the possibly converted file is not a .gif. 
            if (isValidImage) {
                if (thisFile.getPath().endsWith(".gif")) {
                    //Image is not valid.
                    thisFile.delete();
                    countReadErrors++;
                    WeatherLogger.log(Level.SEVERE, "Image file "
                        +  thisFile.getAbsolutePath() 
                        + " could not be converted to a JPEG. ");
                    Debug.println("Image file "+  thisFile.getAbsolutePath() 
                        + " could not be converted to a JPEG. ");
                    isValidImage = false;
                }
            }
            
            //If image is valid, increment imageNumber for next loop.
            if (isValidImage) {
                imageNumber++;
            }

            //Extra Debug information.
            Debug.println("countWriteErrors is " + countWriteErrors
                    + "; countReadErrors is " + countReadErrors);
            Debug.println("J is " + j + " next imageNumber is " + imageNumber
                    + " File was " + thisFile.getAbsolutePath());
            if (j == 0) {
                Debug.println("First file is " + thisFile.
                        getAbsolutePath());
            }
            if (j == (instances.size() - 1)) {
                Debug.println("Last file is " + thisFile.getAbsolutePath());
            }
        }//End of outer for loop.

        //Now our former .gif images all have the extension .jpeg.
        if (format == ResourceFileFormatType.gif) {
            format = ResourceFileFormatType.jpeg;
        }

        //Final debug of function; first undo last loop increment.
        imageNumber--;
        Debug.println("Number of good images: " + imageNumber + " out of " 
                + instances.size());
    }
    
    /**
     * Helper function that creates the directories needed for video creation.
     */
    private void createTempFolders() {
        //Find shared path where all data is to stored.  This is a time-stamped
        //[ImageBuild-(Time-in-Mills)] subfolder of the folder used to store 
        //data for the resoure in use or a subfolder of Generic Movies is there
        //is no resource.

        //Get resource storage location.
        String storageFolderName;
        if (resource == null) {
            //Generic movies are beimg created.
            storageFolderName = "Generic Movies";
        } else {
            //Movies are being created for a resource.
            storageFolderName = resource.getStorageFolderName();
        }
        
        //Construct name of temporary root folder.
        String tempFolderName = "ImageBuild-" + System.currentTimeMillis();
        
        //Make path to root folder for temporary data.
        rootTempFolder = new File(storageSystem.getRootDirectory()
            + File.separator + storageFolderName + File.separator
            + tempFolderName);
        
        while (rootTempFolder.exists()) {
            //Loop until the current time is not in a folder name.
            //The odds of the folder existing are very small.
            tempFolderName = "ImageBuild-" + System.currentTimeMillis();

            //Reset path to root folder for temporary data.
            rootTempFolder = new File(storageSystem.getRootDirectory()
                    + File.separator + storageFolderName + File.separator
                    + tempFolderName);
        }
        
        //Actually make root temp folder.
        rootTempFolder.mkdirs();
        
        //Make folder for images.
        imageTempFolder = new File(rootTempFolder.getAbsolutePath() 
                + File.separator + "Images");
        imageTempFolder.mkdirs();
    } 

    /**
     * Returns the created AVI file or throws an exception if one was not
     * created due to an error or the lack of an attempt or if cleanup() has
     * been called. Note that cleanup() should always be called when the need
     * for this object is over.
     *
     * @return The created AVI file.
     * @throws WeatherException
     */
    public File getAVI() throws WeatherException {
        if (aviFile == null) {
            throw new WeatherException(4025, "No AVI movie file was returned.");
        }
        return aviFile;
    }
    
    /**
     * Returns the created MP4 file or throws an exception if one was not
     * created due to an error or the lack of an attempt or if cleanup() has
     * been called. Note that cleanup() should always be called when the need
     * for this object is over.
     *
     * @return The created MP4 file.
     * @throws WeatherException
     */
    public File getMP4() throws WeatherException {
        if (mp4File == null) {
            throw new WeatherException(4025, "No MP4 movie file was returned.");
        }
        return mp4File;
    }
    
    /**
     * Cleans up temporary data created by this object.  Also clears the 
     * temporary copies of the created movie files. This method should always be
     * called when the need for this object is over.
     */
    public void cleanup() {
        //Delete temporary images.
        if (imageTempFolder != null && imageTempFolder.exists()) {
            for (File image : imageTempFolder.listFiles()) {
                image.delete();
            }
        }
        
        //Delete temporary videos and set their objects to null if they exist.
        if (aviFile != null && aviFile.exists()) {
            aviFile.delete();
            aviFile = null;
        }
        if (mp4File != null && mp4File.exists()) {
            mp4File.delete();
            mp4File = null;
        }
        
         //Delete temporary folders and set their objects to null if they exist.
        if (imageTempFolder != null && imageTempFolder.exists()) {
            imageTempFolder.delete();
            imageTempFolder = null;
        }
        if (rootTempFolder != null && rootTempFolder.exists()) {
            rootTempFolder.delete();
            rootTempFolder = null;
        }
    }
}
