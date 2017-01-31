package weather.clientside.gui.client;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weather.clientside.manager.WeatherStationPanelManager;
import weather.common.data.resource.ResourceRange;
import weather.common.data.weatherstation.WeatherStationDataType;
import weather.common.data.weatherstation.WeatherStationTwoVariablesProperties;
import weather.common.data.weatherstation.WeatherStationVariable;
import weather.common.data.weatherstation.WeatherStationVariableProperties;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;

/**
 * This class is used to construct a data plot of weather station data, and plot
 * the data into the chart.
 *
 * @author John Lenhart (2012)
 * @version 2012
 */
public class JFreeChartPlotPanel extends JPanel implements WeatherDataPlotPanel {
    private PlotData plotData;
    private boolean fitDataToPlot = false;
    private int numDaysShown;
    private JFreeChart chart;
    private WeatherStationVariableProperties wvProperties;
    private WeatherStationTwoVariablesProperties twoVariablesProperties;
    private String rangeAxisTitle1 = "";
    private String rangeAxisTitle2 = "";
    private Color variable1Color = null;
    private Color variable2Color = null;
    private int rangeMax1FromPropertyFile;
    private int rangeMax2FromPropertyFile;
    private int rangeMin1FromPropertyFile;
    private int rangeMin2FromPropertyFile;
    private long domainStartTime = 0;
    private long domainEndTime = 0;
    private Marker timeTrace;
    
    //Wind direction class variables.
    //Is wind direction shown at all?
    private boolean isWindirectionShown = false;
    //Is wind direction on the right-hand axis of a two-variable graph?
    private boolean isWindDirectionOnRightOf2VarGraph = false;
    //X and Y values for all eventually ploted wind direction points.
    private ArrayList<Integer> windDirectionValues;
    private ArrayList<Long> windDirectionDates;
    //As (n) wind direction points are averaged, the last (n - 1) are
    //replaced by this value and not plotted.
    private static final int DO_NOT_PLOT_VALUE = 60000;
    
    private double minDataValue;
    private double maxDataValue;
    private int numVariables;

    //Needed for bookmark plot image.
    private static final int PLOT_WIDTH = 667;
    private static final int PLOT_HEIGHT = 437;

    /**
     * Creates the structure for a
     * <code>JFreeChartPlotPanel</code> using passed
     * <code>PlotData</code> to build the chart.  Does not load data.
     *
     * @param data The <code>PlotData</code> to be used for plotting.
     */
    public JFreeChartPlotPanel(PlotData data) {
        super();
        plotData = data;
    }

    /**
     * Determines if wind direction is currently shown.
     *
     * @return True if the wind direction is currently shown.
     */
    private boolean windDirectionIsShown() {
        for (WeatherStationVariable wv : plotData.getValues()) {
            if (wv.isShown()) {
                if (wv.getVariableKey().equals("windDirection")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates the plot and initializes the components.
     *
     * @param zone The <code>TimeZone</code> in which to display time data.
     */
    private void createPlot(TimeZone zone) {
        numVariables = 0;
        minDataValue = Double.MAX_VALUE;
        maxDataValue = Double.MIN_VALUE;
        
        // Clear and reset wind direction variables
        windDirectionValues = new ArrayList<Integer>();
        windDirectionDates = new ArrayList<Long>();
        isWindirectionShown = windDirectionIsShown();

        // Reset other class variables 
        wvProperties = new WeatherStationVariableProperties();
        twoVariablesProperties = new WeatherStationTwoVariablesProperties(null);
        timeTrace = new ValueMarker(plotData.getCurrentTimeShown());
        
        Debug.println("Current time shown in create plot is " + plotData.getCurrentTimeShown());
        rangeMax1FromPropertyFile = Integer.MAX_VALUE;
        rangeMin1FromPropertyFile = Integer.MIN_VALUE;
        rangeMax2FromPropertyFile = Integer.MAX_VALUE;
        rangeMin2FromPropertyFile = Integer.MIN_VALUE;

        // Create data set with which to make chart.  NOTE: This data set will
        // NOT include wind direction data, but the process of creating the data
        // set will store wind direction data is class variables.
        final XYDataset dataset = createDataset();
        
        // Create chart using the above data sat and, if needed, the wind 
        // direction class variables.
        chart = createChart(dataset, zone);
        
        ChartPanel chPanel = new ChartPanel(chart, true, true, true, false, true);

        chPanel.setMouseWheelEnabled(false);
        chPanel.setMouseZoomable(false);
        chPanel.setDomainZoomable(false);
        chPanel.setRangeZoomable(false);
        //Needed for resizing.
        this.setLayout(new BorderLayout());
        //Never scale. Always full resolution resize.
        chPanel.setMaximumDrawHeight(999999999);
        chPanel.setMaximumDrawWidth(999999999);

        this.add(chPanel);
        this.validate();
    }

    /**
     * Sets if the data is fitted on the plot or not.
     *
     * @param isFitted Sets the data to be fitted to the plot if true.
     */
    @Override
    public void setRange(boolean isFitted) {
        fitDataToPlot = isFitted;
    }

    /**
     * Sets the number of days displayed across the domain.
     *
     * @param days The number of days to display.
     */
    @Override
    public void setDays(int days) {
        numDaysShown = days;
    }

    /**
     * Gets the number of days displayed across the domain.
     *
     * @return The number of days displayed.
     */
    @Override
    public int getDays() {
        return numDaysShown;
    }

    /**
     * Gets the plot data that is currently in the plot.
     *
     * @return The plots data.
     */
    @Override
    public PlotData getPlotData() {
        return plotData;
    }

    /**
     * Sets the plots data.
     *
     * @param plotData The new data to set for the plot.
     */
    @Override
    public void setPlotData(PlotData plotData) {
        this.plotData = plotData;
    }

    /**
     * Gets the current time shown on the plot.
     *
     * @return The time where the trace is currently drawn.
     */
    @Override
    public long getCurrentTimeShown() {
        return plotData.getCurrentTimeShown();
    }

    /**
     * Sets the current time to be shown in the plot.
     *
     * @param currentTimeShown The time the trace will be set to.
     */
    @Override
    public void setCurrentTimeShown(long currentTimeShown) {
        plotData.setCurrentTimeShown(currentTimeShown);
    }

    /**
     * Gets the current resource range.
     *
     * @return The current resource range.
     */
    @Override
    public ResourceRange getResourceRange() {
        return plotData.getResourceRange();
    }

    /**
     * Sets the current resource range.
     *
     * @param resourceRange The resource range to set the plot to.
     */
    @Override
    public void setResourceRange(ResourceRange resourceRange) {
        plotData.setResourceRange(resourceRange);
    }

    /**
     * Gets the current values of the weather plot.
     *
     * @return A vector of the current values in the plot.
     */
    @Override
    public Vector<WeatherStationVariable> getValues() {
        return plotData.getValues();
    }

    /**
     * Refreshes the plot.
     *
     * @param forceUpdate If true the plot will be updated as long as the plot
     * data has been changed. If false only the time marker will be updated.
     * @param zone The <code>TimeZone</code> in which to display time data.
     * This is not used if the first parameter is false
     */
    @Override
    public void refreshPlot(boolean forceUpdate, TimeZone zone) {
        if (!forceUpdate) //Just update the marker
        {
            XYPlot plot = chart.getXYPlot();
            plot.removeDomainMarker(timeTrace);
            timeTrace = new ValueMarker(plotData.getCurrentTimeShown());
            Debug.println("Current time shown in refresh plot is " + plotData.getCurrentTimeShown());
            timeTrace.setStroke(new BasicStroke(1));
            timeTrace.setPaint(Color.BLACK);
            plot.addDomainMarker(timeTrace);
        } else if (plotData.isValuesChanged() || plotData.isResourceRangeChanged()) {
            //Clear data, if pressent.
            if (windDirectionDates != null) { //Clear wind direction class variables
                windDirectionDates.clear();
                windDirectionValues.clear();
                System.gc();
            }

            this.removeAll();
            createPlot(zone);
        }
    }

    /**
     * Gets the panels width.
     *
     * @return The panels width.
     */
    @Override
    public int getWeatherDataPlotPanelWidth() {
        return this.getSize().width;
    }

    /**
     * Gets the panels height.
     *
     * @return The panels height.
     */
    @Override
    public int getWeatherDataPlotPanelHeight() {
        return this.getSize().height;
    }

    /**
     * Gets a
     * <code>BufferredImage</code> of the plot.
     *
     * @return The <code>BufferredImage</code> of the plot.
     */
    @Override
    public BufferedImage getImage() {
        return chart.createBufferedImage(PLOT_WIDTH, PLOT_HEIGHT);
    }

    /**
     * Creates the dataset for the chart and store date in the returned object
     * and class variables.
     *
     * @return The dataset for the chart excluding wind direction.  Wind 
     * direction data is placed in class variables.
     */
    private XYDataset createDataset() {
        // Begin setting one wind direction class varible by assuming wind
        // direction is not on the graph's right.
        isWindDirectionOnRightOf2VarGraph = false;
        
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        Vector<WeatherStationVariable> shownVariables = new Vector<>();
        
        // Get timing data.
        Vector<WeatherStationVariable> data = plotData.getValues();
        WeatherStationVariable xValues = getTimeWeatherVariable(data);
        
        for (WeatherStationVariable wv : plotData.getValues()) {
            if (wv.isShown()) {
                // Alter wind direction, if shown.
                // Combine data points into a smaller set of points to reduce 
                // scatter plat clutter.
                if (wv.getVariableKey().equals("windDirection")) {
                    averageWindValues(xValues, wv);
                }
                
                // Make sure barmetric pressure, if shown, is in millibars.
                if (wv.getVariableKey().equals("rawBarometer")) {
                    changeToMillibars(wv);
                }
                shownVariables.add(wv);
            }
        }

        // Set the domain range.
        domainStartTime = plotData.getResourceRange().getStartTime().getTime();
        domainEndTime = plotData.getResourceRange().getStopTime().getTime();

        // If the selected variable is from the one variable selection tab.
        if (plotData.getSelectedVariableSize() == 1) {
            String variableKey = plotData.getSelectedVariableKey(0);

            rangeAxisTitle1 = wvProperties.getDescriptionName(variableKey) + " "
                    + wvProperties.getUnits(variableKey);
            rangeAxisTitle2 = rangeAxisTitle1;

            variable1Color = wvProperties.getColor(variableKey);
            variable2Color = variable1Color;

            rangeMin1FromPropertyFile = wvProperties.getMinValue(variableKey).intValue();
            rangeMax1FromPropertyFile = wvProperties.getMaxValue(variableKey).intValue();
            numVariables = 1;
        } else if (plotData.getSelectedVariableSize() == 3) {
            //TODO
            numVariables = 3;
        }

        if (shownVariables.size() > 0 && !WeatherStationPanelManager.isNoneStation) {
            String twoVariablesGroupKey;
            WeatherStationVariable wvOne, wvTwo;
            if (shownVariables.size() == 2) {
                wvOne = shownVariables.get(0);
                wvTwo = shownVariables.get(1);
                twoVariablesGroupKey = twoVariablesProperties.getVariableKeyForVariables(wvOne.getVariableKey(),
                        wvTwo.getVariableKey());
                //Get names of variables.
                String variableOneName = twoVariablesProperties.getFirstName(twoVariablesGroupKey);
                String variableTwoName = twoVariablesProperties.getSecondName(twoVariablesGroupKey);
                
                //If wvOne is not the variableOneName, exchange the wvOne and wvTwo.
                if (!wvOne.getVariableKey().equals(variableOneName)) {
                    wvOne = shownVariables.get(1);
                    wvTwo = shownVariables.get(0);
                }
                
                //If wvTwo is wind direction, swap data.  (Make sure wind 
                //direction is "first" weather variable in terns of the class
                //variables that hold weather variable infomation.)
                if (wvTwo.getVariableKey().equals("windDirection")) {
                    // Wind direction should be or right dispite being "first"
                    // variable.  This is needed because the data set returned
                    // by the function in this case will only contain data
                    // from the other variable, which will always be at the 
                    // zeroth index.
                    isWindDirectionOnRightOf2VarGraph = true;
                    
                    WeatherStationVariable swapVar = wvOne;
                    wvOne = wvTwo;
                    wvTwo = swapVar;
                    String swapString = variableOneName;
                    variableOneName = variableTwoName;
                    variableTwoName = swapString;
                }
                
                rangeAxisTitle1 = wvProperties.getDescriptionName(wvOne.getVariableKey()) + " "
                        + wvProperties.getUnits(wvOne.getVariableKey());
                rangeAxisTitle2 = wvProperties.getDescriptionName(wvTwo.getVariableKey()) + " "
                        + wvProperties.getUnits(wvTwo.getVariableKey());

                variable1Color = wvProperties.getColor(wvOne.getVariableKey());
                variable2Color = wvProperties.getColor(wvTwo.getVariableKey());

                rangeMin1FromPropertyFile = wvProperties.getMinValue(wvOne.getVariableKey()).intValue();
                rangeMin2FromPropertyFile = wvProperties.getMinValue(wvTwo.getVariableKey()).intValue();
                rangeMax1FromPropertyFile = wvProperties.getMaxValue(wvOne.getVariableKey()).intValue();
                rangeMax2FromPropertyFile = wvProperties.getMaxValue(wvTwo.getVariableKey()).intValue();

                // If statement keeps wind direction from being added to the
                // returned dataset.  The call to getSeriesData actually stores
                // data in the other wind direction class variables.
                if (wvOne.getVariableKey().equals("windDirection")) {
                    getSeriesData(xValues, wvOne, variableOneName);
                    dataset.addSeries(getSeriesData(xValues, wvTwo, variableTwoName));
                } else {
                    dataset.addSeries(getSeriesData(xValues, wvOne, variableOneName));
                    dataset.addSeries(getSeriesData(xValues, wvTwo, variableTwoName));
                }
                numVariables = 2;
            } else if (shownVariables.size() == 1) {
                wvOne = shownVariables.get(0);
                rangeMin1FromPropertyFile = wvProperties.getMinValue(wvOne.getVariableKey()).intValue();
                rangeMax1FromPropertyFile = wvProperties.getMaxValue(wvOne.getVariableKey()).intValue();
                variable1Color = wvProperties.getColor(wvOne.getVariableKey());
                if (!isWindirectionShown) {
                    dataset.addSeries(getSeriesData(xValues, wvOne, wvProperties.getDescriptionName(wvOne.getVariableKey())));
                } else {
                    getSeriesData(xValues, wvOne, wvProperties.getDescriptionName(wvOne.getVariableKey()));
                }
                numVariables = 1;
            }
        }

        // Record that chart has changed
        plotData.setValuesChanged(false);
        
        return dataset;
    }

    /**
     * Gets the values for a series of data as a <code>TimeSeries</code> object.
     * 
     * NOTE: the returned object should be discarded when handling wind 
     * direction.  In that case, the function places data in class variables.
     *
     * @param timeVariable The time variable for the series.
     * @param wsv The weather station variable for the series.
     * @param name The title name of the series.
     * 
     * @return The <code>TimeSeries</code> for the given data.
     */
    private TimeSeries getSeriesData(WeatherStationVariable timeVariable, WeatherStationVariable wsv, String name) {
        Debug.println("Loading Varible: " + wsv.getVariableKey());
        final TimeSeries series = new TimeSeries(name);
        Calendar currentTime = Calendar.getInstance();
        Calendar lastTime = Calendar.getInstance();
        
        //Set theshold for gaps for missing data
        long nullThreshold = 2 * ResourceTimeManager.MILLISECONDS_PER_HOUR; //assume 3 or 7 day plot
        if(numDaysShown == 1) {
            nullThreshold = 10 * ResourceTimeManager.MILLISECONDS_PER_MINUTE;
        }
        if (numDaysShown == 35) {
            nullThreshold = 10 * ResourceTimeManager.MILLISECONDS_PER_HOUR;
        }

        //Retrieve values in for loop
        for (int i = 0; i < wsv.size() && i < timeVariable.size(); ++i) {
            currentTime.setTimeInMillis(timeVariable.get(i).longValue());

            //Check for possible null value
            if (i > 0) {
                if (currentTime.getTimeInMillis() - lastTime.getTimeInMillis() > nullThreshold) {
                    //Compute null time as Calendar
                    Calendar nullCalendar = (Calendar) lastTime.clone();
                    nullCalendar.add(Calendar.MINUTE, 1);

                    //Add null value to series
                    series.addOrUpdate(new Second(nullCalendar.getTime()), null);
                }
            }

            //Get the type of the variable according to the variable key.
            switch (wvProperties.getType(wsv.getVariableKey())) {
                case FLOAT:
                    if (wsv.get(i) != null && !wsv.isDefaultValue(i) && currentTime.getTimeInMillis() > domainStartTime) {
                        //Add data point
                        series.addOrUpdate(new Second(currentTime.getTime()), wsv.get(i).floatValue());
                         
                        //Resize Graph
                        if (minDataValue > wsv.get(i).floatValue()) {
                            minDataValue = wsv.get(i).floatValue();
                        }
                        if (maxDataValue < wsv.get(i).floatValue()) {
                            maxDataValue = wsv.get(i).floatValue();
                        }
                    }
                    break;

                case INT:
                    if (!wsv.isDefaultValue(i)) {
                        if (wsv.getVariableKey().equals("windDirection")) {
                            // Load wind direction class variable without
                            // adding data to return object and remove all times
                            // with DO_NOT_PLOT_VALUE as a value.
                            if (wsv.get(i).intValue() != DO_NOT_PLOT_VALUE && currentTime.getTimeInMillis() > domainStartTime) {
                                windDirectionValues.add(wsv.get(i).intValue());
                                windDirectionDates.add(currentTime.getTime().getTime());
                            }
                        } else if (currentTime.getTimeInMillis() > domainStartTime) {
                            //Add data point
                            series.addOrUpdate(new Second(currentTime.getTime()), wsv.get(i).floatValue());
                            
                            //Resize Graph
                            if (minDataValue > wsv.get(i).floatValue()) {
                                minDataValue = wsv.get(i).floatValue();
                            }
                            if (maxDataValue < wsv.get(i).floatValue()) {
                                maxDataValue = wsv.get(i).floatValue();
                            }
                        }
                    }
                    break;

                case TIME:
                    break;

                case LONG:
                    if (!wsv.isDefaultValue(i) && currentTime.getTimeInMillis() > domainStartTime) {
                        //Add data point
                        series.addOrUpdate(new Second(currentTime.getTime()), wsv.get(i).longValue());
                         
                        //Resize Graph
                        if (minDataValue > wsv.get(i).longValue()) {
                            minDataValue = wsv.get(i).longValue();
                        }
                        if (maxDataValue < wsv.get(i).longValue()) {
                            maxDataValue = wsv.get(i).longValue();
                        }
                    }
            }

            //Set time for next null check
            lastTime.setTimeInMillis(currentTime.getTimeInMillis());
        }//End for
        
        return series;
    }

    /**
     * Takes a vector of the old values and averages a specified number of
     * minutes to get the average of wind values.
     *
     * @param timeVariable The WeatherStationVariable holding the times that
     * match the wind direction data
     * @param weatherStationVariable The WeatherStationVariable, should only be
     * used with windDirection
     */
    private void averageWindValues(WeatherStationVariable timeVariable,
            WeatherStationVariable weatherStationVariable) {
        //Get old data and make sure it is not already averaged.
        Vector<Number> oldData = weatherStationVariable.getValues();
        
        if (oldData.contains(DO_NOT_PLOT_VALUE)) {
            return;
        }
        
        //Scale minutes to average by days shown to reduce graph clutter.
        int minutesToAverage =
                numDaysShown * Integer.parseInt(PropertyManager
                        .getGeneralProperty("windDirectionAverage"));
        
        //Vector to hold waluses after averaging,
        Vector<Number> newData = new Vector<>();

        //Each period of (minutesToAverages) minute will produce one value that
        //can be plotted. As newData will be the same size as oldData, the 
        //averaged value will be placed at the index that starts the period.  
        //The other indexes in the period will be filled with a value that the
        //plotting code will not plot.  The newData structure will now be 
        //filled with this value, which will be replaced with averages where
        //appropriate.
        for (int i = 0; i < oldData.size(); i++) {
            newData.add(i, DO_NOT_PLOT_VALUE);
        }   
        
        //Variables for tracking the average currently being computed.
        int firstIndexOfCurrentPeriod = 0;
        long endOfCurrentPeriodInMillis = 0;
        int runningTotalForCurrentAverage = 0;
        int runningNumberOfValuesInCurrentAverage = 0;
        
        //Do work while looping through time values.
        for (int i = 0; i < timeVariable.size(); i++) {            
            if (runningTotalForCurrentAverage == 0) {
                //Begin calculation of next average.
                firstIndexOfCurrentPeriod = i;
                endOfCurrentPeriodInMillis = timeVariable.get(i).longValue()
                        + minutesToAverage * ResourceTimeManager
                        .MILLISECONDS_PER_MINUTE;
            }
            
            //Record addition of the wind direction for the current time into
            //the running totals.
            runningTotalForCurrentAverage += oldData.get(i).intValue();
            runningNumberOfValuesInCurrentAverage++;
            
            //Check if this should be the last number to be aversged, which
            //happens if this is the last value or if the next value is beyond
            //the end of the current period.
            if(i == timeVariable.size() - 1 || timeVariable.get(i + 1)
                    .longValue() >= endOfCurrentPeriodInMillis) {
                //Compute this average.
                int currentAverage = runningTotalForCurrentAverage
                        / runningNumberOfValuesInCurrentAverage;
                
                //Reset variables declared just above loop.
                runningTotalForCurrentAverage = 0;
                runningNumberOfValuesInCurrentAverage = 0;
                
                //Place average in newData vector.
                newData.setElementAt(currentAverage, firstIndexOfCurrentPeriod);
            }
        }

       //Store new date in parameter variable.
        weatherStationVariable.resetValues(newData);
    }

    /**
     * Converts inches of mercury to Millibars. BP already in millibars is
     * unchanged.
     *
     * @param weatherStationVariable The WeatherStationVariable, should only be
     * used with rawBarometer.
     */
    private void changeToMillibars(WeatherStationVariable weatherStationVariable) {
        Vector<Number> newData = new Vector<Number>();
        Vector<Number> input = weatherStationVariable.getValues();
        //If data alread in corrent range, don't change it (all mb's > 500}
        if ((float) input.elementAt(0) > 500.0f) {
            return;
        }

        for (int i = 0; i < input.size(); i++) {
            float valueInMb = (float) input.elementAt(i);
            Debug.println("Value before change: " + valueInMb);
            valueInMb *= 1013.25 / 29.92;
            Debug.println("Value after change: " + valueInMb);
            newData.add(i, valueInMb);
        }

        weatherStationVariable.resetValues(newData);
    }

    /**
     * Get the Time variable from current
     * <code>Vector<code> of
     * <code>WeatherStationData</code>.
     *
     * @param data The vector that stores the all
     * the <code>WeatherStationData</code>
     * @return The time of current <code>WeatherStationData</code>.
     */
    private WeatherStationVariable getTimeWeatherVariable(Vector<WeatherStationVariable> data) {
        for (WeatherStationVariable wv : data) {
            if (wvProperties.getType(wv.getVariableKey()) == WeatherStationDataType.TIME) {
                return wv;
            }
        }
        return null;
    }

    /**
     * Creates the chart object.
     * 
     * NOTE: Wind direction is added from class variables.
     *
     * @param dataset The dataset for the chart.
     * @param zone The <code>TimeZone</code> in which to display time data.
     * @return The new <code>JFreeChart.</code>
     */
    private JFreeChart createChart(final XYDataset dataset, TimeZone zone) {

        //Set x-axis label
        SimpleDateFormat yearDF = new SimpleDateFormat("y");
        yearDF.setTimeZone(zone);
        String startYear = yearDF.format(new Date(domainStartTime));
        String endYear = yearDF.format(new Date(domainEndTime));
        String xLabel = "Date (" + startYear;
        if (startYear.equals(endYear)) {
            xLabel += ")";
        } else {
            xLabel += " - " + endYear + ")";
        }
        if (numDaysShown == 1) {
            SimpleDateFormat dayDF = new SimpleDateFormat("MM/dd/yy");
            dayDF.setTimeZone(zone);
            xLabel = "Time (" + dayDF.format(new Date(domainStartTime)) + ")";
        }
        
        // Make chart without any wind direction data. 
        final JFreeChart returnedChart = ChartFactory.createTimeSeriesChart(
                null,
                xLabel,
                "Values",
                dataset,
                false,
                true,
                false);

        //Set chart properties
        returnedChart.setBackgroundPaint(Color.white);
        returnedChart.setAntiAlias(true);
        returnedChart.setTextAntiAlias(true);
        final XYPlot plot = returnedChart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        Font easyToReadFont = new Font("Sans-Serif", Font.PLAIN, 12);

        //Set properties of left-hand range axis.
        final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLabel(rangeAxisTitle1);
        rangeAxis1.setLabelFont(easyToReadFont);
        rangeAxis1.setLabelPaint(variable1Color);
        plot.getRenderer().setSeriesPaint(0, variable1Color);
        
        //The next 2 if-else chains explicitly set the range of the left-hand
        //axis if wind direction is not plotted.  Note that this range will
        //later be copied for the right-hand axis, so it must accomodate the
        //ranges for both variables of a two-variable graph.

        //Handle case where "Fit" option is not selected and base range on
        //weather variable properties.
        if (numVariables == 1 && !fitDataToPlot && !isWindirectionShown) {
            rangeAxis1.setRange(rangeMin1FromPropertyFile, rangeMax1FromPropertyFile);
        } else if (numVariables == 2 && !fitDataToPlot && !isWindirectionShown) {
            rangeAxis1.setRange(Math.min(rangeMin1FromPropertyFile, rangeMin2FromPropertyFile),
                    Math.max(rangeMax1FromPropertyFile, rangeMax2FromPropertyFile));
        }

        //Handle case where "Fit" option is selected and usable.  The option
        //must be ignored if there is no data or if wind directions are on the
        //graph.  When usable, the option bases the range on the data to plot.
        if(!isWindirectionShown && maxDataValue != Double.MIN_VALUE 
                && fitDataToPlot) {
            rangeAxis1.setAutoRange(false);
            //+-.01 for (0,0) case.
            double rangeMin = minDataValue - minDataValue * .02 - .01;
            double rangeMax = maxDataValue + maxDataValue * .02 + .01;
            Debug.println("Fitted Range: " + rangeMin + " to " + rangeMax);
            rangeAxis1.setRange(rangeMin, rangeMax);
        } else {
            Debug.println("Fitted Range Not Triggered.");
        }

        //Set properties of right-hand range axis.  Several are copied from the
        //left-hand axis.  It is not yet time to set the range if wind direction
        //is on the graph.
        final NumberAxis rangeAxis2 = new NumberAxis(rangeAxisTitle2);
        rangeAxis2.setLabelFont(rangeAxis1.getLabelFont());
        if (!isWindirectionShown) {
            rangeAxis2.setRange(rangeAxis1.getRange());
        }
        rangeAxis2.setStandardTickUnits(rangeAxis1.getStandardTickUnits());
        rangeAxis2.setTickLabelFont(rangeAxis1.getTickLabelFont());
        rangeAxis2.setLabelPaint(variable2Color);
        plot.setRangeAxis(1, rangeAxis2);
        plot.getRenderer().setSeriesPaint(1, variable2Color);
        
        //This if block handles graphs with wind directions.
        if (isWindirectionShown) {
            //Create data set by calling a function that uses the wind direction
            //class variables.
            XYDataset scatterDataset = createScatterDataset();
            
            //Add wind direction data set to graph while recording its index.
            XYItemRenderer scatterRenderer = new XYLineAndShapeRenderer(false, true);
         
            /*Create Vertical Axes*/
            //NOTE: The below code relies on wind direction being "weather
            //      variable 1" and this is assured by createDataset().
            
            //Create vertical axes that show directions.  The two copies provide
            //separate objects for the right and left of the graph for the 
            //single-variable case.  
            String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
            ValueAxis directionAxis1 = new SymbolAxis(rangeAxisTitle1,
                        directions);
            directionAxis1.setLabelPaint(variable1Color);
            ValueAxis directionAxis2 = new SymbolAxis(rangeAxisTitle1,
                        directions);
            directionAxis2.setLabelPaint(variable1Color);
    
            //This axis is just used to plot the points for the wind direction,
            //because you can't plot with N,NW,W... etc.
            final NumberAxis hiddenAxis = new NumberAxis();
            hiddenAxis.setRange(rangeMin1FromPropertyFile, rangeMax1FromPropertyFile);
            
            //Crate axis for other variable if required.  It is assure to be 
            //"weather variable 2."
            final NumberAxis otherAxis = new NumberAxis();
            if (numVariables == 2) {
                otherAxis.setRange(rangeMin2FromPropertyFile, rangeMax2FromPropertyFile);
                otherAxis.setLabelPaint(variable2Color);
                otherAxis.setLabel(rangeAxisTitle2);
            }
            
            //Add scatter plot to data set and set properties.  It is added to 
            //index 1 because any data placed in the plot by createDataset() 
            //will be at index 0.
            plot.setDataset(1, scatterDataset);
            plot.setRenderer(1, scatterRenderer);
            scatterRenderer.setSeriesPaint(0, variable1Color);
            
            //Add hidden axis to graph and map data.  Placing it is position 2
            //allows it to leter be hidden while maintaining vertical axes on 
            //the right and left of the graph.
            plot.setRangeAxis(2, hiddenAxis, false);
            plot.mapDatasetToRangeAxis(1, 2);

            //Use class variables (isWindDirectionOnRightOf2VarGraph and numVariables) to
            //determine where the axes should be placed on the graph.  If there
            //is a second variable, it will be plotted after its axis is added.
            if (!isWindDirectionOnRightOf2VarGraph) {
                //Wind direction is not on the right-hand axis od a two-variable
                //graph, so the left-hand axis (axis 0) is wind direction.
                plot.setRangeAxis(0, directionAxis1, false);
                //The right-hand axis (axis 1) is either wind direction again
                //(if single-variable graph) or the axis for the second 
                //variable, so check for second variable.
                if (numVariables == 2) {
                    //Plot second variable to right-hand axis.
                    plot.setRangeAxis(1, otherAxis, false);
                    plot.mapDatasetToRangeAxis(0, 1);
                } else {
                    //Copy directions to right with 2nd direction axis object.
                    plot.setRangeAxis(1, directionAxis2, false);
                } //End of second variable if
            } else { //Directions disired on right, so there ane two variables.
                //Plot second variable to left-hand axis.
                plot.setRangeAxis(0, otherAxis, false);
                plot.mapDatasetToRangeAxis(0, 0);
                
                //Place directions on right-hand axis.
                plot.setRangeAxis(1, directionAxis1, false); 
            }
            
            //Hide hidden axis.
            hiddenAxis.setVisible(false);
        }  //End of wind direction if.
        
        //Set time axis.
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setRange(domainStartTime, domainEndTime);
        setTimeLabeling(domainAxis, zone);

        //Set trace.
        timeTrace.setStroke(new BasicStroke(1));
        timeTrace.setPaint(Color.BLACK);
        plot.addDomainMarker(timeTrace);

        return returnedChart;
    }

    /**
     * Create the scatter plot dataset for wind direction values.
     *
     * @return The scatter plot dataset for wind direction values.
     */
    private XYDataset createScatterDataset() {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("");
        for (int i = 0; i < windDirectionValues.size(); i++) {
            series.addOrUpdate(windDirectionDates.get(i), windDirectionValues.get(i));
        }
        result.addSeries(series);
        return result;
    }

    /**
     * Sets the labeling interval and time format..
     */
    private void setTimeLabeling(DateAxis domainAxis, TimeZone zone) {
        if (numDaysShown == 1) {
            domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 3));
            SimpleDateFormat df = new SimpleDateFormat("ha");
            df.setTimeZone(zone);
            domainAxis.setDateFormatOverride(df);
        } else if (numDaysShown == 3 || numDaysShown == 7) {
            domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
            SimpleDateFormat df = new SimpleDateFormat("MM/dd");
            df.setTimeZone(zone);
            domainAxis.setDateFormatOverride(df);
        } else if (numDaysShown == 35) {
            domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 7));
            SimpleDateFormat df = new SimpleDateFormat("MM/dd");
            df.setTimeZone(zone);
            domainAxis.setDateFormatOverride(df);
        } else {//Use default interval.
            domainAxis.setTimeZone(zone);
        }
    }
}
