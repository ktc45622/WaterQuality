package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.resource.AVIInstance;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.MP4Instance;
import weather.common.data.resource.Resource;
import weather.common.data.weatherstation.WeatherUndergroundInstance;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;

/**
 * NOTE: This test program requires the remote part of the storage system (file
 * system root and storage port) to be configured elsewhere.
 */


public class TestStorageSystemImpl {

    public static void main(String[] args) {
        //Setup resource numbers.
        int videoResourceNumber = 102;
        int stationResourceNumber = 299;

        //Setup storage system
        StorageControlSystemImpl storage = StorageControlSystemImpl.getStorageSystem();
        
        //Get video resource.
        //Setup remote storage
        DBMSSystemManager dbms = null;
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        
        //Setup test data.
        File testDataMP4 = new File("NoData.mp4");
        if (testDataMP4.exists()) {
            Debug.println("Test MP4 data exists.");
        } else {
            Debug.println("Test MP4 data does NOT exist.");
            return;
        }
        File testDataAVI = new File("NoData.avi");
        if (testDataAVI.exists()) {
            Debug.println("Test AVI data exists.");
        } else {
            Debug.println("Test AVI data does NOT exist.");
            return;
        }
        File testDataCSV = new File("TestFile.csv");
        if (testDataCSV.exists()) {
            Debug.println("Test CSV data exists.");
        } else {
            Debug.println("Test CSV data does NOT exist.");
            return;
        }
        File testDataJPEG = new File("TestPic.jpeg");
        if (testDataJPEG.exists()) {
            Debug.println("Test JPEG data exists.");
        } else {
            Debug.println("Test JPEG data does NOT exist.");
            return;
        }
        File testDataLongMP4 = new File("GenericDayVideo.mp4");
        if (testDataLongMP4.exists()) {
            Debug.println("Test long MP4 data exists.");
        } else {
            Debug.println("Test long data does NOT exist.");
            return;
        }


        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        startCalendar.set(2015, GregorianCalendar.JANUARY, 1, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2015, GregorianCalendar.JANUARY, 2, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);

        //Store mp4 hour-long movies.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.HOUR, 1)) {

            MP4Instance mp4Instance = new MP4Instance();
            mp4Instance.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            mp4Instance.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance.readFile(testDataMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (storage.placeResourceInstance(mp4Instance)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }

        //Store hour-long avi movies.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.HOUR, 1)) {

            AVIInstance aviInstance = new AVIInstance();
            aviInstance.setStartTime(loopCalendar.getTimeInMillis());
            aviInstance.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_HOUR);
            aviInstance.setTime(new Date(loopCalendar.getTimeInMillis()));
            aviInstance.setResourceNumber(videoResourceNumber);

            try {
                aviInstance.readFile(testDataAVI);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (storage.placeResourceInstance(aviInstance)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }

        //Store images.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.MINUTE, 10)) {

            ImageInstance imageInstance = new ImageInstance();
            imageInstance.setStartTime(loopCalendar.getTimeInMillis());
            imageInstance.setEndTime(loopCalendar.getTimeInMillis());
            imageInstance.setTime(new Date(loopCalendar.getTimeInMillis()));
            imageInstance.setResourceNumber(videoResourceNumber);

            try {
                imageInstance.readFile(testDataJPEG);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (storage.placeResourceInstance(imageInstance)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }

        //Store weather station files.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.DATE, 1)) {

            WeatherUndergroundInstance wui = new WeatherUndergroundInstance();
            wui.setStartTime(loopCalendar.getTimeInMillis());
            wui.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_DAY);
            wui.setTime(new Date(loopCalendar.getTimeInMillis()));
            wui.setResourceNumber(stationResourceNumber);

            try {
                wui.readFile(testDataCSV);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (storage.placeResourceInstance(wui)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }

        //Store day-long videos.
        for (GregorianCalendar loopCalendar = (GregorianCalendar) startCalendar.clone();
                loopCalendar.getTimeInMillis() < endCalendar.getTimeInMillis();
                loopCalendar.add(GregorianCalendar.DATE, 1)) {

            MP4Instance mp4Instance1 = new MP4Instance();
            mp4Instance1.setStartTime(loopCalendar.getTimeInMillis());
            mp4Instance1.setEndTime(loopCalendar.getTimeInMillis() - 1
                + ResourceTimeManager.MILLISECONDS_PER_DAY);
            mp4Instance1.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance1.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance1.readFile(testDataLongMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            MP4Instance mp4Instance2 = new MP4Instance();
            mp4Instance2.setTime(new Date(loopCalendar.getTimeInMillis()));
            mp4Instance2.setResourceNumber(videoResourceNumber);

            try {
                mp4Instance2.readFile(testDataLongMP4);
            } catch (WeatherException ex) {
                Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (storage.placeDayLongMovie(mp4Instance1, mp4Instance2)) {
                Debug.println("Store Succcessful.");
            } else {
                Debug.println("Store NOT Succcessful.");
            }
        }
        
        
        //Test default video creation.
        ImageInstance imageInstance = new ImageInstance();
        try {
            imageInstance.readFile(testDataJPEG);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Store no-data video.
        if (storage.setNewDefaultGenericNoDataMovie(imageInstance)) {
            Debug.println("Video made.");
        } else {
            Debug.println("Video NOT made.");
        }
        if (storage.setNewDefaultDaytimeMovie(movieResource, imageInstance)) {
            Debug.println("Video made.");
        } else {
            Debug.println("Video NOT made.");
        }
        if (storage.setNewDefaultNightimeMovie(movieResource, imageInstance)) {
            Debug.println("Video made.");
        } else {
            Debug.println("Video NOT made.");
        }
    }
}
