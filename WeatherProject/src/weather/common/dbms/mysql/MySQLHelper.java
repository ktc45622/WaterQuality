package weather.common.dbms.mysql;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.*;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.BookmarkFileInstance;
import weather.common.data.bookmark.BookmarkRank;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.data.diary.BarometricPressureTrendType;
import weather.common.data.diary.CloudType;
import weather.common.data.diary.DailyDiaryWebLinks;
import weather.common.data.diary.DailyEntry;
import weather.common.data.diary.DewPointTrendType;
import weather.common.data.diary.RelativeHumidityTrendType;
import weather.common.data.diary.TemperatureTrendType;
import weather.common.data.diary.WindDirectionSummaryType;
import weather.common.data.diary.WindDirectionType;
import weather.common.data.diary.WindSpeedType;
import weather.common.data.forecasterlesson.Answer;
import weather.common.data.forecasterlesson.AnswerType;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.Instructions;
import weather.common.data.forecasterlesson.InstructorResponse;
import weather.common.data.forecasterlesson.MissingWebGradingDataRecord;
import weather.common.data.forecasterlesson.PointScale;
import weather.common.data.forecasterlesson.Question;
import weather.common.data.forecasterlesson.QuestionTemplate;
import weather.common.data.forecasterlesson.Response;
import weather.common.data.forecasterlesson.Score;
import weather.common.data.forecasterlesson.Station;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonCategory;
import weather.common.data.lesson.LessonEntry;
import weather.common.data.lesson.LessonFileInstance;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.Note;
import weather.common.data.note.NoteFileInstance;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceCollectionSpan;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceTimeZone;
import weather.common.data.resource.WeatherResourceType;
import weather.common.data.version.Version;
import weather.common.utilities.Debug;
import weather.common.utilities.FormatManager;
import weather.common.utilities.WeatherLogger;

/**
 * The MySQLHelper class resolves the problem of code duplication. It contains
 * methods repeatedly used by the methods of the classes in the
 * weather.common.dbms.mysql package. The following operations are implemented
 * in the methods of this class: execution and closing a
 * <code>PreparedStatement</code>, retrieval of values from a
 * <code>ResultSet</code>, closing <code>ResultSet</code>,
 * <code>Statement</code>, <code>PreparedStatement</code> and
 * <code>Connection</code> objects if these objects are not null.
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Mike Graboske (2008)
 * @author Ioulia Lee (2010)
 * @author Joseph Horro (2011)
 * @author Jeremy Benscoter (2014)
 * @version Spring 2010
 *
 */
public class MySQLHelper {

    /**
     * Takes a <code>ResultSet</code> (already at the desired row) and makes an
     * <code>User</code> object out of the data at that row.
     *
     * @param rs The ResultSet (at the desired row!) of the user you want to
     * create.
     * @return A <code>User</code> object representing that ResultSet row.
     * @throws java.sql.SQLException - If the columnLabel is not valid or the
     * given ResultSet is closed.
     */
    public static User makeUserFromResultSet(ResultSet rs) throws SQLException {
        int number = rs.getInt("userNumber");
        String userLoginID = rs.getString("loginID");
        String password = rs.getString("loginPassword");
        String email = rs.getString("email");
        String first = rs.getString("firstName");
        String last = rs.getString("lastName");
        String type = rs.getString("userType");
        UserType typeFromString = UserType.valueOf(type.toLowerCase());
        String notes = rs.getString("notes");
        User u = new User(number, userLoginID, password, email, first, last, typeFromString, notes);
        Timestamp test = rs.getTimestamp("lastLoginTime");
        u.setLastLogInDate(test);
        u.setNumberOfLogins(rs.getInt("loginCount"));

        return u;
    }
    
    /**
     * Takes a
     * <code>ResultSet</code> (already at the desired row) and makes an
     * <code>InstructorResponse</code> object out of the data at that row.
     *
     * @param rs The ResultSet (at the desired row!) of the user you want to
     * create.
     * @return A <code>InstructorResponse</code> object representing that 
     * ResultSet row.
     * @throws java.sql.SQLException - If the columnLabel is not valid or the
     * given ResultSet is closed.
     */
    public static InstructorResponse makeInstructorResponseFromResultSet(ResultSet rs) throws SQLException {
        String forecasterInstructorResponseID = rs
                .getString("forecasterInstructorResponseId");
        String questionID = rs.getString("questionId");
        Date responseDate = rs.getDate("responseDate");
        String responseValue = rs.getString("responseValue");
        String stationCode = rs.getString("stationCode");
        InstructorResponse ir =
                new InstructorResponse(forecasterInstructorResponseID,
                questionID, responseValue, responseDate, stationCode);

        return ir;
    }

    /**
     * Takes a <code>ResultSet</code> (already at the desired row) and makes an
     * <code>Student</code> object out of the data at that row.
     *
     * @param rs - The ResultSet (at the desired row!) of the user you want to
     * create.
     * @return A <code>Student</code> object representing that ResultSet row, or
     * null if none found.
     * @throws java.sql.SQLException - If the columnLabel is not valid or the
     * given ResultSet is closed.
     */
    public static EnrolledStudent makeEnrolledStudentSet(ResultSet rs) throws SQLException {
        return new EnrolledStudent(
                rs.getString("studentID"),
                rs.getInt("courseNumber"),
                rs.getInt("userNumber"));
    }

    public static Property makePropertyFromResultSet(ResultSet rs) throws SQLException {
        Property property = null;
        int propertyID = rs.getInt("propId");
        String propertyType = rs.getString("propType");
        String propertyTypeDisplayName = rs.getString("propTypeDisplayName");
        String propertyName = rs.getString("propName");
        String propertyDisplayName = rs.getString("propDisplayName");
        String propertyValue = rs.getString("propValue");
        byte isEditable = rs.getByte("isEditable");
        String notes = rs.getString("notes");
        String defaultValue = rs.getString("defaultValue");
        String previousValue = rs.getString("previousValue");
        property = new Property(propertyID, propertyType, propertyTypeDisplayName, 
                                propertyName, propertyDisplayName, propertyValue,
                                isEditable, notes, defaultValue, previousValue);
        
        
        return property;
    }
    
    public static Station makeStationFromResultSet(ResultSet rs) throws SQLException {
        Station station = null;
        String stationCode = rs.getString("stationCode");
        String stationName = rs.getString("stationName");
        String state = rs.getString("state");
              
        station = new Station(stationCode, stationName, state);
        
        return station;
    }
    
    /**
     * Takes a <code>ResultSet</code> (already at the desired row) and makes a
     * <code>Resource</code> object out of the data at that row.
     *
     * @param rs The <code>ResultSet</code> (at the desired row!) of the
     * resource you want to create.
     * @return A <code>Resource</code> object representing that
     * <code>ResultSet</code> row, or null if none found.
     * @throws java.sql.SQLException If the columnLabel is not valid; if a
     * database access error occurs or this method is called on a closed result
     * set.
     * @throws MalformedURLException If a URL in our database is incorrectly
     * specified.
     */
    public static Resource makeResourceFromResultSet(ResultSet rs) throws SQLException, MalformedURLException {
        Resource r = null;
        int resourceId = rs.getInt("resourceNumber");
        String name = rs.getString("name");
        String typeTemp = rs.getString("type");
        WeatherResourceType typeTempFromString = WeatherResourceType.valueOf(typeTemp);
        String retrievalMethod = rs.getString("retrievalMethod");
        RetrievalMethod retrievalMethodFromString = RetrievalMethod.valueOf(retrievalMethod.toLowerCase());
        String folderTemp = rs.getString("storageFolderName");
        String formatStringTemp = rs.getString("format");
        ResourceFileFormatType formatTemp = ResourceFileFormatType.enumFromString(formatStringTemp);
        URL urlTemp = new URL(rs.getString("urlString"));
        int frequencyTemp = rs.getInt("timeInterval");
        boolean activeTemp = rs.getBoolean("active");
        boolean visibleTemp = rs.getBoolean("visible");
        Date dateTemp = rs.getDate("dateInitiated");
        int startTime = rs.getInt("startTime");
        int endTime = rs.getInt("endTime");
        ResourceCollectionSpan collectionSpan = ResourceCollectionSpan.valueOf(rs.getString("collectionSpan"));
        float longitude = rs.getFloat("longitude");
        float latitude = rs.getFloat("latitude");
        int orderRank = rs.getInt("orderRank");
        int width = rs.getInt("width");
        int height = rs.getInt("height");
        int updateHour = rs.getInt("updateHour");
        String timeZoneString = rs.getString("timeZone");
        ResourceTimeZone timeZone = ResourceTimeZone.valueOf(timeZoneString);

        r = new Resource(resourceId,
                typeTempFromString,
                name,
                retrievalMethodFromString,
                folderTemp,
                formatTemp,
                urlTemp,
                frequencyTemp,
                activeTemp,
                visibleTemp,
                dateTemp,
                collectionSpan,
                startTime,
                endTime,
                longitude,
                latitude,
                timeZone,
                width,
                height,
                orderRank,
                updateHour);
        return r;
    }

    public static Bookmark makeBookmarkFromResultSet(ResultSet rs) throws SQLException {
        //Retrieve data in need of processing.
        Date startTime = new Date(rs.getTimestamp("startTime").getTime());
        Date endTime = new Date(rs.getTimestamp("endTime").getTime());
        Date plotStartTime = new Date(rs.getTimestamp("dpStartTime").getTime());
        Date plotEndTime = new Date(rs.getTimestamp("dpEndTime").getTime());

        // create ImageInstances
        ImageInstance weatherCameraPicture, weatherMapPicture, weatherStationPicture;
        if (rs.getBytes("weatherCameraPicture").length == 0) {
            weatherCameraPicture = null;
        } else {
            weatherCameraPicture = new ImageInstance(rs.getBytes("weatherCameraPicture"));
        }

        if (rs.getBytes("weatherMapPicture").length == 0) {
            weatherMapPicture = null;
        } else {
            weatherMapPicture = new ImageInstance(rs.getBytes("weatherMapPicture"));
        }

        if (rs.getBytes("weatherStationPicture").length == 0) {
            weatherStationPicture = null;
        } else {
            weatherStationPicture = new ImageInstance(rs.getBytes("weatherStationPicture"));
        }

        //Return result, which is a bookmark if startTime eguala endTime and an 
        //event otherwise.
        if (startTime.equals(endTime)) {
            //bookmak constructor
            return new Bookmark(
                    rs.getInt("bookmarkNumber"),
                    rs.getInt("bookmarkCategoryNumber"),
                    rs.getInt("bookmarkTypeNumber"),
                    rs.getString("name"),
                    rs.getInt("createdBy"),
                    AccessRights.valueOf(rs.getString("accessRights")),
                    startTime,
                    BookmarkRank.getEnum(rs.getString("ranking")),
                    rs.getInt("weatherCameraResourceNumber"),
                    rs.getInt("weatherMapLoopResourceNumber"),
                    rs.getInt("weatherStationResourceNumber"),
                    weatherCameraPicture,
                    weatherMapPicture,
                    weatherStationPicture,
                    rs.getString("notes"),
                    plotStartTime,
                    plotEndTime,
                    rs.getBoolean("dpFitted"),
                    rs.getString("dpGraphSelection"),
                    rs.getInt("dpDaySpanSelection"));
        } else {
            //event constructor
            return new Bookmark(
                    rs.getInt("bookmarkNumber"),
                    rs.getInt("bookmarkCategoryNumber"),
                    rs.getInt("bookmarkTypeNumber"),
                    rs.getString("name"),
                    rs.getInt("createdBy"),
                    AccessRights.valueOf(rs.getString("accessRights")),
                    startTime,
                    endTime,
                    BookmarkRank.getEnum(rs.getString("ranking")),
                    rs.getInt("weatherCameraResourceNumber"),
                    rs.getInt("weatherMapLoopResourceNumber"),
                    rs.getInt("weatherStationResourceNumber"),
                    weatherCameraPicture,
                    weatherMapPicture,
                    weatherStationPicture,
                    rs.getString("notes"),
                    plotStartTime,
                    plotEndTime,
                    rs.getBoolean("dpFitted"),
                    rs.getString("dpGraphSelection"),
                    rs.getInt("dpDaySpanSelection"));
        }
    }

    /**
     * Makes a BookmarkType from a result set.
     *
     * @param rs The result to parse over.
     * @return A new BookmarkType object.
     * @throws SQLException On SQL errors.
     */
    public static BookmarkType makeBookmarkTypeFromResultSet(ResultSet rs) throws SQLException {
//        BookmarkType(int instanceTypeNumber, int categoryNumber,
//            String name, int createdBy, CategoryViewRights viewRights,
//            String notes)
        return new BookmarkType(
                rs.getInt("bookmarkInstanceTypeNumber"),
                rs.getInt("bookmarkCategoryNumber"),
                rs.getString("name"),
                rs.getInt("createdBy"),
                CategoryViewRights.getEnum(rs.getString("viewRights")),
                rs.getString("notes"),
                rs.getInt("orderRank"));
    }

    /**
     * Make a BookmarkCategory from a ResultSet.
     *
     * @param rs The <code>ResultSet</code>.
     * @return A <code>BookmarkCategory</code>.
     * @throws java.sql.SQLException
     */
    public static BookmarkCategory makeBookmarkCategoryFromResultSet(ResultSet rs) throws SQLException {
        // Instantiate bookmark.
        return new BookmarkCategory(
                rs.getInt("bookmarkCategoryNumber"),
                rs.getString("name"),
                rs.getInt("createdBy"),
                CategoryViewRights.valueOf(rs.getString("viewRights")),
                BookmarkDuration.getEnum(rs.getString("bookmarkAlternative")),
                rs.getString("notes"),
                rs.getInt("orderRank"));
    }

    /**
     * Takes a <code>ResultSet</code> and converts it into a <code>Course</code>
     * object.
     *
     * @param rs The <code>ResultSet</code> to make a <code>Course</code> from.
     * @return A <code>Course</code> object extracted from the given
     * <code>ResultSet</code>, or null if none found.
     * @throws java.sql.SQLException - If the columnLabel is not valid; if a
     * database access error occurs or this method is called on a closed result
     * set.
     */
    public static Course makeCourseFromResultSet(ResultSet rs) throws SQLException {
        Course c = null;
        int courseNumber = rs.getInt("courseNumber");
        String departmentName = rs.getString("departmentName");
        String classIdentifier = rs.getString("classIdentifier");
        int section = rs.getInt("section");
        String className = rs.getString("className");
        String typeTemp = rs.getString("semesterType");
        SemesterType semester = SemesterType.valueOf(typeTemp);
        int year = rs.getInt("year");

        User instructor = makeUserFromResultSet(rs);
        //makeUserFromResultSet will set the id of the user to that of the Course
        instructor.setUserNumber(rs.getInt("instructorNumber"));
        Timestamp creationDate = rs.getTimestamp("creationDate");
        c = new Course(courseNumber, departmentName, classIdentifier, section,
                className, semester, year, instructor, creationDate);
        Timestamp test = rs.getTimestamp("creationDate");
        c.setCreationDate(test);

        return c;
    }

    /**
     * Takes a <code>ResultSet</code> and converts it into an
     * <code>InstructorNote</code>.
     *
     * @param rs The <code>ResultSet</code> to convert into an
     * <code>InstructorNote</code>.
     * @return the <code>InstructorNote</code> made from the
     * <code>ResultSet</code>, or null if none found.
     * @throws java.sql.SQLException If the columnLabel is not valid or the
     * given result set is closed.
     */
    public static InstructorNote makeNoteFromResultSet(ResultSet rs) throws SQLException {
        InstructorNote instructorNote;
        int number = rs.getInt("noteNumber");
        String noteTitle = rs.getString("noteTitle");
        Date startTime = new Date(rs.getTimestamp("startTime").getTime());
        Date endTime = new Date(rs.getTimestamp("endTime").getTime());
        int instructorNumber = rs.getInt("instructorNumber");
        AccessRights accessRights = AccessRights.valueOf(rs.getString("accessRights"));
        String text = rs.getString("note");
        int cameraNumber = rs.getInt("cameraNumber");
        int stationNumber = rs.getInt("stationNumber");
        instructorNote = new InstructorNote(number, noteTitle, startTime, 
                endTime, instructorNumber, accessRights, text, cameraNumber, 
                stationNumber);
        return instructorNote;
    }

    /**
     * Executes an INSERT query and returns the generated key (auto_increment
     * field) for the new data. The given <code>PreparedStatement</code> has to
     * be executed first for the <code>getGeneratedKeys</code> method to work.
     *
     * @param ps The <code>PreparedStatement</code> ready to be executed.
     * @return The id of the new record.
     * @throws java.sql.SQLException If a database access error occurs; this
     * method is called on a closed PreparedStatement or an argument is supplied
     * to this method.
     */
    public static int executeStatementAndReturnGeneratedKey(PreparedStatement ps) throws SQLException {
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.first()) {
            int id = rs.getInt(1);
            return id;
        } else {
            throw new SQLException();
        }
    }

    /**
     * Given a valid <code>ResultSet<code> object this method extracts
     * the next result from the set and makes a <code>Weblink</code> object from
     * it, which it returns.
     *
     * @param rs A result set object containing database query results from the
     * WebLinks database table.
     * @return The WebLink built from the result set, or null if none found.
     * @throws java.sql.SQLException If the columnLabel is not valid; if a
     * database access error occurs or this method is called on a closed result
     * set.
     */
    public static WebLink makeWebLinkFromResultSet(ResultSet rs) throws SQLException {
        WebLink wl = null;
        int linkNumber = rs.getInt("linkNumber");
        String name = rs.getString("name");
        String url = rs.getString("URL");
        WebLinkType type = WebLinkType.valueOf(rs.getString("type"));
        int categoryNumber = rs.getInt("linkCategoryNumber");
        int orderRank = rs.getInt("orderRank");
        wl = new WebLink(linkNumber, name, url, type, categoryNumber, orderRank);
        return wl;
    }

    /**
     * Given a valid <code>ResultSet<code> object this method extracts
     * the next result from the set and makes a <code>DailyDiaryWeblinks</code>
     * object from it, which it returns.
     *
     * @param rs A result set object containing database query results from the
     * dailyDiaryWebLinks database table.
     * @return The DailyDiaryWebLinks built from the result set, or null if none
     * found.
     * @throws java.sql.SQLException If the columnLabel is not valid; if a
     * database access error occurs or this method is called on a closed result
     * set.
     */
    public static DailyDiaryWebLinks makeDailyDiaryWebLinkFromResultSet(ResultSet rs) throws SQLException {
        DailyDiaryWebLinks wl = null;
        String name = rs.getString("linkName");
        String url = rs.getString("URL");
        int linkNumber = rs.getInt("linkNumber");
        wl = new DailyDiaryWebLinks(linkNumber, name, url);
        return wl;
    }

    /**
     * Returns a new WebLinkCategory object from a result set.
     *
     * @param rs The result set to extract data from.
     * @return A new WeblinkCategory object.
     * @throws SQLException If the columnLabel is not valid or if a database
     * access error occurs this error is thrown.
     */
    public static WebLinkCategories makeWebLinkCategoryFromResultSet(ResultSet rs) throws SQLException {
        return new WebLinkCategories(rs.getInt("linkCategoryNumber"),
                rs.getString("linkCategory"), rs.getInt("orderRank"));
    }

    /**
     * Closes the given <code>Connection</code> object if it is not null.
     * Catches SQLException if it is thrown by the <code>close</code> method of
     * <code>Connection</code> interface and logs the message in the log file
     * specified in <code>weather.common.utilities.WeatherLogger</code> class.
     *
     * @param conn The Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
            Debug.println(conn.toString() + " closed");
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to close a Connection object.  The connection "
                    + "object was not null.", e);
        }

    }

    /**
     * Closes the given <code>ResultSet</code> object if it is not null. Catches
     * SQLException if it is thrown by the <code>close</code> method of
     * <code>ResultSet</code> interface and logs the message in the log file
     * specified in <code>weather.common.utilities.WeatherLogger</code> class.
     *
     * @param rs The ResultSet object to close.
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a ResultSet object. The connection "
                        + "object is not null.", e);
            }
        }
    }

    /**
     * Closes the given <code>PreparedStatement</code> object if it is not null.
     * Catches SQLException if it is thrown by the <code>close</code> method of
     * <code>PreparedStatement</code> interface and logs the message in the log
     * file specified in <code>weather.common.utilities.WeatherLogger</code>
     * class.
     *
     * @param ps The PreparedStatement object to close.
     */
    public static void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a PreparedStatement object. The connection "
                        + "object is not null.", e);
            }
        }
    }

    /**
     * Closes the given <code>Statement</code> object if it is not null. Catches
     * SQLException if it is thrown by the <code>close</code> method of
     * <code>Statement</code> interface and logs the message in the log file
     * specified in <code>weather.common.utilities.WeatherLogger</code> class.
     *
     * @param stmt The Statement object to close.
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a Statement object. The connection "
                        + "object is not null.", e);
            }
        }
    }

    public static Lesson makeLessonFromResultSet(ResultSet rs) throws SQLException {
        return new Lesson(rs.getInt("lessonNumber"), rs.getInt("instructorNumber"),
                rs.getInt("lessonCategoryNumber"), AccessRights.getEnum(rs.getString("accessRights")), rs.getString("lessonName"));
    }

    public static LessonCategory makeLessonCategoryFromResultSet(ResultSet rs) throws SQLException {
        return new LessonCategory(rs.getInt("lessonCategoryNumber"), rs.getString("lessonCategoryName"),
                rs.getInt("instructorNumber"), CategoryViewRights.getEnum(rs.getString("accessRights")), rs.getInt("displayOrder"));
    }

    public static LessonEntry makeLessonEntryFromResultSet(ResultSet rs) throws SQLException {
        return new LessonEntry(rs.getInt("lessonEntryNumber"), rs.getInt("lessonNumber"), rs.getString("lessonEntryName"), rs.getInt("bookmarkNumber"),
                WeatherResourceType.valueOf(rs.getString("bookmarkResourceIdentifier")), rs.getInt("windowPosition"));
    }

    public static InstructorFileInstance makeFileInstanceFromResultSet(ResultSet rs) throws SQLException {
        int fileNumber = rs.getInt("fileNumber");
        int instructorNumber = rs.getInt("instructorNumber");
        InstructorDataType type = InstructorDataType.fromString(rs
                .getString("dataType"));
        String fileName = rs.getString("fileName");
        int dataNumber = rs.getInt("dataNumber");
        byte[] file = rs.getBytes("fileContent");

        return new InstructorFileInstance(fileNumber, instructorNumber, type, 
                dataNumber, fileName, file);
    }

    /**
     * Takes a <code>ResultSet</code> and converts it into a 
     * <code>ForecasterLesson</code> object.
     *
     * @param rs The <code>ResultSet</code> to make a <code>ForecasterLesson</code> 
     * from.
     * @return A <code>ForecasterLesson</code> object extracted from the given
     * <code>ResultSet</code>, or null if none found.
     * @throws java.sql.SQLException - If the columnLabel is not valid; if a
     * database access error occurs or this method is called on a closed result
     * set.
     */
    public static ForecasterLesson makeForecasterLessonFromResultSet(
        ResultSet rs) throws SQLException {
        
        String lessonId = rs.getString("forecasterLessonId");
        String name = rs.getString("name");
        Timestamp startDateTime = rs.getTimestamp("startDate");
        Timestamp dueDateTime = rs.getTimestamp("dueDate");
        Date startDate = new Date(startDateTime.getTime());
        Date dueDate = new Date(dueDateTime.getTime());
        int maxTries = rs.getInt("maximumTries");
        String studentEditType = rs.getString("studentEditType");
        boolean active = rs.getBoolean("active");
        boolean useArchiveData = rs.getBoolean("useArchiveData");
        Date archivedStartDate = rs.getDate("archivedDataDate");
        String stationCode = rs.getString("stationCode");

        Course c = makeCourseFromResultSet(rs);
        
        //TODO get questions and instructions as well.
        ForecasterLesson fl = new ForecasterLesson(lessonId, name, studentEditType,
                stationCode, startDate, dueDate, maxTries,
                useArchiveData, archivedStartDate, new Instructions(), c, 
                new ArrayList<Question>(), null);
        try {
            ArrayList<Question> questions = MySQLImpl.getMySQLDMBSSystem().getForecasterQuestionManager().getQuestions(fl);
            fl.setQuestions(questions);
            
            Instructions i = makeInstructionsFromResultSet(rs);
            fl.setInstructions(i);
            
            PointScale ps = makePointScaleFromResultSet(rs);
            fl.setPointScale(ps);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return fl;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Question makeQuestionFromResultSet(
        ResultSet rs) throws SQLException {
        
        String questionId = rs.getString("questionId");
        
        String questionZulu = rs.getString("questionZulu");

        QuestionTemplate qt = makeQuestionTemplateFromResultSet(rs);
        
        //TODO get questions and instructions as well.
        Question q = new Question(questionZulu, questionId, qt);
        q.setQuestionNumber(rs.getInt("questionNumber"));
        return q;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static QuestionTemplate makeQuestionTemplateFromResultSet(
        ResultSet rs) throws SQLException {
        
        String questionTemplateId = rs.getString("questionTemplateId");
        String questionText = rs.getString("questionText");
        String dataKeyPrefix = rs.getString("dataKeyPrefix");
        String questionName = rs.getString("questionName");
        String urlLocation = rs.getString("urlLocation");
        String urlText = rs.getString("urlText");
        String questionType = rs.getString("questionType");
        
        QuestionTemplate qt = new QuestionTemplate(questionTemplateId,
            questionText, dataKeyPrefix, questionName, urlLocation, 
            urlText, AnswerType.valueOf(questionType));
        
        return qt;
    }
     
    /**
     * Takes a
     * <code>ResultSet</code> (already at the desired row) and makes a
     * <code>NoteFileInstance</code> object out of the data at that row.
     *
     * @param rs The <code>ResultSet</code> to make a
     * <code>NoteFileInstance</code> from.
     * @return The <code>NoteFileInstance</code> made from the
     * <code>ResultSet</code>, or null if none found.
     * @throws SQLException If the columnLabel is not valid or the given
     * ResultSet is closed.
     */
    public static NoteFileInstance makeNotesFileInstanceFromResultSet(ResultSet rs)
            throws SQLException {
        NoteFileInstance notesFileInstance;
        int noteFileNumber = rs.getInt("fileNumber");
        int noteNumber = rs.getInt("dataNumber");
        int instructorNumber = rs.getInt("instructorNumber");
        String fileName = rs.getString("fileName");
        byte[] file = rs.getBytes("fileContent");
        notesFileInstance = new NoteFileInstance(noteFileNumber, noteNumber,
                instructorNumber, fileName, file);
        return notesFileInstance;
    }

    /**
     * Takes a
     * <code>ResultSet</code> (already at the desired row) and makes a
     * <code>BookmarkFileInstance</code> object out of the data at that row.
     *
     * @param rs The <code>ResultSet</code> to make a
     * <code>BookmarkFileInstance</code> from.
     * @return The <code>BookmarkFileInstance</code> made from the
     * <code>ResultSet</code>, or null if none found.
     * @throws SQLException If the columnLabel is not valid or the given
     * ResultSet is closed.
     */
    public static BookmarkFileInstance makeBookmarkFileInstanceFromResultSet(ResultSet rs)
            throws SQLException {
        BookmarkFileInstance bookmarkFileInstance;
        int bookmarkFileNumber = rs.getInt("fileNumber");
        int bookmarkNumber = rs.getInt("dataNumber");
        int instructorNumber = rs.getInt("instructorNumber");
        String fileName = rs.getString("fileName");
        byte[] file = rs.getBytes("fileContent");
        bookmarkFileInstance = new BookmarkFileInstance(bookmarkFileNumber, bookmarkNumber,
                instructorNumber, fileName, file);
        return bookmarkFileInstance;
    }

    /**
     * Takes a
     * <code>ResultSet</code> (already at the desired row) and makes a
     * <code>LessonFileInstance</code> object out of the data at that row.
     *
     * @param rs The <code>ResultSet</code> to make a
     * <code>NoteFileInstance</code> from.
     * @return The <code>LessonFileInstance</code> made from the
     * <code>ResultSet</code>, or null if none found.
     * @throws SQLException If the columnLabel is not valid or the given
     * ResultSet is closed.
     */
    public static LessonFileInstance makeLessonFileInstanceFromResultSet(ResultSet rs)
            throws SQLException {
        LessonFileInstance lessonFileInstance;
        int lessonFileNumber = rs.getInt("fileNumber");
        int lessonNumber = rs.getInt("dataNumber");
        int instructorNumber = rs.getInt("instructorNumber");
        String fileName = rs.getString("fileName");
        byte[] file = rs.getBytes("fileContent");
        lessonFileInstance = new LessonFileInstance(lessonFileNumber, lessonNumber,
                instructorNumber, fileName, file);
        return lessonFileInstance;
    }
    
    /**
     * Calculates a range for the two values passed into the method.
     * @param maxVal The max value of the range to calculate.
     * @param minVal The min value of the range to calculate.
     * @return The range of the two values.
     */
    private static String getRange(String maxVal, String minVal) {
        DecimalFormat df = new DecimalFormat("#0.###");
        if (FormatManager.isDecimalValid(maxVal)
                && FormatManager.isDecimalValid(minVal)) {
            return df.format(Double.parseDouble(maxVal)
                    - Double.parseDouble(minVal));
        }
        return "";
    }
    
    /**
     * Takes a <code>ResultSet</code> (already at the desired row) and makes a
     * <code>DailyEntry</code> object for a daily diary out of the data at that 
     * row. Note that the resource names must be passed separately.
     *
     * @param rs The <code>ResultSet</code> to make a <code>DailyEntry</code> 
     * from.
     * @param cameraName The name of the camera <code>Resource</code> for the 
     * entry.
     * @param stationName The name of the station <code>Resource</code> for the 
     * entry.
     * @return The <code>DailyEntry</code> made from the <code>ResultSet</code>.
     * @throws SQLException If the columnLabel is not valid or the given
     * ResultSet is closed.
     */
    public static DailyEntry makeDailyEntryFromResultSet(ResultSet rs, 
            String cameraName, String stationName)
            throws SQLException {
        DailyEntry dailyEntry;
        //Retrieve entry date.
        Timestamp entryDateTimestamp = rs.getTimestamp("entryDate");
        Date entryDate = new Date(entryDateTimestamp.getTime());
        
        int cameraNumber = rs.getInt("cameraNumber");
        int stationNumber = rs.getInt("stationNumber");
        Note entryNote = new Note(entryDate, rs.getString("note"));
        String maxTemp = rs.getString("tempMax");
        String minTemp = rs.getString("tempMin");
        String tempRange = getRange(maxTemp, minTemp);
        TemperatureTrendType tempTrendType = TemperatureTrendType
                .getEnum(rs.getString("tempTrend"));
        String startBP = rs.getString("bpStart");
        String endBP = rs.getString("bpEnd");
        String bpRange = getRange(startBP, endBP);
        BarometricPressureTrendType bpTrendType = BarometricPressureTrendType
                .getEnum(rs.getString("bpTrend"));
        String startDP = rs.getString("dpStart");
        String endDP = rs.getString("dpEnd");
        String dpRange = getRange(startDP, endDP);
        DewPointTrendType dpTrendType = DewPointTrendType
                .getEnum(rs.getString("dpTrend"));
        String maxRH = rs.getString("rhMax");
        String minRH = rs.getString("rhMin");
        String rhRange = getRange(maxRH, minRH);
        RelativeHumidityTrendType rhTrendType = RelativeHumidityTrendType
                .getEnum(rs.getString("rhTrend"));
        CloudType primaryMorningCloudType = CloudType
                .getEnum(rs.getString("cloudsMorningPrimary"));
        CloudType secondaryMorningCloudType = CloudType
                .getEnum(rs.getString("cloudsMorningSecondary"));
        CloudType primaryAfternoonCloudType = CloudType
                .getEnum(rs.getString("cloudsAfternoonPrimary"));
        CloudType secondaryAfternoonCloudType = CloudType
                .getEnum(rs.getString("cloudsAfternoonSecondary"));
        CloudType primaryNightCloudType = CloudType
                .getEnum(rs.getString("cloudsNightPrimary"));
        CloudType secondaryNightCloudType = CloudType
                .getEnum(rs.getString("cloudsNightSecondary"));
        
        //Make wind direction array.
        ArrayList<WindDirectionType> surfaceAirWindDirection
                = new ArrayList<>();
        StringTokenizer windTokenizer = new StringTokenizer(rs
                .getString("windDirectionList"), ",");
        while (windTokenizer.hasMoreTokens()) {
            String token = windTokenizer.nextToken();
            surfaceAirWindDirection.add(WindDirectionType.getEnum(token));
        }
        
        WindDirectionSummaryType windDirectionSummary = WindDirectionSummaryType
                .getEnum(rs.getString("windDirectionSummary"));
        WindSpeedType windSpeed = WindSpeedType.getEnum(rs.
                getString("windSpeed"));
        String maxGustSpeed = rs.getString("windGust");
        String dailyPrecipitation = rs.getString("dailyPrecip");
        String maxHeatIndex = rs.getString("heatIndex");
        String minWindChill = rs.getString("windChill");
        WindDirectionType upperAirWindDirection = WindDirectionType
                .getEnum(rs.getString("upperAirWindDirection"));
        
        //Retrieve the date when the entry was last modified.
        Timestamp lastModifiedTimestamp = rs.getTimestamp("lastModified");
        Date lastModifiedDate = new Date(lastModifiedTimestamp.getTime());
        
        dailyEntry = new DailyEntry(entryDate, cameraNumber, cameraName, 
            stationNumber, stationName, entryNote, maxTemp, minTemp, tempRange, 
            tempTrendType, startBP, endBP, bpRange, bpTrendType, startDP,
            endDP, dpRange, dpTrendType, maxRH, minRH, rhRange, rhTrendType,
            primaryMorningCloudType, secondaryMorningCloudType,
            primaryAfternoonCloudType, secondaryAfternoonCloudType,
            primaryNightCloudType, secondaryNightCloudType, 
            surfaceAirWindDirection, windDirectionSummary, windSpeed, 
            maxGustSpeed, dailyPrecipitation, maxHeatIndex, minWindChill,
            upperAirWindDirection, lastModifiedDate);
        return dailyEntry;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Answer makeAnswerFromResultSet(ResultSet rs) 
            throws SQLException {
        String answerId = rs.getString("answerId");
        String answerText = rs.getString("answerText");
        String answerValue = rs.getString("answerValue");
       
        Question q = makeQuestionFromResultSet(rs);
        //TODO get questions and instructions as well.
        Answer a = new Answer(answerId, answerText, answerValue, q);

        return a;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Response makeResponseFromResultSet(ResultSet rs) 
            throws SQLException {
        String responseId = rs.getString("responseId");
        
        rs.getString("scoreId");
        Score s = null;
        if(!rs.wasNull())
        {
            s = makeScoreFromResultSet(rs);
        }
        
        Response r = new Response(responseId, null);
        
        if(s != null)
        {
            r.setResponseScore(s);
        }

        return r;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Attempt makeAttemptFromResultSet(ResultSet rs) 
            throws SQLException{
        String attemptId = rs.getString("attemptId");
        Date attemptDate = rs.getDate("attemptDate");
        String stationCode = rs.getString("stationCode");
        User user = makeUserFromResultSet(rs);
        
        Attempt a = new Attempt(attemptId, stationCode, user, attemptDate);
        
        return a;
    }
    
    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Score makeScoreFromResultSet(ResultSet rs) 
            throws SQLException{
        String attemptScoreId = rs.getString("scoreId");
        int pointsEarned = rs.getInt("pointsEarned");
        int pointsPossible = rs.getInt("pointsPossible");
        
        Score s = new Score(attemptScoreId, pointsEarned, pointsPossible);
        
        return s;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static PointScale makePointScaleFromResultSet(ResultSet rs) 
            throws SQLException{
        String scoreId = rs.getString("scoreId");
        int correctPoints = rs.getInt("correctPoints");
        int incorrectPoints = rs.getInt("incorrectPoints");
        int unansweredPoints = rs.getInt("unansweredPoints");
        int topScoresCounted = rs.getInt("topScoresCounted");
        boolean requireAnswers = rs.getBoolean("requireAnswers");
        
        PointScale ps = new PointScale(scoreId, correctPoints, incorrectPoints, 
                unansweredPoints, topScoresCounted, requireAnswers);
        
        return ps;
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Instructions makeInstructionsFromResultSet(ResultSet rs) 
             throws SQLException{
        String instructionsId = rs.getString("instructionId");
        String instructionsText = rs.getString("instructionsText");

        
        Instructions i = new Instructions(instructionsId, instructionsText);
        return i;
    }
    
    /**
     * 
     * @param rs
     * @return
     * @throws SQLException 
     */
    public static MissingWebGradingDataRecord 
            makeMissingWebGradingDataRecordFromResultSet(ResultSet rs) 
             throws SQLException{
        String recordId = rs.getNString("forecasterMissingDataRowId");
        String lessonId = rs.getNString("forecasterLessonId");
        Date recordDate = rs.getDate("recordDate");
        String stationCode = rs.getString("stationCode");
        boolean isInstructorDataSet = rs.getBoolean("hasInstructorData");
        boolean wasEmailSent = rs.getBoolean("emailSent");
        
        MissingWebGradingDataRecord r = new MissingWebGradingDataRecord(
                recordId, lessonId, recordDate, stationCode, 
                isInstructorDataSet, wasEmailSent);
        return r;
    }
    
    /**
     * Takes a
     * <code>ResultSet</code> (already at the desired row) and makes a
     * <code>Version</code> object out of the data at that row.
     *
     * @param rs The <code>ResultSet</code> to make a <code>Version</code> from.
     * @return The <code>Version</code> made from the <code>ResultSet</code>, or
     * null if none found.
     * @throws SQLException If the columnLabel is not valid or the given
     * ResultSet is closed.
     */
    public static Version makeVersionFromResultSet(ResultSet rs)
            throws SQLException {
        if (rs == null) {
            return null;
        }
        int majorVersionNumber = rs.getInt("majorVersionNumber");
        int minorVersionNumber = rs.getInt("minorVersionNumber");
        int minorReleaseNumber = rs.getInt("minorReleaseNumber");
        String releaseNotes = rs.getString("releaseNotes");
        Date releaseDate = rs.getDate("releaseDate"); 
        Version version = new Version(majorVersionNumber, minorVersionNumber,
                minorReleaseNumber);
        version.setVersionNotes(releaseNotes);
        version.setReleaseDate(releaseDate);
        return version;
    }
}
