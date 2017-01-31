package weather.common.data.weatherstation;

import java.util.Vector;
import weather.common.data.resource.ResourceRange;
import weather.common.utilities.WeatherException;
/**
 * This class is to calculate daily minimum,daily maximum and daily average 
 * values of weather station data.
 * @author Alinson Antony
 * @version Spring 2012
 */

public class WeatherStationDailyAverageCalculation {
    protected Vector<WeatherStationVariable> values;
    
    /**
     * Constructor.
     * @param weatherStation The <code>WeatherUndergroundInstance</code> object.
     * @throws WeatherException
     */
    public WeatherStationDailyAverageCalculation(WeatherUndergroundInstance weatherStation,ResourceRange range) 
    {
        if(weatherStation.isCompressed()) {
            weatherStation.createValuesVector();
        }
        values= weatherStation.getValuesForResourceRange(range);
    }
    
    /**
     * Method to calculate daily minimum value.
     * @param variableKey The name of the variable.
     * @return The minimum value of the data.
     */
    public double MinimumValue(String variableKey)
    {
         Vector<Number> data=null;
        for(WeatherStationVariable wsv:values)
        {
            if(wsv.getVariableKey().equals(variableKey)){
              data=wsv.getValues();  
               break; 
            }
        }
        Number minimum=data.get(0);
        for(int i=1;i<data.size();i++)
            if(minimum.doubleValue()>data.get(i).doubleValue())
                minimum=data.get(i);
        return minimum.doubleValue();
    }
    
    /**
     * Method to calculate daily maximum value.
     * @param variableKey The name of the variable.
     * @return The maximum value of the data.
     */
     public double MaximumValue(String variableKey)
    {
        Vector<Number> data=null;
        for(WeatherStationVariable wsv:values)
        {
            if(wsv.getVariableKey().equals(variableKey))
            {
               data=wsv.getValues();  
               break; 
            }
        }
        Number maximum=data.get(0);
        for(int i=1;i<data.size();i++)
            if(maximum.doubleValue()<data.get(i).doubleValue())
                maximum=data.get(i);
        return maximum.doubleValue();
    }
     
    /**
     * Method to calculate daily average value.
     * @param variableKey The name of the variable.
     * @return The average value of the data.
     */
     public double AverageValue(String variableKey)
    {
        Vector<Number> data=null;
        for(WeatherStationVariable wsv:values)
        {
            if(wsv.getVariableKey().equals(variableKey)){
              data=wsv.getValues();  
               break; 
            }
        }
        double total=0;
        for(int i=0;i<data.size();i++)
               total+=data.get(i).doubleValue();
        return total/data.size();
    }
     /**
     * Method to calculate daily total value and it is using to find total 
     * daily precipitation.
     * @param variableKey The name of the variable.
     * @return The total value of the data.
     */
     public double TotalValue(String variableKey)
    {
        Vector<Number> data=null;
        for(WeatherStationVariable wsv:values)
        {
            if(wsv.getVariableKey().equals(variableKey)){
              data=wsv.getValues();  
               break; 
            }
        }
        double total=0;
        for(int i=0;i<data.size();i++)
               total+=data.get(i).doubleValue();
        return total;
    }
}
