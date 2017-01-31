package weather.common.dbms.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.ForecasterLesson;
import static weather.common.data.forecasterlesson.ForecasterLessonGrader.NO_ANSWER_VALUE;
import weather.common.data.forecasterlesson.Question;
import weather.common.dbms.DBMSForecasterQuestionManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages questions in the database
 * @author jbenscoter
 */
public class MySQLForecasterQuestionManager implements DBMSForecasterQuestionManager {

    private MySQLImpl dbms;

    /**
     * constructs a new question manager.
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterQuestionManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    @Override
    public ArrayList<Question> getQuestions(ForecasterLesson lesson) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Question> questions = new ArrayList<>();
        ArrayList<String> questionIds = new ArrayList<>();
        
        try {
            String sql = "{call sp_getQuestionsByForecasterLesson(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, lesson.getLessonID());
            rs = statement.executeQuery();

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
                    
                    //create Question and add it to list
                    q = MySQLHelper.makeQuestionFromResultSet(rs);
                    //remove instances of "No Answer Given."
                    Answer thisAnswer = MySQLHelper.makeAnswerFromResultSet(rs);
                    if (!thisAnswer.getAnswerValue().equals(NO_ANSWER_VALUE)) {
                        answers.add(thisAnswer);
                    }
                    questionIds.add(q.getQuestionID());
                    questions.add(q);
                }
                else
                {
                    //remove instances of "No Answer Given."
                    Answer thisAnswer = MySQLHelper.makeAnswerFromResultSet(rs);
                    if (!thisAnswer.getAnswerValue().equals(NO_ANSWER_VALUE)) {
                        answers.add(thisAnswer);
                    }
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

    @Override
    public Question insertQuestion(ForecasterLesson lesson, Question question) {
        ResultSet rs = null;
        Connection conn = null;

        Question newQuestion = null;
        try {
            String sql = "{call sp_insertQuestion(?,?,?,?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, lesson.getLessonID());
            statement.setString(2, question.getQuestionTemplate().getID());
            statement.setInt(3, question.getQuestionNumber());
            statement.setString(4, question.getQuestionZulu());
            
            statement.execute();
            rs = statement.getResultSet();
            
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newQuestion = MySQLHelper.makeQuestionFromResultSet(rs);
            }
            
            ArrayList<Answer> newAnswers = new ArrayList<>();
            for(int i = 0; i < question.getAnswers().size(); i++)
            {
                newAnswers.add(dbms.getForecasterAnswerManager().addAnswerToQuestion(question.getAnswers().get(i), newQuestion));
            }
            newQuestion.setAnswers(newAnswers);
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return newQuestion;
    }

    @Override
    public Question updateQuestion(Question question) {
         ResultSet rs = null;
        Connection conn = null;

        Question newQuestion = null;
        try {
            String sql = "{call sp_insertQuestion(?,?,?,?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, question.getQuestionID());
            statement.setString(2, question.getQuestionTemplate().getID());
            statement.setInt(3, question.getQuestionNumber());
            statement.setString(4, question.getQuestionZulu());
            
            statement.execute();
            rs = statement.getResultSet();
            
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newQuestion = MySQLHelper.makeQuestionFromResultSet(rs);
            }
            
            for(int i = 0; i < question.getAnswers().size(); i++)
            {
                if(!question.getAnswers().get(i).getAnswerID().equals("1A"))
                {
                    dbms.getForecasterAnswerManager().updateAnswer(question.getAnswers().get(i));
                }
                else
                {
                    newQuestion.getAnswers().add(dbms.getForecasterAnswerManager().addAnswerToQuestion(question.getAnswers().get(i), question));
                }
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
        return newQuestion;
    }

    @Override
    public boolean deleteQuestion(Question question) {
        ResultSet rs = null;
        Connection conn = null;

        try {
            String sql = "{call sp_deleteQuestion(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, question.getQuestionID());
            
            statement.execute();
            return true;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return false;
    }

}
