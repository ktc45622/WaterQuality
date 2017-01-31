package weather.common.dbms;
import java.sql.Date;
import java.util.HashMap;
import weather.common.data.forecasterlesson.Station;

/**
 * manages <code>Station</code> objects in the database
 * @author jjb01420
 */
public interface DBMSForecasterStationDataManager {

    /**
     * adds station data to the database
     * @param stationCode the station code to add the station to
     * @param date the date to add the data to.
     * @param data the data to add for the specified date.
     * @return true if successful, false otherwise.
     */
    public boolean addStationData(String stationCode, Date date, HashMap<String, String> data);

    /**
     * gets a <code>Station</code> object for the specified date.
     * @param stationCode the station code of the <code>Station</code> object
     * @param date the date to lookup for.
     * @return
     */
    public Station getStation(String stationCode, Date date);
}
