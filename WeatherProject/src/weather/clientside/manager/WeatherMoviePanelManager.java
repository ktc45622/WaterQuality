package weather.clientside.manager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.*;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import weather.ApplicationControlSystem;
import weather.GeneralService;
import weather.clientside.gui.client.ExternalWindow;
import weather.clientside.gui.client.MainApplicationWindow;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.PopupListener;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.clientside.imageprocessing.JPanelPlayerVLCJ;
import weather.clientside.utilities.ResourceTreeManager;
import weather.clientside.utilities.SnapShotViewer;
import weather.clientside.utilities.StorageSpaceTester;
import weather.clientside.utilities.TimedLoader;
import weather.clientside.utilities.WeatherFileChooser;
import weather.common.data.resource.*;
import weather.common.gui.component.*;
import weather.common.utilities.*;

/**
 * This class is responsible for playing and stopping movies, loading the
 * correct movies to go with selected weather cameras, getting pictures from
 * movies, and saving movies.
 *
 * @author Joe Sharp
 * @author Joe Van Lente (2010)
 * @author Dustin Jones
 *
 * @version Spring 2010
 */
public class WeatherMoviePanelManager extends javax.swing.JPanel implements
        MoviePanelManager {

    private static final int VERTICAL_BUTTON_PANEL_OFFSET = 27;
    private static final int HORIZONTAL_BUFFER = 10;
    private final int COMBOBOX_MAX_ROW_COUNT = 25;
    private Resource resource;
    private ResourceRange resourceRange;
    private boolean listsInitialized;
    private boolean external = false; //Must be changed if external!
    private JPanelPlayerVLCJ videoPlayer;
    private ApplicationControlSystem appControl;
    private MainApplicationWindow mainWin;
    private MovieController movieController;
    private float movieRate;
    private Dimension panelDimension;
    private JPopupMenu rightClickMenu;
    private WeatherResourceType resourceType;
    private ItemListener comboBoxListener;
    private Vector<ResourceHolder> holder;
    
    //Path to local resource, null for other uses. 
    private String absolutePath = null;
    
    //The entire window if this object is part of an external window; ignored
    //otherwise.
    private BUJFrame externalWindow = null; 
    
    /**
     * This flag is for use by forceNonePanel().  That function forces the none
     * panel by changing the selection of the combo box.  When it does, we don't
     * want videos to load.  This flag is set to true to stop that.
     */
    private boolean forcingNone = false;
    
    //THESE TWO FUNCTIONS ARE FOR DEBUGING
    private void debugDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:mm;ss a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        if (date == null) {
            Debug.println(name + ": null");
        } else {
            Debug.println(name + ": " + simpleDateFormat.format(date));
        }
    }

    private void debugResourceRange(ResourceRange range) {
        debugDate(range.getStartTime(), "Range Start: ");
        debugDate(range.getStopTime(), "Range End: ");
    }
    
    /**
     * Get the whole window containing this object.
     * @return The whole window containing this object.
     */
    private Component getWholeWindow() {
        if (external) {
            return externalWindow;
        } else {
            return mainWin;
        }
    }
    
    @Override
    public void setToNone() {
        itemComboBox.setSelectedIndex(itemComboBox.getItemCount() - 1);  //"None"
    }
    
    /**
     * Tests if panel is showing none.
     * @return True if panel shows none, false otherwise
     */
    @Override
    public boolean isSetToNone(){
        return itemComboBox.getSelectedIndex() == itemComboBox.getItemCount() - 1;  //"None"
    }

    @Override
    public boolean isCameraManager() {
        return resourceType == WeatherResourceType.WeatherCamera;
    }
    
    /**
     * Forces the appearance of the panel that indicates no resource is selected.
     */
    public void forceNonePanel() {
        /**
         * We assume "None" is selected without the panel showing.  In order to
         * show the panel, must select another option and then reselect "Nome."
         * (Also see the note above the declaration of forcingNone.)
         */
        forcingNone = true;
        itemComboBox.setSelectedIndex(0);
        forcingNone = false;
        setToNone();
    }
    
    /**
     * Forces the picture to be shown by changing the drop down list, which is 
     * assumed to not be set to "None."
     */
    public void forcePicture() {
        /**
         * We assume a video source is selected without the video showing.  
         * In order to show the video, must select another option and then 
         * reselect the original option.  For ease of coding, the other option
         * that is temporarily selected is "Nome."
         */
        int index = itemComboBox.getSelectedIndex();
        setToNone();
        itemComboBox.setSelectedIndex(index);
    }

    /**
     * Specifies the signature of the method to save the movie.
     *
     * @param filenames The list of file names to hold the movie segments
     * @throws WeatherException if save is not successful.
     */
    @Override
    public void saveMovie(ArrayList<String> filenames) throws WeatherException {
        //Make sure path exists.
        String defaultPath = resourceType == WeatherResourceType.WeatherCamera
                ? CommonLocalFileManager.getCameraMovieDirectory() 
                : CommonLocalFileManager.getMapLoopDirectory();
        File targetFolder = new File(defaultPath);
        if (!targetFolder.exists()) {
            if (!targetFolder.mkdirs()) {
                throw new WeatherException();
            }
        }
        
        //Let user choose directory.
        String chosenDirectory = WeatherFileChooser
                .openDirectoryChooser(targetFolder, "Save Videos...", null,
                        getWholeWindow());
        if (chosenDirectory != null) {

            //VLCJ implementation returns a linked list of all the movies that 
            //represent the current movie.
            LinkedList<String> tempList = videoPlayer.getCurrentMoviePaths();
            int count = 0;
            
            //Track results.
            int goodSaves = 0;
            int badSaves = 0;
            
            //Attempt saves.
            for (String currentMoviePath : tempList) {
                String extension = currentMoviePath
                        .substring(currentMoviePath.lastIndexOf("."));
                File source = new File(currentMoviePath);
                File target = new File(chosenDirectory, filenames.get(count++)
                        + extension);

                target.mkdirs();

                try {
                    if (!target.exists()) {
                        target.createNewFile();
                    }
                    Files.copy(source.toPath(), target.toPath(), 
                            REPLACE_EXISTING);
                    goodSaves++;
                } catch (IOException | HeadlessException ex) {
                    badSaves++;
                }
            }
            
            //Show results.
            JOptionPane.showMessageDialog(getWholeWindow(), 
                    "Number of Successful Saves: " + goodSaves 
                    + "\nNumber of Failed Saves: " + badSaves,
                    "Video Save Result", JOptionPane.INFORMATION_MESSAGE);
            
            //Test for remaining space in application home, which has no effect
            //if the save was not there.
            StorageSpaceTester.testApplicationHome();
        }
    }

    @Override
    public TimeZone getTimeZone() {
        return ((ResourceListCellItem) itemComboBox
                .getSelectedItem()).getResourceTimeZone(appControl
                .getGeneralService());
    }

    /**
     * ResourceHolder is a private class held in this panel that holds the
     * resources and names of them, as they are initialized at startup. This is
     * used mainly as a way to prevent errors where during runtime, resource
     * names are edited which can cause an exception.
     */
    private class ResourceHolder {

        private String name;
        private Resource r;

        public ResourceHolder(Resource r) {
            name = r.getResourceName();
            this.r = r;
        }

        public String getName() {
            return name;
        }

        public Resource getResource() {
            return r;
        }
    }

    /**
     * Creates new form WeatherMoviePanelManager 
     * @param controller The controller for the movie being played. 
     * @param appControl The program's application control system.
     * @param resource The initial resource.
     * @param range The initial resource range.
     * @param resourceType The type of resource in the panel (WeatherCamera or 
     * WeatherMapLoop)
     * @param mainWin The program's main window.
     */
    public WeatherMoviePanelManager(MovieController controller,
            final ApplicationControlSystem appControl, Resource resource,
            ResourceRange range, WeatherResourceType resourceType, 
            MainApplicationWindow mainWin) {
        movieController = controller;
        this.resourceType  = resourceType;
        this.appControl = appControl;
        this.resource = resource;
        this.mainWin = mainWin;
        
        holder = new Vector<>();
        if (resourceType == WeatherResourceType.WeatherCamera) {
            for (Resource r : appControl.getGeneralService().getWeatherCameraResources()) {
                holder.add(new ResourceHolder(r));
            }
        } else {
            for (Resource r : appControl.getGeneralService().getWeatherMapLoopResources()) {
                holder.add(new ResourceHolder(r));
            }
        }

        movieRate = 1.0f; // Single precision floating-point number constant
        // This constant represents the rate of the movie
        // A rate of 1.0 represents normal playing speed
        comboBoxListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                //Only change on SELECTED
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    TimedLoader loader = new TimedLoader() {
                        @Override
                        protected String getLabelText() {
                            return "Weather Resource Video";
                        }

                        @Override
                        protected void doLoading() {
                            updateInformation();
                        }
                    };
                    loader.execute();
                }
            }

        };
        initComponents();
        if(resourceType == WeatherResourceType.WeatherMapLoop) {
            //Change tool tips and none label to map loop text.
            itemComboBox.setToolTipText("Select weather map/diagram resource selected.");
            externalWindowButton.setToolTipText("Display the Weather Map data in a separate window.");
            printButton.setToolTipText("Print the current frame displayed in the Weather Map window.");
            snapShotButton.setToolTipText("Save a snapshot of the current image displayed in the Weather Map window.");
            noMovieLabel.setText("No weather map/daiagram resource selected.");
        }
        itemComboBox.addItemListener(comboBoxListener);
        setResourceRange(range);
        movieRate = movieController.getCurrentRate();
        listsInitialized = false;
        initializeLists();
        nonePanel.setVisible(false);
        itemComboBox.setMaximumRowCount(COMBOBOX_MAX_ROW_COUNT);
        if (itemComboBox.getSelectedItem().toString().equals("None")) {
            setButtonsEnabled(false);
        }
    }
    
    /**
     * Creates a version of the panel for a local file viewer.
     * @param controller The controller for the movie being played. 
     * @param appControl The program's application control system.
     * @param filePath The path of the file to be shown.
     * @param mainWin The program's main window.
     * @param resourceType The type of resource in the panel (WeatherCamera or 
     * WeatherMapLoop)
     */
    public WeatherMoviePanelManager(MovieController controller,
            final ApplicationControlSystem appControl, String filePath,
            MainApplicationWindow mainWin, WeatherResourceType resourceType) {
        this(controller, appControl, null, null, resourceType, mainWin);
        absolutePath = filePath;
        itemComboBox.setVisible(false);
        
        //Get time zone for movie controller.
        
        //Assume defauli in case resource isn't found.
        TimeZone resourceTimeZone = TimeZone.getDefault();
        
        //Must find resource to get time zone.
        Resource fileResource = null;
        
        /**
         * Note on the end of the below range: The function used finds the end 
         * of the time range in the filePath string.  There are three characters
         * between this and the resource name, so subtracting three gives the 
         * end of the resource name.
         */
        String resourceName = filePath.substring(filePath.lastIndexOf("\\") + 1,
                findRangeStartInAbsolutePath() - 3);
        
        Debug.println("Resouce Name: #" + resourceName + "#");
        for (Resource aResource : appControl.getDBMSSystem()
                .getResourceManager().getResourceList()) {
            if (aResource.getName().equals(resourceName)) {
                fileResource = aResource;
                Debug.println("Resource found.");
                break;
            }
        }
        
        //Set time zone.
        if (fileResource != null) {
            resourceTimeZone = fileResource.getTimeZone().getTimeZone();
            Debug.println("Yiewer time zone : " + resourceTimeZone.getID());
        }
        

        //Set range of movie contoller

        //Isolate range from rest of string
        String rangeString;  //will he range as string
        int rangeStringStartIndex = findRangeStartInAbsolutePath();
        int rangeStringEndIndex = filePath.lastIndexOf(".");
        rangeString = filePath
                .substring(rangeStringStartIndex, rangeStringEndIndex);
        Debug.println("rangeString: #" + rangeString + "#");
                   
        //break into from and to strings
        StringTokenizer tokenizer = new StringTokenizer(rangeString);

        String fromString = tokenizer.nextToken("t");
        //remove trailing space and change UTC to GMT
        fromString = fromString.trim().replaceAll("UTC", "GMT")
                .replaceAll("_", ":");
        tokenizer.nextToken(" "); //discaed "to"
        String toString = tokenizer.nextToken("@"); //rest of string
        //remove leading space and change UTC to GMT and _'s to :'s
        toString = toString.trim().replaceAll("UTC", "GMT")
                .replaceAll("_", ":");
        
        Debug.println("fromString: #" + fromString + "#");
        Debug.println("toString: #" + toString + "#");
        
        //convert strings to times in mills
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy '-' HH.mm.ss z");
        long startTime;
        long endTime;
        try {
            startTime = df.parse(fromString).getTime();
            endTime = df.parse(toString).getTime();
        } catch (ParseException ex) {
            showErrorPane("Unable to parse dates.", "Unable to Parse");
            return;
        }
        
        //make controller resource range
        ResourceRange controllerRange = new ResourceRange(new Date(startTime),
                new Date(endTime));
        debugResourceRange(controllerRange);
        
        //update range and set time zone of controller
        movieController.setLocalViewerRangeAndTimeZone(controllerRange,
                resourceTimeZone);
    }

    /**
     * Initializes the resources that will be available in the combo box.
     */
    private void initializeLists() {
        //Set for local video.  Items needed for resizing, although box is hidden.
        if(resourceRange == null){
            itemComboBox.addItem(new ResourceListCellItem("Local Resource"));
            itemComboBox.addItem(new ResourceListCellItem());   //"None"
            itemComboBox.setSelectedIndex(0);
            listsInitialized = true;
            return;
        }
        
        if (resourceType == WeatherResourceType.WeatherCamera) {
            GUIComponentFactory.initCameraComboBox(itemComboBox, 
                    appControl.getGeneralService(), true);
        } else {
            GUIComponentFactory.initMapLoopComboBox(itemComboBox, 
                    appControl.getGeneralService(), true);
        }

        if (resource != null) {
            itemComboBox.setSelectedItem(new ResourceListCellItem(resource.getResourceName()));
        } else {
            itemComboBox.setSelectedItem(new ResourceListCellItem("None"));
        }

        listsInitialized = true;
    }
    
    /**
     * Configure this object for an external window.
     * 
     * @param externalWindow The external window to contain this object.
     */
    public void setToExternal(BUJFrame externalWindow) {
        this.externalWindow = externalWindow;
        external = true;
        externalWindowButton.setEnabled(false);
    }
    
    /**
     * This sets the movie vector stored in the videoPlayer in this
     * WeatherCameraPanelManager.
     *
     * @param movieVector The vector of movies to load into this videoPlayer.
     */
    private void setMovieVector(Vector<AVIInstance> movieVector) throws WeatherException {
        nonePanel.setVisible(false);
        if (videoPlayer != null) {
            moviePanel.remove(videoPlayer);
            videoPlayer.cleanup();
            videoPlayer = null;

        }
        if(absolutePath == null){
            videoPlayer = new JPanelPlayerVLCJ(movieVector);
        } else {
            videoPlayer = new JPanelPlayerVLCJ(absolutePath);
        }
        
        // Must re-register menu here because the panel was reinitialized.
        addRightClickMenu();
       

        if (panelDimension != null) {
            int movieWidth = panelDimension.width - HORIZONTAL_BUFFER;
            int movieHeight = panelDimension.height - VERTICAL_BUTTON_PANEL_OFFSET;
            videoPlayer.setMovieSize(movieWidth, movieHeight);
            moviePanel.add(videoPlayer, new AbsoluteConstraints(HORIZONTAL_BUFFER / 2,
                    VERTICAL_BUTTON_PANEL_OFFSET, movieWidth, movieHeight));
        }
        //Debug.println("Do videoPlayer Setup : Camera");
        moviePanel.validate();
    }

    /**
     * This method updates the camera movie after a new camera is selected from
     * the camera comboBox.
     */
    private void updateMovie() {
        if(absolutePath != null){
            try {
                setMovieVector(null);   //Sets vector to absolute path.
                return;
            } catch (WeatherException ex) {
                ex.show();
            }
        }
        if (resource != null && resourceRange != null) {
            try {  
                Vector<AVIInstance> movieVector = new Vector<>();
                              
                Vector<ResourceInstance> cameraInstances = ResourceTreeManager
                        .getResourceInstancesForRange(resource, resourceRange);
                for (ResourceInstance camInstance : cameraInstances) {
                        movieVector.add(((AVIInstance) camInstance));
                    } 
               
              //  Debug.println("MovieVector has size " + movieVector.size());
                if (movieVector.size() > 0) {
                    setMovieVector(movieVector);
                }
            } catch (WeatherException ex) {
                ex.show();
            }
        } else if (resource == null) {
            if (videoPlayer != null) {
                moviePanel.remove(videoPlayer);
            }
            nonePanel.repaint();

            nonePanel.setVisible(true);
        }
    }

    /**
     * Enable or disable buttons.
     *
     * @param setting True to enable, false to disable.
     */
    private void setButtonsEnabled(boolean setting) {
        snapShotButton.setEnabled(setting);
        printButton.setEnabled(setting);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cameraPanel = new javax.swing.JPanel();
        moviePanel = new javax.swing.JPanel();
        cameraSelectionPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        externalWindowButton = new javax.swing.JButton();
        snapShotButton = snapShotButton = new JButton()
        ;
        printButton = new javax.swing.JButton();
        itemComboBox = new javax.swing.JComboBox<ResourceListCellItem>();
        nonePanel = new javax.swing.JPanel();
        noMovieLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        cameraPanel.setLayout(new java.awt.BorderLayout());

        moviePanel.setPreferredSize(new java.awt.Dimension(630, 460));
        moviePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cameraSelectionPanel.setPreferredSize(new java.awt.Dimension(638, 27));
        cameraSelectionPanel.setLayout(new java.awt.GridLayout(1, 0));

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        externalWindowButton.setIcon(IconProperties.getExternalWindowIconImage());
        externalWindowButton.setToolTipText("Display the Weather Camera movie in a separate window.");
        externalWindowButton.setMaximumSize(new java.awt.Dimension(75, 25));
        externalWindowButton.setMinimumSize(new java.awt.Dimension(75, 25));
        externalWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                externalWindowButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(externalWindowButton);

        snapShotButton.setIcon(IconProperties.getCameraIconImage());
        snapShotButton.setToolTipText("Save a snapshot of the current image displayed in the Weather Camera window.");
        snapShotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapShotButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(snapShotButton);

        printButton.setIcon(IconProperties.getSnapshotPrintIconImage());
        printButton.setToolTipText("Print the current frame displayed in the Weather Camera window.");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(printButton);

        cameraSelectionPanel.add(buttonPanel);

        itemComboBox.setToolTipText("Select weather camera.");
        itemComboBox.setLightWeightPopupEnabled(false);
        cameraSelectionPanel.add(itemComboBox);

        moviePanel.add(cameraSelectionPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 661, 27));

        nonePanel.setPreferredSize(new java.awt.Dimension(500, 427));
        nonePanel.setLayout(new java.awt.BorderLayout());

        noMovieLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noMovieLabel.setText("No weather camera resource selected.");
        nonePanel.add(noMovieLabel, java.awt.BorderLayout.CENTER);

        moviePanel.add(nonePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 27, 661, 471));

        cameraPanel.add(moviePanel, java.awt.BorderLayout.CENTER);

        add(cameraPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Create a new external window to display a WeatherCameraPanelManager.
     */
    private void externalWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_externalWindowButtonActionPerformed
        if(resourceType == WeatherResourceType.WeatherCamera) {
            ExternalWindow.displayExternalCameraWindow(resource, resourceRange, 
                    appControl, mainWin, movieController.getSliderSecondsElapsed());
        } else {
            ExternalWindow.displayExternalSiteWindow(resource, resourceRange, 
                    appControl, mainWin, movieController.getSliderSecondsElapsed());
        }
}//GEN-LAST:event_externalWindowButtonActionPerformed

    /**
     * Updates the movies to be shown based on the combo box item selected.
     */
    private void updateInformation() { 
        if(forcingNone) {
            /**
             * The combo box was only changed by the program to force the "None"
             * panel to show.  We don't actually want to do anything.
             */
            return;
        }
        
        Resource weatherStationResource = null;
        GeneralService generalService = appControl.getGeneralService();

        if (listsInitialized) {
            int elapsedSeconds = movieController.getSliderSecondsElapsed(); 
            
            movieController.stopSliderThread();
            String name = itemComboBox.getSelectedItem().toString();
            
            //If the user selects None, the update is still triggered
            try {
                if (itemComboBox.getSelectedItem().toString().equals("None")) {
                    if (videoPlayer != null) {
                        moviePanel.remove(videoPlayer);
                        videoPlayer.cleanup();
                        videoPlayer = null;
                    }
                    setButtonsEnabled(false);
                    resource = null;
                    if (!external) {
                        // Since no movies were returned, set the current weather
                        // camera or map resource loaded into the main program to null.
                        if (resourceType == WeatherResourceType.WeatherCamera) {
                            appControl.getGeneralService().setCurrentWeatherCameraResource(null);
                            //There is also no station.
                            appControl.getGeneralService().setCurrentWeatherStationResource(null);
                            movieController.setWeatherStationResource(null);
                        } else {
                            appControl.getGeneralService().setCurrentWeatherMapLoopResource(null);
                        }
                    }
                    nonePanel.setVisible(true);
                    
                    //If panel is of movie type and not external, tell 
                    //controller to update note and diary settings.
                    checkForDiaryChange();
                    
                    //Set to local time if camera is "none."
                    //Finish setting movie controller.
                    if (resourceType == WeatherResourceType.WeatherCamera
                            || external) {
                        movieController.updateTimeZone();
                    }
                    
                    //Preserve slider position.
                    movieController.setToSecondCount(elapsedSeconds);
                    
                    //Check if controller button shoud be enabled or disabled.
                    movieController.checkToEnableButtons();
                    
                    return;
                }

            } catch (Exception ex) {
                System.err.println(ex.getMessage() + "\n" + ex.getStackTrace().toString());
            }
            
            //With resource, we fall to here.
            setButtonsEnabled(true);
            
            //Update resource and general service if not external.
            if (!external) {
                for (ResourceHolder rh : holder) {
                    if (rh.getName().equals(name)) {
                        resource = rh.getResource();
                        break;
                    }
                }

                if (resourceType == WeatherResourceType.WeatherCamera) {
                    //Update the data plot if it is linked to the controller.
                    if (movieController.isDataPlotLinked()) {
                        if (resource != null) {
                            weatherStationResource = generalService.getDBMSSystem().
                                getResourceRelationManager().getRelatedStationResource(resource);
                        }

                        if (weatherStationResource == null) {
                            Debug.println("Related weather resource is null");
                        } else {
                            Debug.println("Related weather resource is NOT null");
                        }

                        //Note that null is a legal value for weatherstationresource
                        movieController.setWeatherStationResource(weatherStationResource);
                    }

                    //Set the current camera in General Services.
                    generalService.setCurrentWeatherCameraResource(resource);
                } else {
                    //Set the current map in General Services.
                    generalService.setCurrentWeatherMapLoopResource(resource);
                }
            } else if (absolutePath == null) {
                // Still must set resource if external, but not local viewer.
                for (ResourceHolder rh : holder) {
                    if (rh.getName().equals(name)) {
                        resource = rh.getResource();
                        break;
                    }
                } 
            }

            Debug.println("Updating movie from combo box");
            updateMovie();
            try {
                movieController.resetSlider();
            } catch (WeatherException ex) {
                ex.show();
            }
            
            //If panel is of movie type and not external, tell 
            //controller to update note and diary settings.
            checkForDiaryChange();
            
            //Finish setting movie controller.
            if (resourceType == WeatherResourceType.WeatherCamera
                    || external) {
                movieController.updateTimeZone();
            }
            movieController.setReady();
            
            //Preserve slider position.
            movieController.setToSecondCount(elapsedSeconds);

            //Check if controller button shoud be enabled or disabled.
            movieController.checkToEnableButtons();            
        }//end if statement
    }
    
    
    /**
     * Checks if the movie controller should be notified to change the resource 
     * of the diary manager and, if so, requests the change.
     */
    public void checkForDiaryChange() {
        if (resourceType == WeatherResourceType.WeatherCamera
                && !external) {
            movieController.updateDiaryPanelResource(resource);
        }
    }

    private void snapShotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapShotButtonActionPerformed
        createSnapshot();
    }//GEN-LAST:event_snapShotButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        print();
    }//GEN-LAST:event_printButtonActionPerformed

    private void print() {
        //Get the current resource
        Resource thisResource = getResource();

        //Check if resource was not found.
        if (thisResource == null) {
            showErrorPane("There is no image to print.", "Print Not Possible");
            return;
        }

        long currnetTimeInMills = movieController.getCurrentDateAndTime().getTimeInMillis();
        SnapShotViewer snapshot = new SnapShotViewer(this.getPictFromMovie(), 
                new Date(currnetTimeInMills), thisResource, ".jpeg");
        snapshot.print();
    }

    /**
     * This will return a buffered image of the current frame displayed in the
     * videoPlayer.
     *
     * @return The buffered image of the current frame displayed in the
     * videoPlayer.
     */
    @Override
    public BufferedImage getPictFromMovie() {
        stop();
        return videoPlayer.getBufferedImage();

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel cameraPanel;
    private javax.swing.JPanel cameraSelectionPanel;
    private javax.swing.JButton externalWindowButton;
    private javax.swing.JComboBox<ResourceListCellItem> itemComboBox;
    private javax.swing.JPanel moviePanel;
    private javax.swing.JLabel noMovieLabel;
    private javax.swing.JPanel nonePanel;
    private javax.swing.JButton printButton;
    private javax.swing.JButton snapShotButton;
    // End of variables declaration//GEN-END:variables

    /**
     * Displays the camera panel.
     *
     * @param width The width of the panel.
     * @param height The height of the panel.
     * @return The weather camera panel.
     */
    @Override
    public JPanel getPanel(int width, int height) {
        //Clear designer and reset movie panel.
        panelDimension = new Dimension(width, height);
        moviePanel.remove(cameraSelectionPanel);
        moviePanel.remove(nonePanel);
        moviePanel.add(cameraSelectionPanel, new AbsoluteConstraints(0, 0, panelDimension.width,
                VERTICAL_BUTTON_PANEL_OFFSET));
        moviePanel.add(nonePanel, new AbsoluteConstraints(HORIZONTAL_BUFFER / 2,
                VERTICAL_BUTTON_PANEL_OFFSET, panelDimension.width - HORIZONTAL_BUFFER,
                panelDimension.height - VERTICAL_BUTTON_PANEL_OFFSET));
        
        //Set video for local movie player.
        if (absolutePath != null) {
            updateMovie();
        } 
        
        cameraPanel.setSize(panelDimension);
        return cameraPanel;
    }

    /**
     * Sets the current range of resources to be used in the manager. This
     * method also updates the current movie.
     *
     * @param range The new resource range object.
     */
    @Override
    public final void setResourceRange(ResourceRange range) {
        resourceRange = range;
        if (resource != null || absolutePath != null) {
            updateMovie();
        } else {
            nonePanel.setVisible(true);
        }
    }

    /*
     * This function stops the vector of movies from playing in the videoPlayer.
     */
    @Override
    public void stop() {
        //Debug.println("Stopping Camera video.");
        //NullPointerException occurs during Selective Start or No Data-Load
        //if videoPlayer is null.
        if (videoPlayer != null) {
            videoPlayer.stopMovie();
        }
    }

    @Override
    public void reset() {
        if (videoPlayer != null) {
            videoPlayer.reset();
        }
    }

    /*
     * This function will set the videoPlayer to play at the supplied rate.
     * @param rate the rate to set the videoPlayer to play at. @param isPlaying
     * true if the videoPlayer is currently in play, false if it is not.
     */
    @Override
    public void setMovieRate(float rate, boolean isPlaying) {
        this.movieRate = rate;
        if (isPlaying) {
            try {
                //NullPointerException occurs during Selective Start or No Data-Load
                //if videoPlayer is null.
                if (videoPlayer != null) {
                    videoPlayer.setMovieRate(rate);
                }
            } catch (WeatherException ex) {
                ex.show();
            }
        }
    }

    /**
     * This will set the size of the videoPlayer.
     *
     * @param width the width to set the videoPlayer to.
     * @param height The height to set the videoPlayer to.
     */
    @Override
    public void setMovieSize(int width, int height) {
        videoPlayer.setSize(width, height);
    }

    /*
     * This function will move the videoPlayer to the desired location in the
     * vector of movies. @param progress The position to set the videoPlayer to
     * (in seconds of recorded time).
     */
    @Override
    public void setMovieProgress(int progress) {
        //NullPointerException occurs during Selective Start or No Data-Load
        //if videoPlayer is null.
        if (videoPlayer != null) {
            videoPlayer.setTimeInSeconds(progress);
        }
    }

    /**
     * Cleans up the Camera Player.
     */
    @Override
    public void cleanup() {
        if (videoPlayer != null) {
            videoPlayer.cleanup();
        }
    }
    
    /**
     * Displays always on top error massage.
     * @param message The message
     * @param title The title of the window
     */
    private void showErrorPane(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(getWholeWindow());
        dialog.setVisible(true);
    }
    
    /**
     * This method is only for use with the local file viewer version of this 
     * class.  It returns the index of the absolute path where the time range of
     * the video begins.
     * @return The index of the absolute path where the time range of the video
     * begins.
     */
    private int findRangeStartInAbsolutePath() {
        //Reduce path to the point where the last dash is just before the start
        //of the range.
        String reducedPath = absolutePath;
        //Must remove 6 -'s.
        for (int i = 0; i < 6; i++) {
            reducedPath = reducedPath.substring(0, 
                    reducedPath.lastIndexOf("-"));
        }
        
        //Range starts 2 places after last remaining dash.
        return (reducedPath.lastIndexOf("-") + 2);
    }
    
    /**
     * Helper function to retrieve the current resource.
     * @return The current resource.
     */
    private Resource getResource(){
        Resource thisResource = null;  //handle for this method.
        if (resource != null || absolutePath != null) {
            //Resouse must be found for local player.
            if (resource != null) {
                thisResource = resource;
            } else {
                //Trim absolute path to resource name.
                String resourceName = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1);
                //Must remove 7 -'s.
                for (int i = 0; i < 7; i++) {
                    resourceName = resourceName.substring(0, resourceName.lastIndexOf("-"));
                }
                //Final trim for space at end.
                resourceName = resourceName.trim();
                Debug.println("resource name: #" + resourceName + "#");

                //Get resources for comparison to name.
                Vector<Resource> resources;
                if (resourceType == WeatherResourceType.WeatherCamera) {
                    resources = appControl.getGeneralService().getWeatherCameraResources();
                } else {
                    resources = appControl.getGeneralService().getWeatherMapLoopResources();
                }
                //Find resource.
                for (int j = 0; j < resources.size(); j++) {
                    if (resources.get(j).getName().equals(resourceName)) {
                        thisResource = resources.get(j);
                        break;
                    }
                }
            }
        }
        return thisResource;
    }

    /**
     * This function captures a picture from the current time in the movie.
     */
    
    public void createSnapshot() {
        //Get the current resource
        Resource thisResource = getResource();

        //Check if resource was not found.
        if (thisResource == null) {
            showErrorPane("This resource is not available.", "Resource Not Found");
            return;
        }

        long currnetTimeInMills = movieController.getCurrentDateAndTime().getTimeInMillis();
        SnapShotViewer snapshot = new SnapShotViewer(this .getPictFromMovie(), 
                new Date(currnetTimeInMills), thisResource, ".jpeg");
        snapshot.preview();
    }
    
    
    /**
     * This method sets the print button to visible.
     *
     * @param v a boolean value.
     */
    public void setPrintButtonVisible(boolean v) {
        printButton.setVisible(v);
    }
   
    @Override
    public boolean isPlaying() {
        //NullPointerException occurs during Selective Start or No Data-Load
        //if videoPlayer is null.
        if (videoPlayer == null) {
            return false;
        }
        return videoPlayer.isPlaying();
    }

    private void addRightClickMenu() {
        rightClickMenu = new JPopupMenu();

        // Get icons.
        Icon previewIcon = IconProperties.getSnapshotPreviewIconImage();
        Icon printIcon = IconProperties.getSnapshotPrintIconImage();

        // Add menu items and action listeners.

        JMenuItem previewSave = new JMenuItem("Preview and Save", previewIcon);
        previewSave.addActionListener(new MoviePreviewSaveActionListener());

        JMenuItem print = new JMenuItem("Print", printIcon);
        print.addActionListener(new MoviePrintActionListener());

        rightClickMenu.add(previewSave);
        rightClickMenu.add(print);

        // Add components that can bring up this popup menu.
        videoPlayer.addMouseListener(new PopupListener(rightClickMenu));
    }

    /**
     * Returns the ID number for the current resource.
     *
     * @return the ID for the current resource
     */
    @Override
    public int getCurrentResourceNumber() {
        if(absolutePath != null){
            return -2;
        }
        if (itemComboBox.getSelectedItem().toString().equalsIgnoreCase("None")) {
            return -1;
        }
        return resource.getResourceNumber();
    }

    /**
     * Takes a vector of resources and sets the current resource to the first
     * appropriate resource in the list. The change id only guaranteed to take
     * effect when the range is next set.
     * 
     * @param resources A list of resources, the first appropriate resource will
     * be displayed in this panel
     */
    @Override
    public void setFutureResource(Vector<Resource> resources) {
        for (Resource newResource : resources) {
            for (int i = 0; i < itemComboBox.getItemCount(); i++) {
                if (newResource.getName().equals(
                        itemComboBox.getItemAt(i).toString())) {
                    itemComboBox.removeItemListener(comboBoxListener);
                    itemComboBox.setSelectedIndex(i);
                    resource = newResource;
                    itemComboBox.addItemListener(comboBoxListener);
                    return;
                }
            }
        }
    }
    
    /**
     * Returns the number of seconds into the video of the image that is
     * showing. (The measurement is in time filmed, not actual video length.) 
     *
     * @return The number of seconds into the video of the image that is
     * showing. (The measurement is in time filmed, not actual video length.) 
     */
    @Override
    public int getCurrentVideoSecond() {
        if (videoPlayer == null) {
            return -1;
        } else {
            return videoPlayer.getCurrentTimeOfMovies();
        }
    }

    private class MoviePreviewSaveActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            createSnapshot();
        }
    }

    private class MoviePrintActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            print();
        }
    }
}
