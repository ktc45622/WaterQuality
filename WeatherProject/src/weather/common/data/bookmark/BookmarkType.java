package weather.common.data.bookmark;

/**
 * Represents a bookmark sub-category.
 * @author Joseph Horro
 * @version Spring 2011
 */
public class BookmarkType implements java.io.Serializable  {

    // fields match database columns.

    /**
     * Auto generated field, leave at -1 if inserting this object into the
     * database and the returned object will have the correct new number.
     */
    private int instanceTypeNumber;
    /**
     * FK that links bookmark_types with the bookmark_categories table to
     * define the category this bookmark belongs with.
     */
    private int categoryNumber;
    /**
     * The name of the bookmark.
     * Note: Varchar type in database, limit 50 characters.
     */
    private String name;
    /**
     * FK that links bookmark_types with the user that created it in the
     * Users table.
     */
    private int createdBy;
    /**
     * An enumeration for who can view this bookmark object.
     */
    private CategoryViewRights viewRights;
    /**
     * The notes for this bookmark.
     * Limited to 250 characters (Varchar(250)).
     * May be null.
     */
    private String notes;
    private int orderRank;

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Not necessary to include in first version of the class, but included here
     * as a reminder of its importance. Maintainers must change this value if
     * and only if the new version of this class is not compatible with old
     * versions.
     * 
     * @see <a href="http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html">Java specification for
     * serialization</a>
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for making a new BookmarkType. This constructor is meant to
     * be used with database information as the instanceTypeNumber is an auto
     * generated field.
     *
     * @param instanceTypeNumber  Auto generated field used as a primary key.
     * @param categoryNumber  FK that links bookmark_types with the
     *      bookmark_categories table to define the category this bookmark
     *      belongs with.
     * @param name Varchar type in database, limit 50 characters.
     * @param createdBy FK that links bookmark_types with the user that created
     *      it in the Users table.
     * @param viewRights An enumeration for who can view this bookmark object.
     * @param notes The notes for this bookmark, it may be null.
     */
    public BookmarkType(int instanceTypeNumber, int categoryNumber,
            String name, int createdBy, CategoryViewRights viewRights,
            String notes, int orderRank){
        this(categoryNumber, name, createdBy, viewRights, notes);
        this.orderRank = orderRank;
        this.instanceTypeNumber = instanceTypeNumber;
    }

    /**
     * Constructor for making a new BookmarkType. This constructor is meant to
     * be used in creating a new instance to insert into the database. It does
     * not require the auto generated instanceTypeNumber.
     *
     * @param categoryNumber  FK that links bookmark_types with the
     *      bookmark_categories table to define the category this bookmark
     *      belongs with.
     * @param name Varchar type in database, limit 50 characters.
     * @param createdBy FK that links bookmark_types with the user that created
     *      it in the Users table.
     * @param viewRights An enumeration for who can view this bookmark object.
     * @param notes The notes for this bookmark, it may be null.
     */
    public BookmarkType(int categoryNumber, String name, int createdBy,
            CategoryViewRights viewRights, String notes){
        this.instanceTypeNumber = -1;
        this.categoryNumber = categoryNumber;
        this.name = name;
        this.createdBy = createdBy;
        this.viewRights = viewRights;
        this.notes = notes;
    }


    /**
     * Compares bookmark types based on the instanceTypeNumber.
     *
     * @param obj The object to compare to this one
     * @return True if the given object is equal to this one, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final BookmarkType other = (BookmarkType) obj;

        return (instanceTypeNumber == other.instanceTypeNumber);
    }

    /**
     * Calculates the hash code value, which is just the instanceTypeNumber.
     *
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return (instanceTypeNumber);
    }

    /**
     * Gets the category number of the bookmark.
     * 
     * @return The category number of the bookmark.
     */
    public int getCategoryNumber() {
        return categoryNumber;
    }
    /**
     * Sets the category number of the bookmark.
     * 
     * @param categoryNumber The number to set as the category number.
     */
    public void setCategoryNumber(int categoryNumber) {
        this.categoryNumber = categoryNumber;
    }
    /**
     * Get the user that created this bookmark.
     * 
     * @return The number of the user who created this bookmark.
     */
    public int getCreatedBy() {
        return createdBy;
    }
    /**
     * Sets who created this bookmark.
     * 
     * @param createdBy The number of the user who created this bookmark.
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    /**
     * Gets the instance type number.
     * 
     * @return The instance type number.
     */
    public int getInstanceTypeNumber() {
        return instanceTypeNumber;
    }
    /**
     * Sets the instance type number.
     * 
     * @param instanceTypeNumber The number to set the instance type number.
     */
    public void setInstanceTypeNumber(int instanceTypeNumber) {
        this.instanceTypeNumber = instanceTypeNumber;
    }
    /**
     * Gets the name of the bookmark.
     * 
     * @return The name of the bookmark.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the bookmark.
     *      -Limited to 50 characters (Varchar(50))
     * 
     * @param name The String to set as the bookmarks name.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Gets the notes of the bookmark.
     * 
     * @return The notes of the bookmark.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * The maximum length cannot be greater than 250 characters.
     *
     * @param notes The notes to set for this bookmark type.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    /**
     * Get the view rights of the bookmark instance.
     * 
     * @return The view rights of this bookmark instance.
     */
    public CategoryViewRights getViewRights() {
        return viewRights;
    }
    /**
     * Sets the view rights of the bookmark instance.
     * 
     * @param viewRights The view rights of the bookmark instance. 
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
     * Returns a string representing this bookmark type(the name of this type).
     * @return A string representing this type.
     */
    @Override
    public String toString(){
        return getName();
    }
}
