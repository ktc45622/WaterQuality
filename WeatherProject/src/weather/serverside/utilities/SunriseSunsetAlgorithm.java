package weather.serverside.utilities;

/**
 * Determines the times of sunrise and sunset for a given latitude, longitude, 
 * a date (which includes a time element), and a time zone.
 *
 * NOTE: certain dates may not have a sunset and certain dates may not have a
 *        sunrise
 * NOTE: longitude is positive for East and negative for West
 *
 * NOTE: It is the responsibility of the implementing class to make the
 *        necessary changes to the date to account for differences in the
 *        two classes, GregorianCalendar and Date.
 *
 * NOTE: Many methods of the Date class are deprecated. Use the methods
 *        Calendar.getTimeInMillis (),
 *        Calendar.set (int field, int value),
 *        Date.getTime (),
 *        and the constructor Date (long date)
 *        for best results when converting.
 *
 * NOTE: When using DateFormat, you must also set the time zone using its
 *        DateFormat.setTimeZone () method.
 *
 * NOTE: Implementing classes must set the sunrise/sunset times in the time zone
 *        of the location given so that times may be compared correctly.
 *
 * @author Eric Subach (2010)
 */
public interface SunriseSunsetAlgorithm {
    /**
     * Return the sunrise time in the local time zone of the location on the 
     * given date.
     * @return Date and time of sunrise expressed as a long or, if there is no 
     * sunrise on the given date, return 0.
     */
    public long getSunrise ();

    /**
     * Return the sunset time in the local time zone of the location on the 
     * given date.
     * @return Date and time of sunset expressed as a long or, if there is no 
     * sunset on the given date, return 0.
     */
    public long getSunset ();

    /**
     * Returns true if the time element of the given date is in daytime, false
     * otherwise.
     * @return True if the time element of the given date is in daytime, False
     * otherwise.
     */
    public boolean isDaytime ();
}
