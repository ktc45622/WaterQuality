package weather.common.dbms.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.User;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.Response;
import weather.common.dbms.DBMSForecasterAttemptManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * managers the attempts in the databse
 * @author Jeremy
 */
public class MySQLForecasterAttemptManager implements DBMSForecasterAttemptManager{

    private MySQLImpl dbms;
    
    /**
     * constructs a new attempt manager
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterAttemptManager(MySQLImpl dbms)
    {
        this.dbms = dbms;
    }
    
    @Override
    public Attempt insertAttempt(Attempt a, ForecasterLesson fl) {
        ResultSet rs = null;
        Connection conn = null;

        Attempt newAttempt = null;
        try {
            String sql = "{call sp_insertAttempt(?,?,?,?)}";
            
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, fl.getLessonID());
            statement.setString(2, a.getStationCode());
            statement.setInt(3, a.getStudent().getUserNumber());
            statement.setDate(4, new java.sql.Date(a.getAttemptDate().getTimeInMillis()));
            
            statement.execute();
            rs = statement.getResultSet();
            
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newAttempt = MySQLHelper.makeAttemptFromResultSet(rs);
            }
            
            for(int i = 0; i < a.getResponses().size(); i++)
            {
                dbms.getForecasterResponseManager().insertResponse(newAttempt, a.getResponses().get(i));
            }
            
            ArrayList<Response> responses = dbms.getForecasterResponseManager()
                        .getResponsesByAttempt(newAttempt);
                
            for(int i = 0; i < responses.size(); i++)
            {
                newAttempt.addResponse(i,responses.get(i));
            }
            newAttempt.calculateAttemptScore();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return newAttempt;
    }

    @Override
    public Attempt updateAttempt(Attempt a) {
        ResultSet rs = null;
        Connection conn = null;
        Attempt newAttempt = null;
        try {
            String sql = "{call sp_updateAttempt(?,?,?,?)}";
            
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, a.getAttemptID());
            statement.setString(2, a.getStationCode());
            statement.setInt(3, a.getStudent().getUserNumber());
            statement.setDate(4, new java.sql.Date(a.getAttemptDate().getTimeInMillis()));
            
            statement.execute();
            rs = statement.getResultSet();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                newAttempt = MySQLHelper.makeAttemptFromResultSet(rs);
            }
            
            for(int i = 0; i < a.getResponses().size(); i++)
            {
                if(a.getResponses().get(i).getResponseID().equals("1A"))
                {
                    dbms.getForecasterResponseManager().insertResponse(newAttempt, a.getResponses().get(i));
                }
                else
                {
                    dbms.getForecasterResponseManager().updateResponse(a.getResponses().get(i));
                }
            }
            
            ArrayList<Response> responses = dbms.getForecasterResponseManager()
                        .getResponsesByAttempt(a);
            newAttempt.getResponses().clear();
            for(int i = 0; i < responses.size(); i++)
            {
                newAttempt.addResponse(i,responses.get(i));
            }
            newAttempt.calculateAttemptScore();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return newAttempt;
    }

    @Override
    public boolean removeAttempt(Attempt a) {
        ResultSet rs = null;
        Connection conn = null;

        try {
            String sql = "{call sp_deleteAttempt(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, a.getAttemptID());
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

    @Override
    public ArrayList<Attempt> getAllAttempts() {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Attempt> attempts = new ArrayList<>();

        try {
            String sql = "{call sp_getAllAttempts()}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                Attempt a = MySQLHelper.makeAttemptFromResultSet(rs);
                
                ArrayList<Response> responses = dbms.getForecasterResponseManager()
                        .getResponsesByAttempt(a);
                
                for(int i = 0; i < responses.size(); i++)
                {
                    a.addResponse(i,responses.get(i));
                }
                a.calculateAttemptScore();
                attempts.add(a);
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
        return attempts;
    }

    @Override
    public ArrayList<Attempt> getAttempts(User u) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Attempt> attempts = new ArrayList<>();

        try {
            String sql = "{call sp_getAttemptsByUser(?)}";
            conn = dbms.getLocalConnection();
            CallableStatement statement = conn.prepareCall(sql);
            statement.setInt(1, u.getUserNumber());

            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                Attempt a = MySQLHelper.makeAttemptFromResultSet(rs);
                
                ArrayList<Response> responses = dbms.getForecasterResponseManager()
                        .getResponsesByAttempt(a);
                
                for(int i = 0; i < responses.size(); i++)
                {
                    a.addResponse(i,responses.get(i));
                }
                a.calculateAttemptScore();
                attempts.add(a);
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
        return attempts;
    }

    @Override
    public ArrayList<Attempt> getAttempts(ForecasterLesson fl) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Attempt> attempts = new ArrayList<>();

        try {
            String sql = "{call sp_getAttemptsByForecasterLesson(?)}";
            conn = dbms.getLocalConnection();
            if(conn == null || conn.isClosed() || !conn.isValid(4)){
                Debug.println("connection in getAttempts(ForecasterLesson fl) is not valid");
                conn = dbms.getLocalConnection();
            }
            else
                Debug.println("connection in getAttempts(ForecasterLesson fl) is valid");
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, fl.getLessonID());
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                Attempt a = MySQLHelper.makeAttemptFromResultSet(rs);
                
                ArrayList<Response> responses = dbms.getForecasterResponseManager()
                        .getResponsesByAttempt(a);
                
                for(int i = 0; i < responses.size(); i++)
                {
                    a.addResponse(i,responses.get(i));
                }
                a.calculateAttemptScore(); 
                attempts.add(a);
            }
            
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            /*
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
            */
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return attempts;
    }

    @Override
    public ArrayList<Attempt> getAttempts(ForecasterLesson fl, User u) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<Attempt> attempts = new ArrayList<>();

        try {
            String sql = "{call sp_getAttemptsByLessonAndUser(?,?)}";
            conn = dbms.getLocalConnection();
            
             if(conn == null || conn.isClosed() || !conn.isValid(4)){
                Debug.println("connection in getAttempts is not valid");
                conn = dbms.getLocalConnection();
            }
            else
                Debug.println("connection in getAttempts is valid");
            
            CallableStatement statement = conn.prepareCall(sql);
            statement.setString(1, Integer.toString(u.getUserNumber()));
            statement.setString(2, fl.getLessonID());
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                Attempt a = MySQLHelper.makeAttemptFromResultSet(rs);
                
                ArrayList<Response> responses = dbms.getForecasterResponseManager()
                        .getResponsesByAttempt(a);
                
                for(int i = 0; i < responses.size(); i++)
                {
                    a.addResponse(i,responses.get(i));
                }
                a.calculateAttemptScore();
                attempts.add(a);
            }
            
        } catch (SQLException e) {
            Debug.println("sql error in getAttempts");
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            /*
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
           */
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return attempts;
    }
    
}
