
package weather.common.data.weatherstation;

import java.io.Serializable;

/**
 * This enumeration is list of under ground weather station variables using 
 * for calculating daily minimum, daily maximum and daily average values.
 * @author alinson(2012).
 */

public enum WeatherStationDailyAverageType implements Serializable{
    /*
     * this variable for temperature.
     */
    outsideTemperature,
     /*
     * this variable for dewpoint.
     */
    dewPoint,
     /*
     * this variable for humidity.
     */
    humidity,
     /*
     * this variable for barometric pressure.
     */
    rawBarometer,
     /*
     * this variable for windspeed.
     */
    windSpeed,
     /*
     * this variable for solar radiation.
     */
    solar,
     /*
     * this variable for hourly rain fall.
     */
    hourlyRainfall,
     /*
     * this variable for wind gust.
     */
    windGust,
     /*
     * this variable for daily rain fall.
     */
    dailyRainfall;
    private static final long serialVersionUID = 1L;
    
    
}
