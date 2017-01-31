package weather.clientside.gui.client;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Vector;
import weather.GeneralService;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.clientside.utilities.ResourceVisibleTester;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.DateRangeSelectionWindow;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;

/**
 * Advanced options for the login window.
 *
 * @author arb35598
 * @author Dustin Jones
 * @author Trevor Erdley
 */
public class AdvancedLoginWindow extends BUDialog {
    // List of valid resources.

    private Vector<Resource> camResources;
    private Vector<Resource> siteResources;
    private Vector<Resource> weatherStationResources;
    private ResourceRange range = ResourceTimeManager.getResourceRange();
    private GeneralService generalService;
    /**
     * Variables used to save the users previously selected options. When
     * close/cancel is pressed these values are used to re-populate the selected
     * choices on the form. These variables are only used in
     * recordUserSelection() and revertUserChanges().
     */
    private boolean loadWeatherCam;
    private Object cameraSelection;
    private boolean loadRadar;
    private Object radarSelection;
    private boolean loadStation;
    private Object stationSelection;
    private ResourceRange oldRange;
    /**
     * Variables for string formatting.
     */
    private static final String dateFormat = "MM/dd/yy hh:00 a z";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    private String fromString;
    private String toString;
    /**
     * Variables to set parent login window to "Normal" if cancel is selected
     * before ok in the lifespan of the form, which can appear more than once. 
     */
    LoginWindow parent;
    boolean setParentToNormal = true; //False once "ok" is presswed
    
    /**
     * Used when parent leaves "Selective" mode.  When this happens, we no 
     * longer want the program to load with previously selected data if this
     * form is canceled.
     */
    public void setTriggeringOfParentResetsOnCancel() {
        setParentToNormal = true;
    }
    
    /**
     * Creates new form AdvancedLoginWindow.
     */
    public AdvancedLoginWindow(GeneralService generalService,
            Vector<Resource> camResources, Vector<Resource> mapLoopResources,
            Vector<Resource> weatherStationResources, LoginWindow parent) {
        super();
        initComponents();
        int width = 412 + this.getInsets().left + this.getInsets().right;
        int height = 273 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();

        this.generalService = generalService;
        this.parent = parent;
        this.camResources = camResources;
        this.siteResources = mapLoopResources;
        this.weatherStationResources = weatherStationResources;
        
        GUIComponentFactory.initCameraComboBox(cameraResourceComboBox, 
                generalService, true);
        GUIComponentFactory.initMapLoopComboBox(siteResourceComboBox, 
                generalService, true);
        GUIComponentFactory.initWeatherStationComboBox(stationResourceComboBox, 
                generalService, true);
        
        initDefaultSelection();
        loadDefaultResources();
        recordUserSelection();  //Make defaults current selection, so cancal will work.
        super.postInitialize(false);
    }
    
    /**
     * Load default resources.
     */
    private void loadDefaultResources(){
        String defaultWeatherCamera = PropertyManager.getDefaultProperty("DEFAULT_WEATHER_CAMERA");
        String defaultWeatherMapLoop = PropertyManager.getDefaultProperty("DEFAULT_WEATHER_MAP_LOOP");
        String defaultWeatherStation = PropertyManager.getDefaultProperty("DEFAULT_WEATHER_STATION");
        
        // Find default camera.
        for (int i = 0; i < camResources.size(); i++) {
            Resource curCamera = camResources.get(i);
            if (curCamera.getName().equals(defaultWeatherCamera)) {
                // Set to camera if user can see it.
                if (ResourceVisibleTester.canUserSeeResource(generalService.getUser(), curCamera)) {
                    cameraResourceComboBox.setSelectedIndex(i);
                }
                break;
            }
        }
        
        // Show resource range in camera time zone.
        setLabel();
        
        // Find default map.
        for (int i = 0; i < siteResources.size(); i++) {
            Resource curMap = siteResources.get(i);
            if (curMap.getName().equals(defaultWeatherMapLoop)) {
                // Set to map if user can see it.
                if (ResourceVisibleTester.canUserSeeResource(generalService.getUser(), curMap)) {
                    siteResourceComboBox.setSelectedIndex(i);
                }
                break;
            }
        }

        // Find default station.
        for (int i = 0; i < weatherStationResources.size(); i++) {
            Resource curStation = weatherStationResources.get(i);
            if (curStation.getName().equals(defaultWeatherStation)) {
                // Set to station if user can see it.
                if (ResourceVisibleTester.canUserSeeResource(generalService.getUser(), curStation)) {
                    stationResourceComboBox.setSelectedIndex(i);
                }
                break;
            }
        }
    }
    
    /**
     * Displays the current time range in the time of the current camera.
     */
    private void setLabel() {
        java.util.Date from = range.getStartTime();
        java.util.Date to = range.getStopTime();
        TimeZone currentTimeZone;
        if (cameraCheckBox.isSelected()) {
            currentTimeZone = ((ResourceListCellItem) cameraResourceComboBox
                .getSelectedItem()).getResourceTimeZone(generalService);
        } else {
            currentTimeZone = TimeZone.getDefault();
        }
        simpleDateFormat.setTimeZone(currentTimeZone);
        
        //Change GMT to UTC
        fromString = simpleDateFormat.format(from).toString()
                .replaceAll("GMT", "UTC");
        toString = simpleDateFormat.format(to).toString()
                .replaceAll("GMT", "UTC");
        rangeLabel.setText("<html>" + fromString + " to<br/>" + toString 
                + "</html>");
    }
    
    /**
     * Used to initialize common settings for all constructors.
     */
    private void initDefaultSelection() {
        //Initialize variables used in saving/reverting user selection
        loadStation = false;
        loadRadar = false;
        loadWeatherCam = false;
    }

    /**
     * Used to record the selections made by the user on the current form.
     */
    private void recordUserSelection() {
        loadWeatherCam = cameraCheckBox.isSelected();
        cameraSelection = cameraResourceComboBox.getSelectedItem();
        loadRadar = radarCheckBox.isSelected();
        radarSelection = siteResourceComboBox.getSelectedItem();
        loadStation = stationCheckBox.isSelected();
        stationSelection = stationResourceComboBox.getSelectedItem();
        oldRange = range;
        loadWeatherCam = cameraCheckBox.isSelected();
        loadRadar = radarCheckBox.isSelected();
        loadStation = stationCheckBox.isSelected();
    }

    /**
     * Used to restore the saved form state. This method used with
     * recordUserSelection allows the window to be modified and then reverted to
     * the last selected state.
     */
    private void revertUserChanges() {
        stationResourceComboBox.setSelectedItem(stationSelection);
        siteResourceComboBox.setSelectedItem(radarSelection);
        cameraResourceComboBox.setSelectedItem(cameraSelection);

        cameraCheckBox.setSelected(loadWeatherCam);
        radarCheckBox.setSelected(loadRadar);
        stationCheckBox.setSelected(loadStation);

        /**
         * If the variable is null, we need to select none, which is the last
         * item in the combo box. We can use the resource vector.size() to
         * always get the accurate index. If the vector size is 0 then we select
         * item zero from the combo box which is guaranteed to be the only item,
         * which is at position 0.
         */
        if (cameraSelection == null) {
            cameraResourceComboBox.setSelectedIndex(camResources.size());
        }
        if (radarSelection == null) {
            siteResourceComboBox.setSelectedIndex(siteResources.size());
        }
        if (stationSelection == null) {
            stationResourceComboBox.setSelectedIndex(weatherStationResources.size());
        }
        range = oldRange;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startUpModeLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cameraCheckBox = new javax.swing.JCheckBox();
        radarCheckBox = new javax.swing.JCheckBox();
        stationCheckBox = new javax.swing.JCheckBox();
        cameraResourceComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        siteResourceComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        stationResourceComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        rangeLabel = new javax.swing.JLabel();
        setRangeButton = new javax.swing.JButton();
        lblCurrentRangeText = new javax.swing.JLabel();

        setTitle("Weather Viewer - Advanced Startup Options");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        startUpModeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        startUpModeLabel.setText("Please select your desired startup data from the options below.");
        getContentPane().add(startUpModeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 388, -1));

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Close out of this window without saving changes");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(329, 236, -1, -1));

        okButton.setText("OK");
        okButton.setToolTipText("Apply the selected login settings and return to the login screen");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        getContentPane().add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(257, 236, 65, -1));

        cameraCheckBox.setSelected(true);
        cameraCheckBox.setText("Load Weather Camera");
        cameraCheckBox.setToolTipText("Select an available weather camera to be loaded");
        cameraCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraCheckBoxActionPerformed(evt);
            }
        });
        getContentPane().add(cameraCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 46, -1, -1));

        radarCheckBox.setSelected(true);
        radarCheckBox.setText("Load Weather Map Data");
        radarCheckBox.setToolTipText("Select an available radar map to be loaded");
        radarCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radarCheckBoxActionPerformed(evt);
            }
        });
        getContentPane().add(radarCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 82, -1, -1));

        stationCheckBox.setSelected(true);
        stationCheckBox.setText("Load Weather Station Plot");
        stationCheckBox.setToolTipText("Select an available weather station to be loaded");
        stationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stationCheckBoxActionPerformed(evt);
            }
        });
        getContentPane().add(stationCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 118, -1, -1));

        cameraResourceComboBox.setToolTipText("The list of weather cameras available");
        cameraResourceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraResourceComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(cameraResourceComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 46, 203, -1));

        siteResourceComboBox.setToolTipText("The list of available radar site maps");
        getContentPane().add(siteResourceComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 82, 203, -1));

        stationResourceComboBox.setToolTipText("The list of weather stations available");
        getContentPane().add(stationResourceComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 118, 203, -1));
        getContentPane().add(rangeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 154, 260, 32));

        setRangeButton.setText("Change Date Range...");
        setRangeButton.setToolTipText("Set the specific date and time of the resource range to be loaded");
        setRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setRangeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(setRangeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, -1, -1));

        lblCurrentRangeText.setText("Current Date Range:");
        getContentPane().add(lblCurrentRangeText, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 154, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The Cancel Button is clicked.
     *
     * @param evt The ActionEvent to see if the "Cancel" button was clicked on.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.revertUserChanges();
        if(setParentToNormal) {
            // Options not kept; set parent to "Normal" login mode'
            parent.revertToNormal();
        }
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * OK button is clicked and sends the new information to the main
     * application. This is where the settings are updated in
     * ResourceTimeManager.
     *
     * @param evt The ActionEvent to see if the "OK" button was clicked on.
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.recordUserSelection();
        this.setParentToNormal = false;  // Keep "Selective" option
        this.setLoginMode();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Sets the login mode.
     */
    private void setLoginMode() {
        if (this.cameraCheckBox.isSelected()) {
            if ((cameraResourceComboBox.getSelectedItem()).toString().equals("None")) {
                generalService.setCurrentWeatherCameraResource(null);
            } else {
                for (int i = 0; i < camResources.size(); i++) {
                    if (camResources.get(i).getName().equals(cameraResourceComboBox.getSelectedItem().toString())) {
                        generalService.setCurrentWeatherCameraResource(camResources.get(i));
                    }
                }
            }
        } else {
            generalService.setCurrentWeatherCameraResource(null);
        }

        if (this.radarCheckBox.isSelected()) {
            if ((siteResourceComboBox.getSelectedItem().toString()).equals("None")) {
                generalService.setCurrentWeatherMapLoopResource(null);
            } else {
                for (int i = 0; i < siteResources.size(); i++) {
                    if (siteResources.get(i).getName().equals(siteResourceComboBox.getSelectedItem().toString())) {
                        generalService.setCurrentWeatherMapLoopResource(siteResources.get(i));
                    }
                }
            }
        } else {
            generalService.setCurrentWeatherMapLoopResource(null);
        }

        if (this.stationCheckBox.isSelected()) {
            if ((stationResourceComboBox.getSelectedItem().toString()).equals("None")) {
                generalService.setCurrentWeatherStationResource(null);
            } else {
                for (int i = 0; i < weatherStationResources.size(); i++) {
                    if (weatherStationResources.get(i).getName().equals(stationResourceComboBox.getSelectedItem().toString())) {
                        generalService.setCurrentWeatherStationResource(weatherStationResources.get(i));
                    }
                }
            }
        } else {
            generalService.setCurrentWeatherStationResource(null);
        }
        ResourceTimeManager.setResourceRange(range);
        this.setVisible(false);
    }

    /**
     * The set range button was clicked. Brings up a new DateRange selector.
     *
     * @param evt The ActionEvent to see if the "Set Range" button was clicked
     * on.
     */
    private void setRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setRangeButtonActionPerformed
        TimeZone currentTimeZone;
        if (cameraCheckBox.isSelected()) {
            currentTimeZone = ((ResourceListCellItem) cameraResourceComboBox
                    .getSelectedItem()).getResourceTimeZone(generalService);
        } else {
            currentTimeZone = TimeZone.getDefault();
        }
        ResourceRange newRange = DateRangeSelectionWindow
                .getNewResourceRange(range, currentTimeZone, false, false,
                false);
        if (newRange != null) {
            range = newRange;
        }
        setLabel();
        rangeLabel.repaint();
    }//GEN-LAST:event_setRangeButtonActionPerformed

    /**
     * Checks to see if the camera CheckBox was selected.
     *
     * @param evt The ActionEvent to check to see if the "Camera" CheckBox was
     * selected.
     */
    private void cameraCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraCheckBoxActionPerformed
        if (cameraCheckBox.isSelected()) {
            cameraResourceComboBox.setEnabled(true);
        } else {
            cameraResourceComboBox.setEnabled(false);
        }
        setLabel();
    }//GEN-LAST:event_cameraCheckBoxActionPerformed

    /**
     * Checks to see if the "Radar" CheckBox was selected.
     *
     * @param evt The ActionEvent to check to see if the "Radar" CheckBox was
     * selected.
     */
    private void radarCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radarCheckBoxActionPerformed
        if (radarCheckBox.isSelected()) {
            siteResourceComboBox.setEnabled(true);
        } else {
            siteResourceComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_radarCheckBoxActionPerformed

    /**
     * Checks to see if the "Station" CheckBox was selected.
     *
     * @param evt The ActionEvent to check to see if the "Station" CheckBox was
     * selected.
     */
    private void stationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stationCheckBoxActionPerformed
        if (stationCheckBox.isSelected()) {
            stationResourceComboBox.setEnabled(true);
        } else {
            stationResourceComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_stationCheckBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.revertUserChanges();
    }//GEN-LAST:event_formWindowClosing

    private void cameraResourceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraResourceComboBoxActionPerformed
        setLabel();
    }//GEN-LAST:event_cameraResourceComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cameraCheckBox;
    private javax.swing.JComboBox<ResourceListCellItem> cameraResourceComboBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel lblCurrentRangeText;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox radarCheckBox;
    private javax.swing.JLabel rangeLabel;
    private javax.swing.JButton setRangeButton;
    private javax.swing.JComboBox<ResourceListCellItem> siteResourceComboBox;
    private javax.swing.JLabel startUpModeLabel;
    private javax.swing.JCheckBox stationCheckBox;
    private javax.swing.JComboBox<ResourceListCellItem> stationResourceComboBox;
    // End of variables declaration//GEN-END:variables
}
