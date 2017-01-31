package weather.clientside.gui.client;

import weather.common.gui.component.IconProperties;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import weather.clientside.manager.MovieController;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;

/**
 * The <code>AdvanvedSearchBookmarkDialog</code> class creates a form for users 
 * to search for bookmarks in a more detailed way.  Its constructors are
 * designed to be called from specific classes.
 * @author kc70024
 */
public class AdvancedSearchBookmarkDialog extends BUDialog {

    private JDialog parent;
    private boolean isButtonClicked = false;
    private BookmarkDateSelectionWindow sbcwindow;
    private Calendar startDate;
    private Calendar endDate;
    private String selectedSubcategory;
   
   /**
    * Creates new <code>AdvancedSearchBookmarkDialog</code> called from a 
    * <code>SearchBookmarkDialog</code>.
    * @param parent JDialog that will help create this form.
    * @param controller The main window's <code>MovieController</code>.
    */
    public AdvancedSearchBookmarkDialog(SearchBookmarkDialog parent,
            MovieController controller) {
        super();
        this.parent = parent;
        initComponents();
        loadRangefromController(controller);
        ButtonGroup bg = new ButtonGroup();
        bg.add(specifyNameJCheckBox);
        bg.add(TimeRange_CheckBox);
        bg.add(this.allOfTypeRatioButton);
        allOfTypeRatioButton.setSelected(true);
        specifyBookmarkNameTextField.setEnabled(false);
        timeFromJTextField.setEditable(false);
        timeToJTextField.setEditable(false);
        Calendar_JButton.setEnabled(false);
        error_JLabel.setVisible(false);
        subCategoryLabel.setVisible(false);
        subcategoriesComboBox.setVisible(false);
        btnClearTime.setVisible(false);
        
        //Size form
        int width = 657 + this.getInsets().left + this.getInsets().right;
        int height = 289 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        this.revalidate();
        super.postInitialize(false);
    }
    /**
     * Creates new <code>AdvancedSearchBookmarkDialog</code> called from a 
    * <code>OpenManageBookmarkDialog</code>.
     * @param parent The JDialog parent of this window.
     * @param controller The main window's <code>MovieController</code>.
     * @param subCategories The sub-categories to display in the ComboBox.
     */
    public AdvancedSearchBookmarkDialog(OpenManageBookmarkDialog parent, 
            MovieController controller, ArrayList<String> subCategories) {
        super();
        this.parent = parent;
        initComponents();
        loadRangefromController(controller);
        ButtonGroup bg = new ButtonGroup();
        bg.add(specifyNameJCheckBox);
        bg.add(TimeRange_CheckBox);
        bg.add(this.allOfTypeRatioButton);
        this.setTitle("Weather Viewer - Advanced Options");
        specifyBookmarkNameTextField.setEnabled(false);
        timeFromJTextField.setEditable(false);
        timeToJTextField.setEditable(false);
        Calendar_JButton.setEnabled(false);
        error_JLabel.setVisible(false);

        allOfTypeRatioButton.setVisible(false);
        specifyNameJCheckBox.setVisible(false);
        specifyBookmarkNameTextField.setVisible(false);
        subCategoryLabel.setVisible(true);
        subcategoriesComboBox.setVisible(true);
        TimeRange_CheckBox.setVisible(false);
        btnClearTime.setText("Set Date Range");
        
        //Size form
        int width = 657 + this.getInsets().left + this.getInsets().right;
        int height = 289 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        subcategoriesComboBox.addItem("Any Subcategory");
        for(String subCategory : subCategories) {
            subcategoriesComboBox.addItem(subCategory);
        }

        this.revalidate();
        super.postInitialize(false);
    }
    
    //THIS FUNCTION IS FOR DEBUGING
    private void debugDate(Calendar date, String name){
        String dateFormat = "MM/dd/yy hh:mm:ss a z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Debug.print("Sent: " );
        if(date == null){
            Debug.println(name + ": null");
        } else {
            Debug.println(name + ": " + simpleDateFormat.format(date.getTime()));
        }
    }
    
    
    /**
     * Loads the <code>Calender</code> fields to match the time rage shown on a 
     * <code>MovieController</code>.
     * @param controller The <code>MovieController</code>.
     */
    private void loadRangefromController(MovieController controller) {
        startDate = new GregorianCalendar();
        startDate.setTimeZone(controller.getPrimaryTimeZone());
        startDate.setTimeInMillis(controller.getResourceRange().getStartTime()
                .getTime());
        endDate = new GregorianCalendar();
        endDate.setTimeZone(controller.getPrimaryTimeZone());
        endDate.setTimeInMillis(controller.getResourceRange().getStopTime()
                .getTime());
    }
    
    /**
     * Sets the start time for the search.
     *
     * @param start <code>Calendar</code> holding the starting time.
     */
    public void setStartTime(Calendar start) {
        startDate = start;
        Debug.println("Returned starting time: " + CalendarFormatter.
                formatWithTimeZone(startDate));
    }

    /**
     * Sets the end time for the search.
     *
     * @param end <code>Calendar</code> holding the ending time..
     */
    public void setEndTime(Calendar end) {
        endDate = end;
        Debug.println("Returned ending time: " + CalendarFormatter.
                formatWithTimeZone(endDate));
    }
    
    /**
     * Loads the last saved range into the text fields.
     */
    private void loadLastSavedRange() {
        timeFromJTextField.setText(CalendarFormatter.format(startDate,
                CalendarFormatter.DisplayFormat.DATE) + " "
                + CalendarFormatter.format(startDate,
                CalendarFormatter.DisplayFormat.TIME_12) + " "
                + CalendarFormatter.format(startDate,
                CalendarFormatter.DisplayFormat.TIME_ZONE));
        
        timeToJTextField.setText(CalendarFormatter.format(endDate,
                CalendarFormatter.DisplayFormat.DATE) + " "
                + CalendarFormatter.format(endDate,
                CalendarFormatter.DisplayFormat.TIME_12) + " "
                + CalendarFormatter.format(endDate,
                CalendarFormatter.DisplayFormat.TIME_ZONE));

    }
    
    /**
     * Returns the <code>TimeZone</code> used by the from.
     * @return The <code>TimeZone</code> used by the from.
     */
    public TimeZone getTimeZone() {
        return startDate.getTimeZone();
    }

    /**
     * Retrieves the start date set for the search
     *
     * @return Date object representing that start
     */
    public Calendar getStartTime() {
        if (sbcwindow != null && sbcwindow.hasSelection()) {
            startDate = sbcwindow.getStart();
        }
        return startDate;
    }

    /**
     * Retrieves the end date set for the search
     *
     * @return Date object representing the end
     */
    public Calendar getEndTime() {
        if (sbcwindow != null && sbcwindow.hasSelection()) {
            endDate = sbcwindow.getEnd();
        }
        return endDate;
    }

    /**
     * Gets the selected Sub-category.
     *
     * @return A String which represents the Sub-categories name.
     */
    public String getSelectedSubcategory() {
        return selectedSubcategory;
    }

    /**
     * Gets the information searched whether by date, by all, or by name.
     *
     * @return a String of what was searched
     */
    public String getSearchInformation() {
        if (TimeRange_CheckBox.isSelected()) {
            return "Time Selected";
        } else if (specifyNameJCheckBox.isSelected()) {
            return "Specific Name Selected";
        } else {
            return "All Selected";
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        timeToJTextField = new javax.swing.JTextField();
        specifyBookmarkNameTextField = new javax.swing.JTextField();
        Calendar_JButton = new javax.swing.JButton();
        OkButton = new javax.swing.JButton();
        TimeRange_CheckBox = new javax.swing.JRadioButton();
        timeFromJTextField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();
        specifyNameJCheckBox = new javax.swing.JRadioButton();
        allOfTypeRatioButton = new javax.swing.JRadioButton();
        subcategoriesComboBox = new javax.swing.JComboBox<String>();
        subCategoryLabel = new javax.swing.JLabel();
        error_JLabel = new javax.swing.JLabel();
        btnClearTime = new javax.swing.JButton();

        setTitle("Weather Viewer - Advanced Bookmark Search");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("To");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 76, 15, 16));
        jPanel1.add(timeToJTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(307, 73, 250, 22));

        specifyBookmarkNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                specifyBookmarkNameTextFieldFocusGained(evt);
            }
        });
        jPanel1.add(specifyBookmarkNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(219, 114, 400, 22));

        Calendar_JButton.setIcon(IconProperties.getCalendarLargeIcon());
        Calendar_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Calendar_JButtonActionPerformed(evt);
            }
        });
        jPanel1.add(Calendar_JButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 73, 42, -1));

        OkButton.setText("OK");
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });
        jPanel1.add(OkButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 223, 63, 25));

        TimeRange_CheckBox.setText("Select Date Range ");
        TimeRange_CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TimeRange_CheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(TimeRange_CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 45, -1, -1));
        jPanel1.add(timeFromJTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 73, 250, 22));

        cancelButton.setText("Close");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(556, 223, 63, 25));

        specifyNameJCheckBox.setText("Search by Bookmark Name ");
        specifyNameJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specifyNameJCheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(specifyNameJCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 113, -1, -1));

        allOfTypeRatioButton.setText("All Bookmarks of This Category and Subcategory");
        allOfTypeRatioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allOfTypeRatioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(allOfTypeRatioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 2, -1, -1));

        subcategoriesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subcategoriesComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(subcategoriesComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 189, 184, -1));

        subCategoryLabel.setText("Sub Category");
        jPanel1.add(subCategoryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 166, -1, -1));

        error_JLabel.setForeground(new java.awt.Color(255, 0, 51));
        error_JLabel.setIcon(IconProperties.getResourceInactiveIconImage());
        error_JLabel.setText("The name is illegal for a bookmark");
        jPanel1.add(error_JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 140, 221, -1));

        btnClearTime.setText("Clear Date Rage");
        btnClearTime.setToolTipText("");
        btnClearTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearTimeActionPerformed(evt);
            }
        });
        jPanel1.add(btnClearTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 45, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 633, 265));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Calendar_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Calendar_JButtonActionPerformed
        sbcwindow = new BookmarkDateSelectionWindow(this,
                timeFromJTextField, timeToJTextField, endDate.getTimeZone());
}//GEN-LAST:event_Calendar_JButtonActionPerformed

    /**
     * Checks to see if OK or Cancel is clicked.
     *
     * @return True if OK, false if Cancel.
     */
    public boolean get_OK_Cancel() {
        return isButtonClicked;
    }

    /**
     * Gets the BookmarkInstances by name.
     *
     * @return True if the specifyNameJCheckBox is selected, false otherwise.
     */
    public boolean byName() {
        if (specifyNameJCheckBox.isSelected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the name of the BookmarkInstance.
     *
     * @return The name of the Bookmark.
     */
    public String getSpecifyName() {
        return specifyBookmarkNameTextField.getText();
    }

    /**
     * Gets the BookmarkInstances from that specific time range.
     *
     * @return True if the TimeRange_CheckBox is selected, false otherwise.
     */
    public boolean byTimeRange() {
        if (TimeRange_CheckBox.isSelected()) {
            return true;
        } else {
            return false;
        }
    }

    private void specifyNameJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specifyNameJCheckBoxActionPerformed
        error_JLabel.setVisible(false);
        specifyBookmarkNameTextField.setText("");
        if (specifyNameJCheckBox.isSelected()) {
            this.timeFromJTextField.setText("");
            this.timeToJTextField.setText("");
            this.timeFromJTextField.setEnabled(false);
            this.timeToJTextField.setEnabled(false);
            this.Calendar_JButton.setEnabled(false);
            specifyBookmarkNameTextField.setEnabled(true);
        } else {
            specifyBookmarkNameTextField.setEnabled(false);
        }
    }//GEN-LAST:event_specifyNameJCheckBoxActionPerformed

    private void TimeRange_CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TimeRange_CheckBoxActionPerformed
        loadLastSavedRange();
        error_JLabel.setVisible(false);
        if (TimeRange_CheckBox.isSelected()) {
            specifyBookmarkNameTextField.setText("");
            specifyBookmarkNameTextField.setEnabled(false);
            turnOnRange();
        } else {
            turnOffRange();
        }
    }//GEN-LAST:event_TimeRange_CheckBoxActionPerformed

    private void turnOffRange() {
        this.timeFromJTextField.setEnabled(false);
        this.timeToJTextField.setEnabled(false);
        this.timeFromJTextField.setText("");
        this.timeToJTextField.setText("");
        this.Calendar_JButton.setEnabled(false);
    }

    private void turnOnRange() {
        this.timeFromJTextField.setEnabled(true);
        this.timeToJTextField.setEnabled(true);
        this.Calendar_JButton.setEnabled(true);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        parent.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        if (specifyNameJCheckBox.isSelected() && specifyBookmarkNameTextField.getText().trim().equals("")) {
            error_JLabel.setVisible(true);
            return;
        }
        isButtonClicked = true;

        //If parent is OpenManageBookmarkDialog, it must be told OK was pressed
        //and passed values.
        if (parent instanceof OpenManageBookmarkDialog) {
            OpenManageBookmarkDialog casting = (OpenManageBookmarkDialog) parent;
            casting.setAreAdvancedOptionsSelected(true);
            casting.setSelectedSubcategory(subcategoriesComboBox.getSelectedItem().toString());

            //Check for unselected radio.
            if (!btnClearTime.getText().equals("Clear Date Range")) {
                startDate = null;
                endDate = null;
            }

            //Send dates
            debugDate(startDate, "startDate");
            debugDate(endDate, "endDate");
            casting.setStartDate(startDate);
            casting.setEndDate(endDate);
        }

        parent.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_OkButtonActionPerformed

    private void allOfTypeRatioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allOfTypeRatioButtonActionPerformed
        error_JLabel.setVisible(false);
        if (allOfTypeRatioButton.isSelected()) {
            specifyBookmarkNameTextField.setText("");
            specifyBookmarkNameTextField.setEnabled(false);
            this.timeFromJTextField.setText("");
            this.timeToJTextField.setText("");
            this.timeFromJTextField.setEnabled(false);
            this.timeToJTextField.setEnabled(false);
            this.Calendar_JButton.setEnabled(false);
        }
    }//GEN-LAST:event_allOfTypeRatioButtonActionPerformed

    private void subcategoriesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subcategoriesComboBoxActionPerformed
        selectedSubcategory = subcategoriesComboBox.getSelectedItem().toString();
    }//GEN-LAST:event_subcategoriesComboBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        parent.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void btnClearTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearTimeActionPerformed
        loadLastSavedRange();
        if (btnClearTime.getText().equals("Clear Date Range")) {
            btnClearTime.setText("Set Date Range");
            turnOffRange();
        } else {
            btnClearTime.setText("Clear Date Range");
            turnOnRange();
        }
    }//GEN-LAST:event_btnClearTimeActionPerformed

    private void specifyBookmarkNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_specifyBookmarkNameTextFieldFocusGained
        error_JLabel.setVisible(false);
    }//GEN-LAST:event_specifyBookmarkNameTextFieldFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Calendar_JButton;
    private javax.swing.JButton OkButton;
    private javax.swing.JRadioButton TimeRange_CheckBox;
    private javax.swing.JRadioButton allOfTypeRatioButton;
    private javax.swing.JButton btnClearTime;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel error_JLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField specifyBookmarkNameTextField;
    private javax.swing.JRadioButton specifyNameJCheckBox;
    private javax.swing.JLabel subCategoryLabel;
    private javax.swing.JComboBox<String> subcategoriesComboBox;
    private javax.swing.JTextField timeFromJTextField;
    private javax.swing.JTextField timeToJTextField;
    // End of variables declaration//GEN-END:variables
}
