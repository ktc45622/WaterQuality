package BBTest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;

/**
 * A testing program to test a method of <code>ResourceTimeManager</code> which
 * is designed to change the <code>TimeZone</code> of a given <code>Date</code>
 * while preserving the other data e. g. 1:30 PM ET becomes 1:30 PM MT.
 * 
 * @author Brian Bankes
 */
public class TestRTMTimeZoneChangeFunction {

    public static void main(String[] args) {
        //Set date to change.  Start with calendar object.
        TimeZone inputZone = TimeZone.getTimeZone("America/Denver");
        GregorianCalendar timeCalendar = new GregorianCalendar();
        timeCalendar.setTimeZone(inputZone);
        timeCalendar.set(2015, GregorianCalendar.JUNE, 26, 11, 0, 0);
        timeCalendar.set(GregorianCalendar.MILLISECOND, 0);
        
        //Make Date object.
        Date timeToChange = new Date(timeCalendar.getTimeInMillis());
        //Confirm input.
        Debug.setEnabled(true);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy hh:mm:ss.SSS a z");
        sdf.setTimeZone(inputZone);
        Debug.println("Input: " + sdf.format(timeToChange));
        
        //Convert date.
        TimeZone outputZone = TimeZone.getTimeZone("America/Denver");
        Date result = ResourceTimeManager.changeDateTimeZone(timeToChange, 
                inputZone, outputZone);
        
        //Show result.
        sdf.setTimeZone(outputZone);
        Debug.println("Result: " + sdf.format(result));
    }
}
