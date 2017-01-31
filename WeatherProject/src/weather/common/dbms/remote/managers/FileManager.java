
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import weather.StorageControlSystem;
import weather.common.data.Instructor;
import weather.common.data.InstructorFileInstance;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkFileInstance;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonFileInstance;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.NoteFileInstance;
import weather.common.dbms.DBMSFileManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSFileManager.
 * @author Brian Zaiser
 */
public class FileManager implements DBMSFileManager{

    /**
     * Sets the StorageSystem used by the application.
     * @param system The StorageSystem to use.
     */
    @Override
    public void setStorageSystem(StorageControlSystem system) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.File_SetStorage;
       arguments = new ArrayList();
       arguments.add(system);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return ; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return ;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return ;
        } //end of if statement
    }

    /**
     * Stores the given file on the server as a private file for the given 
     * instructor.
     * 
     * @param instructor The Instructor that is storing the file.
     * @param fileName The name of the file to be stored.
     * @param fileData The data in the file to be stored.
     * @return True if the operation is successful, false otherwise.
     */
    @Override
    public boolean storePrivateFile(Instructor instructor, String fileName,
            byte[] fileData) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.File_StoreFile;
       arguments = new ArrayList();
       arguments.add(instructor);
       arguments.add(fileName);
       arguments.add(fileData);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Retrieves the file information for the specified instructor.
     * @param instructorNumber The identifying number of an instructor.
     * @return A collection of InstructorFileInstance objects.
     */
    @Override
    public Collection<InstructorFileInstance> getAllPrivateFilesForInstructor(int instructorNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.File_GetPrivateFiles;
       arguments = new ArrayList();
       arguments.add(instructorNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Collection<InstructorFileInstance>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all file information for the specified instructor.
     * @param instructorNumber The identifying number of the instructor.
     * @return A collection of InstructorFileInstance objects.
     */
    @Override
    public Collection<InstructorFileInstance> getAllFilesForInstructor(int instructorNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.File_GetAllFilesForInstructor;
       arguments = new ArrayList();
       arguments.add(instructorNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Collection<InstructorFileInstance>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the file information by file number.
     * @param fileNumber The identifying number of the file.
     * @return The file information in an InstructorFileInstance.
     */
    @Override
    public InstructorFileInstance getFile(int fileNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.File_GetFile;
       arguments = new ArrayList<Object>();
       arguments.add(fileNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (InstructorFileInstance) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Deletes the record for the file, determined by file number.
     * @param fileNumber The identifying number of the file to be deleted.
     * @return True, if deleted successfully; false, otherwise.
     */
    @Override
    public boolean removeFile(int fileNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.File_RemoveFile;
       arguments = new ArrayList<Object>();
       arguments.add(fileNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }
    
    /**
     * Retrieves the records for the specified note.
     *
     * @param note The specific note to retrieve.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<NoteFileInstance> getAllFilesForNote(InstructorNote note) {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = null;
        ArrayList<Object> arguments = null;
        commandType = DatabaseCommandType.Note_GetAllFilesForNote;
        arguments = new ArrayList<Object>();
        arguments.add(note);
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            return null;
        }
        if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
            return (Vector<NoteFileInstance>) result.getResult();
        } else {
            // create and show weather exception. Log error with weatherLogger class
            return null;
        } //end of if statement
    }

    /**
     * Deletes the database record for the specified NoteFileInstance associated
     * with the specified instructor.
     *
     * @param notesFile The specific notes file.
     * @return True, if deleted successfully; false, otherwise.
     */
    @Override
    public boolean deleteNotesFile(NoteFileInstance notesFile) {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = null;
        ArrayList<Object> arguments = null;
        commandType = DatabaseCommandType.Note_DeleteNotesFile;
        arguments = new ArrayList<Object>();
        arguments.add(notesFile);
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            return false;
        }
        if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
            return true;
        } else {
            // create and show weather exception. Log error with weatherLogger class
            return false;
        } //end of if statement
    }

    /**
     * Retrieves all records for the specified instructor.
     *
     * @param instructor The specific instructor.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<NoteFileInstance> getAllNoteFilesForInstructor(User instructor) {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = null;
        ArrayList<Object> arguments = null;
        commandType = DatabaseCommandType.Note_GetNoteFilesForInstructor;
        arguments = new ArrayList<Object>();
        arguments.add(instructor);
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            return null;
        }
        if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
            return (Vector<NoteFileInstance>) result.getResult();
        } else {
            // create and show weather exception. Log error with weatherLogger class
            return null;
        } //end of if statement
    }

    /**
     * Adds a database record for the specified NoteFileInstance associated with
     * the specified instructor.
     *
     * @param notesFile
     * @return True, if added successfully; false, otherwise.
     */
    @Override
    public boolean insertNotesFile(NoteFileInstance notesFile) {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = null;
        ArrayList<Object> arguments = null;
        commandType = DatabaseCommandType.Note_InsertNotesFile;
        arguments = new ArrayList<Object>();
        arguments.add(notesFile);
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            return false;
        }
        if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
            return true;
        } else {
            // create and show weather exception. Log error with weatherLogger class
            return false;
        } //end of if statement
    }

    /**
     * Updates the database record for the specified NoteFileInstance.
     *
     * @param notesFile
     * @return True, if updated successfully; false, otherwise.
     */
    @Override
    public boolean updateNotesFile(NoteFileInstance notesFile) {
        RemoteDatabaseCommand command = null;
        RemoteDatabaseResult result = null;
        DatabaseCommandType commandType = null;
        ArrayList<Object> arguments = null;
        commandType = DatabaseCommandType.Note_UpdateNotesFile;
        arguments = new ArrayList<Object>();
        arguments.add(notesFile);
        command = new RemoteDatabaseCommand(commandType, arguments);
        result = command.execute();
        if (result == null) {
            return false;
        }
        if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
            return true;
        } else {
            // create and show weather exception. Log error with weatherLogger class
            return false;
        } //end of if statement
    }
    
    /**
     * Retrieves all file names for the given bookmark stored in the database.
     *
     * @param bookmark The bookmark to retrieve the file names for.
     * @return A <code>Vector</code> of all bookmark files.
     */
    @Override
    public Vector<BookmarkFileInstance> getAllFilesForBookmark(Bookmark bookmark) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Deletes an instance of the
     * <code>BookmarkFileInstance</code> object stored in the database.
     *
     * @param bookmarkFile The file to be deleted.
     * @return True if the file was deleted, false otherwise.
     */
    @Override
    public boolean deleteBookmarksFile(BookmarkFileInstance bookmarkFile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Retrieves all bookmark file for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all bookmark files.
     */
    @Override
    public Vector<BookmarkFileInstance> getAllBookmarkFilesForInstructor(User instructor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Inserts the given file for the given bookmark into the database.
     *
     * @param bookmarkFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    @Override
    public boolean insertBookmarksFile(BookmarkFileInstance bookmarkFile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Updates the given bookmark file instance stored in the database.
     *
     * @param bookmarkFile The bookmark file to be updated.
     * @return True if the bookmark file was updated, false otherwise.
     */
    @Override
    public boolean updateBookmarkFile(BookmarkFileInstance bookmarkFile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Retrieves all file names for the given lesson stored in the database.
     *
     * @param lesson The lesson to retrieve the file names for.
     * @return A <code>Vector</code> of all lesson files.
     */
    @Override
    public Vector<LessonFileInstance> getAllFilesForLesson(Lesson lesson) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Deletes an instance of the
     * <code>LessonFileInstance</code> object stored in the database.
     *
     * @param lessonFile The file to be deleted.
     * @return True if the file was deleted, false otherwise.
     */
    @Override
    public boolean deleteLessonsFile(LessonFileInstance lessonFile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Retrieves all lesson file for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all lesson files.
     */
    @Override
    public Vector<LessonFileInstance> getAllLessonFilesForInstructor(User instructor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Inserts the given file for the given lesson into the database.
     *
     * @param lessonFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    @Override
    public boolean insertLessonsFile(LessonFileInstance lessonFile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Updates the given lesson file instance stored in the database.
     *
     * @param lessonFile The lesson file to be updated.
     * @return True if the lesson file was updated, false otherwise.
     */
    @Override
    public boolean updateLessonFile(LessonFileInstance lessonFile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the name of the object to which the given file instance is attached.
     * This can be null if the file instance is not attached to an object.
     * 
     * @param instance The given file instance.
     * @return The name of the object to which the given file instance is
     * attached. This can be null if the file instance is not attached to an
     * object.
     */
    @Override
    public String getAttachedObjectName(InstructorFileInstance instance) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
