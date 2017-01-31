package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import weather.AdministratorControlSystem;
import weather.GeneralService;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>WeatherResourceManagerWindow</code> class creates a form that
 * lists all weather resources (cameras, stations, etc.) in the database.
 * It also allows the user to add/edit/delete the resources.
 * This is a modified version of WeatherResourceListWindow
 * @author Bloomsburg University Software Engineering
 * @author Mike Graboske (2008)
 * @author Ora Merkel (2009)
 * @author Ty Vanderstappen (2012)
 * @author Bingchen Yan (2012)
 * @version Spring 2012
 */
public final class WeatherResourceManagerWindow extends BUDialog {

    private final GeneralService generalService;
    private final AdministratorControlSystem adminControl;
    private int numberOfRowsInTable = 0;    // TODO redundant.
    private MyDefaultTableModel tableModel;
    private final int numberOfColumnsInTable = 7;
    private java.util.Vector<Resource> weatherResourceList;
    
    /** 
     * Creates new form WeatherResourceListWindow.
     * @param parent The frame that called this method.
     * @param adminControl The administrator control object.
     */
    public WeatherResourceManagerWindow(java.awt.Frame parent, 
        final AdministratorControlSystem adminControl) {
        super();
        this.generalService = adminControl.getGeneralService();
        this.adminControl = adminControl;
        weatherResourceList = generalService.getDBMSSystem().
                getResourceManager().getResourceList();
        setNumberOfRows();
        tableModel = new MyDefaultTableModel(numberOfRowsInTable, 
                numberOfColumnsInTable);
        parent.setTitle("Weather Viewer - Manage Resources");
        initComponents();
        Dimension thisDim = new Dimension(1000, 680);
        setSize(thisDim);
        setPreferredSize(thisDim);
        setMaximumSize(thisDim);
        setMinimumSize(thisDim);
        pack();
        initResourceTable();
        
        MouseAdapter doubleClick = new MouseAdapter() {

            /**
             * This method is called when the user double-clicks on a row in the
             * table. The user is then allowed to edit the resource.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    try {
                        Resource selected = findResource(Integer.parseInt(
                                target.getValueAt(target.getSelectedRow(), 2).
                                toString().trim()));
                        adminControl.editResourceSettingsService(selected, 
                                selected.getResourceType());
                        refreshDisplay();
                    } catch (WeatherException ex) {
                        WeatherLogger.log(Level.SEVERE, "Could not set the "
                                + "resource settings back to NULL in "
                                + "onEditResouces WeatherResourceManagerWindow"
                                + " and the program needs to close.", ex);
                        ex.show();
                    }
                }
            }
            //Resource number is column #3, value 2
            public Resource findResource(int resnum) {
                for(Resource r : weatherResourceList) {
                    if(r.getResourceNumber() == resnum)
                        return r;
                }
                return null;
            }
        };
        resourceTable.addMouseListener(doubleClick);
        refreshDisplay();
        pack();
        super.postInitialize(true);
    }

    /**
     *  Sets the number of numberOfRowsInTable in JTable.
     *  The number of numberOfRowsInTable is determined by how many resources we have 
     *  in our DBMS.
     */
    private void setNumberOfRows()
    {
        numberOfRowsInTable = generalService.getDBMSSystem().
                getResourceManager().getResourceList().size();
    }

    /**
     * Returns the number of numberOfRowsInTable in the JTable. This 
     * information is determined how many resources are in our system.
     * 
     * @return The number of resources  in the system.
     */
    private int getRows()
    {
        return numberOfRowsInTable;
    }
    
    /**
     * Sets the window visible and rebuilds the window.
     * 
     * @param weatherResourceList A list of weather resources to use to 
     *        refresh the window with.
     */
      public void show(java.util.Vector<weather.common.data.resource.Resource> weatherResourceList) {
          this.weatherResourceList = weatherResourceList; // get from database
        refreshDisplay();
        setVisible(true);
    }

    /**
     * Initialize settings of the resource table.
     */
    private void initResourceTable () {
        // Enable alphabetical sorting of the column headers.
        resourceTable.setAutoCreateRowSorter(true);
        // Set column headers.
        resourceTable.getColumnModel().getColumn(0).setHeaderValue("Resource Name");
        resourceTable.getColumnModel().getColumn(1).setHeaderValue("Active");
        resourceTable.getColumnModel().getColumn(2).setHeaderValue("Res #");
        resourceTable.getColumnModel().getColumn(3).setHeaderValue("Date Initalized");
        resourceTable.getColumnModel().getColumn(4).setHeaderValue("Type");
        resourceTable.getColumnModel().getColumn(5).setHeaderValue("Storage Folder");
        resourceTable.getColumnModel().getColumn(6).setHeaderValue("URL");
        this.setTitle("Weather Viewer - Resource Manager");     
        // Set cell renderers.
        DefaultTableCellRenderer tcrCenter = new DefaultTableCellRenderer();
        tcrCenter.setHorizontalAlignment(SwingConstants.CENTER);
        resourceTable.getColumnModel().getColumn(1).setCellRenderer(tcrCenter);
        resourceTable.getColumnModel().getColumn(2).setCellRenderer(tcrCenter);
        resourceTable.getColumnModel().getColumn(3).setCellRenderer(tcrCenter);
    }
    
    /**
     * Rebuilds the resource list window.
     */
    public void refreshDisplay() {
        weatherResourceList.removeAllElements();
        weatherResourceList = generalService.getDBMSSystem().
                getResourceManager().getResourceList();
        if (numberOfRowsInTable < weatherResourceList.size()) {
            while (numberOfRowsInTable < weatherResourceList.size()) {
                tableModel.addRow(new Object[]{
                            null, null, null, null, null, null, null
                        });
                numberOfRowsInTable++;
            }
        } else if (numberOfRowsInTable > weatherResourceList.size()) {
            tableModel.removeRow(numberOfRowsInTable - 1);
            numberOfRowsInTable = weatherResourceList.size();
            resourceTable.updateUI();

        }
        for (int i = 0; i < weatherResourceList.size(); i++) {
            for (int j = 0; j < numberOfColumnsInTable; j++) {
                if (j == 0) {
                    resourceTable.setValueAt(" " + weatherResourceList.get(i).getResourceName(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(200);
                } else if (j == 1) {
                    //resourceTable.getColumnModel().getColumn(j).setCellRenderer(new BooleanCellRenderer());
                    if (weatherResourceList.get(i).isActive()) {
                        resourceTable.setValueAt("Yes", i, j);
                    } else {
                        resourceTable.setValueAt("No", i, j);
                    }
                    //resourceTable.setValueAt(weatherResourceList.get(i).isActive(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(40);
                } else if (j == 2) {
                    resourceTable.setValueAt(" " + weatherResourceList.get(i).getResourceNumber(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(50);
                } else if (j == 3) {
                    resourceTable.setValueAt(" " + weatherResourceList.get(i).getDateInitiated(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(95);
                } else if (j == 4) {
                    resourceTable.setValueAt(" " + weatherResourceList.get(i).getResourceType(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(130);
                } else if (j == 5) {
                    resourceTable.setValueAt(" " + weatherResourceList.get(i).getStorageFolderName(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(125);
                } else {
                    resourceTable.setValueAt(" " + weatherResourceList.get(i).getURL(), i, j);
                    resourceTable.getColumnModel().getColumn(j).setPreferredWidth(500);
                }
            }
        }
    }

    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlContainerPanel = new javax.swing.JPanel();
        resourceListScrollPanel = new javax.swing.JScrollPane();
        resourceTable = new javax.swing.JTable(getRows(), 6);
        doubleClickLabel = new javax.swing.JLabel();
        displayOrderButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        addCameraButton = new javax.swing.JButton();
        addStationButton = new javax.swing.JButton();
        addMapButton = new javax.swing.JButton();
        deleteResourceButton = new javax.swing.JButton();
        editCamButton = new javax.swing.JButton();
        editStatButton = new javax.swing.JButton();
        editMapButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setTitle("Resource List");
        setLocationByPlatform(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        controlContainerPanel.setPreferredSize(new java.awt.Dimension(1200, 300));
        controlContainerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resourceListScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        resourceListScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        resourceListScrollPanel.setPreferredSize(new java.awt.Dimension(300, 402));

        resourceTable.setModel(tableModel);
        resourceTable.setToolTipText("");
        resourceTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        resourceTable.setAutoscrolls(false);
        resourceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resourceTable.setShowVerticalLines(false);
        resourceTable.getTableHeader().setReorderingAllowed(false);
        resourceListScrollPanel.setViewportView(resourceTable);

        controlContainerPanel.add(resourceListScrollPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 976, 561));

        doubleClickLabel.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        doubleClickLabel.setText("To edit a resource, double-click on it.");
        controlContainerPanel.add(doubleClickLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 622, -1, -1));

        displayOrderButton.setText("Change Display Order");
        displayOrderButton.setToolTipText("Change the order of the displayed items");
        displayOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayButtonClicked(evt);
            }
        });
        controlContainerPanel.add(displayOrderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(251, 615, -1, -1));

        buttonPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        addCameraButton.setText("Add Camera");
        addCameraButton.setToolTipText("Adds a new camera resource");
        addCameraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addResource(evt);
            }
        });
        buttonPanel.add(addCameraButton);

        addStationButton.setText("Add Station");
        addStationButton.setToolTipText("Adds a new station resource");
        addStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addResource(evt);
            }
        });
        buttonPanel.add(addStationButton);

        addMapButton.setText("Add Map");
        addMapButton.setToolTipText("Adds a new map resource");
        addMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addResource(evt);
            }
        });
        buttonPanel.add(addMapButton);

        deleteResourceButton.setText("Delete");
        deleteResourceButton.setToolTipText("Currently must edit the resource to delete it");
        deleteResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteResourceButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(deleteResourceButton);

        editCamButton.setText("Edit Cameras");
        editCamButton.setToolTipText("Edit camera resources");
        editCamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCamButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(editCamButton);

        editStatButton.setText("Edit Stations");
        editStatButton.setToolTipText("Edit station resources");
        editStatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editStatButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(editStatButton);

        editMapButton.setText("Edit Maps");
        editMapButton.setToolTipText("Edit map resources");
        editMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMapButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(editMapButton);

        closeButton.setText("Close");
        closeButton.setToolTipText("Closes this window");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        controlContainerPanel.add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 585, 976, 25));

        getContentPane().add(controlContainerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 680));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This is called when the Delete button is clicked.
     * TODO: Finish this method
     * @param evt 
     */
    private void deleteResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteResourceButtonActionPerformed
        int row = resourceTable.getSelectedRow();
        
        if(row < 0){
            JOptionPane.showMessageDialog(this, "Please select a resource to delete.",
                    "No Resource Selected", JOptionPane.OK_OPTION);
            return;
        }
        Resource r = weatherResourceList.get(row);
        String resourceName = r.getResourceName();
        
        if(JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + resourceName + "?", 
                "Delete Resource", JOptionPane.YES_NO_OPTION) == 
                JOptionPane.YES_OPTION) {
            if (generalService.getDBMSSystem().getResourceManager()
                    .removeResource(r)) {
                weatherResourceList.remove(r);
                refreshDisplay();
                JOptionPane.showMessageDialog(this, 
                        "This resource has been deleted.", "Delete Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "This resource has NOT been deleted.", 
                        "Delete Not Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteResourceButtonActionPerformed

    /**
     * This closes the WeatherResourceManagerWindow.
     * @param evt The event that the Close button is clicked.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void displayButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayButtonClicked
        try{
            adminControl.editWeatherResourceSettingsService(
                    WeatherResourceType.undefined);
            refreshDisplay();
        }catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not set the resource settings back to NULL in displayButtonClicked WeatherResourceManagerWindow and the program needs to close.",ex);
            ex.show();
        }
    }//GEN-LAST:event_displayButtonClicked

    private void editCamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCamButtonActionPerformed
        try{
            adminControl.editWeatherResourceSettingsService(
                    WeatherResourceType.WeatherCamera);
            refreshDisplay();
        }catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not set the resource settings back to NULL in displayButtonClicked WeatherResourceManagerWindow and the program needs to close.",ex);
            ex.show();
        }
    }//GEN-LAST:event_editCamButtonActionPerformed

    private void editStatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editStatButtonActionPerformed
        try{
            adminControl.editWeatherResourceSettingsService(
                    WeatherResourceType.WeatherStationValues);
            refreshDisplay();
        }catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "",ex);
            ex.show();
        }
    }//GEN-LAST:event_editStatButtonActionPerformed

    private void editMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMapButtonActionPerformed
        try{
            adminControl.editWeatherResourceSettingsService(
                    WeatherResourceType.WeatherMapLoop);
            refreshDisplay();
        }catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "",ex);
            ex.show();
        }
    }//GEN-LAST:event_editMapButtonActionPerformed

    /**
     * This method is called when any of the the Add buttons are clicked.
     * This opens a new window that allows the user to add a resource.
     * @param evt The event that a button is clicked.
     */
    private void addResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResource
        WeatherResourceType resourceType;
        if(evt.getSource().equals(this.addCameraButton)) {
            resourceType = WeatherResourceType.WeatherCamera;
        } else if(evt.getSource().equals(this.addStationButton)) {
            resourceType = WeatherResourceType.WeatherStationValues;
        } else {
            resourceType = WeatherResourceType.WeatherMapLoop;
        }
        try {
            adminControl.addResourceService(resourceType);
            refreshDisplay();
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not set the resource settings back to NULL"
                    + "in the method addEditResources.  WeatherResourceManagerWindow and the program needs to close.", ex);
        }
    }//GEN-LAST:event_addResource

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCameraButton;
    private javax.swing.JButton addMapButton;
    private javax.swing.JButton addStationButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel controlContainerPanel;
    private javax.swing.JButton deleteResourceButton;
    private javax.swing.JButton displayOrderButton;
    private javax.swing.JLabel doubleClickLabel;
    private javax.swing.JButton editCamButton;
    private javax.swing.JButton editMapButton;
    private javax.swing.JButton editStatButton;
    private javax.swing.JScrollPane resourceListScrollPanel;
    private javax.swing.JTable resourceTable;
    // End of variables declaration//GEN-END:variables
}
