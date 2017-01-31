package weather.common.utilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

/**
 * Creates a GUI formed calendar with similar look and feel as of other
 * calendars. There are "magical numbers", however upon closer inspection, these
 * numbers are result of the time of day, days of a month, months, or even a
 * year.
 *
 * @author Alex Funk
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class BUCalendar extends javax.swing.JPanel {

    private JToggleButton[] dayButtons;
    //7 days to a week, maximum of 6 partial weeks to a month
    private final int NUMBER_OF_DAY_BUTTONS = 7*6;
    private int month;
    private int day;
    private int year;
    private int timeInHour;

    private int arrayIndexOfFirstOfMonth;   //Index of dayButtons labeled "1"
    private int daysInMonth;
    private Calendar calendar;
    private ButtonGroup dayButtonGroup;

    /**
     * Creates new form <code>BUCalendar</code> with all functionality enabled.
     */
    public BUCalendar() {
        this(true, true, true);
    }

    /**
     * Creates new form <code>BUCalendar</code> with options possibly disabled.
     * @param previousButtonsEnabled Are previous buttons enabled?
     * @param nextButtonsEnabled are next buttons enabled?
     * @param timeShowing Is the time-selection control visible?
     */
    public BUCalendar(boolean previousButtonsEnabled, boolean nextButtonsEnabled, boolean timeShowing) {
        initComponents();
        calendar = new GregorianCalendar();

        initializeDateAndTime();

        dayButtons = new JToggleButton[NUMBER_OF_DAY_BUTTONS];
        dayButtonGroup = new ButtonGroup();

        monthYearLabel.setText(getMonthName(month) + " " + year);
        
        timeComboBox.setSelectedIndex(timeInHour);
        timeComboBox.addPropertyChangeListener(new DropDownListoner()); 

        previousMonthButton.setEnabled(previousButtonsEnabled);
        previousYearButton.setEnabled(previousButtonsEnabled); 

        nextMonthButton.setEnabled(nextButtonsEnabled);
        nextYearButton.setEnabled(nextButtonsEnabled);
        
        timeComboBox.setVisible(timeShowing);
        
        updateCalendar();
    }
    
    /**
     * Sets the time zone of the instance.
     * @param zone The time zone.
     */
    public void setTimeZone(TimeZone zone) {
        calendar.setTimeZone(zone);
    }

    /**
     * Shows and hides time combo box.
     * @param isVisible Is combo box visible?
     */
    public void setComboBoxVisible(boolean isVisible){
        timeComboBox.setVisible(isVisible);
    }
    
    /**
     * Gets the number of days in the given month with year.
     *
     * @param month the month to be given
     * @param year the year to be given
     * @return the number of days in the month
     */
    private int getDaysInMonth(int month, int year) {
        if (month == 3 || month == 5 || month == 8 || month == 10) {
            return 30;
        } else if (month == 0 || month == 2 || month == 4 || month == 6
                || month == 7 || month == 9 || month == 11) {
            return 31;
        } else if (year % 4 == 0 && year % 100 != 0) {
            return 29;
        } else {
            return 28;
        }
    }

    /**
     * Initializes the date and time values from the given calendar object.
     */
    private void initializeDateAndTime() {
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);
        arrayIndexOfFirstOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1;  
        timeInHour = calendar.get(Calendar.HOUR_OF_DAY);
        daysInMonth = getDaysInMonth(this.month, this.year);
    }

    /**
     * Gets the month name from an integer value
     *
     * @param month the integer value of the month
     * @return a String of the month
     */
    private String getMonthName(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
            default:
                return "NULL";
        }
    }

    /**
     * Updates the panel according to the month and year.
     */
    private void updateCalendar() {
        //This calendar is used to calculate the new value of 
        //arrayIndexOfFirstOfMonth
        Calendar cal = (Calendar)calendar.clone();
        cal.set(Calendar.YEAR, this.year);
        cal.set(Calendar.MONTH, this.month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        arrayIndexOfFirstOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1; 
        
        this.monthYearLabel.setText(getMonthName(month) + " " + year);
        daysButtonPanel.removeAll();
        initializeDayButtons();
        addAllDayButtons();
    }
    
    private void addAllDayButtons(){
        for(JToggleButton jtb: dayButtons){
            daysButtonPanel.add(jtb);
        }
    }

    /**
     * Initializes the buttons associated with the month and year specified.
     */
    private void initializeDayButtons() {
        for (int i = 0; i < arrayIndexOfFirstOfMonth; i++) {
            dayButtons[i] = new JToggleButton();
            dayButtons[i].setEnabled(false);
            dayButtons[i].addActionListener(new DayListener());
            dayButtonGroup.add(dayButtons[i]);
        }
        for (int i = arrayIndexOfFirstOfMonth; i
                < (arrayIndexOfFirstOfMonth + daysInMonth); i++) {
            dayButtons[i] = new JToggleButton("" + (i - arrayIndexOfFirstOfMonth + 1));
            dayButtons[i].addActionListener(new DayListener());
            dayButtonGroup.add(dayButtons[i]);
        }
        for (int i = (arrayIndexOfFirstOfMonth + daysInMonth);
                i < NUMBER_OF_DAY_BUTTONS; i++) {
            dayButtons[i] = new JToggleButton();
            dayButtons[i].setEnabled(false);
            dayButtons[i].addActionListener(new DayListener());
            dayButtonGroup.add(dayButtons[i]);
        }
        dayButtons[arrayIndexOfFirstOfMonth + day - 1].setSelected(true);
    }

    /**
     * Sets the calendar with the specified date.
     *
     * @param date the date specified
     */
    public void setTime(Date date) {
        calendar.setTimeInMillis(date.getTime());
        initializeDateAndTime();
        timeComboBox.setSelectedIndex(timeInHour);
        updateCalendar();
        validate();
        this.setVisible(true);
    }

    /**
     * Sets the calendar to the given
     * <code>Calendar</code> and makes the window visible.
     *
     * @param c The new <code>Calendar</code> used by the window.
     */
    public void setDate(Calendar c) {
        calendar = (Calendar) c.clone();
        initializeDateAndTime();
        timeComboBox.setSelectedIndex(timeInHour);
        updateCalendar();
        validate();
        this.setVisible(true);
    }

    /**
     * Deactivates everything before the start date and after the end date with 
     * the given date selected.
     * @param start A <code>Calendar</code> representing the start date of the
     * active range.
     * @param end A <code>Calendar</code> representing the last valid date for 
     * the given start date.
     * @param date A <code>Calendar</code> representing the date to be selected  
     * on this object.
     */
    public void setActiveRange(Calendar start, Calendar end, Calendar date) {      
        setDate(date);

        //deactivate things outside of the maximum range
        end = verifyValidEnd(end);
        deactivateDayButtonsBefore(start);
        deactivateDayButtonsAfter(end);
    }
    
    /**
     * Deactivates everything before the start date with the given date selected
     * if it is after the start date.  Otherwise, the start date is selected.
     * @param start A <code>Calendar</code> representing the start date of the
     * active range.
     * @param date A <code>Calendar</code> representing the date to be selected  
     * on this object if after the start date.
     */
    public void setActiveRange(Calendar start, Calendar date) { 
        setDate(date.getTimeInMillis() > start.getTimeInMillis() ? date : start);
        deactivateDayButtonsBefore(start);
    }

    /**
     * Verifies whether the given day is past the current day and provides a 
     * valid end date.
     * @param end A <code>Calendar</code> representing the current end day of 
     * the range.
     * @return A <code>Calendar</code> representing a valid end date.
     */
    private Calendar verifyValidEnd(Calendar end) {
        Calendar today = new GregorianCalendar();
        if(today.before((Calendar)end)) {
            end=today;
        }

        return end;
    }

    /**
     * Deactivates all day buttons before the given day.
     * @param day A <code>Calendar</code> object representing the first day to
     * be active.
     */
    private void deactivateDayButtonsBefore(Calendar day) {
        int dayOfMonth = day.get(Calendar.DAY_OF_MONTH);
        int dayYear = day.get(Calendar.YEAR);
        int dayMonth = day.get(Calendar.MONTH);

        if (dayYear == year && dayMonth == month) {
            for (JToggleButton jtb : dayButtons) {
                if (!jtb.getText().equals("") && Integer.parseInt(jtb.getText()) < dayOfMonth) {
                    jtb.setEnabled(false);
                }
            }
        } else if ((month < dayMonth && dayYear == year) || year < dayYear) {
            for (JToggleButton jtb : dayButtons) {
                jtb.setEnabled(false);
            }
        }
    }

    /**
     * Deactivates all day buttons after the given day. 
     * @param day A <code>Calendar</code> object representing the last day to 
     * be active.
     */
    private void deactivateDayButtonsAfter(Calendar day) {
        int dayOfMonth = day.get(Calendar.DAY_OF_MONTH);
        int todayYear = day.get(Calendar.YEAR);
        int todayMonth = day.get(Calendar.MONTH);

        if (todayMonth == month && todayYear == year) {
            for (JToggleButton jtb : dayButtons) {
                if (!jtb.getText().equals("") && Integer.parseInt(jtb.getText()) > dayOfMonth) {
                    jtb.setEnabled(false);
                }
            }
        } else if ((month > todayMonth && todayYear == year) || year > todayYear) {
            for (JToggleButton jtb : dayButtons) {
                jtb.setEnabled(false);
            }
        }
    }

    /**
     * Gets the calendar specified from the GUI form.  Also updates stored calendar 
     * value to that which is depicted.
     *
     * @return a calendar object representing the values from the GUI
     */
    public Calendar getCalendar() {
        Calendar cal = (Calendar)calendar.clone();
        int dayDate = 0;
        for (int i = 0; i < NUMBER_OF_DAY_BUTTONS; i++) {
            if (dayButtons[i].isSelected()) {
                dayDate = (i + 1);
                break;
            }
        }
        day = dayDate - arrayIndexOfFirstOfMonth;
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, timeComboBox.getSelectedIndex());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        calendar = cal; //Calendar should be calculated value.

        return cal;
    }

    /**
     * Gets the date specified from the GUI.
     *
     * @return a date object representing the values from the GUI
     */
    public Date getDate() {
        return getCalendar().getTime();
    }

    public void deactivateNextButtons() {
        nextYearButton.setEnabled(false);
        nextMonthButton.setEnabled(false);
    }

    public void deactivatePreviousButtons() {
        previousYearButton.setEnabled(false);
        previousMonthButton.setEnabled(false);
    }

    //These are listeners followed by the method that fires events
    class DayListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            firePropertyChange("Day Changed");
        }
    }
    
    class DropDownListoner implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            firePropertyChange("Time Changed");
        }
        
    }

    public void firePropertyChange(String name) {
        Calendar storedCalendar = (Calendar) calendar.clone();
        PropertyChangeEvent event = new PropertyChangeEvent(this, name, storedCalendar, getCalendar());
        firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        previousYearButton = new javax.swing.JButton();
        previousMonthButton = new javax.swing.JButton();
        monthYearLabel = new javax.swing.JLabel();
        nextMonthButton = new javax.swing.JButton();
        nextYearButton = new javax.swing.JButton();
        daysButtonPanel = new javax.swing.JPanel();
        dayNamePanel = new javax.swing.JPanel();
        sundayLabel = new javax.swing.JLabel();
        mondayLabel = new javax.swing.JLabel();
        tuesdayLabel = new javax.swing.JLabel();
        wednesdayLabel = new javax.swing.JLabel();
        thursdayLabel = new javax.swing.JLabel();
        fridayLabel = new javax.swing.JLabel();
        saturdayLabel = new javax.swing.JLabel();
        timeComboBox = new javax.swing.JComboBox();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        previousYearButton.setText("<<");
        previousYearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousYearButtonActionPerformed(evt);
            }
        });

        previousMonthButton.setText("<");
        previousMonthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousMonthButtonActionPerformed(evt);
            }
        });

        monthYearLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        nextMonthButton.setText(">");
        nextMonthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextMonthButtonActionPerformed(evt);
            }
        });

        nextYearButton.setText(">>");
        nextYearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextYearButtonActionPerformed(evt);
            }
        });

        daysButtonPanel.setLayout(new java.awt.GridLayout(6, 7, 1, 1));

        dayNamePanel.setLayout(new java.awt.GridLayout(1, 7));

        sundayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sundayLabel.setText("Sun");
        sundayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(sundayLabel);

        mondayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mondayLabel.setText("Mon");
        mondayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(mondayLabel);

        tuesdayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tuesdayLabel.setText("Tue");
        tuesdayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(tuesdayLabel);

        wednesdayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wednesdayLabel.setText("Wed");
        wednesdayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(wednesdayLabel);

        thursdayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        thursdayLabel.setText("Thu");
        thursdayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(thursdayLabel);

        fridayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fridayLabel.setText("Fri");
        fridayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(fridayLabel);

        saturdayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saturdayLabel.setText("Sat");
        saturdayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dayNamePanel.add(saturdayLabel);

        timeComboBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        timeComboBox.setMaximumRowCount(5);
        timeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12 AM", "  1 AM", "  2 AM", "  3 AM", "  4 AM", "  5 AM", "  6 AM", "  7 AM", "  8 AM", "  9 AM", "10 AM", "11 AM", "12 PM", "  1 PM", "  2 PM", "  3 PM", "  4 PM", "  5 PM", "  6 PM", "  7 PM", "  8 PM", "  9 PM", "10 PM", "11 PM" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(previousYearButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(previousMonthButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(monthYearLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextMonthButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextYearButton))
                            .addComponent(dayNamePanel, 0, 0, Short.MAX_VALUE)
                            .addComponent(daysButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(timeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previousMonthButton)
                    .addComponent(previousYearButton)
                    .addComponent(monthYearLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextYearButton)
                    .addComponent(nextMonthButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dayNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(daysButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(timeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void previousMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousMonthButtonActionPerformed
        this.month = this.month - 1;
        if (this.month < 0) {
            this.month = 11;
            this.year = this.year - 1;
        }
        daysInMonth = getDaysInMonth(this.month, this.year);
        if (this.day > daysInMonth) {
            this.day = daysInMonth;
        }
        updateCalendar();
        firePropertyChange("Previus Month");
    }//GEN-LAST:event_previousMonthButtonActionPerformed

    private void previousYearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousYearButtonActionPerformed
        this.year = this.year - 1;
        daysInMonth = getDaysInMonth(this.month, this.year);
        if (this.day > daysInMonth) {
            this.day = daysInMonth;
        }
        updateCalendar();
        firePropertyChange("Previous Year");
    }//GEN-LAST:event_previousYearButtonActionPerformed

    private void nextMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMonthButtonActionPerformed
        this.month = this.month + 1;
        if (this.month > 11) {
            this.month = 0;
            this.year = this.year + 1;
        }
        daysInMonth = getDaysInMonth(this.month, this.year);
        if (this.day > daysInMonth) {
            this.day = daysInMonth;
        }
        updateCalendar();
        firePropertyChange("Next Month");
    }//GEN-LAST:event_nextMonthButtonActionPerformed

    private void nextYearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextYearButtonActionPerformed
        this.year = this.year + 1;
        daysInMonth = getDaysInMonth(this.month, this.year);
        if (this.day > daysInMonth) {
            this.day = daysInMonth;
        }
        updateCalendar();
        firePropertyChange("Next Year");
    }//GEN-LAST:event_nextYearButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dayNamePanel;
    private javax.swing.JPanel daysButtonPanel;
    private javax.swing.JLabel fridayLabel;
    private javax.swing.JLabel mondayLabel;
    private javax.swing.JLabel monthYearLabel;
    private javax.swing.JButton nextMonthButton;
    private javax.swing.JButton nextYearButton;
    private javax.swing.JButton previousMonthButton;
    private javax.swing.JButton previousYearButton;
    private javax.swing.JLabel saturdayLabel;
    private javax.swing.JLabel sundayLabel;
    private javax.swing.JLabel thursdayLabel;
    private javax.swing.JComboBox timeComboBox;
    private javax.swing.JLabel tuesdayLabel;
    private javax.swing.JLabel wednesdayLabel;
    // End of variables declaration//GEN-END:variables
}
