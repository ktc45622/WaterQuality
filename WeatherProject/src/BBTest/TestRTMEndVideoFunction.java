package BBTest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;

/**
 * A testing program to test a method of <code>ResourceTimeManager</code> which
 * is designed to determine, for a given day, when the available video ends in a
 * given <code>TimeZone</code>.
 * 
 * @author Brian Bankes
 */
public class TestRTMEndVideoFunction {

    public static void main(String[] args) {
        //Setup dbms.
        DBMSSystemManager dbms;
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            dbms = null;
            System.exit(1);
        }
        
        //Get resource.
        int resourceNumber = 129;
        Resource resource = dbms.getResourceManager()
                .getWeatherResourceByNumber(resourceNumber);
        
        //Set date for below testing (local time).  Start with calendar object.
        GregorianCalendar timeCalendar = new GregorianCalendar();
//        timeCalendar.set(2015, GregorianCalendar.DECEMBER, 26, 2, 0, 0);
//        timeCalendar.set(GregorianCalendar.MILLISECOND, 0);
        //Make Date object.
        Date timeToCheck = new Date(timeCalendar.getTimeInMillis());
        
        //Get result of method.
        long resultInMillis = ResourceTimeManager.getLastMilliOfExpectedVideo(
                timeToCheck.getTime(), resource.getTimeZone().getTimeZone());
        
        //Get result.
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy hh:mm:ss.SSS z");
        sdf.setTimeZone(resource.getTimeZone().getTimeZone());
        String result = sdf.format(new Date(resultInMillis));
        
        //Show input and result.
        Debug.setEnabled(true);
        Debug.println("Input: " + sdf.format(timeToCheck));
        if (resultInMillis > 0) {
            Debug.println("Result: " + result);
        } else {
            Debug.println("No data available.");
        }
    }
}
