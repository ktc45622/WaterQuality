package weather.serverside.FFMPEG;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;

/**
 * This file contains a utility to create a low-quality copy of a video file 
 * using ffmpeg.  The calling code must specify the full path, including the 
 * name, of the file to contain the copied data.
 * 
 * @author Brian Bankes
 */
public class VideoLowQualityCopier {
    
    /**
     * Method to create a low-quality copy of a video file using ffmpeg. The
     * calling code must specify the full path, including the name, of the file
     * to contain the copied data.
     * 
     * @param inputFile The file to be copied, which must exist.
     * @param targetFile The output file to be created.  It will be overwritten
     * if it exists.
     * @return True if the copying was successful; false otherwise.
     */
    public static boolean makeLowQualityCopy(File inputFile, 
            File targetFile) {
        long startTime = System.currentTimeMillis();
        Debug.println("Starting Low Quality Copier.");
        
        //Check for valid parameters.
        if (inputFile == null) {
            WeatherLogger.log(Level.SEVERE,
                    "Low-quality video copying function passed null input "
                    + "file.");
            Debug.println(
                    "Low-quality video copying function passed null input "
                    + "file.");
            return false;
        }
        if (!inputFile.exists()) {
            WeatherLogger.log(Level.SEVERE,
                    "Low-quality video copying function passed nonexistent "
                    + "input file.");
            Debug.println(
                    "Low-quality video copying function passed nonexistent "
                    + "input file.");
            return false;
        }
        if (targetFile == null) {
            WeatherLogger.log(Level.SEVERE,
                    "Low-quality video copying function passed null target "
                    + "file.");
            Debug.println(
                    "Low-quality video copying function passed null target "
                    + "file.");
            return false;
        }
        
        //Use helper functions to run ffmpeg process.
        ProcessBuilder pb = getProcessBuilder(inputFile, targetFile);
        boolean success = runProcess(pb);
        
        //If the target file was made but the copying did not succeed, 
        //the file must be deleted.
        if (!success && targetFile.exists()) {
            targetFile.delete();
        }
        
        long endTime = System.currentTimeMillis();
        double elapsedSecs = ((double)(endTime - startTime)) / 1000;
        Debug.println("Ending Low Quality Copier at " + elapsedSecs 
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
                    "Error starting low-quality video copy process.", ex);
            Debug.println("Error starting low-quality video copy process.");
            
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
                    "Low-quality video copy process interrupted.", ex);
            Debug.println("Low-quality video copy process interrupted.");
            
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
                    "Error in low-quality copying process.");
            Debug.println("Error in low-quality copying process.");
            return false;
        } else {
            //No error were found, so return process true.
            return true;
        }
    }
    
    /**
     * Returns the <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create the low-quality copy of the video at the give path location.
     * 
     * @param inputFile The file to be copied, which must exist.
     * @param targetFile The output file to be created.  For a successful
     * outcome, the file must NOT exist.
     * @return The <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create the low-quality copy of the video.
     */
    private static ProcessBuilder getProcessBuilder(File inputFile, 
            File targetFile) {
        ArrayList<String> wordsOfCommand = new ArrayList<>();
        String ffmpegPath = PropertyManager.getServerProperty("FFMPEG_PATH") 
                + "ffmpeg";
        
        //Build command.
        wordsOfCommand.add(ffmpegPath);
        wordsOfCommand.add("-v");
        wordsOfCommand.add("error");
        wordsOfCommand.add("-y");
        wordsOfCommand.add("-i");
        wordsOfCommand.add(inputFile.getAbsolutePath());
        wordsOfCommand.add("-crf");
        wordsOfCommand.add(PropertyManager
                .getGeneralProperty("CRF_ALL_DAY_LOW_QUALITY_VALUE"));
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
        
        //Specify test input, which must exist for a success.
        File testData = new File("C:\\Testing Space\\LongVideo.mp4");
        
        //Provide location and name of result file to be made.
        File targetFile = new File("C:\\Testing Space\\LongVideoLow.mp4");
        
        boolean result = makeLowQualityCopy(testData, targetFile); 
        if (result) {
            Debug.println("Low-quality copying successful.");
        } else {
            Debug.println("Low-quality copying NOT successful.");
        }
    }
}
