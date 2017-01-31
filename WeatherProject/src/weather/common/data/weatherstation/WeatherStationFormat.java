package weather.common.data.weatherstation;

/**
 * An enumeration containing the possible types of weather station formats.
 * 
 * @author Mitch Gordner(2012)
 * @version Spring 2012
 * 
 */
public enum WeatherStationFormat {
    BUWeatherStationFormat("BU_Weather_Station_Format"),
    WeatherUndergroundFormat("Weather_Underground_Format");
    
    private String stringValue;

    WeatherStationFormat(String stringValue) {
        this.stringValue = stringValue;
    }
    
    @Override
    public String toString(){
        return stringValue;
    }
       
    /**
     * Return the WeatherStationFormat with the corresponding string value.
     *
     * @param value The string value of the enumeration.
     * @return The enumeration with the string value.
     */
    public static WeatherStationFormat getEnum (String value) {
       WeatherStationFormat format = null;

        for (WeatherStationFormat f : WeatherStationFormat.values ()) {
            if (f.stringValue.equals(value))
            {
                format = f;
                break;
            }
        }
        return (format);
    }
    
    private static final long serialVersionUID = 1L;
};
