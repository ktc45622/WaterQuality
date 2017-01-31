package weather.common.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Provides methods for displaying the date/time of a {@code GregorianCalendar}
 * in many formats.
 *
 * @author Eric Subach (2010)
 */
public class CalendarFormatter {

    private static DateFormat format;

    public static enum DisplayFormat {

        DIRECTORY_DEFAULT("yyyy/MM/dd/hh:mm:ss a"),
        DEFAULT("MM/dd/yyyy hh:mm:ss a"),
        DATE("MM/dd/yyyy"),
        DATE_SHORT("MMM d, yyyy"),
        DATE_LONG("MMMMM d, yyyy"),
        HOUR_24_LONG("HH"),
        MINUTE_LONG("mm"),
        SECOND_LONG("ss"),
        TIME_12("hh:mm:ss a"),
        TIME_24("HH:mm:ss"),
        TIME_ZONE("z"),
        DEFAULT_WITH_TIME_ZONE("MM/dd/yyyy hh:mm:ss a z");

        DateFormat format;

        DisplayFormat(String string) {
            format = new SimpleDateFormat(string);
        }
    }

    /**
     * Formats the given Calendar object using the {@code DEFAULT} format.
     *
     * @param calendar the Calendar object to format
     * @return {@code calender} as a formatted string, using {@code DEFAULT}
     * format
     */
    public static String format(Calendar calendar) {
        return (format(calendar, DisplayFormat.DEFAULT));
    }
    
    /**
     * Formats the given Calendar object using the
     * {@code DEFAULT_WITH_TIME_ZONE} format.
     *
     * @param calendar the Calendar object to format
     * @return {@code calender} as a formatted string, using
     * {@code DEFAULT_WITH_TIME_ZONE} format
     */
    public static String formatWithTimeZone(Calendar calendar) {
        return (format(calendar, DisplayFormat.DEFAULT_WITH_TIME_ZONE));
    }

    /**
     * Formats the given Calendar object using the {@code DIRECTORY_DEFAULT}
     * format.  Format is identical to the {@code DEFAULT} format, but has a 
     * directory marker between the year and hour.
     *
     * @param calendar the GregorianCalendar object to format
     * @return {@code calendar} as a formatted string, using directory format
     */
    public static String directoryFormat(GregorianCalendar calendar) {
        return (format(calendar, DisplayFormat.DIRECTORY_DEFAULT));
    }

    /**
     * Formats the given Calendar object using the given DisplayFormat object.
     *
     * @param calendar the Calendar object to format
     * @param displayFormat the DisplayFormat object to use to format {@code calendar}
     * @return {@code calendar} as a formatted string, using {@code display}
     */
    public static String format(Calendar calendar, DisplayFormat displayFormat) {
        format = displayFormat.format;
        format.setTimeZone(calendar.getTimeZone());

        return (format.format(calendar.getTimeInMillis()));
    }

    // Convenience methods.
    /**
     * Formats the given Calendar object to 12-hour format.
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted string, using {@code TIME_12}
     * format
     */
    public static String formatTime(Calendar calendar) {
        return (format(calendar, DisplayFormat.TIME_12));
    }

    /**
     * Formats the given Calendar object to 24-hour format.
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted string, using {@code TIME_24}
     * format
     */
    public static String formatTime24(Calendar calendar) {
        return (format(calendar, DisplayFormat.TIME_24));
    }

    /**
     * Formats the given Calendar object to time zone format (returns just the 
     * time zone of the Calendar object).
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted string, using {@code TIME_ZONE} 
     * format
     */
    public static String formatTimeZone(Calendar calendar) {
        return (format(calendar, DisplayFormat.TIME_ZONE));
    }

    /**
     * Formats the given Calendar object to just hours, in 24-hour format, with 
     * two digit places.
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted string, using 
     * {@code HOUR_24_LONG} format
     */
    public static String formatHour24Long(Calendar calendar) {
        return (format(calendar, DisplayFormat.HOUR_24_LONG));
    }

    /**
     * Formats the given Calendar object to just minutes, with two digit places.
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted string, using {@code MINUTE_LONG}
     * format
     */
    public static String formatMinuteLong(Calendar calendar) {
        return (format(calendar, DisplayFormat.MINUTE_LONG));
    }

    /**
     * Formats the given Calendar object to just seconds, with two digit places.
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted string, using {@code SECOND_LONG}
     * format
     */
    public static String formatSecondLong(Calendar calendar) {
        return (format(calendar, DisplayFormat.SECOND_LONG));
    }

    /**
     * Formats the given Calendar object to short date format, "MMM DD, YYYY".
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted date, using {@code DATE_SHORT}
     * format
     */
    public static String formatDateShort(Calendar calendar) {
        return format(calendar, DisplayFormat.DATE_SHORT);
    }

    /**
     * Formats the given Calendar object to long date format, "Month Day, Year".
     *
     * @param calendar the Calendar object to format
     * @return {@code calendar} as a formatted date, using {@code DATE_LONG} 
     * format
     */
    public static String formatDateLong(Calendar calendar) {
        return format(calendar, DisplayFormat.DATE_LONG);
    }

    /**
     * Parses the given string using the specified format, returning a Calendar
     * object representing this time.
     *
     * @param input string representation of a date/time to be parsed
     * @param displayFormat expected format of {@code input}
     * @param timeZone The <code>TimeZone</code> of the input string.  The will
     * be applied to the return object.
     * @return a Calendar object representing the date/time from {@code input}
     * @throws ParseException if {@code input} cannot be parsed
     */
    public static Calendar parse(String input, DisplayFormat displayFormat,
            TimeZone timeZone)
            throws ParseException {
        Calendar result = new GregorianCalendar();
        result.setTimeZone(timeZone);
        format = displayFormat.format;
        format.setTimeZone(timeZone);
        result.setTime(format.parse(input));
        return result;
    }
}
