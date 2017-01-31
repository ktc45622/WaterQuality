package weather.common.data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class is a thin data wrapper for a webLinkCategory row in the database.
 *
 * @author Joseph Horro
 * @version Spring 2011
 */
public class WebLinkCategories implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    /**
     * The primary key, linkCategoryNumber which links this table to the webLink
     * table.
     */
    private int linkCategoryNumber;
    /**
     * The name of some category, used to dynamically build the web links menu.
     */
    private String linkCategory;
    /**
     * This field is used to order the web links in drop down and other lists.
     */
    private int orderRank;

    /**
     * Constructor used when all data is known. Typically used when data is pulled
     * from the database.
     *
     * @param linkCategoryNumber The number for that specific <code>WebLink</code> category.
     * @param linkCategory A string representing this linkCategory.
     * @param orderRank The orderRank of the category.
     */
    public WebLinkCategories(int linkCategoryNumber, String linkCategory, int orderRank) {
        this.linkCategoryNumber = linkCategoryNumber;
        this.linkCategory = linkCategory;
        this.orderRank = orderRank;
    }

    /**
     * This constructor is meant to be used with new link Categories. Build this
     * with a name for the category and use the addLinkCategory method in
     * MySQLWebLinkManager, leave the link number as -1 (default).
     *
     * @param linkCategory The <code>WebLink</code> category.
     */
    public WebLinkCategories(String linkCategory) {
        this.linkCategory = linkCategory;
        this.linkCategoryNumber = -1;
    }

    /**
     * Gets this linkCategory.
     *
     * @return A string representing this linkCategory.
     */
    public String getLinkCategory() {
        return linkCategory;
    }

    /**
     * Sets the linkCategory.
     *
     * @param linkCategory The string containing the new linkCategory.
     */
    public void setLinkCategory(String linkCategory) {
        this.linkCategory = linkCategory;
    }

    /**
     * Gets the linkCategoryNumber.
     *
     * @return The linkCategoryNumber.
     */
    public int getLinkCategoryNumber() {
        return linkCategoryNumber;
    }

    /**
     * Sets the linkCategoryNumber.
     *
     * @param linkCategoryNumber The linkCategoryNumber.
     */
    public void setLinkCategoryNumber(int linkCategoryNumber) {
        this.linkCategoryNumber = linkCategoryNumber;
    }

    /**
     * Gets the orderRank for this web link.
     *
     * @return The order rank.
     */
    public int getOrderRank() {
        return orderRank;
    }

    /**
     * Sets the orderRank for this web link.
     *
     * @param orderRank The new order rank.
     */
    public void setOrderRank(int orderRank) {
        this.orderRank = orderRank;
    }

    /**
     * Determines whether the given object is equal to this WebLinkCategory.
     *
     * @param obj The object to compare to this WebLink category.
     * @return True if the given object is equal to this WebLink category, false
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
        final WebLinkCategories other = (WebLinkCategories) obj;
        return (linkCategoryNumber == other.linkCategoryNumber);
    }

    /**
     * Returns the WebLinkCategory number representing the hash code.
     *
     * @return The number of this WebLinkCategory.
     */
    @Override
    public int hashCode() {
        return (linkCategoryNumber);
    }

    /**
     * Compares the linkCategoryNumber of one entry to another. If the
     * linkCategoryNumber of this entry is less than the linkCategoryNumber of
     * the other one, -1 is returned. If the linkCategoryNumber of this entry is
     * greater than the linkCategoryNumber of the other one, 1 is returned. If
     * they are equal, 0 is returned.
     * TODO: does the javadoc name make more sense?
     * @param o The entry to compare to.
     * @return -1, 1, or 0 based on how the two linkNumber's compare.
     */
    @Override
    public int compareTo(Object o) {
        WebLinkCategories cmpCategory = (WebLinkCategories) o;
        if (linkCategoryNumber < cmpCategory.getLinkCategoryNumber()) {
            return -1;
        }
        if (linkCategoryNumber > cmpCategory.getLinkCategoryNumber()) {
            return 1;
        }
        return 0;
    }

    /**
     * Comparator class for use in sorting collections by order rank.
     */
    public static class WebLinkCategoriesOrderRankComparator implements Comparator{
        @Override
        public int compare(Object obj1, Object obj2){
            int rank1 = ((WebLinkCategories)obj1).getOrderRank();
            int rank2 = ((WebLinkCategories)obj2).getOrderRank();

            if(rank1 > rank2) return 1;
            else if(rank1 < rank2) return -1;
            else return 0;
        }
    }

}
