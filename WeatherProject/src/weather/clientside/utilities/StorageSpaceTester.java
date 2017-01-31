package weather.clientside.utilities;

import java.io.File;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import weather.clientside.gui.client.LocalDirectoryWindow;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * This class is designed for testing client-side storage space.
 * @author Brian Bankes
 */
public class StorageSpaceTester {
    //This prevents multiple warnings if the user doesn't change directory.
    private static boolean notifyUserAtWarningLevel = true;
    
    
    /**
     * Tests the current application home for available space and access.
     * It will terminate the program for either reason and also has a space
     * warning level which will trigger a warning when first crossed.  The flag
     * that triggers the warning must be reset if the directory is changed.
     */
    public static void testApplicationHome() {
        //Get memory cutoff levels.
        long bytesForRecommendedChange = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_RECOMMENDED"));
        long bytesForRequiredChange = Long.parseLong(PropertyManager
                .getGeneralProperty("DIRECTORY_CHANGE_REQUIRED"));
        
        //Get application home.
        String applicationHomePath = PropertyManager
                .getLocalProperty("CORE_DIR") + File.separator 
                + PropertyManager.getLocalProperty("ROOT_DIR");
        
        //Get location to test.
        File applicationHome = new File(applicationHomePath);
        
        //If ROOT_DIR is not pessent, mut directly test stored path.
        if (!applicationHome.exists()) {
            applicationHomePath = PropertyManager.getLocalProperty("CORE_DIR");

            //Get location to test.
            applicationHome = new File(applicationHomePath);
        }
        
        //Check for disk access.
        if (!applicationHome.canWrite() || !applicationHome.canRead()) {
            String driveLetter = applicationHomePath.substring(0, 1);
            String message = "You data on drive " + driveLetter
                    + " could not be accessed.\nWould you like"
                    + " to try again?\nIMPORTANT: Choosing \"NO\' will end"
                    + " the program!";
            String title = "Data Transfer Error";
            boolean result = StorageSpaceTester
                    .askUserStorageQuestion(message, title);
            if (result) {
                //Try again.
                testApplicationHome();
                return;
            } else {
                //Set GeneralWeather.properties to indicate the program is not running.
                PropertyManager.setGeneralProperty("ClientRunning", "false");
                System.exit(-1);
            }
        }
        
        //Check for available disk space.
        if (applicationHome.getUsableSpace() < bytesForRequiredChange) {
            String message =
                    "The currrent storage location does not have emough space\n"
                    + "available.  Please press OK to select a different"
                    + " location.";
            String tilte = "Very Low Memory";
            showStorageMessage(message, tilte);
            new LocalDirectoryWindow(true);
            //Stop this call.
            return;
        }
        
        
        if (notifyUserAtWarningLevel && applicationHome.getUsableSpace() 
                < bytesForRecommendedChange) {
            notifyUserAtWarningLevel = false;
            String message = 
                    "The currrent storage location is low on memory.  Would\n"
                    + "you like to select a different location.";
            String title = "Low Memory";
            boolean result = askUserStorageQuestion(message, title);
            if (result) {
                new LocalDirectoryWindow(false);
            }
        }
    }
    
    /**
     * Resets the flag that triggers the warning for low memory. 
     */
    public static void resetTestFlag() {
        notifyUserAtWarningLevel = true;
    }
    
    /**
     * Helper function to ask the user a question about storage that can always 
     * be seen.
     * 
     * @param message The message in the box.
     * @param title The title of the box.
     * @return True if the user answers yes, false otherwise.
     */
    public static boolean askUserStorageQuestion(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setAlwaysOnTop(true);
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
     * Helper function to show an information message about storage that can 
     * always be seen.
     * 
     * @param message The message in the box.
     * @param title The title of the box.
     */
    public static void showStorageMessage(String message, String title) {
        JOptionPane pane = new JOptionPane(message, 
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setAlwaysOnTop(true);
        ScreenInteractionUtility.positionWindow(dialog, true);
        dialog.setVisible(true);
    }
}
