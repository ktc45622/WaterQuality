package weather.common.data;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.logging.Level;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * This class represents a web link.  It contains the name that is associated
 * with the link, the links URL, and the type of the link.  The link types are
 * general web links (LINK) and forecast web links (FORECAST).  The URL is
 * stored as a string, but there is a method getURL() that first converts the
 * string into a URL object and returns that object.
 *
 * @author Joe Sharp (2009)
 * @author Ioulia Lee (2010)
 * @author Joseph Horro (2011)
 *          - renamed several properties. Updated get set methods.
 * @version Spring 2010
 * @version Spring 2010
 */
public class WebLink implements Serializable, Comparable {

    private static final long serialVersionUID = 1;
    private int linkNumber;
    private String name;
    private String URL;
    private WebLinkType type;
    private int linkCategoryNumber;
    private int orderRank;

    /**
     * Constructs a new instance of WebLink with the given parameters.  URL must
     * be a valid URL in order for the class to work correctly.
     * @param linkName The name to be used to refer to this link.
     * @param linkURL The URL of this link.
     * @param linkType The type of this link (LINK, FORECAST).
     * @param linkCategoryNumber The number of a category this link belong to.
     */
    public WebLink(String linkName, String linkURL, WebLinkType linkType,
            int linkCategoryNumber) {
        this.name = linkName;
        this.URL = linkURL;
        this.type = linkType;
        this.linkCategoryNumber = linkCategoryNumber;

    }

    /**
     * Constructs a new instance of WebLink with the given parameters.  URL must
     * be a valid URL in order for the class to work correctly. This method is meant
     * to be used when pulling from the database.
     * @param linkName The name to be used to refer to this link.
     * @param linkURL The URL of this link.
     * @param linkType The type of this link (LINK, FORECAST).
     * @param linkCategoryNumber The number of a category this link belong to.
     */
    public WebLink(int linkNumber, String linkName, String linkURL, WebLinkType linkType,
            int linkCategoryNumber, int orderRank) {
        this.linkNumber = linkNumber;
        this.name = linkName;
        this.URL = linkURL;
        this.type = linkType;
        this.linkCategoryNumber = linkCategoryNumber;
        this.orderRank = orderRank;
    }

    /**
     * Gets the type of this link.
     *
     * @return The type of this link.
     */
    public WebLinkType getType() {
        return type;
    }

    /**
     * Gets the name of this link.
     *
     * @return The name of this link.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the URL of this link.
     *
     * @return A string representation of the URL.
     */
    public String getURLString() {
        return URL;
    }

    /**
     * Gets the URL for this link.
     *
     * @return The URL as a URL object.
     */
    public URL getURL() {
        URL url = null;
        try {
            url = new URL(this.URL);
        } catch (MalformedURLException e) {
            WeatherLogger.log(Level.SEVERE, "MalformedURLException is thrown "
                    + "while trying to retrieve URL for this WebLink.", e);
            String str = "\nThe URL for the web link with name "
                    + this.getName() + " is not formed correctly.";
            new WeatherException(3015, e, str).show();
        }
        return url;
    }

    /**
     * Gets the linkCategoryNumber for this link.
     *
     * @return The <code>WebLink</code> category number.
     */
    public int getLinkCategoryNumber() {
        return linkCategoryNumber;
    }

    /**
     * Sets the name of this link.
     *
     * @param name The name of the link.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets this links URL.
     *
     * @param url The URL of this link.
     */
    public void setURL(String url) {
        this.URL = url;
    }

    /**
     * Sets the type of this link.
     *
     * @param type The type of this link.
     */
    public void setType(WebLinkType type) {
        this.type = type;
    }

    /**
     * Sets the link category number for this link.
     *
     * @param linkCategoryNumber The number of a category this link belong to.
     */
    public void setLinkCategoryNumber(int linkCategoryNumber) {
        this.linkCategoryNumber = linkCategoryNumber;
    }

    /**
     * Gets the linkNumber of this webLink.
     *
     * @return The link number of this webLink.
     */
    public int getLinkNumber() {
        return linkNumber;
    }

    /**
     * Sets the link number of this webLink.
     *
     * @param linkNumber The linkNumber to be set to this webLink.
     */
    public void setLinkNumber(int linkNumber) {
        this.linkNumber = linkNumber;
    }

    /**
     * Gets the order rank for this web link.
     *
     * @return The order rank.
     */
    public int getOrderRank() {
        return orderRank;
    }

    /**
     * Sets the order rank for this web link.
     *
     * @param orderRank The new order rank.
     */
    public void setOrderRank(int orderRank) {
        this.orderRank = orderRank;
    }

    /**
     * Determines whether the given object is equal to this WebLink.
     *
     * @param obj The object to compare to this WebLink.
     * @return True if the given object is equal to this Course, false
     * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebLink other = (WebLink) obj;
        return (linkNumber == other.linkNumber);
    }

    /**
     * Returns the WebLink number representing the hash code.
     *
     * @return The number of this course.
     */
    @Override
    public int hashCode() {
        return (linkNumber);
    }

    /**
     * Compares the linkNumber of one entry to another. If the linkNumber of
     * this entry is less than the linkNumber of the other one, -1 is returned.
     * If the linkNumber of this entry is greater than the linkNumber of the
     * other one, 1 is returned. If they are equal, 0 is returned.
     *
     * @param o The entry to compare to.
     * @return -1, 1, or 0 based on how the two linkNumber's compare.
     */
    @Override
    public int compareTo(Object o) {
        WebLink cmpLink = (WebLink) o;
        if (linkNumber < cmpLink.getLinkNumber()) {
            return -1;
        }
        if (linkNumber > cmpLink.getLinkNumber()) {
            return 1;
        }
        return 0;
    }
}
