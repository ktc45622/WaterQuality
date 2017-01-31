package weather.common.dbms;

import java.util.Vector;
import weather.common.data.AccessRights;
import weather.common.data.lesson.Lesson;

/**
 * This interface manages the lesson table of the database.
 * @author Justin Enslin (2012)
 * @version 2012
 */
public interface DBMSLessonManager {
    /**
     * Adds a new <code>Lesson</code> to the database, generating a new <code>Lesson</code> number in the
     * process.
     * 
     * @param lesson The <code>Lesson</code> to add to the database.
     * @return A <code>Lesson</code> object with updated primary key if successful, null otherwise.
     */
    public boolean add(Lesson lesson);
    /**
     * Updates an existing <code>Lesson</code> in the database.
     * 
     * @param lesson The <code>Lesson</code> to update.
     * @return True if this operation is successful, otherwise false.
     */
    public boolean update(Lesson lesson);
    /**
     * Deletes an existing <code>Lesson</code> from the database.
     * 
     * @param lesson The <code>Lesson</code> to remove.
     * @return True if this operation is successful, otherwise false.
     */
    public boolean delete(Lesson lesson);
    /**
     * Gets the <code>Lesson</code> represented by the given number.
     * 
     * @param lessonNumber A valid <code>Lesson</code> number.
     * @return The <code>Lesson</code> identified by the given lesson number.
     */
    public Lesson get(int lessonNumber);
    /**
     * Gets a list of all <code>Lesson</code> stored in the database.
     * 
     * @return A vector containing all <code>Lesson</code> in the database.
     */
    public Vector<Lesson> obtainAll();
    /**
     * Gets a list of all <code>Lesson</code> stored in the database that belong to the 
     * specified user.
     * 
     * @return A vector containing all <code>Lesson</code> in the database that belong to 
     * the specified user.
     */
    public Vector<Lesson> obtainByUser(int userNumber);
    /**
     * Gets a list of all <code>Lesson</code> stored in the database that belong to the 
     * specified category.
     * 
     * @return A vector containing all <code>Lesson</code> in the database that belong to 
     * the specified category.
     */
    public Vector<Lesson> obtainByCategory(int categoryNumber);
    /**
     * Gets a list of all <code>Lesson</code> stored in the database that have the 
     * specified access rights.
     * 
     * @return A vector containing all <code>Lesson</code> in the database that have the 
     * specified access rights.
     */
    public Vector<Lesson> obtainByAccessRights(AccessRights access);
}
