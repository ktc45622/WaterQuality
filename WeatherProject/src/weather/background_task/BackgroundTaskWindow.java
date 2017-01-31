package weather.background_task;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import weather.ApplicationControlSystem;
import weather.clientside.utilities.LocalFileCleaner;
import weather.clientside.utilities.ResourceTreeManager;
import weather.clientside.utilities.ResourceVisibleTester;
import weather.common.data.User;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.IconProperties;
import weather.common.gui.component.BUJFrame;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;

/**
 * This class is used for downloading any resource that the user wants in the
 * background. It used to download movies in the future or movies in that past.
 * It can be ran forever or it can be stopped at a certain time of day. It will
 * check the server once every hour after the range grace retrieval period to
 * grab the new movies/weather stations for each active resource. It will store
 * the resources in the folders used when running the main application.
 *
 * @author Colton Daily (2014)
 * @version Spring 2014
 */
public class BackgroundTaskWindow extends BUJFrame implements Runnable {
    
    private final ApplicationControlSystem appControl;

    private boolean shouldRun;

    private final Vector<Resource> cameraResources;
    private final Vector<Resource> mapLoopResources;
    private final Vector<Resource> weatherStationResources;
    private final Vector<Resource> selectedResources;

    //Dates selected by user.
    private Calendar selectedStartDate;
    private Calendar selectedEndDate;
    
    private final SimpleDateFormat dateFormatter;

    private ItemListener itemListener;

    private Thread thread;

    private SystemTray tray;
    private TrayIcon trayIcon;

    private MenuItem openItem;
    private MenuItem exitItem;
    private MenuItem startItem;
    private MenuItem stopItem;
    
    private final User user;

    /**
     * Creates new form BackgroundTaskFrame
     *
     * @param appControl The program instance of ApplicationControlSystem
     */
    public BackgroundTaskWindow(ApplicationControlSystem appControl) {
        super();
        shouldRun = false;
        initComponents();
        checkForSystemTray();

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm aa");
        initListeners();
        setupCalendar();

        firstHoursComboBox.addItemListener(itemListener);
        firstAmOrPmComboBox.addItemListener(itemListener);
        lastHoursComboBox.addItemListener(itemListener);
        lastAmOrPmComboBox.addItemListener(itemListener);
        aviRadioButton.addItemListener(itemListener);
        mp4RadioButton.addItemListener(itemListener);
        bothRadioButton.addItemListener(itemListener);
        
        this.appControl = appControl;
        user = appControl.getGeneralService().getUser();
        cameraResources = appControl.getGeneralService().getWeatherCameraResources();
        mapLoopResources = appControl.getGeneralService().getWeatherMapLoopResources();
        weatherStationResources = appControl.getGeneralService().getWeatherStationResources();
        selectedResources = new Vector<>();
        stopButton.setEnabled(false);
        stopItem.setEnabled(false);

        initResourceCheckBox(weatherCamerasPanel, cameraResources);
        initResourceCheckBox(mapLoopsPanel, mapLoopResources);
        initResourceCheckBox(weatherStationsPanel, weatherStationResources);

        //Prepare tree with all data
        ResourceTreeManager.setToAVIAndMP4();
        ResourceTreeManager.initializeData();
        
        //Size and center window
        int width = 1000 + this.getInsets().left + this.getInsets().right;
        int height = 414 + this.getInsets().top + this.getInsets().bottom;
        Dimension thisDim = new Dimension(width, height);
        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        super.postInitialize(true);
    }
    
     //THESE TWO FUNCTIONS ARE FOR DEBUGING
    private void debugDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:00 a";
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
     * Determines whether or not an hour of video is available for download.
     * @param startOfHour Calendar holding the start of the hour to be checked.
     * @return True if hour is available, false if not.
     */ 
    private boolean isHourDownloadable(Calendar startOfHour){
        //Set a calendar to the time this hour becomes available.
        Calendar timeToCheck = (Calendar) startOfHour.clone();
        timeToCheck.add(Calendar.HOUR, 1);
        timeToCheck.add(Calendar.MINUTE, Integer.parseInt(PropertyManager
                .getGeneralProperty("rangeRetrieveGracePeriod")));
        
        //Get current time.
        Calendar now = Calendar.getInstance();
        
        //Compare times.
        return now.getTimeInMillis() >= timeToCheck.getTimeInMillis();
    }
    
    /**
     * Downloads all selected resources for a given range.
     * @param range The given range.
     */
    private void downloadResourcesForRange(ResourceRange range){
        for (Resource r : selectedResources) {
            ResourceTreeManager.getResourceInstancesForRange(r, range);
        }
    }
    
    @Override
    public void run() {
        /*Get initially available data.*/

        //Find end of download range.
        Calendar endOfRange = (Calendar) selectedStartDate.clone();
        while (isHourDownloadable(endOfRange) //Hour can be downloaded...
                && (neverCheckBox.isSelected() //and is wanted (check is also next line.
                || endOfRange.getTimeInMillis() < selectedEndDate.getTimeInMillis())) {
            endOfRange.add(Calendar.HOUR, 1);
        }
        debugDate(new Date(endOfRange.getTimeInMillis()), "Range End");

        //Make show we have a gap in time.
        if (selectedStartDate.getTimeInMillis() < endOfRange.getTimeInMillis()) {
            ResourceRange downloadRange = new ResourceRange(
                    new Date(selectedStartDate.getTimeInMillis()),
                    new Date(endOfRange.getTimeInMillis()));
            Debug.println("Downloading Initial Range:"); 
            debugResourceRange(downloadRange);
            downloadResourcesForRange(downloadRange);
        }
        
        /*Loop for remaining time in range.*/
        
        while (shouldRun) {
            //Test to set interupt for sleep below.  Yhis will happen
            //if the end of the selrcted range has been reached.
            if(!neverCheckBox.isSelected() && endOfRange.getTimeInMillis() 
                    == selectedEndDate.getTimeInMillis()){
                stopDownloader();
                thread.interrupt();
            }
            
            //Find next download range.
            debugDate(new Date(endOfRange.getTimeInMillis()), "Range End");
            Calendar startOfRange = (Calendar)endOfRange.clone();
            endOfRange.add(Calendar.HOUR, 1);
            ResourceRange downloadRange = new ResourceRange(
                    new Date(startOfRange.getTimeInMillis()),
                    new Date(endOfRange.getTimeInMillis()));
            
            //Sleep time is the time between when we can download and now.
            Calendar downloadTime = (Calendar)endOfRange.clone();
            downloadTime.add(Calendar.MINUTE, Integer.parseInt(PropertyManager
                .getGeneralProperty("rangeRetrieveGracePeriod")));
            Calendar now = Calendar.getInstance();
            long sleepTime = downloadTime.getTimeInMillis() - now.getTimeInMillis();
            
            //Show next update tine
            updateLabel.setText("Next update at: " 
                    + new SimpleDateFormat("h:mm aa").format(downloadTime.getTime()));
            
            //Wait and download if not interupted
            try{
                //If time range has passed steepTime is negative.  This would
                //trigger an exeption for a illegal argument.  So, the argument
                //musr be made positive.  The thread won't actually sleep this
                //time, as the thread will act on an interupt set above.
                Thread.sleep(sleepTime > 0 ? sleepTime : 1000);
                downloadResourcesForRange(downloadRange);
            } catch (InterruptedException ex) {
                stopDownloader();   //Sete shouldRun = false
                Debug.println("Thead was interrrupted");
            }
        }
    }

    /**
     * When the application is first loaded the end date is set to the current
     * time and the start date is usually 4 hours before it but it will change
     * to whatever the DEFAULT_START_HOURS property value is.
     */
    private void setupCalendar() {

        //gets the current hour and sets the last hour combo box to it
        Calendar cal = Calendar.getInstance();
        int rangePeriod = Integer.parseInt(PropertyManager.getGeneralProperty("rangeRetrieveGracePeriod"));
        //if the current hour is not passed the range grace period
        //than the last hour should be the previous hour
        if (cal.get(Calendar.MINUTE) < rangePeriod) {
            cal.add(Calendar.HOUR, -1);
        }

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        //Set stop time controls - first is hour box
        int currentHour = cal.get(Calendar.HOUR);
        if(currentHour == 0){ //0 returned for midnight or noon.
            currentHour = 12;
        }
        //Must adjust - hours are 1 to 12, but indexes are 0 to 11
        lastHoursComboBox.setSelectedIndex(currentHour - 1);
        
        //Now set AM/PM
        String timeStamp = new SimpleDateFormat("a").format(cal.getTime().getTime());
        if (timeStamp.equals("PM")) {
            lastAmOrPmComboBox.setSelectedIndex(1); //PM
        }
        
        selectedEndDate = (Calendar) cal.clone();
        validateEndDate();

        //gets the default start hours and subtracts that from the current time
        //and sets the first combo box to the new time
        int hoursToSubtract = Integer.parseInt(PropertyManager.getLocalProperty("DEFAULT_START_HOURS"));
        cal.add(Calendar.HOUR, -hoursToSubtract);
        
       //Set start time controls - first is hour box
        currentHour = cal.get(Calendar.HOUR);
        if (currentHour == 0) { //0 returned for midnight or noon.
            currentHour = 12;
        }
        //Must adjust - hours are 1 to 12, but indexes are 0 to 11
        firstHoursComboBox.setSelectedIndex(currentHour - 1);
        
        //Now set AM/PM
        timeStamp = new SimpleDateFormat("a").format(cal.getTime().getTime());
        if (timeStamp.equals("PM")) {
            firstAmOrPmComboBox.setSelectedIndex(1); //PM
        }
        
        selectedStartDate = (Calendar) cal.clone();
        validateStartDate();
        
        updateDateLabel();
    }

    /**
     * Creates all the listeners for the application.
     */
    private void initListeners() {
        itemListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    if (e.getSource().equals(firstAmOrPmComboBox)) {
                        if (firstAmOrPmComboBox.getSelectedIndex() == 0) {
                            selectedStartDate.set(Calendar.AM_PM, Calendar.AM);
                        } else {
                            selectedStartDate.set(Calendar.AM_PM, Calendar.PM);
                        }
                        updateDateLabel();
                    }

                    if (e.getSource().equals(lastAmOrPmComboBox)) {
                        if (lastAmOrPmComboBox.getSelectedIndex() == 0) {
                            selectedEndDate.set(Calendar.AM_PM, Calendar.AM);
                        } else {
                            selectedEndDate.set(Calendar.AM_PM, Calendar.PM);
                        }
                        updateDateLabel();
                    }

                    //FIRST HOUR COMBO BOX
                    if (e.getSource().equals(firstHoursComboBox)) {
                        validateStartDate();
                        updateDateLabel();
                    }

                    if (e.getSource().equals(lastHoursComboBox)) {
                        //set the hour based on the combo box
                        validateEndDate();
                        updateDateLabel();
                    }
                    //RADIO BUTTONS
                    if (e.getSource().equals(aviRadioButton)) {
                        ResourceTreeManager.setToAVI();
                    } else if (e.getSource().equals(mp4RadioButton)) {
                        ResourceTreeManager.setToMP4();
                    } else if (e.getSource().equals(bothRadioButton)) {
                        ResourceTreeManager.setToAVIAndMP4();
                    }

                }
            }
        };
        //checks if the window is minimized or maximized
        addWindowStateListener(new WindowStateListener() {

            @Override
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == ICONIFIED) {
                   minimizeToTray();
                }
                if (e.getNewState() == NORMAL) {
                    tray.remove(trayIcon);
                    setVisible(true);
                }
            }
        });

        //when the X button is pressed, this will minimize to icon tray
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                minimizeToTray();
            }
        });
    }
    
    /**
     * Function to minimize window to system tray.
     */
    private void minimizeToTray(){
        try {
            tray.add(trayIcon);
            setVisible(false);
            if (shouldRun) {
                trayIcon.setToolTip("Background Task Downloader...Running!");
            } else {
                trayIcon.setToolTip("Background Task Downloader...Stopped!");
            }
        } catch (AWTException ex) {
            JOptionPane.showMessageDialog(this, "System tray could not be found.");
        }
    }
    
    
    /**
     * Checks if user wants to close program and closes it if so.
     */
    private void checkToClose(){
        int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION) {
            if (shouldRun) {
                shouldRun = false;
                thread.interrupt();
            }
            LocalFileCleaner.cleanup(appControl, true);
            tray.remove(trayIcon);
            appControl.getGeneralService().getDBMSSystem()
                    .closeDatabaseConnections();
            //Set GeneralWeather.properties to indicate the program is not running.
            PropertyManager.setGeneralProperty("ClientRunning", "false");
            System.exit(0);
        }
    }

    /**
     * Checks all the available resources and creates a new checkbox and adds
     * them to the specified panel. The default resources will be the selected.
     *
     * @param panel the pane to add the new check boxes too.
     * @param resources the resources to add to the panel
     */
    private void initResourceCheckBox(JPanel panel, Vector<Resource> resources) {
        for (final Resource resource : resources) {
            if (ResourceVisibleTester.canUserSeeResource(user, resource)) {
                final JCheckBox checkBox = new JCheckBox(resource.getResourceName());
                checkBox.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (checkBox.isSelected()) {
                            selectedResources.add(resource);
                        } else {
                            selectedResources.remove(resource);
                        }
                    }
                });
                if (PropertyManager.getLocalProperty("DEFAULT_WEATHER_CAMERA").equals(resource.getName())
                        || PropertyManager.getLocalProperty("DEFAULT_WEATHER_MAP_LOOP").equals(resource.getName())
                        || PropertyManager.getLocalProperty("DEFAULT_WEATHER_STATION").equals(resource.getName())) {
                    panel.add(checkBox);
                    checkBox.setSelected(true);
                } else {
                    panel.add(checkBox);
                    checkBox.setSelected(false);
                }
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        movieTypeButtonGroup = new javax.swing.ButtonGroup();
        firstHourLabel = new javax.swing.JLabel();
        firstAmOrPmComboBox = new javax.swing.JComboBox();
        lastHourLabel = new javax.swing.JLabel();
        lastAmOrPmComboBox = new javax.swing.JComboBox();
        neverCheckBox = new javax.swing.JCheckBox();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        descriptionLabel = new javax.swing.JLabel();
        firstHoursComboBox = new javax.swing.JComboBox();
        lastHoursComboBox = new javax.swing.JComboBox();
        firstDateSelectedLabel = new javax.swing.JLabel();
        selectStartDateButton = new javax.swing.JButton();
        selectEndDateButton = new javax.swing.JButton();
        endDateSelectedLabel = new javax.swing.JLabel();
        minimizeToTrayButton = new javax.swing.JButton();
        updateLabel = new javax.swing.JLabel();
        cameraScrollPane = new javax.swing.JScrollPane();
        weatherCamerasPanel = new javax.swing.JPanel();
        mapScrollPane = new javax.swing.JScrollPane();
        mapLoopsPanel = new javax.swing.JPanel();
        weatherStationScrollPane = new javax.swing.JScrollPane();
        weatherStationsPanel = new javax.swing.JPanel();
        aviRadioButton = new javax.swing.JRadioButton();
        mp4RadioButton = new javax.swing.JRadioButton();
        downloadTypeLabel = new javax.swing.JLabel();
        bothRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Background Task Downloader");
        setName("backgroundTaskWindow"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        firstHourLabel.setText("First Hour to Download");
        getContentPane().add(firstHourLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 231, -1, -1));

        firstAmOrPmComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A.M.", "P.M." }));
        getContentPane().add(firstAmOrPmComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(564, 227, -1, -1));

        lastHourLabel.setText("Last Hour to Download");
        getContentPane().add(lastHourLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 268, -1, -1));

        lastAmOrPmComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A.M.", "P.M." }));
        getContentPane().add(lastAmOrPmComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(564, 264, -1, -1));

        neverCheckBox.setText("Never");
        neverCheckBox.setToolTipText("This will allow to run forever until it is stopped manually.");
        neverCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                neverCheckBoxItemStateChanged(evt);
            }
        });
        getContentPane().add(neverCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(718, 264, 61, 25));

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        getContentPane().add(startButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 377, 61, 25));

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        getContentPane().add(stopButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(397, 377, 59, 25));

        exitButton.setText("Terminate Application");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        getContentPane().add(exitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(831, 377, 157, 25));

        descriptionLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        descriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        descriptionLabel.setText("Specify the Resources the Program should Download as a Background Task.   ");
        getContentPane().add(descriptionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 976, 16));

        firstHoursComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00" }));
        getContentPane().add(firstHoursComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(481, 227, 76, -1));

        lastHoursComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00" }));
        getContentPane().add(lastHoursComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(481, 264, 76, -1));

        firstDateSelectedLabel.setText("StartDate");
        getContentPane().add(firstDateSelectedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(787, 231, 201, -1));

        selectStartDateButton.setText("Start Date");
        selectStartDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectStartDateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(selectStartDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(624, 227, 91, -1));

        selectEndDateButton.setText("End Date");
        selectEndDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectEndDateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(selectEndDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(624, 264, 91, -1));

        endDateSelectedLabel.setText("EndDate");
        getContentPane().add(endDateSelectedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(787, 268, 201, -1));

        minimizeToTrayButton.setText("Minimize to Tray");
        minimizeToTrayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeToTrayButtonActionPerformed(evt);
            }
        });
        getContentPane().add(minimizeToTrayButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(831, 340, 157, 25));

        updateLabel.setText("Currently not running.");
        getContentPane().add(updateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(468, 381, 351, 16));

        cameraScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Weather Cameras"));
        cameraScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        weatherCamerasPanel.setLayout(new javax.swing.BoxLayout(weatherCamerasPanel, javax.swing.BoxLayout.Y_AXIS));
        cameraScrollPane.setViewportView(weatherCamerasPanel);

        getContentPane().add(cameraScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 300, 175));
        cameraScrollPane.getAccessibleContext().setAccessibleDescription("");

        mapScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Map Loops"));
        mapScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        mapLoopsPanel.setLayout(new java.awt.GridLayout(5, 0));
        mapScrollPane.setViewportView(mapLoopsPanel);

        getContentPane().add(mapScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 40, 664, 175));

        weatherStationScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Weather Stations"));
        weatherStationScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        weatherStationsPanel.setLayout(new javax.swing.BoxLayout(weatherStationsPanel, javax.swing.BoxLayout.Y_AXIS));
        weatherStationScrollPane.setViewportView(weatherStationsPanel);

        getContentPane().add(weatherStationScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 227, 300, 175));

        movieTypeButtonGroup.add(aviRadioButton);
        aviRadioButton.setText("AVI");
        getContentPane().add(aviRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(481, 301, 49, 25));

        movieTypeButtonGroup.add(mp4RadioButton);
        mp4RadioButton.setText("MP4");
        getContentPane().add(mp4RadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(542, 301, 53, 25));

        downloadTypeLabel.setText("Video Type to Download");
        getContentPane().add(downloadTypeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 305, -1, -1));

        movieTypeButtonGroup.add(bothRadioButton);
        bothRadioButton.setSelected(true);
        bothRadioButton.setText("Both");
        getContentPane().add(bothRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(607, 301, 53, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Stops the thread if it is still running and closes the program.
     *
     * @param evt
     */
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        checkToClose();
    }//GEN-LAST:event_exitButtonActionPerformed

    /**
     * Checks if the system supports a system tray. If a system tray is
     * supported then a system tray icon will be added if minimized with a popup
     * menu. The TrayIcon doesn't support JPopupMenu so it has to use PopupMenu
     * instead.
     */
    private void checkForSystemTray() {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            Image image = IconProperties.getTitleBarIconImage();
            PopupMenu popupMenu = new PopupMenu();
            exitItem = new MenuItem("Exit");
            stopItem = new MenuItem("Stop");
            startItem = new MenuItem("Start");
            openItem = new MenuItem("Open");

            openItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                    setExtendedState(BUJFrame.NORMAL);
                    tray.remove(trayIcon);
                }
            });
            stopItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    stopDownloader();
                }
            });
            startItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    startDownloader();
                }
            });
            exitItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    checkToClose();
                }
            });

            popupMenu.add(openItem);
            popupMenu.addSeparator();
            popupMenu.add(startItem);
            popupMenu.add(stopItem);
            popupMenu.addSeparator();
            popupMenu.add(exitItem);

            trayIcon = new TrayIcon(image, "Background Task Downloader", popupMenu);
            trayIcon.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    tray.remove(trayIcon);
                    setVisible(true);
                    setExtendedState(BUJFrame.NORMAL);
                }
            });
            trayIcon.setImageAutoSize(true);
        }

    }

    /**
     * Starts a new thread and runs it.
     */
    private void startDownloader() {
        shouldRun = true;
        updateLabel.setText("Running...");
        trayIcon.setToolTip("Background Task Downloader...Running!");
        thread = new Thread(this);
        thread.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        startItem.setEnabled(false);
        stopItem.setEnabled(true);
        setDateSelectionEnabled(false);
    }

    /**
     * Stops the thread.
     */
    private void stopDownloader() {
        validateStartDate();
        validateEndDate();
        if (shouldRun) {
            shouldRun = false;
            thread.interrupt();
        }
        updateLabel.setText("Stopped...");
        trayIcon.setToolTip("Background Task Downloader...Stopped!");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        startItem.setEnabled(true);
        stopItem.setEnabled(false);
        setDateSelectionEnabled(true);
    }

    /**
     * Enables / Disables the date selection part of the program. Mainly used to
     * stop the users from changing the times/dates when the thread is running.
     * If the users changed the date when it is running it will screw it up.
     *
     * @param enabled true if the date selection is enabled, false otherwise.
     */
    private void setDateSelectionEnabled(boolean enabled) {
        firstHoursComboBox.setEnabled(enabled);
        firstAmOrPmComboBox.setEnabled(enabled);
        neverCheckBox.setEnabled(enabled);
        selectStartDateButton.setEnabled(enabled);
        aviRadioButton.setEnabled(enabled);
        mp4RadioButton.setEnabled(enabled);
        bothRadioButton.setEnabled(enabled);
        if (!neverCheckBox.isSelected()) {
            lastHoursComboBox.setEnabled(enabled);
            lastAmOrPmComboBox.setEnabled(enabled);
            selectEndDateButton.setEnabled(enabled);
        }
    }

    /**
     * Updates the date selection label with the new dates selected.
     */
    private void updateDateLabel() {
        firstDateSelectedLabel.setText(dateFormatter.format(selectedStartDate.getTime()) + " to ");
        if (!neverCheckBox.isSelected()) {
            endDateSelectedLabel.setText(dateFormatter.format(selectedEndDate.getTime()));
        }
    }

    /**
     * If the selected start date is before the end date, the dates are valid.
     * But if the end dates is before the start date, invalid
     *
     * @return true if valid dates; false otherwise.
     */
    private boolean validDates() {
        if (!neverCheckBox.isSelected()) {
            return selectedStartDate.getTimeInMillis() < selectedEndDate.getTimeInMillis();
        }
        return true;
    }

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        if (validDates()) {
            startDownloader();
        } else {
            JOptionPane.showMessageDialog(this, "Your dates are not correct! Your end date cannot be before your start date!", "Invalid Dates!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        stopDownloader();
    }//GEN-LAST:event_stopButtonActionPerformed

    /**
     * Item listener for the neverCheckBox.
     *
     * @param evt
     */
    private void neverCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_neverCheckBoxItemStateChanged
        if (neverCheckBox.isSelected()) {
            lastAmOrPmComboBox.setEnabled(false);
            lastHoursComboBox.setEnabled(false);
            selectEndDateButton.setEnabled(false);
            updateDateLabel();
            endDateSelectedLabel.setText("Until Stopped");
        } else {
            lastAmOrPmComboBox.setEnabled(true);
            lastHoursComboBox.setEnabled(true);
            selectEndDateButton.setEnabled(true);
            updateDateLabel();
        }

    }//GEN-LAST:event_neverCheckBoxItemStateChanged

    /**
     * Selects the date the user wants the downloader to start at.
     *
     * @param evt
     */
    private void selectStartDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectStartDateButtonActionPerformed
        selectedStartDate = BackgroundDateSelectionWindow.getNewDate(selectedStartDate);
        //if select start date is starts after the selected end date,
        //move the selected end date 1 day ahead of the start date.
        if (selectedEndDate.before(selectedStartDate)) {
            selectedEndDate = (Calendar) selectedStartDate.clone();
            validateEndDate();
        }
        validateStartDate();
        updateDateLabel();
    }//GEN-LAST:event_selectStartDateButtonActionPerformed

    /**
     * Selects the date the users wants the downloader to end at.
     *
     * @param evt
     */
    private void selectEndDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectEndDateButtonActionPerformed
        selectedEndDate = BackgroundDateSelectionWindow.getNewDate(selectedEndDate);
        //if select end date is starts before the selected start date,
        //move the selected start date 1 day back of the end date.
        if (selectedStartDate.after(selectedEndDate)) {
            selectedStartDate = (Calendar) selectedEndDate.clone();
            validateStartDate();
        }
        validateEndDate();
        updateDateLabel();
    }//GEN-LAST:event_selectEndDateButtonActionPerformed

    /**
     * Sets up the start date according to the first hour combo box and the
     * month, day, and year picked.
     */
    private void validateStartDate() {
        int hour = firstHoursComboBox.getSelectedIndex() + 1;
        //hour 12 is represented by a zero, not a 12
        if (hour == 12) {
            hour = 0;
        }
        selectedStartDate.set(Calendar.HOUR, hour);
        if (firstAmOrPmComboBox.getSelectedIndex() == 0) {
            selectedStartDate.set(Calendar.AM_PM, Calendar.AM);
        } else {
            selectedStartDate.set(Calendar.AM_PM, Calendar.PM);
        }
        selectedStartDate.set(Calendar.MINUTE, 0);
        selectedStartDate.set(Calendar.SECOND, 0);
        selectedStartDate.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Sets up the end date according to the last hour combo box and the month,
     * day, and year picked.
     */
    private void validateEndDate() {
        int hour = lastHoursComboBox.getSelectedIndex() + 1;
        if (hour == 12) {
            hour = 0;
        }
        selectedEndDate.set(Calendar.HOUR, hour);
        if (lastAmOrPmComboBox.getSelectedIndex() == 0) {
            selectedEndDate.set(Calendar.AM_PM, Calendar.AM);
        } else {
            selectedEndDate.set(Calendar.AM_PM, Calendar.PM);
        }
        selectedEndDate.set(Calendar.MINUTE, 0);
        selectedEndDate.set(Calendar.SECOND, 0);
        selectedEndDate.set(Calendar.MILLISECOND, 0);
    }

    private void minimizeToTrayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeToTrayButtonActionPerformed
        minimizeToTray();
    }//GEN-LAST:event_minimizeToTrayButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton aviRadioButton;
    private javax.swing.JRadioButton bothRadioButton;
    private javax.swing.JScrollPane cameraScrollPane;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel downloadTypeLabel;
    private javax.swing.JLabel endDateSelectedLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JComboBox firstAmOrPmComboBox;
    private javax.swing.JLabel firstDateSelectedLabel;
    private javax.swing.JLabel firstHourLabel;
    private javax.swing.JComboBox firstHoursComboBox;
    private javax.swing.JComboBox lastAmOrPmComboBox;
    private javax.swing.JLabel lastHourLabel;
    private javax.swing.JComboBox lastHoursComboBox;
    private javax.swing.JPanel mapLoopsPanel;
    private javax.swing.JScrollPane mapScrollPane;
    private javax.swing.JButton minimizeToTrayButton;
    private javax.swing.ButtonGroup movieTypeButtonGroup;
    private javax.swing.JRadioButton mp4RadioButton;
    private javax.swing.JCheckBox neverCheckBox;
    private javax.swing.JButton selectEndDateButton;
    private javax.swing.JButton selectStartDateButton;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JPanel weatherCamerasPanel;
    private javax.swing.JScrollPane weatherStationScrollPane;
    private javax.swing.JPanel weatherStationsPanel;
    // End of variables declaration//GEN-END:variables

}
