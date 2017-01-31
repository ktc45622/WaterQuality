
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.dbms.DBMSBookmarkInstanceTypesManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of the BookmarkInstanceTypesManager methods.
 * @author Brian Zaiser
 * @author Xiang Li
 */
public class BookmarkInstanceTypesManager implements DBMSBookmarkInstanceTypesManager{

    /**
     * Retrieves the associated information of a BookmarkType from the database 
     * based on the name of the BookmarkType.
     * @param name The name of the BookmarkType.
     * @return A BookmarkType object with all fields filled.
     */
    @Override
    public BookmarkType searchByName(String name) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_SearchByName;
       arguments = new ArrayList<Object>();
       arguments.add(name);
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
     * Retrieves the associated information of a BookmarkType from the database 
     * based on the number of the BookmarkType.
     * @param typeNumber The number of the BookmarkType.
     * @return A BookmarkType object with all fields filled.
     */
    @Override
    public BookmarkType searchByBookmarkTypeNumber(int typeNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_SerachByBookmarkTypeNumber;
       arguments = new ArrayList<Object>();
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
     * Retrieves all of the associated information of all the BookmarkTypes from
     * the database.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_ObtainAll;
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
     * Retrieves all of the associated information of all the BookmarkTypes from
     * the database based on the viewing rights of the current user.
     * @param viewRights The viewing rights of the current user.
     * @return A collection of BookamrkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAll(CategoryViewRights viewRights) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_ObtainAllByRights;
       arguments = new ArrayList<Object>();
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
     * Retrieves all of the associated information of all the BookmarkTypes from
     * the database based on the number of the BookmarkCategory.
     * @param bookmarkCategoryNumber The number of the BookmarkCategory.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAll(int bookmarkCategoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_ObtainAllByCategoryNumber;
       arguments = new ArrayList<Object>();
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
     * Retrieves all of the associated information of all the BookmarkTypes from
     * the database based on the current user identification.
     * @param userID The current user identification.
     * @return A collection of BookmarkType objects with all fields filled.
     */
    @Override
    public Vector<BookmarkType> obtainAllbyUserID(int userID) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_ObtainAllByUser;
       arguments = new ArrayList<Object>();
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
     * Updates the record in the database for the specified BookmarkType.
     * @param bookmarkType The BookmarkType to be updated, with the new information.
     * @return True, if the update was successful; false, otherwise.
     */
    @Override
    public boolean update(BookmarkType bookmarkType) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_Update;
       arguments = new ArrayList<Object>();
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
     * Adds a record in the database for the specified BookmarkType.
     * @param bookmarkType The BookmarkType to be added to the database.
     * @return True, if the addition was successful; false, otherwise.
     */
    @Override
    public boolean add(BookmarkType bookmarkType) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_Add;
       arguments = new ArrayList<Object>();
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
     * Deletes the record in the database for the specified BookmarkType.
     * @param bookmarkType The BookmarkType to be deleted from the database.
     * @return True, if the deletion was successful; false, otherwise.
     */
    @Override
    public boolean removeOne(BookmarkType bookmarkType) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_RemoveOneByType;
       arguments = new ArrayList<Object>();
       arguments.add(bookmarkType);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }

    /**
     * Deletes the record in the database for a BookmarkType specified by name.
     * @param name The name of the BookmarkType to be deleted from the database.
     * @return True, if the deletion was successful; false, otherwise.
     */
    @Override
    public boolean removeOne(String name) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_RemoveOneByName;
       arguments = new ArrayList<Object>();
       arguments.add(name);
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
     * Deletes the record in the database for BookmarkTypes based on the 
     * specified number of the BookmarkCategory.
     * @param bookmarkCategoryNumber The number of the BookmarkCategory.
     * @return The number of records deleted from the database.
     */
    @Override
    public int removeMany(int bookmarkCategoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstanceType_RemoveMany;
       arguments = new ArrayList<Object>();
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
