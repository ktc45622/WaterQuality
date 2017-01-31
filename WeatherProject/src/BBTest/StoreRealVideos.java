package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.resource.MP4Instance;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

/**
 * This is a program used to make some testing possible.  It is intended to 
 * store real data on a testing implementation of a local storage system. 
 */
public class StoreRealVideos {
    
    public static void main(String[] args) {
        //Setup resource numbers.
        int videoResourceNumber = 104;
        
        //Setup local storage
        DBMSSystemManager dbms = null;
        String fileSystemRoot = "C:";

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        StorageControlSystemLocalImpl localServer = new StorageControlSystemLocalImpl(dbms, fileSystemRoot);
        
       
        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        startCalendar.set(2014, GregorianCalendar.JANUARY, 10, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2014, GregorianCalendar.JANUARY, 11, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);

        //Store mp4 hour-long movies.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.HOUR, 1)) {

            MP4Instance mp4Instance = new MP4Instance();
            mp4Instance.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            mp4Instance.setResourceNumber(videoResourceNumber);
            
            File sourceFile = new File("C:\\FULL_DAY_104\\R" + videoResourceNumber
                + "_" + loopCalendar.get(GregorianCalendar.HOUR_OF_DAY) + ".mp4");
            try {
                mp4Instance.readFile(sourceFile);
            } catch (WeatherException ex) {
                Logger.getLogger(StoreRealVideos.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!localServer.placeResourceInstance(mp4Instance)) {
                Debug.println("Unable to place instance!");
            }
        }
        
        dbms.closeDatabaseConnections();
    }
}
