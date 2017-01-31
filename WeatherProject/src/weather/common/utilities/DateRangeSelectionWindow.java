package weather.common.utilities;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import weather.common.gui.component.IconProperties;
import weather.common.data.resource.ResourceRange;

/**
 * Allows user to select start and end dates using a calendar.
 * @author Bloomsburg University Software Engineering
 * @author Jason Hunter (2009)
 * @version Spring 2009
 */
public class DateRangeSelectionWindow extends weather.common.gui.component.BUDialog {
    //There are two formats for the date label depending on whether or not the
    //user can select a time.
    private static final String dateFormatWithTime = "EEE. MMM. dd, yyyy hh:00 a z";
    private static final String dateFormatWithoutTime = "EEEE MMMM dd, yyyy";
    private static SimpleDateFormat simpleDateFormat;
    //This is to determine if the user clicked ok.  If they clicked ok
    //this is set to true.  If they clicked cancel or closed out the window
    //it is false.  This variable is only set to true in the function okButtonPressed()
    private static boolean okButtonPressed;
    //This variable determines if the wimdow is being used to set the range of a
    //weather station data plot.
    private static boolean isWeatherStation;
    //This variable determines if the wimdow is being used to set the range of
    //an instructor note or to give a range to make day-long videos.
    private static boolean isInstructorNoteOrVideoDownload;
    private static DateRangeSelectionWindow dateSelectionWindowSingleton;
    private static ResourceRange range,     //Currently shown range.
            resetRange;     //Range to which to reset if checkDate fails.
    
    
    /**
     * Private constructor for singleton.
     */
    private DateRangeSelectionWindow() {
        super();
        initComponents();
        int width = 779 + this.getInsets().left + this.getInsets().right;
        int height = 478 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        getRootPane().setDefaultButton(okButton);
        this.setModalityType(ModalityType.TOOLKIT_MODAL);
        this.setModal(true);
        
        fromCalendar.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!isWeatherStation && !isInstructorNoteOrVideoDownload) {
                    //Compute parameters for default movie length and date range.
                    int maxNumberOfDaysToLoad =
                            Integer.parseInt(PropertyManager.getGeneralProperty("maxNumberOfDaysToLoad"));
                    Calendar endRange = (Calendar)fromCalendar.getCalendar().clone();
                    endRange.add(Calendar.DATE, maxNumberOfDaysToLoad);
                    int defaultStartHours = Integer.parseInt(PropertyManager.getLocalProperty("DEFAULT_START_HOURS"));
                    Calendar endHour = (Calendar)fromCalendar.getCalendar().clone();
                    endHour.add(Calendar.HOUR, defaultStartHours);
                    toCalendar.setActiveRange(fromCalendar.getCalendar(), endRange, endHour);
                } else if(isWeatherStation) {
                    int defaultRangeEnd = Integer.parseInt(PropertyManager
                            .getLocalProperty("DEFAULT_WEATHER_STATION_SPAN"))
                            - 1;  //One less because all math is done at 12am.
                    Calendar endDay = (Calendar) fromCalendar.getCalendar().clone();
                    endDay.add(Calendar.DATE, defaultRangeEnd);
                    toCalendar.setActiveRange(fromCalendar.getCalendar(), endDay);
                } else {
                    toCalendar.setActiveRange(fromCalendar.getCalendar(), toCalendar.getCalendar());
                }
                fromDateLabel.setText(simpleDateFormat.format(fromCalendar.getDate()).toString()
                        .replaceAll("GMT", "UTC"));
                toDateLabel.setText(simpleDateFormat.format(toCalendar.getDate()).toString()
                        .replaceAll("GMT", "UTC"));
            }
            
        });
        
        toCalendar.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) { 
                toDateLabel.setText(simpleDateFormat.format(toCalendar.getDate()).toString()
                        .replaceAll("GMT", "UTC"));
            }
            
        });
    }

    /**
     * Sets the calendars and date labels to the supplied ResourceRange.
     * @param newRange The ResourceRange containing the start and ending dates.
     */
    private void setRange(ResourceRange newRange) {
        java.util.Date from = newRange.getStartTime();
        java.util.Date to = newRange.getStopTime();
        int maxNumberOfDaysToLoad =
                Integer.parseInt(PropertyManager.getGeneralProperty("maxNumberOfDaysToLoad"));

        //We want to set the calendars to the given resource range.
        fromCalendar.setTime(from);
        toCalendar.setTime(to);
        fromDateLabel.setText(simpleDateFormat.format(fromCalendar.getDate()).toString()
                .replaceAll("GMT", "UTC"));
        toDateLabel.setText(simpleDateFormat.format(toCalendar.getDate()).toString()
                .replaceAll("GMT", "UTC"));
         //Set Active Range
        if(!isWeatherStation && !isInstructorNoteOrVideoDownload){
            Calendar endRange = fromCalendar.getCalendar();
            endRange.add(Calendar.DATE, maxNumberOfDaysToLoad);
            toCalendar.setActiveRange(fromCalendar.getCalendar(), endRange, toCalendar.getCalendar());
        }
        else{
            toCalendar.setActiveRange(fromCalendar.getCalendar(), toCalendar.getCalendar());
        }
    }

    /**
     * Displays the date selection window and return the ResourceRange the user
     * has selected in the window.
     * @param initialRange The ResourceRange the calendars and date labels
     * should be set to upon opening.
     * @param timezone The time zone in which times should be displayed.
     * @param isWeatherStation Whether or not the resource range is for the
     * weather station.
     * @param isInstructorNoteOrVideoDownload Whether or not the resource range 
     * is for an instructor note or the download of day-long videos.
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     * @return newRange The ResourceRange containing the starting and ending
     * dates the user has selected in the date selection window.
     */
    public static ResourceRange getNewResourceRange(ResourceRange initialRange,
            TimeZone timezone, boolean isWeatherStation,
            boolean isInstructorNoteOrVideoDownload, boolean shouldCenter) {
        DateRangeSelectionWindow.isWeatherStation = isWeatherStation;
        DateRangeSelectionWindow.isInstructorNoteOrVideoDownload = isInstructorNoteOrVideoDownload;
        DateRangeSelectionWindow.resetRange = initialRange;
        ResourceRange newRange = null;
        if (display(initialRange, timezone, shouldCenter)) {
            newRange = new ResourceRange(range.getStartTime(), range.getStopTime());
        }
        return newRange;
    }

    /**
     * Displays the date selection window.
     * It returns a boolean value if the user pressed the OK button.
     * @param initialRange The ResourceRange containing the starting and
     * ending date the calendars should be set to upon opening.
     * @param timezome The time zone in which times should be displayed.
     * @return True if the user pressed the OK button, false if the user hit cancel
     * or closed the window.
     * @param shouldCenter True if this object should be centered on the
     * monitor that currently shows the window with focus; False if an offset
     * from the window with focus should be used.
     */
    private static boolean display(ResourceRange initialRange, 
            TimeZone timezone, boolean shouldCenter) {
        if (dateSelectionWindowSingleton == null) {
            dateSelectionWindowSingleton = new DateRangeSelectionWindow();
        }
        
        //Set time selection
        dateSelectionWindowSingleton
                .setTimeDisplayStyle(!isWeatherStation && !isInstructorNoteOrVideoDownload,
                timezone);

        dateSelectionWindowSingleton.setRange(initialRange);
        
        dateSelectionWindowSingleton.postInitialize(shouldCenter);
        return okButtonPressed;
    }
    
    /**
     * Set the way in which times are displayed.
     * @param isTimeChangeVisible Are drop down boxes and labels visible?
     * @param timezome The time zone in which times should be displayed.
     */
    private void setTimeDisplayStyle(boolean isTimeChangeVisible, TimeZone timezone) {
        fromCalendar.setTimeZone(timezone);
        toCalendar.setTimeZone(timezone);
        fromCalendar.setComboBoxVisible(isTimeChangeVisible);
        toCalendar.setComboBoxVisible(isTimeChangeVisible);
        if(isTimeChangeVisible) {
            simpleDateFormat = new SimpleDateFormat(dateFormatWithTime);
        } else {
            simpleDateFormat = new SimpleDateFormat(dateFormatWithoutTime);
        }
        simpleDateFormat.setTimeZone(timezone);
    }

    /**
     * Resets the DateSelectionWindow to the initial resource range.
     */
    private static void reset() {
        dateSelectionWindowSingleton.setRange(resetRange);
    }

    /**
     * This will create the ResourceRange that the user has chosen
     * in the DateSelectionWindow.
     *
     * @param start The start time in milliseconds.
     * @param end The end time in milliseconds.
     */
    private static void setResourceRange(long start, long end) {
        java.sql.Date fromDate = new java.sql.Date(start);
        java.sql.Date toDate = new java.sql.Date(end);
        range = new ResourceRange(fromDate, toDate);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        calendarPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        fromDateLabel = new javax.swing.JLabel();
        toDateLabel = new javax.swing.JLabel();
        fromCalendar = new weather.common.utilities.BUCalendar();
        toCalendar = new weather.common.utilities.BUCalendar();
        labelPanel = new javax.swing.JPanel();
        fromLabel = new javax.swing.JLabel();
        toLabel = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Weather Viewer - Date Selection");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        calendarPanel.setPreferredSize(new java.awt.Dimension(755, 386));
        calendarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        okButton.setText("OK");
        okButton.setToolTipText("Close window and view selected timeframe");
        okButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        okButton.setBorderPainted(false);
        okButton.setMaximumSize(new java.awt.Dimension(73, 23));
        okButton.setMinimumSize(new java.awt.Dimension(73, 23));
        okButton.setPreferredSize(new java.awt.Dimension(39, 21));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 13, 71, 23));

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Clicking cancel will not change the dates");
        cancelButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(132, 13, 71, 23));

        calendarPanel.add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(266, 313, 223, 49));

        fromDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fromDateLabel.setText(" ");
        fromDateLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        calendarPanel.add(fromDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(36, 0, 290, -1));

        toDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toDateLabel.setText(" ");
        toDateLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        calendarPanel.add(toDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(413, 0, 289, -1));
        calendarPanel.add(fromCalendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 27, -1, -1));
        calendarPanel.add(toCalendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 27, -1, -1));

        getContentPane().add(calendarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 80, -1, -1));

        labelPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fromLabel.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        fromLabel.setLabelFor(toCalendar);
        fromLabel.setText("Start Date");
        labelPanel.add(fromLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(114, 17, -1, -1));

        toLabel.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        toLabel.setText("End Date");
        labelPanel.add(toLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 17, -1, -1));

        getContentPane().add(labelPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 7, 743, -1));

        getAccessibleContext().setAccessibleParent(null);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * When the OK button is pressed, both calendar instances are saved.
     * Then the new resource range is set by passing the current from and to
     * dates (in milliseconds) to setResourceRangeMilliseconds in
     * ResourceTimeManger.  After the new resource range is set, a
     * call to updateDisplay(), and updateDateLabels() is made to update
     * the features in MainApplicationWindow.
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if(this.checkDate()){
            okButtonPressed = true;
            dateSelectionWindowSingleton.setVisible(false);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void showErrorPane(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Checks if the date given is a valid date. A valid date is a date which
     * is not greater than the current date and time or if one date is greater
     * than another date, i.e. if a beginning date is after the end date.
     * 
     * @return true if the date is valid, false if not valid.
     */
    private boolean checkDate() {
        Calendar from = fromCalendar.getCalendar();
        Calendar to = toCalendar.getCalendar();
        Calendar now = Calendar.getInstance();

        //This is the maximum number of days the user should be allowed to load.
        //It comes from a property file.
        //User is allowed to load more days than the property file specifies if
        //it is for the weather station.
        int maxNumberOfDaysToLoad =
                Integer.parseInt(PropertyManager.getGeneralProperty("maxNumberOfDaysToLoad"));
        
        //Check to make sure the start date is not past the current time.
        if (from.getTimeInMillis() > now.getTimeInMillis()) {
            showErrorPane("You can not choose an start date that is "
                    + "past the current time.", "Date Selection Error");
            reset();
            return false;
        }
        
        //Repeat to account for grace period, which is handled by the resource
        //time manager.
        else if (from.getTimeInMillis() > ResourceTimeManager.getDefaultRange()
                .getStopTime().getTime()) {
            showErrorPane("The infomation for the start date is not yat "
                    + "available.", "Date Selection Error");
            reset();
            return false;
        }

        //Check to make sure the ending date is not past the current time, 
        //unless it is for a weather station.
        else if (!isWeatherStation && to.getTimeInMillis() > now.getTimeInMillis()) {
            showErrorPane("You can not choose an end date that is "
                    + "past the current time.", "Date Selection Error");
            reset();
            return false;
        }
        
        //Repeat to account for grace period, which is handled by the resource
        //time manager.
        else if (!isWeatherStation && to.getTimeInMillis() > ResourceTimeManager
                .getDefaultRange().getStopTime().getTime()) {
            showErrorPane("The infomation for this range is not yat "
                    + "available.", "Date Selection Error");
            reset();
            return false;
        }
        
        //Check to make sure the starting date is not past the ending date.
        else if (from.getTimeInMillis() > to.getTimeInMillis()) {
            showErrorPane("The start date is past the end date.",
                    "Date Selection Error");
            reset();
            return false;
        }
        
        //Check to make sure the user is not trying to load more days
        //than allowed from the property file, unless it is for a weather station 
        //or instructor note.
        else if (!isWeatherStation && !isInstructorNoteOrVideoDownload 
                && (to.getTimeInMillis() - from.getTimeInMillis())
                > (maxNumberOfDaysToLoad * ResourceTimeManager.MILLISECONDS_PER_DAY)) {
            showErrorPane("You can not load more than " + maxNumberOfDaysToLoad + " days at once.", 
                    "Date Selection Error");
            reset();
            return false;
        }
        
        //Check to make sure the user did not pick the same date and time for 
        //both calendars, unless it is for a weather station or instructor note.
        else if(from.getTimeInMillis() == to.getTimeInMillis() && !isWeatherStation
                && !isInstructorNoteOrVideoDownload) {
            showErrorPane("The start and end date are exactly the same.", "Date Selection Error");
            reset();
            return false;
        }
        
        //Set range.
        setResourceRange(from.getTimeInMillis(), to.getTimeInMillis());
        return true;
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        okButtonPressed = false;
        dateSelectionWindowSingleton.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        okButtonPressed = false;
        dateSelectionWindowSingleton.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel calendarPanel;
    private javax.swing.JButton cancelButton;
    private weather.common.utilities.BUCalendar fromCalendar;
    private static javax.swing.JLabel fromDateLabel;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JButton okButton;
    private weather.common.utilities.BUCalendar toCalendar;
    private static javax.swing.JLabel toDateLabel;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables
}
