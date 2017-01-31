package weather.common.dbms.mysql;

import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.User;
import weather.common.data.bookmark.*;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the bookmark_categories table in the
 * database.
 * 
 * @author Joseph Horro
 * @version Spring 2011
 */
public final class MYSQLBookmarkCategoriesManager implements DBMSBookmarkCategoriesManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MYSQLBookmarkCategoriesManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Retrieves a Bookmark category by name.
     * NOTE: The name is unique.
     * 
     * @param name The name of the bookmark category to retrieve.
     * @return The bookmark category object found, or null if there is none.
     */
    @Override
    public BookmarkCategory searchByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BookmarkCategory bmc = null;
        try {
            String sql = "SELECT * FROM bookmark_categories "
                    + "WHERE name = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                bmc = MySQLHelper.makeBookmarkCategoryFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return bmc;
    }

    /**
     * Retrieves a single bookmark category by its primary key.
     * 
     * @param categoryNumber The primary key integer to search by.
     * @return A bookmark category object that was found or null if none were found.
     */
    @Override
    public BookmarkCategory searchByBookmarkCategoryNumber(int categoryNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BookmarkCategory bmc = null;
        try {
            String sql = "SELECT * FROM bookmark_categories "
                    + "WHERE bookmarkCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                bmc = MySQLHelper.makeBookmarkCategoryFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return bmc;
    }
    
    /**
     * Returns a vector containing all bookmark categories created by the user.
     * 
     * @param u The user who created the categories you want returned.
     * @return A vector containing all the categories created by the given user,
     * empty if it does not contain anything.
     */
    @Override
    public Vector<BookmarkCategory> obtainAllByUser(User u) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<BookmarkCategory> list = new Vector<BookmarkCategory>();
        
        try {
            String sql = "SELECT * FROM bookmark_categories WHERE createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, u.getUserNumber());
            rs = ps.executeQuery();
            if (rs.next())
                list.add(MySQLHelper.makeBookmarkCategoryFromResultSet(rs));
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        
        return list;
    }
    
    /**
     * Retrieves a bookmark category by name.
     * 
     * @param name The name of the bookmark category.
     * @return The bookmark category if that name exists, null otherwise.
     */
    public BookmarkCategory get(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BookmarkCategory cat = null;
        
        try {
            String sql = "SELECT * FROM bookmark_categories WHERE name = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.first())
                cat = MySQLHelper.makeBookmarkCategoryFromResultSet(rs);
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        
        return cat;
    }

    /**
     * Retrieves a vector containing all the bookmark categories.
     * 
     * @return A vector of all the bookmark categories, empty if there is none.
     */
    @Override
    public Vector<BookmarkCategory> obtainAll() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<BookmarkCategory> list = new Vector<BookmarkCategory>();
        try {
            String sql = "SELECT * FROM bookmark_categories ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Retrieves a vector containing all the bookmark categories with a certain
     * view rights.
     * @param viewRights The view rights to filter search results by.
     * @return A vector containing all the bookmark categories under a certain
     * view rights, empty if there is none.
     */
    @Override
    public Vector<BookmarkCategory> obtainAll(CategoryViewRights viewRights) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<BookmarkCategory> list = new Vector<BookmarkCategory>();

        try {
            String sql = "SELECT * FROM bookmark_categories "
                    + "WHERE viewRights = (?) ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, viewRights.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }

    /**
     * Updates a given bookmark category.
     * @param bookmarkCategory The bookmark category to update. Requires a good
     * bookmark category object.
     * @return True if the bookmark type was updated, false otherwise.
     */
    @Override
    public boolean update(BookmarkCategory bookmarkCategory) {
        //Protect against "null" nots.
        if (bookmarkCategory.getNotes() == null) {
            bookmarkCategory.setNotes("");
        }
        
        // make sure this is not a new object
        if (bookmarkCategory.getBookmarkCategoryNumber() == -1
                || bookmarkCategory == null) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;

        try {
            String sql = "UPDATE bookmark_categories SET "
                    + "name = (?), notes = (?), createdBy = (?), "
                    + "viewRights = (?), bookmarkAlternative = (?)"
                    + ", orderRank = (?)"
                    + "WHERE bookmarkCategoryNumber = (?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, bookmarkCategory.getName());
            ps.setString(2, bookmarkCategory.getNotes());
            ps.setInt(3, bookmarkCategory.getCreatedBy());
            ps.setString(4, bookmarkCategory.getViewRights().toString());
            ps.setString(5, bookmarkCategory.getAlternative().toString());
            ps.setInt(6, bookmarkCategory.getOrderRank());
            ps.setInt(7, bookmarkCategory.getBookmarkCategoryNumber());

            isSuccessful = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Adds a new bookmark category from a pre-constructed bookmark type object.
     * This method will update the object passed in with the new auto generated
     * key from the database as the objects bookmarkCategoryNumber field. Note:
     * this object will return false if it fails or if the object to be inserted
     * does not have a bookmarkCategoryNumber of -1.
     * 
     * @param bookmarkCategory The bookmark to add.
     * @return True if added, false otherwise.
     */
    @Override
    public boolean add(BookmarkCategory bookmarkCategory) {
        //Protect against "null" nots.
        if(bookmarkCategory.getNotes() == null){
            bookmarkCategory.setNotes("");
        }
        
        // make sure this is a new object
        if (bookmarkCategory.getBookmarkCategoryNumber() != -1
                || bookmarkCategory == null) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        int orderRank = 0;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            conn = dbms.getLocalConnection();
            stmt=conn.createStatement();

            String sql = "SELECT MAX(orderRank) FROM bookmark_categories";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                orderRank = rs.getInt("MAX(orderRank)") + 1;
            }
            sql = "INSERT INTO bookmark_categories ( name, notes, "
                    + "createdBy, viewRights, bookmarkAlternative, orderRank) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);
            ps.setString(1, bookmarkCategory.getName());
            ps.setString(2, bookmarkCategory.getNotes());
            ps.setInt(3, bookmarkCategory.getCreatedBy());
            ps.setString(4, bookmarkCategory.getViewRights().toString());
            ps.setString(5, bookmarkCategory.getAlternative().toString());
            ps.setInt(6, orderRank);

            int key = MySQLHelper.executeStatementAndReturnGeneratedKey(ps);

            if (key > 0) {
                bookmarkCategory.setBookmarkCategoryNumber(key);
                isSuccessful = true;
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Removes a specific bookmark category.
     * 
     * @param bookmarkCategory The bookmark category to remove.
     * @return True if removed, false if not.
     */
    @Override
    public boolean removeOne(BookmarkCategory bookmarkCategory) {
        // check to see if this is a valid bookmark
        if (bookmarkCategory.getBookmarkCategoryNumber() == -1
                || bookmarkCategory == null) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccessful = false;
        DBMSBookmarkInstanceManager manager = dbms.getBookmarkManager();
        DBMSBookmarkEventTypesManager typeman = dbms.getBookmarkTypesManager();
        
        try {
            //We must get a list of effected bookmarks and move them to a different category and type

            //First, get replacements
            BookmarkCategory uncategorized = get("<Uncategorized>");
            BookmarkType none = typeman.searchByName("<None>", "<Uncategorized>");
            
            //Now, update bookmarks
            Vector<Bookmark> effectedBookmarks = manager.searchByCategoryNumber(bookmarkCategory.getBookmarkCategoryNumber());
            for (Bookmark bookmark : effectedBookmarks) {
                bookmark.setCategoryNumber(uncategorized.getBookmarkCategoryNumber());
                bookmark.setTypeNumber(none.getInstanceTypeNumber());
                manager.update(bookmark);
            }
            
            //Now, remove subcategories(types)
            Vector<BookmarkType> effectedTypes = typeman.obtainAll(bookmarkCategory.getBookmarkCategoryNumber());
            for (BookmarkType type : effectedTypes) {
                typeman.removeOne(type);
            }
            
            conn = dbms.getLocalConnection();
            String sql = "DELETE FROM bookmark_categories "
                    + "WHERE bookmarkCategoryNumber = (?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkCategory.getBookmarkCategoryNumber());
            isSuccessful = !ps.execute();
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
            MySQLHelper.closePreparedStatement(ps);
        }
        return isSuccessful;
    }

    /**
     * Removes bookmark categories that were created by a specific user.
     * 
     * @param createdBy The user number to filter the deletion by.
     * @return The number of records effected.
     */
    @Override
    public int removeManyByUser(int createdBy) {
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM bookmark_categories "
                    + "WHERE createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, createdBy);

            // if the update was successful, test whether any rows were affected,
            // and return how many
            if (!ps.execute()) {
                rowsAffected = ps.getUpdateCount();
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return rowsAffected;
    }

    /**
     * Retrieves a vector containing all the bookmark categories with a certain
     * bookmark alternative.
     * 
     * @param bookmarkAlternative Represents instances or events.
     * @return A vector of bookmark categories, empty if none are found.
     */
    @Override
    public Vector<BookmarkCategory> obtainAll(BookmarkDuration bookmarkAlternative) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<BookmarkCategory> list = new Vector<BookmarkCategory>();

        try {
            String sql = "SELECT * FROM bookmark_categories "
                    + "WHERE bookmarkAlternative = (?) ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, bookmarkAlternative.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
        return list;
    }
}
