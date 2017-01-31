package weather.common.data.weatherstation;

import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.utilities.WeatherException;

/**
 * 
 * @author Alinson Antony
 * @version Spring 2012
 */


public class WeatherStationDailyAverageValues  {
    private WeatherStationDailyAverage average;
    private WeatherStationDailyAverageCalculation data;
    
    /**
     * Constructor.
     * @param resource A Resource object.
     * @param weatherStation A WeatherUndergroundInstance object.
     * @throws WeatherException
     */
    public WeatherStationDailyAverageValues(Resource resource,WeatherUndergroundInstance weatherStation,ResourceRange range) 
    {
        average=new WeatherStationDailyAverage(resource);
        data=new WeatherStationDailyAverageCalculation(weatherStation,range);
    }
    
    /**
     * Method to set all values to WeatherStationDailyAverage object.
     * @return All of the set values.
     * 
     * TODO: fix the return tag if necessary.
     */
    public WeatherStationDailyAverage setValues()
    {
        average.setTemperatureMax(data.MaximumValue(WeatherStationDailyAverageType.outsideTemperature.toString()));
        average.setTemperatureMin(data.MinimumValue(WeatherStationDailyAverageType.outsideTemperature.toString()));
        average.setTemperatureMedian(data.AverageValue(WeatherStationDailyAverageType.outsideTemperature.toString()));
        average.setDewPointMax(data.MaximumValue(WeatherStationDailyAverageType.dewPoint.toString()));
        average.setDewPointMin(data.MinimumValue(WeatherStationDailyAverageType.dewPoint.toString()));
        average.setDewPointMedian(data.AverageValue(WeatherStationDailyAverageType.dewPoint.toString()));
        average.setRelativeHumidityMin(data.MinimumValue(WeatherStationDailyAverageType.humidity.toString()));
        average.setRelativeHumidityMax(data.MaximumValue(WeatherStationDailyAverageType.humidity.toString()));
        average.setRelativeHumidityMedian(data.AverageValue(WeatherStationDailyAverageType.humidity.toString()));
        average.setPressureMin(data.MinimumValue(WeatherStationDailyAverageType.rawBarometer.toString()));
        average.setPressureMax(data.MaximumValue(WeatherStationDailyAverageType.rawBarometer.toString()));
        average.setPressureMedian(data.AverageValue(WeatherStationDailyAverageType.rawBarometer.toString()));
        average.setWindSpeedMin(data.MinimumValue(WeatherStationDailyAverageType.windSpeed.toString()));
        average.setWindSpeedMax(data.MaximumValue(WeatherStationDailyAverageType.windSpeed.toString()));
        average.setWindSpeedMedian(data.AverageValue(WeatherStationDailyAverageType.windSpeed.toString()));
        average.setSolarRadiationMin(data.MinimumValue(WeatherStationDailyAverageType.solar.toString()));
        average.setSolarRadiationMax(data.MaximumValue(WeatherStationDailyAverageType.solar.toString()));
        average.setSolarRadiationMedian(data.AverageValue(WeatherStationDailyAverageType.solar.toString()));
        average.setHourlyPrecipitationMin(data.MinimumValue(WeatherStationDailyAverageType.hourlyRainfall.toString()));
        average.setHourlyPrecipitationMax(data.MaximumValue(WeatherStationDailyAverageType.hourlyRainfall.toString()));
        average.setHourlyPrecipitationMedian(data.AverageValue(WeatherStationDailyAverageType.hourlyRainfall.toString()));
        average.setWindgustMin(data.MinimumValue(WeatherStationDailyAverageType.windGust.toString()));
        average.setWindgustMax(data.MaximumValue(WeatherStationDailyAverageType.windGust.toString()));
        average.setWindgustMedian(data.AverageValue(WeatherStationDailyAverageType.windGust.toString()));
        average.setDailyPrecipitation(data.TotalValue(WeatherStationDailyAverageType.dailyRainfall.toString()));
        return average;
    }
    
    
}
