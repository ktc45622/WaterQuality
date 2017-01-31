
package weather.common.dbms.remote.managers;

import java.util.ArrayList;
import java.util.Vector;
import weather.common.data.WebLink;
import weather.common.data.WebLinkCategories;
import weather.common.dbms.DBMSWebLinkManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 *
 * @author Brian Zaiser
 */
public class WebLinkManager implements DBMSWebLinkManager {

    /**
     * Retrieves all records for web links in the specified category.
     * @param category The specific category.
     * @return A collection of WebLink objects with all fields filled.
     */
    @Override
    public Vector<WebLink> getLinksForCategory(String category) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_GetLinksForCategory;
       arguments = new ArrayList();
       arguments.add(category);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<WebLink>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Deletes the record of a WebLink based on the address.
     * @param URL The specific address of the link.
     * @return True, if record successfully deleted; false, otherwise.
     */
    @Override
    public boolean deleteLink(String URL) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_DeleteLink;
       arguments = new ArrayList();
       arguments.add(URL);
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
     * Adds a record for a web link category from the specified name, address, and category.
     * @param name The name for the new web link.
     * @param url The address for the new web link.
     * @param category The category for the new web link.
     * @return True, if record successfully added; false, otherwise.
     */
    @Override
    public boolean addLinkForCategory(String name, String url, String category) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_AddLinkForCategory;
       arguments = new ArrayList();
       arguments.add(name);
       arguments.add(url);
       arguments.add(category);
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
     * Updates the record for the specified WebLinkCategory.
     * @param oldCategory The web link category to be updated using the field values.
     * @return True, if record successfully updated; false, otherwise.
     */
    @Override
    public boolean updateWebLinkCategory(WebLinkCategories oldCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_UpdateWebLinkCategory;
       arguments = new ArrayList();
       arguments.add(oldCategory);
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
     * Adds a record for the specified WebLinkCategory.
     * @param webLinkCategory The web link category to be added.
     * @return True, if record successfully added; false, otherwise.
     */
    @Override
    public boolean addWebLinkCategory(WebLinkCategories webLinkCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_AddWebLinkCategory;
       arguments = new ArrayList();
       arguments.add(webLinkCategory);
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
     * Deletes the record for the specified WebLinkCategory.
     * @param webLinkCategory The web link category to be deleted.
     * @return True, if record successfully deleted; false, otherwise.
     */
    @Override
    public boolean removeLinkCategory(WebLinkCategories webLinkCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_RemoveLinkCategory;
       arguments = new ArrayList();
       arguments.add(webLinkCategory);
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
     * Retrieves all records for WebLinkCategories.
     * @return A collection of WebLinkCategories objects with all fields filled.
     */
    @Override
    public Vector<WebLinkCategories> obtainAllWebLinkCategories() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_ObtainAllWebLinkCategories;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<WebLinkCategories>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for WebLinks.
     * @return A collection of WebLink objects with all fields filled.
     */
    @Override
    public Vector<WebLink> obtainAllWebLinks() {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_ObtainAllWebLinks;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<WebLink>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Retrieves all records for WebLinks in the specified category.
     * @param webLinkCategory The specific WebLinkCategory.
     * @return A collection of WebLink objects with all fields filled.
     */
    @Override
    public Vector<WebLink> obtainWebLinksFromACategory(WebLinkCategories webLinkCategory) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_ObtainAllWebLinksFromACategory;
       arguments = new ArrayList();
       arguments.add(webLinkCategory);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Vector<WebLink>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement
    }

    /**
     * Updates the record for the specified WebLink.
     * @param link The WebLink to update using field values.
     * @return True, if record successfully updated; false, otherwise.
     */
    @Override
    public boolean updateWebLink(WebLink link) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WebLink_UpdateWebLink;
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
    
}
