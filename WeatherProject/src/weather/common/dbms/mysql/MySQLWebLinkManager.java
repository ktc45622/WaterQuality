package weather.common.dbms.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import weather.common.data.WebLink;
import weather.common.data.WebLinkCategories;
import weather.common.dbms.DBMSWebLinkManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * The <code>MySQLWebLinkManager</code> class interacts with the WebLinks table.
 * It allows to maintain web links in the database. An instance of this class
 * can be created and used in the following way:
 * <pre>
 *  DBMSSystem dbms;
 *  dbms = adminService.getGeneralService().getDBMSSystem();
 *  webLinkManager = new MySQLWebLinkManager((MySQLImpl)dbms);
 *  webLinkManager.deleteLink(urlName.getText());
 * </pre>
 *
 * @author jjsharp
 * @author Ioulia Lee (2010)
 * @author Joseph Horro (2011)
 * @version Spring 2010
 * @version Spring 2011
 */
public class MySQLWebLinkManager implements DBMSWebLinkManager {

    /**
     * This object's DatabaseManagementSystem instance.
     */
    private MySQLImpl dbms;

    public MySQLWebLinkManager(MySQLImpl dbms) {
        this.dbms = dbms;
    }

    /**
     * Deletes a link with the given URL from the database.
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param url The URL of the link to delete.
     * @return True if a link with the given URL was removed from the database,
     * false otherwise.
     */
    @Override
    public boolean deleteLink(String url) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        try {
            // A cascade type delete.
            String sql = "DELETE FROM weblinks WHERE URL = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, url);

            // if the update was successfull, test wether any rows were affected,
            // if not, nothing was deleted, return false
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 0) {
                    isSuccessfull = false;
                } else {
                    isSuccessfull = true;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return isSuccessfull;
    }

    /**
     * Adds a record of a link with the given name, URL and category to
     * the <code>WebLink</code>s table.
     * 
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * 
     * @param name The name of the link to add.
     * @param url The URL of the link to add.
     * @param category The category of the link to add.
     * @return True if a link with the given name, URL, and category  was added
     * to the database, false otherwise.
     */
    @Override
    public boolean addLinkForCategory(String name, String url, String category) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        Statement stmt = null;
        ResultSet rs = null;
        int orderRank = 0;
        try {
            conn = dbms.getLocalConnection();
            stmt = conn.createStatement();
            // first find the max value of orderRank, and give this web link
            // a value of one greater - if there are none, the first web link
            // will have a rank of 0
            String sql = "SELECT MAX(orderRank) FROM weblinks";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                orderRank = rs.getInt("MAX(orderRank)") + 1;
            }
            sql = "INSERT INTO weblinks (name, URL, linkCategoryNumber) "
                    + "VALUES ( (?), (?), (SELECT linkCategoryNumber FROM weblink_categories "
                    + "WHERE linkCategory = (?) ),? )";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, url);
            ps.setString(3, category);
            ps.setInt(4, orderRank);
            if (ps.executeUpdate() == 1) {
                isSuccessfull = true;
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
        return isSuccessfull;
    }

    /**
     * Retrieves web links of the given category, arranges them in an
     * <code>ArrayList</code> and returns it.
     * 
     * Catches <code>SQLException</code> if there is an error in the SQL
     * statement. Logs <code>SQLException</code> in the log file, creates a new
     * <code>WeatherException</code> and displays it to a user.
     * Note: this method exists to aid in using drop down lists that store strings.
     * 
     * @param category The type of web links to be returned.
     * @return An <code>ArrayList</code> of <code>WebLink</code> objects
     * representing web links for the given category.
     */
    @Override
    public Vector<WebLink> getLinksForCategory(String category) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector<WebLink> links = new Vector<WebLink>();
        try {
            String sql = "SELECT weblinks.* FROM weblinks, weblink_categories"
                    + " WHERE weblinks.linkCategoryNumber = weblink_categories.linkCategoryNumber"
                    + " AND  weblink_categories.linkCategory = (?) ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            rs = ps.executeQuery();
            while (rs.next()) {
                links.add(MySQLHelper.makeWebLinkFromResultSet(rs));
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
        return links;
    }

    /**
     * Adds the given web link category to the database. All web link categories
     * are stored in the webLink_categories. Updates webLinkCategory object with the
     * auto generated key, so the incoming object must have a key of -1.
     *
     * @param webLinkCategory The web link category to be added to the database.
     * @return True if the given category was added to the database, false
     * otherwise.
     */
    @Override
    public boolean addWebLinkCategory(WebLinkCategories webLinkCategory) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        int rows = 0;
        ResultSet rs = null;
        Statement stmt = null;
        int orderRank = 0;

        // this method is used to insert new objects. The auto generated field
        // should be set to -1 and will be updated and the new key will be updated
        // in the object.
        if (webLinkCategory.getLinkCategoryNumber() != -1) {
            return false;
        }

        try {
            conn = dbms.getLocalConnection();
            stmt = conn.createStatement();
            // first find the max value of orderRank, and give this web link categorie
            // a value of one greater - if there are none, the first web link categorie
            // will have a rank of 0
            String sql = "SELECT MAX(orderRank) FROM weblink_categories";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                orderRank = rs.getInt("MAX(orderRank)") + 1;
            }
            sql = "INSERT INTO weblink_categories(linkCategory, orderRank)"
                    + "VALUES (?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, webLinkCategory.getLinkCategory());
            ps.setInt(2, orderRank);
            rows = ps.executeUpdate();
            if (rows == 0) {
                // nothing updated.
                return false;
            }
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                isSuccessfull = true;
                webLinkCategory.setLinkCategoryNumber((Integer.parseInt(rs.getString(1))));
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return isSuccessfull;
    }

    /**
     * Removes the given web link category and all webLinks in that category
     * from the database. (Warning: effects 2 tables)
     *
     * @param webLinkCategory The web link category to be removed from the database.
     * @return True if the given category was removed, false otherwise.
     */
    @Override
    public boolean removeLinkCategory(WebLinkCategories webLinkCategory) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        try {
            // A cascade type delete.
            String sql = "DELETE FROM weblinks WHERE "
                    + "linkCategoryNumber = (?)";
            //Add one execute centence to delete weblinks and weblinkCategory
            String sql2 = "DELETE FROM weblink_categories WHERE linkCategoryNumber=(?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, webLinkCategory.getLinkCategoryNumber());

            // if the update was successfull, test wether any rows were affected,
            // if not, nothing was deleted, return false
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 0) {
                    isSuccessfull = false;
                } else {
                    isSuccessfull = true;
                }
            }
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, webLinkCategory.getLinkCategoryNumber());

            // if the update was successful, test whether any rows were affected,
            // if not, nothing was deleted, return false
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 0) {
                    isSuccessfull = false;
                } else {
                    isSuccessfull = true;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return isSuccessfull;
    }

    /**
     * Retrieves all web link categories from the database.
     *
     * @return An ArrayList of all web link categories in the database.
     */
    @Override
    public Vector<WebLinkCategories> obtainAllWebLinkCategories() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<WebLinkCategories> categories = new Vector<WebLinkCategories>();
        try {
            String sql = "SELECT * FROM weblink_categories ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                categories.add(MySQLHelper.makeWebLinkCategoryFromResultSet(rs));
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
        return categories;
    }

    /**
     * Gets all webLinks from the database.
     * @return A vector of all webLinks.
     */
    @Override
    public Vector<WebLink> obtainAllWebLinks() {
        Connection conn = null;
        ResultSet rs = null;
        Vector<WebLink> categories = new Vector<WebLink>();
        try {
            String sql = "SELECT * FROM weblinks ORDER BY orderRank ASC";
            conn = dbms.getLocalConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                categories.add(MySQLHelper.makeWebLinkFromResultSet(rs));
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
        return categories;
    }

    /**
     * Updates a given web link category.
     * @param webLinkCategory The web link to update in the database.
     */
    @Override
    public boolean updateWebLinkCategory(WebLinkCategories webLinkCategory) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccess = false;
        try {
            conn = dbms.getLocalConnection();
            String sql = "UPDATE weblink_categories SET linkCategory  = (?), "
                    + "orderRank = (?) "
                    + "WHERE linkCategoryNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, webLinkCategory.getLinkCategory());
            ps.setInt(2,webLinkCategory.getOrderRank());
            ps.setInt(3, webLinkCategory.getLinkCategoryNumber());

            // Check to make sure one was acctually updated
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 1) {
                    isSuccess = true;
                }
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
        return isSuccess;
    }

    /**
     * Gets a subset of webLinks based on a certain category.
     * @param webLinkCategory The webLinkCategory to match the subset against.
     * @return A vector of webLinks that match webLinkCategory.
     */
    @Override
    public Vector<WebLink> obtainWebLinksFromACategory(WebLinkCategories webLinkCategory) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<WebLink> categories = new Vector<WebLink>();
        try {
            String sql = "SELECT * FROM weblinks WHERE linkNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, webLinkCategory.getLinkCategoryNumber());
            rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(MySQLHelper.makeWebLinkFromResultSet(rs));
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
        return categories;
    }

    /**
     * Updates a given webLink link that matches the links linkNumber.
     * @param link The link to update.
     * @return True if updated, false if not.
     */
    @Override
    public boolean updateWebLink(WebLink link) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isSuccessfull = false;
        try {
            String sql = "UPDATE weblinks SET  "
                    + "name = (?), URL = (?), type = (?), linkCategoryNumber = (?), "
                    + "orderRank = (?) "
                    + "WHERE linkNumber = (?)";
            conn = dbms.getLocalConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, link.getName());
            ps.setString(2, link.getURLString());
            ps.setString(3, link.getType().toString());
            ps.setInt(4, link.getLinkCategoryNumber());
            ps.setInt(5,link.getOrderRank());
            ps.setInt(6, link.getLinkNumber());

            // check the rows updated
            if (!ps.execute()) {
                if (ps.getUpdateCount() == 1) {
                    isSuccessfull = true;
                }
            }
        } catch (SQLException e) {
            WeatherLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to execute an SQL statement. There may be an "
                    + "error in SQL syntax.", e);
            new WeatherException(0012, e, "Cannot complete the requested "
                    + "operation due to an internal problem.").show();
        }
        return isSuccessfull;
    }

}
