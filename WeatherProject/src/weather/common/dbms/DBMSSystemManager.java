package weather.common.dbms;

import weather.common.utilities.WeatherException;

/**
 * This interface defines the different systems that must be provided by our database.
 * Currently, each table is getting its own DBMSManager, which needs to be responsible for
 * the actions that need to be performed using that table.
 * 
 * NOTE: This interface is intended to be implemented by classes with singleton 
 * implementations.  To avoid run-time failure, all implementing classes MUST 
 * have a static method getInstance() that returns the singleton instance as 
 * type <code>DBMSSystemManager</code>.  It is invoked via the 
 * <code>Method</code> class in the initialization of 
 * <code>CreateApplicationControlSystemThread</code>.
 *
 * @author Bloomsburg University Software Engineering
 * @author Curt Jones (2008)
 * @author Chad Hall (2008)
 * @author Joseph Horro (2011)
 * @version Spring 2008
 * @version Spring 2011
 * 
 */
public interface DBMSSystemManager {
    
    /**
     * Creates the table structure for the User and Resource table. The table
     * design should be generated from a property file.
     *
     * @throws weather.common.utilities.WeatherException  If a database access
     * error occurs
     */
    public void createTables() throws WeatherException;

    /**
     * Inserts our default 'Guest', 'Student', 'Instructor', and 'Admin' users.
     *
     * @throws weather.common.utilities.WeatherException  If a database access
     * error occurs
     */
    public void insertDefaultUsers() throws WeatherException;

    /**
     * Returns the Manager responsible for the Users table.
     *
     * @return The
     * <code>DBMSUserManager</code> object.
     */
    public DBMSUserManager getUserManager();

    /**
     * Returns the Manager responsible for the Resources table.
     *
     * @return The
     * <code>DBMSResourceManager</code> object.
     */
    public DBMSResourceManager getResourceManager();

    /**
     * Returns the Manager responsible for the Courses table.
     *
     * @return The
     * <code>DBMSCourseManager</code> object.
     */
    public DBMSCourseManager getCourseManager();

    /**
     * Returns the Manager responsible for the Enrollment table.
     *
     * @return The
     * <code>DBMSEnrollmentManager</code> object.
     */
    public DBMSEnrollmentManager getEnrollmentManager();

    /**
     * Returns the Manager responsible for the Notes table.
     *
     * @return The
     * <code>DBMSNoteManager</code> object.
     */
    public DBMSNoteManager getNoteManager();

    /**
     * Returns the Manager responsible for the ResourceRelation table.
     *
     * @return The
     * <code>DBMSResourceRelationManager</code> object.
     */
    public DBMSResourceRelationManager getResourceRelationManager();

    /**
     * Returns the Manager responsible for the WebLinks table.
     *
     * @return The
     * <code>DBMSWebLinkManager</code> object.
     */
    public DBMSWebLinkManager getWebLinkManager();

    /**
     * Returns an instance of the
     * <code>DBMSEventManager</code> class.
     *
     * @return A
     * <code>DBMSEventManager</code> object.
     */
    public DBMSEventManager getEventManager();

    /**
     * Returns an instance of the
     * <code>DBMSBookmarkEventTypesManager</code>.
     * 
     * @return A 
     * <code>DBMSBookmarkEventTypesManager</code> object.
     */
    public DBMSBookmarkEventTypesManager getBookmarkTypesManager();

    /**
     * Returns an instance of the
     * <code>DBMSBookmarkInstanceManager</code>.
     *
     * @return A
     * <code>DBMSBookmarkInstanceManager</code> object.
     */
    public DBMSBookmarkInstanceManager getBookmarkManager();

    /**
     * Returns an instance of
     * <code>DBMSBookmarkCategoriesManager</code>.
     *
     * @return A
     * <code>DBMSBookmarkCategoriesManager</code> object.
     */
    public DBMSBookmarkCategoriesManager getBookmarkCategoriesManager();
    
    /**
     * Returns an instance of
     * <code>DBMSLessonManager</code>.
     *
     * @return A
     * <code>DBMSLessonManager</code> object.
     */
    public DBMSLessonManager getLessonManager();
    
    /**
     * Returns an instance of
     * <code>DBMSLessonEntryManager</code>.
     *
     * @return A
     * <code>DBMSLessonEntryManager</code> object.
     */
    public DBMSLessonEntryManager getLessonEntryManager();
    
    /**
     * Returns an instance of
     * <code>DBMSLessonCategoryManager</code>.
     *
     * @return A
     * <code>DBMSLessonCategoryManager</code> object.
     */
    public DBMSLessonCategoryManager getLessonCategoryManager();
    
    /**
     * Returns an instance of
     * <code>DBMSFileManager</code>.
     *
     * @return A
     * <code>DBMSFileManager</code> object.
     */
    public DBMSFileManager getFileManager();
    
    /**
     * Returns an instance of
     * <code>DBMSDailyDiaryWebLinkManager</code>.
     *
     * @return A
     * <code>DBMSDailyDiaryWebLinkManager</code> object.
     */
    public DBMSDailyDiaryWebLinkManager getDailyDiaryWebLinkManager();
    
    /**
     * Returns an instance of
     * <code>DBMSWeatherStationSearchDataManager</code>.
     *
     * @return A
     * <code>DBMSWeatherStationSearchDataManager</code> object.
     */
    public DBMSWeatherStationSearchDataManager getWeatherStationSearchDataManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterLessonManager</code>.
     *
     * @return A
     * <code>DBMSForecasterLessonManager</code> object.
     */
    public DBMSForecasterLessonManager getForecasterLessonManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterAnswerManager</code>.
     *
     * @return A
     * <code>DBMSForecasterAnswerManager</code> object.
     */
    public DBMSForecasterAnswerManager getForecasterAnswerManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterAttemptManager</code>.
     *
     * @return A
     * <code>DBMSForecasterAttemptManager</code> object.
     */
    public DBMSForecasterAttemptManager getForecasterAttemptManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterHintManager</code>.
     *
     * @return A
     * <code>DBMSForecasterHintManager</code> object.
     */
    public DBMSForecasterHintManager getForecasterHintManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterQuestionManager</code>.
     *
     * @return A
     * <code>DBMSForecasterQuestionManager</code> object.
     */
    public DBMSForecasterQuestionManager getForecasterQuestionManager();
    
    /**
     * Returns an instance
     * <code>DBMSForecasterQuestionTemplateManager</code>.
     *
     * @return A
     * <code>DBMSForecasterQuestionTemplateManager</code> object.
     */
    public DBMSForecasterQuestionTemplateManager getForecasterQuestionTemplateManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterResponseManager</code>.
     *
     * @return A
     * <code>DBMSForecasterResponseManager</code> object.
     */
    public DBMSForecasterResponseManager getForecasterResponseManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterScorseManager</code>.
     *
     * @return A
     * <code>DBMSForecasterScoreManager</code> object.
     */
    public DBMSForecasterScoreManager getForecasterScoreManager();
    
    /**
     * Returns an instance of
     * <code>DBMSForecasterStationManager</code>.
     *
     * @return A
     * <code>DBMSForecasterStationManager</code> object.
     */
    public DBMSForecasterStationDataManager getForecasterStationDataManager();
    
    /**
     * Returns an instance of
     * <code>DBMSInstructorResponseManager</code>.
     *
     * @return A
     * <code>DBMSInstructorResponseManager</code> object.
     */
    public DBMSInstructorResponseManager getInstructorResponseManager();
    
    /**
     * Returns an instance of
     * <code>DBMSStationManager</code>.
     *
     * @return A
     * <code>DBMSStationManager</code> object.
     */
    public DBMSStationManager getStationManager();
    
    /**
     * Returns an instance of
     * <code>DBMSPropertyManager</code>.
     *
     * @return A
     * <code>DBMSPropertyManager</code> object.
     */
    public DBMSPropertyManager getPropertyManager();
    
    /**
     * Returns an instance of
     * <code>DBMSMissingDataRecordManager</code>.
     *
     * @return A
     * <code>DBMSMissingDataRecordManager</code> object.
     */
    public DBMSMissingDataRecordManager getMissingDataRecordManager();
    
    /**
     * Returns an instance of
     * <code>DBMSVersionManager</code>.
     *
     * @return A
     * <code>DBMSVersionManager</code> object.
     */
    public DBMSVersionManager getVersionManager();
    
    /**
     * Returns an instance of
     * <code>DBMSDiaryManager</code>.
     *
     * @return A
     * <code>DBMSDiaryManager</code> object.
     */
    public DBMSDiaryManager getDiaryManager();
    
    /**
     * Closes the connections to to institutional database and the Bloomsburg 
     * University database.  
     */
    public void closeDatabaseConnections();
    
    /**
     * Checks if the connection to the Bloomsburg database is established.
     * 
     * @return False if the connection is null; True otherwise.
     */
    public boolean isBUConnectionOpen();
}