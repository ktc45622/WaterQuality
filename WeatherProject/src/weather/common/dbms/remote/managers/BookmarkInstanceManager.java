
package weather.common.dbms.remote.managers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.AccessRights;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkRank;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of the BookmarkInstanceManager methods.
 * @author Brian Zaiser
 * @author Xiang Li(2014)
 */
public class BookmarkInstanceManager implements DBMSBookmarkInstanceManager {

    /**
     * Adds a record to the database for the specified Bookmark.
     * @param bookmark The Bookmark to be added to the database.
     * @return True, if the Bookmark was added successfully; false, otherwise.
     */
    @Override
    public boolean add(Bookmark bookmark) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_Add;
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
     * Deletes the specified Bookmark from the database.
     * @param bookmark The Bookmark to be deleted from the database.
     * @return True, if the Bookmark was deleted successfully; false, otherwise.
     */
    @Override
    public boolean removeOne(Bookmark bookmark) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_RemoveOne;
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
     * Deletes Bookmarks from the database based on the number of the BookmarkCategory.
     * @param categoryNumber The number of the BookmarkCategory for the 
     *      Bookmarks to be deleted from the database.
     * @return The number of records deleted.
     */
    @Override
    public int removeManyByCategoryNumber(int categoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_RemoveManyByCategoryNumber;
       arguments = new ArrayList<Object>();
       arguments.add(categoryNumber);
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

    /**
     * Deletes Bookmarks from the database based on the number of the BookmarkType.
     * @param typeNumber The number of the BookmarkType for the Bookmarks to be 
     *      deleted from the database.
     * @return The number of records deleted.
     */
    @Override
    public int removeManyByTypeNumber(int typeNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_RemoveManyByTypeNumber;
       arguments = new ArrayList<Object>();
       arguments.add(typeNumber);
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

    /**
     * Deletes Bookmarks from the database based on the number of the user who 
     * created them.
     * @param userNumber The number of the user - the creator of the Bookmarks.
     * @return The number of records deleted.
     */
    @Override
    public int removeManyByUserNumber(int userNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_RemoveManyByUserNumber;
       arguments = new ArrayList<Object>();
       arguments.add(userNumber);
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

    /**
     * Deletes Bookmarks from the database based on the rank of the Bookmarks.
     * @param rank The rank of the Bookmarks to be deleted from the database.
     * @return The number of records deleted.
     */
    @Override
    public int removeManyByRanking(BookmarkRank rank) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_RemoveManyByRanking;
       arguments = new ArrayList<Object>();
       arguments.add(rank);
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

    /**
     * Retrieves a Bookmark from the database based on the number of the Bookmark.
     * @param bookmarkNumber The number of the Bookmark to be retrieved.
     * @return A Bookmark object with all fields filled.
     */
    @Override
    public Bookmark searchByBookmarkNumber(int bookmarkNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByBookmarkNumber;
       arguments = new ArrayList<Object>();
       arguments.add(bookmarkNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.SingleResultObjectReturned)) {
           return (Bookmark) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database based on the user 
     * who created them.
     * @param createdBy The number of the user - the creator of the Bookmarks.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByCreatedBy(int createdBy) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByCreatedBy;
       arguments = new ArrayList<Object>();
       arguments.add(createdBy);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database based on the access
     *  rights of the current user.
     * @param accessRights The access rights of the current user.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByAccessRights(AccessRights accessRights) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByAccessRights;
       arguments = new ArrayList<Object>();
       arguments.add(accessRights);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database based on rank.
     * @param ranking The rank of the Bookmarks that will be retrieved from the database.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByRank(BookmarkRank ranking) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByRank;
       arguments = new ArrayList<Object>();
       arguments.add(ranking);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database based on the number
     *  of the BookmarkCategory.
     * @param categoryNumber The number of the BookmarkCategory.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByCategoryNumber(int categoryNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByCategoryNumber;
       arguments = new ArrayList<Object>();
       arguments.add(categoryNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database based on the number
     *  of the BookamrkType.
     * @param typeNumber The number of the BookmarkType.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByTypeNumber(int typeNumber) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByTypeNumber;
       arguments = new ArrayList<Object>();
       arguments.add(typeNumber);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the database record for this Bookmark.
     * @param bookmark The Bookmark to be updated, with its new information.
     * @return True, if the Bookmark was updated successfully; false, otherwise.
     */
    @Override
    public boolean update(Bookmark bookmark) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_Update;
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
     * Retrieves all of the Bookmarks from the database.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> obtainAll() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_ObtainAll;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of all Bookmarks in the database viewable by the 
     * user with the specified instructors.
     * @param user The current user.
     * @param instructorList The list of instructors of the courses for the current user.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchAllBookmarksViewableByUser(User user, Vector<User> instructorList) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchAllViewableByUser;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       arguments.add(instructorList);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of all Bookmarks in the database viewable by the 
     * current user, based on a date range and a list of instructors.
     * @param user The current user.
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @param instructorList The list of instructors for the current user.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchAllBookmarksViewableByUserWithinTimeRange(User user, Date startDate, Date endDate, Vector<User> instructorList) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchAllViewableByDate;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       arguments.add(startDate);
       arguments.add(endDate);
       arguments.add(instructorList);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database viewable by the 
     * current user based on the number of the BookamrkCategory and a list of instructors.
     * @param user The current user.
     * @param categoryNumber The number of the BookmarkCategory.
     * @param instructorList The list of instructors for the current user.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByCategoryNumberForUser(User user, int categoryNumber, Vector<User> instructorList) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByCategoryNumberForUser;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       arguments.add(categoryNumber);
       arguments.add(instructorList);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves a collection of Bookmarks from the database viewable by the 
     * current user, based on the number of the BookmarkType and a list of 
     * instructors for the current user.
     * @param user The current user.
     * @param typeNumber The number of the BookmarkType.
     * @param instructorList The list of instructors for the current user.
     * @return A collection of Bookmark objects with all fields filled.
     */
    @Override
    public Vector<Bookmark> searchByTypeNumberForUser(User user, int typeNumber, Vector<User> instructorList) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.BookmarkInstance_SearchByTypeNumberForUser;
       arguments = new ArrayList<Object>();
       arguments.add(user);
       arguments.add(typeNumber);
       arguments.add(instructorList);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<Bookmark>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }
    
}
