/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package weather.common.dbms;
import java.util.Vector;
import java.util.ArrayList;
import weather.common.data.forecasterlesson.Station;
/**
 * Manages the stations and station data in the forecaster_station and
 * forecaster_station_data tables.
 * @author jbenscoter
 * @author Xiang Li(2014)
 */
public interface DBMSStationManager {
    
    /**
     * Inserts a new station into the forecaster_station table and inserts
     * any station data into the forecaster_station_data table.
     * The stationId field must be blank.
     * @param station The <code>Station</code> to be inserted into the tables.
     * @return An instance of <code>Station</code> with the stationId field populated.
     */
    public Station insertStation(Station station);
    
    /**
     * Updates an existing station into the forecaster_station table and updates
     * any station data into the forecaster_station_data table.
     * The stationId field must not be blank.
     * @param station The <code>Station</code> to be updated.
     * @return True if the <code>Station</code> was updated successfully. False otherwise.
     */
    public boolean updateStation(Station station);
    
    /**
     * Deletes an existing station in the forecaster_station table and deletes
     * any station data in the forecaster_station_data table.
     * The stationId field must not be blank.
     * @param station The <code>Station</code> to be deleted.
     * @return True if the <code>Station</code> was deleted successfully. False otherwise.
     */
    public boolean deleteStation(Station station);
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Stations</code> in the 
     * table.
     * @return An <code>ArrayList</code> of <code>Station</code> objects that
     * represent all of the <code>Stations</code> in the database.
     */
    public Vector<Station> getAllStations();
    
    /**
     * Returns an <code>ArrayList</code> containing all the <code>Stations</code>
     * in the table that belong to the provided state.
     * @param state The state to get the stations for.
     * @return An <code>ArrayList</code> of <code>Stations</code> that are in 
     * the provided state.
     */
    public Vector<Station> getAllStationsByState(String state);
    
    public ArrayList<String> getStates();

    
    /**
     * Return <code>Station</code> that matches the given name or code.
     * @param stationName The given name or code.
     * @return The matching <code>Station</code>.
     */
    public Station obtainStation(String stationName);
}
