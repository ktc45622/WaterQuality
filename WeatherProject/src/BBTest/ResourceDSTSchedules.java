package BBTest;

import java.util.Calendar;
import java.util.SimpleTimeZone;

/**
 * Contains <code>SimpleTimeZone</code> objects that have DST schedule
 * information for predefined locations.
 *
 * For more information, see <code>TimeZone</code>.
 *
 * NOTE: The times may be off due to incomplete understanding of the time zone class.
 *
 * @author Eric Subach (2010)
 * @version Spring 2010
 */
public class ResourceDSTSchedules {
    /* DST Schedule
     * ============
     *
     * North America: 2nd Sunday in March --- 1st Sunday in November
     *                1 hour saved at 2 a.m.
     *
     * Europe:        Last Sunday in March --- Last Sunday in October
     *                1 hour saved at 2 a.m.
     *
     * Australia:     Last Sunday in October --- Last Sunday in March
     *                1 hour saved at 2 a.m.
     *
     * New Zealand:   First Sunday in October --- Third Sunday in March
     *                1 hour saved at 2 a.m.
     *
     * Brazil:        First Sunday in October --- First Sunday in February with day greater than or equal to 11
     *                1 hour saved at 2 a.m.
     *
     * Chile:         First Sunday in October with day greater than or equal to 9 --- First Sunday in March with day greater than or equal to 9
     *                1 hour saved at 2 a.m.
     *
     * Egypt:         Last Friday in April --- Last Friday in September
     *                1 hour saved at 2 a.m.
     *
     * Israel:        Second Friday in March --- Fourth Sunday in October (dates are only estimates because they are chosen yearly by the government)
     *                1 hour saved at 2 a.m.
     */
    // Provides the regular time without compensating for DST.
    public static SimpleTimeZone None = new SimpleTimeZone (0, "None");
    //public static SimpleTimeZone NorthAmerica = new SimpleTimeZone (0, "NorthAmerica", Calendar.MARCH, 2, -Calendar.SUNDAY, 7200000, Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone NorthAmerica = new SimpleTimeZone (0, "NorthAmerica", Calendar.MARCH, 8, -Calendar.SUNDAY, 7200000, Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone Europe = new SimpleTimeZone (0, "Europe", Calendar.MARCH, -1, Calendar.SUNDAY, 7200000, Calendar.OCTOBER, -1, Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone Australia = new SimpleTimeZone (0, "Australia", Calendar.OCTOBER, -1, Calendar.SUNDAY, 7200000, Calendar.MARCH, -1, Calendar.SUNDAY, 7200000, 3600000);
    //public static SimpleTimeZone NewZealand = new SimpleTimeZone (0, "Australia", Calendar.OCTOBER, 1, -Calendar.SUNDAY, 7200000, Calendar.MARCH, 3, -Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone NewZealand = new SimpleTimeZone (0, "Australia", Calendar.OCTOBER, 1, -Calendar.SUNDAY, 7200000, Calendar.MARCH, 15, -Calendar.SUNDAY, 7200000, 3600000);
    //public static SimpleTimeZone Brazil = new SimpleTimeZone (0, "Brazil", Calendar.OCTOBER, 1, -Calendar.SUNDAY, 7200000, Calendar.FEBRUARY, 1, -Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone Brazil = new SimpleTimeZone (0, "Brazil", Calendar.OCTOBER, 1, -Calendar.SUNDAY, 7200000, Calendar.FEBRUARY, 11, -Calendar.SUNDAY, 7200000, 3600000);
    //public static SimpleTimeZone Chile = new SimpleTimeZone (0, "Brazil", Calendar.OCTOBER, 1, -Calendar.SUNDAY, 7200000, Calendar.MARCH, 1, -Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone Chile = new SimpleTimeZone (0, "Chile", Calendar.OCTOBER, 9, -Calendar.SUNDAY, 7200000, Calendar.MARCH, 9, -Calendar.SUNDAY, 7200000, 3600000);
    public static SimpleTimeZone Egypt = new SimpleTimeZone (0, "Egypt", Calendar.APRIL, -1, Calendar.FRIDAY, 7200000, Calendar.SEPTEMBER, -1, Calendar.FRIDAY, 7200000, 3600000);
    //public static SimpleTimeZone Israel = new SimpleTimeZone (0, "Israel", Calendar.MARCH, 2, -Calendar.FRIDAY, 7200000, Calendar.OCTOBER, 4, Calendar.SUNDAY, 7200000, 3600000);
    //public static SimpleTimeZone Israel = new SimpleTimeZone (0, "Israel", Calendar.MARCH, 8, -Calendar.FRIDAY, 7200000, Calendar.OCTOBER, 29, Calendar.SUNDAY, 7200000, 3600000);
}
