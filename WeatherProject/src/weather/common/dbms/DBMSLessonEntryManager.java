package weather.common.dbms;

import java.util.Vector;
import weather.common.data.AccessRights;
import weather.common.data.lesson.LessonEntry;

/**
 *
 * @author Nicole Burfeind
 * @version Spring 2012
 */
public interface DBMSLessonEntryManager {
    
    /**
     * Adds a new <code>LessonEntry</code> to the database.
     * 
     * @param entry The <code>LessonEntry</code> to add to the database.
     * @return A <code>LessonEntry</code> object with updated primary key if successful, null otherwise
     */
    public boolean add(LessonEntry entry);
    
    /**
     * Updates an existing <code>LessonEntry</code> in the database.
     * 
     * @param entry The <code>LessonEntry</code> to update.
     * @return True if the update is successful, false otherwise.
     */
    public boolean update(LessonEntry entry);
    
    /**
     * Deletes an existing <code>LessonEntry</code> from the database.
     * 
     * @param entry The <code>LessonEntry</code> to delete.
     * @return True if the delete is successful, false otherwise.
     */
    public boolean delete(LessonEntry entry);
    
    /**
     * Gets the <code>LessonEntry</code> represented by the given number.
     * 
     * @param lessonEntryNumber A valid <code>LessonEntry</code> number.
     * @return The <code>LessonEntry</code> identified by the given <code>LessonEntry</code> number.
     */
    public LessonEntry get(int lessonEntryNumber);
    
    /**
     * Gets a list of all <code>LessonEntry</code> stored in the database.
     * 
     * @return A vector containing all <code>LessonEntry</code> in the database.
     */
    public Vector<LessonEntry> obtainAll();
    
    /**
     * Gets a list of all <code>LessonEntry</code> stored in the database that belong to the 
     * specified user.
     * 
     * @return A vector containing all <code>LessonEntry</code> in the database that belong to 
     * the specified user.
     */
    public Vector<LessonEntry> obtainByUser(int userNumber);
    
    /**
     * Gets a list of all <code>LessonEntry</code> stored in the database that belong to the 
     * specified category.
     * 
     * @return A vector containing all <code>LessonEntry</code> in the database that belong to 
     * the specified category.
     */
    public Vector<LessonEntry> obtainByCategory(int categoryNumber);
    
    /**
     * Gets a list of all <code>LessonEntry</code> stored in the database that have the 
     * specified access rights.
     * 
     * @param access The <code>AccessRights</code> to the specific <code>LessonEntry</code>.
     * @return A vector containing all <code>LessonEntry</code> in the database that have the 
     * specified access rights.
     */
    public Vector<LessonEntry> obtainByAccessRights(AccessRights access);
    
    /**
     * Gets a list of all <code>LessonEntry</code> objects stored in the database that 
     * have that lessonNumber.
     * @param lessonNumber The <code>Lesson</code> number to that specific <code>LessonEntry</code>.
     * @return A <code>Vector</code> of <code>LessonEntry</code> objects in the database with
     * that lessonNumber.
     */
    public Vector<LessonEntry> obtainByLessonNumber(int lessonNumber);
}
