
package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import weather.common.utilities.CalendarFormatter;

/**
 *
 * @author cjones
 */
public class TimeTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       Calendar cal = Calendar.getInstance();
       Date time = new Date(System.currentTimeMillis());
       cal.setTime(time);
       StringBuilder path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("No time zone set - timezone was: "+cal.getTimeZone().getDisplayName() + " Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
      
        
       cal.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
       path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as EST5EDT: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
        
        cal.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as CST6CDT: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
        
        cal.setTimeZone(TimeZone.getTimeZone("MST7MDT"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as MST7MDT: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
        
        cal.setTimeZone(TimeZone.getTimeZone("PST8PDT"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as PST8PDT: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
      
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as UTC: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
        
        cal.setTimeZone(TimeZone.getTimeZone("Pacific/Samoa"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
       path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as Pacific/Samoa: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
        
        cal.setTimeZone(TimeZone.getTimeZone("US/Samoa"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as US/Samoa: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
        
        cal.setTimeZone(TimeZone.getTimeZone("Not_valid"));
        path = new StringBuilder("");

        path.append(File.separator).append(cal.get(Calendar.YEAR));
        path.append(File.separator).append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.US));
        path.append(File.separator).append(cal.get(Calendar.DAY_OF_MONTH)).append(File.separator+"Filename");
      
        path.append (CalendarFormatter.formatDateShort(cal)).append(" ")
                .append(CalendarFormatter.formatTime24(cal));
        System.out.println("Time zone set as Not_valid: Milliseconds = "+ cal.getTimeInMillis()+" filename "+ path);
    }
    
}
