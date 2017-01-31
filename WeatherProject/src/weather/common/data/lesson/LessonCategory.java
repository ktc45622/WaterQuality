package weather.common.data.lesson;

import java.io.Serializable;
import weather.common.data.AccessRights;
import weather.common.data.bookmark.CategoryViewRights;

/**
 *
 * @author Justin Gamble
 * @version Spring 2012
 */
public class LessonCategory implements Serializable{

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
     * The DBMS ID number for this LessonCategory.
     */
    private int lessonCategoryNumber;
    /**
     * The name of this LessonCategory.
     */
    private String categoryName;
    /**
     * Display position when in a list.
     */
    private int displayOrder;
    /**
     * The DBMS ID number for the Instructor who created this LessonCategory.
     */
    private int instructorNumber;
    /**
     * The AccessRights (See/Use) for this LessonCategory.
     */
    private CategoryViewRights accessRights;
    
    /**
     * Creates a LessonCategory with the provided data.
     * @param lessonCategoryNumber DBMS number for the LessonCategory.
     * @param lessonCategoryName Name for the LessonCategory.
     * @param instructorNumber DBMS InstructorNumber of the category creator.
     * @param accessRights See/Use access for the LessonCategory.
     * @param order Preferred display position in a list view.
     */
    public LessonCategory(int lessonCategoryNumber, String lessonCategoryName, int instructorNumber, CategoryViewRights accessRights, int order){
        this.lessonCategoryNumber = lessonCategoryNumber;
        this.categoryName = lessonCategoryName;
        this.displayOrder = order;
        this.accessRights = accessRights;
        this.instructorNumber = instructorNumber;
    }

    /**
     * Retrieves the AccessRights for this LessonCategory.
     * @return The AccessRights for this LessonCategory.
     */
    public CategoryViewRights getAccessRights() {
        return accessRights;
    }
    /**
     * Sets the AccessRights for this LessonCategory.
     * @param accessRights The new AccessRights for this LessonCategory.
     */
    public void setAccessRights(CategoryViewRights accessRights) {
        this.accessRights = accessRights;
    }
    /**
     * Returns the name of this LessonCategory.
     * @return The name of this LessonCategory.
     */
    public String getCategoryName() {
        return categoryName;
    }
    /**
     * Sets the name of this LessonCategory.
     * @param categoryName The new name of this LessonCategory.
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    /**
     * Returns the preferred display position of this LessonCategory.
     * @return The preferred display position of this LessonCategory.
     */
    public int getDisplayOrder() {
        return displayOrder;
    }
    /**
     * Sets the preferred display position of this LessonCategory.
     * @param displayOrder The new preferred display position of this LessonCategory.
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
    /**
     * Returns The DBMS ID number of the instructor who created this category.
     * @return The instructorNumber of the creator of this LessonCategory.
     */
    public int getInstructorNumber() {
        return instructorNumber;
    }
    /**
     * Sets the ID number of the instructor who created this LessonCategory.
     * @param instructorNumber The DBMS ID number of the new creator of this LessonCategory.
     */
    public void setInstructorNumber(int instructorNumber) {
        this.instructorNumber = instructorNumber;
    }
    /**
     * Returns the DBMS ID number of this LessonCategory.
     * @return The LessonCategoryNumber of this LessonCategory.
     */
    public int getLessonCategoryNumber() {
        return lessonCategoryNumber;
    }
    /**
     * Sets the LessonCategoryNumber of this LessonCategory.
     * @param lessonCategoryNumber The new LessonCategoryNumber for this LessonCategory.
     */
    public void setLessonCategoryNumber(int lessonCategoryNumber) {
        this.lessonCategoryNumber = lessonCategoryNumber;
    }
    
    /**
     * Calculates the hash code value, which is just the DBMS number.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return (lessonCategoryNumber);
    }

    /**
     * Compares LessonCategories based on the DBMS number.
     *
     * @param obj The object to compare to this one.
     * @return True if the given object is equal to this one, false
     * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final LessonCategory other = (LessonCategory) obj;

        return (lessonCategoryNumber == other.getLessonCategoryNumber());
    }
    /**
     * Gets the name of the LessonCategory.
     * 
     * @return The name of the LessonCategory.
     */
    public String getName() {
        return (categoryName);
    }
    
    
    
}
