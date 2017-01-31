package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.Instructor;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.note.InstructorNote;
import weather.common.dbms.DBMSNoteManager;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The MySQLNoteManager class interacts with the notes table. The notes table
 * defines an <code>InstructorNote</code>. Instructor notes are made by instructors
 * and stored in the database for retrieval and manipulation in the program.
 * This class handles the retrieval of all notes; the retrieval of notes by
 * instructor; the retrieval of notes within a time period when notes are active;
 * the retrieval of notes for a specified user; the retrieval of notes for
 * a specified course; the removal of a note; the updating a note;
 * the insertion of a note.
 *
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 * 
 */
public class MySQLNoteManager implements DBMSNoteManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;
    private Vector<StorageControlSystem> storageSystems;
    
    /**
     * Assigns a database management system to the location where notes are managed from.
     * @param dbms The database management system to set as the location to manage notes from.
     */
    public MySQLNoteManager(MySQLImpl dbms) {
        this.dbms = dbms;
        storageSystems = new Vector<StorageControlSystem>();
        addStorageSystem(StorageControlSystemImpl.getStorageSystem());
    }

     /**
     * Adds a storage system to the object
     * @param scsi the storage system to add to the object
     */
    public void addStorageSystem(StorageControlSystem scsi) {
        this.storageSystems.add(scsi);
    }

    /**
     * Retrieves all notes from the notes table. A single note is defined by
     * an <code>InstructorNote</code> object. Arranges all notes of
     * <code>InstructorNote</code> type in a <code>Vector</code>
     * object and returns it.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @return A <code>Vector</code> of all notes in the database.
     */
    @Override
    public Vector<InstructorNote> getAllNotes(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {
            String sql = "SELECT * FROM notes_view " +
                    "order by startTime DESC, endTime DESC";;
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Retrieves all notes for the given instructor from the Notes table. A single
     * note is defined by an <code>InstructorNote</code> object. This method
     * arranges <code>InstructorNote</code> objects in a <code>Vector</code>
     * object and returns it.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statements. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param instructor The instructor to retrieve the notes for.
     * @return A <code>Vector</code> of all notes for the given instructor.
     */
    @Override
    public Vector<InstructorNote> getNotesByInstructor(User instructor) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {
            String sql = "SELECT * FROM notes_view WHERE instructorNumber = ? "
                    + "order by startTime DESC, endTime DESC";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Retrieves all notes visible to the given instructor from the database.
     *
     * @param instructor The instructor to retrieve all notes for.
     * @return A <code>Vector</code> of all instructor notes.
     */
    @Override
    public Vector<InstructorNote> getAllNotesVisibleToInstructor(User instructor) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {
            String sql = "SELECT * FROM notes_view WHERE instructorNumber = ? or "
                    + "accessRights in ('Everyone','Instructors') "
                    + "order by startTime, endTime";;
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Retrieves all notes for the given time from the Notes table. A single
     * note is defined by an <code>InstructorNote</code> object. The given time
     * is of <code>Date</code> type. This method selects the notes that meet
     * the following criteria: the given time is between the StartTime and the
     * EndTime of each note. The StartTime of a note is the time when this note
     * becomes active. The EndTime of a note is the time when this Note is no
     * longer active. This method arranges selected notes in a <code>Vector</code>
     * object and returns it.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param currentDateTime The time to return the notes for.
     * @return A <code>Vector</code> of all notes for the time span specified
     * by the currentDateTime parameter.
     */
    @Override
    public Vector<InstructorNote> getNotesForTimespan(Date currentDateTime){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {    
            String sql = "SELECT * FROM notes_view WHERE ? BETWEEN startTime AND endTime";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(currentDateTime.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return list;
    }

    /**
     * Retrieves all active notes accessible to a particular user.
     * @param user The user for which notes are needed.
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToUser(User user, Date date) {
        if (user.getUserType() == UserType.student) {
            return getNotesVisibleToStudent(user, date);
        } else if (user.getUserType() == UserType.instructor) {
            return getNotesVisibleToInstructor(user, date);
        } else if (user.getUserType() == UserType.guest) {
            return getNotesVisibleToGuest(date);
        } else {
            return getNotesForTimespan(date);
        }
    }

    /**
     * Retrieves all active notes accessible to a particular user.
     * @param user The user for which notes are needed.
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToStudent(User user, Date date) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> notes = new Vector<>();
        try {
            String sql =
                    "select * from notes_view where ? between starttime and endtime and "
                    + "(accessRights in ('Everyone','AllStudents') or "
                    + "accessRights='CourseStudents' and instructorNumber in ("
                    + "select instructorNumber from courses where courseNumber in ("
                    + "select courseNumber from enrollment where userNumber=?))) "
                    + "order by startTime DESC, endTime DESC";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(date.getTime()));
            ps.setInt(2, user.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                notes.add(MySQLHelper.makeNoteFromResultSet(rs));
            }
        } catch (SQLException e){
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return notes;
    }

    /**
     * Removes the given note from the Notes table. The note is defined by
     * an <code>InstructorNote</code> object. Deletes any files associated
     * with the given note from the stored_files table.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     *
     * @param instructor The instructor to delete the note for.
     * @param note The note to be removed.
     * @return True if the given note was removed, false otherwise.
     */
    @Override
    public boolean removeNote(Instructor instructor, InstructorNote note){
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            conn = dbms.getLocalConnection();
            
            //Delete note record, with notes and note resource record removed by
            //a database trigger.
            String sql2 = "DELETE FROM notes WHERE noteNumber = ?";
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, note.getNoteNumber());
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
     * Finds the given note in the Notes table. Updates the record of the given
     * note by setting the fields to the new values.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param note The note to be updated.
     * @return True if the given note was updated, false otherwise.
     */
    @Override
    public boolean updateNote(InstructorNote note){
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bSuccess = false;
        try {
            String sql = "UPDATE notes SET noteTitle = ?, startTime = ?, endTime = ?,"+
                    "instructorNumber = ?, accessRights = ?, note = ?"+
                    "WHERE noteNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, note.getNoteTitle());
            ps.setTimestamp(2, new Timestamp(note.getStartTime().getTime()));
            ps.setTimestamp(3, new Timestamp(note.getEndTime().getTime()));
            ps.setInt(4, note.getInstructorNumber());
            ps.setString(5, note.getAccessRights().toString());
            ps.setString(6, note.getText());
            ps.setInt(7, note.getNoteNumber());
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
     * Inserts the given <code>InstructorNote</code> into the database.
     *
     * @param note The instructor note to be inserted.
     * @return The instructor note.
     */
    @Override
    public InstructorNote insertNote(InstructorNote note){
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2;
        try {
            String sql = "INSERT INTO notes (noteTitle, startTime, endTime, instructorNumber,"+
                    "accessRights, note) VALUES (?, ?, ?, ?, ?, ?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, note.getNoteTitle());
            ps.setTimestamp(2, new Timestamp(note.getStartTime().getTime()));
            ps.setTimestamp(3, new Timestamp(note.getEndTime().getTime()));
            ps.setInt(4, note.getInstructorNumber());
            ps.setString(5, note.getAccessRights().toString());
            ps.setString(6, note.getText());
            int noteNumber = MySQLHelper.executeStatementAndReturnGeneratedKey(ps);
            note.setNoteNumber(noteNumber);
            
            //Save resource numbers
            String sql2 = "INSERT INTO note_resources VALUES (?, ?, ?)";
            ps2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps2.setInt(1, noteNumber);
            ps2.setInt(2, note.getCameraNumber());
            ps2.setInt(3, note.getStationNumber());
            ps2.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
            Debug.println(e.getMessage());
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return note;
    }
    
    /**
     * Retrieves the <code>InstructorNote</code> object with the given note number.
     * 
     * @param noteNumber The requested note's number; the number serves as the
     * primary key in the database.
     * @return The <code>InstructorNote</code> with the supplied note number.
     */
    @Override
    public InstructorNote getNote(int noteNumber)   {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InstructorNote note = null;
        try {
            String sql = "SELECT * FROM notes_view WHERE noteNumber = ?";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, noteNumber);
            rs = ps.executeQuery();
            rs.first();
            note = MySQLHelper.makeNoteFromResultSet(rs);            
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return note;
    }

    /**
     * Retrieves all records for the specified student for all dates.
     *
     * @param user The specific user.
     * @return The collection of InstructorNote objects visible to the user.
     */
    @Override
    public Vector<InstructorNote> getAllNotesVisibleToStudent(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> notes = new Vector<>();
        try {
            String sql =
                    "select * from notes_view where "
                    + "(accessRights in ('Everyone','AllStudents') or "
                    + "accessRights='CourseStudents' and instructorNumber in ("
                    + "select instructorNumber from courses where courseNumber in ("
                    + "select courseNumber from enrollment where userNumber=?)))";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                notes.add(MySQLHelper.makeNoteFromResultSet(rs));
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
        return notes;
    }

    /**
     * Retrieves all notes visible to the given instructor or a given date.
     *
     * @param instructor The instructor to get the notes for.
     * @param date The set date.
     * @return A <code>Vector</code> of instructor notes.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToInstructor(User instructor, Date date) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {
            String sql = "SELECT * FROM notes_view WHERE ? between "
                    + "starttime and endtime and (instructorNumber = ? or "
                    + "accessRights in ('Everyone','Instructors')) "
                    + "order by startTime, endTime";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(date.getTime()));
            ps.setInt(2, instructor.getUserNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
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
     * Retrieves all active notes accessible to a guest on the given date.
     *
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToGuest(Date date) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {
            String sql = "SELECT * FROM notes_view WHERE ? between "
                    + "starttime and endtime and accessRights = "
                    + "'Everyone' order by startTime, endTime";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(date.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
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
     * Retrieves all active notes accessible to a guest on the given date.
     *
     * @return A vector of InstructorNote objects.
     */
    @Override
    public Vector<InstructorNote> getAllNotesVisibleToGuest() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<InstructorNote> list = new Vector<>();
        try {
            String sql = "SELECT * FROM notes_view WHERE accessRights = 'Everyone' "
                    + "order by startTime DESC, endTime DESC";;
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeNoteFromResultSet(rs));
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
}
