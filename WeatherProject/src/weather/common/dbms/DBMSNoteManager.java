package weather.common.dbms;

import java.sql.Date;
import java.util.Vector;
import weather.common.data.*;
import weather.common.data.note.InstructorNote;

/**
 * This manager is for managing instructor notes. 
 *
 * @author Bloomsburg University Software Engineering
 * @author Chad Hall (2008)
 * @version Spring 2008
 */
public interface DBMSNoteManager {
    /* These 2 functions can return notes visible to an administrator. */

    /**
     * Returns all notes of
     * <code>InstructorNote</code> type in the database.
     *
     * @return A <code>Vector</code> of all of the notes in the database.
     */
    public Vector<InstructorNote> getAllNotes();

    /**
     * Returns all currently active notes at the given time.
     *
     * @param date The <code>Date</code> you wish to test and receive active
     * notes for.
     * @return A <code>Vector</code> of notes that meet the given criteria.
     */
    public Vector<InstructorNote> getNotesForTimespan(Date date);

    /* These 3 functions are for use with instructors.  They cover authorship,
     * overall visibility, and visibility on a given day.
     */
    
    /**
     * Returns all notes created by the given instructor.
     *
     * @param instructor The instructor to receive a list of notes from.
     * @return A <code>Vector</code> of notes created by the given instructor.
     */
    public Vector<InstructorNote> getNotesByInstructor(User instructor);

    /**
     * Retrieves all notes visible to the given instructor.
     *
     * @param instructor The instructor to get the notes for.
     * @return A <code>Vector</code> of instructor notes.
     */
    public Vector<InstructorNote> getAllNotesVisibleToInstructor(User instructor);

    /**
     * Retrieves all notes visible to the given instructor or a given date.
     *
     * @param instructor The instructor to get the notes for.
     * @param date The set date.
     * @return A <code>Vector</code> of instructor notes.
     */
    public Vector<InstructorNote> getNotesVisibleToInstructor(User instructor,
            Date date);

    /* These 2 functions cover notes visible to a given student, hoth overall and
     * for a given day
     */
    
    /**
     * Retrieves all active notes accessible to a particular student on the
     * given date.
     *
     * @param user The user for which notes are needed.
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    public Vector<InstructorNote> getNotesVisibleToStudent(User user, Date date);

    /**
     * Retrieves all notes visible to the specified student for all dates.
     *
     * @param user The specific user.
     * @return The collection of InstructorNote objects visible to the user.
     */
    public Vector<InstructorNote> getAllNotesVisibleToStudent(User user);

    /* These 2 functions cover notes visible to a guest user, hoth overall and
     * for a given day
     */
    
    /**
     * Retrieves all active notes accessible to a guest on the given date.
     *
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    public Vector<InstructorNote> getNotesVisibleToGuest(Date date);

    /**
     * Retrieves all notes visible to a guest for all dates.
     *
     * @return The collection of InstructorNote objects visible to the user.
     */
    public Vector<InstructorNote> getAllNotesVisibleToGuest();

    /**
     * Retrieves all active notes accessible to a particular user.
     *
     * @param user The user for which notes are needed.
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    public Vector<InstructorNote> getNotesVisibleToUser(User user, Date date);

    /**
     * Removes a note with the same number as the given note.
     *
     * @param instructor The instructor who wrote the note.
     * @param note The <code>InstructorNote</code> object with the number of the
     * note to be removed from the database.
     * @return True if the note was removed, false otherwise.
     */
    public boolean removeNote(Instructor instructor, InstructorNote note);

    /**
     * Updates the note with the passed InstructorNote with the passed
     * attributes.
     *
     * @param note The note with the number and attributes to be updated.
     * @return True if the note was updated, false otherwise.
     */
    public boolean updateNote(InstructorNote note);

    /**
     * Inserts the given
     * <code>InstructorNote</code> into the database.
     *
     * @param note The note to be inserted.
     * @return The instructor note.
     */
    public InstructorNote insertNote(InstructorNote note);

    /**
     * Retrieves the
     * <code>InstructorNote</code> object with the given note number.
     *
     * @param noteNumber The requested note's number; the number serves as the
     * primary key in the database.
     * @return The <code>InstructorNote</code> with the supplied note number.
     */
    public InstructorNote getNote(int noteNumber);
}
