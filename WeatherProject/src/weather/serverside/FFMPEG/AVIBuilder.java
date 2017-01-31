package weather.serverside.FFMPEG;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;

/**
 * This file contains a utility that uses FFMPEG to make a video from a series
 * of images.  The calling code must specify the full path, including the name,
 * of the MP4 file to be created.  Also to be provided are a set of images with
 * lexicographically-ordered file names, the video's length, the format of the
 * image files, and the video's dimensions.
 * @author Brian Bankes
 */
public class AVIBuilder {
    
    /**
     * This method crates an AVI video from a folder's worth of image files
     * 
     * @param imageFolder The folder containing the image files.  It can 
     * contain nothing else and the images must have  lexicographically-ordered 
     * file names according to their desired place in the AVI video.
     * @param length The length of the the desired AVI video in seconds.
     * @param format The format of the input images. This must be either JPEG or
     * PNG.
     * @param width The width of the AVI video in pixels, which must be even 
     * for FFMPEG.
     * @param height The height of the AVI video in pixels, which must be even 
     * for FFMPEG.
     * @param codec The video codec of the the AVI video.
     * @param targetFile The AVI file to be created. It will be overwritten if
     * it exists.
     * @return True if the video was created; False otherwise.
     */
    public static boolean makeAVIFromImages(File imageFolder, int length,
            ResourceFileFormatType format, int width, int height, 
            String codec, File targetFile) {
        long startTime = System.currentTimeMillis();
        Debug.println("Starting AVI creation.");
        
        //Check for valid parameters.
        if (imageFolder == null) {
            logAndDebugError("AVI Builder sent null input folder.");
            return false;
        }
        if (!imageFolder.exists()) {
            logAndDebugError("AVI Builder sent nonexistent input folder.");
            return false;
        }
        if (imageFolder.isFile()) {
            logAndDebugError("AVI Builder sent file in place of input folder.");
            return false;
        }
        if (imageFolder.listFiles().length == 0) {
            logAndDebugError("AVI Builder sent empty input folder.");
            return false;
        }
        if (length < 1) {
            logAndDebugError("AVI Builder sent nonpositive length.");
            return false;
        }
        if (format != ResourceFileFormatType.jpeg && format
                 != ResourceFileFormatType.png) {
            logAndDebugError("AVI Builder sent ResourceFileFormatType other "
                + "than jpeg or png.");
            return false;
        }
        if (width < 1 || width % 2 == 1 || height < 1 || height % 2 == 1) {
            logAndDebugError("AVI Builder sent dimensions that are not both "
                + "positive even integers (" + width + "x" + height + ").");
            return false;
        } 
        if (codec == null) {
            logAndDebugError("AVI Builder sent null codec.");
            return false;
        }
        if (codec.isEmpty()) {
            logAndDebugError("AVI Builder sent empty string as codec.");
            return false;
        }
        if (targetFile == null) {
            logAndDebugError("AVI Builder sent null target file.");
            return false;
        }
        
        //Find frame rates and number of images.
        
        //Find image count.
        int numberOfImages = imageFolder.listFiles().length;
        
        //Input framerates less than the minimum will have FFMPEG dupe frames.
        double inFrameRate, outFrameRate;
        double minFrameRate = Double.parseDouble(PropertyManager
                .getGeneralProperty("MOVIE_PADDED_FRAMERATE"));
        inFrameRate = Math.max(numberOfImages, 1) / ((double) (length));

        if (inFrameRate < minFrameRate) {
            outFrameRate = minFrameRate;
        } else {
            outFrameRate = inFrameRate;
        }
        
        //Make temporary subfolder of imput folder to hold renamed images,which 
        //will be renamed to image1, image2, and so on.
        File numberedImageFolder = new File(imageFolder.getAbsolutePath()
            + File.separator + "Numbered Images");
        numberedImageFolder.mkdir();
        
        //Rename images in input folder and add extra image if necessary, as
        //when a video with more then one image is made, FFMPEG is skipping the
        //first image (as of 3/2/15).
        try {

            //Set first image number to make room to copy the first image if 
            //necessary.
            int imageNumber = 2;
            if (numberOfImages == 1) {
                imageNumber = 1;
            }
            
            //Copy files.
            for (File source : imageFolder.listFiles()) {
                if (source.isDirectory()) {
                    continue;
                }
                File target = new File(numberedImageFolder.getAbsolutePath()
                        + File.separator + "image" + imageNumber + "." + format);
                Files.copy(source.toPath(), target.toPath());
                imageNumber++;
            }

            //Copy first image if necessary.
            if (numberOfImages > 1) {
                File source = new File(numberedImageFolder.getAbsoluteFile()
                        + File.separator + "image2." + format);
                File target = new File(numberedImageFolder.getAbsoluteFile()
                        + File.separator + "image1." + format);
                Files.copy(source.toPath(), target.toPath());
            }

        //Catch copying exceptions. 
        } catch (IOException ex) {
            clearTempImageFolder(numberedImageFolder);
            logAndDebugError("AVIBuilder unable to renumber input images.");
            return false;
        }
        
        //Use helper functions to run ffmpeg process.
        ProcessBuilder pb = getProcessBuilder(numberedImageFolder, length, 
                format, inFrameRate, outFrameRate, numberOfImages, width, 
                height, codec, targetFile);
        boolean ffmpegHadErrorFreeRun = runProcess(pb);
        
        //The variable will indicate if a valid video was made.
        boolean success;
        
        //Check for vaild video.
        if (ffmpegHadErrorFreeRun) {
            success = true;
        } else if (!targetFile.exists()) {
            success = false;
        } else {
            //The movie length must be checked.
            int actualLength = VideoLengthCalculator
                    .getLengthOfMP4FileInSeconds(targetFile);
            success = (actualLength == length);
        }
        
        //If the AVI file was made but the copying did not succeed, 
        //the file must be deleted.
        if (!success && targetFile.exists()) {
            targetFile.delete();
        }
        
        //Clear renumbered data.
        clearTempImageFolder(numberedImageFolder);
        
        //Log error if not successful
        if (!success) {
            WeatherLogger.log(Level.SEVERE,
                    "Error in AVI creation process.");
            Debug.println("Error in AVI creation process.");
        }
        
        long endTime = System.currentTimeMillis();
        double elapsedSecs = ((double)(endTime - startTime)) / 1000;
        Debug.println("Ending AVI creation at " + elapsedSecs 
                + " seconds.");
        
        //Return result.
        return success;
    }
    
    /**
     * A helper function to clear to renumbered image folder created by the 
     * public function.
     * 
     * @param tempFolder The renumbered image folder created by the public
     * function.
     */
    private static void clearTempImageFolder(File tempFolder) {
        if (tempFolder.exists()) {
            for (File image : tempFolder.listFiles()) {
                image.delete();
            }
            tempFolder.delete();
        }
    }
    
    /**
     * A helper function to log an error message as severe an send it to the
     * debug output.
     * 
     * @param message The error message. 
     */
    private static void logAndDebugError(String message) {
        WeatherLogger.log(Level.SEVERE, message);
        Debug.println(message);
    }
    
    /**
     * Method that executes the ffmpeg <code>Process</code>.
     * 
     * @param builder The <code>ProcessBuilder</code> that will start the 
     * ffmpeg <code>Process</code>.
     * @return True if the process ran without producing ffmpeg errors; false 
     * otherwise.
     * NOTE: The production of an ffmpeg error does NOT mean a valid video was 
     * not made.  The return value should be used as a flag indicating whether
     * or not the caller must check if a valid video was made.
     */
    private static boolean runProcess(ProcessBuilder builder) {
        //Strings of output from streams.
        String outputLine;
        String errorLine;
        
        //Start process
        Process process;
        try {
            process = builder.start();
        } catch (IOException ex) {
            //Log error.
            WeatherLogger.log(Level.SEVERE,
                    "Error starting AVI video creation process.", ex);
            Debug.println("Error starting AVI video creation process.");
            
            return false;
        }
        
        //Construct readers.
        Scanner outputReader = new Scanner(new InputStreamReader(process
                .getInputStream()));
        Scanner errorReader = new Scanner(new InputStreamReader(process
                 .getErrorStream()));
        
        //Track if error was found, assume not.
        boolean errorFound = false;

        while (outputReader.hasNextLine()) {
            outputLine = outputReader.nextLine();
            Debug.println("OUTPUT: " + outputLine);
        }
        
        //Read error stream.
        while (errorReader.hasNextLine()) {
            errorLine = errorReader.nextLine();
            Debug.println("ERROR TEXT: " + errorLine);
            
            //Update flag to show that an error has occurred.
            errorFound = true;
        }

        //Wait for process to complete.
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            //Log error.
            WeatherLogger.log(Level.SEVERE,
                    "Process of AVI creation interrupted.", ex);
            Debug.println("Process of AVI creation interrupted.");
            
            //Close Scanners before reporting error
            outputReader.close();
            errorReader.close();
            
            return false;
        }
        
        //Close Scanners.
        outputReader.close();
        errorReader.close();
        
        //Check if error was reported from process error stream.
        if (errorFound) {
            //Report ffmpeg error.
            WeatherLogger.log(Level.INFO,
                    "AVI creation process produced FFMPEG error.");
            Debug.println("AVI creation process produced FFMPEG error.");
            return false;
        } else {
            //No error were found, so return process true.
            return true;
        }
    }
    
    /**
     * Returns the <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create the AVI video at the given path location from the given 
     * images.
     * 
     * @param renumberedImageFolder A folder with the images renamed as image1,
     * image2, image3, and so on.  Due to an FFMPEG issue (noted above as a 
     * 3/2/15 note) image2 should be a repeat of image1 for multiple-image 
     * videos.
     * @param length The length of the the desired AVI video in seconds.
     * @param format The format of the input images.
     * @param inFrameRate The input frame rate for the FFMPEG command.
     * @param outFrameRate The output frame rate for the FFMPEG command.
     * @param numberOfImages The number of input images.
     * @param width The width of the AVI video in pixels, which must be even 
     * for FFMPEG.
     * @param height The height of the AVI video in pixels, which must be even 
     * for FFMPEG.
     * @param codec The video codec of the the AVI video.
     * @param targetFile The AVI file to be created. It will be overwritten if
     * it exists.
     * @return True if the video was created; False otherwise.
     */
    private static ProcessBuilder getProcessBuilder(File renumberedImageFolder, 
            int length, ResourceFileFormatType format, double inFrameRate, 
            double outFrameRate, int numberOfImages, int width, int height, 
            String codec, File targetFile) {
        ArrayList<String> wordsOfCommand = new ArrayList<>();
        String ffmpegPath = PropertyManager.getServerProperty("FFMPEG_PATH") 
                + "ffmpeg";
        
        //Build command.
        wordsOfCommand.add(ffmpegPath);
        wordsOfCommand.add("-v");
        wordsOfCommand.add("error");
        wordsOfCommand.add("-y");
        if (numberOfImages == 1) {
            wordsOfCommand.add("-loop");
            wordsOfCommand.add("1");
            wordsOfCommand.add("-i");
            wordsOfCommand.add(renumberedImageFolder.getAbsolutePath() 
                    + File.separator + "image1." + format);
            wordsOfCommand.add("-t");
            wordsOfCommand.add("" + length);
        } else {
            wordsOfCommand.add("-r");
            wordsOfCommand.add("" + inFrameRate);
            wordsOfCommand.add("-i");
            wordsOfCommand.add(renumberedImageFolder.getAbsolutePath() 
                    + File.separator + "image%d." + format);
            wordsOfCommand.add("-r");
            wordsOfCommand.add("" + outFrameRate);
        }
        wordsOfCommand.add("-c:v");
        wordsOfCommand.add(codec);
        wordsOfCommand.add("-s");
        wordsOfCommand.add("" + width + "x" + height);
        wordsOfCommand.add("-crf");
        wordsOfCommand.add(PropertyManager
            .getServerProperty("AVI_CRF"));
        wordsOfCommand.add("-vsync");
        wordsOfCommand.add("1");
        wordsOfCommand.add(targetFile.getAbsolutePath());
         
        //Debug command.
        for (String w : wordsOfCommand) {
            Debug.print(" " + w);
        }
        Debug.println();
        
        return new ProcessBuilder(wordsOfCommand);       
    }
    
    /**
     * For testing.
     * @param args 
     */
    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Specify test input directory, which must exist for a success.
        File dataFolder = new File("C:\\2016 Test Image");
        
        //Provide location and name of result file to be made.
        File targetFile = new File("C:\\Testing Space\\BugControl4.avi");
        
        boolean result = makeAVIFromImages(dataFolder, 
                Integer.parseInt(PropertyManager
                .getGeneralProperty("MOVIE_LENGTH")), 
                ResourceFileFormatType.jpeg, 1296, 960, 
                PropertyManager.getServerProperty("FFMPEG_VCODEC"), 
                targetFile); 
        if (result) {
            Debug.println("AVI creation successful.");
        } else {
            Debug.println("AVI creation NOT successful.");
        }
    }
}
