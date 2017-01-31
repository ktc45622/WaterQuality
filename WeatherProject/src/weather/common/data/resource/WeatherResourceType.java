package weather.common.data.resource;

import java.io.Serializable;

/**
 * This enumerated type specifies the valid resource types
 * for our weather resources. 
 * 
 * The valid resource types are:
 *  undefined, weather_camera, weather_site, weather_station, 
 *  weather_station_values, text_file,
 *  weather_station_daily_log, weather_station_hourly_log, weather_movie,
 *  weather_image.
 *  The default is undefined. 
 * 
 *@author Bloomsburg University Software Engineering
 *@author Jacob Kelly (2007)
 *
 *@version Spring 2007
 */
public enum WeatherResourceType implements Serializable {
    
    /**
     * Indicates that the weather resource type is undefined.
     */
    undefined,
    
    /**
     * Indicates that the weather resource type is a WeatherCamera.
     */
    WeatherCamera,
    
    /**
     * Indicates that the weather resource type is a WeatherMapLoop.
     */
    WeatherMapLoop,
    
    /**
     * Indicates that the weather resource type is a WeatherStation.
     */
    WeatherStation,
    
    /**
     * Indicates that the weather resource type is a WeatherStationValues.
     */
    WeatherStationValues,
    
    /**
     * Indicates that the weather resource type is a WeatherImage (any legal image type).
     */
    WeatherImage,
    
    /**
     * Indicates that the weather resource type is a WeatherNotes.
     * TODO:  delete this one.
     */
    WeatherNotes,
   
    /**
     * Indicates that the weather resource type is a WeatherStationDailyLog.
     * TODO: delete this one.
     */
  //  WeatherStationDailyLog,
    
    /**
     * Indicates that the weather resource type is a WeatherStationHourlyLog.
     * TODO: delete this one.
     */
  //  WeatherStationHourlyLog,
    
    /**
     * Indicates that the weather resource type is a Text File.
     */
    TextFile,
    AVIVideo,
    MP4Video,
  //  WeatherString, // TODO: Where is this one used, if not -- then delete it
    WeatherUnderground,
    webpage, // make sure database can handle this addition
    zip; // make sure database can handle this addition

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun documentation
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     */
    private static final long serialVersionUID = 1L;
}
