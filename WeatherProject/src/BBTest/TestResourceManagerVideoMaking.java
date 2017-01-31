package BBTest;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.resource.ImageInstance;
import weather.common.dbms.DBMSResourceManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;

/**
 * This is a program to test the video making functionality of the 
 * ResourceManager class.
 * 
 * NOTES: This test program requires the remote part of the storage system (file
 * system root and storage port) to be configured elsewhere.  Also, the SQL code
 * in the called methods or ResourceManager should be disabled for testing.
 */


public class TestResourceManagerVideoMaking {

    public static void main(String[] args) {
        //Setup resource numbers.
        int videoResourceNumber = 104;
        
        //Get DBMS.
        DBMSSystemManager dbms = null;
        
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Setup test data.
        File testDataJPEG = new File("TestPic.jpeg");
        if (testDataJPEG.exists()) {
            Debug.println("Test JPEG data exists.");
        } else {
            Debug.println("Test JPEG data does NOT exist.");
            return;
        }
        
        //Get resource manager.
        DBMSResourceManager rm = dbms.getResourceManager();
        
        //Test default video creation.
        ImageInstance imageInstance = new ImageInstance();
        try {
            imageInstance.readFile(testDataJPEG);
        } catch (WeatherException ex) {
            Logger.getLogger(TestRemoteStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Store no-data video.
        if (rm.setDefaultGenericNoDataImage(imageInstance)) {
            Debug.println("Video made.");
        } else {
            Debug.println("Video NOT made.");
        }
        if (rm.setDefaultDaytimePicture(videoResourceNumber, imageInstance)) {
            Debug.println("Video made.");
        } else {
            Debug.println("Video NOT made.");
        }
        if (rm.setDefaultNighttimePicture(videoResourceNumber, imageInstance)) {
            Debug.println("Video made.");
        } else {
            Debug.println("Video NOT made.");
        }
    }
}
