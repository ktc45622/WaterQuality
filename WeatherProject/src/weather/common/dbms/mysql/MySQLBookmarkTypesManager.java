package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class manages interactions with the bookmark_types table in the
 * database.
 * @author Joseph Horro
 * @version Spring 2011
 */
public final class MySQLBookmarkTypesManager implements DBMSBookmarkEventTypesManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLBookmarkTypesManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        BookmarkType bmt = null;
        int categoryNumber = dbms.getBookmarkCategoriesManager().searchByName(category)
                .getBookmarkCategoryNumber();
        try {
            String sql = "SELECT * FROM bookmark_types "
                    + "WHERE name = (?) and bookmarkCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, type);
            ps.setInt(2, categoryNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                bmt = MySQLHelper.makeBookmarkTypeFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return bmt;
    }

    /**
     * Retrieve a single bookmark type by its primary key.
     * @param typeNumber The primary key integer to search by.
     * @return A <code>BookmarkType</code> object with the typeNumber or null if none is found.
     */
    @Override
    public BookmarkType searchByBookmarkTypeNumber(int typeNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BookmarkType bmt = null;
        try {
            String sql = "SELECT * FROM bookmark_types "
                    + "WHERE bookmarkInstanceTypeNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, typeNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                bmt = MySQLHelper.makeBookmarkTypeFromResultSet(rs);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return bmt;
    }

    /**
     * Retrieves a vector containing all the bookmark types.
     * @return A vector containing all the bookmark types, empty if there is none.
     */
    @Override
    public Vector<BookmarkType> obtainAll() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<BookmarkType> list = new Vector<BookmarkType>();
        try {
            String sql = "SELECT * FROM bookmark_types";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkTypeFromResultSet(rs));
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
     * Retrieves a vector containing all the bookmark types with a certain
     * view right.
     * @param viewRights The view rights to filter search results by.
     * @return A vector containing all the bookmark types under a certain view
     * rights, empty if there is none.
     */
    @Override
    public Vector<BookmarkType> obtainAll(CategoryViewRights viewRights) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<BookmarkType> list = new Vector<BookmarkType>();

        try {
            String sql = "SELECT * FROM bookmark_types "
                    + "WHERE viewRights = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, viewRights.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkTypeFromResultSet(rs));
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
     * Retrieves a vector containing all the bookmark types with a certain
     * bookmark category number.
     * @param bookmarkCategoryNumber The bookmark category number to filter
     * search results by.
     * @return A vector containing all the bookmark types under a certain
     * bookmark category number, empty if there is none.
     */
    
    @Override
    public Vector<BookmarkType> obtainAll(int bookmarkCategoryNumber) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<BookmarkType> list = new Vector<BookmarkType>();

        try {
            String sql = "SELECT * FROM bookmark_types "
                    + "WHERE bookmarkCategoryNumber = (?) "
                    + "ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkCategoryNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkTypeFromResultSet(rs));
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
     * Retrieves a vector containing all the bookmark types by some user or null
     * if none are found.
     * @param userID The userID to filter the search by.
     * @return A vector containing all the bookmark types made by a specific
     * user, empty if there is none.
     */
    @Override
    public Vector<BookmarkType> obtainAllbyUserID(int userID){
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<BookmarkType> list = new Vector<BookmarkType>();

        try {
            String sql = "SELECT * FROM bookmark_types "
                    + "WHERE createdBy = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(MySQLHelper.makeBookmarkTypeFromResultSet(rs));
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
     * Updates a given bookmark type.
     * @param bookmarkType The type to update. Requires a valid bookmark type
     * object.
     * @return True if the bookmark type was updated, false otherwise.
     */
    @Override
    public boolean update(BookmarkType bookmarkType){
        // make sure this is not a new object
        if (bookmarkType.getInstanceTypeNumber() == -1
                || bookmarkType == null) {
            return false;
        }

        //Protect against "null" nots.
        if (bookmarkType.getNotes() == null) {
            bookmarkType.setNotes("");
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;

        try {
            String sql = "UPDATE bookmark_types SET "
                    + "bookmarkCategorynumber = (?), name = (?), "
                    + "createdBy = (?), viewRights = (?), notes = (?), orderRank = (?) "
                    + "WHERE bookmarkInstanceTypeNumber = (?)";

            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, bookmarkType.getCategoryNumber());
            ps.setString(2, bookmarkType.getName());
            ps.setInt(3,bookmarkType.getCreatedBy());
            ps.setString(4, bookmarkType.getViewRights().toString());
            ps.setString(5, bookmarkType.getNotes());
            ps.setInt(6, bookmarkType.getOrderRank());
            ps.setInt(7, bookmarkType.getInstanceTypeNumber());

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
     * Adds a new bookmarkType from a pre-constructed bookmark type object. This
     * method will update the object passed in with the new auto generated key
     * from the database as the objects bookmarkInstanceTypeNumber field. Note:
     * this object will return false if it fails or if the object to be inserted
     * does not have a bookmarkInstanceTypeNumber of -1.
     * 
     * @param bookmarkType The bookmark type to add. Requires a
     * bookmarkInstanceTypeNumber of -1 (Please use the proper BookmarkType
     * constructor).
     * @return True if successful, false if it fails or receives an invalid
     * object.
     */
    @Override
    public boolean add(BookmarkType bookmarkType){
        // make sure this is a new object
        if (bookmarkType.getInstanceTypeNumber() != -1
                || bookmarkType == null) {
            return false;
        }
        
        //Protect against "null" nots.
        if (bookmarkType.getNotes() == null) {
            bookmarkType.setNotes("");
        }

        int orderRank = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccessful = false;

        try {
            conn = dbms.getLocalConnection();
            
            
            String orderSQL = "SELECT MAX(orderRank) FROM bookmark_types";
            ps = conn.prepareStatement(orderSQL);
            rs = ps.executeQuery(orderSQL);
            if (rs.next())
                orderRank = rs.getInt("MAX(orderRank)") + 1;
            
            
            String sql = "INSERT INTO bookmark_types ( bookmarkCategoryNumber, "
                    + "name, createdBy, viewRights, notes, orderRank) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, ps.RETURN_GENERATED_KEYS);

            ps.setInt(1,bookmarkType.getCategoryNumber());
            ps.setString(2, bookmarkType.getName());
            ps.setInt(3, bookmarkType.getCreatedBy());
            ps.setString(4, bookmarkType.getViewRights().toString());
            ps.setString(5, bookmarkType.getNotes());
            ps.setInt(6, orderRank);

            int key = MySQLHelper.executeStatementAndReturnGeneratedKey(ps);

            if (key > 0) {
                bookmarkType.setInstanceTypeNumber(key);
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
     * Removes a given bookmark type.
     * @param bookmarkType The bookmark type to remove.
     * @return True if successful, false if not or if an invalid object is
     * received.
     */
    @Override
    public boolean removeOne(BookmarkType bookmarkType){
        // check to see if this is a valid bookmark
        if (bookmarkType.getInstanceTypeNumber() == -1
                || bookmarkType == null) {
            return false;
        }
        
        DBMSBookmarkInstanceManager bookman = dbms.getBookmarkManager();
        DBMSBookmarkCategoriesManager catman = dbms.getBookmarkCategoriesManager();
        
        //Change bookmarks to <None> subcategory
        Vector<Bookmark> affectedBookmarks = bookman.searchByTypeNumber(bookmarkType.getInstanceTypeNumber());
        //If bookmarks exist, so does default subcategory, which only ceases to exist if the main
        //category is being deleted.
        if (affectedBookmarks.size() > 0) {
            //First, find subcategory number
            BookmarkCategory thisCategory = catman.searchByBookmarkCategoryNumber(bookmarkType.getCategoryNumber());
            BookmarkType noneSubcategory = searchByName("<None>", thisCategory.getName());
            int noneSubcategoryNumber = noneSubcategory.getInstanceTypeNumber();

            //Now, upate bookmarks
            for (Bookmark bookmark : affectedBookmarks) {
                bookmark.setTypeNumber(noneSubcategoryNumber);
                bookman.update(bookmark);
            }
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccessful = false;
        try {
            conn = dbms.getLocalConnection();
            String sql = "DELETE FROM bookmark_types "
                    + "WHERE bookmarkInstanceTypeNumber = (?)";
            ps = conn.prepareStatement(sql);

            ps.setInt(1, bookmarkType.getInstanceTypeNumber());

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
     * Removes bookmark types based on the bookmark category number.
     * @param bookmarkCategoryNumber The number to filter the deletion by.
     * @return The number of records effected. 0 For none.
     */
    @Override
    public int removeMany(int bookmarkCategoryNumber){
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM bookmark_types "
                    + "WHERE bookmarkCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmarkCategoryNumber);

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
}
