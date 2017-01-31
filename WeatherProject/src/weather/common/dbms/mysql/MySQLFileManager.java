package weather.common.dbms.mysql;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.Instructor;
import weather.common.data.InstructorDataType;
import weather.common.data.InstructorFileInstance;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkFileInstance;
import weather.common.data.lesson.Lesson;
import weather.common.data.lesson.LessonFileInstance;
import weather.common.data.note.InstructorNote;
import weather.common.data.note.NoteFileInstance;
import weather.common.dbms.DBMSFileManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This manager was created to simplify storing files with other types of data.
 * @author Shane Levengood
 */
public class MySQLFileManager implements DBMSFileManager {
    
    private MySQLImpl dbms;
    
    public MySQLFileManager(MySQLImpl impl) {
        this.dbms = impl;
    }
    
    /**
     * This method is not needed for this implementation.
     * @param system This is not used.
     */
    @Override
    public void setStorageSystem(StorageControlSystem system) {
        //No work needed.
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        
        try {
            String sql = "INSERT INTO stored_files (dataNumber, instructorNumber,"
                    + "fileName, fileContent, dataType) VALUES (?, ?, ?, ?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 0);    //Not attached to anything.
            ps.setInt(2, instructor.getUserNumber());
            ps.setString(3, fileName);
            ps.setBytes(4, fileData);
            ps.setString(5, InstructorDataType.Private.toString());
            success = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception thrown while "
                    + "trying to execute an SQL statement.", e);
            new WeatherException(0012, e, "Cannot complete the requested operation"
                    + "due to an internal problem.").show();
        } 
        finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        
        return success;
    }

    @Override
    public Collection<InstructorFileInstance> getAllPrivateFilesForInstructor(int instructorNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<InstructorFileInstance> returnedFiles = new ArrayList<>();
        
        DBMSUserManager userMan = dbms.getUserManager();
        Instructor inst = new Instructor(userMan.obtainUser(instructorNumber));
        
        try {
            String sql = "SELECT * FROM stored_files WHERE instructorNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, inst.getUserNumber());
            ps.setString(2, InstructorDataType.Private.toString());
            
            rs = ps.executeQuery();
            while (rs.next()) {
                InstructorFileInstance newFile = MySQLHelper.makeFileInstanceFromResultSet(rs);
                returnedFiles.add(newFile);
            }
        } catch (SQLException e) {
            Debug.println("Error while retrieving a file from the database");
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        
        return returnedFiles;
    }

    @Override
    public Collection<InstructorFileInstance> getAllFilesForInstructor(int instructorNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<InstructorFileInstance> returnedFiles = new ArrayList<>();
        
        DBMSUserManager userMan = dbms.getUserManager();
        Instructor inst = new Instructor(userMan.obtainUser(instructorNumber));
        
        try {
            String sql = "SELECT * FROM stored_files WHERE instructorNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, inst.getUserNumber());
            
            rs = ps.executeQuery();
            while (rs.next()) {
                InstructorFileInstance newFile = MySQLHelper.makeFileInstanceFromResultSet(rs);
                returnedFiles.add(newFile);
            }
        } catch (SQLException e) {
            Debug.println("Error while retrieving a file from the database");
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        
        return returnedFiles;
    }
    
    @Override
    public InstructorFileInstance getFile(int fileNumber)   {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InstructorFileInstance file = null;
        
        try {
            String sql = "SELECT * FROM stored_files WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, fileNumber);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                file = MySQLHelper.makeFileInstanceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            Debug.println("Error while retrieving a file from the database");
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        
        return file;
    }
    
    @Override
    public boolean removeFile(int fileNumber)  {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;        
        try {
            String sql = "DELETE FROM stored_files WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, fileNumber);
            
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }
    
    /**
     * Retrieves the names of all files associated with the given
     * <code>InstructorNote</code> stored in the database.
     *
     * @param note The InstructorNote to get all file names for.
     * @return A <code>Vector</code> of all files stored in the database
     * for the given note.
     */
    @Override
    public Vector<NoteFileInstance> getAllFilesForNote(InstructorNote note) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<NoteFileInstance> list = new Vector<>();
        try {
            String sql = "SELECT * FROM stored_files WHERE dataNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, note.getNoteNumber());
            ps.setString(2, InstructorDataType.Notes.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNotesFileInstanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Deletes an instance of the
     * <code>NoteFileInstance</code> object stored in the database.
     *
     * @param noteFile The file to be deleted.
     * @return True if the file was deleted, false otherwise.
     */
    @Override
    public boolean deleteNotesFile(NoteFileInstance noteFile) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "DELETE FROM stored_files WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, noteFile.getFileNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Retrieves all note files from the database for the given instructor.
     *
     * @param instructor The instructor to get the file names for.
     * @return A <code>Vector</code> of all note files for the given instructor.
     */
    @Override
    public Vector<NoteFileInstance> getAllNoteFilesForInstructor(User instructor) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<NoteFileInstance> list = new Vector<>();
        try {
            String sql = "SELECT * FROM stored_files WHERE instructorNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructor.getUserNumber());
            ps.setString(2, InstructorDataType.Notes.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNotesFileInstanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Inserts the given file for the given instructor note into the
     * database. Tells the storage system to store the file.
     *
     * @param notesFile The file to be inserted into the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    @Override
    public boolean insertNotesFile(NoteFileInstance notesFile) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        try {
            String sql = "INSERT INTO stored_files (dataNumber, instructorNumber,"
                    + "fileName, fileContent, dataType) VALUES (?, ?, ?, ?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notesFile.getNoteNumber());
            ps.setInt(2, notesFile.getInstructorNumber());
            ps.setString(3, notesFile.getFileName());
            ByteArrayInputStream in = new ByteArrayInputStream(notesFile.getFileData());
            ps.setBinaryStream(4, in, notesFile.length());
            ps.setString(5, InstructorDataType.Notes.toString());
            success = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closeStatement(stmt);
            MySQLHelper.closePreparedStatement(ps);
        }
        return success;
    }

    /**
     * Updates the given note file instance stored in the database.
     *
     * @param notesFile The note file to be updated.
     * @return True if the note file was updated, false otherwise.
     */
    @Override
    public boolean updateNotesFile(NoteFileInstance notesFile) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE stored_files SET dataNumber = ?, "
                    + "instructorNumber = ?, fileName = ? WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notesFile.getNoteNumber());
            ps.setInt(2, notesFile.getInstructorNumber());
            ps.setString(3, notesFile.getFileName());
            ps.setInt(4, notesFile.getFileNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }
    
    /**
     * Retrieves all file names for the given bookmark stored in the database.
     *
     * @param bookmark The bookmark to retrieve the file names for.
     * @return A <code>Vector</code> of all bookmark files.
     */
    @Override
    public Vector<BookmarkFileInstance> getAllFilesForBookmark(Bookmark bookmark) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<BookmarkFileInstance> list = new Vector<>();
        try {
            String sql = "SELECT * FROM stored_files WHERE dataNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmark.getBookmarkNumber());
            ps.setString(2, InstructorDataType.Bookmarks.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFileInstanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
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
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "DELETE FROM stored_files WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkFile.getFileNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Retrieves all bookmark file for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all bookmark files.
     */
    @Override
    public Vector<BookmarkFileInstance> getAllBookmarkFilesForInstructor(User instructor) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<BookmarkFileInstance> list = new Vector<>();
        try {
            String sql = "SELECT * FROM stored_files WHERE instructorNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructor.getUserNumber());
            ps.setString(2, InstructorDataType.Bookmarks.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkFileInstanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Inserts the given file for the given bookmark into the database.
     *
     * @param bookmarkFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    @Override
    public boolean insertBookmarksFile(BookmarkFileInstance bookmarkFile) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        try {
            String sql = "INSERT INTO stored_files (dataNumber, instructorNumber,"
                    + "fileName, fileContent, dataType) VALUES (?, ?, ?, ?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkFile.getBookmarkNumber());
            ps.setInt(2, bookmarkFile.getInstructorNumber());
            ps.setString(3, bookmarkFile.getFileName());
            ByteArrayInputStream in = new ByteArrayInputStream(bookmarkFile.getFileData());
            ps.setBinaryStream(4, in, bookmarkFile.length());
            ps.setString(5, InstructorDataType.Bookmarks.toString());
            success = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closeStatement(stmt);
            MySQLHelper.closePreparedStatement(ps);
        }
        return success;
    }

    /**
     * Updates the given bookmark file instance stored in the database.
     *
     * @param bookmarkFile The bookmark file to be updated.
     * @return True if the bookmark file was updated, false otherwise.
     */
    @Override
    public boolean updateBookmarkFile(BookmarkFileInstance bookmarkFile) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE stored_files SET dataNumber = ?, "
                    + "instructorNumber = ?, fileName = ? WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkFile.getBookmarkNumber());
            ps.setInt(2, bookmarkFile.getInstructorNumber());
            ps.setString(3, bookmarkFile.getFileName());
            ps.setInt(4, bookmarkFile.getFileNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }
    
    /**
     * Retrieves all file names for the given lesson stored in the database.
     *
     * @param lesson The lesson to retrieve the file names for.
     * @return A <code>Vector</code> of all lesson files.
     */
    @Override
    public Vector<LessonFileInstance> getAllFilesForLesson(Lesson lesson) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<LessonFileInstance> list = new Vector<>();
        try {
            String sql = "SELECT * FROM stored_files WHERE dataNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lesson.getLessonNumber());
            ps.setString(2, InstructorDataType.InstructionalLessons.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonFileInstanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
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
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "DELETE FROM stored_files WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonFile.getFileNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
    }

    /**
     * Retrieves all lesson file for the given instructor.
     *
     * @param instructor The instructor to retrieve all file names for
     * @return A <code>Vector</code> of all lesson files.
     */
    @Override
    public Vector<LessonFileInstance> getAllLessonFilesForInstructor(User instructor) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<LessonFileInstance> list = new Vector<>();
        try {
            String sql = "SELECT * FROM stored_files WHERE instructorNumber = ? AND dataType = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructor.getUserNumber());
            ps.setString(2, InstructorDataType.InstructionalLessons.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeLessonFileInstanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Inserts the given file for the given lesson into the database.
     *
     * @param lessonFile The file to be stored in the database.
     * @return True if the given file name was inserted, false otherwise.
     */
    @Override
    public boolean insertLessonsFile(LessonFileInstance lessonFile) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        try {
            String sql = "INSERT INTO stored_files (dataNumber, instructorNumber,"
                    + "fileName, fileContent, dataType) VALUES (?, ?, ?, ?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonFile.getLessonNumber());
            ps.setInt(2, lessonFile.getInstructorNumber());
            ps.setString(3, lessonFile.getFileName());
            ByteArrayInputStream in = new ByteArrayInputStream(lessonFile.getFileData());
            ps.setBinaryStream(4, in, lessonFile.length());
            ps.setString(5, InstructorDataType.InstructionalLessons.toString());
            success = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closeStatement(stmt);
            MySQLHelper.closePreparedStatement(ps);
        }
        return success;
    }

    /**
     * Updates the given lesson file instance stored in the database.
     *
     * @param lessonFile The lesson file to be updated.
     * @return True if the lesson file was updated, false otherwise.
     */
    @Override
    public boolean updateLessonFile(LessonFileInstance lessonFile) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE stored_files SET dataNumber = ?, "
                    + "instructorNumber = ?, fileName = ? WHERE fileNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonFile.getLessonNumber());
            ps.setInt(2, lessonFile.getInstructorNumber());
            ps.setString(3, lessonFile.getFileName());
            ps.setInt(4, lessonFile.getFileNumber());
            bSuccess = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return bSuccess;
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
        switch (instance.getInstructorDataType()) {
            case InstructionalLessons:
                Lesson attachedLesson = dbms.getLessonManager().get(instance
                    .getDataNumber());
                return attachedLesson.getName();
            case Bookmarks:
                Bookmark attachedBookmark = dbms.getBookmarkManager()
                        .searchByBookmarkNumber(instance.getDataNumber());
                return attachedBookmark.getName();
            case Notes:
                InstructorNote attachedNote = dbms.getNoteManager()
                        .getNote(instance.getDataNumber());
                return attachedNote.getNoteTitle();
            default:
                return null;
        }
    }
}
