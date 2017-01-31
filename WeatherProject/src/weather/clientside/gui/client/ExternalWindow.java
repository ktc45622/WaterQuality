package weather.clientside.gui.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import weather.ApplicationControlSystem;
import weather.clientside.manager.*;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.data.resource.WeatherResourceType;
import weather.common.gui.component.BUJFrame;
import weather.common.utilities.Debug;
import weather.common.utilities.ScreenInteractionUtility;

/**
 * This is a GUI Utility class that constructs and displays the various external
 * windows that are needed by the rest of the application.  This is accomplished
 * through a series of static methods that construct new ExternalWindows passing
 * in the appropriate parameters to build the necessary ExternalWindow.
 *
 * @author Joe Sharp
 * @author Chris Mertens (2009)
 * @author Joe Van Lente (2010)
 *
 * @version Spring 2010
 */
public class ExternalWindow {
    private static final String CAM_TITLE = "Weather Viewer - Weather Camera";
    private static final String SITE_TITLE = "Weather Viewer - Weather Maps";
    private static final String STATION_TITLE = "Weather Viewer - Weather Station";
    private Dimension currentDim;
    private BUJFrame mainFrame = new BUJFrame();
    private JPanel displayPanel;
    private JPanel controlPanel;
    private WeatherMoviePanelManager movieManager;
    private WeatherStationPanelManager stationManager;
    private MovieController controller;
    private boolean isResizeTriggered = false;
    private MainApplicationWindow mainWin;
    
    /**
     * This ArrayList and function are used to bring all of the JFrames of the 
     * currently active instances of this class to the front
     */
    private static ArrayList<JFrame> currentFrames = new ArrayList<>();
    
    private static void bringAllToFront(){
        for(JFrame frame : currentFrames){
            frame.toFront();
        }
    }
    
    //Runnable object for invokeLater
    Runnable redrawingRunnable = new Runnable(){

        @Override
        public void run() {
            mainFrame.getContentPane().removeAll(); //Clear old controls.
            sizeFrame();  //Place new controls.
            if(!movieManager.isSetToNone()) {
                movieManager.forcePicture();    //Make movie show.
            } else {
                movieManager.forceNonePanel();  //Make none panel show.
            }
            isResizeTriggered = false; //Clear for next resize.
            Debug.println("Resize done.");
        }
        
    };
    
    /**
     * This method sizes the panels of the private <code>JFrame</code> so that, 
     * to the best extant possible, the panels fix within the dimensions of the
     * frame.  It should not be called if the instance is a data plot 
     * (WeatherStation).  
     * 
     * Note: The function will set the <code>currentDim</code> variable to the 
     * current size of the <code>JFrame</code>.
     */
    private void sizeFrame() { 
        currentDim = mainFrame.getSize();
        //Compute available space.
        int gapSize = 12;
        //height has extra gap between panels
        int availableWidth = currentDim.width - mainFrame.getInsets().left 
                - mainFrame.getInsets().right - 2 * gapSize;
        int availableHeight = currentDim.height - mainFrame.getInsets().top
                - mainFrame.getInsets().bottom - 3 * gapSize;
        //specify and compute panel heights.
        int controllerHeight = 110;
        int cameraPanelHeight = availableHeight - controllerHeight;
        //Get panels.
        displayPanel = movieManager.getPanel(availableWidth, cameraPanelHeight);
        controlPanel = controller.getControlPanel(availableWidth, controllerHeight);
        //Add panels.
        mainFrame.getContentPane().add(displayPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(gapSize, gapSize,
                availableWidth, cameraPanelHeight));
        mainFrame.getContentPane().add(controlPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(gapSize,
                cameraPanelHeight + 2 * gapSize,
                availableWidth, controllerHeight));
        
        mainFrame.setSize(currentDim);
        mainFrame.setPreferredSize(currentDim);
        mainFrame.setMaximumSize(ScreenInteractionUtility
                .getCurrentScreenResolution());
        mainFrame.setMinimumSize(new Dimension(400, 300));
        mainFrame.pack();
    }
    
    /**
     * Scales the given dimension by the given doubles.
     * @param input The given dimension.
     * @param xScale The scale in the x direction.
     * @param yScale The scale in the y direction.
     * @return The scaled dimension.
     */
    private Dimension scaleByDimension(Dimension input, double xScale, double yScale) {
        return new Dimension((int)(xScale * input.width),
                (int)(yScale * input.height));
        
    }

    /**
     * Constructor for the ExternalWindow for either WeatherSite panels or
     * WeatherCamera panels.
     * @param movieManager The MoviePanelManager for the movie to be played.
     * @param controller The MovieController for the given movieManager.
     * @param isWeatherCamera True if the given manager is for a weather camera,
     * false otherwise.
     * @param mainWin The application's main window. 
     */
    private ExternalWindow(final WeatherMoviePanelManager movieManager, 
            final MovieController cont, boolean isWeatherCamera,
            final MainApplicationWindow mainWin) {
        mainFrame.pack(); //Needed for Insets.  They are 0 before.
        if (isWeatherCamera) {
            mainFrame.setTitle(CAM_TITLE);
        }
        else {
            mainFrame.setTitle(SITE_TITLE);
        }
        
        this.controller = cont;
        this.mainWin = mainWin;
        this.movieManager = movieManager;
        mainFrame.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //Get initial dimension.
        currentDim = scaleByDimension(ScreenInteractionUtility
                .getCurrentScreenResolution(), .5, .5);
        
        //Make sure initial width is wide enough to show all controls
        if(currentDim.width < 625) {
            currentDim.width = 625;
        }
        
        //Set initial size.
        mainFrame.setSize(currentDim);
        mainFrame.setPreferredSize(currentDim);
        mainFrame.setMaximumSize(ScreenInteractionUtility
                .getCurrentScreenResolution());
        mainFrame.setMinimumSize(new Dimension(400, 300));
        mainFrame.pack();

        sizeFrame();  //Initially set controls
        
        mainFrame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we){
                controller.cleanup();
                currentFrames.remove(mainFrame);
                bringAllToFront();
            }
        });
        
        mainFrame.addWindowFocusListener(new WindowAdapter(){
            @Override
            public void windowGainedFocus(WindowEvent we){
                mainWin.toBack();
            }
        });

        controller.checkToEnableButtons();
        
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                //Stop here if resize already triggered.
                if(isResizeTriggered){
                    Debug.println("Resize already set.");
                    return;
                }
                
                controller.stopMovies();    //Can't resize with movie playing.
                
                //Add resize code to queue.
                Debug.println("Adding to queue.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ExternalWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                EventQueue.invokeLater(redrawingRunnable);
                
                //Mark form as resized.
                isResizeTriggered = true;
                
                Debug.println("Resize set.");
            }
        });
        currentFrames.add(mainFrame);
    }
    
    /**
     * ExternalWindow constructor for constructing a new ExternalWindow
     * containing a WeatherStation.
     * @param stationManager The station manager for this window.
     * @param mainWin The application's main window. 
     */
    private ExternalWindow(WeatherStationPanelManager stationManager, 
            final MainApplicationWindow mainWin) {
        this.mainWin = mainWin;
        this.stationManager = stationManager;
        mainFrame.setTitle(STATION_TITLE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //Get initial dimension.
        currentDim = scaleByDimension(ScreenInteractionUtility
                .getCurrentScreenResolution(), .5, .5);
        mainFrame.setPreferredSize(currentDim);
        mainFrame.setMaximumSize(ScreenInteractionUtility
                .getCurrentScreenResolution());
        mainFrame.setMinimumSize(new Dimension(400, 300));
        mainFrame.pack();

        this.displayPanel = this.stationManager.getWeatherStationPanel();
        mainFrame.add(displayPanel, BorderLayout.CENTER);
        stationManager.refreshPlot();
        
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                currentFrames.remove(mainFrame);
                bringAllToFront();
            }
        });
        
        mainFrame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent we) {
                mainWin.toBack();
            }
        });
        
        currentFrames.add(mainFrame);
    }
    
    /**
     * Sets the ExternalWindow to visible and places it on the screen.
     */
    private void setVisibleAndPosition() {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        mainFrame.postInitialize(true);
                        mainWin.toBack();
                        mainFrame.toFront();
                    }
                });
    }
   
    /**
     * Sets the title of the ExternalWindow.
     * @param title The title.
     */
    private void setTile(String title){
        mainFrame.setTitle(title);
    }
    
    /**
     * Initializes and displays a new ExternalWindow containing a video stored
     * on the local computer given the file path to that video.
     *
     * @param filePath The file path to the video to be shown.
     * @param title The title of the window.
     * @param appControl The appControl object for the running program.
     * @param resourceType The type of resource the window will hold.
     * @param mainWin The application's main window.
     */
    public static void displayExternalLocalVideoWindow(String filePath, String title,
            ApplicationControlSystem appControl, WeatherResourceType resourceType, 
            MainApplicationWindow mainWin) {
        MovieController controller = new MovieController(appControl, false);
        WeatherMoviePanelManager camManager
                = new WeatherMoviePanelManager(controller,
                        appControl, filePath, mainWin, resourceType);

        ExternalWindow externalCameraWindow
                = new ExternalWindow(camManager, controller, true,
                        mainWin);
        camManager.setToExternal(externalCameraWindow.mainFrame);
        
        controller.registerMoviePanelManager(camManager);
        externalCameraWindow.setTile(title);
        externalCameraWindow.setVisibleAndPosition();
        controller.setReady();
    }
    
    /**
     * Initializes and displays a new ExternalWindow containing a
     * WeatherCameraPanel that displays the given initial resource, and
     * resource range, and a MovieController that controls the given movie.
     *
     * @param initialResource The initial resource to display.
     * @param initialRange The initial resource range to display.
     * @param appControl The appControl object for the running program.
     * @param mainWin The application's main window.
     * @param initialSliderPosition The point, measured in elapsed seconds, at
     * which the video is to be initially set.
     */
    public static void displayExternalCameraWindow(Resource initialResource,
            ResourceRange initialRange, ApplicationControlSystem appControl,
            MainApplicationWindow mainWin, final int initialSliderPosition) {
        final MovieController controller = new MovieController(appControl, true);
        WeatherMoviePanelManager camManager
                = new WeatherMoviePanelManager(controller, appControl,
                        initialResource, initialRange,
                        WeatherResourceType.WeatherCamera, mainWin);

        ExternalWindow externalCameraWindow = new ExternalWindow(camManager,
                controller, true, mainWin);
        camManager.setToExternal(externalCameraWindow.mainFrame);
        
        controller.registerMoviePanelManager(camManager);
        externalCameraWindow.setVisibleAndPosition();
        controller.setReady();
        
        //Make sure slider positioning is done ather picture is forced.
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        controller.setToSecondCount(initialSliderPosition);
                    }
                });
    }

    /**
     * Initializes and displays a new ExternalWindow containing a
     * WeatherSitePanel that displays the given initial resource, and
     * resource range and a MovieController that will control the movie.
     *
     * @param initialResource The initial resource to display.
     * @param initialRange The initial resource range to display.
     * @param appControl The appControl object for the running program.
     * @param mainWin The application's main window.
     * @param initialSliderPosition The point, measured in elapsed seconds,
     * at which the video is to be initially set.
     */
    public static void displayExternalSiteWindow(Resource initialResource,
            ResourceRange initialRange, ApplicationControlSystem appControl, 
            MainApplicationWindow mainWin, final int initialSliderPosition) {

        final MovieController controller = new MovieController(appControl, true);
        WeatherMoviePanelManager siteManager
                = new WeatherMoviePanelManager(controller, appControl,
                        initialResource, initialRange,
                        WeatherResourceType.WeatherMapLoop, mainWin);

        ExternalWindow externalCameraWindow = new ExternalWindow(siteManager,
                controller, false, mainWin);
        siteManager.setToExternal(externalCameraWindow.mainFrame);
        
        controller.registerMoviePanelManager(siteManager);
        externalCameraWindow.setVisibleAndPosition();
        controller.setReady();
        
        //Make sure slider positioning is done ather picture is forced.
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        controller.setToSecondCount(initialSliderPosition);
                    }
                });
    }

    /**
     * Initializes and displays a new ExternalWindow containing a
     * WeatherStationPanel that displays the given initial resource, and
     * resource range.
     *
     * @param initialResource The initial resource to display.
     * @param initialRange The initial resource range to display.
     * @param appControl The appControl object for the running program
     * @param mainWin The application's main window..
     */
    public static void displayExternalStationWindow(Resource initialResource,
            ResourceRange initialRange, ApplicationControlSystem appControl, 
            MainApplicationWindow mainWin) {
        WeatherStationPanelManager stationManager 
                = new WeatherStationPanelManager(appControl, initialRange, 
                        initialResource, mainWin);
        
        //Must get the main window's WeatherStationPanelManager to retrieve its
        //settings.
        WeatherStationPanelManager sourceManager = mainWin
                .getWeatherStationManager();
        
        //Copy settings.
        stationManager.setDaysShownForExternalManger(sourceManager
                .getDaysShown());
        stationManager.setGraphFittedState(sourceManager.isGraphFitted());
        stationManager.setGraphByRadioText(sourceManager
                .getSelectedRadioText());
        
        //Setup external window.
        ExternalWindow externalWindow = new ExternalWindow(stationManager, 
                mainWin);
        stationManager.setToExternal();
        externalWindow.setVisibleAndPosition();
    }
}
