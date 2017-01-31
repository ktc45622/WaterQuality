package BBTest;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;

public class TestLocalStorageImageNoDataFileCreation {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        //Setup resource number.
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
        
        //Setup test images.
        File testNoDataJPEG = new File("C:\\Test Image\\Test.jpg");
        if (testNoDataJPEG.exists()) {
            Debug.println("Test No Data JPEG data exists.");
        } else {
            Debug.println("Test No Data JPEG data does NOT exist.");
            return;
        }
        File testDaytimeJPEG = new File("C:\\Test Image\\Test.jpg");
        if (testDaytimeJPEG.exists()) {
            Debug.println("Test Daytime JPEG data exists.");
        } else {
            Debug.println("Test Daytime JPEG data does NOT exist.");
            return;
        }
        File testNighttimeJPEG = new File("C:\\Test Image\\Test.jpg");
        if (testNighttimeJPEG.exists()) {
            Debug.println("Test Nighttime JPEG data exists.");
        } else {
            Debug.println("Test Nighttime JPEG data does NOT exist.");
            return;
        }
        
        //Setup resource and image instances.
        Resource resource = dbms.getResourceManager().getWeatherResourceByNumber(videoResourceNumber);
        
        ImageInstance noDateImageInstance = new ImageInstance();
        ImageInstance daytimeImageInstance = new ImageInstance();
        ImageInstance nighttimeImageInstance = new ImageInstance();
        try {
            noDateImageInstance.readFile(testNoDataJPEG);
            daytimeImageInstance.readFile(testDaytimeJPEG);
            nighttimeImageInstance.readFile(testNighttimeJPEG);
        } catch (WeatherException ex) {
            Logger.getLogger(TestLocalStorageImageNoDataFileCreation.class.getName()).log(Level.SEVERE, null, ex);
            Debug.println("Unable to load image instances.");
            return;
        }

        if (localServer.setNewDefaultDaytimeMovie(resource, daytimeImageInstance)) {
            Debug.println("Video Saved.");
        } else {
            Debug.println("Video NOT Saved.");
        }
        
        if (localServer.setNewDefaultNightimeMovie(resource, nighttimeImageInstance)) {
            Debug.println("Video Saved.");
        } else {
            Debug.println("Video NOT Saved.");
        }
        
        if (localServer.setNewDefaultGenericNoDataMovie(noDateImageInstance)) {
            Debug.println("Video Saved.");
        } else {
            Debug.println("Video NOT Saved.");
        }
    }
}
