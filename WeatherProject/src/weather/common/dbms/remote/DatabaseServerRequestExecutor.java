/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.common.dbms.remote;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Vector;
import weather.StorageControlSystem;
import weather.common.data.AccessRights;
import weather.common.data.Course;
import weather.common.data.Instructor;
import weather.common.data.InstructorFileInstance;
import weather.common.data.User;
import weather.common.data.WebLink;
import weather.common.data.WebLinkCategories;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.BookmarkRank;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.data.diary.DailyDiaryWebLinks;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonCategory;
import weather.common.data.lesson.LessonEntry;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.NoteFileInstance;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.data.weatherstation.WeatherStationDailyAverage;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSDailyDiaryWebLinkManager;
import weather.common.dbms.DBMSEnrollmentManager;
import weather.common.dbms.DBMSEventManager;
import weather.common.dbms.DBMSFileManager;
import weather.common.dbms.DBMSLessonCategoryManager;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.dbms.DBMSLessonManager;
import weather.common.dbms.DBMSNoteManager;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.DBMSResourceRelationManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.dbms.DBMSWeatherStationSearchDataManager;
import weather.common.dbms.DBMSWebLinkManager;
import weather.common.dbms.ResourceChangeListener;
import weather.common.utilities.WeatherException;

/**
 * Helper class for executing remote database server commands.
 *
 * @author Patrick Brinich
 */
public class DatabaseServerRequestExecutor {

    private DatabaseCommandType command;
    private ArrayList<Object> args;
    private DBMSSystemManager dbms;

    /**
     * Creates a new DatabaseServerRequestExecutor
     *
     * @param command the command to be executed
     * @param dbms the DBMSSystemManager used. Preferably the MSQLImpl
     */
    public DatabaseServerRequestExecutor(RemoteDatabaseCommand command, DBMSSystemManager dbms) {
        this.command = command.getDatabaseCommand();
        this.args = command.getArguments();
        this.dbms = dbms;
    }

    /**
     * Executes this DatabaseServerRequestExecutor's command.
     *
     * @return the results of the command's execution.
     */
    public RemoteDatabaseResult execute() {
        RemoteDatabaseResult retval = null;
        try {
            retval = executeHelper();
        } catch (Exception e) {
            //TODO: Correct exception number
            WeatherException ex = new WeatherException(0, e);
            retval = bundle(ex);
        }
        return retval;
    }

    /**
     * Helper method for execute method.
     *
     * @return the results of the command's execution.
     */
    private RemoteDatabaseResult executeHelper() {
        RemoteDatabaseResult retval = null;
        DBMSBookmarkCategoriesManager bcm;
        DBMSBookmarkEventTypesManager betm;
        DBMSBookmarkInstanceManager bim;
        DBMSCourseManager cm;
        DBMSDailyDiaryWebLinkManager ddm;
        DBMSEnrollmentManager erm;
        DBMSEventManager evm;
        DBMSFileManager fm;
        DBMSLessonManager lm;
        DBMSLessonCategoryManager lcm;
        DBMSLessonEntryManager lem;
        DBMSNoteManager nm;
        DBMSResourceManager rm;
        DBMSResourceRelationManager rrm;
        DBMSWeatherStationSearchDataManager wsm;
        DBMSWebLinkManager wlm;
        DBMSUserManager um;


        switch (command) {
            case Server_IsRunning:
                retval = new RemoteDatabaseResult(
                        RemoteDatabaseResultStatus.SingleResultObjectReturned, true);
                break;
            case BookmarkCategories_SearchByName:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.searchByName((String) args.get(0)));
                break;
            case BookmarkCategories_SearchByNumber:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.searchByBookmarkCategoryNumber((int) args.get(0)));
                break;
            case BookmarkCategories_ObtainAll:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.obtainAll());
                break;
            case BookmarkCategories_ObtainAllByDuration:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.obtainAll((BookmarkDuration) args.get(0)));
                break;
            case BookmarkCategories_ObtainAllByRights:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.obtainAll((CategoryViewRights) args.get(0)));
                break;
            case BookmarkCategories_ObtainAllByUser:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.obtainAllByUser((User) args.get(0)));
                break;
            case BookmarkCategories_Update:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.update((BookmarkCategory) args.get(0)));
                break;
            case BookmarkCategories_Add:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.add((BookmarkCategory) args.get(0)));
                break;
            case BookmarkCategories_RemoveOneByCategory:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.removeOne((BookmarkCategory) args.get(0)));
                break;
            case BookmarkCategories_RemoveManyByUser:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.removeManyByUser((int) args.get(0)));
                break;
            case BookmarkCategories_Get:
                bcm = dbms.getBookmarkCategoriesManager();
                retval = bundle(bcm.get((String) args.get(0)));
                break;
//This Section is for BookmarkEventType Commands:
            case BookmarkEventTypes_SearchByName:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.searchByName((String) args.get(0), (String) args.get(1)));
                break;
            case BookmarkEventTypes_SearchByNumber:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.searchByBookmarkTypeNumber((int) args.get(0)));
                break;
            case BookmarkEventTypes_ObtainAll:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.obtainAll());
                break;
            case BookmarkEventTypes_ObtainAllByCategoryNumber:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.obtainAll((int) args.get(0)));
                break;
            case BookmarkEventTypes_ObtainAllByRights:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.obtainAll((CategoryViewRights) args.get(0)));
                break;
            case BookmarkEventTypes_ObtainAllByUser:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.obtainAllbyUserID((int) args.get(0)));
                break;
            case BookmarkEventTypes_Update:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.update((BookmarkType) args.get(0)));
                break;
            case BookmarkEventTypes_Add:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.add((BookmarkType) args.get(0)));
                break;
            case BookmarkEventTypes_RemoveOneByType:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.removeOne((BookmarkType) args.get(0)));
                break;
            case BookmarkEventTypes_RemoveMany:
                betm = dbms.getBookmarkTypesManager();
                retval = bundle(betm.removeMany((int) args.get(0)));
                break;
//This Section is for BookmarkInstance Commands:
            case BookmarkInstance_SearchByAccessRights:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.searchByAccessRights((AccessRights) args.get(0)));
                break;
            case BookmarkInstance_SearchByBookmarkNumber:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.searchByBookmarkNumber((int) args.get(0)));
                break;
            case BookmarkInstance_SearchByCategoryNumber:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.searchByCategoryNumber((int) args.get(0)));
                break;
            case BookmarkInstance_SearchByCreatedBy:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.searchByCreatedBy((int) args.get(0)));
                break;
            case BookmarkInstance_SearchByRank:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.searchByRank((BookmarkRank) args.get(0)));
                break;
            case BookmarkInstance_SearchByTypeNumber:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.searchByTypeNumber((int) args.get(0)));
                break;
            case BookmarkInstance_ObtainAll:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.obtainAll());
                break;
            case BookmarkInstance_Update:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.update((Bookmark) args.get(0)));
                break;
            case BookmarkInstance_Add:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.add((Bookmark) args.get(0)));
                break;
            case BookmarkInstance_RemoveOne:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.removeOne((Bookmark) args.get(0)));
                break;
            case BookmarkInstance_RemoveManyByCategoryNumber:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.removeManyByCategoryNumber((int) args.get(0)));
                break;
            case BookmarkInstance_RemoveManyByRanking:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.removeManyByRanking((BookmarkRank) args.get(0)));
                break;
            case BookmarkInstance_RemoveManyByTypeNumber:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.removeManyByTypeNumber((int) args.get(0)));
                break;
            case BookmarkInstance_RemoveManyByUserNumber:
                bim = dbms.getBookmarkManager();
                retval = bundle(bim.removeManyByUserNumber((int) args.get(0)));
                break;
            case BookmarkInstance_SearchAllViewableByDate:
                bim = dbms.getBookmarkManager();
                retval = bundle(
                        bim.searchAllBookmarksViewableByUserWithinTimeRange(
                        (User) args.get(0), (Date) args.get(1),
                        (Date) args.get(2), (Vector<User>) args.get(3)));
                break;
            case BookmarkInstance_SearchAllViewableByUser:
                bim = dbms.getBookmarkManager();
                retval = bundle(
                        bim.searchAllBookmarksViewableByUser(
                        (User) args.get(0), (Vector<User>) args.get(1)));
                break;
            case BookmarkInstance_SearchByCategoryNumberForUser:
                bim = dbms.getBookmarkManager();
                retval = bundle(
                        bim.searchByCategoryNumberForUser(
                        (User) args.get(0), (int) args.get(1),
                        (Vector<User>) args.get(2)));
                break;
            case BookmarkInstance_SearchByTypeNumberForUser:
                bim = dbms.getBookmarkManager();
                retval = bundle(
                        bim.searchByTypeNumberForUser(
                        (User) args.get(0), (int) args.get(1),
                        (Vector<User>) args.get(2)));
                break;
//This Section is for BookmarkInstanceType Commands:
            case BookmarkInstanceType_SearchByName:
            case BookmarkInstanceType_SerachByBookmarkTypeNumber:
            case BookmarkInstanceType_ObtainAll:
            case BookmarkInstanceType_ObtainAllByCategoryNumber:
            case BookmarkInstanceType_ObtainAllByRights:
            case BookmarkInstanceType_ObtainAllByUser:
            case BookmarkInstanceType_Update:
            case BookmarkInstanceType_Add:
            case BookmarkInstanceType_RemoveOneByName:
            case BookmarkInstanceType_RemoveOneByType:
            case BookmarkInstanceType_RemoveMany:
                retval = bundleUnsupportedException();
                break;
//This Section is for Course Commands:
            case Course_ObtainCourse:
                cm = dbms.getCourseManager();
                retval = bundle(cm.obtainCourse((String) args.get(0), (int) args.get(1), (String) args.get(2)));
                break;
            case Course_ObtainAllCourses:
                cm = dbms.getCourseManager();
                retval = bundle(cm.obtainAllCourses());
                break;
            case Course_ObtainAllCoursesByUser:
                cm = dbms.getCourseManager();
                retval = bundle(cm.obtainAllCoursesTaughyByUser((User) args.get(0)));
                break;
            case Course_ObtainInactiveCourses:
                cm = dbms.getCourseManager();
                retval = bundle(cm.obtainInactiveCourses((Date) args.get(0)));
                break;
            case Course_Update:
                cm = dbms.getCourseManager();
                retval = bundle(cm.updateCourse((Course) args.get(0)));
                break;
            case Course_Insert:
                cm = dbms.getCourseManager();
                retval = bundle(cm.insertCourse((Course) args.get(0)));
                break;
            case Course_RemoveCourse:
                cm = dbms.getCourseManager();
                retval = bundle(cm.removeCourse((Course) args.get(0)));
                break;
            case Course_RemoveCourseByDate:
                cm = dbms.getCourseManager();
                retval = bundle(cm.removeCoursesBeforeDate((Date) args.get(0)));
                break;
            case Course_GetMetadata:
                cm = dbms.getCourseManager();
                retval = bundle(cm.getMetaData());
                break;
//This Section is for DailyDiaryWebLink commands:
            case DailyDiary_Get:
                ddm = dbms.getDailyDiaryWebLinkManager();
                retval = bundle(ddm.getLinks());
                break;
            case DailyDiary_Update:
                ddm = dbms.getDailyDiaryWebLinkManager();
                retval = bundle(ddm.updateLink(((DailyDiaryWebLinks) args.get(0))));
                break;
            case DailyDiary_Add:
            case DailyDiary_Delete:
                retval = bundleUnsupportedException();
                break;
//This Section is for Enrollment commands:
            case Enrollment_GetCoursesForStudent:
                erm = dbms.getEnrollmentManager();
                retval = bundle(erm.getCoursesForStudent((User) args.get(0)));
                break;
            case Enrollment_GetCoursesForInstructor:
                erm = dbms.getEnrollmentManager();
                retval = bundle(erm.getCoursesForInstructor((User) args.get(0)));
                break;
            case Enrollment_GetStudentsInCourse:
                erm = dbms.getEnrollmentManager();
                retval = bundle(erm.getStudentsInCourse((Course) args.get(0)));
                break;
            case Enrollment_InsertStudentInCourse:
                erm = dbms.getEnrollmentManager();
                retval = bundle(erm.getStudentsInCourse((Course) args.get(0)));
                break;
            case Enrollment_RemoveStudentFromCourse:
                erm = dbms.getEnrollmentManager();
                retval = bundle(erm.removeStudentFromCourse((User) args.get(0), (Course) args.get(1)));
                break;
            case Enrollment_RemoveAllStudentsFromCourse:
                erm = dbms.getEnrollmentManager();
                erm.removeAllStudentsFromCourse((Course) args.get(0));
                retval = bundle(true);
                break;
            case Enrollment_DeleteStudentsNotEnrolled:
                erm = dbms.getEnrollmentManager();
                retval = bundle(erm.deleteStudentsEnrolledInNoCourses());
                break;
//This Section is for Event Commands:
            case Event_GetBookmarkCategoryByName:
                evm = dbms.getEventManager();
                retval = bundle(evm.getBookmarkCategoryByName((String) args.get(0)));
                break;
            case Event_GetBookmarkCategoryByNumber:
                evm = dbms.getEventManager();
                retval = bundle(evm.getBookmarkCategoryByNumber((int) args.get(0)));
                break;
            case Event_GetAllBookmarkCategories:
                evm = dbms.getEventManager();
                retval = bundle(evm.getAllBookmarkCategories());
                break;
            case Event_Update:
                evm = dbms.getEventManager();
                retval = bundle(evm.updateBookmarkCategory((BookmarkCategory) args.get(0)));
                break;
            case Event_Add:
                evm = dbms.getEventManager();
                retval = bundle(evm.addBookmarkCategory((BookmarkCategory) args.get(0)));
                break;
            case Event_Delete:
                evm = dbms.getEventManager();
                retval = bundle(evm.deleteBookmarkCategory((BookmarkCategory) args.get(0)));
                break;
//This Section is for File commands:
            case File_GetFile:
                fm = dbms.getFileManager();
                retval = bundle(fm.getFile((int) args.get(0)));
                break;
            case File_GetFilesForBookmark:
                fm = dbms.getFileManager();
                retval = bundle(fm.getAllFilesForBookmark((Bookmark) args.get(0)));
                break;
            case File_GetFilesForLesson:
                fm = dbms.getFileManager();
                retval = bundle(fm.getAllFilesForLesson((Lesson) args.get(0)));
                break;
            case File_GetAllFilesForInstructor:
                fm = dbms.getFileManager();
                retval = bundle(fm.getAllFilesForInstructor((int) args.get(0)));
                break;
            case File_GetPrivateFiles:
                fm = dbms.getFileManager();
                retval = bundle(fm.getAllPrivateFilesForInstructor((int) args.get(0)));
                break;
            case File_SetStorage:
                fm = dbms.getFileManager();
                fm.setStorageSystem((StorageControlSystem) args.get(0));
                retval = bundle(true);
                break;
            case File_StoreFile:
                fm = dbms.getFileManager();
                retval = bundle(fm.storePrivateFile((Instructor) args.get(0), 
                        (String) args.get(1), (byte[]) args.get(2)));
                break;
            case File_RemoveFile:
                fm = dbms.getFileManager();
                retval = bundle(fm.removeFile((int) args.get(0)));
                break;
//This Section is for Lesson commands:
            case Lesson_Get:
                lm = dbms.getLessonManager();
                retval = bundle(lm.get((int) args.get(0)));
                break;
            case Lesson_ObtainAll:
                lm = dbms.getLessonManager();
                retval = bundle(lm.obtainAll());
                break;
            case Lesson_ObtainByAccessRights:
                lm = dbms.getLessonManager();
                retval = bundle(lm.get((int) args.get(0)));
                break;
            case Lesson_ObtainByCategory:
                lm = dbms.getLessonManager();
                retval = bundle(lm.obtainByCategory((int) args.get(0)));
                break;
            case Lesson_ObtainByUser:
                lm = dbms.getLessonManager();
                retval = bundle(lm.obtainByUser((int) args.get(0)));
                break;
            case Lesson_Update:
                lm = dbms.getLessonManager();
                retval = bundle(lm.update((Lesson) args.get(0)));
                break;
            case Lesson_Add:
                lm = dbms.getLessonManager();
                retval = bundle(lm.add((Lesson) args.get(0)));
                break;
            case Lesson_Delete:
                lm = dbms.getLessonManager();
                retval = bundle(lm.delete((Lesson) args.get(0)));
                break;
//This Section is for LessonCategory commands:
            case LessonCategory_Get:
                lcm = dbms.getLessonCategoryManager();
                retval = bundle(lcm.get((int) args.get(0)));
                break;
            case LessonCategory_ObtainAll:
                lcm = dbms.getLessonCategoryManager();
                retval = bundle(lcm.obtainAll());
                break;
            case LessonCategory_ObtainViewableBy:
                lcm = dbms.getLessonCategoryManager();
                retval = bundle(lcm.obtainViewableBy((int) args.get(0)));
                break;
            case LessonCategory_Update:
                lcm = dbms.getLessonCategoryManager();
                retval = bundle(lcm.update((LessonCategory) args.get(0)));
                break;
            case LessonCategory_Add:
                lcm = dbms.getLessonCategoryManager();
                retval = bundle(lcm.add((LessonCategory) args.get(0)));
                break;
            case LessonCategory_Delete:
                lcm = dbms.getLessonCategoryManager();
                retval = bundle(lcm.delete((LessonCategory) args.get(0)));
                break;
//This Section is for LessonEntry commands:
            case LessonEntry_Get:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.get((int) args.get(0)));
                break;
            case LessonEntry_ObtainAll:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.obtainAll());
                break;
            case LessonEntry_ObtainByAccessRights:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.obtainByAccessRights((AccessRights) args.get(0)));
                break;
            case LessonEntry_ObtainByCategory:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.obtainByCategory((int) args.get(0)));
                break;
            case LessonEntry_ObtainByLessonNumber:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.obtainByLessonNumber((int) args.get(0)));
                break;
            case LessonEntry_ObtainByUser:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.obtainByUser((int) args.get(0)));
                break;
            case LessonEntry_Update:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.update((LessonEntry) args.get(0)));
                break;
            case LessonEntry_Add:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.add((LessonEntry) args.get(0)));
                break;
            case LessonEntry_Delete:
                lem = dbms.getLessonEntryManager();
                retval = bundle(lem.delete((LessonEntry) args.get(0)));
                break;
//This Section is for Note commands:
            case Note_GetNote:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getNote((int) args.get(0)));
                break;
            case Note_GetAllNotes:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getAllNotes());
                break;
            case Note_GetNotesByInstructor:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getNotesByInstructor((User) args.get(0)));
                break;
            case Note_GetAllNotesForInstructor:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getAllNotesVisibleToInstructor((User) args.get(0)));
                break;
            case Note_GetNotesForStudent:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getNotesVisibleToStudent((User) args.get(0), (Date) args.get(1)));
                break;
            case Note_GetNotesForTimespan:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getNotesForTimespan((Date) args.get(0)));
                break;
            case Note_GetNotesForUser:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getNotesVisibleToUser((User) args.get(0), (Date) args.get(1)));
                break;
            case Note_GetAllFilesForNote:
                fm = dbms.getFileManager();
                retval = bundle(fm.getAllFilesForNote((InstructorNote) args.get(0)));
                break;
            case Note_GetNoteFilesForInstructor:
                nm = dbms.getNoteManager();
                retval = bundle(nm.getAllNotesVisibleToInstructor((User) args.get(0)));
                break;
            case Note_UpdateNote:
                nm = dbms.getNoteManager();
                retval = bundle(nm.updateNote((InstructorNote) args.get(0)));
                break;
            case Note_UpdateNotesFile:
                fm = dbms.getFileManager();
                retval = bundle(fm.updateNotesFile((NoteFileInstance) args.get(0)));
                break;
            case Note_InsertNote:
                nm = dbms.getNoteManager();
                retval = bundle(nm.insertNote((InstructorNote) args.get(0)));
                break;
            case Note_InsertNotesFile:
                fm = dbms.getFileManager();
                retval = bundle(fm.insertNotesFile((NoteFileInstance) args.get(0)));
                break;
            case Note_RemoveNote:
                nm = dbms.getNoteManager();
                retval = bundle(nm.removeNote((Instructor) args.get(0), (InstructorNote) args.get(1)));
                break;
            case Note_DeleteNotesFile:
                fm = dbms.getFileManager();
                retval = bundle(fm.deleteNotesFile((NoteFileInstance) args.get(0)));
                break;
//This Section is for Resource commands:
            case Resource_GetResourceList:
                rm = dbms.getResourceManager();
                retval = bundle(rm.getResourceList());
                break;
            case Resource_GetWeatherResourceByNumber:
                rm = dbms.getResourceManager();
                retval = bundle(rm.getWeatherResourceByNumber((int) args.get(0)));
                break;
            case Resource_GetDefaultDaytimePicture:
                rm = dbms.getResourceManager();
                retval = bundle(rm.getDefaultDaytimePicture((int) args.get(0)));
                break;
            case Resource_GetDefaultGenericNoDataImage:
                rm = dbms.getResourceManager();
                retval = bundle(rm.getDefaultGenericNoDataImage());
                break;
            case Resource_GetDefaultNighttimePicture:
                rm = dbms.getResourceManager();
                retval = bundle(rm.getDefaultNighttimePicture((int) args.get(0)));
                break;
            case Resource_UpdateWeatherResource:
                rm = dbms.getResourceManager();
                retval = bundle(rm.updateWeatherResource((Resource) args.get(0)));
                break;
            case Resource_SetDefaultDaytimePicture:
                rm = dbms.getResourceManager();
                rm.setDefaultDaytimePicture((int) args.get(0),
                        (ImageInstance) args.get(1));
                retval = bundle(true);
                break;
            case Resource_SetDefaultGenericNoDataImage:
                rm = dbms.getResourceManager();
                rm.setDefaultGenericNoDataImage((ImageInstance) args.get(0));
                retval = bundle(true);
                break;
            case Resource_SetDefaultNighttimePicture:
                rm = dbms.getResourceManager();
                rm.setDefaultNighttimePicture((int) args.get(0),
                        (ImageInstance) args.get(1));
                retval = bundle(true);
                break;
            case Resource_AddResourceChangeListener:
                rm = dbms.getResourceManager();
                rm.addResourceChangeListener((ResourceChangeListener) args.get(0));
                retval = bundle(true);
                break;
            case Resource_RemoveResourceByNumber:
                rm = dbms.getResourceManager();
                retval = bundle(rm.removeResource((int) args.get(0)));
                break;
            case Resource_RemoveResourceByResource:
                rm = dbms.getResourceManager();
                retval = bundle(rm.removeResource((Resource) args.get(0)));
                break;
            case Resource_RemoveResourceChangeListener:
                rm = dbms.getResourceManager();
                rm.addResourceChangeListener((ResourceChangeListener) args.get(0));
                retval = bundle(true);
                break;
//This Section is for ResourceRelation commands:
            case ResourceRelation_GetRelatedStationResource:
                rrm = dbms.getResourceRelationManager();
                retval = bundle(rrm.getRelatedStationResource((Resource) args.get(0)));
                break;
            case ResourceRelation_SetResourcerelation:
                rrm = dbms.getResourceRelationManager();
                rrm.setResourceRelation((Resource) args.get(0), (Resource) args.get(1));
                retval = bundle(true);
                break;
            case ResourceRelation_RemoveResourceRelation:
                rrm = dbms.getResourceRelationManager();
                rrm.removeResourceRelation((Resource) args.get(0));
                retval = bundle(true);
                break;
//This Section is for WeatherStationSearchData commands:
            case WeatherStationSearchData_GetData:
                wsm = dbms.getWeatherStationSearchDataManager();
                bundle(wsm.getData((int) args.get(0), (String) args.get(1),
                        (Date) args.get(2), (Date) args.get(3)));
                break;
            case WeatherStationSearchData_GetAllValues:
                wsm = dbms.getWeatherStationSearchDataManager();
                bundle(wsm.getAllValues((int) args.get(0),
                        (Date) args.get(1), (Date) args.get(2)));
                break;
            case WeatherStationSearchData_InsertData:
                wsm = dbms.getWeatherStationSearchDataManager();
                bundle(wsm.insertData((WeatherStationDailyAverage) args.get(0)));
                retval = bundleUnsupportedException();
                break;
//This Section is for WebLink commands:
            case WebLink_GetLinksForCategory:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.getLinksForCategory((String) args.get(0)));
                break;
            case WebLink_ObtainAllWebLinkCategories:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.obtainAllWebLinkCategories());
                break;
            case WebLink_ObtainAllWebLinks:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.obtainAllWebLinks());
                break;
            case WebLink_ObtainAllWebLinksFromACategory:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.obtainWebLinksFromACategory((WebLinkCategories) args.get(0)));
                break;
            case WebLink_UpdateWebLink:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.updateWebLink((WebLink) args.get(0)));
                break;
            case WebLink_UpdateWebLinkCategory:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.updateWebLinkCategory((WebLinkCategories) args.get(0)));
                break;
            case WebLink_AddLinkForCategory:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.addLinkForCategory((String) args.get(0), (String) args.get(1), (String) args.get(2)));
                break;
            case WebLink_AddWebLinkCategory:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.addWebLinkCategory((WebLinkCategories) args.get(0)));
                break;
            case WebLink_DeleteLink:
                wlm = dbms.getWebLinkManager();
                retval = bundle(wlm.deleteLink((String) args.get(0)));
                break;
            case WebLink_RemoveLinkCategory:
//This Section is for User commands:
            case User_GetEnrolledStudentByID:
                um = dbms.getUserManager();
                retval = bundle(um.getEnrolledStudentByID((String) args.get(0)));
                break;
            case User_ObtainUserByLoginID:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUser((String) args.get(0)));
                break;
            case User_ObtainUserByName:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUser((String) args.get(0), (String) args.get(1)));
                break;
            case User_ObtainUserByUserID:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUser((int) args.get(0)));
                break;
            case User_ObtainUserEmail:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUserEmail((String) args.get(0)));
                break;
            case User_ObtainAllUsers:
                um = dbms.getUserManager();
                retval = bundle(um.obtainAllUsers());
                break;
            case User_ObtainUsersByEmail:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUsersByEmail((String) args.get(0)));
                break;
            case User_ObtainUsersByExactLoginID:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUsersByUsername((String) args.get(0)));
                break;
            case User_ObtainUsersByFirstName:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUsersWithFirstNameSubstring((String) args.get(0)));
                break;
            case User_ObtainUsersByLastName:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUsersWithLastNameSubstring((String) args.get(0)));
                break;
            case User_ObtainUsersByLoginID:
                um = dbms.getUserManager();
                retval = bundle(um.obtainUsersWithLoginIDSubstring((String) args.get(0)));
                break;
            case User_ObtainAllAdministrators:
                um = dbms.getUserManager();
                retval = bundle(um.obtainAllAdministrators());
                break;
            case User_ObtainAllInstructors:
                um = dbms.getUserManager();
                retval = bundle(um.obtainAllInstructors());
                break;
            case User_ObtainAllInstructorsAndAdministrators:
                um = dbms.getUserManager();
                retval = bundle(um.obtainAllInstructorsAndAdministrators());
                break;
            case User_ObtainAllEnrolledStudents:
                um = dbms.getUserManager();
                retval = bundle(um.obtainAllEnrolledStudents());
                break;
            case User_ObtainAllStudentsInCourse:
                um = dbms.getUserManager();
                retval = bundle(um.obtainAllStudentsInCourse((Course) args.get(0)));
                break;
            case User_ObtainInactiveStudents:
                um = dbms.getUserManager();
                retval = bundle(um.obtainInactiveStudents((Date) args.get(0)));
                break;
            case User_UpdateLoginDateAndNumberOfLogins:
                um = dbms.getUserManager();
                retval = bundle(um.updateLoginDateAndNumberOfLogins((User) args.get(0)));
                break;
            case User_UpdatePassword:
                um = dbms.getUserManager();
                retval = bundle(um.updatePassword((String) args.get(0), (String) args.get(1)));
                break;
            case User_UpdateUser:
                um = dbms.getUserManager();
                retval = bundle(um.updateUser((User) args.get(0), (int) args.get(1)));
                break;
            case User_AddUser:
                um = dbms.getUserManager();
                retval = bundle(um.addUser((User) args.get(0)));
                break;
            case User_RemoveStudentsBeforeDate:
                um = dbms.getUserManager();
                retval = bundle(um.removeStudentsBeforeDate((Date) args.get(0)));
                break;
            case User_RemoveUserByLoginID:
                um = dbms.getUserManager();
                retval = bundle(um.removeUser((String) args.get(0)));
                break;
            case User_RemoveUserByUserID:
                um = dbms.getUserManager();
                retval = bundle(um.removeUser((int) args.get(0)));
                break;
//This Section is for SystemManager commands:
            case SystemManager_CreateTables:
                try {
                    dbms.createTables();
                    retval = bundle(true);
                } catch (WeatherException ex) {
                    retval = bundle(ex);
                }
                break;
            case SystemManager_InsertDefaultUsers:
                try {
                    dbms.insertDefaultUsers();
                    retval = bundle(true);
                } catch (WeatherException ex) {
                    retval = bundle(ex);
                }
                break;
            default:
                retval = bundleUnsupportedException();
                break;
        }

        return retval;

    }

    /**
     * Bundles a Vector<T> into a RemoteDatabaseResult. Use type inference for
     * ease of use.
     *
     * @param <T> the type of object the Vector is holding
     * @param v the Vector<T> to be bundled
     * @return the RemoteDatabaseResult containing <code>v</code>
     */
    private <T> RemoteDatabaseResult bundle(Vector<T> v) {
        return new RemoteDatabaseResult(
                RemoteDatabaseResultStatus.VectorOfResultsReturned, v);
    }

    /**
     * Bundles a WeatherException into a RemoteDatabaseResult.
     *
     * @param ex the WeatherException to bundle
     * @return RemoteDatabaseResult containing the WeatherException
     */
    private RemoteDatabaseResult bundle(WeatherException ex) {
        return new RemoteDatabaseResult(
                RemoteDatabaseResultStatus.ErrorObjectReturned, null, ex);
    }

    /**
     * Bundles up a RemoteDatabaseResult depending on whether or not database
     * modification was successful.
     *
     * @param successful a flag indicating whether some database modification
     * was successful
     * @return the bundled RemoteDatabaseResult
     */
    private RemoteDatabaseResult bundle(boolean successful) {
        if (successful) {
            return new RemoteDatabaseResult(
                    RemoteDatabaseResultStatus.DatabaseModificationSuccessful, null, null);
        } else {
            //TODO: correct exception number
            WeatherException ex = new WeatherException(0);
            return bundle(ex);
        }
    }

    /**
     * Bundles an Object into a RemoteDatabaseResult
     *
     * @param o the Object to be bundled.
     * @return the RemoteDatabaseResult containing the object.
     */
    private RemoteDatabaseResult bundle(Object o) {
        return new RemoteDatabaseResult(
                RemoteDatabaseResultStatus.SingleResultObjectReturned, o);
    }

    /**
     * Creates a RemoteDatabaseResult with a WeatherException constructed with
     * an UnsupportedOperationException. This is for commands that aren't
     * supported yet.
     *
     * @return RemoteDatabaseResult containing the WeatherException
     */
    private RemoteDatabaseResult bundleUnsupportedException() {
        WeatherException ex = new WeatherException(0,
                new UnsupportedOperationException(
                "Unsupported Command"));
        return bundle(ex);
    }
}
