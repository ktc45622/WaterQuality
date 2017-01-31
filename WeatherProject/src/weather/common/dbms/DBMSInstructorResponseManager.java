package weather.common.dbms;
import java.sql.Date;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.InstructorResponse;
import weather.common.data.forecasterlesson.Question;

/**
 * This class manages the <code>InstuctorResponse</code> objects in the 
 * database.
 * @see InstructorResponse
 * @author Brian Bankes
 */
public interface DBMSInstructorResponseManager {

    /**
     * Inserts an <code>InstructorResponse</code> object in the database
     * @param response The <code>InstructorResponse</code> object to insert.
     * @return the <code>InstructorResponse</code> object with the id filled 
     * out or null is the response could not be inserted.
     */
    public InstructorResponse insertResponse(InstructorResponse response);

    /**
     * Deletes an <code>InstructorResponse</code> object in the database.
     * @param response The <code>InstructorResponse</code> object to delete.
     * @return The <code>InstructorResponse</code> object with the id set to 
     * null or the original response if the delete could not happen.
     */
    public InstructorResponse deleteResponse(InstructorResponse response);

    /**
     * Gets an <code>ArrayList</code> of type <code>InstructorResponse</code> 
     * for a given <code>Question</code>, <code>Date</code>, 
     * and weather station. 
     * @param question The given <code>Question</code>.
     * @param date The given <code>Date</code>.
     * @param stationCode The code of the station for which responses are 
     * being sought.
     * @return an <code>ArrayList</code> of <code>InstructorResponse</code>
     * that apply to the given <code>Question</code>, <code>Date</code>,
     * and weather station.
     */
    public ArrayList<InstructorResponse> 
            getResponsesByQuestionAndDateAndStation(Question question, 
            Date date, String stationCode);
}
