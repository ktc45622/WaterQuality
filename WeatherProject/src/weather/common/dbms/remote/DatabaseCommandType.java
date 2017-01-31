
package weather.common.dbms.remote;

import java.io.Serializable;

/**
 * An <code>enumeration</code> of the methods that use SQL. Used at the remote 
 * user, in place of the SQL statements, to be sent to the server to call the 
 * correct method. Will be used in conjunction with an <code>Object</code> that 
 * will contain the values to be used in the <code>PreparedStatements</code> in 
 * the methods.
 * @author Brian Zaiser
 */
public enum DatabaseCommandType implements java.io.Serializable  {
    
    Server_IsRunning ("Server", "isRunning"),
    // This Section is for BookmarkCategory Commands 
    BookmarkCategories_SearchByName("BookmarkCategories", "searchByName"),
    BookmarkCategories_SearchByNumber("BookmarkCategories", "searchByBookmarkCategoryNumber"),
        //Overloaded method, correct version determined by arguments
    BookmarkCategories_ObtainAll("BookmarkCategories", "obtainAll"),
    BookmarkCategories_ObtainAllByDuration("BookmarkCategories", "obtainAllByDuration"),
    BookmarkCategories_ObtainAllByRights("BookmarkCategories", "obtainAllByViewRights"),
    BookmarkCategories_ObtainAllByUser("BookmarkCategories", "obtainAllByUser"),
    BookmarkCategories_Update("BookmarkCategories", "update"),
    BookmarkCategories_Add("BookmarkCategories", "add"),
        //Overloaded method, correct version determined by arguments
    //BookmarkCategories_RemoveOne("BookmarkCategories", "removeOne"),
    BookmarkCategories_RemoveOneByCategory("BookmarkCategories", "removeOneByCategory"),
    BookmarkCategories_RemoveManyByUser("BookmarkCategories", "removeManyByUser"),
    BookmarkCategories_Get("BookmarkCategories", "get"),
    
    //This Section is for BookmarkEventType Commands
    BookmarkEventTypes_SearchByName("BookmarkEventTypes","searchByName"),
    BookmarkEventTypes_SearchByNumber("BookmarkEventTypes", "searchByBookmarkTypeNumber"),
        //Overloaded method, correct version determined by arguments
    BookmarkEventTypes_ObtainAll("BookmarkEventTypes", "obtainAll"),
    BookmarkEventTypes_ObtainAllByCategoryNumber("BookmarkEventTypes", "obtainAllByCategoryNumber"),
    BookmarkEventTypes_ObtainAllByRights("BookmarkEventTypes", "obtainAllByViewRights"),
    BookmarkEventTypes_ObtainAllByUser("BookmarkEventTypes", "obtainAllbyUserID"),
    BookmarkEventTypes_Update("BookmarkEventTypes", "update"),
    BookmarkEventTypes_Add("BookmarkEventTypes", "add"),
        //Overloaded method, correct version determined by arguments
    BookmarkEventTypes_RemoveOneByType("BookmarkEventTypes", "removeOneByType"),
    BookmarkEventTypes_RemoveMany("BookmarkEventTypes", "removeMany"),
    
    //This Section is for BookmarkInstance Commands
    BookmarkInstance_SearchByAccessRights("BookmarkInstance", "searchByAccessRights"),
    BookmarkInstance_SearchByBookmarkNumber("BookmarkInstance", "searchByBookmarkNumber"),
    BookmarkInstance_SearchByCategoryNumber("BookmarkInstance", "searchByCategoryNumber"),
    BookmarkInstance_SearchByCreatedBy("BookmarkInstance", "searchByCreatedBy"),
    BookmarkInstance_SearchByRank("BookmarkInstance", "searchByRank"),
    BookmarkInstance_SearchByTypeNumber("BookmarkInstance", "searchByTypeNumber"),
    BookmarkInstance_ObtainAll("BookmarkInstance", "obtainAll"),
    BookmarkInstance_Update("BookmarkInstance", "update"),
    BookmarkInstance_Add("BookmarkInstance", "add"),
    BookmarkInstance_RemoveOne("BookmarkInstance", "removeOne"),
    BookmarkInstance_RemoveManyByCategoryNumber("BookmarkInstance", "removeManyByCategoryNumber"),
    BookmarkInstance_RemoveManyByRanking("BookmarkInstance", "removeManyByRanking"),
    BookmarkInstance_RemoveManyByTypeNumber("BookmarkInstance", "removeManyByTypeNumber"),
    BookmarkInstance_RemoveManyByUserNumber("BookmarkInstance", "removeManyByUserNumer"),
    BookmarkInstance_SearchAllViewableByDate("BookmarkInstance", "searchAllBookmarksViewableByUserWithinTimeRange"),
    BookmarkInstance_SearchAllViewableByUser("BookmarkInstance", "searchAllBookmarksViewableByUser"),
    BookmarkInstance_SearchByCategoryNumberForUser("BookmarkInstance", "searchByCategoryNumberForUser"),
    BookmarkInstance_SearchByTypeNumberForUser("BookmarkInstance", "searchByTypeNumberForUser"),
    
    //This Section is for BookmarkInstanceType Commands
    BookmarkInstanceType_SearchByName("BookmarkInstanceType", "searchByName"),
    BookmarkInstanceType_SerachByBookmarkTypeNumber("BookmarkInstanceType", "serachByBookamrkTypeNumber"),
        //Overloaded method, correct version determined by arguments
    BookmarkInstanceType_ObtainAll("BookmarkInstanceType", "obtainAll"),
    BookmarkInstanceType_ObtainAllByCategoryNumber("BookmarkInstanceType", "obtainAllByNumber"),
    BookmarkInstanceType_ObtainAllByRights("BookmarkInstanceType", "obtainAllByRights"),
    BookmarkInstanceType_ObtainAllByUser("BookmarkInstanceType", "obtainAllbyUserID"),
    BookmarkInstanceType_Update("BookmarkInstanceType", "update"),
    BookmarkInstanceType_Add("BookmarkInstanceType", "add"),
        //Overloaded method, correct version determined by arguments
    //BookmarkInstanceType_RemoveOne("BookmarkInstanceType", "removeOne"),
    BookmarkInstanceType_RemoveOneByName("BookmarkInstanceType", "removeOneByName"),
    BookmarkInstanceType_RemoveOneByType("BookmarkInstanceType", "removeOneByType"),
    BookmarkInstanceType_RemoveMany("BookmarkInstanceType", "removeMany"),
    
    //This Section is for Course Commands
    Course_ObtainCourse("Course", "obtainCourse"),
        //Overloaded method, correct version determined by arguments
    Course_ObtainAllCourses("Course", "obtainAllCourses"),
    Course_ObtainAllCoursesByUser("Course", "obtainAllCoursesByUser"),
    Course_ObtainInactiveCourses("Course", "obtainInactiveCourses"),
    Course_Update("Course", "updateCourse"),
    Course_Insert("Course", "insertCourse"),
    Course_RemoveCourse("Course", "removeCourse"),
    Course_RemoveCourseByDate("Course", "removeCoursesBeforeDate"),
    Course_GetMetadata("Course", "getMetaData"),
    
    //This Section is for DailyDiaryWebLink commands
    DailyDiary_Get("DailyDiary", "getLinks"),
    DailyDiary_Update("DailyDiary", "updateLink"),
    DailyDiary_Add("DailyDiary", "addLink"),//commented out in manager
    DailyDiary_Delete("DailyDiary", "deleteLink"),//commented out in manager
    
    //This Section is for Enrollment commands
    Enrollment_GetCoursesForStudent("Enrollment", "getCoursesForStudent"),
    Enrollment_GetCoursesForInstructor("Enrollment", "getCoursesForInstructor"),
    Enrollment_GetStudentsInCourse("Enrollment", "getStudentsInCourse"),
    Enrollment_InsertStudentInCourse("Enrollment", "insertStudentIntoCourse"),
    Enrollment_RemoveStudentFromCourse("Enrollment", "removeStudentFromCourse"),
    Enrollment_RemoveAllStudentsFromCourse("Enrollment", "removeAllStudentsFromCourse"),
    Enrollment_DeleteStudentsNotEnrolled("Enrollment", "deleteStudentsEnrolledInNoCourses"),
    
    //This Section is for Event Commands
    Event_GetBookmarkCategoryByName("Event", "getBookmarkCategoryByName"),
    Event_GetBookmarkCategoryByNumber("Event", "getBookmarkCategoryByNumber"),
    Event_GetAllBookmarkCategories("Event", "getAllBookmarkCategories"),
    Event_Update("Event", "updateBookmarkCategory"),
    Event_Add("Event", "addBookmarkCategory"),
    Event_Delete("Event", "deleteBookmarkCategory"),
    
    //This Section is for File commands
    File_GetFile("File", "getFile"),
    File_GetFilesForBookmark("File", "getFilesForBookmark"),
    File_GetFilesForLesson("File", "getFilesForLesson"),
    File_GetAllFilesForInstructor("File", "gettAllFilesForInstructor"),
    File_GetPrivateFiles("File", "getPrivateFiles"),
    File_SetStorage("File", "setStorageSystem"),
    File_StoreFile("File", "storeFile"),
    File_RemoveFile("File", "removeFile"),
    
    //This Section is for Lesson commands
    Lesson_Get("Lesson", "get"),
    Lesson_ObtainAll("Lesson", "obtainAll"),
    Lesson_ObtainByAccessRights("Lesson", "obtainByAccessRights"),
    Lesson_ObtainByCategory("Lesson", "obtainByCategory"),
    Lesson_ObtainByUser("Lesson", "obtainByUser"),
    Lesson_Update("Lesson", "update"),
    Lesson_Add("Lesson", "add"),
    Lesson_Delete("Lesson", "delete"),
    
    //This Section is for LessonCategory commands
    LessonCategory_Get("LessonCategory", "get"),
    LessonCategory_ObtainAll("LessonCategory", "obtainAll"),
    LessonCategory_ObtainViewableBy("LessonCategory", "obtainViewableBy"),
    LessonCategory_Update("LessonCategory", "update"),
    LessonCategory_Add("LessonCategory", "add"),
    LessonCategory_Delete("LessonCategory", "delete"),
    
    //This Section is for LessonEntry commands
    LessonEntry_Get("LessonEntry", "get"),
    LessonEntry_ObtainAll("LessonEntry", "obtainAll"),
    LessonEntry_ObtainByAccessRights("LessonEntry", "obtainByAccessRights"),
    LessonEntry_ObtainByCategory("LessonEntry", "obtainByCategory"),
    LessonEntry_ObtainByLessonNumber("LessonEntry", "obtainByLessonNumber"),
    LessonEntry_ObtainByUser("LessonEntry", "obtainByUser"),
    LessonEntry_Update("LessonEntry", "update"),
    LessonEntry_Add("LessonEntry", "add"),
    LessonEntry_Delete("LessonEntry", "delete"),
    
    //This Section is for Note commands
    Note_GetNote("Note", "getNote"),
    Note_GetAllNotes("Note", "getAllNotes"),
    Note_GetNotesByInstructor("Note", "getNotesByInstructor"),
    Note_GetAllNotesForInstructor("Note", "getAllNotesForInstructor"),
    Note_GetNotesForStudent("Note", "getNotesForStudent"),
    Note_GetNotesForTimespan("Note", "getNotesForTimespan"),
    Note_GetNotesForUser("Note", "getNotesForUser"),
    Note_GetAllFilesForNote("Note", "getAllFilesForNote"),
    Note_GetNoteFilesForInstructor("Note", "getNoteFilesForInstructor"),
    Note_UpdateNote("Note", "updateNote"),
    Note_UpdateNotesFile("Note", "updateNotesFile"),
    Note_InsertNote("Note", "insertNote"),
    Note_InsertNotesFile("Note", "insertNotesFile"),
    Note_RemoveNote("Note", "removeNote"),
    Note_DeleteNotesFile("Note", "deleteNotesFile"),
    
    //This Section is for Resource commands
    Resource_GetResourceList("Resource", "getResourceList"),
    Resource_GetWeatherResourceByNumber("Resource", "getWeatherResourceByNumber"),
    Resource_GetDefaultDaytimePicture("Resource", "getDefaultDaytimePicture"),
    Resource_GetDefaultGenericNoDataImage("Resource", "getDefaultGenericNoDataImage"),
    Resource_GetDefaultNighttimePicture("Resource", "getDefaultNighttimePicture"),
    Resource_UpdateWeatherResource("Resource", "updateWeatherResource"),
    Resource_SetDefaultDaytimePicture("Resource", "setDefaultDaytimePicture"),
    Resource_SetDefaultGenericNoDataImage("Resource", "setDefaultGenericNoDataImage"),
    Resource_SetDefaultNighttimePicture("Resource", "setDefaultNighttimePicture"),
    Resource_AddResourceChangeListener("Resource", "addResourceChangeListener"),
        //Overloaded method, correct version determined by arguments
    //Resource_RemoveResource("Resource", "removeResource"),
    Resource_RemoveResourceByNumber("Resource", "removeResourceByNumber"),
    Resource_RemoveResourceByResource("Resource", "removeResourceByResource"),
    Resource_RemoveResourceChangeListener("Resource", "removeResourceChangeListener"),
    
    //This Section is for ResourceRelation commands
    ResourceRelation_GetRelatedStationResource("ResourceRelation", "getRelatedStationRresource"),
    ResourceRelation_SetResourcerelation("ResourceRelation", "setResourceRelation"),
    ResourceRelation_RemoveResourceRelation("ResourceRelation", "removeResourceRelation"),
    
    //This Section is for WeatherStationSearchData commands
    WeatherStationSearchData_GetData("WeatherStationSearchData", "getData"),
    WeatherStationSearchData_GetAllValues("WeatherStationSearchData", "getAllValues"),
    WeatherStationSearchData_InsertData("WeatherStationSearchData", "insertData"),
    
    //This Section is for WebLink commands
    WebLink_GetLinksForCategory("WebLink", "getLinksForCategory"),
    WebLink_ObtainAllWebLinkCategories("WebLink", "obtainAllWebLinkCategories"),
    WebLink_ObtainAllWebLinks("WebLink", "obtainAllWebLinks"),
    WebLink_ObtainAllWebLinksFromACategory("WebLink", "obtainAllWebLinksFromACategory"),
    WebLink_UpdateWebLink("WebLink", "updateWebLink"),
    WebLink_UpdateWebLinkCategory("WebLink", "updateWebLinkCategory"),
    WebLink_AddLinkForCategory("WebLink", "addLinkForCategory"),
    WebLink_AddWebLinkCategory("WebLink", "addWebLinkCategory"),
    WebLink_DeleteLink("WebLink", "deleteLink"),
    WebLink_RemoveLinkCategory("WebLink", "removeLinkCategory"),
    
    //This Section is for User commands
    User_GetEnrolledStudentByID("User", "getEnrolledStudentByID"),
        //Overloaded method, correct version determined by arguments
    //User_ObtainUser("User", "obtainUser"),
    User_ObtainUserByLoginID("User", "obtainUserByLoginID"),
    User_ObtainUserByName("User", "obtainUserByNmae"),
    User_ObtainUserByUserID("User", "obtainUserByUserID"),
    User_ObtainUserEmail("User", "obtainUserEmail"),
    User_ObtainAllUsers("User", "obtainAllUsers"),
    User_ObtainUsersByEmail("User", "obtainUsersByEmail"),
    User_ObtainUsersByExactLoginID("User", "obtainUsersByExactLoginID"),
    User_ObtainUsersByFirstName("User", "obtainUsersByFirstName"),
    User_ObtainUsersByLastName("User", "obtainUsersByLastName"),
    User_ObtainUsersByLoginID("User", "obtainUsersByLoginID"),
    User_ObtainAllAdministrators("User", "obtainAllAdministrators"),
    User_ObtainAllInstructors("User", "obtainAllInstructors"),
    User_ObtainAllInstructorsAndAdministrators("User", "obtainAllInstructorsAndAdministrators"),
    User_ObtainAllEnrolledStudents("User", "obtainAllEnrolledStudents"),
    User_ObtainAllStudentsInCourse("User", "obtainAllStudentsInCourse"),
    User_ObtainInactiveStudents("User", "obtainInactiveStudents"),
    User_UpdateLoginDateAndNumberOfLogins("User", "updateLoginDateAndNumberOfLogins"),
    User_UpdatePassword("User", "updatePassword"),
    User_UpdateUser("User", "updateUser"),
    User_AddUser("User", "addUser"),
    User_RemoveStudentsBeforeDate("User", "removeStudentsBeforeDate"),
        //Overloaded method, correct version determined by arguments
    //User_RemoveUser("User", "removeUser"),
    User_RemoveUserByLoginID("User", "removeUserByLoginID"),
    User_RemoveUserByUserID("User", "removeUserByUserID"),
    
    //For completeness? This Section is for ResourceChangeListener commands
    ResourceChangeListener_UpdateResources("ResourceChangeListener", "updateResources"),
    ResourceChangeListener_AddResources("ResourceChangeListener", "addResources"),
    ResourceChangeListener_RemoveResources("ResourceChangeListener", "removeResources"),
        
    //This Section is for SystemManager commands
    SystemManager_CreateTables("SystemManager", "createTables"),
    SystemManager_InsertDefaultUsers("SystemManager", "insertDefaultUsers"),
    ;
    
    private static final long serialVersionUID = 1L;
    private String commandType; 
    private String commandName;
    
    /**
     * Explicit Constructor - uses the values of the enum, or passed values.
     * @param commandType the manager type
     * @param comamndName the method name in the corresponding manager
     */
    DatabaseCommandType(String commandType, String comamndName) {
        this.commandType = commandType; 
        this.commandName = comamndName;
    }
    
    /**
     * Returns the type of the manager.
     * @return the commandType
     */
    public String getCommandType(){
        return this.commandType;
    }
    
    /**
     * Returns the method name.
     * @return the commandName
     */
    public String getCommandName(){
        return this.commandName;
    }
    
    
    /**
     * Returns a String representation of this DatabaseCommandType.
     * @return String representation of this DatabaseCommandType.
     */
    @Override
    public String toString() {
        return this.commandName + "," + this.commandType;
    }
    
    /**
     * Returns an enumerated constant based on a String command name and a 
     * String command type.
     * If the command name does not represent a defined enumerated value, 
     * it returns null.
     * 
     * @param commandType Type of the command.
     * @param commandName Name of the command.
     * @return An enumerated constant matching the command name or null if none was found.
     */
    public static DatabaseCommandType fromString(String commandType, String commandName) {
        
        for (DatabaseCommandType t : DatabaseCommandType.values()) {
            if (t.commandType.equals(commandType)&& t.commandName.equals(commandName) )
                return t;
        }
        
        return null;
    }
}
