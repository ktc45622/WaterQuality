
package weather.common.dbms;

import java.util.Vector;
import weather.common.data.diary.DailyDiaryWebLinks;
/*
 *  This class manages the dailydiaryWebLinks table in the database. 
 */

public interface DBMSDailyDiaryWebLinkManager 
{
     /**
     * Retrieves all web links contained in the database and
     * returns them as an <code>ArrayList</code> of Links.
     *
     * @return An <code>ArrayList</code> containing all of the web links contained
     * in the database.
     */
    public Vector<DailyDiaryWebLinks> getLinks();
    /**
     * Deletes the <code>DailyDiaryWebLink</code> related to the given name from
     * the database.
     * 
     * @param name The name of the <code>DailyDiaryWebLink</code> to delete.
     * @return True if a link with the given URL was removed from the database,
     * false otherwise.
     */
  //  public boolean deleteLink(String name);
    
    /**
     * Updates a given Daily Diary webLink link that matches the name.
     * @param link The DailyDiaryWebLinks to update.
     * @return True if updated, false if not.
     */
    public boolean updateLink(DailyDiaryWebLinks link);
    /**
     * Add a given Daily Diary webLink link.
     * @param link The DailyDiaryWebLinks to add.
     * @return True if updated, false if not.
     */
    /**
     * Add new URL and its corresponding name to the daily dairy web links table.
     * @param name Name to be added.
     * @param url URL to be added.
     * @return True if added, false if not.
     */
  //  public boolean addLink(String name,String url);
}
