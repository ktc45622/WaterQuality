package weather.common.dbms.mysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.Question;
import weather.common.dbms.DBMSForecasterQuestionTemplateManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages question templates in the database
 * @author jbenscoter
 */
public class MySQLForecasterQuestionTemplateManager implements DBMSForecasterQuestionTemplateManager {
    private MySQLImpl dbms;

    /**
     * constructs a new question template object
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterQuestionTemplateManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    @Override
    public ArrayList<Question> getLessonTemplate() {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Question> questions = new ArrayList<>();
        ArrayList<String> questionIds = new ArrayList<>();
        try {
            String sql = "{call sp_getForecasterTemplate()}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            rs = statement.executeQuery();
            //while (rs.next()) {
                //create forecasterLesson and add it to list
            //    template.add(MySQLHelper.makeQuestionFromResultSet(rs));
            //}
            
            Question q = null;
            ArrayList<Answer> answers = new ArrayList<>();
            while (rs.next()) {
                if(!questionIds.contains(rs.getString("questionId")))
                {
                    if(q != null)
                    {
                        q.setAnswers(answers);
                        answers = new ArrayList<>();
                    }
                    
                    //create forecasterLesson and add it to list
                    q = MySQLHelper.makeQuestionFromResultSet(rs);
                    //ArrayList<Answer> answers = fam.getAnswers(q);
                    //q.setAnswers(answers);
                    if(rs.getString("answerText") != null)
                    {
                        answers.add(MySQLHelper.makeAnswerFromResultSet(rs));
                    }
                    
                    questionIds.add(q.getQuestionID());
                    q.setQuestionID("1A");
                    questions.add(q);
                }
                else if(rs.getString("answerText") != null)
                {
                    answers.add(MySQLHelper.makeAnswerFromResultSet(rs));
                }
            }
            if(q != null)
            {
                q.setAnswers(answers);
            }
            
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return questions;
    }
    
}
