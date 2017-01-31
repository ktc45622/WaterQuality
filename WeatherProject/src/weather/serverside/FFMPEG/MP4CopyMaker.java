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
 * This file contains a utility to create copy of an AVI or MOV video file which
 * is an MP4 video file with a smaller file size. This is done using ffmpeg. The
 * calling code must specify the full path, including the name, of the MP4 file
 * to be created.
 * @author Brian Bankes
 */
public class MP4CopyMaker {
    
    /**
     * Method to create an MP4 video file that is a copy of an AVI or MOV video
     * file using ffmpeg. The MP4 file will be smaller in size. The calling code
     * must specify the full path, including the name, of the file to be
     * created.
     * 
     * @param inputFile The input file to be copied, which must exist.
     * @param mp4File The MP4 file to be created.  It will be overwritten if it
     * exists.
     * @return True if the copying was successful; false otherwise.
     */
    public static boolean makeMP4Copy(File inputFile, File mp4File) {
        long startTime = System.currentTimeMillis();
        Debug.println("Starting MP4 Copier.");
        
        //Check for valid parameters.
        if (inputFile == null) {
            WeatherLogger.log(Level.SEVERE,
                    "MP4 maker passed null input file.");
            Debug.println(
                    "MP4 maker passed null input file.");
            return false;
        }
        if (!inputFile.exists()) {
            WeatherLogger.log(Level.SEVERE,
                    "MP4 maker passed nonexistent input file.");
            Debug.println(
                    "MP4 maker passed nonexistent input file.");
            return false;
        }
        if (mp4File == null) {
            WeatherLogger.log(Level.SEVERE,
                    "MP4 maker passed null MP4 file.");
            Debug.println(
                    "MP4 maker passed null MP4 file.");
            return false;
        }
        
        //Use helper functions to run ffmpeg process.
        ProcessBuilder pb = getProcessBuilder(inputFile, mp4File);
        boolean success = runProcess(pb);
        
        //If the MP4 file was made but the copying did not succeed, 
        //the file must be deleted.
        if (!success && mp4File.exists()) {
            mp4File.delete();
        }
        
        long endTime = System.currentTimeMillis();
        double elapsedSecs = ((double)(endTime - startTime)) / 1000;
        Debug.println("Ending MP4 Copier at " + elapsedSecs 
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
                    "Error starting MP4 video copy process.", ex);
            Debug.println("Error starting MP4 video copy process.");
            
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
                    "Process of MP4 copier interrupted.", ex);
            Debug.println("Process of MP4 copier interrupted.");
            
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
                    "Error in MP4 copying process.");
            Debug.println("Error in MP4 copying process.");
            return false;
        } else {
            //No error were found, so return process true.
            return true;
        }
    }
    
    /**
     * Returns the <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create the MP4 copy of the input video at the given path location.
     * 
     * @param inputFile The input file to be copied, which must exist.
     * @param mp4File The MP4 file to be created. It will be overwritten if it
     * exists.
     * @return The <code>ProcessBuilder</code> for the <code>Process</code> that
     * will create the low-quality copy of the video.
     */
    private static ProcessBuilder getProcessBuilder(File inputFile, 
            File mp4File) {
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
        wordsOfCommand.add("-fs");
        wordsOfCommand.add(PropertyManager
            .getServerProperty("MP4_FILE_SIZE") + "KB");
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
        
        //Specify test input, which must exist for a success.
        File testData = new File("C:\\Testing\\Test.mov");
        
        //Provide location and name of result file to be made.
        File targetFile = new File("C:\\Testing\\MOV Test 2.mp4");
        
        boolean result = makeMP4Copy(testData, targetFile); 
        if (result) {
            Debug.println("MP4 copying successful.");
        } else {
            Debug.println("MP4 copying NOT successful.");
        }
    }
}
