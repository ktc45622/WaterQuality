package weather.clientside.gui.administrator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import weather.ApplicationControlSystem;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.common.data.User;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkType;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;


/**
 * The <code>ListBookmarkCategoryWindow</code> class creates a form that
 * lists bookmark sub-categories.
 * @author Eric Subach
 * @author Alex Funk
 * @author Justin Enslin
 * @version 2012
 */
public class ManageBookmarkSubCategoryDialog extends BUDialog {
    JTable subCategoryTable;
    private ApplicationControlSystem clientControl;
    /**
     * The column number of the sub-category header.
     */
    private final int subCategoryColumnNumber = 0;
    /**
     * The column number of the description header.
     */
    private final int decriptionColumnNumber = 1;
    /**
     * Used to tell if combobox has been initialized.
     */
    private boolean initialized = false;
    
    
    /**
     * Opens a window to manage bookmark sub-categories with the default 
     * category chosen.
     * @param clientControl The application control system.
     */
    public ManageBookmarkSubCategoryDialog (ApplicationControlSystem clientControl) {
        super (clientControl);
        this.clientControl = clientControl;
        initialize ();
        categoryComboBox.setSelectedIndex(0);
        int width = 827 + this.getInsets().left + this.getInsets().right;
        int height = 375 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        postInitialize(false);
    }
    
    /**
     * Opens a window to manage bookmark sub-categories with the given category
     * chosen.
     * @param clientControl The application control system.
     * @param category The category this window will default to.
     */
    public ManageBookmarkSubCategoryDialog(ApplicationControlSystem clientControl, 
            String category){
        super(clientControl);
        this.clientControl = clientControl;
        initialize();
        categoryComboBox.setSelectedItem(category);
        int width = 827 + this.getInsets().left + this.getInsets().right;
        int height = 375 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        postInitialize(false);
    }
    
    /**
     * Returns this object as a <code>Component</code> for use by inner classes.
     * @return This object as a <code>Component</code>.
     */
    private Component thisComponent() {
        return this;
    }

    /**
     * Function called by the constructor to help with setup of the form.
     */
    private void initialize () {
        // Initialize components generated from NetBeans form.
        initComponents();
        nothingSelectedLabel.setVisible(false);
        setTitle ("Weather Viewer - Manage Bookmark Sub-Categories");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setIconImage(IconProperties.getTitleBarIconImage());
        //Populate the combo box
        User user = clientControl.getGeneralService().getUser();
        GUIComponentFactory.initBookmarkCategoryBoxWithoutDefault(categoryComboBox,
                eventManager, user);
        initialized = true;
    }
    
    /**
     * Updates the table.
     */
    private void updateTable() {
        BookmarkCategory item = clientControl.getDBMSSystem().getBookmarkCategoriesManager()
                .searchByName(categoryComboBox.getItemAt(categoryComboBox.getSelectedIndex()));
        subCategoryTable = GUIComponentFactory.
                getBookmarkSubCategoryTable(clientControl.getDBMSSystem().
                getBookmarkTypesManager(), item.getBookmarkCategoryNumber(),
                clientControl.getGeneralService().getUser());
        subCategoryTable.setAutoCreateRowSorter(true);
        subCategoryTable.getColumnModel().getColumn(subCategoryColumnNumber).setHeaderValue("Sub-Category");
        subCategoryTable.getColumnModel().getColumn(decriptionColumnNumber).setHeaderValue("Description");
        subCategoryTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(subCategoryTable);
        this.validate();
        subCategoryTable.addMouseListener(doubleClick);
    }

    /**
     * Mouse adapter that opens an edit dialog on double-click.
     */
    MouseAdapter doubleClick = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable target = (JTable) e.getSource();
                String subcategory = (String) target.getValueAt(target.getSelectedRow(), 0);
                if ("<None>".equals(subcategory)) {
                    JOptionPane.showMessageDialog(thisComponent(), 
                            "You cannot alter the default subcategory",
                            "Update Not Allowed", 
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                new EditBookmarkSubCategoryDialog(clientControl, subcategory,
                        categoryComboBox.getSelectedItem().toString());
            }
        }
    };


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        close_Button = new javax.swing.JButton();
        newSubCategoryButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        categoryComboBox = new javax.swing.JComboBox<String>();
        removeSubCategoryButton = new javax.swing.JButton();
        nothingSelectedLabel = new javax.swing.JLabel();
        editLabel = new javax.swing.JLabel();
        displayOrderButton = new javax.swing.JButton();

        setTitle("Weather Viewer - Manage Bookmark Categories");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        close_Button.setText("Close");
        close_Button.setToolTipText("Close the window without saving");
        close_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_ButtonActionPerformed(evt);
            }
        });
        getContentPane().add(close_Button, new org.netbeans.lib.awtextra.AbsoluteConstraints(752, 310, -1, -1));

        newSubCategoryButton.setText("Add Sub-Category...");
        newSubCategoryButton.setToolTipText("Create a new category");
        newSubCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSubCategoryButtonActionPerformed(evt);
            }
        });
        getContentPane().add(newSubCategoryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 310, -1, -1));
        getContentPane().add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 46, 803, 252));

        categoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(categoryComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 241, 22));

        removeSubCategoryButton.setText("Delete Selected Sub-Category");
        removeSubCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSubCategoryButtonActionPerformed(evt);
            }
        });
        getContentPane().add(removeSubCategoryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(344, 310, -1, -1));

        nothingSelectedLabel.setForeground(new java.awt.Color(204, 0, 0));
        nothingSelectedLabel.setText("No Sub-Category Selected.");
        getContentPane().add(nothingSelectedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(552, 314, -1, -1));

        editLabel.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        editLabel.setText("To edit a sub-category, double-click on its name.");
        getContentPane().add(editLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 347, 320, 16));

        displayOrderButton.setText("Change Display Order...");
        displayOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayOrderButtonActionPerformed(evt);
            }
        });
        getContentPane().add(displayOrderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 310, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void close_ButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_ButtonActionPerformed
        dispose ();
    }//GEN-LAST:event_close_ButtonActionPerformed


    private void newSubCategoryButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSubCategoryButtonActionPerformed
        new AddBookmarkSubCategoryWindow(clientControl, 
                (String)categoryComboBox.getSelectedItem());
    }//GEN-LAST:event_newSubCategoryButtonActionPerformed

    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryComboBoxActionPerformed
        if(initialized) {
            updateTable();
        }
    }//GEN-LAST:event_categoryComboBoxActionPerformed

private void removeSubCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSubCategoryButtonActionPerformed
    int row = subCategoryTable.getSelectedRow();
    if (row == -1) {
        nothingSelectedLabel.setVisible(true);
    } else {
        String subCategoryToRemove = (String) subCategoryTable.
                getValueAt(subCategoryTable.getSelectedRow(), 0);
        if ("<None>".equals(subCategoryToRemove)) {
            JOptionPane.showMessageDialog(this, 
                    "You cannot remove the default subcategory",
                    "Illegal Delete", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this subcategory?",
                "Weather Viewer - Remove Sub-category",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.YES_OPTION) {
            DBMSBookmarkEventTypesManager typeManager = 
            clientControl.getAdministratorControlSystem().
                    getGeneralService().getDBMSSystem()
                    .getBookmarkTypesManager();
            BookmarkType bookmarkType = typeManager.searchByName((String) subCategoryTable.getValueAt(row, 0),
                    categoryComboBox.getSelectedItem().toString());
            if (typeManager.removeOne(bookmarkType)) {
                JOptionPane.showMessageDialog(this, "Subcategory Removed Successfully",
                        "Remove Bookmark Subcategory", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Subcategory Not Removed",
                        "Remove Bookmark Subcategory Error", JOptionPane.ERROR_MESSAGE);
            }
            nothingSelectedLabel.setVisible(false);
        }
    }
    updateTable();
}//GEN-LAST:event_removeSubCategoryButtonActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        updateTable();
    }//GEN-LAST:event_formWindowGainedFocus

    private void displayOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayOrderButtonActionPerformed
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        new BookmarkSubCategoryDisplayOrderWindow(appControl, selectedCategory);
    }//GEN-LAST:event_displayOrderButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JButton close_Button;
    private javax.swing.JButton displayOrderButton;
    private javax.swing.JLabel editLabel;
    private javax.swing.JButton newSubCategoryButton;
    private javax.swing.JLabel nothingSelectedLabel;
    private javax.swing.JButton removeSubCategoryButton;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

}
