package weather.common.dbms;
import java.sql.Date;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.MissingWebGradingDataRecord;

/**
 * This interface manages the <code>MissingWebGradingDataRecord</code> objects 
 * in the database.
 * @see MissingWebGradingDataRecord
 * @author Brian Bankes
 */
public interface DBMSMissingDataRecordManager {

    /**
     * Inserts an <code>MissingWebGradingDataRecord</code> object in the 
     * database.
     * @param record The <code>MissingWebGradingDataRecord</code> object to 
     * insert.
     * @return The <code>MissingWebGradingDataRecord</code> object with the id 
     * filled out or null is the response could not be inserted.
     */
    public MissingWebGradingDataRecord 
            insertRecord(MissingWebGradingDataRecord record);

    /**
     * Updates a <code>MissingWebGradingDataRecord</code> object in the
     * database.
     * @param record The <code>MissingWebGradingDataRecor</code> object to
     * update.
     * @return True if the update was successful; False otherwise.
     */
    public boolean updateRecord(MissingWebGradingDataRecord record);

    /**
     * Gets the <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code>, <code>Date</code>, and weather station
     * code.  An object will always be returned.  If this combination is in the
     * database, it will be the database record.  If not, an object holding an
     * unsaved record will be returned.
     * @param lesson The given <code>ForecasterLesson</code>.
     * @param date The given <code>Date</code>.
     * @param stationCode The code of the station for which responses are 
     * being sought.
     * @return The <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code>, <code>Date</code>, and weather station
     * code.  An object will always be returned.  If this combination is in the
     * database, it will be the database record.  If not, an object holding an
     * unsaved record will be returned.
     */
    public MissingWebGradingDataRecord 
            getRecordByLessonAndDateAndStation(ForecasterLesson lesson, 
            Date date, String stationCode);
    
    /**
     * Gets all of the saved instances of
     * <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code> in a <code>ArrayList</code>.
     * @param lesson The given <code>ForecasterLesson</code>.
     * @return All of the saved instances of
     * <code>MissingWebGradingDataRecord</code> for a given 
     * <code>ForecasterLesson</code> in a <code>ArrayList</code>.
     */
    public ArrayList<MissingWebGradingDataRecord> 
            getAllRecordsForLesson(ForecasterLesson lesson);
}
