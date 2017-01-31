package weather;

import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import weather.clientside.utilities.BarebonesBrowser;
import weather.clientside.utilities.CreateApplicationControlSystemThread;
import weather.common.data.version.Version;
import weather.common.gui.component.IconProperties;
import weather.common.gui.component.SplashScreenWindow;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.ScreenInteractionUtility;
import weather.common.utilities.WeatherLogger;

/**
 * This object will start the application. It will first display a splash
 * screen, then load the database. It then runs the application control system,
 * which handles the rest.
 *
 * Copyright -- Dr. Jeff Brunskill, Dr. Curt Jones, Bloomsburg University All
 * Rights reserved.
 *
 * @author Bloomsburg University Software Engineering
 * @version Spring 2014
 */
public class MainProgram {

    public static void main(String[] args) {
        // Creates a splash screen that will be displayed until the user presses
        // a key or clicks a mouse button.
        new SplashScreenWindow();
        Debug.println("Splash Screen closed");
        
        // Sets the look and feel to that of the system being used.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            WeatherLogger.log(Level.SEVERE, "Error setting look and feel.", ex);
        }
        
        Debug.setEnabled(true);
        Debug.println("Turning debugging on in the first line of the main program");
        
        Debug.println("Setting ClientRunning Property...");
        /*
         * Set GeneralWeather.properties to indicate that a client-side
         * jar is running.  This is necessary to keep the installer or
         * uninstaller from running while a client-side jar is also 
         * running.
         */
        PropertyManager.configure();
        PropertyManager.setGeneralProperty("ClientRunning", "true");
        Debug.println("Running Tool Tip code...");
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        Debug.println("Setting menus over canvases.");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        Debug.println("Setting VLC...");
        boolean found = new NativeDiscovery().discover();
        if (found) {
            Debug.println("VLC found: " + LibVlc.INSTANCE.libvlc_get_version());
        } else {
            Debug.println("VLC NOT FOUND!");
            //Set GeneralWeather.properties to indicate the program is not running.
            PropertyManager.setGeneralProperty("ClientRunning", "false");
            System.exit(-1);
        }
        Debug.println("Starting MainProgram.java");

        // Make database connection and download data while splash is showing.
        CreateApplicationControlSystemThread appControlSystemThread =
                new CreateApplicationControlSystemThread();
        appControlSystemThread.start();

        try {
            // Wait for appControlSystemThread to finish.
            Debug.println("About to try join");
            appControlSystemThread.join();
            Debug.println("After join");
            ApplicationControlSystem applicationControl =
                    appControlSystemThread.getApplicationControlSystem();
            Debug.println("after get");
            if (applicationControl == null) {
                Debug.println("application control is null");
            } else {
                Debug.println("application control is NOT null");
            }
            
            // Check program version.
            if (applicationControl != null && applicationControl
                    .getDBMSSystem().isBUConnectionOpen()) {
                Version thisVersion = Version.parseVersion(PropertyManager
                        .getGeneralProperty("PROGRAM_VERSION"));
                Version newestDBVersion = applicationControl.getDBMSSystem()
                        .getVersionManager().getMostResentVersion();

                // Check if major or minor version is changed, 
                if (newestDBVersion.isNewerVersionThan(thisVersion)) {
                    askIfUserWantsToUpdate(applicationControl);
                }
            } else {
                Debug.println("Bypassing Version Check -- No BU Connection.");
            }
            
            // Start the main application
            //Debug.println("Control Systems are created \nMain Application Starting ... ");
            applicationControl.mainApplicationControlService();
            Debug.println("Main program class has finished executing... ");

        } catch (OutOfMemoryError mex) {
            //StorageSpaceTester should stop cose from getting here.
            WeatherLogger.log(Level.SEVERE, "Program ran out of memory.", mex);
            WeatherException exception = new WeatherException(18, true, mex);
            exception.show("Program ran out of memory.");
        } catch (InterruptedException ex) {
            WeatherLogger.log(Level.SEVERE, "InterruptedException in Main program.", ex);
            WeatherException exception = new WeatherException(0, true, ex);
            exception.show("Fatal exception in Main program.");
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "An unhandled WeatherException has occurred in Main.", ex);
            ex.show();
        }
        /*
         catch (Exception ex) {
         WeatherLogger.log(Level.SEVERE, "Fatal exception in Main program.", ex);
         WeatherException exception = new WeatherException(0, true,ex);
         exception.show("Fatal exception in Main program.");
         } */
    }
    
    /**
     * Helper function to ask the user a question with <code>JOptionPane</code>.
     * 
     * @param message The message of the <code>JOptionPane</code>.
     * @param title The title of the <code>JOptionPane</code>.
     * @return True if user answers yes; False otherwise.
     */
    private static boolean askUserQuestion(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        ScreenInteractionUtility.positionWindow(dialog, true);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue);
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
    
    /**
     * Helper function to ask if the user wants to close the program when a
     * significant update is available. This function is to be called when
     * either the major or miner version is changed, as should be tested before
     * this function is called. When requested, a browser is opened to the
     * client-side download web page.
     * 
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     */
    private static void askIfUserWantsToUpdate(ApplicationControlSystem appControl) {
        // Specify message for dialog box.
        String message = "A newer version of the Bloomsburg University Weather\n"
                + "Viewer is available.  Would you like to close the program\n"
                + "and download an update?";

        // Ask if user wants to update program.
        if (askUserQuestion(message, "New Version Available")) {
            // Open web page after message is closed.
            String updateURL = PropertyManager
                    .getGeneralProperty("URL_for_Client_Downloads");
            BarebonesBrowser.openURL(updateURL, null);

            // Close database connections.
            appControl.getGeneralService().getDBMSSystem()
                    .closeDatabaseConnections();

            // End program.
            
            //Set GeneralWeather.properties to indicate the program is not running.
            PropertyManager.setGeneralProperty("ClientRunning", "false");
            System.exit(0);
        }
    }
}
