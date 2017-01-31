package weather.clientside.gui.administrator;

/**
 * This class is used to store all the information for a weather station
 *  properties file.
 * 
 * 	#variable.variableName.index
 *	#variable.variableName.displayName
 *	#variable.variableName.description
 *	#variable.variableName.units
 *	#variable.variableName.min
 *	#variable.variableName.max
 *	#variable.variableName.color 
 *	#variable.variableName.type
 *	#variable.variableName.common
 *	#variable.variableName.used
 *	#variable.variableName.spacing
 *	#variable.variableName.ordering
 * 
 * For each variable,
 * 	Index (The index of the variable,
 *          Must be indexed the same as it comes from the server!),
 *	displayName (The shorter variable variableKey, to be display on the single variable plot panel),
 *	description (The full variable variableKey),
 *	units (The units the variable uses),
 *	min (The minimum value for the default range),
 *	max(The maximum value for the default range),
 *	color (The color of the trace to be graphed,
 *          using http://www.computerhope.com/htmcolor.htm to get the color code) ,
 *	type (The type of the variable (int, float...)),
 *	common (not be used),
 *	used (Which variables are currently being used),
 *	spacing (The distance and frequency of the tick marks),
 *	ordering (The order in which the variables are displayed on the tab).
 *
 * For example:
 *   index          variable.windDirection.index=1
 *   displayName     variable.windDirection.displayName=Wind Dir
 *   decription      variable.windDirection.description=Wind Direction
 *   units         variable.windDirection.units=°
 *   minUnit       variable.windDirection.min=0
 *   maxUnit       variable.windDirection.max=360
 *   color         variable.windDirection.color=#800080
 *   type          variable.windDirection.type=INT
 *   common        variable.windDirection.common=Wind
 *   used          variable.windDirection.used=Yes
 *   space         variable.windDirection.spacing=2
 *   order         variable.windDirection.ordering=5
 *
 * 
 * @author Bloomsburg University Software Engineering
 * @author Fen Qin (2009)
 * @author Xianrui Meng (2010)
 * @version Spring 2010
 */
public class VariablesProperties {

    private String variableKey;
    private String index;
    private String displayName;
    private String decription;
    private String type;
    private String units;
    private String minUnit;
    private String maxUnit;
    private String color;
    private String group;
    private String used;
    private String order;
    private String space;
    private String common;

    /**
     * Constructs a station property with a given variableKey.
     * @param string The variableKey of the property.
     */
    public VariablesProperties(String string) {
        this.variableKey = string;
    }

    /**
     * Returns the common variableKey of the property.
     * @return the property's common variableKey.
     */
    public String getCommon() {
        return common;
    }

    /**
     * Sets the common variableKey of the property.
     * @param common The common variableKey to be given to the property.
     */
    public void setCommon(String common) {
        this.common = common;
    }

    /**
     * Returns the property's plot color.
     * @return the property's plot color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the property's plot color.
     * @param color The plot color to be set for the property.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns the string value ("Yes" or "No"), depending on whether the 
     *      property is used in the station.
     * @return The string value "Yes" or "No".
     */
    public String getUsed() {
        return used;
    }

    /**
     * Sets a string value ("Yes" or "No") if the property is used in 
     *      the weather station.
     * @param used The string value "Yes" or "No".
     */
    public void setUsed(String used) {
        this.used = used;
    }

    /**
     * Returns the spacing and tick marks for the property on the 
     *      weather station plot.
     * @return The spacing and tick marks on the plot for the property.
     */
    public String getSpace() {
        return space;
    }

    /**
     * Returns the index of the variable. This is the position of the 
     *      property as it comes from the server (i.e. and index of 1 means 
     *      this is the first property to come from the server).
     * @return The index of the property.
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the index of the property. If a property's index
     * is set to 1, for example, then this means that this property will
     * be the first to come down from the server.
     * @param index The index to be set for the property.
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Sets the distance and frequency of the tick marks
     * for this property on the station plot.
     * @param space The spacing for the property.
     */
    public void setSpace(String space) {
        this.space = space;
    }

    /**
     * Returns the description variableKey of the property.
     * @return The description variableKey of the property.
     */
    public String getDescription() {
        return decription;
    }

    /**
     * Sets the description variableKey of the property.
     * @param description The description variableKey of the property.
     */
    public void setDescription(String description) {
        this.decription = description;
    }

    /**
     * Returns the maximum value allowed for the property.
     * @return The maximum value allowed for the property.
     */
    public String getMaxUnit() {
        return maxUnit;
    }

    /**
     * Sets the maximum value allowed for a property.
     * @param maxUnit The maximum value allowed for a property.
     */
    public void setMaxUnit(String maxUnit) {
        this.maxUnit = maxUnit;
    }

    /**
     * Returns the minimum value allowed for a property.
     * @return The minimum value allowed for a property.
     */
    public String getMinUnit() {
        return minUnit;
    }

    /**
     * Sets the minimum value allowed for a property.
     * @param minUnit The minimum value allowed for a property.
     */
    public void setMinUnit(String minUnit) {
        this.minUnit = minUnit;
    }

    /**
     * Returns the short variableKey for a property.
     * @return The short variableKey of a property.
     */
    public String getVariableKey() {
        return variableKey;
    }

    /**
     * Sets the variableKey of a property.
     * @param key The variableKey of a property.
     */
    public void setVariableKey(String key) {
        this.variableKey = key;
    }

    /**
     * Gets the order of the property.
     * @return The display placement in the plot panel.
     */
    public String getOrder() {
        return order;
    }

    /**
     * Sets the position of the property in the station property file.
     * @param order The position of the property in the station's property file.
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * Returns the property's short variableKey.
     * @return The property's short variableKey.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the property's short file variableKey.
     * @param shortName The property's short variableKey.
     */
    public void setDisplayName(String shortName) {
        this.displayName = shortName;
    }

    /**
     * Returns the variable type that the property uses
     * (int, float, etc.)
     * @return The variable type used for the property.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the variable type used for the property.
     * @param type The type of variable that the 
     *              property uses (int, float, etc.).
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the property's units.
     * @return The property's units.
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units for the property.
     * @param units The units for the property (i.e. "mph").
     */
    public void setUnits(String units) {
        this.units = units;
    }
}
