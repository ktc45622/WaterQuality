
package BBTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Vector;
import java.util.logging.Level;
import weather.StorageControlSystem;
import weather.common.data.resource.Resource;
import weather.common.data.resource.WeatherResourceType;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;
import weather.serverside.applicationimpl.StorageControlSystemLocalImpl;
import weather.serverside.movie.DayLongVideoScheduler;

/**
 * This is a test program for the DayLongVideoScheduler.
 */
public class TestDayLongVideoScheduler {
    
    public static void main(String[] args) 
            throws ClassNotFoundException, InstantiationException, 
            IllegalAccessException, WeatherException {
        Debug.setEnabled(true);
        String fileSystemRoot = "C:";
        DBMSSystemManager DBMS =  MySQLImpl.getMySQLDMBSSystem();
        StorageControlSystem storage = new StorageControlSystemLocalImpl(DBMS,
            fileSystemRoot);
        
        DayLongVideoScheduler dayLongVideoScheduler = new DayLongVideoScheduler(storage);
        WeatherResourceType type;

        // For each resource, criteria required to schedule movie creation:
        // active and of type weather camera or weather map loop plus test for 
        // different time zone.
        Vector<Resource> resources = DBMS.getResourceManager().getResourceList();
        
        // Place "no data" movie in file system if necessary.
        String genericFolderName = "Generic Movies";
        File genericFolder = new File(fileSystemRoot + File.separator
            + genericFolderName);
        if (!genericFolder.exists()) {
            genericFolder.mkdirs();
        }
        String fileName = "NoData.mp4";
        File projectFile = new File(fileName);
        File targetFile = new File(fileSystemRoot + File.separator
            + genericFolderName + File.separator + fileName);
        if (!targetFile.exists()) {
            try {
                Files.copy(projectFile.toPath(), targetFile.toPath());
            } catch (IOException ex) {
                Debug.println(ex.getMessage());
            }
        }
        
        // Count active video resources.
        int activeVideoCount = 0;
        
        for(Resource r : resources) {
            type = r.getResourceType ();

            if (r.isActive()) {
                if (type == WeatherResourceType.WeatherCamera ||
                    type == WeatherResourceType.WeatherMapLoop) {
                    activeVideoCount++;
                    
                    // Create storage folder if necessary.
                    File folder = new File(fileSystemRoot + File.separator
                        + r.getStorageFolderName());
                    if(!folder.exists()) {
                        folder.mkdirs();
                    }

                    // Schedule resource.
                    dayLongVideoScheduler.startMaker(r);
                }
            }
        }
        
        Debug.println("Number of active video resources: " + activeVideoCount);
    }
}
