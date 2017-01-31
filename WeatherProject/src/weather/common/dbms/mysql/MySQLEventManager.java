package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.dbms.DBMSArg;
import weather.common.dbms.DBMSArgType;
import weather.common.dbms.DBMSEventManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * Manages events that happen in the system for a MySQL database.
 *
 * Right now, only Bookmark category events are handled.
 *
 */

public class MySQLEventManager implements DBMSEventManager {
    // MySQL DBMS.

    private MySQLImpl dbms;

    /**
     * Assign DBMS where events are stored.
     * 
     * @param dbms DBMS to use.
     */
    public MySQLEventManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Add a bookmark category.
     *
     * @param bookmark A bookmark object that is to be added to the database.
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean addBookmarkCategory(BookmarkCategory bookmark) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;
        String sql;

        try {
            sql = "INSERT INTO bookmark_categories (name, note) VALUES (?, ?)";
            conn = (Connection) dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, bookmark.getName());
            ps.setString(2, bookmark.getNotes());

            int key = MySQLHelper.executeStatementAndReturnGeneratedKey(ps);
            if (key > 0) {
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
     * Delete a bookmark category and all associated bookmarks.
     *
     * @param bookmark A bookmark category object to delete
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean deleteBookmarkCategory(BookmarkCategory bookmark) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;

        try {
            String sql = "DELETE FROM bookmark_categories WHERE bookmarkCategoryNumber = ?";
            conn = (Connection) dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookmark.getBookmarkCategoryNumber());
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
     * Update a bookmark category.
     *
     * @param bookmark A bookmark category object to update.
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean updateBookmarkCategory(BookmarkCategory bookmark) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessful = false;

        try {
            String sql = "UPDATE bookmark_categories SET "
                + "name = ?, "
                + "notes = ?, "
                + "orderRank = ? "
                + "WHERE bookmarkCategoryNumber = ?";

            conn = (Connection) dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, bookmark.getName());
            ps.setString(2, bookmark.getNotes());
            ps.setInt(3, bookmark.getOrderRank());
            ps.setInt(4,bookmark.getBookmarkCategoryNumber());
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
        return (isSuccessful);
    }

    /**
     * Get all bookmark categories.
     *
     * @return A vector of all bookmark categories, or null if none are found.
     */
    @Override
    public Vector<BookmarkCategory> getAllBookmarkCategories() {
        Vector<BookmarkCategory> categories = new Vector<>();
        ResultSet rs = null;
        Connection conn = null;

        try {
            String sql = "SELECT * FROM bookmark_categories ORDER BY orderRank ASC";
            conn = (Connection)dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                BookmarkCategory b = MySQLHelper.makeBookmarkCategoryFromResultSet(rs);
                categories.add(b);
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to execute an SQL statement. There may be an " +
                    "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested " +
                    "operation due to an internal problem.").show();
        } finally {
            MySQLHelper.closeResultSet(rs);
        }
      return (categories);
    }

    /**
     * Get bookmark category by number.
     *
     * @param num The <code>BookmarkCategory</code> number.
     * @return The <code>BookmarkCategory</code> with specified number, or null if not found.
     */
    @Override
    public BookmarkCategory getBookmarkCategoryByNumber(int num) {
        String sql = "SELECT * FROM bookmark_categories WHERE bookmarkCategoryNumber = ?";
        Vector<BookmarkCategory> categories;
        BookmarkCategory category = null;

        Vector<DBMSArg> args = new Vector<DBMSArg>();
        args.add(new DBMSArg(num, DBMSArgType.INT));

        categories = queryBookmarkCategory(sql, args);

        if (categories != null) {
            category = categories.get(0);
        }

        return (category);
    }

    /**
     * Get bookmark category by name.
     * 
     * @param name The <code>BookmarkCategory</code> name.
     * @return The <code>BookmarkCategory</code> with specified name.
     */
    @Override
    public BookmarkCategory getBookmarkCategoryByName(String name) {
        String sql = "SELECT * FROM bookmark_categories WHERE name = ?";
        Vector<BookmarkCategory> categories;
        BookmarkCategory category = null;

        Vector<DBMSArg> args = new Vector<DBMSArg>();
        args.add(new DBMSArg(name, DBMSArgType.STRING));

        categories = queryBookmarkCategory(sql, args);

        if (categories != null) {
            category = categories.get(0);
        }

        return (category);
    }

    private Vector<BookmarkCategory> queryBookmarkCategory(String sql, Vector<DBMSArg> args) {
        MySQLQuery query;
        ResultSet rs;
        Vector<BookmarkCategory> list;

        list = new Vector<BookmarkCategory>();

        query = new MySQLQuery(dbms, sql, args);


        try {
            query.execute();
            rs = query.getResultSet();

            // Add results to the list.
            while (rs.next()) {
                // Make another bookmark from the resultset.
                list.add(MySQLHelper.makeBookmarkCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            query.handleSQLException(e);
        } finally {
            query.close();
        }

        return (list);
    }
}
