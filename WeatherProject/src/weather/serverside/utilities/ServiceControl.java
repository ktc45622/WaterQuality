/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.serverside.utilities;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class is used to interact with services in Windows.  The service 
 * control command used is sc, which takes command in the form: 
 * <pre>sc <command> <service></pre> 
 *
 * TODO: Add weather trace and logging trackers.
 * TODO: Consider moving service status constants into enum
 * 
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class ServiceControl {
    private static final String SERVICE_COMMAND = "sc";
    private static final String QUERY = "query";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String SERVICE_STATE = "STATE";
    
    public static final String RUNNING= "Running";
    public static final String STOPPED= "Stopped";
    public static final String START_PENDING="Start_Pending";
    public static final String STOP_PENDING="Stop_Pending";
    public static final String UNKOWN="Unknown";
    
    /**
     * Stops the given service, if it is not already stopped.
     * @param serviceName The name of the service to stop.
     */
    public static void stopService(WeatherServiceNames serviceName) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                SERVICE_COMMAND,STOP,serviceName.getShortName());
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException ex) {

        }
    }
    
    /**
     * Starts the given service, if it is not already started.
     * @param serviceName The name of the service to start.
     */
    public static void startService(WeatherServiceNames serviceName){
        ProcessBuilder processBuilder = new ProcessBuilder(
                SERVICE_COMMAND,START,serviceName.getShortName());

        try {
            Process process = processBuilder.start();
        } catch (IOException ex) {

        }
    }
    
    /**
     * Stops the given service, then starts it again.
     * @param serviceName The name of the service to restart.
     */
    public static void restartService(WeatherServiceNames serviceName){
        stopService(serviceName);
        while(!isRunning(serviceName)){
            startService(serviceName);
        }
    }

    /**
     * Checks the status of the current service.
     * @param serviceName The name of the service to check.
     * @return The state of the service, or an empty string if there is an error.
     */
    public static String checkService(WeatherServiceNames serviceName) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                SERVICE_COMMAND,QUERY,serviceName.getShortName());

        try {
            Process process = processBuilder.start();

            Scanner s = new Scanner(process.getInputStream());
            
            while (s.hasNextLine()) {
                String line = s.nextLine();
                
                if (line.contains(SERVICE_STATE)) {
                    String[] tokens = line.split(" ");

                    for (String st : tokens) {
                        if (!st.equals(SERVICE_STATE) && st.length() > 1) {
                            return getStatus(st);
                        }
                    }
                }
            }

        } catch (IOException ex) {
            return "Error checking status.";
        }
        return "Unknown";
    }

    public static boolean isRunning(WeatherServiceNames service){
        if(!checkService(service).equalsIgnoreCase(RUNNING)){
            return false;
        }
        return true;
    }
    
    /**
     * Returns a display-friendly version of the status.
     * @param statusToConvert The system status as taken from the environment.
     * @return A proper-caps version of the string for display purposes. 
     */
    private static String getStatus(String statusToConvert){
        if(statusToConvert.equalsIgnoreCase(RUNNING)){
            return RUNNING;
        }
        else if(statusToConvert.equalsIgnoreCase(STOPPED)){
            return STOPPED;
        }
        return statusToConvert;
    }
}
