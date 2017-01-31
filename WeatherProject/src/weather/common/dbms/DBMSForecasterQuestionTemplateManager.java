package weather.common.dbms;

import java.util.ArrayList;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.QuestionTemplate;

/**
 * Manages the <code>QuestionTemplates</code> in the database.
 * @author jbenscoter
 */
public interface DBMSForecasterQuestionTemplateManager {
    /**
     * Return an <code>ArrayList</code> of <code>QuestionTemplate</code>
     * objects that represent all of the <code>QuestionTemplates</code> in 
     * the database for creating a new <code>ForecasterLesson</code>
     * @return An <code>ArrayList</code> representing all of the
     * <code>QuestionTemplates</code> for a new <code>ForecasterLesson</code>
     */
    public ArrayList<Question> getLessonTemplate();
}
