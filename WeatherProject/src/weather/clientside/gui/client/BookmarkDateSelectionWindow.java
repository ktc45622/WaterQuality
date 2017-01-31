package weather.clientside.gui.client;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.CalendarFormatter;

/**
 * The <code>BookmarkDateSelectionWindow</code> class creates forms that
 * bookmark-related forms use to retrieve dates.
 * 
 * @author kc70024
 * @author John Lenhart
 * @version Spring 2012
 */
public class BookmarkDateSelectionWindow extends BUDialog {
    private Calendar startDate;
    private Calendar endDate;
    private JDialog parent;
    private Calendar timeFrom;
    private Calendar timeTo;
    private JTextField timeFromField;
    private JTextField timeToField;
    private JTextPane dateFromPane;
    private JTextPane timeFromPane;
    private boolean isBookmarkAddEditWindow;
    private boolean select = false;
    
    /**
     * This creates new form BookmarkDateSelectionWindow and is called from
     * <code>AdvancedSearchBookmarkDialog</code>.
     *
     * @param parent The parent dialog.
     * @param timeFromField A <code>JTextField</code> holding the "From" portion
     * of the initial setting of the form.
     * @param timeToField A <code>JTextField</code> holding the "To" portion of
     * the initial setting of the form.
     * @param timezone The <code>TimeZone</code> in use by the parent dialog.
     */
    public BookmarkDateSelectionWindow(JDialog parent, JTextField timeFromField, 
            JTextField timeToField, TimeZone timezone) {
        super();
        isBookmarkAddEditWindow = false;
        this.timeFromField = timeFromField;
        this.timeToField = timeToField;
        startDate = new GregorianCalendar();
        startDate.setTimeZone(timezone);
        endDate = new GregorianCalendar();
        endDate.setTimeZone(timezone);
        this.parent = parent;
        
        initComponents();
        oneCalendarCloseButton.setVisible(false);
        
        timeFromCalendar.setDate(parseTimeForTwoCalendarWindow(timeFromField, 
                timezone));
        timeToCalendar.setDate(parseTimeForTwoCalendarWindow(timeToField, 
                timezone));
        setTitle("Weather Viewer - Specify Bookmark Instance Date Range");
        super.postInitialize(false);
    }
    
    /**
     * This opens up a window with a single
     * <code>BUCalendar</code> for getting a date and time. it is used by
     * <code>BookmarkAddEditWindow</code>.
     *
     * @author John Lenhart
     * @param parent The parent dialog.
     * @param datePane A <code>JTextPane</code> holding the date portion of the
     * initial setting of the form.
     * @param timePane A <code>JTextPane</code> holding the time portion of the
     * initial setting of the form.
     * @param timezone  The <code>TimeZone</code> in use by the parent dialog.
     */
    public BookmarkDateSelectionWindow(JDialog parent, JTextPane datePane, 
            JTextPane timePane, TimeZone timezone) {
        super();
        this.parent = parent;
        isBookmarkAddEditWindow = true;
        dateFromPane = datePane;
        timeFromPane = timePane;
        startDate = new GregorianCalendar();
        startDate.setTimeZone(timezone);
        
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        initComponents();
        
        timeFromCalendar.setDate(parseTimeForOneCalendarWindow(timezone));
        setTitle("Choose A Time For The Bookmark/Event");
        timeToCalendar.setVisible(false);
        setSize(400, this.getHeight());
        setResizable(false);
        twoCalendarCloseButton.setVisible(false);
        oneCalendarCloseButton.setVisible(true);
        timeToLabel.setVisible(false);
        timeFromLabel.setVisible(false);
        super.postInitialize(false);
    }
    
    /**
     * A helper function for use to construct a <code>Calendar</code> object
     * holding time data sent to the two-calendar window.
     * @param sourceField A <code>JTextField</code> holding the desired portion
     * of the initial setting of the form.
     * @param timezone The <code>TimeZone</code> in use by the parent dialog.
     * @return The constructed <code>Calendar</code>.
     */
    private Calendar parseTimeForTwoCalendarWindow(JTextField sourceField, 
            TimeZone timezone) {
        Calendar returnCalendar = new GregorianCalendar();
        
        //Set time zone.
        returnCalendar.setTimeZone(timezone);
        
        //Parse data from text panes.
        String foratInputString = sourceField.getText().trim();
        try {
            returnCalendar.setTime(CalendarFormatter.parse(foratInputString, 
                    CalendarFormatter.DisplayFormat.DEFAULT_WITH_TIME_ZONE,
                    timezone).getTime());
        } catch (ParseException e) {
            return returnCalendar;  //Unable to parse data.
        }
        
        return returnCalendar;
    }
    
    /**
     * A helper function for use to construct a <code>Calendar</code> object 
     * holding time data sent to the one-calendar window.
     * @param timezone The <code>TimeZone</code> in use by the parent dialog.
     * @return The constructed <code>Calendar</code>.
     */
    private Calendar parseTimeForOneCalendarWindow(TimeZone timezone) {
        Calendar returnCalendar = new GregorianCalendar();
        
        //Set time zone.
        returnCalendar.setTimeZone(timezone);
        
        //Parse data from text panes.
        String foratInputString = dateFromPane.getText().trim() + ' '
                + timeFromPane.getText().trim();
        try {
            returnCalendar.setTime(CalendarFormatter.parse(foratInputString, 
                    CalendarFormatter.DisplayFormat.DEFAULT, timezone)
                    .getTime());
        } catch (ParseException e) {
            return returnCalendar;  //Unable to parse data.
        }
        
        return returnCalendar;
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timeFromLabel = new javax.swing.JLabel();
        timeToLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        twoCalendarCloseButton = new javax.swing.JButton();
        timeFromCalendar = new weather.common.utilities.BUCalendar();
        timeToCalendar = new weather.common.utilities.BUCalendar();
        oneCalendarCloseButton = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        timeFromLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        timeFromLabel.setText("Time From");

        timeToLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        timeToLabel.setText("Time To");

        okButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        twoCalendarCloseButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        twoCalendarCloseButton.setText("Close");
        twoCalendarCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoCalendarCloseButtonActionPerformed(evt);
            }
        });

        oneCalendarCloseButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        oneCalendarCloseButton.setText("Close");
        oneCalendarCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoCalendarCloseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addComponent(timeFromLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(213, 213, 213)
                                .addComponent(oneCalendarCloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(timeFromCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(timeToCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(timeToLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(139, 139, 139)))
                    .addComponent(twoCalendarCloseButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeFromLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeToLabel))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timeFromCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeToCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(twoCalendarCloseButton)
                    .addComponent(okButton)
                    .addComponent(oneCalendarCloseButton))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        //Get data and valid input if two-calendar version.        
        startDate.setTime(timeFromCalendar.getDate());
        if (!isBookmarkAddEditWindow) {
            endDate.setTime(timeToCalendar.getDate());

            //If the range is not valid, don't allow it to be updated.
            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this, "The start date cannot be"
                        + " after the end date.", "Date Selection Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        
       //Return output.
        if(isBookmarkAddEditWindow){
            dateFromPane.setText(CalendarFormatter.format(startDate, 
                    CalendarFormatter.DisplayFormat.DATE));
            timeFromPane.setText(CalendarFormatter.format(startDate, 
                    CalendarFormatter.DisplayFormat.TIME_12));
        } else {
            parent.setVisible(true);
            timeFromField.setText(CalendarFormatter.format(startDate, 
                    CalendarFormatter.DisplayFormat.DATE) + " "
                    + CalendarFormatter.format(startDate,
                    CalendarFormatter.DisplayFormat.TIME_12) + " "
                    + CalendarFormatter.format(startDate,
                    CalendarFormatter.DisplayFormat.TIME_ZONE));
            timeToField.setText(CalendarFormatter.format(endDate, 
                    CalendarFormatter.DisplayFormat.DATE) + " "
                    + CalendarFormatter.format(endDate,
                    CalendarFormatter.DisplayFormat.TIME_12) + " "
                    + CalendarFormatter.format(endDate,
                    CalendarFormatter.DisplayFormat.TIME_ZONE));
            if (parent instanceof AdvancedSearchBookmarkDialog) {
                AdvancedSearchBookmarkDialog casting = (AdvancedSearchBookmarkDialog) parent;
                casting.setStartTime(startDate);
                casting.setEndTime(endDate);
            }
        }
        
        //Store data for function calls by parent.
        timeFrom = startDate;
        timeTo = endDate;
        this.select = true;
        
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Gets the start date for the bookmark.
     * @return The start date.
     */
    public Calendar getStart() {
        return timeFrom;
    }
    
    /**
     * Gets the end date for the bookmark.
     * @return The end date.
     */
    public Calendar getEnd() {
        return timeTo;
    }
    
    /**
     * Sees if something was selected or not.
     * @return True if it was selected, false otherwise.
     */
    public boolean hasSelection() {
        return select;
    }
    
    private void twoCalendarCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoCalendarCloseButtonActionPerformed
         dispose();
         select = false;
         parent.setVisible(true);         
    }//GEN-LAST:event_twoCalendarCloseButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
         dispose();
         select = false;
         parent.setVisible(true);  
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton okButton;
    private javax.swing.JButton oneCalendarCloseButton;
    private weather.common.utilities.BUCalendar timeFromCalendar;
    private javax.swing.JLabel timeFromLabel;
    private weather.common.utilities.BUCalendar timeToCalendar;
    private javax.swing.JLabel timeToLabel;
    private javax.swing.JButton twoCalendarCloseButton;
    // End of variables declaration//GEN-END:variables

}
