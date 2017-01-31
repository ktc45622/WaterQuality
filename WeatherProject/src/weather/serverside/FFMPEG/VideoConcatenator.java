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
 * This file contains a utility to concatenate an <code>ArrayList</code> of 
 * video files into a single file using ffmpeg. Because videos can have 
 * different dimensions, those of the output video must be specified.
 * 
 * @author Brian Bankes
 */
public class VideoConcatenator {
    
    /**
     * Method to concatenate an <code>ArrayList</code> of video files into a
     * single file.
     * 
     * @param inputFiles The <code>ArratList</code> of video files containing 
     * the input data.  For a successful outcome, all files must exist.
     * @param targetFile The output file to be created.  It will be overwritten
     * if it exists.
     * @param width The width of the video to be made in pixels, which must be
     * even for FFMPEG.
     * @param height The height of the video to be made in pixels, which must be
     * even for FFMPEG.
     * @return True if the concatenation was successful; false otherwise.
     */
    public static boolean concatenateVideoFiles(ArrayList<File> inputFiles, 
            File targetFile, int width, int height) {
        long startTime = System.currentTimeMillis();
        Debug.println("Starting Concatenator.");
        
        //Check for valid parameters.
        if (inputFiles == null) {
            WeatherLogger.log(Level.SEVERE,
                    "Video concatenation function passed null input list.");
            Debug.println(
                    "Video concatenation function passed null input list.");
            return false;
        }
        if (inputFiles.size() < 2) {
            WeatherLogger.log(Level.SEVERE,
                    "Video concatenation function passed input list of size "
                    + "under 2.");
            Debug.println(
                    "Video concatenation function passed input list of size "
                    + "under 2.");
            return false;
        }
        for (File file : inputFiles) {
            if (file == null) {
                WeatherLogger.log(Level.SEVERE,
                        "Video concatenation function passed null element in "
                        + "input list.");
                Debug.println(
                        "Video concatenation function passed null element in "
                        + "input list.");
                return false;
            }
            if (!file.exists()) {
                WeatherLogger.log(Level.SEVERE,
                        "Video concatenation function passed nonexistent " 
                        + "element in input list.");
                Debug.println(
                        "Video concatenation function passed nonexistent " 
                        + "element in input list.");
                return false;
            }
        }
        if (targetFile == null) {
            WeatherLogger.log(Level.SEVERE,
                    "Video concatenation function passed null target file.");
            Debug.println(
                    "Video concatenation function passed null target file.");
            return false;
        }
        if (width < 1 || width % 2 == 1 || height < 1 || height % 2 == 1) {
             WeatherLogger.log(Level.SEVERE,
                     "Video concatenation function passed dimensions that are "
                     + "not both positive even integers (" + width + "x" 
                     + height + ").");
             Debug.println(
                     "Video concatenation function passed dimensions that are "
                     + "not both positive even integers (" + width + "x" 
                     + height + ").");
            return false;
        }
        
        //Use helper functions to run ffmpeg process.
        ProcessBuilder pb = getProcessBuilder(inputFiles, targetFile, width,
                height);
       
        boolean success = runProcess(pb);
        
        //If the target file was made but the concatenation did not succeed, 
        //the file must be deleted.
        if (!success && targetFile.exists()) {
            targetFile.delete();
        }
        
        long endTime = System.currentTimeMillis();
        double elapsedSecs = ((double)(endTime - startTime)) / 1000;
        Debug.println("Ending Concatenator at " + elapsedSecs 
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
                    "Error starting video concatenation process.", ex);
            Debug.println("Error starting video concatenation process.");
            
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
                    "Video concatenation process interrupted.", ex);
            Debug.println("Video concatenation process interrupted.");
            
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
                    "Error in video concatenation process.");
            Debug.println("Error in video concatenation process.");
            return false;
        } else {
            //No error were found, so return process true.
            return true;
        }
    }
    
    /**
     * Returns the <code>ProcessBuilder</code> for the <code>Process</code> that
     * will concatenate the videos.
     * 
     * @param inputFiles The <code>ArratList</code> of video files containing 
     * the input data.  For a successful outcome, all files must exist.
     * @param targetFile The output file to be created.  For a successful
     * outcome, the file must NOT exist.
     * @param width The width of the video to be made in pixels, which must be
     * even for FFMPEG.
     * @param height The height of the video to be made in pixels, which must be
     * even for FFMPEG.
     * @return The <code>ProcessBuilder</code> for the <code>Process</code> that
     * will concatenate the videos.
     */
    private static ProcessBuilder getProcessBuilder(ArrayList<File> inputFiles, 
            File targetFile, int width, int height) {
        ArrayList<String> wordsOfCommand = new ArrayList<>();
        String ffmpegPath = PropertyManager.getServerProperty("FFMPEG_PATH") 
                + "ffmpeg";
        
        //Build command.
        wordsOfCommand.add(ffmpegPath);
        wordsOfCommand.add("-v");
        wordsOfCommand.add("error");
        wordsOfCommand.add("-y");
        for (File file : inputFiles) {
            wordsOfCommand.add("-i");
            wordsOfCommand.add(file.getAbsolutePath());
        }
        wordsOfCommand.add("-filter_complex");
        
        //Add to command to ensure all video hava the the specified image size.
        //A common SAR must also be set.
        //See ffmpeg documentation or SAR and filtergraphs.
        String sizeAndSAR = "scale=" + width + "x" + height + ",setsar=1:1";
        for (int i = 0; i < inputFiles.size(); i++) {
            String filterInput = "[" + i + ":v]"; 
            String filterOutput = "[v" + i + "];";
            if (i == 0) {
                wordsOfCommand.add("\"" + filterInput);
            } else {
                wordsOfCommand.add(filterInput);
            }
            wordsOfCommand.add(sizeAndSAR);
            wordsOfCommand.add(filterOutput);
        }
        for (int i = 0; i < inputFiles.size(); i++) {
            wordsOfCommand.add("[v" + i + "]");
        }
        
        //This "word" extended in SAR fix.
        wordsOfCommand.add("concat=n=" + inputFiles.size() + ":v=1:a=0\"");
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
        
        //Setup test data, which must exist for a success.
        File testData1 = new File("C:\\Testing Space\\LongVideo.mp4");
        File testData2 = new File("C:\\Testing Space\\LongVideo.mp4"); 
        File testData3 = new File("C:\\Testing Space\\LongVideo.mp4");
        
        //Provide location and name of result file to be made.
        File targetFile = new File("C:\\Testing Space\\MinuteVideo.mp4");
        
        //Make input array.
        ArrayList<File> inputFiles = new ArrayList<>();
        inputFiles.add(testData1);
        inputFiles.add(testData2);
        inputFiles.add(testData3);
        
        boolean result = concatenateVideoFiles(inputFiles, targetFile, 
                1296, 960); 
        if (result) {
            Debug.println("Concatenation successful.");
        } else {
            Debug.println("Concatenation NOT successful.");
        }
    }
}
