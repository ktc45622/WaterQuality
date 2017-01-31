package weather.clientside.manager;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.SearchResearchDataWindow;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.DateRangeSelectionWindow;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * ResourceSearchController is designed to let the user specify certain aspects
 * of the data they want to search, along with from what sources.
 * @author Ty Vanderstappen
 * @author Bingchen Yan
 * @version 2012
 */
public class ResourceSearchController extends javax.swing.JPanel {

    private Vector<Resource> camResources;
    private Vector<Resource> mapResources;
    private Vector<Resource> stationResources;
    private final ApplicationControlSystem appControl;
    SearchResearchDataWindow parent;
    private ResourceRange resourceRange;
    private Resource camera;
    private Resource mapLoop;
    private Resource station;
    private int max;  //Maximum number of images to load per resource.
     
    /**
     * Creates new form ResourceSearchController
     * @param appControl The ApplicationControlSystem.
     * @param camera The initial camera resource.
     * @param mapLoop The initial map loop resource.
     * @param station The weather station resource.
     * @param max The initial maximum number of images to load per resource.
     * @param parent the parent window of this panel.
     * @param resourceRange The current resource range.
     */
    public ResourceSearchController(ApplicationControlSystem appControl, 
            Resource camera, Resource mapLoop, Resource station, int max, 
            SearchResearchDataWindow parent, ResourceRange resourceRange) {
        this.appControl = appControl;
        this.parent = parent;
        this.resourceRange = resourceRange;
        this.camera = camera;
        this.mapLoop = mapLoop;
        this.station = station;
        this.max = max;
        initComponents();
        initializeList();
        setTimeRange();
    }
    
    /**
     * Initializes the combo boxes.
     */
    private void initializeList() {
        camResources = appControl.getGeneralService().getWeatherCameraResources();
        GUIComponentFactory.initCameraComboBox(cameraComboBox,
                appControl.getGeneralService(), false);
        for (int i = 0; i < cameraComboBox.getItemCount(); i++) {
            if (cameraComboBox.getItemAt(i).getName().equals(camera.getResourceName())) {
                cameraComboBox.setSelectedIndex(i);
                break;
            }
        }

        mapResources = appControl.getGeneralService().getWeatherMapLoopResources();
        GUIComponentFactory.initMapLoopComboBox(mapComboBox,
                appControl.getGeneralService(), false);
        for (int i = 0; i < mapComboBox.getItemCount(); i++) {
            if (mapComboBox.getItemAt(i).getName().equals(mapLoop.getResourceName())) {
                mapComboBox.setSelectedIndex(i);
                break;
            }
        }

        stationResources = appControl.getGeneralService().getWeatherStationResources();
        GUIComponentFactory.initWeatherStationComboBox(stationComboBox,
                appControl.getGeneralService(), false);
        for (int i = 0; i < stationComboBox.getItemCount(); i++) {
            if (stationComboBox.getItemAt(i).getName().equals(station.getResourceName())) {
                stationComboBox.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 1; i <= 60; i++) {
            imagesComboBox.addItem(i);
        }
        imagesComboBox.setSelectedIndex(max - 1);
    }

    /**
     * Returns the current camera time zone.
     *
     * @return The current camera time zone.
     */
    private TimeZone getCurrentCameraTimeZone() {
        return ((ResourceListCellItem) cameraComboBox
                .getSelectedItem()).getResourceTimeZone(appControl
                .getGeneralService());
    }

    /**
     * Sets timeRangeLabel to the current range in the current camera time zone.
     */
    private void setTimeRange() {
        StringBuilder newLabel = new StringBuilder();
        SimpleDateFormat timeFormat = new SimpleDateFormat("M/dd/yyyy hh:00a");
        SimpleDateFormat zoneFormat = new SimpleDateFormat("z");
        TimeZone timeZone = getCurrentCameraTimeZone();

        //Set time zone of formats.
        timeFormat.setTimeZone(timeZone);
        zoneFormat.setTimeZone(timeZone);

        //Create time zone string.
        String fullZoneString;  //Could have 2 labels if spanning DST change.
        String fromZoneString = zoneFormat.format(resourceRange.getStartTime());
        String toZoneString = zoneFormat.format(resourceRange.getStopTime());
        if (fromZoneString.equals(toZoneString)) {
            fullZoneString = toZoneString;
        } else {
            fullZoneString = fromZoneString + " - " + toZoneString;
        }

        //Set label txet.
        newLabel.append("<html><center><b>");
        newLabel.append(timeFormat.format(resourceRange.getStartTime()));
        newLabel.append(" to<br/>");
        newLabel.append(timeFormat.format(resourceRange.getStopTime()));
        newLabel.append("<br/>");
        newLabel.append(fullZoneString);
        newLabel.append("</b></center></html>");

        //Place text on label with "GMT" replaced by "UTC."
        timeRangeLabel.setText(newLabel.toString().replaceAll("GMT", "UTC"));
    }
    
    /**
     * Finds the resource that corresponds to the given name.
     *
     * @param name The name of the resource to find.
     * @param resources the specify resource list.
     * @return The resource that corresponds to the given name or null if no
     * resource is found.
     */
    private Resource getResourceForName(String name, Vector<Resource> resources) {
        for (Resource resource : resources) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        return null; // Resource of that name not found
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTime = new javax.swing.JPanel();
        timeRangeLabel = new javax.swing.JLabel();
        timeRangeButton = new javax.swing.JButton();
        pnlCombos = new javax.swing.JPanel();
        cameraComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        mapComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        stationComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        pnlBackOptions = new javax.swing.JPanel();
        pnlFirstButton = new javax.swing.JPanel();
        firstButton = new javax.swing.JButton();
        pnlBackButton = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        backLabel = new javax.swing.JLabel();
        pnlForwardOptions = new javax.swing.JPanel();
        pnlForwardButton = new javax.swing.JPanel();
        forwardButton = new javax.swing.JButton();
        forwardLabel = new javax.swing.JLabel();
        pnlLastButton = new javax.swing.JPanel();
        lastButton = new javax.swing.JButton();
        pnlGoTo = new javax.swing.JPanel();
        numTextField = new javax.swing.JTextField();
        gotoButton = new javax.swing.JButton();
        pnlMaxImageAndLoad = new javax.swing.JPanel();
        imagesLabel = new javax.swing.JLabel();
        plnNoImages = new javax.swing.JPanel();
        imagesComboBox = new javax.swing.JComboBox<Integer>();
        lblComboBoxSpaceFill = new javax.swing.JLabel();
        pnlLoadButton = new javax.swing.JPanel();
        loadButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1733, 90));
        setLayout(new java.awt.GridLayout(1, 0));

        pnlTime.setLayout(new javax.swing.BoxLayout(pnlTime, javax.swing.BoxLayout.Y_AXIS));

        timeRangeLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        timeRangeLabel.setText("Current Time Range");
        pnlTime.add(timeRangeLabel);

        timeRangeButton.setText("Select Time Range");
        timeRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeRangeButtonActionPerformed(evt);
            }
        });
        pnlTime.add(timeRangeButton);

        add(pnlTime);

        pnlCombos.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        cameraComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraComboBoxActionPerformed(evt);
            }
        });
        pnlCombos.add(cameraComboBox);

        pnlCombos.add(mapComboBox);

        pnlCombos.add(stationComboBox);

        add(pnlCombos);

        pnlBackOptions.setLayout(new java.awt.GridLayout(1, 0));

        pnlFirstButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 23));

        firstButton.setText("First");
        firstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstButtonActionPerformed(evt);
            }
        });
        pnlFirstButton.add(firstButton);

        pnlBackOptions.add(pnlFirstButton);

        pnlBackButton.setLayout(new java.awt.GridLayout(0, 1));

        backButton.setIcon(IconProperties.getArrowLeftSmallIcon());
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        pnlBackButton.add(backButton);

        backLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backLabel.setText("<html><center/>Back<br/>In Time</html>");
        pnlBackButton.add(backLabel);

        pnlBackOptions.add(pnlBackButton);

        add(pnlBackOptions);

        pnlForwardOptions.setLayout(new java.awt.GridLayout(1, 0));

        pnlForwardButton.setLayout(new java.awt.GridLayout(0, 1));

        forwardButton.setIcon(IconProperties.getArrowRightSmallIcon());
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });
        pnlForwardButton.add(forwardButton);

        forwardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forwardLabel.setText("<html><center/>Forward<br/>In Time</html>");
        pnlForwardButton.add(forwardLabel);

        pnlForwardOptions.add(pnlForwardButton);

        pnlLastButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 25));

        lastButton.setText("Last");
        lastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastButtonActionPerformed(evt);
            }
        });
        pnlLastButton.add(lastButton);

        pnlForwardOptions.add(pnlLastButton);

        add(pnlForwardOptions);

        pnlGoTo.setLayout(new javax.swing.BoxLayout(pnlGoTo, javax.swing.BoxLayout.X_AXIS));

        numTextField.setMaximumSize(new java.awt.Dimension(50, 22));
        numTextField.setMinimumSize(new java.awt.Dimension(50, 22));
        numTextField.setPreferredSize(new java.awt.Dimension(50, 22));
        pnlGoTo.add(numTextField);

        gotoButton.setText("GOTO");
        gotoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoButtonActionPerformed(evt);
            }
        });
        pnlGoTo.add(gotoButton);

        add(pnlGoTo);

        pnlMaxImageAndLoad.setLayout(new java.awt.GridLayout(0, 1));

        imagesLabel.setText("Max # Images Per Hour");
        pnlMaxImageAndLoad.add(imagesLabel);

        plnNoImages.setLayout(new java.awt.GridLayout(1, 0));

        imagesComboBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        plnNoImages.add(imagesComboBox);
        plnNoImages.add(lblComboBoxSpaceFill);

        pnlMaxImageAndLoad.add(plnNoImages);

        pnlLoadButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        loadButton.setText("Load Data");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        pnlLoadButton.add(loadButton);

        pnlMaxImageAndLoad.add(pnlLoadButton);

        add(pnlMaxImageAndLoad);
    }// </editor-fold>//GEN-END:initComponents

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardButtonActionPerformed
        parent.nextSet();
    }//GEN-LAST:event_forwardButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        parent.previousSet();
    }//GEN-LAST:event_backButtonActionPerformed

    private void timeRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeRangeButtonActionPerformed
        ResourceRange newRange = DateRangeSelectionWindow
                .getNewResourceRange(resourceRange, getCurrentCameraTimeZone(), 
                false, false, false);
        if (newRange != null) {
            this.resourceRange = newRange;
            setTimeRange();
        }
    }//GEN-LAST:event_timeRangeButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        this.camera = getResourceForName(cameraComboBox.getSelectedItem().toString(), camResources);
        this.mapLoop = getResourceForName(mapComboBox.getSelectedItem().toString(), mapResources);
        this.station = getResourceForName(stationComboBox.getSelectedItem().toString(), stationResources);
        this.max = imagesComboBox.getSelectedIndex() + 1;
        parent.setCamera(camera);
        parent.setMap(mapLoop);
        parent.setStation(station);
        parent.updateLabel();
        parent.setRange(resourceRange);
        parent.setIndex(1);
        parent.setMax(max);
        try {
            parent.loadData();
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void lastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastButtonActionPerformed
        parent.last();
    }//GEN-LAST:event_lastButtonActionPerformed

    private void firstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstButtonActionPerformed
        parent.start();
    }//GEN-LAST:event_firstButtonActionPerformed

    private void gotoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoButtonActionPerformed
        int position = 0;
        try{
        position = Integer.parseInt(numTextField.getText());
        parent.setPosition(position);
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(parent, "Please input a integer",
            "Message", JOptionPane.INFORMATION_MESSAGE);
        }//select the number of camera image
    }//GEN-LAST:event_gotoButtonActionPerformed

    private void cameraComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraComboBoxActionPerformed
        this.setTimeRange();
    }//GEN-LAST:event_cameraComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JLabel backLabel;
    private javax.swing.JComboBox<ResourceListCellItem> cameraComboBox;
    private javax.swing.JButton firstButton;
    private javax.swing.JButton forwardButton;
    private javax.swing.JLabel forwardLabel;
    private javax.swing.JButton gotoButton;
    private javax.swing.JComboBox<Integer> imagesComboBox;
    private javax.swing.JLabel imagesLabel;
    private javax.swing.JButton lastButton;
    private javax.swing.JLabel lblComboBoxSpaceFill;
    private javax.swing.JButton loadButton;
    private javax.swing.JComboBox<ResourceListCellItem> mapComboBox;
    private javax.swing.JTextField numTextField;
    private javax.swing.JPanel plnNoImages;
    private javax.swing.JPanel pnlBackButton;
    private javax.swing.JPanel pnlBackOptions;
    private javax.swing.JPanel pnlCombos;
    private javax.swing.JPanel pnlFirstButton;
    private javax.swing.JPanel pnlForwardButton;
    private javax.swing.JPanel pnlForwardOptions;
    private javax.swing.JPanel pnlGoTo;
    private javax.swing.JPanel pnlLastButton;
    private javax.swing.JPanel pnlLoadButton;
    private javax.swing.JPanel pnlMaxImageAndLoad;
    private javax.swing.JPanel pnlTime;
    private javax.swing.JComboBox<ResourceListCellItem> stationComboBox;
    private javax.swing.JButton timeRangeButton;
    private javax.swing.JLabel timeRangeLabel;
    // End of variables declaration//GEN-END:variables
}
