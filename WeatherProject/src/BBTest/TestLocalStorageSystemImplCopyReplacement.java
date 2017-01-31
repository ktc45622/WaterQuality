package BBTest;

/**
 * This file is to test if updated versions of day-long videos are correctly 
 * saved in place of old versions
 */

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

public class TestLocalStorageSystemImplCopyReplacement {

    public static void main(String[] args) {
        //Setup resource numbers.
        int videoResourceNumber = 102;

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
        
        //Setup test data.
        File testDataMP4 = new File("NoData.mp4");
        if (testDataMP4.exists()) {
            Debug.println("Test MP4 data exists.");
        } else {
            Debug.println("Test MP4 data does NOT exist.");
        }
         File testDataLongMP4 = new File("GenericDayVideo.mp4");
        if (testDataLongMP4.exists()) {
            Debug.println("Long test MP4 data exists.");
        } else {
            Debug.println("Long test MP4 data does NOT exist.");
        }

        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        startCalendar.set(2015, GregorianCalendar.FEBRUARY, 1, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2015, GregorianCalendar.FEBRUARY, 2, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);

        //Perform first save. 
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.DATE, 1)) {

            MP4Instance mp4Instance1 = new MP4Instance();
            mp4Instance1.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance1.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance1.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            mp4Instance1.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance1.readFile(testDataMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestLocalStorageSystemImplCopyReplacement.class.getName()).log(Level.SEVERE, null, ex);
            }

            MP4Instance mp4Instance2 = new MP4Instance();
            mp4Instance2.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance2.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance2.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            mp4Instance2.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance2.readFile(testDataMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestLocalStorageSystemImplCopyReplacement.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (localServer.placeDayLongMovie(mp4Instance1, mp4Instance2)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }
        
        Debug.println();
        
        //Perform second save. 
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.DATE, 1)) {

            MP4Instance mp4Instance1 = new MP4Instance();
            mp4Instance1.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance1.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance1.setEndTime(loopCalendar.getTimeInMillis() - 1
                    + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            mp4Instance1.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance1.readFile(testDataLongMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestLocalStorageSystemImplCopyReplacement.class.getName()).log(Level.SEVERE, null, ex);
            }

            MP4Instance mp4Instance2 = new MP4Instance();
            mp4Instance2.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance2.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance2.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            mp4Instance2.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance2.readFile(testDataLongMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestLocalStorageSystemImplCopyReplacement.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (localServer.placeDayLongMovie(mp4Instance1, mp4Instance2)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }
    }
}
