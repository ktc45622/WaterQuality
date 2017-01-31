package weather.clientside.gui.client;

import java.awt.image.BufferedImage;
import java.util.TimeZone;
import java.util.Vector;
import weather.common.data.resource.ResourceRange;
import weather.common.data.weatherstation.WeatherStationVariable;

/**
 * This interface represents a panel that holds a weather station chart.
 * 
 * @author Justin Enslin (2012)
 * @version 2012
 */
public interface WeatherDataPlotPanel {
    /**
     * Sets whether the y-axis is set to the default range or to fit the data 
     * shown.
     * 
     * @param isFitted Whether or not the range of the y-axis is fitted.
     */
    public void setRange(boolean isFitted);

    /**
     * Sets the number of days shown by the chart.
     * @param days The number of days shown
     */
    public void setDays(int days);
    
    /**
     * Gets the number of days shown by the chart.
     * @return The number of days shown
     */
    public int getDays();

    /**
     * Returns <code>PlotData</code> used for this chart.
     * @return <code>PlotData</code> used for this chart.
     */
    public PlotData getPlotData();

    /**
     * Sets <code>PlotData</code> used for this chart.
     * @param plotData <code>PlotData</code> used for this chart.
     */
    public void setPlotData(PlotData plotData);

    /**
     * Returns Current time shown in <code>PlotData</code>.
     * @return Current time shown in milliseconds.
     */
    public long getCurrentTimeShown();

    /**
     * Sets the currentTimeShown in <code>PlotData</code>.
     * @param currentTimeShown Time to set in milliseconds.
     */
    public void setCurrentTimeShown(long currentTimeShown);

    /**
     * Returns <code>ResourceRange</code> for <code>PlotData</code>.
     * @return <code>ResourceRange</code> for <code>PlotData</code>.
     */
    public ResourceRange getResourceRange();

    /**
     * Sets resource range for <code>PlotData</code>.
     * @param resourceRange The ResourceRange for the PlotData.
     */
    public void setResourceRange(ResourceRange resourceRange);

    /**
     * Returns <code>Vector<code> of <code>WeatherStationData</code> for chart.
     * @return a <code>Vector<code> of <code>WeatherStationData</code> for chart.
     */
    public Vector<WeatherStationVariable> getValues();
    
    /**
     * Refreshes the plot.
     *
     * @param forceUpdate If true the plot will be updated as long as the plot
     * data has been changed. If false only the time marker will be updated.
     * @param zone The <code>TimeZone</code> in which to display time data. This
     * is not used if the first parameter is false
     */
    public void refreshPlot(boolean forceUpdate, TimeZone zone);
    
    /*
     * Returns the width of the WeatherDataPlotPanel.
     * 
     * @return The width of the WeatherDataPlotPanel.
     */
    public int getWeatherDataPlotPanelWidth();

    /*
     * Returns the height of the WeatherDataPlotPanel.
     * 
     * @return The height of the WeatherDataPlotPanel.
     */
    public int getWeatherDataPlotPanelHeight();

    /*
     * Gets an image of the current plot.
     * 
     * @return A BufferedImage of the current chart.
     */
    public BufferedImage getImage();
}
