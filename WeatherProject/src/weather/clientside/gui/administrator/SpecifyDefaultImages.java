package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.ImageRightClickMenu;
import weather.clientside.gui.component.PopupListener;
import weather.clientside.utilities.ImageFilter;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLResourceManager;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.ImageFileTester;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The
 * <code>SpecifyDefaultImages</code> class creates a form that allows the
 * administrator to select the default images for both the no-data and nighttime
 * fields. Displays the current image in that field by default.
 * For this window we only get the current pictures from database and send 
 * pictures to database
 * 
 * @author Andrew Bennett (2010)
 *
 * @version Spring 2010
 */
public class SpecifyDefaultImages extends BUDialog {

    // TODO: javadoc for each member
    private String picPath = CommonLocalFileManager.getPictureDirectory();
    private DBMSSystemManager system;
    private MySQLResourceManager rm;
    private Vector<Resource> resourceList;
    private File dtSelFile;
    private File ntSelFile;
    private Resource resource;
    private final int dayTimeNumber = 1;
    private final int nightTimeNumber = 2;
    private final int specifierDayNumber = 3;
    private final int specifierNightNumber = 4;
    private String dayTimeFilePath = "";
    private String nightTimeFilePath = "";
    private boolean needToSave;
    private final int resourceCount;

    /**
     * Creates new form SpecifyDefaultImages.
     * 
     * @param appControl Control system for the application.
     */
    public SpecifyDefaultImages(ApplicationControlSystem appControl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        super(appControl);
        resourceCount = appControl.getGeneralService().getWeatherCameraResources().size()
                + appControl.getGeneralService().getWeatherMapLoopResources().size();
        this.resource = appControl.getGeneralService().getCurrentWeatherCameraResource();
        this.system = appControl.getGeneralService().getDBMSSystem();
        this.rm = (MySQLResourceManager) system.getResourceManager();
        initComponents();
        initializeList();

        int width = 571 + this.getInsets().left + this.getInsets().right;
        int height = 330 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        this.addRightClickMenu(dayTimeNumber);
        this.addRightClickMenu(nightTimeNumber);
        weather.clientside.utilities.RightClickMenu.addMenuTo(
                new javax.swing.text.JTextComponent[]{});
        super.postInitialize(true);
    }
    
    /**
     * Creates and adds a right click menu.
     *
     * @param labelNumber The number of a certain label.
     */
    private void addRightClickMenu(int labelNumber) {
        //Get a buffered Image.
        BufferedImage bImage = null;
        try {
            bImage = (BufferedImage) getImage(labelNumber);
        } catch (WeatherException ex) {
            Logger.getLogger(SpecifyNoDataImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (bImage == null) {
            //Remove any old popup menu and tool tip.
            switch (labelNumber) {
                case 1:
                    if (dayTimePicLabel.getMouseListeners().length > 0) {   //remove old menu
                        this.dayTimePicLabel.removeMouseListener(this.dayTimePicLabel.getMouseListeners()[0]);
                        dayTimePicLabel.setToolTipText("");
                    }
                    break;
                case 2:
                    if (nightTimePicLabel.getMouseListeners().length > 0) {   //remove old menu
                        this.nightTimePicLabel.removeMouseListener(nightTimePicLabel.getMouseListeners()[0]);
                        nightTimePicLabel.setToolTipText("");
                    }
                    break;
                default:
                    break;
            }
            return;
        }
        
        ImageRightClickMenu rightClickMenu = new ImageRightClickMenu(bImage,
                getImageName(labelNumber), ".jpeg", null, null, true, this);
        //Add components that can bring up this popup menu.
        switch (labelNumber) {
            case 1:
                if (dayTimePicLabel.getMouseListeners().length > 0) {   //remove old menu and tool tip
                    this.dayTimePicLabel.removeMouseListener(this.dayTimePicLabel.getMouseListeners()[0]);
                    dayTimePicLabel.setToolTipText("");
                }
                if (this.dayTimePicLabel.getIcon() != null) {
                    dayTimePicLabel.addMouseListener(new PopupListener(rightClickMenu));
                    dayTimePicLabel.setToolTipText("Right Click To Get More Option");
                }
                break;
            case 2:
                if (nightTimePicLabel.getMouseListeners().length > 0) {   //remove old menu and tool tip
                    this.nightTimePicLabel.removeMouseListener(nightTimePicLabel.getMouseListeners()[0]);
                    nightTimePicLabel.setToolTipText("");
                }
                if (this.nightTimePicLabel.getIcon() != null) {
                    this.nightTimePicLabel.addMouseListener(new PopupListener(rightClickMenu));
                    nightTimePicLabel.setToolTipText("Right Click To Get More Option");
                }
                break;
            default:
                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instructionJLabel = new javax.swing.JLabel();
        stationLabel = new javax.swing.JLabel();
        resourceComboBox = new javax.swing.JComboBox<String>();
        daytimeBrowseButton = new javax.swing.JButton();
        nighttimeBrowseButton = new javax.swing.JButton();
        dayTimePicLabel = new javax.swing.JLabel();
        nightTimePicLabel = new javax.swing.JLabel();
        rightClickJLabel = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Specify Default Images");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        instructionJLabel.setText("<html>\nPlease select the desired camera or map and then modify either or both of the default images.\n</html>");
        getContentPane().add(instructionJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 547, 16));

        stationLabel.setText("Select Weather Resource:");
        getContentPane().add(stationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 43, 150, 16));

        resourceComboBox.setToolTipText("This associates with the weather station name");
        resourceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourceComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(resourceComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 389, 22));

        daytimeBrowseButton.setText("Select Daytime Image");
        daytimeBrowseButton.setToolTipText("Select a image for daytime image in database");
        daytimeBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daytimeBrowseButtonActionPerformed(evt);
            }
        });
        getContentPane().add(daytimeBrowseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 74, 267, 25));

        nighttimeBrowseButton.setText("Select Nighttime Image");
        nighttimeBrowseButton.setToolTipText("Select a image for nighttime image in database");
        nighttimeBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nighttimeBrowseButtonActionPerformed(evt);
            }
        });
        getContentPane().add(nighttimeBrowseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(292, 74, 267, 25));

        dayTimePicLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dayTimePicLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(dayTimePicLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 111, 267, 143));

        nightTimePicLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nightTimePicLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(nightTimePicLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(292, 111, 267, 143));

        rightClickJLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rightClickJLabel.setText("Right click on an image to get more options.");
        rightClickJLabel.setToolTipText("");
        getContentPane().add(rightClickJLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(163, 266, 245, 15));

        submitButton.setText("Submit Image(s)");
        submitButton.setToolTipText("Update the images in the database.");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        getContentPane().add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(341, 293, 129, 25));

        closeButton.setText("Close");
        closeButton.setToolTipText("Close this window.");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(482, 293, 77, 25));

        noteLabel.setText("<html>Please note that changes will only affect videos obtained from the system going forward.</html>");
        getContentPane().add(noteLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 288, 317, 35));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Initializes the resources that available to specify the default images.
     */
    private void initializeList() {
        resourceList = appControl.getGeneralService().getWeatherCameraResources();
        for (Resource camResource : resourceList) {
            resourceComboBox.addItem(camResource.getResourceName());
        }
        resourceList = appControl.getGeneralService().getWeatherMapLoopResources();
        for (Resource mapResource : resourceList) {
            resourceComboBox.addItem(mapResource.getResourceName());
        }
        if (resource != null) {
            resourceComboBox.setSelectedItem(resource.getResourceName());
        } else {
            resourceComboBox.setSelectedIndex(0);
        }
        needToSave = false;
    }

    /**
     * Initializes the default images for the specified weather camera. If no
     * default images is selected the field displays "<none>".
     */
    private void initializeImages() {
        this.resource = getResourceForName(resourceComboBox.getSelectedItem().toString());
        if (rm.getDefaultDaytimePicture(resource.getResourceNumber()) == null) {
            dayTimePicLabel.setIcon(null);
            dayTimePicLabel.setText("<The requested data is not available>");
        } else {
            this.staffPic(this.dayTimePicLabel, this.dayTimeNumber);
        }
        if (rm.getDefaultNighttimePicture(resource.getResourceNumber()) == null) {
            nightTimePicLabel.setIcon(null);
            nightTimePicLabel.setText("<The requested data is not available>");
        } else {
            this.staffPic(this.nightTimePicLabel, this.nightTimeNumber);
        }
        needToSave = false;
    }

    /**
     * Make the dimensions of a source image even and returns it.  Odd dimensions
     * are decreased by one.
     * @param source The source image.
     * @return The source image with even dimensions.
     */
    private BufferedImage evenImageDims(BufferedImage source) {
        int width = (source.getWidth() / 2) * 2;
        int height = (source.getHeight() / 2) * 2;
        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = destination.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance((double) width / source.getWidth(), (double) height / source.getHeight());
        g.drawRenderedImage(source, at);
        return destination;
    }
    
    /**
     * Assigns a given picture and label number to their correct location.
     *
     * @param picLabel JLabel representing a default picture to be assigned.
     * @param labelNumber The JLabel's number.
     */
    private void staffPic(JLabel picLabel, int labelNumber) {
        try {
            BufferedImage target = null;
            switch (labelNumber) {
                case 1: //init day label from database
                    target = (BufferedImage) (rm.getDefaultDaytimePicture(resource.getResourceNumber()).getImage());
                    break;
                case 2: //init night label from database
                    target = (BufferedImage) (rm.getDefaultNighttimePicture(resource.getResourceNumber()).getImage());
                    break;
                case 3: //find new day image
                    try {
                        target = (BufferedImage) (ImageIO.read(new File(this.dayTimeFilePath.trim())));
                        target = evenImageDims(target);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Cannot open your image.",
                                "Open Error", JOptionPane.ERROR_MESSAGE);
                        WeatherLogger.log(Level.SEVERE, null, ex);
                    }
                    break;
                case 4: //find new night image
                    try {
                        target = (BufferedImage) (ImageIO.read(new File(this.nightTimeFilePath.trim())));
                        target = evenImageDims(target);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Cannot open your image.",
                                "Open Error", JOptionPane.ERROR_MESSAGE);
                        WeatherLogger.log(Level.SEVERE, null, ex);
                    }
                    break;
            }
            picLabel.setText("");
            picLabel.setIcon(IconProperties.getSnapShotImage(target.getScaledInstance(picLabel.getWidth(),
                    picLabel.getHeight(), BufferedImage.TYPE_3BYTE_BGR)));
        } catch (WeatherException ex) {
            JOptionPane.showMessageDialog(this, "Cannot open the image.",
                    "Open Error", JOptionPane.ERROR_MESSAGE);
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Finds the resource that corresponds to the given name.
     *
     * @param name The name of the resource to find.
     * @return The resource that corresponds to the given name or null if no
     * resource is found.
     */
    private Resource getResourceForName(String name) {
        for (Resource cameraResource : appControl.getGeneralService().
                getWeatherCameraResources()) {
            if (cameraResource.getName().equals(name)) {
                return cameraResource;
            }
        }
        for (Resource mapResource : appControl.getGeneralService().
                getWeatherMapLoopResources()) {
            if (mapResource.getName().equals(name)) {
                return mapResource;
            }
        }
        return null; //Resource of that name not found
    }

    private void daytimeBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daytimeBrowseButtonActionPerformed
        this.dayTimeFilePath = "";
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(picPath), null, "Select Default Daytime Image", 
                new ImageFilter(), null, this);
        // Show open dialog and get image
        if (file != null) {
            this.dayTimeFilePath = file.toString();
            this.dtSelFile = file;
            
            int testResult = this.testPic(this.dayTimeNumber);
            if (testResult == 1) {
                this.staffPic(this.dayTimePicLabel, this.specifierDayNumber);
            } else {
                JOptionPane.showMessageDialog(this, "This is not a valid picture.",
                        "File Type Error", JOptionPane.ERROR_MESSAGE);
                this.dayTimeFilePath = "";
                this.initializeImages();
            }
            this.addRightClickMenu(this.dayTimeNumber);
            needToSave = true;
        }
    }//GEN-LAST:event_daytimeBrowseButtonActionPerformed

    private void nighttimeBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nighttimeBrowseButtonActionPerformed
        this.nightTimeFilePath = "";
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(picPath), null, "Select Default Nighttime Image", 
                new ImageFilter(), null, this);
        // Show open dialog and get image
        if (file != null) {
            this.nightTimeFilePath = file.toString();
            this.ntSelFile = file;
            
            int testResult = this.testPic(this.nightTimeNumber);
            if (testResult == 1) {
                this.staffPic(this.nightTimePicLabel, this.specifierNightNumber);
            } else {
                JOptionPane.showMessageDialog(this, "This is not a valid picture.",
                        "File Type Error", JOptionPane.ERROR_MESSAGE);
                this.nightTimeFilePath = "";
                this.initializeImages();
            }
            this.addRightClickMenu(this.nightTimeNumber);
            needToSave = true;
        }
    }//GEN-LAST:event_nighttimeBrowseButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if(needToSave) {
            if(appControl.getGeneralService().leaveWithoutSaving(this)) {
                dispose();
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Action listener for when the camera resource is changed via the
     * cameraComboBox. Loads the default Images from the database.
     *
     * @param evt The ActionEvent that sees if the camera resource is changed.
     */
    private void resourceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourceComboBoxActionPerformed
        if (this.resourceComboBox.getItemCount() == resourceCount) {
            this.dayTimeFilePath = "";
            this.nightTimeFilePath = "";
            initializeImages();
            repaint();
            this.addRightClickMenu(this.dayTimeNumber);
            this.addRightClickMenu(this.nightTimeNumber);
        }
    }//GEN-LAST:event_resourceComboBoxActionPerformed

    /**
     * This method is called when the "update" button was clicked. It first
     * checks to see if the user has selected an Image file for both fields. If
     * so read both files. If not it checks one field after another and reads
     * them accordingly.
     *
     * @param evt The ActionEvent to see if the "update" button was clicked.
     */
    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        if ((this.dayTimeFilePath.equals("")) && (this.nightTimeFilePath.equals(""))) {
            return;
        }
        if (!this.dayTimeFilePath.equals("")) {
            this.updatePic(this.dayTimeNumber);
            this.dayTimeFilePath = "";
        }
        if (!this.nightTimeFilePath.equals("")) {
            this.updatePic(this.nightTimeNumber);
            this.nightTimeFilePath = "";
        }
        this.initializeImages();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(needToSave) {
            if(appControl.getGeneralService().leaveWithoutSaving(this)) {
                dispose();
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * Updates the default pic specified by the number.
     *
     * @param updateNumber The picture number to be updated.
     */
    private void updatePic(int updateNumber) {
        String warning = "";
        this.resource = getResourceForName(resourceComboBox.getSelectedItem().toString());
        switch (updateNumber) {
            case 1:
                warning = " Daytime Picture for " + resource.getResourceName();
                break;
            case 2:
                warning = " Nighttime Picture for " + resource.getResourceName();
                break;
            default:
                break;
        }
        int ans = JOptionPane.showConfirmDialog(this, "This will change the default"
                + warning + "\nYou will lose the old data, do you want to continue?",
                "Change Default Images", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            ImageInstance imgDefault = new ImageInstance(resource);
            try {
                switch (updateNumber) {
                    case 1:
                        dtSelFile = new File(this.dayTimeFilePath);
                        imgDefault.readFile(dtSelFile);
                        if (rm.setDefaultDaytimePicture(resource
                                .getResourceNumber(),imgDefault)) {
                            JOptionPane.showMessageDialog(this, 
                                    "Update" + warning + " was successful", 
                                    "Update Successful", 
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                    "Update" + warning + " was NOT successful", 
                                    "Update Not Successful", 
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case 2:
                        ntSelFile = new File(this.nightTimeFilePath);
                        imgDefault.readFile(ntSelFile);
                        if (rm.setDefaultNighttimePicture(resource
                                .getResourceNumber(),imgDefault)) {
                            JOptionPane.showMessageDialog(this, 
                                    "Update" + warning + " was successful", 
                                    "Update Successful", 
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                    "Update" + warning + " was NOT successful", 
                                    "Update Not Successful", 
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    default:
                        break;
                }
            } catch (WeatherException ex) {
                WeatherLogger.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this,
                        "Update" + warning + " was NOT successful",
                        "Update Not Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Gets the image by number.
     *
     * @param labelNumber The number of that image.
     * @return The image.
     */
    private Image getImage(int labelNumber) throws WeatherException {
        Image img = null;
        ImageInstance retrievedInstance;
        switch (labelNumber) {
            case 1:
                if (this.dayTimeFilePath.equals("")) {
                    retrievedInstance = rm.getDefaultDaytimePicture(resource.getResourceNumber());
                    if (retrievedInstance != null) {
                        img = retrievedInstance.getImage();
                    }
                } else {
                    try {
                        img = ImageIO.read(new File(this.dayTimeFilePath));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Cannot open the image",
                                "Open Image Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case 2:
                if (this.nightTimeFilePath.equals("")) {
                    retrievedInstance = rm.getDefaultNighttimePicture(resource.getResourceNumber());
                    if (retrievedInstance != null) {
                        img = retrievedInstance.getImage();
                    }
                } else {
                    try {
                        img = ImageIO.read(new File(this.nightTimeFilePath));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Cannot open the image",
                                "Open Image Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            default:
                break;

        }
        return img;
    }

    /**
     * Gets the image name specified by number.
     * 
     * @param labelNumber The number of that image.
     * @return The name of the image.
     */
    private String getImageName(int labelNumber) {
        String pic_Name = "";
        switch (labelNumber) {
            case 1:
                if (this.dayTimeFilePath.equals("")) {
                    pic_Name = " Default Day Time Image In Database for " + resource.getResourceName();
                } else {
                    pic_Name = " Day Time Image You Want To Upload ";
                }
                break;
            case 2:
                if (this.nightTimeFilePath.equals("")) {
                    pic_Name = " Default Night Time Image In Database for " + resource.getResourceName();
                } else {
                    pic_Name = " Night Time Image You Want To Upload";
                }
                break;
            default:
                break;

        }
        return pic_Name;
    }

    /**
     * Tests to see if the picture exists.
     * Resets path if image is not good.
     * 
     * @param labelNumber The number of the application method to check to see if it is labelNumber standard image.
     * @return 1 if it is labelNumber valid picture and the correct size,
     *          2 if it does not exist (indicated by IOException), 
     */
    private int testPic(int labelNumber) {
        String img = null;
        switch (labelNumber) {
            case 1:
                img = this.dayTimeFilePath;
                break;
            case 2:
                img = this.nightTimeFilePath;
                break;
        }
        File imgFile;
        try {
            imgFile = new File(img);
            ImageIO.read(imgFile);
            if (!ImageFileTester.isImageFile(imgFile)) {
                throw new IOException();
            }
            return 1;
        } catch (IOException ex) {
            switch (labelNumber) {
                case 1:
                    this.dayTimeFilePath = "";
                    break;
                case 2:
                    this.nightTimeFilePath = "";
                    break;
            }
            return 2;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel dayTimePicLabel;
    private javax.swing.JButton daytimeBrowseButton;
    private javax.swing.JLabel instructionJLabel;
    private javax.swing.JLabel nightTimePicLabel;
    private javax.swing.JButton nighttimeBrowseButton;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JComboBox<String> resourceComboBox;
    private javax.swing.JLabel rightClickJLabel;
    private javax.swing.JLabel stationLabel;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}

