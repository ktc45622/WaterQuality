
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.AccessRights;
import weather.common.data.lesson.Lesson;
import weather.common.dbms.DBMSLessonManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSLessonManager.
 * @author Brian Zaiser
 */
public class LessonManager implements DBMSLessonManager{

    /**
     * Adds a database record for the specified Lesson.
     * @param lesson The Lesson to be added to the database.
     * @return True, if successfully added; false, otherwise.
     */
    @Override
    public boolean add(Lesson lesson) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_Add;
       arguments = new ArrayList<Object>();
       arguments.add(lesson);
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
     * Updates the database record for the specified Lesson.
     * @param lesson The Lesson to be updated with information in fields.
     * @return True, if successfully updated; false, otherwise.
     */
    @Override
    public boolean update(Lesson lesson) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_Update;
       arguments = new ArrayList<Object>();
       arguments.add(lesson);
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
     * Deletes the database record for the specified Lesson.
     * @param lesson The Lesson to be deleted from the database.
     * @return True, if successfully deleted; false, otherwise.
     */
    @Override
    public boolean delete(Lesson lesson) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_Delete;
       arguments = new ArrayList<Object>();
       arguments.add(lesson);
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
     * Retrieves the record from the database for the Lesson specified by number.
     * @param lessonNumber The identifying number of the Lesson.
     * @return A Lesson object with all fields filled.
     */
    @Override
    public Lesson get(int lessonNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_Get;
       arguments = new ArrayList<Object>();
       arguments.add(lessonNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (Lesson) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records from the database.
     * @return A collection of Lesson objects with all fields filled.
     */
    @Override
    public Vector<Lesson> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_ObtainAll;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Lesson>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records the specified user is in.
     * @param userNumber The identifying number of the user.
     * @return A collection of Lesson objects with all fields filled.
     */
    @Override
    public Vector<Lesson> obtainByUser(int userNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_ObtainByUser;
       arguments = new ArrayList<Object>();
       arguments.add(userNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Lesson>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for the specified LessonCategory.
     * @param categoryNumber The identifying number of the LessonCategory.
     * @return A collection of Lesson objects with all fields filled.
     */
    @Override
    public Vector<Lesson> obtainByCategory(int categoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_ObtainByCategory;
       arguments = new ArrayList<Object>();
       arguments.add(categoryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Lesson>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records based on access rights.
     * @param access The access rights.
     * @return A collection of Lesson objects with all fields filled.
     */
    @Override
    public Vector<Lesson> obtainByAccessRights(AccessRights access) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Lesson_ObtainByAccessRights;
       arguments = new ArrayList<Object>();
       arguments.add(access);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Lesson>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
}
