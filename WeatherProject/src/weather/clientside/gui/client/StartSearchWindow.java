package weather.clientside.gui.client;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import weather.ApplicationControlSystem;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.clientside.utilities.ResourceVisibleTester;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.utilities.DateRangeSelectionWindow;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>StartSearchWindow</code> class creates a form that
 * allows for searching for observational data gathered by weather resources.
 * Start with this window to customize user settings.
 * The user can change the settings using the drop down lists to select different
 * camera, map loop, station and maximum number of images per hour, and change 
 * different time range by clicking “select different time range” button.

 * @author Bingchen Yan
 * @version 2012 spring
 */
public class StartSearchWindow extends weather.common.gui.component.BUJFrame {

    private Vector<Resource> camResources;
    private Vector<Resource> mapResources;
    private Vector<Resource> stationResources;
    private String defaultWeatherCamera;
    private String defaultWeatherMapLoop;
    private String defaultWeatherStation;
    private ApplicationControlSystem appControl;
    private ResourceRange resourceRange;
    private Resource camera;
    private Resource mapLoop;
    private Resource station;
    private int max;
    /**
     * Creates new form StartSearchWindow
     * @param appControl The ApplicationControlSystem.
     * @param resourceRange The current resource range.
     */
    public StartSearchWindow(ApplicationControlSystem appControl, ResourceRange resourceRange) {
        super();
        this.appControl = appControl;
        this.resourceRange = resourceRange;
        defaultWeatherCamera = PropertyManager.getLocalProperty("DEFAULT_WEATHER_CAMERA");
        defaultWeatherMapLoop = PropertyManager.getLocalProperty("DEFAULT_WEATHER_MAP_LOOP");
        defaultWeatherStation = PropertyManager.getLocalProperty("DEFAULT_WEATHER_STATION");
        this.camera = appControl.getGeneralService().getCurrentWeatherCameraResource();
        this.mapLoop = appControl.getGeneralService().getCurrentWeatherMapLoopResource();
        this.station = appControl.getGeneralService().getCurrentWeatherStationResource();
        this.max = 30;
        initComponents();
        initializeList();
        setTimeRange();
        setTitle("Weather Viewer - Search Observational Data");
        super.postInitialize(true);
    }
    
    /**
     * Initializes the combo boxes.
     */
    private void initializeList() {
        camResources = appControl.getGeneralService().getWeatherCameraResources();
        GUIComponentFactory.initCameraComboBox(cameraComboBox, 
                appControl.getGeneralService(), false);
        //Default to current camera, if pressent.
        if (camera != null) {
            for(int i = 0; i < cameraComboBox.getItemCount(); i++) {
                if (cameraComboBox.getItemAt(i).getName().equals(camera.getResourceName())) {
                    cameraComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            //Set to camera if user can see it.
            for (int i = 0; i < camResources.size(); i++) {
                Resource curCamera = camResources.get(i);
                if (curCamera.getName().equals(defaultWeatherCamera)) {
                    if (ResourceVisibleTester.canUserSeeResource(appControl
                            .getGeneralService().getUser(), curCamera)) {
                        cameraComboBox.setSelectedIndex(i);
                    } else {
                        //Can't see default, so settle for start of list.
                        cameraComboBox.setSelectedIndex(0);
                    }
                    break;
                }
            }
        }
        
        mapResources = appControl.getGeneralService().getWeatherMapLoopResources();
        GUIComponentFactory.initMapLoopComboBox(mapComboBox, 
                appControl.getGeneralService(), false);
        //Default to current map loop, if pressent.
        if (mapLoop != null) {
            for (int i = 0; i < mapComboBox.getItemCount(); i++) {
                if (mapComboBox.getItemAt(i).getName().equals(mapLoop.getResourceName())) {
                    mapComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            //Set to map if user can see it.
            for (int i = 0; i < mapResources.size(); i++) {
                Resource curMap = mapResources.get(i);
                if (curMap.getName().equals(defaultWeatherMapLoop)) {
                    if (ResourceVisibleTester.canUserSeeResource(appControl
                            .getGeneralService().getUser(), curMap)) {
                        mapComboBox.setSelectedIndex(i);
                    } else {
                        //Can't see default, so settle for start of list.
                        mapComboBox.setSelectedIndex(0);
                    }
                    break;
                }
            }
        }
        
        stationResources = appControl.getGeneralService().getWeatherStationResources();
        GUIComponentFactory.initWeatherStationComboBox(stationComboBox,
                appControl.getGeneralService(), false);
        //Default to current weather station, if pressent.
        if (station != null) {
            for (int i = 0; i < stationComboBox.getItemCount(); i++) {
                if (stationComboBox.getItemAt(i).getName().equals(station.getResourceName())) {
                    stationComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            //Set to weather station if user can see it.
            for (int i = 0; i < stationResources.size(); i++) {
                Resource curStation = stationResources.get(i);
                if (curStation.getName().equals(defaultWeatherStation)) {
                    if (ResourceVisibleTester.canUserSeeResource(appControl
                            .getGeneralService().getUser(), curStation)) {
                        stationComboBox.setSelectedIndex(i);
                    } else {
                        //Can't see default, so settle for start of list.
                        stationComboBox.setSelectedIndex(0);
                    }
                    break;
                }
            }
        }
        
        for(int i = 1; i<=60; i++){
            imagesComboBox.addItem(i);
        }
        imagesComboBox.setSelectedIndex(max - 1);
    }
    
    /**
     * Returns the current camera time zone.
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
        String fromZoneString = zoneFormat.format(resourceRange.getStartTime())
                .toString();
        String toZoneString = zoneFormat.format(resourceRange.getStopTime())
                .toString();
        if (fromZoneString.equals(toZoneString)) {
            fullZoneString = toZoneString;
        } else {
            fullZoneString = fromZoneString + " - " + toZoneString;
        }

        //Set label txet.
        newLabel.append("<html><center><b>");
        newLabel.append(timeFormat.format(resourceRange.getStartTime())
                .toString());
        newLabel.append(" to<br/>");
        newLabel.append(timeFormat.format(resourceRange.getStopTime())
                .toString());
        newLabel.append("<br/>");
        newLabel.append(fullZoneString);
        newLabel.append("</b></center></html>");

        //Place text or label with "GMT" replaced by "UTC."
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

        cameraComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        mapComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        stationComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        timeRangeLabel = new javax.swing.JLabel();
        timeRangeButton = new javax.swing.JButton();
        imagesLabel = new javax.swing.JLabel();
        imagesComboBox = new javax.swing.JComboBox<Integer>();
        startButton = new javax.swing.JButton();
        textLabel = new javax.swing.JLabel();
        rangeHeaderLabel = new javax.swing.JLabel();
        cameraLabel = new javax.swing.JLabel();
        mapLabel = new javax.swing.JLabel();
        stationLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        cameraComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraComboBoxActionPerformed(evt);
            }
        });

        timeRangeLabel.setText("Current Time Range");

        timeRangeButton.setText("Select Different Time Range");
        timeRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeRangeButtonActionPerformed(evt);
            }
        });

        imagesLabel.setText("Maximum Number of Images Per Hour:");

        imagesComboBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        textLabel.setText("Please select the data:");

        rangeHeaderLabel.setText("Resource Range:");

        cameraLabel.setText("Camera:");

        mapLabel.setText("Maploop:");

        stationLabel.setText("Station:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(imagesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(imagesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stationLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textLabel)
                                    .addComponent(rangeHeaderLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(timeRangeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(timeRangeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(31, 31, 31)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cameraLabel)
                            .addComponent(mapComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cameraComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mapLabel))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textLabel)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeHeaderLabel)
                    .addComponent(cameraLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cameraComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mapLabel))
                    .addComponent(timeRangeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeRangeButton)
                    .addComponent(mapComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imagesLabel)
                    .addComponent(stationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imagesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void timeRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeRangeButtonActionPerformed
        ResourceRange newRange = DateRangeSelectionWindow
                .getNewResourceRange(resourceRange, getCurrentCameraTimeZone(), 
                false, false, false);
        if (newRange != null) {
            this.resourceRange = newRange;
            setTimeRange();
        }
    }//GEN-LAST:event_timeRangeButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        this.camera = getResourceForName(cameraComboBox.getSelectedItem().toString(), camResources);
        this.mapLoop = getResourceForName(mapComboBox.getSelectedItem().toString(), mapResources);
        this.station = getResourceForName(stationComboBox.getSelectedItem().toString(), stationResources);
        this.max = imagesComboBox.getSelectedIndex() + 1;
        this.setVisible(false);
        try {
            new SearchResearchDataWindow(appControl, camera, mapLoop, station, max, resourceRange);
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_startButtonActionPerformed

    private void cameraComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraComboBoxActionPerformed
        setTimeRange();
    }//GEN-LAST:event_cameraComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ResourceListCellItem> cameraComboBox;
    private javax.swing.JLabel cameraLabel;
    private javax.swing.JComboBox<Integer> imagesComboBox;
    private javax.swing.JLabel imagesLabel;
    private javax.swing.JComboBox<ResourceListCellItem> mapComboBox;
    private javax.swing.JLabel mapLabel;
    private javax.swing.JLabel rangeHeaderLabel;
    private javax.swing.JButton startButton;
    private javax.swing.JComboBox<ResourceListCellItem> stationComboBox;
    private javax.swing.JLabel stationLabel;
    private javax.swing.JLabel textLabel;
    private javax.swing.JButton timeRangeButton;
    private javax.swing.JLabel timeRangeLabel;
    // End of variables declaration//GEN-END:variables
}
