package weather.clientside.gui.administrator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.ChooseDate;
import weather.clientside.utilities.JTextFieldLimit;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.Course;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.Instructions;
import weather.common.data.forecasterlesson.MissingWebGradingDataRecord;
import weather.common.data.forecasterlesson.PointScale;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.dbms.DBMSForecasterQuestionTemplateManager;
import weather.common.dbms.DBMSMissingDataRecordManager;
import weather.common.dbms.DBMSStationManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;

/**
 * This window is used by the instructor to create or edit a newLesson for their
 * students to take. The instructor will be able to set the name, start and end
 * date for the newLesson, the station code of the weather station to be used,
 * the points for each question, the maximum amount of times the students can
 * attempt the newLesson, and the maximum amount of tries to be graded. After
 * this is done, the instructor can either use the default set of instructions
 * or they can change the instructions that their students will see prior to
 * taking the newLesson. The questions for this newLesson are displayed in the
 * final tab for the instructors reference.
 *
 * Note: This form should only be created by an instance of
 * <code>InstructorLessonManager</code>.
 *
 * @author Joshua Whiteman
 * @author Brandon True
 * @author Xiang Li
 */
public class InstructorLessonCreator extends BUDialog {

    private final DBMSCourseManager courseManager;
    private final DBMSForecasterLessonManager lessonManager;
    private final DBMSForecasterQuestionTemplateManager questionTemplateManager;
    private final DBMSMissingDataRecordManager missingDataRecordManager;
    private final DBMSStationManager stationManager;
    private ForecasterLesson lesson;
    private final ArrayList<Question> questionArray;
    private ArrayList<Course> courses;
    private Date startDate = null;
    private Date endDate = null;
    private final String lessonCreatorInstructions = setInstructions();
    private Instructions instructions;
    private Vector<Station> stations;
    private DefaultListModel<String> listModel;
    private final ItemListener comboBoxListener;
    private Course course;
    private boolean edit;
    private DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
    
    //To check for changes
    private String originalLessonName;
    private String originalStartDate;
    private String originalEndDate;
    private String originalPointsPerQuestion;
    private String originalAttemptedPoints;
    private String originalUnansweredPoints;
    private String originalMaximumAttempts;
    private String originalTriesCounted;
    private String originalStationCode;
    private boolean originalRequireAnswers;
    
    //For missing data attempts table.
    private MyDefaultTableModel missingDataModel;
    private DateFormat tableDateFormat = new SimpleDateFormat("MMM. dd, yyyy");;
    
    //True once the form is initialized.
    private boolean formInitialized = false;
    
    //Dimensions (large will show help, small will not).
    private final Dimension smallDim;
    private final Dimension largeDim;
    
    //True if help is showing, false otherwise.
    private boolean isHelpShowing;
    
    //The screen location of the form that created this object.
    private final Point parentPosition;

    /**
     * Helper function to store data from last save in storage variables.
     */
    private void storeResetData(){
        originalLessonName = this.lessonNameText.getText().trim();
        originalStartDate = this.startDateText.getText().trim();
        originalEndDate = this.endDateText.getText().trim();
        originalPointsPerQuestion = this.pointsPerQuestionText.getText().trim();
        originalAttemptedPoints = this.attemptedPointsText.getText().trim();
        originalUnansweredPoints = this.unansweredPointsText.getText().trim();
        originalMaximumAttempts = this.maximumAttemptsText.getText().trim();
        originalTriesCounted = this.triesCountedText.getText().trim();
        originalStationCode = this.stationCodeText.getText().trim();
        originalRequireAnswers = this.requireAnswersCheckBox.isSelected();
    }
    
    /**
     * Helper function to see if data has been changed from that in the storage
     * variables.
     * @return True if there have been changes, false otherwise.
     */
    private boolean changed() {
        if (!lessonNameText.getText().trim().equals(originalLessonName)) {
            return true;
        }
        if (!startDateText.getText().trim().equals(originalStartDate)) {
            return true;
        }
        if (!endDateText.getText().trim().equals(originalEndDate)) {
            return true;
        }
        if (!pointsPerQuestionText.getText().trim().equals(originalPointsPerQuestion)) {
            return true;
        }
        if (!attemptedPointsText.getText().trim().equals(originalAttemptedPoints)) {
            return true;
        }
        if (!unansweredPointsText.getText().trim().equals(originalUnansweredPoints)) {
            return true;
        }
        if (!maximumAttemptsText.getText().trim().equals(originalMaximumAttempts)) {
            return true;
        }
        if (!triesCountedText.getText().trim().equals(originalTriesCounted)) {
            return true;
        }
        if (!stationCodeText.getText().trim().equals(originalStationCode)) {
            return true;
        }
        if (requireAnswersCheckBox.isSelected() != originalRequireAnswers) {
            return true;
        }
        return false;
    }

    /**
     * Helper function to replace this screen with an updated
     * <code>InstructorLessonManager</code>.
     */
    private void showLessonManager(){
        dispose();
        new InstructorLessonManager(appControl, parentPosition);
    }
    
    /**
     * Helper function called when the user attempts to chose the form.  It
     * checks to save any unsaved data and then show an updated
     * <code>InstructorLessonManager</code>.
     */
    private void checkClose() {
        if (!changed()) {
            showLessonManager();
        } else if (appControl.getGeneralService().leaveWithoutSaving(this) 
                == true) {
            showLessonManager();
        }
    }
    
    /**
     * This constructor creates a new window for the instructor to create a 
     * newLesson with.  This window is called when an instructor wants to create
     * a new newLesson.
     *
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     * @param course The course for the newLesson to be created for.
     * @param parentPosition The screen location of the 
     * <code>InstructorLessonManager</code> that created this object.
     */
    public InstructorLessonCreator(ApplicationControlSystem appControl,
            Course course, Point parentPosition) {
        super(appControl);
        this.setModal(true);
        this.setTitle("Weather Viewer - Create a Forecasting Lesson");
        lesson = null;
        this.course = course;
        this.parentPosition = parentPosition;
        lessonManager = appControl.getDBMSSystem().getForecasterLessonManager();
        courseManager = appControl.getDBMSSystem().getCourseManager();
        stationManager = appControl.getDBMSSystem().getStationManager();
        questionTemplateManager = appControl.getDBMSSystem().getForecasterQuestionTemplateManager();
        missingDataRecordManager = null;
        
        //TODO: Let the user change this array.
        questionArray = questionTemplateManager.getLessonTemplate();
        
        comboBoxListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    updateStationCodeList();
                }   
            }
        };
        
        initComponents();
        this.createLessonTabbedPane.setEnabledAt(3, false);
        lessonNameText.setDocument(new JTextFieldLimit(30));
        
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.append(lessonCreatorInstructions.toString());

        questionsTextArea.setWrapStyleWord(true);
        questionsTextArea.setLineWrap(true);

        Iterator<Question> questionIterator = questionArray.iterator();
        while (questionIterator.hasNext()) {
            String concat = questionIterator.next().getQuestionText();
            questionsTextArea.append(concat + "\n\n");
        }
        
        stateComboBox.addItemListener(comboBoxListener);
        initialize();
        storeResetData();
        
        //Set scroll panes to top.
        instructionsTextArea.setCaretPosition(0);
        questionsTextArea.setCaretPosition(0);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        formInitialized = true;
        
        //Set Dimension variables.
        int width = 636 + this.getInsets().left + this.getInsets().right;
        int widthWithHelp = 1000 + this.getInsets().left + this.getInsets().right; 
        int height = 627 + this.getInsets().top + this.getInsets().bottom;
        smallDim = new Dimension(width, height);
        largeDim = new Dimension(widthWithHelp, height);
        
        //Set to small size.
        this.setSize(smallDim);
        this.setPreferredSize(smallDim);
        this.setMaximumSize(smallDim);
        this.setMinimumSize(smallDim);
        pack();
        isHelpShowing = false;
        
        super.postInitialize(false);
    }
    
    /**
     * This constructor creates a new window for the instructor to edit a
     * newLesson that already exists. An instance of the newLesson is passed to
     * this constructor to display all of the previously created data and allow
     * the instructor to change whatever data they would like.
     *
     * @param appControl The program's ApplicationControlSystem.
     * @param lesson An instance of the newLesson that is to be edited.
     * @param parentPosition The screen location of the
     * <code>InstructorLessonManager</code> that created this object.
     */
    public InstructorLessonCreator(ApplicationControlSystem appControl,
            ForecasterLesson lesson, Point parentPosition) {
        super();
        this.appControl = appControl;
        this.setTitle("Weather Viewer - Edit a Forecasting Lesson");
        this.lesson = lesson;
        this.parentPosition = parentPosition;
        edit = true;
        lessonManager = appControl.getDBMSSystem().getForecasterLessonManager();
        courseManager = appControl.getDBMSSystem().getCourseManager();
        stationManager = appControl.getDBMSSystem().getStationManager();
        questionTemplateManager = appControl.getDBMSSystem().getForecasterQuestionTemplateManager();
        missingDataRecordManager = appControl.getDBMSSystem().getMissingDataRecordManager();
        
        //TODO: Let the user change this array.
        questionArray = questionTemplateManager.getLessonTemplate();
        
        comboBoxListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    updateStationCodeList();
                }    
            }
        };
        
        initComponents();
        createButton.setText("Save Changes");

        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.append(lesson.getInstructions().getInstructionsText());

        questionsTextArea.setWrapStyleWord(true);
        questionsTextArea.setLineWrap(true);
        
        //Set up missing data table.
        missingDataModel = new MyDefaultTableModel(0, 0);
        missingDataTable.setModel(missingDataModel);
        missingDataTable.addMouseListener(tableMouseAdapter);

        Iterator<Question> questionIterator = questionArray.iterator();
        while (questionIterator.hasNext()) {
            String concat = questionIterator.next().getQuestionText();
            questionsTextArea.append(concat + "\n\n");
        }
        
        stateComboBox.addItemListener(comboBoxListener);
        editLessonInitialize();
        storeResetData();
        updateMissingDataTable();
        
        //Set scroll panes to top.
        instructionsTextArea.setCaretPosition(0);
        questionsTextArea.setCaretPosition(0);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                checkClose();
            }
        });
        
        formInitialized = true;
       
         //Set Dimension variables.
        int width = 636 + this.getInsets().left + this.getInsets().right;
        int widthWithHelp = 1000 + this.getInsets().left + this.getInsets().right; 
        int height = 627 + this.getInsets().top + this.getInsets().bottom;
        smallDim = new Dimension(width, height);
        largeDim = new Dimension(widthWithHelp, height);
        
        //Set to small size.
        this.setSize(smallDim);
        this.setPreferredSize(smallDim);
        this.setMaximumSize(smallDim);
        this.setMinimumSize(smallDim);
        pack();
        isHelpShowing = false;
        
        super.postInitialize(false);
    }
    
    /**
     * Returns this object as a <code>Component</code> for use by inner classes.
     * 
     * @return This object as a <code>Component</code>.
     */
    private Component thisComponent() {
        return this;
    }
    
    /**
     * Mouse adaptor for missing data table.
     */
    MouseAdapter tableMouseAdapter = new MouseAdapter() {
        /**
         * Checks to see if the mouse was clicked.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //Check for double-click.
            if (e.getClickCount() != 2) {
                return;
            }
            
            //Get date of selected row.
            String dateString = missingDataTable.getValueAt(missingDataTable
                    .getSelectedRow(), 0).toString();
            Date date;
            try {
                date = tableDateFormat.parse(dateString);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(thisComponent(), 
                        "Unable to parse date.", "Parsing Error", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            //Get weather station of selected row.
            String stationString = missingDataTable.getValueAt(missingDataTable
                    .getSelectedRow(), 1).toString();
            
            Station station;
            station = stationManager.obtainStation(stationString);
            if (station == null) {
                JOptionPane.showMessageDialog(thisComponent(), 
                        "Unable to find station.", "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            //Get all attempts for this row and check if any are ungraded.
            boolean isRegrading = true; //Assume no ungraded attempts.
            ArrayList<Attempt> attemptsToGrade = new ArrayList<>();
            ArrayList<Attempt> allLessonAttempts = appControl.getDBMSSystem()
                    .getForecasterAttemptManager().getAttempts(lesson);

            for (Attempt attempt : allLessonAttempts) {
                //Check station and date.
                if (attempt.getForecastedDate().getTimeInMillis() == date.getTime()
                        && attempt.getStationCode().equals(station.getStationId())) {
                    attemptsToGrade.add(attempt);
                    //Check if regrading flag should be false (if is isn't so fsr)/
                    if (isRegrading && !attempt.hasBeenGraded()) {
                        isRegrading = false;
                    }
                }
            }

            if (attemptsToGrade.isEmpty()) {
                JOptionPane.showMessageDialog(thisComponent(),
                        "There are no attemps for the selected weather station\n"
                        + "and date fon this lesson, so no responses need to be\n"
                        + "be provided.", "No Attempts Found", 
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            new InstructorResonseEntryForm(appControl, attemptsToGrade, 
                false, lesson, station, new java.sql.Date(date.getTime()),
                    isRegrading);
            
            //Show any changes.
            updateMissingDataTable();
        }
    };
    
    /**
     * Helper function to update the missing data table on the "Grading Status"
     * tab.
     */
    private void updateMissingDataTable() {
        ArrayList<MissingWebGradingDataRecord> rowEntries = missingDataRecordManager.getAllRecordsForLesson(lesson);

        //Sort list by data and station code.
        Collections.sort(rowEntries, new Comparator<MissingWebGradingDataRecord>() {
            @Override
            public int compare(MissingWebGradingDataRecord record1,
                    MissingWebGradingDataRecord record2) {
                int dateCompareResult = record1.gatDate().compareTo(record2.gatDate());
                if (dateCompareResult != 0) {
                    return dateCompareResult;
                } else {
                    return record1.getStationCode().compareTo(record2.getStationCode());
                }
            }
        });


        //Set up table.
        missingDataModel.setRowCount(rowEntries.size());
        missingDataModel.setColumnCount(3);
        missingDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        missingDataTable.getColumnModel().getColumn(0).setHeaderValue("Date");
        missingDataTable.getColumnModel().getColumn(1).setHeaderValue("Station");
        missingDataTable.getColumnModel().getColumn(2).setHeaderValue("Status");
        
        //Fill table.
        for (int i = 0; i < rowEntries.size(); i++) {
            MissingWebGradingDataRecord currentRecord = rowEntries.get(i);
            
            missingDataTable.setValueAt(tableDateFormat
                    .format(currentRecord.gatDate()), i, 0);
            missingDataTable.setValueAt(currentRecord.getStationCode(), i, 1);
            if (currentRecord.getIsInstructorDataSet()) {
                missingDataTable.setValueAt("Answers Provided", i, 2);
            } else {
                missingDataTable.setValueAt("Answers NOT Provided", i, 2);
            }
        }
    }

    
    /**
     * Initializes the station listener for creating a newLesson and calls the 
     * initializeBasicSettings method to initialize all of the fields in the window 
     */
    private void initialize() {
        changeStationListener();
        initializeBasicSettings();
    }
    
    /**
     * Initializes the station listener for editing a newLesson and calls the 
     * initializeLessonSettings method to initialize all of the fields in the window 
     */
    private void editLessonInitialize(){
        changeStationListener();
        initializeLessonSettings();
    }
    
    /**
     * This method initializes the windows settings for when an instructor will
     * be editing a previously existing newLesson.  Uses the values stored in the
     * database for the newLesson to initialize the text fields for each option.
     */
    private void initializeLessonSettings(){
        courses = new ArrayList<Course>();
        courses.add(lesson.getCourse());
        Iterator<Course> i = courses.iterator();
        ArrayList<String> courseNames = new ArrayList<>();
        while (i.hasNext()) {
            courseNames.add(i.next().getClassName());
        }
        String[] courseArray
                = courseNames.toArray(new String[courseNames.size()]);
        courseComboBox.setModel(new DefaultComboBoxModel<String>(courseArray));
        //Only one option, so disable box.
        courseComboBox.setEnabled(false);
        questionsTextArea.setEnabled(false);
        stationCodeText.setEnabled(false);
        
        pointsPerQuestionText.setText(String.valueOf(lesson.getPointScale().getCorrectPoints()));
        attemptedPointsText.setText(String.valueOf(lesson.getPointScale().getIncorrectPoints()));
        unansweredPointsText.setText(String.valueOf(lesson.getPointScale().getUnansweredPoints()));
        maximumAttemptsText.setText(String.valueOf(lesson.getMaximumTries()));
        triesCountedText.setText(String.valueOf(lesson.getPointScale().getTopScoreCounted()));

        lessonNameText.setText(lesson.getLessonName());
        startDateText.setText(dateFormat.format(lesson.getLessonStartDate()));
        startDate = lesson.getLessonStartDate();
        
        //Can't change a start date that's passed.
        if(startDate.getTime() < Calendar.getInstance().getTimeInMillis()) {
            chooseStartDateButton.setEnabled(false);
        }
        
        endDateText.setText(dateFormat.format(lesson.getLessonEndDate()));
        endDate = lesson.getLessonEndDate();
        
        stationCodeText.setText(lesson.getStationCode());
        studentSelectionCheckBox.setSelected(stationCodeText.getText().isEmpty());
        
        //Check for default grading option.
        if (pointsPerQuestionText.getText().equals("3")
                && attemptedPointsText.getText().equals("1")
                && unansweredPointsText.getText().equals("0")) {
            defaultScoringCheckbox.setSelected(true);
            pointsPerQuestionLabel.setEnabled(false);
            pointsPerQuestionText.setEnabled(false);
            attemptedPointsLabel.setEnabled(false);
            attemptedPointsText.setEnabled(false);
            unansweredPointsLabel.setEnabled(false);
            unansweredPointsText.setEnabled(false);
        } else {
            defaultScoringCheckbox.setSelected(false);
            pointsPerQuestionLabel.setEnabled(true);
            pointsPerQuestionText.setEnabled(true);
            attemptedPointsLabel.setEnabled(true);
            attemptedPointsText.setEnabled(true);
            unansweredPointsLabel.setEnabled(true);
            unansweredPointsText.setEnabled(true);
        }
       
        //Check if unlimited tries should be checked.
        String unlimitedValue = String.valueOf((endDate.getTime() - startDate.getTime())
                / ResourceTimeManager.MILLISECONDS_PER_DAY + 1);
        if (maximumAttemptsText.getText().equals(unlimitedValue)) {
            unlimitedTriesCheckbox.setSelected(true);
            maximumAttemptsText.setEnabled(false);
            maximumAttemptsLabel.setEnabled(false);
        } else {
            unlimitedTriesCheckbox.setSelected(false);
            maximumAttemptsText.setEnabled(true);
            maximumAttemptsLabel.setEnabled(true);
        }
        
        //Check if count all tries should be checked.
        if (triesCountedText.getText().equals(maximumAttemptsText.getText())) {
            allScoresCountedCheckbox.setSelected(true);
            triesCountedText.setEnabled(false);
            triesCountedLabel.setEnabled(false);
        } else {
            allScoresCountedCheckbox.setSelected(false);
            triesCountedText.setEnabled(true);
            triesCountedLabel.setEnabled(true);
        }
        
        //Check if require all answers should be checked.
        if (lesson.getPointScale().getRequireAnswers()) {
            requireAnswersCheckBox.setSelected(true);
            unansweredPointsLabel.setEnabled(false);
            unansweredPointsText.setEnabled(false);
        } else {
            requireAnswersCheckBox.setSelected(false);
        }
        
        //Disable grading panel if lesson has opened.
        if (startDate.getTime() < System.currentTimeMillis()) {
            for (Component component : gradingPanel.getComponents()) {
                component.setEnabled(false);
            }
        }
        
        //Disable remaining fields if lesson has closed.
        if(endDate.getTime() < Calendar.getInstance().getTimeInMillis()) {
            chooseEndDateButton.setEnabled(false);
            stateComboBox.setEnabled(false);
            studentSelectionCheckBox.setEnabled(false);
        }
    }

    /**
     * This method initializes the windows settings for when an instructor will
     * be creating a new newLesson.  If a default should exist for a field, the
     * text field for that option will be initialized otherwise it will be left
     * blank.
     */
    private void initializeBasicSettings() {
        courses = new ArrayList<Course>(courseManager.obtainAllCoursesTaughyByUser(
                appControl.getGeneralService().getUser()));
        Iterator<Course> i = courses.iterator();
        ArrayList<String> courseNames = new ArrayList<>();
        while (i.hasNext()) {
            courseNames.add(i.next().getClassName());
        }
        String[] courseArray
                = courseNames.toArray(new String[courseNames.size()]);
        courseComboBox.setModel(new DefaultComboBoxModel<>(courseArray));
        questionsTextArea.setEnabled(false);
        stationCodeText.setEnabled(false);
        
        if(course != null){
            int index = 0;
            for (int j = 0; j < courseArray.length; j++) {
                if(courseArray[j].equals(course.getClassName())) {
                    index = j;
                }
            }
            courseComboBox.setSelectedIndex(index);
        }
        
        //Reset to defult grading
        pointsPerQuestionText.setText("3");
        attemptedPointsText.setText("1");
        unansweredPointsText.setText("0");
        defaultScoringCheckbox.setSelected(true);
        pointsPerQuestionLabel.setEnabled(false);
        pointsPerQuestionText.setEnabled(false);
        attemptedPointsLabel.setEnabled(false);
        attemptedPointsText.setEnabled(false);
        unansweredPointsLabel.setEnabled(false);
        unansweredPointsText.setEnabled(false);
        
        //Set other grading options.
        maximumAttemptsText.setText("50");
        triesCountedText.setText("10");
        unlimitedTriesCheckbox.setSelected(false);
        maximumAttemptsText.setEnabled(true);
        maximumAttemptsLabel.setEnabled(true);
        allScoresCountedCheckbox.setSelected(false);
        triesCountedText.setEnabled(true);
        triesCountedLabel.setEnabled(true);
        requireAnswersCheckBox.setSelected(true);
    }

    /**
     * This method sets the variables for each of the fields needed to create a
     * forecaster newLesson. It then calls the insertForecasterLesson method from
     * the forecaster newLesson manager database manager to insert all of the
     * information into the database.
     */
    void createLesson() {
        String lessonID = "1A";
        String lessonName = lessonNameText.getText();
        String studentEditType = "deny";//to be removed
        Date lessonStartDate = startDate;
        Date lessonEndDate = endDate;
        int topScoresCounted;
        int maximumTries;
        int pointsPerQuestion = Integer.parseInt(pointsPerQuestionText.getText());
        int attemptedPoints = Integer.parseInt(attemptedPointsText.getText());
        int unansweredPoints = Integer.parseInt(unansweredPointsText.getText());
        String stationCode;


        if (unlimitedTriesCheckbox.isSelected()) {
            maximumTries = (int) ((endDate.getTime() - startDate.getTime())
                    / ResourceTimeManager.MILLISECONDS_PER_DAY + 1);
        } else {
            maximumTries = Integer.parseInt(maximumAttemptsText.getText());
        }

        if (allScoresCountedCheckbox.isSelected()) {
            topScoresCounted = maximumTries;
        } else {
            topScoresCounted = Integer.parseInt(triesCountedText.getText());
        }

        Date today = new Date();

        PointScale ps = new PointScale("1A", pointsPerQuestion, attemptedPoints,
                unansweredPoints, topScoresCounted, requireAnswersCheckBox.isSelected());

        boolean fromArchivedData = false;
        Date dateArchived = today;

        int count = 0;
        Iterator<Course> i = courses.iterator();
        //traverses the list of courses an instructor has checks to see if the
        //course selected is the course at the given index then sets the 
        //course for newLesson creation to that course.
        while (i.hasNext()) {
            if (String.valueOf(courses.get(count))
                    .equals(String.valueOf(courseComboBox.getSelectedItem())
                    .concat(" (" + courses.get(count).getSemester() + " "
                    + courses.get(count).getYear() + ")"))) {
                course = courses.get(count);
            }
            i.next();
            count++;
        }

        //TODO: Let the user change this array.
        ArrayList<Question> questions = new ArrayList<>();
        Question question;
        Iterator<Question> questionIterator = questionArray.iterator();
        count = 0;
        while (questionIterator.hasNext()) {
            if (count <= 3 || count >= 10) {
                question = questionIterator.next();
                question.setQuestionZulu("12");
                questions.add(question);
                count++;
            } else {
                question = questionIterator.next();
                question.setQuestionZulu("18");
                questions.add(question);
                count++;
            }
        }
        instructions = new Instructions("1A", instructionsTextArea.getText());
        if (stationCodeText.getText().equals("")) {
            stationCode = null;
        } else {
            stationCode = stationCodeText.getText();
        }
        ForecasterLesson newLesson = new ForecasterLesson(lessonID, lessonName,
                studentEditType, stationCode, lessonStartDate, lessonEndDate,
                maximumTries, fromArchivedData, dateArchived, instructions,
                course, questions, ps);
        try {
            lessonManager.insertForecasterLesson(newLesson);
        } catch (WeatherException ex) {
            Logger.getLogger(InstructorLessonCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method sets the variables needed for editing a forecaster newLesson to
     * the new data the instructor provides. It then calls the
     * updateForecasterLesson from the forecaster newLesson manager database
     * manager to edit the existing data in the database to reflect the changes
     * the instructor wanted.
     */
    void editLesson() {
        String lessonID = lesson.getLessonID();
        String lessonName = lessonNameText.getText();
        String studentEditType = "deny";//to be removed
        Date lessonStartDate = startDate;
        Date lessonEndDate = endDate;
        int topScoresCounted;
        int maximumTries;
        if (unlimitedTriesCheckbox.isSelected()) {
            maximumTries = (int) ((endDate.getTime() - startDate.getTime())
                    / ResourceTimeManager.MILLISECONDS_PER_DAY + 1);
        } else {
            maximumTries = Integer.parseInt(maximumAttemptsText.getText());
        }
        if (allScoresCountedCheckbox.isSelected()) {
            topScoresCounted = maximumTries;
        } else {
            topScoresCounted = Integer.parseInt(triesCountedText.getText());
        }
        int pointsPerQuestion = Integer.parseInt(pointsPerQuestionText.getText());
        int attemptedPoints = Integer.parseInt(attemptedPointsText.getText());
        int unansweredPoints = Integer.parseInt(unansweredPointsText.getText());
        String stationCode;

        Date today = new Date();

        PointScale ps = new PointScale(lesson.getPointScale().getPointScaleId(), 
                pointsPerQuestion, attemptedPoints,unansweredPoints, 
                topScoresCounted, requireAnswersCheckBox.isSelected());

        boolean fromArchivedData = false;
        Date dateArchived = today;

        course = lesson.getCourse();
        ArrayList<Question> questions = lesson.getQuestions();

        String lessonCreatorInstruction = instructionsTextArea.getText();
        Instructions instruction = new Instructions(lesson.getInstructions().getInstructionsID(), lessonCreatorInstruction);

        if (stationCodeText.getText().equals("")) {
            stationCode = null;
        } else {
            stationCode = stationCodeText.getText();
        }
        ForecasterLesson editedLesson = new ForecasterLesson(lessonID, lessonName,
                studentEditType, stationCode, lessonStartDate, lessonEndDate,
                maximumTries, fromArchivedData, dateArchived, instruction,
                course, questions, ps);

        lessonManager.updateForecasterLesson(editedLesson);
        
        lesson = editedLesson;
    }
    
    /**
     * Helper function to update the station list after a state is selected.
     */
    private void updateStationCodeList() {
        if (stateComboBox.getSelectedIndex() == 0) {
            return;
        }
        String state = stateComboBox.getSelectedItem().toString().trim();
        int position = state.indexOf("(");
        state = state.substring(position + 1, position + 3);
        stations = stationManager.getAllStationsByState(state);
        listModel = new DefaultListModel<>(); 
        
        stationList.setModel(listModel);
        
        for(Station s : stations)
        {
            listModel.addElement(s.getStationName());
        }
    }
    
    /**
     * Sets the default instructions for a newLesson based on the instructions
     * provided by the Iowa state website.
     * @return A string to be used to set the default instructions the instructor
     * will see when opening the instructions tab while creating a newLesson.
     */
    public String setInstructions(){
        String instructionsForLesson = "This page gives access to the Forecasting assignments.  "
                + "You are to do the Daily Forecasting Assignment ONLY. \n\n" +
                "1. Daily Forecasting Assignment\n" + "Under Options, select Assignments, "
                + "and then the Forecasting assignment link to submit forecasts. \n\n" +
                "You will forecast for Athens, GA (station name: KAHN) and use your best judgment "
                + "to answer 12 questions about the weather in Athens the following day.\n\n" +
                "Click Save Answers as you move along and Submit when you are done.\n\n" +
                "After making your forecast on a particular day, you can change your forecast at any "
                + "time until midnight, but the assignment will automatically close at midnight prior to the day you are forecasting for.\n\n" +
                "Because I will only count your 20 best forecasts, there is no penalty for trying.  You can "
                + "attempt up to 50 forecasts total from Oct. 10-Dec. 5, but only your best 20 will count.\n\n" +
                "Scoring: You will get 3 points for a correct answer, one point for trying, and no points if you "
                + "don't participate.  Each forecast is worth a maximum of 36 points. ";
        return instructionsForLesson;
    }
    
    /**
     * Function that handles selection of station from list.
     */
    private void changeStationListener()
    {
        MouseAdapter changeStation;
        changeStation = new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (stationList.getSelectedValue() == null) {
                    return;
                }
                
                stationCodeText.setText(stationManager.obtainStation(stationList
                        .getSelectedValue().toString().trim()).getStationId());
                studentSelectionCheckBox.setSelected(false);
                
            }
        };
        
        stationList.addMouseListener(changeStation);
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        submitionButtonGroup = new javax.swing.ButtonGroup();
        createLessonTabbedPane = new javax.swing.JTabbedPane();
        settingsTab = new javax.swing.JPanel();
        basicSettingPanel = new javax.swing.JPanel();
        courseLabel = new javax.swing.JLabel();
        courseComboBox = new javax.swing.JComboBox<String>();
        lessonNameLabel = new javax.swing.JLabel();
        startDateLabel = new javax.swing.JLabel();
        endDateLabel = new javax.swing.JLabel();
        lessonNameText = new javax.swing.JTextField();
        startDateText = new javax.swing.JTextField();
        endDateText = new javax.swing.JTextField();
        chooseStartDateButton = new javax.swing.JButton();
        chooseEndDateButton = new javax.swing.JButton();
        charLimitjLabel = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();
        preferencesPanel = new javax.swing.JPanel();
        stationLabel = new javax.swing.JLabel();
        stationCodeText = new javax.swing.JTextField();
        stateComboBox = new javax.swing.JComboBox();
        stationScrollPane = new javax.swing.JScrollPane();
        stationList = new javax.swing.JList();
        submitionLabel = new javax.swing.JLabel();
        yesRadioButton = new javax.swing.JRadioButton();
        noRadioButton = new javax.swing.JRadioButton();
        studentSelectionCheckBox = new javax.swing.JCheckBox();
        gradingPanel = new javax.swing.JPanel();
        pointsPerQuestionLabel = new javax.swing.JLabel();
        unansweredPointsLabel = new javax.swing.JLabel();
        attemptedPointsLabel = new javax.swing.JLabel();
        pointsPerQuestionText = new javax.swing.JTextField();
        unansweredPointsText = new javax.swing.JTextField();
        attemptedPointsText = new javax.swing.JTextField();
        defaultScoringCheckbox = new javax.swing.JCheckBox();
        unlimitedTriesCheckbox = new javax.swing.JCheckBox();
        allScoresCountedCheckbox = new javax.swing.JCheckBox();
        maximumAttemptsText = new javax.swing.JTextField();
        triesCountedText = new javax.swing.JTextField();
        maximumAttemptsLabel = new javax.swing.JLabel();
        triesCountedLabel = new javax.swing.JLabel();
        requireAnswersCheckBox = new javax.swing.JCheckBox();
        instructionsTab = new javax.swing.JPanel();
        instructionsScrollPane = new javax.swing.JScrollPane();
        instructionsTextArea = new javax.swing.JTextArea();
        questionsTab = new javax.swing.JPanel();
        questionsScrollPane = new javax.swing.JScrollPane();
        questionsTextArea = new javax.swing.JTextArea();
        gradingTab = new javax.swing.JPanel();
        gradingHeaderLabel = new javax.swing.JLabel();
        missingDaysScrollPane = new javax.swing.JScrollPane();
        missingDataTable = new javax.swing.JTable();
        footerPanel = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        helpPanel = new javax.swing.JPanel();
        helpScrollPane = new javax.swing.JScrollPane();
        helpTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        settingsTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        basicSettingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Basic Settings", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        basicSettingPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        courseLabel.setText("Course:");
        basicSettingPanel.add(courseLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 20, 45, 16));

        courseComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        basicSettingPanel.add(courseComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 20, 146, 22));

        lessonNameLabel.setText("Lesson Name:");
        basicSettingPanel.add(lessonNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 54, 81, 16));

        startDateLabel.setText("Start Time:");
        basicSettingPanel.add(startDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 88, 66, 16));

        endDateLabel.setText("End Time:");
        basicSettingPanel.add(endDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 125, 59, 16));
        basicSettingPanel.add(lessonNameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 54, 263, 22));

        startDateText.setEditable(false);
        basicSettingPanel.add(startDateText, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 88, 147, 22));

        endDateText.setEditable(false);
        basicSettingPanel.add(endDateText, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 125, 147, 22));

        chooseStartDateButton.setText("Choose Date");
        chooseStartDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseStartDateButtonActionPerformed(evt);
            }
        });
        basicSettingPanel.add(chooseStartDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 88, 105, 25));

        chooseEndDateButton.setText("Choose Date");
        chooseEndDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseEndDateButtonActionPerformed(evt);
            }
        });
        basicSettingPanel.add(chooseEndDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 125, 105, 25));

        charLimitjLabel.setText("Limit 30 characters");
        basicSettingPanel.add(charLimitjLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(392, 54, 109, 16));

        helpButton.setText("Show Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        basicSettingPanel.add(helpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(466, 20, -1, 25));

        settingsTab.add(basicSettingPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 577, 162));

        preferencesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preferences", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        preferencesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        stationLabel.setText("Set Station Code:");
        preferencesPanel.add(stationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 20, 101, 16));
        preferencesPanel.add(stationCodeText, new org.netbeans.lib.awtextra.AbsoluteConstraints(131, 20, 79, 22));

        stateComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select a State", "ALABAMA (AL) ", "ARKANSAS (AR)  ", "ARIZONA (AZ) ", "CALIFORNIA (CA) ", "COLORADO (CO) ", "CONNECTICUT (CT)  ", "DELAWARE (DE)  ", "FLORIDA (FL)  ", "GEORGIA (GA) ", "IDAHO (ID) ", "ILLINOIS (IL) ", "INDIANA (IN)  ", "IOWA (IA)  ", "KANSAS (KS)  ", "KENTUCKY (KY)  ", "LOUISIANA (LA)  ", "MAINE (ME)  ", "MARYLAND (MD)  ", "MASSACHUSETTS (MA)  ", "MICHIGAN (MI)  ", "MINNESOTA (MN)  ", "MISSISSIPPI (MS)  ", "MISSOURI (MO) ", "MONTANA (MT)  ", "NEBRASKA (NE)  ", "NEVADA (NV)  ", "NEW HAMPSHIRE (NH)  ", "NEW JERSEY (NJ)  ", "NEW MEXICO (NM)  ", "NEW YORK (NY)  ", "NORTH CAROLINA (NC)  ", "NORTH DAKOTA (ND)  ", "OHIO (OH)  ", "OKLAHOMA (OK)  ", "OREGON (OR)  ", "PENNSYLVANIA (PA)  ", "RHODE ISLAND (RI) ", "SOUTH CAROLINA (SC)  ", "SOUTH DAKOTA (SD)  ", "TENNESSEE (TN)  ", "TEXAS (TX)  ", "UTAH (UT)  ", "VERMONT (VT)  ", "VIRGINIA (VA)  ", "WASHINGTON (WA) ", "WISCONSIN (WI) ", "WEST VIRGINIA (WV)  ", "WYOMING (WY)" }));
        preferencesPanel.add(stateComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(228, 20, 172, 22));

        stationList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        stationScrollPane.setViewportView(stationList);

        preferencesPanel.add(stationScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 140, 96));

        submitionLabel.setText("Allow edits after submition?");
        submitionLabel.setEnabled(false);
        preferencesPanel.add(submitionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 95, -1, -1));

        submitionButtonGroup.add(yesRadioButton);
        yesRadioButton.setText("Yes");
        yesRadioButton.setEnabled(false);
        preferencesPanel.add(yesRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 91, -1, -1));

        submitionButtonGroup.add(noRadioButton);
        noRadioButton.setSelected(true);
        noRadioButton.setText("No");
        noRadioButton.setEnabled(false);
        preferencesPanel.add(noRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 91, -1, -1));

        studentSelectionCheckBox.setSelected(true);
        studentSelectionCheckBox.setText("Allow students to select weather station");
        studentSelectionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentSelectionCheckBoxActionPerformed(evt);
            }
        });
        preferencesPanel.add(studentSelectionCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 54, 260, 25));

        settingsTab.add(preferencesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 186, 577, 128));

        gradingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Grading", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        gradingPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                gradingPanelMouseMoved(evt);
            }
        });
        gradingPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pointsPerQuestionLabel.setText("Points per question:");
        gradingPanel.add(pointsPerQuestionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 57, 114, 16));

        unansweredPointsLabel.setText("Points for unanswered:");
        gradingPanel.add(unansweredPointsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 131, 133, 16));

        attemptedPointsLabel.setText("Points for attempting:");
        gradingPanel.add(attemptedPointsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 94, 124, 16));
        gradingPanel.add(pointsPerQuestionText, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 57, 36, 22));
        gradingPanel.add(unansweredPointsText, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 131, 36, 22));
        gradingPanel.add(attemptedPointsText, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 94, 36, 22));

        defaultScoringCheckbox.setText("Default Scoring");
        defaultScoringCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultScoringCheckboxActionPerformed(evt);
            }
        });
        gradingPanel.add(defaultScoringCheckbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 25, -1, -1));

        unlimitedTriesCheckbox.setText("Unlimited");
        unlimitedTriesCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlimitedTriesCheckboxActionPerformed(evt);
            }
        });
        gradingPanel.add(unlimitedTriesCheckbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(476, 57, -1, -1));

        allScoresCountedCheckbox.setText("All");
        allScoresCountedCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allScoresCountedCheckboxActionPerformed(evt);
            }
        });
        gradingPanel.add(allScoresCountedCheckbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(476, 94, -1, -1));
        gradingPanel.add(maximumAttemptsText, new org.netbeans.lib.awtextra.AbsoluteConstraints(422, 57, 36, 22));
        gradingPanel.add(triesCountedText, new org.netbeans.lib.awtextra.AbsoluteConstraints(422, 94, 36, 22));

        maximumAttemptsLabel.setText("Maximum Attempts:");
        maximumAttemptsLabel.setToolTipText("");
        gradingPanel.add(maximumAttemptsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 57, 115, 16));

        triesCountedLabel.setText("Tries Counted:");
        gradingPanel.add(triesCountedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 94, 85, 16));

        requireAnswersCheckBox.setSelected(true);
        requireAnswersCheckBox.setText("Require Answers to All Questions");
        requireAnswersCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requireAnswersCheckBoxActionPerformed(evt);
            }
        });
        gradingPanel.add(requireAnswersCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 131, -1, -1));

        settingsTab.add(gradingPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 326, 577, 168));

        createLessonTabbedPane.addTab("Settings", settingsTab);

        instructionsTextArea.setColumns(20);
        instructionsTextArea.setRows(5);
        instructionsTextArea.setWrapStyleWord(true);
        instructionsScrollPane.setViewportView(instructionsTextArea);

        javax.swing.GroupLayout instructionsTabLayout = new javax.swing.GroupLayout(instructionsTab);
        instructionsTab.setLayout(instructionsTabLayout);
        instructionsTabLayout.setHorizontalGroup(
            instructionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(instructionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
        );
        instructionsTabLayout.setVerticalGroup(
            instructionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(instructionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );

        createLessonTabbedPane.addTab("Instructions", instructionsTab);

        questionsTextArea.setColumns(20);
        questionsTextArea.setRows(5);
        questionsScrollPane.setViewportView(questionsTextArea);

        javax.swing.GroupLayout questionsTabLayout = new javax.swing.GroupLayout(questionsTab);
        questionsTab.setLayout(questionsTabLayout);
        questionsTabLayout.setHorizontalGroup(
            questionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(questionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
        );
        questionsTabLayout.setVerticalGroup(
            questionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(questionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );

        createLessonTabbedPane.addTab("Questions", questionsTab);

        gradingTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        gradingHeaderLabel.setText("<html> The following is a list of weather station-date pairings for which attempts made for this lesson could not be graded with Internet data should any exist.  To provide answers so these attempts can be graded, double-click on a pairing.</html>");
        gradingTab.add(gradingHeaderLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 577, 48));

        missingDaysScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        missingDaysScrollPane.setToolTipText("");

        missingDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        missingDaysScrollPane.setViewportView(missingDataTable);

        gradingTab.add(missingDaysScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 72, 577, 420));

        createLessonTabbedPane.addTab("Grading Status", gradingTab);

        getContentPane().add(createLessonTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 612, 536));

        footerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        createButton.setText("Create Lesson");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resetButton)
                .addGap(163, 163, 163)
                .addComponent(createButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 180, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, footerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetButton)
                    .addComponent(createButton)
                    .addComponent(closeButton))
                .addContainerGap())
        );

        getContentPane().add(footerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 560, 612, 55));

        helpPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        helpTextPane.setEditable(false);
        helpTextPane.setText("The following instructions provide a brief overview of how to create a forecasting lesson.\n\nBegin by selecting the course, entering a forecasting lesson name, and choosing the starting and ending times for the forecast. The start time will default to the next available hour.  The end time will default to the end of the current day.\n\nNext, the instructor has the option of choosing the forecast site for the lesson by selecting a state and city from the Iowa State Dynamic Weather Forecaster (DWF) database or allowing the student to have the option to create a forecast for any city listed in the database.\n\nThe default scoring awards three points for a correct answer, zero points for unanswered questions and one point for incorrect attempts by default. Points are awarded for the twelve content questions/items.  The instructor may modify the point scheme, if desired.\n\nOnce the instructor selects a beginning and ending date for the assignment, the program automatically calculates the maximum number of attempts for the date range (assuming one attempt per day). The instructor may modify this option or set the number of attempts to unlimited.  In addition, the instructor has the ability to modify the number of attempts that will be counted in the final grade.  In other words, if a forecasting assignment is posted for 8 days. The instructor may allow 8 attempts, but only use the 5 highest scores to calculate a final grade.\n\nOnce the settings have been modified, an instructor may modify the instructions that are presented to the students on the Instructions tab. To do this, simply modify the text and it will be saved to the database once the forecasting lesson is created.\n\nThe third tab displays the 12 questions that are presented to the students. These questions are currently not editable.\n\nOnce the assignment is prepared, click Create to post the assignment for public view. Some settings may be edited later from the forecasting lesson manager.\n\nThe fourth tab, which is only available after a lesson is created, provides an opportunity to supply answers to student attempts which could not be graded because data could not be retrieved from the Internet.");
        helpScrollPane.setViewportView(helpTextPane);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(helpScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
        );

        getContentPane().add(helpPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(636, 12, 352, 603));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        checkClose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Helper function to verify input related to dates. this includes:
     * 1. making sure the start date hasn't happened already if it is editable.
     * 2. making sure the start date is before the end date and the end date 
     *    hasn't happened yet.
     * 3. making sure the maximum number of attempts is not too large for the
     *    date range.
     */
    private void verifyDateRelatedInput(){
        //Test start date.
        if(chooseStartDateButton.isEnabled() && startDate != null
                && startDate.getTime() < Calendar.getInstance().getTimeInMillis()) {
            JOptionPane.showMessageDialog(this, 
                    "The start date must be after the current time.",
                    "Invalid Start Date", JOptionPane.INFORMATION_MESSAGE);
            startDateText.setText("");
            startDate = null;
        }
        
        //Test end date.
        if(endDate != null && startDate != null
                && startDate.getTime() > endDate.getTime()) {
            JOptionPane.showMessageDialog(this, 
                    "The start date must be before the end date.",
                    "Invalid End Date", JOptionPane.INFORMATION_MESSAGE);
            endDateText.setText("");
            endDate = null;
        }
        
        if(endDate != null
                && endDate.getTime() < Calendar.getInstance().getTimeInMillis()) {
            JOptionPane.showMessageDialog(this, 
                    "The end date must be after the current time.",
                    "Invalid Start Date", JOptionPane.INFORMATION_MESSAGE);
            endDateText.setText("");
            endDate = null;
        }
        
        //Test if the maximum number of attemps is not mote them the munber od
        //days given and chamge it if not.  The code alse makes sure the number
        //of tries to be graded is a proper amount.
        if(startDate == null || endDate == null) {
            return;
        }
        int maximumTries = (int)((endDate.getTime() - startDate.getTime())
                    / ResourceTimeManager.MILLISECONDS_PER_DAY + 1);
        try {
            int enteredMaxAttempts = Integer.parseInt(maximumAttemptsText.getText());
            if(enteredMaxAttempts > maximumTries) {
                throw new Exception();
            }
        } catch (Exception ex) {
            maximumAttemptsText.setText(String.valueOf(maximumTries));
        }
        try {
            int enteredMaxAttemptsToGrade = Integer.parseInt(triesCountedText.getText());
            if(enteredMaxAttemptsToGrade > maximumTries) {
                throw new Exception();
            }
        } catch (Exception ex) {
            triesCountedText.setText(String.valueOf(maximumTries));
        }
        try {
            if (!triesCountedText.getText().isEmpty() && !maximumAttemptsText.getText().isEmpty()
                    && Integer.valueOf(triesCountedText.getText()) > Integer.valueOf(maximumAttemptsText.getText())) {
                triesCountedText.setText(String.valueOf(Integer.valueOf(maximumAttemptsText.getText())));
            }
        } catch (NumberFormatException nfe) {
            //No work needed - caught on save
        }
    }
    
    /**
     * Helper function to return the next hour after a given time.
     * @param time <code>Date</code> holding the given time.
     * @return The next hour after the given time.
     */
    private Date nextHour(Date time){
        //If already the start of an hour, return as is.
        if(time.getTime() % ResourceTimeManager.MILLISECONDS_PER_HOUR == 0) {
            return time;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(time);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * sets the date for the start date of the newLesson to the date the instructor
     * selects
     * @param evt 
     */
    private void chooseStartDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseStartDateButtonActionPerformed
        ChooseDate tempDialog = 
                new ChooseDate(nextHour(startDate == null ? new Date() : startDate), false);
        if(tempDialog.getSelectedDate() != null) {
            startDate = tempDialog.getSelectedDate();
        }
        if(startDate != null) {
            startDateText.setText(dateFormat.format(startDate));
        }
        verifyDateRelatedInput();
    }//GEN-LAST:event_chooseStartDateButtonActionPerformed

    /**
     * sets the date for the end date of the newLesson to the date the instructor
     * selects
     * @param evt 
     */
    private void chooseEndDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseEndDateButtonActionPerformed
        ChooseDate tempDialog 
                = new ChooseDate(endDate == null ? new Date() : endDate, true);
        if(tempDialog.getSelectedDate() != null) {
            endDate = tempDialog.getSelectedDate();
        }
        if(endDate != null) {
            endDateText.setText(dateFormat.format(endDate));
        }
        verifyDateRelatedInput();
    }//GEN-LAST:event_chooseEndDateButtonActionPerformed

    /**
     * This controls the default scoring for a Lesson.  The default scoring
     * checkbox is initialized to be selected.  The instructor can unselect it
     * to enter their own point values or reselect it to reset the points to the
     * default values.
     * @param evt 
     */
    private void defaultScoringCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultScoringCheckboxActionPerformed
        if(!formInitialized){
            return;
        }
        pointsPerQuestionLabel.setEnabled(!pointsPerQuestionLabel.isEnabled());
        pointsPerQuestionText.setEnabled(!pointsPerQuestionText.isEnabled());
        attemptedPointsLabel.setEnabled(!attemptedPointsLabel.isEnabled());
        attemptedPointsText.setEnabled(!attemptedPointsText.isEnabled());
        unansweredPointsLabel.setEnabled(!unansweredPointsLabel.isEnabled());
        unansweredPointsText.setEnabled(!unansweredPointsText.isEnabled());
        if(defaultScoringCheckbox.isSelected()){
            pointsPerQuestionText.setText("3");
            attemptedPointsText.setText("1");
            unansweredPointsText.setText("0");
        }
        if(requireAnswersCheckBox.isSelected()) {
            unansweredPointsLabel.setEnabled(false);
            unansweredPointsText.setEnabled(false);
        }
        repaint();
    }//GEN-LAST:event_defaultScoringCheckboxActionPerformed

    /**
     * If the instructor selects the unlimited tries checkbox this method 
     * changes the state of the label and text associated with it to either
     * enabled or unenabled.
     * @param evt 
     */
    private void unlimitedTriesCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unlimitedTriesCheckboxActionPerformed
        maximumAttemptsLabel.setEnabled(!maximumAttemptsLabel.isEnabled());
        maximumAttemptsText.setEnabled(!maximumAttemptsText.isEnabled());
        if(!maximumAttemptsText.isEnabled() && startDate != null && endDate != null) {
            maximumAttemptsText.setText(String.valueOf((endDate.getTime() - startDate.getTime()) 
                    / ResourceTimeManager.MILLISECONDS_PER_DAY + 1));
        }
        repaint();
    }//GEN-LAST:event_unlimitedTriesCheckboxActionPerformed

    /**
     * If the instructor selects the All scores counted checkbox this method 
     * changes the state of the label and text associated with it to either
     * enabled or unenabled.
     * @param evt 
     */
    private void allScoresCountedCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allScoresCountedCheckboxActionPerformed
        triesCountedLabel.setEnabled(!triesCountedLabel.isEnabled());
        triesCountedText.setEnabled(!triesCountedText.isEnabled());
        if (!triesCountedText.isEnabled()) {
            try {
                Integer.parseInt(maximumAttemptsText.getText());
                triesCountedText.setText(maximumAttemptsText.getText());
            } catch (NumberFormatException nfe) {
                //No work needed - caught on save
            }
        }
        repaint();
    }//GEN-LAST:event_allScoresCountedCheckboxActionPerformed

    /**
     * This event handled is used to check to make sure that no logical errors
     * occur when the instructor is entering values for the grading.  When a 
     * mouse movement is detected in the panel, it checks to make sure that
     * the instructor has not entered a higher point total for an incorrect or
     * unanswered question than is given for a correct question.  Also checks to
     * make sure that the amount of tries being counted is not more than the 
     * maximum attempts for a newLesson and that the maximum number of attempts
     * does not exceed one per day and that all tries will be graded if the 
     * "all" check box is checked.
     * @param evt 
     */
    private void gradingPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gradingPanelMouseMoved
       //Compare points per qustion to points for attempt.
        try {
            if (!attemptedPointsText.getText().isEmpty() && !pointsPerQuestionText.getText().isEmpty()
                    && Integer.valueOf(attemptedPointsText.getText()) > Integer.valueOf(pointsPerQuestionText.getText())) {
                attemptedPointsText.setText(String.valueOf(Integer.valueOf(pointsPerQuestionText.getText())));
            }
        } catch (NumberFormatException nfe) {
            //No work needed - caught on save
        }

        //Compare points for attempt to points for unanswered question.
        try {
            if (!unansweredPointsText.getText().isEmpty() && !attemptedPointsText.getText().isEmpty()
                    && Integer.valueOf(unansweredPointsText.getText()) > Integer.valueOf(attemptedPointsText.getText())) {
                unansweredPointsText.setText(String.valueOf(Integer.valueOf(attemptedPointsText.getText())));
            }
        } catch (NumberFormatException nfe) {
            //No work needed - caught on save
        }
        
        //Compare maximum tries to date range.
        if (startDate != null && endDate != null) {
            int maximumTries = (int) ((endDate.getTime() - startDate.getTime())
                    / ResourceTimeManager.MILLISECONDS_PER_DAY + 1);
            int enteredMaxAttempts;
            try {
                enteredMaxAttempts = Integer.parseInt(maximumAttemptsText.getText());
                if (enteredMaxAttempts > maximumTries) {
                    maximumAttemptsText.setText(String.valueOf(maximumTries));
                }
            } catch (NumberFormatException nfe) {
                //No work needed - caught on save
            }
        }
        
        //Compare tries to be graded to maximum tries.
        try {
            if (!triesCountedText.getText().isEmpty() && !maximumAttemptsText.getText().isEmpty()
                    && Integer.valueOf(triesCountedText.getText()) > Integer.valueOf(maximumAttemptsText.getText())) {
                triesCountedText.setText(String.valueOf(Integer.valueOf(maximumAttemptsText.getText())));
            }
        } catch (NumberFormatException nfe) {
            //No work needed - caught on save
        }
        
        //Check that all tries will be graded if the "all" check box is checked.
        if (allScoresCountedCheckbox.isSelected()) {
            try {
                Integer.parseInt(maximumAttemptsText.getText());
                triesCountedText.setText(maximumAttemptsText.getText());
            } catch (NumberFormatException nfe) {
                //No work needed - caught on save
            }
        }
    }//GEN-LAST:event_gradingPanelMouseMoved

    /**
     * Helper function to validate input and display error messages if input is
     * not valid.
     * @return True if input is valid, false otherwise.
     */
    private boolean isValidInput(){
        //Check if input is pressent and, when neccessary, numeric.
        if (!lessonNameText.getText().isEmpty() && !startDateText.getText().isEmpty()
                && !endDateText.getText().isEmpty() && !pointsPerQuestionText.getText().isEmpty()
                && !attemptedPointsText.getText().isEmpty() && !unansweredPointsText.getText().isEmpty()
                && !maximumAttemptsText.getText().isEmpty() && !triesCountedText.getText().isEmpty()
                && (!stationCodeText.getText().isEmpty() || studentSelectionCheckBox.isSelected())) {
            try {
                Integer.parseInt(pointsPerQuestionText.getText());
                Integer.parseInt(attemptedPointsText.getText());
                Integer.parseInt(unansweredPointsText.getText());
                Integer.parseInt(maximumAttemptsText.getText());
                Integer.parseInt(triesCountedText.getText());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,
                        "One of the grading fields is not an appropriate value.\n "
                        + "Please enter a correct value.", "Invalid Input",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            return true;    //input pressent and well-formed
        } else {
            JOptionPane.showMessageDialog(this,
                    "You have not filled in all of the necessary"
                    + " fields.\nPlease fill in all empty fields.",
                    "Missing Dats", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }
    
    /**
     * First checks to make sure that all appropriate fields have values in them,
     * then checks to see if this is a created newLesson or an edited newLesson and 
     * calls the appropriate method to handle the insertion into the database.
     * @param evt 
     */
    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        //Must check if name is availabe in case course was changed
        //after the name was typed.
        if(!isCourseNameAvailable()) {
            return;
        }
        //Check if add or edit can and should proceed.
        boolean shouldProceed;  //to record if add or edit should occur
        if (!isValidInput()) {
            shouldProceed = false;
        } else {
            int response;
            if (edit) {
                response = JOptionPane.showConfirmDialog(this,
                        "Would you like to save your edits?",
                        "Save Edits", JOptionPane.YES_NO_OPTION);
            } else {
                response = JOptionPane.showConfirmDialog(this,
                        "Would you like to save this lesson?",
                        "Save Lesson", JOptionPane.YES_NO_OPTION);
            }
            shouldProceed = response == JOptionPane.YES_OPTION;
        }

        //Perfotm add or edit.
        if (shouldProceed) {
            if (edit) {
                editLesson();
                JOptionPane.showMessageDialog(this,
                        "Your lesson has been successfully edited.",
                        "Edit Successful", JOptionPane.INFORMATION_MESSAGE);
                storeResetData();
            } else {
                createLesson();
                JOptionPane.showMessageDialog(this,
                        "Your lesson has been successfully created.",
                        "Lesson Added", JOptionPane.INFORMATION_MESSAGE);
                showLessonManager();
            }
        }
    }//GEN-LAST:event_createButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        if (isHelpShowing) {
            this.setSize(smallDim);
            this.setPreferredSize(smallDim);
            this.setMaximumSize(smallDim);
            this.setMinimumSize(smallDim);
            pack();
            isHelpShowing = false;
            helpButton.setText("Show Help");
        } else {
            helpScrollPane.getVerticalScrollBar().setValue(0);
            this.setSize(largeDim);
            this.setPreferredSize(largeDim);
            this.setMaximumSize(largeDim);
            this.setMinimumSize(largeDim);
            pack();
            isHelpShowing = true;
            helpButton.setText("Hide Help");
        }
    }//GEN-LAST:event_helpButtonActionPerformed

    /**
     * When the reset button is clicked, this resets all of the texts fields
     * to an empty string to start over.
     * @param evt 
     */
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        if(edit){
            int response
                    = JOptionPane.showConfirmDialog(this,
                            "Values will be reset to original lesson values. "
                                    + "Are you sure you want to reset?",
                            "Reset Forecast", JOptionPane.YES_NO_OPTION);
            if (response == 0) {
                initializeLessonSettings();
            }            
        }
        else{
            int response
                    = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to reset all of the values?",
                            "Reset Forecast", JOptionPane.YES_NO_OPTION);
            if (response == 0) {
                initializeBasicSettings();
                lessonNameText.setText("");
                startDateText.setText("");
                startDate = null;
                endDateText.setText("");
                endDate = null;
                stationCodeText.setText("");
            }
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    private void studentSelectionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentSelectionCheckBoxActionPerformed
        if(studentSelectionCheckBox.isSelected()) {
            stationCodeText.setText("");
        }
    }//GEN-LAST:event_studentSelectionCheckBoxActionPerformed

    /**
     * Checks if a change to the required answers check box should enable or
     * disable the unanswered points input.
     * @param evt 
     */
    private void requireAnswersCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requireAnswersCheckBoxActionPerformed
        if (!formInitialized) {
            return;
        }
        boolean enabled = !defaultScoringCheckbox.isSelected() 
                && !requireAnswersCheckBox.isSelected();
        unansweredPointsLabel.setEnabled(enabled);
        unansweredPointsText.setEnabled(enabled);
    }//GEN-LAST:event_requireAnswersCheckBoxActionPerformed

    /**
     * This function removes the lesson name if it is being used by another
     * lesson in the course.  It notifies the user of this and returns a
     * value the indicated if the name is available to use.
     * @return True if the name is available, False otherwise.
     */
    private boolean isCourseNameAvailable() {
        String typedName = lessonNameText.getText().trim();
        
        //Do nothing if no test is pessent (missing data is caught elsewhere).
        if (typedName.isEmpty()) {
            return true;
        }
        
        //Do nothing if name is the same as the one in the database for this
        //lesson.  This check is for edit mode only.
        if(edit) {
            if(typedName.equals(lesson.getLessonName())) {
                return true;
            }
        }
        
        //Check name against the lesson mames in this course.
        ArrayList<ForecasterLesson> lessons;
        //Find course to check.
        Course courseToCheck = null;
        int count = 0;
        Iterator<Course> i = courses.iterator();
        while (i.hasNext()) {
            if (String.valueOf(courses.get(count)).
                    equals(String.valueOf(courseComboBox.getSelectedItem()).
                    concat(" (" + courses.get(count).getSemester() + " "
                    + courses.get(count).getYear() + ")"))) {
                courseToCheck = courses.get(count);
            }
            i.next();
            count++;
        }
        //Load lessons to check.
        lessons = lessonManager.getForecasterLessonsByCourse(courseToCheck
                .getCourseNumber());
        //Check each lesson.
        for(ForecasterLesson savedLesson : lessons) {
            if(savedLesson.getLessonName().equals(typedName)) {
                //Match found.
                JOptionPane.showMessageDialog(this, 
                        "This course already has a lesson with the given name,\n"
                        + "so the name will be cleared.", "Name Not Available",
                        JOptionPane.INFORMATION_MESSAGE);
                if(edit) {
                    lessonNameText.setText(lesson.getLessonName());
                } else {
                    lessonNameText.setText("");
                }
                return false;
            }
        }
        
        //No match found.
        return true;
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allScoresCountedCheckbox;
    private javax.swing.JLabel attemptedPointsLabel;
    private javax.swing.JTextField attemptedPointsText;
    private javax.swing.JPanel basicSettingPanel;
    private javax.swing.JLabel charLimitjLabel;
    private javax.swing.JButton chooseEndDateButton;
    private javax.swing.JButton chooseStartDateButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox<String> courseComboBox;
    private javax.swing.JLabel courseLabel;
    private javax.swing.JButton createButton;
    private javax.swing.JTabbedPane createLessonTabbedPane;
    private javax.swing.JCheckBox defaultScoringCheckbox;
    private javax.swing.JLabel endDateLabel;
    private javax.swing.JTextField endDateText;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JLabel gradingHeaderLabel;
    private javax.swing.JPanel gradingPanel;
    private javax.swing.JPanel gradingTab;
    private javax.swing.JButton helpButton;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JScrollPane helpScrollPane;
    private javax.swing.JTextPane helpTextPane;
    private javax.swing.JScrollPane instructionsScrollPane;
    private javax.swing.JPanel instructionsTab;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JLabel lessonNameLabel;
    private javax.swing.JTextField lessonNameText;
    private javax.swing.JLabel maximumAttemptsLabel;
    private javax.swing.JTextField maximumAttemptsText;
    private javax.swing.JTable missingDataTable;
    private javax.swing.JScrollPane missingDaysScrollPane;
    private javax.swing.JRadioButton noRadioButton;
    private javax.swing.JLabel pointsPerQuestionLabel;
    private javax.swing.JTextField pointsPerQuestionText;
    private javax.swing.JPanel preferencesPanel;
    private javax.swing.JScrollPane questionsScrollPane;
    private javax.swing.JPanel questionsTab;
    private javax.swing.JTextArea questionsTextArea;
    private javax.swing.JCheckBox requireAnswersCheckBox;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel settingsTab;
    private javax.swing.JLabel startDateLabel;
    private javax.swing.JTextField startDateText;
    private javax.swing.JComboBox stateComboBox;
    private javax.swing.JTextField stationCodeText;
    private javax.swing.JLabel stationLabel;
    private javax.swing.JList stationList;
    private javax.swing.JScrollPane stationScrollPane;
    private javax.swing.JCheckBox studentSelectionCheckBox;
    private javax.swing.ButtonGroup submitionButtonGroup;
    private javax.swing.JLabel submitionLabel;
    private javax.swing.JLabel triesCountedLabel;
    private javax.swing.JTextField triesCountedText;
    private javax.swing.JLabel unansweredPointsLabel;
    private javax.swing.JTextField unansweredPointsText;
    private javax.swing.JCheckBox unlimitedTriesCheckbox;
    private javax.swing.JRadioButton yesRadioButton;
    // End of variables declaration//GEN-END:variables
}
