package weather.clientside.gui.administrator;

import java.awt.Dimension;
import weather.GeneralService;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.gui.component.BUDialog;

/**
 * A window that displays tabs for editing and adding resources.  The three main
 * resource types have tabs to allow resources of that type to be edited.  
 * There is also a display order tab to change the ordering of resources.
 * 
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class ResourceSettingsWindow extends BUDialog {

    private ResourceDisplayOrderPanel displayOrderPanel;
    private ResourceSettingsPanel cameraSettingsPanel;
    private ResourceSettingsPanel stationSettingsPanel;
    private ResourceSettingsPanel mapSettingsPanel;
    
    /**
     * This is the index of the selected index of the tabbed pane.  It must be
     * maintained as a separate variable or order to test for unsaved data when 
     * changing tabs.  No tab number is saved until the constructor runs.
     */ 
    private int selectedTabIndex = -1;
    
    /**
     * This flag in used to prevent the change listener from executing twice on
     * the same user action. It must be set to false before the change listener
     * changes a tab to keep the listener from fully running a second time.
     */
    private boolean checkDataOnTabChange = true;
    
    /**
     * Creates a new
     * <code>ResourceSettingsWindow</code> and displays the values for the given
     * <code>Resource</code>.
     *
     * @param generalService The <code>GeneralService</code> object used by the
     * client.
     * @param resource The <code>Resource</code> to load. If the 
     * <code>WeatherResourceType</code> of the <code>Resource</code> is 
     * undefined, the tab to change the display order of all resources will be
     * selected at first.
     * @param update True if form should update combos and show their first 
     * resources, false if not.
     */
    public ResourceSettingsWindow(GeneralService generalService, 
            Resource resource, boolean update) {
        super();
        int width = 468 + this.getInsets().left + this.getInsets().right;
        int height = 590 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();

        initComponents();
        
        //Initialize Panels
        cameraSettingsPanel = new ResourceSettingsPanel(this, 
                WeatherResourceType.WeatherCamera, generalService);
        stationSettingsPanel = new ResourceSettingsPanel(this, 
                WeatherResourceType.WeatherStationValues, generalService);
        mapSettingsPanel = new ResourceSettingsPanel(this, 
                WeatherResourceType.WeatherMapLoop, generalService);
        displayOrderPanel = new ResourceDisplayOrderPanel(generalService);
        
        //Add Tabs
        settingsTabPane.addTab("Camera Settings", cameraSettingsPanel);
        settingsTabPane.addTab("Station Settings", stationSettingsPanel);
        settingsTabPane.addTab("Map Settings", mapSettingsPanel);
        settingsTabPane.addTab("Display Order", displayOrderPanel);
        
        //Add Tab listener
        settingsTabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                //Don't do anything in constructor or when one firing causes a
                //second one.
                if(selectedTabIndex == -1  || !checkDataOnTabChange) {
                    return;
                }
                
                //Check for unsaved data and reset if the user wants to do so.
                if(selectedTabIndex == 0) {
                    if(!cameraSettingsPanel.isOKToLeavePanel()) {
                        checkDataOnTabChange = false;
                        settingsTabPane.setSelectedIndex(0);
                        checkDataOnTabChange = true;
                        return;
                    }
                }
                if(selectedTabIndex == 1) {
                    if(!stationSettingsPanel.isOKToLeavePanel()) {
                        checkDataOnTabChange = false;
                        settingsTabPane.setSelectedIndex(1);
                        checkDataOnTabChange = true;
                        return;
                    }
                }
                if(selectedTabIndex == 2) {
                    if(!mapSettingsPanel.isOKToLeavePanel()) {
                        checkDataOnTabChange = false;
                        settingsTabPane.setSelectedIndex(2);
                        checkDataOnTabChange = true;
                        return;
                    }
                }
                
                //Update data.
                stationSettingsPanel.updateComboBox();
                mapSettingsPanel.updateComboBox();
                cameraSettingsPanel.updateComboBox();
                displayOrderPanel.updateResourceTable();
                selectedTabIndex = settingsTabPane.getSelectedIndex();
            }
        });

        //Make empty resoure show "Add new resource."
        if (resource.getResourceNumber() == -1) {
            resource.setResourceName(ResourceSettingsPanel.NEW_RESOURCE_ENTRY);
        }

        switch (resource.getResourceType()) {
            case WeatherCamera:
                settingsTabPane.setSelectedComponent(cameraSettingsPanel);
                cameraSettingsPanel.setResource(resource);
                cameraSettingsPanel.setToEdit();
                break;
            case WeatherMapLoop:
                settingsTabPane.setSelectedComponent(mapSettingsPanel);
                mapSettingsPanel.setResource(resource);
                mapSettingsPanel.setToEdit();
                break;
            case WeatherStationValues:
                settingsTabPane.setSelectedComponent(stationSettingsPanel);
                stationSettingsPanel.setResource(resource);
                stationSettingsPanel.setToEdit();
                break;
            case undefined:
                settingsTabPane.setSelectedComponent(displayOrderPanel);
                break;
        }
        selectedTabIndex = settingsTabPane.getSelectedIndex();

        //Attach listeners now, so they don't fire sooner
        cameraSettingsPanel.attachListener();
        stationSettingsPanel.attachListener();
        mapSettingsPanel.attachListener();
        
        //Update comboboxes if asked
        if (update) {
            stationSettingsPanel.updateComboBox();
            mapSettingsPanel.updateComboBox();
            cameraSettingsPanel.updateComboBox();
        }

        super.postInitialize(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsTabPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Edit Resources");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(settingsTabPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 444, 536));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int index = settingsTabPane.getSelectedIndex();
        switch (index) {
            case 0: // Camera panel
                cameraSettingsPanel.checkForParentClose();
                break;
            case 1: // Station panel
                stationSettingsPanel.checkForParentClose();
                break;
            case 2: // Map panel
                mapSettingsPanel.checkForParentClose();
                break;
            case 3: // Order panel
                dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane settingsTabPane;
    // End of variables declaration//GEN-END:variables
}
