package weather.serverside.utilities;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceCollectionSpan;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.ResourceTimeManager;

/**
 * Gets the start and end collection times for a given resource and a given date.
 * This can be modified from the actual start and stop times.
 *
 * In instances where the code determines the sun does not set, this class will 
 * report the stop time at 23:59:59 p.m.
 *
 * In instances where the code determines the sun does not rise, this class
 * will report it rises at 12:00:00 a.m.
 *
 * 
 * @author Bloomsburg University Software Engeneering
 * @author Dr. Curt Jones
 * @author David Reichert (2008)
 * @author Xianrui Meng (2010)
 * @author Eric Subach (2010)
 * @version Spring 2010
 */
public class ResourceCollectionTimeUtility {
    /**
     * Determines the time to start collecting data for the given date and resource.
     * The start time returned is determined by subtracting our adjustment
     * time factor from the actual time. We then truncate the minute value to
     * zero. If there is no start, we return the start time as 12:00 a.m.
     * e.g. 6:50 start will be 5:50 with 1 hour (our normal adjustment)
     * subtraction. We then truncate the minute value to zero and start
     * collecting data at 5:00 a.m.        
     * @param resource User input resource.
     * @param date The time being checked, held in a 
     * <code>GregorianCalendar</code>.
     * @return The start time date.
     */
    public static GregorianCalendar getCollectionStartTime(Resource resource, GregorianCalendar date) {
        // Time of start.
        GregorianCalendar startTime = null;
        
        // How much extra time (minutes), before start do
        // we want to collect data for. Valid range [0-60].
        int adjustTimeFactor;

        // Collection span time.
        ResourceCollectionSpan collectTime = resource.getCollectionSpan();

        // Take action depending on the collection time.
        if (collectTime == ResourceCollectionSpan.FullTime) {
            // return 12:00 a.m. on the current day.

            startTime = new GregorianCalendar();
            startTime.setTimeZone(resource.getTimeZone().getTimeZone());
            startTime.setTimeInMillis(date.getTimeInMillis());

            startTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
            startTime.set(GregorianCalendar.MINUTE, 0);
            startTime.set(GregorianCalendar.SECOND, 0);
            startTime.set(GregorianCalendar.MILLISECOND, 0);
        }
        else if (collectTime == ResourceCollectionSpan.SpecifiedTimes) {
            // return start time stored in resouce object set to the
            // current day.

            startTime = new GregorianCalendar();
            startTime.setTimeZone(resource.getTimeZone().getTimeZone());
            startTime.setTimeInMillis(date.getTimeInMillis());
           
            startTime.set(GregorianCalendar.HOUR_OF_DAY, resource
                    .getStartTime());
            startTime.set(GregorianCalendar.MINUTE, 0);
            startTime.set(GregorianCalendar.SECOND, 0);
            startTime.set(GregorianCalendar.MILLISECOND, 0);
        }
        else if (collectTime == ResourceCollectionSpan.DaylightHours) {
            SunriseSunsetAlgorithm calculator;

            calculator = getCalculator(resource, date);

            // How much to adjust collection time.
            adjustTimeFactor = getAdjustTimeFactor();


            // Calculate start time.
            startTime = new GregorianCalendar();
            startTime.setTimeInMillis(calculator.getSunrise());

            // If there is a start.
            if (startTime.getTimeInMillis() != 0) {
                // Adjust time unless doing so would change the date to the previous day.
                if (startTime.get(GregorianCalendar.HOUR_OF_DAY) > 0) {
                    startTime.add(GregorianCalendar.MINUTE, -adjustTimeFactor);
                }
            }
            // If no start, set time to 12:00 a.m. which is 00:00:00.
            else {
                // Use the date given.
                startTime.setTimeInMillis(date.getTimeInMillis());
                startTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
                // Can't add in any offset, already at start of day
            }

            // Times start on the hour.
            startTime.set(GregorianCalendar.MINUTE, 0);
            startTime.set(GregorianCalendar.SECOND, 0);
            startTime.set(GregorianCalendar.MILLISECOND, 0);


            Debug.println("Collection start time is "
                    + startTime.get(GregorianCalendar.HOUR_OF_DAY)
                    + ":"
                    + startTime.get(GregorianCalendar.MINUTE)
                    + ":"
                    + startTime.get(GregorianCalendar.SECOND)
                    + " "
                    + startTime.getTime()
                    );
        }

        return (startTime);
    }


    /**
     * Determines the time to stop collecting data for the given date and resource.
     * The stop time is obtained by adding one hour to the actual time and 
     * then setting the minute value to 59. If there
     * is no stop, we return the stop time for 23:59:59.
     * If the code calculates a 8:22 stop time, we will then add 1 hour to
     * the stop time.
     *
     * @param resource User input resource.
     * @param date The time being checked, held in a 
     * <code>GregorianCalendar</code>.
     * @return Sunset time date.
     */
    public static GregorianCalendar getCollectionStopTime(Resource resource, 
            GregorianCalendar date) {
        // Time of stop.
        GregorianCalendar stopTime = null;

        // How much extra time (minutes), after stop do
        // we want to collect data for. Valid range [0-60].
        int adjustTimeFactor;

        // Collection span time.
        ResourceCollectionSpan collectTime = resource.getCollectionSpan();


        // Take action depending on the collection time.

        if (collectTime == ResourceCollectionSpan.FullTime) {
            // return 23:59:59 p.m. on the current day.
            stopTime = new GregorianCalendar();
            stopTime.setTimeZone(resource.getTimeZone().getTimeZone());
            stopTime.setTimeInMillis(date.getTimeInMillis());

            stopTime.set(GregorianCalendar.HOUR_OF_DAY, 23);
            stopTime.set(GregorianCalendar.MINUTE, 59);
            stopTime.set(GregorianCalendar.SECOND, 59);
            stopTime.set(GregorianCalendar.MILLISECOND, 999);
        }
        else if (collectTime == ResourceCollectionSpan.SpecifiedTimes) {
            // return end time stored in resouce object set to the
            // current day.

            stopTime = new GregorianCalendar();
            stopTime.setTimeZone(resource.getTimeZone().getTimeZone());
            stopTime.setTimeInMillis(date.getTimeInMillis());
            
            // must decrement hour, because e. g., 8:00 brcomes 7:59:59.999.
            int hour = resource.getEndTime() - 1;
            // must correct for the day's end being stored as 0.
            if(hour == -1) {
                hour = 23;
            } 

            stopTime.set(GregorianCalendar.HOUR_OF_DAY, hour);
            stopTime.set(GregorianCalendar.MINUTE, 59);
            stopTime.set(GregorianCalendar.SECOND, 59);
            stopTime.set(GregorianCalendar.MILLISECOND, 999);
        }
        else if (collectTime == ResourceCollectionSpan.DaylightHours) {
            SunriseSunsetAlgorithm calculator;

            calculator = getCalculator(resource, date);

            // How much to adjust collection time.
            adjustTimeFactor = getAdjustTimeFactor();

            // Calculate stop time.
            stopTime = new GregorianCalendar();
            stopTime.setTimeInMillis(calculator.getSunset());

            // If there is stop.
            if (stopTime.getTimeInMillis() != 0) {   
                // Adjust time unless doing so would change the date to the next day.
                if (stopTime.get(GregorianCalendar.HOUR_OF_DAY) < 23) {
                    stopTime.add(GregorianCalendar.MINUTE, adjustTimeFactor);
                }
            }
            // No stop; set time to 23:59:59.
            else {
                stopTime.setTimeInMillis(date.getTimeInMillis());
                stopTime.set(GregorianCalendar.HOUR_OF_DAY, 23);
            }
            
            // Complete the hour -- Movies will be made for this hour
            stopTime.set(GregorianCalendar.MINUTE, 59);
            stopTime.set(GregorianCalendar.SECOND, 59);
            stopTime.set(GregorianCalendar.MILLISECOND, 999);

            Debug.println("Collection stop time is "
                    + stopTime.get(GregorianCalendar.HOUR_OF_DAY)
                    + ":"
                    + stopTime.get(GregorianCalendar.MINUTE)
                    + ":"
                    + stopTime.get(GregorianCalendar.SECOND)
                    + " "
                    + stopTime.getTime());
        }

        return (stopTime);
    }


    /**
     * Determines if the time specified to collect data for this resource is reasonable for the date specified.
     * 
     *  1). If the selected resource span is in daylight hours, return true if the hours are in the daytime.
     *  2). If the selected resource span is the whole day, always return true.
     *  3). If the selected resource span is some specified time by the user, return true
     *        if the current time is between the start and stop times.
     * @param resource The resource used to check the time of collection.
     * @param date The date that is being checked for validity.
     *
     * @return True if the data collection time specified for this resource is
     *              reasonable for the date specified, and false otherwise.
     */
    public static boolean validDataCollectionTime (Resource resource, GregorianCalendar date) {
        switch (resource.getCollectionSpan()) {

            case DaylightHours:
            case SpecifiedTimes:
                    
                //Get start time
                long start = getCollectionStartTime(resource, date)
                        .getTimeInMillis();

                //Get stop time
                long stop = getCollectionStopTime(resource, date)
                        .getTimeInMillis();

                //Get current time
                long now = date.getTimeInMillis();
                
                //return true if current time between start time and stop time.
                return start <= now && now <= stop;
                
            // If always collecting, any time is valid.
            case FullTime:
                return true;

            default:
                return (false);
        }
    }


    /**
     * Get the start/sunset calculator for a resource at a particular date.
     *
     * @param resource The resource to get the start/sunset calculator from.
     * @param calendar The date to get the start/sunset calculator for.
     * @return The requested start/sunset calculator for the resource on the 
     * specified date.
     */
    public static SunriseSunsetAlgorithm getCalculator (Resource resource, GregorianCalendar calendar)
    {
        SunriseSunsetAlgorithm calculator;
        float latitude;
        float longitude;
        TimeZone timeZone;
        latitude = resource.getLatitude();
        longitude = resource.getLongitude();
        timeZone = resource.getTimeZone().getTimeZone();
        
        

        // Algorithm for calculating start/sunset at location on date.
        calculator = new SunriseSunsetAlgorithmNOAAImpl(latitude,
                                                        longitude,
                                                        calendar.getTimeInMillis(),
                                                        timeZone);

        return (calculator);
    }


    /**
     * Get how much time before start and after stop to collect data for.
     *
     * Valid range is [0-60].
     *
     * @return Time adjustment factor.
     */
   private static int getAdjustTimeFactor ()
    {
        int adjustTimeFactor = Integer.parseInt(PropertyManager.getGeneralProperty("daylightOffset"));

        if (adjustTimeFactor > 60) {
            adjustTimeFactor = 60;
        }
        else if (adjustTimeFactor < 0) {
            adjustTimeFactor = 0;
        }

        return (adjustTimeFactor);
    }
}
