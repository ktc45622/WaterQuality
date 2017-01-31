package weather.clientside.gui.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.clientside.gui.administrator.ManageBookmarkCategoryDialog;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.manager.MovieController;
import weather.clientside.manager.WeatherStationPanelManager;
import weather.clientside.utilities.BookmarkTimeZoneFinder;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.*;
import weather.common.data.bookmark.*;
import weather.common.data.resource.*;
import weather.common.dbms.DBMSBookmarkInstanceManager;
import weather.common.dbms.DBMSFileManager;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSUserManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.gui.component.BUDialog;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.*;

/**
 * Brings up the Create A New Bookmark Window.
 *
 * Currently allows the local system to duplicate bookmarks from the database
 * and vice-versa. To disallow duplicates, uncomment the marked blocks in the
 * createButtonActionPerformed method.
 *
 * TODO: Attaching files to local bookmarks needs to be implemented. The code to
 * save files to the database/storage system is commented out due to problems
 * retrieving the attached files.
 *
 * @author Trevor Erdley 2011
 * @author John Lenhart 2012
 * @author Justin Enslin 2012
 */
public class BookmarkAddEditWindow extends BUDialog {
    
    private GeneralService genService;
    private int camNum;
    private int radarNum;
    private int dataplotNum;
    private ResourceRange range;
    private WeatherStationPanelManager stationManager;
    private MovieController mainMovieController;
    
    //Times taken from movie controller
    private Calendar now;   //current time of slider
    private Calendar start; //start of controller range
    private Calendar end;   //end of controller range
    
    private DBMSBookmarkInstanceManager bookmarkManager;
    private DBMSUserManager userManager;
    private DBMSFileManager fileManager;
    private Vector<BookmarkType> bookmarkTypeList;
    private Vector<User> userList;
    private User user;
    private boolean localFlag;  //Was the form loaded with a local bookmark/event?
    private boolean editExisting;
    private Bookmark currentBookmark;
    private TimeZone timeZone;  //Time zone of camera resource.

    //Error messages
    private static String emptyNameError = "A name must be entered to create the Bookmark/Event.";
    private static String endNotAfterStart = "End Date Must Be After Start Date";
    private static String eventAfterNowError = "The data for thet time range is not yet availabe.";
    private static String invalidFromDateError = "Date From: Invalid Time or Date";
    private static String invalidToDateError = "Date To: Invalid Time or Date";
    private BookmarkDateSelectionWindow searchBookmarkCalendarWindowOne;
    //Attached files section
    //List of names of currently attached files
    private DefaultListModel<String> fileListModel;
    //List to hold files attached when note was loaded.
    private LinkedList<BookmarkFileInstance> originalFiles;
    //List to hold currently attached files.
    private LinkedList<BookmarkFileInstance> attachedFiles;
    private boolean manageCategoriesOpened = false;
    private ActionListener comboBoxListener;

    /**
     * Constructor for a new CreateBookmarkWindow.
     *
     * @param ap ApplicationControl control system used in the program.
     * @param station Data plot panel from the MainApplicationWindow.
     * @param mc MovieControler slider panel from MainApplicationWindow.
     * @param isEvent Is event radio button selected at creation?
     * @param isLocal Is local storage radio button selected at creation?
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public BookmarkAddEditWindow(ApplicationControlSystem ap, 
            WeatherStationPanelManager station, MovieController mc,
            boolean isEvent, boolean isLocal, boolean shouldCenter) {
        //Prepare for initComponents
        super(ap);
        fileListModel = new DefaultListModel<>();

        //Let the GUI Editor build the form
        initComponents();
        
        //Add popup menu listener.
        addPopupListener();

        //Finish setup for files
        attachLabel.setVisible(false);
        attachLabel.setIcon(IconProperties.getAttachmentIconImage());
        originalFiles = new LinkedList<>();
        attachedFiles = new LinkedList<>();

        setModalityType(ModalityType.APPLICATION_MODAL);

        user = ap.getGeneralService().getUser();
        setButtonGroupAndInit();
        if (user.getUserType() == UserType.student || user.getUserType() == UserType.guest) {
            categorySubcategoryButton.setEnabled(false);
            saveLocationHardDriveRadio.doClick();
            saveLocationDatabaseRadio.setEnabled(false);
        } else {
            saveLocationDatabaseRadio.doClick();
        }
        isBookmarkInstanceTypeRadio.setSelected(true);
        //Set the title text on the form.
        this.setTitle("Weather Viewer - Create New Bookmark or Event");
        //This is the add constructor, we are not editing, set it to false.
        //This is used in the create/save button action.
        editExisting = false;
        genService = appControl.getGeneralService();
        bookmarkManager = genService.getDBMSSystem().getBookmarkManager();
        fileManager = genService.getDBMSSystem().getFileManager();
        fileManager.setStorageSystem(StorageControlSystemImpl.getStorageSystem());
        //The following data could be null. Check to make sure and if so, grey it out.
        if (ap.getGeneralService().getCurrentWeatherCameraResource() != null) {
            camNum = genService.getCurrentWeatherCameraResource().getResourceNumber();
        } else {
            //The resource is null, set the number to the database default.
            //Then disable the ability to check the resource to be saved.
            camNum = -1;
            weatherCameraCheckBox.setEnabled(false);
        }
        if (ap.getGeneralService().getCurrentWeatherMapLoopResource() != null) {
            radarNum = genService.getCurrentWeatherMapLoopResource().getResourceNumber();
        } else {
            //The resource is null, set the number to the database default.
            //Then disable the ability to check the resource to be saved.
            radarNum = -1;
            radarMapCheckBox.setEnabled(false);
        }
        if (ap.getGeneralService().getCurrentWeatherStationResource() != null) {
            dataplotNum = genService.getCurrentWeatherStationResource().getResourceNumber();
        } else {
            //The resource is null, set the number to the database default.
            //Then disable the ability to check the resource to be saved.
            dataplotNum = -1;
            dataplotCheckBox.setEnabled(false);
        }
        
        //Continuing to initialize class variables from constructor data.
        stationManager = station;
        mainMovieController = mc;
        now = mainMovieController.getCurrentDateAndTime();
        start = mainMovieController.getStartingDateAndTime();
        end = mainMovieController.getEndingDateAndTime();
        localFlag = isLocal;
        errorLabel.setVisible(false);
        timeZone = mainMovieController.getPrimaryTimeZone();
        initMyComponents();//This initializes the combo boxes and sets the default radio button selection

        //Set type and storage.
        if (isEvent) {
            this.isBookmarkEventTypeRadio.setSelected(true);
            this.isBookmarkEventTypeRadio.doClick();    //Change from default.
        } else {
            this.isBookmarkInstanceTypeRadio.setSelected(true);
        }
        if (isLocal) {
            this.saveLocationHardDriveRadio.setSelected(true);
            this.saveLocationHardDriveRadio.doClick();    //Change from default.
        } else {
            this.saveLocationDatabaseRadio.setSelected(true);
        }

        //Set size
        int width = 963 + this.getInsets().left + this.getInsets().right;
        int height = 595 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(shouldCenter);
    }

    /**
     * Creates a new window for editing an existing bookmark.
     *
     * @param appControl ApplicationControl control system used in the program.
     * @param bookmark Bookmark Instance used.
     * @param station Data plot panel from the MainApplicationWindow.
     * @param mc MovieControler slider panel from MainApplicationWindow.
     * @param isEvent Is event radio button selected at creation?
     * @param isLocal Is local storage radio button selected at creation?
     * @param shouldCenter True if this object should be centered on the monitor
     * that currently shows the window with focus; False if an offset from the
     * window with focus should be used.
     */
    public BookmarkAddEditWindow(ApplicationControlSystem appControl,
            Bookmark bookmark, WeatherStationPanelManager station,
            MovieController mc, boolean isEvent, boolean isLocal, 
            boolean shouldCenter) {
        //Prepare for initComponents
        super(appControl);
        fileListModel = new DefaultListModel<>();

        //Let the GUI Editor build the form
        initComponents();
        
        //Add popup menu listener.
        addPopupListener();

        //Set service and managers
        genService = appControl.getGeneralService();
        bookmarkManager = genService.getDBMSSystem().getBookmarkManager();
        fileManager = genService.getDBMSSystem().getFileManager();

        //Finish setup for files
        attachLabel.setIcon(IconProperties.getAttachmentIconImage());
        originalFiles = new LinkedList<>();
        attachedFiles = new LinkedList<>();
        for (BookmarkFileInstance bfi : fileManager.getAllFilesForBookmark(bookmark)) {
            originalFiles.add(bfi);
            attachedFiles.add(bfi);
            fileListModel.addElement(bfi.getFileName());
        }
        attachLabel.setVisible(fileListModel.size() > 0);

        stationManager = station;
        mainMovieController = mc;

        setModalityType(ModalityType.APPLICATION_MODAL);

        fileManager.setStorageSystem(StorageControlSystemImpl.getStorageSystem());
        bookmarkTypeList = appControl.getDBMSSystem().getBookmarkTypesManager()
                .obtainAll();
        user = appControl.getGeneralService().getUser();

        setButtonGroupAndInit();

        //Set the title. 
        this.setTitle("Weather Viewer - Edit " + (bookmark.getType()
                == BookmarkDuration.instance ? "Bookmark " : "Event ")
                + bookmark.getName());

        saveLocationHardDriveRadio.setSelected(true);
        if (bookmark.getBookmarkNumber() != -1) {
            saveLocationDatabaseRadio.setSelected(true);
        }
        //This is the edit contructor, so we need to make note for the create/save button.
        editExisting = true;
        //TODO Make it so this functionality works when editing a bookmark/event.
        weatherCameraCheckBox.setEnabled(false);
        radarMapCheckBox.setEnabled(false);
        dataplotCheckBox.setEnabled(false);
        dataPanel.setEnabled(false);
        //Change the label on the create button
        createButton.setText("Save");
        createButton.setToolTipText("Save the changes to this bokmark or event.");
        createButton.revalidate();
        //Take the bookmarkInstace passed to this window constructor and add all the values back
        //into the fields.
        currentBookmark = bookmark;
        //The following information cannot be null, the database/bookmark passed should always
        //have a number for it.
        camNum = currentBookmark.getWeatherCameraResourceNumber();
        radarNum = currentBookmark.getWeatherMapLoopResourceNumber();
        dataplotNum = currentBookmark.getWeatherStationResourceNumber();

        localFlag = isLocal;
        errorLabel.setVisible(false);

        //Initialize the rest of the values
        initComponentsFromBookmarkInstance();

        //Set type and storage.
        if (isEvent) {
            this.isBookmarkEventTypeRadio.setSelected(true);
            this.isBookmarkEventTypeRadio.doClick();    //Change from default.
        } else {
            this.isBookmarkInstanceTypeRadio.setSelected(true);
        }
        if (isLocal) {
            this.saveLocationHardDriveRadio.setSelected(true);
            this.saveLocationHardDriveRadio.doClick();    //Change from default.
        } else {
            this.saveLocationDatabaseRadio.setSelected(true);
        }

        //Disable type change.
        this.isBookmarkInstanceTypeRadio.setEnabled(false);
        this.isBookmarkEventTypeRadio.setEnabled(false);

        //Disable Time Change
        datePanel.setEnabled(false);
        this.fromjLabel.setEnabled(false);
        this.date1Label.setEnabled(false);
        this.date1TextPane.setEnabled(false);
        this.time1Label.setEnabled(false);
        this.time1TextPane.setEnabled(false);
        this.time1Button.setEnabled(false);
        this.tojLabel.setEnabled(false);
        this.date2Label.setEnabled(false);
        this.date2TextPane.setEnabled(false);
        this.time2Label.setEnabled(false);
        this.time2TextPane.setEnabled(false);
        this.time2Button.setEnabled(false);

        //Set size
        int width = 963 + this.getInsets().left + this.getInsets().right;
        int height = 595 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(shouldCenter);
    }
    
    private void addPopupListener(){
        PopupMenuListener popupMenuListener = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                fileList.clearSelection();
            }
        };

        popupMenu.addPopupMenuListener(popupMenuListener);
    }

    /**
     * Helper method that displays the
     * <code>leaveWithoutSaving()</code> dialog.
     */
    private void close() {
        if (!changed() 
                || appControl.getGeneralService().leaveWithoutSaving(this)) {
            dispose();
        }
    }

    /**
     * Checks if a linked list of
     * <code>BookmarkFileInstance</code> contains an instance with a given file
     * number.
     *
     * @param list The list to be checked.
     * @param bookmarkFileNumber The file number to be found.
     * @return True if the indicated file is found, false otherwise.
     */
    private boolean collectionHasFileNumber(LinkedList<BookmarkFileInstance> list, int bookmarkFileNumber) {
        for (BookmarkFileInstance nfi : list) {
            if (nfi.getFileNumber() == bookmarkFileNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function to see if the user has changed the attached files since
     * opening the window.
     *
     * @return False if the user has changed the attached files since opening
     * the window, true otherwise.
     */
    private boolean areFilesUnchanged() {
        //Return false if any original files are not pressent.
        for (BookmarkFileInstance bfi : originalFiles) {
            if (!collectionHasFileNumber(attachedFiles, bfi.getFileNumber())) {
                return false;
            }
        }

        //Return false if are current files are not in database.
        for (BookmarkFileInstance bfi : attachedFiles) {
            if (!collectionHasFileNumber(originalFiles, bfi.getFileNumber())) {
                return false;
            }
        }

        //Return true as lists match.
        return true;
    }

    /**
     * Helper method to determine whether or not something has been changed in
     * the window.
     *
     * @return True if something was changed, false otherwise.
     */
    private boolean changed() {
        if (!editExisting) {
            //Check if "Add" version has been changed.
            boolean dateUnchanged;
            if (this.isBookmarkInstanceTypeRadio.isSelected()) {
                //Time can't be changed for bookmarks.
                dateUnchanged = true;
            } else {
                //Check if adding events.
                dateUnchanged = CalendarFormatter.format(start, 
                        CalendarFormatter.DisplayFormat.DEFAULT)
                        .equals(date1TextPane.getText() 
                        + ' ' + time1TextPane.getText()) 
                        && CalendarFormatter.format(end,
                        CalendarFormatter.DisplayFormat.DEFAULT)
                        .equals(date2TextPane.getText()
                        + ' ' + time2TextPane.getText());
            }

            boolean dataSelectionUnchanged =
                    ((genService.getCurrentWeatherCameraResource() != null)
                    == (weatherCameraCheckBox.isSelected()))
                    && ((genService.getCurrentWeatherMapLoopResource() != null)
                    == (radarMapCheckBox.isSelected()))
                    && ((genService.getCurrentWeatherStationResource() != null)
                    == (dataplotCheckBox.isSelected()));

            boolean permissionsUnchanged = user.getUserType() == UserType.guest
                    || user.getUserType() == UserType.student
                    || everyoneRadio.isSelected();

            return !(bookmarkNameTextField.getText().isEmpty()
                    && user.getLoginId().equals((String) ownerComboBox.
                    getSelectedItem())
                    && categoryComboBox.getSelectedIndex() == 0
                    && typeComboBox.getSelectedIndex() == 0
                    && dateUnchanged && dataSelectionUnchanged
                    && permissionsUnchanged
                    && rankComboBox.getSelectedIndex() == 0
                    && notesEditorPane.getText().isEmpty()
                    && attachedFiles.size() == 0);
        } else {
            //Check if "Edit" version has been changed.
            boolean nameUnchanged = bookmarkNameTextField.getText().trim().
                    equals(currentBookmark.getName());
            
            boolean ownerUnchanged = ((String) ownerComboBox.getSelectedItem()).
                    equals(userManager.obtainUser(currentBookmark
                    .getCreatedBy()).getLoginId());
            
            boolean categoryUnchanged = ((String) categoryComboBox.
                    getSelectedItem()).equals(genService.getDBMSSystem().
                    getBookmarkCategoriesManager().
                    searchByBookmarkCategoryNumber(
                    currentBookmark.getCategoryNumber()).getName());
            
            boolean subCategoryUnchanged;
            if (typeComboBox.getSelectedItem() != null) {
                subCategoryUnchanged = ((String) typeComboBox.
                        getSelectedItem()).equals(genService.getDBMSSystem().
                        getBookmarkTypesManager().searchByBookmarkTypeNumber(
                        currentBookmark.getTypeNumber()).getName());
            } else {
                subCategoryUnchanged = (currentBookmark.getTypeNumber() == -1);
            }
            
            boolean dataSelectionUnchanged = (!weatherCameraCheckBox.isEnabled() || weatherCameraCheckBox.isSelected())
                    && (!radarMapCheckBox.isEnabled()
                    || radarMapCheckBox.isSelected())
                    && (!dataplotCheckBox.isEnabled()
                    || dataplotCheckBox.isSelected());
            
            boolean saveLocationUnchanged =
                    saveLocationHardDriveRadio.isSelected()
                    == localFlag;
            
            boolean permissionsUnchanged =
                    (courseStudentsRadio.isSelected() && currentBookmark.
                    getAccessRights().equals(AccessRights.CourseStudents)
                    || (allStudentsRadio.isSelected() && currentBookmark.
                    getAccessRights().equals(AccessRights.AllStudents))
                    || (instructorsRadio.isSelected() && currentBookmark.
                    getAccessRights().equals(AccessRights.Instructors))
                    || (everyoneRadio.isSelected() && currentBookmark.
                    getAccessRights().equals(AccessRights.Everyone))
                    || (privateRadio.isSelected() && currentBookmark.
                    getAccessRights().equals(AccessRights.Private)));
            
            boolean rankUnchanged = ((String) rankComboBox.getSelectedItem()).
                    equals(currentBookmark.getRanking().toString());
            
            boolean notesUnchanged = notesEditorPane.getText().trim().
                    equals(currentBookmark.getNotes().trim());
            
            return !(nameUnchanged && ownerUnchanged
                    && categoryUnchanged && subCategoryUnchanged
                    && dataSelectionUnchanged
                    && saveLocationUnchanged && permissionsUnchanged
                    && rankUnchanged && notesUnchanged
                    && areFilesUnchanged());
        }
    }

    /**
     * Helper function that fills the combo boxes.
     *
     * @param owner The user that will be selected in the owner combo box
     */
    private void initializeComboBoxes(User owner) {
        //Set category box
        categoryComboBox.removeAllItems();

        GUIComponentFactory.initRankingBox(rankComboBox);
        GUIComponentFactory.initBookmarkCategoryBox(categoryComboBox,
                appControl.getDBMSSystem().getEventManager(),
                genService.getUser());

        //Set owner box
        int index = 0;  //To store index of owner when found.
        userManager = genService.getDBMSSystem().getUserManager();
        userList = userManager.obtainAllUsers();
        for (User userListItem : userList) {
            ownerComboBox.addItem(userListItem.getLoginId());
            //Get infomation to seect owner
            if (owner.getLoginId().equals(userListItem.getLoginId())) {
                index = ownerComboBox.getItemCount() - 1;
            }
        }
        //Seect owner
        ownerComboBox.setSelectedIndex(index);
        //Decide if user can change owner
        if (user.getUserType() != UserType.administrator) {
            ownerComboBox.setEnabled(false);
        }
    }

    /**
     * Helper function that fills the combo boxes without selecting owner.
     */
    private void initializeComboBoxes() {
        //Set category box
        categoryComboBox.removeAllItems();

        GUIComponentFactory.initRankingBox(rankComboBox);
        GUIComponentFactory.initBookmarkCategoryBox(categoryComboBox,
                appControl.getDBMSSystem().getEventManager(),
                genService.getUser());

        //Set owner box
        userManager = genService.getDBMSSystem().getUserManager();
        userList = userManager.obtainAllUsers();
        for (User userListItem : userList) {
            ownerComboBox.addItem(userListItem.getLoginId());
        }
        //Decide if user can change owner
        if (user.getUserType() != UserType.administrator) {
            ownerComboBox.setEnabled(false);
        }
    }

    /**
     * Sets the radio-buttons to a button group.
     */
    private void setButtonGroupAndInit() {
        ButtonGroup viewRightsGroup = new ButtonGroup();
        viewRightsGroup.add(courseStudentsRadio);
        viewRightsGroup.add(allStudentsRadio);
        viewRightsGroup.add(privateRadio);
        viewRightsGroup.add(instructorsRadio);
        viewRightsGroup.add(everyoneRadio);
        date1TextPane.setEditable(false);
        time1TextPane.setEditable(false);
        date2TextPane.setEditable(false);
        time2TextPane.setEditable(false);
        ButtonGroup saveLocationGroup = new ButtonGroup();
        saveLocationGroup.add(saveLocationDatabaseRadio);
        saveLocationGroup.add(saveLocationHardDriveRadio);

        ButtonGroup bookmarkTypeGroup = new ButtonGroup();
        bookmarkTypeGroup.add(isBookmarkEventTypeRadio);
        bookmarkTypeGroup.add(isBookmarkInstanceTypeRadio);
    }

    /**
     * Initializes the form controls based on data saved in a bookmark object.
     */
    private void initComponentsFromBookmarkInstance() {
        //Init and select the items for the Catagory and Type comboBoxes
        BookmarkCategory category = genService.getDBMSSystem()
                .getBookmarkCategoriesManager()
                .searchByBookmarkCategoryNumber(currentBookmark.getCategoryNumber());
        BookmarkType type = genService.getDBMSSystem().getBookmarkTypesManager()
                .searchByBookmarkTypeNumber(currentBookmark.getTypeNumber());
        bookmarkNameTextField.setText(currentBookmark.getName());
        initializeComboBoxes();
        rankComboBox.setSelectedItem(currentBookmark.getRanking().toString());
        rankComboBox.revalidate();
        try {
            ownerComboBox.setSelectedItem(userManager.
                    obtainUser(currentBookmark.getCreatedBy()).getLoginId());
        } catch (NullPointerException ex) {
            WeatherLogger.log(Level.SEVERE, "Database error - no user found for"
                    + "bookmark #" + currentBookmark.getBookmarkNumber(), ex);
        }
        //Set the catagory comboBox using the value stored in the bookmark
        try {
            categoryComboBox.setSelectedItem(category.getName());
        } catch (NullPointerException ex) {
            WeatherLogger.log(Level.SEVERE, "Database error - no category found"
                    + " for bookmark #" + currentBookmark.getBookmarkNumber(),
                    ex);
        }
        categoryComboBox.revalidate();

        //The same with the type number
        try {
            typeComboBox.setSelectedItem(type.getName());
        } catch (NullPointerException ex) {
            WeatherLogger.log(Level.SEVERE, "Database error - no sub-category "
                    + "found for bookmark #"
                    + currentBookmark.getBookmarkNumber(), ex);
        }
        typeComboBox.revalidate();
        if (currentBookmark.getType() == BookmarkDuration.instance) {
            isBookmarkInstanceTypeRadio.setSelected(true);
        } else {
            isBookmarkEventTypeRadio.setSelected(true);
        }
        //Check the data sources on the form based on if data exists in the bookmark
        if (currentBookmark.getWeatherMapLoopResourceNumber() != -1) {
            radarMapCheckBox.setSelected(true);
        } else {
            radarMapCheckBox.setEnabled(false);
        }

        if (currentBookmark.getWeatherCameraResourceNumber() != -1) {
            weatherCameraCheckBox.setSelected(true);
        } else {
            weatherCameraCheckBox.setEnabled(false);
        }

        if (currentBookmark.getWeatherStationResourceNumber() != -1) {
            dataplotCheckBox.setSelected(true);
        } else {
            dataplotCheckBox.setEnabled(false);
        }

        notesEditorPane.setText(currentBookmark.getNotes());
        
        //Set time fields.    
        
        //Must gwt time zone.
        timeZone = BookmarkTimeZoneFinder.findTimeZone(currentBookmark, 
                genService);
        
        start = new GregorianCalendar();
        start.setTimeZone(timeZone);
        start.setTimeInMillis(currentBookmark.getStartTime().getTime());
        end = new GregorianCalendar();
        end.setTimeZone(timeZone);
        end.setTimeInMillis(currentBookmark.getEndTime().getTime());

        date1TextPane.setText(CalendarFormatter.format(
                start, CalendarFormatter.DisplayFormat.DATE));
        time1TextPane.setText(CalendarFormatter.format(start,
                CalendarFormatter.DisplayFormat.TIME_12));

        date2TextPane.setText(CalendarFormatter.format(
                end, CalendarFormatter.DisplayFormat.DATE));
        time2TextPane.setText(CalendarFormatter.format(end,
                CalendarFormatter.DisplayFormat.TIME_12));

        AccessRights access = currentBookmark.getAccessRights();
        if (access == AccessRights.AllStudents) {
            allStudentsRadio.setSelected(true);
        } else if (access == AccessRights.Instructors) {
            instructorsRadio.setSelected(true);
        } else if (access == AccessRights.Everyone) {
            everyoneRadio.setSelected(true);
        } else if (access == AccessRights.Private) {
            privateRadio.setSelected(true);
        } else {
            courseStudentsRadio.setSelected(true);
        }
    }

    /**
     * Helper method used for initializing the components for a new add bookmark
     * window.
     */
    private void initMyComponents() {
        Resource cam = genService.getCurrentWeatherCameraResource();
        Resource radar = genService.getCurrentWeatherMapLoopResource();
        Resource dataplot = genService.getCurrentWeatherStationResource();

        //Load values into window or default if the resource is null
        if (cam != null) {
            camNum = cam.getResourceNumber();
            weatherCameraCheckBox.setSelected(true);
        } else {
            camNum = -1;
        }
        if (radar != null) {
            radarNum = radar.getResourceNumber();
            radarMapCheckBox.setSelected(true);
        } else {
            radarNum = -1;
        }
        if (dataplot != null) {
            dataplotNum = dataplot.getResourceNumber();
            dataplotCheckBox.setSelected(true);
        } else {
            dataplotNum = -1;
        }

        initializeComboBoxes(user);

        isBookmarkInstanceTypeRadio.setSelected(true);
        rankComboBox.setSelectedIndex(0);

        //Set the default time of the bookmark
        date1TextPane.setText(CalendarFormatter.format(now,
                CalendarFormatter.DisplayFormat.DATE));
        date2TextPane.setText(CalendarFormatter.format(now,
                CalendarFormatter.DisplayFormat.DATE));
        time1TextPane.setText(CalendarFormatter.format(now, 
                CalendarFormatter.DisplayFormat.TIME_12));
        time2TextPane.setText(CalendarFormatter.format(now, 
                CalendarFormatter.DisplayFormat.TIME_12));

        //Init the defaults for the options
        this.categoryComboBoxActionPerformed(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        removeItem = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        categoryComboBox = new javax.swing.JComboBox<>();
        catagoryLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        datePanel = new javax.swing.JPanel();
        date1Label = new javax.swing.JLabel();
        date1TextPanel = new javax.swing.JScrollPane();
        date1TextPane = new javax.swing.JTextPane();
        date2Label = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        date2TextPane = new javax.swing.JTextPane();
        time1Label = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        time1TextPane = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        time2TextPane = new javax.swing.JTextPane();
        time2Label = new javax.swing.JLabel();
        tojLabel = new javax.swing.JLabel();
        time1Button = new javax.swing.JButton();
        time2Button = new javax.swing.JButton();
        fromjLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();
        notesLabel = new javax.swing.JLabel();
        dataPanel = new javax.swing.JPanel();
        weatherCameraCheckBox = new javax.swing.JCheckBox();
        radarMapCheckBox = new javax.swing.JCheckBox();
        dataplotCheckBox = new javax.swing.JCheckBox();
        accessPanel = new javax.swing.JPanel();
        everyoneRadio = new javax.swing.JRadioButton();
        courseStudentsRadio = new javax.swing.JRadioButton();
        allStudentsRadio = new javax.swing.JRadioButton();
        instructorsRadio = new javax.swing.JRadioButton();
        privateRadio = new javax.swing.JRadioButton();
        notesScrollPanel = new javax.swing.JScrollPane();
        notesEditorPane = new javax.swing.JEditorPane();
        rankPanel = new javax.swing.JPanel();
        rankComboBox = new javax.swing.JComboBox<>();
        saveLocationPanel = new javax.swing.JPanel();
        saveLocationHardDriveRadio = new javax.swing.JRadioButton();
        saveLocationDatabaseRadio = new javax.swing.JRadioButton();
        categorySubcategoryButton = new javax.swing.JButton();
        ownerLabel = new javax.swing.JLabel();
        ownerComboBox = new javax.swing.JComboBox<>();
        errorLabel = new javax.swing.JLabel();
        attachFilesButton = new javax.swing.JButton();
        fileListScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList<>();
        attachLabel = new javax.swing.JLabel();
        bookmarkNameTextField = new javax.swing.JTextField();
        bookmarkNameLabel = new javax.swing.JLabel();
        isBookmarkInstanceTypeRadio = new javax.swing.JRadioButton();
        isBookmarkEventTypeRadio = new javax.swing.JRadioButton();

        removeItem.setText("Remove");
        removeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeItemActionPerformed(evt);
            }
        });
        popupMenu.add(removeItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        categoryComboBox.setToolTipText("The bookmarks catagory (ex: 'Clouds')");
        comboBoxListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                categoryComboBoxActionPerformed(e);
            }
        };
        categoryComboBox.addActionListener(comboBoxListener);
        categoryComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        mainPanel.add(categoryComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 50, 143, 22));

        catagoryLabel.setText("Category:");
        mainPanel.add(catagoryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 53, -1, -1));

        typeLabel.setText("Sub-Category:");
        mainPanel.add(typeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(252, 53, -1, -1));

        typeComboBox.setToolTipText("The bookmarks type (ex: 'Cirrus')");
        typeComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        mainPanel.add(typeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(347, 50, 136, 22));

        datePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Video Date and Time Range:"));

        date1Label.setText("Date:");
        date1Label.setEnabled(false);

        date1TextPane.setEnabled(false);
        date1TextPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        date1TextPanel.setViewportView(date1TextPane);

        date2Label.setText("Date:");
        date2Label.setEnabled(false);

        date2TextPane.setEnabled(false);
        date2TextPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        jScrollPane2.setViewportView(date2TextPane);

        time1Label.setText("Time:");
        time1Label.setEnabled(false);

        time1TextPane.setEnabled(false);
        time1TextPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        jScrollPane3.setViewportView(time1TextPane);

        time2TextPane.setEnabled(false);
        time2TextPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        jScrollPane4.setViewportView(time2TextPane);

        time2Label.setText("Time:");
        time2Label.setEnabled(false);

        tojLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tojLabel.setText("To");
        tojLabel.setEnabled(false);

        time1Button.setText("Select Date/Time");
        time1Button.setToolTipText("");
        time1Button.setEnabled(false);
        time1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                time1ButtonActionPerformed(evt);
            }
        });
        time1Button.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        time2Button.setText("Select Date/Time");
        time2Button.setEnabled(false);
        time2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                time2ButtonActionPerformed(evt);
            }
        });
        time2Button.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        fromjLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fromjLabel.setText("From");
        fromjLabel.setToolTipText("");
        fromjLabel.setEnabled(false);

        javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
        datePanel.setLayout(datePanelLayout);
        datePanelLayout.setHorizontalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, datePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(datePanelLayout.createSequentialGroup()
                        .addComponent(date1Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(date1TextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(time1Button)
                    .addComponent(fromjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(datePanelLayout.createSequentialGroup()
                            .addComponent(time2Button)
                            .addGap(122, 122, 122))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, datePanelLayout.createSequentialGroup()
                            .addComponent(date2Label)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(time2Label)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))
                    .addGroup(datePanelLayout.createSequentialGroup()
                        .addComponent(tojLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        datePanelLayout.setVerticalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tojLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fromjLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date1TextPanel)
                    .addComponent(time1Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(time2Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(date2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(date1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(time1Button)
                    .addComponent(time2Button)))
        );

        mainPanel.add(datePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 125, 543, 103));

        cancelButton.setText("Close");
        cancelButton.setToolTipText("Exit the window without saving changes");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        mainPanel.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(842, 496, 83, 25));

        createButton.setText("Create");
        createButton.setToolTipText("Create the bookmark or event and save it for later use.");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        createButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        mainPanel.add(createButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 496, 83, 25));

        notesLabel.setText("Notes/Comments:");
        mainPanel.add(notesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 242, -1, 14));

        dataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data To Be Saved:"));

        weatherCameraCheckBox.setText("Weather Camera");
        weatherCameraCheckBox.setToolTipText("Save the image/video loaded in the weather camera window");
        weatherCameraCheckBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        radarMapCheckBox.setText("Radar Map");
        radarMapCheckBox.setToolTipText("Save the image/video loaded in the radar map window");
        radarMapCheckBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        dataplotCheckBox.setText("Dataplot");
        dataplotCheckBox.setToolTipText("Save the image/video loaded in the dataplot window");
        dataplotCheckBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(weatherCameraCheckBox)
                .addGap(18, 18, 18)
                .addComponent(radarMapCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(dataplotCheckBox)
                .addContainerGap())
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(weatherCameraCheckBox)
                .addComponent(radarMapCheckBox)
                .addComponent(dataplotCheckBox))
        );

        mainPanel.add(dataPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 14, 354, 50));

        accessPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Access/Visibility:"));
        accessPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        everyoneRadio.setSelected(true);
        everyoneRadio.setText("Everyone");
        everyoneRadio.setToolTipText("Visible to every user");
        everyoneRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        accessPanel.add(everyoneRadio);

        courseStudentsRadio.setText("Course Students");
        courseStudentsRadio.setToolTipText("Visible to students only in this course");
        courseStudentsRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        accessPanel.add(courseStudentsRadio);

        allStudentsRadio.setText("All Students");
        allStudentsRadio.setToolTipText("Visible to all students who use this program");
        allStudentsRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        accessPanel.add(allStudentsRadio);

        instructorsRadio.setText("Instructors");
        instructorsRadio.setToolTipText("Visible only to instructors");
        instructorsRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        accessPanel.add(instructorsRadio);

        privateRadio.setText("Private");
        privateRadio.setToolTipText("Visible only to the user who creates it");
        privateRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        accessPanel.add(privateRadio);

        mainPanel.add(accessPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 211, 354, 88));

        notesScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        notesEditorPane.setToolTipText("Notes and comments about the bookmark");
        notesEditorPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        notesScrollPanel.setViewportView(notesEditorPane);

        mainPanel.add(notesScrollPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 260, 543, 100));

        rankPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Quality:"));

        rankComboBox.setToolTipText("How well the bookmark captures the ideal instance or event");
        rankComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        javax.swing.GroupLayout rankPanelLayout = new javax.swing.GroupLayout(rankPanel);
        rankPanel.setLayout(rankPanelLayout);
        rankPanelLayout.setHorizontalGroup(
            rankPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rankPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rankComboBox, 0, 149, Short.MAX_VALUE)
                .addContainerGap())
        );
        rankPanelLayout.setVerticalGroup(
            rankPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rankPanelLayout.createSequentialGroup()
                .addComponent(rankComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(rankPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 151, 185, 46));

        saveLocationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Save Location:"));

        saveLocationHardDriveRadio.setText("Local Hard Drive");
        saveLocationHardDriveRadio.setToolTipText("Visible to students only in this course");
        saveLocationHardDriveRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLocationHardDriveRadioActionPerformed(evt);
            }
        });
        saveLocationHardDriveRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        saveLocationDatabaseRadio.setSelected(true);
        saveLocationDatabaseRadio.setText("Database");
        saveLocationDatabaseRadio.setToolTipText("Visible to all students who use this program");
        saveLocationDatabaseRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLocationDatabaseRadioActionPerformed(evt);
            }
        });
        saveLocationDatabaseRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        javax.swing.GroupLayout saveLocationPanelLayout = new javax.swing.GroupLayout(saveLocationPanel);
        saveLocationPanel.setLayout(saveLocationPanelLayout);
        saveLocationPanelLayout.setHorizontalGroup(
            saveLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveLocationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveLocationHardDriveRadio)
                .addGap(18, 18, 18)
                .addComponent(saveLocationDatabaseRadio)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        saveLocationPanelLayout.setVerticalGroup(
            saveLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveLocationPanelLayout.createSequentialGroup()
                .addGroup(saveLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveLocationHardDriveRadio)
                    .addComponent(saveLocationDatabaseRadio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(saveLocationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 78, 354, 59));

        categorySubcategoryButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        categorySubcategoryButton.setText("Manage Categories/Sub-Categories");
        categorySubcategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categorySubcategoryButtonActionPerformed(evt);
            }
        });
        categorySubcategoryButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        mainPanel.add(categorySubcategoryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 86, 235, 25));

        ownerLabel.setText("Owner:");
        mainPanel.add(ownerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 17, -1, -1));

        ownerComboBox.setToolTipText("The bookmarks catagory (ex: 'Clouds')");
        ownerComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        mainPanel.add(ownerComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 14, 143, 22));

        errorLabel.setForeground(new java.awt.Color(204, 0, 0));
        errorLabel.setText("Date From: Invalid Time or Date");
        mainPanel.add(errorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 500, 717, 16));

        attachFilesButton.setText("Attach File");
        attachFilesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachFilesButtonActionPerformed(evt);
            }
        });
        attachFilesButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        mainPanel.add(attachFilesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 374, 109, 25));

        fileListScrollPane.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Files"));
        fileListScrollPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });

        fileList.setBackground(new java.awt.Color(236, 233, 216));
        fileList.setModel(fileListModel);
        fileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fileList.setFixedCellWidth(150);
        fileList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileListMouseReleased(evt);
            }
        });
        fileListScrollPane.setViewportView(fileList);

        mainPanel.add(fileListScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 413, 464, 69));
        mainPanel.add(attachLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(73, 413, 14, 17));

        getContentPane().add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 48, 939, 535));

        bookmarkNameTextField.setToolTipText("Name to be used to referance this bookmark after it is created");
        bookmarkNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        getContentPane().add(bookmarkNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 13, 360, -1));

        bookmarkNameLabel.setText("Name:");
        getContentPane().add(bookmarkNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(407, 16, -1, -1));

        isBookmarkInstanceTypeRadio.setSelected(true);
        isBookmarkInstanceTypeRadio.setText("Bookmark");
        isBookmarkInstanceTypeRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isBookmarkInstanceTypeRadioActionPerformed(evt);
            }
        });
        isBookmarkInstanceTypeRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        getContentPane().add(isBookmarkInstanceTypeRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 12, -1, -1));

        isBookmarkEventTypeRadio.setText("Event");
        isBookmarkEventTypeRadio.setToolTipText("");
        isBookmarkEventTypeRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isBookmarkEventTypeRadioActionPerformed(evt);
            }
        });
        isBookmarkEventTypeRadio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileListScrollPaneFocusGained(evt);
            }
        });
        getContentPane().add(isBookmarkEventTypeRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 12, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void time1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_time1ButtonActionPerformed
        searchBookmarkCalendarWindowOne =
                new BookmarkDateSelectionWindow(this, date1TextPane, 
                time1TextPane, timeZone);
        //An Intance has the same start and end date.
        if (isBookmarkInstanceTypeRadio.isSelected() && !errorLabel.isVisible()) {
            date2TextPane.setText(date1TextPane.getText());
            time2TextPane.setText(time1TextPane.getText());
        }
        if (searchBookmarkCalendarWindowOne.hasSelection()) {
            errorLabel.setVisible(false);
        }

    }//GEN-LAST:event_time1ButtonActionPerformed

    private void time2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_time2ButtonActionPerformed
        searchBookmarkCalendarWindowOne =
                new BookmarkDateSelectionWindow(this, date2TextPane, 
                time2TextPane, timeZone);
        if (searchBookmarkCalendarWindowOne.hasSelection()) {
            errorLabel.setVisible(false);
        }
    }//GEN-LAST:event_time2ButtonActionPerformed

    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        typeComboBox.removeAllItems();
        //Code goes here to fill the Type with the information from the database
        BookmarkCategory category = genService.getDBMSSystem().
                getBookmarkCategoriesManager().get((String) categoryComboBox.
                getSelectedItem());
        bookmarkTypeList = appControl.getAdministratorControlSystem().
                getGeneralService().getDBMSSystem().getBookmarkTypesManager().
                obtainAll(category.getBookmarkCategoryNumber());
        if (bookmarkTypeList.isEmpty()) {
            typeComboBox.setEnabled(false);
        } else {
            typeComboBox.setEnabled(true);
            for (BookmarkType bt : bookmarkTypeList) {
                if (user.getUserType() == UserType.administrator
                        || bt.getViewRights() == CategoryViewRights.system_wide
                        || user.getUserNumber() == bt.getCreatedBy() || !editExisting) {
                    typeComboBox.addItem(bt.getName());
                }
            }
        }
        typeComboBox.revalidate();
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        //Check to see that bookmark has name
        if (bookmarkNameTextField.getText().trim().isEmpty()) {
            errorLabel.setText(emptyNameError);
            errorLabel.setVisible(true);
            return;
        }

        //Time to create a bookmark and upload it to the database.
        //All values are from the form.
        
        //Needed for processing
        DBMSResourceManager resourceManager = genService.getDBMSSystem().
                getResourceManager();

        //First, create bookmark if new
        errorLabel.setVisible(false);
        if (!editExisting) {
            currentBookmark = new Bookmark();
        }

        //Second, load current information into bookmark.
        //Get the name
        currentBookmark.setName(bookmarkNameTextField.getText());

        if (isBookmarkInstanceTypeRadio.isSelected()) {
            currentBookmark.setType(BookmarkDuration.instance);
        } else {
            currentBookmark.setType(BookmarkDuration.event);
        }

        //Get the Bookmark Rank from the comboBox, Nulls are used for any missing value
        String rankSelection = rankComboBox.getSelectedItem().toString();
        BookmarkRank rank;
        if (rankSelection != null) {
            rank = BookmarkRank.getEnum(rankSelection);
        } else {
            rank = BookmarkRank.NOT_RANKED;
        }
        currentBookmark.setRanking(rank);

        //Get the owner
        User owner = userManager.obtainUser((String) ownerComboBox.
                getSelectedItem());
        currentBookmark.setCreatedBy(owner.getUserNumber());

        //Get the catagory number
        String categoryName = categoryComboBox.getSelectedItem().toString();
        currentBookmark.setCategoryNumber(genService.getDBMSSystem()
                .getBookmarkCategoriesManager().searchByName(categoryName).getBookmarkCategoryNumber());

        //Get the sub-category number
        String subCategory = (String) typeComboBox.getSelectedItem();
        currentBookmark.setTypeNumber(genService.getDBMSSystem().
                getBookmarkTypesManager().
                searchByName(subCategory, categoryName).
                getInstanceTypeNumber());

        //Get the access rights
        AccessRights access = null;
        if (courseStudentsRadio.isSelected()) {
            access = AccessRights.CourseStudents;
        } else if (allStudentsRadio.isSelected()) {
            access = AccessRights.AllStudents;
        } else if (instructorsRadio.isSelected()) {
            access = AccessRights.Instructors;
        } else if (everyoneRadio.isSelected()) {
            access = AccessRights.Everyone;
        } else if (privateRadio.isSelected()) {
            access = AccessRights.Private;
        }
        currentBookmark.setAccessRights(access);

        //Get the notes the user typed
        currentBookmark.setNotes(notesEditorPane.getText());

        //Get start and end times from fields
        Calendar startTime;
        String startString = date1TextPane.getText().trim() + ' '
                + time1TextPane.getText().trim();
        try {
            startTime = CalendarFormatter.parse(startString, 
                    CalendarFormatter.DisplayFormat.DEFAULT, timeZone);
        } catch (ParseException ex) {
            errorLabel.setText(invalidFromDateError);
            errorLabel.setVisible(true);
            return;
        }

        Date startDate = new Date(startTime.getTimeInMillis());
        currentBookmark.setStartTime(startDate);
        
        Calendar endTime = startTime;
        if (isBookmarkEventTypeRadio.isSelected()) {
            String endString = date2TextPane.getText().trim() + ' '
                    + time2TextPane.getText().trim();
            try {
                endTime = CalendarFormatter.parse(endString, 
                        CalendarFormatter.DisplayFormat.DEFAULT, timeZone);
            } catch (ParseException ex) {
                errorLabel.setText(invalidToDateError);
                errorLabel.setVisible(true);
                return;
            }
        }
        
        Date endDate = new Date(endTime.getTimeInMillis());
        currentBookmark.setEndTime(endDate); 

        //Check for time errors that will stop save
        long maxLength = Integer.parseInt(PropertyManager.
                getGeneralProperty("maxNumberOfDaysToLoad"));

        if (currentBookmark.getType().equals(BookmarkDuration.event) 
                && !endTime.after(startTime)) {
            errorLabel.setText(endNotAfterStart);
            errorLabel.setVisible(true);
            return;
        }
        
        if (endTime.getTimeInMillis() > ResourceTimeManager.getDefaultRange()
                .getStopTime().getTime()) {
            errorLabel.setText(eventAfterNowError);
            errorLabel.setVisible(true);
            return;
        }
        
        if (endTime.getTimeInMillis() - startTime.getTimeInMillis()
                > maxLength * ResourceTimeManager.MILLISECONDS_PER_DAY) {
            errorLabel.setText("Events cannot be longer than " + maxLength
                    + " days.");
            errorLabel.setVisible(true);
            return;
        }

        //Get resource data
        try {
            File radarCameraFile = null;
            File cameraFile = null;
            File stationFile = null;

            range = new ResourceRange(new Date(startTime.getTimeInMillis()),
                    new Date(endTime.getTimeInMillis()));

            //Make sure there is at least one image in the range if camera and
            //radar are nonel
            if (camNum != -1  && radarNum != -1) {
                long timeDifference = endTime.getTimeInMillis()
                        - startTime.getTimeInMillis();
                long maxTimeBetweenImages = Math.max(resourceManager.
                        getWeatherResourceByNumber(camNum).getFrequency(),
                        resourceManager.getWeatherResourceByNumber(radarNum).
                        getFrequency());

                maxTimeBetweenImages *= ResourceTimeManager.MILLISECONDS_PER_SECOND;
                if (timeDifference < maxTimeBetweenImages) {
                    range.setStopTime(new Date(endTime.getTimeInMillis()
                            + maxTimeBetweenImages));
                }
            }
            
            //Check for just camera.
            if (camNum != -1  && radarNum == -1) {
                long timeDifference = endTime.getTimeInMillis()
                        - startTime.getTimeInMillis();
                long maxTimeBetweenImages = resourceManager.
                        getWeatherResourceByNumber(camNum).getFrequency();

                maxTimeBetweenImages *= ResourceTimeManager.MILLISECONDS_PER_SECOND;
                if (timeDifference < maxTimeBetweenImages) {
                    range.setStopTime(new Date(endTime.getTimeInMillis()
                            + maxTimeBetweenImages));
                }
            }
            
            //Check for just rader.
            if (camNum == -1  && radarNum != -1) {
                long timeDifference = endTime.getTimeInMillis()
                        - startTime.getTimeInMillis();
                long maxTimeBetweenImages = resourceManager.
                        getWeatherResourceByNumber(radarNum).getFrequency();

                maxTimeBetweenImages *= ResourceTimeManager.MILLISECONDS_PER_SECOND;
                if (timeDifference < maxTimeBetweenImages) {
                    range.setStopTime(new Date(endTime.getTimeInMillis()
                            + maxTimeBetweenImages));
                }
            }

            //Weather Camera Image
            if (camNum != -1 && weatherCameraCheckBox.isSelected()) {
                ResourceFileFormatType cameraFormat = 
                        resourceManager.getWeatherResourceByNumber(camNum).getFormat();
                Debug.println("cameraFormat: " + cameraFormat.toString());
                ResourceInstance camImage = getResourceInstance(range, camNum,
                        cameraFormat);
                if (camImage == null) {
                    currentBookmark.setWeatherCameraPicture(null);
                } else {
                    cameraFile = File.createTempFile("camera", ".jpeg");
                    camImage.writeFile(cameraFile);
                    currentBookmark.setWeatherCameraPicture(
                            new ImageInstance(cameraFile));
                }
            } else {
                currentBookmark.setWeatherCameraPicture(null);
            }



            //Radar Image
            if (radarNum != -1 && radarMapCheckBox.isSelected()) {
                ResourceFileFormatType mapFormat = 
                        resourceManager.getWeatherResourceByNumber(radarNum).getFormat();
                Debug.println("mapFormat: " + mapFormat.toString());
                ResourceInstance radarImage = getResourceInstance(range, radarNum,
                        mapFormat);
                if (radarImage == null) {
                    currentBookmark.setWeatherMapPicture(null);
                } else {
                    radarCameraFile = File.createTempFile("siteCamera",
                            ".jpeg");
                    radarImage.writeFile(radarCameraFile);
                    currentBookmark.setWeatherMapPicture(
                            new ImageInstance(radarCameraFile));
                }
            } else {
                currentBookmark.setWeatherMapPicture(null);
            }

            //Dataplot Image
            if (dataplotNum != -1 && dataplotCheckBox.isSelected()) {
                //Create image file
                stationFile = File.createTempFile("stationInstance", ".jpeg");
                stationFile.mkdirs();
                ImageIO.write(stationManager.getImageFromPlotPanel(),
                        "jpeg", stationFile);
                currentBookmark.setWeatherStationPicture(
                        new ImageInstance(stationFile));
            } else {
                currentBookmark.setWeatherStationPicture(null);
            }

            //Cleanup temp filrs
            if (radarCameraFile != null) {
                radarCameraFile.deleteOnExit();
            }
            if (cameraFile != null) {
                cameraFile.deleteOnExit();
            }
            if (stationFile != null) {
                stationFile.deleteOnExit();
            }
        } catch (WeatherException | IOException ex) {
            errorLabel.setText("An error occurred saving the "
                    + (isBookmarkEventTypeRadio.isSelected() ? "Event."
                    : "Bookmark."));
            errorLabel.setVisible(true);
            errorLabel.repaint();
            WeatherLogger.log(Level.SEVERE, null, ex);
            return;
        }

        if (weatherCameraCheckBox.isSelected()) {
            currentBookmark.setWeatherCameraResourceNumber(camNum);
        }
        if (radarMapCheckBox.isSelected()) {
            currentBookmark.setWeatherMapLoopResourceNumber(radarNum);
        }
        if (dataplotCheckBox.isSelected()) {
            currentBookmark.setWeatherStationResourceNumber(dataplotNum);
        }
        
        //Get extra fields for initial save
        if (!editExisting) {
            currentBookmark.setPlotRangeStartTime(stationManager
                    .getResourceRange().getStartTime());
            currentBookmark.setPlotRangeEndTime(stationManager
                    .getResourceRange().getStopTime());
            currentBookmark.setGraphFittedOption(stationManager.isGraphFitted());
            currentBookmark.setSelectedRadioName(stationManager
                    .getSelectedRadioText());
            currentBookmark.setPlotDaySpanComboBoxIndex(stationManager
                    .getDaySpanOptionByIndex());
        }

        //Third, save bookmark
        if (saveLocationHardDriveRadio.isSelected()) {
            //This option covers local saves
            SaveBookmarkInstance sbi;
            if (isBookmarkEventTypeRadio.isSelected()) {
                sbi = new SaveBookmarkInstance(currentBookmark, BookmarkDuration.event);
            } else {
                sbi = new SaveBookmarkInstance(currentBookmark, BookmarkDuration.instance);
            }
            sbi.saveBookmark(this);
        } else {
            if (!editExisting) {
                //This is all that is needed to upload the bookmark to the database.
                if (bookmarkManager.add(currentBookmark)) {
                    //Give files correct bookmark number and save them.
                    for (BookmarkFileInstance bfi : attachedFiles) {
                        bfi.setBookmarkNumber(currentBookmark.getBookmarkNumber());
                        fileManager.insertBookmarksFile(bfi);
                    }
                    JOptionPane.showMessageDialog(this,
                            (currentBookmark.getType()
                            == BookmarkDuration.instance ? "Bookmark " : "Event ")
                            + "added successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    bookmarkNameTextField.setText("");
                    notesEditorPane.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            (currentBookmark.getType()
                            == BookmarkDuration.instance ? "Bookmark " : "Event ")
                            + "could not be added.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //Updating an old bookmark in the database.
                if(bookmarkManager.update(currentBookmark)){
                    //Update files
                    //Find and remove notes in original list not now pressent.
                    for (BookmarkFileInstance bfi : originalFiles) {
                        if (!collectionHasFileNumber(attachedFiles, bfi.getFileNumber())) {
                            fileManager.deleteBookmarksFile(bfi);
                        }
                    }

                    //Find and add any pressent notes not in database
                    for (BookmarkFileInstance bfi : attachedFiles) {
                        if (!collectionHasFileNumber(originalFiles, bfi.getFileNumber())) {
                            fileManager.insertBookmarksFile(bfi);
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            (currentBookmark.getType()
                            == BookmarkDuration.instance ? "Bookmark " : "Event ")
                            + "updated successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    bookmarkNameTextField.setText("");
                    notesEditorPane.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            (currentBookmark.getType()
                            == BookmarkDuration.instance ? "Bookmark " : "Event ")
                            + "could not be updated.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }  //End of database saves.
        this.dispose();
    }//GEN-LAST:event_createButtonActionPerformed

    
    /**
     * Returns the first resource instance that meets the given parameters
     *
     * @param range The range in which the resource instance must be
     * @param resourceNumber The ID number of the resource
     * @param resourceType The file type of the requested resource.
     * @return The first resource instance that meets these criteria or null
     * if no resources were found.
     * @throws WeatherException
     */
    private static ResourceInstance getResourceInstance(ResourceRange range, int resourceNumber,
            ResourceFileFormatType resourceType)
            throws WeatherException {
        DBMSSystemManager dbms = null;
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            //Set GeneralWeather.properties to indicate the program is not running.
            PropertyManager.setGeneralProperty("ClientRunning", "false");
            System.exit(1);
        }
        Resource resource = dbms.getResourceManager().getWeatherResourceByNumber(resourceNumber);
        if (resource == null) {
            return null;
        }
        ResourceInstancesRequested request;
        request = new ResourceInstancesRequested(range, 1, false, resourceType, resource);
        Vector<ResourceInstance> instances = StorageControlSystemImpl.getStorageSystem()
                .getResourceInstances(request).getResourceInstances();
        if (instances.isEmpty()) {
            return null;
        }
        return instances.get(0);
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        close();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveLocationHardDriveRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLocationHardDriveRadioActionPerformed
        accessPanel.setEnabled(false);
        courseStudentsRadio.setEnabled(false);
        allStudentsRadio.setEnabled(false);
        instructorsRadio.setEnabled(false);
        everyoneRadio.setEnabled(false);
        privateRadio.setEnabled(false);
        setCorrectAttachmentState();
    }//GEN-LAST:event_saveLocationHardDriveRadioActionPerformed

    private void saveLocationDatabaseRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLocationDatabaseRadioActionPerformed
        accessPanel.setEnabled(true);
        courseStudentsRadio.setEnabled(true);
        allStudentsRadio.setEnabled(true);
        instructorsRadio.setEnabled(true);
        everyoneRadio.setEnabled(true);
        privateRadio.setEnabled(true);
        setCorrectAttachmentState();
    }//GEN-LAST:event_saveLocationDatabaseRadioActionPerformed

    private void categorySubcategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categorySubcategoryButtonActionPerformed
        manageCategoriesOpened = true;
        new ManageBookmarkCategoryDialog(appControl, false);
    }//GEN-LAST:event_categorySubcategoryButtonActionPerformed

    private void isBookmarkEventTypeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isBookmarkEventTypeRadioActionPerformed
        time1TextPane.setEnabled(true);
        date1TextPane.setEnabled(true);
        time1Button.setEnabled(true);
        fromjLabel.setEnabled(true);
        date1Label.setEnabled(true);
        time1Label.setEnabled(true);
        time2TextPane.setEnabled(true);
        date2TextPane.setEnabled(true);
        time2Button.setEnabled(true);
        tojLabel.setEnabled(true);
        date2Label.setEnabled(true);
        time2Label.setEnabled(true);
        setCorrectAttachmentState();

        //Set the default time of the event
        date1TextPane.setText(CalendarFormatter.format(start,
                CalendarFormatter.DisplayFormat.DATE));
        date2TextPane.setText(CalendarFormatter.format(end,
                CalendarFormatter.DisplayFormat.DATE));
        time1TextPane.setText(CalendarFormatter.format(start,
                CalendarFormatter.DisplayFormat.TIME_12));
        time2TextPane.setText(CalendarFormatter.format(end,
                CalendarFormatter.DisplayFormat.TIME_12));
    }//GEN-LAST:event_isBookmarkEventTypeRadioActionPerformed

    /**
     * Checks if attachments can be added, which can only be done for database
     * bookmarks.  It then either enables attachments or disables them and
     * removes any that are present.
     */
    private void setCorrectAttachmentState(){
        if(isBookmarkInstanceTypeRadio.isSelected() && saveLocationDatabaseRadio.isSelected()){
            attachFilesButton.setEnabled(true);
        } else {
            attachFilesButton.setEnabled(false);
            attachLabel.setVisible(false);
            attachedFiles.clear();
            originalFiles.clear();
            fileListModel.clear();
        }
    }
    
    private void isBookmarkInstanceTypeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isBookmarkInstanceTypeRadioActionPerformed
        time1TextPane.setEnabled(false);
        date1TextPane.setEnabled(false);
        time1Button.setEnabled(false);
        fromjLabel.setEnabled(false);
        date1Label.setEnabled(false);
        time1Label.setEnabled(false);
        time2TextPane.setEnabled(false);
        date2TextPane.setEnabled(false);
        time2Button.setEnabled(false);
        tojLabel.setEnabled(false);
        date2Label.setEnabled(false);
        time2Label.setEnabled(false);
        setCorrectAttachmentState();

        //Set the default time of the bookmark
        date1TextPane.setText(CalendarFormatter.format(now,
                CalendarFormatter.DisplayFormat.DATE));
        date2TextPane.setText(CalendarFormatter.format(now,
                CalendarFormatter.DisplayFormat.DATE));
        time1TextPane.setText(CalendarFormatter.format(now,
                CalendarFormatter.DisplayFormat.TIME_12));
        time2TextPane.setText(CalendarFormatter.format(now,
                CalendarFormatter.DisplayFormat.TIME_12));
    }//GEN-LAST:event_isBookmarkInstanceTypeRadioActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void attachFilesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachFilesButtonActionPerformed
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                new File(CommonLocalFileManager.getRootDirectory()),
                "Attach File", this);
        if (file == null) {
            return;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            int size = 0;
            while (fis.read() != -1) {
                size++;
            }
            fis.close();

            byte[] fileData = new byte[size];
            fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();

            BookmarkFileInstance bfi = new BookmarkFileInstance(-1,
                    editExisting ? currentBookmark.getBookmarkNumber() : -1,
                    genService.getUser().getUserNumber(), file.getName(), 
                    fileData);
            attachedFiles.add(bfi);
            fileListModel.addElement(bfi.getFileName());
            attachLabel.setVisible(true);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to load file.");
            WeatherLogger.log(Level.SEVERE, "Unable to load file.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An I/O error has occurred.");
            WeatherLogger.log(Level.SEVERE, "I/O error :" + e.getMessage());
        }
    }//GEN-LAST:event_attachFilesButtonActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        if (manageCategoriesOpened) {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            String selectedSubCategory = (String) typeComboBox.getSelectedItem();

            categoryComboBox.removeActionListener(comboBoxListener);
            categoryComboBox.removeAllItems();

            GUIComponentFactory.initBookmarkCategoryBox(categoryComboBox,
                    appControl.getDBMSSystem().getEventManager(),
                    genService.getUser());

            categoryComboBox.addActionListener(comboBoxListener);
            if (selectedCategory != null) {
                for (int i = categoryComboBox.getItemCount() - 1; i >= 0; i--) {
                    if (i == 0 || selectedCategory.equals(categoryComboBox.getItemAt(i))) {
                        categoryComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (selectedSubCategory != null) {
                for (int i = typeComboBox.getItemCount() - 1; i >= 0; i--) {
                    if (i == 0 || selectedSubCategory.equals(typeComboBox.getItemAt(i))) {
                        typeComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            manageCategoriesOpened = false;
        }
    }//GEN-LAST:event_formWindowGainedFocus

    private void fileListScrollPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fileListScrollPaneFocusGained
        // Used to clear label by all input and save controls.
        errorLabel.setVisible(false);
    }//GEN-LAST:event_fileListScrollPaneFocusGained

    private void fileListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMouseReleased
        //Handle removals.
        if (evt.isPopupTrigger()) {
            popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            fileList.setSelectedIndex(fileList.locationToIndex(evt.getPoint()));
            return;
        }

        //If code get here, user is saving.
        if (attachedFiles.size() == 0) {
            return;
        }

        int index = fileList.locationToIndex(evt.getPoint());
        BookmarkFileInstance bfi = attachedFiles.get(index);
        
        //Get location from user.
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(new File(CommonLocalFileManager
                        .getDataDirectory()), "Save File...", null, this);
        if (chosenDirectory == null) {
            fileList.clearSelection();
            return;
        }
        File dest = new File(chosenDirectory + File.separator
                + bfi.getFileName());
        if (dest.exists()) {
            JOptionPane.showMessageDialog(this,
                    "The file is already saved at this location.");
            fileList.clearSelection();
            return;
        }

        try {
            FileOutputStream fout = new FileOutputStream(dest);
            fout.write(bfi.getFileData());
            fout.close();
        } catch (Exception e) {
            fileList.clearSelection();
            JOptionPane.showMessageDialog(this, "An error occurred while trying to save the file.");
            //Test for remaining space in application home, which has no effect
            //if the save was not there.
            StorageSpaceTester.testApplicationHome();
            return;
        }
        fileList.clearSelection();
        JOptionPane.showMessageDialog(this, "File saved.");
        
         //Test for remaining space in application home, which has no effect
         //if the save was not there.
         StorageSpaceTester.testApplicationHome();
    }//GEN-LAST:event_fileListMouseReleased

    private void removeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeItemActionPerformed
        if (popupMenu.getInvoker() == fileList) {
            removeSelectedFile();
        }
    }//GEN-LAST:event_removeItemActionPerformed

    /**
     * Removes the selected file from the file list which shows attached files.
     */
    private void removeSelectedFile() {
        int index = fileList.getSelectedIndex();
        try {
            attachedFiles.remove(index);
            fileListModel.remove(index);
        } catch (Exception e) {
            return;  // This just happens when the file list itself is selected, but not any particular file. No action needed.
        }
        attachLabel.setVisible(fileListModel.size() > 0);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel accessPanel;
    private javax.swing.JRadioButton allStudentsRadio;
    private javax.swing.JButton attachFilesButton;
    private javax.swing.JLabel attachLabel;
    private javax.swing.JLabel bookmarkNameLabel;
    private javax.swing.JTextField bookmarkNameTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel catagoryLabel;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JButton categorySubcategoryButton;
    private javax.swing.JRadioButton courseStudentsRadio;
    private javax.swing.JButton createButton;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JCheckBox dataplotCheckBox;
    private javax.swing.JLabel date1Label;
    private javax.swing.JTextPane date1TextPane;
    private javax.swing.JScrollPane date1TextPanel;
    private javax.swing.JLabel date2Label;
    private javax.swing.JTextPane date2TextPane;
    private javax.swing.JPanel datePanel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JRadioButton everyoneRadio;
    private javax.swing.JList<String> fileList;
    private javax.swing.JScrollPane fileListScrollPane;
    private javax.swing.JLabel fromjLabel;
    private javax.swing.JRadioButton instructorsRadio;
    private javax.swing.JRadioButton isBookmarkEventTypeRadio;
    private javax.swing.JRadioButton isBookmarkInstanceTypeRadio;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JEditorPane notesEditorPane;
    private javax.swing.JLabel notesLabel;
    private javax.swing.JScrollPane notesScrollPanel;
    private javax.swing.JComboBox<String> ownerComboBox;
    private javax.swing.JLabel ownerLabel;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JRadioButton privateRadio;
    private javax.swing.JCheckBox radarMapCheckBox;
    private javax.swing.JComboBox<String> rankComboBox;
    private javax.swing.JPanel rankPanel;
    private javax.swing.JMenuItem removeItem;
    private javax.swing.JRadioButton saveLocationDatabaseRadio;
    private javax.swing.JRadioButton saveLocationHardDriveRadio;
    private javax.swing.JPanel saveLocationPanel;
    private javax.swing.JButton time1Button;
    private javax.swing.JLabel time1Label;
    private javax.swing.JTextPane time1TextPane;
    private javax.swing.JButton time2Button;
    private javax.swing.JLabel time2Label;
    private javax.swing.JTextPane time2TextPane;
    private javax.swing.JLabel tojLabel;
    private javax.swing.JComboBox<String> typeComboBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JCheckBox weatherCameraCheckBox;
    // End of variables declaration//GEN-END:variables
}
