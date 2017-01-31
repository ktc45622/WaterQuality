
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of the BookmarkEventTypesManager methods.
 * @author Brian Zaiser
 */
public class BookmarkEventTypesManager  implements DBMSBookmarkEventTypesManager{

    /**
     * Retrieves a bookmark type by type from a given category.
     * Note: names are unique so this method will return a single bookmark type
     * or null.
     * @param type The type of the bookmark  type to attempt to find.
     * @param category The bookmark type bring searched.
     * @return - A bookmark type or null if none is found.
     */
    @Override
    public BookmarkType searchByName(String type, String category) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_SearchByName;
       arguments = new ArrayList<Object> ();
       arguments.add(type);
       arguments.add(category);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (BookmarkType) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a BookmarkType, specified by number of the BookmarkType, from the database.
     * @param typeNumber The number of the BookmarkType.
     * @return The BookmarkType object with all fields filled.
     */
    @Override
    public BookmarkType searchByBookmarkTypeNumber(int typeNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_SearchByNumber;
       arguments = new ArrayList<Object> ();
       arguments.add(typeNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (BookmarkType) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all the BookmarkTypes from the database.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_ObtainAll;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<BookmarkType>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all the BookmarkTypes from the database based on the viewing 
     * rights of the current user.
     * @param viewRights The viewing rights of this user.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAll(CategoryViewRights viewRights) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_ObtainAllByRights;
       arguments = new ArrayList<Object> ();
       arguments.add(viewRights);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<BookmarkType>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all BookmarkTypes from the database with the specified 
     * BookmarkCategory number.
     * @param bookmarkCategoryNumber The number of the BookamrkCategory.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAll(int bookmarkCategoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_ObtainAllByCategoryNumber;
       arguments = new ArrayList<Object> ();
       arguments.add(bookmarkCategoryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<BookmarkType>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all BookmarkTypes from the database created by the specified user.
     * @param userID The user identification for the current user.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAllbyUserID(int userID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_ObtainAllByUser;
       arguments = new ArrayList<Object> ();
       arguments.add(userID);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<BookmarkType>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the database record of the specified BookmarkType.
     * @param bookmarkType The BookmarkType to be updated.
     * @return True, if the update was successful; false, otherwise.
     */
    @Override
    public boolean update(BookmarkType bookmarkType) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_Update;
       arguments = new ArrayList<Object> ();
       arguments.add(bookmarkType);
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
     * Adds a database record for the specified BookmarkType.
     * @param bookmarkType The BookmarkType to be added to the database.
     * @return True, if the record was successfully added; false, otherwise.
     */
    @Override
    public boolean add(BookmarkType bookmarkType) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_Add;
       arguments = new ArrayList<Object> ();
       arguments.add(bookmarkType);
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
     * Deletes the specified BookmarkType from the database.
     * @param bookmarkType The BookmarkType to be deleted from the database.
     * @return True, if the record was successfully removed; false, otherwise.
     */
    @Override
    public boolean removeOne(BookmarkType bookmarkType) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_RemoveOneByType;
       arguments = new ArrayList<Object> ();
       arguments.add(bookmarkType);
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
     * Deletes records from the database based on the number of the BookmarkCategory.
     * @param bookmarkCategoryNumber The number of the BookmarkCategory for the 
     *      BookmarkTypes to be deleted.
     * @return True, if the records were successfully removed; false, otherwise.
     */
    @Override
    public int removeMany(int bookmarkCategoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkEventTypes_RemoveMany;
       arguments = new ArrayList<Object> ();
       arguments.add(bookmarkCategoryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return -1; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return ((Integer) result.getResult()).intValue();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return -1;
        } //end of if statement
    }
    
}
