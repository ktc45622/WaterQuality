package weather.common.dbms;

import java.util.Vector;
import weather.common.data.bookmark.BookmarkCategory;

// @TODO Must be updated along with MySQLEventManager. JH 11-4-2

/**
 * Manages events that happen in the system.
 *
 * Right now, only Bookmark category events are handled.
 *
 * @author Eric Subach (2010)
 */
public interface DBMSEventManager {
    /**
     * Add bookmark category.
     *
     * @param bookmark bookmark category to add
     * @return true if successful add, false otherwise
     */
    public boolean addBookmarkCategory (BookmarkCategory bookmark);


    /**
     * Delete bookmark category and all associated bookmarks.
     *
     * @param bookmark bookmark category to delete
     * @return true if successful delete, false otherwise
     */
    public boolean deleteBookmarkCategory (BookmarkCategory bookmark);


    /**
     * Update a bookmark category.
     *
     * @param bookmark bookmark category to update
     * @return true if successful update, false otherwise
     */
    public boolean updateBookmarkCategory (BookmarkCategory bookmark);


    /**
     * Get all bookmark categories.
     *
     * @return vector of all bookmark categories
     */
    public Vector<BookmarkCategory> getAllBookmarkCategories ();


    /**
     * Get bookmark category by number.
     *
     * @param num bookmark category number
     * @return bookmark category with specified number
     */
    public BookmarkCategory getBookmarkCategoryByNumber (int num);


    /**
     * Get bookmark category by name.
     *
     * @param name bookmark category name
     * @return bookmark category with specified name
     */
    public BookmarkCategory getBookmarkCategoryByName (String name);

}
