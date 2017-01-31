package weather.common.dbms;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Response;

/**
 * interface for managing <code>Answers</code> for <code>ForecasterLesson</code>
 * objects in the database
 * @author jbenscoter
 */
public interface DBMSForecasterAnswerManager {

    /**
     * adds an <code>Answer</code> to a <code>Question</code>
     * @param a the <code>Answer</code> object to be added to the <code>Questions</code>
     * @param q the <code>Question</code> object add the <code>Answer</code> to
     * @return the <code>Answer</code> object with the ID field set.
     */
    public Answer addAnswerToQuestion(Answer a, Question q);

    /**
     * updates an <code>Answer</code> in the database.
     * @param a the <code>Answer</code> to be added to the database
     * @return true if it is successful, false otherwise.
     */
    public boolean updateAnswer(Answer a);

    /**
     * deletes an <code>Answer</code> in the database.
     * @param a the <code>Answer</code> to be deleted from the database
     * @return true if it is successful, false otherwise.
     */
    public boolean deleteAnswer(Answer a);

    /**
     * returns an <code>ArrayList</code> of <code>Answer</code> objects for the
     * specified <code>Question</code>
     * @param q the <code>Question</code> to get the <code>Answer</code> objects
     * for
     * @return an <code>ArrayList</code> of <code>Answer</code> objects for the
     * specified <code>Question</code>
     */
    public ArrayList<Answer> getAnswers(Question q);

    /**
     * returns an <code>ArrayList</code> of <code>Answer</code> objects for the
     * specified <code>Response</code>
     * @param r the <code>Response</code> to get the <code>Answer</code> objects
     * for
     * @return an <code>ArrayList</code> of <code>Answer</code> objects for the
     * specified <code>Response</code>
     */
    public ArrayList<Answer> getAnswers(Response r);
}
