package weather.clientside.gui.administrator;

import java.awt.Dimension;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.ListCellDateRenderer;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSStationManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.PageChecker;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WebGradingTools;

/**
 * This is a form to determine the <code>Date</code> and <code>Station</code> 
 * for which an <code>InstructorResponse</code> will be given. The
 * <code>ForecasterLesson</code> will have already been chosen and provided to
 * this form.
 * 
 * @author Brian Bankes
 */
public class SelectInstructorResponseDateAndStationDialog extends BUDialog {
    
    //The provided forecasting lesson.
    private ForecasterLesson lesson;
    
    //For selection of date.
    
    //Base URL to test.
    private final static String UCRA_DATA_URL = PropertyManager
            .getGeneralProperty("UCRA_DATA_URL");
    
    //Constants to determins which dates to show.
    private final static int MINIMUM_GRADING_DAYS = Integer.parseInt(
            PropertyManager.getGeneralProperty("MINIMUM_GRADING_DAYS"));
    private final static int MAXIMUM_GRADING_DAYS = Integer.parseInt(
            PropertyManager.getGeneralProperty("MAXIMUM_GRADING_DAYS"));
    
    //Model for list box.
    private DefaultListModel<Date> dateModel;
    
    //The format with which to show the dates.
    private String dateFormatString 
            = PropertyManager.getGeneralProperty("dateFormatString");
    
    //Renderer for the list box.
    private ListCellDateRenderer renderer
            = new ListCellDateRenderer(dateFormatString);
    
    //For the selection of the station code.
    
    //Message to select a state for station box.
    private final String stateMessage = "<Please select a state.>";
    
    //Database manager.
    private final DBMSStationManager stationManager;
    
    //List to load state combovox.
    private String[] states;

    /**
     * Creates new form SelectInstructorResponseDateAndStationDialog. (NOTE: 
     * This constructor will NOT show the form; see showFormOrNoDatesMessage().)
     * 
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     * @param lesson The <code>ForecasterLesson</code> for which the
     * <code>Date</code> and <code>Station</code> are being selected.
     */
    public SelectInstructorResponseDateAndStationDialog(
            ApplicationControlSystem appControl, ForecasterLesson lesson) {
        super(appControl);
        
        this.lesson = lesson;
        stationManager = appControl.getDBMSSystem().getStationManager();
        
        states = (new String[] {"Select a State", "ALABAMA (AL) ", "ARKANSAS (AR)  ", 
            "ARIZONA (AZ) ", "CALIFORNIA (CA) ", "COLORADO (CO) ", "CONNECTICUT (CT)  ", 
            "DELAWARE (DE)  ", "FLORIDA (FL)  ", "GEORGIA (GA) ", "IDAHO (ID) ", 
            "ILLINOIS (IL) ", "INDIANA (IN)  ", "IOWA (IA)  ", "KANSAS (KS)  ", 
            "KENTUCKY (KY)  ", "LOUISIANA (LA)  ", "MAINE (ME)  ", "MARYLAND (MD)  ", 
            "MASSACHUSETTS (MA)  ", "MICHIGAN (MI)  ", "MINNESOTA (MN)  ", 
            "MISSISSIPPI (MS)  ", "MISSOURI (MO) ", "MONTANA (MT)  ", "NEBRASKA (NE)  ", 
            "NEVADA (NV)  ", "NEW HAMPSHIRE (NH)  ", "NEW JERSEY (NJ)  ", 
            "NEW MEXICO (NM)  ", "NEW YORK (NY)  ", "NORTH CAROLINA (NC)  ", 
            "NORTH DAKOTA (ND)  ", "OHIO (OH)  ", "OKLAHOMA (OK)  ", "OREGON (OR)  ", 
            "PENNSYLVANIA (PA)  ", "RHODE ISLAND (RI) ", "SOUTH CAROLINA (SC)  ", 
            "SOUTH DAKOTA (SD)  ", "TENNESSEE (TN)  ", "TEXAS (TX)  ", "UTAH (UT)  ", 
            "VERMONT (VT)  ", "VIRGINIA (VA)  ", "WASHINGTON (WA) ", "WISCONSIN (WI) ", 
            "WEST VIRGINIA (WV)  ", "WYOMING (WY)" });
        
        initComponents();
        
        lblCurrentLesson.setText("Selected Lesson: " 
                + lesson.getLessonName());
        
        //Set date list.
        dateModel = new DefaultListModel<>();
        
        //Get start and end dates for available date search as calendars.
        //NOTE: This feature uses the local time zome.
        Calendar startDateCal = ResourceTimeManager
                .getStartOfDayCalendarFromMilliseconds(lesson
                .getLessonStartDate().getTime(), TimeZone.getDefault());
        
        Calendar endDateCal = ResourceTimeManager
                .getStartOfDayCalendarFromMilliseconds(lesson
                .getLessonEndDate().getTime(), TimeZone.getDefault());
        
        //Compute the start of the most resent day that we want to grade.
        long startOfLastGradableDayInMills = ResourceTimeManager
                .getStartOfDayFromMilliseconds(System.currentTimeMillis(), 
                TimeZone.getDefault()) - MINIMUM_GRADING_DAYS
                * ResourceTimeManager.MILLISECONDS_PER_DAY;
        
        
        //Set the last day for the given lesson.
        if (endDateCal.getTimeInMillis() > startOfLastGradableDayInMills) {
            endDateCal.setTimeInMillis(startOfLastGradableDayInMills);
        }
        
        //Compute the start of the oldest day for which web grading is possible.
        long startOfWebGradingRangeInMills = ResourceTimeManager
                .getStartOfDayFromMilliseconds(System.currentTimeMillis(), 
                TimeZone.getDefault()) - MAXIMUM_GRADING_DAYS
                * ResourceTimeManager.MILLISECONDS_PER_DAY;
        
        Calendar loopCal = (Calendar)startDateCal.clone();
        while (loopCal.getTimeInMillis() <= endDateCal.getTimeInMillis()) {
            if (loopCal.getTimeInMillis() < startOfWebGradingRangeInMills) {
                //Add all days before grading range.  If the web page in not
                //present, the instructor car provide responses.
                dateModel.addElement(new Date(loopCal.getTimeInMillis())); 
            } else {
                //Add date to list if web page is present.
                String pageToCheck = UCRA_DATA_URL + WebGradingTools
                        .getFormattedDate(loopCal) + ".out";

                if (PageChecker.doesPageExist(pageToCheck)) {
                    dateModel.addElement(new Date(loopCal.getTimeInMillis()));
                }
            }
            
            //Prepare for next loop.
            loopCal.add(Calendar.DATE, 1);
        }
        
        lstViewDates.setModel(dateModel);
        lstViewDates.setCellRenderer(renderer);
        
        //Select station if specified by lesson.
        if (lesson.getStationCode() != null) {
            Station station = stationManager.obtainStation(lesson
                    .getStationCode());
            
            //Find and set state.
            for (int i = 1; i < states.length; i++) {
                String state = states[i];
                int position = state.indexOf("(");
                state = state.substring(position + 1, position + 3);
                if (station.getState().equals(state)) {
                    stateComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            stationComboBox.setSelectedItem(station.getStationName() + " ("
                    + station.getStationId() + ")");
            stateComboBox.setEnabled(false);
            stationComboBox.setEnabled(false); 
        } else {
            //Show that the user must select a state.
            stationComboBox.addItem(stateMessage);
            stationComboBox.setEnabled(false);
        }
        
        int width = 417 + this.getInsets().left + this.getInsets().right;
        int height = 218 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        //super.postInitialize(); //Done after ensuring there are dates to list.
    }  
    
    /**
     * This method determines if the form can be shown or if there are no 
     * available dates.  It should be called after the constructor an the
     * constructor will no show the form.  If this method does not show the
     * form, a dialog will tell the user there are no dates.
     */
    public void showFormOrNoDatesMessage() {
        if (dateModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "There are no available dates for which responses\n"
                    + "can be supplied.", "No Dates Available", 
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            super.postInitialize(false);
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

        dateScrollPane = new javax.swing.JScrollPane();
        lstViewDates = new javax.swing.JList<Date>();
        lblCurrentLesson = new javax.swing.JLabel();
        lblAvailableDates = new javax.swing.JLabel();
        stationComboBox = new javax.swing.JComboBox<String>();
        lblSelectState = new javax.swing.JLabel();
        lblSelectStation = new javax.swing.JLabel();
        stateComboBox = new javax.swing.JComboBox<String>();
        btnSelect = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setTitle("Select Weather Station and Date");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lstViewDates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dateScrollPane.setViewportView(lstViewDates);

        getContentPane().add(dateScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 76, 102, 130));

        lblCurrentLesson.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblCurrentLesson.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(lblCurrentLesson, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 393, 16));

        lblAvailableDates.setText("<html><b>Available Dates</b></html>");
        getContentPane().add(lblAvailableDates, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 48, 102, 16));
        getContentPane().add(stationComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 138, 271, 22));

        lblSelectState.setText("<html><b>Select a State</b></html>");
        getContentPane().add(lblSelectState, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 48, 92, 16));

        lblSelectStation.setText("<html><b>Select a Weather Station</b></html>");
        getContentPane().add(lblSelectStation, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 110, 164, 16));

        stateComboBox.setModel(new javax.swing.DefaultComboBoxModel(states));
        stateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stateComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(stateComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 76, 271, 22));

        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        getContentPane().add(btnSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 172, 67, 25));

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(213, 172, 71, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stateComboBoxActionPerformed
        //Clear box and keep empty if no state is picked.
        stationComboBox.removeAllItems();
        stationComboBox.setEnabled(true);
        if(stateComboBox.getSelectedIndex() == 0) {
            stationComboBox.addItem(stateMessage);
            stationComboBox.setEnabled(false);
            return;
        }
        
        //Get stations for selected state.
        String state = stateComboBox.getSelectedItem().toString().trim();
        int position = state.indexOf("(");
        state = state.substring(position + 1, position + 3);
        Vector<Station> stations = stationManager.getAllStationsByState(state);
        
        //Load station box.
        for (Station thisStation : stations) {
            stationComboBox.addItem(thisStation.getStationName() + " ("
                    + thisStation.getStationId() + ")");
        }
    }//GEN-LAST:event_stateComboBoxActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        //Check to see if data was supplied.
        if(lstViewDates.getSelectedValuesList().isEmpty()
                && stateComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "Plese select a weather station and a date.", 
                    "Please Supply Data", JOptionPane.INFORMATION_MESSAGE);
        } else if (lstViewDates.getSelectedValuesList().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Plese select a date.",
                    "Please Supply Data", JOptionPane.INFORMATION_MESSAGE);
        } else if(stateComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "Plese select a weather station.", 
                    "Please Supply Data", JOptionPane.INFORMATION_MESSAGE);
        } else {
            //Input is good, so get data.
            String stationString = stationComboBox.getSelectedItem().toString();
            int endIndex = stationString.lastIndexOf("(") - 1;
            stationString = stationString.substring(0, endIndex);
            Station station = stationManager.obtainStation(stationString);
            Date date = lstViewDates.getSelectedValuesList().get(0);
            
            //Check if there are attempts to be graded or regraded.
            ArrayList<Attempt> attemptsToGrade = new ArrayList<>();
            ArrayList<Attempt> allLessonAttempts = appControl.getDBMSSystem()
                    .getForecasterAttemptManager().getAttempts(lesson);

            for (Attempt attempt : allLessonAttempts) {
                //Check station and date.
                if (attempt.getForecastedDate().getTimeInMillis() == date.getTime()
                        && attempt.getStationCode().equals(station.getStationId())) {
                    attemptsToGrade.add(attempt);
                }
            }
            
            //Check if there are attempts to grade.  If not, alert user and 
            //return.
            
            if(attemptsToGrade.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "There are no attemps for the selected weather station\n"
                        + "and date fon this lesson, so no responses need to be\n"
                        + "be provided.", "No Attempts Found", JOptionPane.
                        INFORMATION_MESSAGE);
                return;
            }
            
            //Variable to check if web data has been retrieved.
            boolean isDataPresent;
            
           //Variable to check if the attempts have beeen graded before.
            boolean isRegrading;
            
            //Test if web data is present.
            Calendar dateToCheck = Calendar.getInstance();
            dateToCheck.setTimeInMillis(date.getTime());
            long daysOld = (ResourceTimeManager
                    .getStartOfDayFromMilliseconds(System.currentTimeMillis(),
                    TimeZone.getDefault()) - dateToCheck.getTimeInMillis())
                    / ResourceTimeManager.MILLISECONDS_PER_DAY;
            if (daysOld <= MAXIMUM_GRADING_DAYS) {
                //Date is only available if web page exists.
                isDataPresent = true;
            } else { //past MAXIMUM_GRADING_DAYS in check for web data.
                //Check if data is already in database.
                isDataPresent = appControl.getDBMSSystem().
                        getForecasterStationDataManager()
                        .getStation(station.getStationId(),
                        new Date(dateToCheck.getTimeInMillis())) != null;
            }
            
            //Check for regrading; assume so until an ungraded attempt is found.
            isRegrading = true;
            for (Attempt attempt : attemptsToGrade) {
                if (!attempt.hasBeenGraded()) {
                    isRegrading = false;
                    break;
                }
            }
            
            //Tell user if all answer must be provided.
            if (!isDataPresent) {
                JOptionPane.showMessageDialog(this,
                        "The grading information from the web is missing for\n"
                        + "the selected date.  Before grading, an answer must\n"
                        + "be provided for every question.", "Web Data Missing", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (!isRegrading){
                //Tell user attempts have yet to be graded.
                JOptionPane.showMessageDialog(this,
                        "The student attempts for this date and station have\n"
                        + "yet to be graded.  If you close the next form\n"
                        + "without clicking on \"Grade Assignment\", thay\n"
                        + "will remain ungraded.", "Ungraded Student Attempts",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            
            //Show next form.
            dispose();
            new InstructorResonseEntryForm(appControl, attemptsToGrade, 
                    isDataPresent, lesson, station, date, isRegrading);
        }       
    }//GEN-LAST:event_btnSelectActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSelect;
    private javax.swing.JScrollPane dateScrollPane;
    private javax.swing.JLabel lblAvailableDates;
    private javax.swing.JLabel lblCurrentLesson;
    private javax.swing.JLabel lblSelectState;
    private javax.swing.JLabel lblSelectStation;
    private javax.swing.JList<Date> lstViewDates;
    private javax.swing.JComboBox<String> stateComboBox;
    private javax.swing.JComboBox<String> stationComboBox;
    // End of variables declaration//GEN-END:variables
}
