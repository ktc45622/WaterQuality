package BBTest;

import java.sql.Date;
import java.util.GregorianCalendar;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

public class TestLocalStorageSystemImplMOVToMP4Changing {

    public static void main(String[] args) {
        //Setup local storage
        DBMSSystemManager dbms = null;
        String fileSystemRoot = "C:";
        
        Debug.setEnabled(true);
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        StorageControlSystemLocalImpl localStore = new StorageControlSystemLocalImpl(dbms, fileSystemRoot);
        
         //Setup resource number.
        int videoResourceNumber = 102;

        //Set date for below test (local time).  Start with calendar object.
        GregorianCalendar dateCalendar = new GregorianCalendar();
        //Set calendar to date.  (Set in local time.)
        dateCalendar.set(2011, GregorianCalendar.JUNE, 11, 0, 0, 0);
        dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
        
        //Make date object.
        Date date = new Date(dateCalendar.getTimeInMillis());
        
        //Test MP4 creation method.
        if(localStore.convertMOVVideosToMP4Videos(videoResourceNumber, date)) {
            Debug.println("All conversions successful.");
        } else {
            Debug.println("All conversions NOT successful.");
        }

    }
}
