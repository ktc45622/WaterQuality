package weather.clientside.manager;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.*;
import weather.ApplicationControlSystem;
import weather.clientside.gui.client.*;
import weather.clientside.gui.component.GUIComponentFactory;
import weather.clientside.gui.component.ResourceListCellItem;
import weather.clientside.utilities.SnapShotViewer;
import weather.clientside.utilities.TimedLoader;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.data.weatherstation.WeatherStationTwoVariablesProperties;
import weather.common.data.weatherstation.WeatherStationVariableProperties;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.gui.component.IconProperties;
import weather.common.utilities.*;

/**
 * This class is essentially the entire bottom right panel in the main window.
 * It represents the wrapper for the majority of our application logic. It
 * contains the logic for changing dates and changing fits.  This class spans 
 * the gap between when and what to plot and the actual plotting itself.
 *
 * @author Joe Sharp (2009)
 * @author Xianrui Meng (2010)
 * @author Software Engineering 2010 Spring
 */
public final class WeatherStationPanelManager extends javax.swing.JPanel {
    //The last of the below options is for exteral windows only
    private static final String[] DAY_SPAN_OPTIONS = {"35 Days", "7 Days", 
        "3 Days", "Current Day", "Manual", "1 Day"};
    private ResourceRange resourceRange,    //Actual range reguested by user
            lastManualRange,    //Most resent range requested via MovieContoller
            plottedRange;   //Entire range shown on graph
    private boolean listsInitialized;
    private static ApplicationControlSystem appControl;
    private MainApplicationWindow mainWin;
    private PlotData plotData;
    private WeatherDataPlotPanel plotPanel;
    private JPanel noStationPanel;
    private JLabel noStationLabel;
    private final Vector<Resource> stationResources;
    private Resource currentResource;
    private WeatherStationVariableProperties wvProps;
    private WeatherStationTwoVariablesProperties twoVariablesProperties;
    private boolean dayBoxInitialized = false;
    private boolean panelInitialized = false;
    private final ArrayList<String> shownVariables;
    private final ArrayList<String> allVariables;
    private ButtonGroup varRadioButtons;  //The grouping of radio buttons
    private boolean isShown;
    private final int COMBOBOX_MAX_ROW_COUNT = 20;
    public static boolean isNoneStation;
    private boolean isExternal = false; //Must be changed if external!
    //Specifies if the list of stations was refreshed externally via the 
    //refreshStationList method.
    private static boolean refreshedStationList = false;
    //Specifies whether or not a change in the movie controller range changes
    //the range of the plot.
    private boolean changeRangeWithController = true;

    /**
     * Initialize all the attributes in this panel manager.
     *
     * @param appControl An ApplicationControlSystem.
     * @param range A ResourceRange.
     * @param initialResource The initial resource.
     * @param mainWin The application's main window.
     */
    public WeatherStationPanelManager(ApplicationControlSystem appControl,
            ResourceRange range, Resource initialResource,
            MainApplicationWindow mainWin) {
        WeatherStationPanelManager.appControl = appControl;
        this.mainWin = mainWin;
        resourceRange = range;
        lastManualRange = range;
        currentResource = initialResource;
        shownVariables = new ArrayList<>();
        allVariables = new ArrayList<>();
        isNoneStation = true; //assume we don't have a station then set it to
        //true when we know we have one. 

        /**
         * @TODO These next lines contain hard coded properties files. When we
         * make it possible for users to create their own WeatherStations, we
         * need to get these values from the database to correspond to the
         * WeatherStation that the user chooses. They will also need updated
         * whenever the user changes the WeatherStation resource.
         */
        if (initialResource != null && WeatherUndergroundInstance.isWeatherUndergroundInstance(initialResource)) {
            wvProps = new WeatherStationVariableProperties();
            twoVariablesProperties =
                    new WeatherStationTwoVariablesProperties(
                    PropertyManager.getGeneralProperty("WEATHER_STATION_PATH")
                    + PropertyManager.getGeneralProperty("WUndergroundTwoVariables"));
        } else { //Default to  noSolar version -- only taking from weather underground now
            wvProps =
                    new WeatherStationVariableProperties();
            twoVariablesProperties =
                    new WeatherStationTwoVariablesProperties(
                    PropertyManager.getGeneralProperty("WEATHER_STATION_PATH")
                    + PropertyManager.getGeneralProperty("WUndergroundTwoVariables"));
        }
        stationResources = new Vector<>();
        initComponents();
        listsInitialized = false;
        initializePlotPanel();
        initializeLists();
        setDaySpanItems(smallestFitForRange(range) == 1);
        initPanes();
        stationComboBox.setMaximumRowCount(COMBOBOX_MAX_ROW_COUNT);
        if (!stationComboBox.getSelectedItem().equals("None")) {
            snapshotButton.setEnabled(true);
            printButton.setEnabled(true);
        }
        panelInitialized = true;
    }

    //THESE TWO FUNCTIONS ARE FOR DEBUGING
    private void debugDate(Date date, String name) {
        String dateFormat = "MM/dd/yy hh:mm:ss a z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(this.getResourceTimeZone());
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
     * Configure this object for an external window.
     */
    public void setToExternal() {
        isExternal = true;
        externalStationButton.setEnabled(false);
        linkCheckBox.setVisible(false);
        linkCheckBox.setSelected(false);
        changeRangeWithController = false;
    }

    /**
     * Returns whether or not the controller of the attached main window should
     * change the plotted range.
     * @return  whether or not the controller of the attached main window should
     * change the plotted range.
     */
    public boolean shouldBeChangedWithController() {
        return changeRangeWithController;
    }
    
    /**
     * Gets the resourceRange the manual option will go to.
     * @return The manual range.
     */
    public ResourceRange getLastManualRange() {
        return lastManualRange;
    }
    
    /**
     * Sets the ResourceRange the manual option will go to.
     * @param range The manual range.
     */
    public void setLastManualRange(ResourceRange range) {
        lastManualRange = range;
    }
    
     /**
     * Gets the current resource range.
     * @return The current resource range.
     */
    public ResourceRange getResourceRange() {
        return resourceRange;
    }
    
    /**
     * This function determines the text that should currently be shown on the
     * noStationLabel object.
     * @return The text that should currently be shown on the noStationLabel
     * object.
     */
    private String getNoDataText() {
        String notLinked = "<html><center>No weather station linked to the "
                + "current weather camera.</center></html>";
        String noCameraSelected = "<html><center>No weather camera selected."
                + "</center></html>";
        String noStationSelected = "<html><center>No weather station selected."
                + "</center></html>";
        if(mainWin.getMovieController() == null) {
            return "Loading...";    // The form is loading.
        } else if (isExternal) {
            return noStationSelected;
        } else if (!mainWin.getMovieController()
                .doesPrimaryPanelHaveValidResource()) {
            return noCameraSelected;
        } else if (!mainWin.getMovieController()
                .doesPrimaryPanelHaveRelatedResource()) {
            return notLinked;
        } else {
            return noStationSelected;
        }
    }
    
    /**
     * Sets the current resource range.
     * @param range The range to be shown.
     */
    public void setResourceRange(ResourceRange range) {
        resourceRange = range;
        setCenteredResourceRange();
        if (isShown) {
            showGraph();
        }
    }
    
    /**
     * This function returns the text of the radio button that specifies the
     * manager's current graph.  It should not be called if no button is
     * selected.
     * @return The text of the radio button that specifies the manager's current
     * graph.
     */
    public String getSelectedRadioText() {
        Enumeration<AbstractButton> buttonList = varRadioButtons.getElements();
        while(buttonList.hasMoreElements()) {
            AbstractButton thisButton = buttonList.nextElement();
            if(thisButton.isSelected()) {
                return thisButton.getText();
            }
        }
        
        //Code should not get here, but this line needed to compile.
        return null;
    }
    
    /**
     * This function sets the manager's current graph by selecting a single- or
     * multi-variable plot by specifying the text of the radio button that the
     * user would click to see the plot.  The radio button is selected.
     * @param radioText The text of the button to select.  (The program will 
     * error if it is not a valid button text.)
     */
    public void setGraphByRadioText(String radioText) {
        Enumeration<AbstractButton> buttonList = varRadioButtons.getElements();
        while (buttonList.hasMoreElements()) {
            AbstractButton thisButton = buttonList.nextElement();
            if (thisButton.getText().equals(radioText)) {
                thisButton.setSelected(true);
                return;
            }
        }
    }
    
    /**
     * Returns whether or not the manager's current graph is fitted.
     * @return True if the manager's current graph is fitted, false otherwise.
     */
    public boolean isGraphFitted() {
        return fittedYAxisRadioButton.isSelected();
    }
    
    /**
     * Sets the state of the manager's current graph to either fitted or 
     * default.
     * @param isFitted True if the graph is to be fitted; false if the default
     * setting is to be applied.
     */
    public void setGraphFittedState(boolean isFitted) {
        if (!isFitted) {
            defaultYAxisRadioButton.setSelected(true);
        } else {
            fittedYAxisRadioButton.setSelected(true);
        }
    }
    
    /**
     * Returns the number of days on the manager's current graph.
     * @return The number of days on the manager's current graph.
     */
    public int getDaysShown() {
        return plotPanel.getDays();
    }
    
    /**
     * This function is used to set the number of days shown be external
     * instances of this class.  If had no effect if the instance is not 
     * external.
     * @param days The number of days to be shown. 
     */
    public void setDaysShownForExternalManger(int days) {
        //Make sure this manager is external
        if(!isExternal) {
            return;
        }
        
        //Assume 1 day. NOTE: making this line always execute ensurss that
        //daySpanComboBox will not be inconsistant with the graph.
        daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[5]);
        
        if (days == 35) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[0]);
        } else if (days == 7) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[1]);
        } else if (days == 3) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[2]);
        }
    }
    
    /**
     * Returns an index specifying an element in a private class array the that
     * corresponds to the selected element of the day span selection box.
     * @return An integer that corresponds to the selected element of the day
     * span selection box.
     */
    public int getDaySpanOptionByIndex() {
        for (int i = 0; 1 < DAY_SPAN_OPTIONS.length; i++) {
            if (daySpanComboBox.getSelectedItem().equals(DAY_SPAN_OPTIONS[i])) {
                return i;
            }
        }
        
        //Code should not get here, but this line needed to compile.
        return -1;
    }
    
    /**
     * Sets the day span combo box to one of its possible options based on the 
     * supplied integer.  That integer corresponds to the option's place in the
     * private class array referenced by getDaySpanOptionByIndex().  
     * @param arrayIndex The supplied integer.
     */
    public void setDaySpanOptionByIndex(int arrayIndex) {
        daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[arrayIndex]);
    }
    
    /**
     * Returns the selected index of the manager's tabbed pane.
     * @return The selected index of the manager's tabbed pane.
     */
    public int getTabIndex() {
        return stationTabbedPane.getSelectedIndex();
    }
    
    /**
     * Sets the selected index of the manager's tabbed pane.
     * @param index The index to be selected.
     */
    public void setTabIndex(int index) {
        stationTabbedPane.setSelectedIndex(index);
    }
    
    /**
     * Returns whether or not the "Link" check box is selected.
     * @return True if the "Link"  check box is selected; false otherwise.
     */
    public boolean isLiskCheckBoxSelected() {
        return linkCheckBox.isSelected();
    }
    
    /**
     * Sets the state of the "Link" check box.
     * @param isSelected True if the "Link" check box should be selected; False
     * if it should be deselected.
     */
    public void setLinkCheckBoxState(boolean isSelected) {
        linkCheckBox.setSelected(isSelected);
    }

    /**
     * Initializes the WeatherStation Variable. Add all the resource to the
     * list.
     */
    private void initializeLists() {
        GUIComponentFactory.initWeatherStationComboBox(stationComboBox, 
                appControl.getGeneralService(), true);

        if (currentResource != null) {
            stationComboBox.setSelectedItem(new ResourceListCellItem(currentResource.getResourceName()));
            isNoneStation = false;
        } else {
            stationComboBox.setSelectedItem(new ResourceListCellItem("None"));
        }

        listsInitialized = true;
    }

    /**
     * Refreshes the combo box which stores a list of all of the weather station
     * resources.
     */
    public void refreshStationList() {
        refreshedStationList = true;
        //Calls the item change listener, so we want refreshedStationList = true.
        stationComboBox.removeAllItems();
        initializeLists();
        refreshedStationList = false;
    }

    /**
     * Gets the time zone in which to show the plot.
     * @return The time zone of the current resource or the local time zone if
     * the current resource is null.
     */
    private TimeZone getResourceTimeZone() {
        if (currentResource == null) {
            return TimeZone.getDefault();
        } else {
            return currentResource.getTimeZone().getTimeZone();
        }
    }
    
    /**
     * Creates the plot panel and initializes the plot data.
     */
    private void initializePlotPanel() {
        plotData = new PlotData(currentResource);
        setCenteredResourceRange();

        if (PropertyManager.ChartType.valueOf(PropertyManager.getGeneralProperty("CHART_TYPE"))
                .equals(PropertyManager.ChartType.J_FREE_CHART)) {
            plotPanel = new JFreeChartPlotPanel(plotData);
        } else {
            plotPanel = new JFreeChartPlotPanel(plotData); // will change when we have new charting software
            WeatherLogger.log(Level.WARNING, "Attempted to use an unsupported CHART_TYPE.\n"
                    + "JFreeChart is being used instead.");
            new WeatherException("Attempted to use an unsupported CHART_TYPE.\n"
                    + "JFreeChart is being used instead.").show();
        }
        plotPanel.setDays(smallestFitForRange(resourceRange));
        //Changes how the range is set on the panel.
        plotPanel.setRange(false);
        //plotPanel.setRange(true);

        dataPlotTab.setLayout(new BorderLayout());

        // Create the panel for when no weather station is selected.
        noStationPanel = new JPanel(new BorderLayout());
        noStationLabel = new JLabel("", JLabel.CENTER); //Text added later.
        noStationPanel.add(noStationLabel, BorderLayout.CENTER);
        dataPlotTab.add(noStationPanel, BorderLayout.CENTER);

        showGraph();
    }

    /**
     * Sets the resource range to be centered on the domain.
     */
    private void setCenteredResourceRange() {
        //Get days to be plotted.
        int daysToBePlotted;
        if (plotPanel != null) {
            daysToBePlotted =  plotPanel.getDays();
        } else {
            daysToBePlotted = smallestFitForRange(resourceRange);
        }
        Debug.println("Days to be plotted: " + daysToBePlotted);
        
        //Debug and declare variables.
        Debug.println("Range to be cantered:");
        debugResourceRange(resourceRange);
        long domainStartTime;
        long domainEndTime;
        
        //Domain is initially all for days needed to hold the requested range.
        domainStartTime = ResourceTimeManager.
                getStartOfDayFromMilliseconds(resourceRange.getStartTime()
                .getTime(), this.getResourceTimeZone());
        domainEndTime = ResourceTimeManager.
                getEndOfDayFromMilliseconds(resourceRange.getStopTime()
                .getTime() - 1, this.getResourceTimeZone());
        
        //Find the mumber of days the range now covers
        int daysToBeShown = (int)((domainEndTime - domainStartTime + 1)
                / ResourceTimeManager.MILLISECONDS_PER_DAY);
        
        //Handle day daylight savings time begins.
        if((domainEndTime - domainStartTime + 1) 
                % ResourceTimeManager.MILLISECONDS_PER_DAY
                > ResourceTimeManager.MILLISECONDS_PER_HOUR) {
            daysToBeShown++;
        }
        
        Debug.println("Days to be shown: " + daysToBeShown);
            
       //If there ara fewer days in range than be shown, add days until there aren't.
        while (daysToBeShown < daysToBePlotted) {
            //Add to end first.
            domainEndTime +=  ResourceTimeManager.MILLISECONDS_PER_DAY;
            daysToBeShown++;
            //Add to beginning if shown days is still less.
            if (daysToBeShown < daysToBePlotted) {
                domainStartTime -= ResourceTimeManager.MILLISECONDS_PER_DAY;
                daysToBeShown++;
            }
        }
        
        //Take end off range if it is too long to be shown.
        if (daysToBeShown > daysToBePlotted) {
            int daysToRemove = daysToBeShown - daysToBePlotted;
            domainEndTime -= ResourceTimeManager.MILLISECONDS_PER_DAY
                    * daysToRemove;
        }
        
        plottedRange = new ResourceRange(new java.sql.Date(domainStartTime),
                new java.sql.Date(domainEndTime));
        Debug.println("plotted range:");
        debugResourceRange(plottedRange);
        Debug.println("Range to be cantered:");
        debugResourceRange(resourceRange);

        plotData.setResourceRange(plottedRange);
    }

    /**
     * Determines smallest fit for given resource range.
     *
     * @return The smallest number of days from 1, 3, 7, or 35 that will fit the
     * current range or 35 if none will.
     * @param range the range
     */
    public int smallestFitForRange(ResourceRange range) {
        if (ResourceTimeManager.getEndOfDayFromMilliseconds(range.getStopTime().getTime() - 1,
                this.getResourceTimeZone())
                - ResourceTimeManager.getStartOfDayFromMilliseconds(range.getStartTime().getTime(),
                this.getResourceTimeZone())
                > 7 * ResourceTimeManager.MILLISECONDS_PER_DAY) {
            return 35;
        } else if (ResourceTimeManager.getEndOfDayFromMilliseconds(range.getStopTime().getTime() - 1,
                this.getResourceTimeZone()) - 
                ResourceTimeManager.getStartOfDayFromMilliseconds(range.getStartTime().getTime(),
                this.getResourceTimeZone())
                > 3 * ResourceTimeManager.MILLISECONDS_PER_DAY) {
            return 7;
        } else if (ResourceTimeManager.getStartOfDayFromMilliseconds(range.getStartTime().getTime(),
                this.getResourceTimeZone())
                < ResourceTimeManager.getStartOfDayFromMilliseconds(range.getStopTime().getTime() - 1,
                this.getResourceTimeZone())) {
            return 3;
        } else {
            return 1;
        }
    }

    /**
     * Sets number of days displayed on panel.
     * @param days # of days - Must be 1, 3, 7 or 35.
     */
    public void setDays(int days) {
        plotPanel.setDays(days);
    }

    /**
     * Sets the available options for the ComboBox.
     * @param hasCurrentDay Tells if "Current Day" will be added.
     */
    public void setDaySpanItems(boolean hasCurrentDay) {
        dayBoxInitialized = false;
        daySpanComboBox.removeAllItems();
        
        //Always add first 3 options.
        for (int i = 0; i < 3; i++) {
            daySpanComboBox.addItem(DAY_SPAN_OPTIONS[i]);
        }
        
        //Determine if a single-day option should be added.
        if (this.isExternal) {
            daySpanComboBox.addItem(DAY_SPAN_OPTIONS[5]);
        } else if (hasCurrentDay) {
            daySpanComboBox.addItem(DAY_SPAN_OPTIONS[3]);
        }

        //Add "Manual" if linked to a controller.
        if (this.changeRangeWithController) {
            daySpanComboBox.addItem(DAY_SPAN_OPTIONS[4]);
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[4]);
        }
        dayBoxInitialized = true;
    }

    /**
     * This method initializes the singleVarPanel variable.
     */
    private void initSingleVarPanel() {
        singleVarPanel.removeAll();
        varRadioButtons = new ButtonGroup();
        //Vector<String> orderedVariableKeys = wvProp.getOrderedVariableKeys();
        Vector<String> ordering = wvProps.getOrdering();
        int numberOfVariableKeys = ordering.size();


        // If the list gets too big, the number shown may be reduced.
        if (numberOfVariableKeys > 12) {
            singleVarPanel.setLayout(new GridLayout(10, 2));
        } else {
            singleVarPanel.setLayout(new GridLayout(numberOfVariableKeys, 1));
        }
        // For common plots tab

        // Display the variables.
        for (int i = 0; i < numberOfVariableKeys; ++i) {
            //String variableKey : wvProp.getOrderedVariableKeys()) {

            String variableKey = ordering.get(i);
            //Create individual JRadioButtons for each variable, store them in a Vector
            //and display them in the JTabbedPane on the "Common Variables" tab.
            JRadioButton varRadioButton =
                    new JRadioButton(wvProps.getDisplayName(variableKey));
            varRadioButton.setName(variableKey);

            varRadioButtons.add(varRadioButton);
            singleVarPanel.add(varRadioButton);

            allVariables.add(variableKey);

            if (variableKey.contains(PropertyManager
                    .getLocalProperty("INITIAL_PLOT_DATA_TRACE"))) {
                plotData.setShown(variableKey, true);
                plotData.addSelectedVariableKey(variableKey);
                shownVariables.add(variableKey);
                varRadioButton.setSelected(true);
            }
            //Add a common plot item listener
            varRadioButton.addItemListener(getCommonPlotItemListener());
        }

    }

    /**
     * This method initializes the multiVarPanel variable.
     */
    private void initMultiVarPanel() {
        multiVarPanel.removeAll();
        // Go through all the common groups
        Vector<String> orderedCommonKeys = twoVariablesProperties.getOrderedCommonKeys();
        multiVarPanel.setLayout(new GridLayout(orderedCommonKeys.size(), 1));

        JRadioButton multiVarRadioButton;
        for (int i = 0; i < orderedCommonKeys.size(); i++) {//String variableKey : wvProp.getOrderedVariableKeys()) {
            final String commonKey = orderedCommonKeys.get(i);
            if (twoVariablesProperties.isUsed(commonKey)) {
                //Create individual radio buttons for each group, store them in a Vector
                //and display them in the JTabbedPane on the "Multi-Variable Plots" tab.
                multiVarRadioButton =
                        new JRadioButton("" + twoVariablesProperties.getLongName(commonKey));
                multiVarRadioButton.setName(commonKey);
                //Store all the variableKeys in the variableKeys list.
                varRadioButtons.add(multiVarRadioButton);
                multiVarPanel.add(multiVarRadioButton);
                allVariables.add(commonKey);

                multiVarRadioButton.addItemListener(getMultiVarPlotItemListener());
            }
        }
        // End common checkboxes
    }
    
    /**
     * This method is necessary to ensure the manager is configured properly 
     * when the main window opens.  It should only be called by the constructor
     * of that form after the other initialization is complete.
     */
    public void configureForMainWindow() {
        if (isNoneStation) {
            // Update "None" panel message.
            setShown(false);
        }
    }

    /**
     * This method initializes the dataPlotTab.
     */
    private void initDataPlotTab() {
        dataPlotTab.add((JPanel) plotPanel, BorderLayout.CENTER);

        // Show the data plot only if we have a station loaded.
        if (isNoneStation) {
            setShown(false);
        } else {
            setShown(true);
        }
    }

    /**
     * Initializes the data plot pane, common Plots pane, and multi-variable
     * plots pane.
     */
    private void initPanes() {

        initSingleVarPanel();

        initMultiVarPanel();

        initDataPlotTab();
    }

    /**
     * Refreshes the plot at the given time.
     * @param timeToShow The given time.
     */
    public void refreshPlot(long timeToShow) {
        //Sets this data plot's current time.
        plotData.setCurrentTimeShown(timeToShow);
        if (isShown) {
            plotPanel.refreshPlot(true, this.getResourceTimeZone());
        }
    }

    /**
     * Refresh the data plot.
     */
    public void refreshPlot() {
        if (isShown) {
            plotPanel.refreshPlot(true, this.getResourceTimeZone());
        }
    }

    /**
     * Refresh only the current time of the data plot. Nothing else is refreshed
     * but the "time line".
     * @param timeToShow The given time.
     */
    public void refreshPlotTime(long timeToShow) {
        //Sets this data plot's current time.
        plotData.setCurrentTimeShown(timeToShow);
        if (isShown) {
            plotPanel.refreshPlot(false, null);
        }
    }

    /**
     * Shows or hides the plot.
     * @param shown Is true if shown, false if not.
     */
    public void setShown(boolean shown) {
        this.isShown = shown;

        // Add the correct panel depending on visibility of the plot.
        if (shown) {
            dataPlotTab.removeAll();
            dataPlotTab.add((JPanel) plotPanel, BorderLayout.CENTER);
        } else {
            dataPlotTab.removeAll();
            noStationLabel.setText(getNoDataText());
            dataPlotTab.add(noStationPanel, BorderLayout.CENTER);
        }

        // Prevents incorrect displaying.
        dataPlotTab.repaint();
    }

    /**
     * Returns the station panel.
     * @return A weather station panel.
     */
    public JPanel getWeatherStationPanel() {
        return stationPanel;
    }

    /**
     * Returns the selected resource from the station combobox.
     * @return The selected resources in the ComboBox.
     */
    public Resource getSelectedResource() {
        if (stationResources.size() > 0) {
            if (stationComboBox.getSelectedIndex() > 0) {
                return stationResources.get(stationComboBox.getSelectedIndex());
            }
        }
        return null;
    }

    /**
     * Returns the data for the plot.
     * @return The plot data.
     */
    public PlotData getPlotData() {
        return plotData;
    }

    /**
     * Returns the current resource that is selected.
     * @return The current resource.
     */
    public Resource getResource() {
        return currentResource;
    }

    /**
     * Finds the resource that corresponds to the given name.
     * @param name The name of the resource to find.
     * @return The resource that corresponds to the given name. Or null if no
     * resource is found.
     */
    private Resource getWeatherStationResourceForName(String name) {
        Resource defaultResource = null;
        for (Resource resource : appControl.getGeneralService().getWeatherStationResources()) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        return defaultResource;
    }

    /**
     * An itemListener for the common plot tab. It determines which variable to
     * show in the plot panel according to the RadioButton that has been
     * selected by the user.
     *
     * @return An ItemListener that determine whether the item in the
     * RadioButton has been changed.
     */
    private ItemListener getCommonPlotItemListener() {
        ItemListener listener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JRadioButton button = (JRadioButton) e.getSource();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotData.setShown(button.getName(), true);
                    //add the selected variableKey to the chart.
                    plotData.addSelectedVariableKey(button.getName());
                    shownVariables.add(button.getName());
                } else {
                    plotData.setShown(button.getName(), false);
                    shownVariables.remove(button.getName());
                }
                plotPanel.refreshPlot(true, getResourceTimeZone());
            }
        };
        return listener;
    }

    /**
     * An itemListener for the multi-variable plot tab. It determines which pair
     * of variables to show in the plot panel according to the RadioButton that
     * has been selected by the user.
     * @return An ItemListener that determine whether the item in the
     * RadioButton has been changed.
     */
    private ItemListener getMultiVarPlotItemListener() {
        ItemListener listener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JRadioButton button = (JRadioButton) e.getSource();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotData.setShown(twoVariablesProperties.getFirstName(button.getName()), true);
                    plotData.setShown(twoVariablesProperties.getSecondName(button.getName()), true);
                    //To store the variables keys in the plot, and display in the chart.
                    plotData.addMultiSelectedVariableKeys(twoVariablesProperties.getFirstName(button.getName()),
                            twoVariablesProperties.getSecondName(button.getName()), button.getName());
                    shownVariables.add(twoVariablesProperties.getFirstName(button.getName()));
                    shownVariables.add(twoVariablesProperties.getSecondName(button.getName()));
                } else {
                    plotData.setShown(twoVariablesProperties.getFirstName(button.getName()), false);
                    plotData.setShown(twoVariablesProperties.getSecondName(button.getName()), false);
                    shownVariables.remove(twoVariablesProperties.getFirstName(button.getName()));
                    shownVariables.remove(twoVariablesProperties.getSecondName(button.getName()));
                }
                refreshPlot(plotData.getCurrentTimeShown());
            }
        };
        return listener;
    }

    /**
     * Tests if panel is showing none.
     * @return True if panel shows none, false otherwise
     */
    public boolean isSetToNone() {
        return stationComboBox.getSelectedIndex() 
                == stationComboBox.getItemCount() - 1;  //"None"
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        yAxisButtonGroup = new javax.swing.ButtonGroup();
        stationPanel = new javax.swing.JPanel();
        externalStationButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        stationTabbedPane = new javax.swing.JTabbedPane();
        dataPlotTab = new javax.swing.JPanel();
        singleVarPlotTab = new javax.swing.JPanel();
        singleVarScrollPane1 = new javax.swing.JScrollPane();
        singleVarPanel = new javax.swing.JPanel();
        multiVariableTab = new javax.swing.JPanel();
        multiScrollPane = new javax.swing.JScrollPane();
        multiVarPanel = new javax.swing.JPanel();
        dateRangePanel = new javax.swing.JPanel();
        dateRangeButton = new javax.swing.JButton();
        daySpanComboBox = new javax.swing.JComboBox<String>();
        fittedYAxisRadioButton = new javax.swing.JRadioButton();
        defaultYAxisRadioButton = new javax.swing.JRadioButton();
        linkCheckBox = new javax.swing.JCheckBox();
        snapshotButton = new javax.swing.JButton();
        stationComboBox = new javax.swing.JComboBox<ResourceListCellItem>();

        externalStationButton.setIcon(IconProperties.getExternalWindowIconImage());
        externalStationButton.setToolTipText("Display the Data Plot in a separate window.");
        externalStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                externalStationButtonActionPerformed(evt);
            }
        });

        printButton.setIcon(IconProperties.getSnapshotPrintIconImage());
        printButton.setToolTipText("Print the current plot displayed in the Data Plot window.");
        printButton.setEnabled(false);
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        stationTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        stationTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stationTabbedPaneStateChanged(evt);
            }
        });

        javax.swing.GroupLayout dataPlotTabLayout = new javax.swing.GroupLayout(dataPlotTab);
        dataPlotTab.setLayout(dataPlotTabLayout);
        dataPlotTabLayout.setHorizontalGroup(
            dataPlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
        dataPlotTabLayout.setVerticalGroup(
            dataPlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 301, Short.MAX_VALUE)
        );

        stationTabbedPane.addTab("Data Plot", dataPlotTab);

        singleVarPanel.setLayout(new java.awt.GridLayout(14, 2));
        singleVarScrollPane1.setViewportView(singleVarPanel);

        javax.swing.GroupLayout singleVarPlotTabLayout = new javax.swing.GroupLayout(singleVarPlotTab);
        singleVarPlotTab.setLayout(singleVarPlotTabLayout);
        singleVarPlotTabLayout.setHorizontalGroup(
            singleVarPlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(singleVarScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
        );
        singleVarPlotTabLayout.setVerticalGroup(
            singleVarPlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(singleVarScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
        );

        stationTabbedPane.addTab("Single-Variable Plots", singleVarPlotTab);

        multiVarPanel.setLayout(new java.awt.BorderLayout());
        multiScrollPane.setViewportView(multiVarPanel);

        javax.swing.GroupLayout multiVariableTabLayout = new javax.swing.GroupLayout(multiVariableTab);
        multiVariableTab.setLayout(multiVariableTabLayout);
        multiVariableTabLayout.setHorizontalGroup(
            multiVariableTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(multiScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
        );
        multiVariableTabLayout.setVerticalGroup(
            multiVariableTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(multiScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
        );

        stationTabbedPane.addTab("Multi-Variable Plots", multiVariableTab);

        dateRangePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        dateRangeButton.setText("Date Range");
        dateRangeButton.setToolTipText("Select a the range of dates to see the data plot for");
        dateRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateRangeButtonActionPerformed(evt);
            }
        });
        dateRangePanel.add(dateRangeButton);

        daySpanComboBox.setToolTipText("Choose the day span for the data plot");
        daySpanComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daySpanComboBoxActionPerformed(evt);
            }
        });
        dateRangePanel.add(daySpanComboBox);

        yAxisButtonGroup.add(fittedYAxisRadioButton);
        fittedYAxisRadioButton.setText("Fit");
        fittedYAxisRadioButton.setToolTipText("Fits the Y axis to the screen");
        fittedYAxisRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fittedYAxisRadioButtonItemStateChanged(evt);
            }
        });
        dateRangePanel.add(fittedYAxisRadioButton);

        yAxisButtonGroup.add(defaultYAxisRadioButton);
        defaultYAxisRadioButton.setSelected(true);
        defaultYAxisRadioButton.setText("Default");
        defaultYAxisRadioButton.setToolTipText("Select the defaut Y axis");
        dateRangePanel.add(defaultYAxisRadioButton);

        linkCheckBox.setSelected(true);
        linkCheckBox.setText("Link");
        linkCheckBox.setToolTipText("This will cause this graph to change when a new range is selected for the camera and map loop.");
        linkCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkCheckBoxActionPerformed(evt);
            }
        });
        dateRangePanel.add(linkCheckBox);

        snapshotButton.setIcon(IconProperties.getCameraIconImage());
        snapshotButton.setToolTipText("Save a snapshot of the current plot displayed in the Data Plot window.");
        snapshotButton.setEnabled(false);
        snapshotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapshotButtonActionPerformed(evt);
            }
        });

        stationComboBox.setToolTipText("Select weather station.");
        stationComboBox.setLightWeightPopupEnabled(false);
        stationComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stationComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout stationPanelLayout = new javax.swing.GroupLayout(stationPanel);
        stationPanel.setLayout(stationPanelLayout);
        stationPanelLayout.setHorizontalGroup(
            stationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stationPanelLayout.createSequentialGroup()
                .addGroup(stationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stationPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(externalStationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(snapshotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(stationComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(stationTabbedPane)
                    .addComponent(dateRangePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        stationPanelLayout.setVerticalGroup(
            stationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, stationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(stationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(stationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(externalStationButton)
                            .addComponent(snapshotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(printButton))
                    .addComponent(stationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stationTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateRangePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        stationPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {externalStationButton, printButton, snapshotButton, stationComboBox});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(stationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(stationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This method displays the date selection window.
     * @param evt The ActionEvent that triggers the dateRangeButton
     */
    private void dateRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateRangeButtonActionPerformed
        //Must convert plotted range so that it's end is the start of the last day.
        Date startOfLastDay = new Date(ResourceTimeManager
                .getStartOfDayFromMilliseconds(plottedRange.getStopTime()
                .getTime(), this.getResourceTimeZone()));
        ResourceRange windowStartRange = new ResourceRange(this.getStartOfPlottedRange(),
                startOfLastDay);
        Debug.print("Sent Range: ");
        debugResourceRange(windowStartRange);
        final ResourceRange returnedWindowRange = DateRangeSelectionWindow
                .getNewResourceRange(windowStartRange, getResourceTimeZone(), 
                true, false, !isExternal);
        
        //Leave if user cancelled.
        if (returnedWindowRange == null) {
            return;
        }
        
        //"Current Day" depends on main window range.
        ResourceRange mainWindowRange = mainWin.getMovieController()
                .getResourceRange();
        setDaySpanItems(this.smallestFitForRange(mainWindowRange) == 1);
        
        //Create and start TimedLoader.
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Weather Station Data";
            }

            @Override
            protected void doLoading() {
                //Must convert returned range so that it's end is the end of the last day.
                Date startOfFirstDay = new Date(returnedWindowRange
                        .getStartTime().getTime());
                Date endOfLastDay = new Date(ResourceTimeManager
                        .getEndOfDayFromMilliseconds(returnedWindowRange
                                .getStopTime().getTime(), getResourceTimeZone()));
                ResourceRange rangeToSet = new ResourceRange(startOfFirstDay,
                        endOfLastDay);
                
                //Set to new range.
                Debug.print("Range To Be Set: ");
                debugResourceRange(rangeToSet);
                setResourceRange(rangeToSet);
                if (smallestFitForRange(rangeToSet) == 35) {
                    daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[0]);
                } else if (smallestFitForRange(rangeToSet) == 7) {
                    daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[1]);
                } else if (isExternal
                        && smallestFitForRange(rangeToSet) == 1) {
                    //Option of "1 day" only pressent in external windows.
                    daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[5]);
                } else {
                    daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[2]);
                }
            }
        };
        loader.execute();
}//GEN-LAST:event_dateRangeButtonActionPerformed

    /**
     * This method takes a snapshot of the current plot and opens it in a
     * snapshot window.
     * @param evt The event that the snapshot button is pressed.
     */
    private void snapshotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapshotButtonActionPerformed
        BufferedImage image = plotPanel.getImage();//GEN-HEADEREND:event_snapshotButtonActionPerformed
        Date time = this.getStartOfPlottedRange();
        SnapShotViewer snapshot = new SnapShotViewer(image, time, 
                currentResource, ".png");
        snapshot.preview();
}//GEN-LAST:event_snapshotButtonActionPerformed

    /**
     * This method shows plots for 1, 3, 7, or 35 days.
     * @param evt The event that the day span combo box is changed.
     */
    private void daySpanComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daySpanComboBoxActionPerformed
        //Create and start TimedLoader.
        TimedLoader loader = new TimedLoader() {
            @Override
            protected String getLabelText() {
                return "Weather Station Data";
            }

            @Override
            protected void doLoading() {
                if (dayBoxInitialized) {
                    if (daySpanComboBox.getSelectedItem().equals(DAY_SPAN_OPTIONS[0])) {
                        plotPanel.setDays(35);
                    } else if (daySpanComboBox.getSelectedItem().equals(DAY_SPAN_OPTIONS[1])) {
                        plotPanel.setDays(7);
                    } else if (daySpanComboBox.getSelectedItem().equals(DAY_SPAN_OPTIONS[2])) {
                        plotPanel.setDays(3);
                    } else if (daySpanComboBox.getSelectedItem().equals(DAY_SPAN_OPTIONS[5])) {
                        plotPanel.setDays(1);
                    } else if (daySpanComboBox.getSelectedItem().equals(DAY_SPAN_OPTIONS[4])) {
                        plotPanel.setDays(smallestFitForRange(lastManualRange));
                        resourceRange = lastManualRange;
                        setDaySpanItems(smallestFitForRange(lastManualRange) == 1);
                    } else {
                        //Set for "Current Day"
                        resourceRange = mainWin.getMovieController().getResourceRange();
                        plotPanel.setDays(1);
                    }
                    setCenteredResourceRange();
                    showGraph();
                }
            }
        };
        loader.execute();
    }//GEN-LAST:event_daySpanComboBoxActionPerformed

    private void showGraph() {
        if (isShown) {
            for (String variableName : allVariables) {
                plotData.setShown(variableName, false);
            }
            for (String variableName : shownVariables) {
                plotData.setShown(variableName, true);
            }
            refreshPlot(plotData.getCurrentTimeShown());
        }
    }

    private void fittedYAxisRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fittedYAxisRadioButtonItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            plotPanel.setRange(true);
            refreshPlot(plotData.getCurrentTimeShown());
        } else {
            plotPanel.setRange(false);
            refreshPlot(plotData.getCurrentTimeShown());
        }
    }//GEN-LAST:event_fittedYAxisRadioButtonItemStateChanged

    /**
     * Enable or disable buttons.
     * @param setting True to enable, false to disable.
     */
    private void setButtonsEnabled(boolean setting) {
        snapshotButton.setEnabled(setting);
        printButton.setEnabled(setting);
    }

    private void stationComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stationComboBoxItemStateChanged
        if (!refreshedStationList && evt.getStateChange()
                == ItemEvent.SELECTED) {
            TimedLoader loader = new TimedLoader() {
                @Override
                protected String getLabelText() {
                    return "Weather Station Data";
                }

                @Override
                protected void doLoading() {
                    //Store day span so is can be reset later.
                    String currentOption = null;
                    if (listsInitialized) {
                        currentOption = daySpanComboBox.getSelectedItem().toString();
                    }

                    changeResource();
                    setDaySpanItems(smallestFitForRange(resourceRange) == 1);
                    setDays(smallestFitForRange(resourceRange));

                    //This line hera for side effect of redrawing plot.
                    setResourceRange(resourceRange);

                    //Select previous option if not "manual." which would already be
                    //selected.  ("Current Day" will be ignored if no longer pressent.)
                    if (currentOption != null && !currentOption
                            .equals(DAY_SPAN_OPTIONS[4])) {
                        daySpanComboBox.setSelectedItem(currentOption);
                    }
                }
            };
            loader.execute();
        }
    }//GEN-LAST:event_stationComboBoxItemStateChanged

    /**
     * This method changes the resource displayed according to that station that
     * is selected in the stationComboBox.
     */
    private void changeResource() {
        if (listsInitialized) {
            isNoneStation = false;
            shownVariables.clear();
            allVariables.clear();
            setButtonsEnabled(true);
            setShown(true);
            Resource resource = getWeatherStationResourceForName(stationComboBox.getSelectedItem().toString());
            plotData.setCurrentResource(resource);
            currentResource = resource;
            //check if it is a weather underground instance
            if (resource != null && WeatherUndergroundInstance.isWeatherUndergroundInstance(resource)) {
                wvProps = new WeatherStationVariableProperties();
                twoVariablesProperties
                        = new WeatherStationTwoVariablesProperties(
                                PropertyManager.getGeneralProperty("WEATHER_STATION_PATH")
                                + PropertyManager.getGeneralProperty("WUndergroundTwoVariables"));
                initSingleVarPanel();
                initMultiVarPanel();
            } else {  //default is weather underground -- not using Bloomsburg's weather station 
                wvProps = new WeatherStationVariableProperties();
                twoVariablesProperties = new WeatherStationTwoVariablesProperties(
                        PropertyManager.getGeneralProperty("WEATHER_STATION_PATH")
                        + PropertyManager.getGeneralProperty("WUndergroundTwoVariables"));
                initSingleVarPanel();
                initMultiVarPanel();
            }
            if (!isExternal) {
                appControl.getGeneralService().setCurrentWeatherStationResource(resource);
            }
            refreshPlot();
            if (stationComboBox.getSelectedItem().equals("None")) {
                isNoneStation = true;
                shownVariables.clear();
                allVariables.clear();
                setButtonsEnabled(false);
                setShown(false);
                if (!isExternal) {
                    appControl.getGeneralService().setCurrentWeatherStationResource(null);
                }
            }
        }
    }

    /**
     * This method opens the station plot in an external window.
     * @param evt The event that the External button is pressed.
     */
    private void externalStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_externalStationButtonActionPerformed
        ExternalWindow.displayExternalStationWindow(currentResource, resourceRange, appControl, mainWin);
    }//GEN-LAST:event_externalStationButtonActionPerformed

    /**
     * This method takes a snapshot of the current plot and sends it to the
     * printer.
     * @param evt The event that the print button is clicked.
     */
    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        BufferedImage image = plotPanel.getImage();
        Date time = this.getStartOfPlottedRange();
        SnapShotViewer snapshot = new SnapShotViewer(image, time, 
                currentResource, ".png");
        snapshot.print();
    }//GEN-LAST:event_printButtonActionPerformed

    private void linkCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkCheckBoxActionPerformed
        //Stop unneccessary execution.
        if(isExternal) {
            return;
        }
        
        this.changeRangeWithController = this.linkCheckBox.isSelected();
        //Must reset combo box to exculude or include "Manual."
        //"Current Day" depends on main window range.
        ResourceRange mainWindowRange = mainWin.getMovieController()
                .getResourceRange(); 
        setDaySpanItems(this.smallestFitForRange(mainWindowRange) == 1);     
        if (smallestFitForRange(plottedRange) == 35) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[0]);
        } else if (smallestFitForRange(plottedRange) == 7) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[1]);
        } else if (smallestFitForRange(plottedRange) == 3) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[2]);
        } else {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[3]);
        }
         
         if(this.linkCheckBox.isSelected()) {
             //Make the last manual range what the main window shows.
             lastManualRange = mainWindowRange;
         }
    }//GEN-LAST:event_linkCheckBoxActionPerformed

    private void stationTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stationTabbedPaneStateChanged
        //Do nothing is constructor.
        if(!panelInitialized) {
            return;
        }
        
        if (stationTabbedPane.getSelectedIndex() != 0) {
            //A graph is not shown, so disable all buttons.
            externalStationButton.setEnabled(false);
            setButtonsEnabled(false);
        } else {
            //Reset buttons for the shown graph.
            externalStationButton.setEnabled(!isExternal);
            setButtonsEnabled(!stationComboBox.getSelectedItem().equals("None"));
        }
    }//GEN-LAST:event_stationTabbedPaneStateChanged

    /*
     * This function uses the built-in getImage() function in the plotPanel to
     * retrieve an image of the plot panel.
     * @return An image of the plot panel.
     */
    public BufferedImage getImageFromPlotPanel() {
        return plotPanel.getImage();
    }
    
    /**
     * This function returns a date holding the start of the shown range.
     * @return A date holding the start of the shown range. 
     */
    public Date getStartOfPlottedRange() {
        return plottedRange.getStartTime();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataPlotTab;
    private javax.swing.JButton dateRangeButton;
    private javax.swing.JPanel dateRangePanel;
    private javax.swing.JComboBox<String> daySpanComboBox;
    private javax.swing.JRadioButton defaultYAxisRadioButton;
    private javax.swing.JButton externalStationButton;
    private javax.swing.JRadioButton fittedYAxisRadioButton;
    private javax.swing.JCheckBox linkCheckBox;
    private javax.swing.JScrollPane multiScrollPane;
    private javax.swing.JPanel multiVarPanel;
    private javax.swing.JPanel multiVariableTab;
    private javax.swing.JButton printButton;
    private javax.swing.JPanel singleVarPanel;
    private javax.swing.JPanel singleVarPlotTab;
    private javax.swing.JScrollPane singleVarScrollPane1;
    private javax.swing.JButton snapshotButton;
    private javax.swing.JComboBox<ResourceListCellItem> stationComboBox;
    private javax.swing.JPanel stationPanel;
    private javax.swing.JTabbedPane stationTabbedPane;
    private javax.swing.ButtonGroup yAxisButtonGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * Sets time in milliseconds.
     * @param currentMilliseconds The time in milliseconds.
     */
    public void updateCurrentTime(long currentMilliseconds) {
        refreshPlotTime(currentMilliseconds);
    }

    /**
     * Sets the current resource.
     * @param resource The resource to be shown.
     */
    public void setResource(Resource resource) {
        if (resource != null) {
            setShown(true);
            currentResource = resource;
            stationComboBox.setSelectedItem(new ResourceListCellItem(currentResource.getResourceName()));
            plotPanel.refreshPlot(true, this.getResourceTimeZone());

            // Set the current resource in general services.
            appControl.getGeneralService().setCurrentWeatherStationResource(resource);
        } else {
            setShown(false);
            currentResource = null;
            stationComboBox.setSelectedItem(new ResourceListCellItem("None"));
        }
    }

    /**
     * The function is used to reset the combo box when it is NOT linked.  It
     * is necessary due to the addition and removal of the "current day"
     * option.
     * @param newRange The new range of the main controller.  It is necessary 
     * to determine if, in the event that "current day" is selected, that
     * option can stay selected.
     */
    public void resetComboBox(ResourceRange newRange) {
        if (smallestFitForRange(plottedRange) == 35) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[0]);
        } else if (smallestFitForRange(plottedRange) == 7) {
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[1]);
        } else if (smallestFitForRange(plottedRange) == 1
                && smallestFitForRange(newRange) == 1
                && ResourceTimeManager.getStartOfDayFromMilliseconds(newRange
                .getStartTime().getTime(), this.getResourceTimeZone())
                == ResourceTimeManager.getStartOfDayFromMilliseconds(plottedRange
                .getStartTime().getTime(), this.getResourceTimeZone())) {
            //can only use "current day" if staying in same, single day 
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[3]);
        } else { //select "3 Days"
            daySpanComboBox.setSelectedItem(DAY_SPAN_OPTIONS[2]);
        }
    }
}
