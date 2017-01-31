package weather.clientside.utilities;

import java.awt.Component;
import java.io.File;
import weather.common.data.resource.ResourceInstance;
import weather.common.utilities.CommonLocalFileManager;

/**
 * Contains client-size functions for saving files locally.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 * @version Spring 2010
 */
public class ClientSideLocalFileManager {
    
    /**
     * Saves file in location specified by user with WeatherFileChooser and
     * returns the saved file.
     *
     * @param defaultPath The default directory pathname, which is ensured in
     * this method.
     * @param defaultName The default filename.
     * @param resourceToSave The resource instance to be saved.
     * @param extension The default extension of the filename; ignored if null.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return The file just saved or null if no fill was saved.
     */
    public static File saveSpecifiedFileReturnFile(String defaultPath, 
            String defaultName, ResourceInstance resourceToSave, 
            String extension, Component parent) {
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.SAVE,
                new File(defaultPath), defaultName, "Save " + defaultName, 
                null, extension, parent);
        if (file != null) {
            boolean result = CommonLocalFileManager
                    .saveFileToDirectory(file.getParentFile(),
                    file.getName(), resourceToSave, parent);
            if (result) {
                return file;
            } else {
                return null;
            }
        }
        // user hit cancel or closed file chooser
        return null;
    }

    /**
     * Loads a file specified by the user with the WeatherFileChooser.
     *
     * @param defaultPath The path the file chooser will open directly to.
     * @param extension The default extension of the filename; ignored if null.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return The file selected by the user.
     */
    public static File loadSpecifiedFile(String defaultPath, String extension,
            Component parent) {
        return loadSpecifiedFile(new File(defaultPath), extension, parent);
    }

    /**
     * Loads a file specified by the user with the WeatherFileChooser
     *
     * @param defaultDir The directory the file chooser will open directly to.
     * @param extension The default extension of the filename; ignored if null.
     * @param parent The parent component of any JOptionPane dialog to be 
     * displayed.
     * @return The file selected by the user.
     */
    public static File loadSpecifiedFile(File defaultDir, String extension,
            Component parent) {
        return WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                defaultDir, null, null, null, extension, parent);
    }
}