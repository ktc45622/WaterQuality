package BBTest;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.resource.ImageInstance;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.serverside.storage.StorageManagement;

public class TestStorageManagementVideoCreation2 {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup remote storage
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
        StorageManagement storageManagement = new StorageManagement(dbms, fileSystemRoot);
        
        //Setup resource number,
        int videoResourceNumber = 303;
        
        //Setup image instances..
        File testDaytimeFile = new File("C:\\Gif Folder\\data.gif");
        if (testDaytimeFile.exists()) {
            Debug.println("Test Daytime file data exists.");
        } else {
            Debug.println("Test Daytime file data does NOT exist.");
            return;
        }
        File testNighttimeFile = new File("C:\\Png Folder\\data.png");
        if (testNighttimeFile.exists()) {
            Debug.println("Test Nighttime file data exists.");
        } else {
            Debug.println("Test Nighttime file data does NOT exist.");
            return;
        }

        ImageInstance daytimeImageInstance = new ImageInstance();
        ImageInstance nighttimeImageInstance = new ImageInstance();
        try {
            daytimeImageInstance.readFile(testDaytimeFile);
            nighttimeImageInstance.readFile(testNighttimeFile);
        } catch (WeatherException ex) {
            Logger.getLogger(TestStorageManagementVideoCreation2.class.getName()).log(Level.SEVERE, null, ex);
            Debug.println("Unable to load image instances.");
            return;
        }
        
        try {
            storageManagement.createAndStoreDefaultMovieFile(videoResourceNumber,
                    StorageCommandType.STORE_DEFAULT_DAY, daytimeImageInstance,
                    ResourceFileFormatType.avi);
        } catch (WeatherException ex) {
            ex.show();
        }
        try {
            storageManagement.createAndStoreDefaultMovieFile(videoResourceNumber,
                    StorageCommandType.STORE_DEFAULT_DAY, daytimeImageInstance,
                    ResourceFileFormatType.mp4);
        } catch (WeatherException ex) {
            ex.show();
        }
        try {
            storageManagement.createAndStoreDefaultMovieFile(videoResourceNumber,
                    StorageCommandType.STORE_DEFAULT_NIGHT, nighttimeImageInstance,
                    ResourceFileFormatType.avi);
        } catch (WeatherException ex) {
            ex.show();
        }
        try {
            storageManagement.createAndStoreDefaultMovieFile(videoResourceNumber,
                    StorageCommandType.STORE_DEFAULT_NIGHT, nighttimeImageInstance,
                    ResourceFileFormatType.mp4);
        } catch (WeatherException ex) {
            ex.show();
        }
    }
}
