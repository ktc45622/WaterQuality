package weather;

import java.awt.Component;
import weather.common.utilities.WeatherException;

/**
 * Specifies general GUI control operations, such as handling the starting of
 * the main program, the login process, and keeping track of the debug status
 * of the application.
 * 
 * Debugging code in this system should be surrounded by ...
 * <code>
 * if ( ClientControlSystem.isDebug() ){
 *   // debugging code
 * }
 * </code>
 *
 * @author Bloomsburg University Software Engineering
 * @author David Lusby (2008)
 * @version Spring 2008
 */


public interface ClientControlSystem {

    /**
     * Specifies the signature of a method used to set this client's
     * general backend service provider.
     * @param generalService the backend service provider.
     */
    public void setGeneralService(GeneralService generalService);

    /**
     * Specifies signature of method used to enable the main GUI.
     */
    public void enableMainGUI();
	
    /**
     * Specifies the signature of the method to implement the 
     * Login use case. It requires no parameters.
     *
     */
    public void login();

     /**
      * Specifies the signature of the method to implement the
      * Main Application Control use case.
      *
      * @param applicationControlService the new application control system to use.
      * @throws WeatherException
     */
    public void mainApplicationControlService(
            ApplicationControlSystem applicationControlService) 
            throws WeatherException;

  
    /**
     * Specifies the signature of a method that returns whether or not this
     * program is in debugging mode.
     *
     * TODO remove this when new client is rolled out (02/08)
     *
     * @return true if debugging mode is on, false otherwise.
     */
    public boolean isDebug();


   /**
     * Specifies the signature of a method to close our program. The user
     * is notified and given the choice to return to the program if notifyUser is true.
     * If notifyUser is false, then the program immediately terminates. Use
     * system.exit(0)if notifyUser is true and System.exit(-1) if notifyUser is false.
     *
     * @param appControl The program's application control system.
     * @param notifyUser Boolean to notify the user when the program is about to
     * close.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     */
    public void closeProgram(ApplicationControlSystem appControl,
            boolean notifyUser, Component parent);
    
    /**
     * Sets a flag to notify the instance if selective mode was picked during
     * the login process. The flag must be kept current in order for the initial
     * time span of the main window to be correct.
     * @param aFlag The value of the flag.
     */
    public void setSelectiveModePicked(boolean aFlag);
}