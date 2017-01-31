package weather.serverside.FFMPEG;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;

/**
 * This file contains a utility that trims a video to a given number a seconds
 * using ffmpeg.  The file must have the extension .avi or .mp4  Excess time is 
 * always removed from the end of the input file.
 * 
 * @author Brian Bankes
 */
public class VideoTrimmer {
    
    /**
     * Method that trims a video to a given number a seconds using ffmpeg.  
     * Excess time is always removed from the end of the input file.
     * NOTE: To save time, the trimming is approximate.
     * 
     * @param fileToTrim The file to be trimmed, which must exist and end in 
     * either .avi or .mp4
     * @param trimmingLength The length, in seconds to which the video is to be
     * trimmed.  This length must be positive and entering a time longer than
     * the video will cause the function to have no effect.
     * @return True if the trimming was successful or had no effect; false 
     * otherwise.
     */
    public static boolean trimVideo(File fileToTrim, int trimmingLength) {
        long startTime = System.currentTimeMillis();
        Debug.println("Starting Trimmer.");
        
        //Check for valid parameters and file type/
        if (fileToTrim == null) {
            WeatherLogger.log(Level.SEVERE,
                    "Video trimming function passed null input file.");
            Debug.println(
                    "Video trimming function passed null input file.");
            return false;
        }
        if (!fileToTrim.exists()) {
            WeatherLogger.log(Level.SEVERE,
                    "Video trimming function passed nonexistent input file.");
            Debug.println(
                    "Video trimming function passed nonexistent input file.");
            return false;
        }
        if (trimmingLength <= 0) {
             WeatherLogger.log(Level.SEVERE,
                    "Video trimming function passed trimming length of less"
                    + " than or equal to zero.");
            Debug.println(
                    "Video trimming function passed trimming length of less"
                    + " than or equal to zero.");
            return false;
        }
        String originalFileName = fileToTrim.getAbsolutePath();
        int originalExtensionStart = originalFileName.lastIndexOf(".");
        String extension =  originalFileName.substring(originalExtensionStart);
        boolean usingAVI = true; //Assume AVI and change later if MP4.
        if (extension.equals(".mp4")) {
            usingAVI = false;
        } else if (!extension.equals(".avi")) {
            WeatherLogger.log(Level.SEVERE,
                    "Video trimming function passed file with extension other"
                    + " than .avi or .mp4.");
            Debug.println(
                    "Video trimming function passed file with extension other"
                    + " than .avi or .mp4.");
            return false;
        }
        
        //Make path to temporary file required to store ffmpag outpuy.
        File tempFile = new File(originalFileName
                .substring(0, originalExtensionStart) + "_Trim_Temp"
                + extension);
        
        //Use helper functions to run ffmpeg process.
        ProcessBuilder pb = getProcessBuilder(fileToTrim, tempFile, 
                trimmingLength, usingAVI);
        boolean success = runProcess(pb);
        
        //Copy temporary file into original file if ffmpeg was successful.
        if (success) {
            try {
                Files.copy(tempFile.toPath(), fileToTrim.toPath(), 
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                success = false;
            }
        }
        
        //Delete temporary if it exists. 
        if (tempFile.exists()) {
            tempFile.delete();
        }
        
        long endTime = System.currentTimeMillis();
        double elapsedSecs = ((double)(endTime - startTime)) / 1000;
        Debug.println("Ending Trimmer at " + elapsedSecs 
                + " seconds.");
        
        //Return result.
        return success;
    }
    
    /**
     * Method that executes the ffmpeg <code>Process</code>.
     * 
     * @param builder The <code>ProcessBuilder</code> that will start the 
     * ffmpeg <code>Process</code>.
     * @return True if the process ran successfully; false otherwise.
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
                    "Error starting video trimming process.", ex);
            Debug.println("Error starting video trimming process.");
            
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
                    "Video trimming process interrupted.", ex);
            Debug.println("Video trimming process interrupted.");
            
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
            //Log error.
            WeatherLogger.log(Level.SEVERE,
                    "Error in video trimming process.");
            Debug.println("Error in video trimming process.");
            return false;
        } else {
            //No error were found, so return process true.
            return true;
        }
    }
    
    /**
     * Returns the <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create a trimmed copy of the input file.  A temporary copy will be
     * placed at a given file location.
     * 
     * @param fileToTrim The file to be trimmed, which must exist.
     * @param tempOutputFile The output file to be created.  For a successful
     * outcome, the file must NOT exist.
     * @param trimmingLength The length, in seconds to which the video is to be
     * trimmed.
     * @param usingAVI True if the file to be trimmed is an AVI; False if it is
     * an MP4.
     * @return The <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create the low-quality copy of the video.
     */
    private static ProcessBuilder getProcessBuilder(File fileToTrim, 
            File tempOutputFile, int trimmingLength, boolean usingAVI) {
        ArrayList<String> wordsOfCommand = new ArrayList<>();
        String ffmpegPath = PropertyManager.getServerProperty("FFMPEG_PATH") 
                + "ffmpeg";
        
        //Build command.
        wordsOfCommand.add(ffmpegPath);
        wordsOfCommand.add("-i");
        wordsOfCommand.add(fileToTrim.getAbsolutePath());
        wordsOfCommand.add("-v");
        wordsOfCommand.add("error");
        wordsOfCommand.add("-y");
        wordsOfCommand.add("-ss");
        wordsOfCommand.add("0");
        wordsOfCommand.add("-t");
        wordsOfCommand.add(String.valueOf(trimmingLength));
        
        /**
         * Extra arguments must be added to prevent the introduction of 
         * pixilation when trimming an AVI video.  These arguments will reduce
         * precision and are unnecessary when trimming an MP4 video.
         */
        if (usingAVI) {
            wordsOfCommand.add("-codec");
            wordsOfCommand.add("copy");
        }
        
        wordsOfCommand.add(tempOutputFile.getAbsolutePath());
         
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
        
        //Specify test input, which must exist for a success.
        File testData = new File("C:\\USAPrecip\\2014\\January\\10\\movies\\USAPrecip_01-10-2014_low.mp4");
        
        boolean result = trimVideo(testData, 3); 
        if (testData.exists()) {
            Debug.println("Test MP4 data exists.");
        } else {
            Debug.println("Test MP4 data does NOT exist.");
        }
        
        if (result) {
            Debug.println("Video trimming successful.");
        } else {
            Debug.println("Video trimming NOT successful.");
        }
    }
}
