package weather.clientside.manager;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.BookmarkAddEditWindow;
import weather.clientside.gui.client.SearchBookmarkDialog;
import weather.clientside.utilities.TimedLoader;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.DateRangeSelectionWindow;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;

/**
 * Panel providing GUI controls to control the playback of movies registered to
 * this controller.
 *
 * It provides the GUI with a control panel, and has methods to register and
 * unregister MoviePanelManagers, which this controller will have control over.
 * This controller also has methods to register and unregister UpdateListeners,
 * which will receive updates on resource, resource range, and current movie
 * progress updates from this controller. The listeners must implement the
 * weather.clientSide.manager.MoviePanelManager interface.
 *
 * IMPORTANT NOTE: This class extends JPanel, but is not meant to be an actual
 * JPanel in itself. The only reason that it extends JPanel, is to enable the
 * use of the GUI Builder to design the control panel that this class contains.
 * The designed functionality is to use the GUI Builder to build up a JPanel
 * (see mainPanel) that can be returned by the getPanel() method.
 *
 * @see weather.clientside.manager.MoviePanelManager
 * @author Joe Sharp (2009)
 * @author Dustin Jones
 * @author Eric Lowrie (2012)
 *
 * @version Spring 2012
 */
public class MovieController extends JPanel implements Runnable {

    
    //Variables to control refrestPlot(true); (sends errors if it adds trace
    //too often)  Now refreshes trace if the trace is DELTA millisecond from the
    //possible new trace.
    private long lastTraceTime;
    private static final long DELTA = 5 * ResourceTimeManager
            .MILLISECONDS_PER_MINUTE;
    
    private ResourceRange range;
    private Vector<MoviePanelManager> controlledMoviePanels;
    private Vector<WeatherStationPanelManager> weatherStationPanels;
    private Vector<NotesAndDiaryPanelManager> notesManagers;
    private final static ImageIcon playingIcon = IconProperties.getStopMovieIconImage();
    private final static ImageIcon pausedIcon = IconProperties.getPlayMovieIconImage();
    private final long MILLISECONDS_IN_ONE_DAY = ResourceTimeManager.MILLISECONDS_PER_DAY;
    private final long MILLISECONDS_IN_ONE_HOUR = ResourceTimeManager.MILLISECONDS_PER_HOUR;
   
    /**
     * The application control system.
     */
    private ApplicationControlSystem appControl;
    /**
     * Serial Version ID.
     */
    private final static long serialVersionUID = 1L;
    /**
     * Current Playback Rate.
     */
    private float currentRate;
    /**
     * Boolean play.
     */
    private boolean isPlaying;
    private boolean shouldRun;
    /**
     * Holds the last slider value for which a video was shown.
     */
    private int lastSliderValue;

    /**
     * These are the Thread variable which handles updating the video slider.
     * TODO: This thread crashes when a bookmark hour is loaded, then partly 
     * played, then the same bookmark is loaded again.
     */
    private Thread videoSliderThread;
    private final Object isPaused = new Object();
    
    //For seeing if the day has changed and notes need to be updated 
    private long currentTime;
    private long previousTime;
    
    //Listeners for play/pause button
    private ActionListener playListener;
    private ActionListener stopListener;
    
    /**
     * Holds the time zone in which to display times if this is part of a local 
     * file viewer.  This must be left as null if this instance is not a local 
     * file viewer.
     */
    private TimeZone localTimeZone = null;
    
    //THESE TWO FUNCTIONS ARE FOR DEBUGING
    private void debugDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:mm:ss a z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(controlledMoviePanels
                .get(0).getTimeZone());
        if (date == null) {
            Debug.println(name + ": null");
        } else {
            Debug.println(name + ": " + simpleDateFormat.format(date));
        }
    }

    private void debugResourceRange(ResourceRange range) {
        debugDate(range.getStartTime(), "Range Start");
        debugDate(range.getStopTime(), "Range End");
    }

    /**
     * Constructor for MultiMovieController. Default constructor that creates an
     * empty Vector and creates the GUI elements such as the buttons and slider.
     *
     * @param appControl The application control system.
     * @param external Whether or not this part of an external window other 
     * than a local file viewer.
     */
    public MovieController(ApplicationControlSystem appControl, boolean external) {
        this.appControl = appControl;
        controlledMoviePanels = new Vector<>();
        weatherStationPanels = new Vector<>();
        notesManagers = new Vector<>();
        range = ResourceTimeManager.getResourceRange();
        initComponents();
        setTimeRangeLabel();

        //Sets the current rate to 1.0f. (Standard Play speed)
        this.currentRate = 1.0f;

        this.isPlaying = false;
        shouldRun = true;
        videoSliderThread = new Thread(this, "Movie Controller - VideoSliderThread");

        currentTime = range.getStartTime().getTime();
        previousTime = currentTime;
        lastTraceTime = currentTime;
        lastSliderValue = 0;
        
        // Set action listeners that will be used for the play/stop button.
        setListeners();
        
        //Hide slider pick date if not external.
        sliderPickDateButton.setVisible(external);
        
        mainPanel.validate();
    }
      
    /**
     * This stop button is clicked. All of the players are stopped, and the
     * slider is updated.
     *
     * @param evt An unused MouseEvent.
     * @throws weather.common.utilities.WeatherException
     */
    private void stopButtonMousePressed(MouseEvent evt) throws WeatherException {
        stopMovies();
    }

    /**
     * Adds a weather station manager to the panel.
     *
     * @param manager The weather station manager.
     */
    public void registerWeatherStationPanel(WeatherStationPanelManager manager) {
        weatherStationPanels.add(manager);
    }

    /**
     * Removes a weather station manager from the panel.
     *
     * @param manager The weather station manager.
     */
    public void removeWeatherStationPanel(WeatherStationPanelManager manager) {
        weatherStationPanels.remove(manager);
    }
    
    /**
     * Returns the first weather station panel manager to be registered or null
     * if none have been.
     * @return The first weather station panel manager to be registered or null
     * if none have been.
     */
    public WeatherStationPanelManager getPrimaryWeatherStationPanelManager() {
        if (weatherStationPanels.isEmpty()) {
            return null;
        } else {
            return weatherStationPanels.get(0);
        }
    }
    
    /**
     * Adds a note manager object to the panel.
     *
     * @param notesManager The note manager.
     */
    public void registerNotesManager(NotesAndDiaryPanelManager notesManager) {
        notesManagers.add(notesManager);
    }

    /**
     * Removes a note manager object from the panel.
     *
     * @param notesManager The note manager.
     */
    public void removeNotesManager(NotesAndDiaryPanelManager notesManager) {
        notesManagers.remove(notesManager);
    }

    /**
     * This play button is clicked. All of the players are set to play.
     *
     * @param evt An unused MouseEvent.
     * @throws weather.common.utilities.WeatherException
     */
    private void playButtonMousePressed(MouseEvent evt) throws WeatherException {
        
        int speed = speedSlider.getValue();
        float newRate = 1.0f;
        if (speed == 0) {
            newRate = 0.125f;
        } else if (speed == 1) {
            newRate = 0.125f;
        } else if (speed == 2) {
            newRate = 0.25f;
        } else if (speed == 3) {
            newRate = 0.5f;
        } else if (speed == 4) {
            newRate = 0.75f;
        } else if (speed == 5) {
            newRate = 1.0f;
        } else if (speed == 6) {
            newRate = 1.5f;
        } else if (speed == 7) {
            newRate = 2.0f;
        } else if (speed == 8) {
            newRate = 3.0f;
        } else if (speed == 9) {
            newRate = 4.0f;
        }

        this.currentRate = newRate;
        setPlaying(true);
        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.setMovieRate(this.currentRate, isPlaying);
        }


        /*
         * When the start button is pressed, a new thread replaces the old
         * thread and the thread starts and continues to run while isPlaying is
         * set to true.
         */

    }

    /**
     * This function updates the label that shows the local and GMT time
     * indicated by the slider.
     */
    private void updateTimeLabel() {
        StringBuilder newLabel = new StringBuilder();
        
        //Build Date Formats.
        String formatString = "M/dd/yyyy hh:mm:ss a z";
        SimpleDateFormat localFormat = new SimpleDateFormat(formatString);
        SimpleDateFormat zuluFormat = new SimpleDateFormat(formatString);

        //Set time zones of formats, with local first.
        TimeZone videoTimeZone;
        
        //First, handle case of local file viewer and use local viewer time zone
        if (localTimeZone != null) {
            videoTimeZone = localTimeZone;
        } else {    //all other cases
            //Determine time zone of the "first" panal's resource or to the 
            //local time zone if that resource is null.
            videoTimeZone = controlledMoviePanels.get(0).getTimeZone();
        }
        localFormat.setTimeZone(videoTimeZone);
        
        //Set second timeFormat to zulu time.
        TimeZone zuluTimeZone = TimeZone.getTimeZone("GMT");
        zuluFormat.setTimeZone(zuluTimeZone);
        
        newLabel.append("<html><center><b>");
        newLabel.append(localFormat.format(this.getCurrentAbsoluteTime()));
        newLabel.append("</b><br/>");
        if(!videoTimeZone.equals(zuluTimeZone)) {
            newLabel.append(zuluFormat.format(this.getCurrentAbsoluteTime()));
        }
        newLabel.append("</center></html>");

        //Change buffer to string showing UTC instead of GMT. 
        timeLabel.setText(newLabel.toString().replaceAll("GMT", "UTC"));
    }
    
    /**
     * This JSlider is changed. The players are stopped. The Movies are set to a
     * new time based on the percent of the slider.
     *
     * @param e An unused MouseEvent.
     * @throws weather.common.utilities.WeatherException
     */
    private void sliderBarStateChanged(MouseEvent e) throws WeatherException {
        //Debug.println ("MC in sliderBarStateChanged mouseevent");
        //Debug.println(e.toString());
        stopMovies();
        setPlaying(false);
        int sliderValue = videoSlider.getValue();
        if (Math.abs(sliderValue - lastSliderValue) < 10) {
            return; //avoids setMovieProgress being called too often.
        }
        lastSliderValue = sliderValue;
        
        for (MoviePanelManager movieManager : controlledMoviePanels) {

            ////Debug.println ("calling setMovieProgress.");
            movieManager.setMovieProgress(sliderValue);
        }
        stopMovies();
        setPlaying(false);
        stopMovies();
        updateTimeLabel();
    }

    /**
     * This JSlider is changed. The players are stopped. The Movies are set to a
     * new time based on the percent of the slider.
     *
     * @param e An unused ChangeEvent.
     * @throws weather.common.utilities.WeatherException
     */
    private void sliderBarStateChanged(ChangeEvent e) throws WeatherException {
        //Debug.println ("MC in sliderBarStateChanged change event.");
        // event source is always the jslider itself.
        stopMovies();
        setPlaying(false);
        int sliderValue = videoSlider.getValue();
        if (Math.abs(sliderValue - lastSliderValue) < 10) {
            return; //avoids setMovieProgress being called too often.
        }
        lastSliderValue = sliderValue;

        for (MoviePanelManager movieManager : controlledMoviePanels) {

            //Debug.println ("calling setmovieprogress (for loop)");
            movieManager.setMovieProgress(sliderValue);
        }
        stopMovies();
        setPlaying(false);
        stopMovies();
        updateTimeLabel();
    }

    /**
     * This RateSlider is changed. The movies of the players are accessed and
     * the internal rate is set to the new rate.
     *
     * @param e An unused ChangeEvent.
     */
    private void rateSliderBarStateChanged(ChangeEvent e) throws WeatherException {
        int speed = speedSlider.getValue();
        String rate = "";
        float newRate = 1.0f;

        if (speed == 0) {
            newRate = 0.125f;
            rate = "1/8";
        } else if (speed == 1) {
            newRate = 0.125f;
            rate = "1/8";
        } else if (speed == 2) {
            newRate = 0.25f;
            rate = "1/4";
        } else if (speed == 3) {
            newRate = 0.5f;
            rate = "1/2";
        } else if (speed == 4) {
            newRate = 0.75f;
            rate = "3/4";
        } else if (speed == 5) {
            newRate = 1.0f;
            rate = "normal";
        } else if (speed == 6) {
            newRate = 1.5f;
            rate = "1.5x";
        } else if (speed == 7) {
            newRate = 2.0f;
            rate = "2x";
        } else if (speed == 8) {
            newRate = 3.0f;
            rate = "3x";
        } else if (speed == 9) {
            newRate = 4.0f;
            rate = "4x";
        }

        //speedSlider.setToolTipText(rate);
        playSpeedLabel.setText("<html>Playing at<br/>" + rate + " speed.</html>");
        this.currentRate = newRate;
        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.setMovieRate(newRate, isPlaying);
        }
    }

    /**
     * Forward button pressed. The players stop if playing and the Movies are
     * advanced to the next key frame.
     *
     * @param evt The MouseEvent.
     */
    private void forwardButtonMousePressed(MouseEvent evt) throws WeatherException {
        stopMovies();
        setPlaying(false);
        
        int timeToSet = videoSlider.getValue() + 60;

        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.setMovieProgress(timeToSet);
        }

        removeSliderListener();
        videoSlider.setValue(timeToSet);
        lastSliderValue = timeToSet;
        addSliderListener();
        updateNotesAndSignalListeners(false);
        stopMovies();
        setPlaying(false);
        stopMovies();
        updateTimeLabel();
    }
    
    /**
     * Sets the slider to the specified count of seconds, where 0 represents the
     * first second of the video range.
     * 
     * @param seconds The second at which the slider and its videos are to be
     * set.
     */
    public void setToSecondCount(int seconds) {
        stopMovies();
        setPlaying(false);

        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.setMovieProgress(seconds);
        }

        removeSliderListener();
        videoSlider.setValue(seconds);
        lastSliderValue = seconds;
        addSliderListener();
        updateNotesAndSignalListeners(false);
        stopMovies();
        setPlaying(false);
        stopMovies();
        updateTimeLabel();
    }

    /**
     * Backward button pressed. The players stop and the Movies back up to the
     * last key frame.
     *
     * The back time has to be longer than the time between key frames are the
     * movie will actually progress forward.
     *
     *
     * @param evt The event that the backward button is pressed.
     */
    private void backwardButtonMousePressed(MouseEvent evt) throws WeatherException {
        stopMovies();
        setPlaying(false);

        int timeToSet = videoSlider.getValue() - 60;

        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.setMovieProgress(timeToSet);
        }
        
        removeSliderListener();
        videoSlider.setValue(timeToSet);
        lastSliderValue = timeToSet;
        addSliderListener();
        updateNotesAndSignalListeners(false);
        stopMovies();
        setPlaying(false);
        stopMovies();
        updateTimeLabel();
    }

    /**
     * This function adjusts only the range of the controller and does not check
     * for consistency.  It should only be used by windows with locally stored
     * videos.  The function also provides the time zone of the local video.
     * @param newRange The range of the local viewer.
     * @param zone The time zone of the local viewer.
     */
    public void setLocalViewerRangeAndTimeZone(ResourceRange newRange,
            TimeZone zone){
        range = newRange;
        localTimeZone = zone;
    }

    /**
     * Cleanup the MovieManagers.
     */
    public void cleanup() {
        shouldRun = false;
        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.cleanup();
        }
    }

    /**
     * Gets the start button.
     *
     * @return The start button instance.
     */
    public JButton getStartButton() {
        return this.playPauseButton;
    }

    /**
     * Gets the forward button.
     *
     * @return The forward button instance.
     */
    public JButton getForwardButton() {
        return this.incrementButton;
    }

    /**
     * Gets the backward button.
     *
     * @return The backward button instance.
     */
    public JButton getBackwardButton() {
        return this.decrementButton;
    }

    /**
     * Gets the default slider. This method can also be used to return the
     * current instance of the JSlider.
     *
     * @return The JSlider instance.
     */
    public JSlider getDefaultSlider() {
        return this.videoSlider;
    }

    /**
     * This method returns the movie controller main panel.
     *
     * @param width The width of the main panel.
     * @param height The height of the main panel.
     * @return The movie controller's main panel.
     */
    public JPanel getControlPanel(int width, int height) {
        this.mainPanel.setSize(width, height);
        return this.mainPanel;
    }
    
     /**
     * This method returns the movie controller button panel.
     *
     * @param width The width of the main panel.
     * @param height The height of the main panel.
     * @return The movie controller's bookmark panel.
     */
    public JPanel getButtonPanel(int width, int height) {
        this.buttonsPanel.setSize(width, height);
        return this.buttonsPanel;
    }

    /**
     * Returns the absolute time (start time of movie + milliseconds of the
     * movie played).
     *
     * @return The absolute time.
     */
    public Date getCurrentAbsoluteTime() {
        /**
         * Let M be the movie length in seconds and N be the number of hours the
         * movie covers. So M(s) = N (hrs). converting hours to ms we get M(s) =
         * MS_PER_HOUR * N (ms). Our conversion factor is now
         * (MS_PER_HOUR*N)(ms)/M(s). (Multiplying by our current position in the
         * movie cancels out the seconds)
         */
        int pos = videoSlider.getValue();
        int movieLength = videoSlider.getMaximum();
        int hoursCovered = (int)((range.getStopTime().getTime() 
                - range.getStartTime().getTime()) 
                / ResourceTimeManager.MILLISECONDS_PER_HOUR);

        int timeAt = (int) ((pos * (ResourceTimeManager.MILLISECONDS_PER_HOUR
                * hoursCovered)) / movieLength);
        //getting the current start date and adding the number of ms to where
        //we stopped
        Date date = new Date(range.getStartTime().getTime() + timeAt);
        return date;
    }

    /**
     * Saves the selected movie.
     *
     * @param pos The position of the movie in the controller.
     * @param filenames The list of file names to hold the movie segments
     * @throws WeatherException
     */
    public void saveMovie(int pos, ArrayList<String> filenames) throws WeatherException {
        controlledMoviePanels.get(pos).saveMovie(filenames);
    }

    /**
     * Get Rate Slider.
     *
     * @return The rate slider instance.
     */
    public JSlider getSpeedSlider() {
        return this.speedSlider;
    }

    /**
     * Returns the current playing rate of the movie.
     *
     * @return The current play rate as a float.
     */
    public float getCurrentRate() {
        return this.currentRate;
    }

    /**
     * Returns the main panel that represents the control panel that will
     * control the MoviePanelManagers that are registered with this controller.
     *
     * @return The mainPanel which is the ControlPanel for this MovieController.
     */
    public JPanel getDefaultControlPanel() {
        return this.mainPanel;
    }

    /**
     * Adds a MoviePanelManager object to the panel.
     *
     * @param panelManager The MoviePanelManager object.
     */
    public void registerMoviePanelManager(MoviePanelManager panelManager) {
        controlledMoviePanels.add(panelManager);
        try {
            resetSlider();
        } catch (WeatherException ex) {
            ex.show();
        }
    }
    
    /**
     * This function determines if a valid resource is selected within the
     * primary movie panel.
     * @return True if a valid resource is selected within the
     * primary movie panel; False otherwise.
     */
    public boolean doesPrimaryPanelHaveValidResource() {
        DBMSSystemManager dbms = appControl.getDBMSSystem();
        Resource resource = dbms.getResourceManager()
                .getWeatherResourceByNumber(controlledMoviePanels.get(0)
                .getCurrentResourceNumber());
        if (resource == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * This function determines if resource selected within the primary movie
     * panel has a related resource.  Note that, im order to awkward messages,
     * this function treats the "None" camera option as if it is linked to a
     * station.
     * @return True if resource selected within the primary movie panel has a 
     * related resource or is set to "None;" False otherwise.
     */
    public boolean doesPrimaryPanelHaveRelatedResource() {
        DBMSSystemManager dbms = appControl.getDBMSSystem();
        Resource camera = dbms.getResourceManager()
                .getWeatherResourceByNumber(controlledMoviePanels.get(0)
                .getCurrentResourceNumber());
        if (camera == null) {
            return true;
        }
        Resource station = dbms.getResourceRelationManager()
                .getRelatedStationResource(camera);
        if (station == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Determines if there is a weather station linked to this object that 
     * is set to change with the other controlled objects.
     * @return True if there is a weather station linked to this object that is 
     * set to change with the other controlled objects, False otherwise.
     */
    public boolean isDataPlotLinked() {
        if (weatherStationPanels.isEmpty()) {
            return false;
        }
        return weatherStationPanels.get(0).shouldBeChangedWithController();
    }

    /**
     * Sets the resource range and updates movie panel managers and movie
     * controller updateFieldContent listeners.
     *
     * @param newRange The new resource range.
     */
    private void setResourceRange(ResourceRange newRange) {
        range = newRange;
        ResourceTimeManager.setResourceRange(range);
        for (MoviePanelManager manager : controlledMoviePanels) {
            manager.setResourceRange(range);
        }
        try {
            resetSlider();
        } catch (WeatherException ex) {
            ex.show();
        }
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            panel.setDaySpanItems(panel.smallestFitForRange(range) == 1);
            //Due to addition and removal of "current Day", the panel's
            //combo box must be reset if the panel is not set to updateFieldContent with
            //the controller.
            if(!panel.shouldBeChangedWithController()) {
                panel.resetComboBox(range);
                continue;
            }
            panel.setDays(panel.smallestFitForRange(range));
            panel.setLastManualRange(range);
            panel.setResourceRange(range);
        }
        setTimeRangeLabel();
        updateTimeLabel();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        timeLabel = new javax.swing.JLabel();
        videoSlider = new javax.swing.JSlider();
        controlsPanel = new javax.swing.JPanel();
        decrementButton = //backwardIncrementButton (Jump backward 1% of movie slider)
        decrementButton = new JButton();
        decrementButton.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                try {
                    backwardButtonMousePressed(evt);
                } catch (WeatherException ex) {
                    /*
                    * ex.show();
                    * Should not be an issue that would cause a fatal error, this
                    * may change depending on future versions of the code.
                    */
                }

            }
        });
        playPauseButton = //Play button (MAIN)
        playPauseButton = new JButton();
        ;
        incrementButton = //forwardIncrementButton (Jump forward 1% of movie slider)
        incrementButton = new JButton();
        incrementButton.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                try {
                    forwardButtonMousePressed(evt);
                } catch (WeatherException ex) {
                    /*
                    * ex.show();
                    * Should not be an issue that would cause a fatal error, this
                    * may change depending on future versions of the code.
                    */
                }

            }
        });
        playSpeedLabel = new javax.swing.JLabel();
        speedSlider =
        speedSlider = new JSlider(0, 9, 5);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);

        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {

            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                try {
                    ChangeEvent e = new ChangeEvent(new String("Drag"));
                    rateSliderBarStateChanged(e);
                } catch (WeatherException ex) {
                    /*
                    * ex.show();
                    * Should not be an issue that would cause a fatal error, this
                    * may change depending on future versions of the code.
                    */
                }
            }
        });
        speedLabel = new javax.swing.JLabel();
        sliderPickDateButton = new javax.swing.JButton();
        buttonsPanel = new javax.swing.JPanel();
        timePeriodLabel = new javax.swing.JLabel();
        lblSetTime = new javax.swing.JLabel();
        chooseDateButton = new javax.swing.JButton();
        mostRecentDataButton = new javax.swing.JButton();
        prevNextHoursPanel = new javax.swing.JPanel();
        previousHoursButton = new javax.swing.JButton();
        nextHoursButton = new javax.swing.JButton();
        prevNextDayPanel = new javax.swing.JPanel();
        previousDayButton = new javax.swing.JButton();
        nextDayButton = new javax.swing.JButton();
        lblBookmarks = new javax.swing.JLabel();
        createBookmarkEventButton = new javax.swing.JButton();
        searchBookmarkEventButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setToolTipText("");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMaximumSize(new java.awt.Dimension(638, 112));
        setMinimumSize(new java.awt.Dimension(444, 81));
        setPreferredSize(new java.awt.Dimension(638, 112));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());

        timeLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel.setText("<html><center><b>Local Time</b><br/>Zulu Time</center></html>");
        topPanel.add(timeLabel, java.awt.BorderLayout.CENTER);

        videoSlider.setToolTipText(null);
        videoSlider.setDoubleBuffered(true);
        videoSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                videoSliderStateChanged(evt);
            }
        });
        videoSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                videoSliderMouseDragged(evt);
            }
        });
        topPanel.add(videoSlider, java.awt.BorderLayout.PAGE_END);
        videoSlider.getAccessibleContext().setAccessibleName("");

        mainPanel.add(topPanel, java.awt.BorderLayout.PAGE_START);

        decrementButton.setIcon(IconProperties.getFrameBackwardIconImage());
        decrementButton.setIcon(IconProperties.getFrameBackwardIconImage());
        decrementButton.setToolTipText("Step Back");
        decrementButton.setMaximumSize(new java.awt.Dimension(41, 25));
        decrementButton.setMinimumSize(new java.awt.Dimension(41, 25));
        decrementButton.setPreferredSize(null);

        playPauseButton.setIcon(IconProperties.getClientPlayNormalIcon());
        playPauseButton.setToolTipText("Play Video");
        playPauseButton.setPreferredSize(null);
        playPauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPauseButtonActionPerformed(evt);
            }
        });

        incrementButton.setIcon(IconProperties.getFrameForwardIconImage());
        incrementButton.setIcon(IconProperties.getFrameForwardIconImage());
        incrementButton.setToolTipText("Step Forward");
        incrementButton.setPreferredSize(null);

        playSpeedLabel.setText("<html>Playing at<br/>normal speed.</html>");

        speedSlider.setToolTipText(null);
        speedSlider.setMinimumSize(new java.awt.Dimension(36, 20));
        speedSlider.setPreferredSize(new java.awt.Dimension(200, 20));

        speedLabel.setText("Play Speed:");

        sliderPickDateButton.setText("Change Time Range");
        sliderPickDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sliderPickDateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlsPanelLayout = new javax.swing.GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
            controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlsPanelLayout.createSequentialGroup()
                .addComponent(decrementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(playPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(incrementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(speedLabel)
                .addGap(5, 5, 5)
                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(playSpeedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(sliderPickDateButton))
        );
        controlsPanelLayout.setVerticalGroup(
            controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(decrementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(playPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(incrementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(speedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(sliderPickDateButton)
                .addComponent(playSpeedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(controlsPanel, java.awt.BorderLayout.CENTER);

        add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 61, 575, 140));

        buttonsPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        timePeriodLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timePeriodLabel.setText("Period");
        timePeriodLabel.setMaximumSize(new java.awt.Dimension(100, 50));
        timePeriodLabel.setMinimumSize(new java.awt.Dimension(100, 50));
        timePeriodLabel.setOpaque(true);
        timePeriodLabel.setPreferredSize(new java.awt.Dimension(100, 50));
        buttonsPanel.add(timePeriodLabel);

        lblSetTime.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblSetTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSetTime.setText("<html><p align = \"center\">Set Time<br/>Range</p></html>");
        lblSetTime.setToolTipText(null);
        lblSetTime.setMaximumSize(new java.awt.Dimension(100, 50));
        lblSetTime.setMinimumSize(new java.awt.Dimension(100, 50));
        lblSetTime.setOpaque(true);
        lblSetTime.setPreferredSize(new java.awt.Dimension(100, 50));
        buttonsPanel.add(lblSetTime);

        chooseDateButton.setText("<html><p align = \"center\">Pick<br/>Date</p></html>");
        chooseDateButton.setToolTipText("Choose the date range for which information is shown.");
        chooseDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseDateButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(chooseDateButton);

        mostRecentDataButton.setText("<html><p align = \"center\">Most<br/>Recent Data</p></html>");
        mostRecentDataButton.setToolTipText("Load data for the four most recent hours.");
        mostRecentDataButton.setActionCommand("getLastFourHours");
        mostRecentDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mostRecentDataButton.setMargin(new java.awt.Insets(2, 8, 2, 8));
        mostRecentDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostRecentDataButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(mostRecentDataButton);

        prevNextHoursPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        previousHoursButton.setText("<html><p align=\"center\">Previous<br/>4 Hours</html>");
        previousHoursButton.setToolTipText("Load data for the four previous hours.");
        previousHoursButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousHoursButtonActionPerformed(evt);
            }
        });
        prevNextHoursPanel.add(previousHoursButton);

        nextHoursButton.setText("<html><p align=\"center\">Next<br/>4 Hours</html>");
        nextHoursButton.setToolTipText("Load data for the next four hours.");
        nextHoursButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextHoursButtonActionPerformed(evt);
            }
        });
        prevNextHoursPanel.add(nextHoursButton);

        buttonsPanel.add(prevNextHoursPanel);

        prevNextDayPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        previousDayButton.setText("<html><p align = \"center\">Previous<br/>Day</p></html>");
        previousDayButton.setToolTipText("Load data for the previous day.");
        previousDayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousDayButtonActionPerformed(evt);
            }
        });
        prevNextDayPanel.add(previousDayButton);

        nextDayButton.setText("<html><p align = \"center\">Next<br/>Day</p></html>");
        nextDayButton.setToolTipText("Load data for the next day.");
        nextDayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextDayButtonActionPerformed(evt);
            }
        });
        prevNextDayPanel.add(nextDayButton);

        buttonsPanel.add(prevNextDayPanel);

        lblBookmarks.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblBookmarks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBookmarks.setText("<html><p align = \"center\">Manage<br/>Bookmarks</p></html>");
        lblBookmarks.setMaximumSize(new java.awt.Dimension(100, 50));
        lblBookmarks.setMinimumSize(new java.awt.Dimension(100, 50));
        lblBookmarks.setOpaque(true);
        lblBookmarks.setPreferredSize(new java.awt.Dimension(100, 50));
        buttonsPanel.add(lblBookmarks);

        createBookmarkEventButton.setText("<html><p align = \"center\">Create<br/>Bookmark</p></html>");
        createBookmarkEventButton.setToolTipText("Create a bookmark of an event based on the data here.");
        createBookmarkEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBookmarkEventButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(createBookmarkEventButton);

        searchBookmarkEventButton.setText("<html><p align = \"center\">Search Database<br/>Bookmarks</p></html>");
        searchBookmarkEventButton.setToolTipText("Look up stored bookmarks and events.");
        searchBookmarkEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBookmarkEventButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(searchBookmarkEventButton);

        add(buttonsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 250, 210, 710));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This calls sliderBarStateChanged when ever a user drags the video slider.
     */
    private void videoSliderMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_videoSliderMouseDragged
    {//GEN-HEADEREND:event_videoSliderMouseDragged
        try {
            //Debug.println ("Mouse Dragged: calling MC sliderBarStateChanged");
            sliderBarStateChanged(evt);
        } catch (WeatherException ex) {
            ex.show();
        }
    }//GEN-LAST:event_videoSliderMouseDragged

    /**
     * This method updates all video and diary/notes data when the slider for
     * the video updates.
     *
     * @param evt The event that the video slider changes position.
     */
    private void videoSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_videoSliderStateChanged
        updateNotesAndSignalListeners(false);

        //Debug.println ("videoSliderStateChanged: calling MC sliderBarStateChanged");
        try {
            sliderBarStateChanged(evt);
        } catch (WeatherException ex) {
            ex.show();
        }
    }//GEN-LAST:event_videoSliderStateChanged

    /**
     * This method updates all video and diary/notes data when the slider for
     * the video updates.
     * @param calledInLoop Tells function if it was called from loop in the run function.
     * If so, the slider data plot every DELTA calls.  If not, it updates every call.
     */
    private void updateNotesAndSignalListeners(boolean calledInLoop) {
        //Debug.println ("In MC updateNotesAndSignalListeners");
        TimeZone timezone = DiaryManager.getResource().getTimeZone()
                .getTimeZone();
        currentTime = this.getCurrentDateAndTime().getTimeInMillis();
        if (ResourceTimeManager.getStartOfDayFromMilliseconds(currentTime,
                timezone)!= ResourceTimeManager
                .getStartOfDayFromMilliseconds(previousTime, timezone)) {
            for (NotesAndDiaryPanelManager notesManager : notesManagers) {
                //Leave external managers as they are.
                if(!notesManager.isExternal()) {
                    notesManager.updateFieldContent(new Date(currentTime));
                }
            }
            previousTime = currentTime;
        }

        if (!calledInLoop ||
                Math.abs(currentTime - lastTraceTime) >= DELTA) {
            for (WeatherStationPanelManager panel : weatherStationPanels) {
                //Debug.println ("updateListener (loop)");
                updateListenerToCurrentTime(panel);
            }
            lastTraceTime = currentTime;
        }

        //Debug.println ("returning MC updateNotesAndSignalListeners");
    }

    /**
     * Sets the slider to its beginning and updates its markings based on the 
     * current number of movies in the player.
     *
     * @throws WeatherException
     */
    public void resetSlider() throws WeatherException {
        //Set slider max based on lenth of time in range.
        long rangeLengthInMills = range.getStopTime().getTime() 
                - range.getStartTime().getTime();
        int rangeLengthInHours = (int)(rangeLengthInMills
                / MILLISECONDS_IN_ONE_HOUR);
        videoSlider.setMinimum(0);
        videoSlider.setMaximum(rangeLengthInHours * 3600);
        
        videoSlider.setValue(0);
        lastSliderValue = 0;
        
        //Hhow 1 mark evert hour, noting slider ticks are in seconds.
        videoSlider.setMajorTickSpacing(3600);

        videoSlider.setPaintTicks(true);
        videoSlider.updateUI();
    }
    
    /**
     * Gets the current <code>ResourceRange</code>.
     * @return The current <code>ResourceRange</code>.
     */
    public ResourceRange getResourceRange() {
        return range;
    }
    
    /**
     * Gets the time zone of the "first" movie panel (the camera panel of the
     * main window or the video panel of the external file viewer).
     * @return The time zone of the "first" movie panel.
     */
    public TimeZone getPrimaryTimeZone() {
        //First, handle case of local file viewer and use local viewer time zone
        if (localTimeZone != null) {
            return localTimeZone;
        } else {    //all other cases
            //Determine time zone of the "first" panal's resource or to the 
            //local time zone if that resource is null.
            return controlledMoviePanels.get(0).getTimeZone();
        }
    }
    
    /**
     * Gets the current Date and Time the movie stopped at in REAL Time. Easily
     * get a Date object using <code>GregorianCalendar.getTime()</code>.
     *
     * @return The date and time the movie stopped at.
     */
    public GregorianCalendar getCurrentDateAndTime() {
        GregorianCalendar dateAndTime = new GregorianCalendar();
        dateAndTime.setTimeZone(getPrimaryTimeZone());
        dateAndTime.setTimeInMillis(this.getCurrentAbsoluteTime().getTime());
        return dateAndTime;
    }
    
    /**
     * Gets the Date and Time of the beginning of the shown range in REAL Time. 
     * Easily get a Date object using <code>GregorianCalendar.getTime()</code>.
     *
     * @return The date and time of the beginning of the shown range.
     */
    public GregorianCalendar getStartingDateAndTime() {
        GregorianCalendar dateAndTime = new GregorianCalendar();
        dateAndTime.setTimeZone(getPrimaryTimeZone());
        dateAndTime.setTimeInMillis(range.getStartTime().getTime());
        return dateAndTime;
    }
    
    /**
     * Gets the Date and Time of the ending of the shown range in REAL Time. 
     * Easily get a Date object using <code>GregorianCalendar.getTime()</code>.
     *
     * @return The date and time of the ending of the shown range.
     */
    public GregorianCalendar getEndingDateAndTime() {
        GregorianCalendar dateAndTime = new GregorianCalendar();
        dateAndTime.setTimeZone(getPrimaryTimeZone());
        dateAndTime.setTimeInMillis(range.getStopTime().getTime());
        return dateAndTime;
    }
//END RODCODE SECTION
    
    /**
     * This method first stops all currently playing movies, then displays the
     * date selection window, then displays the movie for the selected time
     * range.
     *
     * @param evt The event that the time period label is pressed.
     */
    private void chooseDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseDateButtonActionPerformed
        stopMovies();
        setPlaying(false);
        TimeZone cameraTimeZone = controlledMoviePanels
                .get(0).getTimeZone();
        ResourceRange newRange = DateRangeSelectionWindow
                .getNewResourceRange(range, cameraTimeZone, false, false, true);
        if (newRange != null) {
            this.debugResourceRange(newRange);
            if (!range.isSameHours(newRange)) {
                updateRange(newRange);
                for (WeatherStationPanelManager panel : weatherStationPanels) {
                    updateListenerToCurrentTime(panel);
                }
            }
            setReady();
        }
    }//GEN-LAST:event_chooseDateButtonActionPerformed

    private void mostRecentDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostRecentDataButtonActionPerformed
        stopMovies();
        setPlaying(false);
        ResourceRange newRange = ResourceTimeManager.getDefaultRange();
        updateRange(newRange);
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            updateListenerToCurrentTime(panel);
        }
        
        setReady();
    }//GEN-LAST:event_mostRecentDataButtonActionPerformed

    private void playPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPauseButtonActionPerformed
        try {
            playButtonMousePressed(null);
        } catch (WeatherException ex) {
            /*
             * ex.show(); Should not be an issue that would cause a fatal error,
             * this may change depending on future versions of the code.
             */
        }
    }//GEN-LAST:event_playPauseButtonActionPerformed

    private void createBookmarkEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBookmarkEventButtonActionPerformed
        /**
         * TODO A better way to get the MoviePanelManager's and
         * WeatherStationPanelManager should be found.
         */
        
        //Find default save location.
        User user = this.appControl.getGeneralService().getUser();
        boolean isLocal = false;    //assume admin buser
        //Set the user type.
        if (user.getUserType() == UserType.student || user.getUserType() == UserType.guest){
            isLocal = true;
        }
        
        if (controlledMoviePanels.size() > 0 && weatherStationPanels.size() > 0) {
            new BookmarkAddEditWindow(appControl, 
                    (WeatherStationPanelManager) weatherStationPanels.get(0), this, 
                    false, isLocal, true);
        }
    }//GEN-LAST:event_createBookmarkEventButtonActionPerformed

    private void searchBookmarkEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBookmarkEventButtonActionPerformed
        new SearchBookmarkDialog(appControl, this, false, true);
    }//GEN-LAST:event_searchBookmarkEventButtonActionPerformed

    private void previousDayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousDayButtonActionPerformed
        stopMovies();
        setPlaying(false);
        long startTime = range.getStartTime().getTime() - MILLISECONDS_IN_ONE_DAY;
        long endTime = range.getStopTime().getTime() - MILLISECONDS_IN_ONE_DAY;
        updateRange(new ResourceRange(new Date(startTime), new Date(endTime)));
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            updateListenerToCurrentTime(panel);
        }
        setReady();
    }//GEN-LAST:event_previousDayButtonActionPerformed

    private void nextDayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextDayButtonActionPerformed
        stopMovies();
        setPlaying(false);
        long lastAvailableTime = ResourceTimeManager.getDefaultRange().getStopTime().getTime();
        long time = range.getStopTime().getTime() + MILLISECONDS_IN_ONE_DAY;
        if (time > lastAvailableTime) {
            ResourceRange newRange = ResourceTimeManager.getDefaultRange();
            updateRange(newRange);
        } else {
            long startTime = range.getStartTime().getTime() + MILLISECONDS_IN_ONE_DAY;
            long endTime = range.getStopTime().getTime() + MILLISECONDS_IN_ONE_DAY;
            updateRange(new ResourceRange(new Date(startTime), new Date(endTime)));
        }
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            updateListenerToCurrentTime(panel);
        }
        setReady();
    }//GEN-LAST:event_nextDayButtonActionPerformed

    private void previousHoursButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousHoursButtonActionPerformed
        stopMovies();
        setPlaying(false);
        long startTime = range.getStartTime().getTime() - 4 * MILLISECONDS_IN_ONE_HOUR;
        long endTime = range.getStartTime().getTime();
        updateRange(new ResourceRange(new Date(startTime), new Date(endTime)));
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            updateListenerToCurrentTime(panel);
        }
        setReady();
    }//GEN-LAST:event_previousHoursButtonActionPerformed

    private void nextHoursButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextHoursButtonActionPerformed
        stopMovies();
        setPlaying(false);
        long lastAvailableTime = ResourceTimeManager.getDefaultRange().getStopTime().getTime();
        long time = range.getStopTime().getTime() + 4 * MILLISECONDS_IN_ONE_HOUR;
        if (time > lastAvailableTime) {
            ResourceRange tempRange = ResourceTimeManager.getDefaultRange();
            //Range must be exactly 4 hours long.
            long startTime = tempRange.getStopTime().getTime()
                    - 4 * MILLISECONDS_IN_ONE_HOUR;
            long endTime = tempRange.getStopTime().getTime();
            updateRange(new ResourceRange(new Date(startTime), new Date(endTime)));
        } else {
            long startTime = range.getStopTime().getTime();
            long endTime = range.getStopTime().getTime() + 4 * MILLISECONDS_IN_ONE_HOUR;
            updateRange(new ResourceRange(new Date(startTime), new Date(endTime)));
        }
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            updateListenerToCurrentTime(panel);
        }
        setReady();
    }//GEN-LAST:event_nextHoursButtonActionPerformed

    private void sliderPickDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sliderPickDateButtonActionPerformed
        stopMovies();
        setPlaying(false);
        TimeZone cameraTimeZone = controlledMoviePanels
                .get(0).getTimeZone();
        ResourceRange newRange = DateRangeSelectionWindow
                .getNewResourceRange(range, cameraTimeZone, false, false, 
                false);
        if (newRange != null) {
          updateRange(newRange);
        }
    }//GEN-LAST:event_sliderPickDateButtonActionPerformed

    /**
     * Updates the resource of the controlled note manger.  The parameter should
     * be the controlled video camera for consistency.  If the note manager if
     * external, consistency is not an issue and this method will do nothing.
     * @param newResource The current camera resource.
     */
    public void updateDiaryPanelResource(Resource newResource) {
        if(!notesManagers.get(0).isExternal()) {
            notesManagers.get(0).updateInternalDiaryResource(newResource);
            notesManagers.get(0).updateFieldContent(getCurrentAbsoluteTime());
        }
    }
    
    /**
     * Function to updateFieldContent the date range from a range selector on the form.
     *
     * @param newRange The ResourceRange to updateFieldContent the range to.
     */
    public void updateRange(final ResourceRange newRange) {
        if (newRange != null) {
            //Create and start TimedLoader.
            TimedLoader loader = new TimedLoader() {
                @Override
                protected String getLabelText() {
                    return "Weather Data";
                }

                @Override
                protected void doLoading() {
                    for (NotesAndDiaryPanelManager notesManager : notesManagers) {
                        //Leave external managers as they are.
                        if (!notesManager.isExternal()) {
                            notesManager.updateFieldContent(newRange.getStartTime());
                        }
                    }
                    setResourceRange(newRange);
                    Debug.println("running gc: updateRange() was called");
                    Runtime.getRuntime().gc();
                    setReady();
                } 
            };
            loader.execute();
        }
    }

    /**
     * Sets the date text on the timePeriodLabel to the current resource range.
     */
    private void setTimeRangeLabel() {
        StringBuilder newLabel = new StringBuilder();
        SimpleDateFormat timeFormat = new SimpleDateFormat("M/dd/yyyy hh:00a");
        SimpleDateFormat zoneFormat = new SimpleDateFormat("z");
        
        //Assume local time zone.  This assumption is necessary at start-up
        //when there are no controlled panels.
        TimeZone videoTimeZone = TimeZone.getDefault();
        
        if (!controlledMoviePanels.isEmpty()) {
            //Determine time zone of camera resource or to the local time zone 
            //if that resource is null.
            videoTimeZone = controlledMoviePanels.get(0).getTimeZone();
        }
        
        //Set time zone of format.
        timeFormat.setTimeZone(videoTimeZone);
        zoneFormat.setTimeZone(videoTimeZone);
        
        //Create time zone string.
        String fullZoneString;  //Could have 2 labels if spanning DST change.
        String fromZoneString = zoneFormat.format(range.getStartTime()).toString();
        String toZoneString = zoneFormat.format(range.getStopTime()).toString();
        if(fromZoneString.equals(toZoneString)) {
            fullZoneString = toZoneString;
        } else {
            fullZoneString = fromZoneString + " - " + toZoneString;
        }
        
        //Set label txet.
        newLabel.append("<html><center><b>");
        newLabel.append(timeFormat.format(range.getStartTime()).toString());
        newLabel.append(" to<br/>");
        newLabel.append(timeFormat.format(range.getStopTime()).toString());
        newLabel.append("<br/>");
        newLabel.append(fullZoneString);
        newLabel.append("</b></center></html>");
        
        //Place text or label with "GMT" replaced by "UTC."
        timePeriodLabel.setText(newLabel.toString().replaceAll("GMT", "UTC"));
    }
    
    /**
     * Update times displayed by the <code>MovieController</code> to match the
     * <code>ResourceTimeZone</code> of the current camera <code>Resource</code>
     * or to the local time zone if that resource is null.
     */
    public void updateTimeZone() {
        this.updateTimeLabel();
        this.setTimeRangeLabel();
    }

    /**
     * Cycles through all of the MovieManagers registered to this controller and
     * stops the movies that are playing in those managers.
     */
    public void stopMovies() {
        for (MoviePanelManager movieManager : controlledMoviePanels) {
            movieManager.stop();
        }
        setPlaying(false);
    }

    /*
     * This is the run() function for the thread used for the videoSlider. While
     * isPlaying is set to true, it constantly updates the video slider to the
     * correct position based on the movie. After it updates it will sleep for a
     * period of time.
     */
    @Override
    public void run() {

        while (shouldRun) {
            if(controlledMoviePanels.size() < 1){
                break;
            }
            MoviePanelManager controlPanel = controlledMoviePanels.get(0);
            if(controlPanel.getCurrentResourceNumber() == -1){
                if(controlledMoviePanels.size() > 1){
                    controlPanel = controlledMoviePanels.get(1);
                }
            }
            int secsComplete = controlPanel.getCurrentVideoSecond();


            if (secsComplete <= 0 && !controlPanel.isPlaying()) {
                //video has reached the end and reset, updateFieldContent play state
                setPlaying(false);

                removeSliderListener();
                videoSlider.setValue(secsComplete);
                lastSliderValue = secsComplete;
                addSliderListener();
                updateNotesAndSignalListeners(false);
                updateTimeLabel();
            }

            // Removing the panel before the change, then adding it back
            // will solve the problem.
            // We could also have 2 slider components and swap them in the
            // UI as needed.

            removeSliderListener();
            videoSlider.setValue(secsComplete);
            lastSliderValue = secsComplete;
            addSliderListener();

            // This updates the other media such as weather map and graph
            // so they are synchronized with the video.
            updateNotesAndSignalListeners(true);
            updateTimeLabel();

            if (isPlaying == false) {
                //Debug.println("Waiting videoSliderThread");
                synchronized (isPaused) {
                    try {
                        isPaused.wait();
                    } catch (InterruptedException ex) {
                        Debug.println("videoSliderThread interrupted");
                    }
                }
            }
        }
        Debug.println("videoSliderThread Exiting");
    }
    
    /**
     * Checks to see if the play, increment, and decrement buttons should be
     * enabled or not depending on whether on not the is at least one controlled
     * panel showing a video.
     */
    public void checkToEnableButtons() {
        for (MoviePanelManager manager : controlledMoviePanels) {
            if (manager.getCurrentResourceNumber() != -1) {
                // Found a video, so enable buttons.
                incrementButton.setEnabled(true);
                playPauseButton.setEnabled(true);
                decrementButton.setEnabled(true);
                return;
            }
        }
        // No videos found, so disable buttons.
        incrementButton.setEnabled(false);
        playPauseButton.setEnabled(false);
        decrementButton.setEnabled(false); 
    }
    
    /**
     * Returns the current slider position in ticks, which represents the number
     * of seconds between the start of the current range and the real time that
     * matches the current slider position.
     * 
     * @return The current slider position in ticks, which represents the number
     * of seconds between the start of the current range and the real time that
     * matches the current slider position.
     */
    public int getSliderSecondsElapsed() {
        return videoSlider.getValue();
    }

    private void setListeners() {
        playListener = new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    playButtonMousePressed(null);
                } catch (WeatherException ex) {
                    /*
                     * ex.show(); Should not be an issue that would cause a
                     * fatal error, this may change depending on future versions
                     * of the code.
                     */
                }
            }
        };

        stopListener = new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    stopButtonMousePressed(null);
                } catch (WeatherException ex) {
                    /*
                     * ex.show(); Should not be an issue that would cause a
                     * fatal error, this may change depending on future versions
                     * of the code.
                     */
                }
            }
        };
    }

    private void setPlaying(boolean value) {
        this.isPlaying = value;

        if (value) {
            // Change the button icon.
            playPauseButton.setIcon(playingIcon);

            // Remove the old action panel.
            ActionListener[] listeners = playPauseButton.getActionListeners();
            for (ActionListener listener : listeners) {
                playPauseButton.removeActionListener(listener);
            }

            // Add the new action panel.
            playPauseButton.addActionListener(stopListener);
            synchronized (isPaused) {
                isPaused.notify();
            }
        } else {
            // Change the button icon.
            playPauseButton.setIcon(pausedIcon);

            // Remove the old action panel.
            ActionListener[] listeners = playPauseButton.getActionListeners();
            for (ActionListener listener : listeners) {
                playPauseButton.removeActionListener(listener);
            }

            // Add the new action panel.
            playPauseButton.addActionListener(playListener);
        }
    }

    /**
     * Only use this to solve the skipping problem caused by slider updateFieldContent.
     */
    private void removeSliderListener() {
        ChangeListener[] listeners = videoSlider.getChangeListeners();
        for (ChangeListener listener : listeners) {
            videoSlider.removeChangeListener(listener);
        }
    }

    /**
     * Only use this to solve the skipping problem caused by slider updateFieldContent.
     */
    private void addSliderListener() {
        videoSlider.addChangeListener(new javax.swing.event.ChangeListener() {

            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                videoSliderStateChanged(evt);
            }
        });
    }
    
    /**
     * Sets the resources for the weather camera and weather map to the first 
     * appropriate resources contained in the given vector.  The change id only
     * guaranteed to take effect when the range is next set.
     * @param resources A list of resources used to set the displayed resources.
     */
    public void setFutureVideoResources(Vector<Resource> resources){
        setPlaying(false);
        for(MoviePanelManager manager : controlledMoviePanels){
            manager.setFutureResource(resources);
        } 
    }
    
    /**
     * Sets the current resource used by the weather station to the given
     * resource.
     * @param resource The resource to set.
     */
    public void setWeatherStationResource(Resource resource) {
        for (WeatherStationPanelManager panel : weatherStationPanels) {
            panel.setResource(resource);
        }
    }

    
    /**
     * Update the time of the given weather station panel manager to match that
     * of this controller.
     * @param panel The given weather station panel manager.
     */
    private void updateListenerToCurrentTime(WeatherStationPanelManager panel) {
        panel.updateCurrentTime(getCurrentAbsoluteTime().getTime());
    }

    /**
     * The Video Surface is displayed, and can begin playback.
     */
    public void setReady() {
        //Load the preview image.
        Debug.println("Load the preview image in setReady.");
        for (MoviePanelManager temp : controlledMoviePanels) {
            setPlaying(false);
            temp.reset();
        }
        
        //Start slider thead, if neccessary.
        Debug.println("videoSliderThread state: " + videoSliderThread.isAlive());
        if (!videoSliderThread.isAlive()) {
            Debug.println("Starting videoSliderThread.");
            videoSliderThread.start();
        }
    }

    /**
     * Stops the video slider thread.
     */
    public void stopSliderThread() {
        setPlaying(false);
    }
    
    /**
     * Sets video panels to none as requested.
     * @param hasNoCamera true if camera panel is to be none
     * @param hasNoMap true if map panel is to be none
     */
    public void setVideoPanelsToNone(boolean hasNoCamera, boolean hasNoMap){
        for (MoviePanelManager movieManager : controlledMoviePanels) {
            if(movieManager instanceof WeatherMoviePanelManager
                    && movieManager.isCameraManager() && hasNoCamera) {
                movieManager.setToNone();
            }
            if(movieManager instanceof WeatherMoviePanelManager
                    && !movieManager.isCameraManager() && hasNoMap) {
                movieManager.setToNone();
            }
        } 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton chooseDateButton;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JButton createBookmarkEventButton;
    private javax.swing.JButton decrementButton;
    private javax.swing.JButton incrementButton;
    private javax.swing.JLabel lblBookmarks;
    private javax.swing.JLabel lblSetTime;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton mostRecentDataButton;
    private javax.swing.JButton nextDayButton;
    private javax.swing.JButton nextHoursButton;
    private javax.swing.JButton playPauseButton;
    private javax.swing.JLabel playSpeedLabel;
    private javax.swing.JPanel prevNextDayPanel;
    private javax.swing.JPanel prevNextHoursPanel;
    private javax.swing.JButton previousDayButton;
    private javax.swing.JButton previousHoursButton;
    private javax.swing.JButton searchBookmarkEventButton;
    private javax.swing.JButton sliderPickDateButton;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel timePeriodLabel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JSlider videoSlider;
    // End of variables declaration//GEN-END:variables
}
