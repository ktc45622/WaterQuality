package weather.clientside.gui.administrator;

import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import weather.GeneralService;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.gui.component.IconProperties;

/**
 * Panel to edit the display order of resources.  
 * 
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class ResourceDisplayOrderPanel extends javax.swing.JPanel{

    private GeneralService generalService;
    private final int ACTIVE_CAMERA_INDEX = 0;
    private final int ACTIVE_STATION_INDEX = 1;
    private final int ACTIVE_MAP_INDEX = 2;
    private final int INACTIVE_CAMERA_INDEX = 3;
    private final int INACTIVE_STATION_INDEX = 4;
    private final int INACTIVE_MAP_INDEX = 5;

    public ResourceDisplayOrderPanel(){}
    
    
    /**
     * Creates a new <code>ResourceDisplayOrderPanel</code> and loads the 
     * list of active cameras by default.
     * @param generalService The <code>GeneralService</code> object used by the
     * client.
     */
    public ResourceDisplayOrderPanel(GeneralService generalService) {
        this.generalService = generalService;
        initComponents();
        errorLabel.setVisible(false);
        updateResourceTable();
    }

    /**
     * Returns a list of resources of the given type and activity type.
     * @param type The type of resource to add to the list.
     * @param active Whether to load active or inactive resources.
     * @return A <code>Vector</code> of <code>Resource</code>s that match the 
     * given parameters.
     */
    private Vector<Resource> getResources(WeatherResourceType type, boolean active) {
        Vector<Resource> resources = new Vector<Resource>();
        for (Resource r :
                generalService.getDBMSSystem().getResourceManager().getResourceList()) {
            if (r.getResourceType() == type && r.isActive() == active) {
                resources.add(r);
            }
        }
        return resources;
    }

    /**
     * Uses the selected item to get the desired list of resources.
     * @param selectedIndex The index selected from the combo box.
     * @return A <code>Vector</code> containing <code>Resource</code>s matching
     * the desired parameters.  Returns an empty <code>Vector</code> if there is
     * an error.
     */
    private Vector<Resource> getSelectedResources(int selectedIndex) {
        Vector<Resource> resources;
        switch (resourceTypeBox.getSelectedIndex()) {
            case ACTIVE_CAMERA_INDEX:
                resources = getResources(WeatherResourceType.WeatherCamera, true);
                break;
            case ACTIVE_STATION_INDEX:
                resources = getResources(WeatherResourceType.WeatherStationValues, true);
                break;
            case ACTIVE_MAP_INDEX:
                resources = getResources(WeatherResourceType.WeatherMapLoop, true);
                break;
            case INACTIVE_CAMERA_INDEX:
                resources = getResources(WeatherResourceType.WeatherCamera, false);
                break;
            case INACTIVE_STATION_INDEX:
                resources = getResources(WeatherResourceType.WeatherStationValues, false);
                break;
            case INACTIVE_MAP_INDEX:
                resources = getResources(WeatherResourceType.WeatherMapLoop, false);
                break;
            default:
                resources = new Vector<Resource>();
                break;
        }
        return resources;
    }

    /**
     * Updates the table to display the selected type of <code>Resource</code>
     */
    public void updateResourceTable() {
        int row = 0;
        Vector<Resource> resources = getSelectedResources(
                resourceTypeBox.getSelectedIndex());

        this.resourceTable.setModel(new MyDefaultTableModel(resources.size(), 1));
        resourceTable.getColumnModel().getColumn(0).setHeaderValue("Resource Name");
        resourceTable.getColumnModel().getColumn(0).setMinWidth(250);
        for (Resource r : resources) {
            resourceTable.setValueAt(r.getResourceName(), row, 0);
            row++;
        }
    }

    /**
     * Swaps the rank order of the <code>Resource</code> <code>positionShift</code>
     * spaces from <code>selectedIndex</code>.
     * @param selectedIndex The index of the selected item in the combo box.
     * @param positionShift The number of spaces away the resource to swap is.
     */
    private void updateResourceOrder(int selectedIndex, int positionShift) {
        Vector<Resource> resources = getSelectedResources(
                resourceTypeBox.getSelectedIndex());
        
        errorLabel.setVisible(false);
        Resource r1 = resources.get(selectedIndex);
        Resource r2 = resources.get(selectedIndex+positionShift);
        
        int old_Rank = r1.getOrderRank();
        r1.setOrderRank(r2.getOrderRank());
        r2.setOrderRank(old_Rank);
        
        //Try to save update.
        try {
            r1 = generalService.updateWeatherResource(r1);
            r2 = generalService.updateWeatherResource(r2);
            if (r1.getResourceNumber() != -1 && r2.getResourceNumber() != -1) {
                //Successful save.
                this.updateResourceTable();
                resourceTable.setRowSelectionInterval(selectedIndex 
                        + positionShift, selectedIndex + positionShift);
            } else {
                //All unsuccessful saves handled in catch.
                throw new Exception();  
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, 
                     "This update was NOT successful.", "Update Not Successful", 
                     JOptionPane.INFORMATION_MESSAGE);
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

        resourceTypePanel = new javax.swing.JPanel();
        resourceTypeLabel = new javax.swing.JLabel();
        resourceTypeBox = new javax.swing.JComboBox();
        resourceScrollPane = new javax.swing.JScrollPane();
        resourceTable = new JTable();
        buttonPanel = new javax.swing.JPanel();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();

        resourceTypeLabel.setText("Resource Type:");

        resourceTypeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Active Cameras", "Active Weather Stations", "Active Weather Map Loops ", "Inactive Cameras", "Inactive Weather Stations", "Inactive Weather Map Loops" }));
        resourceTypeBox.setToolTipText("");
        resourceTypeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourceTypeBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resourceTypePanelLayout = new javax.swing.GroupLayout(resourceTypePanel);
        resourceTypePanel.setLayout(resourceTypePanelLayout);
        resourceTypePanelLayout.setHorizontalGroup(
            resourceTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resourceTypePanelLayout.createSequentialGroup()
                .addComponent(resourceTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resourceTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 174, Short.MAX_VALUE))
        );
        resourceTypePanelLayout.setVerticalGroup(
            resourceTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resourceTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(resourceTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(resourceTypeLabel))
        );

        resourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        resourceScrollPane.setViewportView(resourceTable);

        upButton.setIcon(IconProperties.getArrowUpSmallIcon());
        upButton.setToolTipText("Move the selected resource up one index");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setIcon(IconProperties.getArrowDownSmallIcon());
        downButton.setToolTipText("Move the current resource down one index");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(downButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(upButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 5, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton)
                .addGap(20, 20, 20))
        );

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resourceTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(resourceScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(errorLabel))
                        .addGap(0, 12, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resourceTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resourceScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Swaps the selected item's order rank with the item above it.
     * @param evt The event that triggers the action.
     */
    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed

        int i = this.resourceTable.getSelectedRow();
        if (i == -1) {
            errorLabel.setText("A resource was not selected");
            errorLabel.setVisible(true);
        } else if (i != 0) {
            updateResourceOrder(i,-1);
        }
    }//GEN-LAST:event_upButtonActionPerformed

    /**
     * Swaps the selected item's order rank with the item below it.
     * @param evt The event that triggers the action. 
     */
    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        Vector<Resource> resources = getSelectedResources(
                resourceTypeBox.getSelectedIndex());
        int i = this.resourceTable.getSelectedRow();

        if (i == -1) {
            errorLabel.setText("A resource was not selected");
            errorLabel.setVisible(true);
        } else if (i + 1 < this.resourceTable.getRowCount()) {
            updateResourceOrder(i,1);
        }
    }//GEN-LAST:event_downButtonActionPerformed

    /**
     * Updates the table based on the selected item in the combo box.
     * @param evt The event that triggers the action.
     */
    private void resourceTypeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourceTypeBoxActionPerformed
        updateResourceTable();
    }//GEN-LAST:event_resourceTypeBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton downButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JScrollPane resourceScrollPane;
    private javax.swing.JTable resourceTable;
    private javax.swing.JComboBox resourceTypeBox;
    private javax.swing.JLabel resourceTypeLabel;
    private javax.swing.JPanel resourceTypePanel;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
}
