package weather.clientside.gui.client;

import java.awt.Color;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import weather.clientside.utilities.ResourceTreeManager;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.data.weatherstation.WeatherStationInstance;
import weather.common.data.weatherstation.WeatherStationVariable;
import weather.common.data.weatherstation.WeatherStationVariableProperties;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.utilities.ResourceTimeManager;

/**
 * This class holds the data to be plotted.  It is used to select the range of
 * data and to get the color for each variable.
 * 
 * TODO: Finish Documentation of the constructors and a couple methods
 * 
 * @version Bloomsburg University Software Engineering
 * @author Steve Rhein (2008)
 * @author Xianrui Meng (2010)
 * @version Spring 2012
 */
public class PlotData {

    //Resource from the weather station
    private Resource currentResource;
    //Range of time for our valuesToAdd
    private ResourceRange currentResourceRange;
    //Data values to add to plot
    private Vector<WeatherStationVariable> values;
    //Time to be marked. (Should always match the time of the main window movies.)
    private long currentTime;
    // range of time shown in panel
    private WeatherStationVariableProperties weatherVariableProperties;
    private boolean valuesChanged = true;
    private Date startTime;
    private Date endTime;
    //Flag that determine whether the resource range has been changed.
    private boolean resourceRangeChanged = true;
    //Store the variable selected in the one variable tab.
    private ArrayList<String> selectedVariable;

    /**
     * Construct the object with defaults.
     */
    public PlotData() {
        this.currentResourceRange =
                new ResourceRange(new Date(Calendar.getInstance().getTimeInMillis()),
                new Date(Calendar.getInstance().getTimeInMillis()));
        this.values = new Vector<>();
        this.currentTime = ResourceTimeManager.getResourceStartMilliseconds();
        this.weatherVariableProperties = new WeatherStationVariableProperties();
        this.selectedVariable = new ArrayList<>();
    }

    /**
     * Construct the object with the given initial resource.
     * @param initialResource The given initial resource.
     */
    public PlotData(Resource initialResource) {
        this();
        currentResource = initialResource;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("<PlotData tostring>\n");
        for(WeatherStationVariable wv:values) {
            out.append(wv.getVariableKey()).append(": ").append(wv.get(0)).append("\n");
        }
        out.append("</PlotData tostring>");

        return out.toString();
    }

    /**
     * Get color for representation of a variable.
     * 
     * @param variableKey Variable to get color of.
     * @return The color for representation of the variable.
     */
    public Color getColor(String variableKey) {
        return weatherVariableProperties.getColor(variableKey);
    }

    /**
     * Sets whether a variable is to be shown.
     * @param variableKey The variable to set.
     * @param isShown True if variable is shown, false otherwise.
     */
    public void setShown(String variableKey, boolean isShown) {
        for (WeatherStationVariable wv : values) {
            if (wv.getVariableKey().equals(variableKey)) {
                if (wv.isShown() != isShown) {
                    //Note that graph has been changed.
                    valuesChanged = true;
                }
                wv.setShown(isShown);
            }
        }
    }
    
    /**
     * Sets the variables given to true, sets all others to false.
     * @param variableKeys The list of variables to set to true.
     */
    public void setShown(ArrayList<String> variableKeys){
        for(WeatherStationVariable wv : values){
            if(variableKeys.contains(wv.getVariableKey())) {
                wv.setShown(true);
            }
            else {
                wv.setShown(false);
            }
            //Note that graph has been changed.
            valuesChanged = true;
        }
    }
    
    /**
     * Checks whether a variable is to be shown in the plot.
     * @param variableKey The variable to check.
     * @return True if variable is to be shown, false otherwise.
     */
    public boolean getShown(String variableKey) {
        for (WeatherStationVariable wv : values) {
            if (wv.getVariableKey().equals(variableKey)) {
                return wv.isShown();
            }
        }
        return false;
    }

    /**
     * Sets the current resource.
     * 
     * @param resource The current resource.
     */
    public void setCurrentResource(Resource resource) {
        if (resource == null) {
            currentResource = null;
        }
        // Only download data if the resource is not null.
        else if(resource != currentResource) {
            currentResource = resource;
            clear(); //remove the old values from the plot
            //get the resources and add them to this list.
            Vector<ResourceInstance> ins;
            ins = ResourceTreeManager.getResourceInstancesForRange(resource, currentResourceRange);

            //add the new values to the plot
            for(ResourceInstance inst : ins) {
                if(((WeatherStationInstance)inst).isCompressed()) {
                    ((WeatherUndergroundInstance)inst).createValuesVector();
                }
                //add values 
                this.appendValues(((WeatherStationInstance)inst).getValues());
            }

        }
    }

    /**
     * Returns the start time for the panel's date range.
     * @return The start time for the panel's date range.
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time for the panel's date range.
     * @return The end time for the panel's date range.
     */
    public Date getEndTime() {
        return endTime;
    }
    
    /**
     * Sets the start time for the panel's date range.
     * @param start The start time for the panel's date range.
     */
    public void setStartTime(Date start) {
        this.startTime = start;
    }

    /**
     * Sets the end time for the panel's date range.
     * @param end The end time for the panel's date range.
     */
    public void setEndTime(Date end) {
        this.endTime = end;
    }

    /**
     * Returns this plot's resource range.
     * @return This plot's resource range.
     */
    public ResourceRange getResourceRange() {
        return currentResourceRange;
    }

    /**
     * Sets this data plot's resource range. If the range is different than the
     * current range that is set, the values stored are cleared and new values
     * are loaded in from the appropriate WeatherStationInstances that are
     * stored in the tree.
     * 
     * @param resourceRange This data plot's new resource range.
     */
    public void setResourceRange(ResourceRange resourceRange) {
        //If range is null, do nothing.
        if (resourceRange == null) {
            return;
        }
        //Determine if we need to get new date.  Two things might cause this.
        //1.  The range is different.
        //2.  The range ends in the future, meaning new data may have been
        //    collected since the last call of this function.
        if(!resourceRange.equals(currentResourceRange)
                || Calendar.getInstance().getTimeInMillis()
                < resourceRange.getStopTime().getTime()) {
            //Note that graph has been changed.
            valuesChanged = true;
            
            resourceRangeChanged = true;
            currentResourceRange = resourceRange;
            values.clear();
            setStartTime(resourceRange.getStartTime());
            setEndTime(resourceRange.getStopTime());
            if (currentResource != null) {
                Vector<ResourceInstance> weatherInstances =
                        ResourceTreeManager.getResourceInstancesForRange(currentResource, resourceRange);
                for (ResourceInstance instance : weatherInstances) {
                    if (((WeatherStationInstance) instance).isCompressed()) {
                        ((WeatherUndergroundInstance) instance).createValuesVector();
                    }
                    if(WeatherUndergroundInstance.isWeatherUndergroundInstance(currentResource)) {
                        appendValues(((WeatherUndergroundInstance) instance).getValuesForResourceRange(resourceRange));
                    }
                    else {
                        appendValues(((WeatherStationInstance) instance).getValuesForResourceRange(resourceRange));
                    }
                }
            }
        }
    }

    /**
     * Returns this plot's <code>Vector</code> of <code>WeatherStationData</code>.
     * @return This plot's <code>Vector</code> of <code>WeatherStationData</code>.
     */
    public Vector<WeatherStationVariable> getValues() {
        return new Vector<WeatherStationVariable>(this.values);
    }

    /**
     * Clears the values of the plot.
     */
    public void clear() {
        this.values.clear();
    }

    /**
     * Appends a vector of WeatherStationVariables to the data values that will
     * be plotted.
     *
     * @param variables The data values to be added to the existing values.
     */
    private void appendValues(Vector<WeatherStationVariable> variables) {
        if (values == null || values.isEmpty()) {
            values = new Vector<>(variables);
        } else {
            //Debug.println("Plot data.java line 291 - values.size = " + values.size());
            //Debug.println("Plot data.java line 292 - variables.size = " + variables.size());
            if (variables.size() > 0) {
                for (int index = 0; index < values.size(); index++) {
                    values.get(index).getValues().addAll(variables.get(index).getValues());
                }
            }
        }
    }

    /**
     * Returns the current time of this data plot.
     * @return The Current time of this data plot.
     */
    public long getCurrentTimeShown() {
        return currentTime;
    }

    /**
     * Sets this data plot's current time and displays it on the graph.
     * @param currentTimeShown This data plot's new current time.
     */
    public void setCurrentTimeShown(long currentTimeShown) {
        this.currentTime = currentTimeShown;
    }

    /**
     * Returns true if valuesToAdd have been changed since last read.
     * @return True if values have changed, false otherwise.
     */
    public boolean isValuesChanged() {
        return valuesChanged;
    }

    /**
     * Returns true if <code>ResourceRange</code> has been changed since last read.
     * @return true if <code>ResourceRange</code> has been changed.
     */
    public boolean isResourceRangeChanged() {
        return resourceRangeChanged;
    }

    /**
     * To add the variable selected by the radio button.

     * @param variableKey The variable key.
     */
    public void addSelectedVariableKey(String variableKey) {
        this.selectedVariable.clear();
        this.selectedVariable.add(variableKey);
    }

    /**
     * To add the multi-variables selected by the radio button.
     * 
     * @param variable1 The first variable name.
     * @param variable2 The second variable name.
     * @param multiPlotKey The key for the multi plot.
     */
    public void addMultiSelectedVariableKeys(String variable1, String variable2,
            String multiPlotKey) {
        this.selectedVariable.clear();
        this.selectedVariable.add(variable1);
        this.selectedVariable.add(variable2);
        this.selectedVariable.add(multiPlotKey);
    }

    /**
     * Get the selected variable's Key from the radio button.
     * If we selected from the single variable tab, we just get key of the index 0.
     * If we selected from the single variable tab, 0 is the first variable, 1 is
     * the second variable, 2 is the multi-variable key.
     * 
     * @param index The index of the variable to be returned.
     * @return The index from selectedVariable.
     */
    public String getSelectedVariableKey(int index) {
        return selectedVariable.get(index);
    }

    /**
     * @return The size of the selected variables from the radio buttons.
     */
    public int getSelectedVariableSize() {
        return this.selectedVariable.size();
    }

    /**
     * Gets the selectedVariable ArrayList.
     * @return An ArrayList of the selected variables.
     */
    public ArrayList<String> getSelectedVariables() {
        return selectedVariable;
    }

    /**
     * Sets whether values have been changed.
     * @param valuesChanged True if values were changed, false otherwise.
     */
    public void setValuesChanged(boolean valuesChanged) {
        this.valuesChanged = valuesChanged;
    }
}
