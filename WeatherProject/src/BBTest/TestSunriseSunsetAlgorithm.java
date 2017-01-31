package BBTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.serverside.utilities.ResourceCollectionTimeUtility;
import weather.serverside.utilities.SunriseSunsetAlgorithm;
import weather.serverside.utilities.SunriseSunsetAlgorithmNOAAImpl;

/**
 * This is a file to test the sunrise/sunset algorithm and the
 * <code>ResourceCollectionTimeUtility</code>.
 * @author Brian Bankes
 */
public class TestSunriseSunsetAlgorithm {
    
    private static void testResourceCollectionTimeUtility(Resource resource, GregorianCalendar calendar) {
        TimeZone timeZone = resource.getTimeZone().getTimeZone();

        //Get results from utility.
        GregorianCalendar rangeStart = ResourceCollectionTimeUtility
                .getCollectionStartTime(resource, calendar);
        GregorianCalendar rangeStop = ResourceCollectionTimeUtility
                .getCollectionStopTime(resource, calendar);
        boolean isCollectionTime = ResourceCollectionTimeUtility
                .validDataCollectionTime(resource, calendar);

        //Show results.
        String dateFormat = "MM/dd/yy hh:mm:ss a z";
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(timeZone);

        Debug.println("Testing Resource:" + resource.getResourceName());
        Debug.println("Testing Time: " + df.format(new Date(calendar.getTimeInMillis())));
        Debug.println("Testing Utility;");
        Debug.println("Start Time: " + df.format(new Date(rangeStart.getTimeInMillis())));
        Debug.println("Stop Time: " + df.format(new Date(rangeStop.getTimeInMillis())));
        if (isCollectionTime) {
            Debug.println("This is a collection time.");
        } else {
            Debug.println("This is NOT a collection time.");
        }
        Debug.println();
    }
    
    private static void testNOAAAlgorithm(Resource resource, GregorianCalendar calendar) {
        SunriseSunsetAlgorithm calculator;
        float latitude;
        float longitude;
        TimeZone timeZone;
        latitude = resource.getLatitude();
        longitude = resource.getLongitude();
        timeZone = resource.getTimeZone().getTimeZone();

        //Algorithm for calculating sunrise/sunset at location on date.
        calculator = new SunriseSunsetAlgorithmNOAAImpl(latitude,
                longitude,
                calendar.getTimeInMillis(),
                timeZone);

        //Show results.
        String dateFormat = "MM/dd/yy hh:mm:ss a z";
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(timeZone);

        Debug.println("Testing Resource:" + resource.getResourceName());
        Debug.println("Testing Time: " + df.format(new Date(calendar.getTimeInMillis())));
        Debug.println("Algorithm: NOAA");
        if (calculator.isDaytime()) {
            Debug.println("Time is daytime.");
        } else {
            Debug.println("Time is NOT daytime.");
        }
        Debug.println("Sunrise Time: " + df.format(new Date(calculator.getSunrise())));
        Debug.println("Sunset Time: " + df.format(new Date(calculator.getSunset())));
        Debug.println();
    }
    
     public static void main(String[] args) {
        //Get resources.
         DBMSSystemManager dbms = null;
         try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        Resource bloomResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(102);
        Resource wyomingResource = dbms.getResourceManager()
                 .getWeatherResourceByNumber(129);
         
         //Start testing
         GregorianCalendar cal = new GregorianCalendar();
         cal.set(2014, GregorianCalendar.MARCH, 8, 7, 0);
         
         //Do testing.
         testNOAAAlgorithm(bloomResource, cal);
         testNOAAAlgorithm(wyomingResource, cal);
         testResourceCollectionTimeUtility(bloomResource, cal);
         testResourceCollectionTimeUtility(wyomingResource, cal);
         
         //Make next day to test.
         cal.set(2014, GregorianCalendar.MARCH, 9, 7, 0);
         
         //Do testing.
         testNOAAAlgorithm(bloomResource, cal);
         testNOAAAlgorithm(wyomingResource, cal);
         testResourceCollectionTimeUtility(bloomResource, cal);
         testResourceCollectionTimeUtility(wyomingResource, cal);
         
         //Make next day to test.
         cal.set(2014, GregorianCalendar.MARCH, 10, 7, 0);
         
         //Do testing.
         testNOAAAlgorithm(bloomResource, cal);
         testNOAAAlgorithm(wyomingResource, cal);
         testResourceCollectionTimeUtility(bloomResource, cal);
         testResourceCollectionTimeUtility(wyomingResource, cal);
         
         //Make next day to test.
         cal.set(2014, GregorianCalendar.NOVEMBER, 1, 7, 0);
         
         //Do testing.
         testNOAAAlgorithm(bloomResource, cal);
         testNOAAAlgorithm(wyomingResource, cal);
         testResourceCollectionTimeUtility(bloomResource, cal);
         testResourceCollectionTimeUtility(wyomingResource, cal);
         
         //Make next day to test.
         cal.set(2014, GregorianCalendar.NOVEMBER, 2, 20, 0);
         
         //Do testing.
         testNOAAAlgorithm(bloomResource, cal);
         testNOAAAlgorithm(wyomingResource, cal);
         testResourceCollectionTimeUtility(bloomResource, cal);
         testResourceCollectionTimeUtility(wyomingResource, cal);
         
         //Test full-time resource
         Resource fullTimeResource = dbms.getResourceManager()
                 .getWeatherResourceByNumber(117);
         testResourceCollectionTimeUtility(fullTimeResource, cal);
     }
}
