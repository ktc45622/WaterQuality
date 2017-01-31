
package weather.clientside.gui.administrator;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.common.data.Property;
import weather.common.dbms.DBMSPropertyManager;
import weather.common.gui.component.BUDialog;
/**
 * The <code>EditPropertyWindow</code> creates a form used for
 * administrators to edit properties in the database.
 * @author Xiang
 */
public class EditPropertyWindow extends BUDialog {

    private final ApplicationControlSystem finalClientControl;
    private DBMSPropertyManager propertyMgr;
    private int propertyID;
    private Property property;
    
    /**
     * Creates new form EditPropertyWindow.
     * @param appControl The implementation to be used to create the window.
     * @param toEdit String holding property id to edit.
     */
    public EditPropertyWindow(ApplicationControlSystem appControl, String toEdit) {
        super(appControl);
        propertyMgr = appControl.getGeneralService().getDBMSSystem().getPropertyManager();
        initComponents();
        
        property = propertyMgr.obtainProperty(Integer.parseInt(toEdit));
        propertyID = property.getPropertyID();
        propertyIDTextField.setText("" + propertyID);
        propertyTypeTextField.setText(property.getPropertyType());
        propertyTypeDisplayNameTextField.setText(property.getPropertyTypeDisplayName());
        propertyNameTextField.setText(property.getPropertyName());
        propertyDisplayNameTextField.setText(property.getPropertyDisplayName());
        propertyValueTextField.setText(property.getPropertyValue());
        notesTextArea.setText(property.getNotes());
        lastChangeTextField.setText(property.getPreviousValue());
        defaultWarningLabel.setVisible(false);
        
        
        if(property.getIsEditable() == 0)
        {
            isEditableTextField.setText("No");
            propertyValueTextField.setEditable(false);
            editButton.setVisible(true);
        }
        else
        {
            isEditableTextField.setText("Yes");
            propertyValueTextField.setEditable(true);
            editButton.setVisible(false);
        }
        
        getRootPane().setDefaultButton(saveButton);
        propertyValueWarningLabel.setVisible(false);
        typeDisplayNameErrorLabel.setVisible(false);
        displayNameErrorLabel.setVisible(false);
        this.finalClientControl = appControl;
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                if(!savedCheck(property)){
                    if(blankCheck()==false)
                        if (finalClientControl.getGeneralService()
                                .leaveWithoutSaving(thisComponent())) {
                            dispose();
                    } else {
                        setDefaultCloseOperation(BUDialog.DO_NOTHING_ON_CLOSE);
                    }
                }
                dispose();
            }
        });
        
        super.postInitialize(false);
    }

    /**
     * Returns this object as a <code>Component</code> for use by inner classes.
     * @return This object as a <code>Component</code>.
     */
    private Component thisComponent() {
        return this;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        propertyTypeLabel = new javax.swing.JLabel();
        propertyTypeTextField = new javax.swing.JTextField();
        propertyNameLabel = new javax.swing.JLabel();
        propertyNameTextField = new javax.swing.JTextField();
        propertyValueLabel = new javax.swing.JLabel();
        propertyValueTextField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        removePropertyButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        propertyIDLabel = new javax.swing.JLabel();
        propertyIDTextField = new javax.swing.JTextField();
        propertyValueWarningLabel = new javax.swing.JLabel();
        propertyTypeDisplayNameLabel = new javax.swing.JLabel();
        propertyTypeDisplayNameTextField = new javax.swing.JTextField();
        propertyDisplayNameLabel = new javax.swing.JLabel();
        propertyDisplayNameTextField = new javax.swing.JTextField();
        typeDisplayNameErrorLabel = new javax.swing.JLabel();
        displayNameErrorLabel = new javax.swing.JLabel();
        isEditableLabel = new javax.swing.JLabel();
        isEditableTextField = new javax.swing.JTextField();
        notesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notesTextArea = new javax.swing.JTextArea();
        editButton = new javax.swing.JButton();
        lastChangeTextField = new javax.swing.JTextField();
        previousChangeLabel = new javax.swing.JLabel();
        defaultButton = new javax.swing.JButton();
        defaultWarningLabel = new javax.swing.JLabel();

        setTitle("Weather Viewer - Edit Property");
        setResizable(false);

        propertyTypeLabel.setText("Property Type");

        propertyTypeTextField.setEditable(false);

        propertyNameLabel.setText("Property Name");

        propertyNameTextField.setEditable(false);

        propertyValueLabel.setText("Property Value");

        saveButton.setText("Update Changes");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        removePropertyButton.setText("Remove Property");
        removePropertyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePropertyButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        propertyIDLabel.setText("Property ID");

        propertyIDTextField.setEditable(false);

        propertyValueWarningLabel.setForeground(new java.awt.Color(251, 51, 51));
        propertyValueWarningLabel.setText("* Property Value Required");

        propertyTypeDisplayNameLabel.setText("Property Type Display Name");

        propertyDisplayNameLabel.setText("Property Display Name");

        typeDisplayNameErrorLabel.setForeground(new java.awt.Color(251, 51, 51));
        typeDisplayNameErrorLabel.setText("* Property Type Display Name Requird");

        displayNameErrorLabel.setForeground(new java.awt.Color(251, 51, 51));
        displayNameErrorLabel.setText("* Property Display Name Requird");

        isEditableLabel.setText("Is this Editable");

        isEditableTextField.setEditable(false);

        notesLabel.setText("Description of This Property");

        notesTextArea.setColumns(20);
        notesTextArea.setRows(5);
        jScrollPane1.setViewportView(notesTextArea);

        editButton.setText("I have access to edit ");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        lastChangeTextField.setEditable(false);

        previousChangeLabel.setText("Previous Chnage");

        defaultButton.setText("Set to Default");
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });

        defaultWarningLabel.setForeground(new java.awt.Color(251, 51, 51));
        defaultWarningLabel.setText("Press Update Changes to save the changes.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(propertyDisplayNameLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(propertyNameLabel)
                                    .addComponent(propertyTypeDisplayNameLabel)
                                    .addComponent(propertyTypeLabel)
                                    .addComponent(isEditableLabel)
                                    .addComponent(previousChangeLabel))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(propertyValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(propertyDisplayNameTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                .addComponent(propertyNameTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(propertyIDTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(propertyTypeDisplayNameTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(propertyTypeTextField, javax.swing.GroupLayout.Alignment.LEADING)))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(typeDisplayNameErrorLabel)
                                            .addComponent(displayNameErrorLabel)
                                            .addComponent(propertyValueWarningLabel)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(isEditableTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                        .addComponent(lastChangeTextField, javax.swing.GroupLayout.Alignment.LEADING))))
                            .addComponent(propertyIDLabel))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(defaultWarningLabel)
                            .addComponent(notesLabel)
                            .addComponent(propertyValueLabel)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 692, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(saveButton)
                                .addGap(18, 18, 18)
                                .addComponent(removePropertyButton)
                                .addGap(18, 18, 18)
                                .addComponent(defaultButton)
                                .addGap(18, 18, 18)
                                .addComponent(editButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(closeButton)))
                        .addGap(0, 10, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyIDLabel)
                    .addComponent(propertyIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(propertyTypeLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyTypeDisplayNameLabel)
                    .addComponent(propertyTypeDisplayNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeDisplayNameErrorLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyNameLabel)
                    .addComponent(propertyNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyDisplayNameLabel)
                    .addComponent(propertyDisplayNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(displayNameErrorLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyValueLabel)
                    .addComponent(propertyValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(propertyValueWarningLabel))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastChangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(previousChangeLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isEditableTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isEditableLabel))
                .addGap(18, 18, 18)
                .addComponent(notesLabel)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(removePropertyButton)
                    .addComponent(editButton)
                    .addComponent(closeButton)
                    .addComponent(defaultButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(defaultWarningLabel)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * A button to close the window, if the value in the text field has been changed
     * with saving, it will show an option to remind administrator to save the change 
     * @param evt 
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if(!savedCheck(property)){        
            int ans = JOptionPane.showConfirmDialog(this,"Would you like to save the changes you have made?"
                ,"Weather Viewer - Select An Option",JOptionPane.YES_NO_CANCEL_OPTION);
            if(ans == JOptionPane.YES_OPTION){
                if(!savedCheck(property)){
                    if(blankCheck()==false){
                        propertyMgr.updateProperty(this.setNewProperty());
                        JOptionPane.showMessageDialog(this, "Property updated.", "Weather Viewer", JOptionPane.OK_OPTION);
                        dispose();
                    }
                }
            } else if(ans == JOptionPane.NO_OPTION) {
                dispose();
            } else {
                setDefaultCloseOperation(BUDialog.DO_NOTHING_ON_CLOSE);
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * A button to remove one property 
     * @param evt 
     */
    private void removePropertyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePropertyButtonActionPerformed
        
        if(property.getIsEditable() == 0)
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
    }//GEN-LAST:event_removePropertyButtonActionPerformed

    private void removeProperty()
    {
        int ans = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this Property?", 
                "Weather Viewer - Select an Option"
                , JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            
            String prop = "" + property.getPropertyID();
            Vector<Property> p = new Vector<Property>(); 
            if(property.getPropertyType().equals("general"))
                p = propertyMgr.obtainAllGeneralProperties();
            if(property.getPropertyType().equals("gui"))
                p = propertyMgr.obtainAllGUIProperties();
            if(property.getPropertyType().equals("wunder_twovar"))
                p = propertyMgr.obtainAllWeatherStationTwoVariableProperties();
            if(property.getPropertyType().equals("wunder_nosolar"))
                p = propertyMgr.obtainAllWeatherStationNoSolarVariableProperties();
            if(property.getPropertyType().equals("wunder"))
                p = propertyMgr.obtainAllWeatherStationVariableProperties();
            
            for (int i = 0; i < p.size(); i++) {
                if (prop.equals(""+p.get(i).getPropertyID())) {
                    property = p.get(i);
                }
            }
            propertyMgr.removeProperty(property);
                    //populateClassBox();
            JOptionPane.showMessageDialog(this, "Property deleted successfully.", 
                        "Weather Viewer - Delete Property", JOptionPane.OK_OPTION);
                this.dispose();
       }              
    }
    
    /**
     * THe change will be saved to the database when save button is clicked
     * @param evt 
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if(blankCheck()==false)
        {
            if(propertyValueTextField.isEditable() == true)
            {
                if (propertyMgr.updateProperty(this.setNewProperty())) {
                    JOptionPane.showMessageDialog(this, "Property updated.");
                    lastChangeTextField.setText(property.getPreviousValue());
                    defaultWarningLabel.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Property update failed.");
                }
            }else
            {
                JOptionPane.showMessageDialog(this, "You can't modify this property.");
            }
        }

        this.repaint();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        PropertyAuthenticatorWindow tempWindow = new PropertyAuthenticatorWindow(finalClientControl);
        if(tempWindow.getIsAdmin() == true)
        {
            propertyValueTextField.setEditable(true);
            JOptionPane.showMessageDialog(this, "You can edit this property right now");
        }
        else
        {
            JOptionPane.showMessageDialog(this, "You are not administrator, you have no access to edit this property");
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultButtonActionPerformed
        
        int ans = JOptionPane.showConfirmDialog(this,"Would you like to save the changes you have made?"
                ,"Weather Viewer - Select An Option",JOptionPane.YES_NO_CANCEL_OPTION);
            if(ans == JOptionPane.YES_OPTION)
            {
                    defaultWarningLabel.setVisible(true);
                    propertyValueTextField.setText(property.getDefaultValue());
            }
    }//GEN-LAST:event_defaultButtonActionPerformed

    /**
     * Return a new property to use it to update the property
     * @return a new property to use it to update the property.
     */
    private Property setNewProperty()
    {
        Vector<Property> p = new Vector<Property>(); 
        if(property.getPropertyType().equals("general"))
            p = propertyMgr.obtainAllGeneralProperties();
        if(property.getPropertyType().equals("gui"))
            p = propertyMgr.obtainAllGUIProperties();
        if(property.getPropertyType().equals("wunder_twovar"))
            p = propertyMgr.obtainAllWeatherStationTwoVariableProperties();
        if(property.getPropertyType().equals("wunder_nosolar"))
            p = propertyMgr.obtainAllWeatherStationNoSolarVariableProperties();
        if(property.getPropertyType().equals("wunder"))
            p = propertyMgr.obtainAllWeatherStationVariableProperties();
        
        String prop = property.getPropertyID() + "";
        for (int i = 0; i < p.size(); i++) {
            if (prop.equals("" + p.get(i).getPropertyID())) {
                property = p.get(i);
            }
        }
        try{
            property.setPreviousValue(property.getPropertyValue());
            property.setPropertyTypeDisplayName(propertyTypeDisplayNameTextField.getText().toString().trim());
            property.setPropertyDisplayName(propertyDisplayNameTextField.getText().toString().trim());
            property.setPropertyValue(propertyValueTextField.getText().toString().trim());
            property.setNotes(notesTextArea.getText().toString());
        }catch(NumberFormatException e){
                propertyValueWarningLabel.setVisible(true);
        }
            return property;
    }
    
    /**
     * Check if the text field is empty, if it is empty the red words warning will show up 
     * @return true if data is missing in any text field. False otherwise.
     */
    private boolean blankCheck()
    {
        propertyValueWarningLabel.setVisible(false);
        boolean blank = false;
        if(propertyValueTextField.getText().toString().trim().equals(""))
        {
            propertyValueWarningLabel.setVisible(true);
            blank = true;
        }
        
        if(propertyTypeDisplayNameTextField.getText().toString().trim().equals(""))
        {
            typeDisplayNameErrorLabel.setVisible(true);
            blank = true;
        }
        
        if(propertyDisplayNameTextField.getText().toString().trim().equals(""))
        {
            displayNameErrorLabel.setVisible(true);
            blank = true;
        }
        
        return blank;
    }
    
    /**
     * Check if changes has been saved
     * @param property the property to check
     * @return true if saved false otherwise.
     */
    private boolean savedCheck(Property property)
    {
        if(propertyMgr.obtainAllProperties().isEmpty())
            return true;
        if(propertyValueTextField.getText().toString().trim().equals(""))
        {
            propertyValueWarningLabel.setVisible(true);
            return true;
        }
        
        if(propertyTypeDisplayNameTextField.getText().toString().trim().equals(""))
        {
            typeDisplayNameErrorLabel.setVisible(true);
            return true;
        }
        
        if(propertyDisplayNameTextField.getText().toString().trim().equals(""))
        {
            displayNameErrorLabel.setVisible(true);
            return true;
        }
        
        try
        {
              if(property.getPropertyValue().equals(propertyValueTextField.getText().toString().trim())
                      && property.getPropertyTypeDisplayName().equals((propertyTypeDisplayNameTextField.getText().toString().trim()))
                      && property.getPropertyDisplayName().equals(propertyDisplayNameTextField.getText().toString().trim()))
                      return true;
              
        }catch(NumberFormatException e)
        {
            return true;
        }
        
        return false;
    }
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton defaultButton;
    private javax.swing.JLabel defaultWarningLabel;
    private javax.swing.JLabel displayNameErrorLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel isEditableLabel;
    private javax.swing.JTextField isEditableTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastChangeTextField;
    private javax.swing.JLabel notesLabel;
    private javax.swing.JTextArea notesTextArea;
    private javax.swing.JLabel previousChangeLabel;
    private javax.swing.JLabel propertyDisplayNameLabel;
    private javax.swing.JTextField propertyDisplayNameTextField;
    private javax.swing.JLabel propertyIDLabel;
    private javax.swing.JTextField propertyIDTextField;
    private javax.swing.JLabel propertyNameLabel;
    private javax.swing.JTextField propertyNameTextField;
    private javax.swing.JLabel propertyTypeDisplayNameLabel;
    private javax.swing.JTextField propertyTypeDisplayNameTextField;
    private javax.swing.JLabel propertyTypeLabel;
    private javax.swing.JTextField propertyTypeTextField;
    private javax.swing.JLabel propertyValueLabel;
    private javax.swing.JTextField propertyValueTextField;
    private javax.swing.JLabel propertyValueWarningLabel;
    private javax.swing.JButton removePropertyButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel typeDisplayNameErrorLabel;
    // End of variables declaration//GEN-END:variables

}
