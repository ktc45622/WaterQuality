package weather.common.dbms;

import java.sql.Date;
import java.util.ArrayList;
import weather.common.data.User;
import weather.common.data.diary.DailyEntry;
import weather.common.data.resource.Resource;

/**
 * This interface is for managing daily diary entry database table.
 *
 * @author Bloomsburg University Software Engineering
 * @author Brian Bankes
 */

public interface DBMSDiaryManager {
    /**
     * Saves a diary entry to the database table. The database operation will be
     * un update if an entry is already present for the parameter's user, camera
     * resource and entry date or an update otherwise.
     * 
     * @param entry The diary entry.
     * @param author The author of the entry.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean saveEntry(DailyEntry entry, User author);
    
    /**
     * Gets the diary entry for the given user, entry date, and camera resource.
     * 
     * @param user The given user.
     * @param entryDate The date of the desired entry, which must be the start
     * of the day in the time zone of the given camera resource.
     * @param cameraResource The given camera resource.
     * @return The diary entry for the given user, entry date, and camera
     * resource if it exists; null otherwise.
     */
    public DailyEntry getEntry(User user, Date entryDate, 
            Resource cameraResource);
    
    /**
     * Get a possibly empty list of all diary entries by the given user.
     * 
     * @param user The given user.
     * @return A possibly empty list of all diary entries by the given user.
     */
    public ArrayList<DailyEntry> getAllEntriesByUser(User user);
    
    /**
     * Deletes. the diary entry for the given user, entry date, and camera 
     * resource if one exists.
     * 
     * @param user The given user.
     * @param entryDate The date of the desired entry, which must be the start
     * of the day in the time zone of the given camera resource.
     * @param cameraResource The given camera resource.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean deleteEntry(User user, Date entryDate, 
            Resource cameraResource);
}
