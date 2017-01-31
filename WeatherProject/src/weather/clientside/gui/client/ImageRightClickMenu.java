package weather.clientside.gui.client;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.TransferableImage;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.resource.Resource;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherLogger;

/**
 * This file contains a right click menu to be attached to images.
 *
 * @author Brian Bankes
 */
public class ImageRightClickMenu extends JPopupMenu {

    private BufferedImage image;
    private String extension;
    private PreviewImage previewImage;
    private Component parent;
    //Save paths
    private static String defaultPath = CommonLocalFileManager
            .getPictureDirectory();
    private static String userDefinedPath = CommonLocalFileManager
            .getPictureDirectory();

    /**
     * Creates a right click menu to be attached to an image.
     *
     * @param image The image.
     * @param previewImageTitle The name of the file for the title bar of a
     * PreviewImage. (can be null)
     * @param extension The file extension denoting the type of image. (includes
     * leading dot)
     * @param resource The resource providing the image. (null if N/A)
     * @param date The time of the image. (null if N/A)
     * @param canBeEnlarged True if right click menu should have enlarge option;
     * false otherwise.
     * @param parent The window on which this menu appears. (should only be null
     * if the window does not yet exist.)
     */
    public ImageRightClickMenu(BufferedImage image, String previewImageTitle,
            String extension, Resource resource,
            Date date, boolean canBeEnlarged, Component parent) {
        this.parent = parent;
        this.image = image;
        this.extension = extension;
        previewImage = new PreviewImage(image, previewImageTitle, extension,
                resource, date);

        //Copy option
        Icon copyIcon = IconProperties.getCopyIcon();
        JMenuItem copy = new JMenuItem("Copy", copyIcon);
        this.add(copy);
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyAction();
            }
        });

        //Enlarge option
        if (canBeEnlarged) {
            Icon enlargeIcon = IconProperties.getExternalWindowIconImage();
            JMenuItem enlarge = new JMenuItem("Enlarge and Preview", enlargeIcon);
            this.add(enlarge);
            enlarge.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enlargeAction();
                }
            });
        }
        
        //Save option
        Icon saveIcon = IconProperties.getSaveIconImage();
        JMenuItem save = new JMenuItem("Save", saveIcon);
        this.add(save);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAction();
            }
        });
        
        //Print option
        Icon printIcon = IconProperties.getSnapshotPrintIconImage();
        JMenuItem print = new JMenuItem("Print", printIcon);
        this.add(print);
        print.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printAction();
            }
        });
    }

    /*
     * This function copies the snapshot image to the system clipboard
     */
    public void copyAction() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        TransferableImage transferableImage =
                new TransferableImage(image);
        clipboard.setContents(transferableImage, null);
    }

    /**
     * This function shows the full preview image form.
     */
    public void enlargeAction() {
        previewImage.showFrom(false);
    }

    /**
     * This function saves the image.
     */
    public void saveAction() {
        //Make defult save name.
        String storageFileName = previewImage.makeFileName();

        //Use the user defined path unless it doesn't exist.
        File dir = new File(userDefinedPath);
        if (!dir.isDirectory()) {
            dir = new File(defaultPath);
        }

        // Save the file using a GUI.
        File file = WeatherFileChooser.
                openFileChooser(WeatherFileChooser.SAVE, dir, storageFileName, 
                        "Save " + storageFileName, null, extension, parent);
        if(file == null){
            return;
        }
        try {
            ImageIO.write(image, extension.substring(1), file);
            JOptionPane.showMessageDialog(parent, "File saved successfully.",
                    "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            //Test for remaining space in application home, which has
            //no effect if the save was not there.
            StorageSpaceTester.testApplicationHome();
        } catch (IOException | HeadlessException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(parent, "File not saved successfully.",
                    "Save Unsuccessful", JOptionPane.ERROR_MESSAGE);
            //Test for remaining space in application home, which has
            //no effect if the save was not there.
            StorageSpaceTester.testApplicationHome();
        }
    }

    /**
     * This function prints the image.
     */
    public void printAction() {
        //Use print code from PreviewImage
        previewImage.printSnapshotFile(image.getWidth(), image.getHeight(), 
                previewImage.makeFileName());
    }
    
    /**
     * This function should used to set a handle to the window in which the 
     * image appear if the window is created after this object.
     * 
     * @param parent The window on which this menu appears.
     */
    public void setParent(Component parent) {
        this.parent = parent;
    }
}