package weather.common.data.bookmark;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Represents a bookmark category for storing bookmarks.
 *
 * A bookmark is a weather phenomenon that happens or can be viewed at a single
 * instance in time. An image of a cloud would be an example of a weather
 * phenomenon we would bookmark.
 *
 * A weather event is related, but requires a range of time to specify or view.
 *
 * @author Eric Subach (2010)
 * @author Joseph Horro (2011 - spring)
 * @version Spring 2011
 */
public class BookmarkCategory implements Serializable {

    // Unique category number.
    private int bookmarkCategoryNumber;
    private String name;
    private String notes;
    private int createdBy;
    private CategoryViewRights viewRights;
    private BookmarkDuration alternative;
    private int orderRank;
    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Not necessary to include in first version of the class, but included here
     * as a reminder of its importance. Maintainers must change this value if
     * and only if the new version of this class is not compatible with old
     * versions.
     * 
     * @see <a href="http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html">Sun documentation for serialization
     * for details.</a>
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a BookmarkCategory. All fields are set to default, or unspecified.
     */
    public BookmarkCategory() {
        this(-1, "Not Specified", -1,
                CategoryViewRights.instructor_only,
                BookmarkDuration.instance,
                null, -1);
    }

    /**
     * Create a BookmarkCategory.
     *
     * @param number The primary key for the category that is auto incremented.
     * @param name The name of category.
     * @param createdBy The userNumber of the user who created this bookmark.
     * @param viewRights The view rights for this bookmark object. Uses an enumeration.
     * @param alternative The type of event, for example instance or event.
     * @param notes The notes about this category.
     * @param orderRank The order rank.
     */
    public BookmarkCategory(int number, String name, int createdBy,
            CategoryViewRights viewRights,
            BookmarkDuration alternative, String notes, int orderRank) {
        this.bookmarkCategoryNumber = number;
        this.name = name;
        this.createdBy = createdBy;
        this.viewRights = viewRights;
        this.alternative = alternative;
        this.notes = notes;
        this.orderRank = orderRank;
    }

    /**
     * Create a BookmarkCategory without a bookmarkCategoryNumber - USE this method
     * for creating a bookmark to insert into the database.
     *
     * @param name The name of category.
     * @param createdBy The userNumber of the user who created this bookmark.
     * @param viewRights The view rights for this bookmark object. Uses an
     *      enumeration.
     * @param alternative The type of event, for example instance or event.
     * @param notes The notes about this category.
     */
    public BookmarkCategory(String name, int createdBy,
            CategoryViewRights viewRights,
            BookmarkDuration alternative, String notes) {
        this.bookmarkCategoryNumber = -1;
        this.name = name;
        this.createdBy = createdBy;
        this.viewRights = viewRights;
        this.alternative = alternative;
        this.notes = notes;
    }

    /**
     * Calculates the hash code value, which is just the category number.
     *
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return (bookmarkCategoryNumber);
    }

    /**
     * Compares bookmark categories based on the category number.
     *
     * @param obj The object to compare to the current one.
     * @return True if the given object is equal to this one, false
     * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final BookmarkCategory other = (BookmarkCategory) obj;

        return (bookmarkCategoryNumber == other.bookmarkCategoryNumber);
    }
    /**
     * Gets the name of the bookmark category.
     * 
     * @return The name of the bookmark category.
     */
    public String getName() {
        return (name);
    }
    /**
     * Sets the name of the bookmark category.
     * 
     * @param name The name to set as the bookmark categories name. 
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Gets the notes about the category.
     * 
     * @return The notes about the category. 
     */
    public String getNotes() {
        return (notes);
    }
    /**
     * Sets the notes about this category.
     * 
     * @param note The notes to set for the category.
     */
    public void setNotes(String note) {
        this.notes = note;
    }
    /**
     * Gets the number of the bookmark category.
     * 
     * @return The number of the bookmark category. 
     */
    public int getBookmarkCategoryNumber() {
        return (bookmarkCategoryNumber);
    }
    /**
     * Sets the bookmark categories number.
     * 
     * @param num The number to set as the bookmark categories number. 
     */
    public void setBookmarkCategoryNumber(int num) {
        this.bookmarkCategoryNumber = num;
    }
    /**
     * Gets the event type for the bookmark category.
     * 
     * @return The event type for the bookmark category.
     */
    public BookmarkDuration getAlternative() {
        return alternative;
    }
    /**
     * Sets the event type for the bookmark category.
     * 
     * @param alternative The event type for the bookmark category.
     */
    public void setAlternative(BookmarkDuration alternative) {
        this.alternative = alternative;
    }
    /**
     * Gets the user number of the user who created this bookmark.
     * 
     * @return The user number of the user who created this bookmark. 
     */
    public int getCreatedBy() {
        return createdBy;
    }
    /**
     * Sets the user number of the user who created this bookmark.
     * 
     * @param createdBy The user number of the user who created this bookmark.
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    /**
     * Gets the view rights for this bookmark.
     * 
     * @return The view rights for this bookmark.
     */
    public CategoryViewRights getViewRights() {
        return viewRights;
    }
    /**
     * Sets the view rights for this bookmark.
     * 
     * @param viewRights The view rights for this bookmark.
     */
    public void setViewRights(CategoryViewRights viewRights) {
        this.viewRights = viewRights;
    }
    /**
     * Gets the order rank.
     * 
     * @return The order rank.
     */
    public int getOrderRank() {
        return orderRank;
    }
    /**
     * Sets the order rank.
     * 
     * @param orderRank The order rank to be set. 
     */
    public void setOrderRank(int orderRank) {
        this.orderRank = orderRank;
    }
    
    /**
     * Returns a string representing this bookmark category(the name of this 
     * category).
     * @return A string representing this category.
     */
    @Override
    public String toString(){
        return getName();
    }
}
