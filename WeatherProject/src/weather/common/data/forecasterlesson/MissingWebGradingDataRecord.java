package weather.common.data.forecasterlesson;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * A MissingWebGradingDataRecord is a data structure that holds all the
 * information necessary to note the fact that an <code>Attempt</code> exists
 * that cannot be graded with web data because the web page with the data is 
 * missing.  Instances of this class each pertain to a given 
 * <code>ForecasterLesson</code> at a giver <code>Station</code> on a given
 * <code>Date</code>.  If more than one <code>Attempt</code> meets these
 * criteria, only one record is necessary.  Because the <code>Instructor</code>
 * can use the <code>InstructorResponse</code> class to provide the missing
 * data, each record maintains an indication of whether or not its attempts can
 * be graded with the instructor's answers.  The class also stores whether on
 * not an email has been sent to notify the <code>Instructor</code> that missing
 * data is preventing grading.
 * 
 * @author Brian Bankes
 */
public class MissingWebGradingDataRecord {
    
    private final String recordID;
    private final String lessonID;
    private final Date date;
    private final String stationCode;
    private boolean isInstructorDataSet;
    private boolean wasEmailSent;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor.
     * 
     * @param recordID the ID of the record in the database or null if the
     * instance is not saved in the database.
     * @param lessonID the ID of the <code>ForecasterLesson</code> in the 
     * database for which this instance indicates missing data.
     * @param date The <code>Date</code> for which this instance indicates 
     * missing data.
     * @param stationCode The code of the weather station for which this
     * instance indicates missing data.
     * @param isInstructorDataSet Whether or not grading can be done with
     * <code>InstructorResponse</code> data.
     * @param wasEmailSent Whether or not an email was sent to the 
     * <code>Instructor</code>.
     */
    public MissingWebGradingDataRecord(String recordID, String lessonID, 
            Date date, String stationCode, boolean isInstructorDataSet,
            boolean wasEmailSent) {
        this.recordID = recordID;
        this.lessonID = lessonID;
        this.date = date;
        this.stationCode = stationCode;
        this.isInstructorDataSet = isInstructorDataSet;
        this.wasEmailSent = wasEmailSent;
    }
    
    /**
     * Returns the ID of this record.
     * 
     * @return The ID of the record or null if the record is not in the 
     * database.
     */
    public String getRecordID(){
        return recordID;
    }
    
    /**
     * Returns whether or not the record has a database id.
     * 
     * @return True if the record has a database id; False otherwise. 
     */
    public boolean hasDatabaseId() {
        return recordID != null;
    }
    
    /**
     * Returns the ID of the <code>Lesson</code> in the database for which 
     * this instance indicates missing data.
     * 
     * @return The ID of the <code>Lesson</code> in the database for
     * which this instance indicates missing data.
     */
    public String getLessonID(){
        return lessonID;
    }
    
    /**
     * Gets the <code>Date</code> on which this instance indicates missing data.
     * 
     * @return The <code>Date</code> on which this instance indicates missing 
     * data.
     */
    public Date gatDate() {
        return date;
    }
    
    /**
     * Gets the code of the weather station for which this instance indicates
     * missing data.
     *
     * @return The code of the weather station for which this instance indicates
     * missing data.
     */
    public String getStationCode() {
        return stationCode;
    }
    
    /**
     * Sets the flag that indicates whether or not grading can be done with
     * <code>InstructorResponse</code> data.
     * 
     * @param value Whether or not grading can be done with
     * <code>InstructorResponse</code> data.
     */
    public void setIsInstructorDataSet(boolean value) {
        isInstructorDataSet = value;
    }
    
    /**
     * Returns whether or not grading can be done with
     * <code>InstructorResponse</code> data.
     * 
     * @return True if grading can be done with <code>InstructorResponse</code> 
     * data; False otherwise.
     */
    public boolean getIsInstructorDataSet() {
        return isInstructorDataSet;
    }
    
    /**
     * Sets the flag that indicates whether or an email was sent to the 
     * <code>Instructor</code>.
     *
     * @param value Whether or not an email was sent to the 
     * <code>Instructor</code>.
     */
    public void setWasEmailSent(boolean value) {
        wasEmailSent = value;
    }

    /**
     * Returns whether or not an email was sent to the <code>Instructor</code>.
     *
     * @return True if an email was sent to the <code>Instructor</code>; False 
     * otherwise.
     */
    public boolean getWasEmailSent() {
        return wasEmailSent;
    }
    
    /**
     * Public method used for testing only that displays the record values.
     * @return a String representing the
     * <code>MissingWebGradingDataRecord</code>.
     */
    @Override
    public String toString() {
        return "MissingWebGradingDataRecord(recordID=" + (recordID == null 
                ? "null" : recordID) + ", lessonID=" + (lessonID == null 
                ? "null" : lessonID) + ", stationCode=" + (stationCode == null 
                ? "null" : stationCode) + ", date=" + (date == null ? "null" 
                : df.format(date)) + ", isInstructorDataSet="
                + (isInstructorDataSet ? "True" : "False") + ", wasEmailSent="
                + (wasEmailSent ? "True" : "False") + ")";
    }
}
