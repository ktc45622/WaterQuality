
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.dbms.DBMSEventManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSEventManager.
 * @author Brian Zaiser
 * @author Xiang Li
 */
public class EventManager implements DBMSEventManager{

    /**
     * Adds a record for the specified BookmarkCategory.
     * @param bookmark The BookmarkCategory being added.
     * @return True, if added successfully; false, otherwise.
     */
    @Override
    public boolean addBookmarkCategory(BookmarkCategory bookmark) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Event_Add;
       arguments = new ArrayList<Object>();
       arguments.add(bookmark);
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
     * Deletes the record for the specified BookmarkCategory.
     * @param bookmark The BookamrkCategory to delete.
     * @return True, if deleted successfully; false, otherwise.
     */
    @Override
    public boolean deleteBookmarkCategory(BookmarkCategory bookmark) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Event_Delete;
       arguments = new ArrayList<Object>();
       arguments.add(bookmark);
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
     * Updates the record for the specified BookmarkCategory with the 
     * information in the other fields.
     * @param bookmark The BookmarkCategory to update with the new information.
     * @return True, if updated successfully; false, otherwise.
     */
    @Override
    public boolean updateBookmarkCategory(BookmarkCategory bookmark) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Event_Update;
       arguments = new ArrayList<Object>();
       arguments.add(bookmark);
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
     * Retrieves a collection of all BookmarkCategories.
     * @return A collection of BookmarkCategory objects with all fields filled.
     */
    @Override
    public Vector<BookmarkCategory> getAllBookmarkCategories() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Event_GetAllBookmarkCategories;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<BookmarkCategory>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves the data for the specified BookmarkCategory, identified by number.
     * @param num The identifying number of the BookmarkCategory.
     * @return A BookmarkCategory object with all fields filled.
     */
    @Override
    public BookmarkCategory getBookmarkCategoryByNumber(int num) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Event_GetBookmarkCategoryByNumber;
       arguments = new ArrayList<Object>();
       arguments.add(num);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (BookmarkCategory) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a BookmarkCategory by name.
     * @param name The name of the BookmarkCategory.
     * @return A BookamrkCategory object with all fields filled.
     */
    @Override
    public BookmarkCategory getBookmarkCategoryByName(String name) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.Event_GetBookmarkCategoryByName;
       arguments = new ArrayList<Object>();
       arguments.add(name);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (BookmarkCategory) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
}
