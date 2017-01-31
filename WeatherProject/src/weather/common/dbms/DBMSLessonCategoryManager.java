package weather.common.dbms;

import java.util.Vector;
import weather.common.data.lesson.LessonCategory;

/**
 * This interface manages the lesson_category table of the database.
 * @author Justin Enslin
 */
public interface DBMSLessonCategoryManager {
    /**
     * Adds a <code>LessonCategory</code> to the database, generating a new category number 
     * in the process.
     * 
     * @param category The <code>LessonCategory</code> to add.
     * @return A <code>LessonCategory</code> object with updated primary key if successful, null otherwise.
     */
    public boolean add(LessonCategory category);
    /**
     * Updates an existing <code>LessonCategory</code> in the database.
     * 
     * @param category The <code>LessonCategory</code> to update.
     * @return True if this operation is successful, otherwise false.
     */
    public boolean update(LessonCategory category);
    /**
     * Deletes a <code>LessonCategory</code> category from the database.
     * 
     * @param category The <code>LessonCategory</code> to remove.
     * @return True if this operation is successful, otherwise false.
     */
    public boolean delete(LessonCategory category);
    
    /**
     * Gets the <code>LessonCategory</code> represented by the given number.
     * 
     * @param categoryNumber A valid <code>LessonCategory</code> number.
     * @return The <code>LessonCategory</code> identified by the given category number.
     */
    public LessonCategory get(int categoryNumber);
    
    /**
     * Gets a list of all lesson categories stored in the database.
     * 
     * @return A vector containing all lesson categories in the database.
     */
    public Vector<LessonCategory> obtainAll();
    
    public Vector<LessonCategory> obtainViewableBy(int userNumber); 
}
