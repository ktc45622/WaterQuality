package BBTest;

import java.sql.Date;
import java.util.GregorianCalendar;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;
import weather.serverside.old_day_long_maker.OldDayLongVideoMaker;

//This is a program to test the OldDayLongVideoMaker class.
public class TestOldVideoMaker {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Debug.setEnabled(true);
        DBMSSystemManager dbms = null;
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        //Setup resource.
        int videoResourceNumber = 104;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
                
        //Get storage system.
        StorageControlSystem storage
                = new StorageControlSystemLocalImpl(dbms, "C:");
        Debug.println("\nGot storage system. (Root Drive: "
                + storage.getRootDirectory() + ")\n");
        
        //Set time to test (resource time zone).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        startCalendar.set(2015, GregorianCalendar.MAY, 3, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        //Set time as date.
        Date date = new Date(startCalendar.getTimeInMillis());
        
        //Do testing.
        boolean success = OldDayLongVideoMaker.makeOldVideo(storage, 
                movieResource, date, false, false) == 0;
        
        if (success) {
            Debug.println("Old Video Made.");
        } else {
            Debug.println("Old Video NOT Made.");
        }
                
        //Close database connections
        dbms.closeDatabaseConnections();           
    }
}