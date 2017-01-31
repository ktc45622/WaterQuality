package weather.common.dbms;

import java.util.ArrayList;
import weather.common.data.User;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;

/**
 * Manages the <code>Attempts</code> in the database.
 * @author jbenscoter
 */
public interface DBMSForecasterAttemptManager {
    /**
     * Inserts a new <code>Attempt</code> into the database.
     * @param a The <code>Attempt</code> to be inserted.
     * @return The <code>Attempt</code> object with the id
     * from the database.
     */
    public Attempt insertAttempt(Attempt a, ForecasterLesson fl);
    /**
     * Updates a <code>Attempt</code> in the database.
     * @param a The <code>Attempt</code> to be updated in the database.
     * @return True if successful, false otherwise.
     */
    public Attempt updateAttempt(Attempt a);
    
    /**
     * Removes an <code>Attempt</code> from the database.
     * @param a The <code>Attempt</code> to be removed from the database.
     * @return True if attempt was removed.
     */
    public boolean removeAttempt(Attempt a);
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database.
     * @return An <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database.
     */
    public ArrayList<Attempt> getAllAttempts();
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database that belong to a specific <code>User</code>
     * @param u The <code>User</code> to lookup the <code>Attempts</code> for.
     * @return An <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database that belong to the specified <code>User</code>
     */
    public ArrayList<Attempt> getAttempts(User u);
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database that belong to a specific <code>ForecasterLesson</code>
     * @param fl The <code>ForecasterLesson</code> to lookup the 
     * <code>Attempts</code> for.
     * @return An <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database that belong to the specified <code>ForecasterLesson</code>
     */
    public ArrayList<Attempt> getAttempts(ForecasterLesson fl);
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database that belong to a specific <code>ForecasterLesson</code>
     * @param fl The <code>ForecasterLesson</code> to lookup the 
     * <code>Attempts</code> for.
     * @param u The <code>User</code> to lookup the <code>Attempts</code> for.
     * @return An <code>ArrayList</code> containing all the <code>Attempts</code>
     * in the database that belong to the specified <code>ForecasterLesson</code>
     * and <code>User</code>
     */
    public ArrayList<Attempt> getAttempts(ForecasterLesson fl, User u);
}
