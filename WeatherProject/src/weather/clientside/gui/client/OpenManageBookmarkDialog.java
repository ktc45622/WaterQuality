package weather.clientside.gui.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import weather.ApplicationControlSystem;
import weather.clientside.gui.administrator.*;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.manager.MovieController;
import weather.clientside.manager.WeatherStationPanelManager;
import weather.clientside.utilities.BookmarkEventOpener;
import weather.clientside.utilities.BookmarkTimeZoneFinder;
import weather.clientside.utilities.BooleanCellRenderer;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.clientside.utilities.ResourceVisibleTester;
import weather.clientside.utilities.TimedLoader;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.*;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * <code>OpenListBookmarkInstanceWindow</code> class creates a form that opens a
 * list of bookmarks from the database.
 * 
 * @author Alex Funk
 * @author John Lenhart
 * @author Justin Enslin
 * @version Spring 2012
 */
public class OpenManageBookmarkDialog extends BUDialog {

    private DBMSSystemManager dbms;
    private WeatherStationPanelManager station;
    private DBMSBookmarkCategoriesManager categories;
    private DBMSBookmarkEventTypesManager types;
    private DBMSBookmarkInstanceManager bookmarksManager;
    private DBMSEnrollmentManager courseManager;
    private DBMSResourceManager resources;
    private DBMSFileManager fileManager;
    private MovieController controller;
    private User user;
    private Vector<Course> courses;
    private Vector<User> instructors;
    private Vector<Bookmark> bookmarksDatabase;
    private Vector<Bookmark> bookmarksLocal;
    private Vector<Bookmark> eventsLocal;
    private Vector<Bookmark> eventsDatabase;
    private Vector<Bookmark> selectedBookmarksDatabase;
    private Vector<Bookmark> selectedEventsDatabase;
    private Vector<Bookmark> selectedBookmarksLocal;
    private Vector<Bookmark> selectedEventsLocal;
    private Bookmark bookmarkToOpenDatabase;
    private Bookmark eventToOpenDatabase;
    private Bookmark bookmarkToOpenLocal;
    private Bookmark eventToOpenLocal;
    private boolean isSelectAllBookmarksDatabase;
    private boolean isSelectAllEventsDatabase;
    private boolean isSelectAllBookmarksLocal;
    private boolean isSelectAllEventsLocal;
    
    //for advanced search dialogs
    private Calendar startDate;
    private Calendar endDate;
    private String selectedSubcategory;
    private boolean areAdvancedOptionsSelected = false;
    
    private String selectedCategoryBookmarksLocal = "Any Category";
    private String selectedCategoryEventsLocal = "Any Category";
    private String selectedCategoryBookmarksDatabase = "Any Category";
    private String selectedCategoryEventsDatabase = "Any Category";
    private SimpleDateFormat dateFormat 
            = new SimpleDateFormat("M/d/y h:mm:ss a z");
    private List<RowFilter<MyDefaultTableModel, Object>> filtersBookmarksDatabase;
    private List<RowFilter<MyDefaultTableModel, Object>> filtersEventsDatabase;
    private MyDefaultTableModel modelBookmarksDatabase;
    private MyDefaultTableModel modelEventsDatabase;
    private TableRowSorter<MyDefaultTableModel> sorterForBookmarksDatabase;
    private TableRowSorter<MyDefaultTableModel> sorterForEventsDatabase;
    private List<RowFilter<MyDefaultTableModel, Object>> filtersBookmarksLocal;
    private List<RowFilter<MyDefaultTableModel, Object>> filtersEventsLocal;
    private MyDefaultTableModel modelBookmarksLocal;
    private MyDefaultTableModel modelEventsLocal;
    private TableRowSorter<MyDefaultTableModel> sorterForBookmarksLocal;
    private TableRowSorter<MyDefaultTableModel> sorterForEventsLocal;
    private boolean isManageWindow = false;
    private Vector<Bookmark> externalBookmarks;
    private final boolean isSelectPhenomenonWindow;
    private Bookmark selectedPhenomenon = null;
    private SearchBookmarkDialog parent;
    
    //Criteria for search done via local search constuctor'
    String name = null;
    Calendar localStartDate = null;
    Calendar localEndDate = null;
    String category = null;
    String subCategory = null;

    /**
     * Creates a new OpenInstructorBookmarkInstance Window with a given amount
     * of bookmarks. Used in searching for database bookmarks and events. (Used
     * by SeachBookmarkDialog)
     *
     * @param appControl The application control system
     * @param selectedBookmarks The bookmarks to be implemented into the result
     * table
     * @param title "Searched by" then what ever the search is for
     * @param mc The movie controller
     * @param category The category to search for.
     */
    public OpenManageBookmarkDialog(ApplicationControlSystem appControl,
        Vector<Bookmark> selectedBookmarks, String title, MovieController mc,
        String category) {
        super(appControl);
        isSelectPhenomenonWindow = false;
        initComponents();
        initMyComponents();
        this.bookmarksDatabase = selectedBookmarks;
        externalBookmarks = selectedBookmarks;
        controller = mc;
        
        user = this.appControl.getAdministratorControlSystem().getGeneralService().getUser();
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        categories = appControl.getAdministratorControlSystem().getGeneralService().getDBMSSystem().getBookmarkCategoriesManager();
        bookmarksManager = dbms.getBookmarkManager();
        courseManager = dbms.getEnrollmentManager();
        types = dbms.getBookmarkTypesManager();
        resources = dbms.getResourceManager(); 
        fileManager = dbms.getFileManager();
        fileManager.setStorageSystem( StorageControlSystemImpl.getStorageSystem()) ;
        
        errorBookmarkLocalLabel.setVisible(false);
        errorEventsDatabaseLabel.setVisible(false);
        errorBookmarkDatabaseLabel.setVisible(false);
        addBookmarkBookmarkDatabaseButton.setVisible(false);
        addBookmarkBookmarskLocalButton.setVisible(false);
        addBookmarkEventsDBButton.setVisible(false);
        addBookmarkEventsLocalButton.setVisible(false);
        removedBookmarkDatabaseButton.setVisible(false);
        removeBookmarkLocalButton.setVisible(false);
        removeEventLocalButton.setVisible(false);
        addBookmarkEventsDBButton.setVisible(false);
        manageCategoriesELButton.setVisible(false);
        manageCategoriesBDButton.setVisible(false);
        manageCategoriesBLButton.setVisible(false);
        manageCategoriesEDButton.setVisible(false);
        errorEventsLocalLabel.setVisible(false);
        removedEventDatabaseButton1.setVisible(false);
        
        if (category != null && !category.trim().isEmpty() && !category.equals("All")) {
            filterCategoryBookmarksDatabaseComboBox.setSelectedItem(category);
            filterCategoryEventsDatabaseComboBox.setSelectedItem(category);
        }
        
        updateEventsDatabaseTable();
        updateBookmarksDatabaseTable();
        int bookmarksLocalTabIndex = 1;
        int eventsLocaltabIndex = 3;
        mainDisplayjTabbedPane.setEnabledAt(bookmarksLocalTabIndex, false);
        mainDisplayjTabbedPane.setEnabledAt(eventsLocaltabIndex, false);
        
        this.setTitle("Weather Viewer - Searched by " + title);
        setIconImage(IconProperties.getTitleBarIconImage());
        this.validate();
        super.postInitialize(false);
    }
    
    /**
     * Opens the search bookmarks window with the local results filtered by the
     * search criteria. Pass null for criteria which shouldn't be considered.
     * (Used by SeachBookmarkDialog)
     *
     * @param appControl The application control system.
     * @param title "Searched by" then what ever the search is for.
     * @param mc The movie controller.
     * @param startDate The start date to search from.
     * @param name The name to search for.
     * @param endDate The end date to search to.
     * @param category The category to search for.
     * @param subCategory The sub-category to search for.
     * @param parent The form that called this.
     */
    public OpenManageBookmarkDialog(ApplicationControlSystem appControl, String title, MovieController mc, 
            String name, Calendar startDate, Calendar endDate, String category, String subCategory,
            SearchBookmarkDialog parent){
        super(appControl);
        isSelectPhenomenonWindow = false;
        initComponents();
        initMyComponents();
        this.bookmarksDatabase = null;
        controller = mc;
        user = this.appControl.getAdministratorControlSystem().getGeneralService().getUser();
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        categories = appControl.getAdministratorControlSystem().getGeneralService().getDBMSSystem().getBookmarkCategoriesManager();
        bookmarksManager = dbms.getBookmarkManager();
        courseManager = dbms.getEnrollmentManager();
        types = dbms.getBookmarkTypesManager();
        resources = dbms.getResourceManager();
        fileManager = dbms.getFileManager();
        fileManager.setStorageSystem(StorageControlSystemImpl.getStorageSystem());
        
        errorBookmarkLocalLabel.setVisible(false);
        errorEventsDatabaseLabel.setVisible(false);
        errorBookmarkDatabaseLabel.setVisible(false);
        addBookmarkBookmarkDatabaseButton.setVisible(false);
        addBookmarkBookmarskLocalButton.setVisible(false);
        addBookmarkEventsDBButton.setVisible(false);
        addBookmarkEventsLocalButton.setVisible(false);
        removedBookmarkDatabaseButton.setVisible(false);
        removeBookmarkLocalButton.setVisible(false);
        removeEventLocalButton.setVisible(false);
        addBookmarkEventsDBButton.setVisible(false);
        manageCategoriesELButton.setVisible(false);
        manageCategoriesBDButton.setVisible(false);
        manageCategoriesBLButton.setVisible(false);
        manageCategoriesEDButton.setVisible(false);
        errorEventsLocalLabel.setVisible(false);
        removedEventDatabaseButton1.setVisible(false);
        
        this.name = name;
        this.localStartDate = startDate;
        this.localEndDate = endDate;
        this.category = category;
        this.subCategory = subCategory;
        
        int bookmarksDatabaseTabIndex = 0;
        int bookmarksLocalTabIndex = 1;
        int eventsDatabasetabIndex = 2;
        mainDisplayjTabbedPane.setEnabledAt(bookmarksDatabaseTabIndex, false);
        mainDisplayjTabbedPane.setEnabledAt(eventsDatabasetabIndex, false);
        mainDisplayjTabbedPane.setSelectedIndex(bookmarksLocalTabIndex);
        
        this.setTitle("Weather Viewer - Searched by " + title);
        setIconImage(IconProperties.getTitleBarIconImage());
        this.validate();
        
        updateBookmarksLocalResultTable();
        updateEventsLocalResultTable();
        
        if (category != null && !category.trim().isEmpty() && !category.equals("All")) {
            filterCategoryBookmarkLocalComboBox.setSelectedItem(category);
            filterCategoryEventLocalComboBox.setSelectedItem(category);
        }
        
        //In case no results, don't call until we check. 
        //super.postInitialize(false);   
        this.parent = parent;
    }

    /**
     * Opens a weather event from the current instructor from the database.
     * IsLocal is used to for students/guests, if not is local, then the user is
     * either admin or instructor. (Used by LessonAddEditWindow and main window)
     *
     * @param appControl The application control system.
     * @param type The instance type enumeration.
     * @param mc The movie controller.
     * @param isSelectPhenomenonWindow True if allows a phenomenon to be selected.
     */
    public OpenManageBookmarkDialog(ApplicationControlSystem appControl,
            BookmarkDuration type, MovieController mc, boolean isSelectPhenomenonWindow) {
        super(appControl);
        initComponents();
        this.isSelectPhenomenonWindow = isSelectPhenomenonWindow;
        initMyComponents();
        if (type == BookmarkDuration.event) {
            this.setTitle("Open Event");
        } else if (type == BookmarkDuration.instance) {
            this.setTitle("Open Bookmark");
        }
        if(isSelectPhenomenonWindow){
            this.setTitle("Select Phenomenon");
            this.setModalityType(ModalityType.APPLICATION_MODAL);
            selectAllBookmarkLocalButton.setVisible(false);
            selectAllBookmarksDatabaseButton.setVisible(false);
            selectAllEventsDatabaseButton.setVisible(false);
            selectAllEventsLocalButton.setVisible(false);
            openBookmarDatabasekButton.setVisible(false);
            openEventsDatabasekButton.setVisible(false);
            openBookmarkLocalButton.setVisible(false);
            openEventLocalButton.setVisible(false);
        }
        controller = mc;
        
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        categories = dbms.getBookmarkCategoriesManager();
        user = this.appControl.getAdministratorControlSystem().getGeneralService().getUser();
        bookmarksManager = dbms.getBookmarkManager();
        types = dbms.getBookmarkTypesManager();
        courseManager = dbms.getEnrollmentManager();
        resources = dbms.getResourceManager();
        fileManager = dbms.getFileManager();
        fileManager.setStorageSystem(StorageControlSystemImpl.getStorageSystem());
        if (user.getUserType() == UserType.student || user.getUserType() == UserType.guest) {
            if (user.getUserType() == UserType.student) {
                courses = courseManager.getCoursesForStudent(user);
            } //The else is for guests, so a guest will get all courses, no matter what.
            else if (user.getUserType() == UserType.guest) {
                courses = appControl.getAdministratorControlSystem().getGeneralService().getDBMSSystem().getCourseManager().obtainAllCourses();
            }
            for (Course c : courses) {
                boolean isBroken = false;
                for (User i : instructors) {
                    if (i.getUserNumber() == c.getInstructor().getUserNumber()) {
                        isBroken = true;
                        break;
                    }
                }
                if (!isBroken) {
                    instructors.add(c.getInstructor());
                }
            }
        }
        if (user.getUserType() == UserType.administrator || user.getUserType() == UserType.instructor) {
            instructors = new Vector<User>();
        }
        
        searchBookmarkLocalButton.setVisible(false);
        errorBookmarkLocalLabel.setVisible(false);
        searchBookmarksDatabaseButton.setVisible(false);
        errorBookmarkDatabaseLabel.setVisible(false);
        addBookmarkBookmarkDatabaseButton.setVisible(false);
        addBookmarkBookmarskLocalButton.setVisible(false);
        errorEventsDatabaseLabel.setVisible(false);
        addBookmarkEventsDBButton.setVisible(false);
        addBookmarkEventsLocalButton.setVisible(false);
        removedBookmarkDatabaseButton.setVisible(false);
        removeBookmarkLocalButton.setVisible(false);
        removeEventLocalButton.setVisible(false);
        addBookmarkEventsDBButton.setVisible(false);
        manageCategoriesELButton.setVisible(false);
        manageCategoriesBDButton.setVisible(false);
        manageCategoriesBLButton.setVisible(false);
        manageCategoriesEDButton.setVisible(false);
        searchEventLocalButton.setVisible(false);
        errorEventsLocalLabel.setVisible(false);
        removedEventDatabaseButton1.setVisible(false);
        searchEventsDatabaseButton.setVisible(false);
        
        updateEventsLocalResultTable();
        updateBookmarksDatabaseTable();
        updateBookmarksLocalResultTable();
        updateEventsDatabaseTable();
        
        this.validate();
        super.postInitialize(true);
    }
    
    /**
     * This constructor is used to make the manage bookmarks window.
     * 
     * @param appControl The application control system.
     * @param station The currently selected weather station.
     * @param mc The movie controller.
     */
    public OpenManageBookmarkDialog(ApplicationControlSystem appControl,
         WeatherStationPanelManager station, MovieController mc) {
        super(appControl);
        isSelectPhenomenonWindow = false;
        initComponents();
        isManageWindow = true;
        initMyComponents();
        this.setTitle("Manage Bookmarks and Events");
        controller = mc;
        
        this.dbms = appControl.getGeneralService().getDBMSSystem();
        categories = dbms.getBookmarkCategoriesManager();
        user = this.appControl.getAdministratorControlSystem().getGeneralService().getUser();
        bookmarksManager = dbms.getBookmarkManager();
        types = dbms.getBookmarkTypesManager();
        courseManager = dbms.getEnrollmentManager();
        resources = dbms.getResourceManager();
        fileManager = dbms.getFileManager();
        fileManager.setStorageSystem(StorageControlSystemImpl.getStorageSystem());
        findLocalBookmarkButton.setVisible(false);
        findLocalEventButton.setVisible(false);

        searchBookmarkLocalButton.setVisible(false);
        errorBookmarkLocalLabel.setVisible(false);
        openBookmarDatabasekButton.setVisible(false);
        openBookmarkLocalButton.setVisible(false);
        openEventsDatabasekButton.setVisible(false);
        searchEventLocalButton.setVisible(false);
        openEventLocalButton.setVisible(false);
        errorEventsLocalLabel.setVisible(false);
        searchEventsDatabaseButton.setVisible(false);
        errorBookmarkDatabaseLabel.setVisible(false);
        searchBookmarksDatabaseButton.setVisible(false);
        errorEventsDatabaseLabel.setVisible(false);
        
        updateBookmarksDatabaseTable();
        updateBookmarksLocalResultTable();
        updateEventsLocalResultTable();
        updateEventsDatabaseTable();
        
        this.station = station;
        this.validate();
        super.postInitialize(true);
     }
    
    /**
     * Updates all 4 tables.
     */
    private void updateAllTables(){
        updateEventsLocalResultTable();
        updateBookmarksDatabaseTable();
        updateBookmarksLocalResultTable();
        updateEventsDatabaseTable();
        this.mainDisplayjTabbedPane.repaint();
    }
    
    /**
     * Set event tables to single selection.
     */
    private void setSingleEventSelection() {
        this.selectAllEventsDatabaseButton.setVisible(false);
        this.selectAllEventsLocalButton.setVisible(false);
        this.eventsLocalResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.resultTableEventsDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    /**
     * Make the given row of the calling table the only selected row.
     * @param callingTable The table to be altered.
     * @param row The row to be selected.
     */
    private void makeOnlySelection(JTable callingTable, int row) {
        for (int i = 0; i < callingTable.getRowCount(); i++) {
            if (i != row) {
                callingTable.setValueAt(row == i, i, 0);
            }
        }
    }        
      
    /**
     * Initializes the objects that do not depend on the constructor called.
     */
     private void initMyComponents()
     {
         selectedEventsLocal = new Vector<>();
         instructors = new Vector<>();
         selectedBookmarksLocal = new Vector<>();
         selectedBookmarksDatabase = new Vector<>();
         selectedEventsDatabase = new Vector<>();
         filtersEventsLocal = new ArrayList<>();
         filtersBookmarksLocal = new ArrayList<>();
         filtersBookmarksDatabase = new ArrayList<>();
         filtersEventsDatabase = new ArrayList<>();
         externalBookmarks = null;
         bookmarkLocalResultTable.setRowSelectionAllowed(true);
         bookmarkLocalResultTable.setAutoCreateRowSorter(true);
         eventsLocalResultTable.setRowSelectionAllowed(true);
         eventsLocalResultTable.setAutoCreateRowSorter(true);
         resultTableBookmarksDatabase.setRowSelectionAllowed(true);
         resultTableBookmarksDatabase.setAutoCreateRowSorter(true);
         selectPhenomenonBmDbButton.setVisible(isSelectPhenomenonWindow);
         selectPhenomenonBmLocalButton.setVisible(isSelectPhenomenonWindow);
         selectPhenomenonEvDbButton.setVisible(isSelectPhenomenonWindow);
         selectPhenomenonEvLocalButton.setVisible(isSelectPhenomenonWindow);
         
         //Add the Categories to the Combo Box.
        GUIComponentFactory.initBookmarkCategoryBox(filterCategoryBookmarkLocalComboBox,
                appControl.getDBMSSystem().getEventManager(), 
                appControl.getGeneralService().getUser());
        GUIComponentFactory.initBookmarkCategoryBox(filterCategoryBookmarksDatabaseComboBox,
                appControl.getDBMSSystem().getEventManager(), 
                appControl.getGeneralService().getUser());
        GUIComponentFactory.initBookmarkCategoryBox(filterCategoryEventLocalComboBox,
                appControl.getDBMSSystem().getEventManager(), 
                appControl.getGeneralService().getUser());
       GUIComponentFactory.initBookmarkCategoryBox(filterCategoryEventsDatabaseComboBox,
                appControl.getDBMSSystem().getEventManager(), 
                appControl.getGeneralService().getUser());
       
        //Bookmarks Database table and filtering initialization.
        modelBookmarksDatabase = new MyDefaultTableModel(0, 0);
        sorterForBookmarksDatabase = new TableRowSorter<MyDefaultTableModel>(modelBookmarksDatabase);
        resultTableBookmarksDatabase.setModel(modelBookmarksDatabase);
        resultTableBookmarksDatabase.setRowSorter(sorterForBookmarksDatabase);
        resultTableBookmarksDatabase.addMouseListener(mouseClickBookmarkDatabase);
        
        //Bookmarks Local table and filtering initialization.
        modelBookmarksLocal = new MyDefaultTableModel(0, 0);
        sorterForBookmarksLocal = new TableRowSorter<MyDefaultTableModel>(modelBookmarksLocal);
        bookmarkLocalResultTable.setModel(modelBookmarksLocal);
        bookmarkLocalResultTable.setRowSorter(sorterForBookmarksLocal);
        bookmarkLocalResultTable.addMouseListener(mouseClickBookmarkLocal);
        
        //Events Local table and filtering initialization.
        modelEventsLocal = new MyDefaultTableModel(0, 0);
        sorterForEventsLocal = new TableRowSorter<MyDefaultTableModel>(modelEventsLocal);
        eventsLocalResultTable.setModel(modelEventsLocal);
        eventsLocalResultTable.setRowSorter(sorterForEventsLocal);
        eventsLocalResultTable.addMouseListener(mouseClickEventsLocal);
        
        //Events Database table and filtering initialization.
        modelEventsDatabase = new MyDefaultTableModel(0, 0);
        sorterForEventsDatabase = new TableRowSorter<MyDefaultTableModel>(modelEventsDatabase);
        resultTableEventsDatabase.setModel(modelEventsDatabase);
        resultTableEventsDatabase.setRowSorter(sorterForEventsDatabase);
        resultTableEventsDatabase.addMouseListener(mouseClickEventsDatabase);
       
        //Tab colors. 
        mainDisplayjTabbedPane.setForegroundAt(0, new Color(128, 0, 0, 255));
        mainDisplayjTabbedPane.setForegroundAt(1, new Color(128, 0, 0, 255));
        
        //Set event row selection.
        if(!isManageWindow){
            setSingleEventSelection();
        }
        
        //Set size
        int width = 1000 + this.getInsets().left + this.getInsets().right;
        int height = 424 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
     }
     
      /**
       * This function checks if both local tables are empty.
       */
     public void checkForLocalResults(){
        if (bookmarkLocalResultTable.getRowCount() > 0 || eventsLocalResultTable.getRowCount() > 0){
            parent.dispose();
            super.postInitialize(false);
        } else {
            parent.showNoResultsLabel();
            this.dispose();
        }
     }
     
    //to give advanced search dialogs variable access
    public void setStartDate(Calendar startDate){
        this.startDate = startDate;
    }
    
    public void setEndDate(Calendar endDate){
        this.endDate = endDate;
    }
    
    public void setSelectedSubcategory(String selectedSubcategory){
        this.selectedSubcategory = selectedSubcategory;
    }
    
    public void setAreAdvancedOptionsSelected(boolean areAdvancedOptionsSelected){
        this.areAdvancedOptionsSelected = areAdvancedOptionsSelected;
    }
    
    /**
      * Filters database events.
      */ 
    
    private void filterEventsDatabase() {
       //Remove the selected bookmarks when filtering
        isSelectAllEventsDatabase = true;
        selectAllEventsDatabaseButton.doClick();
        filtersEventsDatabase.clear();

        int categoryColumn = 2;
        int subcategoryColumn = 3;
        int dateColumn = 10;

        //The Advanced Search Window was set.
        if (areAdvancedOptionsSelected) {
            RowFilter<MyDefaultTableModel, Object> filterBySubcategory;
            if (selectedSubcategory.equals("Any Subcategory")) {
                filterBySubcategory = RowFilter.regexFilter(".*", subcategoryColumn);
            } else {
                filterBySubcategory = RowFilter.regexFilter("^" + selectedSubcategory + "$", subcategoryColumn);
            }
            filtersEventsDatabase.add(filterBySubcategory);
            
            if (startDate != null) {
                //Costruct date filter.
                Debug.println("Filter Start: " + CalendarFormatter.formatWithTimeZone(startDate) 
                        + " (" + startDate.getTimeInMillis() + ")");
                Debug.println("Filter End: " + CalendarFormatter.formatWithTimeZone(endDate) 
                        + " (" + endDate.getTimeInMillis() + ")");
                
                //Start half of date filter.
                RowFilter<MyDefaultTableModel, Object> filterByAfterDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, startDate.getTimeInMillis(), dateColumn);
                RowFilter<MyDefaultTableModel, Object> filterByEqualStartDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, startDate.getTimeInMillis(), dateColumn);
                ArrayList<RowFilter<MyDefaultTableModel, Object>> startFilterList = new ArrayList<>();
                startFilterList.add(filterByAfterDate);
                startFilterList.add(filterByEqualStartDate);
                RowFilter<MyDefaultTableModel, Object> totalStartFilter =
                        RowFilter.orFilter(startFilterList);
               
                //End half of date filter.
                RowFilter<MyDefaultTableModel, Object> filterByBeforeDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, endDate.getTimeInMillis(), dateColumn);
                RowFilter<MyDefaultTableModel, Object> filterByEqualEndDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, endDate.getTimeInMillis(), dateColumn);
                ArrayList<RowFilter<MyDefaultTableModel, Object>> endFilterList = new ArrayList<>();
                endFilterList.add(filterByBeforeDate);
                endFilterList.add(filterByEqualEndDate);
                RowFilter<MyDefaultTableModel, Object> totalEndFilter =
                        RowFilter.orFilter(endFilterList);
                
                //Combine halves of date filter.
                ArrayList<RowFilter<MyDefaultTableModel, Object>> finalDateFilterList = new ArrayList<>();
                finalDateFilterList.add(totalStartFilter);
                finalDateFilterList.add(totalEndFilter);
                RowFilter<MyDefaultTableModel, Object> totalDateFilter =
                        RowFilter.andFilter(finalDateFilterList);
                
                filtersEventsDatabase.add(totalDateFilter);
            }
        }

        RowFilter<MyDefaultTableModel, Object> filterByCategoryEventsDatabase;
        if (selectedCategoryEventsDatabase.equals("Any Category")) {
            filterByCategoryEventsDatabase = RowFilter.regexFilter(".*", categoryColumn);
        } else {
            filterByCategoryEventsDatabase = RowFilter.regexFilter("^" + selectedCategoryEventsDatabase + "$", categoryColumn);
        }
        filtersEventsDatabase.add(filterByCategoryEventsDatabase);
        
        RowFilter<MyDefaultTableModel, Object> compoundRowFilter = 
                RowFilter.andFilter(filtersEventsDatabase);
        sorterForEventsDatabase.setRowFilter(compoundRowFilter);
    }
    
     /**
      * Filters local events.
      */
     
      private void filterEventsLocal() {
         //Remove the selected bookmarks when filtering
         isSelectAllEventsLocal = true;
         selectAllEventsLocalButton.doClick();
         filtersEventsLocal.clear();
         
         int categoryColumn = 2;
         int subcategoryColumn = 3;
         int dateColumn = 10;
         
         //The Advanced Search Window was set.
         if (areAdvancedOptionsSelected) {
            RowFilter<MyDefaultTableModel, Object> filterBySubcategory = null;
             if (selectedSubcategory != null) {
                 if (selectedSubcategory.equals("Any Subcategory")) {
                     filterBySubcategory = RowFilter.regexFilter(".*", subcategoryColumn);
                 } else {
                     filterBySubcategory = RowFilter.regexFilter("^" + selectedSubcategory + "$", subcategoryColumn);
                 }
                 filtersEventsLocal.add(filterBySubcategory);
             }

             if (startDate != null) {
                 //Costruct date filter.
                 Debug.println("Filter Start: " + CalendarFormatter.formatWithTimeZone(startDate)
                         + " (" + startDate.getTimeInMillis() + ")");
                 Debug.println("Filter End: " + CalendarFormatter.formatWithTimeZone(endDate)
                         + " (" + endDate.getTimeInMillis() + ")");

                 //Start half of date filter.
                 RowFilter<MyDefaultTableModel, Object> filterByAfterDate =
                         RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, startDate.getTimeInMillis(), dateColumn);
                 RowFilter<MyDefaultTableModel, Object> filterByEqualStartDate =
                         RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, startDate.getTimeInMillis(), dateColumn);
                 ArrayList<RowFilter<MyDefaultTableModel, Object>> startFilterList = new ArrayList<>();
                 startFilterList.add(filterByAfterDate);
                 startFilterList.add(filterByEqualStartDate);
                 RowFilter<MyDefaultTableModel, Object> totalStartFilter =
                         RowFilter.orFilter(startFilterList);

                 //End half of date filter.
                 RowFilter<MyDefaultTableModel, Object> filterByBeforeDate =
                         RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, endDate.getTimeInMillis(), dateColumn);
                 RowFilter<MyDefaultTableModel, Object> filterByEqualEndDate =
                         RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, endDate.getTimeInMillis(), dateColumn);
                 ArrayList<RowFilter<MyDefaultTableModel, Object>> endFilterList = new ArrayList<>();
                 endFilterList.add(filterByBeforeDate);
                 endFilterList.add(filterByEqualEndDate);
                 RowFilter<MyDefaultTableModel, Object> totalEndFilter =
                         RowFilter.orFilter(endFilterList);

                 //Combine halves of date filter.
                 ArrayList<RowFilter<MyDefaultTableModel, Object>> finalDateFilterList = new ArrayList<>();
                 finalDateFilterList.add(totalStartFilter);
                 finalDateFilterList.add(totalEndFilter);
                 RowFilter<MyDefaultTableModel, Object> totalDateFilter =
                         RowFilter.andFilter(finalDateFilterList);

                 filtersEventsLocal.add(totalDateFilter);
             }
         }

         RowFilter<MyDefaultTableModel, Object> filterByCategory;
         if (selectedCategoryEventsLocal.equals("Any Category")) {
             filterByCategory = RowFilter.regexFilter(".*", categoryColumn);
         } else {
             filterByCategory = RowFilter.regexFilter("^" + selectedCategoryEventsLocal + "$", categoryColumn);
         }
         filtersEventsLocal.add(filterByCategory);
         
         //Turn filter list into one filter.
         RowFilter<MyDefaultTableModel, Object> compoundRowFilter =
                 RowFilter.andFilter(filtersEventsLocal);
         sorterForEventsLocal.setRowFilter(compoundRowFilter);
     }
     
     /**
      * Filters database bookmarks.
      */
      
      private void filterBookmarksDatabase() {
          //Remove the selected bookmarks when filtering
        isSelectAllBookmarksDatabase = true;
        selectAllBookmarksDatabaseButton.doClick();
        filtersBookmarksDatabase.clear();

        int categoryColumn = 2;
        int subcategoryColumn = 3;
        int dateColumn = 11;

        //The Advanced Search Window was set.
        if (areAdvancedOptionsSelected) {
            RowFilter<MyDefaultTableModel, Object> filterBySubcategoryBookmarksDatabase = null;
            if(selectedSubcategory.equals("Any Subcategory")) {
                filterBySubcategoryBookmarksDatabase = RowFilter.regexFilter(".*", subcategoryColumn);
            } else {
                filterBySubcategoryBookmarksDatabase = RowFilter.regexFilter("^" + selectedSubcategory + "$", subcategoryColumn);
            }
            filtersBookmarksDatabase.add(filterBySubcategoryBookmarksDatabase);
            
            if (startDate != null) {
                //Costruct date filter.
                Debug.println("Filter Start: " + CalendarFormatter.formatWithTimeZone(startDate)
                        + " (" + startDate.getTimeInMillis() + ")");
                Debug.println("Filter End: " + CalendarFormatter.formatWithTimeZone(endDate)
                        + " (" + endDate.getTimeInMillis() + ")");

                //Start half of date filter.
                RowFilter<MyDefaultTableModel, Object> filterByAfterDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, startDate.getTimeInMillis(), dateColumn);
                RowFilter<MyDefaultTableModel, Object> filterByEqualStartDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, startDate.getTimeInMillis(), dateColumn);
                ArrayList<RowFilter<MyDefaultTableModel, Object>> startFilterList = new ArrayList<>();
                startFilterList.add(filterByAfterDate);
                startFilterList.add(filterByEqualStartDate);
                RowFilter<MyDefaultTableModel, Object> totalStartFilter =
                        RowFilter.orFilter(startFilterList);

                //End half of date filter.
                RowFilter<MyDefaultTableModel, Object> filterByBeforeDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, endDate.getTimeInMillis(), dateColumn);
                RowFilter<MyDefaultTableModel, Object> filterByEqualEndDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, endDate.getTimeInMillis(), dateColumn);
                ArrayList<RowFilter<MyDefaultTableModel, Object>> endFilterList = new ArrayList<>();
                endFilterList.add(filterByBeforeDate);
                endFilterList.add(filterByEqualEndDate);
                RowFilter<MyDefaultTableModel, Object> totalEndFilter =
                        RowFilter.orFilter(endFilterList);

                //Combine halves of date filter.
                ArrayList<RowFilter<MyDefaultTableModel, Object>> finalDateFilterList = new ArrayList<>();
                finalDateFilterList.add(totalStartFilter);
                finalDateFilterList.add(totalEndFilter);
                RowFilter<MyDefaultTableModel, Object> totalDateFilter =
                        RowFilter.andFilter(finalDateFilterList);

                filtersBookmarksDatabase.add(totalDateFilter);
            }
        }

        RowFilter<MyDefaultTableModel, Object> filterByCategoryBookmarksDatabase;
        if (selectedCategoryBookmarksDatabase.equals("Any Category")) {
            filterByCategoryBookmarksDatabase = RowFilter.regexFilter(".*", categoryColumn);
        } else {
            filterByCategoryBookmarksDatabase = RowFilter.regexFilter("^" + selectedCategoryBookmarksDatabase + "$", categoryColumn);
        }
        
        filtersBookmarksDatabase.add(filterByCategoryBookmarksDatabase);
         
         //Turn filter list into one filter.
        RowFilter<MyDefaultTableModel, Object> compoundRowFilter = 
                RowFilter.andFilter(filtersBookmarksDatabase);
        sorterForBookmarksDatabase.setRowFilter(compoundRowFilter);
    }
     
    /**
      * Filters local bookmarks.
      */
      
      private void filterBookmarksLocal() {
        //Remove the selected bookmarks when filtering
        isSelectAllBookmarksLocal = true;
        selectAllBookmarkLocalButton.doClick();
        filtersBookmarksLocal.clear();
        
        int categoryColumn = 2;
        int subcategoryColumn = 3;
        int dateColumn = 10;
        
        //The Advanced Search Window was set.
        if (areAdvancedOptionsSelected) {
            RowFilter<MyDefaultTableModel, Object> filterBySubcategoryBookmarksLocal = null;
            if (selectedSubcategory != null) {
                if (selectedSubcategory.equals("Any Subcategory")) {
                    filterBySubcategoryBookmarksLocal = RowFilter.regexFilter(".*", subcategoryColumn);
                } else {
                    filterBySubcategoryBookmarksLocal = RowFilter.regexFilter("^" + selectedSubcategory + "$", subcategoryColumn);
                }
                filtersBookmarksLocal.add(filterBySubcategoryBookmarksLocal);
            }

            if (startDate != null) {
                //Costruct date filter.
                Debug.println("Filter Start: " + CalendarFormatter.formatWithTimeZone(startDate)
                        + " (" + startDate.getTimeInMillis() + ")");
                Debug.println("Filter End: " + CalendarFormatter.formatWithTimeZone(endDate)
                        + " (" + endDate.getTimeInMillis() + ")");

                //Start half of date filter.
                RowFilter<MyDefaultTableModel, Object> filterByAfterDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, startDate.getTimeInMillis(), dateColumn);
                RowFilter<MyDefaultTableModel, Object> filterByEqualStartDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, startDate.getTimeInMillis(), dateColumn);
                ArrayList<RowFilter<MyDefaultTableModel, Object>> startFilterList = new ArrayList<>();
                startFilterList.add(filterByAfterDate);
                startFilterList.add(filterByEqualStartDate);
                RowFilter<MyDefaultTableModel, Object> totalStartFilter =
                        RowFilter.orFilter(startFilterList);

                //End half of date filter.
                RowFilter<MyDefaultTableModel, Object> filterByBeforeDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, endDate.getTimeInMillis(), dateColumn);
                RowFilter<MyDefaultTableModel, Object> filterByEqualEndDate =
                        RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, endDate.getTimeInMillis(), dateColumn);
                ArrayList<RowFilter<MyDefaultTableModel, Object>> endFilterList = new ArrayList<>();
                endFilterList.add(filterByBeforeDate);
                endFilterList.add(filterByEqualEndDate);
                RowFilter<MyDefaultTableModel, Object> totalEndFilter =
                        RowFilter.orFilter(endFilterList);

                //Combine halves of date filter.
                ArrayList<RowFilter<MyDefaultTableModel, Object>> finalDateFilterList = new ArrayList<>();
                finalDateFilterList.add(totalStartFilter);
                finalDateFilterList.add(totalEndFilter);
                RowFilter<MyDefaultTableModel, Object> totalDateFilter =
                        RowFilter.andFilter(finalDateFilterList);

                filtersBookmarksLocal.add(totalDateFilter);
            }
        }

        RowFilter<MyDefaultTableModel, Object> filterByCategoryBookmarksLocal;
        if (selectedCategoryBookmarksLocal.equals("Any Category")) {
            filterByCategoryBookmarksLocal = RowFilter.regexFilter(".*", categoryColumn);
        } else {
            filterByCategoryBookmarksLocal = RowFilter.regexFilter("^" + selectedCategoryBookmarksLocal + "$", categoryColumn);
        }
        filtersBookmarksLocal.add(filterByCategoryBookmarksLocal);;
         
         //Turn filter list into one filter.
        RowFilter<MyDefaultTableModel, Object> compoundRowFilter = 
                RowFilter.andFilter(filtersBookmarksLocal);
        sorterForBookmarksLocal.setRowFilter(compoundRowFilter);
    }

     
     /**
      * Gets the selected Phenomenon.
      * 
      * @return The selected Phenomenon. If none were selected null is returned.
      */
     public Bookmark getSelectedPhenomenon() {
        return selectedPhenomenon;
    }

    /**
     * Updates the local bookmarks table.
     */
    private void updateBookmarksLocalResultTable() {
        //Get data and set table structure with a TimedLoader
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Local Bookmarks";
            }

            @Override
            protected void doLoading() {
                bookmarksLocal = new Vector<Bookmark>();
                Vector<Bookmark> allLocal = new Vector<Bookmark>();

                //Load all local bookmarks
                ArrayList<String> keys = PropertyManager.getBookmarkKeys();
                for (String key : keys) {
                    try {
                        ObjectInputStream in = new ObjectInputStream(new FileInputStream(PropertyManager.
                                getBookmarkProperty(key)));
                        allLocal.add((Bookmark) in.readObject());
                        in.close();
                    } catch (FileNotFoundException ex) {
                        PropertyManager.deleteBookmarkProperty(key);
                    } catch (IOException ex) {
                        WeatherLogger.log(Level.SEVERE, "Could not read bookmark.", ex);
                    } catch (ClassNotFoundException ex) {
                        WeatherLogger.log(Level.SEVERE, "Could not read bookmark.", ex);
                    }
                }

                //Filter for instances
                for (Bookmark bookmark : allLocal) {
                    if (bookmark.getType() == BookmarkDuration.instance) {
                        bookmarksLocal.add(bookmark);
                    }
                }

                //Filter for category
                if (category != null && !category.equals("All")) {
                    int categoryNumber = categories.searchByName(category)
                            .getBookmarkCategoryNumber();
                    for (int i = 0; i < bookmarksLocal.size(); i++) {
                        if (bookmarksLocal.get(i).getCategoryNumber() != categoryNumber) {
                            bookmarksLocal.removeElementAt(i);
                            i--;
                        }
                    }

                    //Filter by subcategory (If category is non-null, so is subcaregory)
                    if (!subCategory.equals("All")) {
                        int subCategoryNumber = types.searchByName(subCategory, category)
                                .getInstanceTypeNumber();
                        for (int i = 0; i < bookmarksLocal.size(); i++) {
                            if (bookmarksLocal.get(i).getTypeNumber() != subCategoryNumber) {
                                bookmarksLocal.removeElementAt(i);
                                i--;
                            }
                        }
                    }
                }

                //Filter by name
                if (name != null) {
                    for (int i = 0; i < bookmarksLocal.size(); i++) {
                        if (!bookmarksLocal.get(i).getName().equals(name)) {
                            bookmarksLocal.removeElementAt(i);
                            i--;
                        }
                    }
                }

                //Filter by time range
                if (localStartDate != null) {
                    for (int i = 0; i < bookmarksLocal.size(); i++) {
                        if (bookmarksLocal.get(i).getStartTime().getTime()
                                < localStartDate.getTimeInMillis()
                                || bookmarksLocal.get(i).getStartTime().getTime()
                                > localEndDate.getTimeInMillis()) {
                            bookmarksLocal.removeElementAt(i);
                            i--;
                        }
                    }
                }

                sorterForBookmarksLocal.setRowFilter(null);
                bookmarkLocalResultTable.setRowSorter(sorterForBookmarksLocal);
                modelBookmarksLocal.setRowCount(bookmarksLocal.size());
                modelBookmarksLocal.setColumnCount(11);
                bookmarkLocalResultTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanCellRenderer());
                bookmarkLocalResultTable.getColumnModel().getColumn(0).setHeaderValue("");
                bookmarkLocalResultTable.getColumnModel().getColumn(2).setHeaderValue("Category Name");
                bookmarkLocalResultTable.getColumnModel().getColumn(3).setHeaderValue("Sub-Category Name");
                bookmarkLocalResultTable.getColumnModel().getColumn(1).setHeaderValue("Bookmark Name");
                bookmarkLocalResultTable.getColumnModel().getColumn(4).setHeaderValue("Rank Type");
                bookmarkLocalResultTable.getColumnModel().getColumn(5).setHeaderValue("Date And Time");
                bookmarkLocalResultTable.getColumnModel().getColumn(5).setMinWidth(150);
                bookmarkLocalResultTable.getColumnModel().getColumn(6).setHeaderValue("Camera Location");
                bookmarkLocalResultTable.getColumnModel().getColumn(4).setMaxWidth(120);
                bookmarkLocalResultTable.getColumnModel().getColumn(0).setMaxWidth(30);
                bookmarkLocalResultTable.getColumnModel().getColumn(7).setHeaderValue("Author");
                bookmarkLocalResultTable.getColumnModel().getColumn(7).setMaxWidth(300);
                bookmarkLocalResultTable.getColumnModel().getColumn(8).setHeaderValue("Access");
                bookmarkToOpenLocal = null;
                selectedBookmarksLocal.clear();
                bookmarkLocalResultTable.getColumnModel().getColumn(5).setCellRenderer(tableCellDateRenderer);

                for (int i = 0; i < bookmarksLocal.size(); i++) {
                    Bookmark currentBookmark = bookmarksLocal.get(i);
                    //Get bookmark type and test for category change
                    BookmarkType currentBookmarkType
                            = types.searchByBookmarkTypeNumber(currentBookmark.getTypeNumber());
                    if (currentBookmarkType != null) {
                        currentBookmark.setCategoryNumber(currentBookmarkType.getCategoryNumber());
                    }
                    //If category or subcategory has been drleted, we must set them to the defaults
                    if (categories.searchByBookmarkCategoryNumber(currentBookmark.getCategoryNumber())
                            == null) {
                        //The category has been deleted, so set to <Uncategorized>.
                        currentBookmark.setCategoryNumber(categories.searchByName("<Uncategorized>")
                                .getBookmarkCategoryNumber());
                    }
                    if (currentBookmarkType == null) {
                        //The subcategory has been deleted, so set to <None>.
                        currentBookmark.setTypeNumber(types.searchByName("<None>",
                                categories.searchByBookmarkCategoryNumber(
                                        currentBookmark.getCategoryNumber()).getName()).getInstanceTypeNumber());
                    }

                    //Load bookmark data.
                    bookmarkLocalResultTable.setValueAt(false, i, 0);
                    bookmarkLocalResultTable.setValueAt(categories.
                            searchByBookmarkCategoryNumber(currentBookmark.getCategoryNumber()).getName(), i, 2);
                    bookmarkLocalResultTable.setValueAt(types.searchByBookmarkTypeNumber(
                            currentBookmark.getTypeNumber()).getName(), i, 3);
                    bookmarkLocalResultTable.setValueAt(currentBookmark.getName(), i, 1);
                    bookmarkLocalResultTable.setValueAt(currentBookmark.getRanking(), i, 4);
                    dateFormat.setTimeZone(BookmarkTimeZoneFinder
                            .findTimeZone(currentBookmark, appControl.getGeneralService()));
                    bookmarkLocalResultTable.setValueAt(dateFormat.format(currentBookmark.
                            getStartTime()), i, 5);
                    //2 is not a magical number, but if a resource is less than 2,it is not present
                    if (currentBookmark.getWeatherCameraResourceNumber() < 2) {
                        bookmarkLocalResultTable.setValueAt("No Camera", i, 6);
                    } else {
                        bookmarkLocalResultTable.setValueAt(resources.getWeatherResourceByNumber(
                                currentBookmark.getWeatherCameraResourceNumber()).getName(), i, 6);
                    }
                    bookmarkLocalResultTable.setValueAt(user.getFirstName() + " "
                            + user.getLastName(), i, 7);
                    bookmarkLocalResultTable.setValueAt("local", i, 8);
                    //This is needed because local bookmarks do not have a unique Bookmark number (all -1).
                    String uniqueKey = currentBookmark.getName() + currentBookmark.getStartTime();
                    bookmarkLocalResultTable.setValueAt(uniqueKey, i, 9);

                    //Time in millis for filtering.
                    bookmarkLocalResultTable.setValueAt(currentBookmark.getStartTime().getTime(), i, 10);
                }

                //Hide key and time in millis.
                bookmarkLocalResultTable.getColumnModel().getColumn(9).setMinWidth(0);
                bookmarkLocalResultTable.getColumnModel().getColumn(9).setMaxWidth(0);
                bookmarkLocalResultTable.getColumnModel().getColumn(9).setWidth(0);
                bookmarkLocalResultTable.getColumnModel().getColumn(10).setMinWidth(0);
                bookmarkLocalResultTable.getColumnModel().getColumn(10).setMaxWidth(0);
                bookmarkLocalResultTable.getColumnModel().getColumn(10).setWidth(0);
            }
        };

        loader.execute();

        filterBookmarksLocal();
        bookmarkLocalResultTable.revalidate();
    }
    
     /**
     * Updates the local bookmarks table.
     */
    private void updateEventsLocalResultTable() {
        //Get data and set table structure with a TimedLoader
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Local Events";
            }

            @Override
            protected void doLoading() {
                eventsLocal = new Vector<Bookmark>();
                Vector<Bookmark> allLocal = new Vector<Bookmark>();

                //Load all local bookmarks
                ArrayList<String> keys = PropertyManager.getBookmarkKeys();
                for (String key : keys) {
                    try {
                        ObjectInputStream in = new ObjectInputStream(new FileInputStream(PropertyManager.
                                getBookmarkProperty(key)));
                        allLocal.add((Bookmark) in.readObject());
                        in.close();
                    } catch (FileNotFoundException ex) {
                        PropertyManager.deleteBookmarkProperty(key);
                    } catch (IOException ex) {
                        WeatherLogger.log(Level.SEVERE, "Could not read bookmark.", ex);
                    } catch (ClassNotFoundException ex) {
                        WeatherLogger.log(Level.SEVERE, "Could not read bookmark.", ex);
                    }
                }
                //Filter for events
                for (Bookmark bookmark : allLocal) {
                    if (bookmark.getType() == BookmarkDuration.event) {
                        eventsLocal.add(bookmark);
                    }
                }

                //Filter for category
                if (category != null && !category.equals("All")) {
                    int categoryNumber = categories.searchByName(category)
                            .getBookmarkCategoryNumber();
                    for (int i = 0; i < eventsLocal.size(); i++) {
                        if (eventsLocal.get(i).getCategoryNumber() != categoryNumber) {
                            eventsLocal.removeElementAt(i);
                            i--;
                        }
                    }

                    //Filter by subcategory (If category is non-null, so is subcaregory)
                    if (!subCategory.equals("All")) {
                        int subCategoryNumber = types.searchByName(subCategory, category)
                                .getInstanceTypeNumber();
                        for (int i = 0; i < eventsLocal.size(); i++) {
                            if (eventsLocal.get(i).getTypeNumber() != subCategoryNumber) {
                                eventsLocal.removeElementAt(i);
                                i--;
                            }
                        }
                    }
                }

                //Filter by name
                if (name != null) {
                    for (int i = 0; i < eventsLocal.size(); i++) {
                        if (!eventsLocal.get(i).getName().equals(name)) {
                            eventsLocal.removeElementAt(i);
                            i--;
                        }
                    }
                }

                //Filter by time range
                if (localStartDate != null) {
                    for (int i = 0; i < eventsLocal.size(); i++) {
                        if (eventsLocal.get(i).getStartTime().getTime()
                                < localStartDate.getTimeInMillis()
                                || eventsLocal.get(i).getStartTime().getTime()
                                > localEndDate.getTimeInMillis()) {
                            eventsLocal.removeElementAt(i);
                            i--;
                        }
                    }
                }

                sorterForEventsLocal.setRowFilter(null);
                eventsLocalResultTable.setRowSorter(sorterForEventsLocal);
                modelEventsLocal.setRowCount(eventsLocal.size());
                modelEventsLocal.setColumnCount(11);
                eventsLocalResultTable.getColumnModel().getColumn(0).setCellRenderer(new BooleanCellRenderer());
                eventsLocalResultTable.getColumnModel().getColumn(0).setHeaderValue("");
                eventsLocalResultTable.getColumnModel().getColumn(2).setHeaderValue("Category Name");
                eventsLocalResultTable.getColumnModel().getColumn(3).setHeaderValue("Sub-Category Name");
                eventsLocalResultTable.getColumnModel().getColumn(1).setHeaderValue("Bookmark Name");
                eventsLocalResultTable.getColumnModel().getColumn(4).setHeaderValue("Rank Type");
                eventsLocalResultTable.getColumnModel().getColumn(5).setHeaderValue("Date And Time");
                eventsLocalResultTable.getColumnModel().getColumn(5).setMinWidth(150);
                eventsLocalResultTable.getColumnModel().getColumn(6).setHeaderValue("Camera Location");
                eventsLocalResultTable.getColumnModel().getColumn(4).setMaxWidth(120);
                eventsLocalResultTable.getColumnModel().getColumn(0).setMaxWidth(30);
                eventsLocalResultTable.getColumnModel().getColumn(7).setHeaderValue("Author");
                eventsLocalResultTable.getColumnModel().getColumn(7).setMaxWidth(300);
                eventsLocalResultTable.getColumnModel().getColumn(8).setHeaderValue("Access");
                eventToOpenLocal = null;
                selectedEventsLocal.clear();
                eventsLocalResultTable.getColumnModel().getColumn(5).setCellRenderer(tableCellDateRenderer);

                for (int i = 0; i < eventsLocal.size(); i++) {
                    Bookmark currentBookmark = eventsLocal.get(i);
                    //Get bookmark type and test for category change
                    BookmarkType currentBookmarkType
                            = types.searchByBookmarkTypeNumber(currentBookmark.getTypeNumber());
                    if (currentBookmarkType != null) {
                        currentBookmark.setCategoryNumber(currentBookmarkType.getCategoryNumber());
                    }
                    //If category or subcategory has been drleted, we must set them to the defaults
                    if (categories.searchByBookmarkCategoryNumber(currentBookmark.getCategoryNumber())
                            == null) {
                        //The category has been deleted, so set to <Uncategorized>.
                        currentBookmark.setCategoryNumber(categories.searchByName("<Uncategorized>")
                                .getBookmarkCategoryNumber());
                    }
                    if (currentBookmarkType == null) {
                        //The subcategory has been deleted, so set to <None>.
                        currentBookmark.setTypeNumber(types.searchByName("<None>",
                                categories.searchByBookmarkCategoryNumber(
                                        currentBookmark.getCategoryNumber()).getName()).getInstanceTypeNumber());
                    }

                    //Load event data.
                    eventsLocalResultTable.setValueAt(false, i, 0);
                    eventsLocalResultTable.setValueAt(categories.searchByBookmarkCategoryNumber(
                            currentBookmark.getCategoryNumber()).getName(), i, 2);
                    eventsLocalResultTable.setValueAt(types.searchByBookmarkTypeNumber(
                            currentBookmark.getTypeNumber()).getName(), i, 3);
                    eventsLocalResultTable.setValueAt(currentBookmark.getName(), i, 1);
                    eventsLocalResultTable.setValueAt(currentBookmark.getRanking(), i, 4);
                    dateFormat.setTimeZone(BookmarkTimeZoneFinder
                            .findTimeZone(currentBookmark, appControl.getGeneralService()));
                    eventsLocalResultTable.setValueAt(dateFormat.format(currentBookmark.
                            getStartTime()), i, 5);

                    //Determine what to show for camera location.
                    Resource resource = resources.getWeatherResourceByNumber(currentBookmark
                            .getWeatherCameraResourceNumber());
                    if (resource != null && ResourceVisibleTester.canUserSeeResource(appControl
                            .getGeneralService().getUser(), resource)) {
                        eventsLocalResultTable.setValueAt(resource.getName(), i, 6);
                    } else {
                        eventsLocalResultTable.setValueAt("No Camera", i, 6);
                    }

                    eventsLocalResultTable.setValueAt(user.getFirstName() + " "
                            + user.getLastName(), i, 7);
                    eventsLocalResultTable.setValueAt("local", i, 8);
                    //This is needed because local bookmarks do not have a unique Bookmark number (all -1).
                    String uniqueKey = currentBookmark.getName() + currentBookmark.getStartTime();
                    eventsLocalResultTable.setValueAt(uniqueKey, i, 9);

                    //Time in millis for filtering.
                    eventsLocalResultTable.setValueAt(currentBookmark.getStartTime().getTime(), i, 10);
                }

                //Hide key and time in millis.
                eventsLocalResultTable.getColumnModel().getColumn(9).setMinWidth(0);
                eventsLocalResultTable.getColumnModel().getColumn(9).setMaxWidth(0);
                eventsLocalResultTable.getColumnModel().getColumn(9).setWidth(0);
                eventsLocalResultTable.getColumnModel().getColumn(10).setMinWidth(0);
                eventsLocalResultTable.getColumnModel().getColumn(10).setMaxWidth(0);
                eventsLocalResultTable.getColumnModel().getColumn(10).setWidth(0);
            }
        };
        loader.execute();

        filterEventsLocal();
        eventsLocalResultTable.revalidate();
    }

    /**
     * Updates the database bookmarks table.
     */
    private void updateBookmarksDatabaseTable() {
        //Get data and set table structure with a TimedLoader
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Database Bookmarks";
            }

            @Override
            protected void doLoading() {
                Vector<Bookmark> allDatabase;
                if (externalBookmarks != null) {
                    allDatabase = externalBookmarks;
                } else if (isManageWindow) {
                    if (user.getUserType() == UserType.administrator) {
                        allDatabase = bookmarksManager.obtainAll();
                    } else {
                        allDatabase = bookmarksManager.
                                searchByCreatedBy(user.getUserNumber());
                    }
                } else {
                    allDatabase = bookmarksManager.
                            searchAllBookmarksViewableByUser(user, instructors);
                }

                bookmarksDatabase = new Vector<Bookmark>();
                for (Bookmark bookmark : allDatabase) {
                    if (bookmark.getType() == BookmarkDuration.instance) {
                        bookmarksDatabase.add(bookmark);
                    }
                }

                sorterForBookmarksDatabase.setRowFilter(null);
                resultTableBookmarksDatabase.setRowSorter(sorterForBookmarksDatabase);
                modelBookmarksDatabase.setRowCount(bookmarksDatabase.size());
                modelBookmarksDatabase.setColumnCount(12);
                resultTableBookmarksDatabase.getColumnModel().getColumn(0).setCellRenderer(new BooleanCellRenderer());
                resultTableBookmarksDatabase.getColumnModel().getColumn(0).setHeaderValue("");
                resultTableBookmarksDatabase.getColumnModel().getColumn(2).setHeaderValue("Category Name");
                resultTableBookmarksDatabase.getColumnModel().getColumn(3).setHeaderValue("Sub-Category Name");
                resultTableBookmarksDatabase.getColumnModel().getColumn(1).setHeaderValue("Bookmark Name");
                resultTableBookmarksDatabase.getColumnModel().getColumn(4).setHeaderValue("Rank Type");
                resultTableBookmarksDatabase.getColumnModel().getColumn(5).setHeaderValue("Date And Time");
                resultTableBookmarksDatabase.getColumnModel().getColumn(5).setMinWidth(150);
                resultTableBookmarksDatabase.getColumnModel().getColumn(6).setHeaderValue("Camera Location");
                resultTableBookmarksDatabase.getColumnModel().getColumn(4).setMaxWidth(120);
                resultTableBookmarksDatabase.getColumnModel().getColumn(0).setMaxWidth(30);
                resultTableBookmarksDatabase.getColumnModel().getColumn(7).setHeaderValue("Author");
                resultTableBookmarksDatabase.getColumnModel().getColumn(7).setMaxWidth(300);
                resultTableBookmarksDatabase.getColumnModel().getColumn(8).setHeaderValue("Access");
                resultTableBookmarksDatabase.getColumnModel().getColumn(10).setHeaderValue("Files");
                resultTableBookmarksDatabase.getColumnModel().getColumn(10).setMaxWidth(50);
                bookmarkToOpenDatabase = null;
                selectedBookmarksDatabase.clear();
                resultTableBookmarksDatabase.getColumnModel().getColumn(5).setCellRenderer(tableCellDateRenderer);

                for (int i = 0; i < bookmarksDatabase.size(); i++) {
                    Bookmark currentBookmark = bookmarksDatabase.get(i);
                    /*
             * The database does not clean up the bookmarks when categories,
             * sub-categories, and users are deleted. These try/catch blocks are
             * a temporary fix to allow us to access this window while the
             * database is being fixed.
                     */
                    String categoryName, subcategoryName;
                    try {
                        categoryName = categories.searchByBookmarkCategoryNumber(
                                currentBookmark.getCategoryNumber()).getName();
                    } catch (NullPointerException ex) {
                        categoryName = "Database Error";
                    }

                    try {
                        subcategoryName = types.searchByBookmarkTypeNumber(
                                currentBookmark.getTypeNumber()).getName();
                    } catch (NullPointerException ex) {
                        subcategoryName = "Database Error";
                    }

                    //Load bookmark data
                    resultTableBookmarksDatabase.setValueAt(false, i, 0);
                    resultTableBookmarksDatabase.setValueAt(categoryName, i, 2);
                    resultTableBookmarksDatabase.setValueAt(subcategoryName, i, 3);

                    resultTableBookmarksDatabase.setValueAt(currentBookmark.getName(), i, 1);
                    resultTableBookmarksDatabase.setValueAt(currentBookmark.getRanking(), i, 4);
                    dateFormat.setTimeZone(BookmarkTimeZoneFinder
                            .findTimeZone(currentBookmark, appControl.getGeneralService()));
                    resultTableBookmarksDatabase.setValueAt(dateFormat.format(currentBookmark.
                            getStartTime()), i, 5);
                    //2 is not a magical number, but if a resource is less than 2,it is not present
                    if (currentBookmark.getWeatherCameraResourceNumber() < 2) {
                        resultTableBookmarksDatabase.setValueAt("No Camera", i, 6);
                    } else {
                        resultTableBookmarksDatabase.setValueAt(resources.getWeatherResourceByNumber(
                                currentBookmark.getWeatherCameraResourceNumber()).getName(), i, 6);
                    }
                    /*
             * The database does not clean up the bookmarks when categories,
             * sub-categories, and users are deleted. These try/catch blocks are
             * a temporary fix to allow us to access this window while the
             * database is being fixed.
                     */
                    String bookmarkHolderName;
                    try {
                        User bookmarkHolder = appControl.getDBMSSystem().getUserManager().obtainUser(currentBookmark.getCreatedBy());
                        bookmarkHolderName = bookmarkHolder.getFirstName() + " "
                                + bookmarkHolder.getLastName();
                    } catch (NullPointerException ex) {
                        bookmarkHolderName = "Database Error";
                    }
                    resultTableBookmarksDatabase.setValueAt(bookmarkHolderName, i, 7);
                    resultTableBookmarksDatabase.setValueAt(currentBookmark.getAccessRights().toString(), i, 8);
                    resultTableBookmarksDatabase.setValueAt(currentBookmark.getBookmarkNumber(), i, 9);

                    //Load number of files
                    int numFiles = dbms.getFileManager().getAllFilesForBookmark(currentBookmark).size();
                    resultTableBookmarksDatabase.setValueAt(numFiles, i, 10);

                    //Time in millis for filtering.
                    resultTableBookmarksDatabase.setValueAt(currentBookmark.getStartTime().getTime(), i, 11);
                }

                //Hide key and time in millis.
                resultTableBookmarksDatabase.getColumnModel().getColumn(9).setMinWidth(0);
                resultTableBookmarksDatabase.getColumnModel().getColumn(9).setMaxWidth(0);
                resultTableBookmarksDatabase.getColumnModel().getColumn(9).setWidth(0);
                resultTableBookmarksDatabase.getColumnModel().getColumn(11).setMinWidth(0);
                resultTableBookmarksDatabase.getColumnModel().getColumn(11).setMaxWidth(0);
                resultTableBookmarksDatabase.getColumnModel().getColumn(11).setWidth(0);
            }
        };
        loader.execute();

        filterBookmarksDatabase();
        resultTableBookmarksDatabase.revalidate();
        this.validate();
    }
    
    /**
     * Updates the events database table.
     */
    private void updateEventsDatabaseTable() {
        //Get data and set table structure with a TimedLoader
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Database Events";
            }

            @Override
            protected void doLoading() {
                Vector<Bookmark> allDatabase;
                if (externalBookmarks != null) {
                    allDatabase = externalBookmarks;
                } else if (isManageWindow) {
                    if (user.getUserType() == UserType.administrator) {
                        allDatabase = bookmarksManager.obtainAll();
                    } else {
                        allDatabase = bookmarksManager.
                                searchByCreatedBy(user.getUserNumber());
                    }
                } else {
                    allDatabase = bookmarksManager.
                            searchAllBookmarksViewableByUser(user, instructors);
                }

                eventsDatabase = new Vector<Bookmark>();
                for (Bookmark bookmark : allDatabase) {
                    if (bookmark.getType() == BookmarkDuration.event) {
                        eventsDatabase.add(bookmark);
                    }
                }

                sorterForEventsDatabase.setRowFilter(null);
                resultTableEventsDatabase.setRowSorter(sorterForEventsDatabase);
                modelEventsDatabase.setRowCount(eventsDatabase.size());
                modelEventsDatabase.setColumnCount(11);
                resultTableEventsDatabase.getColumnModel().getColumn(0).setCellRenderer(new BooleanCellRenderer());
                resultTableEventsDatabase.getColumnModel().getColumn(0).setHeaderValue("");
                resultTableEventsDatabase.getColumnModel().getColumn(2).setHeaderValue("Category Name");
                resultTableEventsDatabase.getColumnModel().getColumn(3).setHeaderValue("Sub-Category Name");
                resultTableEventsDatabase.getColumnModel().getColumn(1).setHeaderValue("Bookmark Name");
                resultTableEventsDatabase.getColumnModel().getColumn(4).setHeaderValue("Rank Type");
                resultTableEventsDatabase.getColumnModel().getColumn(5).setHeaderValue("Date And Time");
                resultTableEventsDatabase.getColumnModel().getColumn(5).setMinWidth(150);
                resultTableEventsDatabase.getColumnModel().getColumn(6).setHeaderValue("Camera Location");
                resultTableEventsDatabase.getColumnModel().getColumn(4).setMaxWidth(120);
                resultTableEventsDatabase.getColumnModel().getColumn(0).setMaxWidth(30);
                resultTableEventsDatabase.getColumnModel().getColumn(7).setHeaderValue("Author");
                resultTableEventsDatabase.getColumnModel().getColumn(7).setMaxWidth(300);
                resultTableEventsDatabase.getColumnModel().getColumn(8).setHeaderValue("Access");
                eventToOpenDatabase = null;
                selectedEventsDatabase.clear();
                resultTableEventsDatabase.getColumnModel().getColumn(5).setCellRenderer(tableCellDateRenderer);

                for (int i = 0; i < eventsDatabase.size(); i++) {
                    Bookmark currentBookmark = eventsDatabase.get(i);
                    /*
             * The database does not clean up the bookmarks when categories,
             * sub-categories, and users are deleted. These try/catch blocks are
             * a temporary fix to allow us to access this window while the
             * database is being fixed.
                     */
                    String categoryName, subcategoryName;
                    try {
                        categoryName = categories.searchByBookmarkCategoryNumber(
                                currentBookmark.getCategoryNumber()).getName();
                    } catch (NullPointerException ex) {
                        categoryName = "Database Error";
                    }

                    try {
                        subcategoryName = types.searchByBookmarkTypeNumber(
                                currentBookmark.getTypeNumber()).getName();
                    } catch (NullPointerException ex) {
                        subcategoryName = "Database Error";
                    }
                    resultTableEventsDatabase.setValueAt(false, i, 0);
                    resultTableEventsDatabase.setValueAt(categoryName, i, 2);
                    resultTableEventsDatabase.setValueAt(subcategoryName, i, 3);

                    resultTableEventsDatabase.setValueAt(currentBookmark.getName(), i, 1);
                    resultTableEventsDatabase.setValueAt(currentBookmark.getRanking(), i, 4);

                    //Load event data
                    dateFormat.setTimeZone(BookmarkTimeZoneFinder
                            .findTimeZone(currentBookmark, appControl.getGeneralService()));
                    resultTableEventsDatabase.setValueAt(dateFormat.format(currentBookmark.
                            getStartTime()), i, 5);

                    //Determine what to show for camera location.
                    Resource resource = resources.getWeatherResourceByNumber(currentBookmark
                            .getWeatherCameraResourceNumber());
                    if (resource != null && ResourceVisibleTester.canUserSeeResource(appControl
                            .getGeneralService().getUser(), resource)) {
                        resultTableEventsDatabase.setValueAt(resource.getName(), i, 6);
                    } else {
                        resultTableEventsDatabase.setValueAt("No Camera", i, 6);
                    }
                    /*
             * The database does not clean up the bookmarks when categories,
             * sub-categories, and users are deleted. These try/catch blocks are
             * a temporary fix to allow us to access this window while the
             * database is being fixed.
                     */
                    String bookmarkHolderName;
                    try {
                        User bookmarkHolder = appControl.getDBMSSystem().getUserManager().obtainUser(currentBookmark.getCreatedBy());
                        bookmarkHolderName = bookmarkHolder.getFirstName() + " "
                                + bookmarkHolder.getLastName();
                    } catch (NullPointerException ex) {
                        bookmarkHolderName = "Database Error";
                    }
                    resultTableEventsDatabase.setValueAt(bookmarkHolderName, i, 7);
                    resultTableEventsDatabase.setValueAt(currentBookmark.getAccessRights().toString(), i, 8);
                    resultTableEventsDatabase.setValueAt(currentBookmark.getBookmarkNumber(), i, 9);

                    //Time in millis for filtering.
                    resultTableEventsDatabase.setValueAt(currentBookmark.getStartTime().getTime(), i, 10);
                }

                //Hide key and time in millis.
                resultTableEventsDatabase.getColumnModel().getColumn(9).setMinWidth(0);
                resultTableEventsDatabase.getColumnModel().getColumn(9).setMaxWidth(0);
                resultTableEventsDatabase.getColumnModel().getColumn(9).setWidth(0);
                resultTableEventsDatabase.getColumnModel().getColumn(10).setMinWidth(0);
                resultTableEventsDatabase.getColumnModel().getColumn(10).setMaxWidth(0);
                resultTableEventsDatabase.getColumnModel().getColumn(10).setWidth(0);
            }
        };
        loader.execute();

        filterEventsDatabase();
        resultTableEventsDatabase.revalidate();
        this.validate();
    }
    
    MouseAdapter mouseClickBookmarkDatabase = new MouseAdapter() {

        /**
         * Checks to see if the mouse was clicked.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
             //Clear error Label
            errorBookmarkDatabaseLabel.setVisible(false);
            
            if (e.getClickCount() == 1) {
                int bookmarkDatabaseUniqueID = Integer.parseInt(resultTableBookmarksDatabase.getValueAt(
                        resultTableBookmarksDatabase.getSelectedRow(), 9).toString());
                for (Bookmark bi : bookmarksDatabase) {
                    if (bi.getBookmarkNumber() == bookmarkDatabaseUniqueID) {
                        bookmarkToOpenDatabase = bi;
                        break;
                    }
                }
            }
            if (e.getClickCount() == 1 && resultTableBookmarksDatabase.getSelectedColumn() == 0) {
                boolean selected = Boolean.parseBoolean(resultTableBookmarksDatabase.getValueAt(
                        resultTableBookmarksDatabase.getSelectedRow(), 0).toString());
                if (selected) {
                    resultTableBookmarksDatabase.setValueAt(false, 
                            resultTableBookmarksDatabase.getSelectedRow(), 0);
                    selectedBookmarksDatabase.remove(bookmarkToOpenDatabase);
                    bookmarkToOpenDatabase = null;
                } else {
                    resultTableBookmarksDatabase.setValueAt(true, resultTableBookmarksDatabase.getSelectedRow(), 0);
                    selectedBookmarksDatabase.add(bookmarkToOpenDatabase);
                }
            }
            if (e.getClickCount() == 2) {
                if(isSelectPhenomenonWindow)
                    return;
                int bookmarkDatabaseUniqueID = Integer.parseInt(resultTableBookmarksDatabase.getValueAt(
                        resultTableBookmarksDatabase.getSelectedRow(), 9).toString());
                for (Bookmark bi : bookmarksDatabase) {
                    if (bi.getBookmarkNumber() == bookmarkDatabaseUniqueID) {
                        bookmarkToOpenDatabase = bi;
                        break;
                    }
                }
                if (bookmarkToOpenDatabase != null) {
                    errorBookmarkDatabaseLabel.setVisible(false);
                    setModalityType(ModalityType.MODELESS);
                    if(!isManageWindow){
                        try {
                            new OpenBookmarkInstanceWindow(appControl, bookmarkToOpenDatabase, controller).
                                    setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                        } catch (WeatherException ex) {
                            WeatherLogger.log(Level.SEVERE, ex.toString());
                        }
                    }else{
                        new BookmarkAddEditWindow(appControl, 
                                bookmarkToOpenDatabase, station, controller,
                                false, false, false);
                        updateAllTables(); //Show Edit
                    } 
                    bookmarkToOpenDatabase = null;
                }
            }
        }
    };

     MouseAdapter mouseClickEventsDatabase = new MouseAdapter() {

        /**
         * Checks to see if the mouse was clicked.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //Clear error Label
            errorEventsDatabaseLabel.setVisible(false);
            
            if (e.getClickCount() == 1) {
                int uniqueID = Integer.parseInt(resultTableEventsDatabase.getValueAt(
                        resultTableEventsDatabase.getSelectedRow(), 9).toString());
                for (Bookmark bi : eventsDatabase) {
                    if (bi.getBookmarkNumber() == uniqueID) {
                        eventToOpenDatabase = bi;
                        break;
                    }
                }
            }
            if (e.getClickCount() == 1 && resultTableEventsDatabase.getSelectedColumn() == 0) {
                boolean selected = Boolean.parseBoolean(resultTableEventsDatabase.getValueAt(
                        resultTableEventsDatabase.getSelectedRow(), 0).toString());
                if (selected) {
                    resultTableEventsDatabase.setValueAt(false, resultTableEventsDatabase.getSelectedRow(), 0);
                    selectedEventsDatabase.remove(eventToOpenDatabase);
                    eventToOpenDatabase = null;
                } else {
                    resultTableEventsDatabase.setValueAt(true, resultTableEventsDatabase.getSelectedRow(), 0);
                    selectedEventsDatabase.add(eventToOpenDatabase);
                    if(!isManageWindow){
                        makeOnlySelection(resultTableEventsDatabase, resultTableEventsDatabase.getSelectedRow());   //Unselect other items.
                    }
                }
            }
            if (e.getClickCount() == 2) {
                if(isSelectPhenomenonWindow)
                    return;
                int uniqueID = Integer.parseInt(resultTableEventsDatabase.getValueAt(
                        resultTableEventsDatabase.getSelectedRow(), 9).toString());
                for (Bookmark bi : eventsDatabase) {
                    if (bi.getBookmarkNumber() == uniqueID) {
                        eventToOpenDatabase = bi;
                        break;
                    }
                }
                if (eventToOpenDatabase != null) {
                    errorEventsDatabaseLabel.setVisible(false);
                    setModalityType(ModalityType.MODELESS);
                    if(isManageWindow){
                         new BookmarkAddEditWindow(appControl, 
                                 eventToOpenDatabase, station, controller, true,
                                 false, false);
                         updateAllTables();   //Show edit.
                    }else{
                         openEventsDatabasekButton.doClick();
                    }
                    eventToOpenDatabase = null;
                }
            }
        }
    };
     
    MouseAdapter mouseClickBookmarkLocal = new MouseAdapter() {
        /**
         * Checks to see if the mouse was clicked.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
             //Clear error Label
            errorBookmarkLocalLabel.setVisible(false);
            
            if (e.getClickCount() == 1) {
                String bookmarkLocalUniqueID = bookmarkLocalResultTable.getValueAt(
                        bookmarkLocalResultTable.getSelectedRow(), 9).toString();
                for (Bookmark bi : bookmarksLocal) {
                    String biUniqeKey = bi.getName() + bi.getStartTime();
                    if (biUniqeKey.equals(bookmarkLocalUniqueID)) {
                        bookmarkToOpenLocal = bi;
                        break;
                    }
                }
            }
            if (e.getClickCount() == 1 && bookmarkLocalResultTable.getSelectedColumn() == 0) {
                boolean selected = Boolean.parseBoolean(bookmarkLocalResultTable.getValueAt(
                        bookmarkLocalResultTable.getSelectedRow(), 0).toString());
                if (selected) {
                    bookmarkLocalResultTable.setValueAt(false, bookmarkLocalResultTable.getSelectedRow(), 0);
                    selectedBookmarksLocal.remove(bookmarkToOpenLocal);
                    bookmarkToOpenLocal = null;
                } else {
                    bookmarkLocalResultTable.setValueAt(true, bookmarkLocalResultTable.getSelectedRow(), 0);
                    selectedBookmarksLocal.add(bookmarkToOpenLocal);
                }
            }
            if (e.getClickCount() == 2) {
                if(isSelectPhenomenonWindow)
                    return;
                String bookmarkLocalUniqueID = bookmarkLocalResultTable.getValueAt(
                        bookmarkLocalResultTable.getSelectedRow(), 9).toString();
                for (Bookmark bi : bookmarksLocal) {
                    String biUniqeKey = bi.getName() + bi.getStartTime();
                    if (biUniqeKey.equals(bookmarkLocalUniqueID)) {
                        bookmarkToOpenLocal = bi;
                        break;
                    }
                }
                if (bookmarkToOpenLocal != null) {
                    errorBookmarkLocalLabel.setVisible(false);
                    setModalityType(ModalityType.MODELESS);
                    if(!isManageWindow){
                        try {   
                            new OpenBookmarkInstanceWindow(appControl, bookmarkToOpenLocal, controller).
                                    setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE); 
                        } catch (WeatherException ex) {
                            WeatherLogger.log(Level.SEVERE, ex.toString());
                        }
                    }else{
                        new BookmarkAddEditWindow(appControl, 
                                bookmarkToOpenLocal, station, controller, false, 
                                true, false);
                        updateAllTables();  //Show edit.
                    }
                    bookmarkToOpenLocal = null;
                }
            }
        }
    };
    MouseAdapter mouseClickEventsLocal = new MouseAdapter() {
        /**
         * Checks to see if the mouse was clicked.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
             //Clear error Label
            errorEventsLocalLabel.setVisible(false);
            
            if (e.getClickCount() == 1) {
                String eventsLocalUniqueID = eventsLocalResultTable.getValueAt(
                        eventsLocalResultTable.getSelectedRow(), 9).toString();
                for (Bookmark bi : eventsLocal) {
                    String biUniqeKey = bi.getName() + bi.getStartTime();
                    if (biUniqeKey.equals(eventsLocalUniqueID)) {
                        eventToOpenLocal = bi;
                        break;
                    }
                }
            }
            if (e.getClickCount() == 1 && eventsLocalResultTable.getSelectedColumn() == 0) {
                boolean selected = Boolean.parseBoolean(eventsLocalResultTable.getValueAt(
                        eventsLocalResultTable.getSelectedRow(), 0).toString());
                if (selected) {
                    eventsLocalResultTable.setValueAt(false, eventsLocalResultTable.getSelectedRow(), 0);
                    selectedEventsLocal.remove(eventToOpenLocal);
                    eventToOpenLocal = null;
                } else {
                    eventsLocalResultTable.setValueAt(true, eventsLocalResultTable.getSelectedRow(), 0);
                    selectedEventsLocal.add(eventToOpenLocal);
                    if(!isManageWindow){
                        makeOnlySelection(eventsLocalResultTable, eventsLocalResultTable.getSelectedRow());  //Unselect other items.
                    }
                }
            }
            if (e.getClickCount() == 2) {
                if(isSelectPhenomenonWindow)
                    return;
                String eventsLocalUniqueID  = eventsLocalResultTable.getValueAt(
                        eventsLocalResultTable.getSelectedRow(), 9).toString();
                for (Bookmark bi : eventsLocal) {
                    String biUniqeKey = bi.getName() + bi.getStartTime();
                    if (biUniqeKey.equals(eventsLocalUniqueID )) {
                        eventToOpenLocal = bi;
                        break;
                    }
                }
                if (eventToOpenLocal != null) {
                    errorEventsLocalLabel.setVisible(false);
                        if(isManageWindow){
                            new BookmarkAddEditWindow(appControl, 
                                    eventToOpenLocal, station, controller, true, 
                                    true, false);
                            updateAllTables(); //Show edit.
                        }else{
                            openEventLocalButton.doClick();
                        }
                        eventToOpenLocal = null;
                }
            }
        }
    };
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        mainDisplayjTabbedPane = new javax.swing.JTabbedPane();
        bookmarksDatabasePanel = new javax.swing.JPanel();
        resultBookmarksDatabaseScrollPane = new javax.swing.JScrollPane();
        resultTableBookmarksDatabase = new javax.swing.JTable();
        closeBookmarksDatabaseButton = new javax.swing.JButton();
        bookmarksDatabaseButtonPanel = new javax.swing.JPanel();
        selectAllBookmarksDatabaseButton = new javax.swing.JButton();
        openBookmarDatabasekButton = new javax.swing.JButton();
        searchBookmarksDatabaseButton = new javax.swing.JButton();
        filterCategoryBookmarksDatabaseComboBox = new javax.swing.JComboBox<String>();
        advancedOptionsBookamrksDatabaseButton = new javax.swing.JButton();
        addBookmarkBookmarkDatabaseButton = new javax.swing.JButton();
        removedBookmarkDatabaseButton = new javax.swing.JButton();
        manageCategoriesBDButton = new javax.swing.JButton();
        selectPhenomenonBmDbButton = new javax.swing.JButton();
        errorBookmarkDatabaseLabel = new javax.swing.JLabel();
        bookmarkLocalPanel = new javax.swing.JPanel();
        resultScrollPane1 = new javax.swing.JScrollPane();
        bookmarkLocalResultTable = new javax.swing.JTable();
        closeBookmarkLocalButton = new javax.swing.JButton();
        bookmarksLocalButtonPanel = new javax.swing.JPanel();
        selectAllBookmarkLocalButton = new javax.swing.JButton();
        openBookmarkLocalButton = new javax.swing.JButton();
        searchBookmarkLocalButton = new javax.swing.JButton();
        filterCategoryBookmarkLocalComboBox = new javax.swing.JComboBox<String>();
        advancedOptionsBookamrksLocalButton = new javax.swing.JButton();
        addBookmarkBookmarskLocalButton = new javax.swing.JButton();
        removeBookmarkLocalButton = new javax.swing.JButton();
        manageCategoriesBLButton = new javax.swing.JButton();
        selectPhenomenonBmLocalButton = new javax.swing.JButton();
        findLocalBookmarkButton = new javax.swing.JButton();
        errorBookmarkLocalLabel = new javax.swing.JLabel();
        eventsDatabasePanel = new javax.swing.JPanel();
        resultEventsDatabaseScrollPane = new javax.swing.JScrollPane();
        resultTableEventsDatabase = new javax.swing.JTable();
        closeEventsDatabaseButton = new javax.swing.JButton();
        eventsDatabaseButtonPanel = new javax.swing.JPanel();
        selectAllEventsDatabaseButton = new javax.swing.JButton();
        openEventsDatabasekButton = new javax.swing.JButton();
        searchEventsDatabaseButton = new javax.swing.JButton();
        filterCategoryEventsDatabaseComboBox = new javax.swing.JComboBox<String>();
        advancedOptionsEventsDatabaseButton = new javax.swing.JButton();
        addBookmarkEventsDBButton = new javax.swing.JButton();
        removedEventDatabaseButton1 = new javax.swing.JButton();
        manageCategoriesEDButton = new javax.swing.JButton();
        selectPhenomenonEvDbButton = new javax.swing.JButton();
        errorEventsDatabaseLabel = new javax.swing.JLabel();
        eventsLocalPanel = new javax.swing.JPanel();
        EventsLocalResultScrollPane = new javax.swing.JScrollPane();
        eventsLocalResultTable = new javax.swing.JTable();
        closeEventsLocalButton = new javax.swing.JButton();
        eventsLocalButtonPanel = new javax.swing.JPanel();
        selectAllEventsLocalButton = new javax.swing.JButton();
        openEventLocalButton = new javax.swing.JButton();
        searchEventLocalButton = new javax.swing.JButton();
        filterCategoryEventLocalComboBox = new javax.swing.JComboBox<String>();
        advancedOptionsEventsLocalButton = new javax.swing.JButton();
        addBookmarkEventsLocalButton = new javax.swing.JButton();
        removeEventLocalButton = new javax.swing.JButton();
        manageCategoriesELButton = new javax.swing.JButton();
        selectPhenomenonEvLocalButton = new javax.swing.JButton();
        findLocalEventButton = new javax.swing.JButton();
        errorEventsLocalLabel = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setResizable(false);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainDisplayjTabbedPane.setPreferredSize(new java.awt.Dimension(976, 400));
        mainDisplayjTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainDisplayjTabbedPaneStateChanged(evt);
            }
        });

        bookmarksDatabasePanel.setMaximumSize(new java.awt.Dimension(976, 370));
        bookmarksDatabasePanel.setMinimumSize(new java.awt.Dimension(976, 370));
        bookmarksDatabasePanel.setPreferredSize(new java.awt.Dimension(976, 370));
        bookmarksDatabasePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resultTableBookmarksDatabase.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bookmark", "Category", "Category Type", "Rank Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultBookmarksDatabaseScrollPane.setViewportView(resultTableBookmarksDatabase);

        bookmarksDatabasePanel.add(resultBookmarksDatabaseScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 952, 270));

        closeBookmarksDatabaseButton.setText("Close");
        closeBookmarksDatabaseButton.setToolTipText("Closes this window");
        closeBookmarksDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksDatabasePanel.add(closeBookmarksDatabaseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(901, 308, -1, -1));

        bookmarksDatabaseButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectAllBookmarksDatabaseButton.setText("Select All");
        selectAllBookmarksDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(selectAllBookmarksDatabaseButton);

        openBookmarDatabasekButton.setText("Open Bookmark(s)");
        openBookmarDatabasekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBookmarDatabasekButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(openBookmarDatabasekButton);

        searchBookmarksDatabaseButton.setText("Search Again");
        searchBookmarksDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(searchBookmarksDatabaseButton);

        filterCategoryBookmarksDatabaseComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Category" }));
        filterCategoryBookmarksDatabaseComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCategoryBookmarksDatabaseComboBoxActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(filterCategoryBookmarksDatabaseComboBox);

        advancedOptionsBookamrksDatabaseButton.setText("Advanced Options");
        advancedOptionsBookamrksDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedOptionsBookamrksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(advancedOptionsBookamrksDatabaseButton);

        addBookmarkBookmarkDatabaseButton.setText("Add Bookmark/Event");
        addBookmarkBookmarkDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookmarkBookmarkDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(addBookmarkBookmarkDatabaseButton);

        removedBookmarkDatabaseButton.setText("Remove Bookmark(s)");
        removedBookmarkDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removedBookmarkDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(removedBookmarkDatabaseButton);

        manageCategoriesBDButton.setText("Manage Categories/Sub-Categories");
        manageCategoriesBDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageCategoriesBDButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(manageCategoriesBDButton);

        selectPhenomenonBmDbButton.setText("Select Phenomenon");
        selectPhenomenonBmDbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPhenomenonBmDbButtonActionPerformed(evt);
            }
        });
        bookmarksDatabaseButtonPanel.add(selectPhenomenonBmDbButton);

        errorBookmarkDatabaseLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorBookmarkDatabaseLabel.setIcon(IconProperties.getResourceInactiveIconImage());
        errorBookmarkDatabaseLabel.setText("No bookmark was selected");
        bookmarksDatabaseButtonPanel.add(errorBookmarkDatabaseLabel);

        bookmarksDatabasePanel.add(bookmarksDatabaseButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 294, 877, 64));

        mainDisplayjTabbedPane.addTab("Bookmarks Database", bookmarksDatabasePanel);

        bookmarkLocalPanel.setMaximumSize(new java.awt.Dimension(976, 370));
        bookmarkLocalPanel.setMinimumSize(new java.awt.Dimension(976, 370));
        bookmarkLocalPanel.setPreferredSize(new java.awt.Dimension(976, 370));
        bookmarkLocalPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bookmarkLocalResultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bookmark", "Category", "Category Type", "Rank Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultScrollPane1.setViewportView(bookmarkLocalResultTable);

        bookmarkLocalPanel.add(resultScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 952, 270));

        closeBookmarkLocalButton.setText("Close");
        closeBookmarkLocalButton.setToolTipText("Closes this window");
        closeBookmarkLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarkLocalPanel.add(closeBookmarkLocalButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(901, 308, 63, 25));

        bookmarksLocalButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectAllBookmarkLocalButton.setText("Select All");
        selectAllBookmarkLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllBookmarkLocalButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(selectAllBookmarkLocalButton);

        openBookmarkLocalButton.setText("Open Bookmark(s)");
        openBookmarkLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBookmarkLocalButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(openBookmarkLocalButton);

        searchBookmarkLocalButton.setText("Search Again");
        searchBookmarkLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(searchBookmarkLocalButton);

        filterCategoryBookmarkLocalComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Category" }));
        filterCategoryBookmarkLocalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCategoryBookmarkLocalComboBoxActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(filterCategoryBookmarkLocalComboBox);

        advancedOptionsBookamrksLocalButton.setText("Advanced Options");
        advancedOptionsBookamrksLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedOptionsBookamrksDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(advancedOptionsBookamrksLocalButton);

        addBookmarkBookmarskLocalButton.setText("Add Bookmark/Event");
        addBookmarkBookmarskLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookmarkBookmarkDatabaseButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(addBookmarkBookmarskLocalButton);

        removeBookmarkLocalButton.setText("Remove Bookmark(s)");
        removeBookmarkLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBookmarkLocalButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(removeBookmarkLocalButton);

        manageCategoriesBLButton.setText("Manage Categories/Sub-Categories");
        manageCategoriesBLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageCategoriesBDButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(manageCategoriesBLButton);

        selectPhenomenonBmLocalButton.setText("Select Phenomenon");
        selectPhenomenonBmLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPhenomenonBmDbButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(selectPhenomenonBmLocalButton);

        findLocalBookmarkButton.setText("Find Local Bookmark");
        findLocalBookmarkButton.setToolTipText("Open a bookmark found on your local hard disk drive");
        findLocalBookmarkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findLocalBookmarkButtonActionPerformed(evt);
            }
        });
        bookmarksLocalButtonPanel.add(findLocalBookmarkButton);

        errorBookmarkLocalLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorBookmarkLocalLabel.setIcon(IconProperties.getResourceInactiveIconImage());
        errorBookmarkLocalLabel.setText("No bookmark was selected");
        bookmarksLocalButtonPanel.add(errorBookmarkLocalLabel);

        bookmarkLocalPanel.add(bookmarksLocalButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 294, 877, 64));

        mainDisplayjTabbedPane.addTab("Bookmarks Local", bookmarkLocalPanel);

        eventsDatabasePanel.setMaximumSize(new java.awt.Dimension(976, 370));
        eventsDatabasePanel.setMinimumSize(new java.awt.Dimension(976, 370));
        eventsDatabasePanel.setPreferredSize(new java.awt.Dimension(976, 370));
        eventsDatabasePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resultTableEventsDatabase.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bookmark", "Category", "Category Type", "Rank Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultEventsDatabaseScrollPane.setViewportView(resultTableEventsDatabase);

        eventsDatabasePanel.add(resultEventsDatabaseScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 952, 270));

        closeEventsDatabaseButton.setText("Close");
        closeEventsDatabaseButton.setToolTipText("Closes this window");
        closeEventsDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeEventsDatabaseButtonActionPerformed(evt);
            }
        });
        eventsDatabasePanel.add(closeEventsDatabaseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(901, 308, 63, 25));

        eventsDatabaseButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectAllEventsDatabaseButton.setText("Select All");
        selectAllEventsDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllEventsDatabaseButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(selectAllEventsDatabaseButton);

        openEventsDatabasekButton.setText("Open Event");
        openEventsDatabasekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEventsDatabasekButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(openEventsDatabasekButton);

        searchEventsDatabaseButton.setText("Search Again");
        searchEventsDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(searchEventsDatabaseButton);

        filterCategoryEventsDatabaseComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Category" }));
        filterCategoryEventsDatabaseComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCategoryEventsDatabaseComboBoxActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(filterCategoryEventsDatabaseComboBox);

        advancedOptionsEventsDatabaseButton.setText("Advanced Options");
        advancedOptionsEventsDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedOptionsBookamrksDatabaseButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(advancedOptionsEventsDatabaseButton);

        addBookmarkEventsDBButton.setText("Add Bookmark/Event");
        addBookmarkEventsDBButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookmarkBookmarkDatabaseButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(addBookmarkEventsDBButton);

        removedEventDatabaseButton1.setText("Remove Event(s)");
        removedEventDatabaseButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removedEventDatabaseButton1ActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(removedEventDatabaseButton1);

        manageCategoriesEDButton.setText("Manage Categories/Sub-Categories");
        manageCategoriesEDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageCategoriesBDButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(manageCategoriesEDButton);

        selectPhenomenonEvDbButton.setText("Select Phenomenon");
        selectPhenomenonEvDbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPhenomenonBmDbButtonActionPerformed(evt);
            }
        });
        eventsDatabaseButtonPanel.add(selectPhenomenonEvDbButton);

        errorEventsDatabaseLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorEventsDatabaseLabel.setIcon(IconProperties.getResourceInactiveIconImage());
        errorEventsDatabaseLabel.setText("No event was selected");
        eventsDatabaseButtonPanel.add(errorEventsDatabaseLabel);

        eventsDatabasePanel.add(eventsDatabaseButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 294, 877, 64));

        mainDisplayjTabbedPane.addTab("Events Database", eventsDatabasePanel);

        eventsLocalPanel.setMaximumSize(new java.awt.Dimension(976, 370));
        eventsLocalPanel.setMinimumSize(new java.awt.Dimension(976, 370));
        eventsLocalPanel.setPreferredSize(new java.awt.Dimension(976, 370));
        eventsLocalPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        eventsLocalResultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bookmark", "Category", "Category Type", "Rank Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        EventsLocalResultScrollPane.setViewportView(eventsLocalResultTable);

        eventsLocalPanel.add(EventsLocalResultScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 952, 270));

        closeEventsLocalButton.setText("Close");
        closeEventsLocalButton.setToolTipText("Closes this window");
        closeEventsLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeEventsLocalButtoncloseButtonActionPerformed(evt);
            }
        });
        eventsLocalPanel.add(closeEventsLocalButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(901, 308, 63, 25));

        eventsLocalButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectAllEventsLocalButton.setText("Select All");
        selectAllEventsLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllEventsLocalButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(selectAllEventsLocalButton);

        openEventLocalButton.setText("Open Event");
        openEventLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEventLocalButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(openEventLocalButton);

        searchEventLocalButton.setText("Search Again");
        searchEventLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBookmarksDatabaseButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(searchEventLocalButton);

        filterCategoryEventLocalComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Category" }));
        filterCategoryEventLocalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCategoryEventLocalComboBoxActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(filterCategoryEventLocalComboBox);

        advancedOptionsEventsLocalButton.setText("Advanced Options");
        advancedOptionsEventsLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedOptionsBookamrksDatabaseButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(advancedOptionsEventsLocalButton);

        addBookmarkEventsLocalButton.setText("Add Bookmark/Event");
        addBookmarkEventsLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookmarkBookmarkDatabaseButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(addBookmarkEventsLocalButton);

        removeEventLocalButton.setText("Remove Event(s)");
        removeEventLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEventLocalButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(removeEventLocalButton);

        manageCategoriesELButton.setText("Manage Categories/Sub-Categories");
        manageCategoriesELButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageCategoriesBDButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(manageCategoriesELButton);

        selectPhenomenonEvLocalButton.setText("Select Phenomenon");
        selectPhenomenonEvLocalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPhenomenonBmDbButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(selectPhenomenonEvLocalButton);

        findLocalEventButton.setText("Find Local Event");
        findLocalEventButton.setToolTipText("Open a bookmark found on your local hard disk drive");
        findLocalEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findLocalEventButtonActionPerformed(evt);
            }
        });
        eventsLocalButtonPanel.add(findLocalEventButton);

        errorEventsLocalLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorEventsLocalLabel.setIcon(IconProperties.getResourceInactiveIconImage());
        errorEventsLocalLabel.setText("No event was selected");
        eventsLocalButtonPanel.add(errorEventsLocalLabel);

        eventsLocalPanel.add(eventsLocalButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 294, 877, 60));

        mainDisplayjTabbedPane.addTab("Events Local", eventsLocalPanel);

        getContentPane().add(mainDisplayjTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeBookmarksDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBookmarksDatabaseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeBookmarksDatabaseButtonActionPerformed

    private void selectAllBookmarksDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllBookmarksDatabaseButtonActionPerformed
        this.errorBookmarkDatabaseLabel.setVisible(false);
        isSelectAllBookmarksDatabase = !isSelectAllBookmarksDatabase;
        if (isSelectAllBookmarksDatabase) {
            for (int i = 0; i < resultTableBookmarksDatabase.getRowCount(); i++) {
                resultTableBookmarksDatabase.setValueAt(true, i, 0);
            }
            //Get the bookmarks which are selected.
            for (int i = 0; i < bookmarksDatabase.size(); i++) {
                for (int j = 0; j < resultTableBookmarksDatabase.getRowCount(); j++) {
                    int bookmarkNameColumn = 1;
                    int startTimeColumn = 5;
                    //TODO Use bookmark number 
                    if (bookmarksDatabase.get(i).getName().equals(resultTableBookmarksDatabase.getValueAt(j, bookmarkNameColumn))) {
                        if (!selectedBookmarksDatabase.contains(bookmarksDatabase.get(i))) {
                            selectedBookmarksDatabase.add(bookmarksDatabase.get(i));
                        }
                        break;
                    }
                }
            }
            selectAllBookmarksDatabaseButton.setText("Deselect All");
            resultTableBookmarksDatabase.revalidate();
        } else if (!isSelectAllBookmarksDatabase) {
            for (int i = 0; i < resultTableBookmarksDatabase.getRowCount(); i++) {
                resultTableBookmarksDatabase.setValueAt(false, i, 0);
            }
            selectedBookmarksDatabase.removeAllElements();
            bookmarkToOpenDatabase = null;
            selectAllBookmarksDatabaseButton.setText("Select All");
            selectAllBookmarksDatabaseButton.revalidate();
        }
    }//GEN-LAST:event_selectAllBookmarksDatabaseButtonActionPerformed

    private void openBookmarDatabasekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBookmarDatabasekButtonActionPerformed
        if (bookmarkToOpenDatabase == null && selectedBookmarksDatabase.isEmpty()) {
            errorBookmarkDatabaseLabel.setVisible(true);
            return;
        }
        if (selectedBookmarksDatabase.size() > 1) {
            int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                    + "open\nall the selected bookmarks?", "Open Bookmarks", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) {
                return;
            }
        }
        setModalityType(ModalityType.MODELESS); // seems wrong -- 5-4-2013 check
        for (Bookmark b : selectedBookmarksDatabase) {
            try {
                errorBookmarkDatabaseLabel.setVisible(false);
                new OpenBookmarkInstanceWindow(appControl, b, controller).setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
            } catch (WeatherException ex) {
                WeatherLogger.log(Level.SEVERE, ex.toString());
            }
        }
    }//GEN-LAST:event_openBookmarDatabasekButtonActionPerformed

    private void searchBookmarksDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBookmarksDatabaseButtonActionPerformed
        dispose(); // dispose the current window.
        boolean isLocal = evt.getSource() == searchBookmarkLocalButton || evt.getSource() == 
                searchEventLocalButton;
        //Create a new window and move it to the front. 
        new SearchBookmarkDialog(appControl, controller, isLocal, false)
                .toFront();
    }//GEN-LAST:event_searchBookmarksDatabaseButtonActionPerformed
   
    /**
     * Renderer which enables the JTable to filter based on date. Code modified
     * from:
     * <code>
     * http://stackoverflow.com/questions/2412007/format-date-in-jtable-resultset
     * </code>
     */
    TableCellRenderer tableCellDateRenderer = new DefaultTableCellRenderer() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Date) {
                value = dateFormat.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
        }
    };

    private void openBookmarkLocalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBookmarkLocalButtonActionPerformed
        if (bookmarkToOpenLocal == null && selectedBookmarksLocal.isEmpty()) {
            errorBookmarkLocalLabel.setVisible(true);
            return;
        }
        if (selectedBookmarksLocal.size() > 1) {
            int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                    + "open\nall the selected bookmarks?", "Open Bookmarks", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) {
                return;
            }
        }
        setModalityType(ModalityType.MODELESS);//I think this is an error  -- 5-4-2013
        for (Bookmark b : selectedBookmarksLocal) {
            try {
                errorBookmarkLocalLabel.setVisible(false);
                new OpenBookmarkInstanceWindow(appControl, b, controller).setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
            } catch (WeatherException ex) {
                WeatherLogger.log(Level.SEVERE, ex.toString());
            }
        }
    }//GEN-LAST:event_openBookmarkLocalButtonActionPerformed

    private void selectAllBookmarkLocalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllBookmarkLocalButtonActionPerformed
        this.errorBookmarkLocalLabel.setVisible(false);
        isSelectAllBookmarksLocal = !isSelectAllBookmarksLocal;
        if (isSelectAllBookmarksLocal) {
            for (int i = 0; i < bookmarkLocalResultTable.getRowCount(); i++) {
                bookmarkLocalResultTable.setValueAt(true, i, 0);
            }
            //Get the bookmarks which are selected.
            for (int i = 0; i < bookmarksLocal.size(); i++) {
                for (int j = 0; j < bookmarkLocalResultTable.getRowCount(); j++) {
                    int nameColumn = 1;
                    int startTimeColumn = 5;
                    if (bookmarksLocal.get(i).getName().equals(bookmarkLocalResultTable.getValueAt(j, nameColumn))) {
                        //Can't use Contains to check because they all have the same ID
                        boolean isAlreadySelected = false;
                        for (int k = 0; k < selectedBookmarksLocal.size(); k++) {
                            if (selectedBookmarksLocal.get(k).getName().equals(bookmarksLocal.get(i).getName())
                                    && selectedBookmarksLocal.get(k).getStartTime().equals(bookmarksLocal.get(i).getStartTime())) {
                                isAlreadySelected = true;
                            }
                        }
                        if (!isAlreadySelected) {
                            selectedBookmarksLocal.add(bookmarksLocal.get(i));
                        }
                        break;
                    }
                }
            }
            selectAllBookmarkLocalButton.setText("Deselect All");
            selectAllBookmarkLocalButton.revalidate();
        } else if (!isSelectAllBookmarksDatabase) {
            for (int i = 0; i < bookmarkLocalResultTable.getRowCount(); i++) {
                bookmarkLocalResultTable.setValueAt(false, i, 0);
            }
            selectedBookmarksLocal.removeAllElements();
            bookmarkToOpenLocal = null;
            selectAllBookmarkLocalButton.setText("Select All");
            selectAllBookmarkLocalButton.revalidate();
        }
    }//GEN-LAST:event_selectAllBookmarkLocalButtonActionPerformed

    private void filterCategoryBookmarkLocalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCategoryBookmarkLocalComboBoxActionPerformed
        areAdvancedOptionsSelected = false;                 //Clear advanced options.
        selectedCategoryBookmarksLocal = (String) filterCategoryBookmarkLocalComboBox.getSelectedItem();
        this.updateBookmarksLocalResultTable();
    }//GEN-LAST:event_filterCategoryBookmarkLocalComboBoxActionPerformed

    private void filterCategoryBookmarksDatabaseComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCategoryBookmarksDatabaseComboBoxActionPerformed
        areAdvancedOptionsSelected = false;                 //Clear advanced options.
        externalBookmarks = bookmarksManager.obtainAll();   //Make all records available.
        selectedCategoryBookmarksDatabase = (String) filterCategoryBookmarksDatabaseComboBox.getSelectedItem();
        this.updateBookmarksDatabaseTable();
    }//GEN-LAST:event_filterCategoryBookmarksDatabaseComboBoxActionPerformed

    private void selectAllEventsLocalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllEventsLocalButtonActionPerformed
        this.errorEventsLocalLabel.setVisible(false);
        isSelectAllEventsLocal = !isSelectAllEventsLocal;
        if (isSelectAllEventsLocal) {
            for (int i = 0; i < eventsLocalResultTable.getRowCount(); i++) {
                eventsLocalResultTable.setValueAt(true, i, 0);
            }
            //Get the bookmarks which are selected.
            for (int i = 0; i < eventsLocal.size(); i++) {
                for (int j = 0; j < eventsLocalResultTable.getRowCount(); j++) {
                    int nameColumn = 1;
                    int startTimeColumn = 5;
                    if (eventsLocal.get(i).getName().equals(eventsLocalResultTable.getValueAt(j, nameColumn))) {
                        //Can't use Contains to check because they all have the same ID
                        boolean isAlreadySelected = false;
                        for (int k = 0; k < selectedEventsLocal.size(); k++) {
                            if (selectedEventsLocal.get(k).getName().equals(eventsLocal.get(i).getName())
                                    && selectedEventsLocal.get(k).getStartTime().equals(eventsLocal.get(i).getStartTime())) {
                                isAlreadySelected = true;
                            }
                        }
                        if (!isAlreadySelected) {
                            selectedEventsLocal.add(eventsLocal.get(i));
                            eventToOpenLocal = eventsLocal.get(i);
                        }
                        break;
                    }
                }
            }
            selectAllEventsLocalButton.setText("Deselect All");
            selectAllEventsLocalButton.revalidate();
        } else if (!isSelectAllEventsLocal) {
            for (int i = 0; i < eventsLocalResultTable.getRowCount(); i++) {
               eventsLocalResultTable.setValueAt(false, i, 0);
            }
            selectedEventsLocal.removeAllElements();
            eventToOpenLocal = null;
            selectAllEventsLocalButton.setText("Select All");
            selectAllEventsLocalButton.revalidate();
        }
    }//GEN-LAST:event_selectAllEventsLocalButtonActionPerformed

    private void openEventLocalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEventLocalButtonActionPerformed
        if (eventToOpenLocal == null) {
            errorEventsLocalLabel.setVisible(true);
            return;
        }
        errorBookmarkLocalLabel.setVisible(false);
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "open\n the selected Event?", "Open Event?", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            ResourceRange mainRange = new ResourceRange(eventToOpenLocal
                    .getStartTime(), eventToOpenLocal.getEndTime());
            BookmarkEventOpener.openBooekmark(appControl, eventToOpenLocal,
                    controller, mainRange);
            this.dispose();
        } 
    }//GEN-LAST:event_openEventLocalButtonActionPerformed

    private void filterCategoryEventLocalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCategoryEventLocalComboBoxActionPerformed
        areAdvancedOptionsSelected = false;                 //Clear advanced options.
        selectedCategoryEventsLocal = (String) filterCategoryEventLocalComboBox.getSelectedItem();
        this.updateEventsLocalResultTable();
    }//GEN-LAST:event_filterCategoryEventLocalComboBoxActionPerformed

    private void closeEventsLocalButtoncloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeEventsLocalButtoncloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeEventsLocalButtoncloseButtonActionPerformed

    private void selectAllEventsDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllEventsDatabaseButtonActionPerformed
        this.errorEventsDatabaseLabel.setVisible(false);
        isSelectAllEventsDatabase = !isSelectAllEventsDatabase;
        if (isSelectAllEventsDatabase) {
            for (int i = 0; i < resultTableEventsDatabase.getRowCount(); i++) {
                resultTableEventsDatabase.setValueAt(true, i, 0);
            }
            //Get the bookmarks which are selected.
            for (int i = 0; i < eventsDatabase.size(); i++) {
                for (int j = 0; j < resultTableEventsDatabase.getRowCount(); j++) {
                    int bookmarkNameColumn = 1;
                    //TODO Use bookmark number here.
                    if (eventsDatabase.get(i).getName().equals(
                        resultTableEventsDatabase.getValueAt(j, bookmarkNameColumn))) {
                        if (!selectedEventsDatabase.contains(eventsDatabase.get(i))) {
                            selectedEventsDatabase.add(eventsDatabase.get(i));
                        }
                        break;
                    }
                }
            }
            if(selectedEventsDatabase.size() > 0)
                eventToOpenDatabase = eventsDatabase.get(0);
            selectAllEventsDatabaseButton.setText("Deselect All");
            resultTableEventsDatabase.revalidate();
        } else if (!isSelectAllEventsDatabase) {
            for (int i = 0; i < resultTableEventsDatabase.getRowCount(); i++) {
                resultTableEventsDatabase.setValueAt(false, i, 0);
            }
            selectedEventsDatabase.removeAllElements();
            eventToOpenDatabase = null;
            selectAllEventsDatabaseButton.setText("Select All");
            selectAllEventsDatabaseButton.revalidate();
        }
    }//GEN-LAST:event_selectAllEventsDatabaseButtonActionPerformed

    private void openEventsDatabasekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEventsDatabasekButtonActionPerformed
        if (eventToOpenDatabase == null) {
            errorEventsDatabaseLabel.setVisible(true);
            return;
        }
        errorEventsDatabaseLabel.setVisible(false);
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "open\n the selected Event?", "Open Event?", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION)  {
            ResourceRange mainRange = new ResourceRange(eventToOpenDatabase
                   .getStartTime(), eventToOpenDatabase.getEndTime());
            BookmarkEventOpener.openBooekmark(appControl, eventToOpenDatabase,
                    controller, mainRange);
            this.dispose();
        } 
    }//GEN-LAST:event_openEventsDatabasekButtonActionPerformed

    private void closeEventsDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeEventsDatabaseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeEventsDatabaseButtonActionPerformed

    private void advancedOptionsBookamrksDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedOptionsBookamrksDatabaseButtonActionPerformed
        Debug.println("Start of advancedOptionsBookamrksDatabaseButtonActionPerformed action listener ");
        Object source = evt.getSource();
        ArrayList<String> subCategories = new ArrayList<String>();
        Vector<BookmarkType> subCategoriesVector = types.obtainAll();

        BookmarkCategory currentCategory = null;

        if (source.equals(advancedOptionsBookamrksDatabaseButton)) {
            currentCategory = categories.searchByName(selectedCategoryBookmarksDatabase);
        } else if (source.equals(advancedOptionsBookamrksLocalButton)) {
            currentCategory = categories.searchByName(selectedCategoryBookmarksLocal);
        } else if (source.equals(advancedOptionsEventsLocalButton)) {
            currentCategory = categories.searchByName(selectedCategoryEventsLocal);
        } else if (source.equals(advancedOptionsEventsDatabaseButton)) {
            currentCategory = categories.searchByName(selectedCategoryEventsDatabase);
        }

        if (currentCategory != null) {  //Not "Any Category"
            subCategoriesVector = appControl.getAdministratorControlSystem().getGeneralService().getDBMSSystem().getBookmarkTypesManager().
                    obtainAll(currentCategory.getBookmarkCategoryNumber());
            for (BookmarkType bt : subCategoriesVector) {
                subCategories.add(bt.getName());
            }
        }

        Debug.println("Got information from database");
        setModalityType(ModalityType.APPLICATION_MODAL); //should allow child window to have focus
        Debug.println("Set current window to application modal, about to open advanced search window");
        if (source.equals(advancedOptionsBookamrksDatabaseButton)) {
            //Show window - code hangs here
            new AdvancedSearchBookmarkDialog(this, controller, subCategories);
            
            this.updateBookmarksDatabaseTable();
            Debug.println("Advanced search window closed");
        } else if (source.equals(advancedOptionsBookamrksLocalButton)) {
            //Show window - code hangs here
            new AdvancedSearchBookmarkDialog(this, controller, subCategories);

            this.updateBookmarksLocalResultTable();
            Debug.println("Advanced search window closed");
        } else if (source.equals(advancedOptionsEventsLocalButton)) {
            //Show window - code hangs here
            new AdvancedSearchBookmarkDialog(this, controller, subCategories);

            this.updateEventsLocalResultTable();
            Debug.println("Advanced search window closed");
        } else if (source.equals(advancedOptionsEventsDatabaseButton)) {
            //Show window - code hangs here
            new AdvancedSearchBookmarkDialog(this, controller, subCategories);

            this.updateEventsDatabaseTable();
            Debug.println("Advanced search window closed");
        }
    }//GEN-LAST:event_advancedOptionsBookamrksDatabaseButtonActionPerformed

    private void addBookmarkBookmarkDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBookmarkBookmarkDatabaseButtonActionPerformed
        boolean isLocal = evt.getSource() == this.addBookmarkBookmarskLocalButton  ||
                evt.getSource() == this.addBookmarkEventsLocalButton;
        boolean isEvent = evt.getSource() == this.addBookmarkEventsDBButton ||
                evt.getSource() == this.addBookmarkEventsLocalButton;
        new BookmarkAddEditWindow(appControl, station, controller, isEvent, 
                isLocal, false);
        updateAllTables();
    }//GEN-LAST:event_addBookmarkBookmarkDatabaseButtonActionPerformed

    private void removedBookmarkDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removedBookmarkDatabaseButtonActionPerformed
        if (bookmarkToOpenDatabase == null && selectedBookmarksDatabase.isEmpty()){
            errorBookmarkDatabaseLabel.setVisible(true);
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "remove\nthe selected bookmarks?", "Delete Bookmarks", JOptionPane.YES_NO_OPTION);
        if(ans != JOptionPane.YES_OPTION) return;
        errorBookmarkDatabaseLabel.setVisible(false);
        if(!selectedBookmarksDatabase.isEmpty()){
            for (Bookmark b:selectedBookmarksDatabase){
                bookmarksManager.removeOne(b);
                /*for(InstructorFileInstance file : fileManager.
                        getFilesForBookmark(b.getBookmarkNumber()))
                    fileManager.removeFile(file.getFileNumber());*/
            }
        }
        else{
            bookmarksManager.removeOne(bookmarkToOpenDatabase);
            /*for(InstructorFileInstance file : fileManager.getFilesForBookmark(
                    bookmarkToOpenDatabase.getBookmarkNumber()))
                fileManager.removeFile(file.getFileNumber());*/
        }
        updateBookmarksDatabaseTable();
        JOptionPane.showMessageDialog(this, "The selected bookmark(s)"
                + " have been successfully\nremoved.", "Bookmarks Deleted",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_removedBookmarkDatabaseButtonActionPerformed

    private void removedEventDatabaseButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removedEventDatabaseButton1ActionPerformed
    if (eventToOpenDatabase == null && selectedEventsDatabase.isEmpty()){
            errorEventsDatabaseLabel.setVisible(true);
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "remove\nthe selected events?", "Delete Events", JOptionPane.YES_NO_OPTION);
        if(ans != JOptionPane.YES_OPTION) return;
        errorEventsDatabaseLabel.setVisible(false);
        if(!selectedEventsDatabase.isEmpty()){
            for (Bookmark b:selectedEventsDatabase){
                bookmarksManager.removeOne(b);
                /*for(InstructorFileInstance file : fileManager.
                        getFilesForBookmark(b.getBookmarkNumber()))
                    fileManager.removeFile(file.getFileNumber());*/
            }
        }
        else{
            bookmarksManager.removeOne(eventToOpenDatabase);
            /*for(InstructorFileInstance file : fileManager.getFilesForBookmark(
                    eventToOpenDatabase.getBookmarkNumber()))
                fileManager.removeFile(file.getFileNumber());*/
        }
        updateEventsDatabaseTable();
        JOptionPane.showMessageDialog(this, "The selected event(s)"
                + " have been successfully\nremoved.", "Event(s) Deleted",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_removedEventDatabaseButton1ActionPerformed

    private void manageCategoriesBDButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageCategoriesBDButtonActionPerformed
        formFocusLost(null);    //Force to clear labels.
        new ManageBookmarkCategoryDialog(appControl, false);
    }//GEN-LAST:event_manageCategoriesBDButtonActionPerformed

    private void removeBookmarkLocalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBookmarkLocalButtonActionPerformed
        if (bookmarkToOpenLocal == null && selectedBookmarksLocal.isEmpty()){
            errorBookmarkLocalLabel.setVisible(true);
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "remove\nthe selected bookmarks?", "Delete Bookmarks", JOptionPane.YES_NO_OPTION);
        if(ans != JOptionPane.YES_OPTION) return;
        errorBookmarkLocalLabel.setVisible(false);
        if(!selectedBookmarksLocal.isEmpty()){
            for (Bookmark b:selectedBookmarksLocal){
                new File(PropertyManager.getBookmarkProperty(Integer.
                        toString(b.getBookmarkNumber()))).delete();
                PropertyManager.deleteBookmarkProperty(Integer.
                        toString(b.getBookmarkNumber()));
            }
        }
        else{
            new File(PropertyManager.getBookmarkProperty(Integer.
                    toString(bookmarkToOpenLocal.getBookmarkNumber()))).
                    delete();
            PropertyManager.deleteBookmarkProperty(Integer.
                    toString(bookmarkToOpenLocal.getBookmarkNumber()));
        }
        updateBookmarksLocalResultTable();
        JOptionPane.showMessageDialog(this, "The selected bookmark(s)"
                + " have been successfully\nremoved.", "Bookmarks Deleted",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_removeBookmarkLocalButtonActionPerformed

    private void removeEventLocalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEventLocalButtonActionPerformed
        if (eventToOpenLocal == null && selectedEventsLocal.isEmpty()){
            errorEventsLocalLabel.setVisible(true);
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to "
                + "remove\nthe selected events?", "Delete Events", JOptionPane.YES_NO_OPTION);
        if(ans != JOptionPane.YES_OPTION) return;
        errorEventsLocalLabel.setVisible(false);
        if(!selectedEventsLocal.isEmpty()){
            for (Bookmark b:selectedEventsLocal){
                new File(PropertyManager.getBookmarkProperty(Integer.
                        toString(b.getBookmarkNumber()))).delete();
                PropertyManager.deleteBookmarkProperty(Integer.
                        toString(b.getBookmarkNumber()));
            }
        }
        else{
            new File(PropertyManager.getBookmarkProperty(Integer.
                    toString(eventToOpenLocal.getBookmarkNumber()))).
                    delete();
            PropertyManager.deleteBookmarkProperty(Integer.
                    toString(eventToOpenLocal.getBookmarkNumber()));
        }
        updateEventsLocalResultTable();
        JOptionPane.showMessageDialog(this, "The selected event(s)"
                + " have been successfully\nremoved.", "Events Deleted",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_removeEventLocalButtonActionPerformed

    private void filterCategoryEventsDatabaseComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCategoryEventsDatabaseComboBoxActionPerformed
        areAdvancedOptionsSelected = false;                 //Clear advanced options.
        externalBookmarks = bookmarksManager.obtainAll();   //Make all records available.
        selectedCategoryEventsDatabase = (String) filterCategoryEventsDatabaseComboBox.getSelectedItem();
        this.updateEventsDatabaseTable();
    }//GEN-LAST:event_filterCategoryEventsDatabaseComboBoxActionPerformed

    private void selectPhenomenonBmDbButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPhenomenonBmDbButtonActionPerformed
        String tooManyPhenomenonsSelected = "Only one Phenomenon can be selected.";
        String onePhenomenonMustBeSelected = "At least one Phenomenon must be selected.";
        String defaultErrorMessage = "No bookmark was selected.";
        if(mainDisplayjTabbedPane.getSelectedIndex() == 0)
        {
            if(selectedBookmarksDatabase.size() > 1){
                errorBookmarkDatabaseLabel.setText(tooManyPhenomenonsSelected);
                errorBookmarkDatabaseLabel.setVisible(true);
            }
            else if(bookmarkToOpenDatabase == null){
                errorBookmarkDatabaseLabel.setText(onePhenomenonMustBeSelected);
                errorBookmarkDatabaseLabel.setVisible(true);
            }
            else{ 
                selectedPhenomenon = bookmarkToOpenDatabase;
                errorBookmarkDatabaseLabel.setText(defaultErrorMessage);
                errorBookmarkDatabaseLabel.setVisible(false);
                this.setVisible(false);
            }
        }
        else if(mainDisplayjTabbedPane.getSelectedIndex() == 1)
        {
            if(selectedBookmarksLocal.size() > 1){
                errorBookmarkLocalLabel.setText(tooManyPhenomenonsSelected);
                errorBookmarkLocalLabel.setVisible(true);
            }
            else if(bookmarkToOpenLocal == null){
                errorBookmarkLocalLabel.setText(onePhenomenonMustBeSelected);
                errorBookmarkLocalLabel.setVisible(true);
            }
            else{ 
                selectedPhenomenon = bookmarkToOpenLocal;
                errorBookmarkLocalLabel.setText(defaultErrorMessage);
                errorBookmarkLocalLabel.setVisible(false);
                this.setVisible(false);
            }
        }        
        else if(mainDisplayjTabbedPane.getSelectedIndex() == 2)
        {
            if(selectedEventsDatabase.size() > 1){
                errorEventsDatabaseLabel.setText(tooManyPhenomenonsSelected);
                errorEventsDatabaseLabel.setVisible(true);
            }
            else if(eventToOpenDatabase == null){
                errorEventsDatabaseLabel.setText(onePhenomenonMustBeSelected);
                errorEventsDatabaseLabel.setVisible(true);
            }
            else{ 
                selectedPhenomenon = eventToOpenDatabase;
                errorEventsDatabaseLabel.setText(defaultErrorMessage);
                errorEventsDatabaseLabel.setVisible(false);
                this.setVisible(false);
            }
        }
         else if(mainDisplayjTabbedPane.getSelectedIndex() == 3)
        {
            if(selectedEventsLocal.size() > 1){
                errorEventsLocalLabel.setText(tooManyPhenomenonsSelected);
                errorEventsLocalLabel.setVisible(true);
            }
            else if(eventToOpenLocal == null){
                errorEventsLocalLabel.setText(onePhenomenonMustBeSelected);
                errorEventsLocalLabel.setVisible(true);
            }
            else{ 
                selectedPhenomenon = eventToOpenLocal;
                errorEventsLocalLabel.setText(defaultErrorMessage);
                errorEventsLocalLabel.setVisible(false);
                this.setVisible(false);
            }
        }
        
    }//GEN-LAST:event_selectPhenomenonBmDbButtonActionPerformed

    private void findLocalBookmarkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findLocalBookmarkButtonActionPerformed
        new FindLocalBookmark(appControl, controller, this).openInstance();
    }//GEN-LAST:event_findLocalBookmarkButtonActionPerformed

    private void findLocalEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findLocalEventButtonActionPerformed
        new FindLocalBookmark(appControl, controller, this).openEvent();
    }//GEN-LAST:event_findLocalEventButtonActionPerformed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        //hide error labels
        this.errorBookmarkDatabaseLabel.setVisible(false);
        this.errorBookmarkLocalLabel.setVisible(false);
        this.errorEventsDatabaseLabel.setVisible(false);
        this.errorEventsLocalLabel.setVisible(false);
        repaint();
    }//GEN-LAST:event_formFocusLost

    private void mainDisplayjTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainDisplayjTabbedPaneStateChanged
        areAdvancedOptionsSelected = false;                 //Clear advanced options.
    }//GEN-LAST:event_mainDisplayjTabbedPaneStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane EventsLocalResultScrollPane;
    private javax.swing.JButton addBookmarkBookmarkDatabaseButton;
    private javax.swing.JButton addBookmarkBookmarskLocalButton;
    private javax.swing.JButton addBookmarkEventsDBButton;
    private javax.swing.JButton addBookmarkEventsLocalButton;
    private javax.swing.JButton advancedOptionsBookamrksDatabaseButton;
    private javax.swing.JButton advancedOptionsBookamrksLocalButton;
    private javax.swing.JButton advancedOptionsEventsDatabaseButton;
    private javax.swing.JButton advancedOptionsEventsLocalButton;
    private javax.swing.JPanel bookmarkLocalPanel;
    private javax.swing.JTable bookmarkLocalResultTable;
    private javax.swing.JPanel bookmarksDatabaseButtonPanel;
    private javax.swing.JPanel bookmarksDatabasePanel;
    private javax.swing.JPanel bookmarksLocalButtonPanel;
    private javax.swing.JButton closeBookmarkLocalButton;
    private javax.swing.JButton closeBookmarksDatabaseButton;
    private javax.swing.JButton closeEventsDatabaseButton;
    private javax.swing.JButton closeEventsLocalButton;
    private javax.swing.JLabel errorBookmarkDatabaseLabel;
    private javax.swing.JLabel errorBookmarkLocalLabel;
    private javax.swing.JLabel errorEventsDatabaseLabel;
    private javax.swing.JLabel errorEventsLocalLabel;
    private javax.swing.JPanel eventsDatabaseButtonPanel;
    private javax.swing.JPanel eventsDatabasePanel;
    private javax.swing.JPanel eventsLocalButtonPanel;
    private javax.swing.JPanel eventsLocalPanel;
    private javax.swing.JTable eventsLocalResultTable;
    private javax.swing.JComboBox<String> filterCategoryBookmarkLocalComboBox;
    private javax.swing.JComboBox<String> filterCategoryBookmarksDatabaseComboBox;
    private javax.swing.JComboBox<String> filterCategoryEventLocalComboBox;
    private javax.swing.JComboBox<String> filterCategoryEventsDatabaseComboBox;
    private javax.swing.JButton findLocalBookmarkButton;
    private javax.swing.JButton findLocalEventButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane mainDisplayjTabbedPane;
    private javax.swing.JButton manageCategoriesBDButton;
    private javax.swing.JButton manageCategoriesBLButton;
    private javax.swing.JButton manageCategoriesEDButton;
    private javax.swing.JButton manageCategoriesELButton;
    private javax.swing.JButton openBookmarDatabasekButton;
    private javax.swing.JButton openBookmarkLocalButton;
    private javax.swing.JButton openEventLocalButton;
    private javax.swing.JButton openEventsDatabasekButton;
    private javax.swing.JButton removeBookmarkLocalButton;
    private javax.swing.JButton removeEventLocalButton;
    private javax.swing.JButton removedBookmarkDatabaseButton;
    private javax.swing.JButton removedEventDatabaseButton1;
    private javax.swing.JScrollPane resultBookmarksDatabaseScrollPane;
    private javax.swing.JScrollPane resultEventsDatabaseScrollPane;
    private javax.swing.JScrollPane resultScrollPane1;
    private javax.swing.JTable resultTableBookmarksDatabase;
    private javax.swing.JTable resultTableEventsDatabase;
    private javax.swing.JButton searchBookmarkLocalButton;
    private javax.swing.JButton searchBookmarksDatabaseButton;
    private javax.swing.JButton searchEventLocalButton;
    private javax.swing.JButton searchEventsDatabaseButton;
    private javax.swing.JButton selectAllBookmarkLocalButton;
    private javax.swing.JButton selectAllBookmarksDatabaseButton;
    private javax.swing.JButton selectAllEventsDatabaseButton;
    private javax.swing.JButton selectAllEventsLocalButton;
    private javax.swing.JButton selectPhenomenonBmDbButton;
    private javax.swing.JButton selectPhenomenonBmLocalButton;
    private javax.swing.JButton selectPhenomenonEvDbButton;
    private javax.swing.JButton selectPhenomenonEvLocalButton;
    // End of variables declaration//GEN-END:variables
}
