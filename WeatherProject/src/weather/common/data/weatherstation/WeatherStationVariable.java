package weather.common.data.weatherstation;

import java.io.Serializable;
import java.util.Vector;

/**
 * This class is a structure for representing a weather variable and its values
 * per minute. It represents a column of values from the Weather Station data.
 * This class includes an entire time range of data values from some Weather
 * Station.
 *
 * @author Bloomsburg University Software Engineering
 * @author Steve Rhein (2008)
 * @version Spring 2008
 */
public class WeatherStationVariable implements Comparable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String INCORRECT_TYPE = "INVALID WEATHER VARIABLE TYPE ";
    public static final long DEFAULT_TIME_MILLIS = -1 * Long.MAX_VALUE;

    private String variableKey;
    private Vector<Number> values;
    private boolean shown;
    private WeatherStationVariableProperties wvProperties;
    
    /**
     * Constructs a <code>WeatherStationVariable</code> with the given
     * <code>varibleKey</code>.  <code>setInitialTimeMillis</code> must be called
     * before any values can be added to this
     * <code>WeatherStationVariable</code>.
     *
     * @param variableKey The key of this variable as represented in a weather
     * variable properties file.
     */
    public WeatherStationVariable(String variableKey) {
        wvProperties = new WeatherStationVariableProperties();
        try {
            wvProperties.getType(variableKey);
        } catch (IllegalArgumentException iae) {
            StringBuilder sb = new StringBuilder(INCORRECT_TYPE);
            sb.append(wvProperties.getType(variableKey));
            sb.append(Character.END_PUNCTUATION);
            throw new IllegalArgumentException(sb.toString());
        }
        this.variableKey = variableKey;
        values = new Vector<>();
        shown = false;
    }

    /**
     * Adds a number of new values to the station variable.
     *
     * @param newValues A vector<Number> of values.
     */
    public void resetValues(Vector<Number> newValues) {
        values = new Vector<Number>(newValues);
    }

    /**
     * Gets the values stored by this weather variable.
     *
     * @return The values stored by this weather variable.
     */
    public Vector<Number> getValues() {
        return values;
    }

    /**
     * Returns the key used to represent this
     * <code>WeatherStationVariable</code> in the properties file.
     *
     * @return The key used to represent this
     * <code>WeatherStationVariable</code> in the properties file.
     */
    public String getVariableKey() {
        return this.variableKey;
    }

    /**
     * This method returns all of the weather variable properties.
     *
     * @return All of the weather variable properties.
     */
    public WeatherStationVariableProperties getWvProperties() {
        return wvProperties;
    }

    /**
     * Returns true if this <code>WeatherStationVariable</code> is to be shown
     * in the chart, false otherwise.
     *
     * @return True if this <code>WeatherStationVariable</code> is to be shown
     * in the chart, false otherwise.
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * Sets whether this <code>WeatherStationVariable</code> is to be shown in
     * the chart.
     *
     * @param shown True if this <code>WeatherStationVariable</code> is to be
     * shown in the plot, false otherwise.
     */
    public void setShown(boolean shown) {
        this.shown = shown;
    }

    /**
     * Add <code>value</code> to the end of the values of this
     * <code>WeatherStationVariable</code>.
     *
     * @param value The value to add to this <code>Weathervariable</code>.
     */
    public void add(Number value) {
        values.add(value);
    }

    /**
     * Returns the value at <code>index</code> of this
     * <code>WeatherStationVariable</code>.
     *
     * @param index The index at which to retrieve the value.
     * @return The value at position <code>index</code>.
     */
    public Number get(int index) {
        Number rawNumberBytes = values.get(index);
        if (values.get(index) == null) {
            return null;
        }
        switch (wvProperties.getType(variableKey)) {
            case INT:
                return rawNumberBytes.intValue();
            case FLOAT:
                return rawNumberBytes.floatValue();
            case TIME:
            case LONG:
                return rawNumberBytes.longValue();
            default:
                return null;
        }
    }

    /**
     * Remove all the value of this <code>WeatherStationVariable</code>.
     *
     * @return Null if <code>WeatherStationVariable</code> is empty.
     */
    public Number removeAtTail() {
        if (values.size() > 0) {
            return values.remove(values.size() - 1);
        }
        return null;
    }

    /**
     * The number of values held in this
     * <code>WeatherStationVariable</code>.
     *
     * @return The number of values held in this
     * <code>WeatherStationVariable</code>.
     */
    public int size() {
        return values.size();
    }

    /**
     * Clears the values of this <code>WeatherStationVariable</code>.
     */
    public void clear() {
        values.clear();
    }

    /**
     * Check the value is the default value for the specified variable type.
     *
     * @param index The value which you want check.
     * @return True if the value is default value, else false.
     */
    public boolean isDefaultValue(int index) {
        switch (wvProperties.getType(variableKey)) {
            case INT:
                return values.get(index).intValue() == wvProperties.getDefaultValue(variableKey).intValue();
            case FLOAT:
                return values.get(index).floatValue() == wvProperties.getDefaultValue(variableKey).floatValue();
            case TIME:
            case LONG:
                return values.get(index).longValue() == wvProperties.getDefaultValue(variableKey).longValue();
            default:
                return true;
        }
    }

    /**
     * Creates a clone of the object and returns it
     *
     * @return A clone of the the object
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        WeatherStationVariable retVal = new WeatherStationVariable(variableKey);
        retVal.shown = this.shown;
        retVal.values.addAll(this.values);
        retVal.wvProperties = (WeatherStationVariableProperties) this.wvProperties.clone();
        return retVal;
    }

    /**
     * Checks to see if two instances of WeatherStationVariable are equal.
     *
     * @param o The object to compare the current WeaterStationVariable to.
     * @return True if the two objects are equal and false otherwise.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeatherStationVariable)) {
            return false;
        }
        WeatherStationVariable other = (WeatherStationVariable) o;
        return (this.variableKey == null ? other.variableKey == null : this.variableKey.equals(other.variableKey))
                && (this.wvProperties == null ? other.wvProperties == null : this.wvProperties.equals(other.wvProperties));
    }

    /**
     * Returns the Hash Code of the object calling this method.
     *
     * @return The hash code of the object calling this method.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.variableKey != null ? this.variableKey.hashCode() : 0);
        hash = 67 * hash + (this.wvProperties != null ? this.wvProperties.hashCode() : 0);
        return hash;
    }

    /**
     * If the index of this <code>WeatherStationVariable</code> in the
     * properties file is less than the index of <code>o</code>, -1 is returned.
     * If the index of this <code>WeatherStationVariable</code> is equal to the
     * index of <code>o</code> , 0 is returned. If the index of this
     * <code>WeatherStationVariable</code> is greater than the index of
     * <code>o</code>, 1 is returned.
     *
     * @param o The object to compare this <code>WeatherStationVariable</code>
     * to.
     * @return If the index of this <code>WeatherStationVariable</code> in the
     * properties file is less than the index of <code>o</code>, -1 is returned.
     * If the index of this <code>WeatherStationVariable</code> is equal to the
     * index of <code>o</code>, 0 is returned. If the index of this
     * <code>WeatherStationVariable</code> is greater than the index of
     * <code>o</code>, 1 is returned.
     */
    @Override
    public int compareTo(Object o) {
        WeatherStationVariable wv = (WeatherStationVariable) o;
        int difference = wvProperties.getIndex(variableKey) - wvProperties.getIndex(wv.variableKey);
        if (difference < 0) {
            return -1;
        } else if (difference > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return variableKey;
    }
}
