package weather.common.dbms;

import java.sql.Date;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.dbms.mysql.TableMetaData;
import weather.common.utilities.WeatherException;

/**
 * The <code>DBMSForecasterLessonManager</code> allows client classes to add,
 * remove, and update lessons stored in the database.
 *
 * @author Jeremy Benscoter (2014)
 * @author Mike Young (2014)
 */
public interface DBMSForecasterLessonManager {

    /**
     * Returns an <code>ArrayList</code> of all <code>ForecasterLessons</code>
     * in the database. If there are no <code>ForecasterLesson</code>s in the
     * database, the returned list will be empty.
     *
     * @return an <code>ArrayList</code> of all <code>ForecasterLessons</code>
     */
    public ArrayList<ForecasterLesson> getAllForecasterLessons();

    /**
     * Retrieves the <code>ForecasterLesson</code> associated with the provided
     * integer ID. If there is no <code>ForecasterLesson</code> associated with
     * the ID in the database, the returned lesson will have an lessonID of -1.
     *
     * @param id the unique identifier for the <code>ForecasterLesson</code>
     * @return a <code>ForecasterLesson</code> object representing the requested
     * lesson with up-to-date data, or a <code>ForecasterLesson</code> object
     * with a lessonID of -1 if not found
     */
    public ForecasterLesson getForecasterLesson(String id);

    /**
     * Creates a new <code>ForecasterLesson</code> in the database with the
     * attributes of the given <code>ForecasterLesson</code>. The given
     * <code>ForecasterLesson</code> object should have its
     * <code>lessonID</code> field set to -1. If the lesson is successfully
     * inserted into the database, an updated lesson object will be returned
     * with its <code>lessonID<code> field set to the auto-generated ID from the
     * database.  Otherwise, the <code>lessonID</code> field will contain -1.
     *
     * @param lesson the <code>ForecasterLesson</code> to be inserted into the
     * database
     * @return the <code>ForecasterLesson</code> with its <code>lessonID</code>
     * field set to the auto-generated ID, or to -1 if unsuccessful
     * @throws weather.common.utilities.WeatherException
     */
    public ForecasterLesson insertForecasterLesson(ForecasterLesson lesson)
            throws WeatherException;

    /**
     * Removes a <code>ForecasterLesson</code> from the database using the
     * <code>lessonID</code> of the given <code>ForecasterLesson</code> object.
     *
     * @param lesson the <code>ForecasterLesson</code> to be removed from the
     * database
     * @return the removed <code>ForecasterLesson</code> object with the 
     * lesson id set to -1
     */
    public ForecasterLesson removeForecasterLesson(ForecasterLesson lesson);

    /**
     * Updates a <code>ForecasterLesson</code> in the database using the
     * <code>lessonID</code> and attributes of the given
     * <code>ForecasterLesson</code> object.
     *
     * @param lesson the <code>ForecasterLesson</code> to be updated in the
     * database
     * @return true if the <code>ForecasterLesson</code> information in the
     * database was updated, false otherwise
     */
    public boolean updateForecasterLesson(ForecasterLesson lesson);

    /**
     * Provides an <code>ArrayList</code> of <code>ForecasterLessons</code> for
     * a specified course.  If no lessons are found, the list returned will be
     * empty.
     * @param courseNumber The course number of the course stored in the 
     * database.
     * @return An <code>ArrayList</code> of <code>ForecasterLesson</code>s.
     */
    public ArrayList<ForecasterLesson> getForecasterLessonsByCourse(int courseNumber);
    
    /**
     * Removes ForecasterLessons created before this date.
     *
     * @param date The date to remove from.
     * @return True if courses were removed, false otherwise.
     */
    public boolean removeForecasterLessonsBeforeDate(Date date);

    /**
     * Retrieves table meta data.
     *
     * @return A table meta data object for this table.
     */
    public TableMetaData getMetaData();
}
