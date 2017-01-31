package weather.serverside.utilities;

/**
 * Enumeration that specifies the long and short names of the
 * weather services in Windows.
 *
 * @author Eric Subach (2010)
 * @version Spring 2010
 */
public enum WeatherServiceNames {
    MOVIE ("WeatherProject Movie System", "WeatherMovie"),
    RETRIEVAL ("WeatherProject Retrieval System", "WeatherRetrieval"),
    STORAGE ("WeatherProject Storage System", "WeatherStorage"),
    WATCHDOG ("WeatherProject Server Watchdog", "Server Watchdog"),
    WEATHERDB ("WeatherProject Database Server", "WeatherDB"),
    UNKNOWN ("Unknown", "Unknown");


    private String longName;
    private String shortName;


    WeatherServiceNames (String longName, String shortName) {
        this.longName = longName;
        this.shortName = shortName;
    }


    /**
     * Gets the short name.
     *
     * @return the short name
     */
    public String getShortName () {
        return (shortName);
    }


    /**
     * Gets the long name.
     *
     * @return the long name
     */
    public String getLongName () {
        return (longName);
    }
}
