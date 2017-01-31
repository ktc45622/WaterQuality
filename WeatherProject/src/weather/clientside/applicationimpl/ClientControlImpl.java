package weather.clientside.applicationimpl;

import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.ClientControlSystem;
import weather.GeneralService;
import weather.clientside.gui.client.LoginWindow;
import weather.clientside.gui.client.MainApplicationWindow;
import weather.clientside.utilities.LocalFileCleaner;
import weather.clientside.utilities.ResourceTreeManager;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.TimedLoader;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.StopWatch;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class implements the ClientControlSystem interface.
 * This class contains the login functionality and starts 
 * the MainApplicationWindow.  This class uses a General Service object to keep
 * the user information and a reference to our Database implementation system.
 * @see weather.GeneralService
 *
 * 
 * 
 * @author Bloomsburg University Software Engineering
 * @author Jacob Kelly Spring (2007)
 * @author David Lusby (2008)
 * @author Tom Hand (2007)
 * @author Mike Graboske (2008)
 * @author Joe Sharp (2009)
 * @version Spring 2009
 */


public class ClientControlImpl implements ClientControlSystem {

    /**
     * The main GUI being displayed.
     */
    private MainApplicationWindow mainApplicationWindow;
    /**
     * This ClientControlImpl's ClientService object.
     * Currently, this only seems to handle logging in. This object was planned
     * to be merged with this one, but that never happened.
     */
    private GeneralService generalService;
    /**
     * This variable indicates whether or not the user has picked "selective"
     * login mode.
     */
    private boolean isSelectiveModePicked = false;
    
    /**
     * Creates a new ClientControlImpl object. The general service
     * object is set and a login window is created. 
     * @param generalService The back-end general service object.
     */
    public ClientControlImpl(GeneralService generalService) {
        this.generalService = generalService;
    }

    

    /**
     * Sets the General Service object used by this implementation of the
     * GUI.
     * a login window with this general services object.
     * @param generalservice The General Service object for this
     * implementation of our GUI.
     */
    @Override
    public void setGeneralService(GeneralService generalservice) {
        this.generalService = generalservice;
    }
    

    /**
     *  Enables the MainApplicationWindow.
     *  Used when returning from an ExternalPlayerWindow
     * (The MainApplicationWindow is disabled when the
     *  ExternalPlayerWindow is created).
     */
    @Override
    public void enableMainGUI() {
        mainApplicationWindow.setEnabled(true);
    }

    

    /** 
     * Displays login window, which sets the user
     * information for the application.
     */
    @Override
    public void login() {
        try {
            EventQueue.invokeAndWait(new Runnable(){
                @Override
                public void run() {
                    showLogin();
                }
            });//end of  EventQueue.invokeLater
        } catch (InterruptedException | InvocationTargetException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not display the login window", ex);
            //Set GeneralWeather.properties to indicate the program is not running.
            PropertyManager.setGeneralProperty("ClientRunning", "false");
            System.exit(1);
        }

    }
    
    /**
     * Shows the login screen.
     */
    private void showLogin() {
        LoginWindow loginWindow = new LoginWindow(generalService, this);
        loginWindow.display(); // System hangs here until window is closed.
    }

    /**
     * Creates a new MainApplicationWindow with a reference to a 
     * ClientControlImpl.  It gets a list of Resource objects and sets the
     * list in the MainApplicationWindow object.  After getting the
     * StorageControlSystem object from the DBMSSystem object, it uses that
     * object to get a list of ResourceInstances by providing the list of 
     * Resources and their types.
     * @param appcontrolsystem The application control system.
     * @throws WeatherException
     */
    @Override
    public void mainApplicationControlService(final ApplicationControlSystem appcontrolsystem)
                throws WeatherException {
        StopWatch mainSW = null;
        if(Debug.isEnabled()){
              Debug.println("Starting stop watch");
              mainSW = new StopWatch();
              mainSW.start();
        }

        //Start TimedLoader
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Bloomsburg Weather Viewer";
            }

            @Override
            protected void doLoading() {
                //Set the default range for downloading resources if not in selective
                //login mode
                if (!isSelectiveModePicked) {
                    Debug.println("Setting default resource range.");
                    ResourceTimeManager.setResourceRange(ResourceTimeManager
                            .getDefaultRange());
                }

                //Test for space to write files.
                StorageSpaceTester.testApplicationHome();

                //Load resource tree
                ResourceTreeManager.initializeData();

                Debug.println("Constructing new MainApplicationWindow.");
                mainApplicationWindow = 
                        new MainApplicationWindow(appcontrolsystem,
                        false, null);
            }    
        };
        loader.execute();

        if(Debug.isEnabled())  {
            mainSW.stop();
            Debug.println("Total Application Loading Time: " +
                    mainSW.getElapsedTime()+"ms");
        }
    }

    /**
     * Checks to see if this ClientControlImpl is currently in debug mode.
     * @return True if this ClientControlImpl is in debug mode.
     */
    @Override
    public boolean isDebug() {
        return Debug.isEnabled();
    }


    /**
     * Helper function to display dialog box to quit.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed
     */
    private static boolean isUserQuitting(Component parent) {
        JOptionPane pane = new JOptionPane("Are you sure you wish to exit?", JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Exit Program");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if(selectedValue == null){
            return false;
        }
        if(selectedValue instanceof Integer){
            int intValue = ((Integer)selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
    
    /**
     * Specifies the signature of a method to close our program. The user is
     * notified and given the choice to return to the program if notifyUser is
     * true. If notifyUser is false, then the program immediately terminates.
     * Use system.exit(0)if notifyUser is true and System.exit(-1) if notifyUser
     * is false.
     *
     * @param appControl The program's application control system.
     * @param notifyUser Boolean to notify the user when the program is about to
     * close.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed
     */
    @Override
    public void closeProgram(ApplicationControlSystem appControl, 
        boolean notifyUser, Component parent) {
        if (notifyUser) {
            if (isUserQuitting(parent)) {
                //Close the movie sessions -- just in case
                if (mainApplicationWindow.getMovieController() != null) {
                    mainApplicationWindow.getMovieController().cleanup();
                }
                LocalFileCleaner.cleanup(appControl, false);
                this.generalService.getDBMSSystem().closeDatabaseConnections();
                //Set GeneralWeather.properties to indicate the program is not running.
                PropertyManager.setGeneralProperty("ClientRunning", "false");
                System.exit(0);
            }
        } else {
            //Close the movie sessions -- just in case
            if (mainApplicationWindow.getMovieController() != null) {
                mainApplicationWindow.getMovieController().cleanup();
            }
            LocalFileCleaner.cleanup(appControl, false);
            this.generalService.getDBMSSystem().closeDatabaseConnections();
            //Set GeneralWeather.properties to indicate the program is not running.
            PropertyManager.setGeneralProperty("ClientRunning", "false");
            System.exit(0);
        }
    }
    
    /**
     * Sets a flag to notify the instance if selective mode was picked 
     * during the login process.  The flag must be kept current in order for
     * the initial time span of the main window to be correct.
     * @param aFlag The value of the flag.
     */
    @Override
    public void setSelectiveModePicked(boolean aFlag) {
        isSelectiveModePicked = aFlag;
    }
}
