package weather.common.data.forecasterlesson;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * An InstructorResponse is a representation of an answer the an instructor can
 * provide to an instance of <code>Question</code>. Note that, while an 
 * <code>Answer</code> has only one selected item, a <code>Response</code> may 
 * have more than one. Also, instances of <code>Response</code> can be graded.
 * Hence. A need arrises for this class, as grading this type of "response" 
 * makes no sense.  Moreover, unlike instances of <code>Response</code>, 
 * instances of this class are connected to specific instances of 
 * <code>Question</code> on a specific <code>Date</code>.
 * 
 * @author Brian Bankes
 */
public class InstructorResponse {
    
    private final String responseID;
    private final String questionID;
    private final String answerString;
    private final Date date;
    private final String stationCode;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor.
     * 
     * @param responseID the ID of the response in the database or null if the
     * instance is not saved in the database.
     * @param questionID the ID of the <code>Question</code> in the database for
     * which this instance provides an "answer."
     * @param answerString A <code>String</code> representation of the choice
     * provided by the instructor.  It is a single value (e. g. "N"), a range
     * (e. g. "50:60"), or a list of <code>Answer</code> values that could make
     * a single <code>Response</code> (e. g. "1,3").
     * @param date The <code>Date</code> on which this instance provides a valid
     * "answer."
     * @param stationCode The code of the weather station for which this
     * instance provides a valid "answer."
     */
    public InstructorResponse(String responseID, String questionID, 
            String answerString, Date date, String stationCode) {
        this.responseID = responseID;
        this.questionID = questionID;
        this.answerString = answerString;
        this.date = date;
        this.stationCode = stationCode;
    }
    
    /**
     * Returns the ID of this response.
     * 
     * @return The ID of the response.
     */
    public String getResponseID(){
        return responseID;
    }
    
    /**
     * Returns the ID of the <code>Question</code> in the database for which 
     * this instance provides an "answer."
     * 
     * @return The ID of the <code>Question</code> in the database for
     * which this instance provides an "answer."
     */
    public String getQuestionID(){
        return questionID;
    }
    
    /**
     * Returns the <code>String</code> representation of the choice given by the
     * instructor. It is a single value (e. g. "N"), a range (e. g. "50:60"), or
     * a list of <code>Answer</code> values that could make a single
     * <code>Response</code> (e. g. "1,3").
     * 
     * @return The <code>String</code> representation of the choice given by the
     * instructor. It is a single value (e. g. "N"), a range (e. g. "50:60"), or
     * a list of <code>Answer</code> values that could make a single
     * <code>Response</code> (e. g. "1,3").
     */
    public String getAnswer() {
        return answerString;
    }
    
    /**
     * Gets the <code>Date</code> on which this instance provides a valid 
     * "answer."
     * 
     * @return The <code>Date</code> on which this instance provides a valid
     * "answer."
     */
    public Date gatDate() {
        return date;
    }
    
    /**
     * Gets the code of the weather station for which this
     * instance provides a valid "answer."
     * 
     * @return The code of the weather station for which this
     * instance provides a valid "answer."
     */
    public String getStationCode() {
        return stationCode;
    }
    
    /**
     * Public method used for testing only that displays the response values.
     * @return a String representing the <code>InstructorResponse</code>.
     */
    @Override
    public String toString() {
        return "InstructorResponse(responseID=" + (responseID == null ? "null"
                : responseID) + ", questionID=" + (questionID == null ? "null"
                : questionID) + ", answer=" + (answerString == null ? "null"
                : answerString) + ", stationCode=" + (stationCode == null 
                ? "null" : stationCode) + ", date=" + (date == null ? "null" 
                : df.format(date)) + ")";
    }
}
