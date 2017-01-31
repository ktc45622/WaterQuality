package weather.common.dbms;

import java.util.Collection;
import java.util.Vector;
import weather.StorageControlSystem;
import weather.common.data.*;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkFileInstance;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonFileInstance;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.NoteFileInstance;

/**
 * This interface specifies methods related to the storing of files in the
 * database.
 * @author Shane Levengood
 */
public interface DBMSFileManager {
    
    /**
     * Sets the storage system that should be used to store the files.
     * 
     * @param system The storage system to be used for file storage.
     */
    public void setStorageSystem(StorageControlSystem system);
    
    /**
     * Stores the given file on the server as a private file for the given 
     * instructor.
     * 
     * @param instructor The Instructor that is storing the file.
     * @param fileName The name of the file to be stored.
     * @param fileData The data in the file to be stored.
     * @return True if the operation is successful, false otherwise.
     */
    public boolean storePrivateFile(Instructor instructor, String fileName,
            byte[] fileData);
    
    /**
     * Retrieves a Collection of private instructor files for the given instructor.
     * 
     * @param instructorNumber The instructorNumber of the Instructor whose files you want.
     * @return A Collection of private instructor files.
     */
    public Collection<InstructorFileInstance> 
        getAllPrivateFilesForInstructor(int instructorNumber);
    
    /**
     * Retrieves a Collection of all files for the given instructor.
     * 
     * @param instructorNumber The instructorNumber of the Instructor whose files you want.
     * @return A Collection of private instructor files.
     */
    public Collection<InstructorFileInstance> 
        getAllFilesForInstructor(int instructorNumber);
    
    /**
     * Returns the instructor file specified by the file number.
     * 
     * @param fileNumber The desired file's file number.
     * @return The specified file.
     */
    public InstructorFileInstance getFile(int fileNumber);
    
    /**
     * Deletes the instructor file specified by the given file number from this
     * system.
     * 
     * @param fileNumber The  file number of the file to be deleted.
     * @return True if the file was removed, false otherwise. 
     */
    public boolean removeFile(int fileNumber);
    
    /**
     * Gets the name of the object to which the given file instance is attached.
     * This can be null if the file instance is not attached to an object.
     * 
     * @param instance The given file instance.
     * @return The name of the object to which the given file instance is
     * attached. This can be null if the file instance is not attached to an
     * object.
     */
    public String getAttachedObjectName(InstructorFileInstance instance);
    
    
    /*These methods are to handle files attached to instructional lessons using
     * class LessonFileInstance*/
    
    /**
     * Retrieves all file names for the given lesson stored in the database.
     *
     * @param lesson The lesson to retrieve the file names for.
     * @return A <code>Vector</code> of all lesson files.
     */
    public Vector<LessonFileInstance> getAllFilesForLesson(Lesson lesson);

    /**
     * Deletes an instance of the
     * <code>LessonFileInstance</code> object stored in the database.
     *
     * @param lessonFile The file to be deleted.
     * @return True if the file was deleted, false otherwise.
     */
    public boolean deleteLessonsFile(LessonFileInstance lessonFile);

    /**
     * Retrieves all lesson file for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all lesson files.
     */
    public Vector<LessonFileInstance> 
        getAllLessonFilesForInstructor(User instructor);

    /**
     * Inserts the given file for the given lesson into the database.
     *
     * @param lessonFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    public boolean insertLessonsFile(LessonFileInstance lessonFile);

    /**
     * Updates the given lesson file instance stored in the database.
     *
     * @param lessonFile The lesson file to be updated.
     * @return True if the lesson file was updated, false otherwise.
     */
    public boolean updateLessonFile(LessonFileInstance lessonFile);
    
    
    /*These methods are to handle files attached to bookmarks using class BookmarkFileInstance*/
    
    /**
     * Retrieves all file names for the given bookmark stored in the
     * database.
     *
     * @param bookmark The bookmark to retrieve the file names for.
     * @return A <code>Vector</code> of all bookmark files.
     */
    public Vector<BookmarkFileInstance> getAllFilesForBookmark(Bookmark bookmark);

    /**
     * Deletes an instance of the
     * <code>BookmarkFileInstance</code> object stored in the database.
     *
     * @param bookmarkFile The file to be deleted.
     * @return True if the file was deleted, false otherwise.
     */
    public boolean deleteBookmarksFile(BookmarkFileInstance bookmarkFile);

    /**
     * Retrieves all bookmark file for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all bookmark files.
     */
    public Vector<BookmarkFileInstance> 
        getAllBookmarkFilesForInstructor(User instructor);

    /**
     * Inserts the given file for the given bookmark into the database.
     *
     * @param bookmarkFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    public boolean insertBookmarksFile(BookmarkFileInstance bookmarkFile);

    /**
     * Updates the given bookmark file instance stored in the database.
     *
     * @param bookmarkFile The bookmark file to be updated.
     * @return True if the bookmark file was updated, false otherwise.
     */
    public boolean updateBookmarkFile(BookmarkFileInstance bookmarkFile);
    
    
    /*These methods are to handle files attached to class notes using class NoteFileInstance*/
    
    /**
     * Retrieves all file names for the given instructor note stored in the
     * database.
     *
     * @param note The note to retrieve the file names for.
     * @return A <code>Vector</code> of all note files.
     */
    public Vector<NoteFileInstance> getAllFilesForNote(InstructorNote note);
    
    /**
     * Deletes an instance of the
     * <code>NoteFileInstance</code> object stored in the database.
     *
     * @param notesFile The file to be deleted.
     * @return True if the file was deleted, false otherwise.
     */
    public boolean deleteNotesFile(NoteFileInstance notesFile);
    
    /**
     * Retrieves all note files for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all note files.
     */
    public Vector<NoteFileInstance> getAllNoteFilesForInstructor(User instructor);
    
    /**
     * Inserts the given file for the given instructor note into the database.
     *
     * @param notesFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    public boolean insertNotesFile(NoteFileInstance notesFile);
    
    /**
     * Updates the given note file instance stored in the database.
     *
     * @param notesFile The note file to be updated.
     * @return True if the note file was updated, false otherwise.
     */
    public boolean updateNotesFile(NoteFileInstance notesFile);
}

