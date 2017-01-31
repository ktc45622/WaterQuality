
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.diary.DailyDiaryWebLinks;
import weather.common.dbms.DBMSDailyDiaryWebLinkManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 * Provides the remote version of DBMSDailyDiaryWebLinkManager.
 * @author Brian Zaiser
 */
public class DailyDiaryWebLinkManager implements DBMSDailyDiaryWebLinkManager{

    /**
     * Retrieves all the DailyDiaryWebLinks records from the database.
     * @return A collection of DailyDiaryWebLinks objects with all fields filled.
     */
    @Override
    public Vector<DailyDiaryWebLinks> getLinks() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.DailyDiary_Get;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<DailyDiaryWebLinks>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the database record for the specified DailyDiaryWebLinks object 
     * with the data in the fields.
     * @param link The DailyDiaryWebLinks object to be updated.
     * @return True, if the update was successful; false, otherwise.
     */
    @Override
    public boolean updateLink(DailyDiaryWebLinks link) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.DailyDiary_Update;
       arguments = new ArrayList();
       arguments.add(link);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }
    
    /**
     * Deletes the <code>DailyDiaryWebLink</code> related to the given name from
     * the database.
     * 
     * @param name The name of the <code>DailyDiaryWebLink</code> to delete.
     * @return True if a link with the given URL was removed from the database,
     * false otherwise.
     */
 /*   public boolean deleteLink(String name) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.DailyDiary_Delete;
       arguments = new ArrayList();
       arguments.add(name);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    }*/
    
    /**
     * Add a given Daily Diary webLink link.
     * @param link The DailyDiaryWebLinks to add.
     * @return True if the link was added successfully; false, otherwise.
     */
    /**
     * Add new URL and its corresponding name to the daily dairy web links table.
     * @param name Name to be added.
     * @param url URL to be added.
     * @return True if the link was added successfully; false, otherwise.
     */
  /*  public boolean addLink(String name, String url) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.DailyDiary_Add;
       arguments = new ArrayList();
       arguments.add(name);
       arguments.add(url);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement
    } */
}
