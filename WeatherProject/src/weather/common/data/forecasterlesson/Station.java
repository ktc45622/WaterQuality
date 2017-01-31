package weather.common.data.forecasterlesson;
import java.util.HashMap;

/**
 * Instances of this class represent a station in the weather database.
 * An instance of this class will represent one row in the forecaster_stations
 * table and the complete set of rows in the forecaster_station_data table.
 * @author jbenscoter
 */
public class Station implements java.io.Serializable {
    private static final long serialVersionUID = 1;
    /*
        The unique station id.
    */
    private String stationId;
    
    /*
        The name of the station.
    */
    private String stationName;
    
    /*
        The state the station is in.
    */
    private String state;
    
    /*
     * Stores the data for the Station.
     */
    private HashMap<String, String> data;
    
    /**
     * Constructs a new instance of <code>Station</code>. This should only be 
     * used by the <code>DBMSStationManager</code> to construct an object
     * from the database.
     * @param stationId The id of the station.
     * @param stationName The name of the station.
     * @param state The state the station is located in.
     */
    public Station(String stationId, String stationName, String state)
    {
        setStationId(stationId);
        setStationName(stationName);
        setState(state);
    }
    
    /**
     * Returns the station id.
     * @return The station id.
     */
    public String getStationId() {
        return stationId;
    }

    /**
     * Sets the station id, but erases all other station data.
     * @param stationId 
     */
    public final void setStationId(String stationId) {
        this.stationId = stationId;
        this.state = "";
        this.stationName = "";
        
        if(this.data != null)
            this.data.clear();
    }

    /**
     * Returns the station name.
     * @return The station name.
     */
    public String getStationName() {
        return stationName;
    }

    /**
     * Sets the name of the station.
     * @param stationName The name of the station
     */
    public final void setStationName(String stationName) {
        this.stationName = stationName;
    }

    /**
     * Returns the state the station is in.
     * @return The state the station is in.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state the station is in.
     * @param state The state the station is in.
     */
    public final void setState(String state) {
        this.state = state;
    }
    
    /**
     * The data associated with this station.
     * @return the data associated with this station.
     */
    public HashMap<String, String> getData() {
        return data;
    }
    
    /**
     * Returns the station information as a string.
     * @return The station id, station name, and state.
     */
    @Override
    public String toString() {
        return "Station Id: " + getStationId() + " Station Name: " + 
                getStationName() + " State: " + getState() + " Data: " + data;
    }

    /**
     * Sets the data for the station.
     * @param data the data for the station.
     */
    public void setData(HashMap<String, String> data) {
        this.data=data;
    }
}
