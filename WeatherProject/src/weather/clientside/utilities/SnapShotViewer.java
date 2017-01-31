package weather.clientside.utilities;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import weather.clientside.gui.client.ImageRightClickMenu;
import weather.clientside.gui.component.PopupListener;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.BUJFrame;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * This file makes it possible to print ant preview an image from a data plot or
 * video. The preview provides access to the functionally of an 
 * <code>ImageRightClickMenu</code> and, for video images only, access to other
 * images in the storage system from the given <code>Resource</code> in the
 * general time frame of the image.
 * 
 * @author Brian Bankes
 */
public class SnapShotViewer {

    private boolean showingDataPlot;  //True if showing data plot, false otherwise. 

    private BufferedImage currentImage;
    private ResourceRange resourceRange;
    private Resource resource;
    private ImageRightClickMenu rightClickMenu;

    /**
     * @param image A BufferedImage to be shown.
     * @param timeOfPict The time shown in the image. 
     * @param resource The current resource. May be null. 
     * @param fileNameExtension The file extension of the image save type. (.png 
     * for data plots, .jpeg for camera images)
     */
    public SnapShotViewer(BufferedImage image, Date timeOfPict, 
            Resource resource, String fileNameExtension) {
        super();
        this.currentImage = image;
        this.resource = resource;
        Date start = new Date(timeOfPict.getTime() - resource.getFrequency() * 3 * ResourceTimeManager.MILLISECONDS_PER_SECOND);
        Date end = new Date(timeOfPict.getTime() + resource.getFrequency() * 3 * ResourceTimeManager.MILLISECONDS_PER_SECOND);
        resourceRange = new ResourceRange(start, end);
        if (fileNameExtension.equals(".png")) {
            showingDataPlot = true;
        } else {
            showingDataPlot = false;
        }
        
        //Make right click menu.
        rightClickMenu = new ImageRightClickMenu(image, null, fileNameExtension,
                resource, timeOfPict, true, null);
    }

    //To resize image for preview.
    private BufferedImage resizeImage(final BufferedImage image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();

        return bufferedImage;
    }
    
    /**
     * This function displays this SnapShotViewer on the screen. The preview
     * provides access to the functionally of an
     * <code>ImageRightClickMenu</code> and, for video images only, access to
     * other images in the storage system from the given <code>Resource</code>
     * in the general time frame of the image.
     */
    
    public void preview() {
        //This is the width and height of the preview window.
        int width = 850;
        int height = 700;

        final BUJFrame frame 
                = new BUJFrame("Weather Viewer - Preview Snapshot Image");
        frame.setIconImage(IconProperties.getTitleBarIconImage());
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(width, height));

        final JButton saveButton = new JButton("Save");
        final JButton getButton = new JButton("Get From the Storage System");
        final JButton cancelButton = new JButton("Close");

        saveButton.setToolTipText("Save this image");
        getButton.setToolTipText("Get the original image from the Storage System.");
        cancelButton.setToolTipText("Clicking close will not save the image");
        
        //find new dimension that keeps aspect ratio.
        int scaledHeight = 800 * currentImage.getHeight() / currentImage.getWidth();
        BufferedImage scaledImage = resizeImage(currentImage, 800, scaledHeight);

        final ImageIcon imageIcon = new ImageIcon(scaledImage);
        final JLabel imageLabel = new JLabel(imageIcon);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(saveButton);
        //Only add the getButton if this is not an image from a weather station, ==============
        if (!showingDataPlot) {
            buttonPanel.add(getButton);
        }
        buttonPanel.add(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonAction();
            }
        });

        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getButtonAction();
            }
        });
        
        //Finish setup of right-click munu.
        rightClickMenu.setParent(frame);
        imageLabel.addMouseListener(new PopupListener(rightClickMenu));
        
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        
        //Center this JFrame on the screen.
        frame.postInitialize(true);
    }

    /**
     * Get images from storage system. NOTE -- see how this is used and what it
     * is suppose to do.
     */
    private void getButtonAction() {
        try {
            new StorageSystemImageChooser(resourceRange, resource);
        } catch (ClassNotFoundException ex) {
            WeatherLogger.log(Level.SEVERE, "Class not found. ", ex);
        } catch (InstantiationException | IllegalAccessException 
                | WeatherException | NullPointerException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }

    private void saveButtonAction() {
        //Use right click menu code.
        rightClickMenu.saveAction();
    }

    /**
     * This function is called to print the image in this object.
     */
    public void print(){
        rightClickMenu.printAction();
    }
}