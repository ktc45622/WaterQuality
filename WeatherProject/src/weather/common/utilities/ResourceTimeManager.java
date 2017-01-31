package weather.common.utilities;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;
import weather.common.data.resource.ResourceRange;

/**
 * This class acts as a time utility for referencing time values; It keeps track
 * of the time span when loading the program in a selective start.
 *
 * After the program loads, this class does NOT keep track of the resource range
 * currently loaded in the GUI. The range is kept within each panel manager.
 * Many of the classes in the application keep track of their own resource range
 * but <b>the range that is contained in this class should be referred to as the
 * default range for the application. It should be kept up to date with the
 * range that is contained in the main application window.</b>
 *
 *
 * @author Bloomsburg University Software Engieneering
 * @author Joe Sharp (2009)
 * @author Dustin Jones
 */
public class ResourceTimeManager {

    private static ResourceRange resourceRange = getDefaultRange();
    // Account for differences in times between movie instances and the resource
    // key for the same movie.
    public static final long TOLERANCE = 5000;
    public static final long MILLISECONDS_PER_DAY = 86400000;
    public static final long MILLISECONDS_PER_HOUR = 3600000;
    public static final long MILLISECONDS_PER_MINUTE = 60000;
    public static final long MILLISECONDS_PER_SECOND = 1000;
    public static final long MILLISECONDS_PER_NONLEAP_YEAR =
            MILLISECONDS_PER_DAY * 365;
    public static final long MILLISECONDS_PER_LEAP_YEAR =
            MILLISECONDS_PER_DAY * 366;

    /**
     * Given the start and end milliseconds, set the resourceRange of this class
     * to those values. The start and end dates are first set, then used to set
     * call the actual method that sets the resource range.
     *
     * @param startMilliseconds the beginning time for the resource range.
     * @param endMilliseconds the ending time for the resource range.
     */
    public static void setResourceRangeMilliseconds(long startMilliseconds,
            long endMilliseconds) {
        setResourceRange(new Date(startMilliseconds),
                new Date(endMilliseconds));
    }

    /**
     * Retrieves the start milliseconds for the current resource time span.
     *
     * @return the start milliseconds for the current resource time span.
     */
    public static long getResourceStartMilliseconds() {
        return resourceRange == null ? null : resourceRange.getStartTime().getTime();
    }

    /**
     * Retrieves the end milliseconds for the current resource time span.
     *
     * @return the end milliseconds for the current resource time span.
     */
    public static long getResourceEndMilliseconds() {
        return resourceRange == null ? null : resourceRange.getStopTime().getTime();
    }

    /**
     * Retrieves the start date for the current resource time span.
     *
     * @return the start date.
     */
    public static Date getResourceStartDate() {
        return resourceRange == null ? null : resourceRange.getStartTime();
    }

    /**
     * Retrieves the end date for the current resource time span.
     *
     * @return the end date.
     */
    public static Date getResourceEndDate() {
        return resourceRange == null ? null : resourceRange.getStopTime();
    }

    /**
     * Gets the day on which the start time for the current resource time span
     * falls on. This is returned as a long integer value which represents the
     * millisecond value of 12am on the day on which the start time falls within
     * the given
     * <code>TimeZone</code>.
     *
     * @param zone The given time zone.
     * @return milliseconds representing 12am on the day on which the start time
     * for the current resource time span falls within the
     * given <code>TimeZone</code>.
     */
    public static long getResourceStartDay(TimeZone zone) {
        if (resourceRange == null) {
            return 0;
        } else {
            return getStartOfDayFromMilliseconds(resourceRange.getStartTime()
                    .getTime(), zone);
        }
    }

    /**
     * Gets the day on which the end time for the current resource time span
     * falls on. This is returned as a long integer value which represents the
     * millisecond value of 12am on the day on which the end time falls within
     * the given
     * <code>TimeZone</code>.
     *
     * @param zone The given time zone.
     * @return milliseconds representing 12am on the day on which the end time
     * for the current resource time span falls within the
     * given <code>TimeZone</code>.
     */
    public static long getResourceEndDay(TimeZone zone) {
        if (resourceRange == null) {
            return 0;
        } else {
            return getStartOfDayFromMilliseconds(resourceRange.getStopTime()
                    .getTime(), zone);
        }
    }

    /**
     * Given a resource range, this method sets all time values relative to that
     * range.
     *
     * @param range the resource range used to set all time values.
     */
    public static void setResourceRange(ResourceRange range) {
        resourceRange = range;
    }

    /**
     * Gets a resource range object that represents the current resource time
     * span for the application. This object will be completely independent of
     * the resource object that is stored in this class, in order to protect the
     * resource object of this class from being inadvertently changed.
     *
     * @return a resource range object that represents the current resource time
     * span for the application.
     */
    public static ResourceRange getResourceRange() {
        return resourceRange;
    }

    /**
     * Given a time in milliseconds, this method will calculate and return the
     * day that this time falls on. (Returns 12am exactly on the day that time
     * falls on within the given
     * <code>TimeZone</code>.)
     *
     * @param zone The given time zone.
     * @param millis the time in milliseconds for which to find the day.
     * @return the date in milliseconds that the given time falls on within the
     * given <code>TimeZone</code>.
     */
    public static long getStartOfDayFromMilliseconds(long millis,
            TimeZone zone) {
        return getStartOfDayCalendarFromMilliseconds(millis, zone)
                .getTimeInMillis();
    }

    /**
     * Mimics the above
     * <code>getStartOfDayFromMilliseconds</code> method, but instead of
     * returning the milliseconds, a
     * <code>Date</code> object created from those milliseconds is returned.
     *
     * @param millis the time in milliseconds for which to find the day.
     * @param zone The given time zone.
     * @return A <code>Date</code> object representing the beginning of the day
     * the milliseconds fall upon within the given <code>TimeZone</code>.
     *
     */
    public static Date getStartOfDayDateFromMilliseconds(long millis,
            TimeZone zone) {
        return new Date(getStartOfDayFromMilliseconds(millis, zone));
    }

    /**
     * Mimics the above
     * <code>getStartOfDayFromMilliseconds</code> method, but instead of
     * returning the milliseconds, a
     * <code>Calendar</code> object created from those milliseconds is returned.
     *
     * @param millis the time in milliseconds for which to find the day.
     * @param zone The given time zone.
     * @return A <code>Calendar</code> object representing the beginning of the
     * day the milliseconds fall upon within the given <code>TimeZone</code>.
     */
    public static Calendar getStartOfDayCalendarFromMilliseconds(long millis,
            TimeZone zone) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(zone);
        cal.setTimeInMillis(millis);
        cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
        cal.set(GregorianCalendar.MINUTE, 0);
        cal.set(GregorianCalendar.SECOND, 0);
        cal.set(GregorianCalendar.MILLISECOND, 0);

        return cal;
    }

    /**
     * Calculates the end of the day on which the time given in millis falls
     * within the given
     * <code>TimeZone</code>.
     *
     * @param millis the time in milliseconds
     * @param zone The given time zone.
     * @return the time of the end of the day within the given
     * <code>TimeZone</code>.
     */
    public static long getEndOfDayFromMilliseconds(long millis,
            TimeZone zone) {
        return getEndOfDayCalendarFromMilliseconds(millis, zone)
                .getTimeInMillis();
    }

    /**
     * Calculates the end of the day on which millis falls within the given
     * <code>TimeZone</code>.
     *
     * @param millis the time in milliseconds
     * @param zone The given time zone.
     * @return a Date object representing the time of the end of the day within
     * the given <code>TimeZone</code>.
     */
    public static Date getEndOfDayDateFromMilliseconds(long millis,
            TimeZone zone) {
        return new Date(getEndOfDayFromMilliseconds(millis, zone));
    }

    /**
     * Calculates the end of the day upon which millis falls within the given
     * <code>TimeZone</code> and returns a
     * <code>Calendar</code> representing the day.
     *
     * @param millis The time in milliseconds from the epoch.
     * @param zone The given time zone.
     * @return A <code>Calendar</code> representing the end of the day specified
     * within the given <code>TimeZone</code>.
     */
    public static Calendar getEndOfDayCalendarFromMilliseconds(long millis,
            TimeZone zone) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(zone);
        cal.setTimeInMillis(millis);
        cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
        cal.set(GregorianCalendar.MINUTE, 59);
        cal.set(GregorianCalendar.SECOND, 59);
        cal.set(GregorianCalendar.MILLISECOND, 999);

        return cal;
    }
    
    /**
     * For a time given in milliseconds, returns the last millisecond of the day
     * for which video data should be available for the day containing that 
     * moment in time or 0 if none should be available.
     * 
     * @param millis The time in milliseconds from the epoch.
     * @param zone A <code>TizeZone</code> object necessary for determining when
     * the day ends.
     * @return The last millisecond of the day for which video data should be
     * available for the day containing that moment in time or null if none
     * should be available.
     */
    public static long getLastMilliOfExpectedVideo(long millis, TimeZone zone) {
        /* The idea is that we give the storage system ten minutes grace the
         * movie for 1:00 is ready at 1:10.
         */
        long millisecondsToSubtract
                = Integer.parseInt(PropertyManager
                .getGeneralProperty("rangeRetrieveGracePeriod"))
                * ResourceTimeManager.MILLISECONDS_PER_MINUTE;

        /* We must compare against a time that is millisecondsToSubtract
         * milliseconds in the past because that is alwaya the time that is
         * during the last available hour of video for the day. 
         */
        long timeToCompare = System.currentTimeMillis() 
                - millisecondsToSubtract;
        
        /* Get the end of the day in question and store it as the potential time
         * to be used for the return object. 
         */
        long returnMillis = getEndOfDayFromMilliseconds(millis, zone);
        
        /* Subtract from return value until we should have the video. */
        while (returnMillis > timeToCompare) {
            returnMillis -= ResourceTimeManager.MILLISECONDS_PER_HOUR;
        }
        
        /* Must compare result against start of day.  If it is before the day 
         * starts, return 0.
         */
        if (returnMillis < getStartOfDayFromMilliseconds(millis, zone)) {
            return 0;
        }
        
        /* Return time as Date object, as we know there is one. */
        return returnMillis;
    }
    
    /**
     * This function is designed to change the <code>TimeZone</code> of a given
     * <code>Date</code> while preserving the other data e. g. 1:30 PM ET
     * becomes 1:30 PM MT.
     * 
     * @param input The <code>Date</code> to be converted.
     * @param oldZone The input <code>TimeZone</code>.
     * @param newZone The output <code>TimeZone</code>.
     * @return The <code>Date</code> converted to the new <code>TimeZone</code>.
     */
    public static Date changeDateTimeZone(Date input, TimeZone oldZone,
            TimeZone newZone) {
        /* Calendar for use with calculations. */
        Calendar cal = new GregorianCalendar();
        
        /** 
         * Set calendar to input values so data e. g. hour and minute can be 
         * retrieved.
         */ 
        cal.setTimeZone(oldZone);
        cal.setTime(input);
        
        /* Get calendar fields. */
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int millisecond = cal.get(Calendar.MILLISECOND);
        
        /* Change calendar timesone and reenter data. */
        cal.setTimeZone(newZone);
        cal.set(year, month, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        
        /* Return value of calendar as a date. */
        long calMillis = cal.getTimeInMillis();
        return new Date(calMillis);
    }

    /**
     * Given a start and end date, sets the resource range to these dates.
     * Instantiates the resource range value if needed.
     *
     * @param startDate the start date to set the start date of the resource
     * range to.
     * @param endDate the end date to set the end date of the resource range to.
     */
    private static void setResourceRange(Date startDate, Date endDate) {
        if (resourceRange == null) {
            resourceRange = new ResourceRange(startDate, endDate);
        } else {
            resourceRange.setStartTime(startDate);
            resourceRange.setStopTime(endDate);
        }
    }

    /**
     * Creates a new Date object with the given milliseconds and returns
     * getFormattedHourString(Date date, TimeZone zone) with the new date.
     * MM/DD/YYYY HH:MM(AM/PM) Z
     *
     * @param milliseconds the milliseconds to format a string for.
     * @param zone The given time zone.
     * @return a string representation of the given milliseconds down to the
     * minute in the given <code>TimeZone</code>.
     */
    public static String getFormattedHourString(long milliseconds,
            TimeZone zone) {
        if (milliseconds == 0) {
            return "null";
        }
        return getFormattedHourString(new Date(milliseconds), zone);
    }

    /**
     * Given a date object, this method will extract all of the appropriate
     * values out of that object and format it into an easily read string
     * representation of that date, down to the minute. MM/DD/YYYY HH:MM(AM/PM)
     * Z
     *
     * @param date the date to format the string to.
     * @param zone The given time zone.
     * @return a string representation of the given date, down to the minute, in
     * the given <code>TimeZone</code>.
     */
    public static String getFormattedHourString(Date date, TimeZone zone) {
        if (date == null) {
            return "null";
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(zone);
        cal.setTime(date);
        String timeString = "";

        //Get MM/DD/YYYY
        timeString += (cal.get(GregorianCalendar.MONTH) + 1) + "/"
                + cal.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                + cal.get(GregorianCalendar.YEAR) + " ";


        //Get HH:
        int hour = cal.get(GregorianCalendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        timeString += hour + ":";

        //Get MM:
        int minute = cal.get(GregorianCalendar.MINUTE);
        if (minute < 10) {
            timeString += "0";
        }
        timeString += minute;

        //Get AM_PM
        timeString += (cal.get(GregorianCalendar.AM_PM) == 0 ? "AM" : "PM");

        //Get time zone with GMT replaced by UTC
        timeString += " " + cal.getTimeZone().getDisplayName(cal.getTimeZone()
                .inDaylightTime(date), TimeZone.SHORT).replaceAll("GMT", "UTC");

        return timeString;
    }

    /**
     * Creates a new Date object with the given milliseconds and returns
     * getFormattedTimeString(Date date, TimeZone zone) with the new date.
     * MM/DD/YYYY HH:MM:SS(AM/PM) Z
     *
     * @param milliseconds the milliseconds to format a string for.
     * @param zone The given time zone.
     * @return a string representation of the given milliseconds down to the
     * minute in the given <code>TimeZone</code>.
     */
    public static String getFormattedTimeString(long milliseconds,
            TimeZone zone) {
        if (milliseconds == 0) {
            return "null";
        }
        return getFormattedTimeString(new Date(milliseconds), zone);
    }

    /**
     * Given a date object, this method will extract all of the appropriate
     * values out of that object and format it into an easily read string
     * representation of that date, down to the second. MM/DD/YYYY
     * HH:MM:SS(AM/PM) Z
     *
     * @param date the date to format the string to.
     * @param zone The given time zone.
     * @return a string representation of the given date, down to the second, in
     * the given <code>TimeZone</code>.
     */
    public static String getFormattedTimeString(Date date, TimeZone zone) {
        if (date == null) {
            return "null";
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(zone);
        cal.setTime(date);
        String timeString = "";

        //Get MM/DD/YYYY
        timeString += (cal.get(GregorianCalendar.MONTH) + 1) + "/"
                + cal.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                + cal.get(GregorianCalendar.YEAR) + " ";


        //Get HH
        int hour = cal.get(GregorianCalendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        timeString += hour + ":";

        //Get MM:
        int minute = cal.get(GregorianCalendar.MINUTE);
        if (minute < 10) {
            timeString += "0";
        }
        timeString += minute;
        timeString += ":";

        //Get SS
        int second = cal.get(GregorianCalendar.SECOND);
        if (second < 10) {
            timeString += "0";
        }
        timeString += second;

        //Get AM_PM
        timeString += (cal.get(GregorianCalendar.AM_PM) == 0 ? "AM" : "PM");

        //Get time zone with GMT replaced by UTC
        timeString += " " + cal.getTimeZone().getDisplayName(cal.getTimeZone()
                .inDaylightTime(date), TimeZone.SHORT).replaceAll("GMT", "UTC");

        return timeString;
    }

    /**
     * Parses a time (in milliseconds) from a filename on the server.
     * NOTE: The time zone of the date in the file path must be given.
     *
     * @param filename the filename to parse.
     * @param zone The given time zone.
     * @return the time in the filename (in milliseconds)
     */
    public static long extractTimeFromFilename(String filename, TimeZone zone) {
        int dashIndex = filename.lastIndexOf("-");
        String ymd = filename.substring(dashIndex - 8, dashIndex);
        String time = filename.substring(dashIndex + 1);
        int y = Integer.parseInt(ymd.substring(0, 4));
        int m = Integer.parseInt(ymd.substring(4, 6));
        int d = Integer.parseInt(ymd.substring(6));
        int hour = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(2, 4));
        int sec = Integer.parseInt(time.substring(4, 6));
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTimeZone(zone);
        cal.set(y, m - 1, d, hour, min, sec);
        return cal.getTimeInMillis();
    }

    /**
     * Returns the number of days in a given month.
     *
     * @param month the month to find the number of days for...(0=Jan, 1=Feb,
     * etc...)
     * @param year the year of the mouth
     * @return the number of days in the month.
     */
    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;
            case 3:
            case 5:
            case 8:
            case 10:
                return 30;
            case 1:
                return isLeapYear(year) ? 29 : 28;
        }
        return -1;
    }

    /**
     * Returns whether or not a given year is a leap year.
     *
     * @param year the year to be tested.
     * @return whether or not a given year is a leap year.
     */
    private static boolean isLeapYear(int year) {
        if (year % 100 == 0) {
            return year % 400 == 0;
        } else {
            return year % 4 == 0;
        }
    }

    /**
     * Extracts the time from the path of one of our resources and returns that
     * time as the number of milliseconds past the epoch (January 1, 1970).
     *
     * Example path name: D:/BUWeatherStation/2010/March/9
     * NOTE: The time zone of the date in the file path must be given.
     *
     * @param directoryPath the file path to parse.
     * @param zone The given time zone.
     * @return the time (in milliseconds) extracted from the file path.
     */
    public static long extractTimeFromPath(String directoryPath, 
            TimeZone zone) {
        Vector<Integer> indexOfBacklashes = new Vector<>();
        for (int i = 0; i < directoryPath.length(); i++) {
            if (directoryPath.charAt(i) == '\\') {
                indexOfBacklashes.add(i);
            }
        }

        int size = indexOfBacklashes.size();
        String stringDay = directoryPath.substring(indexOfBacklashes.get(size - 1) + 1, directoryPath.length());
        String stringMonth = directoryPath.substring(indexOfBacklashes.get(size - 2) + 1, indexOfBacklashes.get(size - 1));
        String stringYear = directoryPath.substring(indexOfBacklashes.get(size - 3) + 1, indexOfBacklashes.get(size - 2));

        int day = Integer.parseInt(stringDay);
        int year = Integer.parseInt(stringYear);
        int month = 0;

        if (stringMonth.equals("January")) {
            month = 0;
        }
        if (stringMonth.equals("February")) {
            month = 1;
        }
        if (stringMonth.equals("March")) {
            month = 2;
        }
        if (stringMonth.equals("April")) {
            month = 3;
        }
        if (stringMonth.equals("May")) {
            month = 4;
        }
        if (stringMonth.equals("June")) {
            month = 5;
        }
        if (stringMonth.equals("July")) {
            month = 6;
        }
        if (stringMonth.equals("August")) {
            month = 7;
        }
        if (stringMonth.equals("September")) {
            month = 8;
        }
        if (stringMonth.equals("October")) {
            month = 9;
        }
        if (stringMonth.equals("November")) {
            month = 10;
        }
        if (stringMonth.equals("December")) {
            month = 11;
        }

        GregorianCalendar c = new GregorianCalendar();
        c.clear();
        c.setTimeZone(zone);
        c.set(year, month, day);
        return c.getTimeInMillis();
    }

    /**
     * Returns the default range for starting up the program.
     *
     * @return the default time range
     */
    public static ResourceRange getDefaultRange() {
        /* The idea is that we give the storage system ten minutes grace
         * the movie for 1:00 is ready at 1:10.
         */
        long millisecondsToSubtract =
                Integer.parseInt(PropertyManager.getGeneralProperty("rangeRetrieveGracePeriod"))
                * ResourceTimeManager.MILLISECONDS_PER_MINUTE;

        // Now the ending time is considered to be right before the program starts
        long endMilliseconds = System.currentTimeMillis() - millisecondsToSubtract;

        /* The following line sets the ending time time to the bottom of the
         * current hour by truncation .
         * The end time is now of the form hh:00:00 (the first time not used)
         */
        endMilliseconds = ((endMilliseconds
                / ResourceTimeManager.MILLISECONDS_PER_HOUR)
                * ResourceTimeManager.MILLISECONDS_PER_HOUR);

        int defaultStartHours = Integer.parseInt(PropertyManager.getLocalProperty("DEFAULT_START_HOURS"));

        Debug.println("Hours in default range: "
                + Integer.parseInt(PropertyManager.getLocalProperty("DEFAULT_START_HOURS")));

        long startMilliseconds = endMilliseconds
                - (ResourceTimeManager.MILLISECONDS_PER_HOUR * defaultStartHours);

        ResourceRange returnRange = new ResourceRange(new Date(startMilliseconds), new Date(endMilliseconds));

        return returnRange;
    }
}
