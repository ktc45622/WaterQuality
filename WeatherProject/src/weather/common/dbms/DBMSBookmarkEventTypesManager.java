package weather.common.dbms;

import java.util.Vector;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;

/**
 * This class manages the bookmark_event_types table in the database.
 * @author Joseph Horro
 * @version Spring 2011
 */
public interface DBMSBookmarkEventTypesManager {

    /**
     * Retrieves a bookmark type by type from a given category.
     * Note: names are unique so this method will return a single bookmark type
     * or null.
     * @param type The type of the bookmark  type to attempt to find.
     * @param category The bookmark type bring searched.
     * @return - A bookmark type or null if none is found.
     */
    public BookmarkType searchByName(String type, String category);

    /**
     * Retrieve a single bookmark type by its primary key.
     * @param typeNumber The primary key integer to search by.
     * @return A new bookmarkType object or null if none is found.
     */
    public BookmarkType searchByBookmarkTypeNumber(int typeNumber);

    /**
     * Retrieves a vector containing all the bookmark types.
     * @return A vector containing all the bookmark types, or null if there is
     * none.
     */
    public Vector<BookmarkType> obtainAll();

    /**
     * Retrieves a vector containing all the bookmark types with a certain
     * view right.
     * @param viewRights The view rights to filter search results by.
     * @return A vector containing all the bookmark types under a certain view
     * rights, or null if there is none.
     */
    public Vector<BookmarkType> obtainAll(CategoryViewRights viewRights);

    /**
     * Retrieves a vector containing all the bookmark types with a certain
     * bookmark category number.
     * @param bookmarkCategoryNumber The bookmark category number to filter
     * search results by.
     * @return A vector containing all the bookmark types under a certain
     * bookmark category number, or null if there is none.
     */
    public Vector<BookmarkType> obtainAll(int bookmarkCategoryNumber);

    /**
     * Retrieves a vector containing all the bookmark types by some user or null
     * if none are found.
     * @param userID The userID to filter the search by.
     * @return A vector containing all the bookmark types made by a specific
     * user, or null if there is none.
     */
    public Vector<BookmarkType> obtainAllbyUserID(int userID);

    /**
     * Updates a given bookmark type.
     * @param bookmarkType The type to update. Requires a valid bookmark type
     * object.
     * @return True if the bookmark type was updated, false otherwise.
     */
    public boolean update(BookmarkType bookmarkType);

    /**
     * Adds a new bookmarkType from a pre-constructed bookmark type object. This
     * method will update the object passed in with the new auto generated key
     * from the database as the objects bookmarkInstanceTypeNumber field. Note:
     * this object will return false if it fails or if the object to be inserted
     * does not have a bookmarkInstanceTypeNumber of -1.
     * @param bookmarkType The bookmark type to add. Requires a
     * bookmarkInstanceTypeNumber of -1 (Please use the proper BookmarkType
     * constructor).
     * @return True if successful, false if it fails or receives an invalid
     * object.
     */
    public boolean add(BookmarkType bookmarkType);

    /**
     * Removes a given bookmark type.
     * @param bookmarkType The bookmark type to remove.
     * @return True if successful, false if not or if an invalid object is
     * received.
     */
    public boolean removeOne(BookmarkType bookmarkType);

    /**
     * Removes bookmark types based on the bookmark category number.
     * @param bookmarkCategoryNumber The number to filter the deletion by.
     * @return The number of records effected. 0 For none.
     */
    public int removeMany(int bookmarkCategoryNumber);
}
