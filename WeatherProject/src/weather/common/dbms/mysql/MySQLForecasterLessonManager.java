package weather.common.dbms.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * manages the forecaster lessons in the database
 * @author jbenscoter
 */
public class MySQLForecasterLessonManager implements DBMSForecasterLessonManager {

    private MySQLImpl dbms;

    /**
     * constucts a new forecaster lesson object
     * @param dbms the <code>MySQLImpl</code> object
     */
    public MySQLForecasterLessonManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    @Override
    public ArrayList<ForecasterLesson> getAllForecasterLessons() {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<ForecasterLesson> lessons = new ArrayList<>();
        CallableStatement statement = null;
        try {
            String sql = "{call sp_getAllForecasterLessons()}";
            conn = dbms.getLocalConnection();
            if(conn == null || conn.isClosed() || !conn.isValid(4)){
                Debug.println("connection in getAllForecasterLessons() is not valid");
                conn = dbms.getLocalConnection();
            }
            else
                Debug.println("connection in getAllForecasterLessons() is valid");
            
            statement = conn.prepareCall(sql);
            
            rs =  statement.executeQuery();
            /*
            rs = conn.prepareCall(sql).executeQuery();
            */
            while (rs.next()) {
                //create forecasterLesson and add it to list
                lessons.add(MySQLHelper.makeForecasterLessonFromResultSet(rs));
            }
            Debug.println("After while loop in getAllForecasterLessons()");
        } catch (SQLException e) {
            Debug.println("Got an exception in getAllForecasterLessons()");
            Debug.println(e.getLocalizedMessage())  ;      

            try{
            if(conn == null || conn.isClosed() || !conn.isValid(4)){
                Debug.println("AFTER SQL EXCEPION the connection in getAllForecasterLessons() is not valid");
                conn = dbms.getLocalConnection();
            }
            else
                Debug.println("AFTER SQL EXCEPION the connection in getAllForecasterLessons() is valid");
            }catch (SQLException e2){
                Debug.println("Got an exception while testing conn after exception in getAllForecasterLessons()");
            }       
            
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
           // new WeatherException(0012, e, "Cannot complete the requested "
           //         + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(statement); 
        }
        return lessons;
    }

    @Override
    public ForecasterLesson getForecasterLesson(String id) {
        ResultSet rs = null;
        Connection conn = null;
        ForecasterLesson lesson = null;

        try {
            String sql = "{call sp_getForecasterLessonById(?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareCall(sql);
            statement.setString(1, id);
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                lesson = MySQLHelper.makeForecasterLessonFromResultSet(rs);
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
        return lesson;
    }

    @Override
    public ForecasterLesson insertForecasterLesson(ForecasterLesson lesson)
        throws WeatherException{
        if (lesson.getLessonID().equals("1A")) {
            ResultSet rs = null;
            Connection conn = null;

            try {
                String sql = "{call sp_insertForecasterLesson(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                conn = dbms.getLocalConnection();
                PreparedStatement statement = conn.prepareCall(sql);
                statement.setString(1, lesson.getLessonID());
                statement.setString(2, lesson.getLessonName());
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                statement.setString(3, dateFormatter.format(lesson.getLessonStartDate()));
                statement.setString(4, dateFormatter.format(lesson.getLessonEndDate()));
                statement.setInt(5, lesson.getMaximumTries());
                statement.setString(6, lesson.getStudentEditType());
                statement.setBoolean(7, lesson.isActive());
                statement.setBoolean(8, lesson.isFromArchivedData());
                
                if (lesson.isFromArchivedData()) {
                    statement.setDate(9, new java.sql.Date(lesson.getDateArchived().getTime()));
                } else {
                    statement.setNull(9, java.sql.Types.DATE);
                }
                if(lesson.getStationCode() != null) {
                    statement.setString(10, lesson.getStationCode());
                } else {
                    statement.setNull(10, java.sql.Types.VARCHAR);
                }
                statement.setInt(11, lesson.getCourse().getCourseNumber());
                statement.setString(12, lesson.getInstructions().getInstructionsID());
                statement.setString(13, lesson.getInstructions().getInstructionsText());
                statement.setString(14, lesson.getPointScale().getPointScaleId());
                statement.setInt(15, lesson.getPointScale().getCorrectPoints());
                statement.setInt(16, lesson.getPointScale().getIncorrectPoints());
                statement.setInt(17, lesson.getPointScale().getUnansweredPoints());
                statement.setInt(18, lesson.getPointScale().getTopScoreCounted());
                statement.setBoolean(19, lesson.getPointScale().getRequireAnswers());
                
                statement.execute();
                statement.getMoreResults();
                rs = statement.getResultSet();
                ForecasterLesson newLesson = null;
                
                while (rs.next()){
                    //create forecasterLesson and add it to list
                    newLesson = MySQLHelper.makeForecasterLessonFromResultSet(rs);
                }
                
                for(int i = 0; i < lesson.getQuestions().size(); i++)
                {
                    newLesson.addQuestion(dbms.getForecasterQuestionManager()
                            .insertQuestion(newLesson, lesson.getQuestions()
                                    .get(i)));
                }
                return newLesson;
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
        } else {
                throw new WeatherException(0012, "ForecasterLesson has an ID other"
                        + " than 1A. A new ForecasterLesson should always have"
                        + " an ID of 1A.");
        }
    }

    @Override
    public ForecasterLesson removeForecasterLesson(ForecasterLesson lesson) {
        ResultSet rs = null;
        Connection conn = null;
        try {
            String sql = "{call sp_deleteForecasterLessonById(?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, lesson.getLessonID());
            statement.execute();
            ForecasterLesson fl = new ForecasterLesson("-1", 
                    lesson.getLessonName(), lesson.getStudentEditType(), 
                    lesson.getStationCode(), lesson.getLessonStartDate(), 
                    lesson.getLessonEndDate(), lesson.getMaximumTries(), 
                    lesson.isFromArchivedData(), lesson.getDateArchived(), 
                    lesson.getInstructions(), lesson.getCourse(), lesson.getQuestions(),
                    lesson.getPointScale());
            
            return fl;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return lesson;
    }

    @Override
    public boolean updateForecasterLesson(ForecasterLesson lesson) {
        ResultSet rs = null;
        Connection conn = null;
        boolean status = false;
        
        try {
            String sql = "{call sp_updateForecasterLessonById(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, lesson.getLessonID());
            statement.setString(2, lesson.getLessonName());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(3, dateFormatter.format(lesson.getLessonStartDate()));
            statement.setString(4, dateFormatter.format(lesson.getLessonEndDate()));
            statement.setInt(5, lesson.getMaximumTries());
            statement.setString(6, lesson.getStudentEditType());
            statement.setBoolean(7, lesson.isActive());
            statement.setBoolean(8, lesson.isFromArchivedData());

            if (lesson.isFromArchivedData()) {
                statement.setDate(9, new java.sql.Date(lesson.getDateArchived().getTime()));
            } else {
                statement.setNull(9, java.sql.Types.DATE);
            }
            if(lesson.getStationCode() != null) {
                statement.setString(10, lesson.getStationCode());
            } else {
                statement.setNull(10, java.sql.Types.VARCHAR);
            }
            statement.setInt(11, lesson.getCourse().getCourseNumber());
            statement.setString(12, lesson.getInstructions().getInstructionsID());
            statement.setString(13, lesson.getInstructions().getInstructionsText());
            statement.setString(14, lesson.getPointScale().getPointScaleId());
            statement.setInt(15, lesson.getPointScale().getCorrectPoints());
            statement.setInt(16, lesson.getPointScale().getIncorrectPoints());
            statement.setInt(17, lesson.getPointScale().getUnansweredPoints());
            statement.setInt(18, lesson.getPointScale().getTopScoreCounted());
            statement.setBoolean(19, lesson.getPointScale().getRequireAnswers());

            statement.execute();
            
            
            status = true;
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return status;
    }

    @Override
    public boolean removeForecasterLessonsBeforeDate(Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TableMetaData getMetaData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<ForecasterLesson> getForecasterLessonsByCourse(int courseNumber) {
        ResultSet rs = null;
        Connection conn = null;
        ArrayList<ForecasterLesson> lessons = new ArrayList<>();

        try {
            String sql = "{call sp_getForecasterLessonsByCourseNumber(?)}";
            conn = dbms.getLocalConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, courseNumber);
            
            
            rs = statement.executeQuery();
            while (rs.next()) {
                //create forecasterLesson and add it to list
                lessons.add(MySQLHelper.makeForecasterLessonFromResultSet(rs));
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
        return lessons;
    }

}
