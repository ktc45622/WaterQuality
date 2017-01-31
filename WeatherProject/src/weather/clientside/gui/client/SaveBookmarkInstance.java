package weather.clientside.gui.client;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.utilities.Debug;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;

/**
 * This class allows a guest or student to save their bookmark instance to their
 * local hard drive.
 *
 * @author Alex Funk
 * @version 2011
 */
public class SaveBookmarkInstance {

    private static final long serialVersionUID = 1L;
    private Bookmark bookmark;
    private BookmarkDuration type;
    /**
     * The file path of the saved Bookmark. This includes the files name and
     * extension. If a problem occurred or a Bookmark was not saved, then
     * <code>saveLocation</code> is
     * <code>null</code>.
     */
    public String filePath;

    /**
     * Constructs a new SaveBookmarkInstance with a given bookmark instance to
     * be saved to the local hard drive.
     *
     * @param bookmark The bookmark instance to be saved.
     * @param type Whether the bookmark is a simple instance or an event.
     */
    public SaveBookmarkInstance(Bookmark bookmark, BookmarkDuration type) {
        this.bookmark = bookmark;
        filePath = null;
        this.type = type;
    }

    /**
     * Saves a Bookmark object to the local file system.
     *
     * @param parentComponent The parent component which the dialog will display
     * over.
     */
    public void saveBookmark(Component parentComponent) {
        String extension = PropertyManager.getGUIProperty("Bookmark");
        String typeName = "Bookmark";
        if (type == BookmarkDuration.event) {
            extension = PropertyManager.getGUIProperty("Event");
            typeName = "Event";
        }
        // TODO Make Events have their own folder.
        File defaultPath = new File(CommonLocalFileManager.getBookmarksDirectory());
        String defaultName;

        if (bookmark.getBookmarkNumber() <= Integer.parseInt(PropertyManager.
                getLocalProperty("LOCAL_BOOKMARKS_INITIAL_NUMBER"))) {
            defaultName = bookmark.getName().trim();
            Debug.println("Local file manager's default path is "
                    + defaultPath.toString());
            File file = WeatherFileChooser.openFileChooser(
                    WeatherFileChooser.SAVE, defaultPath, defaultName,
                    "Save " + typeName, null, extension, parentComponent);
            if (file != null) {
                File old = new File(PropertyManager.getBookmarkProperty(
                        Integer.toString(bookmark.getBookmarkNumber())));
                if (old.exists()) {
                    old.delete();
                }
                if (writeBookmark(file.getPath())) {
                    JOptionPane.showMessageDialog(parentComponent, typeName 
                            + " was saved as: \n" + file.getPath(), typeName 
                            + " Saved", JOptionPane.INFORMATION_MESSAGE);
                    filePath = file.getPath();
                    PropertyManager.setBookmarkProperty(Integer
                            .toString(bookmark.getBookmarkNumber()),
                            filePath);
                } else {
                    JOptionPane.showMessageDialog(parentComponent, 
                            typeName + " was NOT saved.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            String property = PropertyManager.
                    getBookmarkProperty("next");
            int nextNum = Integer.parseInt(property);
            bookmark.setBookmarkNumber(nextNum);
            defaultName = bookmark.getName().trim();
            Debug.println("Local file manager's default path is "
                    + defaultPath.toString());
            File file = WeatherFileChooser.openFileChooser(
                    WeatherFileChooser.SAVE, defaultPath, defaultName,
                    "Save " + typeName, null, extension, parentComponent);
            if (file != null) {
                if (writeBookmark(file.getPath())) {
                    this.writeBookmark(file.getPath());
                    JOptionPane.showMessageDialog(parentComponent, typeName 
                            + " was saved as: \n" + file.getPath(), typeName 
                            + " Saved", JOptionPane.INFORMATION_MESSAGE);
                    filePath = file.getPath();
                    PropertyManager.setBookmarkProperty("next",
                            Integer.toString(nextNum - 1));
                    PropertyManager.setBookmarkProperty(Integer.toString(nextNum), filePath);
                } else {
                    JOptionPane.showMessageDialog(parentComponent,
                            typeName + " was NOT saved.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Saves the bookmark to the given path.
     *
     * @param path The path to which the bookmark should be saved.
     * @return Whether or not the save was successful.
     */
    private boolean writeBookmark(String path) {
        boolean result;
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(bookmark);
            result = true;
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, ex.toString());
            result = false;
        }
        //Test for remaining space in application home, which has
        //no effect if the save was not there.
        StorageSpaceTester.testApplicationHome();

        return result;
    }
}
