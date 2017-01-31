
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.lesson.LessonCategory;
import weather.common.dbms.DBMSLessonCategoryManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSLessonCategoryManager.
 * @author Brian Zaiser
 */
public class LessonCategoryManager implements DBMSLessonCategoryManager{

    /**
     * Adds a record for the specified LessonCategory.
     * @param category The new LessonCategory to be added.
     * @return True, if added successfully; false, otherwise.
     */
    @Override
    public boolean add(LessonCategory category) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonCategory_Add;
       arguments = new ArrayList<Object>();
       arguments.add(category);
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
     * Updates the database record for the specified LessonCategory using the 
     * new information provided in the fields.
     * @param category The LessonCategory to be updated.
     * @return True, if updated successfully; false, otherwise.
     */
    @Override
    public boolean update(LessonCategory category) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonCategory_Update;
       arguments = new ArrayList<Object>();
       arguments.add(category);
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
     * Deletes the database record for the specified LessonCategory.
     * @param category The LessonCategory to be deleted.
     * @return True, if deleted successfully; false, otherwise.
     */
    @Override
    public boolean delete(LessonCategory category) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonCategory_Delete;
       arguments = new ArrayList<Object>();
       arguments.add(category);
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
     * Retrieves a LessonCategory based on its identifying number.
     * @param categoryNumber The identifying number of the LessonCategory.
     * @return  A LessonCategory object with all fields filled.
     */
    @Override
    public LessonCategory get(int categoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonCategory_Get;
       arguments = new ArrayList<Object>();
       arguments.add(categoryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (LessonCategory) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all the LessonCategory records.
     * @return A collection of LessonCategory objects with all fields filled.
     */
    @Override
    public Vector<LessonCategory> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonCategory_ObtainAll;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonCategory>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the LessonCategory records viewable by the specified user.
     * @param userNumber The identifying number of the current user.
     * @return A collection of LessonCategory objects.
     */
    @Override
    public Vector<LessonCategory> obtainViewableBy(int userNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.LessonCategory_ObtainViewableBy;
       arguments = new ArrayList<Object>();
       arguments.add(userNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<LessonCategory>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
}
