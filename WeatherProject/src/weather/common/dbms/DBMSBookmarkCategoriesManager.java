package weather.common.dbms;

import java.util.Vector;
import weather.common.data.User;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.CategoryViewRights;

/**
 * This class manages the bookmark categories table in the database.
 * @author Joseph Horro
 * @version Spring 2011
 */
public interface DBMSBookmarkCategoriesManager {

    /**
     * Retrieves a Bookmark category by name.
     * 
     * @param name The name of the bookmark category to retrieve.
     * @return The bookmark category object found, or null if there is none.
     */
    public BookmarkCategory searchByName(String name);

    /**
     * Retrieves a single bookmark category by its primary key.
     * NOTE: The name is unique.
     * 
     * @param categoryNumber The primary key integer to search by.
     * @return A new bookmark category object or null if none is found.
     */
    public BookmarkCategory searchByBookmarkCategoryNumber(int categoryNumber);

    /**
     * Retrieves a vector containing all the bookmark categories.
     * 
     * @return A vector of all the bookmark categories or null if there is none.
     */
    public Vector<BookmarkCategory> obtainAll();

    /**
     * Retrieves a vector containing all the bookmark categories with a certain
     * view rights.
     * 
     * @param viewRights The view rights to filter search results by.
     * @return A vector containing all the bookmark categories under a certain
     * view rights, or null if there is none.
     */
    public Vector<BookmarkCategory> obtainAll(CategoryViewRights viewRights);

    /**
     * Updates a given bookmark category.
     * 
     * @param bookmarkCategory The bookmark category to update. Requires a good
     * bookmark category object.
     * @return True if the bookmark type was updated, false otherwise.
     */
    public boolean update(BookmarkCategory bookmarkCategory);

    /**
     * Adds a new bookmark category from a pre-constructed bookmark type object.
     * 
     * This method will update the object passed in with the new auto generated
     * key from the database as the objects bookmarkCategoryNumber field. Note:
     * this object will return false if it fails or if the object to be inserted
     * does not have a bookmarkCategoryNumber of -1.
     * 
     * @param bookmarkCategory The bookmark to add.
     * @return True if added, false otherwise.
     */
    public boolean add(BookmarkCategory bookmarkCategory);

    /**
     * Removes a specific bookmark category.
     * 
     * @param bookmarkCategory The bookmark category to remove.
     * @return True if removed, false if not.
     */
    public boolean removeOne(BookmarkCategory bookmarkCategory);

    /**
     * Removes bookmark categories that were created by a specific user.
     * 
     * @param createdBy The user number to filter the deletion by.
     * @return The number of records effected.
     */
    public int removeManyByUser(int createdBy);

    /**
     * Retrieves a vector containing all the bookmark categories with a certain
     * bookmark alternative.
     * 
     * @param bookmarkAlternative Represents instances or events.
     * @return A vector of bookmark categories or null if none are found.
     */
    public Vector<BookmarkCategory>
            obtainAll(BookmarkDuration bookmarkAlternative);
    
     /**
     * Returns a vector containing all bookmark categories created by the user.
     * 
     * @param u The user who created the categories you want returned.
     * @return A vector containing all the categories created by the given user,
     * empty if it does not contain anything.
     */
    public Vector<BookmarkCategory> obtainAllByUser(User u);
   
    
    /**
     * Retrieves a bookmark category by name.
     * 
     * @param name The name of the bookmark category.
     * @return The bookmark category if that name exists, null otherwise.
     */
    public BookmarkCategory get(String name);
}
