package weather.clientside.gui.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.clientside.manager.MovieController;
import weather.clientside.utilities.BookmarkEventOpener;
import weather.clientside.utilities.BookmarkTimeZoneFinder;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkFileInstance;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.gui.component.BUJFrame;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;

/**
 * The <code>OpenBookmarkInstanceWindow</code> class creates a form that
 * opens a selected bookmark from the database.
 * @author Alex Funk
 * @version 2011
 */
public class OpenBookmarkInstanceWindow extends BUJFrame
{
    private DBMSUserManager userManager;
    private DBMSResourceManager resourceManager;
    private ApplicationControlSystem appControl;
    private MovieController movieController;
    private ResourceRange resourceRange;
    private Bookmark bookmark;
    private DefaultListModel<String> fileListModel;
    private LinkedList<BookmarkFileInstance> attachedFiles;
    
    /**
     * Creates a new bookmark instance window with a given bookmark instance
     * name.
     * TODO: this should not throw a WeatherException, but instead deal with the issue
     * @param appControl The application control system.
     * @param bookmark The bookmark to be opened.
     * @param mc The movie controller of the main window.
     * @throws weather.common.utilities.WeatherException
     */
    public OpenBookmarkInstanceWindow(ApplicationControlSystem appControl, 
            Bookmark bookmark, MovieController mc) throws WeatherException {
        this.bookmark = bookmark;
        this.appControl = appControl;
        fileListModel = new DefaultListModel<>();

        initComponents();

        attachedFiles = new LinkedList<>();
        attachLabel.setIcon(IconProperties.getAttachmentIconImage());
        movieController = mc;
        userManager = appControl.getDBMSSystem().getUserManager();
        resourceManager = appControl.getDBMSSystem().getResourceManager();
        String createdBy = userManager.obtainUser(bookmark.getCreatedBy()).getFirstName() + " " +
                userManager.obtainUser(bookmark.getCreatedBy()).getLastName();
        this.setTitle("Weather Viewer - Now Viewing " + bookmark.getName() + "        (" + createdBy + ")");
        setIconImage(IconProperties.getTitleBarIconImage());
        String notes = bookmark.getNotes();

        //This is to display the picture from whatever camera was taken
        ImageInstance weatherCam = bookmark.getWeatherCameraPicture();
        if(weatherCam != null){
            cameraLabel.setText("");
            cameraLabel.setIcon(new ImageIcon(weatherCam.getImage().
                    getScaledInstance(cameraLabel.getWidth(), 
                    cameraLabel.getHeight(), Image.SCALE_DEFAULT)));
        }
        //This is to display the picture from whatever radar map was taken
        ImageInstance weatherMap = bookmark.getWeatherMapPicture();
        if(weatherMap != null){
            radarLabel.setText("");
            radarLabel.setIcon(new ImageIcon(weatherMap.getImage().
                    getScaledInstance(radarLabel.getWidth(), 
                    radarLabel.getHeight(), Image.SCALE_DEFAULT)));
        }
        //This is an image displayed of the picture of whatever dataplot taken
        ImageInstance dataPlot = bookmark.getWeatherStationPicture();
        if(dataPlot != null){
            dataPlotLabel.setText("");
            dataPlotLabel.setIcon(new ImageIcon(dataPlot.getImage().
                    getScaledInstance(dataPlotLabel.getWidth(), 
                    dataPlotLabel.getHeight(), Image.SCALE_SMOOTH)));
        }

        long timeOfInstance = bookmark.getStartTime().getTime();
        long startMillis = timeOfInstance - timeOfInstance 
                % ResourceTimeManager.MILLISECONDS_PER_HOUR;
        long endMillis = startMillis 
                + ResourceTimeManager.MILLISECONDS_PER_HOUR;
        Date startTime = new Date(startMillis);
        Date endTime = new Date(endMillis);
        resourceRange = new ResourceRange(startTime, endTime);
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
        df.setTimeZone(BookmarkTimeZoneFinder.findTimeZone(bookmark, 
                appControl.getGeneralService()));
        String createdDate = df.format(bookmark.getStartTime());
        String cameraNote ="No camera was selected", radarNote ="No radar was selected", dataPlotNote="No weather station was selected";
        
        Resource resource;
        if (bookmark.getWeatherCameraResourceNumber() != -1) {
            resource = resourceManager.getWeatherResourceByNumber(bookmark.getWeatherCameraResourceNumber());
            if (resource != null) {
                cameraNote = resource.getName();
            } else {
                cameraNote = "The weather camera selected for this bookmark is no longer part of our system. ";
            }
        }

        if (bookmark.getWeatherMapLoopResourceNumber() != -1) {
            resource = resourceManager.getWeatherResourceByNumber(bookmark.getWeatherMapLoopResourceNumber());
            if (resource != null) {
                radarNote = resource.getName();
            } else {
                radarNote = "The radar resource selected for this bookmark is no longer part of our system. ";
            }
        }

        if (bookmark.getWeatherStationResourceNumber() != -1) {
            resource = resourceManager.getWeatherResourceByNumber(bookmark.getWeatherStationResourceNumber());
            if (resource != null) {
                dataPlotNote = resource.getName();
            } else {
                dataPlotNote = "The weather station resource selected for this bookmark is no longer part of our system. ";
            }
        }
        
        notesTextArea.setText("Notes/Comments: " + notes +
                "\nCreated date and time: " + createdDate +
                "\nCamera: " + cameraNote + "\nMap Loop: " + radarNote+
                "\nWeather Station: " + dataPlotNote +
                "\nCreated by: "+ createdBy);
        notesTextArea.setEditable(false);
        notesScrollPane.setWheelScrollingEnabled(true);
        notesTextArea.setCaretPosition(0);
        
        //Attach Files
        GeneralService generalService =  appControl.getGeneralService();
        for (BookmarkFileInstance bfi : generalService.getDBMSSystem().getFileManager().
                getAllFilesForBookmark(bookmark)) {
            attachedFiles.add(bfi);
            fileListModel.addElement(bfi.getFileName());
        }
        attachLabel.setVisible(fileListModel.size() > 0);
        fileList.setVisible(fileListModel.size() > 0);
        
        //Set size and focus
        int width = 821 + this.getInsets().left + this.getInsets().right;
        int height = 581 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        this.toFront();


        //This is the last line of the constructor, nothing should come after it.
        //Everything needs to come before it.
        super.postInitialize(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cameraPanel = new javax.swing.JPanel();
        cameraLabel = new javax.swing.JLabel();
        radarPanel = new javax.swing.JPanel();
        radarLabel = new javax.swing.JLabel();
        plotPanel = new javax.swing.JPanel();
        dataPlotLabel = new javax.swing.JLabel();
        notesPanel = new javax.swing.JPanel();
        notesScrollPane = new javax.swing.JScrollPane();
        notesTextArea = new javax.swing.JTextArea();
        openHourButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        attachLabel = new javax.swing.JLabel();
        fileListScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cameraPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cameraPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cameraLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cameraLabel.setText("No image is available for this bookmark.");
        cameraLabel.setToolTipText("");
        cameraLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cameraPanel.add(cameraLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 473, 277));

        getContentPane().add(cameraPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 501, 305));

        radarPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        radarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        radarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        radarLabel.setText("No image is available for this bookmark.");
        radarLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        radarPanel.add(radarLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 256, 159));

        getContentPane().add(radarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(525, 12, 284, 187));

        plotPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        plotPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dataPlotLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dataPlotLabel.setText("No image is available for this bookmark.");
        dataPlotLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plotPanel.add(dataPlotLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 256, 222));

        getContentPane().add(plotPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(525, 211, 284, 250));

        notesPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        notesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        notesTextArea.setColumns(20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setRows(5);
        notesScrollPane.setViewportView(notesTextArea);

        notesPanel.add(notesScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 473, 175));

        getContentPane().add(notesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 329, 501, 203));

        openHourButton.setText("<HTML><U>Open the hour related to this bookmark instance</U></HTML>");
        openHourButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        openHourButton.setRolloverEnabled(false);
        openHourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openHourButtonActionPerformed(evt);
            }
        });
        getContentPane().add(openHourButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 544, 414, 25));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(746, 544, 63, 25));
        getContentPane().add(attachLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(525, 473, 14, 17));

        fileListScrollPane.setBorder(null);

        fileList.setBackground(new java.awt.Color(236, 233, 216));
        fileList.setBorder(javax.swing.BorderFactory.createTitledBorder("FILES"));
        fileList.setModel(fileListModel);
        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileList.setFixedCellWidth(150);
        fileList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        fileList.setName(""); // NOI18N
        fileList.setVisibleRowCount(-1);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fileListMousePressed(evt);
            }
        });
        fileListScrollPane.setViewportView(fileList);

        getContentPane().add(fileListScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 473, 264, 60));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openHourButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openHourButtonActionPerformed
        openHourButton.setForeground(Color.RED);
        BookmarkEventOpener.openBooekmark(appControl, bookmark, movieController, 
                resourceRange);
        this.dispose();
    }//GEN-LAST:event_openHourButtonActionPerformed

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    this.dispose();
}//GEN-LAST:event_closeButtonActionPerformed

    private void fileListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMousePressed
        if (attachedFiles.size() == 0) {
            return;
        }

        int index = fileList.locationToIndex(evt.getPoint());
        BookmarkFileInstance bfi = attachedFiles.get(index);
        
        //Get location from user.
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(new File(CommonLocalFileManager
                        .getDataDirectory()), "Save File...", null, this);
        if (chosenDirectory == null) {
            fileList.clearSelection();
            return;
        }
        File dest = new File(chosenDirectory + File.separator
                + bfi.getFileName());
        if (dest.exists()) {
            JOptionPane.showMessageDialog(this,
                    "The file is already saved at this location.");
            fileList.clearSelection();
            return;
        }

        try {
            FileOutputStream fout = new FileOutputStream(dest);
            fout.write(bfi.getFileData());
            fout.close();
        } catch (Exception e) {
            fileList.clearSelection();
            JOptionPane.showMessageDialog(this, "An error occurred while trying to save the file.");
            //Test for remaining space in application home, which has no effect
            //if the save was not there.
            StorageSpaceTester.testApplicationHome();
            return;
        }
        fileList.clearSelection();
        JOptionPane.showMessageDialog(this, "File saved.");

        //Test for remaining space in application home, which has no effect
        //if the save was not there.
        StorageSpaceTester.testApplicationHome();
    }//GEN-LAST:event_fileListMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attachLabel;
    private javax.swing.JLabel cameraLabel;
    private javax.swing.JPanel cameraPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel dataPlotLabel;
    private javax.swing.JList fileList;
    private javax.swing.JScrollPane fileListScrollPane;
    private javax.swing.JPanel notesPanel;
    private javax.swing.JScrollPane notesScrollPane;
    private javax.swing.JTextArea notesTextArea;
    private javax.swing.JButton openHourButton;
    private javax.swing.JPanel plotPanel;
    private javax.swing.JLabel radarLabel;
    private javax.swing.JPanel radarPanel;
    // End of variables declaration//GEN-END:variables

}
