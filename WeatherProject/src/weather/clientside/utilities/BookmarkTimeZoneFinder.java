package weather.clientside.utilities;

/**
 * This class contain a static utility method to find the time zone of a 
 * bookmark based on the time zone of its camera resource.
 * @author Brian Bankes
 */

import java.util.TimeZone;
import weather.GeneralService;
import weather.common.data.bookmark.Bookmark;
import weather.common.data.resource.Resource;

public class BookmarkTimeZoneFinder {
    
    /**
     * Returns time zone of a bookmark or event based on the time zone of its
     * camera resource.
     * @param bookmark The bookmark or event.
     * @param genService The application's general service.
     * @return The time zone of a bookmark or event based on the time zone of
     * its camera resource. This will be the local time zone if the bookmark or
     * event has no camera or if the camera no longer exists.
     */
    public static TimeZone findTimeZone(Bookmark bookmark,
            GeneralService genService) {
        //Get cameara resource of bookmark, which can be if there is no camera
        //or the camera is not found.
        Resource camera = genService.getDBMSSystem().getResourceManager()
                .getWeatherResourceByNumber(bookmark
                .getWeatherCameraResourceNumber());
        
        //Return the resource time zone or, if the resource is null, the local
        //time zone.
        if (camera == null) {
            return TimeZone.getDefault();
        } else {
            return camera.getTimeZone().getTimeZone();
        } 
    }
    
}
