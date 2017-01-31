package weather.clientside.utilities;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import weather.StorageControlSystem;
import weather.clientside.gui.client.ImageRightClickMenu;
import weather.clientside.gui.component.PopupListener;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ScreenInteractionUtility;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The
 * <code>StorageSystemImageChooser1</code> class creates a form that allows the
 * user to see the images from storage system. Displays 6 images within the
 * short time window when the snapshot image was taken
 *
 * @author Bingchen Yan
 * @version Spring 2012
 */
public class StorageSystemImageChooser extends BUDialog {

    private StorageControlSystem storage;
    private ResourceInstancesRequested request;
    private ResourceInstancesReturned returned;
    private Vector<ResourceInstance> instances;
    private Resource resource;

    //THESE TWO FUNCTIONS ARE FOR DEBUGING
    private void debugDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:mm:ss.zzz a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        if (date == null) {
            Debug.println(name + ": null");
        } else {
            Debug.println(name + ": " + simpleDateFormat.format(date));
        }
    }

    private void debugResourceRange(ResourceRange range) {
        debugDate(range.getStartTime(), "Range Start: ");
        debugDate(range.getStopTime(), "Range End: ");
    }
    
    /**
     * Creates new form StorageSystemImageChooser1.
     *
     * @param resourceRange Current Resource range.
     * @param resource Current Resource.
     * @throws java.lang.ClassNotFoundException
     * @throws weather.common.utilities.WeatherException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     */
    public StorageSystemImageChooser(ResourceRange resourceRange, 
            Resource resource) throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException, WeatherException,
            NullPointerException {
        super();
        storage = StorageControlSystemImpl.getStorageSystem();
        if (storage == null) {
            Debug.println("Storage system is null");
        }
        if (resourceRange == null) {
            Debug.println("resourceRange is null");
        } else {
             debugResourceRange(resourceRange);
        }
        if (resource == null) {
            Debug.println("resource is null");
        }
        this.resource = resource;
        request = new ResourceInstancesRequested(resourceRange, 6,
                false, resource.getFormat(), resource);
        returned = storage.getResourceInstances(request);
        instances = returned.getResourceInstances();
        Debug.println("Returned Images: " + instances.size());
        initComponents();

        Dimension thisDim = new Dimension(ScreenInteractionUtility
                .getCurrentScreenResolution().width,
                496 + this.getInsets().top + this.getInsets().bottom);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();

        //resize scoll panel
        getContentPane().remove(imageScrollPane);
        int paneWidth = thisDim.width - getInsets().left - getInsets().right - 24;
        getContentPane().add(imageScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, paneWidth, 439));

        //position close button
        getContentPane().remove(closeButton);
        int buttonX = thisDim.width - getInsets().left - getInsets().right - 75;
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(buttonX, 462, 63, 25));

        initializeImages();
        setIconImage(IconProperties.getTitleBarIconImage());
        setTitle("Weather Viewer - Images from Storage System");
        setModal(true);
        super.postInitialize(true);
    }

    /**
     * Initializes the 6 images for the specified source. If no images is
     * selected the field displays "<none>".
     */
    private void initializeImages() {
        int size = instances.size();
        int index = 0;
        if (size == 0) {
            imagePicLabel1.setIcon(null);
            imagePicLabel1.setText("<The requested image is not available>");
            imagePicLabel2.setIcon(null);
            imagePicLabel2.setText("<The requested image is not available>");
            imagePicLabel3.setIcon(null);
            imagePicLabel3.setText("<The requested image is not available>");
            imagePicLabel4.setIcon(null);
            imagePicLabel4.setText("<The requested image is not available>");
            imagePicLabel5.setIcon(null);
            imagePicLabel5.setText("<The requested image is not available>");
            imagePicLabel6.setIcon(null);
            imagePicLabel6.setText("<The requested image is not available>");
        } else {
            staffPic(imagePicLabel1, index);
            index++;
            addRightClickMenu(1);

            if (index > size - 1) {
                imagePicLabel2.setText("<The requested image is not available>");
            } else {
                staffPic(imagePicLabel2, index);
                index++;
                addRightClickMenu(2);
            }
            if (index > size - 1) {
                imagePicLabel3.setText("<The requested image is not available>");
            } else {
                staffPic(imagePicLabel3, index);
                index++;
                addRightClickMenu(3);
            }
            if (index > size - 1) {
                imagePicLabel4.setText("<The requested image is not available>");
            } else {
                staffPic(imagePicLabel4, index);
                index++;
                addRightClickMenu(4);
            }
            if (index > size - 1) {
                imagePicLabel5.setText("<The requested image is not available>");
            } else {
                staffPic(imagePicLabel5, index);
                index++;
                addRightClickMenu(5);
            }
            if (index > size - 1) {
                imagePicLabel6.setText("<The requested image is not available>");
            } else {
                staffPic(imagePicLabel6, index);
                index++;
                addRightClickMenu(6);
            }
        }
    }

    /**
     * Assigns a given picture and label number to their correct location.
     *
     * @param picLabel JLabel representing a default picture to be assigned.
     * @param index The index number of Vector.
     */
    private void staffPic(JLabel picLabel, int index) {
        try {
            ImageInstance image = (ImageInstance) instances.get(index);
            picLabel.setText("");
            picLabel.setIcon(IconProperties.getSnapShotImage(image.getImage().getScaledInstance(picLabel.getWidth(), picLabel.getHeight(), 0)));
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
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

        closeButton = new java.awt.Button();
        imageScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        imagePanel1 = new javax.swing.JPanel();
        imagePicLabel1 = new javax.swing.JLabel();
        imagePanel2 = new javax.swing.JPanel();
        imagePicLabel2 = new javax.swing.JLabel();
        imagePanel3 = new javax.swing.JPanel();
        imagePicLabel3 = new javax.swing.JLabel();
        imagePanel4 = new javax.swing.JPanel();
        imagePicLabel4 = new javax.swing.JLabel();
        imagePanel5 = new javax.swing.JPanel();
        imagePicLabel5 = new javax.swing.JLabel();
        imagePanel6 = new javax.swing.JPanel();
        imagePicLabel6 = new javax.swing.JLabel();
        rightClickLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        closeButton.setLabel("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1500, 462, 63, 25));

        imageScrollPane.setPreferredSize(new java.awt.Dimension(3302, 462));

        jPanel1.setPreferredSize(new java.awt.Dimension(3110, 420));

        imagePanel1.setPreferredSize(new java.awt.Dimension(500, 400));

        javax.swing.GroupLayout imagePanel1Layout = new javax.swing.GroupLayout(imagePanel1);
        imagePanel1.setLayout(imagePanel1Layout);
        imagePanel1Layout.setHorizontalGroup(
            imagePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel1Layout.createSequentialGroup()
                .addComponent(imagePicLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        imagePanel1Layout.setVerticalGroup(
            imagePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, imagePanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(imagePicLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        imagePanel2.setPreferredSize(new java.awt.Dimension(500, 400));

        javax.swing.GroupLayout imagePanel2Layout = new javax.swing.GroupLayout(imagePanel2);
        imagePanel2.setLayout(imagePanel2Layout);
        imagePanel2Layout.setHorizontalGroup(
            imagePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel2Layout.createSequentialGroup()
                .addComponent(imagePicLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        imagePanel2Layout.setVerticalGroup(
            imagePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel2Layout.createSequentialGroup()
                .addComponent(imagePicLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        imagePanel3.setPreferredSize(new java.awt.Dimension(500, 400));

        javax.swing.GroupLayout imagePanel3Layout = new javax.swing.GroupLayout(imagePanel3);
        imagePanel3.setLayout(imagePanel3Layout);
        imagePanel3Layout.setHorizontalGroup(
            imagePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel3Layout.createSequentialGroup()
                .addComponent(imagePicLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        imagePanel3Layout.setVerticalGroup(
            imagePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel3Layout.createSequentialGroup()
                .addComponent(imagePicLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        imagePanel4.setPreferredSize(new java.awt.Dimension(500, 400));

        javax.swing.GroupLayout imagePanel4Layout = new javax.swing.GroupLayout(imagePanel4);
        imagePanel4.setLayout(imagePanel4Layout);
        imagePanel4Layout.setHorizontalGroup(
            imagePanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel4Layout.createSequentialGroup()
                .addComponent(imagePicLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        imagePanel4Layout.setVerticalGroup(
            imagePanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel4Layout.createSequentialGroup()
                .addComponent(imagePicLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        imagePanel5.setPreferredSize(new java.awt.Dimension(500, 400));

        javax.swing.GroupLayout imagePanel5Layout = new javax.swing.GroupLayout(imagePanel5);
        imagePanel5.setLayout(imagePanel5Layout);
        imagePanel5Layout.setHorizontalGroup(
            imagePanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel5Layout.createSequentialGroup()
                .addComponent(imagePicLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        imagePanel5Layout.setVerticalGroup(
            imagePanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel5Layout.createSequentialGroup()
                .addComponent(imagePicLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        imagePanel6.setMinimumSize(new java.awt.Dimension(0, 0));
        imagePanel6.setPreferredSize(new java.awt.Dimension(500, 400));

        javax.swing.GroupLayout imagePanel6Layout = new javax.swing.GroupLayout(imagePanel6);
        imagePanel6.setLayout(imagePanel6Layout);
        imagePanel6Layout.setHorizontalGroup(
            imagePanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(imagePanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(imagePicLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
        );
        imagePanel6Layout.setVerticalGroup(
            imagePanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(imagePanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(imagePicLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imagePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(imagePanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(imagePanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(imagePanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(imagePanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(imagePanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imagePanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagePanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagePanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagePanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagePanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        imageScrollPane.setViewportView(jPanel1);

        getContentPane().add(imageScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 1560, 439));

        rightClickLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        rightClickLabel.setText("(Right-click on any image for more options.)");
        getContentPane().add(rightClickLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 462, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button closeButton;
    private javax.swing.JPanel imagePanel1;
    private javax.swing.JPanel imagePanel2;
    private javax.swing.JPanel imagePanel3;
    private javax.swing.JPanel imagePanel4;
    private javax.swing.JPanel imagePanel5;
    private javax.swing.JPanel imagePanel6;
    private javax.swing.JLabel imagePicLabel1;
    private javax.swing.JLabel imagePicLabel2;
    private javax.swing.JLabel imagePicLabel3;
    private javax.swing.JLabel imagePicLabel4;
    private javax.swing.JLabel imagePicLabel5;
    private javax.swing.JLabel imagePicLabel6;
    private javax.swing.JScrollPane imageScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel rightClickLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates and adds a right click menu.
     *
     * @param labelNumber The number of a certain label.
     */
    private void addRightClickMenu(int labelNumber) {
        //Prepare the right click menu.
        ImageRightClickMenu rightClickMenu;
        //Get timee.
        ImageInstance imageInstance = (ImageInstance) instances.get(labelNumber - 1);
        Date time = new Date(imageInstance.getStartTime());
        //Get buffered image.
        Image image;
        try {
            image = imageInstance.getImage();
        } catch (WeatherException ex) {
            Logger.getLogger(StorageSystemImageChooser.class.getName()).log(Level.SEVERE, null, ex);
            Debug.println("Cannot attach right click menu.");
            return;
        }
        BufferedImage bImage = (BufferedImage) image;

        //Build and add menu.
        rightClickMenu = new ImageRightClickMenu(bImage, null, ".jpeg",
                resource, time, true, this);
        switch (labelNumber) {
            case 1:
                imagePicLabel1.addMouseListener(new PopupListener(rightClickMenu));
                break;
            case 2:
                imagePicLabel2.addMouseListener(new PopupListener(rightClickMenu));
                break;
            case 3:
                imagePicLabel3.addMouseListener(new PopupListener(rightClickMenu));
                break;
            case 4:
                imagePicLabel4.addMouseListener(new PopupListener(rightClickMenu));
                break;
            case 5:
                imagePicLabel5.addMouseListener(new PopupListener(rightClickMenu));
                break;
            case 6:
                imagePicLabel6.addMouseListener(new PopupListener(rightClickMenu));
                break;
        }
    }
}
