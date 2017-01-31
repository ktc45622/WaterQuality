package weather.clientside.gui.client;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import weather.ApplicationControlSystem;
import weather.StorageControlSystem;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.gui.component.BUJFrame;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ScreenInteractionUtility;
import weather.common.utilities.WeatherException;

/**
 * TODO WINDOW IN PROGRESS
 * The <code>SearchResearchDataWindow</code> class creates a form that
 * allows for searching for observational data gathered by weather resources.
 * This window contains four parts. First and second panels are different instances
 * of the same class, one is for camera images and another is for map loop images. 
 * Third panel is for station data and the fourth is a controller which can let 
 * user change settings.

 * @author Ty Vandertappen
 * @author Bingchen Yan
 * @version 2012
 */
public final class SearchResearchDataWindow extends BUJFrame {

    private ApplicationControlSystem appControl;
    private StorageControlSystem storage;
    private ResourceInstancesRequested cameraRequest;
    private ResourceInstancesReturned cameraReturned;
    private ResourceInstancesRequested mapRequest;
    private ResourceInstancesReturned mapReturned;
    private Vector<ResourceInstance> shownCameraImages = new Vector<>();
    private Vector<ResourceInstance> shownMapImages = new Vector<>();
    private Resource camera;
    private Resource mapLoop;
    private Resource station;
    private int max;
    private ResourceRange resourceRange;
    private int ratio, index;
    
    //Fomm components
    private weather.clientside.gui.client.SearchResourceDataPanel cameraPanel;
    private weather.clientside.manager.ResourceSearchController controllerPanel;
    private weather.clientside.gui.client.SearchResourceDataPanel mapPanel;
    private weather.clientside.gui.client.SearchResourceDataPanelWeatherStation stationPanel;
    
    //These variables are used for window sizing
    private int interiorHeight;
    private int interiorWidth;
    private int panelWidth;         //For all 4 panels.
    private int panelHeight;        //For the top 3 panels.
    private int controlPanelHeight; //For botton panel.
    private int marginSize;
    private int vGapSize;
    private int mapPanelY;
    private int stationPanelY;
    private int controlPanelY;
    
    /**
     * Replaces initComponants with custom sizing.
     */
    private void initComponentsOnThisScreen() {
        cameraPanel = new weather.clientside.gui.client.SearchResourceDataPanel(shownCameraImages, this, camera, panelWidth, panelHeight);
        mapPanel = new weather.clientside.gui.client.SearchResourceDataPanel(shownMapImages, this, mapLoop, panelWidth, panelHeight);
        stationPanel = new weather.clientside.gui.client.SearchResourceDataPanelWeatherStation(panelWidth, panelHeight);
        controllerPanel = new weather.clientside.manager.ResourceSearchController(appControl, camera, mapLoop, station, max, this, resourceRange);
        
        setTitle("Weather Viewer - Search Observational Data");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        
        //Place panels on sceen.
        getContentPane().add(cameraPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, marginSize, panelWidth, panelHeight));
        getContentPane().add(mapPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, mapPanelY, panelWidth, panelHeight));
        getContentPane().add(stationPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, stationPanelY, panelWidth, panelHeight));
        getContentPane().add(controllerPanel,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(marginSize, controlPanelY, panelWidth, controlPanelHeight));

        pack();
    }
    
    /**
     * Computes parameters for the layout.
     */
    private void computeCoordinates(){
        Dimension screenRes = ScreenInteractionUtility
                .getCurrentScreenResolution();
        Debug.println("\nRESOLUSION: " + screenRes.width + " X " + screenRes.height);
        int horizontalInsets = getInsets().left + getInsets().right;
        //Calculate widest width so that window fits on screen.
        /**
         * The below code is based on maintaining a constant aspect ratio
         * regardless of the aspect ratio of the screen. The window can use the
         * entire width of the screen if the aspect ratio is not greater than
         * 8/5. Otherwise, given that the aspect ratio of the window is
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
        Debug.println("interiorWidth = " + interiorWidth);
        
        //The decimel maintains the aspect ratio.
        interiorHeight = Math.round((float) (.5469314 * interiorWidth));
        Debug.println("interiorHeight = " + interiorHeight);
        
        //Set gap sizes.
        vGapSize = 6;   //Space between panels.
        marginSize = 12;
        
        //Set controller height.
        controlPanelHeight = 75;
        
        //Set panel sizes.
        panelWidth = interiorWidth - 2 * marginSize;
        //Total vertical room for panels needs 2 margins and 3 gaps.
        int totalPanelVertical = interiorHeight - 2 * marginSize - 3 * vGapSize;
        //controller already sized.
        totalPanelVertical -= controlPanelHeight;
        //split remaining height for 3 top panels.
        panelHeight = Math.round((float)(.3333333 * totalPanelVertical));
        
        //Compute panel Y values.
        mapPanelY = marginSize + panelHeight + vGapSize;
        stationPanelY = mapPanelY + panelHeight + vGapSize;
        controlPanelY = stationPanelY + panelHeight + vGapSize; 
    }
    
    /** 
     * Creates new form searchObservationalDataDialog.
     * @param appControl The application control system.
     * @param camera The current camera resource.
     * @param mapLoop The current map resource.
     * @param station The current station resource.
     * @param max The current number of image per hour.
     * @param resourceRange The current resource range.
     * @throws weather.common.utilities.WeatherException
     */
    public SearchResearchDataWindow(ApplicationControlSystem appControl, 
            Resource camera, Resource mapLoop, Resource station, int max, 
            ResourceRange resourceRange) throws WeatherException{
        super();
        this.appControl = appControl;
        storage = StorageControlSystemImpl.getStorageSystem();
        this.resourceRange = resourceRange;
        this.camera = camera;
        this.mapLoop = mapLoop;
        this.station = station;
        this.max = max;
        this.index = 1;
        
       //Prepare to initailize componants. 
        pack(); //Needed for Insets.  They are 0 before.
        computeCoordinates();
        
        //Initialize data and componants.
        initializeInstances();
        initComponentsOnThisScreen();
        updateLabel();
        stationPanel.createPlot(station, resourceRange);
        
        //Size screen.
        Dimension thisDim;
        int externalHeight = interiorHeight + getInsets().top + getInsets().bottom;
        int externalWidth = interiorWidth + getInsets().left + getInsets().right;
        thisDim = new Dimension(externalWidth, externalHeight);

        this.setSize(thisDim);
        this.setPreferredSize(thisDim);
        this.setMaximumSize(thisDim);
        this.setMinimumSize(thisDim);
        pack();
        
        super.postInitialize(false);
        //The memory is cleared when the window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                cleanUp();
                dispose();
            } 
        });
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        toFront();
                    }
                });
    }
    
    /**
     * Initializes the instances. All the images are retrieved from the storage system
     */
    private void initializeInstances() throws WeatherException {
        //Thid calendar holds the beginning of the hour for which to load data,
        Calendar currentHour = new GregorianCalendar();
        currentHour.setTime(resourceRange.getStartTime());
        //Store end point of retrieval range.
        Calendar lastHour = new GregorianCalendar();
        lastHour.setTime(resourceRange.getStopTime());
        
        //Must count the hours in the range.
        int hourCount = 0;
        while (currentHour.getTimeInMillis() < lastHour.getTimeInMillis()) {
            hourCount++;
            //Set next loop text and loop.
            currentHour.add(Calendar.HOUR, 1);
        }
        
        //Get camera images for these hours.
        cameraRequest = new ResourceInstancesRequested(resourceRange, max * hourCount,
                false, camera.getFormat(), camera);
        cameraReturned = storage.getResourceInstances(cameraRequest);
        shownCameraImages = cameraReturned.getResourceInstances();

        //Get map images for this hours.
        mapRequest = new ResourceInstancesRequested(resourceRange, max * hourCount,
                false, mapLoop.getFormat(), mapLoop);
        mapReturned = storage.getResourceInstances(mapRequest);
        shownMapImages = mapReturned.getResourceInstances();
        
        try {
            ratio = shownCameraImages.size() / shownMapImages.size();
        } catch (ArithmeticException e) {
            ratio = Integer.MAX_VALUE;
        }
    }
    
    /**
     * update all the labels.
     */
    public void updateLabel(){
        cameraPanel.setTitle(camera.getResourceName());
        mapPanel.setTitle(mapLoop.getResourceName());
    }
    
    /**
     * clean up.
     */
    public void cleanUp(){
        cameraRequest = null;
        cameraReturned = null;
        mapRequest = null;
        mapReturned = null;
        shownCameraImages.clear();
        shownMapImages.clear();
        cameraPanel.cleanUp();
        mapPanel.cleanUp();
    }
    
    /**
     * load data. The memory is cleared when the data is loaded.
     */
    public void loadData() throws WeatherException{
        cleanUp();
        updateLabel();
        initializeInstances();
        cameraPanel.setData(shownCameraImages);
        mapPanel.setData(shownMapImages);
        cameraPanel.loadData();
        mapPanel.loadData();
        stationPanel.createPlot(station, resourceRange);
    }
    
    /**
     * show next set of images. Relates the first and second panel
     */
    public void nextSet(){
        //Because the number of camera images is different from the number of map 
        //loop’s, we need to relate them when these buttons are clicked. 
        //To do this, we calculate a ratio of the size of two set of images. 
        //The index starts with 0, if the ratio larger than 0, we show next set 
        //of camera images, if index modulus ration is equal to 0, we show next 
        //set of map loop images, the index is increased by 1 at last.

        cameraPanel.nextSet();
        try{
            if((index+1) % ratio == 0) {
                mapPanel.nextSet();
            }
        }catch(ArithmeticException e){
            mapPanel.nextSet();
        }
        index++;
    }
    
    /**
     * show pervious set of images. Relates the first and second panel
     */
    public void previousSet(){
        cameraPanel.previousSet();
        try{
            if((index+1) % ratio == 0) {
                mapPanel.previousSet();
            }
        }catch(ArithmeticException e){
            mapPanel.previousSet();
        }
        index++;
    }
    
    /**
     * set index number.
     * @param m new value of max.
     */
    public void setMax(int m){
        this.max = m;
    }
    
    /**
     * set index number.
     * @param i value of index.
     */
    public void setIndex(int i){
        this.index = i;
    }
    
    /**
     * set current Camera.
     * @param c value of camera.
     */
    public void setCamera(Resource c){
        this.camera = c;
    }
    
    /**
     * set current map.
     * @param m value of mapLoop.
     */
    public void setMap(Resource m){
        this.mapLoop = m;
    }
    
    /**
     * set current station.
     * @param s value of mapLoop.
     */
    public void setStation(Resource s){
        this.station = s;
    }
    
    /**
     * set current resource range
     * @param r value of resourceRange.
     */
    public void setRange(ResourceRange r){
        this.resourceRange = r;
    }

    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setTitle("Weather Viewer - Search Observational Data");
        setMinimumSize(new java.awt.Dimension(1024, 745));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void start() {
        cameraPanel.start();
        mapPanel.start();
    }

    public void last() {
        cameraPanel.last();
        mapPanel.last();
    }

    public void setPosition(int position) {
        if(position < 1 || position > shownCameraImages.size()) {
            JOptionPane.showMessageDialog(this, "Please input a number between 1 and " + shownCameraImages.size(),
                                "Message", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            cameraPanel.setPosition(position);
            float temp = (float)position/(float)shownCameraImages.size();
            position = (int)(temp * shownMapImages.size());
            mapPanel.setPosition(position);
        }
    }
}
