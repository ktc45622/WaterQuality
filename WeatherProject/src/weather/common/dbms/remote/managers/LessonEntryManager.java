
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.AccessRights;
import weather.common.data.lesson.LessonEntry;
import weather.common.dbms.DBMSLessonEntryManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSLessonEntryManager.
 * @author Brian Zaier
 */
public class LessonEntryManager implements DBMSLessonEntryManager{

    /**
     * Adds a record for the specified LessonEntry.
     * @param entry The Lesson to be added.
     * @return True, if added successfully; false, otherwise.
     */
    @Override
    public boolean add(LessonEntry entry) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_Add;
       arguments = new ArrayList<Object>();
       arguments.add(entry);
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
     * Updates the record for the specified LessonEntry with the values in the fields.
     * @param entry The lesson to be updated.
     * @return True, if updated successfully; false, otherwise.
     */
    @Override
    public boolean update(LessonEntry entry) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_Update;
       arguments = new ArrayList<Object>();
       arguments.add(entry);
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
     * Deletes the record for the specified LessonEntry.
     * @param entry The lesson to be deleted.
     * @return True, if deleted successfully; false, otherwise.
     */
    @Override
    public boolean delete(LessonEntry entry) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_Delete;
       arguments = new ArrayList<Object>();
       arguments.add(entry);
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
     * Retrieves the information for the LessonEntry specified by identifying number.
     * @param lessonEntryNumber The identifying number for the LessonEntry.
     * @return A LessonEntry object with all fields filled.
     */
    @Override
    public LessonEntry get(int lessonEntryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_Get;
       arguments = new ArrayList<Object>();
       arguments.add(lessonEntryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (LessonEntry) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all LessonEntry records.
     * @return A collection of LessonEntry objects with all fields filled.
     */
    @Override
    public Vector<LessonEntry> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_ObtainAll;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonEntry>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all LessonEntry records for the user specified by number.
     * @param userNumber The identifying number for the user.
     * @return A collection of LessonEntry objects with all fields filled.
     */
    @Override
    public Vector<LessonEntry> obtainByUser(int userNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_ObtainByUser;
       arguments = new ArrayList<Object>();
       arguments.add(userNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonEntry>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all LessonEntry records for the LessonCategory specified by number.
     * @param categoryNumber The identifying number of the LessonCategory.
     * @return A collection of LessonEntry objects with all fields filled.
     */
    @Override
    public Vector<LessonEntry> obtainByCategory(int categoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_ObtainByCategory;
       arguments = new ArrayList<Object>();
       arguments.add(categoryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonEntry>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all LessonEntry records viewable by the current user.
     * @param access The access rights for the current user.
     * @return A collection of LessonEntry objects with all fields filled.
     */
    @Override
    public Vector<LessonEntry> obtainByAccessRights(AccessRights access) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_ObtainByAccessRights;
       arguments = new ArrayList<Object>();
       arguments.add(access);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonEntry>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all LessonEntry records for the specified lesson number.
     * @param lessonNumber The identifying number for the LessonEntry records.
     * @return A collection of LessonEntry objects with all fields filled.
     */
    @Override
    public Vector<LessonEntry> obtainByLessonNumber(int lessonNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonEntry_ObtainByLessonNumber;
       arguments = new ArrayList<Object>();
       arguments.add(lessonNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonEntry>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
}
