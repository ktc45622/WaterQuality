package weather.common.dbms;

import java.sql.Date;
import java.util.Vector;
import weather.common.data.AccessRights;
import weather.common.data.User;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkRank;

/**
 * This class manages the bookmark_instances table in the database.
 * @author Joseph Horro
 * @version Spring 2011
 */
public interface DBMSBookmarkInstanceManager {

    /**
     * Adds a new bookmark. Requires a pre-constructed bookmark image.
     * This method will update the object passed in with the new auto generated
     * key from the database as the objects bookmarkNumber field. Note:
     * this object will return false if it fails or if the object to be inserted
     * does not have a bookmarkNumber of -1.
     * 
     * @param bookmark The bookmark to add.
     * @return True if added, false otherwise.
     */
    public boolean add(Bookmark bookmark);

    /**
     * Removes the bookmark given.
     * 
     * @param bookmark The bookmark to remove.
     * @return True if removed, false otherwise.
     */
    public boolean removeOne(Bookmark bookmark);

    /**
     * Removes all bookmarks in a category.
     * 
     * @param categoryNumber The category number used to filter the deletion.
     * @return The number of records effected.
     */
    public int removeManyByCategoryNumber(int categoryNumber);

    /**
     * Remove all bookmarks with a certain type number.
     * 
     * @param typeNumber The type number to filter deletions by.
     * @return The number of records effected.
     */
    public int removeManyByTypeNumber(int typeNumber);

    /**
     * Removes all bookmarks by a user.
     * 
     * @param userNumber The user number to filter deletion by.
     * @return The number of records effected.
     */
    public int removeManyByUserNumber(int userNumber);

    /**
     * Removes all bookmarks with a specific rank.
     * 
     * @param rank The rank of bookmark to remove.
     * @return The number of records effected.
     */
    public int removeManyByRanking(BookmarkRank rank);

    /**
     * Retrieves a bookmark by primary key.
     * 
     * @param bookmarkNumber The key to match in the search.
     * @return A new bookmark instance matching the number or null
     * if one does not exists.
     */
    public Bookmark searchByBookmarkNumber(int bookmarkNumber);

    /**
     * Finds all bookmarks created by a particular user.
     * 
     * @param createdBy The userNumber of the user who created the bookmarks.
     * @return A vector of bookmark objects, or null if none are found.
     */
    public Vector<Bookmark> searchByCreatedBy(int createdBy);

    /**
     * Finds all bookmarks with a certain access right.
     * 
     * @param accessRights The access right to filter the search by.
     * @return A vector of bookmark objects, or null if none are found.
     */
    public Vector<Bookmark> searchByAccessRights(AccessRights accessRights);

    /**
     * Finds all bookmarks with a certain rank.
     * 
     * @param ranking The rank to filter the search by.
     * @return A vector of bookmark objects, or null if none are found.
     */
    public Vector<Bookmark> searchByRank(BookmarkRank ranking);

    /**
     * Finds all bookmarks within a certain category.
     * 
     * @param categoryNumber The bookmark category to filter search results by.
     * @return A vector of bookmark objects, or null if none are found.
     */
    public Vector<Bookmark> searchByCategoryNumber(int categoryNumber);

    /**
     * Finds all bookmarks with a certain type.
     * 
     * @param typeNumber The type to filter search results by.
     * @return A vector of bookmark objects, or null if none are found.
     */
    public Vector<Bookmark> searchByTypeNumber(int typeNumber);

    /**
     * Updates the given bookmark.
     * 
     * @param bookmark The bookmark to update.
     * @return True if successful, false otherwise.
     */
    public boolean update(Bookmark bookmark);

    /**
     * Returns all the bookmark instances from the bookmark instances table.
     * 
     * @return A list of all bookmark instances or an empty vector if there is none.
     */
    public Vector<Bookmark> obtainAll();

    /** 
     * Returns all the bookmarks this user can view based on the user's
     * access rights and type. 
     * 
     * Providing the instructor list is necessary if dealing
     * with a student account, otherwise null may be used or an empty vector.
     * @param user The user who wants to view all bookmarks.
     * @param instructorList  The list of instructors a student may have at a given time
     * available in the system to him/her.
     */
    public Vector<Bookmark> searchAllBookmarksViewableByUser(User user, Vector<User> instructorList);

    /** 
     * Returns all the bookmarks in the provided time range that this user can
     * view based on the user's access rights and type.
     * 
     * @param user The user who wants to view all bookmarks.
     * available in the system to him/her.
     * @param startDate The date to begin the search.
     * @param endDate The date to end the search.
     * @param instructorList The list of instructors a student may have, otherwise the
     * value may be null or an empty vector.
     * @return A list of bookmarkInstances.
     */
    public Vector<Bookmark> searchAllBookmarksViewableByUserWithinTimeRange(User user, Date startDate,
            Date endDate, Vector<User> instructorList);
    
    /**
     * Returns a vector of all the bookmark instances an instructor may see with a given
     * category number.
     * @param user The user.
     * @param categoryNumber The bookmark category number.
     * @param instructorList The list of instructors a user may have if they are a student,
     * otherwise the parameter may be left null or filled with an empty vector.
     * @return A vector list of all the bookmark instance objects fitting this criteria.
     */
    public Vector<Bookmark> searchByCategoryNumberForUser(User user, int categoryNumber,
            Vector<User> instructorList);

    /**
     * Returns a vector of all the bookmark instances a guest may see with a given type number
     * @param user The user.
     * @param typeNumber The type number.
     * @param instructorList The list of instructors a user may have if they are a student,
     * otherwise the parameter may be left null or filled with an empty vector.
     * @return A vector list of all the bookmark instance objects fitting this criteria.
     */
    public Vector<Bookmark> searchByTypeNumberForUser(User user, int typeNumber,
            Vector<User> instructorList);

}
