package weather.common.dbms;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.Question;

/**
 * Manages questions for forecaster lessons in the database.
 * @author jbenscoter
 */
public interface DBMSForecasterQuestionManager {
    /**
     * Returns all of the questions for a specific <code>ForecasterLesson</code>.
     *
     * @param lesson The <code>ForecasterLesson</code> to return the 
     * <code>Questions</code> for.
     * @return An <code>ArrayList</code> of all the <code>Questions</code> for the
     * provided <code>ForecasterLesson</code>
     */
    public ArrayList<Question> getQuestions(ForecasterLesson lesson);
    
    /**
     * Adds a question to the <code>forecaster_questions</code> table.
     *
     * @param lesson The <code>ForecasterLesson</code> to add the question to.
     * @param question The <code>Question</code> to add to the 
     * <code>ForecasterLesson</code>
     * @return True if successful, false otherwise.
     */
    public Question insertQuestion(ForecasterLesson lesson, 
            Question question);
    
    /**
     * Updates the question in the database.
     *
     * @param question The <code>Question</code> to be updated.
     * @return True if successful, false otherwise.
     */
    public Question updateQuestion(Question question);
    
    /**
     * Deletes a <code>Question</code> from the database.
     *
     * @param question The question to be deleted.
     * @return True if successful, false otherwise.
     */
    public boolean deleteQuestion(Question question);
}
