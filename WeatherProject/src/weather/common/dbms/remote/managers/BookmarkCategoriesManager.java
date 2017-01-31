
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.User;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of the BookmarkCategoriesManager methods.
 * @author Brian Zaiser
 * @author Xiang Li -- 2014
 */
public class BookmarkCategoriesManager implements DBMSBookmarkCategoriesManager{

    /**
     * Retrieves the associated information for a BookmarkCategory from the 
     * database using the name of the BookmarkCategory.
     * @param name The name of the BookmarkCategory.
     * @return The complete BookmarkCategory object.
     */
    @Override
    public BookmarkCategory searchByName(String name) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_SearchByName;
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

    /**
     * Retrieves the associated information for a BookmarkCategory from the
     * database using the category number of the BookmarkCategory.
     * @param categoryNumber The category number of the BookmarkCategory.
     * @return The complete BookmarkCategory object.
     */
    @Override
    public BookmarkCategory searchByBookmarkCategoryNumber(int categoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_SearchByNumber;
       arguments = new ArrayList<Object>();
       arguments.add(categoryNumber);
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
     * Retrieves a collection of the associated information for the set of all
     * BookmarkCategories.
     * @return A collection of BookmarkCategory objects.
     */
    @Override
    public Vector<BookmarkCategory> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_ObtainAll;
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
     * Retrieves a collection of the associated information for the set of all
     * BookmarkCategories that are viewable by this user.
     * @param viewRights The set of view rights for this user.
     * @return A collection of BookmarkCategory objects.
     */
    @Override
    public Vector<BookmarkCategory> obtainAll(CategoryViewRights viewRights) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_ObtainAllByRights;
       arguments = new ArrayList<Object>();
       arguments.add(viewRights);
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
     * Updates the database record for the specified BookmarkCategory.
     * @param bookmarkCategory The BookmarkCategory to be updated.
     * @return True, if the update was successful; false, otherwise.
     */
    @Override
    public boolean update(BookmarkCategory bookmarkCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_Update;
       arguments = new ArrayList<Object>();
       arguments.add(bookmarkCategory);
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
     * Adds the specified BookmarkCategory to the database.
     * @param bookmarkCategory The BookmarkCategory to be added to the database.
     * @return True, if the BookmarkCategory addition was successful; false, otherwise.
     */
    @Override
    public boolean add(BookmarkCategory bookmarkCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_Add;
       arguments = new ArrayList<Object>();
       arguments.add(bookmarkCategory);
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
     * Removes the specified BookmarkCategory from the database.
     * @param bookmarkCategory The BookmarkCategory to be removed from the database.
     * @return True, if removing the BookmarkCategory was successful; false, otherwise.
     */
    @Override
    public boolean removeOne(BookmarkCategory bookmarkCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_RemoveOneByCategory;
       arguments = new ArrayList<Object>();
       arguments.add(bookmarkCategory);
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
     * Removes the BookmarkCategories created by the specified user (by user number).
     * @param createdBy The number representing the user.
     * @return True, if removing the BookmarkCategories was successful; false, otherwise.
     */
    @Override
    public int removeManyByUser(int createdBy) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_RemoveManyByUser;
       arguments = new ArrayList<Object>();
       arguments.add(createdBy);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return -1; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return ((Integer)result.getResult()).intValue();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return -1;
        } //end of if statement
    }

    /**
     * Retrieves all the BookmarkCategories with the specified duration period.
     * @param bookmarkAlternative The duration period of the BookmarkCategories retrieved.
     * @return A collection of BookmarkCategory objects.
     */
    @Override
    public Vector<BookmarkCategory> obtainAll(BookmarkDuration bookmarkAlternative) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_ObtainAllByDuration;
       arguments = new ArrayList<Object>();
       arguments.add(bookmarkAlternative);
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
     * Retrieves all the BookmarkCategories created by the specified user.
     * @param u The user who created the BookmarkCategories retrieved.
     * @return A collection of BookmarkCategory objects.
     */
    @Override
    public Vector<BookmarkCategory> obtainAllByUser(User u) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_ObtainAllByUser;
       arguments = new ArrayList<Object>();
       arguments.add(u);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<BookmarkCategory>)(result.getResult());
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieve a BookmarkCategory specified by name.
     * @param name The name of the BookmarkCategory to be retrieved.
     * @return The complete BookmarkCategory object.
     */
    @Override
    public BookmarkCategory get(String name) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkCategories_Get;
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
