package weather;

import javax.swing.JMenu;
import weather.clientside.gui.administrator.SearchForAUserDialog.SEARCH_TYPES;
import weather.common.data.resource.WeatherResourceType;
import weather.common.data.resource.Resource;
import weather.common.utilities.WeatherException;

/**
 *
 * This interface specifies the functionality only associated with an
 * administrator user. Each method specified should represent a use case only
 * associate with this user type. That is, specify some action an administrator
 * user wants to accomplish. Methods that would refresh a screen or just obtain
 * data from the database to populate a pull-down menu should not be in this
 * interface. Populating a pull-down menu is not the goal of the administrator
 * user.
 *
 * @author Bloomsburg University Software Engineering
 * @version Spring 2009
 */
public interface AdministratorControlSystem {

    /**
     * Specifies the signature of the method to implement the Edit Weather
     * Resource Settings use case. This method will allow an administrator to
     * change the information stored about any weather resource in our database.
     * It also allows an administrator to add a weather resource to our
     * database. The parameter is used so that the system knows what type of
     * resources are shown to the user.
     *
     * @param resourceType The type of a resource that will be added or edited.
     * @throws WeatherException
     */
    public void editWeatherResourceSettingsService(
            WeatherResourceType resourceType)
            throws WeatherException;

    /**
     * Specifies the signature of the method that allows an administrator to
     * remove students from the weather system.
     */
    public void purgeStudents();

    /**
     * Specifies the signature of the method to implement the add web link use
     * case that was described in class. This method will allow an administrator
     * to add a new web link that will appear on the main application window.
     *
     * @param webLinkMenu the weblink menu to add the link to
     */
    public void addWebLink(JMenu webLinkMenu);

    /**
     * Specifies the signature of the method to implement the edit web link use
     * case. This method will allow an administrator to edit current web links
     * and forecast links that currently are stored in the database.
     *
     * @param webLinkMenu the weblink menu the link you want to edit is in
     */
    public void editWebLink(JMenu webLinkMenu);

    /**
     * Specifies the signature of the method to implement the repaint web link
     * use case. This method will repaint the JMenu "WebLinks" when update the
     * menu's list after the addWebLink or editWebLink was selected.
     */
    public void repaintWebLink();

    /**
     * Specifies the signature of the method to implement the repaint web link
     * categories use case. This method will repaint the JMenu "Web Links
     * category" when update the menu's list after the edit web link category
     * was selected.
     */
    public void listWebLinkCategories();

    /**
     * Specifies the signature of the method to implement the search for user
     * use case. This method will allow an administrator to search for a user
     * and edit that user's settings.
     *
     * @param isAdmin True if listing should have administrative ability to see
     * and edit more than students and guests, false otherwise.
     * @throws WeatherException
     */
    public void searchForUser(boolean isAdmin) throws WeatherException;

    /**
     * Specifies the signature of the method to implement the list users
     * function. This will open a table showing all users in the system and
     * allow an administrator to select a user and edit that user's settings.
     * 
     * @param isAdmin True if listing should have administrative ability to 
     * see and edit more than students and guests, false otherwise.
     */
    public void listUsers(boolean isAdmin);

    /**
     * Specifies the signature of the method to implement the
     * SearchForAUserDialog This method will allow an administrator to view and
     * edit a list of users
     *
     * @param userName the username of a specific user
     * @param type the first and last name and the login of the specific user
     * @param isAdmin True if listing should have administrative ability to 
     * see and edit more than students and guests, false otherwise.
     */
    public void getSpecificUserList(String userName, SEARCH_TYPES type,
            boolean isAdmin);

    /**
     * Specifies the signature of the method to implement the Define or Edit
     * Weather Event Type use case. This method will allow an administrator to
     * define or edit a weather event type in our system.
     */
    public void editWeatherEventTypeService();

    /**
     * Specifies the signature of the method to implement the Define or Edit
     * Weather Image Type use case. This method will allow an administrator to
     * define or edit a weather image type in our system.
     */
    public void editWeatherImageTypeService();

    /**
     * Specifies the signature of the method to implement the Search Stored
     * Files use case. This method allows an administrator to search through the
     * files stored in our file system.
     */
    public void searchStoredFilesService();

    /**
     * Specifies the signature of the method to implement the Archive Use Case.
     * This method allows an administrator to archive old files.
     */
    public void archiveService();

    public void setGeneralService(GeneralService generalService);

    public GeneralService getGeneralService();

    /**
     * Implements the Edit resource Settings use case. This use case control
     * method allows the user to edit or add the weather resources we use.
     * Information is changed in our database. The information also has to be
     * changed in any retrieval system that is already executing. This means
     * that any implementation of this interface must have access to a retrieval
     * and storage system object.
     *
     * @param resourceType the weather resource object type
     * @param resource a specific resource you would like to modify
     * @throws WeatherException
     */
    public void editResourceSettingsService(Resource resource,
            WeatherResourceType resourceType) throws WeatherException;

    /**
     * Implements the add resource setting use case.
     * 
     * @param resourceType The <code>WeatherResourceType</code> of resources to 
     * display to the administrator or undefined to allow changes to resource
     * display orders
     * @throws WeatherException
     */
    public void addResourceService(WeatherResourceType resourceType) throws WeatherException;

    /**
     * Specifies the signature of the method to implement the list user service.
     * This method is to obtain the list of weather resources stored in our
     * database and display that list of resources to an administrator.
     *
     * @throws WeatherException
     */
    public void listWeatherResourceService() throws WeatherException;

    /**
     * Allows an administrator to add a new web link to our system.
     */
    public void addNewWebLinkService();

    /**
     * Displays the URLTester window to allow the administrator to test the
     * validity of a URL.
     */
    public void testURLDisplay();

    /**
     * Allows an administrator to edit resource settings store this information
     * back into our database.
     */
    public void editResourceList();

    public void editCategories(JMenu weblinksMenu);

    public void manageCategories(JMenu weblinksMenu);

    public void listWebLinks(JMenu weblinksMenu);
    
    public void listDailyDairyWebLink(JMenu webLinkMenu);
    
}
