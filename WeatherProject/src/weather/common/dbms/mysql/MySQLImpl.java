package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import weather.common.dbms.*;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The MySQLImpl class performs all interactions with the database.
 * Its constructor is used to created instances of the manager classes as
 * follows:
 * <pre>
 *  DBMSSystemManager system = new MySQLImpl();
 *  Vector<Resource> resources = system.getResourceManager().getResourceList();
 * </pre>
 *
 * @author Bloomsburg University Software Engineering
 * @author Anthony Tersine (2007)
 * @author David Reichert (2008)
 * @author Chad Hall (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */

public class MySQLImpl implements DBMSSystemManager {
    private final MySQLPropertiesSingleton properties;
    
    // These should all be of type DBMS ... NOT MySQL...
    private DBMSUserManager userManager;
    private DBMSResourceManager resourceManager;
    private DBMSCourseManager courseManager;
    private DBMSEnrollmentManager enrollmentManager;
    private DBMSNoteManager noteManager;
    private DBMSResourceRelationManager resourceRelationManager;
    private DBMSWebLinkManager webLinkManager;
    private DBMSEventManager eventManager;
    private DBMSBookmarkEventTypesManager bookmarkTypesManager;
    private DBMSBookmarkInstanceManager bookmarkManager;
    private DBMSBookmarkCategoriesManager bookmarkCategoryManager;
    private DBMSLessonManager lessonManager;
    private DBMSLessonEntryManager lessonEntryManager;
    private DBMSLessonCategoryManager lessonCategoryManager;
    private DBMSFileManager fileManager;
    private DBMSDailyDiaryWebLinkManager dailyDiaryWebLinkManager;
    private DBMSWeatherStationSearchDataManager weatherStationSearchDataManager;
    private DBMSForecasterLessonManager forecasterLessonManager;
    private DBMSForecasterQuestionManager forecasterQuestionManager;
    private DBMSForecasterQuestionTemplateManager forecasterQuestionTemplateManager;
    private DBMSForecasterAnswerManager forecasterAnswerManager;
    private DBMSForecasterAttemptManager forecasterAttemptManager;
    private DBMSForecasterHintManager forecasterHintManager;
    private DBMSForecasterResponseManager forecasterResponseManager;
    private DBMSForecasterScoreManager forecasterScoreManager;
    private DBMSForecasterStationDataManager forecasterStationDataManager;
    private DBMSInstructorResponseManager instructorResponseManager;
    private DBMSMissingDataRecordManager missingDataRecordManager;
    private DBMSPropertyManager propertyManager;
    private DBMSStationManager stationManager;
    private DBMSVersionManager versionManager;
    private DBMSDiaryManager diaryManager;
   
    // Connections and singleton instance of this class.
    private Connection localConnection = null;  //for institutional data
    private Connection bloomConnection = null;  //for bloom data (versions)
    private static MySQLImpl instance = null;

    
    /**
     * Gets DBMS implementation as a <code>MySQLImpl</code>.
     *
     * @throws ClassNotFoundException If the <code>Class</code> cannot be
     * located.
     * @throws IllegalAccessException If the <code>Class</code> or its
     * nullary constructor is not accessible.
     * @throws InstantiationException If this <code>Class</code> represents an
     * abstract class, an interface, an array class, a primitive type, or void;
     * or if the class has no nullary constructor; or if the instantiation fails
     * for some other reason.
     */
    public static MySQLImpl getMySQLDMBSSystem() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        
        if(instance == null){
            instance = new MySQLImpl(); 
        }
        return instance; 
    }
    
    /**
     * NOTE: This method is required ever though it is not directly called.  See
     * the opening remarks of <code>DBMSSystemManager</code>.
     * 
     * Gets DBMS implementation as a <code>DBMSSystemManager</code>.
     *
     * @throws ClassNotFoundException If the <code>Class</code> cannot be
     * located.
     * @throws IllegalAccessException If the <code>Class</code> or its
     * nullary constructor is not accessible.
     * @throws InstantiationException If this <code>Class</code> represents an
     * abstract class, an interface, an array class, a primitive type, or void;
     * or if the class has no nullary constructor; or if the instantiation fails
     * for some other reason.
     */
    public static DBMSSystemManager getInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return getMySQLDMBSSystem();
    }
    
    /**
     * Sets parameters for database connection.
     *
     * @throws ClassNotFoundException If the <code>Class</code> cannot be
     * located.
     * @throws IllegalAccessException If the <code>Class</code> or its
     * nullary constructor is not accessible.
     * @throws InstantiationException If this <code>Class</code> represents an
     * abstract class, an interface, an array class, a primitive type, or void;
     * or if the class has no nullary constructor; or if the instantiation fails
     * for some other reason.
     */
    private  MySQLImpl() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        this.properties = MySQLPropertiesSingleton.getInstance();
        this.userManager = new MySQLUserManager(this);
        this.resourceManager = new MySQLResourceManager(this);
        this.courseManager = new MySQLCourseManager(this);
        this.enrollmentManager = new MySQLEnrollmentManager(this);
        this.noteManager = new MySQLNoteManager(this);
        this.resourceRelationManager = new MySQLResourceRelationManager(this);
        this.webLinkManager = new MySQLWebLinkManager(this);
        this.eventManager = new MySQLEventManager(this);
        this.bookmarkTypesManager = new MySQLBookmarkTypesManager(this);
        this.bookmarkManager = new MySQLBookmarkManager(this);
        this.bookmarkCategoryManager = new MYSQLBookmarkCategoriesManager(this);
        this.lessonManager = new MySQLLessonManager(this);
        this.lessonEntryManager = new MySQLLessonEntryManager(this);
        this.lessonCategoryManager = new MySQLLessonCategoryManager(this);
        this.fileManager = new MySQLFileManager(this);
        this.dailyDiaryWebLinkManager = new MySQLDailyDiaryWebLinkManager(this);
        this.weatherStationSearchDataManager = new MYSQLWeatherstationSearchData(this);
        this.forecasterLessonManager = new MySQLForecasterLessonManager(this);
        this.forecasterQuestionTemplateManager = new MySQLForecasterQuestionTemplateManager(this);
        this.forecasterQuestionManager = new MySQLForecasterQuestionManager(this);
        this.forecasterAttemptManager = new MySQLForecasterAttemptManager(this);
        this.forecasterAnswerManager = new MySQLForecasterAnswerManager(this);
        this.forecasterResponseManager = new MySQLForecasterResponseManager(this);
        this.forecasterStationDataManager = new MySQLForecasterStationDataManager(this);
        this.instructorResponseManager = new MySQLInstructorResponseManager(this);
        this.missingDataRecordManager = new MySQLMissingDataRecordManager(this);
        this.propertyManager = new MYSQLPropertyManager(this);
        this.stationManager = new MYSQLStationManager(this);
        this.versionManager = new MySQLVersionManager(this);
        this.diaryManager = new MySQLDiaryManager(this);
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        localConnection = null;
        localConnection = configureLocalConnection();
        bloomConnection = null;
        bloomConnection = configureBUConnection();
    }
    
    /**
     * Sets either of class <code>Connection</code> variables to the appropriate
     * connection.
     * 
     * @param connection Either <code>localConnection</code> or 
     * <code>bloomConnectionn</code>.
     */
    private void setConnection(Connection connection) {
        if (connection == localConnection) {
            localConnection = configureLocalConnection();
        } else {
            bloomConnection = configureBUConnection();
        }
    }

    /**
     * Creates a connection with a DriverManager and returns it if the
     * connection is established. 
     * 
     * Catches SQLException if it is thrown by the 
     * <code>getConnection(String url, String user, String password)</code>
     * method of <code>DriverManager</code> class; logs the message in the log
     * file specified in the <code>weather.common.utilities.WeatherLogger</code>
     * class; creates a new <code>WeatherException</code> object; shows
     * the message to the user; the program then is terminated.
     *
     * @return A <code>Connection</code> to the database.
     */
    public Connection getLocalConnection() {
        return getConnection(localConnection);
    }
    
    /**
     * Creates a connection with a DriverManager and returns it if the 
     * connection is established. 
     * 
     * Catches SQLException if it is thrown by the 
     * <code>getConnection(String url, String user, String password)</code>
     * method of <code>DriverManager</code> class; logs the message in the log
     * file specified in the <code>weather.common.utilities.WeatherLogger</code>
     * class; creates a new <code>WeatherException</code> object; shows
     * the message to the user; the program then is terminated.
     *
     * @return A <code>Connection</code> to the database.
     */
    public Connection getBUConnection() {
        return getConnection(bloomConnection);
    }
    
    /**
     * Gets the working connection specified by the parameter.
     * 
     * @param connection Either <code>localConnection</code> or 
     * <code>bloomConnectionn</code>.
     * @return The appropriate connection.
     */
    private Connection getConnection(Connection connection) {
        try {
            if (connection == null || connection.isClosed()) {
                setConnection(connection);
                return connection;
            }
            // Now and old connection did exist, but is it still valid?

            //allow 3 seconds to see if connection is still valid
            if (connection.isValid(3)) {
                return connection;
            } else {
                setConnection(connection);
                return connection;
            }
        } catch (SQLException e) {
            Debug.println("An SQL exceoeption in getConnection(Connection connection).The error is");
            Debug.println(e.toString());
            setConnection(connection);
            return connection;
        }
    }

    /**
     * Creates a connection to the weather project database of the user's 
     * institution with a DriverManager and returns it if the connection is
     * established. Catches SQLException if it is thrown by the
     * <code>getConnection(String url, String user, String password)</code>
     * method of <code>DriverManager</code> class; logs the message in the log
     * file specified in the <code>weather.common.utilities.WeatherLogger</code>
     * class; creates a new <code>WeatherException</code> object; shows
     * the message to the user; the program then is terminated.
     *
     * @return A <code>Connection</code> to the database of the user's
     * institution.
     */
    private Connection configureLocalConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(properties.getLocalDatabaseServer(),
                    properties.getDatabaseUsername(), properties.getDatabasePassword());
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to connect to the institutional database. The "
                    + "Connection object was set null. ", e);
            new WeatherException(4012, true, e,
                    "  The program could not connect to the institutional database")
                    .show();
        }
        return conn;
    }

    /**
     * Creates a connection to database containing information needed by all
     * institutions that is at Bloomsburg University with a DriverManager and 
     * returns it if the connection is established. Catches SQLException if it
     * is thrown by the <code>getConnection(String url, String user, String 
     * password)</code> method of <code>DriverManager</code> class; logs the 
     * message in the log file specified in the 
     * <code>weather.common.utilities.WeatherLogger</code> class This function 
     * will return null if no connection is established.
     * 
     * @return A <code>Connection</code> to the Bloomsburg University database 
     * with the publicly-available data.  This function will make a log and 
     * return null if no connection is established.
     */
    private Connection configureBUConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(properties.getBUDatabaseServer(),
                    properties.getDatabaseUsername(), properties.getDatabasePassword());
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to connect to the Bloomsburg University database."
                    + " The Connection object was set null. ", e);
        }
        return conn;
    }
   
    @Override
    public void createTables() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getLocalConnection();
            stmt = conn.createStatement();
            stmt.execute(properties.getCreateUsersTable());
            stmt.execute(properties.getCreateResourcesTable());
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(4013, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeStatement(stmt);
        }
    }

    @Override
    public void insertDefaultUsers() throws WeatherException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getLocalConnection();
            stmt = conn.createStatement();
            stmt.execute(properties.getDefaultUsers());
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(4014, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeStatement(stmt);
        }
    }

    @Override
    public DBMSResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public DBMSUserManager getUserManager() {
        return userManager;
    }

    @Override
    public DBMSCourseManager getCourseManager() {
        return courseManager;
    }

    @Override
    public DBMSEnrollmentManager getEnrollmentManager() {
        return enrollmentManager;
    }
    @Override
    public DBMSNoteManager getNoteManager() {
        return noteManager;
    }

    @Override
    public DBMSResourceRelationManager getResourceRelationManager() {
        return resourceRelationManager;
    }

    @Override
    public DBMSWebLinkManager getWebLinkManager() {
        return webLinkManager;
    }

    @Override
    public DBMSEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public DBMSBookmarkInstanceManager getBookmarkManager() {
        return bookmarkManager;
    }

    @Override
    public DBMSBookmarkCategoriesManager getBookmarkCategoriesManager() {
        return bookmarkCategoryManager;
    }

    @Override
    public DBMSBookmarkEventTypesManager getBookmarkTypesManager() {
        return bookmarkTypesManager;
    }

    @Override
    public DBMSLessonManager getLessonManager() {
        return lessonManager;
    }

    @Override
    public DBMSLessonEntryManager getLessonEntryManager() {
        return lessonEntryManager;
    }

    @Override
    public DBMSLessonCategoryManager getLessonCategoryManager() {
        return lessonCategoryManager;
    }
    
    @Override
    public DBMSFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public DBMSDailyDiaryWebLinkManager getDailyDiaryWebLinkManager() {
        return dailyDiaryWebLinkManager;
    }

    @Override
    public DBMSWeatherStationSearchDataManager getWeatherStationSearchDataManager() {
        return weatherStationSearchDataManager;
    }

    @Override
    public DBMSForecasterLessonManager getForecasterLessonManager() {
        return forecasterLessonManager;
    }

    @Override
    public DBMSPropertyManager getPropertyManager() {
        return propertyManager;
    }

    @Override
    public DBMSForecasterAnswerManager getForecasterAnswerManager() {
        return forecasterAnswerManager;
    }

    @Override
    public DBMSForecasterAttemptManager getForecasterAttemptManager() {
        return forecasterAttemptManager;
    }

    @Override
    public DBMSForecasterHintManager getForecasterHintManager() {
        return forecasterHintManager;
    }

    @Override
    public DBMSForecasterQuestionManager getForecasterQuestionManager() {
        return forecasterQuestionManager;
    }

    @Override
    public DBMSForecasterQuestionTemplateManager getForecasterQuestionTemplateManager() {
        return forecasterQuestionTemplateManager;
    }

    @Override
    public DBMSForecasterResponseManager getForecasterResponseManager() {
        return forecasterResponseManager;
    }

    @Override
    public DBMSForecasterScoreManager getForecasterScoreManager() {
        return forecasterScoreManager;
    }

    @Override
    public DBMSForecasterStationDataManager getForecasterStationDataManager() {
        return forecasterStationDataManager;
    }

    @Override
    public DBMSStationManager getStationManager() {
        return stationManager;
    }

    @Override
    public DBMSInstructorResponseManager getInstructorResponseManager() {
        return instructorResponseManager;
    }

    @Override
    public DBMSMissingDataRecordManager getMissingDataRecordManager() {
        return missingDataRecordManager;
    }

    @Override
    public DBMSVersionManager getVersionManager() {
        return versionManager;
    }
    
    @Override
    public DBMSDiaryManager getDiaryManager() {
        return diaryManager;
    }

    @Override
    public void closeDatabaseConnections() {
        MySQLHelper.closeConnection(localConnection);
        MySQLHelper.closeConnection(bloomConnection);
    }

    @Override
    public boolean isBUConnectionOpen() {
        if (bloomConnection == null)return false;
        int timeout = 0; // no time limit
        try {
            return bloomConnection.isValid(timeout);
        } catch (SQLException ex) {
            return false;
        }
    }
}
