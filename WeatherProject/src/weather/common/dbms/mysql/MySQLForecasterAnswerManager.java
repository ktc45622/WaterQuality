package weather.common.dbms.mysql;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.Response;
import weather.common.dbms.DBMSForecasterAnswerManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages the answers in the database
 * @author Jeremy
 */
public class MySQLForecasterAnswerManager implements DBMSForecasterAnswerManager{
    
    private MySQLImpl dbms;
    
    /**
     * constructs a new forecaster answer manager
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterAnswerManager(MySQLImpl dbms)
    {
        this.dbms = dbms;
    }
    
    @Override
    public Answer addAnswerToQuestion(Answer a, Question q) {
        ResultSet rs = null;
        Connection conn = null;

        Answer newAnswer = null;
        try {
            String sql = "{call sp_insertAnswer(?,?,?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, q.getQuestionID());
            statement.setString(2, a.getAnswerText());
            statement.setString(3, a.getAnswerValue());
            rs = statement.executeQuery();
            
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newAnswer = MySQLHelper.makeAnswerFromResultSet(rs);
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
        return newAnswer;
    }

    @Override
    public boolean updateAnswer(Answer a) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_insertAnswer(?,?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, a.getAnswerID());
            statement.setString(2, a.getAnswerText());
            statement.setString(3, a.getAnswerValue());
            statement.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            return false;
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return true;
    }

    @Override
    public boolean deleteAnswer(Answer a) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_deleteAnswer(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, a.getAnswerID());
            statement.execute();
            
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            return false;
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return true;
    }

    @Override
    public ArrayList<Answer> getAnswers(Question q) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Answer> answers = new ArrayList<>();

        try {
            String sql = "{call sp_getAnswersByQuestion(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, q.getQuestionID());
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                answers.add(MySQLHelper.makeAnswerFromResultSet(rs));
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
        return answers;
    }
    
    @Override
    public ArrayList<Answer> getAnswers(Response q) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Answer> answers = new ArrayList<>();

        try {
            String sql = "{call sp_getAnswersByResponse(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, q.getResponseID());
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                answers.add(MySQLHelper.makeAnswerFromResultSet(rs));
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
        return answers;
    }
}
