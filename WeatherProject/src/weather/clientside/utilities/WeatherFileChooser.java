package weather.clientside.utilities;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import weather.common.utilities.*;

/**
 * This class allows a <code>JFileChooser</code> to be displayed quickly
 * anytime one is needed throughout the program.
 * 
 * It can open a <code>JFileChooser</code> that will choose a directory only, or
 * a <code>JFileChooser</code> that can choose a file. A
 * specific file extension can be given for the file chooser to give it a
 * file filter, and to ensure that saved files have the extension.
 *
 * @author Bloomsburg University Software Engineering
 * @author Jason Hunter (2009)
 * @author Joe Van Lente (2010)
 *
 * @version Spring 2010
 */
public class WeatherFileChooser {
    private static final String DIALOG_TITLE = "Select File Location";

    // Using static final int to be compatible with JFileChooser implementation.
    public static final int OPEN = 0;
    public static final int SAVE = 1;
    
    private static CustomizedFileChooser fileChooser;
    private static JFileChooser directoryChooser;

    private static boolean isInitialized = false;

    /**
     * This method checks to see if the weatherFileChooser is initialized
     * or not.
     * @return True if the WeatherFileChooser is initialized, false otherwise.
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Initializes the settings of the file chooser.
     */
    public static void initialize() {
        //we have already initialized this class
        if(isInitialized) {
            return;
        }
        
        //we are initializing this class so set isInitialized to true.
        isInitialized=true;
        Debug.println("Initializing WeatherFileChooser.");
        fileChooser = new CustomizedFileChooser();
        directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    /**
     * Opens a JFileChooser for opening or saving files with no restrictions on
     * the displayed file types.
     *
     * @param type OPEN for opening files, SAVE for saving files.
     * @param defaultPath The default directory: Callers should ensure the
     * directory exists and can read/write.
     * @param dialogTitle The desired title of the dialog box.
     * @param parent The parent component of all dialogs to be displayed.
     * @return The file chosen by the user, or null if none if selected.
     */
    public static File openFileChooser(int type, File defaultPath,
            String dialogTitle, Component parent) {
        return openFileChooser(type, defaultPath, null, dialogTitle, null, null,
                parent);
    }

    /**
     * This opens a JFileChooser for opening or saving files with an optional
     * <code>FileFilter</code> or file type extension to restrict the files
     * that are shown.
     *
     * @param type OPEN for opening files, SAVE for saving files.
     * @param defaultPath The default directory: Callers should ensure the
     * directory exists and can read/write.
     * @param defaultName The default filename that will appear in the name
     * field but can be changed by the user.
     * @param dialogTitle The desired title of the dialog box.
     * @param filter A <code>FileFilter</code> to restrict the files shown to 
     * the user; this will be ignored if null.
     * @param extension The extension of the file type being opened or saved;
     * this will be ignored if null or if a <code>FileFilter</code> is 
     * specified by the previous parameter; otherwise the dialog box will have a
     * <code>FileFilter</code> set  showing only directories and files with the
     * extension.
     * @param parent The parent component of all dialogs to be displayed.
     * @return The file chosen by the user, or null if none if selected.
     */
    public static File openFileChooser(int type, File defaultPath,
            String defaultName, String dialogTitle, final FileFilter filter,
            final String extension, Component parent) {

        fileChooser.parent = parent;
        fileChooser.setDialogType(type);
        fileChooser.extension = extension;

        if (CommonLocalFileManager.ensureDirectory(defaultPath, type)) {
            if (defaultName == null) {
                fileChooser.setCurrentDirectory(defaultPath);
            } else {
                fileChooser.setSelectedFile(new File(defaultPath, defaultName));
            }
        } else {
            fileChooser.setCurrentDirectory(new File(PropertyManager.getDefaultProperty("CORE_DIR")));
        }

        // If dialogTitle is null, set the title to the default title.
        if (dialogTitle == null) {
            fileChooser.setDialogTitle(DIALOG_TITLE);
        } // If dialogTitle is not null, set the JFileChooser's title to the 
        // text of dialogTitle.
        else {
            fileChooser.setDialogTitle(dialogTitle);
        }
 
        // Add file filter.
        if (filter != null) {
            fileChooser.setFileFilter(filter);
        } else if (extension != null) {
        // Create a filter using the the given extension.
            fileChooser.setFileFilter(
                    new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(extension);
                }

                @Override
                public String getDescription() {
                    return extension + " files";
                }
            }
            );
        }

        File file = null;
        int returnValue = fileChooser.showDialog(parent, null);

        //Check if the user hit ok or cancel.
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        fileChooser.reset();

        return file;
    }

    /**
     * This will open the JFileChooser to choose directories. The function
     * returns a String representation of the path the user has chosen. A return
     * value of null indicates that the user hit cancel.
     *
     * @param defaultPath If a defaultPath is supplied, the JFileChooser will be
     * opened to that path. You can pass in null if you do not want to specify a
     * default path.
     * @param dialogTitle The dialog title of the file chooser. Can be null.
     * @param approveButtonText The text for the approve button. Can be null
     * @param parent The parent component of all dialogs to be displayed.
     * @return The String representation of the path the user has chosen, or
     * null if the user hit cancel.
     */
    public static String openDirectoryChooser(File defaultPath,
            String dialogTitle, String approveButtonText, Component parent) {

        String path = null;
        int returnValue;

        //If defaultpath is not null, set the JFileChooser to open to that path.
        if (defaultPath != null) {
            directoryChooser.setCurrentDirectory(defaultPath);
        }

        //If dialogTitle is not null, set the JFileChooer's title to the text of dialogTitle.
        if (dialogTitle != null) {
            directoryChooser.setDialogTitle(dialogTitle);
        } //If dialogTitle is null, set the title to the default title.
        else {
            directoryChooser.setDialogTitle(DIALOG_TITLE);
        }

        //If okButtonName is not null, set the Ok button to the text of okButtonName.
        if (approveButtonText != null) {
            returnValue = directoryChooser.showDialog(parent, approveButtonText);
        } //If a okButtonName is not specified, it defaults to Ok.
        else {
            returnValue = directoryChooser.showDialog(parent, "OK");
        }

        //Check if the user hit ok or cancel.
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            path = directoryChooser.getSelectedFile().toString();
        }
        if (returnValue == JFileChooser.CANCEL_OPTION) {
            path = null;
        }

        return path;
    }
}

/**
 * This class extends JFileChooser for the purpose of checking whether a chosen
 * file already exists, and prompting the user for appropriate action, before
 * the chooser is closed. This is done by overriding the approveSelection()
 * method.
 *
 * @author jtv73044
 */
class CustomizedFileChooser extends JFileChooser {
    // This unnamed file object is used to reset the selected file property 
    //of the file chooser.

    final File UNNAMED_FILE = new File("");
    String extension = null;
    Component parent = null;

    /**
     * This is called at the time the user hits the approve button, and before
     * the file chooser closes; overridden from JFileChooser class.
     */
    @Override
    public void approveSelection() {
        if (getDialogType() == WeatherFileChooser.SAVE) {
            File file = getSelectedFile();
            if (extension != null && !file.getName().endsWith(extension)) {
                file = new File(file.getPath() + extension);
                setSelectedFile(file);
            }
            if (file.exists()) {
                int resp = JOptionPane.showConfirmDialog(parent,
                        "Replace existing file?", "File Exists",
                        JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.NO_OPTION) {
                    return;
                }
                file.delete();
            }
        }
        super.approveSelection();
    }

    /**
     * Resets file chooser's properties so they're not showing the next time it
     * is opened.
     */
    public void reset() {
        setSelectedFile(UNNAMED_FILE);
        setDialogTitle(null);
        resetChoosableFileFilters();
    }
}
