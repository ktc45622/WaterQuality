
package weather.common.dbms.remote;

import java.util.ArrayList;
import java.util.logging.Level;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSDailyDiaryWebLinkManager;
import weather.common.dbms.DBMSDiaryManager;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.dbms.DBMSEventManager;
import weather.common.dbms.DBMSFileManager;
import weather.common.dbms.DBMSForecasterAnswerManager;
import weather.common.dbms.DBMSForecasterAttemptManager;
import weather.common.dbms.DBMSForecasterHintManager;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.dbms.DBMSForecasterQuestionManager;
import weather.common.dbms.DBMSForecasterQuestionTemplateManager;
import weather.common.dbms.DBMSForecasterResponseManager;
import weather.common.dbms.DBMSForecasterScoreManager;
import weather.common.dbms.DBMSForecasterStationDataManager;
import weather.common.dbms.DBMSInstructorResponseManager;
import weather.common.dbms.DBMSLessonCategoryManager;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.dbms.DBMSLessonManager;
import weather.common.dbms.DBMSMissingDataRecordManager;
import weather.common.dbms.DBMSNoteManager;
import weather.common.dbms.DBMSPropertyManager;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.DBMSResourceRelationManager;
import weather.common.dbms.DBMSStationManager;
import weather.common.dbms.DBMSSystemManager; 
import weather.common.dbms.DBMSUserManager;
import weather.common.dbms.DBMSVersionManager;
import weather.common.dbms.DBMSWeatherStationSearchDataManager;
import weather.common.dbms.DBMSWebLinkManager;
import weather.common.dbms.remote.managers.*;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * Used on the client side to use the remote versions of what is on the server 
 * side, currently also on the client side.
 * @author Brian Zaiser
 */
public class DatabaseRemoteSystemManager implements DBMSSystemManager {

    @Override
    public DBMSForecasterLessonManager getForecasterLessonManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private DBMSBookmarkCategoriesManager bookmarkCategoriesManager;
    private DBMSBookmarkEventTypesManager bookmarkEventTypesManager;
    private DBMSBookmarkInstanceManager bookmarkInstanceManager;
    private DBMSCourseManager courseManager;
    private DBMSEnrollmentManager enrollmentManager;
    private DBMSEventManager eventManager;
    private DBMSFileManager fileManager;
    private DBMSLessonCategoryManager lessonCategoryManager;
    private DBMSLessonEntryManager lessonEntryManager;
    private DBMSLessonManager lessonManager;
    private DBMSNoteManager noteManager;
    private DBMSResourceManager resourceManager;
    private DBMSResourceRelationManager resourceRelationManager;
    private DBMSUserManager userManager;
    private DBMSWebLinkManager webLinkManager;
    private DBMSDailyDiaryWebLinkManager dailyDiaryWebLinkManager;
    private DBMSWeatherStationSearchDataManager weatherStationSearchDataManager;
    private static DatabaseRemoteSystemManager singletonInstance;
    
    /**
     * Default Constructor - instantiates the member managers.
     */
    public DatabaseRemoteSystemManager (){
        
        bookmarkCategoriesManager = new BookmarkCategoriesManager();
        bookmarkEventTypesManager = new BookmarkEventTypesManager();
        bookmarkInstanceManager = new BookmarkInstanceManager();
        courseManager = new CourseManager();
        enrollmentManager = new EnrollmentManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        lessonCategoryManager = new LessonCategoryManager();
        lessonEntryManager = new LessonEntryManager();
        lessonManager = new LessonManager();
        noteManager = new NoteManager();
        resourceManager = new ResourceManager(); 
        resourceRelationManager = new ResourceRelationManager();
        userManager = new UserManager();
        webLinkManager = new WebLinkManager();
        dailyDiaryWebLinkManager = new DailyDiaryWebLinkManager();
        weatherStationSearchDataManager = new WeatherStationSearchDataManager();
    }
    
    /**
     * Fetches the DatabaseRemoteSystemManager singleton instance. 
     * @return the DatabaseRemoteSystemManager singleton instance.
     */
    public static DatabaseRemoteSystemManager getDatabaseRemoteSystemManager() {
        if (singletonInstance == null) {
            singletonInstance = new DatabaseRemoteSystemManager();
        }
        return singletonInstance;
    }
    
    
    /**
     * NOTE: This method is required ever though it is not directly called.  See
     * the opening remarks of <code>DBMSSystemManager</code>.
     * Gets DBMS implementation as a <code>DBMSSystemManager</code>.
     */
    public static DBMSSystemManager getInstance() {
        return getDatabaseRemoteSystemManager();
    }
    
    /**
     * Creates the database tables, if possible; throws a WeatherException otherwise.
     * @throws WeatherException if operation does not complete successfully.
     */
    @Override
    public void createTables() throws WeatherException {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = DatabaseCommandType.SystemManager_CreateTables;
        ArrayList<Object> arguments = new ArrayList<>();
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            WeatherLogger.log(Level.SEVERE, "Bad RemoteDatabaseResult returned"
                    + " while trying to create tables.");
        }
        if (!result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
            WeatherLogger.log(Level.SEVERE, "Bad RemoteDatabaseResult returned"
                    + " while trying to create tables.");
        }
    }

    /**
     * Inserts the default users into the database, if possible; throws a 
     * WeatherException otherwise.
     * @throws WeatherException if operation does not complete successfully.
     */
    @Override
    public void insertDefaultUsers() throws WeatherException {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = DatabaseCommandType.SystemManager_InsertDefaultUsers;
        ArrayList<Object> arguments = new ArrayList<>();
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            WeatherLogger.log(Level.SEVERE, "Bad RemoteDatabaseResult returned"
                    + " while trying to insert default users.");
        }
        if (!result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
            WeatherLogger.log(Level.SEVERE, "Bad RemoteDatabaseResult returned"
                    + " while trying to insert default users.");
        }
    }

    /**
     * Returns the member BookmarkCategoriesManager.
     * @return The member BookmarkCategoriesManager.
     */
    @Override
    public DBMSBookmarkCategoriesManager getBookmarkCategoriesManager() {
        return this.bookmarkCategoriesManager;
    }

    /**
     * Returns the member BookmarkManager.
     * @return The member BookmarkManager.
     */
    @Override
    public DBMSBookmarkInstanceManager getBookmarkManager() {
        return this.bookmarkInstanceManager;
    }

    /**
     * Returns the member BookmarkTypesManager.
     * @return The member BookmarkTypesManager.
     */
    @Override
    public DBMSBookmarkEventTypesManager getBookmarkTypesManager() {
        return this.bookmarkEventTypesManager;
    }

    /**
     * Returns the member CourseManager.
     * @return The member CourseManager.
     */
    @Override
    public DBMSCourseManager getCourseManager() {
        return this.courseManager;
    }

    /**
     * Returns the member EnrollmentManager.
     * @return The member EnrollmentManager.
     */
    @Override
    public DBMSEnrollmentManager getEnrollmentManager() {
        return this.enrollmentManager;
    }

    /**
     * Returns the member EventManager.
     * @return The member EventManager.
     */
    @Override
    public DBMSEventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * Returns the member FileManager.
     * @return The member FileManager.
     */
    @Override
    public DBMSFileManager getFileManager() {        
        return this.fileManager;
    }

    /**
     * Returns the member LessonCategoryManager.
     * @return The member LessonCategoryManager.
     */
    @Override
    public DBMSLessonCategoryManager getLessonCategoryManager() {
        return this.lessonCategoryManager;
    }

    /**
     * Returns the member LessonEntryManager.
     * @return The member LessonEntryManager.
     */
    @Override
    public DBMSLessonEntryManager getLessonEntryManager() {
        return this.lessonEntryManager;
    }

    /**
     * Returns the member LessonManager.
     * @return The member LessonManager.
     */
    @Override
    public DBMSLessonManager getLessonManager() {
        return this.lessonManager;
    }

    /**
     * Returns the member NoteManager.
     * @return The member NoteManager.
     */
    @Override
    public DBMSNoteManager getNoteManager() {
        return this.noteManager;
    }

    /**
     * Returns the member ResourceManager.
     * @return The member ResourceManager.
     */
    @Override
    public DBMSResourceManager getResourceManager() {
        return this.resourceManager;
    }

    /**
     * Returns the member ResourceRelationManager.
     * @return The member ResourceRelationmanager.
     */
    @Override
    public DBMSResourceRelationManager getResourceRelationManager() {
        return this.resourceRelationManager;
    }

    /**
     * Returns the member UserManager.
     * @return The member userManager.
     */
    @Override
    public DBMSUserManager getUserManager() {
        return this.userManager;
    }

    /**
     * Returns the member WebLinkManager.
     * @return The member WebLinkManager.
     */
    @Override
    public DBMSWebLinkManager getWebLinkManager() {
        return this.webLinkManager;
    }

    @Override
    public DBMSDailyDiaryWebLinkManager getDailyDiaryWebLinkManager() {
        return this.dailyDiaryWebLinkManager;
    }

    @Override
    public DBMSWeatherStationSearchDataManager getWeatherStationSearchDataManager() {
        return weatherStationSearchDataManager;
    }
     
    @Override
    public DBMSPropertyManager getPropertyManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Request the connections to the database be dropped (returned to the pool).
     */
    @Override
    public void closeDatabaseConnections() {
    }

    @Override
    public DBMSForecasterAnswerManager getForecasterAnswerManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSForecasterAttemptManager getForecasterAttemptManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSForecasterHintManager getForecasterHintManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSForecasterQuestionManager getForecasterQuestionManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSForecasterQuestionTemplateManager getForecasterQuestionTemplateManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSForecasterResponseManager getForecasterResponseManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSForecasterScoreManager getForecasterScoreManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBMSForecasterStationDataManager getForecasterStationDataManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSStationManager getStationManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DBMSInstructorResponseManager getInstructorResponseManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBMSMissingDataRecordManager getMissingDataRecordManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBMSVersionManager getVersionManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DBMSDiaryManager getDiaryManager() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
     @Override
    public boolean isBUConnectionOpen() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
