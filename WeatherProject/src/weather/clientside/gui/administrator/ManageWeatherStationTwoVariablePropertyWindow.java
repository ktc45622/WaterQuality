package weather.clientside.gui.administrator;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.Property;
import weather.common.dbms.DBMSPropertyManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>ManageWeatherStationTwoVariablePropertyWindow</code> creates a form that 
 * lists all properties available.
 * Double click one property in the table can edit this property
 * Click add Property button can add one property into the database
 * Select one property that listed in the table and delete this property with clicking 
 * the delete button
 * @author Xiang Li(2014)
 */
public class ManageWeatherStationTwoVariablePropertyWindow extends BUDialog {
    
    private DBMSSystemManager dbms;
    private DBMSPropertyManager propertyMgr;
    private final ApplicationControlSystem finalClientControl;
    
    private File systemSettingsInfo;
    
    private final int NUMBER_OF_COLUMNS = 5;
    private final int PROPERTY_ID_COLUMN = 0;
    private final int PROPERTY_TYPE_COLUMN = 1;
    private final int PROPERTY_NAME_COLUMN = 2;
    private final int PROPERTY_VALUE_COLUMN = 3;
    private final int PROPERTY_NOTES_COLUMN = 4;

    private final int PROPERTY_ID_COLUMN_MIN_WIDTH = 0;
    private final int PROPERTY_ID_COLUMN_MAX_WIDTH = 0;
    private final int PROPERTY_TYPE_COLUMN_MIN_WIDTH = 0;
    private final int PROPERTY_TYPE_COLUMN_MAX_WIDTH = 0;
    private final int PROPERTY_NAME_COLUMN_MIN_WIDTH = 200;
    private final int PROPERTY_NAME_COLUMN_MAX_WIDTH = 200;
    private final int PROPERTY_VALUE_MIN_WIDTH = 200;
    private final int PROPERTY_NOTES_WIDTH = 300;

    public ManageWeatherStationTwoVariablePropertyWindow(ApplicationControlSystem appControl) {
        super(appControl);
        systemSettingsInfo = new File("Documents" + File.separator + "SystemSettingsInfo.doc");
        finalClientControl = appControl;
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        propertyMgr = dbms.getPropertyManager();
        initComponents();
        
        propertiesTable.setRowSelectionAllowed(true);
        propertiesTable.setAutoCreateRowSorter(true);
        propertiesTable.getTableHeader().setResizingAllowed(true);
        propertiesTable.getTableHeader().setReorderingAllowed(false);
        propertiesTable.addMouseListener(doubleClick);
        
        
        updateTable();
        super.postInitialize(true);
        propertiesScrollPane.setSize(new Dimension(propertiesTable.getWidth(), 350));
        
    }
    /**
     * Double click one row of the table then pop up an edit window for edit the 
     * corresponding property
     */
    MouseAdapter doubleClick = new MouseAdapter(){
        
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2)
                {   
                    JTable target = (JTable)e.getSource();
                    new EditPropertyWindow(appControl, "" + target.getValueAt(target.getSelectedRow(), 0));
                    updateTable();
                }
            }
            
            
    };
    
    /**
     * Gets all the gui properties from the database and list all of them in the table
     */
    private void updateTable(){
     
        Vector<Property> properties;
           
        properties = propertyMgr.obtainAllWeatherStationTwoVariableProperties();
        
        propertiesTable.setModel(new MyDefaultTableModel(properties.size(), NUMBER_OF_COLUMNS));
        
        propertiesTable.getColumnModel().getColumn(PROPERTY_ID_COLUMN).setHeaderValue("Property ID");
        propertiesTable.getColumnModel().getColumn(PROPERTY_ID_COLUMN).setMinWidth(PROPERTY_ID_COLUMN_MIN_WIDTH);
        propertiesTable.getColumnModel().getColumn(PROPERTY_ID_COLUMN).setMaxWidth(PROPERTY_ID_COLUMN_MAX_WIDTH);
        
        propertiesTable.getColumnModel().getColumn(PROPERTY_TYPE_COLUMN).setHeaderValue("Property Type");
        propertiesTable.getColumnModel().getColumn(PROPERTY_TYPE_COLUMN).setMinWidth(PROPERTY_TYPE_COLUMN_MIN_WIDTH);
        propertiesTable.getColumnModel().getColumn(PROPERTY_TYPE_COLUMN).setMaxWidth(PROPERTY_TYPE_COLUMN_MAX_WIDTH);
            
        propertiesTable.getColumnModel().getColumn(PROPERTY_NAME_COLUMN).setHeaderValue("Property Name");
        propertiesTable.getColumnModel().getColumn(PROPERTY_NAME_COLUMN).setMaxWidth(PROPERTY_NAME_COLUMN_MAX_WIDTH);
        propertiesTable.getColumnModel().getColumn(PROPERTY_NAME_COLUMN).setMinWidth(PROPERTY_NAME_COLUMN_MIN_WIDTH);
        
        propertiesTable.getColumnModel().getColumn(PROPERTY_VALUE_COLUMN).setHeaderValue("Property Value");
        propertiesTable.getColumnModel().getColumn(PROPERTY_VALUE_COLUMN).setMinWidth(PROPERTY_VALUE_MIN_WIDTH);
        
        propertiesTable.getColumnModel().getColumn(PROPERTY_NOTES_COLUMN).setHeaderValue("Notes");
        propertiesTable.getColumnModel().getColumn(PROPERTY_NOTES_COLUMN).setMinWidth(PROPERTY_NOTES_WIDTH);
          
        for (int i = 0; i < properties.size(); i++) 
        {
            propertiesTable.setValueAt(properties.get(i).getPropertyID(), i, PROPERTY_ID_COLUMN);
            propertiesTable.setValueAt(properties.get(i).getPropertyTypeDisplayName(), i, PROPERTY_TYPE_COLUMN);
            propertiesTable.setValueAt(properties.get(i).getPropertyDisplayName(), i, PROPERTY_NAME_COLUMN);
            propertiesTable.setValueAt(properties.get(i).getPropertyValue(), i, PROPERTY_VALUE_COLUMN);
            propertiesTable.setValueAt(properties.get(i).getNotes(), i, PROPERTY_NOTES_COLUMN);
        }
        
        propertiesTable.revalidate();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        propertiesScrollPane = new javax.swing.JScrollPane();
        propertiesTable = new javax.swing.JTable();
        addPropertyButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        editWarningLabel = new javax.swing.JLabel();
        deleteWarningLable = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();

        setTitle("Manage Weather Station Multi-Variable Properties");
        setResizable(false);

        propertiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Property ID", "Property Type", "Property Name", "Property Value", "Notes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        propertiesScrollPane.setViewportView(propertiesTable);

        addPropertyButton.setText("Add Property");
        addPropertyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPropertyButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete Property");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        editWarningLabel.setText("To edit a specific property, double-click on its name.");

        deleteWarningLable.setForeground(new java.awt.Color(255, 51, 51));
        deleteWarningLable.setText("Highlight a row, then click \"Delete Property\" to delete it.");

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(propertiesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addPropertyButton)
                        .addGap(26, 26, 26)
                        .addComponent(deleteButton)
                        .addGap(18, 18, 18)
                        .addComponent(helpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeButton)
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editWarningLabel)
                            .addComponent(deleteWarningLable))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(propertiesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addPropertyButton)
                    .addComponent(deleteButton)
                    .addComponent(closeButton)
                    .addComponent(helpButton))
                .addGap(18, 18, 18)
                .addComponent(editWarningLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteWarningLable)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Click Add property button, the addPropertyWindow shows up then 
     * administrators can use this window to insert one property into the database
     * @param evt 
     */
    private void addPropertyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPropertyButtonActionPerformed
        new AddPropertyWindow(appControl, "wunder_twovar");
        updateTable();
    }//GEN-LAST:event_addPropertyButtonActionPerformed
    /**
     * Select one property that listed in the table and delete this property with clicking 
     * the delete button
     * @param evt 
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int rows[] = propertiesTable.getSelectedRows();
        if(rows.length == 0)
        {
            deleteWarningLable.setVisible(true);
            return; 
        }
        deleteWarningLable.setVisible(false);
        
        for(Integer index : propertiesTable.getSelectedRows()) {
            //Course number is the first column, represented by 0
            //ClassID is the third column, represented by 2
            //Section is the fourth column
            //Course name is the fifth column
            int propertyID = Integer.parseInt(propertiesTable.getValueAt(index, 0).toString());

            Property p = propertyMgr.obtainProperty(propertyID);                        
            if(p.getIsEditable() == 0)
            {
                PropertyAuthenticatorWindow tempWindow = new PropertyAuthenticatorWindow(finalClientControl);
                if(tempWindow.getIsAdmin() == true)
                {
                    removeProperty();
                }else
                {
                    JOptionPane.showMessageDialog(this, "You have no right to delete this property", 
                        "Weather Viewer - Delete Property", JOptionPane.OK_OPTION);
                }
            }
            else
            {
                removeProperty();
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    /**
     * This method will open the system settings information file after the button as been clicked
     * in the manager window.
     * @param evt The MouseEvent to see if the Link as been clicked.
     */
    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        try {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            try {
                desktop.open(systemSettingsInfo);
            } catch (IOException ioe) {
                    WeatherLogger.log(Level.WARNING, "Error while trying to load System Settings Information.", ioe);
            }
        } 
        //If the license document doesn't exist
        catch (IllegalArgumentException ex) {
            WeatherLogger.log(Level.WARNING, "The System Settings Information File was not found in the default "
                + "location.", ex);
        }
    }//GEN-LAST:event_helpButtonActionPerformed

    private void removeProperty()
    {
        int rows[] = propertiesTable.getSelectedRows();
        if(rows.length == 0)
        {
            deleteWarningLable.setVisible(true);
            return; 
        }
        deleteWarningLable.setVisible(false);
        
        String deleted = "";
        int choice = JOptionPane.NO_OPTION;
        for(Integer index : propertiesTable.getSelectedRows()) {
            //Course number is the first column, represented by 0
            //ClassID is the third column, represented by 2
            //Section is the fourth column
            //Course name is the fifth column
            int propertyID = Integer.parseInt(propertiesTable.getValueAt(index, 0).toString());
            String propertyType = propertiesTable.getValueAt(index, 1).toString();
            String propertyName = propertiesTable.getValueAt(index, 2).toString();
            String propertyValue = propertiesTable.getValueAt(index, 3).toString();
            Property p = propertyMgr.obtainProperty(propertyID);                        
            
            if(JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete " + propertyType + ", "
                    + "Property Name " + propertyName + "?", 
                    "Delete Property", JOptionPane.YES_NO_OPTION) == 
                    JOptionPane.YES_OPTION) {
                propertyMgr.removeProperty(p);
                choice = JOptionPane.YES_OPTION;
                //updates the list of which classes were deleted
                deleted = deleted.concat("   Property Type: " + propertyType + "\n   Property Name: "+ propertyName
                                        + "\n   Property Value: " + propertyValue);
            }
        }
        if(choice == JOptionPane.YES_OPTION)
            updateTable();
        //will only show if the user deleted at least one class
        if(deleted.length()>0)
            JOptionPane.showMessageDialog(this, "   These property has been deleted:\n"+deleted, "Deleted Property", JOptionPane.PLAIN_MESSAGE);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPropertyButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel deleteWarningLable;
    private javax.swing.JLabel editWarningLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JScrollPane propertiesScrollPane;
    private javax.swing.JTable propertiesTable;
    // End of variables declaration//GEN-END:variables
}
