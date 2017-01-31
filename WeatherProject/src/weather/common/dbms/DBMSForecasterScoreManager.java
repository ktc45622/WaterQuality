package weather.common.dbms;
import weather.common.data.forecasterlesson.Score;
import weather.common.data.forecasterlesson.ForecasterLesson;

/**
 *
 * @author jbenscoter
 */
public interface DBMSForecasterScoreManager {
    /**
     * Inserts a new score in the database
     * @param s The score to be inserted into the database.
     * @return The score object with the id from the database.
     */
    public Score insertScore(Score s);
    
    /**
     * Updates an existing Score in the database
     * @param s The <code>Score</code> object with the changes.
     * @return True if successful, false otherwise
     */
    public boolean updateScore(Score s);
    
    /**
     * Removes a <code>Score</code> from the database.
     * @param s The <code>Score</code> object to remove from the database.
     * @return The <code>Score</code> object with the id set to -1
     */
    public Score deleteScore(Score s);
    
    /**
     * Gets a <code>Score</code> for the <code>ForecasterLesson</code> in
     * the database.
     * @param fl The <code>ForecasterLesson</code> object to get the 
     * <code>Score</code> object for.
     * @return The <code>Score</code> object related to the specified 
     * <code>ForecasterLesson</code>
     */
    public Score getScoreByForecasterLesson(ForecasterLesson fl);
}
