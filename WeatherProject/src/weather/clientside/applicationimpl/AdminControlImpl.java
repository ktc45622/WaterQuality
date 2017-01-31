package weather.clientside.applicationimpl;

import javax.swing.JMenu;
import weather.AdministratorControlSystem;
import weather.GeneralService;
import weather.clientside.gui.administrator.*;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.utilities.WeatherException;

/**
 * This class implements the AdministratorControlSystem interface and provides
 * one implementation of all administrator use cases. Please consult the use
 * case manual for detailed notes on each method.
 *
 * @see weather.AdministratorControlSystem
 * @author Bloomsburg University Software Engineering
 * @author Jacob Kelly (2008)
 * @version Spring 2010
 */
public class AdminControlImpl implements AdministratorControlSystem {

    GeneralService generalService;
    public JMenu webLink;

    /**
     * A proper AdminControl constructor, requiring a GeneralService parameter.
     *
     * @param generalService General back-end service.
     */
    public AdminControlImpl(GeneralService generalService) {
        this.generalService = generalService;
    }

    /**
     * Sets the back-end service of this object.
     *
     * @param generalService The general back-end service.
     */
    @Override
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    /**
     * Allow an administrator to change the information stored about any weather
     * resource in our database. It also allows an administrator to add a
     * weather resource to our database. The parameter is used so that the
     * system knows what type of resources are to be displayed to the
     * administrator.
     *
     * @param resourceType The <code>WeatherResourceType</code> of resources to 
     * display to the administrator or undefined to allow changes to resource
     * display orders.
     * @throws WeatherException Not thrown in this implementation.
     */
    @Override
    public void editWeatherResourceSettingsService(
            WeatherResourceType resourceType)
            throws WeatherException {
        Resource r=new Resource();
        r.setResourceType(resourceType);
        ResourceSettingsWindow resourceSettingsWindow =
                new ResourceSettingsWindow(generalService, r, true);
    }

    /**
     * Allows an administrator to purge inactive students.
     */
    @Override
    public void purgeStudents() {
        new PurgeStudentsWindow(this);
    }

    /**
     * Allows an administrator to add a web link to our weather system.
     */
    @Override
    public void addWebLink(JMenu webLinkMenu) {
        this.webLink = webLinkMenu;
        AddWebLinkWindow webLinkWindow =
                new AddWebLinkWindow(this);
        webLinkWindow.displayForAdd();
        repaintWebLink();
    }

    /**
     * Called after the add/edit web link to repaint the JMenu that contains the
     * web link list.
     */
    @Override
    public void repaintWebLink() {
        this.webLink.repaint();
    }

    /**
     * Allows an administrator to edit a web link on our weather system.
     */
    @Override
    public void editWebLink(JMenu webLinkMenu) {
        this.webLink = webLinkMenu;
        new EditWebLinks(this);
        repaintWebLink();
    }

    /**
     * Specifies the signature of the method to implement the search for user
     * use case. This method will allow an administrator to search for a user
     * and edit that user's settings.
     *
     * @param isAdmin True if listing should have administrative ability to see
     * and edit more than students and guests, false otherwise.
     * @throws WeatherException
     */
    @Override
    public void searchForUser(boolean isAdmin) throws WeatherException {
        new SearchUserWindow(this, isAdmin);
    }

    /**
     * Opens a table showing all users in the system and allows an administrator
     * to select a user and edit that user's settings.
     * @param isAdmin True if listing should have administrative ability to
     * see and edit more than students and guests, false otherwise.
     */
    @Override
    public void listUsers(boolean isAdmin) {
        new ManageUsersWindow(this, isAdmin);
    }

    /**
     * Allows an administrator to define or edit a weather event type in our
     * system.
     */
    @Override
    public void editWeatherEventTypeService() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Allows an administrator to define or edit a weather image type in our
     * system.
     */
    @Override
    public void editWeatherImageTypeService() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Allows archiving of weather resource instances.
     */
    @Override
    public void archiveService() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Allows stored files to be searched.
     */
    @Override
    public void searchStoredFilesService() {
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Allows the user to edit or add the weather resources used by the system.
     * Just prior to control return from this method, the database is updated.
     * The information must also be changed in any retrieval system that is
     * already executing.
     *
     * @param resource The specific resource you would like to modify, null if
     * you are not looking to modify a specific resource.
     * @param resourceType The type of the weather resource being edited.
     * @throws WeatherException Not thrown in this implementation.
     */
    @Override
    public void editResourceSettingsService(Resource resource,
            WeatherResourceType resourceType) throws WeatherException {
        ResourceSettingsWindow resourceSettingsWindow =
                new ResourceSettingsWindow(generalService, resource, false);

    }

    /**
     * Lists the weather resources within the DBMS systems on the Weather
     * Resource List Window.
     *
     * @throws WeatherException Not thrown in this implementation.
     */
    @Override
    public void listWeatherResourceService() throws WeatherException {
        //java.util.Vector<Resource> weatherResourceList = generalService.getDBMSSystem().getResourceManager().getResourceList();
        //WeatherResourceManagerWindow weatherResourceManagerWindow =
        new WeatherResourceManagerWindow(new javax.swing.JFrame(), this);
        //weatherResourceManagerWindow.show(weatherResourceList);

    }

    /**
     * Returns the general service for the class.
     *
     * @return The general service.
     */
    @Override
    public GeneralService getGeneralService() {
        return this.generalService;
    }

    /**
     * Accepts information for a new weblink as parameters so a new weblink may
     * be listed within the database.
     */
    @Override
    public void addNewWebLinkService() {
        AddWebLinkWindow webLinkWindow =
                new AddWebLinkWindow(this);
        webLinkWindow.displayForAdd();
    }

    /**
     * Displays the URLTester window to allow the administrator to test the
     * validity of a URL.
     */
    @Override
    public void testURLDisplay() {
        URLTester URLTesterWindow = new URLTester(this);
    }

    /**
     * Allows an administrator of the system to edit resource settings, after
     * which, the display on the Weather Resource List Window is updated.
     */
    @Override
    public void editResourceList() {
        throw new RuntimeException("Not yet implemented");
        //editResourceSettingsService(this.resource);
        //weatherResourceListWindow.refreshDisplay(weatherResourceList);
    }

    @Override
    public void editCategories(JMenu weblinksMenu) {
        this.webLink = weblinksMenu;
        new ManageWebLinkCategories(this);
        repaintWebLink();
    }

    @Override
    public void listWebLinks(JMenu weblinksMenu) {
        this.webLink = weblinksMenu;
        new ListWebLinks(this);
        repaintWebLink();
    }

    @Override
    public void manageCategories(JMenu weblinksMenu) {
        this.webLink = weblinksMenu;
        new ManageWebLinkCategories(this);
        repaintWebLink();
    }

    /**
     * Specifies the signature of the method to implement the
     * SearchForAUserDialog This method will allow an administrator to view and
     * edit a list of users
     *
     * @param userName the username of a specific user
     * @param type the first and last name and the login of the specific user
     * @param isAdmin True if listing should have administrative ability to see
     * and edit more than students and guests, false otherwise.
     */
    @Override
    public void getSpecificUserList(String userName, SearchForAUserDialog.SEARCH_TYPES type,
            boolean isAdmin) {
        new SearchForAUserDialog(this, userName, type, isAdmin);
    }

    /**
     * Allows the user to edit a given web link category.
     */
    @Override
    public void listWebLinkCategories() {
        ManageWebLinkCategories lwcd = new ManageWebLinkCategories(this);
    }

    /**
     * Allows the user to add a resource to the weather project.
     * 
     * @param resourceType The <code>WeatherResourceType</code> of the
     *  <code>Resource</code> to be added
     * @throws WeatherException
     */
    @Override
    public void addResourceService(WeatherResourceType resourceType) throws WeatherException {
        Resource r=new Resource();
        r.setResourceType(resourceType);
        ResourceSettingsWindow resourceSettingsWindow =
                new ResourceSettingsWindow(generalService, r, false);
    }

    @Override
    public void listDailyDairyWebLink(JMenu webLinkMenu) {
        this.webLink = webLinkMenu;
        new ListDailyDiaryWebLinks(this);
        repaintWebLink(); 
    }

}
