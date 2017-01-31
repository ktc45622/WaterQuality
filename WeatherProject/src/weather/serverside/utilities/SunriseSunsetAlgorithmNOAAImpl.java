package weather.serverside.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import weather.common.utilities.ResourceTimeManager;

/**
 * Determines the times of sunrise and sunset for a given latitude, longitude,
 * date, and time zone.
 *
 * NOTE: certain dates may not have a sunset and certain dates may not have a
 *       sunrise
 * NOTE: longitude is positive for East and negative for West
 *       latitude is positive for North and negative for South
 * 
 * NOTE: This algorithm maintains its accuracy until 2100.
 * 
 * @author Eric Subach (2010)
 */
public class SunriseSunsetAlgorithmNOAAImpl implements SunriseSunsetAlgorithm {
    private final static double ZENITH = 90.83333333333333;

    private double SunriseTimeLocal;
    private double SunsetTimeLocal;

    // Holds output: Dates of sunrise/sunset.
    private GregorianCalendar sunrise;
    private GregorianCalendar sunset;

    // Is sunrise/sunset for given date.
    private boolean isSunrise;
    private boolean isSunset;

    private boolean isDaytime;

    // Input: Date getting info for.
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    TimeZone timeZone;
    double timeZoneDouble;
    
    /**
     * Uses the algorithm to determine sunrise and sunset times.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param dateAndTimeInMillis The date and time being tested, expressed in
     * milliseconds.
     * @param timeZone The time zone of the location.
     */
    public SunriseSunsetAlgorithmNOAAImpl (double latitude, double longitude,
            long dateAndTimeInMillis, TimeZone timeZone) {
        // Intermediate variables for sunrise/sunset calculation.
        double JulianDay;
        double JulianCentury;
        double MeanObliqEclipticDeg;
        double ObliqCorrDeg;
        double varY;
        double GeomMeanLongSunDeg;
        double GeomMeanAnomSunDeg;
        double EccentEarthOrbit;
        double EqOfTimeMinutes;
        double SunEqOfCtr;
        double SunTrueLongDeg;
        double SunAppLongDeg;
        double SunDeclinDeg;
        double HASunriseDeg;
        double SolarNoonLocal;

        // Julian Day Number and Julian Day.
        double JDN;
        double JD;


        // Parse time zone to create TimeZone object.
        this.timeZone = timeZone;
        timeZoneDouble = getRawOffsetInHours();

        // Parse current date into components.
        parseDateFields(dateAndTimeInMillis);

        /*
         * Gregorian to Julian Day Calculation.
         *
         * From: http://en.wikipedia.org/wiki/Julian_day Once again,
         * Wikipedia saves me when all other resources fail.
         */

        JDN = (1461 * (year + 4800 + (month - 14)/12))/4 + (367 * (month - 2 - 12 * ((month - 14)/12)))/12 - (3 * ((year + 4900 + (month - 14)/12)/100))/4 + day - 32075;
        JD = JDN + (hour - 12) / 24 + minute/1440 + second/86400;

        /*
         * Sunrise/Sunset in local time from Julian Day.
         *
         * From: NOAA Solar Calculations Day (New)
         *
         * Lots of magic numbers, don't try to understand them.
         */

        // Other method of calculating JD.
        JulianDay = JD;

        JulianCentury = (JulianDay - 2451545) / 36525;

        MeanObliqEclipticDeg = 23 + (26 + ((21.448 - JulianCentury * (46.815 + JulianCentury * (0.00059 - JulianCentury * 0.001813)))) / 60) / 60;
        ObliqCorrDeg = MeanObliqEclipticDeg + 0.00256 * Math.cos (Math.toRadians (125.04 - 1934.136 * JulianCentury));

        varY = Math.pow (Math.tan (Math.toRadians (ObliqCorrDeg / 2)), 2);

        GeomMeanLongSunDeg = 280.46646 + JulianCentury * (36000.76983 + JulianCentury * 0.0003032) % 360;
        GeomMeanAnomSunDeg = 357.52911 + JulianCentury * (35999.05029 - 0.0001537 * JulianCentury);

        EccentEarthOrbit = 0.016708634 - JulianCentury * (0.000042037 + 0.0001537 * JulianCentury);

        EqOfTimeMinutes = 4 * Math.toDegrees (varY * Math.sin (2 * Math.toRadians (GeomMeanLongSunDeg)) - 2 * EccentEarthOrbit * Math.sin (Math.toRadians (GeomMeanAnomSunDeg)) + 4 * EccentEarthOrbit * varY * Math.sin (Math.toRadians (GeomMeanAnomSunDeg)) * Math.cos (2 * Math.toRadians (GeomMeanLongSunDeg)) - 0.5 * Math.pow (varY, 2) * Math.sin (4 * Math.toRadians (GeomMeanLongSunDeg)) - 1.25 * Math.pow (EccentEarthOrbit, 2) * Math.sin (2 * Math.toRadians (GeomMeanAnomSunDeg)));

        SunEqOfCtr = Math.sin (Math.toRadians (GeomMeanAnomSunDeg)) * (1.914602 - JulianCentury * (0.004817 + 0.000014 * JulianCentury)) + Math.sin (Math.toRadians (2 * GeomMeanAnomSunDeg)) * (0.019993 - 0.000101 * JulianCentury) + Math.sin (Math.toRadians (3 * GeomMeanAnomSunDeg)) * 0.000289;

        SunTrueLongDeg = GeomMeanLongSunDeg + SunEqOfCtr;
        SunAppLongDeg = SunTrueLongDeg - 0.00569 - 0.00478 * Math.sin (Math.toRadians (125.04 - 1934.136 * JulianCentury));
        SunDeclinDeg = Math.toDegrees (Math.asin (Math.sin (Math.toRadians (ObliqCorrDeg)) * Math.sin (Math.toRadians (SunAppLongDeg))));

        HASunriseDeg = Math.toDegrees (Math.acos (Math.cos (Math.toRadians (90.833)) / Math.cos (Math.toRadians (latitude)) * Math.cos (Math.toRadians (SunDeclinDeg)) - Math.tan (Math.toRadians (latitude)) * Math.tan (Math.toRadians (SunDeclinDeg))));

        SolarNoonLocal = (720 - 4 * longitude - EqOfTimeMinutes + timeZoneDouble * 60) / 1440;

        SunriseTimeLocal = SolarNoonLocal - (HASunriseDeg * 4 / 1440);
        SunsetTimeLocal = SolarNoonLocal + (HASunriseDeg * 4 / 1440);

        sunrise = decimalToCalendar (SunriseTimeLocal);
        sunset = decimalToCalendar (SunsetTimeLocal);

        // Apply DST if necessary.
        fixForDST(dateAndTimeInMillis);

        ////////////////////////////////////////////////////////////////////////
        // Calculate isSunrise and isSunset.
        // Not sure if the following code is correct.

        isSunrise = true;
        isSunset = true;
        
        double sinDec = 0.39782 * Math.sin(Math.toRadians (SunTrueLongDeg));
        double cosDec = Math.cos (Math.toRadians (Math.asin(sinDec) * 180 / Math.PI));
        double cosH = (Math.cos(Math.toRadians (ZENITH)) - (sinDec * Math.sin(Math.toRadians (latitude)))) / (cosDec * Math.cos(Math.toRadians (latitude)));

        //cosH > 1 : the sun never rises on this location (on the specified date)
        if (cosH > 1) {
            isSunrise = false;
        }
        //cosH < -1 : the sun never sets on this location (on the specified date)
        if (cosH < -1) {
            isSunset = false;
        }

        ////////////////////////////////////////////////////////////////////////
        // Calculate isDaytime.
        if (getSunrise() == 0) {
            //no sunrise means its always night.
            isDaytime = false;
            return; 
        }
        
        if (getSunset() == 0) {
            //no sunsut means its always day.
            isDaytime = true;
            return;
        }

        //compare time to sunrise and sunset.
        isDaytime = dateAndTimeInMillis > getSunrise()
                && dateAndTimeInMillis < getSunset();
    }

    /**
     * Returns a <code>GregorianCalendar</code> representing sunrise time.
     * @return A <code>GregorianCalendar</code> representing sunrise time.
     */
    @Override
    public final long getSunrise () {
        if (isSunrise) {
            return (sunrise.getTimeInMillis());
        }
        else {
            return (0);
        }
    }

    /**
     * Returns a <code>GregorianCalendar</code> representing sunset time.
     * @return A <code>GregorianCalendar</code> representing sunset time.
     */
    @Override
    public final long getSunset () {
        if (isSunset) {
            return (sunset.getTimeInMillis());
        }
        else {
            return (0);
        }
    }

    /**
     * Returns whether it is daytime or not.
     * @return True if it is daytime, false otherwise.
     */
    @Override
    public boolean isDaytime () {
        return (isDaytime);
    }

    /**
     * Gets a double representing the time zone.
     *
     * @return A double representing the time zone.
     */
    private double getRawOffsetInHours() {
        double millsInHour = (double) ResourceTimeManager.MILLISECONDS_PER_HOUR;
        double result = (timeZone.getRawOffset() / millsInHour);
        return result;
    }
    
    /**
     * Convert decimal to GregorianCalendar object.
     *
     * Decimal must be in range [0, 1] and represents times between
     * [12:00 AM, 12:00 PM]
     *
     * .5 = 12:00:00, so there is a factor of .5 / 12 = 0.04166
     * minutes, divide that factor by 60, seconds again by 60
     *
     * @param decimal Decimal to convert to <code>GregorianCalendar</code>.
     */
    private GregorianCalendar decimalToCalendar (double decimal) {
        GregorianCalendar calendar;
        // Factors for converting decimal to hour, minute, second.
        double hourFactor = .5 / 12;
        double minuteFactor = hourFactor / 60;
        double secondFactor = minuteFactor / 60;

        // Find how many hours can fit in.
        int outHour = (int)(decimal / hourFactor);
        // Subtract off time that went into hours.
        decimal -= (outHour * hourFactor);

        int outMinute = (int)(decimal / minuteFactor);
        decimal -= (outMinute * minuteFactor);

        int outSecond = (int)(decimal / secondFactor);


        calendar = new GregorianCalendar();
        // Set the time zone.
        calendar.setTimeZone (timeZone);

        // New method: works.
        calendar.set (GregorianCalendar.YEAR, year);
        calendar.set (GregorianCalendar.MONTH, month - 1);
        calendar.set (GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set (GregorianCalendar.HOUR_OF_DAY, outHour);
        calendar.set (GregorianCalendar.MINUTE, outMinute);
        calendar.set (GregorianCalendar.SECOND, outSecond);

        //return (date);
        return (calendar);
    }


    /**
     * Parse the calendar object to get year, month, day, hour, minute, second.
     * TODO: why is minute set to 6?
     * NOTE: hours, minutes, and seconds might not be needed. They are
     *        currently unimplemented. Leave minute set to 6.
     *
     * @param dateAndTimeInMillis The date and time being tested, expressed in
     * milliseconds.
     */
     private void parseDateFields(long dateAndTimeInMillis) {
        SimpleDateFormat dfmtYear = new SimpleDateFormat("yyyy");
        dfmtYear.setTimeZone(timeZone);
        SimpleDateFormat dfmtMonth = new SimpleDateFormat("MM");
        dfmtMonth.setTimeZone(timeZone);
        SimpleDateFormat dfmtDay = new SimpleDateFormat("dd");
        dfmtDay.setTimeZone(timeZone);
        Date date = new Date(dateAndTimeInMillis);

        year = Integer.parseInt(dfmtYear.format(date));
        month = Integer.parseInt(dfmtMonth.format(date));
        day = Integer.parseInt(dfmtDay.format(date));

        // Hours, minutes, seconds unimplemented.
        hour = 0;
        minute = 6;
        second = 0;
     }


    /**
     * Change the times of sunrise and sunset to account for DST, if it is
     * currently in effect for the time zone. If DST is not in effect, nothing
     * changes.
     * 
     * @param dateAndTimeInMillis The date and time being tested, expressed in
     * milliseconds.
     */
    private void fixForDST(long dateAndTimeInMillis) {
        boolean isDST = timeZone.inDaylightTime(new Date(dateAndTimeInMillis));
        if(isDST) {
            sunrise.setTimeInMillis(sunrise.getTimeInMillis()
                    + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            sunset.setTimeInMillis(sunset.getTimeInMillis()
                    + ResourceTimeManager.MILLISECONDS_PER_HOUR);
        }
    }
}
