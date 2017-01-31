package weather.common.dbms;

import java.sql.Date;
import java.util.Collection;
import java.util.Vector;
import weather.common.data.weatherstation.WeatherStationDailyAverage;

/**
 * This class manages the weather station search data table in the database.
 *
 * @author Alinson Antony(2012).
 */
public interface DBMSWeatherStationSearchDataManager {

    /**
     * Retrieves a <code>Collection</code> of <code>Doubles</code> representing 
     * the values for variableKey over the specified date range.
     * @param resourceNumber The resourceNumber of the WeatherStation to return
     * data for.
     * @param variableKey The String containing the variable to be returned.
     * @param startRange A Date representing the start of the requested data
     * range.
     * @param endRange A Date representing the end of the requested data range.
     * @return Collection of doubles representing the values for variableKey.
     */
    public Collection<Double> getData(int resourceNumber, String variableKey, Date startRange, Date endRange);
    
    /**
     * Inserts all variables contained in the WeatherStationDailyAverage object into the
     * database for a given Resource for a day.
     * @param resourceDailyAverage The <code>WeatherStationDailyAverage</code> containing the values.
     * @return True if successful, false otherwise.
     */
    public boolean insertData(WeatherStationDailyAverage resourceDailyAverage);
    /**
     * A method to get all minimum,maximum and average value of all variable 
     * key for a particular day for a particular resource.
     * @param resourceNumber The resourceNumber of the WeatherStation to return data for.
     * @param startRange Starting Date.
     * @param endRange Ending Date.
     * @return Collection of  object of the class WeatherStationDailyAverage.
     */
    public Collection<WeatherStationDailyAverage> getAllValues(int resourceNumber,Date startRange, Date endRange) ; 
    /**
     * add two time points
     * new query -- when data started
     */
    
}
