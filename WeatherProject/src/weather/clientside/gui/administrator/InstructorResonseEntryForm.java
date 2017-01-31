package weather.clientside.gui.administrator;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import weather.ApplicationControlSystem;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.AnswerType;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.ForecasterLessonGrader;
import weather.common.data.forecasterlesson.InstructorResponse;
import weather.common.data.forecasterlesson.MissingWebGradingDataRecord;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Station;
import weather.common.dbms.DBMSInstructorResponseManager;
import weather.common.dbms.DBMSMissingDataRecordManager;
import weather.common.gui.component.BUDialog;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherLogger;
import weather.common.utilities.WebGradingTools;

/**
 * This is the form that allows instances of <code>InstructorResponse</code>
 * to be managed.
 * @author Brian Bankes
 */
public class InstructorResonseEntryForm extends BUDialog {
    
    private SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy");
    
    //Flag so cards don't change before initialization is done.
    private boolean holdCardChange = true;
    
    //Flag to note when a panel is initiialized. (Each combo box change causes a
    //new initislization.)
    private boolean panelInitialized = false;
    
    //Flag to note whether of not responses have been added or removed.  This is
    //used to determine if any grades must be cleard when closing.
    private boolean changesMade = false;
    
    //Database managers.
    private final DBMSInstructorResponseManager responseManager;
    private final DBMSMissingDataRecordManager missingDataManager;
    
    //The choosen lesson.
    private ForecasterLesson lesson;
    
    //Array of questions in chosen lesson.
    private ArrayList<Question> questions;
    
    //Whather or not the web page for grading the chosen date exists and the
    //data has been retrieved.
    private boolean isWebDataPresent;
    
    //The attempts that must be graded or regraded.
    private ArrayList<Attempt> attemptsToGrade;
    
    //The chosen station.
    private Station station;
    
    //The chosen date.
    private Date date;
    
    //The question being shown.
    private Question currentQuestion;
    
    //The answer to the question being shown per the Internet as a String.
    private String webAnswerString;
    
    //The currently-saved instruvtor responses for the question being shown.
    private ArrayList<InstructorResponse> responses;
    
    //Structure to hold database webAnswers.
    private HashMap<String, String> answerMap;
    
    //String to hold "None Of The Above."
    private final String noneString = PropertyManager
            .getGeneralProperty("noneString");

    /**
     * Creates new form InstructorResonseEntryForm
     * 
     * @param appControl The program's <code>ApplicationControlSystem</code>.
     * @param attemptsToGrade An <code>ArrayList</code> holding the instances
     * of type <code>Attempt</code> to be graded or regraded.
     * @param isWebDataPresent Whether or not the web page with the data to do
     * the grading exists and the data has been retrieved.
     * @param lesson The <code>ForecasterLesson</code> for which the
     * instances of <code>InstructorResponse</code> are being modified.
     * @param station The <code>Station</code> for which the instances of
     * <code>InstructorResponse</code> are being modified.
     * @param date The <code>Date</code> for which the instances of
     * <code>InstructorResponse</code> are being modified.
     * @param isRegrading True if the grading button should show "Regrade
     * Assignment;" False otherwise.
     */
    public InstructorResonseEntryForm(ApplicationControlSystem appControl,
            ArrayList<Attempt> attemptsToGrade, boolean isWebDataPresent,
            ForecasterLesson lesson, Station station, Date date,
            boolean isRegrading) {
        super(appControl);
        responseManager = appControl.getDBMSSystem()
                .getInstructorResponseManager();
        missingDataManager = appControl.getDBMSSystem()
                .getMissingDataRecordManager();
        this.station = station;
        this.date = date;
        this.lesson = lesson;
        this.attemptsToGrade = attemptsToGrade;
        this.isWebDataPresent = isWebDataPresent;
        
        //Get webAnswers from Internet.
        loadStationAndAnswerMap();
        
        initComponents();
        
        //Set infomation label.
        String lebelTest = "<html><center>" 
                + "<b>Lesson:</b> " + lesson.getLessonName() + "<br/>"
                + "<b>Station Code:</b> " + station.getStationId() 
                + " <b>Date:</b> " + df.format(date)
                + "</center></html>"; 
        infoLabel.setText(lebelTest);
        
        //Fill guestion list.
        questions = lesson.getQuestions();
        
        for (Question question : questions) {
            int itemNumber = question.getQuestionNumber() + 1;
            
            String description = question.getQuestionName();
            String fullItem = "Item #" + itemNumber + ": "
                    + description;
            
            questionComboBox.addItem(fullItem);
        }
        
        //Change form title if web page does not exist.
        if (!isWebDataPresent) {
            this.setTitle("Supply Missing Answers");
        }
        
        //Change button label if not regrading.
        if(!isRegrading) {
            regradeButton.setText("Grade Assignment");
        }
        
        //Enable combobox and load first question.
        holdCardChange = false;
        setCardPanel();
        
        int width = 424 + this.getInsets().left + this.getInsets().right;
        int height = 459 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(false);
    }

    /**
     * Sets the card panel to show the current question.
     */
    private void setCardPanel() {
        //Set initialization flag, so check box code does not fire.
        panelInitialized = false;
        
        //Get current question.
        currentQuestion = questions.get(questionComboBox.getSelectedIndex());
        
        //Set question label.
        String questionText = "<html><b>Question: </b>" + currentQuestion
                .getQuestionText() + "</html>";
        questionLabel.setText(questionText);
        
        //Get correct card.
        CardLayout cl = (CardLayout)cardPanel.getLayout();
        if (currentQuestion.getAnswerType() == AnswerType.TextField) {
            cl.show(cardPanel, "textCard");
        } else if (currentQuestion.getAnswerType() == AnswerType.RadioButton) {
            cl.show(cardPanel, "radioCard");
        } else {  //checkbox guestion
            cl.show(cardPanel, "checkboxCard");
        }
        
        //Load possible guestion webAnswers.
        if (currentQuestion.getAnswerType() == AnswerType.CheckBox) {
            ArrayList<Answer> answers = currentQuestion.getAnswers();
            JCheckBox thisCheckBox;
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        thisCheckBox = checkBox1;
                        break;
                    case 1:
                        thisCheckBox = checkBox2;
                        break;
                    case 2:
                        thisCheckBox = checkBox3;
                        break;
                    default:
                        thisCheckBox = checkBox4;
                }
                if (answers.size() > i) {
                    thisCheckBox.setText(answers.get(i).getAnswerText());
                    thisCheckBox.setActionCommand(answers.get(i)
                            .getAnswerValue());
                    thisCheckBox.setVisible(true);
                } else {
                    thisCheckBox.setVisible(false);
                }
            }
        } else if (currentQuestion.getAnswerType() == AnswerType.RadioButton) {
            ArrayList<Answer> answers = currentQuestion.getAnswers();
            JCheckBox thisCheckBox;
            for (int i = 0; i < 8; i++) {
                switch (i) {
                    case 0:
                        thisCheckBox = radioChoice1;
                        break;
                    case 1:
                        thisCheckBox = radioChoice2;
                        break;
                    case 2:
                        thisCheckBox = radioChoice3;
                        break;
                    case 3:
                        thisCheckBox = radioChoice4;
                        break;
                    case 4:
                        thisCheckBox = radioChoice5;
                        break;
                    case 5:
                        thisCheckBox = radioChoice6;
                        break;
                    case 6:
                        thisCheckBox = radioChoice7;
                        break;
                    default:
                        thisCheckBox = radioChoice8;
                }
                if (answers.size() > i) {
                    thisCheckBox.setText(answers.get(i).getAnswerText());
                    thisCheckBox.setActionCommand(answers.get(i)
                            .getAnswerValue());
                    thisCheckBox.setVisible(true);
                } else {
                    thisCheckBox.setVisible(false);
                }
            }
            
            //Set label to instructions
            radioPanelStatusLabel.setHorizontalAlignment(SwingConstants
                    .LEADING);
            String labelText = "<html>You may select and deselect acceptable "
                    + "responses, but answers from the Internet cannot be "
                    + "deselected.</html>";
            radioPanelStatusLabel.setText(labelText);
        }
        
        //Get Internet answers as String if map is not null.
        if (answerMap == null) {
            webAnswerString = null;
        } else {
            webAnswerString = answerMap.get(currentQuestion.getDataKey());
        }
        
        //Get instructor responses from database;
        responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());
        
        //Set-up panel.
        if (currentQuestion.getAnswerType() == AnswerType.TextField) {
            if (webAnswerString != null) {
                //Change webAnswerString if nescessary.
                standardizeWebStringBange();
                originalRangeDataLabel.setText(webAnswerString);
            } else {
                originalRangeDataLabel.setText("<None>");
            }
            setTextBoxPanel();
        } else if (currentQuestion.getAnswerType() == AnswerType.RadioButton) {
            //Stays empty if wab data is missig.
            ArrayList<String> webAnswers = new ArrayList<>();
            
            if (webAnswerString != null) {
                //Parse web string.
                String current = webAnswerString;
                int breakpt = 0;

                //Obtain all correct answers from web into array list.
                while (true) {
                    current = current.substring(breakpt);
                    if (current.indexOf('|', 0) == -1) {
                        break; //Here on last term in concatanation.
                    }
                    breakpt = current.indexOf('|', 0);
                    webAnswers.add(current.substring(0, breakpt));
                    breakpt++;
                }
                webAnswers.add(current); //Adding last value after loop break.
            }
            
            //Set radio button choices.
            JCheckBox thisCheckBox;
            for (int i = 0; i < 8; i++) {
                switch (i) {
                    case 0:
                        thisCheckBox = radioChoice1;
                        break;
                    case 1:
                        thisCheckBox = radioChoice2;
                        break;
                    case 2:
                        thisCheckBox = radioChoice3;
                        break;
                    case 3:
                        thisCheckBox = radioChoice4;
                        break;
                    case 4:
                        thisCheckBox = radioChoice5;
                        break;
                    case 5:
                        thisCheckBox = radioChoice6;
                        break;
                    case 6:
                        thisCheckBox = radioChoice7;
                        break;
                    default:
                        thisCheckBox = radioChoice8;
                }
                
                //Check if this is a web answer.
                if (webAnswers.contains(thisCheckBox.getActionCommand())) {
                    thisCheckBox.setSelected(true);
                    thisCheckBox.setEnabled(false);
                } else {
                    thisCheckBox.setSelected(false);
                    thisCheckBox.setEnabled(true);
                    
                    //Check if this is a saved instructor responses.
                    for (InstructorResponse response : responses) {
                        if (response.getAnswer().equals(thisCheckBox
                                .getActionCommand())) {
                            thisCheckBox.setSelected(true);
                            break;
                        }
                    }
                }
            }
        } else {  //checkbox guestion
            //Set to answer from web, so parse web string if peesent.
            //Stays empty if wab data is missig.
            ArrayList<String> webAnswers = new ArrayList<>();
            
            if (webAnswerString != null) {
                //Parse web string.
                String current = webAnswerString;
                int breakpt = 0;

                //Obtain all correct answers from web into array list.
                while (true) {
                    current = current.substring(breakpt);
                    if (current.indexOf(',', 0) == -1) {
                        break; //Here on last term in concatanation.
                    }
                    breakpt = current.indexOf(',', 0);
                    webAnswers.add(current.substring(0, breakpt));
                    breakpt++;
                }
                webAnswers.add(current); //Adding last value after loop break.
            }
            
            //Set check box options.
            JCheckBox thisCheckBox;
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        thisCheckBox = checkBox1;
                        break;
                    case 1:
                        thisCheckBox = checkBox2;
                        break;
                    case 2:
                        thisCheckBox = checkBox3;
                        break;
                    default:
                        thisCheckBox = checkBox4;
                }
                if (webAnswers.contains(thisCheckBox.getActionCommand())) {
                    thisCheckBox.setSelected(true);
                } else {
                    thisCheckBox.setSelected(false);
                }
            }
            
            //Set controls.
            if (webAnswerString != null) {
                setCheckboxPanelControls(webAnswerString);
            } else {
                //Set to no answer if no web string.
                setCheckboxPanelControls("");
            }
        }
        
        //Code on radio button panel can now fire.
        panelInitialized = true;
    }
    
    /**
     * Sets the controls of the text box input panel according the whether or
     * not an instructor response is saved for the question while showing all
     * saved date for the question.
     */
    private void setTextBoxPanel() {
        newRangeTextField.setText("");
        String instructions;
        if (responses.isEmpty()) {
            instructorRangeDataLabel.setText("<None>");
            updateTextBoxRangeButton.setText("Add Supplied Range");
            deleteTextBoxRangeButton.setEnabled(false);
            instructions = "<html>To add a new range, enter it in the input "
                    + "box and press \"Add Supplied Range.\"  The range wll "
                    + "not be saved until the button is pressed.</html>";
        } else {
            instructorRangeDataLabel.setText(responses.get(0).getAnswer());
            updateTextBoxRangeButton.setText("Update Supplied Range");
            deleteTextBoxRangeButton.setEnabled(true);
            instructions = "<html>To update the range, enter the new range in "
                    + "the input box and press \"Update Supplied Range.\"  The "
                    + "range wll not be updated until the button is pressed."
                    + "</html>";
        }
        textPanelInstructionLabel.setText(instructions);
    }
    
    /**
     * Function to test if the enter range on the text box panel is valid.
     * 
     * @return True if the range is valid; False otherwise. 
     */
    private boolean validateRange() {
        String inputString = newRangeTextField.getText();
        
        //To store range form database.
        int databaseMinimumValue;
        int databaseMaximumValue;
        
        //To store user input.
        int enteredMinimumValue;
        int enteredMaximumValue;
        
        //Make sure input is of the form low:high.
        try {
            int firstColonIndex = inputString.indexOf(":");
            int lastColonIndex = inputString.lastIndexOf(":");
            
            //Test for exactly one colon and that it is not on an end. 
            if (firstColonIndex != lastColonIndex || firstColonIndex == -1
                    || firstColonIndex == 0 
                    || firstColonIndex == inputString.length() - 1) {
                throw new Exception();
            }
            
            //Try to parse integers.
            String lowString = inputString.substring(0, firstColonIndex);
            String highString = inputString.substring(firstColonIndex + 1);
            enteredMinimumValue = Integer.parseInt(lowString);
            enteredMaximumValue = Integer.parseInt(highString);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "The input must be of the form "
                    + "low:high\nwhere low and high are integers.", 
                    "Input Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        //Make sure the start of the range is less thsn its end.
        if (enteredMinimumValue >= enteredMaximumValue) {
            JOptionPane.showMessageDialog(this, "The start of the range must "
                    + "be less than its end.", "Input Error", 
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        //Make sure the range is within thet specified by the question.
        
        //The "answers" are the minimum and maximum values the user may enter.
        ArrayList<Answer> answers = currentQuestion.getAnswers();

        //Get the desired values from the database.
        try {
            databaseMinimumValue = Integer.parseInt(answers.get(0)
                    .getAnswerValue());
            databaseMaximumValue = Integer.parseInt(answers.get(1)
                    .getAnswerValue());
        } catch (Exception ex) {
            databaseMinimumValue = 0;
            databaseMaximumValue = 9999;
        }
        
        //Do testing.
        if (enteredMinimumValue < databaseMinimumValue 
                || enteredMaximumValue > databaseMaximumValue) {
            JOptionPane.showMessageDialog(this, "The enterd range must be "
                    + "within the range\nfrom " + databaseMinimumValue + " to "
                    + databaseMaximumValue + " inclusive.", "Input Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        //If all test are passed, return true.
        return true;
    }
    
    /**
     * The function standardizes the answer sting from the grading date so that
     * it does not show a range that extends beyond the permitted input.  It 
     * alters <code>webAnswerString<code> and should only be called if that 
     * wariable is not null and is of the form "low:high".
     */
    private void standardizeWebStringBange() {
        int colonIndex = webAnswerString.indexOf(":");
        
        //To store range form database.
        int databaseMinimumValue;
        int databaseMaximumValue;

        //To store values from webAnswerString.
        int stringMinimumValue;
        int stringMaximumValue;

        //Parse webAnswerString.
        try {
            //Try to parse integers.
            String lowString = webAnswerString.substring(0, colonIndex);
            String highString = webAnswerString.substring(colonIndex + 1);
            stringMinimumValue = Integer.parseInt(lowString);
            stringMaximumValue = Integer.parseInt(highString);
        } catch (Exception e) {
            WeatherLogger.log(Level.SEVERE, "Could not parse web answer.");
            return;
        }

        //Get the desired values from the database.
        //The "answers" are the minimum and maximum values that can be shown.
        ArrayList<Answer> answers = currentQuestion.getAnswers();

        try {
            databaseMinimumValue = Integer.parseInt(answers.get(0)
                    .getAnswerValue());
            databaseMaximumValue = Integer.parseInt(answers.get(1)
                    .getAnswerValue());
        } catch (Exception ex) {
            databaseMinimumValue = 0;
            databaseMaximumValue = 9999;
        }
        
        //Change webAnswerString if nescessary.
        boolean changed = false;
        if (stringMinimumValue < databaseMinimumValue) {
            stringMinimumValue = databaseMinimumValue;
            changed = true;
        }
        if (stringMaximumValue > databaseMaximumValue) {
            stringMaximumValue = databaseMaximumValue;
            changed = true;
        }
        if (changed) {
            webAnswerString = "" + stringMinimumValue + ":" 
                    + stringMaximumValue;
        }
    }
    
    /**
     * Adds the answer corresponding to a given check box, a radio button
     * choice, as an InstructorResponse.
     * 
     * @param checkBox The given check box.
     */
    private void addRadioChoice(JCheckBox checkBox) {
        InstructorResponse newResponse = new InstructorResponse(null,
                currentQuestion.getQuestionID(),
                checkBox.getActionCommand(), date,
                station.getStationId());
        responseManager.insertResponse(newResponse);

        //Upate databse responses.
        responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());
        
        //Update status label.
        radioPanelStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String labelText = "<html><center>Added Response:<br/>" +
                checkBox.getText() + "</center></html>";
        radioPanelStatusLabel.setText(labelText);
        
        //Set flag for closing.
        changesMade = true;
    } 
    
    /**
     * Deletes the answer corresponding to a given check box, a radio button
     * choice, as an InstructorResponse.
     *
     * @param checkBox The given check box.
     */
    private void deleteRadioChoice(JCheckBox checkBox) {
        for (InstructorResponse response : responses) {
            if (response.getAnswer().equals(checkBox.getActionCommand())) {
                responseManager.deleteResponse(response);
                break;
            }
        }

        //Upate databse responses.
        responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());

        //Update status label.
        radioPanelStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String labelText = "<html><center>Deleted Response:<br/>"
                + checkBox.getText() + "</center></html>";
        radioPanelStatusLabel.setText(labelText);
        
         //Set flag for closing.
        changesMade = true;
    }
    
    /**
     * Sets the label and button on the check box panel according the whether 
     * the supplied string (e. g. "1,3" or "2") is the answer from the web,
     * an answer in the database, or neither.
     * 
     * @param answerString The answer to test as a String, which may be the 
     * empty sting.
     */
    private void setCheckboxPanelControls(String answerString) {
        //Handle empty string (no selections made.)
        if (answerString.equals("")) {
            checkboxStatusLabel.setText("Please make a selection.");
            updateCheckboxChoiceButton.setVisible(false);
            return;
        }
        
        if (webAnswerString != null
                && answerString.equals(webAnswerString)) {
            checkboxStatusLabel.setText("You cannot remove the answer taken "
                    + "from the Internet.");

            //Hide update button, as this is web choice.
            updateCheckboxChoiceButton.setVisible(false);
        } else {
            updateCheckboxChoiceButton.setVisible(true);
            //Check if this is a saved instructor responses.
            for (InstructorResponse response : responses) {
                if (response.getAnswer().equals(answerString)) {
                    checkboxStatusLabel.setText("This is a saved response.");
                    updateCheckboxChoiceButton.setText("Remove This Choice");
                    return;
                }
            }
            
            //If the code gets here, the response is not saved.
            checkboxStatusLabel.setText("This is NOT a saved response.");
            updateCheckboxChoiceButton.setText("Add This Choice");
        }
    }
    
    /**
     * Makes a String representation of the response indicated by the selected
     * boxes on the check box panel.  Examples include "1.3", "2", and "" (the 
     * last means no boxes are selected). 
     * 
     * @return A String representation of the response indicated by the selected
     * boxes on the check box panel
     */
    private String makeAnswerStringFromSelectedCheckboxes() {
        StringBuilder sb = new StringBuilder();
        JCheckBox thisCheckBox;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    thisCheckBox = checkBox1;
                    break;
                case 1:
                    thisCheckBox = checkBox2;
                    break;
                case 2:
                    thisCheckBox = checkBox3;
                    break;
                default:
                    thisCheckBox = checkBox4;
            }
            if (thisCheckBox.isSelected()) {
                sb.append(","); //will remove leading comma later.
                sb.append(thisCheckBox.getActionCommand());
            }
        }
        
        //Check if no boxes were checked.
        if (sb.length() == 0) {
            return "";
        }
        
        //Remove leading comma while returning.
        return sb.substring(1);
    }
    
    /**
     * Loads HashMap and Station class variable with webAnswers from database.
     */
    private void loadStationAndAnswerMap() {
        //If the web data does not exist, the map is set to null
        if (isWebDataPresent) {
            //Check if data is in database.
            String stationCode = station.getStationId();
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            if (appControl.getDBMSSystem().getForecasterStationDataManager()
                    .getStation(stationCode, date) == null) {
                //If null was returned null when the code attempted to obtain
                //the station, it means it is not in DB for the date, so run 
                //parser to obtain all station data for this date.
                WebGradingTools.parseWebAnswers(appControl.getDBMSSystem(), cal, 
                        stationCode);
            }

            //Obtain station with data from database.
            station = appControl.getDBMSSystem().getForecasterStationDataManager()
                    .getStation(stationCode, date);

            //Get hash map containing answer data, indexed by data key coming 
            //from the corresponding questions.
            answerMap = station.getData();
        } else {
            answerMap = null;
        }
    }
    
    /**
     * A helper function to regrade all student attempts for this instance's 
     * lesson, date, and station which are already in the database.
     * 
     * @return True if operation was successful; False otherwise.
     */
    private boolean regradeAttempts() {
        /**
         * Check if instructor responses must be entered, which happens if there
         * is no web page and not all the questions have instructor responses.
         */
        if(!isWebDataPresent && !areAllQuestionsAnsweredByInstructor()) {
            JOptionPane.showMessageDialog(this, 
                    "Since the grading data could not be retrieved from the\n"
                    + "Internet, you must provide responses to all of the\n"
                    + "questions.", "Please Provide Answers", JOptionPane
                    .INFORMATION_MESSAGE);
            return false;
        }
        
        //Must update missing record tabe if web page does not exist.
        if (!isWebDataPresent) {
            MissingWebGradingDataRecord record = missingDataManager
                    .getRecordByLessonAndDateAndStation(lesson, date,
                    station.getStationId());
            record.setIsInstructorDataSet(true);
            if (record.hasDatabaseId()) {
                missingDataManager.updateRecord(record);
            } else {
                missingDataManager.insertRecord(record);
            }
        } 
        
        //Regrade all lesson to which the new response set applies.
        for (Attempt attempt : attemptsToGrade) {
            //This attempt at grading will succeed.
            ForecasterLessonGrader.tryToGradeAttempt(appControl.getDBMSSystem(), 
                    lesson, attempt);
        }
 
        return true;
    }
    
    /**
     * Helper function to determine if an instructor response exists for all of
     * the questions in a lesson.
     * 
     * @return True if an instructor response exists for all of the questions in
     * a lesson; False otherwise.
     */
    private boolean areAllQuestionsAnsweredByInstructor() {
        for(Question question : questions) {
            if (responseManager.getResponsesByQuestionAndDateAndStation(
                question, date, station.getStationId()).isEmpty()) {
                //Question has no instructor response, so return false.
                return false;
            }
        }
        
        //All questions answered, so return ture.
        return true;
    }
    
    /**
     * Helper function to be called when the user attempts to close the form.  
     * Its purpose is to ensure all necessary "regrading" is done.
     */
    private void attemptClose() {
        if (changesMade) {
            JOptionPane.showMessageDialog(this, "There are attempts that must "
                    + "be regraded before\nthis form can close.", 
                    "Plese Regrade Attempts", JOptionPane.INFORMATION_MESSAGE);
        } else {
            dispose();
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

        infoLabel = new javax.swing.JLabel();
        questionComboBox = new javax.swing.JComboBox<String>();
        selectLabel = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        textCardPanel = new javax.swing.JPanel();
        originalRangeLabel = new javax.swing.JLabel();
        instructorRangeLabel = new javax.swing.JLabel();
        originalRangeDataLabel = new javax.swing.JLabel();
        newRangeTextField = new javax.swing.JTextField();
        updateTextBoxRangeButton = new javax.swing.JButton();
        deleteTextBoxRangeButton = new javax.swing.JButton();
        instructorRangeDataLabel = new javax.swing.JLabel();
        newDataLabel = new javax.swing.JLabel();
        textPanelInstructionLabel = new javax.swing.JLabel();
        radioCardPanel = new javax.swing.JPanel();
        radioChoice1 = new javax.swing.JCheckBox();
        radioChoice2 = new javax.swing.JCheckBox();
        radioChoice3 = new javax.swing.JCheckBox();
        radioChoice4 = new javax.swing.JCheckBox();
        radioChoice5 = new javax.swing.JCheckBox();
        radioChoice6 = new javax.swing.JCheckBox();
        radioChoice7 = new javax.swing.JCheckBox();
        radioChoice8 = new javax.swing.JCheckBox();
        radioPanelStatusLabel = new javax.swing.JLabel();
        checkboxCardPanel = new javax.swing.JPanel();
        checkBox1 = new javax.swing.JCheckBox();
        checkBox2 = new javax.swing.JCheckBox();
        checkBox3 = new javax.swing.JCheckBox();
        checkBox4 = new javax.swing.JCheckBox();
        checkboxStatusLabel = new javax.swing.JLabel();
        updateCheckboxChoiceButton = new javax.swing.JButton();
        checkboxPanelInstructionLabel = new javax.swing.JLabel();
        questionLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        regradeButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Modify Instructor Responses");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        infoLabel.setToolTipText("");
        infoLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(infoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 400, 32));

        questionComboBox.setMaximumRowCount(12);
        questionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                questionComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(questionComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 72, 400, 22));

        selectLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        selectLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selectLabel.setText("Select Question:");
        getContentPane().add(selectLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 56, 400, 16));

        cardPanel.setLayout(new java.awt.CardLayout());

        textCardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        originalRangeLabel.setText("Original Range Fron Internet:");
        originalRangeLabel.setToolTipText("");
        textCardPanel.add(originalRangeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 168, 16));

        instructorRangeLabel.setText("Instrustor-Supplied Range:");
        instructorRangeLabel.setToolTipText("");
        textCardPanel.add(instructorRangeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 28, 168, 16));

        originalRangeDataLabel.setToolTipText("");
        textCardPanel.add(originalRangeDataLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 0, 205, 16));
        textCardPanel.add(newRangeTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 56, 69, 22));

        updateTextBoxRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateTextBoxRangeButtonActionPerformed(evt);
            }
        });
        textCardPanel.add(updateTextBoxRangeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 90, 166, 25));

        deleteTextBoxRangeButton.setText("Clear Supplied Range");
        deleteTextBoxRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTextBoxRangeButtonActionPerformed(evt);
            }
        });
        textCardPanel.add(deleteTextBoxRangeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(206, 90, 166, 25));

        instructorRangeDataLabel.setToolTipText("");
        textCardPanel.add(instructorRangeDataLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 28, 205, 16));

        newDataLabel.setText("New Instrustor-Supplied Range:");
        newDataLabel.setToolTipText("");
        textCardPanel.add(newDataLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 56, 183, 16));

        textPanelInstructionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textPanelInstructionLabel.setToolTipText("");
        textCardPanel.add(textPanelInstructionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 127, 400, 48));

        cardPanel.add(textCardPanel, "textCard");

        radioCardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        radioChoice1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioChoice1.setText("Choice 1");
        radioChoice1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice1ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 25));

        radioChoice2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioChoice2.setText("Choice 2");
        radioChoice2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice2ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 400, 25));

        radioChoice3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioChoice3.setText("Choice 3");
        radioChoice3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice3ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 400, 25));

        radioChoice4.setText("Choice 4");
        radioChoice4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice4ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 75, 400, 25));

        radioChoice5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioChoice5.setText("Choice 5");
        radioChoice5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice5ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 400, 25));

        radioChoice6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioChoice6.setText("Choice 6");
        radioChoice6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice6ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 125, 400, 25));

        radioChoice7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioChoice7.setText("Choice 7");
        radioChoice7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice7ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 400, 25));

        radioChoice8.setText("Choice 8");
        radioChoice8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioChoice8ActionPerformed(evt);
            }
        });
        radioCardPanel.add(radioChoice8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 175, 400, 25));
        radioCardPanel.add(radioPanelStatusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 212, 400, 32));

        cardPanel.add(radioCardPanel, "radioCard");

        checkboxCardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkBox1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox1.setText("Choice 1");
        checkBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox1ActionPerformed(evt);
            }
        });
        checkboxCardPanel.add(checkBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 25));

        checkBox2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox2.setText("Choice 2");
        checkBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox2ActionPerformed(evt);
            }
        });
        checkboxCardPanel.add(checkBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 400, 25));

        checkBox3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkBox3.setText("Choice 3");
        checkBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox3ActionPerformed(evt);
            }
        });
        checkboxCardPanel.add(checkBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 400, 25));

        checkBox4.setText("Choice 4");
        checkBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox4ActionPerformed(evt);
            }
        });
        checkboxCardPanel.add(checkBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 75, 400, 25));

        checkboxStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        checkboxStatusLabel.setToolTipText("");
        checkboxCardPanel.add(checkboxStatusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 112, 400, 16));

        updateCheckboxChoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateCheckboxChoiceButtonActionPerformed(evt);
            }
        });
        checkboxCardPanel.add(updateCheckboxChoiceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 140, 150, 25));

        checkboxPanelInstructionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        checkboxPanelInstructionLabel.setText("<html>To add and remove answer combinations, setect them by selecting and deselecting the check boxes.</html>");
        checkboxPanelInstructionLabel.setToolTipText("");
        checkboxCardPanel.add(checkboxPanelInstructionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 177, 400, 32));

        cardPanel.add(checkboxCardPanel, "checkboxCard");

        getContentPane().add(cardPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 166, 400, 244));
        cardPanel.getAccessibleContext().setAccessibleName("");
        cardPanel.getAccessibleContext().setAccessibleDescription("");

        questionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        questionLabel.setToolTipText("");
        questionLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(questionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 106, 400, 48));

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));

        regradeButton.setText("Regrade Assignment");
        regradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regradeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(regradeButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 422, 400, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void questionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_questionComboBoxActionPerformed
        if (holdCardChange) {
            return;
        }
        setCardPanel();
    }//GEN-LAST:event_questionComboBoxActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        attemptClose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void checkBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox1ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if (checkBox1.isSelected() && checkBox1.getText()
                .equals(noneString)) {
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
        } else if (checkBox1.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox2.getText().equals(noneString)) {
                checkBox2.setSelected(false);
            }
            if (checkBox3.getText().equals(noneString)) {
                checkBox3.setSelected(false);
            }
            if (checkBox4.getText().equals(noneString)) {
                checkBox4.setSelected(false);
            }
        }
        
        //Set label and button.
        setCheckboxPanelControls(makeAnswerStringFromSelectedCheckboxes());
    }//GEN-LAST:event_checkBox1ActionPerformed

    private void checkBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox2ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if(checkBox2.isSelected() && checkBox2.getText()
                .equals(noneString)) {
            checkBox1.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
        } else if (checkBox2.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox1.getText().equals(noneString)) {
                checkBox1.setSelected(false);
            }
            if (checkBox3.getText().equals(noneString)) {
                checkBox3.setSelected(false);
            }
            if (checkBox4.getText().equals(noneString)) {
                checkBox4.setSelected(false);
            }
        }
        
        //Set label and button.
        setCheckboxPanelControls(makeAnswerStringFromSelectedCheckboxes());
    }//GEN-LAST:event_checkBox2ActionPerformed

    private void checkBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox3ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if (checkBox3.isSelected() && checkBox3.getText()
                .equals(noneString)) {
            checkBox2.setSelected(false);
            checkBox1.setSelected(false);
            checkBox4.setSelected(false);
        } else if (checkBox3.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox2.getText().equals(noneString)) {
                checkBox2.setSelected(false);
            }
            if (checkBox1.getText().equals(noneString)) {
                checkBox1.setSelected(false);
            }
            if (checkBox4.getText().equals(noneString)) {
                checkBox4.setSelected(false);
            }
        }
        
        //Set label and button.
        setCheckboxPanelControls(makeAnswerStringFromSelectedCheckboxes());
    }//GEN-LAST:event_checkBox3ActionPerformed

    private void checkBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox4ActionPerformed
        //If this checkbox is "None Of The Above" and is selected;
        //we must deselect the other checkboxes.
        if(checkBox4.isSelected() && checkBox4.getText()
                .equals(noneString)) {
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox1.setSelected(false);
        } else if (checkBox4.isSelected()) {
            //Unselect any "None Of The Above" checkboxes.
            if (checkBox2.getText().equals(noneString)) {
                checkBox2.setSelected(false);
            }
            if (checkBox3.getText().equals(noneString)) {
                checkBox3.setSelected(false);
            }
            if (checkBox1.getText().equals(noneString)) {
                checkBox1.setSelected(false);
            }
        }
        
        //Set label and button.
        setCheckboxPanelControls(makeAnswerStringFromSelectedCheckboxes());
    }//GEN-LAST:event_checkBox4ActionPerformed

    private void updateCheckboxChoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCheckboxChoiceButtonActionPerformed
        //Check if this is a saved instructor responses.
        //If so, remve it.
        for (InstructorResponse response : responses) {
            if (response.getAnswer()
                    .equals(makeAnswerStringFromSelectedCheckboxes())) {
                responseManager.deleteResponse(response);
                JOptionPane.showMessageDialog(this, 
                        "This response has been deleted.", "Response Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                
                //Upate databse responses.
                responses = responseManager.getResponsesByQuestionAndDateAndStation(
                        currentQuestion, date, station.getStationId());
                
                //Set label and button.
                setCheckboxPanelControls(
                        makeAnswerStringFromSelectedCheckboxes());
                
                 //Set flag for closing.
                changesMade = true;
                
                return;
            }
        }
        
        //If the code gete here it is doing an insert.
        InstructorResponse newResponse = new InstructorResponse(null,
                currentQuestion.getQuestionID(), 
                makeAnswerStringFromSelectedCheckboxes(), date, 
                station.getStationId());
        responseManager.insertResponse(newResponse);
        JOptionPane.showMessageDialog(this,
                "This response has been added.", "Response Added",
                JOptionPane.INFORMATION_MESSAGE);

        //Upate databse responses.
        responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());

        //Set label and button.
        setCheckboxPanelControls(makeAnswerStringFromSelectedCheckboxes());
        
         //Set flag for closing.
        changesMade = true;
    }//GEN-LAST:event_updateCheckboxChoiceButtonActionPerformed

    private void radioChoice1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice1ActionPerformed
        if (panelInitialized) {
            if (radioChoice1.isSelected()) {
                addRadioChoice(radioChoice1);
            } else {
                deleteRadioChoice(radioChoice1);
            }
        }
    }//GEN-LAST:event_radioChoice1ActionPerformed

    private void radioChoice2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice2ActionPerformed
        if (panelInitialized) {
            if (radioChoice2.isSelected()) {
                addRadioChoice(radioChoice2);
            } else {
                deleteRadioChoice(radioChoice2);
            }
        }
    }//GEN-LAST:event_radioChoice2ActionPerformed

    private void radioChoice3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice3ActionPerformed
        if (panelInitialized) {
            if (radioChoice3.isSelected()) {
                addRadioChoice(radioChoice3);
            } else {
                deleteRadioChoice(radioChoice3);
            }
        }
    }//GEN-LAST:event_radioChoice3ActionPerformed

    private void radioChoice4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice4ActionPerformed
        if (panelInitialized) {
            if (radioChoice4.isSelected()) {
                addRadioChoice(radioChoice4);
            } else {
                deleteRadioChoice(radioChoice4);
            }
        }
    }//GEN-LAST:event_radioChoice4ActionPerformed

    private void radioChoice5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice5ActionPerformed
        if (panelInitialized) {
            if (radioChoice5.isSelected()) {
                addRadioChoice(radioChoice5);
            } else {
                deleteRadioChoice(radioChoice5);
            }
        }
    }//GEN-LAST:event_radioChoice5ActionPerformed

    private void radioChoice6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice6ActionPerformed
        if (panelInitialized) {
            if (radioChoice6.isSelected()) {
                addRadioChoice(radioChoice6);
            } else {
                deleteRadioChoice(radioChoice6);
            }
        }
    }//GEN-LAST:event_radioChoice6ActionPerformed

    private void radioChoice7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice7ActionPerformed
        if (panelInitialized) {
            if (radioChoice7.isSelected()) {
                addRadioChoice(radioChoice7);
            } else {
                deleteRadioChoice(radioChoice7);
            }
        }
    }//GEN-LAST:event_radioChoice7ActionPerformed

    private void radioChoice8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioChoice8ActionPerformed
        if (panelInitialized) {
            if (radioChoice8.isSelected()) {
                addRadioChoice(radioChoice8);
            } else {
                deleteRadioChoice(radioChoice8);
            }
        }
    }//GEN-LAST:event_radioChoice8ActionPerformed

    private void updateTextBoxRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateTextBoxRangeButtonActionPerformed
        if(!validateRange()) {
            return;
        }
        
        //Make new response.
        InstructorResponse newResponse = new InstructorResponse(null,
                currentQuestion.getQuestionID(),
                newRangeTextField.getText(), date,
                station.getStationId());
        
        //Check for and hande case of add.
        if (responses.isEmpty()) {
            responseManager.insertResponse(newResponse);
            JOptionPane.showMessageDialog(this,
                "This range has been added.", "Range Added",
                JOptionPane.INFORMATION_MESSAGE);

            //Upate databse responses.
            responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());
        
            //Update form.
            setTextBoxPanel();
            
             //Set flag for closing.
            changesMade = true;
            
            return;
        }
        
        //If the code gets here this is an update, so make sure user wants it.
        if(JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "replace the existing range\nwith a new one?", 
                "Confirm Update",  JOptionPane.YES_NO_OPTION) 
                == JOptionPane.NO_OPTION) {
            return;
        }
        
        //Start update by removing old range.
        responseManager.deleteResponse(responses.get(0));
        
        //Add new range.
        responseManager.insertResponse(newResponse);
        JOptionPane.showMessageDialog(this, "This range has been updated.", 
                "Range Updated", JOptionPane.INFORMATION_MESSAGE);

        //Upate databse responses.
        responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());

        //Update form.
        setTextBoxPanel();
        
         //Set flag for closing.
        changesMade = true;
    }//GEN-LAST:event_updateTextBoxRangeButtonActionPerformed

    private void deleteTextBoxRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTextBoxRangeButtonActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "delete the instructor-supplied range?", "Confirm Delete",  
                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }
        
        responseManager.deleteResponse(responses.get(0));
        
        JOptionPane.showMessageDialog(this, "This range has been deleted.", 
                "Range Deleted", JOptionPane.INFORMATION_MESSAGE);

        //Upate databse responses.
        responses = responseManager.getResponsesByQuestionAndDateAndStation(
                currentQuestion, date, station.getStationId());

        //Update form.
        setTextBoxPanel();
        
         //Set flag for closing.
        changesMade = true;
    }//GEN-LAST:event_deleteTextBoxRangeButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        attemptClose();
    }//GEN-LAST:event_formWindowClosing

    private void regradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regradeButtonActionPerformed
        //Regrade attempts.  If the operation fails, this function nust halt.
        if (!regradeAttempts()) {
            return;
        }
        
        //Notify user.
        if(isWebDataPresent) {
            JOptionPane.showMessageDialog(this, "The lesson has been regraded.",
                    "Lesson Regraded", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "The lesson has been graded.",
                    "Lesson Graded", JOptionPane.INFORMATION_MESSAGE);
        }
        
        //Reset closing test fleg.
        changesMade = false;
    }//GEN-LAST:event_regradeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JCheckBox checkBox1;
    private javax.swing.JCheckBox checkBox2;
    private javax.swing.JCheckBox checkBox3;
    private javax.swing.JCheckBox checkBox4;
    private javax.swing.JPanel checkboxCardPanel;
    private javax.swing.JLabel checkboxPanelInstructionLabel;
    private javax.swing.JLabel checkboxStatusLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteTextBoxRangeButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel instructorRangeDataLabel;
    private javax.swing.JLabel instructorRangeLabel;
    private javax.swing.JLabel newDataLabel;
    private javax.swing.JTextField newRangeTextField;
    private javax.swing.JLabel originalRangeDataLabel;
    private javax.swing.JLabel originalRangeLabel;
    private javax.swing.JComboBox<String> questionComboBox;
    private javax.swing.JLabel questionLabel;
    private javax.swing.JPanel radioCardPanel;
    private javax.swing.JCheckBox radioChoice1;
    private javax.swing.JCheckBox radioChoice2;
    private javax.swing.JCheckBox radioChoice3;
    private javax.swing.JCheckBox radioChoice4;
    private javax.swing.JCheckBox radioChoice5;
    private javax.swing.JCheckBox radioChoice6;
    private javax.swing.JCheckBox radioChoice7;
    private javax.swing.JCheckBox radioChoice8;
    private javax.swing.JLabel radioPanelStatusLabel;
    private javax.swing.JButton regradeButton;
    private javax.swing.JLabel selectLabel;
    private javax.swing.JPanel textCardPanel;
    private javax.swing.JLabel textPanelInstructionLabel;
    private javax.swing.JButton updateCheckboxChoiceButton;
    private javax.swing.JButton updateTextBoxRangeButton;
    // End of variables declaration//GEN-END:variables
}
