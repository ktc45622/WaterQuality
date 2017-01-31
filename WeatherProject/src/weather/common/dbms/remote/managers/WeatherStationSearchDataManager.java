
package weather.common.dbms.remote.managers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import weather.common.data.weatherstation.WeatherStationDailyAverage;
import weather.common.dbms.DBMSWeatherStationSearchDataManager;
import weather.common.dbms.remote.DatabaseCommandType;
import weather.common.dbms.remote.RemoteDatabaseCommand;
import weather.common.dbms.remote.RemoteDatabaseResult;
import weather.common.dbms.remote.RemoteDatabaseResultStatus;

/**
 *
 * @author Brian Zaiser
 */
public class WeatherStationSearchDataManager implements DBMSWeatherStationSearchDataManager{

    /**
     * Retrieves the records within the specified date range for the 
     * specified resource using the specified key.
     * @param resourceNumber The specific number identifying the resource.
     * @param variableKey The key on which to search.
     * @param startRange The start of the date range.
     * @param endRange The end of the date range.
     * @return A collection of Double objects with all fields filled.
     */
    @Override
    public Collection<Double> getData(int resourceNumber, String variableKey, Date startRange, Date endRange) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WeatherStationSearchData_GetData;
       arguments = new ArrayList();
       arguments.add(resourceNumber);
       arguments.add(variableKey);
       arguments.add(startRange);
       arguments.add(endRange);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Collection<Double>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement 
    }

    /**
     * Adds a record of the daily average temperature for the specified resource.
     * @param resourceDailyAverage The daily average temperature for this resource.
     * @return True, if record successfully added; false, otherwise.
     */
    @Override
    public boolean insertData(WeatherStationDailyAverage resourceDailyAverage) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WeatherStationSearchData_InsertData;
       arguments = null;
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return false; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.DatabaseModificationSuccessful)) {
           return true;
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return false;
        } //end of if statement 
    }

    /**
     * Retrieves all records for the specified resource within the specified date range.
     * @param resourceNumber The resource specified by identifying number.
     * @param startRange The start of the date range.
     * @param endRange The end of the date range.
     * @return A collection of WeatherStationDailyAverage objects with all fields filled.
     */
    @Override
    public Collection<WeatherStationDailyAverage> getAllValues(int resourceNumber, Date startRange, Date endRange) {
       RemoteDatabaseCommand command = null;
       RemoteDatabaseResult result = null;
       DatabaseCommandType commandType = null; 
       ArrayList<Object> arguments = null;
       commandType = DatabaseCommandType.WeatherStationSearchData_GetAllValues;
       arguments = new ArrayList();
       arguments.add(resourceNumber);
       arguments.add(startRange);
       arguments.add(endRange);
       command = new RemoteDatabaseCommand (commandType, arguments );
       result = command.execute();
       if (result == null) return null; 
       if (result.getResultStatus().equals(RemoteDatabaseResultStatus.VectorOfResultsReturned)) {
           return (Collection<WeatherStationDailyAverage>) result.getResult();
        }
        else {
           // create and show weather exception. Log error with weatherLogger class
           return null;
        } //end of if statement 
    }
    
}
