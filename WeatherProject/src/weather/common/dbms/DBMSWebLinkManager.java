package weather.common.dbms;

import java.util.Vector;
import weather.common.data.WebLink;
import weather.common.data.WebLinkCategories;

/**
 * This class manages the WebLinks table in the database.  General and Forecast
 * links can be added, retrieved, and deleted from the database.
 *
 * @author Joe Sharp
 */
public interface DBMSWebLinkManager {

    /**
     * Retrieves all web links contained in the database and
     * returns them as an <code>ArrayList</code> of Links.
     *
     * @param category The type of links to return.
     * @return An <code>ArrayList</code> containing all of the web links contained
     * in the database.
     */
    public Vector<WebLink> getLinksForCategory(String category);

    /**
     * Deletes the <code>WebLink</code> related to the given URL from
     * the database.
     * 
     * @param URL The URL of the <code>WebLink</code> to delete.
     * @return True if a link with the given URL was removed from the database,
     * false otherwise.
     */
    public boolean deleteLink(String URL);

    /**
     * Adds the given name and URL into the database as a general 
     * <code>WebLink</code>.
     *
     * @param name The name by which this link will be displayed.
     * @param url The URL for this link.
     * @param category The category that this link will be shown under
     * @return true if a link with the given name, URL, and category  was added
     * to the database, false otherwise.
     */
    public boolean addLinkForCategory(String name, String url, String category);

    /**
     * Updates a given web link category.
     * @param oldCategory The web link to update in the database.
     */
    public boolean updateWebLinkCategory(WebLinkCategories oldCategory);

    /**
     * Adds the given web link category to the database. All web link categories
     * are stored in the web link categories.
     *
     * @param webLinkCategory The web link category to be added to the database.
     * @return True if the given category was added to the database, false
     * otherwise.
     */
    public boolean addWebLinkCategory(WebLinkCategories webLinkCategory);

    /**
     * Removes the given web link category from the database.
     *
     * @param webLinkCategory The web link category to be removed from the database.
     * @return True if the given category was removed, false otherwise.
     */
    public boolean removeLinkCategory(WebLinkCategories webLinkCategory);

    /**
     * Retrieves all web link categories from the database.
     * 
     * @return An ArrayList of all web link categories in the database.
     */
    public Vector<WebLinkCategories> obtainAllWebLinkCategories();

    /**
     * Gets all webLinks from the database.
     * @return A vector of all webLinks.
     */
     public Vector<WebLink> obtainAllWebLinks();

     /**
     * Gets a subset of webLinks based on a certain category.
     * @param webLinkCategory The webLinkCategory to match the subset against.
     * @return A vector of webLinks that match webLinkCategory.
     */
    public Vector<WebLink> obtainWebLinksFromACategory(WebLinkCategories webLinkCategory);

    /**
     * Updates a given webLink link that matches the links linkNumber.
     * @param link The link to update.
     * @return True if updated, false if not.
     */
    public boolean updateWebLink(WebLink link);
}
