package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.InstructorResponse;
import weather.common.data.forecasterlesson.Question;
import weather.common.dbms.DBMSInstructorResponseManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages the instructor responses in the database
 * @author Brian Bankes
 */
public class MySQLInstructorResponseManager implements DBMSInstructorResponseManager {

    private MySQLImpl dbms;

    /**
     * Constructor.
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLInstructorResponseManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    @Override
    public InstructorResponse insertResponse(InstructorResponse response) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_insertInstructorResponse(?,?,?,?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            statement.setString(1, response.getQuestionID());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(2, dateFormatter.format(response.gatDate()));
            statement.setString(3, response.getAnswer());
            statement.setString(4, response.getStationCode());

            rs = statement.executeQuery();
            InstructorResponse newResponse = null;

            while (rs.next()) {
                newResponse = MySQLHelper.makeInstructorResponseFromResultSet(rs);
            }

            return newResponse;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return null;
    }

    @Override
    public InstructorResponse deleteResponse(InstructorResponse response) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_deleteInstructorResponse(?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, response.getResponseID());
            statement.execute();
            InstructorResponse r = new InstructorResponse(null,
                    response.getQuestionID(), response.getAnswer(),
                    response.gatDate(), response.getStationCode());

            return r;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return response;
    }

    @Override
    public ArrayList<InstructorResponse> getResponsesByQuestionAndDateAndStation(
            Question question, Date date, String stationCode) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<InstructorResponse> responses = new ArrayList<>();

        try {
            String sql = "{call sp_getInstructorResponsesByQuestionAndDateAndStation(?,?,?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, question.getQuestionID());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(2, dateFormatter.format(date));
            statement.setString(3, stationCode);
            
            rs = statement.executeQuery();
            while (rs.next()) {
                responses.add(MySQLHelper.makeInstructorResponseFromResultSet(rs));
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
