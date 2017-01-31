package weather.common.dbms.mysql;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.Response;
import weather.common.dbms.DBMSForecasterResponseManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages responses in the database.
 * @author Jeremy
 */
public class MySQLForecasterResponseManager implements DBMSForecasterResponseManager{

    MySQLImpl dbms;
    
    /**
     * constructs a new response manager
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterResponseManager(MySQLImpl dbms)
    {
        this.dbms = dbms;
    }
    @Override
    public Response insertResponse(Attempt a, Response r) {
        ResultSet rs = null;
        Connection conn = null;

        Response newResponse = null;
        try {
            String sql = "{call sp_insertResponseWithScore(?,?,?,?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, a.getAttemptID());
            statement.setString(2, r.getResponseScore().getScoreID());
            statement.setInt(3, r.getResponseScore().getPointsEarned());
            statement.setInt(4, r.getResponseScore().getPointsPossible());

            statement.execute();
            
            rs = statement.getResultSet();
            
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newResponse = MySQLHelper.makeResponseFromResultSet(rs);
            }
            
            for(int i = 0; i < r.getAnswers().size(); i++)
            {                
                Answer temp = r.getAnswers().get(i);
                
                if(temp.getAnswerID().equals("1A"))
                {
                    temp = dbms.getForecasterAnswerManager().addAnswerToQuestion(temp, temp.getQuestion());
                }
                
                sql = "{call sp_insertResponseAnswer(?,?)}";
                statement = conn.prepareCall(sql);
                statement.setString(1, newResponse.getResponseID());
                statement.setString(2, temp.getAnswerID());

                statement.execute();
            }
            
            sql = "{call sp_getResponseById(?)}";
            statement = conn.prepareCall(sql);
            statement.setString(1, newResponse.getResponseID());
            statement.execute();
            
            rs = statement.getResultSet();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newResponse = MySQLHelper.makeResponseFromResultSet(rs);
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
        return newResponse;
    }

    @Override
    public boolean updateResponse(Response r) {
        ResultSet rs = null;
        Connection conn = null;

        try {
            String sql = "{call sp_clearResponseAnswers(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, r.getResponseID());
            statement.execute();
                    
            sql = "{call sp_updateResponseScore(?,?,?)}";
            statement = conn.prepareCall(sql);
            statement.setString(1, r.getResponseScore().getScoreID());
            statement.setInt(2, r.getResponseScore().getPointsEarned());
            statement.setInt(3, r.getResponseScore().getPointsPossible());
            statement.execute();
            
            for(int i = 0; i < r.getAnswers().size(); i++)
            {
                Answer temp = r.getAnswers().get(i);
                
                if(temp.getAnswerID().equals("1A"))
                {
                    temp = dbms.getForecasterAnswerManager().addAnswerToQuestion(temp, temp.getQuestion());
                }
                
                sql = "{call sp_insertResponseAnswer(?,?)}";
                statement = conn.prepareCall(sql);
                statement.setString(1, r.getResponseID());
                statement.setString(2, temp.getAnswerID());

                statement.execute();
            }
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

    @Override
    public Response deleteResponse(Response r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Response> getResponsesByAttempt(Attempt attempt) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Response> responses = new ArrayList<>();
        ArrayList<String> responseIds = new ArrayList<>();
        ArrayList<Answer> answers = new ArrayList<>();
        ArrayList<String> answerIds = new ArrayList<>();
        Response r = null;
        
        try {
            String sql = "{call sp_getResponsesByAttempt(?)}";
            conn = dbms.getLocalConnection();

            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, attempt.getAttemptID());
            rs = statement.executeQuery();
            
            while (rs.next()) {
                if(!responseIds.contains(rs.getString("responseId")))
                {
                    if(r != null)
                    {
                        r.setAnswers(answers);
                        answers = new ArrayList<>();
                    }
                    //create forecasterLesson and add it to list
                    r = MySQLHelper.makeResponseFromResultSet(rs);
                    Answer a = MySQLHelper.makeAnswerFromResultSet(rs);
                    answers.add(a);

                    responses.add(r);
                    answerIds.add(a.getAnswerID());
                    responseIds.add(r.getResponseID());
                }
                else if(!answerIds.contains(rs.getString("answerId")))
                {
                    Answer a = MySQLHelper.makeAnswerFromResultSet(rs);
                    answers.add(a);
                    answerIds.add(a.getAnswerID());
                }
            }
            if (r != null) {
                r.setAnswers(answers);
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
        return responses;
    }
    
}
