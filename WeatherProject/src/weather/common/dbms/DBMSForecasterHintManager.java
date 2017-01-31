package weather.common.dbms;

import java.util.ArrayList;
import weather.common.data.forecasterlesson.Instructions;
import weather.common.data.forecasterlesson.QuestionTemplate;

/**
 * Manages the <code>Hints</code> in the database.
 * @author jbenscoter
 */
public interface DBMSForecasterHintManager {
    /**
     * Inserts a new <code>Instructions</code> into the database.
     * @param instructions The <code>Instructions</code> to be inserted.
     * @return The <code>Instructions</code> object with the id
     * from the database.
     */
    public Instructions insertHint(Instructions instructions);
    
    /**
     * Updates a <code>Instructions</code> in the database.
     * @param instructions The <code>Instructions</code> to be updated in 
     * the database.
     * @return True if successful, false otherwise.
     */
    public boolean updateHint(Instructions instructions);
    
    /**
     * Removes an <code>Instructions</code> from the database.
     * @param instructions The <code>Instructions</code> to be removed from 
     * the database.
     * @return The instructions that were removed from the database
     */
    public Instructions removeHint(Instructions instructions);
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Instructions</code>
     * in the database.
     * @return An <code>ArrayList</code> containing all the <code>Instructions</code>
     * in the database.
     */
    public ArrayList<Instructions> getAllHints();
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Instructions</code>
     * in the database that belong to a specific <code>ForecasterLesson</code>
     * @param qt The <code>QuestionTemplate</code> to lookup the 
     * <code>Attempts</code> for.
     * @return An <code>ArrayList</code> containing all the <code>Instructions</code>
     * in the database that belong to the specified <code>ForecasterLesson</code>
     */
    public ArrayList<Instructions> getAllInstructionsByQuestionTemplate
        (QuestionTemplate qt);
}
