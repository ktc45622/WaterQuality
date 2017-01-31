package weather.clientside.gui.client;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import weather.clientside.gui.component.PopupListener;
import weather.clientside.manager.ResourceSearchListener;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * SearchResourceDataPanel displays up to three different images of either
 * weather maps or snapshot images of a video.  It uses the designer to make its
 * components, but not to place then, as initComponents() is not called. 
 *
 * @author Ty Vanderstappen
 * @author Bingchen Yan
 * @version 2012
 */
public class SearchResourceDataPanel extends javax.swing.JPanel implements ResourceSearchListener {

    private Vector<ResourceInstance> images = new Vector<>();
    private Resource resource;
    private int position;
    private SearchResearchDataWindow parent;
    
    //Handles for deletion of right click menus
    private ImageRightClickMenu rightClickMenu1, rightClickMenu2, rightClickMenu3;
    private PopupListener listener1, listener2, listener3;
    
    //Variables for sizing, starting with panel size
    private int panelWidth;
    private int panelHeight;
    //Other X values
    private int column1X;
    private int column2X;
    private int column3X;
    private int label1X;
    private int label2X;
    private int label3X;
    //Other Y values
    private int pictureY;
    private int timeLabelY;
    private int imageNumLabelY;
    //Other widths
    private int columnWidth;
    private int labelWidth;
    //Other heights
    private int labelHeight;
    private int pictureHeight;
    
    //String to put on label without a picture.
    private String noImageMessage = "<html><center>" + 
            "&lt;The requested image is not available.&gt;</html>";
    
    /**
     * Computes parameters for the layout.
     */
    private void computeCoordinates() {
        //Compute heights
        labelHeight = 16;
        pictureHeight = panelHeight - 3 * labelHeight;
        
        //Compute Y values.
        pictureY = labelHeight;
        timeLabelY = pictureY + pictureHeight;
        imageNumLabelY = timeLabelY + labelHeight;
        
        //Find aspect ratio of pictures for use below.
        ImageInstance image = images.isEmpty() ? //check for no (images.
                null : (ImageInstance) images.get(0);    //use firt image as guide.
        
        //assume 4/3 ratio.
        int ssampleWidth = 133;
        int sampleHeight = 100;
        
        if(image != null){
            try {
                ssampleWidth = image.getImage().getWidth(null);
                sampleHeight = image.getImage().getHeight(null);
            } catch (WeatherException ex) {
                Debug.println("Con't size picture");
            }
        }
        
        float aspectRatio = ((float)ssampleWidth) / sampleHeight;
        
        //Compute width values and the size of the buffer between columns.
        labelWidth = panelWidth / 3;
        columnWidth = Math.round((aspectRatio * pictureHeight));
        int bufferSize = (panelWidth - 3 * columnWidth) / 3;
        
        //Compute X values.
        column1X = bufferSize / 2;
        column2X = column1X + columnWidth + bufferSize;
        column3X = column2X + columnWidth + bufferSize;
        label1X = 0;
        label2X = column2X - bufferSize / 2;
        label3X = column3X - bufferSize / 2;
    }
    
    /**
     * This replaces initComponents() and sizes the panel to its given dimensions. 
     */
    private void initComponentsOnThisScreen() {
        nameLabel = new javax.swing.JLabel();
        imageLabel1 = new javax.swing.JLabel();
        timeLabel1 = new javax.swing.JLabel();
        numberLabel1 = new javax.swing.JLabel();
        imageLabel2 = new javax.swing.JLabel();
        timeLabel2 = new javax.swing.JLabel();
        numberLabel2 = new javax.swing.JLabel();
        imageLabel3 = new javax.swing.JLabel();
        timeLabel3 = new javax.swing.JLabel();
        numberLabel3 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nameLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameLabel.setText("Name");
        add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, panelWidth, labelHeight));

        imageLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imageLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(column1X, pictureY,
                columnWidth, pictureHeight));

        timeLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel1.setText("Time");
        add(timeLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(label1X, timeLabelY,
                labelWidth, labelHeight));

        numberLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numberLabel1.setText("Image #");
        add(numberLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(label1X, imageNumLabelY,
                labelWidth, labelHeight));

        imageLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imageLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(column2X, pictureY,
                columnWidth, pictureHeight));

        timeLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel2.setText("Time");
        add(timeLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(label2X, timeLabelY,
                labelWidth, labelHeight));

        numberLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numberLabel2.setText("Image #");
        add(numberLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(label2X, imageNumLabelY,
                labelWidth, labelHeight));

        imageLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imageLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(column3X, pictureY,
                columnWidth, pictureHeight));

        timeLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel3.setText("Time");
        add(timeLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(label3X, timeLabelY,
                labelWidth, labelHeight));

        numberLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numberLabel3.setText("Image #");
        add(numberLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(label3X, imageNumLabelY,
                labelWidth, labelHeight));
    }
    
    /**
     * Creates new form SearchResourceDataPanel.
     *
     * @param images a set of images should be show in this panel.
     * @param parent the parent window of this panel.
     * @param resource The resource being shown on the panel.
     * @param panelWidth The width of the panel.
     * @param panelHeight The height of the panel.
     */
    public SearchResourceDataPanel(Vector<ResourceInstance> images, 
            SearchResearchDataWindow parent, Resource resource, 
            int panelWidth, int panelHeight) {
        this.images = images;
        this.parent = parent;
        this.resource = resource;
        this.panelHeight = panelHeight;
        this.panelWidth = panelWidth;
        this.position = 0;
        computeCoordinates();
        initComponentsOnThisScreen();
        loadImages();
    }

    /**
     * Loads the 3 images for the specified source. If no images is
     * selected the field displays "<none>".
     */
    private void loadImages() {
        int size = images.size();
        Debug.println(resource.getResourceName() + " size: " + images.size());
        int index = position;
        if (size == 0) {
            imageLabel1.setIcon(null);
            imageLabel1.setText(noImageMessage);
            numberLabel1.setText("Image #");
            timeLabel1.setText("Time");
            imageLabel2.setIcon(null);
            imageLabel2.setText(noImageMessage);
            numberLabel2.setText("Image #");
            timeLabel2.setText("Time");
            imageLabel3.setIcon(null);
            imageLabel3.setText(noImageMessage);
            numberLabel3.setText("Image #");
            timeLabel3.setText("Time");
        } else {
            if (index + 3 > size) {
                index = size - 3;
            }
            if (index < 0) {
                index = 0;
            }
            if (index > size - 1) {
                imageLabel1.setIcon(null);
                imageLabel1.setText(noImageMessage);
                numberLabel1.setText("Image #");
                timeLabel1.setText("Time");
            } else {
                staffPic(imageLabel1, timeLabel1, index);
                numberLabel1.setText("Image " + (index + 1) + "/" + size);
                addRightClickMenu(1, index, listener1, rightClickMenu1);
                index++;
            }
            if (index > size - 1) {
                imageLabel2.setIcon(null);
                imageLabel2.setText(noImageMessage);
                numberLabel2.setText("Image #");
                timeLabel2.setText("Time");
            } else {
                staffPic(imageLabel2, timeLabel2, index);
                numberLabel2.setText("Image " + (index + 1) + "/" + size);
                addRightClickMenu(2, index,  listener2, rightClickMenu2);
                index++;
            }
            if (index > size - 1) {
                imageLabel3.setIcon(null);
                imageLabel3.setText(noImageMessage);
                numberLabel3.setText("Image #");
                timeLabel3.setText("Time");
            } else {
                staffPic(imageLabel3, timeLabel3, index);
                numberLabel3.setText("Image " + (index + 1) + "/" + size);
                addRightClickMenu(3, index,  listener3, rightClickMenu3);
                index++;
            }
        }
    }
    
    /**
     * update the 3 images for the specified source. If no images is selected
     * the field displays "<none>".
     */
    private void updateImages() {
        //Remove listeners
        imageLabel1.removeMouseListener(listener1);
        imageLabel2.removeMouseListener(listener2);
        imageLabel3.removeMouseListener(listener3);
        
        //Clean up.
        listener1 = null;
        listener2 = null;
        listener3 = null;
        rightClickMenu1 = null;
        rightClickMenu2 = null;
        rightClickMenu3 = null;
        System.gc();
        
        //Load new pictures.
        loadImages();
    }

    /**
     * Assigns a given picture and label number to their correct location.
     *
     * @param picLabel JLabel representing a default picture to be assigned.
     * @param timeLabel JLabel showing the time.
     * @param index The index number of Vector.
     */
    private void staffPic(JLabel picLabel, JLabel timeLabel, int index) {
        try {
            ImageInstance image = (ImageInstance) images.get(index);
            StringBuilder newLabel = new StringBuilder();
            SimpleDateFormat format 
                    = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
            format.setTimeZone(resource.getTimeZone().getTimeZone());
            newLabel.append("<html><center>");
            newLabel.append(format.format(image.getStartTime()).toString());
            newLabel.append("</center></html>");
            
            //Place text or label with "GMT" replaced by "UTC."
            timeLabel.setText(newLabel.toString().replaceAll("GMT", "UTC"));
            
            picLabel.setText("");
            picLabel.setIcon(IconProperties.getSnapShotImage(image.getImage().
                    getScaledInstance(columnWidth, pictureHeight, 0)));
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * load data.
     */
    public void loadData() {
        this.position = 0;
        loadImages();
    }

    /**
     * clean up.
     */
    public void cleanUp() {
        images.clear();
    }

    /**
     * Changes the title of this panel.
     *
     * @param title The new title.
     */
    public void setTitle(String title) {
        nameLabel.setText(title);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        imageLabel1 = new javax.swing.JLabel();
        timeLabel1 = new javax.swing.JLabel();
        numberLabel1 = new javax.swing.JLabel();
        imageLabel2 = new javax.swing.JLabel();
        timeLabel2 = new javax.swing.JLabel();
        numberLabel2 = new javax.swing.JLabel();
        imageLabel3 = new javax.swing.JLabel();
        timeLabel3 = new javax.swing.JLabel();
        numberLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1733, 267));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nameLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameLabel.setText("Name");
        add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1733, -1));

        imageLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imageLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(73, 24, 385, 197));

        timeLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel1.setText("Time");
        add(timeLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 228, 164, -1));

        numberLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numberLabel1.setText("Image #");
        add(numberLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 248, -1, -1));

        imageLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imageLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(666, 24, 385, 197));

        timeLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel2.setText("Time");
        add(timeLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(776, 228, 164, -1));

        numberLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numberLabel2.setText("Image #");
        add(numberLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(843, 251, -1, -1));

        imageLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imageLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1265, 24, 385, 197));

        timeLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel3.setText("Time");
        add(timeLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1379, 228, 164, -1));

        numberLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numberLabel3.setText("Image #");
        add(numberLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1437, 251, -1, -1));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel imageLabel1;
    private javax.swing.JLabel imageLabel2;
    private javax.swing.JLabel imageLabel3;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel numberLabel1;
    private javax.swing.JLabel numberLabel2;
    private javax.swing.JLabel numberLabel3;
    private javax.swing.JLabel timeLabel1;
    private javax.swing.JLabel timeLabel2;
    private javax.swing.JLabel timeLabel3;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates and adds a right click menu.
     *
     * @param labelNumber The number of a certain label.
     * @param vectorIndex The vector index if the image to which the picture is
     * attached.
     * @param listener The listener to be attached.
     * @param rightClickMenu The menu to be attached.
     */
    private void addRightClickMenu(int labelNumber, int vectorIndex, 
            PopupListener listener, ImageRightClickMenu rightClickMenu) {
        //Prepare the right click men - first get timee.
        ImageInstance imageInstance = (ImageInstance) images.get(vectorIndex);
        Date time = new Date(imageInstance.getStartTime());
        //Get buffered image.
        Image image;
        try {
            image = imageInstance.getImage();
        } catch (WeatherException ex) {
            Logger.getLogger(SearchResourceDataPanel.class.getName()).log(Level.SEVERE, null, ex);
            Debug.println("Cannot attach right click menu.");
            return;
        }
        BufferedImage bImage = (BufferedImage)image;
       
        //Build and add menu.
        rightClickMenu = new ImageRightClickMenu(bImage, null, ".jpeg",
                resource, time, true, parent);
        listener = new PopupListener(rightClickMenu);
        switch (labelNumber) {
            case 1:
                imageLabel1.addMouseListener(listener);
                break;
            case 2:
                imageLabel2.addMouseListener(listener);
                break;
            case 3:
                imageLabel3.addMouseListener(listener);
        }
    }

    /**
     * set data.
     *
     * @param images new value of images.
     */
    @Override
    public void setData(Vector<ResourceInstance> images) {
        this.images = images;
    }

    /**
     * display the first data value (will be left most data value).
     */
    @Override
    public void start() {
        position = 0;
        updateImages();
    }

    /**
     * display the next data value (increment by 1).
     */
    @Override
    public void next() {
        if (position > (images.size() - 1)) {
            JOptionPane.showMessageDialog(this, "This is the last set of images.",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            position += 1;
            updateImages();
        }
    }

    /**
     * display the last data value (will be right most data value).
     */
    @Override
    public void last() {
        position = images.size() - 3;
        updateImages();
    }

    /**
     * Display the next set of three.
     */
    @Override
    public void nextSet() {
        if (position > (images.size() - 1)) {
            //do nothing
        } else {
            position += 3;
            updateImages();
        }
    }

    /**
     * Display the previous set of three
     */
    @Override
    public void previousSet() {
        if (position < 0) {
            //do nothing
        } else {
            position -= 3;
            updateImages();
        }
    }

    /**
     * display this index (data on left of screen).
     *
     * @param index specify position.
     */
    @Override
    public void setPosition(int index) {

        if (index == images.size() || index == images.size() - 1) {
            index = images.size() - 2;
        }
        position = index - 1;
        updateImages();

    }
}
