package BBTest;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.MP4Instance;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;

/**
 * NOTE: This test program requires the remote part of the storage system (file
 * system root and storage host) to be configured elsewhere.
 */

public class TestStorageSystemImplRetrieval {

    public static void main(String[] args) {
        Debug.setEnabled(true);
        
        //Setup dbms
        DBMSSystemManager dbms = null;
        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
       
        //Setup storage system
        StorageControlSystemImpl remoteStore = StorageControlSystemImpl.getStorageSystem();
        
         //Setup resource numbers and resources.
        int videoResourceNumber = 102;
        int stationResourceNumber = 299;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        Resource stationResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(stationResourceNumber);

        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        //Set calendars to dates.  (Set in local time.)
        startCalendar.set(2015, GregorianCalendar.FEBRUARY, 15, 0, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2015, GregorianCalendar.FEBRUARY, 16, 0, 0, 0);
        endCalendar.set(GregorianCalendar.MILLISECOND, 0);
        //Set resource range.
        ResourceRange range = new ResourceRange(new Date(startCalendar
                .getTimeInMillis()), new Date(endCalendar.getTimeInMillis()));
        
        //Used to make requests.
        ResourceInstancesRequested resourceRequest;
        
        Debug.println();

        //Test mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.mp4, movieResource);
        Debug.println("\nMP4 hour-long list:");
        ResourceInstancesReturned rir = remoteStore
                .getResourceInstances(resourceRequest);
        debugRIR(rir);
        
        Debug.println();
        
        //Test avi movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        Debug.println("\nAVI hour-long list:");
        debugRIR(remoteStore.getResourceInstances(resourceRequest));
        
        Debug.println();
        
        //Test weather station retrieval.
         resourceRequest = new ResourceInstancesRequested(range, 1 ,false, 
                 stationResource.getFormat(), stationResource);
         Debug.println("\nWeather Station Instance list:");
         debugRIR(remoteStore.getResourceInstances(resourceRequest));
        
        Debug.println();
        
        //Test image retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 4, false, 
                movieResource.getFormat(), movieResource);
        Debug.println("\nImage Instance list:");
        debugRIR(remoteStore.getResourceInstances(resourceRequest));
        
        Debug.println();
        
        //Test day-log mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        ArrayList<ResourceInstancesReturned> list = remoteStore
                .getDayLongMovies(resourceRequest);
        
        if (list.isEmpty()) {
            Debug.println("List of day-long rir's is EMPTY!");
            return;
        }
        
        Debug.println("\nMP4 day-long standard-quality list:");
        debugRIR(list.get(0));
        
        Debug.println("\nMP4 day-long low-quality list:");
        debugRIR(list.get(1));
        
        Debug.println();
        
        Debug.println("\nFolder List");
        ArrayList<String> folders = remoteStore.retrieveFolderList();
        
        if (folders.isEmpty()) {
            Debug.println("List is EMPTY!");
        } else {
            for (String name : folders) {
                Debug.println(name);
            }
        }
    }
    
    //Helper function to debug output.
    private static void debugRIR(ResourceInstancesReturned rir) {
        //Check for data.
        if (rir.getNumberOfValuesReturned() == 0) {
            Debug.println("No Elements.");
            return;
        }
        
        for (int j = 0; j < rir.getNumberOfValuesReturned(); j++) {
            ResourceInstance instance = rir.getResourceInstances().get(j);
            if (instance != null) {
                Debug.println("Element " + j + ": Type: " + instance.getClass()
                    .toString() + instance.toString());
            } else {
                Debug.println("Element " + j + " is NULL");
            }
        }
    }
}
