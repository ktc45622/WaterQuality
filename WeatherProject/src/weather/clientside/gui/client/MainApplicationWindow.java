package weather.clientside.gui.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.clientside.gui.administrator.*;
import weather.clientside.manager.*;
import weather.clientside.utilities.BarebonesBrowser;
import weather.clientside.utilities.ClientSideLocalFileManager;
import weather.clientside.utilities.ResourceTreeManager;
import weather.clientside.utilities.ResourceVisibleTester;
import weather.clientside.utilities.SnapShotViewer;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.TimedLoader;
import weather.clientside.utilities.VideoFilter;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.WebLink;
import weather.common.data.WebLinkCategories;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSCourseManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.DBMSWebLinkManager;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.*;

/**
 * This GUI displays synchronized webCam video, radar images and weather station
 * data. This file does not use the designer.
 *
 * TODO: Documentation needs completed
 *
 * @author Software Engineering
 * @author Paul Zipko (2007)
 * @author Tom Hand
 * @author David Lusby
 * @author Mike Graboske
 * @author Chris Mertens (2009)
 * @author Ora Merkel (2009)
 * @author Fen Qin (2009)
 * @author Joe Van Lente (2010)
 * @author Andrew Bennett (2010)
 * @author Eric Lowrie (2012)
 * @author Ty Vanderstappen (2012)
 * @author Xiang Li(2014)
 * @author Colton Daily (2014)
 *
 * @version Spring 2014
 */
public class MainApplicationWindow extends javax.swing.JFrame {

    public static final long serialVersionUID = 1;
    
    /*
     * These variables are needed to move in and out of view as student mode.
     */
    //The last dote showing before student mode was lauched.
    private static Date lastDateBeforeStudentMode; 
    //The last camera resource showing before student mode was lauched.
    private static Resource lastCameraBeforeStudentMode; 
    //The last map loop resource showing before student mode was lauched.
    private static Resource lastMapLoopBeforeStudentMode;
    //The last cweather station resource showing before student mode was lauched.
    private static Resource lastStationBeforeStudentMode;
    //The selected tab index of the notes tabbed pane.
    private static int noteTabIndexBeforeStudentMode;
    //This flag tells if the property manager should be reconfigured when the
    //other managers are loaded.  It is initially false because this is redundant
    //after a login.  However, after that properties must be reconfigured to 
    //move in and out of student mode.
    private static boolean configureProperties = false;
    
    private static final String NO_WEBSITE_AVAILABLE_STRING = "No Website Available";
    
    private AboutWindow about;
    private ApplicationControlSystem appControl;
    private MovieController movieController;
    private WeatherMoviePanelManager cameraManager;
    private WeatherMoviePanelManager siteManager;
    private WeatherStationPanelManager weatherStationManager;
    private NotesAndDiaryPanelManager noteAndDiaryManager;
    private boolean isInViewMode;
    private ViewAsStudentWindow viewModeWindow;
    private UserType userType;
    
    // These variables are used for window sizing
    private int interiorHeight;
    private int interiorWidth;
    private int marginSize;
    private int secondColumnX;
    private int buttonBoxX;
    private int notesY;
    private int controlPanelY;
    private int stationContainerY;
    private int cameraContainerControlPanelWidth;
    private int buttonBoxWidth;
    private int firstColumnWidth;
    private int secondColumnWidth;
    private int buttonBoxHeight;
    private int cameraContainerHeight;
    private int controlPanelHeight;
    private int notesHeight;
    private int siteContainerHeight;
    private int stationContainerHeight;
    private int labelHeight;
    private int edgeSize;
    
    // Variables declaration to drae main form.                    
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addWeatherImageMenuItem;
    private javax.swing.JMenuItem adminManageStudentEnrollment;
    private javax.swing.JMenu adminMangeClassesMenu;
    private javax.swing.JMenuItem adminRemoveOldClasses;
    private javax.swing.JMenu administratorMenu;
    private javax.swing.JMenuItem advancedSettingsItem;
    private javax.swing.JMenu bookmarkMenu;
    private javax.swing.JPopupMenu.Separator bookmarkMenuSeparator;
    private javax.swing.JMenuItem browseWorkingDirectoryItem;
    private javax.swing.JPanel buttonContainerPanel;
    private javax.swing.JPanel cameraContainerPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JMenuItem cameraLocationMenuItem;
    private javax.swing.JMenuItem changePasswordMenuItem;
    private javax.swing.JMenuItem contactAdminItem;
    private javax.swing.JMenuItem createBookmarkMenuItem;
    private javax.swing.JMenuItem createMovieMenuItem;
    private javax.swing.JMenuItem defaultSettingMenuItem;
    private javax.swing.JMenuItem editWeatherImageMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exportDailyDiaryMenuItem;
    private javax.swing.JMenu exportDataMenu;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenu exportWeatherCameraDataMenu;
    private javax.swing.JMenuItem exportWeatherCameraMovieMenuItem;
    private javax.swing.JMenuItem exportWeatherCameraMovieMenuItemAlt;
    private javax.swing.JMenuItem exportWeatherMapLoopMenuItem;
    private javax.swing.JMenuItem exportWeatherStationDataMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem forecasterLessonsMenuItem;
    private javax.swing.JMenuItem genericNoDataImageMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenu instructorMenu;
    private javax.swing.JMenuItem instructionLessonsMenuItem;
    private javax.swing.JPopupMenu.Separator searchSeparator;
    private javax.swing.JMenu lessonMenu;
    private javax.swing.JMenu lessonsMenu;
    private javax.swing.JMenuItem listLocalBookmarkMenuItem;
    private javax.swing.JMenuItem listResourcesMenuItem;
    private javax.swing.JMenuItem manageBookmarkCategoriesMenuItem;
    private javax.swing.JMenu manageClassesMenu;
    private javax.swing.JMenuItem manageDailyDiaryWebLinksMenu;
    private javax.swing.JMenuItem manageInstructorFilesMenuItem;
    private javax.swing.JMenuItem manageInstructionLessonsMenuItem;
    private javax.swing.JMenu manageLessonsMenu;
    private javax.swing.JMenuItem manageForecasterLessonsMenuItem;
    private javax.swing.JMenuItem forecastingGradebookMenuItem;
    private javax.swing.JMenu manageResourcesMenu;
    private javax.swing.JMenuItem manageStudentsMenuItem;
    private javax.swing.JMenu manageUsersMenu;
    private javax.swing.JMenuItem manageUsersInstructorMenuItem;
    private javax.swing.JMenuItem manageWebLinksCategoriesMenu;
    private javax.swing.JMenu manageWebLinksMenu;
    private javax.swing.JMenuItem manualMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuManageClasses;
    private javax.swing.JMenuItem menuManageInstructorClasses;
    private javax.swing.JMenuItem menuManageUsers;
    private javax.swing.JMenuItem menuManageWebLinks;
    private javax.swing.JMenuItem menuPurgeOldClasses;
    private javax.swing.JMenuItem mnuitmPreferences;   
    private javax.swing.JMenu manageDataBasePropertyMenu;
    private javax.swing.JMenuItem manageGeneralPropertyMenuItem;
    private javax.swing.JMenuItem manageGUIPropertyMenuItem;
    private javax.swing.JMenuItem manageWeatherStationTwoVariablePropertyMenuItem;
    private javax.swing.JMenuItem manageWeatherStationPropertyMenuItem;
    private javax.swing.JMenuItem manageWeatherStationNoSolarPropertyMenuItem;
    private javax.swing.JMenuItem openLocalBookmarkMenuItem;
    private javax.swing.JMenu openMenu;
    private javax.swing.JMenuItem openSnapshotImageItem;
    private javax.swing.JMenuItem openWeatherCameraMovieMenuItem;
    private javax.swing.JMenuItem openWeatherMapLoopMenuItem;
    private javax.swing.JMenuItem openWeatherStationData;
    private javax.swing.JPanel pnlClassNotes;
    private javax.swing.JPanel pnlPersonalNotes;
    private javax.swing.JMenuItem printDailyDiary;
    private javax.swing.JMenu printMenu;
    private javax.swing.JMenuItem printWeatherCameraImageMenuItem;
    private javax.swing.JMenuItem printWeatherRadarSiteImageMenuItem;
    private javax.swing.JMenuItem printWeatherStationDataMenuItem;
    private javax.swing.JMenuItem purgeInactive;
    private javax.swing.JMenu searchBookmarkEventUnderBookmarkEventJMenu;
    private javax.swing.JMenuItem searchDatabaseBookmarksUnderBookmarksMenuItem;
    private javax.swing.JMenuItem searchLocalBookmarksEventsUnderBookmarksEventsMenuItem;
    private javax.swing.JMenu searchMenu;
    private javax.swing.JMenuItem searchObservationDataMenuItem;
    private javax.swing.JMenuItem searchWeatherStationDataItem;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JPanel siteContainerPanel;
    private javax.swing.JPanel stationContainerPanel;
    private javax.swing.JMenuItem specifyDefaultImage;
    private javax.swing.JTabbedPane tpneNotes;
    private javax.swing.JMenuItem viewAsStudentAdminMenuItem;
    private javax.swing.JMenuItem viewAsStudentInstructorMenuItem;
    private javax.swing.JMenu weatherImagesMenu;
    private javax.swing.JPopupMenu.Separator webLinksSeperator;
    private javax.swing.JMenuItem webPagesMenuItem;
    private javax.swing.JMenu weblinksMenu;

    /*
     * This method computes placements coordinates.
     */
    private void computeCoordinates() {
        Dimension screenRes = ScreenInteractionUtility
                .getCurrentScreenResolution();
        Debug.println("\nRESOLUSION: " + screenRes.width + " X " + screenRes.height);
        int horizontalInsets = getInsets().left + getInsets().right;
        //Calculate widest width so that window fits on screen.
        /**
         * The below code is based on maintaining a constant aspect ratio
         * regardless of the aspect ratio of the screen. The window can use
         * the entire width of the screen if the aspect ratio is not greater
         * than 8/5. Otherwise, given that the aspect ratio of the window is
         * approximately 8/5, the amount of width that can be used to size the
         * window must be determined based on the height of the screen and the
         * desired aspect ratio of 1.6.
         */
        if (5 * screenRes.width >= 8 * screenRes.height) {
            interiorWidth = (int) (1.6 * screenRes.height);
            Debug.println("Wide screen width.");
        } else {
            interiorWidth = screenRes.width;
        }
        //interiorWidth must have insets subtracted to be the interior width
        interiorWidth -= horizontalInsets;
        /**
         * Because we are maintaining a constant aspect ratio, the interior
         * height is a set fraction of the interior width. In turn, the heights
         * of all components, which are fractions, of the interior height, are
         * also fractions of the interior width. Widths of components are, of
         * course, fractions of the interior width. The below fractions, are
         * thus derived by dividing the coordinates on a trial screen by the
         * width of the chosen design area, namely 1662. The trial screen has a
         * width of 1680 and an aspect ratio of 1.6.
         */
        Debug.println("interiorWidth = " + interiorWidth);
        interiorHeight = Math.round((float) (.5469314 * interiorWidth));
        Debug.println("interiorHeight = " + interiorHeight);
        marginSize = Math.round((float) (.0072202 * interiorWidth));
        Debug.println("marginSize = " + marginSize);
        secondColumnX = Math.round((float) (.6233453 * interiorWidth));
        Debug.println("secondColumnX = " + secondColumnX);
        buttonBoxX = Math.round((float) (.4657039 * interiorWidth));
        Debug.println("buttonBoxX = " + buttonBoxX);
        notesY = Math.round((float) (.3922984 * interiorWidth));
        Debug.println("notesY = " + notesY);
        controlPanelY = Math.round((float) (.2761733 * interiorWidth));
        Debug.println("controlPanelY = " + controlPanelY);
        stationContainerY = Math.round((float) (.2737665 * interiorWidth));
        Debug.println("stationContainerY = " + stationContainerY);
        firstColumnWidth = Math.round((float) (.6089049 * interiorWidth));
        Debug.println("firstColumnWidth = " + firstColumnWidth);
        secondColumnWidth = Math.round((float) (.3694344 * interiorWidth));
        Debug.println("secondColumnWidth = " + secondColumnWidth);
        cameraContainerControlPanelWidth = Math.round((float) (.4512635 * interiorWidth));
        Debug.println("cameraContainerControlPanelWidth = " + cameraContainerControlPanelWidth);
        buttonBoxWidth = Math.round((float) (.1504211 * interiorWidth));
        Debug.println("buttonBoxWidth = " + buttonBoxWidth);
        cameraContainerHeight = Math.round((float) (.2617329 * interiorWidth));
        Debug.println("cameraContainerHeight = " + cameraContainerHeight);
        buttonBoxHeight = Math.round((float) (.3778580 * interiorWidth));
        Debug.println("buttonBoxHeight = " + buttonBoxHeight);
        controlPanelHeight = Math.round((float) (.1089049 * interiorWidth));
        Debug.println("controlPanelHeight = " + controlPanelHeight);
        notesHeight = Math.round((float) (.1474127 * interiorWidth));
        Debug.println("notesHeight = " + notesHeight);
        siteContainerHeight = Math.round((float) (.2593261 * interiorWidth));
        Debug.println("siteContainerHeight = " + siteContainerHeight);
        stationContainerHeight = Math.round((float) (.267148 * interiorWidth));
        Debug.println("stationContainerHeight = " + stationContainerHeight);

        //labelHeight and edgeSize are always the same.
        labelHeight = 16;
        edgeSize = 5;
    }

    /**
     * Creates the main GUI.
     *
     * @param appControl The Application Control object to give us access to all
     * Application Control Systems and Managers. A user needs to have been
     * authenticated before this class is instantiated. The Diary Manager class
     * needs the user information to instantiate itself. We also choose what
     * menu items to show based on he user's access rights.
     * @param isInViewMode Boolean that helps the program know if the user is
     * testing a different view mode other than their own. For example, if an
     * instructor is testing what a student can see, this variable is true. In
     * every case where this is the first MainApplicationWindow being generated,
     * this is false.
     * @param viewModeWindow ViewAsStudentWindow holding the "View as Student 
     * Mode" window. Passing to help allow for setting visibility of each 
     * window. If isInViewMode is true, viewModeWindow will hold the JFrame of 
     * the "View" window, and will make it invisible while the custom view mode
     * of the main application is active. Otherwise, this value is null and the 
     * program operates as normal.
     */
    
    public MainApplicationWindow(ApplicationControlSystem appControl,
            boolean isInViewMode, ViewAsStudentWindow viewModeWindow) {
        /**
         * Listeners needed to maintain the current screen location for the
         * <code>ScreenInteractionUtility</code>.
         *
         */
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            /**
             * After gaining focus, this code must update current screen
             * location for the <code>ScreenInteractionUtility</code>.
             *
             * @param evt Not used.
             */
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                if (getThisWindow().isShowing()) {
                    ScreenInteractionUtility.setFocusLocation(getThisWindow()
                        .getLocationOnScreen());
                }
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            /**
             * After a window move, this code must update current screen
             * location for the <code>ScreenInteractionUtility</code>.
             *
             * @param evt Not used.
             */
            @Override
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                if (getThisWindow().isShowing()) {
                    ScreenInteractionUtility.setFocusLocation(getThisWindow()
                        .getLocationOnScreen());
                }
            }
        });
        
        this.appControl = appControl;
        Debug.println("appControl check: " + this.appControl.toString());
        StopWatch stopWatch = new StopWatch();
        User user = appControl.getGeneralService().getUser();
        this.isInViewMode = isInViewMode; // are we pretending to be a student?
        if (viewModeWindow != null) {
            this.viewModeWindow = viewModeWindow;
            this.viewModeWindow.setVisible(false);
        }
        
        setDefaultCloseOperation(MainApplicationWindow.DO_NOTHING_ON_CLOSE);
        
        if (isInViewMode) {
            setTitle("Bloomsburg Weather Viewer CUSTOM VIEW MODE ("
                    + user + ")   logged in as: "
                    + user.getUserType() + ".");
            requestFocus();
        } else {
            setTitle("Bloomsburg Weather Viewer           ("
                    + user + ")  logged in as: "
                    + user.getUserType() + ".");
        }
        configureUserManagers(user, ResourceTimeManager.getResourceStartDate());

        setIconImage(IconProperties.getTitleBarIconImage());

        initializePanelManagers();

        stopWatch.start();
        Debug.println("Initializing components.");
        pack(); //Needed for Insets.  They are 0 before.
        computeCoordinates();
        initComponentsOnThisScreen();  //Replaces initComponents()
        setResizable(false);

        initializePanels();
        
        //Finish setup of notes and diary manager.
        cameraManager.checkForDiaryChange();
        
        WeatherFileChooser.initialize();
        createLinks();
        userType = user.getUserType();
        setAccessRights(userType);
        tpneNotes.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                noteAndDiaryManager.checkPersonalNotesSaved();
            }
        });
        
        //Size screen.
        Dimension thisDim;
        int externalHeight = interiorHeight + menuBar.getHeight() + getInsets().top + getInsets().bottom;
        int externalWidth = interiorWidth + getInsets().left + getInsets().right;
        thisDim = new Dimension(externalWidth, externalHeight);

        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        ScreenInteractionUtility.positionWindow(this, true);

        stopWatch.stop();
        Debug.println("Post panel manager initialization time: "
                + stopWatch.getElapsedTime() + "ms.");
        
        Debug.println("Setting screen visible.");
        setVisible(true);   //Needed here to show first images.
        
         //Set movie controller
        movieController.updateRange(ResourceTimeManager.getResourceRange());
        movieController.checkToEnableButtons();
        movieController.setReady();
        
        //Must configure weaather station manager for use in main window.
        weatherStationManager.configureForMainWindow();
        
        //Copy state from original data plot if this is "View ad Student" mode
        if (isInViewMode) {
            WeatherStationPanelManager sourceManager = viewModeWindow
                    .getCallingWindow().getWeatherStationManager();
            weatherStationManager.setLastManualRange(sourceManager
                    .getLastManualRange());
            weatherStationManager.setResourceRange(sourceManager
                    .getResourceRange());
            weatherStationManager.setGraphFittedState(sourceManager
                    .isGraphFitted());
            weatherStationManager.setGraphByRadioText(sourceManager
                    .getSelectedRadioText());
            weatherStationManager.setDaySpanOptionByIndex(
                    sourceManager.getDaySpanOptionByIndex());
            weatherStationManager.setTabIndex(sourceManager.getTabIndex());
            weatherStationManager.setLinkCheckBoxState(sourceManager
                    .isLiskCheckBoxSelected());
            
            //Also copy note panel tab idex
            tpneNotes.setSelectedIndex(viewModeWindow.getCallingWindow()
                    .getSelectedNotePanelIndex());
        }
     }
    
    /**
     * Gets the selected index of the notes panel.
     * @return The selected index of the notes panel.
     */
    public int getSelectedNotePanelIndex() {
        return tpneNotes.getSelectedIndex();
    }

    /*
     * Used to fit window on this screen.  This replaces the automatically
     * generated method (initComponents) because that method could not be altered to 
     * allow for variable names to be substituted for numbers in the function
     * calls that add the panels to the layout.  
     */
    private void initComponentsOnThisScreen() {

        aboutMenuItem = new javax.swing.JMenuItem();
        adminRemoveOldClasses = new javax.swing.JMenuItem();
        adminManageStudentEnrollment = new javax.swing.JMenuItem();
        adminMangeClassesMenu = new javax.swing.JMenu();
        administratorMenu = new javax.swing.JMenu();
        advancedSettingsItem = new javax.swing.JMenuItem();
        addWeatherImageMenuItem = new javax.swing.JMenuItem();
        
        bookmarkMenuSeparator = new javax.swing.JPopupMenu.Separator();
        buttonContainerPanel = new javax.swing.JPanel();
        browseWorkingDirectoryItem = new javax.swing.JMenuItem();
        bookmarkMenu = new javax.swing.JMenu();
        
        cameraContainerPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        changePasswordMenuItem = new javax.swing.JMenuItem();
        createBookmarkMenuItem = new javax.swing.JMenuItem();
        createMovieMenuItem = new javax.swing.JMenuItem();
        contactAdminItem = new javax.swing.JMenuItem();
        cameraLocationMenuItem = new javax.swing.JMenuItem();
        
        defaultSettingMenuItem = new javax.swing.JMenuItem();
        
        exportDataMenu = new javax.swing.JMenu();
        exportWeatherCameraMovieMenuItem = new javax.swing.JMenuItem();
        exportWeatherMapLoopMenuItem = new javax.swing.JMenuItem();
        exportWeatherStationDataMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        exportDailyDiaryMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        exportWeatherCameraDataMenu = new javax.swing.JMenu();
        exportWeatherCameraMovieMenuItemAlt = new javax.swing.JMenuItem();
        editWeatherImageMenuItem = new javax.swing.JMenuItem();
        
        fileMenu = new javax.swing.JMenu();
        forecasterLessonsMenuItem = new javax.swing.JMenuItem();
        
        genericNoDataImageMenuItem = new javax.swing.JMenuItem();
        
        helpMenu = new javax.swing.JMenu();
        
        instructorMenu = new javax.swing.JMenu();
        instructionLessonsMenuItem = new javax.swing.JMenuItem();
        
        searchSeparator = new javax.swing.JPopupMenu.Separator();
        
        listResourcesMenuItem = new javax.swing.JMenuItem();
        listLocalBookmarkMenuItem = new javax.swing.JMenuItem();
        lessonMenu = new javax.swing.JMenu();
        lessonsMenu = new javax.swing.JMenu();
        
        manualMenuItem = new javax.swing.JMenuItem(); 
        menuBar = new javax.swing.JMenuBar();
        mnuitmPreferences = new javax.swing.JMenuItem();
        manageBookmarkCategoriesMenuItem = new javax.swing.JMenuItem();
        manageInstructionLessonsMenuItem = new javax.swing.JMenuItem();
        manageClassesMenu = new javax.swing.JMenu();
        menuManageInstructorClasses = new javax.swing.JMenuItem();
        menuPurgeOldClasses = new javax.swing.JMenuItem();
        manageStudentsMenuItem = new javax.swing.JMenuItem();
        manageInstructorFilesMenuItem = new javax.swing.JMenuItem();
        manageResourcesMenu = new javax.swing.JMenu();
        manageUsersMenu = new javax.swing.JMenu();
        manageUsersInstructorMenuItem = new javax.swing.JMenuItem();
        menuManageClasses = new javax.swing.JMenuItem();
        manageWebLinksMenu = new javax.swing.JMenu();
        menuManageWebLinks = new javax.swing.JMenuItem();
        manageWebLinksCategoriesMenu = new javax.swing.JMenuItem();
        manageDailyDiaryWebLinksMenu = new javax.swing.JMenuItem();
        menuManageUsers = new javax.swing.JMenuItem();
        manageForecasterLessonsMenuItem = new javax.swing.JMenuItem();
        forecastingGradebookMenuItem = new javax.swing.JMenuItem();
        manageLessonsMenu = new javax.swing.JMenu();
        manageDataBasePropertyMenu = new javax.swing.JMenu();
        manageGeneralPropertyMenuItem =  new javax.swing.JMenuItem();
        manageGUIPropertyMenuItem = new javax.swing.JMenuItem();
        manageWeatherStationTwoVariablePropertyMenuItem = new javax.swing.JMenuItem();
        manageWeatherStationPropertyMenuItem = new javax.swing.JMenuItem();
        manageWeatherStationNoSolarPropertyMenuItem = new javax.swing.JMenuItem();
        
        openMenu = new javax.swing.JMenu();
        openWeatherCameraMovieMenuItem = new javax.swing.JMenuItem();
        openWeatherMapLoopMenuItem = new javax.swing.JMenuItem();
        openWeatherStationData = new javax.swing.JMenuItem();
        openSnapshotImageItem = new javax.swing.JMenuItem();
        openLocalBookmarkMenuItem = new javax.swing.JMenuItem();
        
        pnlPersonalNotes = new javax.swing.JPanel();
        pnlClassNotes = new javax.swing.JPanel();
        printMenu = new javax.swing.JMenu();
        printDailyDiary = new javax.swing.JMenuItem();
        printWeatherCameraImageMenuItem = new javax.swing.JMenuItem();
        printWeatherRadarSiteImageMenuItem = new javax.swing.JMenuItem();
        printWeatherStationDataMenuItem = new javax.swing.JMenuItem();
        purgeInactive = new javax.swing.JMenuItem();
        
        siteContainerPanel = new javax.swing.JPanel();
        stationContainerPanel = new javax.swing.JPanel(); 
        searchMenu = new javax.swing.JMenu();
        searchObservationDataMenuItem = new javax.swing.JMenuItem();
        searchWeatherStationDataItem = new javax.swing.JMenuItem(); 
        searchBookmarkEventUnderBookmarkEventJMenu = new javax.swing.JMenu();
        searchDatabaseBookmarksUnderBookmarksMenuItem = new javax.swing.JMenuItem();
        searchLocalBookmarksEventsUnderBookmarksEventsMenuItem = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        specifyDefaultImage = new javax.swing.JMenuItem();
        
        tpneNotes = new javax.swing.JTabbedPane();
         
        weblinksMenu = new javax.swing.JMenu();
        webPagesMenuItem = new javax.swing.JMenuItem();
        weatherImagesMenu = new javax.swing.JMenu();
        webLinksSeperator = new javax.swing.JPopupMenu.Separator();
               
        viewAsStudentInstructorMenuItem = new javax.swing.JMenuItem(); 
        viewAsStudentAdminMenuItem = new javax.swing.JMenuItem();  

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeWindow();
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained();
            }
        });

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cameraContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cameraContainerPanel.setMaximumSize(new java.awt.Dimension(638, 2147483647));
        cameraContainerPanel.setPreferredSize(new java.awt.Dimension(638, 427));
        cameraContainerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(cameraContainerPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, marginSize, cameraContainerControlPanelWidth, cameraContainerHeight));
        cameraContainerPanel.getAccessibleContext().setAccessibleName("");
        cameraContainerPanel.getAccessibleContext().setAccessibleDescription("");

        siteContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        siteContainerPanel.setPreferredSize(new java.awt.Dimension(355, 325));
        siteContainerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(siteContainerPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(secondColumnX, marginSize, secondColumnWidth, siteContainerHeight));

        controlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        controlPanel.setMaximumSize(new java.awt.Dimension(999999, 2147483647));
        controlPanel.setMinimumSize(new java.awt.Dimension(638, 81));
        controlPanel.setPreferredSize(new java.awt.Dimension(638, 81));
        controlPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(controlPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, controlPanelY, cameraContainerControlPanelWidth, controlPanelHeight));
        controlPanel.getAccessibleContext().setAccessibleName("");

        pnlPersonalNotes.setLayout(new java.awt.BorderLayout());
        tpneNotes.addTab("Personal Notes", pnlPersonalNotes);

        pnlClassNotes.setLayout(new java.awt.BorderLayout());
        tpneNotes.addTab("Class Notes", pnlClassNotes);

        getContentPane().add(tpneNotes,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, notesY, firstColumnWidth, notesHeight));

        stationContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stationContainerPanel.setMinimumSize(new java.awt.Dimension(575, 400));
        stationContainerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(stationContainerPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(secondColumnX, stationContainerY, secondColumnWidth, stationContainerHeight));

        buttonContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        buttonContainerPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(buttonContainerPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(buttonBoxX, marginSize, buttonBoxWidth, buttonBoxHeight));

        menuBar.setAlignmentX(0.0F);
        menuBar.setMaximumSize(new java.awt.Dimension(677, 26));
        menuBar.setMinimumSize(new java.awt.Dimension(677, 26));

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        settingsMenu.setText("Settings");
        settingsMenu.setIcon(IconProperties.getDefaultSettingIcon());
        
        openMenu.setIcon(IconProperties.getOpenDataIconImage());
        openMenu.setMnemonic('l');
        openMenu.setText("Open");

        openWeatherCameraMovieMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        openWeatherCameraMovieMenuItem.setIcon(IconProperties.getMovieIconImage());
        openWeatherCameraMovieMenuItem.setText("Weather Camera Video");
        openWeatherCameraMovieMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openWeatherCameraMovieMenuItemActionPerformed(evt);
            }
        });
        openMenu.add(openWeatherCameraMovieMenuItem);

        openWeatherMapLoopMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        openWeatherMapLoopMenuItem.setIcon(IconProperties.getMovieIconImage());
        openWeatherMapLoopMenuItem.setText("Weather Map Loop");
        openWeatherMapLoopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openWeatherMapLoopMenuItemActionPerformed(evt);
            }
        });
        openMenu.add(openWeatherMapLoopMenuItem);

        openWeatherStationData.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openWeatherStationData.setIcon(IconProperties.getTextDocumentIconImage());
        openWeatherStationData.setText("Weather Station Data Plot Image");
        openWeatherStationData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openWeatherStationDataActionPerformed(evt);
            }
        });
        openMenu.add(openWeatherStationData);

        openSnapshotImageItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        openSnapshotImageItem.setIcon(IconProperties.getPictureIconImage());
        openSnapshotImageItem.setText("Snapshot Image");
        openSnapshotImageItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSnapshotImageItemActionPerformed(evt);
            }
        });
        openMenu.add(openSnapshotImageItem);

        fileMenu.add(openMenu);

        exportDataMenu.setIcon(IconProperties.getSaveIconImage());
        exportDataMenu.setMnemonic('s');
        exportDataMenu.setText("Save");

        exportWeatherCameraMovieMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exportWeatherCameraMovieMenuItem.setIcon(IconProperties.getMovieIconImage());
        exportWeatherCameraMovieMenuItem.setText("Weather Camera Video");
        exportWeatherCameraMovieMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportWeatherCameraMovieMenuItemActionPerformed(evt);
            }
        });
        exportDataMenu.add(exportWeatherCameraMovieMenuItem);

        exportWeatherMapLoopMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exportWeatherMapLoopMenuItem.setIcon(IconProperties.getMovieIconImage());
        exportWeatherMapLoopMenuItem.setText("Weather Map Loop");
        exportWeatherMapLoopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportWeatherMapLoopMenuItemActionPerformed(evt);
            }
        });
        exportDataMenu.add(exportWeatherMapLoopMenuItem);

        exportWeatherStationDataMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exportWeatherStationDataMenuItem.setIcon(IconProperties.getTextDocumentIconImage());
        exportWeatherStationDataMenuItem.setText("Weather Station Data Plot Image");
        exportWeatherStationDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportWeatherStationDataMenuItemActionPerformed(evt);
            }
        });
        exportDataMenu.add(exportWeatherStationDataMenuItem);

        fileMenu.add(exportDataMenu);

        exportMenu.setIcon(IconProperties.getExportIcon());
        exportMenu.setText("Export");

        exportDailyDiaryMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exportDailyDiaryMenuItem.setText("Daily Diary");
        exportDailyDiaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportDailyDiaryMenuItemActionPerformed(evt);
            }
        });
        exportMenu.add(exportDailyDiaryMenuItem);

        fileMenu.add(exportMenu);

        printMenu.setIcon(IconProperties.getSnapshotPrintIconImage());
        printMenu.setMnemonic('p');
        printMenu.setText("Print");

        printDailyDiary.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        printDailyDiary.setText("Daily Diary");
        printDailyDiary.setEnabled(false);
        printMenu.add(printDailyDiary);

        printWeatherCameraImageMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        printWeatherCameraImageMenuItem.setText("Weather Camera Image");
        printWeatherCameraImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printWeatherCameraImageMenuItemActionPerformed(evt);
            }
        });
        printMenu.add(printWeatherCameraImageMenuItem);

        printWeatherRadarSiteImageMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        printWeatherRadarSiteImageMenuItem.setText("Weather Radar/Satellite Image");
        printWeatherRadarSiteImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printWeatherRadarSiteImageMenuItemActionPerformed(evt);
            }
        });
        printMenu.add(printWeatherRadarSiteImageMenuItem);

        printWeatherStationDataMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        printWeatherStationDataMenuItem.setText("Weather Station Data Plot Image");
        printWeatherStationDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printWeatherStationDataMenuItemActionPerformed(evt);
            }
        });
        printMenu.add(printWeatherStationDataMenuItem);

        fileMenu.add(printMenu);
        
        
        defaultSettingMenuItem.setMnemonic('d');
        defaultSettingMenuItem.setText("Change Local Settings");
        defaultSettingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                 defaultSettingMenuItenActionPerformed(evt);
            }
        });
        
        settingsMenu.add(defaultSettingMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setIcon(IconProperties.getCross());
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(settingsMenu);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        searchMenu.setMnemonic('S');
        searchMenu.setText("Search");

        searchObservationDataMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        searchObservationDataMenuItem.setText("Search Observation Data");
        searchObservationDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchObservationDataMenuItemActionPerformed(evt);
            }
        });
        searchMenu.add(searchObservationDataMenuItem);

        searchWeatherStationDataItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK));
        searchWeatherStationDataItem.setText("Search Weather Station Data");
        searchWeatherStationDataItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchWeatherStationDataItemActionPerformed(evt);
            }
        });
        searchMenu.add(searchWeatherStationDataItem);
        searchMenu.add(searchSeparator);

        browseWorkingDirectoryItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        browseWorkingDirectoryItem.setMnemonic('d');
        browseWorkingDirectoryItem.setText("Browse Data Directory");
        browseWorkingDirectoryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseWorkingDirectoryItemActionPerformed(evt);
            }
        });
        searchMenu.add(browseWorkingDirectoryItem);

        mnuitmPreferences.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnuitmPreferences.setMnemonic('e');
        mnuitmPreferences.setText("Edit Local Working Directory");
        mnuitmPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitmPreferencesActionPerformed(evt);
            }
        });
        settingsMenu.add(mnuitmPreferences);

        changePasswordMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        changePasswordMenuItem.setText("Change Password");
        changePasswordMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePasswordMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(changePasswordMenuItem);
        
        advancedSettingsItem.setText("Advanced Settings");
        advancedSettingsItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                advancedSettingsMenuItemActionperformed(e);
            }
        });
        settingsMenu.add(advancedSettingsItem);

        menuBar.add(searchMenu);

        bookmarkMenu.setText("Bookmarks/Events");

        openLocalBookmarkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        openLocalBookmarkMenuItem.setText("Open Bookmarks/Events");
        openLocalBookmarkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLocalBookmarkMenuItemActionPerformed(evt);
            }
        });
        bookmarkMenu.add(openLocalBookmarkMenuItem);

        searchBookmarkEventUnderBookmarkEventJMenu.setText("Search Bookmarks/Events");

        searchDatabaseBookmarksUnderBookmarksMenuItem.setText("Search Database Bookmarks/Events");
        searchDatabaseBookmarksUnderBookmarksMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDatabaseBookmarksUnderBookmarksMenuItemActionPerformed(evt);
            }
        });
        searchBookmarkEventUnderBookmarkEventJMenu.add(searchDatabaseBookmarksUnderBookmarksMenuItem);

        searchLocalBookmarksEventsUnderBookmarksEventsMenuItem.setText("Search Local Bookmarks/Events");
        searchLocalBookmarksEventsUnderBookmarksEventsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchLocalBookmarksEventsUnderBookmarksEventsMenuItemActionPerformed(evt);
            }
        });
        searchBookmarkEventUnderBookmarkEventJMenu.add(searchLocalBookmarksEventsUnderBookmarksEventsMenuItem);

        bookmarkMenu.add(searchBookmarkEventUnderBookmarkEventJMenu);

        createBookmarkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        createBookmarkMenuItem.setIcon(IconProperties.getAddBookmarkIconImage());
        createBookmarkMenuItem.setText("Create Bookmark/Event");
        createBookmarkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBookmarkMenuItemActionPerformed1(evt);
            }
        });
        bookmarkMenu.add(createBookmarkMenuItem);
        bookmarkMenu.add(bookmarkMenuSeparator);

        listLocalBookmarkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        listLocalBookmarkMenuItem.setText("Manage Bookmarks/Events");
        listLocalBookmarkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLocalBookmarkMenuItemActionPerformed(evt);
            }
        });
        bookmarkMenu.add(listLocalBookmarkMenuItem);

        manageBookmarkCategoriesMenuItem.setText("Manage Bookmark Categories And Sub-Categories");
        manageBookmarkCategoriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageBookmarkCategoriesMenuItemActionPerformed(evt);
            }
        });
        bookmarkMenu.add(manageBookmarkCategoriesMenuItem);

        menuBar.add(bookmarkMenu);

        lessonMenu.setMnemonic('L');
        lessonMenu.setText("Lessons");
        
        lessonsMenu.setText("Lessons");

        instructionLessonsMenuItem.setText("Instruction Lessons");
        instructionLessonsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instructionLessonMenuItemActionPerformed(evt);
            }
        });
        
        forecasterLessonsMenuItem.setText("Forecasting Lessons");
        forecasterLessonsMenuItem.setEnabled(true);
        forecasterLessonsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                forecasterLessonMenuItemActionPerformed(evt);
            }
        });
        
        lessonMenu.add(forecasterLessonsMenuItem);
        // userType variable not properly instantiated, must get user from appControl:
        if(appControl.getGeneralService().getUser().getUserType() == UserType.student 
                || appControl.getGeneralService().getUser().getUserType() == UserType.guest)
            lessonMenu.add(forecastingGradebookMenuItem);
        lessonMenu.add(instructionLessonsMenuItem);
        

        menuBar.add(lessonMenu);

        weblinksMenu.setMnemonic('W');
        weblinksMenu.setText("Web Links");
        menuBar.add(weblinksMenu);

        exportWeatherCameraDataMenu.setMnemonic('c');
        exportWeatherCameraDataMenu.setText("Weather Camera");

        cameraLocationMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        cameraLocationMenuItem.setIcon(IconProperties.getGlobeIconImage());
        cameraLocationMenuItem.setText("Weather Camera Location/Map");
        cameraLocationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraLocationMenuItemActionPerformed(evt);
            }
        });
        exportWeatherCameraDataMenu.add(cameraLocationMenuItem);

        exportWeatherCameraMovieMenuItemAlt.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exportWeatherCameraMovieMenuItemAlt.setText("Save Weather Camera Video");
        exportWeatherCameraMovieMenuItemAlt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportWeatherCameraMovieMenuItemAltActionPerformed(evt);
            }
        });
        exportWeatherCameraDataMenu.add(exportWeatherCameraMovieMenuItemAlt);

        menuBar.add(exportWeatherCameraDataMenu);

        instructorMenu.setText("Instructor");
        
        manageLessonsMenu.setText("Manage Lessons");
        
        manageInstructionLessonsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        manageInstructionLessonsMenuItem.setText("Manage Instruction Lessons");
        manageInstructionLessonsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageInstructorLessonsMenuItemActionPerformed(evt);
            }
        });
        
        manageForecasterLessonsMenuItem.setText("Manage Forecasting Lessons");
        manageForecasterLessonsMenuItem.setEnabled(true);
        manageForecasterLessonsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manageForecasterLessonActionPerformed(e);
            }
        });
        
        forecastingGradebookMenuItem.setText("Forecasting Lesson Gradebook");
        forecastingGradebookMenuItem.setEnabled(true);
        forecastingGradebookMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                forecastingGradebookMenuItemActionPerformed(e);
            }
        });
        
        // userType variable not properly instantiated, must get user from appControl:
        if(appControl.getGeneralService().getUser().getUserType() == UserType.administrator 
                || appControl.getGeneralService().getUser().getUserType() == UserType.instructor)
            manageLessonsMenu.add(forecastingGradebookMenuItem);
        
        manageLessonsMenu.add(manageForecasterLessonsMenuItem);
        manageLessonsMenu.add(manageInstructionLessonsMenuItem);
        
        instructorMenu.add(manageLessonsMenu);

        manageClassesMenu.setText("Manage Classes");

        menuManageInstructorClasses.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        menuManageInstructorClasses.setText("Manage Your Classes");
        menuManageInstructorClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuManageInstructorClassesActionPerformed(evt);
            }
        });
        manageClassesMenu.add(menuManageInstructorClasses);

        menuPurgeOldClasses.setText("Remove Your Old Classes");
        menuPurgeOldClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPurgeOldClassesActionPerformed(evt);
            }
        });
        manageClassesMenu.add(menuPurgeOldClasses);

        manageStudentsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        manageStudentsMenuItem.setText("Manage Student Enrollment");
        manageStudentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageStudentsMenuItemActionPerformed(evt);
            }
        });
        manageClassesMenu.add(manageStudentsMenuItem);

        instructorMenu.add(manageClassesMenu);

        manageInstructorFilesMenuItem.setText("Manage Instructor Files");
        manageInstructorFilesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageInstructorFilesMenuItemActionPerformed(evt);
            }
        });
        instructorMenu.add(manageInstructorFilesMenuItem);

        webPagesMenuItem.setText("View And Save Web Pages");
        webPagesMenuItem.setEnabled(false);
        webPagesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webPagesMenuItemActionPerformed(evt);
            }
        });
        instructorMenu.add(webPagesMenuItem);

        weatherImagesMenu.setText("Weather Images");
        weatherImagesMenu.setEnabled(false);

        addWeatherImageMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        addWeatherImageMenuItem.setText("Add Weather Image");
        addWeatherImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWeatherImageMenuItemActionPerformed(evt);
            }
        });
        weatherImagesMenu.add(addWeatherImageMenuItem);

        editWeatherImageMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        editWeatherImageMenuItem.setText("Edit Weather Image");
        weatherImagesMenu.add(editWeatherImageMenuItem);

        instructorMenu.add(weatherImagesMenu);

        viewAsStudentInstructorMenuItem.setText("View As Your Student");
        viewAsStudentInstructorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAsStudentInstructorMenuItemActionPerformed(evt);
            }
        });
        instructorMenu.add(viewAsStudentInstructorMenuItem);

        createMovieMenuItem.setText("Create A Movie");
        createMovieMenuItem.setEnabled(false);
        createMovieMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //new CreateMovieWindow();
            }
        });
        instructorMenu.add(createMovieMenuItem);
        
        manageUsersInstructorMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        manageUsersInstructorMenuItem.setText("Manage Users");
        manageUsersInstructorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userListActionEvent(false);
            }
        });
        instructorMenu.add(manageUsersInstructorMenuItem);

        menuBar.add(instructorMenu);

        administratorMenu.setText("Administrator");
        administratorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                administratorMenuActionPerformed(evt);
            }
        });

        manageDataBasePropertyMenu.setText("Manage System Settings");
        
        manageGeneralPropertyMenuItem.setText("Manage General Properties");
        manageGeneralPropertyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageGeneralPropertyMenuItemActionPerformed(evt);
            }
        });
        
        manageDataBasePropertyMenu.add(manageGeneralPropertyMenuItem);
        
        manageGUIPropertyMenuItem.setText("Manage GUI Properties");
        manageGUIPropertyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageGUIPropertyMenuItemActionPerformed(evt);
            }
        });
        
        manageDataBasePropertyMenu.add(manageGUIPropertyMenuItem);
        
        manageWeatherStationPropertyMenuItem.setText("Manage Weather Station Single Variable w/ Solar Radiation Plot Properties");
        manageWeatherStationPropertyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageWeatherStationPropertyMenuItemActionPerformed(evt);
            }
        });
        
        manageDataBasePropertyMenu.add(manageWeatherStationPropertyMenuItem);
        
        manageWeatherStationNoSolarPropertyMenuItem.setText("Manage Weather Station Single Variable w/o Solar Radiation Plot Properties");
        manageWeatherStationNoSolarPropertyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageWeatherStationNoSolarPropertyMenuItemActionPerformed(evt);
            }
        });
        
        manageDataBasePropertyMenu.add(manageWeatherStationNoSolarPropertyMenuItem);
        
        manageWeatherStationTwoVariablePropertyMenuItem.setText("Manage Weather Station Multi-Variable Plot Properties");
        manageWeatherStationTwoVariablePropertyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageWeatherStationTwoVariablePropertyMenuItemActionPerformed(evt);
            }
        });
        
        manageDataBasePropertyMenu.add(manageWeatherStationTwoVariablePropertyMenuItem);
     
        
        administratorMenu.add(manageDataBasePropertyMenu);
        
        manageResourcesMenu.setMnemonic('r');
        manageResourcesMenu.setText("Manage Resources");

        listResourcesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        listResourcesMenuItem.setText("Manage Data Resources");
        listResourcesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listResourcesMenuItemActionPerformed(evt);
            }
        });
        manageResourcesMenu.add(listResourcesMenuItem);

        specifyDefaultImage.setMnemonic('d');
        specifyDefaultImage.setText("Modify Default Day/Night Images");
        specifyDefaultImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SpecifyDefaultImageActionPerformed(evt);
            }
        });
        manageResourcesMenu.add(specifyDefaultImage);

        genericNoDataImageMenuItem.setText("Modify Default \"No-Data\" Image");
        genericNoDataImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericNoDataImageMenuItemActionPerformed(evt);
            }
        });
        manageResourcesMenu.add(genericNoDataImageMenuItem);

        administratorMenu.add(manageResourcesMenu);

        manageUsersMenu.setMnemonic('u');
        manageUsersMenu.setText("Manage Users");

        menuManageUsers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuManageUsers.setText("Manage Users");
        menuManageUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userListActionEvent(true);
            }
        });
        manageUsersMenu.add(menuManageUsers);

        purgeInactive.setText("Remove Inactive Students");
        purgeInactive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purgeInactiveActionPerformed(evt);
            }
        });
        manageUsersMenu.add(purgeInactive);

        administratorMenu.add(manageUsersMenu);

        adminMangeClassesMenu.setText("Manage Classes");

        menuManageClasses.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuManageClasses.setText("Manage All Classes");
        menuManageClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuManageClassesActionPerformed(evt);
            }
        });
        adminMangeClassesMenu.add(menuManageClasses);

        adminRemoveOldClasses.setText("Remove Old Classes");
        adminRemoveOldClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminRemoveOldClassesActionPerformed(evt);
            }
        });
        adminMangeClassesMenu.add(adminRemoveOldClasses);

        adminManageStudentEnrollment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        adminManageStudentEnrollment.setText("Manage Student Enrollment");
        adminManageStudentEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminManageStudentEnrollmentActionPerformed(evt);
            }
        });
        adminMangeClassesMenu.add(adminManageStudentEnrollment);

        administratorMenu.add(adminMangeClassesMenu);

        manageWebLinksMenu.setText("Manage Web Links");

        menuManageWebLinks.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuManageWebLinks.setText("Manage Web Links");
        menuManageWebLinks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuManageWebLinksActionPerformed(evt);
            }
        });
        manageWebLinksMenu.add(menuManageWebLinks);
        manageWebLinksMenu.add(webLinksSeperator);

        manageWebLinksCategoriesMenu.setText("Manage Web Link Categories");
        manageWebLinksCategoriesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageWebLinksCategoriesMenuActionPerformed(evt);
            }
        });
        manageWebLinksMenu.add(manageWebLinksCategoriesMenu);

        administratorMenu.add(manageWebLinksMenu);

        manageDailyDiaryWebLinksMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        manageDailyDiaryWebLinksMenu.setText("Manage Daily Diary Web Links");
        manageDailyDiaryWebLinksMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageDailyDiaryWebLinksMenuActionPerformed(evt);
            }
        });
        administratorMenu.add(manageDailyDiaryWebLinksMenu);

        viewAsStudentAdminMenuItem.setText("View As Any Student...");
        viewAsStudentAdminMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAsStudentAdminMenuItemActionPerformed(evt);
            }
        });
        administratorMenu.add(viewAsStudentAdminMenuItem);        

        menuBar.add(administratorMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        manualMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        manualMenuItem.setMnemonic('m');
        manualMenuItem.setText("View Manual");
        manualMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(manualMenuItem);

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        contactAdminItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        contactAdminItem.setMnemonic('c');
        contactAdminItem.setText("Contact the Administrator");
        contactAdminItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactAdminItemActionPerformed(evt);
            }
        });
        helpMenu.add(contactAdminItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }

    /**
     * Returns the MovieController for the main application window.
     *
     * @return The main window movie controller.
     */
    public MovieController getMovieController() {
        return this.movieController;
    }

    /**
     * This method is called when the main application starts. This method is
     * responsible for initializing all panels and resource managers.
     */
    private void initializePanelManagers() {
        Debug.println("Initializing movie controller and managers.");
        initializeStationManager();
        initializeMovieController();
        initializeCameraManager();
        initializeSiteManager();
        initializeNoteAndDiaryManager();
        movieController.registerMoviePanelManager(cameraManager);
        movieController.registerMoviePanelManager(siteManager);
        movieController.registerWeatherStationPanel(weatherStationManager);
        movieController.registerNotesManager(noteAndDiaryManager);
    }

    /**
     * This method is part of setting up the main application window. This
     * method loads the movie controller, which is used in conjunction with the
     * WeatherCameraPanelManager, ExternalWindow and MultiMovieViewer classes to
     * show weather camera videos.
     */
    private void initializeMovieController() {
        movieController = new MovieController(appControl, false);
    }

    /**
     * This method is called when the main application starts. This method is
     * responsible for loading the WeatherStattionManager, which will show the
     * initial/default data plot in the weather station pane.
     */
    private void initializeStationManager() {
        Resource defaultResource = appControl.getGeneralService().getCurrentWeatherStationResource();
        weatherStationManager = new WeatherStationPanelManager(appControl,
                ResourceTimeManager.getResourceRange(), defaultResource, this);
    }

    /**
     * This method is called when the main application starts. This method is
     * responsible for initializing the siteManager, which will load the
     * initial/default weather map data in the site pane.
     */
    private void initializeSiteManager() {
        Resource defaultResource = appControl.getGeneralService().getCurrentWeatherMapLoopResource();
        siteManager = new WeatherMoviePanelManager(movieController, appControl,
                defaultResource, ResourceTimeManager.getResourceRange(), 
                WeatherResourceType.WeatherMapLoop, this);
    }

    /**
     * This method is called when the main application begins. This method is
     * responsible for initializing the weather camera manager, which will show
     * the initial/default camera video in the camera pane.
     */
    private void initializeCameraManager() {
        Resource defaultResource = appControl.getGeneralService().getCurrentWeatherCameraResource();
        cameraManager = new WeatherMoviePanelManager(movieController, appControl,
                defaultResource, ResourceTimeManager.getResourceRange(),
                WeatherResourceType.WeatherCamera, this);
    }

    /**
     * Initializes the NotesAndDiaryManager object for the main application
     * window.
     */
    private void initializeNoteAndDiaryManager() {
        noteAndDiaryManager =
                new NotesAndDiaryPanelManager(this, this.appControl,
                movieController);
    }
    
    /**
     * A function to make this object available to the inner classes.
     * @return This object as a <code>Component</code>.
     */
    private Component getThisWindow() {
        return this;
    }

    /**
     * Sets the Web Links menu dynamically creating a for-each loop going
     * through each web-links categories, then another for-each loop going
     * through each web-links and displays them for each category, gives an
     * empty menu item if the category does not contain any web-links.
     */
    private void createLinks() {
        Debug.println("Creating links.");
        DBMSSystemManager dbms;
        dbms = appControl.getDBMSSystem();
        DBMSWebLinkManager webLinkManager = dbms.getWebLinkManager();
        weblinksMenu.removeAll();
        final Vector<WebLinkCategories> wLinksCats = webLinkManager.obtainAllWebLinkCategories();
        final Vector<WebLink> wLinks = webLinkManager.obtainAllWebLinks();
        boolean isEmpty = true;
        //This will check for all the weblink categories
        for (WebLinkCategories s : wLinksCats) {
            JMenu linkCategories = new JMenu(s.getLinkCategory());
            linkCategories.setText(s.getLinkCategory());
            weblinksMenu.add(linkCategories);
            //This gets a full vector of weblink websites, it associates
            //to the weblinks menus by a number and if the link category is
            //empty it will add an empty link menuitem, otherwise it will
            //add the category of weblinks
            for (WebLink w : wLinks) {
                if (s.getLinkCategoryNumber() == w.getLinkCategoryNumber()) {
                    isEmpty = false;
                    JMenuItem link = new JMenuItem(w.getName());
                    link.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            JMenuItem j = (JMenuItem) evt.getSource();
                            String URL = null;
                            for (WebLink urlLink : wLinks) {
                                if (urlLink.getName().equals(j.getText())) {
                                    URL = urlLink.getURLString();
                                }
                            }
                            BarebonesBrowser.openURL(URL, getThisWindow());
                        }
                    });
                    link.setText(w.getName());
                    linkCategories.add(link);
                }
            }
            if (isEmpty) {
                linkCategories.add(NO_WEBSITE_AVAILABLE_STRING).setEnabled(false);
            }
            isEmpty = true;
        }
        Debug.println("Finished creating links.");
    }

    /**
     * This method is called when the main application begins. This method
     * initializes all panels in the main window.
     */
    private void initializePanels() {
        Debug.println("Initializing panels.");
        initializeControlPanel();
        initializeCameraPanel();
        initializeMapPanel();
        initializeStationPanel();
        initializeNotesPanel();
    }

    /**
     * Initializes the control panel and adds it to the main window. The control
     * panel contains the controls for playing camera videos.
     */
    private void initializeControlPanel() {
        Dimension dim = controlPanel.getSize();
        controlPanel.add(movieController.getControlPanel((int) dim.getWidth(), (int) dim.getHeight()));
        dim = buttonContainerPanel.getSize();
        buttonContainerPanel.add(movieController.getButtonPanel((int) dim.getWidth(), (int) dim.getHeight()));
    }
    
    /**
     * Initializes the given JPanel to show the given MoviePanelManager.
     * @param panel The JPanel.
     * @param manager The MoviePanelManager.
     * @param labelText The text of the JLabel at the top of the panel. 
     */
    private void initPanel(javax.swing.JPanel panel, MoviePanelManager manager, String labelText){
        Dimension dim = panel.getSize();
        JLabel topLabel = new JLabel(labelText);
        topLabel.setFont(new java.awt.Font("Tahoma", 1, 13));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(topLabel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(edgeSize, edgeSize,
                (int) dim.getWidth() - 2 * edgeSize, labelHeight));
        panel.add(manager.getPanel((int) dim.getWidth() - 2 * edgeSize,
                (int) dim.getHeight() - labelHeight - 2 * edgeSize),
                new org.netbeans.lib.awtextra.AbsoluteConstraints(edgeSize, labelHeight + edgeSize,
                (int) dim.getWidth() - 2 * edgeSize, (int) dim.getHeight() - labelHeight - 2 * edgeSize));
        this.repaint();
    }

    /**
     * Initializes the camera panel and adds it to the main window. The camera
     * panel contains the camera video.
     */
    private void initializeCameraPanel() {
        initPanel(cameraContainerPanel, cameraManager, "Weather Camera Window");
    }

    /**
     * Initializes the weather map panel and adds it to the main window. The
     * weather map panel contains weather map data.
     */
    private void initializeMapPanel() {
        initPanel(siteContainerPanel, siteManager, "Weather Map Window");
    }

    /**
     * Initializes the notes panel and adds it to the main window. The notes
     * panel allows the user to write notes and keep a daily diary.
     */
    public void initializeNotesPanel() {
        noteAndDiaryManager.setCurrentPane(tpneNotes);
        pnlPersonalNotes.add(noteAndDiaryManager.getPersonalNotesPanel());
        pnlClassNotes.add(noteAndDiaryManager.getClassNotesPanel());
        this.repaint();
    }

    /**
     * Adds the WeatherDataPlotPanel to one tab of a JTabbedPane and a panel
     * listing the variables that can be displayed to another tab. An
     * ActionListener is added to the tab that updates the plot if the user
     * chooses to hide/show variables.
     */
    private void initializeStationPanel() {
        Dimension dim = stationContainerPanel.getSize();
        JLabel DataPlotLabel = new JLabel("Data Plot Window");
        DataPlotLabel.setFont(new java.awt.Font("Tahoma", 1, 13));
        DataPlotLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stationContainerPanel.add(DataPlotLabel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(edgeSize, edgeSize,
                (int) dim.getWidth() - 2 * edgeSize, labelHeight));
        stationContainerPanel.add(weatherStationManager.getWeatherStationPanel(),
                new org.netbeans.lib.awtextra.AbsoluteConstraints(edgeSize, labelHeight + edgeSize,
                (int) dim.getWidth() - 2 * edgeSize, (int) dim.getHeight() - labelHeight - 2 * edgeSize));;
        this.repaint();
    }

    /**
     * Disables and hides menus according to the type of user. Students and
     * Guests cannot see administrator or instructor menus, Instructors cannot
     * see administrator menus.
     *
     * @param userType The type of user using this GUI.
     */
    public final void setAccessRights(UserType userType) {
        Debug.println("Setting access rights.");
        if (userType == null || userType.equals(UserType.guest)) {
            this.administratorMenu.setVisible(false);
            this.instructorMenu.setVisible(false);
            this.administratorMenu.setEnabled(false);
            this.instructorMenu.setEnabled(false);
            this.changePasswordMenuItem.setEnabled(false);
            this.manageLessonsMenu.setVisible(false);
            this.listLocalBookmarkMenuItem.setVisible(false);
            this.manageBookmarkCategoriesMenuItem.setVisible(false);
            this.bookmarkMenuSeparator.setVisible(false);
        } else if (userType.equals(UserType.student)) {
            this.administratorMenu.setVisible(false);
            this.instructorMenu.setVisible(false);
            this.administratorMenu.setEnabled(false);
            this.instructorMenu.setEnabled(false);
            this.manageLessonsMenu.setVisible(false);
            this.listLocalBookmarkMenuItem.setVisible(false);
            this.manageBookmarkCategoriesMenuItem.setVisible(false);
            this.bookmarkMenuSeparator.setVisible(false);
        } else if (userType.equals(UserType.instructor)) {
            this.instructorMenu.setEnabled(true);
            this.administratorMenu.setVisible(false);
            this.administratorMenu.setEnabled(false);
        } else if (userType == UserType.administrator) {
            this.instructorMenu.setEnabled(true);
            this.instructorMenu.setVisible(true);
        }
    }

    /**
     * Stops both of the movies on this GUI. Used when an ExternalWindow,
     * NotesWindow or some other dialog box opens.
     */
    public void stopMovies() {
        movieController.stopMovies();
    }

    /**
     * Returns the weatherStationPanelManager from this window.
     *
     * @return WeatherStationPanelManager that is the Weather Station Panel
     * portion of this window.
     */
    public WeatherStationPanelManager getWeatherStationManager() {
        return weatherStationManager;
    }

    /**
     * Returns the current Weather Camera Movie Panel Manager from this window.
     *
     * @return MoviePanelManager that is the cameraManager.
     */
    public MoviePanelManager getCameraManager() {
        return cameraManager;
    }

    /**
     * Returns the current Radar site Movie Panel manager from this window.
     *
     * @return MoviePanelManager that is the siteManager.
     */
    public MoviePanelManager getSiteManager() {
        return siteManager;
    }
    
    /**
     * Returns the current notes and diary panel manager from this window.
     *
     * @return the window's NotesAndDiaryPanelManager.
     */
    public NotesAndDiaryPanelManager getNotesManager() {
        return noteAndDiaryManager;
    }


    /**
     * This method is only needed at design time and is used by the Form Editor. 
     * It is a remnant of the fact that, at one time the Form Editor was used to
     * design this form.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1682, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 946, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Sets the last date and resources shown before the program enters view as 
     * student mode so the program can return to its current state after student 
     * mode is closed.
     * @param date The date to to be saved (date of the notes shown).
     * @param cameraResource The current camera <code>Resource</code>
     * @param mapResource The current map loop <code>Resource</code> 
     * @param stationResource The current station <code>Resource</code>  
     * @param notePanelIndex The current index of the note panel.
     */
    public void saveSettingsForReturn(Date date, Resource cameraResource,
            Resource mapResource, Resource stationResource, int notePanelIndex){
        lastDateBeforeStudentMode = date;
        lastCameraBeforeStudentMode = cameraResource;
        lastMapLoopBeforeStudentMode = mapResource;
        lastStationBeforeStudentMode = stationResource;
        noteTabIndexBeforeStudentMode = notePanelIndex;
    }
    
    /**
     * Returns the movie controller time.
     * @return The movie controller time.
     */
    public Date getControllerTime(){
        return movieController.getCurrentAbsoluteTime();
    }
    
    /**
     * Helper function to close window.
     */
    private void closeWindow() {
        if (!this.noteAndDiaryManager.getPersonalNotesSaved()) {
            this.noteAndDiaryManager.checkPersonalNotesSaved();
        }
        if (!isInViewMode) {
            this.appControl.getClientControlSystem().closeProgram(
                    appControl, true, this);
        } else {
            //Find all forms that can't be open.
            String openForms = "";
            if (getNotesManager().isExternal()) {
                //Can not exit student mode if diary window is external.
                openForms += "\nThe daily diary";
            }
            if (ForecasterChooseLesson.areInstancesOpen()) {
                //Can not exit student mode if diary window is external.
                openForms += "\nAny open forecaster lessons";
            }
            if (!openForms.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You can't exit as a studnnt with the following item(s)"
                        + " open:" + openForms + "\nPlease close these items and"
                        + " try again.", "Please Close Items",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            TimedLoader loader = new TimedLoader() {
                @Override
                protected String getLabelText() {
                    return "Previous Configuration";
                }

                @Override
                protected void doLoading() {
                    noteAndDiaryManager.closeAnyPresentExternalFrame();
                    appControl.getGeneralService().setOverrideUser(null);
                    isInViewMode = false;
                    //resrt managers
                    GeneralService gs = appControl.getGeneralService();
                    User user = gs.getUser();
                    gs.setCurrentWeatherCameraResource(lastCameraBeforeStudentMode);
                    gs.setCurrentWeatherMapLoopResource(lastMapLoopBeforeStudentMode);
                    gs.setCurrentWeatherStationResource(lastStationBeforeStudentMode);
                    configureUserManagers(user, lastDateBeforeStudentMode);
                    tpneNotes.setSelectedIndex(noteTabIndexBeforeStudentMode);
                    dispose();
                }
            };
            loader.execute();
            
            //show choice box
            viewModeWindow.setVisible(true);
        }
    }
    
    /**
     * Configures non-graphical managers.  Also checks for available disk space
     * when entering "View as Student" mode.
     * @param user the current user
     */
    private void configureUserManagers(User user, java.util.Date date) {
        if(configureProperties) {
            //This is not the first main window after the login screen, so
            //reconfigure now.
            Debug.println("Configuring local properties in the main application window.");
            PropertyManager.configureLocalProperties(user.getLoginId());
            //Test for space to write files if ettering "View as Student" mode.
            if (isInViewMode) {
                StorageSpaceTester.testApplicationHome();
            }
            //Must reset resource tree.
            ResourceTreeManager.initializeData();
        } else {
            //This is the first main window after the login screen, so don't
            //configure now, but set flag to configure later.
            configureProperties = true;
        }
        Debug.println("Configuring local file manager.");
        CommonLocalFileManager.configure(user.getLoginId());
        Debug.println("Configuring local bookmark properties.");
        PropertyManager.configureBookmarkProperties();
        Debug.println("Configuring diary manager");
        Resource initialDiaryResource = appControl.getGeneralService()
                .getCurrentWeatherCameraResource();
        
        //Initial diary camera cannot be null.
        if (initialDiaryResource == null) {
            //Make sure there is always a diaty resource.
            Vector<Resource> cameraResources = appControl.getGeneralService()
                    .getWeatherCameraResources();
            initialDiaryResource = cameraResources.get(0);
            
            //Look for default.
            String defaultCamera = PropertyManager
                    .getDefaultProperty("DEFAULT_WEATHER_CAMERA");
            for (Resource cameraResource : cameraResources) {
                if (cameraResource.getName().equals(defaultCamera)) {
                    initialDiaryResource = cameraResource;
                }
            }
        }
        DiaryManager.configure(appControl, user, date, initialDiaryResource);
    }

    /**
     * Private method called when the form gains focus.
     */
    private void formFocusGained() {
        weatherStationManager.refreshStationList();
    }

    /**
     * The event the contact Administrator menu item was selected.
     *
     * @param evt The event that the contact administrator menu item was
     * clicked.
     */
    private void contactAdminItemActionPerformed(java.awt.event.ActionEvent evt) {
        new ContactAdminWindow(appControl);
    }

    /**
     * Shows the About window.
     *
     * @param evt The event that the About menu item is selected.
     */
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.about == null) {
            this.about = new AboutWindow(appControl);
        } else {
            this.about.setVisible(true);
        }
    }

    /**
     * Shows the User's Manual.
     *
     * @param evt The event that the View User's Manual menu item is clicked.
     */
    private void manualMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "
                    + "Documents" + File.separator + "Manual(users).pdf");
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "Could not open user's manual", ex);
        }
    }

    private void administratorMenuActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }  

    private void viewAsStudentAdminMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ViewAsStudentWindow vasWindow = new ViewAsStudentWindow(appControl, true, this);
        vasWindow.checkToShow();
    }

    private void manageDailyDiaryWebLinksMenuActionPerformed(java.awt.event.ActionEvent evt) {
        appControl.getAdministratorControlSystem().listDailyDairyWebLink(weblinksMenu);

    }

    private void manageWebLinksCategoriesMenuActionPerformed(java.awt.event.ActionEvent evt) {
        appControl.getAdministratorControlSystem().listWebLinkCategories();
        createLinks();
    }

    private void menuManageWebLinksActionPerformed(java.awt.event.ActionEvent evt) {
        appControl.getAdministratorControlSystem().listWebLinks(this.weblinksMenu);
        createLinks();
    }

    private void adminManageStudentEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {
        if(appControl.getGeneralService().getDBMSSystem().getCourseManager().obtainAllCourses().isEmpty()){
            JOptionPane.showMessageDialog(this, "There are no courses to manage.", 
                    "No Courses Available", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new ManageStudentEnrollment(appControl, null, true, true);
    }

    private void adminRemoveOldClassesActionPerformed(java.awt.event.ActionEvent evt) {
        new PurgeClassesWindow(appControl, true);
    }

    private void menuManageClassesActionPerformed(java.awt.event.ActionEvent evt) {
        new ManageClassesWindow(appControl, true);
    }

    private void purgeInactiveActionPerformed(java.awt.event.ActionEvent evt) {
        appControl.getAdministratorControlSystem().purgeStudents();
    }

    /**
     * Shows user list from administrator and instructor menus.
     * @param isAdmin True if listing should have administrative ability to see
     * and edit more than students and guests, false otherwise.
     */
    private void userListActionEvent(boolean isAdmin) {
        appControl.getAdministratorControlSystem().listUsers(isAdmin); 
        //Needed because deleting a user could affect visible class notes.
        noteAndDiaryManager.setClassNotes();
    }

    private void genericNoDataImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            new SpecifyNoDataImage(this.appControl);
        } catch (Exception e) {
            // TODO: handle ClassNotFoundException, InstantiationException, and IllegalAccessException
        }
    }

    private void SpecifyDefaultImageActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            new SpecifyDefaultImages(this.appControl);
        } catch (Exception e) {
            // @todo handle ClassNotFoundException, InstantiationException, and IllegalAccessException
        }
    }

    /**
     * Lists the weather resource list from client control. The resource list
     * will be displayed in a new window.
     *
     * @param evt The event that the List Resources menu item is selected.
     */
    private void listResourcesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            appControl.getAdministratorControlSystem().listWeatherResourceService();
        } catch (WeatherException ex) {
            WeatherLogger.log(Level.SEVERE, "Failed to open the resource list.", ex);
            ex.show();
        }
    }

    private void viewAsStudentInstructorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ViewAsStudentWindow vasWindow = new ViewAsStudentWindow(appControl, false, this);
        vasWindow.checkToShow();
    }

    private void addWeatherImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void webPagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new ViewSaveWebPages(appControl);
    }
    
    private void manageForecasterLessonActionPerformed(java.awt.event.ActionEvent evt) {
        User user = appControl.getGeneralService().getUser();
        if (appControl.getGeneralService().getDBMSSystem().getCourseManager().obtainAllCoursesTaughyByUser(user).isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must have a class to teach ."
                    + "before\nyou can manage forecasting lessons.",
                    "No Coursess Available", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new InstructorLessonManager(appControl, null);
    }

    private void forecastingGradebookMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if(appControl.getGeneralService().getUser().getUserType() == UserType.administrator 
                || appControl.getGeneralService().getUser().getUserType() == UserType.instructor)
            new GradebookWindow(appControl);
        else if(appControl.getGeneralService().getUser().getUserType() == UserType.student 
                || appControl.getGeneralService().getUser().getUserType() == UserType.guest)
            new GradebookStudentEntryWindow(appControl, appControl
                    .getGeneralService().getUser(), null, true);
    }
    
    private void manageInstructorFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new ManageInstructorFilesWindow(appControl);
        //Needed because class notes table may need to be updated.
        noteAndDiaryManager.setClassNotes();
    }

    private void manageGeneralPropertyMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        new ManageGeneralPropertyWindow(appControl);
    }
    
    private void manageGUIPropertyMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        new ManageGUIPropertyWindow(appControl);
    }
    
    private void manageWeatherStationTwoVariablePropertyMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        new ManageWeatherStationTwoVariablePropertyWindow(appControl);
    }
    
    private void manageWeatherStationPropertyMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        new ManageWeatherStationPropertyWindow(appControl);
    }
    
    private void manageWeatherStationNoSolarPropertyMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        new ManageWeatherStationNoSolarVariablePropertyWindow(appControl);
    }
    
    /**
     * Shows a window that allows the user to edit students and classes.
     *
     * @param evt The event that the Modify Classes menu item is selected.
     */
    private void manageStudentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        User user = appControl.getGeneralService().getUser();
        if(appControl.getGeneralService().getDBMSSystem().getCourseManager().obtainAllCoursesTaughyByUser(user).isEmpty()){
            JOptionPane.showMessageDialog(this, "You have no courses to manage.", 
                    "No Coursess Available", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new ManageStudentEnrollment(appControl, null, false, true);
    }

    private void menuPurgeOldClassesActionPerformed(java.awt.event.ActionEvent evt) {
        new PurgeClassesWindow(appControl, false);
    }

    private void menuManageInstructorClassesActionPerformed(java.awt.event.ActionEvent evt) {
        new ManageClassesWindow(appControl, false);
    }

    private void exportWeatherCameraMovieMenuItemAltActionPerformed(java.awt.event.ActionEvent evt) {
        exportWeatherCameraMovieMenuItemActionPerformed(evt);
    }

    /**
     * Opens a new browser window, which shows the latitude and longitude of the
     * current weather camera.
     *
     * @param evt The event that the Camera Location menu item is clicked.
     */
    private void cameraLocationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (appControl.getGeneralService().getCurrentWeatherCameraResource() == null) {
            JOptionPane.showMessageDialog(this, "Please Select One Camera.");
        } else {
            float lat = appControl.getGeneralService().getCurrentWeatherCameraResource().getLatitude();
            float lon = appControl.getGeneralService().getCurrentWeatherCameraResource().getLongitude();
            BarebonesBrowser.openURL("http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q="
                    + lat + "+" + lon + "&sspn=0.008209,0.013819&ie=UTF8&ll=" 
                    + lat + "," + lon + "&spn=0.008209,0.013819&z=16&iwloc=addr", this);
        }
    }

    private void instructionLessonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new InstructionLessons(appControl);
    }
    
    private void forecasterLessonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ArrayList<Course> courses;
        User user;
        DBMSCourseManager courseManager;
        courseManager = appControl.getDBMSSystem().getCourseManager();
        user = appControl.getGeneralService().getUser();

        if (user.getUserType().equals(UserType.instructor)
                || user.getUserType().equals(UserType.administrator)) {
            courses = new ArrayList<>(courseManager.obtainAllCoursesTaughyByUser(user));
            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You must be teaching a course in order\n"
                        + "to submit a forecasting lesson.",
                        "No Courses Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            courses = new ArrayList<>(courseManager.obtainCoursesByStudent(user));
            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You are currently not enrolled in any courses.",
                        "Not Enrolled", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        new ForecasterChooseLesson(appControl, courses);
    }
    
    private void defaultSettingMenuItenActionPerformed(java.awt.event.ActionEvent evt) {
        GeneralService gs = appControl.getGeneralService();
        User user = gs.getUser();
        if (ResourceVisibleTester.canUserSeeAllTypes(user, gs)) {
            new SettingsWindow(appControl);
        } else {
            String message =
                    "Before you can use this feature, you must have access to at least one\n"
                    + "weather camera, at least one weather map loop, and at least one\n"
                    + "weather station.";
            JOptionPane.showMessageDialog(this, message,
                    "Not Enough Resources Available", JOptionPane.INFORMATION_MESSAGE);
        }
    }
            
    private void manageInstructorLessonsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new ManageLessons(appControl);
    }

    private void manageBookmarkCategoriesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new ManageBookmarkCategoryDialog(appControl, true);
    }

    private void listLocalBookmarkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new OpenManageBookmarkDialog(appControl, getWeatherStationManager(), 
                getMovieController());
    }

    private void createBookmarkMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {
        if (userType == UserType.student || userType == UserType.guest) {
            new BookmarkAddEditWindow(appControl,
                weatherStationManager, movieController, false, true, true);
        } else {
            new BookmarkAddEditWindow(appControl, 
                weatherStationManager, movieController, false, false, true);
        }
    }

    private void searchLocalBookmarksEventsUnderBookmarksEventsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new SearchBookmarkDialog(appControl, movieController, true, true);
    }

    private void searchDatabaseBookmarksUnderBookmarksMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new SearchBookmarkDialog(appControl, movieController, false, true);
    }

    private void openLocalBookmarkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        new OpenManageBookmarkDialog(appControl,
                BookmarkDuration.instance, movieController, false);
    }

    /**
     * TODO Check if a method should go here or the comment should be deleted
     * Displays the Edit Class window.
     *
     * @param evt The event that the Modify Classes menu item is selected.
     */
    /**
     * Stops any movie currently running and opens the Change Password window
     * for the current user.
     *
     * @param evt The event that the Change Password menu item is clicked.
     */
    private void changePasswordMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        this.stopMovies();
        GeneralService service = appControl.getGeneralService();
        String username = service.getUser().getLoginId();
        new ChangePassword(service, username, false);
    }
    
    /**
     * Shows the advanced settings window.
     * @param evt The event that the Advanced Settings menu item is clicked.
     */
    private void advancedSettingsMenuItemActionperformed(java.awt.event.ActionEvent evt) {
        new AdvancedSettingsWindow(appControl);
    }

    /**
     * Shows the local preferences.
     *
     * @param evt The event that the Preferences menu item is clicked.
     */
    private void mnuitmPreferencesActionPerformed(java.awt.event.ActionEvent evt) {
        new LocalDirectoryWindow(false);
    }

    private void browseWorkingDirectoryItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Open an explorer window where the data is saved
            // (Windows specific).
            //Drive letter:\\ will never be valid for a local drive.
            String dataDirectory = CommonLocalFileManager.getDataDirectory().
                    replace(":" + File.separator + File.separator, ":" + File.separator);
            Runtime.getRuntime().exec("explorer " + dataDirectory);
        } catch (IOException ex) {
            WeatherLogger.log(Level.FINE, "Could not open Browse Data Directory Window.", ex);
        }
    }

    private void searchWeatherStationDataItemActionPerformed(java.awt.event.ActionEvent evt) {
        new UnderDevelopmentDialog(appControl);
    }

    private void searchObservationDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        GeneralService gs = appControl.getGeneralService();
        User user = gs.getUser();
        if (ResourceVisibleTester.canUserSeeAllTypes(user, gs)) {
            new StartSearchWindow(appControl, ResourceTimeManager.getResourceRange());
        } else {
            String message =
                    "Before you can use this feature, you must have access to at least one\n"
                    + "weather camera, at least one weather map loop, and at least one\n"
                    + "weather station.";
            JOptionPane.showMessageDialog(this, message, 
                    "Not Enough Resources Available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
       closeWindow();
    }

    /**
     * Prints the current weather station data.
     *
     * @param evt The event that the Print Weather Station Data menu item is
     * clicked.
     */
    private void printWeatherStationDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        BufferedImage image = weatherStationManager.getImageFromPlotPanel();
        Resource resource = weatherStationManager.getResource();
        Date time = weatherStationManager.getStartOfPlottedRange();
        SnapShotViewer snapshot = new SnapShotViewer(image, time, resource, 
                ".png");
        snapshot.print();
    }

    /**
     * Prints the current weather map view.
     *
     * @param evt The event that the Print Weather Map Image menu item is
     * clicked.
     */
    private void printWeatherRadarSiteImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        BufferedImage pict = siteManager.getPictFromMovie();
        int resourceNumber = siteManager.getCurrentResourceNumber();
        Resource resource = appControl.getDBMSSystem().getResourceManager().getWeatherResourceByNumber(resourceNumber);
        Date time = movieController.getCurrentAbsoluteTime();
        SnapShotViewer snapshot = new SnapShotViewer(pict, time, resource, 
                ".jpeg");
        snapshot.print();
    }

    /**
     * Prints the current camera view.
     *
     * @param evt The event that the Print Weather Camera Image menu item is
     * selected.
     */
    private void printWeatherCameraImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        BufferedImage pict = cameraManager.getPictFromMovie();
        int resourceNumber = cameraManager.getCurrentResourceNumber();
        Resource resource = appControl.getDBMSSystem().getResourceManager().getWeatherResourceByNumber(resourceNumber);
        Date time = movieController.getCurrentAbsoluteTime();
        SnapShotViewer snapshot = new SnapShotViewer(pict, time, resource, 
                ".jpeg");
        snapshot.print();
    }

    /**
     * Allows a user to export their daily diary.
     *
     * @param evt The event that the Export Daily Diary menu item is selected.
     */
    private void exportDailyDiaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        noteAndDiaryManager.exportDiary();
    }

    /**
     * Allows a user to export a weather station data plot image.
     *
     * @param evt The event that the Export Weather Station Data menu item is
     * clicked.
     */
    private void exportWeatherStationDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (weatherStationManager.isSetToNone()) {
            JOptionPane.showMessageDialog(this, "There is no weather station currently selected.");
            return;
        }
        BufferedImage image = weatherStationManager.getImageFromPlotPanel();
        Resource resource = weatherStationManager.getResource();
        Date time = weatherStationManager.getStartOfPlottedRange();
        SnapShotViewer snapshot = new SnapShotViewer(image, time, resource, 
                ".png");
        snapshot.preview();
    }
    
    private ArrayList<String> getFileNames(WeatherResourceType resourceType) {
        Resource currentResource;
        if (resourceType == WeatherResourceType.WeatherCamera) {
            currentResource = appControl.getGeneralService().getCurrentWeatherCameraResource();
            if (currentResource == null) {
                JOptionPane.showMessageDialog(this, "There is no weather camera movie currently selected.");
                return null;
            }
        } else {
            currentResource = appControl.getGeneralService().getCurrentWeatherMapLoopResource();
            if (currentResource == null) {
                JOptionPane.showMessageDialog(this, "There is no weather map loop movie currently selected.");
                return null;
            }
        }
        
        //Time endpoints and return variable are below.
        Calendar startTime = new GregorianCalendar();
        startTime.setTime(ResourceTimeManager.getResourceRange().getStartTime());
        Calendar endTime = new GregorianCalendar();
        endTime.setTime(ResourceTimeManager.getResourceRange().getStopTime());
        ArrayList<String> filenames = new ArrayList<>();
        
        //Process through names.
        while(startTime.getTime().getTime() < endTime.getTime().getTime()){
            //Find end of current segment as Calendar.
            Calendar segEndTime = (Calendar)startTime.clone();
            segEndTime.add(Calendar.HOUR, 1);
            
            //Make calendar times strings.
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy '-' HH.mm.ss z");
            df.setTimeZone(currentResource.getTimeZone().getTimeZone());
            //change GMT to UTC and hide :'s with _'s
            String startStr = df.format(startTime.getTime())
                    .replaceAll("GMT", "UTC").replaceAll(":", "_");
            String stopStr = df.format(segEndTime.getTime())
                    .replaceAll("GMT", "UTC").replaceAll(":", "_");
            
            //Make file name.
            String fileName = currentResource.getName() + " - " + startStr 
                    + " to " + stopStr;
            Debug.println("File name: #" + fileName + "#");
            
            //Add namw to ArrayList.
            filenames.add(fileName);
            
            //Prepare for next loop iteration.
            startTime.add(Calendar.HOUR, 1);
        }
        
        //Return list.
        return filenames;
    }

    /**
     * Allows the user to export a Weather Web site movie.
     *
     * @param evt The event that the Export Weather Web site Movie menu item is
     * clicked.
     */
    private void exportWeatherMapLoopMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ArrayList<String> filenames = getFileNames(WeatherResourceType.WeatherMapLoop);
        if(filenames == null){
            //Work is already done.
            return;
        }
        try {
            movieController.saveMovie(1, filenames);
        } catch (WeatherException ex) {
            String msg = " (Could not save Movie to file.)";
            WeatherLogger.log(Level.SEVERE, msg, ex);
            new WeatherException(msg).show();
        }
    }

    /**
     * Allows the user to export a camera movie. The default file name shows the
     * date and time of the movie.
     *
     * @param evt The event that the Export Camera Movie menu item is clicked.
     */
    private void exportWeatherCameraMovieMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ArrayList<String> filenames = getFileNames(WeatherResourceType.WeatherCamera);
        if(filenames == null){
            //Work is already done.
            return;
        }
        try {
            movieController.saveMovie(0, filenames);
        } catch (WeatherException ex) {
            String msg = " (Could not save Movie to file.)";
            WeatherLogger.log(Level.SEVERE, msg, ex);
            new WeatherException(msg).show();
        }
    }
    /**
     * Opens image from picture directory
     * @param fileExtension The default extension for the file manager.
     */
    private void openDirectory(String fileExtension){
        File file = ClientSideLocalFileManager
                .loadSpecifiedFile(CommonLocalFileManager.getPictureDirectory(), 
                        fileExtension, this);
        BufferedImage image;
        if (file != null) {
            try {
                image = (BufferedImage) (ImageIO.read(file));
            } catch (IOException ex) {
                String msg = "Could not open this file";
                WeatherLogger.log(Level.SEVERE, msg, ex);
                new WeatherException(msg).show();
                return;
            }
            //Get parameters for PreviewImage
            String absolutePath = file.getAbsolutePath();
            int nameBegin = absolutePath.lastIndexOf("\\") + 1;
            int extensionBegin = absolutePath.lastIndexOf(".");
            String pictureName = absolutePath.substring(nameBegin, extensionBegin);
            String extension = absolutePath.substring(extensionBegin);
            PreviewImage previewImage = new PreviewImage(image, pictureName,
                    extension, null, null);
            previewImage.showFrom(true);
        }
    }

    private void openSnapshotImageItemActionPerformed(java.awt.event.ActionEvent evt) {
        openDirectory(".jpeg");
    }

    /**
     * Opens a weather station data plot image.
     *
     * @param evt The event that the Specify Weather Station menu item is
     * clicked.
     */
    private void openWeatherStationDataActionPerformed(java.awt.event.ActionEvent evt) {
        openDirectory(".png");
    }

    /**
     * Allows a user to open a weather Web site movie. The movie is opened in an
     * external window.
     *
     * @param evt The event that the Open Weather Web site Movie menu item is
     * clicked.
     */
    private void openWeatherMapLoopMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        File defaultPath = new File(CommonLocalFileManager.getMapLoopDirectory());
        if (!defaultPath.exists()) {
            if (!defaultPath.mkdirs()) {
                JOptionPane.showMessageDialog(this, "Could not find"
                        + " a Weather Map Loop directory at " + defaultPath);
            }
        }
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                defaultPath, null, "Open Weather Camera Movie", 
                new VideoFilter(), null, this);

        if (file == null) {
            JOptionPane.showMessageDialog(this, "No file was selected.", 
                    "No File Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ExternalWindow.displayExternalLocalVideoWindow(file.getAbsolutePath(), "Now Playing: " +
                file.getName(), appControl, WeatherResourceType.WeatherMapLoop, this);
    }

    /**
     * Opens a window that allows the user to select a camera movie to view. The
     * movie is opened in an external window.
     *
     * TODO: need to fix cancel button so it doesn't do a log. Added a
     * JOptionPane but the window still closes.
     *
     * @param evt The event that the Open Weather Camera Movie menu item is
     * clicked.
     */
    private void openWeatherCameraMovieMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        File defaultPath = new File(CommonLocalFileManager.getCameraMovieDirectory());
        if (!defaultPath.exists()) {
            if (!defaultPath.mkdirs()) {
                JOptionPane.showMessageDialog(this, "Could not find"
                        + " a Weather Camera Movie directory at " + defaultPath);
            }
        }
        File file = WeatherFileChooser.openFileChooser(WeatherFileChooser.OPEN,
                defaultPath, null, "Open Weather Camera Movie", 
                new VideoFilter(), null, this);

        if (file == null) {
            JOptionPane.showMessageDialog(this, "No file was selected.",
                    "No File Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ExternalWindow.displayExternalLocalVideoWindow(file.getAbsolutePath(), "Now Playing: "
                + file.getName(), appControl, WeatherResourceType.WeatherCamera, this);
    }

    /**
     * The below comments are remnants of the Form Editor usage.
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
