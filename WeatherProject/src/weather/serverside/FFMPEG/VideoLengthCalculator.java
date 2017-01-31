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
 * This file contains a utility to find the duration of a video file using the 
 * ffmpeg component named ffprobe.
 * 
 * @author Brian Bankes
 */
public class VideoLengthCalculator {
    
    //String to indicate error in ffprobe process
    private static final String ERROR_STRING = "ERROR";
    
    /**
     * Method to find the duration of a video file in seconds.
     * 
     * @param mp4File The <code>File</code> whose duration is desired.
     * @return The input's duration in milliseconds of 0 if an error occurs.
     * The return value is rounded to the nearest second.
     */
    public static int getLengthOfMP4FileInSeconds(File mp4File) {
        Debug.println("Starting Length Calculator");
        long startTime = System.currentTimeMillis();
        float unroundedSecs =  getLengthOfMP4FileInMillis(mp4File) /
               1000.0f;
       
        long endTime = System.currentTimeMillis();
        double elapsedSecs = ((double)(endTime - startTime)) / 1000;
        Debug.println("Ending Length Calculator at " + elapsedSecs 
                + " seconds.");
       
       return Math.round(unroundedSecs);
    }
    
    /**
     * Method to find the duration of a video file in milliseconds.
     * 
     * @param mp4File The <code>File</code> whose duration is desired.
     * @return The input's duration in milliseconds of 0 if an error occurs.
     * The return value is rounded to the nearest millisecond.
     */
    public static long getLengthOfMP4FileInMillis(File mp4File) {
        //Check for null parameter.
        if (mp4File == null) {
            WeatherLogger.log(Level.SEVERE,
                    "Video length function passed null");
            Debug.println("Video length function passed null");
            return 0;
        }
        
        //Use helper functions to gat result as string.
        ProcessBuilder pb = getProcessBuilder(mp4File);
        String resultAsString = runProcessAndGetOutput(pb);
        
        //Check for ffprobe error.
        if (resultAsString.equals(ERROR_STRING)) {
            Debug.println("No video length returned.");
            return 0;
        }
        
        //Parse string.
        int decimalIndex = resultAsString.indexOf(".");
        long secs = Long.parseLong(resultAsString.substring(0, decimalIndex));
        long millis = Long.parseLong(resultAsString.substring(decimalIndex + 1, 
                decimalIndex + 4));
        int roundingNumber = Integer.parseInt(String.valueOf(resultAsString
                .charAt(decimalIndex + 4)));
        if (roundingNumber >= 5) {
            millis++;
        }
        return (secs * 1000 + millis);
    }
    
    /**
     * Method that executes the ffprobe <code>Process</code> and returns the
     * result of the <code>Process</code> (The duration of the 
     * <code>File</code> provided via the <code>ProcessBuilder</code>) as
     * a <code>String</code>.
     * 
     * @param builder The <code>ProcessBuilder</code> that will start the 
     * ffprobe <code>Process</code>.
     * @return The result of the <code>Process</code> as a <code>String</code>.
     * Per ffprobe functionality. this will be the length of the provided file 
     * in seconds with a decimal portion that expresses the fractional part of
     * the duration to the nearest nanosecond.  (NOTE: This function returns the
     * private class constant ERROR_STRING if any errors occur.)
     */
    private static String runProcessAndGetOutput(ProcessBuilder builder) {
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
                    "Error starting video length process.", ex);
            Debug.println("Error starting video length process.");
            
            return ERROR_STRING;
        }
        
        //Construct readers.
        Scanner outputReader = new Scanner(new InputStreamReader(process
                .getInputStream()));
        Scanner errorReader = new Scanner(new InputStreamReader(process
                 .getErrorStream()));
        
        //Capture output if process is successful.
        String output = null;
        
        //Track if error was found, assume not.
        boolean errorFound = false;

        while (outputReader.hasNextLine()) {
            outputLine = outputReader.nextLine();
            Debug.println("OUTPUT: " + outputLine);
            
            //With one line of ffprobe output, this should only execute once
            output = outputLine;
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
                    "Video length process interrupted.", ex);
            Debug.println("Video length process interrupted.");
            
            //Close Scanners before reporting error.
            outputReader.close();
            errorReader.close();
            
            return ERROR_STRING;
        }
        
        //Close Scanners.
        outputReader.close();
        errorReader.close();
        
        //Check if error was reported from process error stream.
        if (errorFound) {
            //Log error.
            WeatherLogger.log(Level.SEVERE,
                    "Error in video length process.");
            Debug.println("Error in video length process.");
            return ERROR_STRING;
        } else {
            //No error were found, so return process output.
            return output;
        }
    }
    
    /**
     * Returns the <code>ProcessBuilder</code> for the <code>Process</code> that
     * will determine the input's length.
     * 
     * @param mp4File The <code>File</code> whose duration is desired.
     * @return The <code>ProcessBuilder</code> for the <code>Process</code> that
     * will determine the input's length.
     */
    private static ProcessBuilder getProcessBuilder(File mp4File) {
        ArrayList<String> wordsOfCommand = new ArrayList<>();
        String ffmpegPath = PropertyManager.getServerProperty("FFMPEG_PATH") 
                + "ffprobe";
        wordsOfCommand.add(ffmpegPath);
        wordsOfCommand.add("-v");
        wordsOfCommand.add("error");
        wordsOfCommand.add("-show_entries");
        wordsOfCommand.add("format=duration");
        wordsOfCommand.add("-of");
        wordsOfCommand.add("default=noprint_wrappers=1:nokey=1");
        wordsOfCommand.add(mp4File.getAbsolutePath());
         
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
        
        //Setup test data.
        File testData = new File("C:\\IVM Result 13\\Result.mp4");
        if (testData.exists()) {
            Debug.println("Test data exists.");
        } else {
            Debug.println("Test data does NOT exist.");
            return;
        }
        
        long length = getLengthOfMP4FileInMillis(testData);
        Debug.println("Returned time is millis: " + length);
        
        length = getLengthOfMP4FileInSeconds(testData);
        Debug.println("Returned time is seconds: " + length);
    }
}
