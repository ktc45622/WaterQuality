package weather.common.data.forecasterlesson;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TimeZone;
import weather.common.dbms.DBMSMissingDataRecordManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.Emailer;
import weather.common.utilities.PageChecker;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WebGradingTools;

/**
 * Contains methods that with grade forecaster Lesson attempts. The public
 * methods check if an attempt is within the date range to attempt grading with 
 * web data.  The public methods also check if the instructor has provided 
 * responses for an attempt which is too old to be graded with web data.  
 * Whenever grading can be done, a private method is called to do it.
 *
 * @author Nikita Maizet
 */
public class ForecasterLessonGrader {

    private final static String UCRA_DATA_URL 
            = PropertyManager.getGeneralProperty("UCRA_DATA_URL");
    private final static int MINIMUM_GRADING_DAYS = Integer.parseInt( 
            PropertyManager.getGeneralProperty("MINIMUM_GRADING_DAYS"));
    private final static int MAXIMUM_GRADING_DAYS = Integer.parseInt( 
            PropertyManager.getGeneralProperty("MAXIMUM_GRADING_DAYS"));
    public final static String NO_ANSWER_VALUE = "X";
    
    /**
     * Tries to grade a lesson attempt by comparing user answers to values
     * stored in DB. If DB data is not present, either via the web or via
     * instructor responses, this method checks if an email should be sent to
     * the course instructor. Note that instructor responses cannot be provided
     * until either an attempt has been graded or the maximum days for checking
     * the web have elapsed. Also no grading will occur until the minimum days
     * for checking the web have elapsed.
     *
     * @param dbms The application's <code>DBMSSystemManager</code>
     * @param lesson The <code>ForecastingLesson</code> of the
     * <code>Attempt</code> to be graded
     * @param attempt The <code>Attempt</code> to be graded
     */
    public static void tryToGradeAttempt(DBMSSystemManager dbms, 
            ForecasterLesson lesson, Attempt attempt) {
//        Debug.println("Lesson: " + lesson.getLessonName());
//        Debug.println("Attempt Date: " + CalendarFormatter
//                .formatDateLong(attempt.getForecastedDate()));
//        Debug.println("Station: " + attempt.getStationCode());
//        Debug.println();
        
        //Compute the number of fully-elapsed days since the date of the lesson.
        long startOfToday = ResourceTimeManager
                .getStartOfDayFromMilliseconds(System.currentTimeMillis(), 
                TimeZone.getDefault());
        long startOfAttemptDate = attempt.getForecastedDate().getTimeInMillis();
        int daysElapsed = (int) ((startOfToday - startOfAttemptDate)
            / ResourceTimeManager.MILLISECONDS_PER_DAY);
        
        //Check if and how grading should be attempted.
        if (daysElapsed < MINIMUM_GRADING_DAYS) {
            //It is too soon to grade this lesson, so do nothing.
        } else if (daysElapsed <= MAXIMUM_GRADING_DAYS) {
            //Grade only of web data is available.
            Calendar cal = (Calendar) attempt.getForecastedDate().clone();
            Debug.println("Finding Date: " + CalendarFormatter.format(cal));

            String filename = UCRA_DATA_URL + WebGradingTools.getFormattedDate(cal)
                    + ".out";
            Debug.println("Filename: " + filename);
            //Check if answer data exists on site providing them for date thet 
            //the assignment if forecasting.
            if (PageChecker.doesPageExist(filename)) {
                //Attempt con be graded.
                String stationCode = attempt.getStationCode();
                if (dbms.getForecasterStationDataManager()
                        .getStation(stationCode, new Date(cal.getTimeInMillis())) == null) {
                    //If the above returned null when attempting to obtain the
                    //station, it means the is not in the DB and the program
                    //rmut un parser to obtain all station data for day that was
                    //forecasted and put the data in the DB.
                    WebGradingTools.parseWebAnswers(dbms, cal, stationCode);
                }
                
                //Grade attempt.
                gradeAttempt(dbms, lesson, attempt);
            } //End of test for web page.
        } else { //past MAXIMUM_GRADING_DAYS
            //Check if data is already in database.
            Calendar cal = (Calendar) attempt.getForecastedDate().clone();
            if (dbms.getForecasterStationDataManager()
                    .getStation(attempt.getStationCode(), 
                    new Date(cal.getTimeInMillis())) != null) {
                //If so, grade attempt.
                gradeAttempt(dbms, lesson, attempt);
                return;
            }
            
            /**
             * If code gets here, the data was not retrieved, and the "missing
             * data functionality must be used.
             */
            
            //Get data and data structures.
            DBMSMissingDataRecordManager missingDataManager = dbms
                    .getMissingDataRecordManager();
            String stationCode = attempt.getStationCode();
            Date attemptDate = new Date(attempt.getForecastedDate()
                    .getTimeInMillis());
            MissingWebGradingDataRecord attemptRecord = missingDataManager
                    .getRecordByLessonAndDateAndStation(lesson, attemptDate, 
                    stationCode);
            
            if(attemptRecord.getIsInstructorDataSet()) {
                //The attempt can be graded with instructor data.
                gradeAttempt(dbms, lesson, attempt);
            } else if (!attemptRecord.hasDatabaseId()) {
                //The record is not in the database;  we also know the 
                //instructor has not been emailed.
                
                //Try to send email and save result.
                boolean wasEmailSent = emailInstuctor(lesson, attempt);
                attemptRecord.setWasEmailSent(wasEmailSent);
                
                //Save record to databse.
                missingDataManager.insertRecord(attemptRecord);
            } else if (!attemptRecord.getWasEmailSent()) {
                //Email send failed before, so try again.
                boolean wasEmailSent = emailInstuctor(lesson, attempt);
                attemptRecord.setWasEmailSent(wasEmailSent);
                
                //Update record in databse.
                missingDataManager.updateRecord(attemptRecord);
            } //End email not sent.
        } //End after MAXIMUM_GRADING_DAYS 
    }
    
    /**
     * Tries to grade all ungraded lesson attempts by comparing user answers to
     * values stored in DB. If DB data is not present, either via the web or via
     * instructor responses, this method checks if an email should be sent to
     * the course instructor. Note that instructor responses cannot be provided
     * until either an attempt has been graded or the maximum days for checking
     * the web have elapsed. Also no grading will occur until the minimum days
     * for checking the web have elapsed.
     *
     * @param dbms The application's <code>DBMSSystemManager</code>
     */
    public static void tryToGradeAllAttempts(DBMSSystemManager dbms) {
        for (ForecasterLesson lesson : dbms.getForecasterLessonManager()
                .getAllForecasterLessons()) {
            for (Attempt attempt : dbms.getForecasterAttemptManager()
                    .getAttempts(lesson)) {
                if (!attempt.hasBeenGraded()) {
                    tryToGradeAttempt(dbms, lesson, attempt);
//                } else {
//                    Debug.println("Lesson: " + lesson.getLessonName());
//                    Debug.println("Attempt Date: " + CalendarFormatter
//                            .formatDateLong(attempt.getForecastedDate()));
//                    Debug.println("Station: " + attempt.getStationCode());
//                    Debug.println("GRADED PREVIOUSLY");
//                    Debug.println();
                }
            }
        }
    }
    
    /**
     * Helper function to notify the instructor that an attempt could not be 
     * graded with web data.  Notification is done via email.
     * 
     * @param lesson The <code>ForecasterLesson</code> for which the attempt was
     * made
     * @param attempt The <code>Attrempt</code> itself.
     * @return True if email was sent successfully; False otherwise.
     */
    private static boolean emailInstuctor(ForecasterLesson lesson, Attempt attempt) {
        String subject = "BU Weather Viewer Forecaster Lesson Grading Notice";
        String address = lesson.getCourse().getInstructor().getEmailAddress();
        String message = "This is a message to inform you that one or more"
                + " student attempts for the lesson " + lesson.getLessonName()
                + " could not be graded because data needed from the Internet"
                + " is missing.  The attempt(s) are for " + CalendarFormatter
                .formatDateLong(attempt.getForecastedDate()) + " and the"
                + " weather station " + attempt.getStationCode() + ".  You can"
                + " provide your own data to grade the attempt(s) using the"
                + " \"Edit Responses\" feature on the \"Manage Forecasting"
                + " Lessons\" menu.  You could also select \"Edit Lesson\" and"
                + " then provide responses under the \"Grading Status\" tab.";
        try {
            Emailer.email(address, message, subject);
            return true;
        } catch (WeatherException ex) {
            return false;
        }
    }
    
    /**
     * Grades a lesson attemptEntry by comparing user answers to values stored
     * in DB. If answer data for specified day are not in DB they are downloaded
     * from the web.
     *
     * @param dbms The application's <code>DBMSSystemManager</code>
     * @param lesson The <code>ForecastingLesson</code> of the 
     * <code>Attempt</code> to be graded
     * @param attempt The <code>Attempt</code> to be graded
     */
    private static void gradeAttempt(DBMSSystemManager dbms, 
            ForecasterLesson lesson, Attempt attempt) {
        //Get attempt station code for getting correct responses.
        String stationCode = attempt.getStationCode();

        //Get day of attemptEntry submission for getting correct responses.
        Calendar cal = (Calendar) attempt.getForecastedDate().clone();

        //Get lesson and attempt information.
        PointScale ps = lesson.getPointScale();
        ArrayList<Question> questions = lesson.getQuestions();
        ArrayList<Response> responses = attempt.getResponses();

        //Get station data, which may be null if web page is missing.
        Station station = dbms.getForecasterStationDataManager()
                .getStation(stationCode, new Date(cal.getTimeInMillis()));

        //Get hash map containing answer data, indexed by data key coming from 
        //corresponding questions.
        HashMap<String, String> answerMap = station == null ? null : station
                .getData();

        //Perform grading.
        for (int i = 0; i < questions.size(); i++) {
            /*Make correct answer string.*/
            String correctAnswerText;

            //Get string from web if map is not null.
            String webAnswerString = null;
            if (answerMap != null) {
                webAnswerString = answerMap.get(questions.get(i).getDataKey());
            }

            // Get instuctor responses
            ArrayList<InstructorResponse> instructorResponses = dbms
                    .getInstructorResponseManager()
                    .getResponsesByQuestionAndDateAndStation((Question) 
                    questions.get(i), new Date(cal.getTimeInMillis()), 
                    stationCode);

            //Build intermediate string with number in place of phrase answers.
            StringBuilder sb = new StringBuilder();
            if (webAnswerString != null) {
                sb.append(webAnswerString);
            }
            int count = 1;
            for (InstructorResponse ir : instructorResponses) {
                if (count > 1 || webAnswerString != null) {
                    sb.append("|");
                }
                sb.append(ir.getAnswer());
                count++;
            }

            // Store this result as a String
            correctAnswerText = sb.toString();

            /*Make student answer string.*/
            String userResponseText;

            // Get value of userResponseText for this table row
            Response r = (Response) responses.get(i);
            userResponseText = "";
            for (Answer a : r.getAnswers()) {
                userResponseText += a.getAnswerValue() + ",";
            }
            userResponseText = userResponseText.substring(0,
                    userResponseText.length() - 1);

            /*Compute score.*/
            int pointsEarned;

            if (userResponseText.equals(NO_ANSWER_VALUE)) {
                pointsEarned = ps.getUnansweredPoints();
            } else if (isResponseCorrect(userResponseText, correctAnswerText)) {
                pointsEarned = ps.getCorrectPoints();
            } else {
                pointsEarned = ps.getIncorrectPoints();
            }

            // store response in Attempt object
            r.getResponseScore().setPointsEarned(pointsEarned);
            r.getResponseScore().setpointsPossible(ps.getCorrectPoints());
            attempt.getResponses().get(i).setResponseScore(r.getResponseScore());
        }
        
        // must recalculate attempt score
        attempt.calculateAttemptScore();

        // at the end must commit grade update
        dbms.getForecasterAttemptManager().updateAttempt(attempt);
    }
    
    /**
     * Checks student response correctness against provided correct answer.
     * Correct answer may have several options separated by vertical bars and
     * may contain ranges of the form int:int.  This method assumes there is a
     * student answer.  The case where there is not should not involve this 
     * method being called.
     *
     * @param studentAns student response
     * @param correctAns correct response value
     * @return true if student answer matches provided correct answer(s),
     * otherwise false.
     */
    private static Boolean isResponseCorrect(String studentAns,
            String correctAns) {
        StringTokenizer st = new StringTokenizer(correctAns, "|");
        
        while(st.hasMoreTokens()) {
            String thisToken = st.nextToken();
            if (thisToken.contains(":")) {
                int minValue, maxValue;
                int studentAnsValue = Integer.parseInt(studentAns);
                minValue = Integer.parseInt(thisToken.substring(0, thisToken
                        .indexOf(':')));
                maxValue = Integer.parseInt(thisToken.substring(thisToken
                        .indexOf(':') + 1));

                if (studentAnsValue <= maxValue && studentAnsValue >= minValue) {
                    return true; //Return true if in range.
                }
            } else {
                //If not in a range, the answer must match exactly.
                if (thisToken.equals(studentAns)) {
                    return true;
                }
            }
        }

        //If all tests fail or do not apply, return false.
        return false;
    }
}