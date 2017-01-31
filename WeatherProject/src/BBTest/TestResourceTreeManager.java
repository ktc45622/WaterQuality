package BBTest;

import java.sql.Date;
import java.util.GregorianCalendar;
import weather.clientside.utilities.ResourceTreeManager;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;

public class TestResourceTreeManager {

    public static void main(String[] args) {
        //Tell our resource tree manager what storage system to use.
        ResourceTreeManager.setStorageControlSystem(
                StorageControlSystemImpl.getStorageSystem());
        
        //Pick video mode.
        //(All options were tested.)
        ResourceTreeManager.setToAVIAndMP4();
        //Load existing data.
        ResourceTreeManager.initializeData();
        
        //Set range for request.  Start with calendars for dates.
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();

        start.set(2014, GregorianCalendar.JANUARY, 24, 20, 0, 0);
        start.set(GregorianCalendar.MILLISECOND, 0);
        end.set(2014, GregorianCalendar.JANUARY, 25, 2, 0, 0);
        end.set(GregorianCalendar.MILLISECOND, 0);

        //Declare range.
        ResourceRange range = new ResourceRange(new Date(start.getTimeInMillis()),
                new Date(end.getTimeInMillis()));
        
        //Get dbms to fimd resource.
        DBMSSystemManager dbms = null;
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Resource for request.
        Resource r = dbms.getResourceManager().getWeatherResourceByNumber(102);
        
        //Load videos.
        ResourceTreeManager.getResourceInstancesForRange(r, range);
        Debug.println("Please check result.");
    }
}
