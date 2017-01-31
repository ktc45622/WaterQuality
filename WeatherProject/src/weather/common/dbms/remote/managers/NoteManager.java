package weather.common.dbms.remote.managers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.Instructor;
import weather.common.data.User;
import weather.common.data.note.InstructorNote;
import weather.common.dbms.DBMSNoteManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSNoteManager.
 * @author Brian Zaiser
 */
public class NoteManager implements DBMSNoteManager{

    /**
     * Retrieves all records.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<InstructorNote> getAllNotes() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetAllNotes;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<InstructorNote>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records created by the specified instructor.
     * @param instructor
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<InstructorNote> getNotesByInstructor(User instructor) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetNotesByInstructor;
       arguments = new ArrayList<Object>();
       arguments.add(instructor);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<InstructorNote>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for the specified instructor.
     * @param instructor The specific instructor.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<InstructorNote> getAllNotesVisibleToInstructor(User instructor) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetAllNotesForInstructor;
       arguments = new ArrayList<Object>();
       arguments.add(instructor);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<InstructorNote>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records between the current date and the specified start date.
     * @param date The start date for the date range.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<InstructorNote> getNotesForTimespan(Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetNotesForTimespan;
       arguments = new ArrayList<Object>();
       arguments.add(date);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<InstructorNote>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for the specified user between today and the specified date.
     * @param user The specific user.
     * @param date The start date for the date range.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToUser(User user, Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetNotesForUser;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       arguments.add(date);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<InstructorNote>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for the specified user between today and the specified date.
     * @param user The specific user.
     * @param date The start date for the date range.
     * @return A collection of InstructorNote objects with all fields filled.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToStudent(User user, Date date) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetNotesForStudent;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       arguments.add(date);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<InstructorNote>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
    /**
     * Deletes the record for the specified InstructorNote associated 
     * with the specified Instructor.
     * @param instructor The specific instructor.
     * @param note The specific note to delete.
     * @return True, if deleted successfully; false, otherwise.
     */
    @Override
    public boolean removeNote(Instructor instructor, InstructorNote note) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_RemoveNote;
       arguments = new ArrayList<Object>();
       arguments.add(instructor);
       arguments.add(note);
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
     * Updates the record for the specified InstructorNote.
     * @param note The specific note to update.
     * @return True, if updated successfully; false, otherwise.
     */
    @Override
    public boolean updateNote(InstructorNote note) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_UpdateNote;
       arguments = new ArrayList<Object>();
       arguments.add(note);
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
     * Retrieves the record specified by the InstructorNote.
     * @param note The specific note to retrieve.
     * @return An InstructorNote object with all fields filled.
     */
    @Override
    public InstructorNote insertNote(InstructorNote note) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_InsertNote;
       arguments = new ArrayList<Object>();
       arguments.add(note);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (InstructorNote) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the note specified by number.
     * @param noteNumber The identifying number of the note.
     * @return An InstructorNote object with all fields filled.
     */
    @Override
    public InstructorNote getNote(int noteNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Note_GetNote;
       arguments = new ArrayList<Object>();
       arguments.add(noteNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (InstructorNote) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Retrieves all records for the specified student for all dates.
     *
     * @param user The specific user.
     * @return The collection of InstructorNote objects visible to the user.
     */
    @Override
    public Vector<InstructorNote> getAllNotesVisibleToStudent(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Retrieves all active notes accessible to a guest on the given date.
     *
     * @param date The set date.
     * @return A vector of InstructorNote objects.
     */
    @Override
    public Vector<InstructorNote> getNotesVisibleToGuest(Date date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Retrieves all notes visible to a guest for all dates.
     *
     * @return The collection of InstructorNote objects visible to the user.
     */
    @Override
    public Vector<InstructorNote> getAllNotesVisibleToGuest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
