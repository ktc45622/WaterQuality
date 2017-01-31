package BBTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.ResourceInstancesRequested;
import weather.common.data.ResourceInstancesReturned;
import weather.common.data.resource.Resource;
import weather.common.data.resource.ResourceFileFormatType;
import weather.common.data.resource.ResourceInstance;
import weather.common.data.resource.ResourceRange;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.servercomm.storage.StorageCommand;
import weather.common.servercomm.storage.StorageCommandType;
import weather.common.utilities.Debug;
//import weather.common.utilities.ResourceTimeManager;
import weather.common.utilities.WeatherException;
import weather.serverside.storage.StorageServerLocal;

public class TestLocalStorageRetrieval {

    public static void main(String[] args) {
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
        StorageServerLocal localStore = new StorageServerLocal(dbms, fileSystemRoot);

        //Set range for below loops (local time).  Start with calendar objects.
        GregorianCalendar startCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        //Set calendars to dates.  (Set in local time.)
        startCalendar.set(2015, GregorianCalendar.DECEMBER, 26, 2, 0, 0);
        startCalendar.set(GregorianCalendar.MILLISECOND, 0);
        endCalendar.set(2015, GregorianCalendar.DECEMBER, 29, 1, 59, 59);
        endCalendar.set(GregorianCalendar.MILLISECOND, 999);
        //Set resource range.
        ResourceRange range = new ResourceRange(new Date(startCalendar
                .getTimeInMillis()), new Date(endCalendar.getTimeInMillis()));
        
        //Setup resource numbers and resources.
        int videoResourceNumber = 129;
        int stationResourceNumber = 302;
        Resource movieResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(videoResourceNumber);
        Resource stationResource = dbms.getResourceManager()
                .getWeatherResourceByNumber(stationResourceNumber);
        
        //Storage command for provide testing.
        StorageCommand provideCommand = new StorageCommand();
        
        //More needed variables
        ResourceInstancesRequested resourceRequest = null;
        
        Debug.println();

        //Test mp4 movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.mp4, movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            Logger.getLogger(TestLocalStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test avi movie retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            Logger.getLogger(TestLocalStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test weather station retrieval.
         resourceRequest = new ResourceInstancesRequested(range, 1 ,false, 
                 stationResource.getFormat(), stationResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            Logger.getLogger(TestLocalStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test image retrieval.
        resourceRequest = new ResourceInstancesRequested(range, 4, false, 
                movieResource.getFormat(), movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            Logger.getLogger(TestLocalStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Debug.println();
        
        //Test day-log mp4 movie retrieval.
        
//         //Change range to test if nulls are handled correctly.
//        range.setStopTime(new Date(range.getStopTime().getTime()
//            + ResourceTimeManager.MILLISECONDS_PER_DAY));
        
        resourceRequest = new ResourceInstancesRequested(range, 1, true,
                ResourceFileFormatType.avi, movieResource);
        provideCommand.setCommandType(StorageCommandType.PROVIDE_DAY_LONG_MP4);
        provideCommand.setResourceRequest(resourceRequest);
        try {
            showResult(localStore.executeLocalProvide(provideCommand));
        } catch (WeatherException ex) {
            Logger.getLogger(TestLocalStorageRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Helper function to debug output.
    private static void showResult(ArrayList<ResourceInstancesReturned> list) {
        for (int i = 0; i < list.size(); i++) {
            Debug.println("\nResult index " + i + ":");
            ResourceInstancesReturned rir = list.get(i);
            for (int j = 0; j < rir.getNumberOfValuesReturned(); j++) {
                ResourceInstance instance = rir.getResourceInstances().get(j);
                if (instance == null) {
                    Debug.println("Element " + j + " is null.");
                } else {
                    Debug.println("Element " + j + ": Type: " + instance.getClass()
                        .toString() + instance.toString());
                }
            }
        }
    }
}
