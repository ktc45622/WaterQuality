package weather.clientside.gui.client;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.common.data.resource.Resource;
import weather.common.data.weatherstation.WeatherStationVariableProperties;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CommonLocalFileManager;
import weather.common.utilities.PropertyManager;

/**
 * This class represents the Settings window.
 *
 * @author Xiang Li (2014)
 * @author Colton Daily (2014)
 */
public class SettingsWindow extends BUDialog {

    private ItemListener newListener;
    private WeatherStationVariableProperties wvProps;
    
    /**
     * Creates the SettingsWindow
     *
     * @param appControl The Application Control object to give us access to all
     * Application Control Systems and Managers.
     */
    public SettingsWindow(ApplicationControlSystem appControl) {

        super(appControl);
        wvProps = new WeatherStationVariableProperties();
        initComponents();
        
        int width = 442 + this.getInsets().left + this.getInsets().right;
        int height = 434 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        //get all the resources
        GUIComponentFactory.initCameraComboBox(cameraComboBox, 
                appControl.getGeneralService(), false);
        GUIComponentFactory.initMapLoopComboBox(mapComboBox, 
                appControl.getGeneralService(), false);
        GUIComponentFactory.initWeatherStationComboBox(weatherStationComboBox, 
                appControl.getGeneralService(), false);
        Vector<String> ordering = wvProps.getOrdering();
        int numberOfVariables = ordering.size();
        for (int i = 0; i < numberOfVariables; i++) {
            String variableKey = ordering.get(i);
            initialPlotDataTraceComboBox.addItem(wvProps.getDisplayName(variableKey));
        }
        getDefaultSettings();
        newListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    //Get camera
                    Resource camera = getCameraFromName(cameraComboBox
                            .getSelectedItem().toString());
                    if (camera == null) {
                        return;
                    }
                    
                    //Get station
                    Resource station = getAppControl().getDBMSSystem()
                            .getResourceRelationManager()
                            .getRelatedStationResource(camera);
                    if (station == null) {
                        return;
                    }
                    
                    //Set station combo box
                    for (int i = 0; i < weatherStationComboBox.getItemCount(); i++) {
                        if (weatherStationComboBox.getItemAt(i).toString()
                                .equals(station.getName())) {
                            weatherStationComboBox.setSelectedIndex(i);
                        }
                    }
                }   
            }
        };
        cameraComboBox.addItemListener(newListener);
        super.postInitialize(true);
    }
    
    /**
     * Returns the object's <code>ApplicationControlSystem</code> object for use
     * by event handlers without direct access to it.
     * @return The object's <code>ApplicationControlSystem</code> object.
     */
    private ApplicationControlSystem getAppControl() {
        return this.appControl;
    }
    
     /**
     * Finds the camera that corresponds to the given name.
     * @param name The name of the resource to find.
     * @return The camera that corresponds to the given name or null if no
     * resource is found.
     */
    private Resource getCameraFromName(String name) {
        Vector<Resource> resources = appControl.getGeneralService()
                .getWeatherCameraResources();
        for (Resource resource : resources) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        return null; // Resource of that name not found
    }
    
    /**
     * This method will set the combo boxes and the text field to the current
     * default settings in the local properties file.
     */
    private void getDefaultSettings() {
        for (int i = 0; i < cameraComboBox.getItemCount(); i++) {
            if (cameraComboBox.getItemAt(i).toString().equals(PropertyManager.getLocalProperty("DEFAULT_WEATHER_CAMERA"))) {
                cameraComboBox.setSelectedIndex(i);
            }
        }

        for (int i = 0; i < mapComboBox.getItemCount(); i++) {
            if (mapComboBox.getItemAt(i).toString().equals(PropertyManager.getLocalProperty("DEFAULT_WEATHER_MAP_LOOP"))) {
                mapComboBox.setSelectedIndex(i);
            }
        }

        for (int i = 0; i < weatherStationComboBox.getItemCount(); i++) {
            if (weatherStationComboBox.getItemAt(i).toString().equals(PropertyManager.getLocalProperty("DEFAULT_WEATHER_STATION"))) {
                weatherStationComboBox.setSelectedIndex(i);
            }
        }

        hoursTextField.setText(PropertyManager.getLocalProperty("DEFAULT_START_HOURS"));
        
        for (int i = 0; i < calendarComboBox.getItemCount(); i++) {
            if (calendarComboBox.getItemAt(i).toString().equals(PropertyManager.getLocalProperty("DEFAULT_WEATHER_STATION_SPAN"))) {
                calendarComboBox.setSelectedIndex(i);
            }
        }
        
        Vector<String> ordering = wvProps.getOrdering();
        int numberOfVariables = ordering.size();
        for (int i = 0; i < numberOfVariables; i++) {
            String variableKey = ordering.get(i);
            if (variableKey.contains(PropertyManager.getLocalProperty("INITIAL_PLOT_DATA_TRACE"))) {
                if (initialPlotDataTraceComboBox.getItemAt(i).toString().equals(wvProps.getDisplayName(variableKey))) {
                    initialPlotDataTraceComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        maxMoviesTextField.setText(PropertyManager.getGeneralProperty("MAX_LOCAL_AVI_TO_KEEP"));
        
        numberOfDaysWeatherStationTextField.setText(PropertyManager.getGeneralProperty("MAX_LOCAL_WEATHER_STATION_DAYS_TO_KEEP"));
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
        defaultCameraLabel = new javax.swing.JLabel();
        defaultMapLabel = new javax.swing.JLabel();
        weatherStationComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        defaultWeatherStationLabel = new javax.swing.JLabel();
        hoursLabel = new javax.swing.JLabel();
        hoursTextField = new javax.swing.JTextField();
        updateButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        topDescriptionLabel = new javax.swing.JLabel();
        cameraIconLabel = new javax.swing.JLabel();
        defaultMapIconLabel = new javax.swing.JLabel();
        defaultWeatherStationIconLabel = new javax.swing.JLabel();
        defaultHoursToLoadIconLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        calendarIconLabel = new javax.swing.JLabel();
        defaultCalendarLabel = new javax.swing.JLabel();
        calendarComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        initialPlotDataTraceLabel = new javax.swing.JLabel();
        initialPlotDataTraceComboBox = new javax.swing.JComboBox();
        maxMoviesLabel = new javax.swing.JLabel();
        maxMoviesTextField = new javax.swing.JTextField();
        deleteMoviesButton = new javax.swing.JButton();
        numberDaysWeatherStationLabel = new javax.swing.JLabel();
        numberOfDaysWeatherStationTextField = new javax.swing.JTextField();
        sharedDataDescriptionLabel = new javax.swing.JLabel();
        weatherStationVariableIconLabel = new javax.swing.JLabel();
        weatherStationKeepNumberIconLabel = new javax.swing.JLabel();
        weatherVideoKeepNumberIconLabel = new javax.swing.JLabel();
        weatherStationKeepNumberIconLabel1 = new javax.swing.JLabel();

        setTitle("Settings");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cameraComboBox.setRequestFocusEnabled(false);
        getContentPane().add(cameraComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 40, 171, 22));

        mapComboBox.setRequestFocusEnabled(false);
        getContentPane().add(mapComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 74, 171, 22));

        defaultCameraLabel.setText("Start-Up Weather Camera:");
        getContentPane().add(defaultCameraLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 40, -1, -1));

        defaultMapLabel.setText("Start-Up Weather Map:");
        getContentPane().add(defaultMapLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 74, -1, -1));

        weatherStationComboBox.setRequestFocusEnabled(false);
        getContentPane().add(weatherStationComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 108, 171, 22));

        defaultWeatherStationLabel.setText("Start-Up Weather Station:");
        getContentPane().add(defaultWeatherStationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 108, -1, -1));

        hoursLabel.setText("Default Video Time Span (Hours):");
        getContentPane().add(hoursLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 142, -1, -1));
        getContentPane().add(hoursTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 142, 171, 22));

        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 397, 73, 25));

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        getContentPane().add(resetButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 397, 73, 25));

        topDescriptionLabel.setText("Please change your local defaults below:");
        getContentPane().add(topDescriptionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 12, 233, 16));

        cameraIconLabel.setIcon(IconProperties.getDefaultCameraIcon());
        getContentPane().add(cameraIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 17, 20));

        defaultMapIconLabel.setIcon(IconProperties.getDefaultMapIcon());
        getContentPane().add(defaultMapIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 74, 17, 20));

        defaultWeatherStationIconLabel.setIcon(IconProperties.getDefaultWeatherStationIcon());
        getContentPane().add(defaultWeatherStationIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 108, 17, 20));

        defaultHoursToLoadIconLabel.setIcon(IconProperties.getDefaultHoursToLoadIcon());
        getContentPane().add(defaultHoursToLoadIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 142, 17, 20));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(314, 397, 73, 25));

        calendarIconLabel.setIcon(IconProperties.getCalendarIcon());
        getContentPane().add(calendarIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 176, 17, 20));

        defaultCalendarLabel.setText("Default Data Plot Search Span (Days):");
        getContentPane().add(defaultCalendarLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 176, -1, -1));

        calendarComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3", "7", "35" }));
        calendarComboBox.setRequestFocusEnabled(false);
        getContentPane().add(calendarComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 176, 171, 22));

        initialPlotDataTraceLabel.setText("Initial Weather Station Plot Variable:");
        getContentPane().add(initialPlotDataTraceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 210, -1, -1));

        getContentPane().add(initialPlotDataTraceComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 210, 171, 22));

        maxMoviesLabel.setText("<html>Max Number of Videos to Keep for<br/>each Type of Video:</html>");
        maxMoviesLabel.setToolTipText("");
        getContentPane().add(maxMoviesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 278, -1, -1));
        maxMoviesLabel.getAccessibleContext().setAccessibleName("<html>Max Number of Videos to Keep for<br/>each Type of Video:</html>");

        getContentPane().add(maxMoviesTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 278, 171, 22));

        deleteMoviesButton.setText("Delete All Stored Videos");
        deleteMoviesButton.setToolTipText("");
        deleteMoviesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMoviesButtonActionPerformed(evt);
            }
        });
        getContentPane().add(deleteMoviesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 316, -1, -1));

        numberDaysWeatherStationLabel.setText("<html>Max Number of Days to Keep for each<br/>Weather Station:</html>");
        getContentPane().add(numberDaysWeatherStationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 353, -1, -1));
        getContentPane().add(numberOfDaysWeatherStationTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 353, 171, 22));

        sharedDataDescriptionLabel.setText("Edit the shared data settings below:");
        getContentPane().add(sharedDataDescriptionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(118, 244, 206, 16));

        weatherStationVariableIconLabel.setIcon(IconProperties.getDefaultWeatherStationIcon());
        weatherStationVariableIconLabel.setToolTipText("");
        getContentPane().add(weatherStationVariableIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 210, 17, 20));

        weatherStationKeepNumberIconLabel.setIcon(IconProperties.getDefaultWeatherStationIcon());
        weatherStationKeepNumberIconLabel.setToolTipText("");
        getContentPane().add(weatherStationKeepNumberIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 353, 17, 20));

        weatherVideoKeepNumberIconLabel.setIcon(IconProperties.getDefaultCameraIcon());
        weatherVideoKeepNumberIconLabel.setToolTipText("");
        getContentPane().add(weatherVideoKeepNumberIconLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 278, 17, 20));

        weatherStationKeepNumberIconLabel1.setIcon(IconProperties.getDefaultWeatherStationIcon());
        weatherStationKeepNumberIconLabel1.setToolTipText("");
        getContentPane().add(weatherStationKeepNumberIconLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 353, 17, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        //Check input
        try {
            int input;
            input = Integer.parseInt(hoursTextField.getText());
            if (input < 1) {
                throw new NumberFormatException();
            }
            input = Integer.parseInt(maxMoviesTextField.getText());
            if (input < 1) {
                throw new NumberFormatException();
            }
            input = Integer.parseInt(numberOfDaysWeatherStationTextField.getText());
            if (input < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "The text fields must be positive numbers.",
                    "Please Enter Positive Numbers", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //Store data
        int dialogResult = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to update the default settings?", 
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION) {
            saveNewDefaultProperties();
            JOptionPane.showMessageDialog(this, "Your default settings have been changed!", 
                    "Update", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to reset the default settings?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION) {
            resetButtonPerformed();
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void deleteMoviesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMoviesButtonActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the stored videos?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            File cameraDir = new File(CommonLocalFileManager.getAVICameraDir());
            File maploopDir = new File(CommonLocalFileManager.getAVIMaploopDir());
            String[] movies = cameraDir.list();
            String[] maploops = maploopDir.list();
            for (String s : movies) {
                File file = new File(cameraDir.getPath(), s);
                file.delete();
            }
            for (String s : maploops) {
                File file = new File(maploopDir.getPath(), s);
                file.delete();
            }

            JOptionPane.showMessageDialog(this, 
                    "The stored movies have been deleted. You may have to restart the application.", "Videos Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteMoviesButtonActionPerformed

    /**
     * This method will set the default property values in the LocalDefault
     * property file once this button is pressed.
     *
     * @param evt the button has been pressed
     */
    private void saveNewDefaultProperties() {
        PropertyManager.setLocalProperty("DEFAULT_WEATHER_CAMERA", cameraComboBox.getSelectedItem().toString().trim());
        PropertyManager.setLocalProperty("DEFAULT_WEATHER_MAP_LOOP", mapComboBox.getSelectedItem().toString().trim());
        PropertyManager.setLocalProperty("DEFAULT_WEATHER_STATION", weatherStationComboBox.getSelectedItem().toString().trim());
        PropertyManager.setLocalProperty("DEFAULT_START_HOURS", hoursTextField.getText().trim());
        PropertyManager.setLocalProperty("DEFAULT_WEATHER_STATION_SPAN", calendarComboBox.getSelectedItem().toString().trim());
        Vector<String> ordering = wvProps.getOrdering();
        int numberOfVariables = ordering.size();
        String initialPlotName = "";
        for (int i = 0; i < numberOfVariables; i++) {
            String variableKey = ordering.get(i);
            if (wvProps.getDisplayName(variableKey).equals(initialPlotDataTraceComboBox.getSelectedItem().toString())) {
                initialPlotName = variableKey;
                break;
            }
        }
        PropertyManager.setLocalProperty("INITIAL_PLOT_DATA_TRACE", initialPlotName);
        PropertyManager.setGeneralProperty("MAX_LOCAL_AVI_TO_KEEP", maxMoviesTextField.getText());
        PropertyManager.setGeneralProperty("MAX_LOCAL_WEATHER_STATION_DAYS_TO_KEEP", numberOfDaysWeatherStationTextField.getText());
    }

    /**
     * This method will reset the combo boxes and text field to their default
     * values. The default values will be the first item in the combo box.
     *
     * @param evt the combo box has been pressed
     */
    private void resetButtonPerformed() {
        getDefaultSettings();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox calendarComboBox;
    private javax.swing.JLabel calendarIconLabel;
    private javax.swing.JComboBox cameraComboBox;
    private javax.swing.JLabel cameraIconLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel defaultCalendarLabel;
    private javax.swing.JLabel defaultCameraLabel;
    private javax.swing.JLabel defaultHoursToLoadIconLabel;
    private javax.swing.JLabel defaultMapIconLabel;
    private javax.swing.JLabel defaultMapLabel;
    private javax.swing.JLabel defaultWeatherStationIconLabel;
    private javax.swing.JLabel defaultWeatherStationLabel;
    private javax.swing.JButton deleteMoviesButton;
    private javax.swing.JLabel hoursLabel;
    private javax.swing.JTextField hoursTextField;
    private javax.swing.JComboBox initialPlotDataTraceComboBox;
    private javax.swing.JLabel initialPlotDataTraceLabel;
    private javax.swing.JComboBox mapComboBox;
    private javax.swing.JLabel maxMoviesLabel;
    private javax.swing.JTextField maxMoviesTextField;
    private javax.swing.JLabel numberDaysWeatherStationLabel;
    private javax.swing.JTextField numberOfDaysWeatherStationTextField;
    private javax.swing.JButton resetButton;
    private javax.swing.JLabel sharedDataDescriptionLabel;
    private javax.swing.JLabel topDescriptionLabel;
    private javax.swing.JButton updateButton;
    private javax.swing.JComboBox weatherStationComboBox;
    private javax.swing.JLabel weatherStationKeepNumberIconLabel;
    private javax.swing.JLabel weatherStationKeepNumberIconLabel1;
    private javax.swing.JLabel weatherStationVariableIconLabel;
    private javax.swing.JLabel weatherVideoKeepNumberIconLabel;
    // End of variables declaration//GEN-END:variables
}
